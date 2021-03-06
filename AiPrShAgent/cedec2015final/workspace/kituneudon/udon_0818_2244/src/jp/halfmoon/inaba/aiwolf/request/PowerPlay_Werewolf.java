package jp.halfmoon.inaba.aiwolf.request;

import java.util.ArrayList;

import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.net.GameInfo;


/**
 * 行動戦術「PP(人狼版)」
 */
public final class PowerPlay_Werewolf extends AbstractActionStrategy {

	@Override
	public ArrayList<Request> getRequests(ActionStrategyArgs args) {

		ArrayList<Request> Requests = new ArrayList<Request>();
		Request workReq;

		GameInfo gameInfo = args.agi.latestGameInfo;


		// PP突入済でなければ空リストを返す
		if( !args.agi.isEnablePowerPlay() ){
			return Requests;
		}


		// 人狼は投票対象にしない
		for( Agent agent : gameInfo.getAgentList() ){
			Role role = gameInfo.getRoleMap().get(agent);
			if( role == Role.WEREWOLF){
				workReq = new Request( agent.getAgentIdx() );
				workReq.vote = 0.0001;
				Requests.add(workReq);
			}
		}

		// 仲間が投票先を宣言済、かつ投票先が人間なら合わせる
		for( int agent : args.agi.getAliveWolfList() ){

			Integer target = args.agi.getSaidVoteAgent(agent);

			if( target != null && !args.agi.isWolf(target) ){
				workReq = new Request( target );
				workReq.vote = 1000000.0;

				// 行動要求の登録
				Requests.add(workReq);

				return Requests;
			}

		}



		return Requests;

	}

}
