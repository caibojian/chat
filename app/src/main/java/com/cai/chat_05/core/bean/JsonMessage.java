package com.cai.chat_05.core.bean;

public class JsonMessage {
	private int jsonMessageType;
	private String jsonStr;
	private int userId;// 当该消息向服务端发送的时候需要该字段

	public int getJsonMessageType() {
		return jsonMessageType;
	}

	public String getJsonStr() {
		return jsonStr;
	}

	public int getUserId() {
		return userId;
	}

	public void setJsonMessageType(int jsonMessageType) {
		this.jsonMessageType = jsonMessageType;
	}

	public void setJsonStr(String jsonStr) {
		this.jsonStr = jsonStr;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}
}
