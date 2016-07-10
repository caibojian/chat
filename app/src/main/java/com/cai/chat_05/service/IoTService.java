package com.cai.chat_05.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.Settings;
import android.util.Log;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttNewMessageCallback;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttQos;
import com.cai.chat_05.bean.Constants;
import com.cai.chat_05.bean.Friends;
import com.cai.chat_05.bean.FriendsGroup;
import com.cai.chat_05.bean.User;
import com.cai.chat_05.cache.CacheManager;
import com.cai.chat_05.core.bean.ChatMessage;
import com.cai.chat_05.core.bean.MyMessage;
import com.cai.chat_05.utils.DBHelper;
import com.cai.chat_05.utils.JsonUtil;
import com.cai.chat_05.utils.UUIDUtil;
import com.google.gson.reflect.TypeToken;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;

/**
 * Created by CAI on 2016/4/29.
 */
public class IoTService extends IoTBaseService{

    public static final String CONNECTIVITY_CHANGE_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";
    private User user;
    private String token;
    private List<Friends> friends;
    private NotificationHelper notificationHelper; //通知提示

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

    @Override
    public void onCreate() {
        super.onCreate();
        user = (User) CacheManager.readObject(this,
                Constants.CACHE_CURRENT_USER);
        mConnectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        clientId = String.format(DEVICE_ID_FORMAT,
                Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));

        HandlerThread thread = new HandlerThread(MQTT_THREAD_NAME);
        thread.start();
        mConnHandler = new Handler(thread.getLooper());

        //初始化iot
        IOTinit();
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
        notificationHelper = new NotificationHelper(IoTService.this);
        Log.i(LOG_TAG,"开始连接iot" );
        connect();
        IoTSubscribeToTopic("system", AWSIotMqttQos.QOS1);
        return START_REDELIVER_INTENT;
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

        public void contralLED(boolean flig){
            Log.i(LOG_TAG, "调用的iotservice的contralLED（）");
            if(flig){
                MyMessage msg = new MyMessage();
                msg.setMsgType(Constants.MYMSG_TYPE_LEDCONTROL_REQ);
                msg.setToId("system");
                msg.setFromId(user.getId()+"");
                msg.setDate(new Date());
                msg.setUuid(UUIDUtil.uuid());
                msg.setContent("0");
                String msgJson = JsonUtil.toJson(msg);
                IoTPublishString(Constants.IOT_TOPOIC_PICONTROL,AWSIotMqttQos.QOS1, msgJson);
            }else{
                MyMessage msg = new MyMessage();
                msg.setMsgType(Constants.MYMSG_TYPE_LEDCONTROL_REQ);
                msg.setToId("system");
                msg.setFromId(user.getId()+"");
                msg.setDate(new Date());
                msg.setUuid(UUIDUtil.uuid());
                msg.setContent("1");
                String msgJson = JsonUtil.toJson(msg);
                IoTPublishString(Constants.IOT_TOPOIC_PICONTROL,AWSIotMqttQos.QOS1, msgJson);
            }
        }

        public void contralInfrared(boolean flig){
            Log.i(LOG_TAG, "调用的iotservice的contralLED（）");
            if(flig){
                MyMessage msg = new MyMessage();
                msg.setMsgType(Constants.MYMSG_TYPE_CONTROLINFRARED_REQ);
                msg.setToId("system");
                msg.setFromId(user.getId()+"");
                msg.setDate(new Date());
                msg.setUuid(UUIDUtil.uuid());
                msg.setContent("0");
                String msgJson = JsonUtil.toJson(msg);
                IoTPublishString(Constants.IOT_TOPOIC_PICONTROL,AWSIotMqttQos.QOS1, msgJson);
            }else{
                MyMessage msg = new MyMessage();
                msg.setMsgType(Constants.MYMSG_TYPE_CONTROLINFRARED_REQ);
                msg.setToId("system");
                msg.setFromId(user.getId()+"");
                msg.setDate(new Date());
                msg.setUuid(UUIDUtil.uuid());
                msg.setContent("1");
                String msgJson = JsonUtil.toJson(msg);
                IoTPublishString(Constants.IOT_TOPOIC_PICONTROL,AWSIotMqttQos.QOS1, msgJson);
            }
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
                                                    List<Friends> friendlist = (List<Friends>) CacheManager.readObject(IoTService.this,
                                                            Friends.getCacheKey(IoTService.this.getUser().getId()));
                                                    Friends friend = null;
                                                    for (Friends friends1 : friendlist){
                                                        if(chatMessage.getFromId() == friends1.getId()){
                                                            friend = friends1;
                                                            break;
                                                        }
                                                    }
                                                    if(friend != null){
                                                        notificationHelper.showChatMessageNotify(chatMessage,friend);
                                                    }
                                                case Constants.MYMSG_TYPE_CONTROLINFRARED_RESP:
                                                    notificationHelper.showNomalNotify(msg.getContent(), "红外警告");
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
