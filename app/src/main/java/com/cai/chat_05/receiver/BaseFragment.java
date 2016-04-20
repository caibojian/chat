package com.cai.chat_05.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;

/**
 * 消息转发总枢纽
 * 
 * @author chenbiao
 *
 */
public abstract class BaseFragment extends Fragment {
	public static final int STATE_NONE = 0;
	public static final int STATE_REFRESH = 1;
	public static final int STATE_PRESSNONE = 2;// 正在下拉但还没有到刷新的状态
	public static int mState = STATE_NONE;

	protected LayoutInflater mInflater;

	private BroadcastReceiver receiver;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 注册监听消息

		receiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				BaseFragment.this.onReceive(context, intent);
			}
		};

		registerReceiver(receiver);
	}

	/**
	 * 当接收到消息该调用方法更新UI，主要用于与Service通信
	 * 
	 * @param context
	 * @param intent
	 */
	public abstract void onReceive(Context context, Intent intent);

	/**
	 * 注册自身关心的消息监听
	 * 
	 * @param receiver
	 */
	public abstract void registerReceiver(BroadcastReceiver receiver);

	/**
	 * 注销自身关心的消息监听
	 * 
	 * @param receiver
	 */
	public abstract void unRegisterReceiver(BroadcastReceiver receiver);

	@Override
	public void onDestroy() {
		unRegisterReceiver(receiver);
		super.onDestroy();
	}

	/**
	 * 注销自身关心的消息监听
	 */

}
