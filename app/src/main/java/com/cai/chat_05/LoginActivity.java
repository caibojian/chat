package com.cai.chat_05;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import com.cai.chat_05.bean.User;
import com.cai.chat_05.utils.SpUtil;

import java.io.UnsupportedEncodingException;
import java.security.KeyStore;
import java.util.UUID;

public class LoginActivity extends BaseActivity {
	static final String LOG_TAG = LoginActivity.class.getCanonicalName();

	private AppContext mAppContext;
	private Context mContext;
	private RelativeLayout rl_user;
	private Button mLoginButton;
	private Button mRegisterButton;
	private EditText mAccount;
	private EditText mPassword;;

	private SharedPreferences sp;

	// Endpoint Prefix = random characters at the beginning of the custom AWS
	// IoT endpoint
	// describe endpoint call returns: XXXXXXXXXX.iot.<region>.amazonaws.com,
	// endpoint prefix string is XXXXXXX
	private static final String CUSTOMER_SPECIFIC_ENDPOINT_PREFIX = "A1KJQ0JFEFY3TN";
	// Cognito pool ID. For this app, pool needs to be unauthenticated pool with
	// AWS IoT permissions.
	private static final String COGNITO_POOL_ID = "ap-northeast-1:96b4bf76-0482-419f-99dc-7e82d70490a1";
	// Name of the AWS IoT policy to attach to a newly created certificate
	private static final String AWS_IOT_POLICY_NAME = "cai_iot_policy_1";

	// Region of AWS IoT
	private static final Regions MY_REGION = Regions.AP_NORTHEAST_1;
	// Filename of KeyStore file on the filesystem
	private static final String KEYSTORE_NAME = "caibojian";
	// Password for the private key in the KeyStore
	private static final String KEYSTORE_PASSWORD = "caibojian.1991";
	// Certificate and key aliases in the KeyStore
	private static final String CERTIFICATE_ID = "cai";



	AWSIotClient mIotAndroidClient;
	AWSIotMqttManager mqttManager;
	String clientId;
	String keystorePath;
	String keystoreName;
	String keystorePassword;

	KeyStore clientKeyStore = null;
	String certificateId;

	CognitoCachingCredentialsProvider credentialsProvider;

	// private NetService mNetService = NetService.getInstance();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		mContext = this;
		mAppContext = AppContext.getInstance() ;

		initViews();
		initEvents();

		sp = SpUtil.getSharePerference(mContext);

		clientId = UUID.randomUUID().toString();

		// Initialize the AWS Cognito credentials provider
		credentialsProvider = new CognitoCachingCredentialsProvider(
				getApplicationContext(), // context
				COGNITO_POOL_ID, // Identity Pool ID
				MY_REGION // Region
		);

		Region region = Region.getRegion(MY_REGION);

		// MQTT Client
		mqttManager = new AWSIotMqttManager(clientId, region, CUSTOMER_SPECIFIC_ENDPOINT_PREFIX);

		// Set keepalive to 10 seconds.  Will recognize disconnects more quickly but will also send
		// MQTT pings every 10 seconds.
		mqttManager.setKeepAlive(10);

		// Set Last Will and Testament for MQTT.  On an unclean disconnect (loss of connection)
		// AWS IoT will publish this message to alert other clients.
		AWSIotMqttLastWillAndTestament lwt = new AWSIotMqttLastWillAndTestament("my/lwt/topic",
				"Android client lost connection", AWSIotMqttQos.QOS0);
		mqttManager.setMqttLastWillAndTestament(lwt);

		// IoT Client (for creation of certificate if needed)
		mIotAndroidClient = new AWSIotClient(credentialsProvider);
		mIotAndroidClient.setRegion(region);

		keystorePath = getFilesDir().getPath();
		keystoreName = KEYSTORE_NAME;
		keystorePassword = KEYSTORE_PASSWORD;
		certificateId = CERTIFICATE_ID;

		// To load cert/key from keystore on filesystem
		try {
			if (AWSIotKeystoreHelper.isKeystorePresent(keystorePath, keystoreName)) {
				if (AWSIotKeystoreHelper.keystoreContainsAlias(certificateId, keystorePath,
						keystoreName, keystorePassword)) {
					Log.i(LOG_TAG, "Certificate " + certificateId
							+ " found in keystore - using for MQTT.");
					// load keystore from file into memory to pass on connection
					clientKeyStore = AWSIotKeystoreHelper.getIotKeystore(certificateId,
							keystorePath, keystoreName, keystorePassword);
					mLoginButton.setEnabled(true);
				} else {
					Log.i(LOG_TAG, "Key/cert " + certificateId + " not found in keystore.");
				}
			} else {
				Log.i(LOG_TAG, "Keystore " + keystorePath + "/" + keystoreName + " not found.");
			}
		} catch (Exception e) {
			Log.e(LOG_TAG, "An error occurred retrieving cert/key from keystore.", e);
		}

		if (clientKeyStore == null) {
			Log.i(LOG_TAG, "Cert/key was not found in keystore - creating new key and certificate.");

			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						// Create a new private key and certificate. This call
						// creates both on the server and returns them to the
						// device.
						CreateKeysAndCertificateRequest createKeysAndCertificateRequest =
								new CreateKeysAndCertificateRequest();
						createKeysAndCertificateRequest.setSetAsActive(true);
						final CreateKeysAndCertificateResult createKeysAndCertificateResult;
						createKeysAndCertificateResult =
								mIotAndroidClient.createKeysAndCertificate(createKeysAndCertificateRequest);
						Log.i(LOG_TAG,
								"Cert ID: " +
										createKeysAndCertificateResult.getCertificateId() +
										" created.");

						// store in keystore for use in MQTT client
						// saved as alias "default" so a new certificate isn't
						// generated each run of this application
						AWSIotKeystoreHelper.saveCertificateAndPrivateKey(certificateId,
								createKeysAndCertificateResult.getCertificatePem(),
								createKeysAndCertificateResult.getKeyPair().getPrivateKey(),
								keystorePath, keystoreName, keystorePassword);

						// load keystore from file into memory to pass on
						// connection
						clientKeyStore = AWSIotKeystoreHelper.getIotKeystore(certificateId,
								keystorePath, keystoreName, keystorePassword);

						// Attach a policy to the newly created certificate.
						// This flow assumes the policy was already created in
						// AWS IoT and we are now just attaching it to the
						// certificate.
						AttachPrincipalPolicyRequest policyAttachRequest =
								new AttachPrincipalPolicyRequest();
						policyAttachRequest.setPolicyName(AWS_IOT_POLICY_NAME);
						policyAttachRequest.setPrincipal(createKeysAndCertificateResult
								.getCertificateArn());
						mIotAndroidClient.attachPrincipalPolicy(policyAttachRequest);

						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								mLoginButton.setEnabled(true);
							}
						});
					} catch (Exception e) {
						Log.e(LOG_TAG,
								"Exception occurred when generating new private key and certificate.",
								e);
					}
				}
			}).start();
		}

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
		User user = new User();
		user.setAccount(account);
		user.setPassword(password);
//		user.setUuid(clientId);
//		SpUtil.setStringSharedPerference(sp, "user.uuid", user.getUuid());
		SpUtil.setStringSharedPerference(sp, "user.password", user.getPassword());
		SpUtil.setStringSharedPerference(sp, "user.account", user.getAccount());

		try {
			mqttManager.connect(clientKeyStore, new AWSIotMqttClientStatusCallback() {
				@Override
				public void onStatusChanged(final AWSIotMqttClientStatus status,
											final Throwable throwable) {
					Log.d(LOG_TAG, "Status = " + String.valueOf(status));

					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							if (status == AWSIotMqttClientStatus.Connecting) {
								Toast.makeText(getApplicationContext(), "Connecting...",
										Toast.LENGTH_SHORT).show();

							} else if (status == AWSIotMqttClientStatus.Connected) {

								Toast.makeText(getApplicationContext(), "Connected",
										Toast.LENGTH_SHORT).show();
							} else if (status == AWSIotMqttClientStatus.Reconnecting) {
								if (throwable != null) {
									Log.e(LOG_TAG, "Connection error.", throwable);
									Toast.makeText(getApplicationContext(), "Connected",
											Toast.LENGTH_SHORT).show();
								}
								Toast.makeText(getApplicationContext(), "Reconnecting",
										Toast.LENGTH_SHORT).show();
							} else if (status == AWSIotMqttClientStatus.ConnectionLost) {
								if (throwable != null) {
									Log.e(LOG_TAG, "Connection error.", throwable);
								}
								Toast.makeText(getApplicationContext(), "Disconnected",
										Toast.LENGTH_SHORT).show();
							} else {
								Toast.makeText(getApplicationContext(), "Disconnected",
										Toast.LENGTH_SHORT).show();

							}
						}
					});
				}
			});
		} catch (final Exception e) {
			Log.e(LOG_TAG, "Connection error.", e);

			Toast.makeText(getApplicationContext(), "Error! " + e.getMessage(),
					Toast.LENGTH_SHORT).show();
		}

		final String topic = clientId;

		Log.d(LOG_TAG, "topic = " + topic);

		try {
			mqttManager.subscribeToTopic(topic, AWSIotMqttQos.QOS0,
					new AWSIotMqttNewMessageCallback() {
						@Override
						public void onMessageArrived(final String topic, final byte[] data) {
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									try {
										String message = new String(data, "UTF-8");
										Log.d(LOG_TAG, "Message arrived:");
										Log.d(LOG_TAG, "   Topic: " + topic);
										Log.d(LOG_TAG, " Message: " + message);

										Toast.makeText(getApplicationContext(), message,
												Toast.LENGTH_SHORT).show();
									} catch (UnsupportedEncodingException e) {
										Log.e(LOG_TAG, "Message encoding error.", e);
									}
								}
							});
						}
					});
		} catch (Exception e) {
			Log.e(LOG_TAG, "Subscription error.", e);
		}
		try {
			mqttManager.publishString("订阅成功", topic, AWSIotMqttQos.QOS0);
		} catch (Exception e) {
			Log.e(LOG_TAG, "Publish error.", e);
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

}
