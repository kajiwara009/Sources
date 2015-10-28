package jp.halfmoon.inaba.aiwolf.request;

import java.util.ArrayList;
import java.util.List;

import jp.halfmoon.inaba.aiwolf.guess.InspectedWolfsidePattern;
import jp.halfmoon.inaba.aiwolf.lib.Judge;

import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Species;
import org.aiwolf.common.net.GameInfo;


/**
 * 行動戦術「基本狩戦術」
 */
public final class BasicGuard extends AbstractActionStrategy {

	@Override
	public ArrayList<Request> getRequests(ActionStrategyArgs args) {

		GameInfo gameInfo = args.agi.latestGameInfo;

		ArrayList<Request> Requests = new ArrayList<Request>();
		Request workReq;



		List<Integer> seers = args.agi.getEnableCOAgentNo(Role.SEER);
		List<Integer> mediums = args.agi.getEnableCOAgentNo(Role.MEDIUM);

		// 偽パターンの最大スコアが最小のものを求める
		double minScore = Double.MAX_VALUE;
		for( int seer : seers ){
			InspectedWolfsidePattern wolfPattern = args.aguess.getMostValidWolfPattern(seer);
			InspectedWolfsidePattern posPattern = args.aguess.getMostValidPossessedPattern(seer);

			double score = ( wolfPattern != null ? wolfPattern.score : 0.0 ) + ( posPattern != null ? posPattern.score : 0.0 );

			minScore = Math.min(score, minScore);
		}

		// 偽スコアの差が大きい占を偽打ち扱い
		int falseCount = 0;
		for( int seer : seers ){
			InspectedWolfsidePattern wolfPattern = args.aguess.getMostValidWolfPattern(seer);
			InspectedWolfsidePattern posPattern = args.aguess.getMostValidPossessedPattern(seer);
			double score = ( wolfPattern != null ? wolfPattern.score : 0.0 ) + ( posPattern != null ? posPattern.score : 0.0 );

			if( score > minScore * 1.6 ){
				falseCount++;
			}
		}

		// １人除いて偽打ちか
		if( falseCount == seers.size() - 1 ){
			// 真打ちした占の護衛を厚くする
			for( int seer : seers ){
				InspectedWolfsidePattern wolfPattern = args.aguess.getMostValidWolfPattern(seer);
				InspectedWolfsidePattern posPattern = args.aguess.getMostValidPossessedPattern(seer);

				double score = ( wolfPattern != null ? wolfPattern.score : 0.0 ) + ( posPattern != null ? posPattern.score : 0.0 );

				if( Double.compare(score, minScore) == 0 ){
					workReq = new Request(seer);
					workReq.guard = 3.0;
					Requests.add(workReq);
				}else{
					workReq = new Request(seer);
					workReq.guard = 0.5;
					Requests.add(workReq);
				}
			}
		}


		//TODO 他編成対応・消去法で特定or不在が分かったパターンの対応(各占視点があれば、生存灰の全員に色がついているかで判断可能)
		// 仕事終了した占は護衛しない
		for( int seer : seers ){
			// 占・霊・それ以外の色が判明した人外数をカウント
			int seerEnemyCnt = seers.size() - 1;
			int mediumEnemyCnt = ( mediums.size() > 1 ) ? (mediums.size() - 1) : 0;
			int hitGrayBlackCnt = 0;
			for( Judge judge : args.agi.getSeerJudgeList() ){
				if( judge.isEnable() &&
				    judge.agentNo == seer &&
				    judge.result == Species.WEREWOLF ){
					// 相手が占霊以外か
					if( args.agi.agentState[judge.targetAgentNo].comingOutRole == null ||
						(args.agi.agentState[judge.targetAgentNo].comingOutRole != Role.SEER && args.agi.agentState[judge.targetAgentNo].comingOutRole != Role.MEDIUM ) ){
						hitGrayBlackCnt++;
					}
				}
			}

			if( seerEnemyCnt + mediumEnemyCnt + hitGrayBlackCnt >= 4 ){
				workReq = new Request(seer);
				workReq.guard = 0.001;
				Requests.add(workReq);
			}
		}



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


		// 得ている票数に応じて護衛を薄くする
		for( int i = 1; i < voteReceiveNum.length; i++ ){
			workReq = new Request(i);
			workReq.guard = 1.00 - voteReceiveNum[i] * 0.03;
			Requests.add(workReq);
		}

		// 最多票を得ているエージェントは護衛先から除外する
		for( int i = 1; i < voteReceiveNum.length; i++ ){
			if( voteReceiveNum[i] >= maxVoteCount ){
				workReq = new Request(i);
				workReq.guard = 0.01;
				Requests.add(workReq);
			}
		}


		return Requests;
	}

}
