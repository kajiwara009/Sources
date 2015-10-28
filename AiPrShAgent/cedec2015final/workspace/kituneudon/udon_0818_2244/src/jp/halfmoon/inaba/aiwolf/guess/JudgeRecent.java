package jp.halfmoon.inaba.aiwolf.guess;

import java.util.ArrayList;
import java.util.List;

import org.aiwolf.client.lib.Topic;
import org.aiwolf.client.lib.Utterance;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Species;
import org.aiwolf.common.data.Talk;

import jp.halfmoon.inaba.aiwolf.condition.AbstractCondition;
import jp.halfmoon.inaba.aiwolf.condition.AndCondition;
import jp.halfmoon.inaba.aiwolf.condition.OrCondition;
import jp.halfmoon.inaba.aiwolf.condition.RoleCondition;
import jp.halfmoon.inaba.aiwolf.lib.CauseOfDeath;
import jp.halfmoon.inaba.aiwolf.lib.Judge;


/**
 * 推理「判定履歴」クラス
 */
public final class JudgeRecent extends AbstractGuessStrategy {

	@Override
	public ArrayList<Guess> getGuessList(GuessStrategyArgs args) {
		// 推理リスト
		ArrayList<Guess> guesses = new ArrayList<Guess>();

		List<Integer> seers = args.agi.getEnableCOAgentNo(Role.SEER);
		List<Integer> mediums = args.agi.getEnableCOAgentNo(Role.MEDIUM);

		// 全ての占判定履歴を確認する
		for( Judge judge : args.agi.getSeerJudgeList() ){

			AbstractCondition agentWolf = RoleCondition.getRoleCondition( judge.agentNo, Role.WEREWOLF );
			AbstractCondition agentPossessed = RoleCondition.getRoleCondition( judge.agentNo, Role.POSSESSED );
			AbstractCondition targetWolf = RoleCondition.getRoleCondition( judge.targetAgentNo, Role.WEREWOLF );

			// 無効な判定であればスキップ
			if( !judge.isEnable() ){
				continue;
			}

			// 人間判定か
			if( judge.result == Species.HUMAN){

				//TODO 日数から白貰い全体の中に囲いがいる確率（全白パターンの否定）、とした方が良いかも？
				// 狼→狼のパターンを濃く見る（囲い）
				Guess guess = new Guess();
				guess.condition = new AndCondition().addCondition(agentWolf).addCondition(targetWolf);
				guess.correlation = 1.02;
				guesses.add(guess);

			}else{

				Guess guess;

				//TODO 時系列のチェック
				// 黒出し先が占霊以外か
				if( args.agi.agentState[judge.targetAgentNo].comingOutRole == null ||
					(args.agi.agentState[judge.targetAgentNo].comingOutRole != Role.SEER && args.agi.agentState[judge.targetAgentNo].comingOutRole != Role.MEDIUM ) ){

					// 狂→狼のパターンを薄く見る（誤爆）
					guess = new Guess();
					guess.condition = new AndCondition().addCondition(agentPossessed).addCondition(targetWolf);
					guess.correlation = 0.85;
					guesses.add(guess);

					// 狼→狼のパターンを薄く見る（逆囲い）
					guess = new Guess();
					guess.condition = new AndCondition().addCondition(agentWolf).addCondition(targetWolf);
					guess.correlation = 0.75;
					guesses.add(guess);
				}


				//TODO 次の日が対抗噛み・霊噛みなら見ないようにする？
				// 初手黒出しは非狼で見る
				if( judge.talk.getDay() == 1 ){
					guess = new Guess();
					guess.condition = agentWolf;
					guess.correlation = 0.95;
					guesses.add(guess);
				}

			}

		}


		// 全ての占CO者を確認する
		List<Integer> attackedSeers = new ArrayList<Integer>();
		for( int seer : seers ){
			// 噛まれた占か
			if( args.agi.agentState[seer].causeofDeath == CauseOfDeath.ATTACKED ){
				attackedSeers.add(seer);
			}
		}

		// 噛まれた占の判定は正しいと見る
		for( Judge judge : args.agi.getSeerJudgeList() ){
			// 噛まれた占からの判定か
			if( attackedSeers.indexOf(judge.agentNo) != -1){

				// 無効な判定であればスキップ
				if( !judge.isEnable() ){
					continue;
				}

				// 人間判定か
				if( judge.result == Species.HUMAN){
					// 占い先が狼のパターンを薄く見る
					Guess guess = new Guess();
					guess.condition = RoleCondition.getRoleCondition(judge.targetAgentNo, Role.WEREWOLF);
					guess.correlation = 0.80;
					guesses.add(guess);
				}else{
					// 占い先が狼のパターンを濃く見る
					Guess guess = new Guess();
					guess.condition = RoleCondition.getRoleCondition(judge.targetAgentNo, Role.WEREWOLF);
					guess.correlation = 1.20;
					guesses.add(guess);
				}

			}
		}

		// 占の判定数チェック
		for( int seer : seers ){
			if( !isValidSeerJudgeNum( seer, args ) ){
				// 偽を濃く見る
				AbstractCondition agentWolf = RoleCondition.getRoleCondition( seer, Role.WEREWOLF );
				AbstractCondition agentPossessed = RoleCondition.getRoleCondition( seer, Role.POSSESSED );

				Guess guess = new Guess();
				guess.condition = new OrCondition().addCondition(agentWolf).addCondition(agentPossessed);
				guess.correlation = 3.0;
				guesses.add(guess);
			}
		}
		// 霊の判定数チェック
		for( int medium : mediums ){
			if( !isValidMediumJudgeNum( medium, args ) ){
				// 偽を濃く見る
				AbstractCondition agentWolf = RoleCondition.getRoleCondition( medium, Role.WEREWOLF );
				AbstractCondition agentPossessed = RoleCondition.getRoleCondition( medium, Role.POSSESSED );

				Guess guess = new Guess();
				guess.condition = new OrCondition().addCondition(agentWolf).addCondition(agentPossessed);
				guess.correlation = 3.0;
				guesses.add(guess);
			}
		}

		//TODO 判定先の妥当性チェック



		// 推理リストを返す
		return guesses;
	}


	/**
	 * 占の判定数が妥当か
	 * @param agentNo
	 * @param args
	 * @return
	 */
	private boolean isValidSeerJudgeNum(int agentNo, GuessStrategyArgs args){

		// 正しい判定数
		int trueJudgeNum;
		if( args.agi.agentState[agentNo].causeofDeath == CauseOfDeath.ALIVE ){
			trueJudgeNum = args.agi.latestGameInfo.getDay();
		}else{
			trueJudgeNum = args.agi.agentState[agentNo].deathDay - 1;
		}

		// 現在の判定数カウント
		int judgeCount = 0;
		for( Judge judge : args.agi.getSeerJudgeList() ){
			if( judge.agentNo == agentNo ){
				judgeCount++;
			}
		}

		// 判定数が多い
		if( judgeCount > trueJudgeNum ){
			return false;
		}

		// 判定数が少ない
		if( judgeCount < trueJudgeNum ){

			Talk lastTalk = null;
			// 最新日の最後の発言を取得
			for( Talk talk : args.agi.latestGameInfo.getTalkList() ){
				if( talk.getAgent().getAgentIdx() == agentNo ){
					lastTalk = talk;
				}
			}

			// 最新日に1回でも発言しているか
			if( lastTalk != null ){
				// 発言済 → 判定が少ないケースはCO直後の報告中のみ
				for( int i = args.agi.latestGameInfo.getTalkList().size() - 1; i >= 0 ; i-- ){
					Talk talk = args.agi.latestGameInfo.getTalkList().get(i);
					if( talk.getAgent().getAgentIdx() == agentNo ){

						// 発言を解析
						Utterance utterance = new Utterance(talk.getContent());

						if( utterance.getTopic() == Topic.COMINGOUT ){
							// CO直後で報告中
							return true;
						}else if( utterance.getTopic() != Topic.DIVINED ){
							// CO・結果報告以外の発言を行っている
							return false;
						}

					}
				}

				// 前日までにCOしており結果が少ない
				return false;

			}else{
				// 未発言 → 判定が１個少ないのが正常
				if( judgeCount != trueJudgeNum - 1 ){
					return false;
				}
			}

		}

		return true;

	}


	/**
	 * 霊の判定数が妥当か
	 * @param agentNo
	 * @param args
	 * @return
	 */
	private boolean isValidMediumJudgeNum(int agentNo, GuessStrategyArgs args){

		// 正しい判定数
		int trueJudgeNum;
		if( args.agi.agentState[agentNo].causeofDeath == CauseOfDeath.ALIVE ){
			trueJudgeNum = ( args.agi.latestGameInfo.getDay() < 2 ) ? 0 : (args.agi.latestGameInfo.getDay() - 1);
		}else{
			trueJudgeNum = args.agi.agentState[agentNo].deathDay - 2;
		}


		// 現在の判定数カウント
		int judgeCount = 0;
		for( Judge judge : args.agi.getMediumJudgeList() ){
			if( judge.agentNo == agentNo ){
				judgeCount++;
			}
		}

		// 判定数が多い
		if( judgeCount > trueJudgeNum ){
			return false;
		}

		// 判定数が少ない
		if( judgeCount < trueJudgeNum ){

			Talk lastTalk = null;
			// 最新日の最後の発言を取得
			for( Talk talk : args.agi.latestGameInfo.getTalkList() ){
				if( talk.getAgent().getAgentIdx() == agentNo ){
					lastTalk = talk;
				}
			}

			// 最新日に1回でも発言しているか
			if( lastTalk != null ){
				// 発言済 → 判定が少ないケースはCO直後の報告中のみ
				for( int i = args.agi.latestGameInfo.getTalkList().size() - 1; i >= 0 ; i-- ){
					Talk talk = args.agi.latestGameInfo.getTalkList().get(i);
					if( talk.getAgent().getAgentIdx() == agentNo ){

						// 発言を解析
						Utterance utterance = new Utterance(talk.getContent());

						if( utterance.getTopic() == Topic.COMINGOUT ){
							// CO直後で報告中
							return true;
						}else if( utterance.getTopic() != Topic.INQUESTED ){
							// CO・結果報告以外の発言を行っている
							return false;
						}

					}
				}

				// 前日までにCOしており結果が少ない
				return false;

			}else{
				// 未発言 → 判定が１個少ないのが正常
				if( judgeCount != trueJudgeNum - 1 ){
					return false;
				}
			}

		}

		return true;

	}

}
