package com.cai.chat_05.utils;

import android.os.Environment;


import com.cai.chat_05.bean.Attachment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

public class AttachmentManager {
	public static String sanitizePath(String path, int type) {
		String voiceRecord = "others";
		String postfix = "";
		switch (type) {
		case Attachment.TYPE_DOC:
			postfix = ".doc";
			voiceRecord = "picture";
			break;
		case Attachment.TYPE_DOCX:
			postfix = ".docx";
			voiceRecord = "picture";
			break;
		case Attachment.TYPE_PDF:
			postfix = ".pdf";
			voiceRecord = "picture";
			break;
		case Attachment.TYPE_PPT:
			postfix = ".ppt";
			voiceRecord = "picture";
			break;
		case Attachment.TYPE_PPTX:
			postfix = ".pptx";
			voiceRecord = "picture";
			break;
		case Attachment.TYPE_XLS:
			postfix = ".xls";
			voiceRecord = "picture";
			break;
		case Attachment.TYPE_XLSX:
			postfix = ".xlsx";
			voiceRecord = "picture";
			break;
		case Attachment.TYPE_TXT:
			voiceRecord = "document";
			postfix = ".docx";
			break;
		case Attachment.TYPE_GIF:
			postfix = ".gif";
			voiceRecord = "picture";
			break;
		case Attachment.TYPE_JPG:
			postfix = ".jpg";
			voiceRecord = "picture";
			break;
		case Attachment.TYPE_PNG:
			postfix = ".png";
			voiceRecord = "picture";
			break;
		case Attachment.TYPE_RAR:
			postfix = ".rar";
			voiceRecord = "zip";
			break;
		case Attachment.TYPE_ZIP:
			postfix = ".zip";
			voiceRecord = "zip";
			break;
		case Attachment.TYPE_VOICE:
			voiceRecord = "voice";
			break;
		case Attachment.TYPE_VIDEO:
			voiceRecord = "video";
			break;
		default:
			voiceRecord = "others";
			break;
		}

		return Environment.getExternalStorageDirectory().getAbsolutePath()
				+ "/weishe/" + voiceRecord + "/" + path + postfix;
	}

	// 根据规则从本地获取
	public static String getFilePath(Attachment attachment) {
		if (attachment == null) {
			return null;
		}
		File file = new File(sanitizePath(attachment.getPath()
				.replace("/", "_"), attachment.getType()));
		if (!file.exists()) {
			return null;
		}
		return file.getPath();
	}

	// 根据规则从本地获取，如果获取不到再从服务器获取
	public static String getFile(Attachment attachment) {
		if (attachment == null) {
			return null;
		}
		File file = new File(sanitizePath(attachment.getPath()
				.replace("/", "_"), attachment.getType()));
		if (!file.exists()) {
			String remoteFilename = attachment.getGroupName() + "/"
					+ attachment.getPath();
			byte[] fb = FastDFSUtil.getInstance().download(remoteFilename);
			if (fb != null && fb.length > 0) {
				try {
					file.createNewFile();
					FileOutputStream out = new FileOutputStream(file);
					out.write(fb);
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
					return null;
				}

			}
		}
		return file.getPath();
	}

	/**
	 * 根据文件在服务器的位置换算本地位置，如果存在则返回文件本地文件，如果不存在则返回null
	 * 
	 * @param groupName
	 * @param path
	 * @return
	 */
	public static File getFile(String groupName, String path) {
		String filePath = Environment.getExternalStorageDirectory()
				.getAbsolutePath()
				+ "/weishe/"
				+ groupName
				+ "/"
				+ path.replace("/", "_");
		File file = new File(filePath);
		if (file.exists()) {
			return file;
		}
		return null;
	}

	/**
	 * 得到amr的时长
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static long getAmrDuration(File file) throws IOException {
		long duration = -1;
		int[] packedSize = { 12, 13, 15, 17, 19, 20, 26, 31, 5, 0, 0, 0, 0, 0,
				0, 0 };
		RandomAccessFile randomAccessFile = null;
		try {
			randomAccessFile = new RandomAccessFile(file, "rw");
			long length = file.length();// 文件的长度
			int pos = 6;// 设置初始位置
			int frameCount = 0;// 初始帧数
			int packedPos = -1;
			// ///////////////////////////////////////////////////
			byte[] datas = new byte[1];// 初始数据值
			while (pos <= length) {
				randomAccessFile.seek(pos);
				if (randomAccessFile.read(datas, 0, 1) != 1) {
					duration = length > 0 ? ((length - 6) / 650) : 0;
					break;
				}
				packedPos = (datas[0] >> 3) & 0x0F;
				pos += packedSize[packedPos] + 1;
				frameCount++;
			}
			// ///////////////////////////////////////////////////
			duration += frameCount * 20;// 帧数*20
		} finally {
			if (randomAccessFile != null) {
				randomAccessFile.close();
			}
		}
		return duration;
	}
}
