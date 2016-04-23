package com.cai.chat_05.fragment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cai.chat_05.MainActivity;
import com.cai.chat_05.R;
import com.cai.chat_05.adppter.FriendListAdapter;
import com.cai.chat_05.bean.Friends;
import com.cai.chat_05.receiver.BaseFragment;
import com.cai.chat_05.view.EmptyLayout;
import com.cai.chat_05.view.IphoneTreeView;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class FriendListFragment extends BaseFragment implements
		SwipeRefreshLayout.OnRefreshListener, OnScrollListener,
		OnChildClickListener {
	private MainActivity mainActivity;
	private View mBaseView;

	@InjectView(R.id.swiperefreshlayout)
	protected SwipeRefreshLayout mSwipeRefreshLayout;

	@InjectView(R.id.iphone_tree_view)
	protected IphoneTreeView mExpandableListView;
	@InjectView(R.id.error_layout)
	protected EmptyLayout mErrorLayout;

	protected FriendListAdapter mAdapter;

	private int indicatorGroupHeight;
	private int the_group_expand_position = -1;
	private int count_expand = 0;
	private Map<Integer, Integer> ids = new HashMap<Integer, Integer>();
	@InjectView(R.id.topGroup)
	protected LinearLayout view_flotage;
	@InjectView(R.id.content_001)
	protected TextView group_content = null;
	@InjectView(R.id.tubiao)
	protected ImageView tubiao;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		mainActivity = (MainActivity) getActivity();
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		mBaseView = inflater.inflate(R.layout.fragment_friendlist, container,
				false);
		System.out.println("初始化friendListFragment");
		return mBaseView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		ButterKnife.inject(this, view);
		init();
	}

	private void init() {
		// 设置下拉刷新
		mSwipeRefreshLayout.setOnRefreshListener(this);
		mSwipeRefreshLayout.setColorSchemeResources(
				R.color.swiperefresh_color1, R.color.swiperefresh_color2,
				R.color.swiperefresh_color3, R.color.swiperefresh_color4);

		if (mAdapter != null) {
			mExpandableListView.setAdapter(mAdapter);
			mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
		} else {
			mAdapter = new FriendListAdapter((MainActivity) this.getActivity(),
					mExpandableListView);
			mExpandableListView.setAdapter(mAdapter);

			mErrorLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
			mState = STATE_NONE;
			requestData(false);
		}

		//
		mExpandableListView.setGroupIndicator(null);

		/**
		 * 监听父节点打开的事件
		 */
		mExpandableListView
				.setOnGroupExpandListener(new OnGroupExpandListener() {
					@Override
					public void onGroupExpand(int groupPosition) {
						the_group_expand_position = groupPosition;
						ids.put(groupPosition, groupPosition);
						count_expand = ids.size();
					}
				});
		/**
		 * 监听父节点关闭的事件
		 */
		mExpandableListView
				.setOnGroupCollapseListener(new OnGroupCollapseListener() {
					@Override
					public void onGroupCollapse(int groupPosition) {
						ids.remove(groupPosition);
						mExpandableListView.setSelectedGroup(groupPosition);
						count_expand = ids.size();
					}
				});

		view_flotage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				view_flotage.setVisibility(View.GONE);
				mExpandableListView.collapseGroup(the_group_expand_position);
				mExpandableListView.setSelectedGroup(the_group_expand_position);
			}
		});
		// group_content = (TextView) findViewById(R.id.content_001);
		// tubiao = (ImageView) findViewById(R.id.tubiao);
		tubiao.setBackgroundResource(R.drawable.btn_browser2);
		// 设置滚动事件
		mExpandableListView.setOnScrollListener(this);

		mExpandableListView.setOnChildClickListener(this);

	}

	/***
	 * 获取列表数据
	 * 
	 * 
	 * 
	 * @return void
	 * @param refresh
	 */
	protected void requestData(boolean refresh) {

		// 取新的数据
//		try {
//			if (mainActivity.getSessionService() != null) {
//				mainActivity.getSessionService().getFriendList();
//				mainActivity.getSessionService().getFriendGroupsList();
//
//			}
//		} catch (RemoteException e) {
//			e.printStackTrace();
//		}

	}

	@Override
	public void onReceive(Context context, Intent intent) {

//		Log.v("freinds fragment org.weishe.weichat", "收到广播消息(好友列表发生变法)！");

		String key = intent.getAction();
//		switch (key) {
//		case Constants.INTENT_ACTION_RECEIVE_FRIEND_LIST:
//			List<Friends> friends = null;
//			try {
//				friends = (List<Friends>) CacheManager.readObject(mainActivity,
//						Friends.getCacheKey(mainActivity.getSessionService()
//								.getUserId()));
//			} catch (RemoteException e) {
//				e.printStackTrace();
//			}
//			if (friends != null && !friends.isEmpty()) {
//				mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
//				mainActivity.setFriends(friends);
//				mAdapter.onDataChanged();
//			}
//			mState = STATE_NONE;
//			setSwipeRefreshLoadedState();
//			break;
//		case Constants.INTENT_ACTION_RECEIVE_FRIEND_GROUP_LIST:
//			List<FriendsGroup> fg = null;
//			try {
//				fg = (List<FriendsGroup>) CacheManager.readObject(mainActivity,
//						FriendsGroup.getCacheKey(mainActivity
//								.getSessionService().getUserId()));
//			} catch (RemoteException e) {
//				e.printStackTrace();
//			}
//
//			if (fg != null && !fg.isEmpty()) {
//				mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
//				mainActivity.setFriendsGroups(fg);
//				mAdapter.onDataChanged();
//			}
//			mState = STATE_NONE;
//			setSwipeRefreshLoadedState();
//			break;
//		default:
//			break;
//		}

	}

	@Override
	public void registerReceiver(BroadcastReceiver receiver) {
//		IntentFilter intentFilter = new IntentFilter();
//		intentFilter.addAction(Constants.INTENT_ACTION_RECEIVE_FRIEND_LIST);
//		intentFilter
//				.addAction(Constants.INTENT_ACTION_RECEIVE_FRIEND_GROUP_LIST);
//		mainActivity.registerReceiver(receiver, intentFilter);
	}

	@Override
	public void unRegisterReceiver(BroadcastReceiver receiver) {
		mainActivity.unregisterReceiver(receiver);
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
		// 让session来获取
		requestData(false);
		// 设置顶部正在刷新
		// mListView.setSelection(0);
		mState = STATE_REFRESH;
	}

//	protected boolean compareTo(List<Friends> data, Friends enity) {
//		if (data == null) {
//			return false;
//		}
//		int s = data.size();
//		if (enity != null) {
//			for (int i = 0; i < s; i++) {
//				if (enity.getId() == data.get(i).getId()) {
//					return true;
//				}
//			}
//		}
//		return false;
//	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// 防止三星,魅族等手机第一个条目可以一直往下拉,父条目和悬浮同时出现的问题
		if (firstVisibleItem == 0) {
			view_flotage.setVisibility(View.GONE);
		}
		// 控制滑动时TextView的显示与隐藏
		int npos = view.pointToPosition(0, 0);
		if (npos != AdapterView.INVALID_POSITION) {
			long pos = mExpandableListView.getExpandableListPosition(npos);
			int childPos = ExpandableListView.getPackedPositionChild(pos);
			final int groupPos = ExpandableListView.getPackedPositionGroup(pos);
			if (childPos == AdapterView.INVALID_POSITION) {
				View groupView = mExpandableListView.getChildAt(npos
						- mExpandableListView.getFirstVisiblePosition());
				indicatorGroupHeight = groupView.getHeight();
			}

			if (indicatorGroupHeight == 0) {
				return;
			}
			// if (isExpanded) {
			if (count_expand > 0) {
				the_group_expand_position = groupPos;
				// group_content.setText(groupData.get(the_group_expand_position)
				// .get("group_text"));
				if (the_group_expand_position != groupPos
						|| !mExpandableListView.isGroupExpanded(groupPos)) {
					view_flotage.setVisibility(View.GONE);
				} else {
					view_flotage.setVisibility(View.VISIBLE);
				}
			}
			if (count_expand == 0) {
				view_flotage.setVisibility(View.GONE);
			}
		}

		if (the_group_expand_position == -1) {
			return;
		}
		/**
		 * calculate point (0,indicatorGroupHeight)
		 */
		int showHeight = getHeight();
		MarginLayoutParams layoutParams = (MarginLayoutParams) view_flotage
				.getLayoutParams();
		// 得到悬浮的条滑出屏幕多少
		layoutParams.topMargin = -(indicatorGroupHeight - showHeight);
		view_flotage.setLayoutParams(layoutParams);

	}

	private int getHeight() {
		int showHeight = indicatorGroupHeight;
		int nEndPos = mExpandableListView.pointToPosition(0,
				indicatorGroupHeight);
		if (nEndPos != AdapterView.INVALID_POSITION) {
			long pos = mExpandableListView.getExpandableListPosition(nEndPos);
			int groupPos = ExpandableListView.getPackedPositionGroup(pos);
			if (groupPos != the_group_expand_position) {
				View viewNext = mExpandableListView.getChildAt(nEndPos
						- mExpandableListView.getFirstVisiblePosition());
				showHeight = viewNext.getTop();
			}
		}
		return showHeight;
	}

	@Override
	public boolean onChildClick(ExpandableListView parent, View v,
			int groupPosition, int childPosition, long id) {
//		Log.v("org.weishe.weichat", "onChildClick groupPosition："
//				+ groupPosition + "  childPosition:" + childPosition);
//		Friends friends = mAdapter.getChild(groupPosition, childPosition);
//		//UIHelper.startChatActivity(mainActivity, friends);
//		int myId = 0;
//		String token = "";
//		try {
//			token = mainActivity.getSessionService().getToken();
//			myId = mainActivity.getSessionService().getUserId();
//		} catch (RemoteException e) {
//			e.printStackTrace();
//		}
//		UIHelper.startUserInforActivity(mainActivity, myId,
//				friends.getUserId(), token,
//				Constants.INTENT_EXTRA_USER_INFOR_TYPE_USERINFOR);
		return true;
	}
}
