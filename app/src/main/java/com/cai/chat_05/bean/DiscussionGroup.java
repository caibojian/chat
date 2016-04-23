package com.cai.chat_05.bean;

import java.io.Serializable;
import java.util.Date;

/**
 * 讨论组
 * 
 * @author chenbiao
 *
 */

public class DiscussionGroup extends Pinyin implements Serializable {
	public static final String CACHE_KEY_PREFIX = "weichat_discussiongroup_list_";
	/**
	 * 讨论组名称
	 */

	private int id;

	private String name;

	/**
	 * 创建时间
	 */
	private Date createDate;

	public String getName() {
		return name;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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
