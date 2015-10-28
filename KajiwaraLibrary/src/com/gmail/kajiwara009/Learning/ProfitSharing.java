package com.gmail.kajiwara009.Learning;

public class ProfitSharing {
	private static final double DEFAULT_ALPHA = 0.1;
	
	private double alpha;

	public ProfitSharing() {
		alpha = DEFAULT_ALPHA;
	}
	
	public ProfitSharing(double alpha){
		this.alpha = alpha;
	}
	
	/**
	 * @param preValue
	 * @param reward 報酬関数で求めた値
	 * @return
	 */
	public double learn(double preValue, double reward){
		double value = (1.0 - alpha) * preValue + alpha * reward;
		return value;
	}

	public float learn(float preValue, float reward){
		float alphaF = (float) alpha;
		float value = (1.0f - alphaF) * preValue + alphaF * reward;
		return value;
	}
	
	
	
	
	
	
	public double getAlpha() {
		return alpha;
	}

	public void setAlpha(double alpha) {
		this.alpha = alpha;
	}

}
