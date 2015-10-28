package jp.halfmoon.inaba.aiwolf.guess;

import java.util.ArrayList;

import jp.halfmoon.inaba.aiwolf.condition.OrCondition;
import jp.halfmoon.inaba.aiwolf.condition.RoleCondition;
import jp.halfmoon.inaba.aiwolf.lib.CauseOfDeath;
import jp.halfmoon.inaba.aiwolf.lib.Judge;

import org.aiwolf.client.lib.Utterance;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Talk;

/**
 * 推理「ノイズ」クラス
 */
public final class Noise extends AbstractGuessStrategy {

	@Override
	public ArrayList<Guess> getGuessList(GuessStrategyArgs args) {

		// 推理リスト
		ArrayList<Guess> guesses = new ArrayList<Guess>();

		//TODO 同じ行動で複数回係数上げるのも微妙なので、フラグ立てて一括で判断する

		// 全ての発言履歴を確認する
		for( int day = 0; day < args.agi.latestGameInfo.getDay(); day++ ){
			for( Talk talk : args.agi.getTalkList(day) ){

				double correlation = 1.0;

				// 発言の詳細の取得
				Utterance utterance = args.agi.getUtterance(talk.getContent());

				switch( utterance.getTopic() ){
					case COMINGOUT:

						// CO対象が自分以外
						if( utterance.getTarget().getAgentIdx() != talk.getAgent().getAgentIdx() ){
							correlation *= 1.5;
						}

						//TODO PP返しを除いた人外COの処理
						//if( utterance.getRole() == Role.WEREWOLF || utterance.getRole() == Role.POSSESSED ){
						//	Correlation *= 1.10;
						//}
						break;
					case VOTE:
						// 対象が存在しない者
						if( !args.agi.isValidAgentNo( utterance.getTarget().getAgentIdx() ) ){
							correlation *= 1.30;
							break;
						}
						// 対象が自分
						if( utterance.getTarget().getAgentIdx() == talk.getAgent().getAgentIdx() ){
							correlation *= 1.10;
						}
						// 発言時点で対象が死亡している
						if( args.agi.getCauseOfDeath( utterance.getTarget().getAgentIdx(), talk.getDay() ) != CauseOfDeath.ALIVE  ){
							correlation *= 1.05;
						}
						break;
					case ESTIMATE:
						// 対象が存在しない者
						if( utterance.getTarget().getAgentIdx() < 1 || utterance.getTarget().getAgentIdx() > args.agi.gameSetting.getPlayerNum() ){
							correlation *= 1.30;
							break;
						}
						// 対象が自分
						if( utterance.getTarget().getAgentIdx() == talk.getAgent().getAgentIdx() ){
							correlation *= 1.05;
						}

						break;
					case DISAGREE:
						// 発言の意味が解析不能
						correlation *= 1.02;

						break;
					case AGREE:
						// 発言の意味を取得
						Utterance refutterance = args.agi.getMeanFromAgreeTalk( talk, 0 );

						// 発言の意味が解析不能
						if( refutterance == null ){
							correlation *= 1.05;
						}else{
							switch( refutterance.getTopic() ){
								case ESTIMATE:
									// 対象が存在しない者
									if( !args.agi.isValidAgentNo( refutterance.getTarget().getAgentIdx() ) ){
										correlation *= 1.30;
										break;
									}
									// 対象が自分
									if( refutterance.getTarget().getAgentIdx() == talk.getAgent().getAgentIdx() ){
										correlation *= 1.05;
									}
									break;
								case VOTE:
									// 対象が存在しない者
									if( !args.agi.isValidAgentNo( refutterance.getTarget().getAgentIdx() ) ){
										correlation *= 1.30;
										break;
									}
									// 対象が自分
									if( refutterance.getTarget().getAgentIdx() == talk.getAgent().getAgentIdx() ){
										correlation *= 1.10;
									}
									// 発言時点で対象が死亡している
									if( args.agi.getCauseOfDeath( refutterance.getTarget().getAgentIdx(), talk.getDay() ) != CauseOfDeath.ALIVE  ){
										correlation *= 1.05;
									}
									break;
								default:
									break;
							}
						}

						break;
					default:
						break;
				}

				// 係数に変化があった場合、推理を追加する
				if( Double.compare(correlation, 1.0) == 0 ){
					// 対象が狼or狂のパターンを濃く見る
					RoleCondition wolfCondition = RoleCondition.getRoleCondition( talk.getAgent().getAgentIdx(), Role.WEREWOLF );
					RoleCondition posCondition = RoleCondition.getRoleCondition( talk.getAgent().getAgentIdx(), Role.POSSESSED );

					Guess guess = new Guess();
					guess.condition = new OrCondition().addCondition(wolfCondition).addCondition(posCondition);
					guess.correlation = correlation;
					guesses.add(guess);
				}

			}
		}


		// 全ての占判定を確認する
		for( Judge judge : args.agi.getSeerJudgeList() ){
			// 不正な判定出し(COせずに判定出しなど)
			if( judge.talk.equals(judge.cancelTalk) ){
				// 対象が狼or狂のパターンを濃く見る
				RoleCondition wolfCondition = RoleCondition.getRoleCondition( judge.agentNo, Role.WEREWOLF );
				RoleCondition posCondition = RoleCondition.getRoleCondition( judge.agentNo, Role.POSSESSED );

				Guess guess = new Guess();
				guess.condition = new OrCondition().addCondition(wolfCondition).addCondition(posCondition);
				guess.correlation = 2.0;
				guesses.add(guess);
			}
		}


		// 全ての霊判定を確認する
		for( Judge judge : args.agi.getMediumJudgeList() ){
			// 不正な判定出し(COせずに判定出しなど)
			if( judge.talk.equals(judge.cancelTalk) ){
				// 対象が狼or狂のパターンを濃く見る
				RoleCondition wolfCondition = RoleCondition.getRoleCondition( judge.agentNo, Role.WEREWOLF );
				RoleCondition posCondition = RoleCondition.getRoleCondition( judge.agentNo, Role.POSSESSED );

				Guess guess = new Guess();
				guess.condition = new OrCondition().addCondition(wolfCondition).addCondition(posCondition);
				guess.correlation = 2.0;
				guesses.add(guess);
			}
		}


		// 推理リストを返す
		return guesses;
	}

}
