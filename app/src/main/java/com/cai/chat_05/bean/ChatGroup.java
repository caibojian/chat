package com.cai.chat_05.bean;

import java.io.Serializable;
import java.util.Date;

/**
 * 聊天群
 * 
 * @author chenbiao
 *
 */
public class ChatGroup extends Pinyin implements Serializable {
	public static final String CACHE_KEY_PREFIX = "weichat_chatgroup_list_";
	private int id;
	/**
	 * 群号,由数组组成，唯一id
	 */
	private String account;
	/**
	 * 群名称
	 */

	private String name;

	/**
	 * 群口号
	 */
	private String slogan;

	/**
	 * 创建时间
	 */
	private Date createDate;

	/**
	 * 群主
	 */
	private User createBy;

	/**
	 * 管理员1
	 */
	private User manager1;
	/**
	 * 管理员2
	 */
	private User manager2;

	/**
	 * 群图标
	 */
	private String avatarPath;
	/**
	 * 群组所属大类
	 */
	private GroupClass bigClass;

	/**
	 * 群租所属小类
	 */
	private GroupClass smallClass;

	public String getAccount() {
		return account;
	}

	public String getName() {
		return name;
	}

	public String getSlogan() {
		return slogan;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public User getCreateBy() {
		return createBy;
	}

	public User getManager1() {
		return manager1;
	}

	public User getManager2() {
		return manager2;
	}

	public GroupClass getBigClass() {
		return bigClass;
	}

	public GroupClass getSmallClass() {
		return smallClass;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setSlogan(String slogan) {
		this.slogan = slogan;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public void setCreateBy(User createBy) {
		this.createBy = createBy;
	}

	public void setManager1(User manager1) {
		this.manager1 = manager1;
	}

	public void setManager2(User manager2) {
		this.manager2 = manager2;
	}

	public void setBigClass(GroupClass bigClass) {
		this.bigClass = bigClass;
	}

	public void setSmallClass(GroupClass smallClass) {
		this.smallClass = smallClass;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAvatarPath() {
		return avatarPath;
	}

	public void setAvatarPath(String avatarPath) {
		this.avatarPath = avatarPath;
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
