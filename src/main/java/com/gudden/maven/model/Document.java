package com.gudden.maven.model;

public class Document {
	
	private String url;
	private String body;
	private String title;

	// ------------------------------------------------------------------------------------------------------
	
	public String getBody() {
		return body;
	}
	
	// ------------------------------------------------------------------------------------------------------
	
	public String getTitle() {
		return title;
	}

	// ------------------------------------------------------------------------------------------------------
	
	public String getUrl() {
		return url;
	}

	// ------------------------------------------------------------------------------------------------------
	
	public void setBody(String body) {
		this.body = body;
	}
	
	// ------------------------------------------------------------------------------------------------------
	
	public void setTitle(String title) {
		this.title = title;
	}

	// ------------------------------------------------------------------------------------------------------
	
	public void setUrl(String url) {
		this.url = url;
	}

	// ------------------------------------------------------------------------------------------------------
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
//		sb.append(url);
//		sb.append(" ");
		sb.append(body);
//		sb.append(" ");
//		sb.append(title);
		return sb.toString();
	}
	
}
