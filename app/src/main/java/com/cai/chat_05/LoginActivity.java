package com.cai.chat_05;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

public class LoginActivity extends Activity {

//	private AppContext mAppContext;
	private Context mContext;
	private RelativeLayout rl_user;
	private Button mLoginButton;
	private Button mRegisterButton;
	private EditText mAccount;
	private EditText mPassword;;

	// private NetService mNetService = NetService.getInstance();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		mContext = this;
//		mAppContext = (AppContext) this.getApplication();
		initViews();
		initEvents();
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

			//直接跳转到主页
			Intent intent = new Intent();
			intent.setClass(mContext, MainActivity.class);
			startActivity(intent);
//			if (account.equals("")) {
//				showCustomToast("请填写账号");
//				mAccount.requestFocus();
//			} else if (password.equals("")) {
//				showCustomToast("请填写密码");
//			} else if (!VerifyUtils.matchAccount(account)) {
//				showCustomToast("账号格式错误");
//				mAccount.requestFocus();
//			} else if (mPassword.length() < 6) {
//				showCustomToast("密码格式错误");
//			} else {
//				tryLogin(account, password);
//			}
		}
	};

	private void tryLogin(final String account, final String password) {
//		User user = new User();
//		user.setAccount(account);
//		user.setPassword(password);
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

}
