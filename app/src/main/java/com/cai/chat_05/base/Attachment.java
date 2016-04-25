package com.cai.chat_05.base;

import java.io.Serializable;
import java.util.Date;

/**
 * 文件附件
 * 
 * @author chenbiao
 *
 */
public class Attachment implements Serializable {

	public static final int TYPE_UNKNOWN = 0;// 未知文件类型
	public static final int TYPE_JPG = 1;
	public static final int TYPE_PNG = 2;
	public static final int TYPE_GIF = 3;
	public static final int TYPE_DOC = 4;
	public static final int TYPE_DOCX = 5;
	public static final int TYPE_PPT = 6;
	public static final int TYPE_PPTX = 7;
	public static final int TYPE_XLS = 8;
	public static final int TYPE_XLSX = 9;
	public static final int TYPE_PDF = 10;
	public static final int TYPE_TXT = 11;
	public static final int TYPE_RAR = 12;
	public static final int TYPE_ZIP = 13;

	public static final int TYPE_VOICE = 101;// 语音
	public static final int TYPE_VIDEO = 102;// 视频

	public Attachment() {
	}

	public Attachment(Long id) {
		this.id = id;
	}

	public Attachment(Long id, Integer attachmentId, String name,
			String groupName, String path, Integer type,
			Date createDate) {
		this.id = id;
		this.attachmentId = attachmentId;
		this.name = name;
		this.groupName = groupName;
		this.path = path;
		this.type = type;
		this.createDate = createDate;
	}

	private Long id;// 本地数据库id
	private int attachmentId;// 在服务器上的数据库id

	/**
	 * 文件名称
	 */
	private String name;

	/**
	 * 对应在FastDFS上的group
	 */
	private String groupName;

	/**
	 * 文件位置，所有的文件都存在网络上
	 */
	private String path;

	private int type;

	private Date createDate;

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public int getType() {
		return type;
	}

	/**
	 * 文件大小
	 */
	private long size;

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public String getGroupName() {
		return groupName;
	}

	public String getPath() {
		return path;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getAttachmentId() {
		return attachmentId;
	}

	public void setAttachmentId(int attachmentId) {
		this.attachmentId = attachmentId;
	}
}
