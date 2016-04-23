package com.cai.chat_05.adppter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cai.chat_05.R;
import com.cai.chat_05.bean.Friends;
import com.cai.chat_05.bean.FriendsGroup;
import com.cai.chat_05.bean.GroupClass;
import com.cai.chat_05.bean.User;
import com.cai.chat_05.view.CircularImage;
import com.cai.chat_05.view.IphoneTreeView;

public class FriendListAdapter extends BaseExpandableListAdapter implements
		IphoneTreeView.IphoneTreeHeaderAdapter {

	private Context mContext;
	private List<FriendsGroup> friendsGroups;
	private List<List<Friends>> friends;
	private List<Friends> friend;
	private User user;
	private IphoneTreeView mIphoneTreeView;

	// 伪数据
	private HashMap<Integer, Integer> groupStatusMap;

	public FriendListAdapter(Context mContext, IphoneTreeView iphoneTreeView) {
		this.mIphoneTreeView = iphoneTreeView;
		this.mContext = mContext;

		// 初始化一部分数据
//		user = (User) CacheManager.readObject(mContext,
//				Constants.CACHE_CURRENT_USER);
//		friend = (List<Friends>) CacheManager.readObject(mContext,
//				Friends.getCacheKey(user.getId()));
//		friendsGroups = (List<FriendsGroup>) CacheManager.readObject(mContext,
//				FriendsGroup.getCacheKey(user.getId()));

		friend = new ArrayList<Friends>();
		friendsGroups = new ArrayList<FriendsGroup>();
		for(int i=0; i<10; i++){
			Friends f = new Friends();
			f.setAge(i);
			f.setId(i);
			f.setAvatarPath("sssssss");
			f.setFriendsGroupId(1);
			f.setGender(0);
			f.setName("name"+i);
			f.setOnline(true);
			f.setUserId(i);
			friend.add(f);
		}

		for(int i=0; i<2; i++){
			FriendsGroup g = new FriendsGroup();
			g.setName("朋友"+i);
			g.setId(i);
			friendsGroups.add(g);
		}
		this.friends = this.getFriendsData();
		this.groupStatusMap = new HashMap<Integer, Integer>();
	}

	@Override
	public int getGroupCount() {
		if (friendsGroups == null) {
			return 0;
		}
		return friendsGroups.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		if (friends == null || friends.size() == 0
				|| groupPosition > friends.size()
				|| groupPosition == friends.size()
				|| friends.get(groupPosition) == null) {
			return 0;
		}
		return friends.get(groupPosition).size();
	}

	@Override
	public FriendsGroup getGroup(int groupPosition) {
		return friendsGroups.get(groupPosition);
	}

	@Override
	public Friends getChild(int groupPosition, int childPosition) {
		return friends.get(groupPosition).get(childPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		return friendsGroups.get(groupPosition).getId();
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {

		return friends.get(groupPosition).get(childPosition).getId();
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			LayoutInflater inflaterGroup = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflaterGroup
					.inflate(R.layout.fragment_constact_group, null);
		}
		TextView title = (TextView) view.findViewById(R.id.group_name);
		TextView onlineCountTV = (TextView) view
				.findViewById(R.id.online_count);
		title.setText(getGroup(groupPosition).getName());
		ImageView image = (ImageView) view.findViewById(R.id.group_indicator);
		int totalCount = getChildrenCount(groupPosition);
		int onlineCount = getGroupOnlineCount(groupPosition);
		onlineCountTV.setText(onlineCount + "/" + totalCount);
		if (isExpanded) {
			image.setBackgroundResource(R.drawable.qb_down);
		} else {
			image.setBackgroundResource(R.drawable.qb_right);
		}
		return view;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		Friends f = friends.get(groupPosition).get(childPosition);

		View view = convertView;
		if (view == null) {
			LayoutInflater inflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.friend_list_item, null);
		}
		TextView userName = (TextView) view.findViewById(R.id.friend_list_name);
		TextView signature = (TextView) view
				.findViewById(R.id.friends_signature);
		CircularImage userPhoto = (CircularImage) view
				.findViewById(R.id.user_photo);
		String path = f.getAvatarPath();
		if (path != null && !path.isEmpty()) {
			String p[] = path.split("#");
			if (p != null && p.length == 2) {
				userPhoto.setImage(p[0], p[1]);
			}
		}

		String nameStr = "";
		if (f.getRemarkName() != null && !f.getRemarkName().isEmpty()) {

			nameStr = f.getRemarkName();
		} else {
			nameStr = f.getName();
		}
		userName.setText(nameStr);
		signature.setText(f.getSignature());

		return view;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return true;
	}

	/**
	 * 当数据发生变法时
	 */
	public void onDataChanged() {
		this.friendsGroups = this.getFriendsGroups();
		this.friends = this.getFriendsData();
		this.notifyDataSetChanged();
	}

	private int getGroupOnlineCount(int groupPosition) {
		int count = 0;
		if (friends == null) {
			return 0;
		}
		List<Friends> fs = friends.get(groupPosition);
		if (fs != null) {
			for (Friends f : fs) {
				if (f.isOnline()) {
					count++;
				}
			}
		}
		return count;
	}

	@Override
	public int getTreeHeaderState(int groupPosition, int childPosition) {
		final int childCount = getChildrenCount(groupPosition);
		if (childPosition == childCount - 1) {
			// mSearchView.setVisibility(View.GONE);
			return PINNED_HEADER_PUSHED_UP;
		} else if (childPosition == -1
				&& !mIphoneTreeView.isGroupExpanded(groupPosition)) {
			// mSearchView.setVisibility(View.VISIBLE);
			return PINNED_HEADER_GONE;
		} else {
			// mSearchView.setVisibility(View.GONE);
			return PINNED_HEADER_VISIBLE;
		}
	}

	@Override
	public void configureTreeHeader(View header, int groupPosition,
			int childPosition, int alpha) {
		// ((TextView) header.findViewById(R.id.group_name))
		// .setText(groups[groupPosition]);
		// ((TextView) header.findViewById(R.id.online_count))
		// .setText(getChildrenCount(groupPosition) + "/"
		// + getChildrenCount(groupPosition));
	}

	@Override
	public void onHeadViewClick(int groupPosition, int status) {
		groupStatusMap.put(groupPosition, status);
	}

	@Override
	public int getHeadViewClickStatus(int groupPosition) {
		if (groupStatusMap.containsKey(groupPosition)) {
			return groupStatusMap.get(groupPosition);
		} else {
			return 0;
		}
	}

	/**
	 * 获取好友分组
	 * 
	 * @return
	 */
	public List<FriendsGroup> getFriendsGroups() {

		FriendsGroup g = new FriendsGroup();
		g.setId(0);
		g.setName("未分组");
		g.setPosition(0);

		// 排序
		if (friend != null) {
			for (Friends f : friend) {
				if (f.getFriendsGroupId() < 1) {
					if (friendsGroups == null) {
						friendsGroups = new ArrayList<FriendsGroup>();
					}
					boolean flag = true;
					for (FriendsGroup fg : friendsGroups) {
						if (fg.getPosition() == 0 || fg.getId() == 0) {
							flag = false;
						}
					}
					if (flag) {
						friendsGroups.add(g);
					}
					break;
				}
			}
		}
		if (friendsGroups != null) {
			Collections.sort(friendsGroups);
		}

		return friendsGroups;
	}

	/**
	 * 按分组获取好友列表
	 * 
	 * @param groupId
	 * @return
	 */
	public List<Friends> getFriendsByGroup(int groupId) {
		ArrayList<Friends> data = new ArrayList<Friends>();
		// 未分组
		if (groupId == 0) {
			for (Friends f : friend) {
				if (f.getFriendsGroupId() < 1) {
					data.add(f);
				}
			}
		} else {
			// 非未分组
			for (Friends f : friend) {
				if (f.getFriendsGroupId() == groupId) {
					data.add(f);
				}
			}
		}

		return data;
	}

	/**
	 * 不要轻易调用，有点儿慢，当程序初始化的时候调用一次
	 * 
	 * @return
	 */
	public List<List<Friends>> getFriendsData() {
		List<List<Friends>> friends = new ArrayList<List<Friends>>();
		List<FriendsGroup> groups = getFriendsGroups();
		if (groups == null) {
			return friends;
		}
		for (FriendsGroup g : groups) {
			friends.add(getFriendsByGroup(g.getId()));
		}
		return friends;
	}
}
