package jp.halfmoon.inaba.aiwolf.guess;

import java.util.ArrayList;
import java.util.List;

import jp.halfmoon.inaba.aiwolf.condition.AbstractCondition;
import jp.halfmoon.inaba.aiwolf.condition.AndCondition;
import jp.halfmoon.inaba.aiwolf.condition.RoleCondition;
import jp.halfmoon.inaba.aiwolf.lib.CauseOfDeath;

import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Vote;

/**
 * 推理「投票履歴」クラス
 */
public final class VoteRecent extends AbstractGuessStrategy {

	@Override
	public ArrayList<Guess> getGuessList(GuessStrategyArgs args) {

		// 推理リスト
		ArrayList<Guess> guesses = new ArrayList<Guess>();


		// 全ての投票履歴を確認する(初回投票=1日目)
		for( int day = 1; day < args.agi.latestGameInfo.getDay(); day++ ){

			// 霊CO者のリストを取得する
			List<Integer> mediums = args.agi.getEnableCOAgentNo(Role.MEDIUM, day, 0);

			for( Vote vote : args.agi.getVoteList(day) ){

				// 投票の推理要素としての重み(手順吊りだと軽くなる)
				double weight = 1.0;

				// 被投票者が複霊の場合、手順吊りとして重みを下げる
				if( mediums.size() >= 2 && mediums.indexOf(vote.getTarget().getAgentIdx()) != -1 ){
					weight *= 0.5;
				}
				// 被投票者が黒貰いの場合、手順吊りとして重みを下げる
				if( args.agi.isReceiveWolfJudge(vote.getTarget().getAgentIdx(), day, 0) ){
					weight *= 0.5;
				}

				AbstractCondition agentWolf = RoleCondition.getRoleCondition( vote.getAgent().getAgentIdx(), Role.WEREWOLF );
				AbstractCondition agentPossessed = RoleCondition.getRoleCondition( vote.getAgent().getAgentIdx(), Role.POSSESSED );
				AbstractCondition targetWolf = RoleCondition.getRoleCondition( vote.getTarget().getAgentIdx(), Role.WEREWOLF );
				AbstractCondition targetNotWolf = RoleCondition.getNotRoleCondition( vote.getTarget().getAgentIdx(), Role.WEREWOLF );

				Guess guess;
				// 狼→狼のパターンを薄く見る（ライン切れより）
				if( args.agi.agentState[vote.getAgent().getAgentIdx()].causeofDeath != CauseOfDeath.ATTACKED &&
				    args.agi.agentState[vote.getTarget().getAgentIdx()].causeofDeath != CauseOfDeath.ATTACKED){
					guess = new Guess();
					guess.condition = new AndCondition().addCondition(agentWolf).addCondition(targetWolf);
					guess.correlation = 1.0 - 0.1 * weight;
					guesses.add(guess);
				}

				// 狼→非狼のパターンを濃く見る（スケープゴート）
				if( args.agi.agentState[vote.getAgent().getAgentIdx()].causeofDeath != CauseOfDeath.ATTACKED ){
					guess = new Guess();
					guess.condition = new AndCondition().addCondition(agentWolf).addCondition(targetNotWolf);
					guess.correlation = 1.0 + 0.020 * weight;
					guesses.add(guess);
				}

				// 狂→非狼のパターンを濃く見る（スケープゴート）
				guess = new Guess();
				guess.condition = new AndCondition().addCondition(agentPossessed).addCondition(targetNotWolf);
				guess.correlation = 1.0 + 0.005 * weight;
				guesses.add(guess);

				//TODO 生存欲とかも見る（別クラスで）
			}
		}

		// 推理リストを返す
		return guesses;
	}

}
