package org.aiwolf.laern.lib;

import java.util.HashMap;

import com.gmail.kajiwara009.Learning.ProfitSharing;
import com.gmail.kajiwara009.Learning.SelectStrategy;

public class LearningControler {
	
	private ProfitSharing ps = new ProfitSharing();
	
	//greedySelectの変数
	private float epsilon;
	public static final float DEFAULT_EPSILON = 0.05f;
	
	//softmaxの変数
	private float temperature;
	public static final float DEFAULT_TEMPERATURE = 0.1f;
	
	public enum Strategy{
		RANDOM, ROULET, SOFTMAX, GREEDY, MAX;
	}
	
	private Strategy strategy;

	public LearningControler() {
		epsilon = DEFAULT_EPSILON;
		temperature = DEFAULT_TEMPERATURE;
		strategy = Strategy.RANDOM;
		// TODO 自動生成されたコンストラクター・スタブ
	}

	
	
	
	
	
	
	
	
	
	
	
	
	public float getEpsilon() {
		return epsilon;
	}

	public void setEpsilon(float epsilon) {
		this.epsilon = epsilon;
	}

	public float getTemperature() {
		return temperature;
	}

	public void setTemperature(float temperature) {
		this.temperature = temperature;
	}

	public Strategy getStrategy() {
		return strategy;
	}

	public void setStrategy(Strategy strategy) {
		this.strategy = strategy;
	}

	public static float getDefaultEpsilon() {
		return DEFAULT_EPSILON;
	}

	public static float getDefaultTemperature() {
		return DEFAULT_TEMPERATURE;
	}













	public ProfitSharing getPs() {
		return ps;
	}













	public void setPs(ProfitSharing ps) {
		this.ps = ps;
	}
	
	
}
