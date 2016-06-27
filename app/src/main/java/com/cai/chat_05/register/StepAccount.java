package com.cai.chat_05.register;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.alibaba.fastjson.JSON;
import com.cai.chat_05.RegisterActivity;
import com.cai.chat_05.view.HandyTextView;

public class StepAccount extends RegisterStep implements TextWatcher {

	private EditText mEtAccount;
	private HandyTextView mHtvNotice;

	private static String mAccount;
	private static boolean mIsChange = true;

	private static Result result = null;
	private static boolean mIsReceived = false;

	public StepAccount(RegisterActivity activity, View contentRootView) {
		super(activity, contentRootView);
	}

	public String getAccount() {
		return mAccount;
	}

	@Override
	public void initViews() {
		mEtAccount = (EditText) findViewById(R.id.reg_account_et_account);
		mHtvNotice = (HandyTextView) findViewById(R.id.reg_account_htv_notice);
	}

	@Override
	public void initEvents() {
		System.out.println(mEtAccount);
		mEtAccount.addTextChangedListener(this);
	}

	@Override
	public void doNext() {
		WeisheApi.isAccountExist(mHandler, mEtAccount.getText().toString()
				.trim());
	}

	@Override
	public boolean validate() {
		mAccount = null;
		if (VerifyUtils.isNull(mEtAccount)) {
			showCustomToast("请填写账号");
			mEtAccount.requestFocus();
			return false;
		}
		String account = mEtAccount.getText().toString().trim();
		if (VerifyUtils.matchAccount(account)) {
			mAccount = account;
			return true;
		}
		showCustomToast("账号格式不正确");
		mEtAccount.requestFocus();
		return false;
	}

	@Override
	public boolean isChange() {
		return mIsChange;
	}

	@Override
	public void afterTextChanged(Editable s) {

	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
								  int after) {

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		mIsChange = true;
		if (s.toString().length() > 0) {
			mHtvNotice.setVisibility(View.VISIBLE);
			char[] chars = s.toString().toCharArray();
			StringBuffer buffer = new StringBuffer();
			for (int i = 0; i < chars.length; i++) {
				buffer.append(chars[i] + "");
			}
			mHtvNotice.setText(buffer.toString());
		} else {
			mHtvNotice.setVisibility(View.GONE);
		}
	}

	protected AsyncHttpResponseHandler mHandler = new AsyncHttpResponseHandler() {

		@Override
		public void onSuccess(int statusCode, Header[] headers,
				byte[] responseBytes) {
			String data = new String(responseBytes);
			Result r = (Result) JSON.parseObject(data, Result.class);

			if (r != null) {
				showCustomToast(r.getMessage());
				if (!r.isSuccess()) {
					mIsChange = false;
					showCustomToast("该账号可用");
					mOnNextActionListener.next();
				}
			} else {
				showCustomToast("注册发生异常！");
			}

			mIsReceived = false;
		}

		@Override
		public void onFailure(int arg0, Header[] arg1, byte[] arg2,
				Throwable arg3) {
			showCustomToast("注册发生异常！");

		}

	};

}
