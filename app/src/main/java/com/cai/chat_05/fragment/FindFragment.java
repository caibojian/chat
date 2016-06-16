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
import android.widget.Toast;

import com.cai.chat_05.MainActivity;
import com.cai.chat_05.R;
import com.cai.chat_05.adppter.FindListAdapter;
import com.cai.chat_05.adppter.FriendListAdapter;
import com.cai.chat_05.bean.Constants;
import com.cai.chat_05.bean.Friends;
import com.cai.chat_05.bean.FriendsGroup;
import com.cai.chat_05.cache.CacheManager;
import com.cai.chat_05.receiver.BaseFragment;
import com.cai.chat_05.utils.UIHelper;
import com.cai.chat_05.view.EmptyLayout;
import com.cai.chat_05.view.FindGridView;
import com.cai.chat_05.view.IphoneTreeView;
import com.cai.chat_05.view.TitleBarView;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class FindFragment extends BaseFragment{
	private MainActivity mainActivity;
	private View mBaseView;
	private FindGridView gridview;

	@InjectView(R.id.title_bar)
	protected TitleBarView mTitleBarView;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		mainActivity = (MainActivity) getActivity();
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {

		mBaseView = inflater.inflate(R.layout.fragment_find, container,
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
		gridview=(FindGridView) mainActivity.findViewById(R.id.gridview);
		gridview.setAdapter(new FindListAdapter(mainActivity));

		mTitleBarView.setCommonTitle(View.GONE, View.VISIBLE, View.GONE,
				View.VISIBLE);
		mTitleBarView.setTitleText(R.string.tab_view_title_trend);
		gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
				Toast.makeText(mainActivity, "position:"+position+",id:"+id,
						Toast.LENGTH_SHORT).show();
				if(position==0){
					UIHelper.ledControlActivity(mainActivity);
				}else if(position==1){
					UIHelper.liveVideoActivity(mainActivity);
				}
			}
		});
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


	}

	@Override
	public void onReceive(Context context, Intent intent) {



	}

	@Override
	public void registerReceiver(BroadcastReceiver receiver) {
		IntentFilter intentFilter = new IntentFilter();
		mainActivity.registerReceiver(receiver, intentFilter);
	}

	@Override
	public void unRegisterReceiver(BroadcastReceiver receiver) {
		mainActivity.unregisterReceiver(receiver);
	}

	/** 设置顶部正在加载的状态 */
	private void setSwipeRefreshLoadingState() {

	}

	/** 设置顶部加载完毕的状态 */
	private void setSwipeRefreshLoadedState() {

	}







}
