package jp.halfmoon.inaba.aiwolf.strategyplayer;


/**
 * 行動を設定するための擬似UI
 */
public final class ActionUI {

	/** 投票先 */
	public Integer voteAgent;

	/** 襲撃先 */
	public Integer attackAgent;

	/** 占い先 */
	public Integer inspectAgent;

	/** 護衛先 */
	public Integer guardAgent;



	/**
	 * 行動設定をリセットする
	 */
	public void reset(){
		voteAgent = null;
		inspectAgent = null;
		attackAgent = null;
		guardAgent = null;
	}


}
