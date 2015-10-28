package jp.halfmoon.inaba.aiwolf.condition;

import java.util.ArrayList;

import jp.halfmoon.inaba.aiwolf.lib.WolfsidePattern;


/**
 * 条件を表す抽象クラス
 */
public abstract class AbstractCondition {


	/**
	 * 条件を満たすか
	 * @return
	 */
	abstract public boolean isValid( WolfsidePattern pattern );


	/**
	 * 対象となるエージェントの番号一覧を取得する
	 * @return
	 */
	abstract public ArrayList<Integer> getTargetAgentNo();


}
