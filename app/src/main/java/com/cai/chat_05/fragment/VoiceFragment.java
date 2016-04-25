package com.cai.chat_05.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import com.cai.chat_05.R;
import com.cai.chat_05.emoji.OnSendClickListener;
import com.cai.chat_05.view.VoiceButton;

public class VoiceFragment extends Fragment {
	private LinearLayout mRootView;
	private CheckBox mCheckBox;
	private VoiceButton mButton;
	private OnSendClickListener listener;
	private VoiceButton.OnSendVoiceListener onSendVoiceListener;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		mRootView = (LinearLayout) inflater.inflate(R.layout.voice_frag_main,
				container, false);
		initWidget(mRootView);

		return mRootView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		mButton.setOnSendVoiceListener(onSendVoiceListener);
	}

	private void initWidget(View rootView) {
		mCheckBox = (CheckBox) rootView.findViewById(R.id.voice_title_flag);
		mButton = (VoiceButton) rootView.findViewById(R.id.voidc_record_btn);
		mCheckBox.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (listener != null) {
					listener.onClickFlagButton();
				}
			}
		});

	}

	public void setOnSendClickListener(OnSendClickListener l) {
		this.listener = l;
	}

	public void setOnSendVoiceListener(VoiceButton.OnSendVoiceListener onSendVoiceListener) {
		this.onSendVoiceListener = onSendVoiceListener;
	}

}
