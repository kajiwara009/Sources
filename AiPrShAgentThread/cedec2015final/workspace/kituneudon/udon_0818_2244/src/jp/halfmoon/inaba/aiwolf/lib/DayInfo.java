package jp.halfmoon.inaba.aiwolf.lib;

import java.util.List;

import org.aiwolf.common.data.Agent;
import org.aiwolf.common.net.GameInfo;


/**
 * 日ごとの情報を表すクラス
 */
public final class DayInfo {

	/** 日数 */
	public final int day;

	/** 生存者一覧 */
	public final List<Agent> aliveAgentList;

	/** 処刑者のエージェント番号(発見された日) */
	public final Integer executeAgentNo;

	/** 被襲撃者のエージェント番号(発見された日) */
	public final Integer attackAgentNo;



	public DayInfo(GameInfo gameInfo){

		day = gameInfo.getDay();

		aliveAgentList = gameInfo.getAliveAgentList();

		if( gameInfo.getAttackedAgent() != null ){
			attackAgentNo = gameInfo.getAttackedAgent().getAgentIdx();
		}else{
			attackAgentNo = null;
		}

		if( gameInfo.getExecutedAgent() != null ){
			executeAgentNo = gameInfo.getExecutedAgent().getAgentIdx();
		}else{
			executeAgentNo = null;
		}

	}



}
