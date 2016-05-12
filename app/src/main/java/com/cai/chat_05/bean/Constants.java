package com.cai.chat_05.bean;

/**
 * 常量类
 * 
 * @author FireAnt（http://my.oschina.net/LittleDY）
 * @version 创建时间：2014年10月27日 下午12:14:42
 * 
 */

public class Constants {

	public static final String INTENT_ACTION_USER_CHANGE = "org.weishe.action.USER_CHANGE";

	public static final String INTENT_ACTION_COMMENT_CHANGED = "org.weishe.action.COMMENT_CHANGED";

	public static final String INTENT_ACTION_NOTICE = "org.weishe.action.APPWIDGET_UPDATE";

	public static final String INTENT_ACTION_LOGOUT = "org.weishe.action.LOGOUT";

	/**
	 * 当前登录用户
	 */
	public static final String CACHE_CURRENT_USER = "org_weishe_weichat_auth_user";
	/**
	 * 当前登录用户
	 */
	public static final String CACHE_CURRENT_USER_TOKEN = "org_weishe_weichat_auth_user_token";
	public static final String CACHE_CURRENT_CLIENT_ID = "org_weishe_weichat_auth_client_id";

	/**
	 * 通知栏操作todo
	 */
	public static final String INTENT_ACTION_HANDING_TODO = "org.weishe.action.todo.handing";

	/**
	 * 操作类型参数名
	 */
	public static final String INTENT_ACTION_HANDING_TODO_TYPE = "org.weishe.action.todo.handing.type";
	public static final int INTENT_EXTRA_HANDING_TODO_TYPE_AGREE = 1;
	public static final int INTENT_EXTRA_HANDING_TODO_TYPE_REFUSE = 2;

	public static final String INTENT_EXTRA_HANDING_TODO_ID = "org.weishe.action.todo.handing.todo.id";

	public static final String WEICHAT_APPID = "wx41be5fe48092e94c";
	public static final String WEICHAT_SECRET = "0101b0595ffe2042c214420fac358abc";

	public static final String QQ_APPID = "100942993";
	public static final String QQ_APPKEY = "8edd3cc7ca8dcc15082d6fe75969601b";

	/**
	 * 远程服务端ip
	 */
	public static final String INTENT_EXTRA_SERVER_IP = "org.weishe.remote.server.ip";
	/**
	 * 远程服务端端口
	 */
	public static final String INTENT_EXTRA_SERVER_PORT = "org.weishe.remote.server.port";
	/**
	 * 登录用户
	 */
	public static final String INTENT_EXTRA_USER = "org.weishe.longin.user";

	public static final String INTENT_EXTRA_FRIEND_LIST = "org.weishe.user.friends";

	public static final String INTENT_EXTRA_CHAT_MESSAGE = "org.weishe.chat.message";

	/**
	 * 语音消息
	 */
	public static final String INTENT_EXTRA_VOICE_MESSAGE = "org.weishe.chat.voice.message";

	public static final String INTENT_EXTRA_CHAT_FRIEND = "org.weishe.chat.friend";

	public static final String INTENT_EXTRA_MAIN_START_TYPE = "org.weishe.weichat.main.start.type";
	/**
	 * 用户认证token
	 */
	public static final String INTENT_EXTRA_TOKEN = "org.weishe.auth.token";
	public static final String INTENT_EXTRA_USER_ID = "org.weishe.auth.user.id";
	public static final String INTENT_EXTRA_CHAT_GROUP_ID = "org.weishe.chat.group.id";
	public static final String INTENT_EXTRA_MY_ID = "org.weishe.auth.user.my.id";
	public static final String INTENT_EXTRA_TO_USER_ID = "org.weishe.touser.id";

	/**
	 * 接收者，有可能是用户有可能是群，讨论组等等
	 */
	public static final String INTENT_EXTRA_TO_ID = "org.weishe.to.id";

	public static final String INTENT_EXTRA_USER_INFOR_TYPE = "org.weishe.auth.user.infor.type";
	public final static String INTENT_EXTRA_USER_INFOR_TYPE_USERINFOR = "org.weishe.auth.user.infor.type.userinfor";
	public final static String INTENT_EXTRA_USER_INFOR_TYPE_ADD_FRIENDS = "org.weishe.auth.user.infor.type.addfriends";

	public static final String INTENT_EXTRA_CHATGROUP_INFOR_TYPE = "org.weishe.chat.group.infor.type";
	public final static String INTENT_EXTRA_CHATGROUP_INFOR_TYPE_LEAVECHATGROUP = "org.weishe.chat.group.infor.type.chatgroupinfor";
	public final static String INTENT_EXTRA_CHATGROUP_INFOR_TYPE_JOINCHATGROUP = "org.weishe.chat.group.infor.type.joinchatgroup";

	public static final String INTENT_SERVICE_SESSION = "org.weishe.weichat.session";

	/**
	 * 接收到好友列表
	 */
	public static final String INTENT_ACTION_RECEIVE_FRIEND_LIST = "org.weishe.weichat.action.receive.friendlist";
	/**
	 * 接收到好友分组列表
	 */
	public static final String INTENT_ACTION_RECEIVE_FRIEND_GROUP_LIST = "org.weishe.weichat.action.receive.friendgrouplist";

	/**
	 * 接收到好友消息
	 */
	public static final String INTENT_ACTION_RECEIVE_CHAT_MESSAGE = "org.weishe.weichat.action.receive.chatmessage";

	/**
	 * 接收到好友消息
	 */
	public static final String INTENT_ACTION_RECEIVE_CHAT_MESSAGE_LIST = "org.weishe.weichat.action.receive.chatmessage.list";
	/**
	 * 接收到待办消息
	 */
	public static final String INTENT_ACTION_RECEIVE_TODO_LIST = "org.weishe.weichat.action.receive.todo.list";

	/**
	 * 发送语音消息消息
	 */
	public static final String INTENT_ACTION_SEND_CHAT_MESSAGE = "org.weishe.weichat.action.send.chatmessage";
	/**
	 * 发送语音消息消息
	 */
	public static final String INTENT_ACTION_SEND_VOICE_MESSAGE1 = "org.weishe.weichat.action.send.voicemessage";

	/**
	 * 收到消息回执
	 */
	public static final String INTENT_ACTION_RECEIVE_RECEIPT_MESSAGE = "org.weishe.weichat.action.receive.receiptmessage";
	/**
	 * 语音消息下载完成
	 */
	public static final String INTENT_ACTION_VOICE_MSG_DOWLOAD = "org.weishe.weichat.voice.msg.download";
	/**
	 * 好友
	 */
	public static final String INTENT_EXTRA_FRIENDS = "org.weishe.weichat.extra.friends";

	/**
	 * 刷新好友列表
	 */
	public static final String INTENT_ACTION_REFRESH_FRIENDS_DATA = "org.weishe.weichat.refresh_friends_data";

	public static final String INTENT_EXTRA_MSG_TYPE = "org.weishe.weichat.msg.msgtype";

	public static final String INTENT_EXTRA_CHAT_TYPE = "org.weishe.weichat.msg.chattype";

	public static final String INTENT_EXTRA_CHAT_CHAT_GROUP = "org.weishe.chat.chatgroup";

	public static final String INTENT_EXTRA_CHAT_DISCUSSION_GROUP = "org.weishe.chat.discussiongroup";

	public static final String CACHE_CURRENT_MAX_MESSAGE_ID = "org.weishe.chat.current.max.messageid";

	public static final String CACHE_CURRENT_MAX_TODO_ID = "org.weishe.chat.current.max.todoid";

	public static final String CACHE_CURRENT_MESSAGE_LIST = "org.weishe.chat.current.messagelist";

	//消息类型
	/**
	 * 发送消息
	 */
	public final static int TYPE_SEND = 0;
	/**
	 * 接收消息
	 */
	public final static int TYPE_RECEIVE = 1;

	public final static int MSG_TYPE_UU = 0;// 人人之间的消息
	public final static int MSG_TYPE_UCG = 1;// 群消息
	public final static int MSG_TYPE_UDG = 2;// 讨论组消息

	public final static int CONTENT_TYPE_NORMAL = 0;// 普通消息
	public final static int CONTENT_TYPE_ATTACHMENT = 1;// 为带附件消息

	public final static int STATUS_SENDING = -1;// 发送中
	public final static int STATUS_UNKNOWN = 0;// 未知状态，在服务端不会出现，在客户端会出现
	public final static int STATUS_SEND = 1;// 默认状态已发送
	public final static int STATUS_RECEIVED = 2;// 已收到
	public final static int STATUS_READ = 3;// 已阅读

}
