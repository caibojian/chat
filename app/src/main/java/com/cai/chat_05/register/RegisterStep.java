package com.cai.chat_05.register;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;

import com.cai.chat_05.RegisterActivity;

public abstract class RegisterStep {
	protected RegisterActivity mActivity;
	protected Context mContext;
	private View mContentRootView;
	protected onNextActionListener mOnNextActionListener;

	public RegisterStep(RegisterActivity activity, View contentRootView) {
		mActivity = activity;
		mContext = mActivity;
		mContentRootView = contentRootView;
		initViews();
		initEvents();
	}

	public abstract void initViews();

	public abstract void initEvents();

	public abstract boolean validate();

	public abstract boolean isChange();

	public View findViewById(int id) {
		return mContentRootView.findViewById(id);
	}

	public abstract void doNext();

	public void nextAnimation() {

	}

	public void preAnimation() {

	}

	protected void showCustomToast(String text) {
		mActivity.showCustomToast(text);
	}

	protected void putAsyncTask(AsyncTask<Void, Void, Integer> asyncTask) {
		mActivity.putAsyncTask(asyncTask);
	}

	protected void showLoadingDialog(String text) {
		mActivity.showLoadingDialog(text);
	}

	protected void dismissLoadingDialog() {
		mActivity.dismissLoadingDialog();
	}

	protected int getScreenWidth() {
		return mActivity.getScreenWidth();
	}

	public void setOnNextActionListener(onNextActionListener listener) {
		mOnNextActionListener = listener;
	}

	public interface onNextActionListener {
		void next();
	}
}
