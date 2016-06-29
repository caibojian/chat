package com.cai.chat_05.bean;

/**
 * 请求返回对象
 * 
 * @author chenbiao
 *
 */
public class Result {
	public Result() {
		super();
	}

	public Result(boolean success) {
		this.success = success;
	}

	public Result(boolean success, String message) {
		this.success = success;
		this.message = message;
	}

	public Result(boolean success, String message, Object obj) {
		this.success = success;
		this.message = message;
		this.obj = obj;
	}

	/**
	 * 请求处理是否成功
	 */
	private boolean success;
	/**
	 * 返回的文本提示消息
	 */
	private String message;
	/**
	 * 附加业务对象
	 */
	private Object obj;

	public boolean isSuccess() {
		return success;
	}

	public String getMessage() {
		return message;
	}

	public Object getObj() {
		return obj;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setObj(Object obj) {
		this.obj = obj;
	}
}
