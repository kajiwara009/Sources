package jp.halfmoon.inaba.aiwolf.guess;

import java.util.ArrayList;

import jp.halfmoon.inaba.aiwolf.condition.AbstractCondition;
import jp.halfmoon.inaba.aiwolf.condition.RoleCondition;
import jp.halfmoon.inaba.aiwolf.lib.CauseOfDeath;

import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Vote;

/**
 * �����u�ӌ����݁v�N���X
 */
public final class AttackObstacle_Guess extends AbstractGuessStrategy {

	@Override
	public ArrayList<Guess> getGuessList(GuessStrategyArgs args) {

		// �������X�g
		ArrayList<Guess> guesses = new ArrayList<Guess>();


		// �S�Ă̓��[�������m�F����(���񓊕[=1����)
		for( int day = 1; day < args.agi.latestGameInfo.getDay(); day++ ){
			for( Vote vote : args.agi.getVoteList(day) ){
				int agentNo = vote.getAgent().getAgentIdx();

				// �P���������҂̓��[��
				if( args.agi.agentState[agentNo].causeofDeath == CauseOfDeath.ATTACKED ){
					// ��CO�E��CO�҂�
					if( args.agi.agentState[agentNo].comingOutRole == null || args.agi.agentState[agentNo].comingOutRole == Role.VILLAGER ){

						// �퓊�[�҂��T�̉\����Z������
						AbstractCondition targetWolf = RoleCondition.getRoleCondition( vote.getTarget().getAgentIdx(), Role.WEREWOLF );

						Guess guess = new Guess();
						guess.condition = targetWolf;
						guess.correlation = 1.03;
						guesses.add(guess);

					}
				}
			}
		}

		// �������X�g��Ԃ�
		return guesses;
	}

}
