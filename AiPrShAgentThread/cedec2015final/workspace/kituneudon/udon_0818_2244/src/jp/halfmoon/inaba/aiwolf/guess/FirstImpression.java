package jp.halfmoon.inaba.aiwolf.guess;

import java.util.ArrayList;

import jp.halfmoon.inaba.aiwolf.condition.OrCondition;
import jp.halfmoon.inaba.aiwolf.condition.RoleCondition;

import org.aiwolf.common.data.Role;

/**
 * 推理「第一印象」クラス
 * 同一スコアの回避と序盤の状況作りが主目的
 */
public final class FirstImpression extends AbstractGuessStrategy {

	// 推理リスト
	private ArrayList<Guess> guesses = null;

	@Override
	public ArrayList<Guess> getGuessList(GuessStrategyArgs args) {

		// 初回取得時に行動リクエストの設定を行う
		if( guesses == null ){
			guesses = new ArrayList<Guess>();
			for( int i = 1; i <= args.agi.gameSetting.getPlayerNum(); i++ ){
				Guess guess;

				// 各エージェントが狼or狂人のパターンを、ランダムで0.95～1.05倍と推理する
				RoleCondition wolfCondition = RoleCondition.getRoleCondition( i, Role.WEREWOLF );
				RoleCondition posCondition = RoleCondition.getRoleCondition( i, Role.POSSESSED );

				guess = new Guess();
				guess.condition = new OrCondition().addCondition(wolfCondition).addCondition(posCondition);
				guess.correlation = 0.95 + Math.random() * 0.1;
				guesses.add(guess);

			}
		}

		// 推理リストを返す
		return guesses;
	}

}
