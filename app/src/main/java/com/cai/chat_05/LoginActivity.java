package com.cai.chat_05;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.iot.AWSIotKeystoreHelper;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttClientStatusCallback;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttLastWillAndTestament;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttManager;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttNewMessageCallback;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttQos;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.iot.AWSIotClient;
import com.amazonaws.services.iot.model.AttachPrincipalPolicyRequest;
import com.amazonaws.services.iot.model.CreateKeysAndCertificateRequest;
import com.amazonaws.services.iot.model.CreateKeysAndCertificateResult;
import com.cai.chat_05.base.BaseActivity;
import com.cai.chat_05.bean.Constants;
import com.cai.chat_05.bean.User;
import com.cai.chat_05.cache.CacheManager;
import com.cai.chat_05.service.IoTService;
import com.cai.chat_05.utils.SpUtil;
import com.cai.chat_05.utils.UIHelper;

import java.io.UnsupportedEncodingException;
import java.security.KeyStore;
import java.util.UUID;

public class LoginActivity extends BaseActivity {
	static final String LOG_TAG = LoginActivity.class.getCanonicalName();

	private IoTService ioTService;
	private AppContext mAppContext;
	private Context mContext;
	private RelativeLayout rl_user;
	private Button mLoginButton;
	private Button mRegisterButton;
	private EditText mAccount;
	private EditText mPassword;
	private String clientId;

	private SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mContext = this;
		mAppContext = AppContext.getInstance() ;

		Intent intent = new Intent(mContext, IoTService.class);
		bindService(intent, conn, Context.BIND_AUTO_CREATE);
		setContentView(R.layout.activity_login);

		initViews();
		initEvents();

		sp = SpUtil.getSharePerference(mContext);

		clientId = UUID.randomUUID().toString();

	}


	protected void initViews() {
		// TODO Auto-generated method stub
		rl_user = (RelativeLayout) findViewById(R.id.rl_user);
		mLoginButton = (Button) findViewById(R.id.login);
		mRegisterButton = (Button) findViewById(R.id.register);
		mAccount = (EditText) findViewById(R.id.account);
		mPassword = (EditText) findViewById(R.id.password);

	}


	protected void initEvents() {
		// TODO Auto-generated method stub
		Animation anim = AnimationUtils.loadAnimation(mContext,
				R.anim.login_anim);
		anim.setFillAfter(true);
		rl_user.startAnimation(anim);

		mLoginButton.setOnClickListener(loginOnClickListener);
		mRegisterButton.setOnClickListener(registerOnClickListener);

	}

	private OnClickListener loginOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			String account = mAccount.getText().toString().trim();
			String password = mPassword.getText().toString().trim();

			if (account.equals("")) {

				Toast.makeText(getApplicationContext(), "请填写账号",
						Toast.LENGTH_SHORT).show();
//				showCustomToast("请填写账号");
				mAccount.requestFocus();
			} else if (password.equals("")) {
				Toast.makeText(getApplicationContext(), "请填写密码",
						Toast.LENGTH_SHORT).show();
//				showCustomToast("请填写密码");
			} else if (mPassword.length() < 6) {
				Toast.makeText(getApplicationContext(), "密码格式错误",
						Toast.LENGTH_SHORT).show();
//				showCustomToast("密码格式错误");
			} else {
				tryLogin(account, password);
			}
		}
	};

	private void tryLogin(final String account, final String password) {
		String localuuid = sp.getString("user.uuid","");
		String localpassword = sp.getString("user.password","");
		String localaccount = sp.getString("user.account","");
		if (localuuid == ""||localuuid == null){
			clientId = UUID.randomUUID().toString();
			User user = new User();
			user.setAccount(account);
			user.setPassword(password);
			user.setId(1);
//			user.setUuid(clientId);
			CacheManager.saveObject(mContext, user,
					Constants.CACHE_CURRENT_USER);
			SpUtil.setStringSharedPerference(sp, "user.uuid", clientId);
			SpUtil.setStringSharedPerference(sp, "user.password", user.getPassword());
			SpUtil.setStringSharedPerference(sp, "user.account", user.getAccount());
			ioTService.setUser(user);
			ioTService.IoTSubscribeToTopic(clientId, AWSIotMqttQos.QOS1);
			ioTService.IoTSubscribeToTopic("system", AWSIotMqttQos.QOS1);
			ioTService.IoTPublishString("system",AWSIotMqttQos.QOS1, "我登陆了："+clientId);
			Log.d(LOG_TAG, " clientId: " + clientId);
			Intent intent = new Intent(mContext, MainActivity.class);
			mContext.startActivity(intent);
		}else{
			User user = new User();
			user.setId(1);
			user.setAccount(account);
			user.setPassword(password);
//			user.setUuid(localuuid);
			clientId = localuuid;
			CacheManager.saveObject(mContext, user,
					Constants.CACHE_CURRENT_USER);
			SpUtil.setStringSharedPerference(sp, "user.uuid", user.getUuid());
			SpUtil.setStringSharedPerference(sp, "user.password", user.getPassword());
			SpUtil.setStringSharedPerference(sp, "user.account", user.getAccount());
			ioTService.IoTSubscribeToTopic(localuuid, AWSIotMqttQos.QOS1);
			ioTService.IoTSubscribeToTopic("system", AWSIotMqttQos.QOS1);
			ioTService.IoTPublishString("system",AWSIotMqttQos.QOS1, "我登陆了："+localuuid);
			CacheManager.saveObject(LoginActivity.this, user,
					Constants.CACHE_CURRENT_USER);
			Log.d(LOG_TAG, " clientId: " + clientId);
			Intent intent = new Intent(mContext, MainActivity.class);
			mContext.startActivity(intent);
		}



//		WeisheApi.login(mHandler, user, mAppContext.getAppId(),
//				ApiClientHelper.getUserAgent(mAppContext));
	}

	private OnClickListener registerOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
//			Intent intent = new Intent(mContext, RegisterActivity.class);
//			startActivity(intent);
		}
	};
//	protected AsyncHttpResponseHandler mHandler = new AsyncHttpResponseHandler() {
//
//		@Override
//		public void onSuccess(int statusCode, Header[] headers,
//				byte[] responseBytes) {
//			String data = new String(responseBytes);
//			Result r = (Result) JSON.parseObject(data, Result.class);
//			if (r != null) {
//				showCustomToast(r.getMessage());
//				if (r.isSuccess()) {
//
//					Intent intent = new Intent();
//					// Constants.INTENT_SERVICE_SESSION
//					intent.putExtra(Constants.INTENT_EXTRA_SERVER_IP,
//							"10.1.11.33");
//					intent.putExtra(Constants.INTENT_EXTRA_SERVER_PORT, 8888);
//					intent.putExtra(Constants.INTENT_EXTRA_TOKEN,
//							r.getMessage());
//					User user = (User) JSON.parseObject(r.getObj().toString(),
//							User.class);
//
//					intent.putExtra(Constants.INTENT_EXTRA_USER, user);
//					intent.setAction(Constants.INTENT_SERVICE_SESSION);
//					intent.setPackage("org.weishe.weichat");
//					startService(intent);
//
//					CacheManager.saveObject(LoginActivity.this, user,
//							Constants.CACHE_CURRENT_USER);
//					CacheManager.saveObject(LoginActivity.this, r.getMessage(),
//							Constants.CACHE_CURRENT_USER_TOKEN);
//					CacheManager.saveObject(LoginActivity.this,
//							mAppContext.getAppId(),
//							Constants.CACHE_CURRENT_CLIENT_ID);
//
//					Intent intent2 = new Intent(mContext, MainActivity.class);
//					startActivity(intent2);
//					finish();
//				}
//			} else {
//				showCustomToast("登录发生异常！");
//			}
//		}
//
//		@Override
//		public void onFailure(int arg0, Header[] arg1, byte[] arg2,
//				Throwable arg3) {
//			showCustomToast("登录发生异常！");
//
//		}
//
//	};

	ServiceConnection conn = new ServiceConnection() {
		@Override
		public void onServiceDisconnected(ComponentName name) {

		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			//返回一个MsgService对象
			ioTService = ((IoTService.MsgBinder)service).getService();

		}
	};

	@Override
	protected void onDestroy() {
		unbindService(conn);
		super.onDestroy();
	}

}
