package com.cai.chat_05;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.cai.chat_05.utils.UIHelper;

public class WelcomeActivity extends Activity {

    protected static final String TAG = "WelcomeActivity";
    private Context mContext;
    private AppContext mAppContext;
    private ImageView mImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
//        mAppContext = (AppContext) this.getApplication();
        findView();
        init();
    }

    private void findView() {
        mImageView = (ImageView) findViewById(R.id.iv_welcome);
    }

    private void init() {
        mImageView.postDelayed(new Runnable() {
            @Override
            public void run() {
//				SpUtil.getInstance();
//				sp = SpUtil.getSharePerference(mContext);
//				SpUtil.getInstance();
//				boolean isFirst = SpUtil.isFirst(sp);
//				if (!isFirst) {
//					// 快速登陆
//					quickLogin();
//				} else {
//					SpUtil.getInstance();
//					SpUtil.setBooleanSharedPerference(sp, "isFirst", false);

                UIHelper.startLonginActivity(WelcomeActivity.this);
//				}
            }
        }, 2000);

    }
}
