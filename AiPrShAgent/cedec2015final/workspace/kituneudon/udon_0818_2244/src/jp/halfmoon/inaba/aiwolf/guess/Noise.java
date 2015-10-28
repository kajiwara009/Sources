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
 * �����u�m�C�Y�v�N���X
 */
public final class Noise extends AbstractGuessStrategy {

	@Override
	public ArrayList<Guess> getGuessList(GuessStrategyArgs args) {

		// �������X�g
		ArrayList<Guess> guesses = new ArrayList<Guess>();

		//TODO �����s���ŕ�����W���グ��̂������Ȃ̂ŁA�t���O���ĂĈꊇ�Ŕ��f����

		// �S�Ă̔����������m�F����
		for( int day = 0; day < args.agi.latestGameInfo.getDay(); day++ ){
			for( Talk talk : args.agi.getTalkList(day) ){

				double correlation = 1.0;

				// �����̏ڍׂ̎擾
				Utterance utterance = args.agi.getUtterance(talk.getContent());

				switch( utterance.getTopic() ){
					case COMINGOUT:

						// CO�Ώۂ������ȊO
						if( utterance.getTarget().getAgentIdx() != talk.getAgent().getAgentIdx() ){
							correlation *= 1.5;
						}

						//TODO PP�Ԃ����������l�OCO�̏���
						//if( utterance.getRole() == Role.WEREWOLF || utterance.getRole() == Role.POSSESSED ){
						//	Correlation *= 1.10;
						//}
						break;
					case VOTE:
						// �Ώۂ����݂��Ȃ���
						if( !args.agi.isValidAgentNo( utterance.getTarget().getAgentIdx() ) ){
							correlation *= 1.30;
							break;
						}
						// �Ώۂ�����
						if( utterance.getTarget().getAgentIdx() == talk.getAgent().getAgentIdx() ){
							correlation *= 1.10;
						}
						// �������_�őΏۂ����S���Ă���
						if( args.agi.getCauseOfDeath( utterance.getTarget().getAgentIdx(), talk.getDay() ) != CauseOfDeath.ALIVE  ){
							correlation *= 1.05;
						}
						break;
					case ESTIMATE:
						// �Ώۂ����݂��Ȃ���
						if( utterance.getTarget().getAgentIdx() < 1 || utterance.getTarget().getAgentIdx() > args.agi.gameSetting.getPlayerNum() ){
							correlation *= 1.30;
							break;
						}
						// �Ώۂ�����
						if( utterance.getTarget().getAgentIdx() == talk.getAgent().getAgentIdx() ){
							correlation *= 1.05;
						}

						break;
					case DISAGREE:
						// �����̈Ӗ�����͕s�\
						correlation *= 1.02;

						break;
					case AGREE:
						// �����̈Ӗ����擾
						Utterance refutterance = args.agi.getMeanFromAgreeTalk( talk, 0 );

						// �����̈Ӗ�����͕s�\
						if( refutterance == null ){
							correlation *= 1.05;
						}else{
							switch( refutterance.getTopic() ){
								case ESTIMATE:
									// �Ώۂ����݂��Ȃ���
									if( !args.agi.isValidAgentNo( refutterance.getTarget().getAgentIdx() ) ){
										correlation *= 1.30;
										break;
									}
									// �Ώۂ�����
									if( refutterance.getTarget().getAgentIdx() == talk.getAgent().getAgentIdx() ){
										correlation *= 1.05;
									}
									break;
								case VOTE:
									// �Ώۂ����݂��Ȃ���
									if( !args.agi.isValidAgentNo( refutterance.getTarget().getAgentIdx() ) ){
										correlation *= 1.30;
										break;
									}
									// �Ώۂ�����
									if( refutterance.getTarget().getAgentIdx() == talk.getAgent().getAgentIdx() ){
										correlation *= 1.10;
									}
									// �������_�őΏۂ����S���Ă���
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

				// �W���ɕω����������ꍇ�A������ǉ�����
				if( Double.compare(correlation, 1.0) == 0 ){
					// �Ώۂ��Tor���̃p�^�[����Z������
					RoleCondition wolfCondition = RoleCondition.getRoleCondition( talk.getAgent().getAgentIdx(), Role.WEREWOLF );
					RoleCondition posCondition = RoleCondition.getRoleCondition( talk.getAgent().getAgentIdx(), Role.POSSESSED );

					Guess guess = new Guess();
					guess.condition = new OrCondition().addCondition(wolfCondition).addCondition(posCondition);
					guess.correlation = correlation;
					guesses.add(guess);
				}

			}
		}


		// �S�Ă̐蔻����m�F����
		for( Judge judge : args.agi.getSeerJudgeList() ){
			// �s���Ȕ���o��(CO�����ɔ���o���Ȃ�)
			if( judge.talk.equals(judge.cancelTalk) ){
				// �Ώۂ��Tor���̃p�^�[����Z������
				RoleCondition wolfCondition = RoleCondition.getRoleCondition( judge.agentNo, Role.WEREWOLF );
				RoleCondition posCondition = RoleCondition.getRoleCondition( judge.agentNo, Role.POSSESSED );

				Guess guess = new Guess();
				guess.condition = new OrCondition().addCondition(wolfCondition).addCondition(posCondition);
				guess.correlation = 2.0;
				guesses.add(guess);
			}
		}


		// �S�Ă̗씻����m�F����
		for( Judge judge : args.agi.getMediumJudgeList() ){
			// �s���Ȕ���o��(CO�����ɔ���o���Ȃ�)
			if( judge.talk.equals(judge.cancelTalk) ){
				// �Ώۂ��Tor���̃p�^�[����Z������
				RoleCondition wolfCondition = RoleCondition.getRoleCondition( judge.agentNo, Role.WEREWOLF );
				RoleCondition posCondition = RoleCondition.getRoleCondition( judge.agentNo, Role.POSSESSED );

				Guess guess = new Guess();
				guess.condition = new OrCondition().addCondition(wolfCondition).addCondition(posCondition);
				guess.correlation = 2.0;
				guesses.add(guess);
			}
		}


		// �������X�g��Ԃ�
		return guesses;
	}

}
