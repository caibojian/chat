package com.cai.chat_05.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cai.chat_05.R;
import com.cai.chat_05.bean.Attachment;
import com.cai.chat_05.bean.Constants;
import com.cai.chat_05.bean.User;
import com.cai.chat_05.cache.CacheManager;
import com.cai.chat_05.view.CircularImage;
import com.cai.chat_05.view.TitleBarView;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class SettingFragment extends Fragment {

	private Activity mContext;
	private View mBaseView;
	@InjectView(R.id.title_bar)
	protected TitleBarView mTitleBarView;

	private PopupWindow mPopupWindow;
	private ImageView mChats, mShare, mCamera, mScan;
	private View mPopView;

	@InjectView(R.id.pic)
	protected CircularImage photo;

	@InjectView(R.id.name)
	protected TextView name;
	@InjectView(R.id.signature)
	protected TextView signature;

	@InjectView(R.id.set_rl)
	protected RelativeLayout setRl;

	@InjectView(R.id.qr_code_rl)
	protected RelativeLayout qrCodeRl;
	@InjectView(R.id.update_rl)
	protected RelativeLayout updateRl;
	@InjectView(R.id.gender)
	protected ImageView gender;

	@InjectView(R.id.rl_canvers)
	protected RelativeLayout mCanversLayout;

//	final UMSocialService mController = UMServiceFactory
//			.getUMSocialService("com.umeng.share");
	public static final String DEFAULT = "http://www.weishe.org/";
	private String mCurrentUrl = DEFAULT;
	private User user;

	private Attachment avatar;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		mContext = getActivity();
		mBaseView = inflater.inflate(R.layout.fragment_my, null);
		mPopView = inflater.inflate(R.layout.fragment_tools_pop, null);
		user = (User) CacheManager.readObject(mContext,
				Constants.CACHE_CURRENT_USER);
//		avatar = JSON.parseObject(user.getAvatar(), Attachment.class);

		return mBaseView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		init(view);
	}

	private void init(View view) {
		ButterKnife.inject(this, view);

		mTitleBarView.setCommonTitle(View.GONE, View.VISIBLE, View.GONE,
				View.VISIBLE);
		mTitleBarView.setTitleText(R.string.mime);
		mTitleBarView.setBtnRight(R.drawable.skin_conversation_title_right_btn);
		mTitleBarView.setBtnRightOnclickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mTitleBarView.setPopWindow(mPopupWindow, mTitleBarView);
				mCanversLayout.setVisibility(View.VISIBLE);
			}
		});

		mChats = (ImageView) mPopView.findViewById(R.id.pop_chat);
		mShare = (ImageView) mPopView.findViewById(R.id.pop_sangzhao);
		mCamera = (ImageView) mPopView.findViewById(R.id.pop_camera);
		mScan = (ImageView) mPopView.findViewById(R.id.pop_scan);

		mPopupWindow = new PopupWindow(mPopView, LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT, true);
		mPopupWindow.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss() {
				mTitleBarView
						.setBtnRight(R.drawable.skin_conversation_title_right_btn);
				mCanversLayout.setVisibility(View.GONE);
			}
		});

		mScan.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
//				UIHelper.startErCodeScanActivity(mContext);
//				mPopupWindow.dismiss();
			}
		});

		mCamera.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
//				Intent intent = new Intent(mContext, WaterCameraActivity.class);
//				startActivity(intent);
//				mPopupWindow.dismiss();

			}
		});
		mShare.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
//				Intent intent = new Intent(mContext, WaterCameraActivity.class);
//
//				mPopupWindow.dismiss();
//				showSharedDialog();
			}
		});

		name.setText(user.getName());
		signature.setText(user.getSignature());
		switch (user.getGender()) {
		case User.GENDER_FEMALE:
			gender.setImageResource(R.drawable.female);
			break;

		case User.GENDER_MALE:
			gender.setImageResource(R.drawable.male);
			break;
		}
		if (avatar != null) {
//			photo.setImage(avatar.getGroupName(), avatar.getPath());
		}

		qrCodeRl.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
//				UIHelper.startQrCodeActivity(mContext);
			}
		});

		updateRl.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
//				UpdateManager.getUpdateManager().checkAppUpdate(mContext, true);
			}
		});
	}

	/**
	 * 打开分享dialog
	 */
	private void showSharedDialog() {
//		final ShareDialog dialog = new ShareDialog(getActivity());
//		dialog.setCancelable(true);
//		dialog.setCanceledOnTouchOutside(true);
//		dialog.setTitle(R.string.share_to);
//		dialog.setOnPlatformClickListener(new OnSharePlatformClick() {
//			@Override
//			public void onPlatformClick(int id) {
//
//				switch (id) {
//				case R.id.ly_share_weichat_circle:
//					shareToWeiChatCircle();
//					break;
//				case R.id.ly_share_weichat:
//					shareToWeiChat();
//					break;
//				case R.id.ly_share_sina_weibo:
//					shareToSinaWeibo();
//					break;
//				case R.id.ly_share_qq:
//					shareToQQ(SHARE_MEDIA.QQ);
//					break;
//				case R.id.ly_share_copy_link:
//					TDevice.copyTextToBoard(mCurrentUrl);
//					break;
//				case R.id.ly_share_more_option:
//					TDevice.showSystemShareOption(getActivity(),
//							getShareTitle(), mCurrentUrl);
//					break;
//				default:
//					break;
//				}
//				dialog.dismiss();
//			}
//		});
//		dialog.show();
	}

//	protected void shareToQQ(SHARE_MEDIA media) {
//		UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(getActivity(),
//				Constants.QQ_APPID, Constants.QQ_APPKEY);
//		qqSsoHandler.setTargetUrl(mCurrentUrl);
//		qqSsoHandler.setTitle(getShareTitle());
//		qqSsoHandler.addToSocialSDK();
//		mController.setShareImage(getShareImg());
//		mController.setShareContent(getShareContent());
//		mController.postShare(getActivity(), media, null);
//	}

	@SuppressWarnings("deprecation")
	private void shareToWeiChatCircle() {
//		// 支持微信朋友圈
//		UMWXHandler wxCircleHandler = new UMWXHandler(getActivity(),
//				Constants.WEICHAT_APPID);
//		wxCircleHandler.setToCircle(true);
//		wxCircleHandler.addToSocialSDK();
//		// 设置微信朋友圈分享内容
//		CircleShareContent circleMedia = new CircleShareContent();
//		// 设置朋友圈title
//		circleMedia.setTitle(getShareTitle());
//		circleMedia.setShareContent(getShareContent());
//		circleMedia.setShareImage(getShareImg());
//		circleMedia.setTargetUrl(mCurrentUrl);
//		mController.setShareMedia(circleMedia);
//		mController.postShare(getActivity(), SHARE_MEDIA.WEIXIN_CIRCLE, null);
	}

	@SuppressWarnings("deprecation")
	private void shareToWeiChat() {
//		// 添加微信平台
//		UMWXHandler wxHandler = new UMWXHandler(getActivity(),
//				Constants.WEICHAT_APPID);
//		wxHandler.addToSocialSDK();
//		// 设置微信好友分享内容
//		WeiXinShareContent weixinContent = new WeiXinShareContent();
//		// 设置分享文字
//		weixinContent.setShareContent(getShareContent());
//		// 设置title
//		weixinContent.setTitle(getShareTitle());
//		// 设置分享内容跳转URL
//		weixinContent.setTargetUrl(mCurrentUrl);
//		// 设置分享图片
//		weixinContent.setShareImage(getShareImg());
//		mController.setShareMedia(weixinContent);
//		mController.postShare(getActivity(), SHARE_MEDIA.WEIXIN, null);
	}

	private void shareToSinaWeibo() {
//		// 设置新浪微博SSO handler
//		mController.getConfig().setSsoHandler(new SinaSsoHandler());
//		if (OauthHelper.isAuthenticated(getActivity(), SHARE_MEDIA.SINA)) {
//			shareContent(SHARE_MEDIA.SINA);
//		} else {
//			mController.doOauthVerify(getActivity(), SHARE_MEDIA.SINA,
//					new UMAuthListener() {
//
//						@Override
//						public void onStart(SHARE_MEDIA arg0) {
//						}
//
//						@Override
//						public void onError(SocializeException arg0,
//								SHARE_MEDIA arg1) {
//						}
//
//						@Override
//						public void onComplete(Bundle arg0, SHARE_MEDIA arg1) {
//							shareContent(SHARE_MEDIA.SINA);
//						}
//
//						@Override
//						public void onCancel(SHARE_MEDIA arg0) {
//						}
//					});
//		}
	}

//	private void shareContent(SHARE_MEDIA media) {
//		mController.setShareContent(getShareContent() + mCurrentUrl);
//		mController.directShare(getActivity(), media, null);
//	}

//	protected UMImage getShareImg() {
//		UMImage img = new UMImage(getActivity(), R.drawable.ic_launcher);
//		return img;
//	}

	private String getShareContent() {
		return "内容";
	}

	private String getShareTitle() {
		return "名字";
	}
}
