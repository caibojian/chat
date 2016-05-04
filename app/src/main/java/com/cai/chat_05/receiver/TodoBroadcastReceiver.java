package com.cai.chat_05.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.cai.chat_05.MainActivity;
import com.cai.chat_05.R;
import com.cai.chat_05.bean.Constants;
import com.cai.chat_05.utils.UIHelper;
import com.cai.chat_05.view.HandyTextView;


public class TodoBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(final Context context, Intent intent) {

//		AsyncHttpResponseHandler fHandler = new AsyncHttpResponseHandler() {
//
//			@Override
//			public void onSuccess(int statusCode, Header[] headers,
//					byte[] responseBytes) {
//				String data = new String(responseBytes);
//				Result r = (Result) JSON.parseObject(data, Result.class);
//				if (r != null) {
//					showCustomToast(r.getMessage(), context);
//					if (r.isSuccess()) {
//						showCustomToast("成功处理！", context);
//					} else {
//						showCustomToast("处理发生异常！", context);
//					}
//				} else {
//					showCustomToast("处理发生异常！", context);
//				}
//			}
//
//			@Override
//			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
//					Throwable arg3) {
//				showCustomToast("处理发生异常！", context);
//
//			}
//
//		};

		Log.v("org.weishe.weichat", "接收到");
		String action = intent.getAction();
		if (action.equals(Constants.INTENT_ACTION_HANDING_TODO)) {
//			UIHelper.showMainActivity(context, MainActivity.START_TYPE_TODO);

			// int todoId = intent.getIntExtra(
			// Constants.INTENT_EXTRA_HANDING_TODO_ID, 0);
			// int userId = intent.getIntExtra(Constants.INTENT_EXTRA_USER_ID,
			// 0);
			// int type = intent.getIntExtra(
			// Constants.INTENT_ACTION_HANDING_TODO_TYPE, 0);
			// String token =
			// intent.getStringExtra(Constants.INTENT_EXTRA_TOKEN);
			// switch (type) {
			// case Constants.INTENT_EXTRA_HANDING_TODO_TYPE_AGREE:
			// Log.v("org.weishe.weichat", "同意");
			// WeisheApi.dealWithTodo(fHandler, userId, token, todoId, true);
			// break;
			// case Constants.INTENT_EXTRA_HANDING_TODO_TYPE_REFUSE:
			// Log.v("org.weishe.weichat", "不同意");
			// WeisheApi.dealWithTodo(fHandler, userId, token, todoId, false);
			// break;
			// default:
			// Log.v("org.weishe.weichat", "没有");
			// break;
			// }
		}

	}

	/** 显示自定义Toast提示(来自String) **/
	protected void showCustomToast(String text, Context context) {
		View toastRoot = LayoutInflater.from(context).inflate(
				R.layout.common_toast, null);
		((HandyTextView) toastRoot.findViewById(R.id.toast_text)).setText(text);
		Toast toast = new Toast(context);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setView(toastRoot);
		toast.show();
	}
}
