package jp.halfmoon.inaba.aiwolf.lib;

import org.aiwolf.common.data.Talk;


/**
 * 護衛履歴を表すクラス
 */
public final class GuardRecent {

	/** 護衛した者のエージェントNo */
	public final int agentNo;

	/** 護衛された者のエージェントNo */
	public final int targetAgentNo;

	/** 判定を出した発言 */
	public final Talk talk;

	/** 判定を取り消した発言 */
	public Talk cancelTalk;

	/** 実行した日(初回護衛=1) */
	public int execDay;


	/**
	 * コンストラクタ
	 * @param agentNo 判定を出した者のエージェントNo
	 * @param targetNo 判定を出された者のエージェントNo
	 * @param talk 判定を出した発言
	 */
	public GuardRecent(int agentNo, int targetNo, Talk talk){
		this.agentNo = agentNo;
		this.targetAgentNo = targetNo;
		this.talk = talk;
	}


	/**
	 * 護衛履歴が有効な状態か
	 * @return
	 */
	public boolean isEnable(){

		if( cancelTalk != null ){
			return false;
		}

		return true;

	}


}
