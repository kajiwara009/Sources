package jp.halfmoon.inaba.aiwolf.lib;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Vote;

/**
 * 共通関数
 */
public final class Common {

	/**
	 * コンストラクタ(インスタンス化禁止)
	 */
	private Common(){}



	/**
	 * 残り処刑回数の取得（最終日まで続く前提）
	 * @param aliveAgentNum 生存エージェント数
	 * @return 残り処刑回数
	 */
	public static int getRestExecuteCount(int aliveAgentNum){

		// (生存者数 - 1) / 2 （小数切捨て）が残り処刑数
		return (aliveAgentNum - 1) / 2;

	}


	/**
	 * エージェント番号のリストを取得する
	 * @param agents
	 * @return
	 */
	public static List<Integer> getAgentNo(List<Agent> agents){

		List<Integer> ret = new ArrayList<Integer>();

		for( Agent agent : agents ){
			ret.add(agent.getAgentIdx());
		}

		return ret;

	}


	/**
	 * ２つの発言の時系列を取得する
	 * @param day1 発言１の日
	 * @param talkid1 発言１の発言ID
	 * @param day2 発言２の日
	 * @param talkid2 発言２の発言ID
	 * @return -1:発言1が先　0:同じ　1:発言2が先
	 */
	public static int compareTalkID( int day1, int talkid1, int day2, int talkid2 ){

		// 発言１が先か
		if( day1 < day2 || ( day1 == day2 && talkid1 < talkid2 ) ){
			return -1;
		}

		// 発言２が先か
		if( day1 > day2 || ( day1 == day2 && talkid1 > talkid2 ) ){
			return 1;
		}

		// 同じ
		return 0;

	}


	/**
	 * 最多票を得た者のNoを取得する（同票時は複数取得）
	 * @param voteList 投票結果
	 * @return
	 */
	public static List<Integer> getMaxVoteAgentNo(List<Vote> voteList){

		// エージェント毎の得票数を取得するマップ key=AgentNo value=票数
		HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();

		// 得票数をカウント
		for( Vote vote : voteList ){
			int target = vote.getTarget().getAgentIdx();

			if( map.containsKey(target) ){
				map.put(target, map.get(target) + 1);
			}else{
				map.put(target, 1);
			}
		}

		// 最大の得票数を取得
		int maxVoteCount = 0;
		for( Entry<Integer, Integer> entry : map.entrySet() ){
			if( entry.getValue() > maxVoteCount ){
				maxVoteCount = entry.getValue();
			}
		}

		// 最大の得票数のエージェントをリスト化する
		List<Integer> ret = new ArrayList<Integer>();
		for( Entry<Integer, Integer> entry : map.entrySet() ){
			if( entry.getValue() == maxVoteCount ){
				ret.add( entry.getKey() );
			}
		}

		return ret;

	}


}
