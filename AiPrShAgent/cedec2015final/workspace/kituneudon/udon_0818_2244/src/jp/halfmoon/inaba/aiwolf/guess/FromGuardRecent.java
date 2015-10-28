package jp.halfmoon.inaba.aiwolf.guess;

import java.util.ArrayList;

import org.aiwolf.common.data.Role;
import org.aiwolf.common.net.GameInfo;

import jp.halfmoon.inaba.aiwolf.condition.AndCondition;
import jp.halfmoon.inaba.aiwolf.condition.RoleCondition;
import jp.halfmoon.inaba.aiwolf.lib.DayInfo;
import jp.halfmoon.inaba.aiwolf.lib.GuardRecent;


/**
 * 推理「護衛履歴」クラス
 */
public final class FromGuardRecent extends AbstractGuessStrategy {

	@Override
	public ArrayList<Guess> getGuessList(GuessStrategyArgs args) {
		// 推理リスト
		ArrayList<Guess> guesses = new ArrayList<Guess>();

		GameInfo gameInfo = args.agi.latestGameInfo;

		// 死体なしの日をチェック
		for( DayInfo dayInfo : args.agi.dayInfoList ){
			if( dayInfo.day >= 2 && dayInfo.attackAgentNo == null ){

				// 自分の護衛位置からの推理
				if( gameInfo.getRole() == Role.BODYGUARD ){

					// GJ先を取得
					Integer guardedAgentNo = args.agi.selfGuardRecent.get(dayInfo.day - 1);

					// 自分が護衛を設定していたか
					if( guardedAgentNo != null ){
						// GJ先が狼の可能性が下がる（狼のケースは襲撃なしや吊り先と被った場合のみ）
						Guess guess = new Guess();
						guess.condition = RoleCondition.getRoleCondition( guardedAgentNo, Role.WEREWOLF );
						guess.correlation = 0.30;
						guesses.add(guess);
					}

				}


				// 護衛履歴の走査
				for( GuardRecent guardRecent : args.agi.getGuardRecentList() ){
					if( guardRecent.agentNo != gameInfo.getAgent().getAgentIdx() &&
					    guardRecent.execDay == dayInfo.day - 1 &&
					    guardRecent.isEnable() ){

						// 狩人が真の場合、GJ先が狼の可能性が下がる（狼のケースは襲撃なしや吊り先と被った場合のみ）
						RoleCondition bodyguardNotWolf = RoleCondition.getNotRoleCondition( guardRecent.agentNo, Role.WEREWOLF );
						RoleCondition bodyguardNotPos = RoleCondition.getNotRoleCondition( guardRecent.agentNo, Role.POSSESSED );
						RoleCondition guardedWolf = RoleCondition.getRoleCondition( guardRecent.targetAgentNo, Role.WEREWOLF );

						Guess guess = new Guess();
						guess.condition = new AndCondition().addCondition(bodyguardNotWolf).addCondition(bodyguardNotPos).addCondition(guardedWolf);
						guess.correlation = 0.30;
						guesses.add(guess);
					}
				}


			}
		}



		// 推理リストを返す
		return guesses;
	}

}
