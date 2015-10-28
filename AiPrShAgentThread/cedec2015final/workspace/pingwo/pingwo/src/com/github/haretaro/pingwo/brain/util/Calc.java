package com.github.haretaro.pingwo.brain.util;

public class Calc {
	public static double sigmoid(double x){
		return (1d + Math.tanh(x))/2;
	}
}