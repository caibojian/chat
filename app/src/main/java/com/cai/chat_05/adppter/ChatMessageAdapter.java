package com.cai.chat_05.adppter;

import java.io.File;
import java.io.IOException;
import java.util.List;

import android.annotation.TargetApi;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.text.Html;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cai.chat_05.ChatActivity;
import com.cai.chat_05.R;
import com.cai.chat_05.bean.Attachment;
import com.cai.chat_05.bean.Constants;
import com.cai.chat_05.bean.User;
import com.cai.chat_05.cache.CacheManager;
import com.cai.chat_05.core.bean.ChatMessage;
import com.cai.chat_05.emoji.InputHelper;
import com.cai.chat_05.utils.AttachmentManager;
import com.cai.chat_05.utils.DBHelper;
import com.cai.chat_05.utils.StringUtils;
import com.cai.chat_05.utils.UIHelper;
import com.cai.chat_05.view.CircularImage;
import com.cai.chat_05.view.TweetTextView;

public class ChatMessageAdapter extends BaseAdapter implements OnClickListener {
	private List<ChatMessage> chatEntities;
	private LayoutInflater mInflater;
	private ChatActivity mContext0;
	private Thread mRecordThread;

	private boolean playState;// 播放状态
	private int aioActionStep = 0;
	private ImageView currentPlayer;// 被点击的语音消息图标
	private int direction;// 是自己（0）发送的还是朋友（1）发送 的
	private MediaPlayer mMediaPlayer;
	private int mScreenWidth, mScreenHeight;

	private User user;
	private String token;

	public ChatMessageAdapter(ChatActivity context, List<ChatMessage> vector) {
		this.chatEntities = vector;
		mInflater = LayoutInflater.from(context);
		mContext0 = context;
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);

		DisplayMetrics dm = new DisplayMetrics();

		wm.getDefaultDisplay().getMetrics(dm);
		mScreenWidth = dm.widthPixels;// 获取屏幕分辨率宽度
		mScreenHeight = dm.heightPixels;
		user = (User) CacheManager.readObject(mContext0,
				Constants.CACHE_CURRENT_USER);
		token = (String) CacheManager.readObject(mContext0,
				Constants.CACHE_CURRENT_USER_TOKEN);
	}

	private void getUser(int userId, User user) {
		User u = (User) CacheManager.readObject(mContext0,
				User.getCacheKey(userId));
//		if (u == null) {
//			// 去网络上取
//		}
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		ChatMessage chatEntity = chatEntities.get(position);
		switch (chatEntity.getContentType()) {
		case Constants.CONTENT_TYPE_ATTACHMENT:
			return getAttachmentView(position, view, parent, chatEntity);
		case Constants.CONTENT_TYPE_NORMAL:
			return getNormalView(position, view, parent, chatEntity);
		default:
			return getNormalView(position, view, parent, chatEntity);
		}
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	private View getAttachmentView(int position, View view, ViewGroup parent,
								   ChatMessage chatEntity) {
		LinearLayout leftLayout, leftLayoutP;
		LinearLayout rightLayout, rightLayoutP;
		ImageView userplayer;
		ImageView friendsplayer;
		CircularImage uPhoto, fPhoto;
		TextView msgStatus, rVoiceTime, lVoiceTime;

		view = mInflater.inflate(R.layout.chat_attachment_message_item, null);

		msgStatus = (TextView) view.findViewById(R.id.msg_status);
		rVoiceTime = (TextView) view.findViewById(R.id.voice_time);
		lVoiceTime = (TextView) view.findViewById(R.id.friend_voice_time);
		leftLayout = (LinearLayout) view
				.findViewById(R.id.chat_friend_left_layout);
		rightLayout = (LinearLayout) view
				.findViewById(R.id.chat_user_right_layout);
		leftLayoutP = (LinearLayout) view
				.findViewById(R.id.chat_message_left_layout);
		rightLayoutP = (LinearLayout) view
				.findViewById(R.id.chat_message_right_layout);
		fPhoto = (CircularImage) view
				.findViewById(R.id.message_friend_userphoto);
		uPhoto = (CircularImage) view.findViewById(R.id.message_user_userphoto);
		userplayer = (ImageView) view.findViewById(R.id.voice_player_user);
		friendsplayer = (ImageView) view
				.findViewById(R.id.voice_player_friends);
		Attachment file = DBHelper.getgetInstance(mContext0).getAttachment(
				chatEntity.getAttachmentId());
		String path = AttachmentManager.getFilePath(file);
		String time = "";
		int length = 60;
		if (path != null && !path.isEmpty()) {
			File f = new File(path);
			long t = 0;
			try {
				t = AttachmentManager.getAmrDuration(f);
			} catch (IOException e) {
				e.printStackTrace();
			}
			time = t / 1000 + "``";
			Log.v("org.weishe.weichat.w", "mScreenWidth=" + mScreenWidth);
			Log.v("org.weishe.weichat.w", "Math.sqrt(t)=" + Math.sqrt(t / 1000));
			double l = mScreenWidth / 5 * 3
					- ((mScreenWidth / 5 * 3) / Math.pow(t / 1500, 2));

			if (l > length) {
				Log.v("org.weishe.weichat.w", "l=" + l);
				length = (int) l;
			}
		}
		Log.v("org.weishe.weichat.w", "length=" + length);
		if (chatEntity.getType() == Constants.TYPE_SEND) {

			switch (chatEntity.getStatus()) {
			case Constants.STATUS_READ:
				msgStatus.setText(R.string.message_read);
				msgStatus.setBackground(mContext0.getResources().getDrawable(
						R.drawable.msg_read_status_bg));
				break;
			case Constants.STATUS_RECEIVED:
				msgStatus.setText(R.string.message_receive);
				msgStatus.setBackground(mContext0.getResources().getDrawable(
						R.drawable.msg_read_status_bg));
				break;
			case Constants.STATUS_SEND:
				msgStatus.setText(R.string.message_send);
				msgStatus.setBackground(mContext0.getResources().getDrawable(
						R.drawable.msg_send_status_bg));
				break;
			case Constants.STATUS_UNKNOWN:
				msgStatus.setBackground(mContext0.getResources().getDrawable(
						R.drawable.msg_status_resend_selector));
				break;
			}

			rVoiceTime.setText(time);
			LayoutParams lp = rVoiceTime.getLayoutParams();
			lp.width = length;
			rVoiceTime.setLayoutParams(lp);
			rightLayoutP.setOnClickListener(this);

			rightLayoutP.setTag(R.id.tag_attachment, file);
			rightLayoutP.setTag(R.id.tag_attachment_group_name,
					chatEntity.getFileGroupName());
			rightLayoutP.setTag(R.id.tag_attachment_path,
					chatEntity.getFilePath());
			rightLayoutP.setTag(R.id.tag_imageview, userplayer);
			rightLayoutP.setTag(R.id.tag_direction, 0);
			rightLayout.setVisibility(View.VISIBLE);
			leftLayout.setVisibility(View.GONE);

			User u = (User) CacheManager.readObject(mContext0,
					User.getCacheKey(chatEntity.getFromId()));

			if (u != null && u.getAvatar() != null && !u.getAvatar().isEmpty()) {
				String[] str = u.getAvatar().split("#");
				if (str.length == 2) {
					uPhoto.setImage(str[0], str[1]);
				}
			}
			uPhoto.setTag(chatEntity.getFromId());
			uPhoto.setOnClickListener(onPhoto);
		} else if (chatEntity.getType() == Constants.TYPE_RECEIVE) {// 本身作为接收方
			lVoiceTime.setText(time);

			LayoutParams lp = lVoiceTime.getLayoutParams();
			int w = lp.width;
			lp.width = length;
			lVoiceTime.setLayoutParams(lp);
			leftLayoutP.setOnClickListener(this);
			leftLayoutP.setTag(R.id.tag_attachment, file);
			leftLayoutP.setTag(R.id.tag_attachment_group_name,
					chatEntity.getFileGroupName());
			leftLayoutP.setTag(R.id.tag_attachment_path,
					chatEntity.getFilePath());
			leftLayoutP.setTag(R.id.tag_imageview, friendsplayer);
			leftLayoutP.setTag(R.id.tag_direction, 1);
			leftLayout.setVisibility(View.VISIBLE);
			rightLayout.setVisibility(View.GONE);

			User u = (User) CacheManager.readObject(mContext0,
					User.getCacheKey(chatEntity.getFromId()));

			if (u != null && u.getAvatar() != null && !u.getAvatar().isEmpty()) {
				String[] str = u.getAvatar().split("#");
				if (str.length == 2) {
					fPhoto.setImage(str[0], str[1]);
				}
			}
			fPhoto.setTag(chatEntity.getFromId());
			fPhoto.setOnClickListener(onPhoto);
		}

		return view;
	}

	@Override
	public boolean isEnabled(int position) {
		return false;
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	private View getNormalView(int position, View view, ViewGroup parent,
							   final ChatMessage chatEntity) {

		RelativeLayout leftLayout;
		RelativeLayout rightLayout;
		TweetTextView leftMessageView;
		TweetTextView rightMessageView;
		TextView timeView, msgStatus;
		TextView friendsNameView;
		CircularImage leftPhotoView;
		CircularImage rightPhotoView;
		view = mInflater.inflate(R.layout.chat_message_item, null);
		msgStatus = (TextView) view.findViewById(R.id.msg_status);
		leftLayout = (RelativeLayout) view
				.findViewById(R.id.chat_friend_left_layout);
		rightLayout = (RelativeLayout) view
				.findViewById(R.id.chat_user_right_layout);
		timeView = (TextView) view.findViewById(R.id.message_time);
		friendsNameView = (TextView) view.findViewById(R.id.friend_name);
		leftPhotoView = (CircularImage) view
				.findViewById(R.id.message_friend_userphoto);
		rightPhotoView = (CircularImage) view
				.findViewById(R.id.message_user_userphoto);
		leftMessageView = (TweetTextView) view
				.findViewById(R.id.friend_message);
		rightMessageView = (TweetTextView) view.findViewById(R.id.user_message);

		timeView.setText(StringUtils.getDateString(chatEntity.getDate()));

		Spanned span = Html.fromHtml(TweetTextView.modifyPath(chatEntity
				.getContent()));
		span = InputHelper.displayEmoji(parent.getContext().getResources(),
				span.toString());
		User u = (User) CacheManager.readObject(mContext0,
				User.getCacheKey(chatEntity.getFromId()));

		if (chatEntity.getMsgType() != Constants.MSG_TYPE_UU) {
			friendsNameView.setVisibility(View.VISIBLE);
			friendsNameView.setText(u.getName());
		}

		if (chatEntity.getType() == Constants.TYPE_SEND) {
			switch (chatEntity.getStatus()) {
			case Constants.STATUS_READ:
				msgStatus.setText(R.string.message_read);
				msgStatus.setBackground(mContext0.getResources().getDrawable(
						R.drawable.msg_read_status_bg));
				break;
			case Constants.STATUS_RECEIVED:
				msgStatus.setText(R.string.message_receive);
				msgStatus.setBackground(mContext0.getResources().getDrawable(
						R.drawable.msg_read_status_bg));
				break;
			case Constants.STATUS_SEND:
				msgStatus.setText(R.string.message_send);
				msgStatus.setBackground(mContext0.getResources().getDrawable(
						R.drawable.msg_send_status_bg));
				break;
			case Constants.STATUS_UNKNOWN:
				msgStatus.setBackground(mContext0.getResources().getDrawable(
						R.drawable.msg_status_resend_selector));
				msgStatus.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
//						try {
//							mContext0.getSessionService().sendMessage(
//									chatEntity.getUuid(),
//									chatEntity.getContentType(),
//									chatEntity.getContent(),
//									chatEntity.getToId(),
//									chatEntity.getMsgType(),
//									chatEntity.getFileGroupName() + "",
//									chatEntity.getFilePath() + "");
//						} catch (RemoteException e) {
//							e.printStackTrace();
//						}
					}
				});
				break;
			}

			rightLayout.setVisibility(View.VISIBLE);
			leftLayout.setVisibility(View.GONE);

			rightMessageView.setText(span);
			rightPhotoView.setTag(chatEntity.getFromId());
			rightPhotoView.setOnClickListener(onPhoto);
			if (u != null && u.getAvatar() != null && !u.getAvatar().isEmpty()) {
				String[] str = u.getAvatar().split("#");
				if (str.length == 2) {
					rightPhotoView.setImage(str[0], str[1]);
				}
			}

		} else if (chatEntity.getType() == Constants.TYPE_RECEIVE) {// 本身作为接收方
			leftLayout.setVisibility(View.VISIBLE);
			rightLayout.setVisibility(View.GONE);

			leftMessageView.setText(span);

			leftPhotoView.setTag(chatEntity.getFromId());
			leftPhotoView.setOnClickListener(onPhoto);

			if (u != null && u.getAvatar() != null && !u.getAvatar().isEmpty()) {
				String[] str = u.getAvatar().split("#");
				if (str.length == 2) {
					leftPhotoView.setImage(str[0], str[1]);
				}
			}

		}

		return view;
	}

	@Override
	public int getCount() {
		return chatEntities.size();
	}

	@Override
	public Object getItem(int position) {
		return chatEntities.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public void onClick(View v) {

		Attachment a = (Attachment) v.getTag(R.id.tag_attachment);
		String groupName = (String) v.getTag(R.id.tag_attachment_group_name);
		String path0 = (String) v.getTag(R.id.tag_attachment_path);
		currentPlayer = (ImageView) v.getTag(R.id.tag_imageview);
		direction = (int) v.getTag(R.id.tag_direction);
		if (a == null) {

			a = DBHelper.getgetInstance(mContext0).getAttachment(groupName,
					path0);
			if (a == null) {
				return;
			}
		}
		String path = AttachmentManager.getFilePath(a);
		if (path != null && !path.isEmpty()) {
			play(path);
			recordTimethread();
		} else {
			new AsyncTask<Attachment, Object, String>() {

				@Override
				protected void onPostExecute(String result) {
					play(result);
					recordTimethread();
				}

				@Override
				protected String doInBackground(Attachment... params) {
					Attachment b = params[0];
					String path = "";
					if (b != null) {
						// 防止异常
						b.setType(Attachment.TYPE_VOICE);
						path = AttachmentManager.getFile(b);
					}
					return path;
				}
			}.execute(a);
		}
	}

	// 录音播放图标线程
	void recordTimethread() {
		mRecordThread = new Thread(recordThread);
		mRecordThread.start();
	}

	// 录音播放线程
	private Runnable recordThread = new Runnable() {

		@Override
		public void run() {
			aioActionStep = 1;
			while (playState) {
				{
					try {
						Thread.sleep(400);
						aioActionStep++;
						if (aioActionStep > 3) {
							aioActionStep = 1;
						}
						recordHandler.sendEmptyMessage(1);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	};

	private void setPlayerImage() {
		switch (aioActionStep) {
		case 1:
			if (direction == 0) {
				currentPlayer
						.setImageResource(R.drawable.skin_aio_ptt_action_r_1);
			} else {
				currentPlayer
						.setImageResource(R.drawable.skin_aio_ptt_action_l_1);
			}
			break;
		case 2:
			if (direction == 0) {
				currentPlayer
						.setImageResource(R.drawable.skin_aio_ptt_action_r_2);
			} else {
				currentPlayer
						.setImageResource(R.drawable.skin_aio_ptt_action_l_2);
			}
			break;
		case 3:
			if (direction == 0) {
				currentPlayer
						.setImageResource(R.drawable.skin_aio_ptt_action_r_3);
			} else {
				currentPlayer
						.setImageResource(R.drawable.skin_aio_ptt_action_l_3);
			}
			break;
		default:
			break;
		}
	}

	public Handler recordHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			setPlayerImage();
		}
	};

	/**
	 * 播放音频
	 * 
	 * @param path
	 */
	private void play(String path) {
		if (!playState) {
			mMediaPlayer = new MediaPlayer();
			try {
				mMediaPlayer.setDataSource(path);
				mMediaPlayer.prepare();

				playState = true;
				mMediaPlayer.start();

				// 设置播放结束时监听
				mMediaPlayer
						.setOnCompletionListener(new OnCompletionListener() {

							@Override
							public void onCompletion(MediaPlayer mp) {
								if (playState) {

									playState = false;
								}
							}
						});
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		} else {
			if (mMediaPlayer.isPlaying()) {
				mMediaPlayer.stop();
				playState = false;
			} else {
				playState = false;
			}

		}
	}

	public void setData(List<ChatMessage> chatList) {
		this.chatEntities.clear();
		this.chatEntities.addAll(chatList);
	}

	private OnClickListener onPhoto = new OnClickListener() {
		@Override
		public void onClick(View v) {
			int userId = (int) v.getTag();
			UIHelper.startUserInforActivity(mContext0, user.getId(), userId,
					token, Constants.INTENT_EXTRA_USER_INFOR_TYPE_USERINFOR);
		}
	};
}
