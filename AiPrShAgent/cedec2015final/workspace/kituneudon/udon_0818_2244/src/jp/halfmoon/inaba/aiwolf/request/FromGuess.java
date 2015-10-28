package jp.halfmoon.inaba.aiwolf.request;

import java.util.ArrayList;

import org.aiwolf.common.data.Role;
import org.aiwolf.common.net.GameInfo;

import jp.halfmoon.inaba.aiwolf.guess.InspectedWolfsidePattern;


/**
 * 行動戦術「推理からの行動」
 */
public final class FromGuess extends AbstractActionStrategy {

	@Override
	public ArrayList<Request> getRequests(ActionStrategyArgs args) {

		ArrayList<Request> Requests = new ArrayList<Request>();
		Request workReq;


		// 単体考察と陣営考察の重みを決める（序盤は半々程度で、終盤はほぼ陣営考察のみにする）
		double singleScoreWeight = Math.min( 0.01, 0.5 - args.agi.latestGameInfo.getDay() * 0.1 );
		double teamScoreWeight = Math.max( 0.99, 0.5 + args.agi.latestGameInfo.getDay() * 0.1 );


		// 各エージェントの 単体/陣営最大 の 狼/狂人 スコアに応じて要求を変える
		for( int i = 1; i <= args.agi.gameSetting.getPlayerNum(); i++ ){

			// 単体狼パターンを取得
			InspectedWolfsidePattern singleWolfPattern = args.aguess.getSingleWolfPattern(i);
			// 狼として最も妥当なパターンを取得
			InspectedWolfsidePattern mostWolfPattern = args.aguess.getMostValidWolfPattern(i);

			if( mostWolfPattern != null ){
				// 狼の可能性がある
				workReq = new Request( i );
				workReq.vote = Math.pow(singleWolfPattern.score, singleScoreWeight) * Math.pow(mostWolfPattern.score, teamScoreWeight) + 0.0001;
				workReq.inspect = Math.pow( workReq.vote, 0.4 );
				workReq.guard = 1 / workReq.vote;

				// 行動要求の登録
				Requests.add(workReq);

			}else{

				// 単体狂人パターンを取得
				InspectedWolfsidePattern singlePosPattern = args.aguess.getSinglePossessedPattern(i);
				// 狂人として最も妥当なパターンを取得
				InspectedWolfsidePattern mostPosPattern = args.aguess.getMostValidPossessedPattern(i);

				if( mostPosPattern != null ){
					// 確定非狼だが、狂人の可能性はある

					// 占霊以外か
					if( args.agi.agentState[i].comingOutRole == null ||
						(args.agi.agentState[i].comingOutRole != Role.SEER && args.agi.agentState[i].comingOutRole != Role.MEDIUM ) ){
						workReq = new Request( i );
						workReq.vote = Math.pow(singlePosPattern.score, singleScoreWeight) * Math.pow(mostPosPattern.score, teamScoreWeight) * 0.4 + 0.0001;
						workReq.guard = 1 / workReq.vote;
						workReq.inspect = 0.0001;
					}else{
						workReq = new Request( i );
						workReq.vote = Math.pow(singlePosPattern.score, singleScoreWeight) * Math.pow(mostPosPattern.score, teamScoreWeight) * 0.8 + 0.0001;
						workReq.guard = 1 / workReq.vote;
						workReq.inspect = 0.0001;
					}

					// 行動要求の登録
					Requests.add(workReq);

				}else{
					// 確定村側
					workReq = new Request( i );
					workReq.vote = 0.01;
					workReq.guard = 1.2;
					workReq.inspect = 0.0001;

					// 行動要求の登録
					Requests.add(workReq);
				}
			}
		}


		// 確定黒かどうか
		for( int i = 1; i <= args.agi.gameSetting.getPlayerNum(); i++ ){
			if( args.agi.selfViewInfo.isFixBlack(i) ){
				// 自分視点で確定狼
				workReq = new Request( i );
				workReq.vote = 1.2;
				workReq.guard = 0.0001;
				workReq.inspect = 0.0001;
			}else if( args.agi.selfViewInfo.isFixWolfSide(i) ){
				// 自分視点で確定人外
				workReq = new Request( i );
				workReq.vote = 1.15;
				workReq.guard = 0.0001;
				workReq.inspect = 0.0001;
			}
		}


//
//		// 最も妥当な狼陣営を取得
//		WolfsidePattern pattern = args.aguess.getMostValidPattern().pattern;
//
//
//		// 最も妥当な狼陣営の生存狼数
//		int aliveWolfNum = 0;
//
//		// 最も妥当な狼陣営の狼候補
//		for( int wolfAgentNo : pattern.wolfAgentNo ){
//
//			if( args.agi.agentState[wolfAgentNo].causeofDeath == CauseOfDeath.ALIVE ){
//				aliveWolfNum++;
//
//				workReq = new Request( wolfAgentNo );
//				workReq.vote = 1.1;
//				workReq.guard = 1 / workReq.vote;
//
//				// 行動要求の登録
//				Requests.add(workReq);
//			}
//
//		}
//
//		// 最も妥当な狼陣営の狂人候補
//		for( int posAgentNo : pattern.possessedAgentNo ){
//
//			workReq = new Request( posAgentNo );
//			// 縄余裕０なら狂人は放置（RPP突入済）
//			if( Common.getRestExecuteCount( gameInfo.getAliveAgentList().size() ) > aliveWolfNum ){
//				workReq.vote = 1.05;
//			}
//			workReq.guard = 1 / workReq.vote;
//
//			// 行動要求の登録
//			Requests.add(workReq);
//
//		}




		return Requests;

	}

}
