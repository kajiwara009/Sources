package jp.halfmoon.inaba.aiwolf.request;

import java.util.ArrayList;
import java.util.List;

import jp.halfmoon.inaba.aiwolf.lib.CauseOfDeath;
import jp.halfmoon.inaba.aiwolf.lib.Common;
import jp.halfmoon.inaba.aiwolf.lib.Judge;
import jp.halfmoon.inaba.aiwolf.lib.ViewpointInfo;

import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Species;
import org.aiwolf.common.net.GameInfo;


/**
 * 行動戦術「基本襲撃戦術」
 */
public final class BasicAttack extends AbstractActionStrategy {

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
			if( voteTarget[i] != null && args.agi.isValidAgentNo(i) ){
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


		// 得ている票数に応じて襲撃を薄くする
		for( int i = 1; i < voteReceiveNum.length; i++ ){
			workReq = new Request(i);
			workReq.attack = Math.max( 1.00 - voteReceiveNum[i] * 0.08, 0.0 );
			Requests.add(workReq);
		}

		// 最多票を得ているエージェントは襲撃先から除外する
		for( int i = 1; i < voteReceiveNum.length; i++ ){
			if( voteReceiveNum[i] >= maxVoteCount ){
				workReq = new Request(i);
				workReq.attack = 0.05;
				Requests.add(workReq);
			}
		}


		// 各役職のCO者を取得
		List<Integer> seers = args.agi.getEnableCOAgentNo(Role.SEER);
		List<Integer> mediums = args.agi.getEnableCOAgentNo(Role.MEDIUM);
		List<Integer> bodyguards = args.agi.getEnableCOAgentNo(Role.BODYGUARD);
		List<Integer> villagers = args.agi.getEnableCOAgentNo(Role.VILLAGER);


		//TODO 100msじゃ時間足りないので無理です。諦める？
//		// 各エージェントを噛んだ場合をシミュレート
//		if( args.agi.latestGameInfo.getDay() > 0 ){
//
//			for( int i = 1; i < args.agi.gameSetting.getPlayerNum(); i++ ){
//
//				// 死亡済・狼は元々非対象なのでスキップ
//				if( args.agi.agentState[i].causeofDeath != CauseOfDeath.ALIVE ||
//				    args.agi.latestGameInfo.getRoleMap().get(Agent.getAgent(i)) == Role.WEREWOLF ){
//					continue;
//				}
//
//				// 噛んだ場合の自分村視点を仮定する
//				ViewpointInfo future;
//				future = new ViewpointInfo(args.agi.selfViewInfo);
//				future.removeWolfPattern(i);
//				if( future.wolfsidePatterns.isEmpty() ){
//					// 自分が破綻するので襲撃しない
//					workReq = new Request(i);
//					workReq.attack = 0.05;
//					Requests.add(workReq);
//				}
//
//
//				// 噛んだ場合の他人視点を仮定する
//				// 明日iを噛んだ視点を作成（全視点）
//				future = new ViewpointInfo(args.agi.allViewTrustInfo);
//				future.removeWolfPattern(i);
//
//				// 地上の狼候補数(今日の生存者数 - 噛み先含む確白の数)
//				int grayAndBlackNum = 0;
//				for( int j = 1; j < voteReceiveNum.length; j++ ){
//					// 噛み先でない かつ 生存 かつ 明日の確定白ではない
//					if( j != i && args.agi.agentState[j].causeofDeath == CauseOfDeath.ALIVE && !future.isFixWhite(j) ){
//						grayAndBlackNum++;
//					}
//				}
//
//				// 明日の残り処刑数を取得
//				int tomorrowRestExucuteNum = Common.getRestExecuteCount(args.agi.latestGameInfo.getAliveAgentList().size() - 2);
//
//				//TODO 吊り先がグレーかとか考える(現状は白カウントで計算)
//				//TODO 狂人の計算も入れる(現状は生存で計算)
//				//TODO 占い師を生かした場合は灰が狭まることも考える
//
//				// 詰みか（地上の狼候補の数が残り吊り数以下）
//				if( grayAndBlackNum <= tomorrowRestExucuteNum - 1 ){
//					// 詰むので襲撃しない
//					workReq = new Request(i);
//					workReq.attack = 0.05;
//					Requests.add(workReq);
//				}
//
//			}
//
//		}


		// 狂人が見えていたら襲撃しない
		for( Judge judge : args.agi.getSeerJudgeList() ){
			// 人間の占い師が間違った判定を出したか
			if( !args.agi.isWolf(judge.agentNo) &&
			    (judge.result == Species.WEREWOLF) != args.agi.isWolf(judge.targetAgentNo) ){
				// 襲撃要求を下げる
				workReq = new Request(judge.agentNo);
				workReq.attack = 0.5;
				Requests.add(workReq);
			}
		}


		// 初回襲撃
		if( args.agi.latestGameInfo.getDay() == 1 ){

			//TODO 狩CO者が吊れそうなら抑止力は気にしなくていい

			// 占0COなら霊は襲撃しない(灰の占狩を狙う)
			if( seers.isEmpty() ){
				for( int medium : mediums ){
					workReq = new Request( medium );
					workReq.attack = 0.1;
					Requests.add(workReq);
				}
			}

			// 占1COなら占は襲撃しない(護衛が予想されるため)
			if( seers.size() == 1 ){
				workReq = new Request( seers.get(0) );
				workReq.attack = 0.1;
				Requests.add(workReq);
			}

		}


		//TODO 狩CO者が吊れそうなら抑止力は気にしなくていい

		// GJが出ている場合
		if( args.agi.latestGameInfo.getDay() >= 2 && args.agi.latestGameInfo.getAttackedAgent() == null ){
			// 襲撃先の候補を得る
			List<Integer> maxVoteAgentList = Common.getMaxVoteAgentNo( args.agi.latestGameInfo.getAttackVoteList() );

			// 噛みと吊りの被りの可能性がない
			if( maxVoteAgentList.indexOf( args.agi.latestGameInfo.getExecutedAgent().getAgentIdx() ) == -1 ){
				// GJ位置は続けて襲撃しない
				for( Integer agent : maxVoteAgentList ){
					workReq = new Request( agent );
					workReq.attack = 0.2;
					Requests.add(workReq);
				}
				// 霊も襲撃しない
				for( int medium : mediums ){
					workReq = new Request( medium );
					workReq.attack = 0.2;
					Requests.add(workReq);
				}
//				// 占霊の襲撃要求を下げる
//				for( int seer : seers ){
//					workReq = new Request( seer );
//					workReq.attack = 0.1;
//					Requests.add(workReq);
//				}
//				for( int medium : mediums ){
//					workReq = new Request( medium );
//					workReq.attack = 0.1;
//					Requests.add(workReq);
//				}
			}
		}


		return Requests;
	}

}
