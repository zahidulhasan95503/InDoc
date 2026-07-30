package com.legal.service;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.HtmlUtils;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.legal.DTO.YouTubeVideo;

@Service
public class YouTubeService {

	private static final Logger log = LoggerFactory.getLogger(YouTubeService.class);

	@Value("${youtube.api.key}")
	private String youtubeApiKey;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private ObjectMapper objectMapper;

	private static final String YOUTUBE_SEARCH_URL = "https://www.googleapis.com/youtube/v3/search";

	@Cacheable("youtubeVideos")
	public List<YouTubeVideo> searchVideos(String query) throws Exception {

		if (query == null || query.trim().isEmpty()) {
			return new ArrayList<>();
		}

		// Build and properly encode the YouTube API URL with query parameters
		URI uri = UriComponentsBuilder.fromHttpUrl(YOUTUBE_SEARCH_URL)
				.queryParam("part", "snippet")
				.queryParam("q", query)
				.queryParam("type", "video") // Ensure we only get videos, not channels/playlists
				.queryParam("regionCode", "IN") // Regional relevance for Indian legal/document search
				.queryParam("key", youtubeApiKey)
				.queryParam("maxResults", 6)
				.build()
				.encode()
				.toUri();

		log.info("Fetching YouTube videos for query: [{}] URI: [{}]", query, uri);

		// Make the API call and get response as a string
		String jsonResponse = restTemplate.getForObject(uri, String.class);

		// Parse JSON and extract video details
		JsonNode rootNode = objectMapper.readTree(jsonResponse);
		JsonNode items = rootNode.path("items");

		List<YouTubeVideo> videos = new ArrayList<>();

		for (JsonNode item : items) {
			String videoId = item.path("id").path("videoId").asText("");
			if (videoId.isEmpty()) {
				continue;
			}

			YouTubeVideo video = new YouTubeVideo();
			String rawTitle = item.path("snippet").path("title").asText("");
			video.setTitle(HtmlUtils.htmlUnescape(rawTitle));
			video.setVideoId(videoId);
			video.setThumbnailUrl(item.path("snippet").path("thumbnails").path("high").path("url").asText(""));
			videos.add(video);
		}

		return videos;

	}
}
