package com.example.pigdig.data;

import java.util.Random;

public class StaticMethods {
	public static int rouletSelect(double[] table){
		double sum = 0;
		for(double d: table){
			sum += d;
		}
		double randNum = new Random().nextDouble() * sum;
		for(int i = 0; i < table.length; i++){
			randNum -= table[i];
			if(randNum <= 0){
				return i;
			}
		}
		System.out.println("StaticMethodのrouletSelectおかしい");
		return -1;
	}

}
