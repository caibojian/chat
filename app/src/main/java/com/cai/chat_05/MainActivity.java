package com.cai.chat_05;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;


import com.cai.chat_05.aidl.SessionService;
import com.cai.chat_05.bean.Constants;
import com.cai.chat_05.bean.Friends;
import com.cai.chat_05.bean.FriendsGroup;
import com.cai.chat_05.bean.User;
import com.cai.chat_05.cache.CacheManager;
import com.cai.chat_05.fragment.ConstactFatherFragment;
import com.cai.chat_05.fragment.MessageListFragment;
import com.cai.chat_05.fragment.SettingFragment;
import com.cai.chat_05.service.IoTService;
import com.cai.chat_05.utils.DBHelper;
import com.cai.chat_05.view.TableView;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends FragmentActivity {
	public static final int START_TYPE_NORMAL = 0;
	public static final int START_TYPE_TODO = 1;

	private IoTService.MsgBinder iBinder;
	private User user;
	private List<Friends> friends;
	private List<FriendsGroup> friendsGroups;

	protected static final String TAG = "MainActivity";
	private Context mContext;
	private View mPopView;
	private View currentButton;

	private TextView app_cancle;
	private TextView app_exit;
	private TextView app_change;

	private PopupWindow mPopupWindow;

	private FragmentTabHost mTabHost;
	private TableView messageView, myView, contactsView, trendView;
	private TabSpec messageTabSpec, contactsTabSpec, trendTabSpec, myTabSpec;
	private View top;
	private String previousTab;// 之前的，不能是publish

	private BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.v("org.weishe.weichat", "main 收到广播消息(好友列表发生变法)！");

			String key = intent.getAction();
			switch (key) {
			case Constants.INTENT_ACTION_RECEIVE_FRIEND_LIST:

				friends = (List<Friends>) CacheManager.readObject(
						MainActivity.this, Friends.getCacheKey(user.getId()));
				break;
			case Constants.INTENT_ACTION_RECEIVE_FRIEND_GROUP_LIST:
				List<FriendsGroup> fg = null;
				friendsGroups = (List<FriendsGroup>) CacheManager.readObject(
						MainActivity.this,
						FriendsGroup.getCacheKey(user.getId()));
				break;
				default:
					break;
			}
		}
	};
	private ServiceConnection connection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			iBinder = (IoTService.MsgBinder) service;
			Log.v("org.weishe.weichat", "获取  SessionService！");
				int fromMessageId = 0;
				try {
					fromMessageId = DBHelper.getgetInstance(mContext)
							.getMaxMessageIdByUserId(
									iBinder.getUserId());
				} catch (RemoteException e1) {
					e1.printStackTrace();
				}
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			iBinder = null;
		}

	};
	/**
	 * ATTENTION: This was auto-generated to implement the App Indexing API.
	 * See https://g.co/AppIndexing/AndroidStudio for more information.
	 */
	private GoogleApiClient client;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = new Intent(this, IoTService.class);
		this.bindService(intent, connection, Context.BIND_AUTO_CREATE);

		setContentView(R.layout.activity_main);
		mContext = this;

		// 初始化一部分数据
		user = (User) CacheManager.readObject(this,
				Constants.CACHE_CURRENT_USER);
		friends = (List<Friends>) CacheManager.readObject(this,
				Friends.getCacheKey(user.getId()));
		//写些假数据
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Constants.INTENT_ACTION_RECEIVE_FRIEND_LIST);
		intentFilter
				.addAction(Constants.INTENT_ACTION_RECEIVE_FRIEND_GROUP_LIST);
		this.registerReceiver(receiver, intentFilter);

		findView();
		initView();
		init();

		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
	}

	private void findView() {
		mPopView = LayoutInflater.from(mContext).inflate(R.layout.app_exit,
				null);

		app_cancle = (TextView) mPopView.findViewById(R.id.app_cancle);
		app_change = (TextView) mPopView.findViewById(R.id.app_change_user);
		app_exit = (TextView) mPopView.findViewById(R.id.app_exit);
	}

	private void initView() {
		top = this.findViewById(R.id.top);

		mTabHost = (FragmentTabHost) findViewById(R.id.tabhost);
		mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);

//		 View view =layoutInflater.inflate(R.layout.tab_view, null);
		messageView = new TableView(this);
		contactsView = new TableView(this);
		trendView = new TableView(this);
		myView = new TableView(this);


		messageView.setTitle(
				R.drawable.tab_message_selector,
				this.getResources().getString(R.string.tab_view_title_message));
		contactsView.setTitle(R.drawable.tab_contacts_selector, this.getResources()
				.getString(R.string.tab_view_title_contacts));
		trendView.setTitle(R.drawable.tab_trend_selector,
				this.getResources().getString(R.string.tab_view_title_trend));
		myView.setTitle(R.drawable.tab_my_selector,
				this.getResources().getString(R.string.tab_view_title_my));

		previousTab = "message";

		messageTabSpec = mTabHost.newTabSpec("message").setIndicator(
				messageView);
		contactsTabSpec = mTabHost.newTabSpec("contacts").setIndicator(
				contactsView);
		trendTabSpec = mTabHost.newTabSpec("trend").setIndicator(trendView);
		myTabSpec = mTabHost.newTabSpec("my").setIndicator(myView);

		mTabHost.addTab(messageTabSpec, MessageListFragment.class, null);
		mTabHost.addTab(contactsTabSpec, ConstactFatherFragment.class, null);
		mTabHost.addTab(trendTabSpec, Fragment.class, null);
		mTabHost.addTab(myTabSpec, SettingFragment.class, null);
		mTabHost.getTabWidget().setDividerDrawable(null);
		//设置高度
		mTabHost.getTabWidget().setMinimumHeight(10);
		mTabHost.setOnTabChangedListener(new OnTabChangeListener() {

			@Override
			public void onTabChanged(String tabId) {
				Log.v("org.weishe", tabId);
				previousTab = tabId;
			}
		});
	}

	private void init() {

		mPopupWindow = new PopupWindow(mPopView, LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT, true);

		app_cancle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mPopupWindow.dismiss();
			}
		});

		app_change.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, LoginActivity.class);
				startActivity(intent);
				((Activity) mContext).overridePendingTransition(
						R.anim.activity_up, R.anim.fade_out);
				finish();
			}
		});

		app_exit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	private void setButton(View v) {
		if (currentButton != null && currentButton.getId() != v.getId()) {
			currentButton.setEnabled(true);
		}
		v.setEnabled(false);
		currentButton = v;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color
					.parseColor("#b0000000")));
			mPopupWindow.showAtLocation(top, Gravity.BOTTOM, 0, 0);
			mPopupWindow.setAnimationStyle(R.style.app_pop);
			mPopupWindow.setOutsideTouchable(true);
			mPopupWindow.setFocusable(true);
			mPopupWindow.update();
		}
		return super.onKeyDown(keyCode, event);

	}

	public IoTService.MsgBinder getSessionService() {
		return iBinder;
	}

	public List<Friends> getFriends() {
		return friends;
	}

	public void setFriends(List<Friends> friends) {
		this.friends = friends;
	}

	public void setFriendsGroups(List<FriendsGroup> friendsGroups) {
		this.friendsGroups = friendsGroups;
	}

	/**
	 * 当好友列表发生该表时调用
	 *
	 * @param data
	 */
	public void addData(List<Friends> data) {
		if (friends == null) {
			friends = new ArrayList<Friends>();
		}
		this.friends.addAll(data);
	}
	protected void onDestroy() {
		this.unbindService(connection);
		this.unregisterReceiver(receiver);
		super.onDestroy();
	}

	@Override
	public void onStart() {
		super.onStart();

		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		client.connect();
		Action viewAction = Action.newAction(
				Action.TYPE_VIEW, // TODO: choose an action type.
				"Main Page", // TODO: Define a title for the content shown.
				// TODO: If you have web page content that matches this app activity's content,
				// make sure this auto-generated web page URL is correct.
				// Otherwise, set the URL to null.
				Uri.parse("http://host/path"),
				// TODO: Make sure this auto-generated app URL is correct.
				Uri.parse("android-app://com.cai.chat_05/http/host/path")
		);
		AppIndex.AppIndexApi.start(client, viewAction);
	}

	@Override
	public void onStop() {
		super.onStop();

		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		Action viewAction = Action.newAction(
				Action.TYPE_VIEW, // TODO: choose an action type.
				"Main Page", // TODO: Define a title for the content shown.
				// TODO: If you have web page content that matches this app activity's content,
				// make sure this auto-generated web page URL is correct.
				// Otherwise, set the URL to null.
				Uri.parse("http://host/path"),
				// TODO: Make sure this auto-generated app URL is correct.
				Uri.parse("android-app://com.cai.chat_05/http/host/path")
		);
		AppIndex.AppIndexApi.end(client, viewAction);
		client.disconnect();
	}

	public User getUser(){
		return user;
	}
}
