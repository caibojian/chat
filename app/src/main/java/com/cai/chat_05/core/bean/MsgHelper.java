package com.cai.chat_05.core.bean;


import com.cai.chat_05.bean.Attachment;
import com.cai.chat_05.bean.Constants;

public class MsgHelper {
	/**
	 * 生成一个带返回消息的消息
	 * 
	 * @param type
	 * @param message
	 * @return
	 */
	public static Msg.Message newResultMessage(Msg.MessageType type, String message) {
		Msg.ResultMessage.Builder builder = Msg.ResultMessage.newBuilder();
		Msg.ResultMessage rtMessage = builder.setMessage(message)
				.setMessageType(type).build();
		Msg.Message.Builder b = Msg.Message.newBuilder();
		Msg.Message m = b.setResultMessage(rtMessage).setMessageType(type)
				.build();
		return m;
	}

	/**
	 * 生成一个带ping/pong消息的消息
	 * 
	 * @param type
	 * @param clientId
	 * @return
	 */
	public static Msg.Message newPingMessage(Msg.MessageType type, String clientId) {
		Msg.PingMessage.Builder bu = Msg.PingMessage.newBuilder();
		Msg.PingMessage rtMessage = bu.setClientId(clientId)
				.setMessageType(type).build();
		Msg.Message.Builder b = Msg.Message.newBuilder();
		Msg.Message m = b.setPingMessage(rtMessage).setMessageType(type)
				.build();
		return m;
	}

	/**
	 * 聊天消息
	 * 
	 * @param fromId
	 * @param toId
	 * @param content
	 * @param token
	 * @param date
	 * @param contentType
	 * @return
	 */
	public static Msg.Message newUUChatMessage(String uuid, int fromId, int toId,
											   String content, String token, boolean transfer, String date,
											   int id, int contentType, String fileGroupName, String path,
											   int status) {
		Msg.ChatMessage chatMessage = Msg.ChatMessage.newBuilder()
				.setContent(content).setFromId(fromId).setToId(toId)
				.setMsgType(Constants.MSG_TYPE_UU).setToken(token)
				.setChatMessageId(id).setDate(date).setTransfer(transfer)
				.setFileGroupName(fileGroupName).setFilePath(path)
				.setStatus(status).setUuid(uuid).setContentType(contentType)
				.build();

		Msg.Message.Builder b = Msg.Message.newBuilder();
		Msg.Message m = b.setChatMessage(chatMessage)
				.setMessageType(Msg.MessageType.CHAT_MESSAGE).build();
		return m;
	}

	/**
	 * 
	 * @param fromId
	 * @param chatGroupId
	 * @param content
	 * @param token
	 * @param transfer
	 * @param date
	 * @param id
	 * @param contentType
	 * @param fileGroupName
	 * @param path
	 * @return
	 */
	public static Msg.Message newUCGChatMessage(String uuid, int fromId,
												int chatGroupId, String content, String token, boolean transfer,
												String date, int id, int contentType, String fileGroupName,
												String path, int status) {
		Msg.ChatMessage chatMessage = Msg.ChatMessage.newBuilder()
				.setContent(content).setFromId(fromId)
				.setChatGroupId(chatGroupId)
				.setMsgType(Constants.MSG_TYPE_UCG).setToken(token)
				.setChatMessageId(id).setDate(date).setTransfer(transfer)
				.setFileGroupName(fileGroupName).setFilePath(path)
				.setUuid(uuid).setStatus(status).setContentType(contentType)
				.build();

		Msg.Message.Builder b = Msg.Message.newBuilder();
		Msg.Message m = b.setChatMessage(chatMessage)
				.setMessageType(Msg.MessageType.CHAT_MESSAGE).build();
		return m;
	}

	/**
	 * 
	 * @param fromId
	 * @param discussionGroupId
	 * @param content
	 * @param token
	 * @param transfer
	 * @param date
	 * @param id
	 * @param contentType
	 * @param fileGroupName
	 * @param path
	 * @return
	 */
	public static Msg.Message newUDGChatMessage(String uuid, int fromId,
												int discussionGroupId, String content, String token,
												boolean transfer, String date, int id, int contentType,
												String fileGroupName, String path, int status) {
		Msg.ChatMessage chatMessage = Msg.ChatMessage.newBuilder()
				.setContent(content).setFromId(fromId)
				.setDiscussionGroupId(discussionGroupId).setUuid(uuid)
				.setMsgType(Constants.MSG_TYPE_UDG).setToken(token)
				.setChatMessageId(id).setDate(date).setTransfer(transfer)
				.setStatus(status).setFileGroupName(fileGroupName)
				.setFilePath(path).setContentType(contentType).build();

		Msg.Message.Builder b = Msg.Message.newBuilder();
		Msg.Message m = b.setChatMessage(chatMessage)
				.setMessageType(Msg.MessageType.CHAT_MESSAGE).build();
		return m;
	}

	/**
	 * 
	 * @param type
	 * @param userId
	 * @param para
	 *            可以为空
	 * @return
	 */
	public static Msg.Message newClientRequestMessage(int type, int userId,
													  String token, String para) {
		Msg.ClientRequestMessage.Builder bu = Msg.ClientRequestMessage
				.newBuilder();
		Msg.ClientRequestMessage rtMessage = bu.setUserId(userId)
				.setToken(token).setRequestType(type).setParameter(para)
				.build();
		Msg.Message.Builder b = Msg.Message.newBuilder();
		Msg.Message m = b.setClientRequestMessage(rtMessage)
				.setMessageType(Msg.MessageType.CLIENT_REQUEST).build();
		return m;
	}

	/**
	 * 文件上传类
	 * 
	 * @param file
	 * @param userId
	 * @param token
	 * @return
	 */
	public static Msg.Message newFileUpload(Attachment file, int userId,
											String token) {
		Msg.FileUpload.Builder bu = Msg.FileUpload.newBuilder();
		Msg.FileUpload f = bu.setGroupName(file.getGroupName())
				.setName(file.getName()).setPath(file.getPath())
				.setSize(file.getSize()).setType(file.getType())
				.setUserId(userId).setToken(token).build();
		Msg.Message.Builder b = Msg.Message.newBuilder();
		Msg.Message m = b.setFileUpload(f).setMessageType(Msg.MessageType.FIEL)
				.build();
		return m;
	}

	/**
	 * 消息回执
	 * 
	 * @param uuid
	 * @param status
	 * @param userId
	 * @param token
	 * @return
	 */
	public static Msg.Message newReceiptMessage(String uuid, int status,
												int userId, String token) {
		Msg.Message.Builder b = Msg.Message.newBuilder();

		Msg.ReceiptMessage.Builder rb = Msg.ReceiptMessage.newBuilder();
		rb.setStatus(status).setUuid(uuid).setUserId(userId).setToken(token);

		Msg.ReceiptMessage rm = rb.build();

		b.setMessageType(Msg.MessageType.RECEIPT);
		b.setReceiptMessage(rm);
		Msg.Message m = b.build();
		return m;
	}
}
