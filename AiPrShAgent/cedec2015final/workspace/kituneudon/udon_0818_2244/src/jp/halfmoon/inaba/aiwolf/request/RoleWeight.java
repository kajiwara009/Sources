package jp.halfmoon.inaba.aiwolf.request;

import java.util.ArrayList;
import java.util.List;

import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Species;
import org.aiwolf.common.net.GameInfo;

import jp.halfmoon.inaba.aiwolf.lib.CauseOfDeath;
import jp.halfmoon.inaba.aiwolf.lib.Judge;


/**
 * 行動戦術「役職の重み」
 */
public final class RoleWeight extends AbstractActionStrategy {

	@Override
	public ArrayList<Request> getRequests(ActionStrategyArgs args) {

		//TODO 縄余裕など、状況判断を掘り下げる（全体的に）
		//TODO 他編成対応

		GameInfo gameInfo = args.agi.latestGameInfo;

		ArrayList<Request> Requests = new ArrayList<Request>();
		Request workReq;



		// 役職が噛まれているかチェック
		boolean isAttackedSeer = false;
		boolean isAttackedMedium = false;
		for( int i = 1; i <= args.agi.gameSetting.getPlayerNum(); i++  ){
			if( args.agi.agentState[i].causeofDeath == CauseOfDeath.ATTACKED ){
				if( args.agi.agentState[i].comingOutRole == Role.SEER ){
					isAttackedSeer = true;
				}
				if( args.agi.agentState[i].comingOutRole == Role.MEDIUM ){
					isAttackedMedium = true;
				}
			}
		}


		// 黒貰い・白貰い
		List<Judge> seerJudges = args.agi.getSeerJudgeList();
		for( Judge judge : seerJudges ){

			// 無効な判定であればスキップ
			if( !judge.isEnable() ){
				continue;
			}

			// 被判定者が既に死んでいればスキップ
			if( args.agi.agentState[judge.targetAgentNo].causeofDeath != CauseOfDeath.ALIVE ){
				continue;
			}

			if( judge.result == Species.WEREWOLF){

				// 確定人外でないか
				if( !args.agi.selfViewInfo.isFixWolfSide(judge.agentNo) ){
					// 黒貰い
					workReq = new Request( judge.targetAgentNo );

					workReq.inspect = Math.min( 0.2 + gameInfo.getDay() * 0.15 , 0.8 );	// 日の経過に従い 0.35(1d)→0.50…0.80(4d) と0.80まで上昇
					workReq.guard = Math.min( 0.1 + gameInfo.getDay() * 0.02 , 0.8 );	// 日の経過に従い 0.12(1d)→0.14…0.20(4d) と0.20まで上昇
					workReq.vote = Math.max( 3.8 - gameInfo.getDay() * 0.7 , 1.0 );		// 日の経過に従い 3.10(1d)→2.40…1.00(4d) と1.00まで低下
					workReq.attack = 0.1;

					// 行動要求の登録
					Requests.add(workReq);
				}

			}else{

				// 白貰い
				workReq = new Request( judge.targetAgentNo );

				workReq.guard = 1.2;
				workReq.attack = 1.4;

				// 確定人外でないか
				if( !args.agi.selfViewInfo.isFixWolfSide(judge.agentNo) ){
					workReq.inspect = Math.min( 0.6 + gameInfo.getDay() * 0.1 , 1.0 );	// 日の経過に従い 0.70(1d)→0.80…1.00(4d) と1.00まで上昇
					workReq.vote = Math.min( 0.4 + gameInfo.getDay() * 0.2 , 1.0 );		// 日の経過に従い 0.60(1d)→0.80…1.00(3d) と1.00まで上昇　※票合わせ能力が弱いので、早めに手を打つ
				}

				// 行動要求の登録
				Requests.add(workReq);

			}
		}

		// 各役職のCO者を取得
		List<Integer> seers = args.agi.getEnableCOAgentNo(Role.SEER);
		List<Integer> mediums = args.agi.getEnableCOAgentNo(Role.MEDIUM);
		List<Integer> bodyguards = args.agi.getEnableCOAgentNo(Role.BODYGUARD);
		List<Integer> villagers = args.agi.getEnableCOAgentNo(Role.VILLAGER);

		// 占CO者
		for( int seer : seers ){
			workReq = new Request( seer );

			// 対抗占いは避ける
			workReq.inspect = 0.05;
			// CO数に応じて護衛を増やす
			if( seers.size() <= 1 ){
				// 1CO
				workReq.guard = 10.0;
			}else if( seers.size() <= 2 ){
				// 2CO
				workReq.guard = 4.0;
			}else if( seers.size() <= 3 ){
				// 3CO
				workReq.guard = 2.0;
			}else{
				// 4CO以上
				workReq.guard = 1.0;
			}
			// 対抗占が噛まれていて、単霊が生きていれば護衛を薄くする
			if( isAttackedSeer &&
			    mediums.size() == 1 && !isAttackedMedium ){
				workReq.guard *= 0.7;
			}

			workReq.vote = Math.min(Math.max( -0.2 + gameInfo.getDay() * 0.3 , 0.0) , 1.0 ) + (isAttackedSeer ? 0.2 : 0.0);	// 日の経過に従い 0.10(1d)→0.70…1.00(4d) と1.00まで上昇
			workReq.attack = 3.0;

			// 行動要求の登録
			Requests.add(workReq);
		}

		// 霊CO者
		for( int medium : mediums ){
			workReq = new Request(medium);

			// 霊能占いは避ける
			workReq.inspect = 0.05;
			// 単霊は価値を上げ、複霊は価値を下げる
			if( mediums.size() <= 1 ){
				// 1CO
				workReq.guard = 3.0;
				workReq.vote = Math.min(Math.max( -0.2 + gameInfo.getDay() * 0.3 , 0.0) , 1.0 );	// 日の経過に従い 0.10(1d)→0.70…1.00(4d) と1.00まで上昇
				workReq.attack = 2.0;
			}else{
				// 2CO以上
				workReq.guard = 0.1;
				workReq.vote = Math.max( 1.0 + mediums.size() + (isAttackedMedium ? 0.6 : 0.0) - gameInfo.getDay() * 0.5 , 1.0 );	// 日の経過に従い 2.50(1d)→2.00…1.0(4d) と1.00まで低下(2CO時。CO数毎に更に+1.0)
				workReq.attack = 0.1;
			}

			// 行動要求の登録
			Requests.add(workReq);
		}


		// 狩CO者
		for( int bodyguard : bodyguards ){
			workReq = new Request(bodyguard);

			workReq.inspect = 0.3;	// 噛みを待ちながら推理で決め打つのが有意
			workReq.guard = 0.001;	// 狩視点では自分以外は偽狩確定
			workReq.vote = Math.min(Math.max( -0.2 + gameInfo.getDay() * 0.3 , 0.0) , 1.0 );	// 日の経過に従い 0.10(1d)→0.70…1.00(4d) と1.00まで上昇
			workReq.attack = 16.0;

			// 行動要求の登録
			Requests.add(workReq);
		}


		// 村CO者
		for( int villager : villagers ){
			workReq = new Request(villager);

			// 非狩確定のため価値が下がる
			workReq.guard = 0.8;
			workReq.vote = Math.max( 1.4 - gameInfo.getDay() * 0.1 , 1.0 );	// 日の経過に従い 1.30(1d)→1.20…1.0(4d) と1.00まで低下
			workReq.attack = 0.8;

			// 行動要求の登録
			Requests.add(workReq);
		}


		return Requests;

	}

}
