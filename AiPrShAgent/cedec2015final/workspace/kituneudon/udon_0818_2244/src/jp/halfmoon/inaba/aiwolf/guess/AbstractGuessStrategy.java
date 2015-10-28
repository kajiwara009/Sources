package jp.halfmoon.inaba.aiwolf.guess;

import java.util.ArrayList;


/**
 * 推理戦術を表す抽象クラス
 */
public abstract class AbstractGuessStrategy {

	/**
	 * 推理のリストを取得する。
	 * @param args 引数セット
	 * @return 推理のリスト
	 */
	public abstract ArrayList<Guess> getGuessList(GuessStrategyArgs args);


}
