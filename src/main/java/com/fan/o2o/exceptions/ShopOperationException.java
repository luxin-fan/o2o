package com.fan.o2o.exceptions;

public class ShopOperationException extends RuntimeException {

	private static final long serialVersionUID = -495866519092258201L;

	/**
	 * 这里是对异常做了一个很薄的封装，作用在于可以发现异常所在的操作是什么
	 * */
	public ShopOperationException(String msg){
		super(msg);
	}
}
