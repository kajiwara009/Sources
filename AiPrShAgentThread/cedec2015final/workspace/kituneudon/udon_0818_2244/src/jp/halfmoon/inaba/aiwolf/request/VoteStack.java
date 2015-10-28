package jp.halfmoon.inaba.aiwolf.request;

import java.util.ArrayList;

import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.net.GameInfo;


/**
 * 行動戦術「票重ね」
 * 本来想定する動きは、通る可能性がある吊りを提案すること。
 */
public final class VoteStack extends AbstractActionStrategy {

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
			// 自分以外の投票宣言をカウントする
			if( i != gameInfo.getAgent().getAgentIdx() && voteTarget[i] != null ){
				voteReceiveNum[voteTarget[i]]++;
			}
		}

		// 投票数に応じて投票要求度を上げる
		for( int i = 1; i < voteReceiveNum.length; i++ ){
			workReq = new Request(i);
			workReq.vote = 1.0 + voteReceiveNum[i] * 0.02 * (1 + voteReceiveNum[gameInfo.getAgent().getAgentIdx()] * 0.05);
			Requests.add(workReq);
		}


		// 自分が狂人時の処理
		if( args.agi.latestGameInfo.getRole() == Role.POSSESSED ){
			for( Agent agent : gameInfo.getAliveAgentList() ){
				// 自分狂視点で確黒か
				if( args.agi.selfRealRoleViewInfo.isFixBlack(agent.getAgentIdx()) ){
					// 投票先を宣言しているか
					if( voteTarget[agent.getAgentIdx()] != null ){
						// 狼様と同じ場所に投票する
						workReq = new Request(voteTarget[agent.getAgentIdx()]);
						workReq.vote = 1.2;
						Requests.add(workReq);
					}
				}
			}
		}


		return Requests;

	}

}
