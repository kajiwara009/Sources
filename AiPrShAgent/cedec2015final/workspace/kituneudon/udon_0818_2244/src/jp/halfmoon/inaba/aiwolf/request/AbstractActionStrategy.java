package jp.halfmoon.inaba.aiwolf.request;


import java.util.ArrayList;


/**
 * 行動戦術を表す抽象クラス。
 */
public abstract class AbstractActionStrategy {

	/**
	 * 要求する行動のリストを取得する。
	 * @param args 引数セット
	 * @return 要求する行動のリスト
	 */
	public abstract ArrayList<Request> getRequests(ActionStrategyArgs args);


}
