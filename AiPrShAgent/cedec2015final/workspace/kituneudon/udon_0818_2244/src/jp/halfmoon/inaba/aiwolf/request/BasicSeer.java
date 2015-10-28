package jp.halfmoon.inaba.aiwolf.request;

import java.util.ArrayList;

import org.aiwolf.common.data.Agent;
import org.aiwolf.common.net.GameInfo;


/**
 * 行動戦術「基本占戦術」
 */
public final class BasicSeer extends AbstractActionStrategy {

	@Override
	public ArrayList<Request> getRequests(ActionStrategyArgs args) {

		GameInfo gameInfo = args.agi.latestGameInfo;

		ArrayList<Request> Requests = new ArrayList<Request>();
		Request workReq;



		// エージェント毎の投票予告先を取得する
		Integer[] voteTarget = new Integer[args.agi.gameSetting.getPlayerNum() + 1];
		for( Agent agent : gameInfo.getAliveAgentList() ){
			voteTarget[agent.getAgentIdx()] = args.agi.getSaidVoteAgent(agent.getAgentIdx());
		}

		// エージェント毎の被投票数を取得する
		int[] voteReceiveNum = new int[args.agi.gameSetting.getPlayerNum() + 1];
		for( int i = 1; i < voteTarget.length; i++ ){
			if( voteTarget[i] != null ){
				voteReceiveNum[voteTarget[i]]++;
			}
		}

		// 最多票のエージェントの票数を取得する
		int maxVoteCount = 0;
		for( int i = 1; i < voteTarget.length; i++ ){
			if( voteReceiveNum[i] > maxVoteCount ){
				maxVoteCount = voteReceiveNum[i];
			}
		}

		// 最多票を得ているエージェントは占い先から除外する
		for( int i = 1; i < voteReceiveNum.length; i++ ){
			if( voteReceiveNum[i] >= maxVoteCount ){
				workReq = new Request(i);
				workReq.inspect = 0.05;
				Requests.add(workReq);
			}
		}


		return Requests;
	}

}
