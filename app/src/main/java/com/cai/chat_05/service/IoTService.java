package com.cai.chat_05.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;
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
import com.cai.chat_05.bean.Constants;
import com.cai.chat_05.bean.Friends;
import com.cai.chat_05.bean.User;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;

import java.io.UnsupportedEncodingException;
import java.nio.channels.SocketChannel;
import java.security.KeyStore;
import java.util.List;
import java.util.UUID;
import java.util.logging.FileHandler;

/**
 * Created by CAI on 2016/4/29.
 */
public class IoTService extends Service implements AWSIotMqttNewMessageCallback{
    static final String LOG_TAG = IoTService.class.getCanonicalName();

    public static final String CONNECTIVITY_CHANGE_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";
    private String serverIp;
    private int serverPort;
    private User user;
    private String token;
    private SocketChannel socketChannel;
    private List<Friends> friends;
    private boolean onInternet;
    private FileHandler fileHandler;

    private boolean firStart = true;
    public boolean reStarting = false;

    //iot service
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

    private static final String 	ACTION_START 	= LOG_TAG + ".START"; // Action to start
    private static final String 	ACTION_STOP		= LOG_TAG + ".STOP"; // Action to stop
    private static final String 	ACTION_KEEPALIVE= LOG_TAG + ".KEEPALIVE"; // Action to keep alive used by alarm manager
    private static final String 	ACTION_RECONNECT= LOG_TAG + ".RECONNECT"; // Action to reconnect
    private static final String 	DEVICE_ID_FORMAT = "andr_%s"; // Device ID Format, add any prefix you'd like
    private static final String		MQTT_THREAD_NAME = "MqttService[" + LOG_TAG + "]"; // Handler Thread ID
    private Handler mConnHandler;	  // Seperate Handler thread for networking
    private boolean mStarted = false; // Is the Client started?
    private AlarmManager mAlarmManager;			// Alarm manager to perform repeating tasks

    AWSIotClient mIotAndroidClient;
    public AWSIotMqttManager mqttManager;
    public String clientId;
    String keystorePath;
    String keystoreName;
    String keystorePassword;

    KeyStore clientKeyStore = null;
    String certificateId;

    CognitoCachingCredentialsProvider credentialsProvider;
    private ConnectivityManager mConnectivityManager; // To check for connectivity changes




    private Handler mHanlder = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            Log.v(LOG_TAG, "Handler"+msg.toString());
        }

    };

    /**
     * Receiver that listens for connectivity chanes
     * via ConnectivityManager
     */
    private final BroadcastReceiver mConnectivityReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(LOG_TAG,"Connectivity Changed...");
        }
    };

    // 消息提示音
//    private BeepManager beepManager;

    @Override
    public void onCreate() {
        super.onCreate();
        mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        mConnectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        clientId = String.format(DEVICE_ID_FORMAT,
                Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));

        HandlerThread thread = new HandlerThread(MQTT_THREAD_NAME);
        thread.start();

        mConnHandler = new Handler(thread.getLooper());

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
                "Android client lost connection", AWSIotMqttQos.QOS1);
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


            } catch (Exception e) {
                Log.e(LOG_TAG,
                        "Exception occurred when generating new private key and certificate.",
                        e);
            }
        }
    }

    /**
     * Service onStartCommand
     * Handles the action passed via the Intent
     *
     * @return START_REDELIVER_INTENT
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.i(LOG_TAG,"链接iot" );
        connect();
        IoTSubscribeToTopic("system", AWSIotMqttQos.QOS1);
        return START_REDELIVER_INTENT;
    }


    /**
     * Query's the AlarmManager to check if there is
     * a keep alive currently scheduled
     * @return true if there is currently one scheduled false otherwise
     */
    private synchronized boolean hasScheduledKeepAlives() {
        Intent i = new Intent();
        i.setClass(this, IoTService.class);
        i.setAction(ACTION_KEEPALIVE);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, PendingIntent.FLAG_NO_CREATE);

        return (pi != null) ? true : false;
    }

    /**
     * Cancels the Pending Intent
     * in the alarm manager
     */
    private void stopKeepAlives() {
        Intent i = new Intent();
        i.setClass(this, IoTService.class);
        i.setAction(ACTION_KEEPALIVE);
        PendingIntent pi = PendingIntent.getService(this, 0, i , 0);
        mAlarmManager.cancel(pi);
    }

    /**
     * Connects to the broker with the appropriate datastore
     */
    private synchronized void connect(){
        mConnHandler.post(new Runnable() {
            public void run() {
                try {
                    mqttManager.connect(clientKeyStore, new AWSIotMqttClientStatusCallback() {
                        @Override
                        public void onStatusChanged(final AWSIotMqttClientStatus status,
                                                    final Throwable throwable) {
                            Log.d(LOG_TAG, "Status1 = " + String.valueOf(status));
                            if (status == AWSIotMqttClientStatus.Connecting) {
                                Log.d(LOG_TAG, "Status2 = " + String.valueOf(status));
                            } else if (status == AWSIotMqttClientStatus.Connected) {
                                Log.d(LOG_TAG, "Status3 = " + String.valueOf(status));
                            } else if (status == AWSIotMqttClientStatus.Reconnecting) {
                                if (throwable != null) {
                                    Log.e(LOG_TAG, "Connection error.", throwable);
                                }
                                Log.d(LOG_TAG, "Status4 = " + String.valueOf(status));
                            } else if (status == AWSIotMqttClientStatus.ConnectionLost) {
                                if (throwable != null) {
                                    Log.e(LOG_TAG, "Connection error.", throwable);
                                }
                                Log.d(LOG_TAG, "Status5 = " + String.valueOf(status));
                            } else {
                                Log.d(LOG_TAG, "Status6 = Disconnected");

                            }
                        }
                    });
                } catch (final Exception e) {
                    Log.e(LOG_TAG, "Connection error.", e);
                }
            }
        });
    }

    /**
     * Attempts to stop the Mqtt client
     * as well as halting all keep alive messages queued
     * in the alarm manager
     */
    private synchronized void stop() {
        if(!mStarted) {
            Log.i(LOG_TAG,"Attemtpign to stop connection that isn't running");
            return;
        }

        if(mqttManager != null) {
            mConnHandler.post(new Runnable() {
                @Override
                public void run() {
                    mqttManager.disconnect();
                    mqttManager = null;
                    mStarted = false;

                    stopKeepAlives();
                }
            });
        }

        unregisterReceiver(mConnectivityReceiver);
    }


    /**
     * Verifies the client State with our local connected state
     * @return true if its a match we are connected false if we aren't connected
     */
    private boolean isConnected() {
        if(mStarted && mqttManager != null && !mqttManager.isAutoReconnect()) {
            Log.i(LOG_TAG,"Mismatch between what we think is connected and what is connected");
        }

        if(mqttManager != null) {
            return (mStarted && mqttManager.isAutoReconnect()) ? true : false;
        }

        return false;
    }

    /**
     * Query's the NetworkInfo via ConnectivityManager
     * to return the current connected state
     * @return boolean true if we are connected false otherwise
     */
    private boolean isNetworkAvailable() {
        NetworkInfo info = mConnectivityManager.getActiveNetworkInfo();

        return (info == null) ? false : info.isConnected();
    }
    /**
     * Checkes the current connectivity
     * and reconnects if it is required.
     */
    private synchronized void reconnectIfNecessary() {
        if(mStarted && mqttManager == null) {
            connect();
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        // 注销监听
        unregisterReceiver(mConnectivityReceiver);
    }

    /**
     * 返回一个Binder对象
     */
    @Override
    public IBinder onBind(Intent intent) {
        return new MsgBinder();
    }

    @Override
    public void onMessageArrived(String topic, byte[] data) {

    }

    public class MsgBinder extends Binder {
    /**
     * 获取当前Service的实例
     * @return
     */
    public IoTService getService(){
            return IoTService.this;
        }
    }

    /**
     * iot订阅消息
     * @param topic
     * @param qos
     */
    public void IoTSubscribeToTopic(String topic, AWSIotMqttQos qos){
        Log.v(LOG_TAG, "IoTSubscribeToTopic订阅");
        try {
            mqttManager.subscribeToTopic(topic, qos,
                    new AWSIotMqttNewMessageCallback() {
                        @Override
                        public void onMessageArrived(final String topic, final byte[] data) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        String message = new String(data, "UTF-8");
                                        Log.d(LOG_TAG, "Message arrived:");
                                        Log.d(LOG_TAG, "   Topic: " + topic);
                                        Log.d(LOG_TAG, " Message: " + message);
                                        Intent intent = new Intent();
                                        intent.setAction(Constants.INTENT_ACTION_RECEIVE_CHAT_MESSAGE_LIST);
                                        intent.putExtra("message", message);
                                        IoTService.this.sendBroadcast(intent);
                                    } catch (UnsupportedEncodingException e) {
                                        Log.e(LOG_TAG, "Message encoding error.", e);
                                    }
                                }
                            }).start();
                        }
                    });
        } catch (Exception e) {
            Log.e(LOG_TAG, "Subscription error.", e);
        }
    }

    /**
     * iot发布消息
     * @param topic
     * @param qos
     * @param msg
     */
    public void IoTPublishString(String topic, AWSIotMqttQos qos, String msg){
        try {
            mqttManager.publishString(msg, topic, qos);
        } catch (Exception e) {
            Log.e(LOG_TAG, "Publish error.", e);
        }
    }

    public String getcClientId(){
        return clientId;
    }
}
