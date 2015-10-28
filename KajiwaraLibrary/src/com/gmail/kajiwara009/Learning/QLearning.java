package com.gmail.kajiwara009.Learning;

import java.util.List;

import com.gmail.kajiwara009.collectionOperation.CollectionOperator;

public class QLearning {
	private final static double DEFAULT_ALPHA = 0.9;
	private final static double DEFAULT_GANMA = 0.99;

	private double alpha; //学習率
	private double ganma; //割引率
	
	public QLearning() {
		alpha = DEFAULT_ALPHA;
		ganma = DEFAULT_GANMA;
	}
	
	public QLearning(double alpha, double ganma){
		this.alpha = alpha;
		this.ganma = ganma;
	}
	
	/**
	 * Q学習
	 * @param preValue 更新前Q値
	 * @param reward 報酬
	 * @param nextMaxValue 移行状態の最大Q値
	 * @return
	 */
	public double learn(double preValue, double reward, double nextMaxValue){
		double updateValue = (1.0 - alpha) * preValue + alpha * (reward + ganma * nextMaxValue);
		return updateValue;
	}
	
	/**
	 * Q学習
	 * @param preValue 更新前Q値
	 * @param reward 報酬
	 * @param nextValueList 移行状態の各行動のQ値List
	 * @return
	 */
	public double learn(double preValue, double reward, List<Double> nextValueList){
		double nextMaxValue =  CollectionOperator.getMaxValue(nextValueList);
		return learn(preValue, reward, nextMaxValue);
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	public double getAlpha() {
		return alpha;
	}

	public void setAlpha(double alpha) {
		this.alpha = alpha;
	}

	public double getGanma() {
		return ganma;
	}

	public void setGanma(double ganma) {
		this.ganma = ganma;
	}

}
