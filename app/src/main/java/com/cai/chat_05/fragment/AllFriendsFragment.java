package com.cai.chat_05.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cai.chat_05.MainActivity;
import com.cai.chat_05.R;
import com.cai.chat_05.sort.ClearEditText;
import com.cai.chat_05.utils.UIHelper;

public class AllFriendsFragment extends Fragment implements OnClickListener {
	private MainActivity mContext;
	private Fragment mListView;
	private ChatGroupFragment mChatGroupFragment;
	private DiscussionGroupFragment mDiscussionGroupFragment;
	private FriendsFragment mFriendsFragment;
	private View mSearchView;
	private ClearEditText mClearEditText;
	private RelativeLayout friends, constacts, group, discuss, addRL;
	private TextView mTab, addTitle;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContext = (MainActivity) getActivity();
		mSearchView = inflater.inflate(R.layout.common_search_xxl, null);

		mChatGroupFragment = new ChatGroupFragment();
		mDiscussionGroupFragment = new DiscussionGroupFragment();
		mFriendsFragment = new FriendsFragment();
		findView();
		init();
		return mSearchView;
	}

	private void init() {
		addRL.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mListView instanceof ChatGroupFragment) {
					UIHelper.startAddChatGroupActivity(mContext);
				} else if (mListView instanceof DiscussionGroupFragment) {

				}
			}
		});
	}

	private void findView() {

		mClearEditText = (ClearEditText) mSearchView
				.findViewById(R.id.filter_edit);
		friends = (RelativeLayout) mSearchView.findViewById(R.id.rl_friends);
		constacts = (RelativeLayout) mSearchView.findViewById(R.id.rl_contacts);
		group = (RelativeLayout) mSearchView.findViewById(R.id.rl_group);
		discuss = (RelativeLayout) mSearchView.findViewById(R.id.rl_discuss);
		addRL = (RelativeLayout) mSearchView.findViewById(R.id.add_rl);

		mTab = (TextView) mSearchView.findViewById(R.id.list_view_title);
		addTitle = (TextView) mSearchView.findViewById(R.id.add_title);

		friends.setOnClickListener(this);
		constacts.setOnClickListener(this);
		group.setOnClickListener(this);
		discuss.setOnClickListener(this);

		FragmentTransaction ft = getFragmentManager().beginTransaction();
		mTab.setText(getResources().getText(R.string.tv_friend));
		addRL.setVisibility(View.INVISIBLE);
		mListView = mFriendsFragment;
		ft.replace(R.id.fcgd_list_view, mListView);
		ft.commit();
	}

	@Override
	public void onClick(View v) {
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		int id = v.getId();
		switch (id) {
		case R.id.rl_friends:
			mTab.setText(getResources().getText(R.string.tv_friend));
			addRL.setVisibility(View.INVISIBLE);
			if (mListView instanceof FriendsFragment) {
				return;
			} else {
				mListView = mFriendsFragment;
			}
			break;
		case R.id.rl_contacts:
			mTab.setText(getResources().getText(R.string.tv_phone));
			addRL.setVisibility(View.INVISIBLE);
			break;
		case R.id.rl_group:
			mTab.setText(getResources().getText(R.string.tv_group));
			addRL.setVisibility(View.VISIBLE);
			addTitle.setText(getResources().getText(R.string.add_chat_group));
			if (mListView instanceof ChatGroupFragment) {
				return;
			} else {
				mListView = mChatGroupFragment;
			}

			break;
		case R.id.rl_discuss:
			mTab.setText(getResources().getText(R.string.tv_discuss));
			addRL.setVisibility(View.VISIBLE);
			addTitle.setText(getResources().getText(
					R.string.add_discussion_group));
			if (mListView instanceof DiscussionGroupFragment) {
				return;
			} else {
				mListView = mDiscussionGroupFragment;
			}
			break;
		default:
			break;
		}

		ft.replace(R.id.fcgd_list_view, mListView);
		ft.commit();
	}

}
