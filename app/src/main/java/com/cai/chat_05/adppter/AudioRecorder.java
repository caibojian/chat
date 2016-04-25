package com.cai.chat_05.adppter;

import java.io.File;
import java.io.IOException;

import android.media.MediaRecorder;
import android.os.Environment;

import com.cai.chat_05.bean.Attachment;
import com.cai.chat_05.utils.AttachmentManager;

public class AudioRecorder {
	private static int SAMPLE_RATE_IN_HZ = 8000; // ������

	private MediaRecorder mMediaRecorder;
	private String mPath;

	public AudioRecorder(String path) {
		if (mMediaRecorder == null) {
			mMediaRecorder = new MediaRecorder();
		}
		this.mPath = AttachmentManager
				.sanitizePath(path, Attachment.TYPE_VOICE);
	}

	public void start() throws IOException {
		String state = Environment.getExternalStorageState();
		if (!state.equals(Environment.MEDIA_MOUNTED)) {
			throw new IOException("SD Card is not mounted,It is  " + state
					+ ".");
		}
		File directory = new File(mPath).getParentFile();
		if (!directory.exists() && !directory.mkdirs()) {
			throw new IOException("Path to file could not be created");
		}
		mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
		mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		// recorder.setAudioChannels(AudioFormat.CHANNEL_CONFIGURATION_MONO);
		mMediaRecorder.setAudioSamplingRate(SAMPLE_RATE_IN_HZ);
		mMediaRecorder.setOutputFile(mPath);
		mMediaRecorder.prepare();
		mMediaRecorder.start();
	}

	public File stop() throws IOException {
		mMediaRecorder.stop();
		mMediaRecorder.release();
		File file = new File(mPath);
		if (file.exists()) {
			return file;
		}
		return null;
	}

	public void cancle() throws IOException {
		mMediaRecorder.stop();
		mMediaRecorder.release();
		File file = new File(mPath);
		if (file.exists()) {
			file.delete();
		}
	}

	public double getAmplitude() {
		if (mMediaRecorder != null) {
			return (mMediaRecorder.getMaxAmplitude());
		} else
			return 0;
	}
}