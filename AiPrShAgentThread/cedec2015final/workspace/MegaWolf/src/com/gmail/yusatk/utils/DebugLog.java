package com.gmail.yusatk.utils;

public class DebugLog {
	static boolean enable = false;
	static public void setEnable(boolean value) {
		enable = value;
	}
	static public void log(String format, Object... args) {
		if(!enable) {
			return;
		}
		System.out.printf(format, args);
	}
	static public void printException(Exception e) {
		if(!enable) {
			return;
		}
		System.out.println(e.getMessage());
		e.printStackTrace();
	}
	static public boolean isEnable() {
		return enable;
	}
}
