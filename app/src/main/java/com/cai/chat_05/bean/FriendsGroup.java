package com.cai.chat_05.bean;

import java.io.Serializable;

/**
 * 好友分组
 * 
 * @author chenbiao
 *
 */
public class FriendsGroup implements Comparable<FriendsGroup>, Serializable {
	public static final String CACHE_KEY_PREFIX = "weichat_friends_group_list_";

	private int id;
	private String name;
	private int position;

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

	public String getName() {
		return name;
	}

	public int getPosition() {
		return position;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPosition(int position) {
		this.position = position;
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

	@Override
	public int compareTo(FriendsGroup another) {

		return this.position - another.getPosition();
	}
}
