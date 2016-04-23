package com.cai.chat_05.bean;

import java.io.Serializable;

public class Friends extends Pinyin implements Serializable {

	public static final String CACHE_KEY_PREFIX = "weichat_friends_list_";
	private int id;
	/**
	 * 正常状态
	 */
	public final static int VISIBLE_ONLINE_NORMAL = 0;
	/**
	 * 在线隐身
	 */
	public final static int VISIBLE_ONLINE_INVISIBLE = 1;
	/**
	 * 隐身可见
	 */
	public final static int VISIBLE_INVISIBLE_VISIBLE = 2;

	private int userId;
	private String name;

	/**
	 * 备注名称
	 */

	private String remarkName;

	/**
	 * 是否在线
	 */

	private boolean online;

	/**
	 * 在线方式
	 */
	private int onlineType;

	/**
	 * 个人头像地址
	 */
	private String avatarPath;

	/**
	 * 个人签名
	 */
	private String signature;

	/**
	 * 性别
	 */
	private int gender;

	/**
	 * 年龄
	 */
	private int age;

	/**
	 * 所在好友组ID
	 */
	private int friendsGroupId;
	/**
	 * 非实际数据字段，仅供临时使用
	 */
	private boolean select = false;

	public boolean isSelect() {
		return select;
	}

	public void setSelect(boolean select) {
		this.select = select;
	}

	public int getId() {
		return id;
	}

	public int getUserId() {
		return userId;
	}

	public String getName() {
		return name;
	}

	public String getRemarkName() {
		return remarkName;
	}

	public boolean isOnline() {
		return online;
	}

	public int getOnlineType() {
		return onlineType;
	}

	public String getAvatarPath() {
		return avatarPath;
	}

	public String getSignature() {
		return signature;
	}

	public int getGender() {
		return gender;
	}

	public int getAge() {
		return age;
	}

	public int getFriendsGroupId() {
		return friendsGroupId;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setRemarkName(String remarkName) {
		this.remarkName = remarkName;
	}

	public void setOnline(boolean online) {
		this.online = online;
	}

	public void setOnlineType(int onlineType) {
		this.onlineType = onlineType;
	}

	public void setAvatarPath(String avatarPath) {
		this.avatarPath = avatarPath;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public void setGender(int gender) {
		this.gender = gender;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public void setFriendsGroupId(int friendsGroupId) {
		this.friendsGroupId = friendsGroupId;
	}

	/**
	 * 
	 * @param userId
	 *            当前登录账户
	 * @return
	 */
	public static String getCacheKey(int userId) {
		return CACHE_KEY_PREFIX + userId;
	}
}
