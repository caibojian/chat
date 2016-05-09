package com.cai.chat_05.core.bean;

import com.cai.chat_05.core.bean.Msg.MessageType;

public class PingMessage {
	private MessageType messageType;
	private String clientId;

	public MessageType getMessageType() {
		return messageType;
	}

	public String getClientId() {
		return clientId;
	}

	public void setMessageType(MessageType messageType) {
		this.messageType = messageType;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
}
