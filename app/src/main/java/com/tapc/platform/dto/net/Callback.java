package com.tapc.platform.dto.net;

public interface Callback {
	/**
	 * 请求�?��
	 * �?��用来，显示loading;
	 */
	void onStart();
	
	/**
	 * 请求成功
	 * @param o 返回数据对象（DTOResponse)
	 */
	void onSuccess(Object o);
	void onSuccess(String result);	
	/**
	 * 请求失败
	 * 关闭loading;
	 */
	void onFailure(Object o);
}
