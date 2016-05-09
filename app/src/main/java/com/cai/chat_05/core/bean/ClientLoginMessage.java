package com.cai.chat_05.core.bean;

public class ClientLoginMessage {
	private String token;
	private int userId;

	public String getToken() {
		return token;
	}

	public int getUserId() {
		return userId;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}
}
