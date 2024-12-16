package com.legal.helper;

public class Mes {
	
	private String Content;
	
	private String type;

	public String getContent() {
		return Content;
	}

	public void setContent(String content) {
		Content = content;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Mes(String content, String type) {
		super();
		this.Content = content;
	    this.type = type;
	}
	
	

}
