package com.cai.chat_05.utils;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ZoomButtonsController;

import com.cai.chat_05.LoginActivity;
import com.cai.chat_05.MainActivity;
import com.cai.chat_05.SearchActivity;
import com.cai.chat_05.bean.ChatGroup;
import com.cai.chat_05.bean.DiscussionGroup;
import com.cai.chat_05.bean.Friends;

/**
 * 界面帮助类
 * 
 * @author FireAnt（http://my.oschina.net/LittleDY）
 * @version 创建时间：2014年10月10日 下午3:33:36
 * 
 */
public class UIHelper {

	/**
	 * 获取webviewClient对象
	 * 
	 * @return
	 */
	public static WebViewClient getWebViewClient() {

		return new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				showUrlRedirect(view.getContext(), url);
				return true;
			}
		};
	}

	/** 全局web样式 */
	// 链接样式文件，代码块高亮的处理
	public final static String linkCss = "<script type=\"text/javascript\" src=\"file:///android_asset/shCore.js\"></script>"
			+ "<script type=\"text/javascript\" src=\"file:///android_asset/brush.js\"></script>"
			+ "<script type=\"text/javascript\" src=\"file:///android_asset/client.js\"></script>"
			+ "<link rel=\"stylesheet\" type=\"text/css\" href=\"file:///android_asset/shThemeDefault.css\">"
			+ "<link rel=\"stylesheet\" type=\"text/css\" href=\"file:///android_asset/shCore.css\">"
			+ "<script type=\"text/javascript\">SyntaxHighlighter.all();</script>"
			+ "<script type=\"text/javascript\">function showImagePreview(var url){window.location.url= url;}</script>";
	public final static String WEB_STYLE = linkCss
			+ "<style>* {font-size:16px;line-height:20px;} p {color:#333;} a {color:#3E62A6;} img {max-width:310px;} "
			+ "img.alignleft {float:left;max-width:120px;margin:0 10px 5px 0;border:1px solid #ccc;background:#fff;padding:2px;} "
			+ "pre {font-size:9pt;line-height:12pt;font-family:Courier New,Arial;border:1px solid #ddd;border-left:5px solid #6CE26C;background:#f6f6f6;padding:5px;overflow: auto;} "
			+ "a.tag {font-size:15px;text-decoration:none;background-color:#cfc;color:#060;border-bottom:1px solid #B1D3EB;border-right:1px solid #B1D3EB;color:#3E6D8E;margin:2px 2px 2px 0;padding:2px 4px;white-space:nowrap;position:relative}</style>";

	public static final String WEB_LOAD_IMAGES = "<script type=\"text/javascript\"> var allImgUrls = getAllImgSrc(document.body.innerHTML);</script>";

	private static final String SHOWIMAGE = "ima-api:action=showImage&data=";

	@SuppressLint({ "JavascriptInterface", "SetJavaScriptEnabled" })
	public static void initWebView(WebView webView) {
		WebSettings settings = webView.getSettings();
		settings.setDefaultFontSize(15);
		settings.setJavaScriptEnabled(true);
		settings.setSupportZoom(true);
		settings.setBuiltInZoomControls(true);
		int sysVersion = Build.VERSION.SDK_INT;
		if (sysVersion >= 11) {
			settings.setDisplayZoomControls(false);
		} else {
			ZoomButtonsController zbc = new ZoomButtonsController(webView);
			zbc.getZoomControls().setVisibility(View.GONE);
		}
		webView.setWebViewClient(UIHelper.getWebViewClient());
	}

	/**
	 * url跳转
	 * 
	 * @param context
	 * @param url
	 */
	public static void showUrlRedirect(Context context, String url) {
		// if (url == null)
		// return;
		// if (url.contains("city.oschina.net/")) {
		// int id = StringUtils.toInt(url.substring(url.lastIndexOf('/') + 1));
		// UIHelper.showEventDetail(context, id);
		// return;
		// }
		//
		// if (url.startsWith(SHOWIMAGE)) {
		// String realUrl = url.substring(SHOWIMAGE.length());
		// try {
		// JSONObject json = new JSONObject(realUrl);
		// int idx = json.optInt("index");
		// String[] urls = json.getString("urls").split(",");
		// showImagePreview(context, idx, urls);
		// } catch (JSONException e) {
		// e.printStackTrace();
		// }
		// return;
		// }
		// URLsUtils urls = URLsUtils.parseURL(url);
		// if (urls != null) {
		// showLinkRedirect(context, urls.getObjType(), urls.getObjId(),
		// urls.getObjKey());
		// } else {
		// openBrowser(context, url);
		// }
	}

	/**
	 * 发送App异常崩溃报告
	 * 
	 * @param
	 * @param crashReport
	 */
	public static void sendAppCrashReport(final Context context,
			final String crashReport) {
//		CommonDialog dialog = new CommonDialog(context);
//
//		dialog.setTitle(R.string.app_error);
//		dialog.setMessage(R.string.app_error_message);
//		dialog.setPositiveButton(R.string.submit_report,
//				new DialogInterface.OnClickListener() {
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						dialog.dismiss();
//						// 发送异常报告
//						TDevice.sendEmail(context, "OSCAndroid客户端耍脾气 - 症状诊断报告",
//								crashReport, "apposchina@163.com");
//						// 退出
//						AppManager.getAppManager().AppExit(context);
//					}
//				});
//		dialog.setNegativeButton(R.string.cancle,
//				new DialogInterface.OnClickListener() {
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						dialog.dismiss();
//						// 退出
//						AppManager.getAppManager().AppExit(context);
//					}
//				});
//		dialog.show();
	}

	public static void sendAppCrashReport(final Context context) {
//		CommonDialog dialog = new CommonDialog(context);
//		dialog.setTitle(R.string.app_error);
//		dialog.setMessage(R.string.app_error_message);
//		dialog.setNegativeButton(R.string.ok,
//				new DialogInterface.OnClickListener() {
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						System.exit(-1);
//					}
//				});
//		dialog.show();
	}

	public static void startSearchActivity(Activity context, int userId,
			String token) {

//		Intent intent = new Intent(context, SearchActivity.class);
//		intent.putExtra(Constants.INTENT_EXTRA_USER_ID, userId);
//		intent.putExtra(Constants.INTENT_EXTRA_TOKEN, token);
//		context.startActivity(intent);
	}
	//测试用
	public static void startSearchActivity(Activity context) {

		Intent intent = new Intent(context, SearchActivity.class);
		context.startActivity(intent);
	}

	/**
	 * 打开用户信息界面
	 * 
	 * @param context
	 * @param myId
	 *            自己的id
	 * @param userId
	 *            要显示的用户的id
	 * @param token
	 *            个人的token
	 */
	public static void startUserInforActivity(Activity context, int myId,
			int userId, String token, String type) {
//		Intent intent = new Intent(context, UserInforActivity.class);
//		intent.putExtra(Constants.INTENT_EXTRA_USER_ID, userId);
//		intent.putExtra(Constants.INTENT_EXTRA_TOKEN, token);
//		intent.putExtra(Constants.INTENT_EXTRA_USER_INFOR_TYPE, type);
//		intent.putExtra(Constants.INTENT_EXTRA_MY_ID, myId);
//		context.startActivity(intent);
	}

	public static void startLonginActivity(Activity mContext) {
		Intent intent = new Intent(mContext, LoginActivity.class);
		mContext.startActivity(intent);
		mContext.finish();
	}

	public static void startChatActivity(Activity mContext, int chatType,
										 Friends friends, ChatGroup chatGroup,
										 DiscussionGroup discussionGroup) {
//		Intent intent = new Intent(mContext, ChatActivity.class);
//		intent.putExtra(Constants.INTENT_EXTRA_CHAT_TYPE, chatType);
//		switch (chatType) {
//		case ChatMessage.MSG_TYPE_UU:
//			intent.putExtra(Constants.INTENT_EXTRA_CHAT_FRIEND, friends);
//			break;
//		case ChatMessage.MSG_TYPE_UCG:
//			intent.putExtra(Constants.INTENT_EXTRA_CHAT_CHAT_GROUP, chatGroup);
//			break;
//		case ChatMessage.MSG_TYPE_UDG:
//			intent.putExtra(Constants.INTENT_EXTRA_CHAT_DISCUSSION_GROUP,
//					discussionGroup);
//			break;
//		default:
//			break;
//		}
//
//		mContext.startActivity(intent);
	}

	public static void showMainActivity(Context mContext, int startType) {
//		Intent intent = new Intent(mContext, MainActivity.class);
//		intent.putExtra(Constants.INTENT_EXTRA_MAIN_START_TYPE, startType);
//		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		mContext.startActivity(intent);
	}

	public static void startGroupControlActivity(MainActivity mContext) {
//		Intent intent = new Intent(mContext, GroupActivity.class);
//		mContext.startActivity(intent);
	}

//	public static void startGroupSelectorActivity(Context context,
//			Friends mFriends) {
//		Intent intent = new Intent(context, FriendsGroupSelectorActivity.class);
//		intent.putExtra(Constants.INTENT_EXTRA_FRIENDS, mFriends);
//		context.startActivity(intent);
//	}

	public static void startAddChatGroupActivity(Context context) {
//		Intent intent = new Intent(context, AddChatGroupActivity.class);
//		context.startActivity(intent);
	}

//	public static void startChatGroupInforActivity(Context context,
//			int groupId, String type) {
//		Intent intent = new Intent(context, GroupInforActivity.class);
//		intent.putExtra(Constants.INTENT_EXTRA_CHATGROUP_INFOR_TYPE, type);
//		intent.putExtra(Constants.INTENT_EXTRA_CHAT_GROUP_ID, groupId);
//		context.startActivity(intent);
//	}

//	public static void startQrCodeActivity(Context context) {
//		Intent intent = new Intent(context, QrCodeActivity.class);
//		context.startActivity(intent);
//	}

//	public static void startErCodeScanActivity(Context context) {
//		Intent intent = new Intent(context, ErcodeScanActivity.class);
//		context.startActivity(intent);
//	}
}
