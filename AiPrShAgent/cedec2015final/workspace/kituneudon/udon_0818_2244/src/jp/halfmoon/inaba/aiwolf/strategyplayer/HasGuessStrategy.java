package jp.halfmoon.inaba.aiwolf.strategyplayer;


import jp.halfmoon.inaba.aiwolf.guess.AbstractGuessStrategy;



/**
 * ÛL·éíªðÇ·éNX
 */
public final class HasGuessStrategy {

	/**
	 * íªNX
	 */
	public AbstractGuessStrategy strategy;

	/**
	 * íªÌäd(ÊÌÖWðWeightæ·é)
	 */
	public double weight;


	/**
	 * RXgN^
	 * @param strategy íªNX
	 * @param weight íªÌäd(ÊÌÖWðWeightæ·é)
	 */
	public HasGuessStrategy(AbstractGuessStrategy strategy, double weight){
		this.strategy = strategy;
		this.weight = weight;
	}


}
