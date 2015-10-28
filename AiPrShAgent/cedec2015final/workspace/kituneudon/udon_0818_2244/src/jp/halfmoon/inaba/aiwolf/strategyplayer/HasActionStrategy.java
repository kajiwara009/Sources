package jp.halfmoon.inaba.aiwolf.strategyplayer;


import jp.halfmoon.inaba.aiwolf.request.AbstractActionStrategy;



/**
 * 保有する行動戦略を管理するクラス
 */
public final class HasActionStrategy {

	/**
	 * 行動戦略クラス
	 */
	public AbstractActionStrategy strategy;

	/**
	 * 戦略の比重(各行動の要求係数をWeight乗する)
	 */
	public double weight;


	/**
	 * コンストラクタ
	 * @param strategy 戦略クラス
	 * @param weight 戦略の比重(推理結果の相関係数をWeight乗する)
	 */
	public HasActionStrategy(AbstractActionStrategy strategy, double weight){
		this.strategy = strategy;
		this.weight = weight;
	}


}
