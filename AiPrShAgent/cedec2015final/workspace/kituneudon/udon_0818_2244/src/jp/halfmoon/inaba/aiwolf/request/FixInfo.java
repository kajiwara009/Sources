package jp.halfmoon.inaba.aiwolf.request;

import java.util.ArrayList;

import jp.halfmoon.inaba.aiwolf.lib.Judge;

import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Status;
import org.aiwolf.common.net.GameInfo;


/**
 * 確定情報から行動要求を行うクラス
 */
public final class FixInfo extends AbstractActionStrategy {

	@Override
	public ArrayList<Request> getRequests(ActionStrategyArgs args) {

		GameInfo gameInfo = args.agi.latestGameInfo;

		ArrayList<Request> Requests = new ArrayList<Request>();
		Request workReq;


		// 死亡済の者は各行動の対象にしない
		for( Agent agent : gameInfo.getAgentList() ){
			Status status = gameInfo.getStatusMap().get(agent);
			if( status == Status.DEAD ){
				workReq = new Request( agent.getAgentIdx() );
				workReq.inspect = 0.0;
				workReq.guard = 0.0;
				workReq.vote =  0.0;
				workReq.attack = 0.0;
				Requests.add(workReq);
			}
		}

		// 人狼は襲撃対象にしない
		for( int agent : args.agi.getWolfList() ){
			workReq = new Request(agent);
			workReq.attack = 0.0001;
			Requests.add(workReq);
		}

		// 自分が一度占った者は占の対象にしない
		for( Judge judge : args.agi.selfInspectList ){
			workReq = new Request( judge.targetAgentNo );
			workReq.inspect = 0.0001;
			Requests.add(workReq);
		}

		// 自分は各行動の対象にしない
		workReq = new Request( gameInfo.getAgent().getAgentIdx() );
		workReq.inspect = 0.0001;
		workReq.guard = 0.0;
		workReq.vote =  0.0;
		workReq.attack = 0.0;
		Requests.add(workReq);

		return Requests;

	}

}
