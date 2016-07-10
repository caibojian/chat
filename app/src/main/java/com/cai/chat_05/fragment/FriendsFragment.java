package com.cai.chat_05.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;


import com.cai.chat_05.ChatActivity;
import com.cai.chat_05.MainActivity;
import com.cai.chat_05.R;
import com.cai.chat_05.adppter.FriendsAdapter;
import com.cai.chat_05.base.AsyncTaskBase;
import com.cai.chat_05.bean.Constants;
import com.cai.chat_05.bean.Friends;
import com.cai.chat_05.bean.FriendsGroup;
import com.cai.chat_05.cache.CacheManager;
import com.cai.chat_05.sort.CharacterParser;
import com.cai.chat_05.sort.PinyinComparator;
import com.cai.chat_05.sort.SideBar;
import com.cai.chat_05.view.CustomListView;
import com.cai.chat_05.view.LoadingView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class FriendsFragment extends Fragment {
	private MainActivity mContext;
	private CustomListView mCustomListView;
	private LoadingView mLoadingView;
	private SideBar sideBar;
	private TextView dialog;

	private View mBaseView;
	private FriendsAdapter adapter;
	private Map<String, String> callRecords;
	/**
	 * 汉字转换成拼音的类
	 */
	private CharacterParser characterParser;
	private List<Friends> SourceDateList;

	/**
	 * 根据拼音来排列ListView里面的数据类
	 */
	private PinyinComparator pinyinComparator;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		mContext = (MainActivity) getActivity();
		mBaseView = inflater.inflate(R.layout.fragment_all_friend, null);
		initView();
		init();
		return mBaseView;
	}

	private void initView() {
		mCustomListView = (CustomListView) mBaseView
				.findViewById(R.id.fragment_list_view);
		mLoadingView = (LoadingView) mBaseView.findViewById(R.id.loading);
		sideBar = (SideBar) mBaseView.findViewById(R.id.sidrbar);
		dialog = (TextView) mBaseView.findViewById(R.id.dialog);
	}

	private void init() {
		mCustomListView.setCanLoadMore(false);
		mCustomListView.setOnRefreshListener(new CustomListView.OnRefreshListener() {
			@Override
			public void onRefresh() {
				new AsyncTaskQQConstact(mLoadingView).execute(0);
			}
		});

		// 实例化汉字转拼音类
		characterParser = CharacterParser.getInstance();

		pinyinComparator = new PinyinComparator();

		sideBar.setTextView(dialog);

		// 设置右侧触摸监听
		sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

			@SuppressLint("NewApi")
			@Override
			public void onTouchingLetterChanged(String s) {
				// 该字母首次出现的位置
				int position = adapter.getPositionForSection(s.charAt(0));
				if (position != -1) {
					mCustomListView.setSelection(position);
				}
			}
		});

		mCustomListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
				Log.v("FriendsFragment","点击了一个人");
				// 这里要利用adapter.getItem(position)来获取当前position所对应的对象
				// Toast.makeText(getApplication(),
				// ((Friends)adapter.getItem(position)).getName(),
				// Toast.LENGTH_SHORT).show();
				/*
				 * String number = callRecords.get(((Friends) adapter
				 * .getItem(position)).getName());
				 */
				Intent intent = new Intent();
				intent.setClass(mContext, ChatActivity.class);
				intent.putExtra(Constants.INTENT_EXTRA_CHAT_FRIEND, (Friends)adapter.getItem(position));
				startActivity(intent);

			}
		});

		new AsyncTaskQQConstact(mLoadingView).execute(0);
	}

	private class AsyncTaskQQConstact extends AsyncTaskBase {
		public AsyncTaskQQConstact(LoadingView loadingView) {
			super(loadingView);
		}

		@Override
		protected Integer doInBackground(Integer... params) {
			int result = -1;
//			callRecords = TestData.getFriends();
			result = 1;
			return result;
		}

		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			mCustomListView.onRefreshComplete();
			if (result == 1) {
				List<Friends> friends = null;
				try {
					friends = (List<Friends>) CacheManager.readObject(mContext,
							Friends.getCacheKey(mContext.getUser().getId()));
				} catch (Exception e) {
					e.printStackTrace();
				}
				SourceDateList = filledData(friends);
				if (SourceDateList == null) {
					return;
				}
				// 根据a-z进行排序源数据
				Collections.sort(SourceDateList, pinyinComparator);
				adapter = new FriendsAdapter(mContext, SourceDateList,
						callRecords, mCustomListView);
				mCustomListView.setAdapter(adapter);
			}
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

	}

	/**
	 * 为ListView填充数据
	 * 
	 * @param data
	 * @return
	 */
	private List<Friends> filledData(List<Friends> data) {

		if (data != null && data.size() > 0) {
			for (Friends u : data) {
				// 汉字转换成拼音
				String pinyin = characterParser.getSelling(u.getName());
				String sortString = pinyin.substring(0, 1).toUpperCase();
				// 正则表达式，判断首字母是否是英文字母
				if (sortString.matches("[A-Z]")) {
					u.setSortLetters(sortString.toUpperCase());
				} else {
					u.setSortLetters("#");
				}
			}
		}

		return data;

	}

	/**
	 * 根据输入框中的值来过滤数据并更新ListView
	 * 
	 * @param filterStr
	 */
	private void filterData(String filterStr) {
		List<Friends> filterDateList = new ArrayList<Friends>();

		if (TextUtils.isEmpty(filterStr)) {
			filterDateList = SourceDateList;
		} else {
			filterDateList.clear();
			for (Friends sortModel : SourceDateList) {
				String name = sortModel.getName();
				if (name.indexOf(filterStr.toString()) != -1
						|| characterParser.getSelling(name).startsWith(
								filterStr.toString())) {
					filterDateList.add(sortModel);
				}
			}
		}

		// 根据a-z进行排序
		Collections.sort(filterDateList, pinyinComparator);
		adapter.updateListView(filterDateList);
	}
}
