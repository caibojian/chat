package com.cai.chat_05.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;


import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.cai.chat_05.MainActivity;
import com.cai.chat_05.R;
import com.cai.chat_05.adppter.MessageListAdapter;
import com.cai.chat_05.bean.ChatGroup;
import com.cai.chat_05.bean.Constants;
import com.cai.chat_05.bean.DiscussionGroup;
import com.cai.chat_05.bean.Friends;
import com.cai.chat_05.bean.Todo;
import com.cai.chat_05.bean.User;
import com.cai.chat_05.cache.CacheManager;
import com.cai.chat_05.core.bean.ChatMessage;
import com.cai.chat_05.menu.creator.MessageListItemSwipeMenuCreator;
import com.cai.chat_05.receiver.BaseFragment;
import com.cai.chat_05.utils.DBHelper;
import com.cai.chat_05.utils.UIHelper;
import com.cai.chat_05.view.EmptyLayout;
import com.cai.chat_05.view.TitleBarView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class MessageListFragment extends BaseFragment implements
		SwipeRefreshLayout.OnRefreshListener {
	private MainActivity mContext;
	private View mBaseView;
//
	@InjectView(R.id.title_bar)
	protected TitleBarView mTitleBarView;
//
	@InjectView(R.id.swiperefreshlayout)
	protected SwipeRefreshLayout mSwipeRefreshLayout;
//
	@InjectView(R.id.recyclerview)
	protected SwipeMenuListView mSwipeMenuListView;
//
	@InjectView(R.id.error_layout)
	protected EmptyLayout mErrorLayout;
//
	private MessageListAdapter adapter;
	private List<ChatGroup> chatGroups;
	private List<DiscussionGroup> discussionGroups;
	private User user;

	private boolean firstFlag = true;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		mContext = (MainActivity) getActivity();
		super.onCreate(savedInstanceState);
		user = (User) CacheManager.readObject(mContext,
				Constants.CACHE_CURRENT_USER);
		chatGroups = (List<ChatGroup>) CacheManager.readObject(mContext,
				ChatGroup.getCacheKey(user.getId()));
		discussionGroups = (List<DiscussionGroup>) CacheManager.readObject(
				mContext, DiscussionGroup.getCacheKey(user.getId()));

		Log.v("org.weishe.l", "onCreate:" + this);
		requestData(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {

		mBaseView = inflater.inflate(R.layout.fragment_message, container,
				false);
		Log.v("org.weishe.l", "onCreateView:" + this);
		return mBaseView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		ButterKnife.inject(this, view);
		init();

		Log.v("caibojian1", "onViewCreated:" + this);
	}

	private void init() {
		// 设置上方按钮
		mTitleBarView.setTitleLeft(R.string.message_list_message);
		mTitleBarView.setTitleRight(R.string.message_list_call);
		mTitleBarView.setLeftRightButtonVisibility(View.GONE);
//		// 设置下拉刷新
		mSwipeRefreshLayout.setOnRefreshListener(this);
		mSwipeRefreshLayout.setColorSchemeResources(
				R.color.swiperefresh_color1, R.color.swiperefresh_color2,
				R.color.swiperefresh_color3, R.color.swiperefresh_color4);
		mTitleBarView.setBtnLeft(R.string.control);
		mTitleBarView.setBtnRight(R.drawable.qq_constact);
//
		mTitleBarView.setBtnRightOnclickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
//				 Intent intent = new Intent(mContext,
//				 SearchFriendActivity.class);
//				 startActivity(intent);
			}
		});
//
//		// 新的控件
		adapter = new MessageListAdapter(mContext);

		mSwipeMenuListView.setAdapter(adapter);
//
//		// step 1. create a MenuCreator
		MessageListItemSwipeMenuCreator creator = new MessageListItemSwipeMenuCreator(
				mContext, this, adapter);

//		// set creator
		mSwipeMenuListView.setMenuCreator(creator);
//
//		// step 2. listener item click event
		mSwipeMenuListView.setOnMenuItemClickListener(creator);
		mSwipeMenuListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
				Object o = adapter.getData().get(position);

				if (o instanceof ChatMessage) {
					ChatMessage msg = (ChatMessage) o;
					switch (msg.getMsgType()) {
					case Constants.MSG_TYPE_UU:

						Friends friend = null;
						for (Friends friends : mContext.getFriends()) {
							switch (msg.getType()) {
							case Constants.TYPE_RECEIVE:
								if (friends.getUserId() == msg.getFromId()) {
									friend = friends;
								}
								break;
							case Constants.TYPE_SEND:
								if (friends.getUserId() == msg.getToId()) {
									friend = friends;
								}
								break;
							default:
								break;
							}
						}
						if (friend != null) {
							 //将所有与该人聊天的记录都至为已读
							DBHelper.getgetInstance(mContext)
									.updateChatMessageChecked(user.getId(),
											friend.getUserId());
							// rushDBData();
							UIHelper.startChatActivity(mContext,
									Constants.MSG_TYPE_UU, friend, null, null);
						} else {
							Log.v("org.weishe.weichat", "好友不存在！");
						}
						break;
					case Constants.MSG_TYPE_UCG:
						ChatGroup cg = null;
						for (ChatGroup g : chatGroups) {
							if (msg.getChatGroupId() == g.getId()) {
								cg = g;
								break;
							}
						}
						if (cg != null) {
//							UIHelper.startChatActivity(mContext,
//									ChatMessage.MSG_TYPE_UCG, null, cg, null);
						}
						break;
					case Constants.MSG_TYPE_UDG:
//						DiscussionGroup dg = null;
//						for (DiscussionGroup g : discussionGroups) {
//							if (msg.getDiscussionGroupId() == g.getId()) {
//								dg = g;
//								break;
//							}
//						}
//						if (dg != null) {
//							UIHelper.startChatActivity(mContext,
//									ChatMessage.MSG_TYPE_UCG, null, null, dg);
//						}
						break;
					}
				}
			}
		});
		if (!firstFlag) {
			requestData(false);
		}
		firstFlag = false;
	}

	/**
	 * 去数据库刷新数据
	 */
	public void rushDBData() {
//		List data = DBHelper.getgetInstance(mContext).getRecentMessage(
//				user.getId());
		List data = DBHelper.getgetInstance(mContext).getRecentMessage(
				user.getId());
		CacheManager.saveObject(mContext, data,
				Constants.CACHE_CURRENT_MESSAGE_LIST + "_" + user.getId());
		Log.v("MessageListFragment", "查询消息数据:" + data.size());
		adapter.setData(data);
		adapter.notifyDataSetChanged();
		mState = STATE_NONE;
		setSwipeRefreshLoadedState();
	}

	private void requestData(boolean refresh) {

		// 获取缓存数据
		if (!refresh) {
			List data = (List) CacheManager.readObject(mContext,
					Constants.CACHE_CURRENT_MESSAGE_LIST + "_" + user.getId());
			adapter.setData(data);
			adapter.notifyDataSetChanged();
			mState = STATE_NONE;
			setSwipeRefreshLoadedState();
			return;
		}

		// 先查询本地数据消息
		int fromMessageId = 0;
		int todoFid = 0;
		if (mContext.getSessionService() != null) {

			Object o = CacheManager
					.readObject(
							mContext,
							Constants.CACHE_CURRENT_MAX_MESSAGE_ID + "_"
									+ user.getId());

			Object o1 = CacheManager.readObject(mContext,
					Constants.CACHE_CURRENT_MAX_TODO_ID + "_" + user.getId());

			if (o != null) {
				fromMessageId = (int) o;
			}
			if (o1 != null) {
				todoFid = (int) o1;
			}
			if (fromMessageId < 1) {
				fromMessageId = DBHelper.getgetInstance(mContext)
						.getMaxMessageIdByUserId(user.getId());
				CacheManager.saveObject(
						mContext,
						fromMessageId,
						Constants.CACHE_CURRENT_MAX_MESSAGE_ID + "_"
								+ user.getId());
			}
			if (todoFid < 1) {
				todoFid = DBHelper.getgetInstance(mContext)
						.getMaxTodoIdByUserId(user.getId());
				CacheManager.saveObject(
						mContext,
						todoFid,
						Constants.CACHE_CURRENT_MAX_TODO_ID + "_"
								+ user.getId());
			}
			// 取新的数据
//			try {
//				if (mContext.getSessionService() != null) {
//					mContext.getSessionService().getMessageList(fromMessageId);
//					mContext.getSessionService().getTodoList(todoFid);
//				}
//			} catch (RemoteException e) {
//				e.printStackTrace();
//			}
		}
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.v("org.weishe.weichat", "收到获取到消息广播消息！aaaa");

		String key = intent.getAction();
		String message = intent.getStringExtra("message");
		switch (key) {
		case Constants.INTENT_ACTION_SEND_CHAT_MESSAGE:
		case Constants.INTENT_ACTION_RECEIVE_TODO_LIST:
		case Constants.INTENT_ACTION_RECEIVE_CHAT_MESSAGE_LIST:
			Log.v("org.weishe.weichat", "收到获取到消息广播消息！"+message);
			List data = DBHelper.getgetInstance(mContext).getRecentMessage(
					user.getId());
			Log.v("org.weishe.weichat", "收到获取到消息广播消息！消息大小"+data.size());
			CacheManager.saveObject(mContext, data,
					Constants.CACHE_CURRENT_MESSAGE_LIST + "_" + user.getId());
			adapter.setData(data);
			adapter.notifyDataSetChanged();
			mState = STATE_NONE;
			setSwipeRefreshLoadedState();
			// 更新当前maxid
			int fromMessageId = DBHelper.getgetInstance(mContext)
					.getMaxMessageIdByUserId(user.getId());
			CacheManager
					.saveObject(
							mContext,
							fromMessageId,
							Constants.CACHE_CURRENT_MAX_MESSAGE_ID + "_"
									+ user.getId());

			break;
		case Constants.INTENT_ACTION_RECEIVE_CHAT_MESSAGE:
			List data2 = null;

			data2 = DBHelper.getgetInstance(mContext).getRecentMessage(
					user.getId());
			CacheManager.saveObject(mContext, data2,
					Constants.CACHE_CURRENT_MESSAGE_LIST + "_" + user.getId());
			adapter.setData(data2);
			adapter.notifyDataSetChanged();
			mState = STATE_NONE;
			setSwipeRefreshLoadedState();

			int todoFid = DBHelper.getgetInstance(mContext)
					.getMaxTodoIdByUserId(user.getId());
			CacheManager.saveObject(mContext, todoFid,
					Constants.CACHE_CURRENT_MAX_TODO_ID + "_" + user.getId());
			break;
		}

	}

	@Override
	public void registerReceiver(BroadcastReceiver receiver) {
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Constants.INTENT_ACTION_RECEIVE_CHAT_MESSAGE_LIST);
		intentFilter.addAction(Constants.INTENT_ACTION_RECEIVE_CHAT_MESSAGE);
		intentFilter.addAction(Constants.INTENT_ACTION_SEND_CHAT_MESSAGE);
		intentFilter.addAction(Constants.INTENT_ACTION_RECEIVE_TODO_LIST);
		mContext.registerReceiver(receiver, intentFilter);
	}

	@Override
	public void unRegisterReceiver(BroadcastReceiver receiver) {
		mContext.unregisterReceiver(receiver);
	}

	/** 设置顶部正在加载的状态 */
	private void setSwipeRefreshLoadingState() {
		if (mSwipeRefreshLayout != null) {
			mSwipeRefreshLayout.setRefreshing(true);
			// 防止多次重复刷新
			mSwipeRefreshLayout.setEnabled(false);
		}
	}

	/** 设置顶部加载完毕的状态 */
	private void setSwipeRefreshLoadedState() {
		if (mSwipeRefreshLayout != null) {
			mSwipeRefreshLayout.setRefreshing(false);
			mSwipeRefreshLayout.setEnabled(true);
		}
	}

	// 下拉刷新数据
	@Override
	public void onRefresh() {
		if (mState == STATE_REFRESH) {
			return;
		}

		requestData(true);
		mState = STATE_REFRESH;
	}

	protected boolean compareTo(List<ChatMessage> data, ChatMessage enity) {
		int s = data.size();
		if (enity != null) {
			for (int i = 0; i < s; i++) {
				if (enity.getChatMessageId() == data.get(i).getChatMessageId()) {
					return true;
				}

			}
		}
		return false;
	}

	@Override
	public void onResume() {
		super.onResume();
		this.rushDBData();
	}

}
