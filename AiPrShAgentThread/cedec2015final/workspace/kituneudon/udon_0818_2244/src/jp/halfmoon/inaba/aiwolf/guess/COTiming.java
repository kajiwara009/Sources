package jp.halfmoon.inaba.aiwolf.guess;

import java.util.ArrayList;

import org.aiwolf.common.data.Role;

import jp.halfmoon.inaba.aiwolf.condition.OrCondition;
import jp.halfmoon.inaba.aiwolf.condition.RoleCondition;
import jp.halfmoon.inaba.aiwolf.lib.ComingOut;

/**
 * „—uCOvƒNƒ‰ƒX
 */
public final class COTiming extends AbstractGuessStrategy {

	@Override
	public ArrayList<Guess> getGuessList(GuessStrategyArgs args) {
		// „—ƒŠƒXƒg
		ArrayList<Guess> guesses = new ArrayList<Guess>();

		for( ComingOut co : args.agi.comingOutList ){

			RoleCondition wolfCondition = RoleCondition.getRoleCondition( co.agentNo, Role.WEREWOLF );
			RoleCondition posCondition = RoleCondition.getRoleCondition( co.agentNo, Role.POSSESSED );

			// –³Œø‚É‚È‚Á‚Ä‚¢‚éCO‚ª‚ ‚éÒ‚Í‹^‚¤
			if( !co.isEnable() ){
				Guess guess = new Guess();
				guess.condition = new OrCondition().addCondition(wolfCondition).addCondition(posCondition);
				guess.correlation = 1.5;
				guesses.add(guess);
			}

			// ”z–ğ‚É‘¶İ‚µ‚È‚¢–ğ‚ğCO‚µ‚½Ò‚Í‹^‚¤
			if( args.agi.gameSetting.getRoleNum(co.role) < 1 ){
				Guess guess = new Guess();
				guess.condition = new OrCondition().addCondition(wolfCondition).addCondition(posCondition);
				guess.correlation = 3.0;
				guesses.add(guess);
			}

			// è—ì‚Ì‚İ‚Ì„—
			if( co.role == Role.SEER || co.role == Role.MEDIUM ){

				// •”»’è‚ğó‚¯‚Ä‚©‚çè—ìCO‚µ‚½Ò‚Í‹^‚¤
				if( args.agi.isReceiveWolfJudge(co.agentNo, co.commingOutTalk.getDay(), co.commingOutTalk.getIdx()) ){
					Guess guess = new Guess();
					guess.condition = wolfCondition;
					guess.correlation = 1.2;
					guesses.add(guess);
				}

				//TODO ‹^‚¤‚Ì‚Í©”­CO‚ÉŒÀ’è‚µA‘ÎRCO‚Í‹–‰Â‚·‚éi”»’f–Ê“|‚È‚Ì‚Å’÷‚ßØ‚è“I‚É’f”Oj
	//			// n‡–ÚˆÈ~‚ÅCO‚µ‚½Ò‚Í‹^‚¤
	//			if( co.commingOutTalk.getIdx() >= args.agi.gameSetting.getPlayerNum() * 5 ){
	//				Guess guess = new Guess();
	//				guess.condition = new OrCondition().addCondition(wolfCondition).addCondition(posCondition);
	//				guess.correlation = 1.1;
	//				guesses.add(guess);
	//			}

				//TODO ‘¼•Ò¬‘Î‰
				// CO‚µ‚½ƒ^ƒCƒ~ƒ“ƒO‚É‚æ‚Á‚Ä‹^‚¢“x‚ğã‚°‚é
				if( co.commingOutTalk.getDay() > 3 ){
					final double hoge[] = { 1.0, 1.1, 1.1, 1.15, 1.2, 1.4, 1.5, 1.5, 1.5, 1.5, 1.5, 1.5, 1.5, 1.5, 1.5, 1.5, 1.5, 1.5 };

					Guess guess = new Guess();
					guess.condition = new OrCondition().addCondition(wolfCondition).addCondition(posCondition);
					guess.correlation = hoge[co.commingOutTalk.getDay()];
					guesses.add(guess);

					// ‘O“ú‚Ü‚Å‚É“¯‚¶–ğE‚ÌCO‚ª‚ ‚ê‚Î‹^‚¢“x‚ğã‚°‚é
					for( ComingOut refCo : args.agi.comingOutList ){
						if( refCo.role == co.role && refCo.commingOutTalk.getDay() < co.commingOutTalk.getDay() ){
							switch(co.role){
								case SEER:
									guess.correlation *= 1.2;
									break;
								case MEDIUM:
									guess.correlation *= 2.0;
									break;
								default:
									break;
							}
							break;
						}
					}
				}

			}


		}


		// „—ƒŠƒXƒg‚ğ•Ô‚·
		return guesses;
	}

}
