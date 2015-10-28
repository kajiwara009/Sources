package jp.halfmoon.inaba.aiwolf.request;

import java.util.ArrayList;
import java.util.List;

import jp.halfmoon.inaba.aiwolf.guess.InspectedWolfsidePattern;
import jp.halfmoon.inaba.aiwolf.lib.ComingOut;


/**
 * 行動戦術「PP(狂人版)」
 */
public final class PowerPlay_Possessed extends AbstractActionStrategy {

	@Override
	public ArrayList<Request> getRequests(ActionStrategyArgs args) {

		ArrayList<Request> Requests = new ArrayList<Request>();
		Request workReq;


		// PP突入済でなければ空リストを返す
		if( !args.agi.isEnablePowerPlay_Possessed() ){
			return Requests;
		}



		// 票を合わせるエージェントを取得
		Integer mostWolfAgentNo = null;

		// 自分狂人視点の確定狼を取得（先頭１名）
		for( int i = 1; i < args.agi.gameSetting.getPlayerNum(); i++ ){
			if( args.agi.selfRealRoleViewInfo.isFixBlack(i) ){
				mostWolfAgentNo = i;
				break;
			}
		}

		// 確定狼がいない
		if( mostWolfAgentNo == null ){
			// 人外ＣＯした者がいる場合、最も早くＣＯした者に投票を合わせる
			for( ComingOut co :args.agi.wolfsideComingOutList ){
				if( co.isEnable() && !args.agi.selfRealRoleViewInfo.isFixWhite(co.agentNo) ){
					mostWolfAgentNo = co.agentNo;
					break;
				}
			}

		}

		//TODO 確定狼にあわせる（済）＞ＣＯ狼に合わせる（済）＞確定○に投票（済）＞誰かを１票にする？（未）＞推理の逆に投票（未）

		// 確定狼がいる場合、確定狼に投票を合わせる
		if( mostWolfAgentNo != null ){

			Integer target = args.agi.getSaidVoteAgent(mostWolfAgentNo);

			if( target != null && target != args.agi.latestGameInfo.getAgent().getAgentIdx() ){
				workReq = new Request( target );
				workReq.vote = 1000000.0;

				// 行動要求の登録
				Requests.add(workReq);

				return Requests;
			}
		}




		// 人間確定のエージェントを取得
		List<Integer> fixHumanAgentNo = new ArrayList<Integer>();

		// 自分狂人視点の確定人間を取得
		for( int i = 1; i < args.agi.gameSetting.getPlayerNum(); i++ ){
			if( args.agi.selfRealRoleViewInfo.isFixWhite(i) ){
				fixHumanAgentNo.add(i);
			}
		}

		// 確定人間がいる場合、確定人間に投票する
		if( !fixHumanAgentNo.isEmpty() ){
			for( int human : fixHumanAgentNo ){
				workReq = new Request( human );
				workReq.vote = 1000000.0;

				// 行動要求の登録
				Requests.add(workReq);
			}

			return Requests;
		}






		//TODO 自分狂人視点で推理しないとスコアが狂う（重ければ諦める？）

		// 単体考察と陣営考察の重みを決める（序盤は半々程度で、終盤はほぼ陣営考察のみにする）
		double singleScoreWeight = Math.min( 0.01, 0.5 - args.agi.latestGameInfo.getDay() * 0.1 );
		double teamScoreWeight = Math.max( 0.99, 0.5 + args.agi.latestGameInfo.getDay() * 0.1 );

		// 各エージェントの 単体/陣営最大 の 狼 スコアに応じて要求を変える
		for( int i = 1; i <= args.agi.gameSetting.getPlayerNum(); i++ ){

			// 単体狼パターンを取得
			InspectedWolfsidePattern singleWolfPattern = args.aguess.getSingleWolfPattern(i);
			// 狼として最も妥当なパターンを取得
			InspectedWolfsidePattern mostWolfPattern = args.aguess.getMostValidWolfPattern(i);

			if( mostWolfPattern != null ){
				// 狼の可能性がある
				workReq = new Request( i );
				workReq.vote = 1 / Math.pow(singleWolfPattern.score, singleScoreWeight) * Math.pow(mostWolfPattern.score, teamScoreWeight) * 100;

				// 行動要求の登録
				Requests.add(workReq);
			}

		}

		return Requests;

	}

}
