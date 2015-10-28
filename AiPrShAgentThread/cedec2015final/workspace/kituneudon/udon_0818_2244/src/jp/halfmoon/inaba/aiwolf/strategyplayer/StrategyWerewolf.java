package jp.halfmoon.inaba.aiwolf.strategyplayer;

import java.util.List;

import jp.halfmoon.inaba.aiwolf.lib.Common;
import jp.halfmoon.inaba.aiwolf.request.Request;

import org.aiwolf.client.lib.TemplateTalkFactory;
import org.aiwolf.client.lib.TemplateWhisperFactory;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.net.GameInfo;


public class StrategyWerewolf extends AbstractBaseStrategyPlayer {


	@Override
	public String talk() {

		// �j�]���̔���
		if( agi.selfViewInfo.wolfsidePatterns.isEmpty() ){

			// �Ƃ肠������CO
			if( !isCameOut ){
				isCameOut = true;

				String ret = TemplateTalkFactory.comingout( getMe(), Role.MEDIUM );
				return ret;
			}

			return TemplateTalkFactory.over();

		}

//		// �^�����T�̐l���ȏ㌾���Ă��Ȃ���Θb��
//		if( agi.talkedSuspicionAgentList.size() < agi.gameSetting.getRoleNumMap().get(Role.WEREWOLF) ){
//			// �^�����b�����͂��擾���A�擾�ł��Ă���Θb��
//			workString = getSuspicionTalkString();
//			if( workString != null ){
//				return workString;
//			}
//		}

		// ���[���ύX����ꍇ�A�V�������[���b��
		if( declaredPlanningVoteAgent != planningVoteAgent ){
			// ���[���ύX����
			actionUI.voteAgent = planningVoteAgent;
			declaredPlanningVoteAgent = planningVoteAgent;

			// ���b
			String ret = TemplateTalkFactory.vote( Agent.getAgent(planningVoteAgent) );
			return ret;
		}

		// �b�����������ꍇ�Aover��Ԃ�
		return TemplateTalkFactory.over();

	}



	@Override
	public String whisper(){

		// �x���E�̕�
		if( declaredFakeRole != agi.fakeRole ){
			declaredFakeRole = agi.fakeRole;
			return TemplateWhisperFactory.comingout(getMe(), agi.fakeRole);
		}

		// ���ݐ�̕�
		if( declaredPlanningAttackAgent != actionUI.attackAgent && actionUI.attackAgent != null ){
			declaredPlanningAttackAgent = actionUI.attackAgent;
			return TemplateWhisperFactory.attack( Agent.getAgent(actionUI.attackAgent) );
		}

		return TemplateWhisperFactory.over();
	}



	@Override
	public Agent vote() {

		// �錾�����ŉ������߂Ώ��Ă��Ԃ�
		Integer ppVoteAgentNo = getSuspectedPPVoteAgent();
		if( ppVoteAgentNo != null ){
			return Agent.getAgent(ppVoteAgentNo);
		}

		if( actionUI.voteAgent == null ){
			// ���[���錾�o���Ă��Ȃ��ꍇ�A���[���悤�Ǝv���Ă����҂ɓ��[
			if( planningVoteAgent == null ){
				return null;
			}
			return Agent.getAgent(planningVoteAgent);
		}
		return Agent.getAgent(actionUI.voteAgent);

	}


	/**
	 * �錾�����ŏ��Ă�ꍇ�A���[����擾����
	 * @return
	 */
	public Integer getSuspectedPPVoteAgent(){

		List<Integer> aliveWolfList = agi.getAliveWolfList();

		// ���ƂP�l����݂�Ώ��Ă��Ԃ�
		if( aliveWolfList.size() >= Common.getRestExecuteCount(agi.latestGameInfo.getAliveAgentList().size()) ){

			GameInfo gameInfo = agi.latestGameInfo;

			// ��i�s
			if( agi.latestGameInfo.getAliveAgentList().size() % 2 == 1 ){

				// �G�[�W�F���g���̓��[�\������擾����
				Integer[] voteTarget = new Integer[agi.gameSetting.getPlayerNum() + 1];
				for( Agent agent : gameInfo.getAliveAgentList() ){
					voteTarget[agent.getAgentIdx()] = agi.getSaidVoteAgent(agent.getAgentIdx());
					if( voteTarget[agent.getAgentIdx()] == null ){
						// ���錾�҂�����ꍇ�͕s�m��v�f������̂ł�߂Ă���
						return null;
					}
				}

				// �G�[�W�F���g���̔퓊�[�����擾����
				int[] voteReceiveNum = new int[agi.gameSetting.getPlayerNum() + 1];
				int[] voteReceiveNumWithoutMe = new int[agi.gameSetting.getPlayerNum() + 1];
				for( int i = 1; i < voteTarget.length; i++ ){
					// ���[�錾���J�E���g����
					if( voteTarget[i] != null ){
						voteReceiveNum[voteTarget[i]]++;
					}
					// �����ȊO�̓��[�錾���J�E���g����
					if( i != gameInfo.getAgent().getAgentIdx() && voteTarget[i] != null ){
						voteReceiveNumWithoutMe[voteTarget[i]]++;
					}
				}

				// �ő��[�̃G�[�W�F���g�̕[�����擾����
				int maxVoteCount = 0;
				for( int i = 1; i < voteTarget.length; i++ ){
					if( voteReceiveNumWithoutMe[i] > maxVoteCount ){
						maxVoteCount = voteReceiveNum[i];
					}
				}

				// �ő��[�𓾂Ă���G�[�W�F���g���擾
				for( int i = 1; i < voteReceiveNum.length; i++ ){
					if( voteReceiveNumWithoutMe[i] >= maxVoteCount ){
						// �����̓��[�������ƘT���݂�ꂻ���H
						if( aliveWolfList.contains(i) ){
							// ���ɍő��[�̐l�Ԃ�����Ȃ牟�����߂Ώ���
							for( int j = 1; j < voteReceiveNum.length; j++ ){
								if( voteReceiveNumWithoutMe[j] >= maxVoteCount && !aliveWolfList.contains(j) ){
									return j;
								}
							}
							// LW�����ɍő��[�����Ȃ�
							if( aliveWolfList.size() <= 1 ){
								for( int j = 1; j < voteReceiveNum.length; j++ ){
									// 1�[���̐l�Ԃ�����Ή�������Ń����_��
									if( voteReceiveNumWithoutMe[j] >= maxVoteCount - 1 && !aliveWolfList.contains(j) ){
										return j;
									}
								}
							}
						}
					}
				}

			}

		}

		return null;

	}


}
