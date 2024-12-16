package com.legal.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.legal.DTO.YouTubeVideo;

@Service
public class YouTubeService {
	
	private static final String YOUTUBE_API_KEY = "AIzaSyA8DKU9vfZRDmCBXpW93vGNCtY7ZDSLGOI"; 
    private static final String YOUTUBE_SEARCH_URL = "https://www.googleapis.com/youtube/v3/search";
    
    public List<YouTubeVideo> searchVideos(String query) throws Exception {
    	RestTemplate restTemplate = new RestTemplate();
    	
    	// Build the YouTube API URL with query parameters
    	UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(YOUTUBE_SEARCH_URL)
    			.queryParam("part","snippet")
    			.queryParam("q", query)
    			.queryParam("key",YOUTUBE_API_KEY)
    			.queryParam("maxResults", 6);
    	        
    	
    	
    	// Make the API call and get response as a string
    	String jsonResponse =  restTemplate.getForObject(builder.toUriString(),String.class);
    	
    		//Parse JSON and extract video details
    	
    	   ObjectMapper objectMapper = new ObjectMapper();
    	   
    	   JsonNode rootNode = objectMapper.readTree(jsonResponse);
    	   
    	   JsonNode items = rootNode.path("items");
    	   
    	   List<YouTubeVideo> videos = new ArrayList<>();
    	   
    	   for(JsonNode item:items) {
    		   
    		   YouTubeVideo video = new YouTubeVideo();
    		   
    		   video.setTitle(item.path("snippet").path("title").asText());
               video.setVideoId(item.path("id").path("videoId").asText());
               video.setThumbnailUrl(item.path("snippet").path("thumbnails").path("high").path("url").asText());
               videos.add(video);
    		   
    	   }
    	   
    	   	return videos;
    	
    }
}
