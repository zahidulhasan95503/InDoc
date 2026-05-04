package com.legal.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.legal.DTO.YouTubeVideo;

@Service
public class YouTubeService {

	@Value("${youtube.api.key}")
	private String youtubeApiKey;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private ObjectMapper objectMapper;

	private static final String YOUTUBE_SEARCH_URL = "https://www.googleapis.com/youtube/v3/search";

	@Cacheable("youtubeVideos")
	public List<YouTubeVideo> searchVideos(String query) throws Exception {
		
		// Build the YouTube API URL with query parameters
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(YOUTUBE_SEARCH_URL)
				.queryParam("part", "snippet")
				.queryParam("q", query)
				.queryParam("key", youtubeApiKey)
				.queryParam("maxResults", 6);

		// Make the API call and get response as a string
		String jsonResponse = restTemplate.getForObject(builder.toUriString(), String.class);

		// Parse JSON and extract video details

		

		JsonNode rootNode = objectMapper.readTree(jsonResponse);

		JsonNode items = rootNode.path("items");

		List<YouTubeVideo> videos = new ArrayList<>();

		for (JsonNode item : items) {

			YouTubeVideo video = new YouTubeVideo();

			video.setTitle(item.path("snippet").path("title").asText());
			video.setVideoId(item.path("id").path("videoId").asText());
			video.setThumbnailUrl(item.path("snippet").path("thumbnails").path("high").path("url").asText());
			videos.add(video);

		}

		return videos;

	}
}
