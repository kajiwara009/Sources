package jp.halfmoon.inaba.aiwolf.lib;

import org.aiwolf.common.data.Species;
import org.aiwolf.common.data.Talk;


/**
 * 占霊判定を表すクラス
 */
public final class Judge {

	/** 判定を出した者のエージェントNo */
	public final int agentNo;

	/** 判定を出された者のエージェントNo */
	public final int targetAgentNo;

	/** 判定結果 */
	public final Species result;

	/** 判定を出した発言 */
	public final Talk talk;

	/** 判定を取り消した発言 */
	public Talk cancelTalk;


	/**
	 * コンストラクタ
	 * @param agentNo 判定を出した者のエージェントNo
	 * @param targetNo 判定を出された者のエージェントNo
	 * @param result 判定結果
	 * @param talk 判定を出した発言
	 */
	public Judge(int agentNo, int targetNo, Species result, Talk talk){
		this.agentNo = agentNo;
		this.targetAgentNo = targetNo;
		this.result = result;
		this.talk = talk;
	}


	/**
	 * 判定が有効な状態か
	 * @return
	 */
	public boolean isEnable(){

		if( cancelTalk != null ){
			return false;
		}

		return true;

	}


}
