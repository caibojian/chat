package com.cai.chat_05.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cai.chat_05.R;
import com.cai.chat_05.adppter.AudioRecorder;
import com.cai.chat_05.bean.Attachment;

import org.csource.common.MyException;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class VoiceButton extends Button {

	private static final int MIN_RECORD_TIME = 1; // 最短录制时间，单位秒，0为无时间限制

	private static final int RECORD_OFF = 0; // 不在录音
	private static final int RECORD_ON = 1; // 正在录音

	private ImageView mIvRecVolume;
	private Dialog mRecordDialog;
	private TextView mTvRecordDialogTxt;

	private Thread mRecordThread;

	private AudioRecorder mAudioRecorder;
	private int recordState = 0; // 录音的状态
	private float recodeTime = 0.0f; // 录音的时间
	private double voiceValue = 0.0; // 麦克风获取的音量值
	private boolean moveState = false; // 手指是否移动
	private boolean sendState = true; // 是否发送
	private float downY;

	private Context mContext;

	private OnSendVoiceListener onSendVoiceListener;

	public VoiceButton(Context context) {
		super(context);
		init(context);
	}

	public VoiceButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public VoiceButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(Context context) {
		this.mContext = context;
		this.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					if (recordState != RECORD_ON) {
						downY = event.getY();

						mAudioRecorder = new AudioRecorder((new Date())
								.getTime() + "");
						recordState = RECORD_ON;
						try {
							mAudioRecorder.start();
							recordTimethread();
							showVoiceDialog(0);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					break;
				case MotionEvent.ACTION_MOVE:
					float moveY = event.getY();
					if (moveY - downY > 50) {
						moveState = true;
						sendState = false;
						showVoiceDialog(1);
					}
					if (moveY - downY < 20) {
						moveState = false;
						sendState = true;
						showVoiceDialog(0);
					}
					break;
				case MotionEvent.ACTION_CANCEL: // 容错
				case MotionEvent.ACTION_OUTSIDE:
				case MotionEvent.ACTION_UP:
					if (recordState == RECORD_ON) {
						recordState = RECORD_OFF;
						if (mRecordDialog.isShowing()) {
							mRecordDialog.dismiss();
						}
						try {
							if (sendState) {
								File file = mAudioRecorder.stop();
								// 上传文件
								new AsyncTask<File, Object, Attachment>() {

									@Override
									protected Attachment doInBackground(
											File... params) {
										Attachment a = null;
										if (params != null && params.length > 0) {
											File f = params[0];
//											try {
//												a = FastDFSUtil.getInstance()
//														.upload(f);
//												// 重命名本地文件
//												String path = f.getParent()
//														+ "/"
//														+ a.getPath().replace(
//																"/", "_");
//
//												f.renameTo(new File(path));
//											} catch (IOException | MyException e) {
//												e.printStackTrace();
//											}
										}
										return a;
									}

									@Override
									protected void onPostExecute(
											Attachment result) {
										if (result != null) {
											result.setType(Attachment.TYPE_VOICE);
//											Attachment at = DBHelper
//													.getgetInstance(mContext)
//													.addAttachment(result);
											// BroadcastHelper
											// .sendVoiceMsg(result,
											// mContext, friendsId);
											// 刷新本地UI
											if (onSendVoiceListener != null) {
//												onSendVoiceListener.onSend(at);
											}
										}
									}
								}.execute(file);

							} else {
								mAudioRecorder.cancle();
							}
							sendState = true;
							mRecordThread.interrupt();
							voiceValue = 0.0;
						} catch (IOException e) {
							e.printStackTrace();
						}

						if (!moveState) {
							if (recodeTime < MIN_RECORD_TIME) {
								showWarnToast("时间太短  录音失败");
							} else {
								showWarnToast("录音时间："
										+ ((int) recodeTime + "s"));
							}
						}
						moveState = false;
					}
					break;
				}
				return false;
			}
		});
	}

	// 录音时显示Dialog
	void showVoiceDialog(int flag) {
		if (mRecordDialog == null) {
			mRecordDialog = new Dialog(mContext, R.style.DialogStyle);
			mRecordDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			mRecordDialog.getWindow().setFlags(
					WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);
			mRecordDialog.setContentView(R.layout.record_dialog);
			mIvRecVolume = (ImageView) mRecordDialog
					.findViewById(R.id.record_dialog_img);
			mTvRecordDialogTxt = (TextView) mRecordDialog
					.findViewById(R.id.record_dialog_txt);
		}
		switch (flag) {
		case 1:
			mIvRecVolume.setImageResource(R.drawable.record_cancel);
			mTvRecordDialogTxt.setText("松开手指可取消录音");
			break;

		default:
			mIvRecVolume.setImageResource(R.drawable.record_animate_01);
			mTvRecordDialogTxt.setText("向下滑动可取消录音");
			break;
		}
		mTvRecordDialogTxt.setTextSize(14);
		mRecordDialog.show();
	}

	// 录音时间太短时Toast显示
	void showWarnToast(String toastText) {
		Toast toast = new Toast(mContext);
		LinearLayout linearLayout = new LinearLayout(mContext);
		linearLayout.setOrientation(LinearLayout.VERTICAL);
		linearLayout.setPadding(20, 20, 20, 20);

		// 定义一个ImageView
		ImageView imageView = new ImageView(mContext);
		imageView.setImageResource(R.drawable.voice_to_short); // 图标

		TextView mTv = new TextView(mContext);
		mTv.setText(toastText);
		mTv.setTextSize(14);
		mTv.setTextColor(Color.WHITE);// 字体颜色

		// 将ImageView和ToastView合并到Layout中
		linearLayout.addView(imageView);
		linearLayout.addView(mTv);
		linearLayout.setGravity(Gravity.CENTER);// 内容居中
		linearLayout.setBackgroundResource(R.drawable.record_bg);// 设置自定义toast的背景

		toast.setView(linearLayout);
		toast.setGravity(Gravity.CENTER, 0, 0);// 起点位置为中间 100为向下移100dp
		toast.show();
	}

	// 获取文件手机路径
	private String getAmrPath() {
		File file = new File(Environment.getExternalStorageDirectory(),
				"weichat/voiceRecord/" + (new Date()).getTime() + ".amr");
		return file.getAbsolutePath();
	}

	// 录音计时线程
	void recordTimethread() {
		mRecordThread = new Thread(recordThread);
		mRecordThread.start();
	}

	// 录音Dialog图片随声音大小切换
	void setDialogImage() {
		if (voiceValue < 600.0) {
			mIvRecVolume.setImageResource(R.drawable.record_animate_01);
		} else if (voiceValue > 600.0 && voiceValue < 1000.0) {
			mIvRecVolume.setImageResource(R.drawable.record_animate_02);
		} else if (voiceValue > 1000.0 && voiceValue < 1200.0) {
			mIvRecVolume.setImageResource(R.drawable.record_animate_03);
		} else if (voiceValue > 1200.0 && voiceValue < 1400.0) {
			mIvRecVolume.setImageResource(R.drawable.record_animate_04);
		} else if (voiceValue > 1400.0 && voiceValue < 1600.0) {
			mIvRecVolume.setImageResource(R.drawable.record_animate_05);
		} else if (voiceValue > 1600.0 && voiceValue < 1800.0) {
			mIvRecVolume.setImageResource(R.drawable.record_animate_06);
		} else if (voiceValue > 1800.0 && voiceValue < 2000.0) {
			mIvRecVolume.setImageResource(R.drawable.record_animate_07);
		} else if (voiceValue > 2000.0 && voiceValue < 3000.0) {
			mIvRecVolume.setImageResource(R.drawable.record_animate_08);
		} else if (voiceValue > 3000.0 && voiceValue < 4000.0) {
			mIvRecVolume.setImageResource(R.drawable.record_animate_09);
		} else if (voiceValue > 4000.0 && voiceValue < 6000.0) {
			mIvRecVolume.setImageResource(R.drawable.record_animate_10);
		} else if (voiceValue > 6000.0 && voiceValue < 8000.0) {
			mIvRecVolume.setImageResource(R.drawable.record_animate_11);
		} else if (voiceValue > 8000.0 && voiceValue < 10000.0) {
			mIvRecVolume.setImageResource(R.drawable.record_animate_12);
		} else if (voiceValue > 10000.0 && voiceValue < 12000.0) {
			mIvRecVolume.setImageResource(R.drawable.record_animate_13);
		} else if (voiceValue > 12000.0) {
			mIvRecVolume.setImageResource(R.drawable.record_animate_14);
		}
	}

	// 录音线程
	private Runnable recordThread = new Runnable() {

		@Override
		public void run() {
			recodeTime = 0.0f;
			while (recordState == RECORD_ON) {
				// if (recodeTime >= MAX_RECORD_TIME && MAX_RECORD_TIME != 0) {
				// imgHandle.sendEmptyMessage(0);
				// } else
				{
					try {
						Thread.sleep(150);
						recodeTime += 0.15;
						if (!moveState) {
							voiceValue = mAudioRecorder.getAmplitude();
							recordHandler.sendEmptyMessage(1);
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	};

	public Handler recordHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			setDialogImage();
		}
	};

	public interface OnSendVoiceListener {
		public void onSend(Attachment a);
	}

	public void setOnSendVoiceListener(OnSendVoiceListener onSendVoiceListener) {
		this.onSendVoiceListener = onSendVoiceListener;
	}
}
