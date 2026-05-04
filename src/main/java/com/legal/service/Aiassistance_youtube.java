package com.legal.service;

import java.util.List;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.legal.DTO.AiResponse;
import com.legal.DTO.YouTubeVideo;

@Service
public class Aiassistance_youtube {

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

		String aiContent = chatModel.call(prompt).getResult().getOutput().getContent();

		// 2. Call YouTube
		List<YouTubeVideo> videos = null;
		try {
			videos = youtubeService.searchVideos(userQuery);
		} catch (Exception e) {
			// Log handled in service or controller if needed
		}

		return new AiResponse(aiContent, videos);
	}
}