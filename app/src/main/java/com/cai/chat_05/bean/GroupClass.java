package com.cai.chat_05.bean;


/**
 * 群类别实体
 * 
 * @author chenbiao
 *
 */
public class GroupClass {
	public final static int TYPE_BIG_CLASS = 0;
	public final static int TYPE_SMALL_CLASS = 1;
	private int id;

	/**
	 * 分类名称
	 */
	private String name;

	/**
	 * 分类类型
	 */
	private int type;

	/**
	 * 所属大类
	 */
	private GroupClass bigClass;

	public String getName() {
		return name;
	}

	public int getType() {
		return type;
	}

	public GroupClass getBigClass() {
		return bigClass;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setType(int type) {
		this.type = type;
	}

	public void setBigClass(GroupClass bigClass) {
		this.bigClass = bigClass;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}
