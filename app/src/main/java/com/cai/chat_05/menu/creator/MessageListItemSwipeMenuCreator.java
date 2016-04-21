package com.cai.chat_05.menu.creator;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;


import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.cai.chat_05.MainActivity;
import com.cai.chat_05.R;
import com.cai.chat_05.adppter.MessageListAdapter;
import com.cai.chat_05.fragment.MessageListFragment;


public class MessageListItemSwipeMenuCreator implements SwipeMenuCreator,
		SwipeMenuListView.OnMenuItemClickListener {

	private MainActivity mContext;
	private MessageListAdapter mAdapter;
	private MessageListFragment messageListFragment;
	private int userId;
	private String token;

	public MessageListItemSwipeMenuCreator(MainActivity context,
										   MessageListFragment messageListFragment, MessageListAdapter adapter) {
		this.mContext = context;
		this.mAdapter = adapter;
		this.messageListFragment = messageListFragment;
//		User user = (User) CacheManager.readObject(mContext,
//				Constants.CACHE_CURRENT_USER);
//		userId = user.getId();
//		token = (String) CacheManager.readObject(mContext,
//				Constants.CACHE_CURRENT_USER_TOKEN);

	}

	@Override
	public void create(SwipeMenu menu) {
		switch (menu.getViewType()) {
		case MessageListAdapter.TYPE_TODO:
			createTodoMenu(menu);
			break;
		case MessageListAdapter.TYPE_UU_CHATMESSAGE:
			createChatMessageMenu(menu);
			break;
		case 3:
			createMenu3(menu);
			break;
		}
	}

	private void createTodoMenu(SwipeMenu menu) {
		SwipeMenuItem item1 = new SwipeMenuItem(mContext);
		item1.setBackground(new ColorDrawable(Color.rgb(0xE5, 0x18, 0x5E)));
		item1.setWidth(dp2px(90));
		 item1.setIcon(R.drawable.ic_action_favorite);
		item1.setTitleSize(15);
		item1.setTitle("同意");
		menu.addMenuItem(item1);
		SwipeMenuItem item2 = new SwipeMenuItem(mContext);
		item2.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9, 0xCE)));
		item2.setWidth(dp2px(90));
		 item2.setIcon(R.drawable.ic_action_good);
		item2.setTitleColor(R.id.text);
		item2.setTitle("拒绝");
		item2.setTitleSize(15);
		menu.addMenuItem(item2);
	}

	private void createChatMessageMenu(SwipeMenu menu) {
		SwipeMenuItem item1 = new SwipeMenuItem(mContext);
		item1.setBackground(new ColorDrawable(Color.rgb(0xE5, 0xE0, 0x3F)));
		item1.setWidth(dp2px(90));
		item1.setIcon(R.drawable.ic_action_important);
		item1.setTitle("标为未读");
		item1.setTitleSize(10);
		item1.setTitleColor(R.id.text);
		menu.addMenuItem(item1);

		SwipeMenuItem item2 = new SwipeMenuItem(mContext);
		item2.setBackground(new ColorDrawable(Color.rgb(0xF9, 0x3F, 0x25)));
		item2.setWidth(dp2px(90));
		item2.setTitleColor(R.id.text);
		item2.setTitle("删除");
		item2.setTitleSize(10);
		item2.setIcon(R.drawable.ic_action_discard);
		menu.addMenuItem(item2);
	}

	private void createMenu3(SwipeMenu menu) {
		SwipeMenuItem item1 = new SwipeMenuItem(mContext);
		item1.setBackground(new ColorDrawable(Color.rgb(0x30, 0xB1, 0xF5)));
		item1.setWidth(dp2px(90));
		item1.setIcon(R.drawable.ic_action_about);
		menu.addMenuItem(item1);
		SwipeMenuItem item2 = new SwipeMenuItem(mContext);
		item2.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9, 0xCE)));
		item2.setWidth(dp2px(90));
		item2.setIcon(R.drawable.ic_action_share);
		menu.addMenuItem(item2);
	}

	private int dp2px(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				mContext.getResources().getDisplayMetrics());
	}

	@Override
	public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
		int viewType = mAdapter.getItemViewType(position);
		if (token == null || token.isEmpty()) {
			try {
//				this.userId = mContext.getSessionService().getUserId();
//				this.token = mContext.getSessionService().getToken();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

//		switch (viewType) {
//		case MessageListAdapter.TYPE_TODO:
//			Todo todo = (Todo) mAdapter.getData().get(position);
//
//			switch (index) {
//			case 0:
//				// 同意
//				WeisheApi.dealWithTodo(fHandler, userId, token,
//						todo.getTodoId(), true);
//				break;
//			case 1:
//				// 拒绝
//				WeisheApi.dealWithTodo(fHandler, userId, token,
//						todo.getTodoId(), false);
//				break;
//			}
//			break;
//		case MessageListAdapter.TYPE_UU_CHATMESSAGE:
//			ChatMessage msg = (ChatMessage) mAdapter.getData().get(position);
//			switch (index) {
//			case 0:
//				// 标为未读
//				DBHelper.getgetInstance(mContext).updateChatMessageUnCheck(
//						msg.getId());
//				break;
//			case 1:
//				// 删除
//				DBHelper.getgetInstance(mContext).deleteChatMessageByType(
//						msg.getId());
//				break;
//			}
//			messageListFragment.rushDBData();
//			mAdapter.notifyDataSetChanged();
//			break;
//
//		}

		return false;
	}

//	AsyncHttpResponseHandler fHandler = new AsyncHttpResponseHandler() {
//
//		@Override
//		public void onSuccess(int statusCode, Header[] headers,
//				byte[] responseBytes) {
//			String data = new String(responseBytes);
//			Result r = (Result) JSON.parseObject(data, Result.class);
//			if (r != null) {
//				Todo todo = (Todo) JSON.parseObject(r.getObj().toString(),
//						Todo.class);
//				if (r.isSuccess()) {
//					showCustomToast("成功处理！", mContext);
//				} else {
//					showCustomToast("处理失败！", mContext);
//				}
//				DBHelper.getgetInstance(mContext).updateTodo(
//						todo.getId().intValue(), todo.getComplete(),
//						todo.getAgree(), todo.getRequestMsg());
//				messageListFragment.rushDBData();
//				mAdapter.notifyDataSetChanged();
//			} else {
//				showCustomToast("处理发生异常！", mContext);
//			}
//		}
//
//		@Override
//		public void onFailure(int arg0, Header[] arg1, byte[] arg2,
//				Throwable arg3) {
//			showCustomToast("处理发生异常！", mContext);
//
//		}
//
//	};

	/** 显示自定义Toast提示(来自String) **/
	protected void showCustomToast(String text, Context context) {
//		View toastRoot = LayoutInflater.from(context).inflate(
//				R.layout.common_toast, null);
//		((HandyTextView) toastRoot.findViewById(R.id.toast_text)).setText(text);
//		Toast toast = new Toast(context);
//		toast.setGravity(Gravity.CENTER, 0, 0);
//		toast.setDuration(Toast.LENGTH_SHORT);
//		toast.setView(toastRoot);
//		toast.show();
	}
}
