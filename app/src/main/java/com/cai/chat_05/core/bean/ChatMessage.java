package com.cai.chat_05.core.bean;

import java.io.Serializable;
import java.util.Date;

public class ChatMessage implements Serializable {

	private static final long serialVersionUID = 11232524855L;
	/**
	 * 非持久化字段，用于计数还有多少未读消息
	 */
	private int unCheckedCount;
	private String uuid;
	private int status;
	private String fileGroupName;
	private String filePath;
	private int contentType;// 消息类型
	private long attachmentId;// 附件在本地的Id

	private int chatMessageId;

	private int whoId;

	private Boolean checked;

	public ChatMessage() {
	}

	public ChatMessage(Long id) {
		this.id = id;
	}

	public ChatMessage(Long id, Integer chatMessageId, String content,
					   Integer fromId, Integer toId, java.util.Date date, Integer type,
					   Integer msgType, Integer chatGroupId, Integer discussionGroupId,
					   Integer whoId, Boolean checked, Long attachmentId,
					   Integer contentType, String fileGroupName, String filePath,
					   String uuid, Integer status) {
		this.id = id;
		this.chatMessageId = chatMessageId;
		this.content = content;
		this.fromId = fromId;
		this.toId = toId;
		this.date = date;
		this.type = type;
		this.msgType = msgType;
		this.chatGroupId = chatGroupId;
		this.discussionGroupId = discussionGroupId;
		this.whoId = whoId;
		this.checked = checked;
		this.attachmentId = attachmentId;
		this.contentType = contentType;
		this.fileGroupName = fileGroupName;
		this.filePath = filePath;
		this.uuid = uuid;
		this.status = status;
	}

	private Long id;

	private int fromId;

	private int toId;

	private String content;

	private Date date;

	/**
	 * 消息类型，0--发送消息，1--接收消息
	 */

	private int type;
	/**
	 * 消息类型，0--人人消息 1--群消息 2--讨论组消息
	 */
	private int msgType;

	/**
	 * 当msgtype为群消息时不为空
	 */

	private int chatGroupId;

	/**
	 * 当为讨论组消息时不为空
	 */

	private int discussionGroupId;
	// 是否需要转发，如果传入的是clinet则需要转发，如果是服务端则说明本就是转发消息不需要转发
	private boolean transfer;

	public boolean isTransfer() {
		return transfer;
	}

	public void setTransfer(boolean transfer) {
		this.transfer = transfer;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getFromId() {
		return fromId;
	}

	public int getToId() {
		return toId;
	}

	public void setFromId(int fromId) {
		this.fromId = fromId;
	}

	public int getChatMessageId() {
		return chatMessageId;
	}

	public void setChatMessageId(int chatMessageId) {
		this.chatMessageId = chatMessageId;
	}

	public void setToId(int toId) {
		this.toId = toId;
	}

	public int getUnCheckedCount() {
		return unCheckedCount;
	}

	public void setUnCheckedCount(int unCheckedCount) {
		this.unCheckedCount = unCheckedCount;
	}

	public Date getDate() {
		return date;
	}

	public Boolean getChecked() {
		return checked;
	}

	public void setChecked(Boolean checked) {
		this.checked = checked;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public int getWhoId() {
		return whoId;
	}

	public void setWhoId(int whoId) {
		this.whoId = whoId;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getMsgType() {
		return msgType;
	}

	public int getChatGroupId() {
		return chatGroupId;
	}

	public int getDiscussionGroupId() {
		return discussionGroupId;
	}

	public void setMsgType(int msgType) {
		this.msgType = msgType;
	}

	public void setChatGroupId(int chatGroupId) {
		this.chatGroupId = chatGroupId;
	}

	public void setDiscussionGroupId(int discussionGroupId) {
		this.discussionGroupId = discussionGroupId;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getContentType() {
		return contentType;
	}

	public long getAttachmentId() {
		return attachmentId;
	}

	public void setContentType(int contentType) {
		this.contentType = contentType;
	}

	public void setAttachmentId(long attachmentId) {
		this.attachmentId = attachmentId;
	}

	public String getFileGroupName() {
		return fileGroupName;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFileGroupName(String fileGroupName) {
		this.fileGroupName = fileGroupName;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
}
