package jp.halfmoon.inaba.aiwolf.strategyplayer;

import org.aiwolf.client.lib.TemplateTalkFactory;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Role;


public class StrategyVillager extends AbstractBaseStrategyPlayer {


	@Override
	public String talk() {

		String workString;


		// 投票先を言っていない場合、新しい投票先を話す
		if( declaredPlanningVoteAgent == null ){
			// 投票先を変更する
			actionUI.voteAgent = planningVoteAgent;
			declaredPlanningVoteAgent = planningVoteAgent;

			// 発話
			String ret = TemplateTalkFactory.vote( Agent.getAgent(planningVoteAgent) );
			return ret;
		}

		// 疑い先を狼の人数以上言っていなければ話す
		if( agi.talkedSuspicionAgentList.size() < agi.gameSetting.getRoleNumMap().get(Role.WEREWOLF) ){
			// 疑い先を話す文章を取得し、取得できていれば話す
			workString = getSuspicionTalkString();
			if( workString != null ){
				return workString;
			}
		}

		// 投票先を変更する場合、新しい投票先を話す
		if( declaredPlanningVoteAgent != planningVoteAgent ){
			// 投票先を変更する
			actionUI.voteAgent = planningVoteAgent;
			declaredPlanningVoteAgent = planningVoteAgent;

			// 発話
			String ret = TemplateTalkFactory.vote( Agent.getAgent(planningVoteAgent) );
			return ret;
		}

		// 信用先を話す文章を取得し、取得できていれば話す
		workString = getTrustTalkString();
		if( workString != null ){
			return workString;
		}

		// 話す事が無い場合、overを返す
		return TemplateTalkFactory.over();

	}



}
