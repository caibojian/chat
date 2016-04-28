package com.cai.chat_05.bean;

import java.io.Serializable;
import java.util.Date;

public class User implements Serializable {
	public static final String CACHE_KEY_PREFIX = "weichat_user_";
	public final static int GENDER_MALE = 0;// 男性
	public final static int GENDER_FEMALE = 1;// 女性
	public final static int GENDER_UNKNOWN = 2;// 未知
	/**
	 * 
	 */
	private static final long serialVersionUID = 112325248L;
	/**
	 * 用户id
	 */
	private int id;
	/**
	 * 用户id
	 */
	private String uuid;
	/**
	 * 账户
	 */
	private String account;

	/**
	 * 密码
	 */
	private String password;

	/**
	 * 昵称
	 */
	private String name;

	/**
	 * 个性签名
	 */
	private String signature;
	/**
	 * 个人性别，0男1女2未知
	 */
	private int gender;
	private int age;
	private String avatar;
	private Date birthday;
	/**
	 * 在线
	 */
	private boolean online;

	public void setUuid(String uuid){
		this.setUuid(uuid);
	}

	public String getUuid(){
		return uuid;
	}
	public boolean isOnline() {
		return online;
	}

	public void setOnline(boolean online) {
		this.online = online;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAccount() {
		return account;
	}

	public String getPassword() {
		return password;
	}

	public String getName() {
		return name;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public int getGender() {
		return gender;
	}

	public int getAge() {
		return age;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setGender(int gender) {
		this.gender = gender;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public static String getCacheKey(int userId) {
		return CACHE_KEY_PREFIX + userId;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}
}
