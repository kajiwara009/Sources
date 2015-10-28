package jp.halfmoon.inaba.aiwolf.guess;


import jp.halfmoon.inaba.aiwolf.condition.AbstractCondition;


/**
 * 推理を表すクラス
 */
public final class Guess {

	/** 条件 */
	public AbstractCondition condition;

	/** 係数(conditionを満たす可能性をcorrelation倍する) */
	public double correlation = 1.0;


}
