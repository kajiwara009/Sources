package jp.halfmoon.inaba.aiwolf.lib;

import org.aiwolf.common.data.Role;

import jp.halfmoon.inaba.aiwolf.lib.CauseOfDeath;


/**
 * エージェントの状態を表すクラス
 */
public final class AgentState {


	/** エージェント番号 */
	public final int agentNo;

	/** 現在有効なCO(未CO時はNull) */
	public Role comingOutRole;

	/** 死亡日（死体が発見された日） 生存時はNull */
	public Integer deathDay;

	/** 死因　正存時はALIVE */
	public CauseOfDeath causeofDeath;


	/**
	 * コンストラクタ
	 * @param agentno
	 */
	public AgentState(int agentno){
		this.agentNo = agentno;
		this.comingOutRole = null;
		this.deathDay = null;
		this.causeofDeath = CauseOfDeath.ALIVE;
	}




}
