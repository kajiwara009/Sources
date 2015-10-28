package jp.halfmoon.inaba.aiwolf.guess;

import java.util.ArrayList;
import java.util.List;

import jp.halfmoon.inaba.aiwolf.condition.AndCondition;
import jp.halfmoon.inaba.aiwolf.condition.OrCondition;
import jp.halfmoon.inaba.aiwolf.condition.RoleCondition;
import jp.halfmoon.inaba.aiwolf.lib.ComingOut;

import org.aiwolf.common.data.Role;

/**
 * 推理「基本陣形観」クラス
 */
public final class Formation_Basic extends AbstractGuessStrategy {

	@Override
	public ArrayList<Guess> getGuessList(GuessStrategyArgs args) {

		// 推理リスト
		ArrayList<Guess> guesses = new ArrayList<Guess>();

		// 占霊ＣＯがそれぞれ最初に行われた日を求める
		int firstSeerCODay = Integer.MAX_VALUE;
		int firstMediumCODay = Integer.MAX_VALUE;
		for( ComingOut co : args.agi.comingOutList ){
			if( co.role == Role.SEER ){
				firstSeerCODay = Math.min(co.commingOutTalk.getDay(), firstSeerCODay);
			}
			if( co.role == Role.MEDIUM ){
				firstMediumCODay = Math.min(co.commingOutTalk.getDay(), firstMediumCODay);
			}
		}


		//TODO 全偽の確率は最初のＣＯ日に依存…としたいのだけど、何故か同種対決で村勝率が下がるので保留。原因究明してから実装。（霊に護衛吸われる？）

		// 占全偽はあまり見ない
		List<Integer> seers = args.agi.getEnableCOAgentNo(Role.SEER);
		if( !seers.isEmpty() ){
			//TODO 他編成対応
			//final double allFakeCorrelation[] = { 0.7, 0.7, 0.8, 0.9, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0 };

			AndCondition allFakeCondition = new AndCondition();

			// 各霊が狼or狂である条件を作成し、全偽条件に追加する
			for( int seer : seers ){
				RoleCondition wolfCondition = RoleCondition.getRoleCondition( seer, Role.WEREWOLF );
				RoleCondition posCondition = RoleCondition.getRoleCondition( seer, Role.POSSESSED );

				OrCondition orCondition = new OrCondition();
				orCondition.addCondition(wolfCondition).addCondition(posCondition);

				allFakeCondition.addCondition(orCondition);
			}

			Guess guess = new Guess();
			guess.condition = allFakeCondition;
			//guess.correlation = allFakeCorrelation[firstSeerCODay];
			guess.correlation = 0.7;
			guesses.add(guess);
		}


		// 霊全偽はあまり見ない
		List<Integer> mediums = args.agi.getEnableCOAgentNo(Role.MEDIUM);
		if( !mediums.isEmpty() ){
			//TODO 他編成対応
			//final double allFakeCorrelation[] = { 0.1, 0.1, 0.4, 0.7, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0 };

			AndCondition allFakeCondition = new AndCondition();

			// 各霊が狼or狂である条件を作成し、全偽条件に追加する
			for( int medium : mediums ){
				RoleCondition wolfCondition = RoleCondition.getRoleCondition( medium, Role.WEREWOLF );
				RoleCondition posCondition = RoleCondition.getRoleCondition( medium, Role.POSSESSED );

				OrCondition orCondition = new OrCondition();
				orCondition.addCondition(wolfCondition).addCondition(posCondition);

				allFakeCondition.addCondition(orCondition);
			}

			Guess guess = new Guess();
			guess.condition = allFakeCondition;
			//guess.correlation = allFakeCorrelation[firstMediumCODay];
			guess.correlation = 0.7;
			guesses.add(guess);
		}


//		// 霊狼はあまり見ない？
//		if( !mediums.isEmpty() ){
//			for( int medium : mediums ){
//				Guess guess = new Guess();
//				guess.condition = new RoleCondition( medium, Role.WEREWOLF );
//				guess.correlation = 0.95;
//				guesses.add(guess);
//			}
//		}


		// 占霊合わせて4CO以上の場合、狂人は騙っていると見る
		if( seers.size() + mediums.size() >= 4 ){

			OrCondition orCondition = new OrCondition();
			for( int seer : seers ){
				orCondition.addCondition( RoleCondition.getRoleCondition( seer, Role.POSSESSED ) );
			}
			for( int medium : mediums ){
				orCondition.addCondition( RoleCondition.getRoleCondition( medium, Role.POSSESSED ) );
			}

			Guess guess = new Guess();
			guess.condition = orCondition;
			guess.correlation = 1.1;
			guesses.add(guess);
		}


		// 狂狩はあまり見ない
		List<Integer> bodyguards = args.agi.getEnableCOAgentNo(Role.BODYGUARD);
		if( !bodyguards.isEmpty() ){
			for( int bodyguard : bodyguards ){
				Guess guess = new Guess();
				guess.condition = RoleCondition.getRoleCondition( bodyguard, Role.POSSESSED );
				guess.correlation = 0.8;
				guesses.add(guess);

				// 自身が狩なら、偽は狼で見る
				if( bodyguard != args.agi.latestGameInfo.getAgent().getAgentIdx() ){
					guess = new Guess();
					guess.condition = RoleCondition.getRoleCondition( bodyguard, Role.WEREWOLF );
					guess.correlation = 1.2;
					guesses.add(guess);
				}
			}
		}



		// 推理リストを返す
		return guesses;
	}

}
