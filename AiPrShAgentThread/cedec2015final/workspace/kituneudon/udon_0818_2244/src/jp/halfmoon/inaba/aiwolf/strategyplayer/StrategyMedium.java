package jp.halfmoon.inaba.aiwolf.strategyplayer;

import java.util.List;

import jp.halfmoon.inaba.aiwolf.lib.DayInfo;
import jp.halfmoon.inaba.aiwolf.lib.Judge;

import org.aiwolf.client.lib.TemplateTalkFactory;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Role;


public class StrategyMedium extends AbstractBaseStrategyPlayer {


	@Override
	public String talk() {

		String workString;

		// 未COの場合
		if( !isCameOut ){

//			// 回避COが必要なら回避COする
//			if( isAvoidance() ){
//				isCameOut = true;
//
//				// 発話
//				workString = TemplateTalkFactory.comingout(getMe(), Role.MEDIUM);
//				return workString;
//			}
//
//			// 自発COが必要ならCOする
//			if( isVoluntaryComingOut() ){
//				isCameOut = true;
//
//				// 発話
//				workString = TemplateTalkFactory.comingout(getMe(), Role.MEDIUM);
//				return workString;
//			}

			// COする
			isCameOut = true;

			// 発話
			workString = TemplateTalkFactory.comingout(getMe(), Role.MEDIUM);
			return workString;
		}

		// CO済の場合
		if( isCameOut ){

			// 未報告の結果を報告する
			if( agi.reportSelfResultCount < agi.selfInquestList.size() ){

				Judge reportJudge = agi.selfInquestList.get( agi.selfInquestList.size() - 1 );

				// 報告済みの件数を増やす
				agi.reportSelfResultCount++;

				// 発話
				workString = TemplateTalkFactory.inquested( Agent.getAgent(reportJudge.targetAgentNo), reportJudge.result );
				return workString;
			}

		}

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
			workString = TemplateTalkFactory.vote( Agent.getAgent(planningVoteAgent) );
			return workString;
		}

		// 信用先を話す文章を取得し、取得できていれば話す
		workString = getTrustTalkString();
		if( workString != null ){
			return workString;
		}

		// 話す事が無い場合、overを返す
		return TemplateTalkFactory.over();

	}


	/**
	 * 自発的霊COするか
	 * @return
	 */
	private boolean isVoluntaryComingOut(){

		// 各役職のCO者を取得
		List<Integer> seers = agi.getEnableCOAgentNo(Role.SEER);
		List<Integer> mediums = agi.getEnableCOAgentNo(Role.MEDIUM);
		List<Integer> bodyguards = agi.getEnableCOAgentNo(Role.BODYGUARD);

		// 2日目
		if( getDay() >= 2 ){
			// COする
			return true;
		}

		// 霊騙りが存在
		if( !mediums.isEmpty() ){
			// COする
			return true;
		}

		// 全露出
		if( seers.size() >= 5 ||
		    bodyguards.size() >= 5 ||
		    seers.size() + bodyguards.size() >= 6 ){
			// COする
			return true;
		}

		// 占われた（黒ならCCO、白なら噛まれやすいので乗っ取り防止）
		for( Judge judge : agi.getSeerJudgeList() ){
			if( judge.isEnable() && judge.targetAgentNo == getMe().getAgentIdx() ){
				// COする
				return true;
			}
		}

		return false;

	}

}
