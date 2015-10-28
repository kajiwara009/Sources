package jp.halfmoon.inaba.aiwolf.strategyplayer;

import java.util.ArrayList;
import java.util.List;

import jp.halfmoon.inaba.aiwolf.lib.AgentState;
import jp.halfmoon.inaba.aiwolf.lib.CauseOfDeath;
import jp.halfmoon.inaba.aiwolf.lib.Judge;
import jp.halfmoon.inaba.aiwolf.lib.ViewpointInfo;

import org.aiwolf.client.lib.TemplateTalkFactory;
import org.aiwolf.client.lib.TemplateTalkFactory.TalkType;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Species;
import org.aiwolf.common.net.GameInfo;


public class StrategyPossessed extends AbstractBaseStrategyPlayer {


	@Override
	public void dayStart() {

		super.dayStart();

		// 騙り判定の追加
		if( agi.latestGameInfo.getDay() > 0 ){
			addFakeSeerJudge();
		}

	}

	@Override
	public String talk() {

		// PP時の発言
		if( agi.isEnablePowerPlay_Possessed() ){
			// 投票先を変更する
			actionUI.voteAgent = planningVoteAgent;
			declaredPlanningVoteAgent = planningVoteAgent;

			// 発話
			String ret = TemplateTalkFactory.vote( Agent.getAgent(planningVoteAgent) );
			return ret;
		}

		// 未COの場合
		if( !isCameOut ){
			isCameOut = true;

			String ret = TemplateTalkFactory.comingout( getMe(), agi.fakeRole );
			return ret;
		}

		// CO済の場合
		if( isCameOut ){

			// 未報告の結果を報告する
			if( agi.reportSelfResultCount < agi.selfInspectList.size() ){

				Judge reportJudge = agi.selfInspectList.get( agi.selfInspectList.size() - 1 );

				// 報告済みの件数を増やす
				agi.reportSelfResultCount++;

				// 発話
				String ret = TemplateTalkFactory.divined( Agent.getAgent(reportJudge.targetAgentNo), reportJudge.result );
				return ret;
			}

		}


//		// 疑い先を狼の人数以上言っていなければ話す
//		if( agi.talkedSuspicionAgentList.size() < agi.gameSetting.getRoleNumMap().get(Role.WEREWOLF) ){
//			// 疑い先を話す文章を取得し、取得できていれば話す
//			workString = getSuspicionTalkString();
//			if( workString != null ){
//				return workString;
//			}
//		}

		// 投票先を変更する場合、新しい投票先を話す
		if( declaredPlanningVoteAgent != planningVoteAgent ){
			// 投票先を変更する
			actionUI.voteAgent = planningVoteAgent;
			declaredPlanningVoteAgent = planningVoteAgent;

			// 発話
			String ret = TemplateTalkFactory.vote( Agent.getAgent(planningVoteAgent) );
			return ret;
		}

		// 話す事が無い場合、overを返す
		return TemplateTalkFactory.over();

	}


	/**
	 * 占い判定を追加する
	 */
	private void addFakeSeerJudge(){

		GameInfo gameInfo = agi.latestGameInfo;

		// 判定先・判定結果の仮設定
		int inspectAgentNo = latestRequest.getMaxInspectRequest().agentNo;
		Species result = Species.HUMAN;

		// 襲撃されたエージェントの取得
		Agent attackedAgent = agi.latestGameInfo.getAttackedAgent();


		// 破綻時
		if( agi.selfViewInfo.wolfsidePatterns.isEmpty() ){
			// 生きている狂視点の確白を探す
			List<Integer> whiteList = new ArrayList<Integer>();
			for( int i = 1; i <= agi.gameSetting.getPlayerNum(); i++ ){
				if( agi.agentState[i].causeofDeath == CauseOfDeath.ALIVE && agi.selfRealRoleViewInfo.isFixWhite(i) ){
					whiteList.add(i);
				}
			}
			// 確白に黒出し
			if( !whiteList.isEmpty() ){
				inspectAgentNo = whiteList.get(0);
				result = Species.WEREWOLF;

				Judge newJudge = new Judge( getMe().getAgentIdx(),
                        inspectAgentNo,
                        result,
                        null );

				agi.addFakeSeerJudge(newJudge);
				return;
			}

		}

		//TODO 一旦これで勝率見て判断
//		// 初回占は黒出しで狂アピ(4CO以下の場合)
//		List<Integer> seers = agi.getEnableCOAgentNo(Role.SEER);
//		List<Integer> mediums = agi.getEnableCOAgentNo(Role.MEDIUM);
//		if( agi.latestGameInfo.getDay() == 1 && seers.size() + mediums.size() <= 4 ){
//			result = Species.WEREWOLF;
//		}

		// 白を出した場合の視点を仮定する
		ViewpointInfo future = new ViewpointInfo(agi.selfViewInfo);
		future.removeWolfPattern(inspectAgentNo);

		// 白出しで内訳が破綻する場合、黒出しを行う
		if( future.wolfsidePatterns.isEmpty() ){
			result = Species.WEREWOLF;
		}

		// 占おうとした先が噛まれた
		if( attackedAgent != null && attackedAgent.getAgentIdx() == inspectAgentNo ){
			// 噛み先には人間判定を出す
			result = Species.HUMAN;
		}

		// 生存者５名以下（村を１人吊ればPP確定）
		if( gameInfo.getAliveAgentList().size() <= 5 ){

			// 生きている確白を探す
			List<Integer> whiteList = new ArrayList<Integer>();
			for( int i = 1; i <= agi.gameSetting.getPlayerNum(); i++ ){
				if( agi.agentState[i].causeofDeath == CauseOfDeath.ALIVE && agi.selfRealRoleViewInfo.isFixWhite(i) ){
					whiteList.add(i);
				}
			}

			// 自分狂視点の確白に黒を出す
			for( Integer white : whiteList ){
				// 自分占視点で色が確定している場合は占わない
				if( agi.selfViewInfo.isFixWhite(white) ||
				    agi.selfViewInfo.isFixBlack(white) ){
					continue;
				}

				// 黒を出した場合の視点を仮定する
				future = new ViewpointInfo(agi.selfViewInfo);
				future.removePatternFromJudge( getMe().getAgentIdx(), white, Species.WEREWOLF );

				// 黒を出して破綻しないならそこに黒を出す
				if( !future.wolfsidePatterns.isEmpty() ){
					inspectAgentNo = white;
					result = Species.WEREWOLF;
					break;
				}
			}

		}

		Judge newJudge = new Judge( getMe().getAgentIdx(),
		                            inspectAgentNo,
		                            result,
		                            null );

		agi.addFakeSeerJudge(newJudge);

	}



}
