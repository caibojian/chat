package com.cai.chat_05.fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.cai.chat_05.MainActivity;
import com.cai.chat_05.R;
import com.cai.chat_05.adppter.ChatGroupAdapter;
import com.cai.chat_05.bean.ChatGroup;
import com.cai.chat_05.core.bean.ChatMessage;
import com.cai.chat_05.sort.CharacterParser;
import com.cai.chat_05.sort.PinyinComparator;
import com.cai.chat_05.sort.SideBar;
import com.cai.chat_05.utils.UIHelper;
import com.cai.chat_05.view.CustomListView;
import com.cai.chat_05.view.LoadingView;
import com.cai.chat_05.view.CustomListView.OnRefreshListener;

public class ChatGroupFragment extends Fragment {
	private MainActivity mContext;
	private CustomListView mCustomListView;
	private LoadingView mLoadingView;
	private SideBar sideBar;
	private TextView dialog;

	private View mBaseView;
	private ChatGroupAdapter adapter;

	/**
	 * 汉字转换成拼音的类
	 */
	private CharacterParser characterParser;
	private List<ChatGroup> SourceDateList;

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
		mCustomListView.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
//				new AsyncTaskQQConstact(mLoadingView).execute(0);
				// WeisheApi.getChatGroup(mHandler, user.getId(), token);
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

				ChatGroup chatGroup = (ChatGroup) adapter.getItem(position - 1);
				UIHelper.startChatActivity(mContext, ChatMessage.MSG_TYPE_UCG,
						null, chatGroup, null);

			}
		});

//		new AsyncTaskQQConstact(mLoadingView).execute(0);
		// WeisheApi.getChatGroup(mHandler, user.getId(), token);
	}

//	private class AsyncTaskQQConstact extends AsyncTaskBase {
//		public AsyncTaskQQConstact(LoadingView loadingView) {
//			super(loadingView);
//		}
//
//		@Override
//		protected Integer doInBackground(Integer... params) {
//			int result = -1;
//			// callRecords = TestData.getFriends();
//			try {
//				mContext.getSessionService().getChatGroupList();
//			} catch (RemoteException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			result = 1;
//			return result;
//		}
//
//		@Override
//		protected void onPostExecute(Integer result) {
//			super.onPostExecute(result);
//			mCustomListView.onRefreshComplete();
//			if (result == 1) {
//				List<ChatGroup> friends = null;
//				try {
//					friends = (List<ChatGroup>) CacheManager.readObject(
//							mContext, ChatGroup.getCacheKey(mContext
//									.getSessionService().getUserId()));
//				} catch (RemoteException e) {
//					e.printStackTrace();
//				}
//
//				SourceDateList = filledData(friends);
//
//				if (SourceDateList != null) {
//					// 根据a-z进行排序源数据
//					Collections.sort(SourceDateList, pinyinComparator);
//				}
//				adapter = new ChatGroupAdapter(mContext, SourceDateList,
//						mCustomListView);
//				mCustomListView.setAdapter(adapter);
//
//			}
//		}
//
//		@Override
//		protected void onPreExecute() {
//			super.onPreExecute();
//		}
//
//	}

	/**
	 * 为ListView填充数据
	 * 
	 * @param data
	 * @return
	 */
	private List<ChatGroup> filledData(List<ChatGroup> data) {
		if (data == null) {
			return data;
		}

		if (data != null && data.size() > 0) {
			for (ChatGroup u : data) {
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
		List<ChatGroup> filterDateList = new ArrayList<ChatGroup>();

		if (TextUtils.isEmpty(filterStr)) {
			filterDateList = SourceDateList;
		} else {
			filterDateList.clear();
			for (ChatGroup sortModel : SourceDateList) {
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
