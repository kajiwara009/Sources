package com.github.haretaro.pingwo.brain.util;

import java.util.List;
import java.util.Random;

public class Util {
	
	public final static boolean DEBUG = false;
	
	public static <T> T randomSelect(List<T> list){
		if(list.size() == 0){
			return null;
		}
		int num = new Random().nextInt(list.size());
		return list.get(num);
	}
	
	public static void printout(Object str){
		if(DEBUG){
			System.out.println("!debug------"+str.toString());
		}
	}
}