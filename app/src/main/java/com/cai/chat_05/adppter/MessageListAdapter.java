package com.cai.chat_05.adppter;

//import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.cai.chat_05.MainActivity;
import com.cai.chat_05.R;
import com.cai.chat_05.bean.Todo;
import com.cai.chat_05.core.bean.ChatMessage;
import com.cai.chat_05.view.CircularImage;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MessageListAdapter extends BaseAdapter {
	public static final int TYPE_COUNT = 5;

	public static final int TYPE_UU_CHATMESSAGE = 0;
	public static final int TYPE_UCG_CHATMESSAGE = 1;
	public static final int TYPE_UDG_CHATMESSAGE = 2;
	public static final int TYPE_TYPE_SEPARATOR = 3;
	public static final int TYPE_TODO = 4;
	private MainActivity mActivity;
	private List data;
//	private User user;
//	private List<ChatGroup> chatGroups;
	private LayoutInflater mInflater;

	public MessageListAdapter(MainActivity context) {
		this.mActivity = context;
		data = new ArrayList();
		mInflater = LayoutInflater.from(context);
//		user = (User) CacheManager.readObject(mActivity,
//				Constants.CACHE_CURRENT_USER);
//		chatGroups = (List<ChatGroup>) CacheManager.readObject(mActivity,
//				ChatGroup.getCacheKey(user.getId()));
	}

	@Override
	public int getCount() {
		if (data != null) {
			return data.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		if (data != null && (data.size() >= (position + 1))) {
			return data.get(position);
		}
		return null;
	}

	public long getItemId(int position) {
		if (data != null && (data.size() >= (position + 1))) {
			Object o = data.get(position);
			if (o instanceof Todo) {
				Todo todo = (Todo) o;
				return todo.getId() + 200000l;// 防止重复id
			} else if (o instanceof ChatMessage) {
				ChatMessage cm = (ChatMessage) o;
				return cm.getId();
			} else {
				return 0;
			}
		} else {
			return 0;
		}
	}

	@Override
	public int getItemViewType(int position) {
		ChatMessage message = null;
		Todo todo = null;
		if (data.get(position) instanceof ChatMessage) {
			ChatMessage cm = (ChatMessage) data.get(position);
			switch (cm.getMsgType()) {
			case ChatMessage.MSG_TYPE_UU:
				return TYPE_UU_CHATMESSAGE;
			case ChatMessage.MSG_TYPE_UCG:
				return TYPE_UCG_CHATMESSAGE;
			case ChatMessage.MSG_TYPE_UDG:
				return TYPE_UDG_CHATMESSAGE;
			}
		} else if (data.get(position) instanceof Todo) {
			return TYPE_TODO;
		}
		return -1;
	}

	@Override
	public int getViewTypeCount() {
		return TYPE_COUNT;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ChatMessage message = null;
		Todo todo = null;
		int viewType = getItemViewType(position);
		// View view = null;
		switch (viewType) {
		case TYPE_UU_CHATMESSAGE:
			MessageHolder mh = null;
			message = (ChatMessage) data.get(position);
			// 创建一个View，简单起见直接使用系统提供的布局，就是一个TextView
			if (convertView == null) {
				convertView = View.inflate(mActivity,
						R.layout.message_list_item, null);
				mh = new MessageHolder(convertView);
				convertView.setTag(mh);
			} else {
				mh = (MessageHolder) convertView.getTag();
			}

			int uc = message.getUnCheckedCount();
			if (uc > 0) {
				mh.unReadCount.setVisibility(View.VISIBLE);
				if (uc < 10) {
					mh.unReadCount.setText(uc+"");
				} else {
					mh.unReadCount.setText("9+");
				}
			} else {
				mh.unReadCount.setVisibility(View.INVISIBLE);
			}
			//
			// mh.avatarView = (CircularImage) convertView
			// .findViewById(R.id.user_photo);
			// mh.nameView = (TextView) convertView
			// .findViewById(R.id.friends_name);
			// mh.dateView = (TextView) convertView
			// .findViewById(R.id.message_date);
			// mh.messageView = (TextView) convertView
			// .findViewById(R.id.friends_message);
			//
			// mh.avatarView.setImageResource(R.drawable.channel_qq);
			String name = "";
//			if (mActivity.getFriends() != null) {
//				for (Friends friends : mActivity.getFriends()) {
//					switch (message.getType()) {
//					case ChatMessage.TYPE_RECEIVE:
//						if (friends.getUserId() == message.getFromId()) {
//							name = friends.getName();
//							if (friends.getAvatarPath() != null
//									&& !friends.getAvatarPath().isEmpty()) {
//								String[] str = friends.getAvatarPath().split(
//										"#");
//								if (str.length == 2) {
//									mh.avatarView.setImage(str[0], str[1]);
//								}
//							}
//						}
//						break;
//					case ChatMessage.TYPE_SEND:
//						if (friends.getUserId() == message.getToId()) {
//							name = friends.getName();
//							if (friends.getAvatarPath() != null
//									&& !friends.getAvatarPath().isEmpty()) {
//								String[] str = friends.getAvatarPath().split(
//										"#");
//								if (str.length == 2) {
//									mh.avatarView.setImage(str[0], str[1]);
//								}
//							}
//						}
//						break;
//					default:
//						break;
//					}
//				}
//				mh.nameView.setText(name);
//				mh.dateView
//						.setText(StringUtils.friendly_time(message.getDate()));
//				if (message.getContentType() == ChatMessage.CONTENT_TYPE_ATTACHMENT) {
//					Attachment a = DBHelper.getgetInstance(mActivity)
//							.getAttachment(message.getFileGroupName(),
//									message.getFilePath());
//					if (a != null) {
//						switch (a.getType()) {
//						case Attachment.TYPE_VIDEO:
//							mh.messageView.setText("[视频]");
//							break;
//						case Attachment.TYPE_VOICE:
//							mh.messageView.setText("[语音]");
//							break;
//						default:
//							mh.messageView.setText("[文件]");
//							break;
//						}
//
//					}
//				} else {
//
//					Spanned span = Html.fromHtml(TweetTextView
//							.modifyPath(message.getContent()));
//					span = InputHelper.displayEmoji(mActivity.getResources(),
//							span.toString());
//
//					mh.messageView.setText(span);
//				}
//			}
			break;

		case TYPE_UCG_CHATMESSAGE:
			MessageHolder mhcg = null;
			message = (ChatMessage) data.get(position);
			// 创建一个View，简单起见直接使用系统提供的布局，就是一个TextView
			if (convertView == null) {
				convertView = View.inflate(mActivity,
						R.layout.message_list_item, null);
				mhcg = new MessageHolder(convertView);
				convertView.setTag(mhcg);
			} else {
				mhcg = (MessageHolder) convertView.getTag();
			}
			int uccg = message.getUnCheckedCount();
			if (uccg > 0) {
				mhcg.unReadCount.setVisibility(View.VISIBLE);
				if (uccg < 10) {
					mhcg.unReadCount.setText(uccg+"");
				} else {
					mhcg.unReadCount.setText("9+");
				}
			} else {
				mhcg.unReadCount.setVisibility(View.INVISIBLE);
			}
			// mhcg.avatarView = (CircularImage) convertView
			// .findViewById(R.id.user_photo);
			// mhcg.nameView = (TextView) convertView
			// .findViewById(R.id.friends_name);
			// mhcg.dateView = (TextView) convertView
			// .findViewById(R.id.message_date);
			// mhcg.messageView = (TextView) convertView
			// .findViewById(R.id.friends_message);
			// mhcg.avatarView.setImageResource(R.drawable.channel_qq);
			String namecg = "";

//			for (ChatGroup cg : chatGroups) {
//				if (cg.getId() == message.getChatGroupId()) {
//					namecg = cg.getName();
//				}
//			}
//			mhcg.avatarView.setImageDrawable(mActivity.getResources()
//					.getDrawable(R.drawable.grouphead_normal));
//			mhcg.nameView.setText(namecg);
//			mhcg.dateView.setText(StringUtils.friendly_time(message.getDate()));
//			if (message.getContentType() == ChatMessage.CONTENT_TYPE_ATTACHMENT) {
//				Attachment a = DBHelper.getgetInstance(mActivity)
//						.getAttachment(message.getFileGroupName(),
//								message.getFilePath());
//				if (a != null) {
//					switch (a.getType()) {
//					case Attachment.TYPE_VIDEO:
//						mhcg.messageView.setText("[视频]");
//						break;
//					case Attachment.TYPE_VOICE:
//						mhcg.messageView.setText("[语音]");
//						break;
//					default:
//						mhcg.messageView.setText("[文件]");
//						break;
//					}
//
//				}
//			} else {
//
//				Spanned span = Html.fromHtml(TweetTextView.modifyPath(message
//						.getContent()));
//				span = InputHelper.displayEmoji(mActivity.getResources(),
//						span.toString());
//
//				mhcg.messageView.setText(span);
//			}

			break;
		case TYPE_UDG_CHATMESSAGE:
			break;
		case TYPE_TODO:
			todo = (Todo) data.get(position);
			// 创建一个View，简单起见直接使用系统提供的布局，就是一个TextView
			TodoHolder th = null;
			if (convertView == null) {
				convertView = View.inflate(mActivity, R.layout.todo_item, null);
				th = new TodoHolder(convertView);
				convertView.setTag(th);
			} else {
				th = (TodoHolder) convertView.getTag();
			}

			th.avatarView = (CircularImage) convertView
					.findViewById(R.id.user_photo);
			th.subjectView = (TextView) convertView.findViewById(R.id.subject);
			th.requestMsgView = (TextView) convertView
					.findViewById(R.id.request_msg);
			TextView todoDate = (TextView) convertView
					.findViewById(R.id.todo_date);

			th.subjectView.setText(todo.getTodoSubject());
			th.requestMsgView.setText(todo.getRequestMsg());
//			todoDate.setText(StringUtils.friendly_time(todo.getCreateDate()));
			break;
		}

		return convertView;
	}

	class TodoHolder {
		@InjectView(R.id.user_photo)
		CircularImage avatarView;
		@InjectView(R.id.subject)
		TextView subjectView;
		@InjectView(R.id.request_msg)
		TextView requestMsgView;

		public TodoHolder(View itemView) {
			ButterKnife.inject(this, itemView);
		}
	}

	class MessageHolder extends RecyclerView.ViewHolder {
		@InjectView(R.id.user_photo)
		CircularImage avatarView;
		@InjectView(R.id.friends_name)
		TextView nameView;
		@InjectView(R.id.message_date)
		TextView dateView;
		@InjectView(R.id.friends_message)
		TextView messageView;
		@InjectView(R.id.unread_message_count)
		TextView unReadCount;

		public MessageHolder(View itemView) {
			super(itemView);
			ButterKnife.inject(this, itemView);
		}
	}

	public List getData() {
		return data;
	}

	public void setData(List d) {
		this.data.clear();
		this.data.addAll(d);
		return;
	}
}
