package com.legal.DTO;

import java.util.List;

public class AiResponse {
	private String aiContent;
	private List<YouTubeVideo> videos;

	public AiResponse(String aiContent, List<YouTubeVideo> videos) {
		this.aiContent = aiContent;
		this.videos = videos;
	}

	public String getAiContent() {
		return aiContent;
	}

	public void setAiContent(String aiContent) {
		this.aiContent = aiContent;
	}

	public List<YouTubeVideo> getVideos() {
		return videos;
	}

	public void setVideos(List<YouTubeVideo> videos) {
		this.videos = videos;
	}

}
