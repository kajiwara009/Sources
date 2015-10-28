package org.aiwolf.Satsuki.reinforcementLearning;

public class ReinforcementLearning {
	private static double	alpha = 0.6, //学習率
							ganma = 0.9; //割引率

	public static double reInforcementLearn(double qVal, double reward, double nextMaxQVal)
	{
		return (1.0 - alpha) * qVal + alpha * (reward + ganma * nextMaxQVal);
	}

	public static double getAlpha() {
		return alpha;
	}

	public static void setAlpha(double alpha) {
		ReinforcementLearning.alpha = alpha;
	}

	public static double getGanma() {
		return ganma;
	}

	public static void setGanma(double ganma) {
		ReinforcementLearning.ganma = ganma;
	}



}
