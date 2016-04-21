package com.cai.chat_05.utils;

import com.cai.chat_05.bean.Attachment;

import org.csource.common.MyException;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.FileInfo;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.StorageClient1;
import org.csource.fastdfs.StorageServer;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerGroup;
import org.csource.fastdfs.TrackerServer;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Date;

public class FastDFSUtil {
	private static FastDFSUtil mFastDFSUtil;
	private static TrackerClient trackerClient;
	private static TrackerServer trackerServer;
	private static StorageServer storageServer;
	private static StorageClient storageClient;
	private static StorageClient1 storageClient1;

	private FastDFSUtil() {

	}

	public static FastDFSUtil getInstance() {
		if (mFastDFSUtil == null) {

			// ClientGlobal.init(configFilePath);
			// 连接超时的时限，单位为毫秒
			ClientGlobal.setG_connect_timeout(2000);

			// 网络超时的时限，单位为毫秒
			ClientGlobal.setG_network_timeout(30000);

			ClientGlobal.setG_anti_steal_token(false);

			// 字符集
			ClientGlobal.setG_charset("UTF-8");

			ClientGlobal.setG_secret_key("FastDFS1234567890");

			// HTTP访问服务的端口号
			ClientGlobal.setG_tracker_http_port(8080);

			// Tracker服务器列表
			InetSocketAddress[] trackerServers = new InetSocketAddress[1];
			trackerServers[0] = new InetSocketAddress("123.57.88.238", 22122);

			ClientGlobal.setG_tracker_group(new TrackerGroup(trackerServers));

			mFastDFSUtil = new FastDFSUtil();

			trackerClient = new TrackerClient();
			try {
				trackerServer = trackerClient.getConnection();
			} catch (IOException e) {
				e.printStackTrace();
			}
			storageServer = null;
			storageClient = new StorageClient(trackerServer, storageServer);
			storageClient1 = new StorageClient1(trackerServer, storageServer);
		}
		return mFastDFSUtil;
	}

	public byte[] download(String remoteFilename) {
		byte[] bytes = null;
		try {
			bytes = storageClient1.download_file1(remoteFilename);
		} catch (IOException | MyException e) {
			e.printStackTrace();
		}
		return bytes;
	}

	/**
	 * 上传文件方法
	 * 
	 * @param file
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws MyException
	 */
	public Attachment upload(File file) throws FileNotFoundException,
			IOException, MyException {

		if (file == null) {
			return null;
		}

		NameValuePair[] meta_list = new NameValuePair[3];

		int type = FileUtil.getFileType(file);
		meta_list[0] = new NameValuePair("author", "panghu");
		meta_list[1] = new NameValuePair("name", file.getName());
		meta_list[2] = new NameValuePair("type", type + "");

		FileInputStream fis = new FileInputStream(file);
		byte[] file_buff = null;
		if (fis != null) {
			int len = fis.available();
			file_buff = new byte[len];
			fis.read(file_buff);
		}

		long startTime = System.currentTimeMillis();
		String[] results = storageClient
				.upload_file(file_buff, null, meta_list);

		if (results == null) {

			return null;
		}

		String group_name = results[0];
		String remote_filename = results[1];
		Attachment attachment = null;
		if (group_name != null && !group_name.isEmpty()
				&& remote_filename != null && !remote_filename.isEmpty()) {
			attachment = new Attachment();
			attachment.setCreateDate(new Date());
			attachment.setGroupName(group_name);
			attachment.setPath(remote_filename);
			attachment.setName(file.getName());
			attachment.setSize(file.length());
		}

		return attachment;
	}

	public Attachment getFileInfor(String groupName, String path) {
		Attachment a = new Attachment();
		try {
			FileInfo fi = storageClient.get_file_info(groupName, path);
			NameValuePair[] meta_list = storageClient.get_metadata(groupName,
					path);
			if (fi != null) {
				a.setCreateDate(fi.getCreateTimestamp());
				a.setGroupName(groupName);
				a.setPath(path);
				a.setSize(fi.getFileSize());
				if (meta_list != null && meta_list.length > 0) {
					for (NameValuePair p : meta_list) {
						switch (p.getName()) {
						case "name":
							a.setName(p.getValue());
							break;
						case "type":
							a.setType(StringUtils.toInt(p.getValue()));
							break;
						default:
							break;
						}
					}
				}
				return a;
			} else {
				return null;
			}
		} catch (IOException | MyException e) {
			e.printStackTrace();
			return null;

		}
	}

}
