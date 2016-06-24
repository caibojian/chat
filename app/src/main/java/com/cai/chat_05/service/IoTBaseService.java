package com.cai.chat_05.service;

import android.app.AlarmManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.iot.AWSIotKeystoreHelper;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttClientStatusCallback;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttLastWillAndTestament;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttManager;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttQos;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.iot.AWSIotClient;
import com.amazonaws.services.iot.model.AttachPrincipalPolicyRequest;
import com.amazonaws.services.iot.model.CreateKeysAndCertificateRequest;
import com.amazonaws.services.iot.model.CreateKeysAndCertificateResult;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;

/**
 * Created by CAI on 2016/6/24.
 */
public abstract class IoTBaseService extends Service {

    static final String LOG_TAG = IoTService.class.getCanonicalName();
    //iot service
    // Endpoint Prefix = random characters at the beginning of the custom AWS
    // IoT endpoint
    // describe endpoint call returns: XXXXXXXXXX.iot.<region>.amazonaws.com,
    // endpoint prefix string is XXXXXXX
    static final String CUSTOMER_SPECIFIC_ENDPOINT_PREFIX = "A1KJQ0JFEFY3TN";
    // Cognito pool ID. For this app, pool needs to be unauthenticated pool with
    // AWS IoT permissions.
    static final String COGNITO_POOL_ID = "ap-northeast-1:96b4bf76-0482-419f-99dc-7e82d70490a1";
    // Name of the AWS IoT policy to attach to a newly created certificate
    static final String AWS_IOT_POLICY_NAME = "android_iot";

    // Region of AWS IoT
    static final Regions MY_REGION = Regions.AP_NORTHEAST_1;
    // Filename of KeyStore file on the filesystem
    static final String KEYSTORE_NAME = "caibojian.bks";
    // Password for the private key in the KeyStore
    static final String KEYSTORE_PASSWORD = "caibojian.1991";
    // Certificate and key aliases in the KeyStore
    static final String CERTIFICATE_ID = "cai";

    //当前iot服务状态
    String iotStatus = "";

    static final String 	ACTION_START 	= LOG_TAG + ".START"; // Action to start
    static final String 	ACTION_STOP		= LOG_TAG + ".STOP"; // Action to stop
    static final String 	ACTION_KEEPALIVE= LOG_TAG + ".KEEPALIVE"; // Action to keep alive used by alarm manager
    static final String 	ACTION_RECONNECT= LOG_TAG + ".RECONNECT"; // Action to reconnect
    static final String 	DEVICE_ID_FORMAT = "andr_%s"; // Device ID Format, add any prefix you'd like
    static final String		MQTT_THREAD_NAME = "MqttService[" + LOG_TAG + "]"; // Handler Thread ID
    Handler mConnHandler;	  // Seperate Handler thread for networking
    boolean mStarted = false; // Is the Client started?

    AWSIotClient mIotAndroidClient;
    public AWSIotMqttManager mqttManager;
    public String clientId;
    String keystorePath;
    String keystoreName;
    String keystorePassword;

    KeyStore clientKeyStore = null;
    String certificateId;

    CognitoCachingCredentialsProvider credentialsProvider;
    ConnectivityManager mConnectivityManager; // To check for connectivity changes

    public void IOTinit(){
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
        mqttManager.setReconnectRetryLimits(2, 100);

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

        if (AWSIotKeystoreHelper.isKeystorePresent(keystorePath, keystoreName)) {
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
        }
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
     * Connects to the broker with the appropriate datastore
     */
    synchronized void connect(){
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
}
