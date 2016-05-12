package com.cai.chat_05.core.bean;

import java.io.Serializable;
import java.util.Date;

public class MyMessage implements Serializable {

	private int unCheckedCount;
	private String uuid;
	private int status;
	private String fileGroupName;
	private String filePath;
	private int contentType;
	private long attachmentId;
	private int chatMessageId;
	private int whoId;
	private Boolean checked;
	private Long id;
	private int fromId;
	private int toId;
	private String content;
	private Date date;
	private int type;
	private int msgType;
	private int chatGroupId;
	private int discussionGroupId;
	private boolean transfer;

	public MyMessage() {
	}

	public MyMessage(Long id) {
		this.id = id;
	}

	public MyMessage(Long id, Integer chatMessageId, String content,
					   Integer fromId, Integer toId, Date date, Integer type,
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
