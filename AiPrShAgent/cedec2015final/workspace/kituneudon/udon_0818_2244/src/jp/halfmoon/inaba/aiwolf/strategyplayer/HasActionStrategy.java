package jp.halfmoon.inaba.aiwolf.strategyplayer;


import jp.halfmoon.inaba.aiwolf.request.AbstractActionStrategy;



/**
 * ÛL·és®íªðÇ·éNX
 */
public final class HasActionStrategy {

	/**
	 * s®íªNX
	 */
	public AbstractActionStrategy strategy;

	/**
	 * íªÌäd(es®ÌvWðWeightæ·é)
	 */
	public double weight;


	/**
	 * RXgN^
	 * @param strategy íªNX
	 * @param weight íªÌäd(ÊÌÖWðWeightæ·é)
	 */
	public HasActionStrategy(AbstractActionStrategy strategy, double weight){
		this.strategy = strategy;
		this.weight = weight;
	}


}
