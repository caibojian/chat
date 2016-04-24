package com.cai.chat_05.adppter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cai.chat_05.R;
import com.cai.chat_05.bean.ChatGroup;
import com.cai.chat_05.bean.User;
import com.cai.chat_05.sort.ClearEditText;
import com.cai.chat_05.view.CircularImage;

public class SearchViewPagerAdapter extends PagerAdapter {
	private ArrayList<View> viewContainter = new ArrayList<View>();
	private ArrayList<String> titleContainer = new ArrayList<String>();
	private Activity mContext;
	private LayoutInflater mInflater;

	private ClearEditText friendSearchCondition;
	private ClearEditText groupSearchCondition;
	private Button friendsSearchButton;
	private Button groupSearchButton;

	private ListView friendsListView;
	private ListView groupListView;
	private BaseAdapter friendsAdapter;
	private BaseAdapter groupAdapter;

	private List<User> friendsData = new ArrayList<User>();
	private List<ChatGroup> chatGroupData = new ArrayList<ChatGroup>();

	private int myId;
	private String token;

	public SearchViewPagerAdapter(Activity context, int myId, String token) {
		this.mContext = context;
		this.myId = myId;
		this.token = token;
		mInflater = LayoutInflater.from(context);
		titleContainer.add("找人");
		titleContainer.add("找群");
		View friendSearch = LayoutInflater.from(context).inflate(
				R.layout.search_tab, null);
		friendSearchCondition = (ClearEditText) friendSearch
				.findViewById(R.id.search_condition);
		friendsSearchButton = (Button) friendSearch
				.findViewById(R.id.search_button);
		friendsListView = (ListView) friendSearch
				.findViewById(R.id.search_list_listview);
		View groupSearch = LayoutInflater.from(context).inflate(
				R.layout.search_tab, null);
		groupSearchCondition = (ClearEditText) groupSearch
				.findViewById(R.id.search_condition);
		groupSearchCondition.setHint(R.string.search_chat_group_hit);
		groupSearchButton = (Button) groupSearch
				.findViewById(R.id.search_button);
		groupListView = (ListView) groupSearch
				.findViewById(R.id.search_list_listview);
		viewContainter.add(friendSearch);
		viewContainter.add(groupSearch);
		intit();
	}

	private void intit() {

		friendsAdapter = new BaseAdapter() {

			@Override
			public int getCount() {
				return friendsData.size();
			}

			@Override
			public User getItem(int position) {
				return friendsData.get(position);
			}

			@Override
			public long getItemId(int position) {

				return friendsData.get(position).getId();
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				convertView = mInflater.inflate(
						R.layout.search_friend_list_item, null);

				RelativeLayout friendsRL = (RelativeLayout) convertView
						.findViewById(R.id.friends_rl);

				ImageView avatarView = (ImageView) convertView
						.findViewById(R.id.user_photo);
				TextView nameView = (TextView) convertView
						.findViewById(R.id.friend_name);
				TextView signatureView = (TextView) convertView
						.findViewById(R.id.friends_signature);
				TextView ageView = (TextView) convertView
						.findViewById(R.id.age);
				ImageView genderView = (ImageView) convertView
						.findViewById(R.id.gender);
				User u = friendsData.get(position);
				nameView.setText(u.getName());
				signatureView.setText(u.getSignature());
				ageView.setText(u.getAge() + "");
				switch (u.getGender()) {
				case 0:
					genderView.setImageResource(R.drawable.icon_man);
					break;
				case 1:
					genderView.setImageResource(R.drawable.icon_lady);
					break;
				case 2:
					genderView.setVisibility(View.GONE);
					break;
				default:
					break;
				}

				return convertView;
			}

		};
		// 监听按钮

		friendsSearchButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				friendsData.clear();
				friendsAdapter.notifyDataSetChanged();
//				WeisheApi.searchFriends(friendsSearchHandler,
//						friendSearchCondition.getText().toString().trim());
			}
		});

		friendsListView.setAdapter(friendsAdapter);
		friendsListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				User u = friendsData.get(position);
//				UIHelper.startUserInforActivity(mContext, myId, u.getId(),
//						token,
//						Constants.INTENT_EXTRA_USER_INFOR_TYPE_ADD_FRIENDS);
			}
		});
		groupAdapter = new BaseAdapter() {

			@Override
			public int getCount() {
				return chatGroupData.size();
			}

			@Override
			public Object getItem(int position) {

				return chatGroupData.get(position);
			}

			@Override
			public long getItemId(int position) {

				return chatGroupData.get(position).getId();
			}

			@Override
			public View getView(int position, View view, ViewGroup parent) {
				ChatGroupVH viewHolder = null;
				final ChatGroup mContent = chatGroupData.get(position);
				if (view == null) {
					viewHolder = new ChatGroupVH();
					view = LayoutInflater.from(mContext).inflate(
							R.layout.chatgroup_item, null);
					viewHolder.tvTitle = (TextView) view
							.findViewById(R.id.title);
					viewHolder.iconView = (CircularImage) view
							.findViewById(R.id.icon);
					view.setTag(viewHolder);
				} else {
					viewHolder = (ChatGroupVH) view.getTag();
				}
				viewHolder.tvTitle.setText(mContent.getName());
				return view;
			}
		};

		groupSearchButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				chatGroupData.clear();
				groupAdapter.notifyDataSetChanged();
//				WeisheApi.searchChatGroup(chatGroupSearchHandler,
//						groupSearchCondition.getText().toString().trim());
			}
		});

		groupListView.setAdapter(groupAdapter);
		groupListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				ChatGroup cg = chatGroupData.get(position);
				if (cg != null) {
//					UIHelper.startChatGroupInforActivity(
//							mContext,
//							cg.getId(),
//							Constants.INTENT_EXTRA_CHATGROUP_INFOR_TYPE_JOINCHATGROUP);
				}
			}

		});
	}

	final static class ChatGroupVH {
		TextView tvTitle;
		CircularImage iconView;
	}

	@Override
	public int getCount() {
		return 2;
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	// 每次滑动的时候生成的组件
	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		((ViewPager) container).addView(viewContainter.get(position));
		return viewContainter.get(position);
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return titleContainer.get(position);
	}

//	protected AsyncHttpResponseHandler friendsSearchHandler = new AsyncHttpResponseHandler() {
//
//		@Override
//		public void onSuccess(int statusCode, Header[] headers,
//				byte[] responseBytes) {
//			String data = new String(responseBytes);
//			if (data == null || data.isEmpty()) {
//				return;
//			}
//			List<User> user = JSON.parseArray(data, User.class);
//			if (user != null && user.size() > 0) {
//				friendsData = user;
//				friendsAdapter.notifyDataSetChanged();
//			}
//		}
//
//		@Override
//		public void onFailure(int arg0, Header[] arg1, byte[] arg2,
//				Throwable arg3) {
//			Log.v("org.weishe.weichat", "搜索好友发生异常！");
//
//		}
//
//	};

//	protected AsyncHttpResponseHandler chatGroupSearchHandler = new AsyncHttpResponseHandler() {
//
//		@Override
//		public void onSuccess(int statusCode, Header[] headers,
//				byte[] responseBytes) {
//			String data = new String(responseBytes);
//			List<ChatGroup> group = JSON.parseArray(data, ChatGroup.class);
//			if (group != null && group.size() > 0) {
//				chatGroupData = group;
//				groupAdapter.notifyDataSetChanged();
//			}
//		}
//
//		@Override
//		public void onFailure(int arg0, Header[] arg1, byte[] arg2,
//				Throwable arg3) {
//			Log.v("org.weishe.weichat", "搜索群发生异常！");
//
//		}
//
//	};
}
