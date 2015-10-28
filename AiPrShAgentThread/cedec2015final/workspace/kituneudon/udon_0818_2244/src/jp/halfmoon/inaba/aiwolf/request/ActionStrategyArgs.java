package jp.halfmoon.inaba.aiwolf.request;

import jp.halfmoon.inaba.aiwolf.guess.AnalysisOfGuess;
import jp.halfmoon.inaba.aiwolf.lib.AdvanceGameInfo;
import jp.halfmoon.inaba.aiwolf.lib.ViewpointInfo;

/**
 * 行動戦術への引数クラス
 */
public final class ActionStrategyArgs {

	/** 整理情報 */
	public AdvanceGameInfo agi;

	/** 視点情報 */
	public ViewpointInfo view;

	/** 推理情報 */
	public AnalysisOfGuess aguess;


}
