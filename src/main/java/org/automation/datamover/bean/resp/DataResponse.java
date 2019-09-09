package org.automation.datamover.bean.resp;

import org.automation.datamover.bean.Constant;

/**
 * 后台请求返回数据包装类
 */
public class DataResponse<T> {

	private boolean success;//本次请求是否成功

	private Integer code;//失败时的错误代码（成功时为空）

	private String message;//本次请求的消息描述（可能是错误消息，也可能是正确时给出的提示）

	private T data;//本次请求得到的数据

	public DataResponse(boolean success, String message, T data) {
		this(success, success ? null : Constant.SYSTEM_CODE_ERROR, message, data);
	}

	protected DataResponse(boolean success, Integer code, String message, T data) {
		this.success = success;
		this.code = code;
		this.message = message;
		this.data = data;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

}
