package com.gmail.yusatk.utils;

public class TimeWatcher {
	public static boolean throwException = false;
	
	long limitMilliSec;
	long start = 0;
	public TimeWatcher(long limitMilliSec) {
		this.limitMilliSec = limitMilliSec;
	}
	
	public void start() {
		start = System.nanoTime();
	}
	
	public void check() throws TimeOverException{
		long now = System.nanoTime();
		long diffMilli = (now - start) / 1000000;
		if(throwException && diffMilli > limitMilliSec) {
			throw new TimeOverException(limitMilliSec, diffMilli);
		}
	}
	public boolean hasExtraTime() {
		long now = System.nanoTime();
		long diffMilli = (now - start) / 1000000;
		return diffMilli < limitMilliSec;
	}
}
