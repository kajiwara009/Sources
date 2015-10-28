package com.gmail.yusatk.utils;

import java.util.Random;

public class LotHelper {
	static Random random = new Random();
	public static boolean chance(int rate) {
		int r = random.nextInt() % 100;
		if(r >= rate) {
			return true;
		}
		return false;
	}
}
