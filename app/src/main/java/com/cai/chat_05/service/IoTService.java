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
import android.os.Bundle;
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
import com.cai.chat_05.aidl.SessionService;
import com.cai.chat_05.bean.Attachment;
import com.cai.chat_05.bean.Constants;
import com.cai.chat_05.bean.Friends;
import com.cai.chat_05.bean.FriendsGroup;
import com.cai.chat_05.bean.User;
import com.cai.chat_05.cache.CacheManager;
import com.cai.chat_05.core.bean.ChatMessage;
import com.cai.chat_05.core.bean.MyMessage;
import com.cai.chat_05.utils.BroadcastHelper;
import com.cai.chat_05.utils.DBHelper;
import com.cai.chat_05.utils.JsonUtil;
import com.cai.chat_05.utils.UUIDUtil;
import com.google.gson.reflect.TypeToken;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.channels.SocketChannel;
import java.security.KeyStore;
import java.util.Date;
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
    //当前iot服务状态
    private String iotStatus = "";

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
    private static final String AWS_IOT_POLICY_NAME = "android_iot";

    // Region of AWS IoT
    private static final Regions MY_REGION = Regions.AP_NORTHEAST_1;
    // Filename of KeyStore file on the filesystem
    private static final String KEYSTORE_NAME = "caibojian.bks";
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
        mqttManager.setReconnectRetryLimits(2, 10);

        // Set Last Will and Testament for MQTT.  On an unclean disconnect (loss of connection)
        // AWS IoT will publish this message to alert other clients.
        AWSIotMqttLastWillAndTestament lwt = new AWSIotMqttLastWillAndTestament("my/lwt/topic",
                "Android client lost connection", AWSIotMqttQos.QOS1);
        mqttManager.setMqttLastWillAndTestament(lwt);

        // IoT Client (for creation of certificate if needed)
        mIotAndroidClient = new AWSIotClient(credentialsProvider);
        mIotAndroidClient.setRegion(region);

        try {
            InputStream inputStream = getAssets().open("caibojian.bks");
            ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
            byte[] buff = new byte[100]; //buff用于存放循环读取的临时数据
            int rc = 0;
            while ((rc = inputStream.read(buff, 0, 100)) > 0) {
                swapStream.write(buff, 0, rc);
            }
            byte[] in_b = swapStream.toByteArray(); //in_b为转换之后的结果
            FileOutputStream outStream = this.openFileOutput("caibojian.bks", Context.MODE_WORLD_READABLE);
            outStream.write(in_b);
            outStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

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
        Log.i(LOG_TAG,"开始连接iot" );
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
                            iotStatus = String.valueOf(status);
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

        public void sendMessage(String uuid, int contentType, String message,
                                int toId, int msgType, String fileGroupName, String filePath, ChatMessage chatMessage)
                throws RemoteException {
            String msgJson = null;

            MyMessage myMessage = new MyMessage();
            myMessage.setUuid(uuid);
            myMessage.setDate(new Date());
            myMessage.setFromId(user.getId()+"");
            myMessage.setToId(toId+"");
            myMessage.setMsgType(Constants.MYMSG_TYPE_CHAT_UU);
            myMessage.setContent(JsonUtil.toJson(chatMessage));
            msgJson = JsonUtil.toJson(JsonUtil.toJson(myMessage));
            IoTPublishString(toId+"",AWSIotMqttQos.QOS1, msgJson);
            chatMessage.setStatus(Constants.STATUS_SEND);
            Log.d(LOG_TAG, " 发送消息: " + msgJson);
            DBHelper.getgetInstance(IoTService.this).addChatMessage(chatMessage,
                    user.getId());
            Intent intent3 = new Intent();
            intent3.setAction(Constants.INTENT_ACTION_RECEIVE_CHAT_MESSAGE);
            Bundle bundle3 = new Bundle();
            bundle3.putSerializable(Constants.INTENT_EXTRA_CHAT_MESSAGE,
                    chatMessage);
            IoTService.this.sendBroadcast(intent3);
//            BroadcastHelper.onSendChatMessage(IoTService.this);
            switch (msgType) {
                case Constants.MSG_TYPE_UU:
//                    msg = MsgHelper.newUUChatMessage(uuid, user.getId(), toId,
//                            message, token, true,
//                            StringUtils.getCurrentStringDate(), 0, contentType,
//                            fileGroupName, filePath, ChatMessage.STATUS_SEND);
                    break;
                case Constants.MSG_TYPE_UCG:
//                    msg = MsgHelper.newUCGChatMessage(uuid, user.getId(), toId,
//                            message, token, true,
//                            StringUtils.getCurrentStringDate(), 0, contentType,
//                            fileGroupName, filePath, ChatMessage.STATUS_SEND);
                    break;
                case Constants.MSG_TYPE_UDG:
//                    msg = MsgHelper.newUUChatMessage(uuid, user.getId(), toId,
//                            message, token, true,
//                            StringUtils.getCurrentStringDate(), 0, contentType,
//                            fileGroupName, filePath, ChatMessage.STATUS_SEND);
                    break;
            }
//            socketChannel.writeAndFlush(msg);
//
//            BroadcastHelper.onSendChatMessage(Session.this);
        }

        public void getFriendList() throws RemoteException {
            Log.i(LOG_TAG, "调用的iotservice的getFriendList（）");
            MyMessage msg = new MyMessage();
            msg.setMsgType(Constants.MYMSG_TYPE_GETFRIENDS_REQ);
            msg.setToId("system");
            msg.setFromId(user.getId()+"");
            msg.setDate(new Date());
            msg.setUuid(UUIDUtil.uuid());
            String msgJson = JsonUtil.toJson(msg);
            IoTPublishString(Constants.IOT_TOPOIC_GETFRIENDS,AWSIotMqttQos.QOS1, msgJson);

        }

        public void getMessageList(int fromMessageId) throws RemoteException {
//            Message msg1 = MsgHelper.newClientRequestMessage(
//                    ClientRequestMessage.CHAT_MESSAGE_LIST, user.getId(),
//                    token, fromMessageId + "");
//
//            socketChannel.writeAndFlush(msg1);
        }

        public int getUserId() throws RemoteException {

            return user.getId();
        }

        public String getUserName() throws RemoteException {
            return user.getName();
        }

        public void getFriendGroupsList() throws RemoteException {
            MyMessage msg = new MyMessage();
            msg.setMsgType(Constants.MYMSG_TYPE_GETFRIENDSGROUP_REQ);
            msg.setToId("system");
            msg.setFromId(user.getId()+"");
            msg.setDate(new Date());
            msg.setUuid(UUIDUtil.uuid());
            String msgJson = JsonUtil.toJson(msg);
            IoTPublishString(Constants.IOT_TOPOIC_GETFRIENDSGROUP,AWSIotMqttQos.QOS1, msgJson);
        }

        public String getToken() throws RemoteException {
            return token;
        }

        public void sendAttachment(long id) throws RemoteException {
//            Attachment a = DBHelper.getgetInstance(Session.this).getAttachment(
//                    id);
//            if (a == null) {
//                return;
//            }
//            Message msg = MsgHelper.newFileUpload(a, user.getId(), token);
//            socketChannel.writeAndFlush(msg);
        }

        public void getTodoList(int fromMessageId) throws RemoteException {
//            Message msg1 = MsgHelper.newClientRequestMessage(
//                    ClientRequestMessage.TODO_LIST, user.getId(), token,
//                    fromMessageId + "");
//            socketChannel.writeAndFlush(msg1);
        }

        public void getChatGroupList() throws RemoteException {
//            Message msg = MsgHelper.newClientRequestMessage(
//                    ClientRequestMessage.CHAT_GROUP_LIST, user.getId(), token,
//                    "");
//            socketChannel.writeAndFlush(msg);
        }

        public void getDiscussionGroupList() throws RemoteException {
//            Message msg = MsgHelper.newClientRequestMessage(
//                    ClientRequestMessage.DISCUSSION_GROUP_LIST, user.getId(),
//                    token, "");
//            socketChannel.writeAndFlush(msg);
        }

        public void getChatGroupMemberList(int groupId) throws RemoteException {
//            Message msg = MsgHelper.newClientRequestMessage(
//                    ClientRequestMessage.CHAT_GROUP_MEMBER_LIST, user.getId(),
//                    token, "" + groupId);
//            socketChannel.writeAndFlush(msg);

        }

        public void getDiscussionGroupMemberList(int dGroupId)
                throws RemoteException {
//            Message msg = MsgHelper.newClientRequestMessage(
//                    ClientRequestMessage.DISCUSSION_GROUP_MEMBER_LIST,
//                    user.getId(), token, "" + dGroupId);
//            socketChannel.writeAndFlush(msg);

        }

        public void getRelateUser() throws RemoteException {
//            Message msg = MsgHelper.newClientRequestMessage(
//                    ClientRequestMessage.RELATE_USER_LIST, user.getId(), token,
//                    "");
//            socketChannel.writeAndFlush(msg);
        }

        public String getIotStatus() {
            return iotStatus;
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
//                                        message = message.replaceAll("\\\\","");
                                        Log.d(LOG_TAG, "Message arrived:");
                                        Log.d(LOG_TAG, "   Topic: " + topic);
                                        Log.d(LOG_TAG, " Message: " + message);
                                        try{
                                            MyMessage msg = new MyMessage();
                                            try{
                                                msg = JsonUtil.fromJson(message, MyMessage.class);
                                            }catch (Exception e){
                                                String changeMsg = JsonUtil.changJson(message);
                                                Log.d(LOG_TAG, " 处理后的json: " + changeMsg);
                                                msg = JsonUtil.fromJson(changeMsg, MyMessage.class);
                                            }
                                            switch (msg.getMsgType()){
                                                case Constants.MYMSG_TYPE_LOGIN_RESP:
                                                    User user = JsonUtil.fromJson(msg.getContent(), User.class);
                                                    setUser(user);
                                                    Log.d(LOG_TAG, " iot服务接收到的user: " + user.toString());
                                                    IoTSubscribeToTopic(user.getId()+"", AWSIotMqttQos.QOS1);
                                                    CacheManager.saveObject(IoTService.this, user,
                                                    Constants.CACHE_CURRENT_USER);
                                                    Intent intent0 = new Intent();
                                                    intent0.setAction(Constants.INTENT_ACTION_LOGIN);
                                                    Bundle bundle = new Bundle();
                                                    bundle.putSerializable(Constants.CACHE_CURRENT_USER,
                                                            user);
                                                    intent0.putExtras(bundle);
                                                    IoTService.this.sendBroadcast(intent0);
                                                    break;
                                                case Constants.MYMSG_TYPE_GETFRIENDS_RESP:
                                                    List<Friends> friends = JsonUtil.fromJson(msg.getContent(),new TypeToken<List<Friends>>() {
                                                    }.getType());
                                                    CacheManager.saveObject(IoTService.this, friends,
                                                            Friends.getCacheKey(getUser().getId()));
                                                    Intent intent1 = new Intent();
                                                    intent1.setAction(Constants.INTENT_ACTION_RECEIVE_FRIEND_LIST);
                                                    IoTService.this.sendBroadcast(intent1);
                                                    break;
                                                case Constants.MYMSG_TYPE_GETFRIENDSGROUP_RESP:
                                                    List<FriendsGroup> friendsGroups = JsonUtil.fromJson(msg.getContent(),new TypeToken<List<FriendsGroup>>() {
                                                    }.getType());
                                                    CacheManager.saveObject(IoTService.this, friendsGroups,
                                                            FriendsGroup.getCacheKey(getUser().getId()));
                                                    Intent intent2 = new Intent();
                                                    intent2.setAction(Constants.INTENT_ACTION_RECEIVE_FRIEND_GROUP_LIST);
                                                    IoTService.this.sendBroadcast(intent2);
                                                    break;
                                                case Constants.MYMSG_TYPE_CHAT_UU:
                                                    Log.d(LOG_TAG, " iot服务接收到的ChatMessage: " + msg.getContent());
                                                    ChatMessage chatMessage = new ChatMessage();
                                                    chatMessage = JsonUtil.fromJson(msg.getContent(), ChatMessage.class);
                                                    Log.d(LOG_TAG, " iot服务接收到的ChatMessage: " + chatMessage.getContent());
                                                    Log.d(LOG_TAG, " iot服务接收到的ChatMessage--getFromId: " + chatMessage.getFromId());
                                                    // 保存数据
                                                    chatMessage.setDate(new Date());
                                                    chatMessage.setType(Constants.TYPE_RECEIVE);
                                                    chatMessage.setWhoId(chatMessage.getToId());
                                                    chatMessage.setChecked(false);
                                                    long maxID = DBHelper.getgetInstance(IoTService.this).getMaxMessageId();
                                                    Log.d(LOG_TAG, " 数据库消息最大ID: " + maxID);
                                                    chatMessage.setId(maxID+1);
                                                    chatMessage.setChatMessageId(DBHelper.getgetInstance(IoTService.this).getMaxMessageIdByUserId(IoTService.this.getUser().getId())+1);
                                                    DBHelper.getgetInstance(IoTService.this).addChatMessage(chatMessage,
                                                            IoTService.this.getUser().getId());
                                                    Intent intent3 = new Intent();

                                                    intent3.setAction(Constants.INTENT_ACTION_RECEIVE_CHAT_MESSAGE);
                                                    Bundle bundle3 = new Bundle();
                                                    bundle3.putSerializable(Constants.INTENT_EXTRA_CHAT_MESSAGE,
                                                            chatMessage);
                                                    IoTService.this.sendBroadcast(intent3);
                                                default:
                                                    Log.d(LOG_TAG, " iot服务接收到未知类型的消息: " + msg);
                                                    break;
                                            }
//                                            ChatMessage msg = JsonUtil.fromJson(message, ChatMessage.class);
//                                            DBHelper.getgetInstance(IoTService.this).addChatMessage(msg, msg.getWhoId());
//                                            Intent intent0 = new Intent();
//                                            intent0.setAction(Constants.INTENT_ACTION_RECEIVE_CHAT_MESSAGE);
//                                            Bundle bundle = new Bundle();
//                                            bundle.putSerializable(Constants.INTENT_EXTRA_CHAT_MESSAGE,
//                                                    msg);
//                                            intent0.putExtras(bundle);
//                                            IoTService.this.sendBroadcast(intent0);
                                        }catch (Exception e){
                                            Log.e(LOG_TAG, "Message encoding error.", e);
                                        }

                                    } catch (UnsupportedEncodingException e) {
                                        Log.e(LOG_TAG, "Message encoding error.", e);
                                    }
                                }
                            }).start();
                        }
                    });
        } catch (Exception e) {
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

    public String getClientId(){
        return clientId;
    }

    public void setUser(User user){
        this.user = user;
    }

    public User getUser(){
        return this.user;
    }
}
