package com.minitwit.model;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.minitwit.util.GravatarUtil;

public class Message {

	private static final String GRAVATAR_DEFAULT_IMAGE_TYPE = "monsterid";
	private static final int GRAVATAR_SIZE = 48;
	
	private int id;
	
	private User user;
	
	private String text;
	
	private Date pubDate;

	//
	// Constructors
	//

	public Message() {
	}

	public Message(final String text, final User user) {
		this.text = text;
		this.user = user;
		this.pubDate = new Date();
	}

	//
	// Accessors
	//

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}

	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}

	public Date getPubDate() {
		return pubDate;
	}
	public void setPubDate(Date pubDate) {
		this.pubDate = pubDate;
	}

	public String getPubDateStr() {
		if(pubDate != null) {
			return new SimpleDateFormat("yyyy-MM-dd @ HH:mm").format(pubDate);
		}
		return "";
	}

	public String getGravatar() {
		return GravatarUtil.gravatarURL(user.getEmail(), GRAVATAR_DEFAULT_IMAGE_TYPE, GRAVATAR_SIZE);
	}
}
