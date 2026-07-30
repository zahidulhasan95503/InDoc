package com.legal.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.legal.DTO.AiResponse;
import com.legal.DTO.YouTubeVideo;

@Service
public class Aiassistance_youtube {

	private static final Logger log = LoggerFactory.getLogger(Aiassistance_youtube.class);

	@Autowired
	private ChatModel chatModel;

	@Autowired
	private YouTubeService youtubeService;

	@Cacheable("aiAssistance")
	public AiResponse getAssistance(String systemRole, String userQuery) {
		// 1. Call AI
		Prompt prompt = new Prompt(
				List.of(
						new SystemMessage(systemRole),
						new UserMessage(userQuery)));

		String aiContent = "";
		try {
			aiContent = chatModel.call(prompt).getResult().getOutput().getContent();
		} catch (Exception e) {
			log.error("AI service error: {}", e.getMessage(), e);
			aiContent = "We are currently experiencing issues connecting to the AI assistant. Please try again later. (Error: "
					+ e.getMessage() + ")";
		}

		// 2. Call YouTube
		List<YouTubeVideo> videos = null;

		String youtubeQuery = userQuery;
		try {
			String extractionRole = "Generate a short, relevant YouTube search query (2 to 6 words) based on the user's issue. Output ONLY the search keywords, without any quotes or extra text.";
			Prompt ytPrompt = new Prompt(List.of(
					new SystemMessage(extractionRole),
					new UserMessage(userQuery)));
			youtubeQuery = chatModel.call(ytPrompt).getResult().getOutput().getContent().replace("\"", "").trim();
		} catch (Exception e) {
			log.warn("Failed to extract concise query using ChatModel, falling back to userQuery: {}", e.getMessage());
			youtubeQuery = userQuery;
		}

		log.info("Original user query: [{}] | YouTube search query: [{}]", userQuery, youtubeQuery);

		try {
			videos = youtubeService.searchVideos(youtubeQuery);
		} catch (Exception e) {
			log.error("Failed to fetch YouTube videos for query [{}]: {}", youtubeQuery, e.getMessage(), e);
		}

		return new AiResponse(aiContent, videos);
	}
}