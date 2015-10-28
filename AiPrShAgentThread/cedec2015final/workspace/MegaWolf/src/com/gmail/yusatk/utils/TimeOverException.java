package com.gmail.yusatk.utils;

public class TimeOverException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2880184334432192544L;

	public TimeOverException(long limit, long actual) {
		super(String.format("*** Time over: Limit: %d, Actual %d\n", limit, actual));
	}
}
