package com.cai.chat_05;

import java.util.Date;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.cai.chat_05.adppter.ChatMessageAdapter;
import com.cai.chat_05.base.BaseActivity;
import com.cai.chat_05.bean.Attachment;
import com.cai.chat_05.bean.ChatGroup;
import com.cai.chat_05.bean.Constants;
import com.cai.chat_05.bean.DiscussionGroup;
import com.cai.chat_05.bean.Friends;
import com.cai.chat_05.bean.User;
import com.cai.chat_05.cache.CacheManager;
import com.cai.chat_05.core.bean.ChatMessage;
import com.cai.chat_05.emoji.KJEmojiFragment;
import com.cai.chat_05.emoji.OnSendClickListener;
import com.cai.chat_05.fragment.VoiceFragment;
import com.cai.chat_05.service.IoTService;
import com.cai.chat_05.utils.DBHelper;
import com.cai.chat_05.utils.UIHelper;
import com.cai.chat_05.utils.UUIDUtil;
import com.cai.chat_05.view.VoiceButton;

public class ChatActivity extends BaseActivity implements OnSendClickListener,
		OnCheckedChangeListener, VoiceButton.OnSendVoiceListener, OnClickListener {
	public final static int CURRENT_INPUT_TYPE_KEYBOARD = 0;// 键盘文字输入
	public final static int CURRENT_INPUT_TYPE_VOICE = 1;// 语音输入

	private BroadcastReceiver receiver;
	private IoTService.MsgBinder iBinder;

	private User user;
	int userId;
	int chatWithId = 0;
	String chatWithUUId = "";

	private Friends friend;
	private ChatGroup chatGroup;
	private DiscussionGroup discussionGroup;

	private int chatType;
	private Button groupInforButton;

	private ListView chatMeessageListView;
	private ChatMessageAdapter chatMessageAdapter;

	private int currentInputType;
	private List<ChatMessage> chatList;

	private KJEmojiFragment emojiFragment = new KJEmojiFragment();

	private VoiceFragment voiceFragment = new VoiceFragment();

	private ServiceConnection connection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			iBinder = (IoTService.MsgBinder) service;
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			iBinder = null;
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_chat_bak);
		groupInforButton = (Button) this.findViewById(R.id.group_infor);
		Intent intent = getIntent();
		user = (User) CacheManager.readObject(ChatActivity.this,
				Constants.CACHE_CURRENT_USER);

		chatType = intent.getIntExtra(Constants.INTENT_EXTRA_CHAT_TYPE, 0);
		switch (chatType) {
		case Constants.MSG_TYPE_UU:
			friend = (Friends) intent
					.getSerializableExtra(Constants.INTENT_EXTRA_CHAT_FRIEND);
			break;
		case Constants.MSG_TYPE_UCG:

			chatGroup = (ChatGroup) intent
					.getSerializableExtra(Constants.INTENT_EXTRA_CHAT_CHAT_GROUP);
			groupInforButton.setVisibility(View.VISIBLE);
			groupInforButton.setOnClickListener(this);
			break;
		case Constants.MSG_TYPE_UDG:
			groupInforButton.setVisibility(View.VISIBLE);
			discussionGroup = (DiscussionGroup) intent
					.getSerializableExtra(Constants.INTENT_EXTRA_CHAT_DISCUSSION_GROUP);
			groupInforButton.setVisibility(View.VISIBLE);
			groupInforButton.setOnClickListener(this);
			break;
		}

		initViews();
		initEvents();

		Intent intent1 = new Intent(this, IoTService.class);
		bindService(intent1, connection, Context.BIND_AUTO_CREATE);

		// 注册监听消息
		receiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				String key = intent.getAction();
				switch (key) {
				case Constants.INTENT_ACTION_RECEIVE_CHAT_MESSAGE:
					Log.v("com.caibojian.chat_05.ChatActivity", "收到聊天信息");
					ChatMessage chatMessage = (ChatMessage) intent
							.getSerializableExtra(Constants.INTENT_EXTRA_CHAT_MESSAGE);
					chatList = DBHelper.getgetInstance(ChatActivity.this)
							.getChatMessageByPage(userId, chatWithId, chatType,
									200);
					chatMessageAdapter.setData(chatList);
					chatMessageAdapter.notifyDataSetChanged();
					chatMeessageListView.setSelection(chatList.size());
					// 更新消息为已读
//					DBHelper.getgetInstance(ChatActivity.this)
//							.updateChatMessageChecked(userId,
//									chatMessage.getFromId());
					DBHelper.getgetInstance(ChatActivity.this)
							.updateChatMessageChecked(userId,
									chatWithId);

					break;
				case Constants.INTENT_ACTION_RECEIVE_RECEIPT_MESSAGE:
					chatList = DBHelper.getgetInstance(ChatActivity.this)
							.getChatMessageByPage(userId, chatWithId, chatType,
									200);
					chatMessageAdapter.setData(chatList);
					chatMessageAdapter.notifyDataSetChanged();
					chatMeessageListView.setSelection(chatList.size());
				case Constants.INTENT_ACTION_VOICE_MSG_DOWLOAD:
					chatList = DBHelper.getgetInstance(ChatActivity.this)
							.getChatMessageByPage(userId, chatWithId, chatType,
									200);
					chatMessageAdapter.setData(chatList);
					chatMessageAdapter.notifyDataSetChanged();
					chatMeessageListView.setSelection(chatList.size());
					break;
				default:
					;
				}
			}
		};
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Constants.INTENT_ACTION_RECEIVE_CHAT_MESSAGE);
		intentFilter.addAction(Constants.INTENT_ACTION_RECEIVE_RECEIPT_MESSAGE);
		intentFilter.addAction(Constants.INTENT_ACTION_VOICE_MSG_DOWLOAD);
		this.registerReceiver(receiver, intentFilter);
		emojiFragment.setOnSendClickListener(this);
		voiceFragment.setOnSendClickListener(this);
		voiceFragment.setOnSendVoiceListener(this);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.emoji_keyboard, emojiFragment).commit();
		currentInputType = CURRENT_INPUT_TYPE_KEYBOARD;

	}

	@Override
	public void onClickSendButton(Editable text) {

		String content = text.toString();
		// 空消息不发送，不包括空格。
		if (content == null || content.isEmpty()) {
			return;
		}

		ChatMessage chatMessage = new ChatMessage();
		chatMessage.setContent(content);

		chatMessage.setFromId(user.getId());
		chatMessage.setDate(new Date());
		chatMessage.setType(Constants.TYPE_SEND);
		chatMessage.setContentType(Constants.CONTENT_TYPE_NORMAL);
		int toId = 0;

		switch (chatType) {
		case Constants.MSG_TYPE_UU:
			chatMessage.setMsgType(Constants.MSG_TYPE_UU);
			chatMessage.setToId(friend.getUserId());
			toId = friend.getUserId();
			break;
		case Constants.MSG_TYPE_UCG:
			chatMessage.setMsgType(Constants.MSG_TYPE_UCG);
			chatMessage.setChatGroupId(chatGroup.getId());
			toId = chatGroup.getId();
			break;
		case Constants.MSG_TYPE_UDG:
			chatMessage.setMsgType(Constants.MSG_TYPE_UDG);
			chatMessage.setDiscussionGroupId(discussionGroup.getId());
			toId = discussionGroup.getId();
			break;
		}

		chatMessage.setWhoId(user.getId());
		chatMessage.setChecked(true);
		String uuid = UUIDUtil.uuid();
		chatMessage.setUuid(uuid);
		DBHelper.getgetInstance(ChatActivity.this).addChatMessage(chatMessage,
				user.getId());

		try {

			iBinder.sendMessage(uuid, Constants.CONTENT_TYPE_NORMAL,
					content, toId, chatType, "", "", chatMessage);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		if (isMyMessage(chatMessage)) {
			if (!compareTo(chatList, chatMessage)) {
				chatList.add(chatMessage);
			}
		}
		chatMessageAdapter.notifyDataSetChanged();
		chatMeessageListView.setSelection(chatList.size());
	}

	@Override
	public void onClickFlagButton() {
		switch (currentInputType) {
		case CURRENT_INPUT_TYPE_KEYBOARD:
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.emoji_keyboard, voiceFragment).commit();
			currentInputType = CURRENT_INPUT_TYPE_VOICE;
			break;
		case CURRENT_INPUT_TYPE_VOICE:
			emojiFragment.setOnSendClickListener(this);
			// 修复再次替换时表情不显示bug
			emojiFragment.setAdapter(getSupportFragmentManager());
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.emoji_keyboard, emojiFragment).commit();
			currentInputType = CURRENT_INPUT_TYPE_KEYBOARD;
			chatMeessageListView.setSelection(chatList.size());
			break;
		default:
			break;
		}
	}

	@Override
	protected void initViews() {
		TextView title = (TextView) findViewById(R.id.title_bar);

		switch (chatType) {
		case Constants.MSG_TYPE_UU:
			title.setText(friend.getName());
			break;
		case Constants.MSG_TYPE_UCG:
			title.setText(chatGroup.getName());
			break;
		case Constants.MSG_TYPE_UDG:
			title.setText(discussionGroup.getName());
			break;
		}
		chatMeessageListView = (ListView) findViewById(R.id.chat_Listview);
	}

	@Override
	protected void initEvents() {

		userId = user.getId();
		chatWithId = 0;
		switch (chatType) {
		case Constants.MSG_TYPE_UU:
			chatWithId = friend.getUserId();
			break;
		case Constants.MSG_TYPE_UCG:
			chatWithId = chatGroup.getId();
			break;
		case Constants.MSG_TYPE_UDG:
			chatWithId = discussionGroup.getId();
			break;
		}
		chatList = DBHelper.getgetInstance(this).getChatMessageByPage(userId,
				chatWithId, chatType, 200);

		chatMessageAdapter = new ChatMessageAdapter(ChatActivity.this, chatList);
		chatMeessageListView.setAdapter(chatMessageAdapter);
		chatMeessageListView.setSelection(chatList.size());
	}



	@Override
	protected void onDestroy() {
		this.unregisterReceiver(receiver);
		this.unbindService(connection);
		super.onDestroy();

	}

	/**
	 * 是这个activity该接受的消息
	 * 
	 * @param message
	 * @return
	 */
	private boolean isMyMessage(ChatMessage message) {
		int toId = 0;
		if (message.getMsgType() != chatType) {
			return false;
		}
//		try {
//			switch (chatType) {
//			case ChatMessage.MSG_TYPE_UU:
////				if ((message.getToId() == iBinder.getUserId()
////						&& message.getType() == ChatMessage.TYPE_RECEIVE && message
////						.getFromId() == friend.getUserId())
////						|| (message.getFromId() == iBinder.getUserId()
////								&& message.getType() == ChatMessage.TYPE_SEND && message
////								.getToId() == friend.getUserId())) {
////					return true;
////				}
//				break;
//			case ChatMessage.MSG_TYPE_UCG:
////				if ((message.getToId() == iBinder.getUserId()
////						&& message.getType() == ChatMessage.TYPE_RECEIVE && message
////						.getChatGroupId() == chatGroup.getId())
////						|| (message.getFromId() == iBinder.getUserId()
////								&& message.getType() == ChatMessage.TYPE_SEND && message
////								.getChatGroupId() == chatGroup.getId())) {
////					return true;
////				}
//				break;
//			case ChatMessage.MSG_TYPE_UDG:
////				if ((message.getToId() == iBinder.getUserId()
////						&& message.getType() == ChatMessage.TYPE_RECEIVE && message
////						.getDiscussionGroupId() == discussionGroup.getId())
////						|| (message.getFromId() == iBinder.getUserId()
////								&& message.getType() == ChatMessage.TYPE_SEND && message
////								.getDiscussionGroupId() == discussionGroup
////								.getId())) {
////					return true;
////				}
//				break;
//			}
//
//		} catch (RemoteException e) {
//			e.printStackTrace();
//		}
		return false;
	}

	protected boolean compareTo(List<ChatMessage> data, ChatMessage enity) {
		int s = data.size();
		if (enity != null) {
			for (int i = 0; i < s; i++) {
				if (enity.getUuid().equals(data.get(i).getUuid())) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

	}

	@Override
	public void onSend(Attachment a) {
//		try {
//			iBinder.sendAttachment(a.getId());
//		} catch (RemoteException e1) {
//			e1.printStackTrace();
//		}
		ChatMessage chatMessage = new ChatMessage();
		// / a = DBHelper.getgetInstance(ChatActivity.this).addAttachment(a);

		chatMessage.setContent("");

		chatMessage.setFromId(user.getId());
		int toId = 0;

		switch (chatType) {
		case Constants.MSG_TYPE_UU:
			chatMessage.setMsgType(Constants.MSG_TYPE_UU);
			chatMessage.setToId(friend.getUserId());
			toId = friend.getUserId();
			break;
		case Constants.MSG_TYPE_UCG:
			chatMessage.setMsgType(Constants.MSG_TYPE_UCG);
			chatMessage.setChatGroupId(chatGroup.getId());
			toId = chatGroup.getId();
			break;
		case Constants.MSG_TYPE_UDG:
			chatMessage.setMsgType(Constants.MSG_TYPE_UDG);
			chatMessage.setDiscussionGroupId(discussionGroup.getId());
			toId = discussionGroup.getId();
			break;
		}

		chatMessage.setDate(new Date());
		chatMessage.setType(Constants.TYPE_SEND);
		chatMessage.setWhoId(user.getId());
		chatMessage.setContentType(Constants.CONTENT_TYPE_ATTACHMENT);
		chatMessage.setChecked(true);
		chatMessage.setFileGroupName(a.getGroupName());
		chatMessage.setFilePath(a.getPath());
		chatMessage.setAttachmentId(a.getId());

		String uuid = UUIDUtil.uuid();
		chatMessage.setUuid(uuid);
		DBHelper.getgetInstance(ChatActivity.this).addChatMessage(chatMessage,
				user.getId());

//		try {
//			iBinder.sendMessage(uuid,
//					ChatMessage.CONTENT_TYPE_ATTACHMENT, "", toId, chatType,
//					a.getGroupName(), a.getPath());
//		} catch (RemoteException e) {
//			e.printStackTrace();
//		}
		if (isMyMessage(chatMessage)) {
			if (!compareTo(chatList, chatMessage)) {
				chatList.add(chatMessage);
			}
		}
		chatMessageAdapter.notifyDataSetChanged();
		chatMeessageListView.setSelection(chatList.size());
	}

	public IoTService.MsgBinder getSessionService() {
		return iBinder;
	}

	@Override
	public void onClick(View v) {
		switch (chatType) {
		case Constants.MSG_TYPE_UCG:
			UIHelper.startChatGroupInforActivity(this, chatGroup.getId(),
					Constants.INTENT_EXTRA_CHATGROUP_INFOR_TYPE_LEAVECHATGROUP);
			break;
		case Constants.MSG_TYPE_UDG:

			break;
		}

	}
}
