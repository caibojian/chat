package com.cai.chat_05;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.cai.chat_05.base.BaseActivity;
import com.cai.chat_05.bean.Attachment;
import com.cai.chat_05.bean.Constants;
import com.cai.chat_05.bean.Friends;
import com.cai.chat_05.bean.FriendsGroup;
import com.cai.chat_05.bean.User;
import com.cai.chat_05.cache.CacheManager;
import com.cai.chat_05.service.IoTService;
import com.cai.chat_05.utils.UIHelper;
import com.cai.chat_05.view.CircularImage;
import com.cai.chat_05.view.HandyTextView;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class LEDControlActivity extends BaseActivity {
	private Context mContext;
	private Switch ledSwitch;
	private BroadcastReceiver receiver;
	private IoTService ioTService;
	private IoTService.MsgBinder iBinder;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		Intent intent = new Intent(mContext, IoTService.class);
		bindService(intent, conn, Context.BIND_AUTO_CREATE);
		setContentView(R.layout.activity_led_control);
		initViews();
		receiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {

			}
		};

		IntentFilter intentFilter = new IntentFilter();
//		intentFilter.addAction(Constants.INTENT_ACTION_LOGIN);
		mContext.registerReceiver(receiver, intentFilter);
	}

	private CompoundButton.OnCheckedChangeListener checkedChange = new CompoundButton.OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			if(isChecked){
				Toast.makeText(getApplicationContext(), "true",
						Toast.LENGTH_SHORT).show();
			}else{
				Toast.makeText(getApplicationContext(), "false",
						Toast.LENGTH_SHORT).show();
			}
			iBinder.contralLED(isChecked);
		}
	};

	ServiceConnection conn = new ServiceConnection() {
		@Override
		public void onServiceDisconnected(ComponentName name) {
			iBinder = null;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			//返回一个MsgService对象
			ioTService = ((IoTService.MsgBinder)service).getService();
			iBinder = (IoTService.MsgBinder) service;

			//dialog.dismiss();

		}
	};

	@Override
	protected void onDestroy() {
		mContext.unregisterReceiver(receiver);
		unbindService(conn);
		super.onDestroy();
	}

	@Override
	protected void initViews() {
		ledSwitch = (Switch) findViewById(R.id.ledswitch);
		ledSwitch.setOnCheckedChangeListener(checkedChange);
	}

	@Override
	protected void initEvents() {

	}
//	protected AsyncHttpResponseHandler addFriendHandler = new AsyncHttpResponseHandler() {
//
//		@Override
//		public void onSuccess(int statusCode, Header[] headers,
//				byte[] responseBytes) {
//			String data = new String(responseBytes);
//			Result u = (Result) JSON.parseObject(data, Result.class);
//			if (u != null && u.isSuccess()) {
//				showCustomToast("添加好友请求已发出！");
//				// actionButton.setEnabled(false);
//				actionButton.setVisibility(View.GONE);
//			} else {
//				showCustomToast("添加好友发生异常！");
//			}
//		}
//
//		@Override
//		public void onFailure(int arg0, Header[] arg1, byte[] arg2,
//				Throwable arg3) {
//			showCustomToast("添加好友发生异常！");
//
//		}
//
//	};
//	protected AsyncHttpResponseHandler mHandler = new AsyncHttpResponseHandler() {
//
//		@Override
//		public void onSuccess(int statusCode, Header[] headers,
//				byte[] responseBytes) {
//			String data = new String(responseBytes);
//			User u = (User) JSON.parseObject(data, User.class);
//			if (u != null) {
//				user = u;
//				initView();
//			} else {
//				showCustomToast("获取用户信息发生异常！");
//			}
//		}
//
//		@Override
//		public void onFailure(int arg0, Header[] arg1, byte[] arg2,
//				Throwable arg3) {
//			showCustomToast("获取用户信息发生异常！");
//
//		}
//
//	};

	/** 显示自定义Toast提示(来自String) **/
	protected void showCustomToast(String text) {
		View toastRoot = LayoutInflater.from(this).inflate(
				R.layout.common_toast, null);
		((HandyTextView) toastRoot.findViewById(R.id.toast_text)).setText(text);
		Toast toast = new Toast(this);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setView(toastRoot);
		toast.show();
	}
}
