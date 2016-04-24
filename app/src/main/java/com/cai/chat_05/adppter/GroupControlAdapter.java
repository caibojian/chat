package com.cai.chat_05.adppter;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.cai.chat_05.R;
import com.cai.chat_05.bean.FriendsGroup;

public class GroupControlAdapter extends BaseAdapter {

	private List<FriendsGroup> data;
	private Context mContext;

	public GroupControlAdapter(List<FriendsGroup> data, Context context) {
		this.data = data;
		this.mContext = context;
	}

	@Override
	public int getCount() {
		if (data != null) {
			return data.size();
		}
		return 0;
	}

	@Override
	public FriendsGroup getItem(int position) {
		if (data != null) {
			return data.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		if (data != null) {
			return data.get(position).getId();
		}
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		FriendsGroup fg = data.get(position);
		ViewHodler vh = null;
		if (convertView == null) {
			convertView = View.inflate(mContext,
					R.layout.friends_group_control_item, null);
			vh = new ViewHodler(convertView);
			convertView.setTag(vh);
		} else {
			vh = (ViewHodler) convertView.getTag();
		}
		vh.friendsGroupName.setText(fg.getName());
		return convertView;
	}

	class ViewHodler {
		@InjectView(R.id.group_delete)
		ImageView groupDeleteIcon;
		@InjectView(R.id.friends_group_name)
		TextView friendsGroupName;
		@InjectView(R.id.delete_btn)
		Button deleteBtn;
		@InjectView(R.id.drag_handle)
		ImageView moveBtn;

		public ViewHodler(View view) {
			ButterKnife.inject(this, view);
		}

	}

	public void remove(FriendsGroup item) {
		data.remove(item);
		notifyDataSetChanged();
	}

	public void insert(FriendsGroup item, int to) {
		data.add(to, item);
	}

	public List<FriendsGroup> getData() {
		return data;
	}

	public void setData(List<FriendsGroup> data) {
		this.data = data;
		notifyDataSetChanged();
	}
}
