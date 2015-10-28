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
 * �����u���[�����v�N���X
 */
public final class VoteRecent extends AbstractGuessStrategy {

	@Override
	public ArrayList<Guess> getGuessList(GuessStrategyArgs args) {

		// �������X�g
		ArrayList<Guess> guesses = new ArrayList<Guess>();


		// �S�Ă̓��[�������m�F����(���񓊕[=1����)
		for( int day = 1; day < args.agi.latestGameInfo.getDay(); day++ ){

			// ��CO�҂̃��X�g���擾����
			List<Integer> mediums = args.agi.getEnableCOAgentNo(Role.MEDIUM, day, 0);

			for( Vote vote : args.agi.getVoteList(day) ){

				// ���[�̐����v�f�Ƃ��Ă̏d��(�菇�݂肾�ƌy���Ȃ�)
				double weight = 1.0;

				// �퓊�[�҂�����̏ꍇ�A�菇�݂�Ƃ��ďd�݂�������
				if( mediums.size() >= 2 && mediums.indexOf(vote.getTarget().getAgentIdx()) != -1 ){
					weight *= 0.5;
				}
				// �퓊�[�҂����Ⴂ�̏ꍇ�A�菇�݂�Ƃ��ďd�݂�������
				if( args.agi.isReceiveWolfJudge(vote.getTarget().getAgentIdx(), day, 0) ){
					weight *= 0.5;
				}

				AbstractCondition agentWolf = RoleCondition.getRoleCondition( vote.getAgent().getAgentIdx(), Role.WEREWOLF );
				AbstractCondition agentPossessed = RoleCondition.getRoleCondition( vote.getAgent().getAgentIdx(), Role.POSSESSED );
				AbstractCondition targetWolf = RoleCondition.getRoleCondition( vote.getTarget().getAgentIdx(), Role.WEREWOLF );
				AbstractCondition targetNotWolf = RoleCondition.getNotRoleCondition( vote.getTarget().getAgentIdx(), Role.WEREWOLF );

				Guess guess;
				// �T���T�̃p�^�[���𔖂�����i���C���؂���j
				if( args.agi.agentState[vote.getAgent().getAgentIdx()].causeofDeath != CauseOfDeath.ATTACKED &&
				    args.agi.agentState[vote.getTarget().getAgentIdx()].causeofDeath != CauseOfDeath.ATTACKED){
					guess = new Guess();
					guess.condition = new AndCondition().addCondition(agentWolf).addCondition(targetWolf);
					guess.correlation = 1.0 - 0.1 * weight;
					guesses.add(guess);
				}

				// �T����T�̃p�^�[����Z������i�X�P�[�v�S�[�g�j
				if( args.agi.agentState[vote.getAgent().getAgentIdx()].causeofDeath != CauseOfDeath.ATTACKED ){
					guess = new Guess();
					guess.condition = new AndCondition().addCondition(agentWolf).addCondition(targetNotWolf);
					guess.correlation = 1.0 + 0.020 * weight;
					guesses.add(guess);
				}

				// ������T�̃p�^�[����Z������i�X�P�[�v�S�[�g�j
				guess = new Guess();
				guess.condition = new AndCondition().addCondition(agentPossessed).addCondition(targetNotWolf);
				guess.correlation = 1.0 + 0.005 * weight;
				guesses.add(guess);

				//TODO �����~�Ƃ�������i�ʃN���X�Łj
			}
		}

		// �������X�g��Ԃ�
		return guesses;
	}

}
