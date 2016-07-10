package com.cai.chat_05;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import com.cai.chat_05.adppter.PiToCantralAdapter;
import com.cai.chat_05.base.BaseActivity;
import com.cai.chat_05.service.IoTService;
import com.cai.chat_05.view.HandyTextView;
import com.kyleduo.switchbutton.SwitchButton;

public class PiControlActivity extends BaseActivity {
	private Context mContext;
	private BroadcastReceiver receiver;
	private IoTService ioTService;
	private IoTService.MsgBinder iBinder;
	private ListView lv;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		Intent intent = new Intent(mContext, IoTService.class);
		bindService(intent, conn, Context.BIND_AUTO_CREATE);
		setContentView(R.layout.activity_led_control);
		initViews();
		initEvents();
		lv.setAdapter(new PiToCantralAdapter(this));
		receiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {

			}
		};

		IntentFilter intentFilter = new IntentFilter();
//		intentFilter.addAction(Constants.INTENT_ACTION_LOGIN);
		mContext.registerReceiver(receiver, intentFilter);
	}


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
		lv = (ListView)findViewById(R.id.pi_to_contral_list);
	}

	@Override
	protected void initEvents() {

	}

	/** 显示自定义Toast提示(来自String) **/
	public void showCustomToast(String text) {
		View toastRoot = LayoutInflater.from(this).inflate(
				R.layout.common_toast, null);
		((HandyTextView) toastRoot.findViewById(R.id.toast_text)).setText(text);
		Toast toast = new Toast(this);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setView(toastRoot);
		toast.show();
	}

	/**
	 * 控制led灯开关
	 * @param isChecked
	 * @param position
     */
	public void controlLed(boolean isChecked,int position){
		iBinder.contralLED(isChecked);
		updateProgressPartly(position, isChecked);
	}

	/**
	 * 控制红外监控开关
	 * @param isChecked
	 * @param position
	 */
	public void controlInfrared(boolean isChecked,int position){
		iBinder.contralInfrared(isChecked);
		updateProgressPartly(position, isChecked);
	}

	private void updateProgressPartly(int position,boolean isChecked){
		int firstVisiblePosition = lv.getFirstVisiblePosition();
		int lastVisiblePosition = lv.getLastVisiblePosition();
		if(position>=firstVisiblePosition && position<=lastVisiblePosition){
			View view = lv.getChildAt(position - firstVisiblePosition);
			if(view.getTag() instanceof PiToCantralAdapter.ItemViewHolder){
				PiToCantralAdapter.ItemViewHolder vh = (PiToCantralAdapter.ItemViewHolder)view.getTag();
				if(isChecked){
					vh.things_photo.setImageResource(R.drawable.led_on);
				}else{
					vh.things_photo.setImageResource(R.drawable.led_off);
				}
			}
		}
	}
}
