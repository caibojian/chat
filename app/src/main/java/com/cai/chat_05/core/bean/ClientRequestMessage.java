package com.cai.chat_05.core.bean;

public class ClientRequestMessage {
	public final static int FRIEND_LIST = 1;// 好友列表
	public final static int DELETE_FRIENDS = 2;// 删除好友
	public final static int SHIELD_FRIENDS_MESSAGE = 3; // 屏蔽好友消息
	public final static int SHIELD_GROUP_MESSAGE = 4;// 屏蔽群消息
	public final static int REMOVE_USER_FROM_CHAT_GROUP = 5;// 从群中踢人
	public final static int DISSOLUTION_CHAT_GROUP = 6;// 解散群组
	public final static int LEAVE_CHAT_GROUP = 7;// 离开群
	public final static int LEAVE_DISCUSSION_GROUP = 8;// 离开讨论组
	public final static int CHAT_MESSAGE_LIST = 9;// 获取本人消息
	public final static int FRIEND_GROUP_LIST = 10;// 好友分组列表
	public final static int TODO_LIST = 11;// 待办列表
	public final static int CHAT_GROUP_LIST = 12;// 群列表
	public final static int CHAT_GROUP_MEMBER_LIST = 13;// 群成员列表
	public final static int DISCUSSION_GROUP_LIST = 14;// 讨论组列表
	public final static int DISCUSSION_GROUP_MEMBER_LIST = 15;// 讨论组成员列表
	public final static int RELATE_USER_LIST = 16;// 获取与用户相关联的用户
	private int userId;
	private String token;
	private int requestType;
	private String parameter;

	public int getUserId() {
		return userId;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public int getRequestType() {
		return requestType;
	}

	public String getParameter() {
		return parameter;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public void setRequestType(int requestType) {
		this.requestType = requestType;
	}

	public void setParameter(String parameter) {
		this.parameter = parameter;
	}
}
