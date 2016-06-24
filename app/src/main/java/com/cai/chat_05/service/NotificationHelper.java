package com.cai.chat_05.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat.Builder;
import android.widget.RemoteViews;

import com.cai.chat_05.ChatActivity;
import com.cai.chat_05.R;
import com.cai.chat_05.bean.Constants;
import com.cai.chat_05.bean.Friends;
import com.cai.chat_05.core.bean.ChatMessage;

import java.util.HashMap;
import java.util.Map;

public class NotificationHelper {

	private Context mContext;
	private static NotificationHelper mNotificationHelper;
	private NotificationManager mNotificationManager;
	private Map<Integer,Notification> notificationMap = new HashMap<Integer, Notification>();

	NotificationHelper(Context context) {
		mContext = context;
		mNotificationManager = (NotificationManager) mContext
				.getSystemService(Context.NOTIFICATION_SERVICE);
	}

	public static NotificationHelper getInstance(Context context) {

		if (mNotificationHelper == null) {
			mNotificationHelper = new NotificationHelper(context);
		}
		return mNotificationHelper;

	}

//	/**
//	 * 显示代办通知，好友申请，加群申请
//	 */
//	public void showTodoNotify(Todo todo, int userId, String token) {
//		Builder mBuilder = new Builder(mContext);
//		RemoteViews mRemoteViews = new RemoteViews(mContext.getPackageName(),
//				R.layout.chatmassage_notification_item);
//		mRemoteViews.setImageViewResource(R.id.user_photo,
//				R.drawable.channel_qq);
//		// API3.0 以上的时候显示按钮，否则消失
//		mRemoteViews.setTextViewText(R.id.subject, todo.getTodoSubject());
//		mRemoteViews.setTextViewText(R.id.request_msg, todo.getRequestMsg());
//
//		// 点击的事件处理
//		Intent buttonIntent = new Intent(Constants.INTENT_ACTION_HANDING_TODO);
//		buttonIntent.putExtra(Constants.INTENT_EXTRA_HANDING_TODO_ID,
//				todo.getTodoId());
//
//		PendingIntent intent_paly = PendingIntent.getBroadcast(mContext, 2,
//				buttonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//		mRemoteViews.setOnClickPendingIntent(R.id.notification, intent_paly);
//
//		// textStyle.bigText("好顶顶顶顶顶顶顶顶顶顶顶");
//		mBuilder.setContent(mRemoteViews)
//				.setContentIntent(
//						getDefalutIntent(Notification.FLAG_ONGOING_EVENT))
//				.setWhen(System.currentTimeMillis())
//				// 通知产生的时间，会在通知信息里显示
//				.setTicker("好友请求").setPriority(Notification.PRIORITY_DEFAULT)// 设置该通知优先级
//				.setOngoing(false).setSmallIcon(R.drawable.icon);
//
//		Notification notify = mBuilder.build();
//		notify.bigContentView = mRemoteViews;
//		notify.flags = Notification.FLAG_AUTO_CANCEL;
//		// 会报错，还在找解决思路
//		// notify.contentView = mRemoteViews;
//		// notify.contentIntent = PendingIntent.getActivity(this, 0, new
//		// Intent(), 0);
//		mNotificationManager.notify(todo.getTodoId(), notify);
//
//	}

	/**
	 * 显示好友聊天消息
	 */
	public void showChatMessageNotify(ChatMessage chatMessage, Friends friend) {
		Builder mBuilder = new Builder(mContext);
		RemoteViews mRemoteViews = new RemoteViews(mContext.getPackageName(),
				R.layout.chatmassage_notification_item);
		mRemoteViews.setImageViewResource(R.id.user_photo,
				R.drawable.channel_qq);
		// API3.0 以上的时候显示按钮，否则消失
		mRemoteViews.setTextViewText(R.id.subject, friend.getName());
		mRemoteViews.setTextViewText(R.id.request_msg, chatMessage.getContent());

		// 点击的事件处理
		Intent intent = new Intent();
		intent.setClass(mContext, ChatActivity.class);
		intent.putExtra(Constants.INTENT_EXTRA_CHAT_FRIEND, friend);

		PendingIntent intent_paly = PendingIntent.getBroadcast(mContext, 2,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);
		mRemoteViews.setOnClickPendingIntent(R.id.notification, intent_paly);

		// textStyle.bigText("好顶顶顶顶顶顶顶顶顶顶顶");
		mBuilder.setContent(mRemoteViews)
				.setContentIntent(
						getDefalutIntent(Notification.FLAG_ONGOING_EVENT))
				.setWhen(System.currentTimeMillis())
				// 通知产生的时间，会在通知信息里显示
				.setTicker("好友请求").setPriority(Notification.PRIORITY_DEFAULT)// 设置该通知优先级
				.setOngoing(false).setSmallIcon(R.drawable.icon);

		Notification notify = mBuilder.build();
		notify.bigContentView = mRemoteViews;
		notify.flags = Notification.FLAG_AUTO_CANCEL;
		notify.defaults = Notification.DEFAULT_SOUND;
		// 会报错，还在找解决思路
		// notify.contentView = mRemoteViews;
		// notify.contentIntent = PendingIntent.getActivity(this, 0, new
		// Intent(), 0);
		mNotificationManager.notify(friend.getId(), notify);
		notificationMap.put(friend.getId(),notify);

	}

	public PendingIntent getDefalutIntent(int flags) {
		PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 1,
				new Intent(), flags);
		return pendingIntent;
	}
}
