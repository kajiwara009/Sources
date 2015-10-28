package jp.halfmoon.inaba.aiwolf.request;

import java.util.ArrayList;
import java.util.List;

import jp.halfmoon.inaba.aiwolf.lib.CauseOfDeath;
import jp.halfmoon.inaba.aiwolf.lib.Common;
import jp.halfmoon.inaba.aiwolf.lib.Judge;
import jp.halfmoon.inaba.aiwolf.lib.ViewpointInfo;

import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Species;
import org.aiwolf.common.net.GameInfo;


/**
 * �s����p�u��{�P����p�v
 */
public final class BasicAttack extends AbstractActionStrategy {

	@Override
	public ArrayList<Request> getRequests(ActionStrategyArgs args) {

		GameInfo gameInfo = args.agi.latestGameInfo;

		ArrayList<Request> Requests = new ArrayList<Request>();
		Request workReq;



		// �G�[�W�F���g���̓��[�\������擾����
		Integer[] voteTarget = new Integer[args.agi.gameSetting.getPlayerNum() + 1];
		for( Agent agent : gameInfo.getAliveAgentList() ){
			voteTarget[agent.getAgentIdx()] = args.agi.getSaidVoteAgent(agent.getAgentIdx());
		}

		// �G�[�W�F���g���̔퓊�[�����擾����
		int[] voteReceiveNum = new int[args.agi.gameSetting.getPlayerNum() + 1];
		for( int i = 1; i < voteTarget.length; i++ ){
			if( voteTarget[i] != null && args.agi.isValidAgentNo(i) ){
				voteReceiveNum[voteTarget[i]]++;
			}
		}

		// �ő��[�̃G�[�W�F���g�̕[�����擾����
		int maxVoteCount = 0;
		for( int i = 1; i < voteTarget.length; i++ ){
			if( voteReceiveNum[i] > maxVoteCount ){
				maxVoteCount = voteReceiveNum[i];
			}
		}


		// ���Ă���[���ɉ����ďP���𔖂�����
		for( int i = 1; i < voteReceiveNum.length; i++ ){
			workReq = new Request(i);
			workReq.attack = Math.max( 1.00 - voteReceiveNum[i] * 0.08, 0.0 );
			Requests.add(workReq);
		}

		// �ő��[�𓾂Ă���G�[�W�F���g�͏P���悩�珜�O����
		for( int i = 1; i < voteReceiveNum.length; i++ ){
			if( voteReceiveNum[i] >= maxVoteCount ){
				workReq = new Request(i);
				workReq.attack = 0.05;
				Requests.add(workReq);
			}
		}


		// �e��E��CO�҂��擾
		List<Integer> seers = args.agi.getEnableCOAgentNo(Role.SEER);
		List<Integer> mediums = args.agi.getEnableCOAgentNo(Role.MEDIUM);
		List<Integer> bodyguards = args.agi.getEnableCOAgentNo(Role.BODYGUARD);
		List<Integer> villagers = args.agi.getEnableCOAgentNo(Role.VILLAGER);


		//TODO 100ms���᎞�ԑ���Ȃ��̂Ŗ����ł��B���߂�H
//		// �e�G�[�W�F���g�����񂾏ꍇ���V�~�����[�g
//		if( args.agi.latestGameInfo.getDay() > 0 ){
//
//			for( int i = 1; i < args.agi.gameSetting.getPlayerNum(); i++ ){
//
//				// ���S�ρE�T�͌��X��ΏۂȂ̂ŃX�L�b�v
//				if( args.agi.agentState[i].causeofDeath != CauseOfDeath.ALIVE ||
//				    args.agi.latestGameInfo.getRoleMap().get(Agent.getAgent(i)) == Role.WEREWOLF ){
//					continue;
//				}
//
//				// ���񂾏ꍇ�̎��������_�����肷��
//				ViewpointInfo future;
//				future = new ViewpointInfo(args.agi.selfViewInfo);
//				future.removeWolfPattern(i);
//				if( future.wolfsidePatterns.isEmpty() ){
//					// �������j�]����̂ŏP�����Ȃ�
//					workReq = new Request(i);
//					workReq.attack = 0.05;
//					Requests.add(workReq);
//				}
//
//
//				// ���񂾏ꍇ�̑��l���_�����肷��
//				// ����i�����񂾎��_���쐬�i�S���_�j
//				future = new ViewpointInfo(args.agi.allViewTrustInfo);
//				future.removeWolfPattern(i);
//
//				// �n��̘T��␔(�����̐����Ґ� - ���ݐ�܂ފm���̐�)
//				int grayAndBlackNum = 0;
//				for( int j = 1; j < voteReceiveNum.length; j++ ){
//					// ���ݐ�łȂ� ���� ���� ���� �����̊m�蔒�ł͂Ȃ�
//					if( j != i && args.agi.agentState[j].causeofDeath == CauseOfDeath.ALIVE && !future.isFixWhite(j) ){
//						grayAndBlackNum++;
//					}
//				}
//
//				// �����̎c�菈�Y�����擾
//				int tomorrowRestExucuteNum = Common.getRestExecuteCount(args.agi.latestGameInfo.getAliveAgentList().size() - 2);
//
//				//TODO �݂�悪�O���[���Ƃ��l����(����͔��J�E���g�Ōv�Z)
//				//TODO ���l�̌v�Z�������(����͐����Ōv�Z)
//				//TODO �肢�t�𐶂������ꍇ�͊D�����܂邱�Ƃ��l����
//
//				// �l�݂��i�n��̘T���̐����c��݂萔�ȉ��j
//				if( grayAndBlackNum <= tomorrowRestExucuteNum - 1 ){
//					// �l�ނ̂ŏP�����Ȃ�
//					workReq = new Request(i);
//					workReq.attack = 0.05;
//					Requests.add(workReq);
//				}
//
//			}
//
//		}


		// ���l�������Ă�����P�����Ȃ�
		for( Judge judge : args.agi.getSeerJudgeList() ){
			// �l�Ԃ̐肢�t���Ԉ����������o������
			if( !args.agi.isWolf(judge.agentNo) &&
			    (judge.result == Species.WEREWOLF) != args.agi.isWolf(judge.targetAgentNo) ){
				// �P���v����������
				workReq = new Request(judge.agentNo);
				workReq.attack = 0.5;
				Requests.add(workReq);
			}
		}


		// ����P��
		if( args.agi.latestGameInfo.getDay() == 1 ){

			//TODO ��CO�҂��݂ꂻ���Ȃ�}�~�͂͋C�ɂ��Ȃ��Ă���

			// ��0CO�Ȃ��͏P�����Ȃ�(�D�̐���_��)
			if( seers.isEmpty() ){
				for( int medium : mediums ){
					workReq = new Request( medium );
					workReq.attack = 0.1;
					Requests.add(workReq);
				}
			}

			// ��1CO�Ȃ��͏P�����Ȃ�(��q���\�z����邽��)
			if( seers.size() == 1 ){
				workReq = new Request( seers.get(0) );
				workReq.attack = 0.1;
				Requests.add(workReq);
			}

		}


		//TODO ��CO�҂��݂ꂻ���Ȃ�}�~�͂͋C�ɂ��Ȃ��Ă���

		// GJ���o�Ă���ꍇ
		if( args.agi.latestGameInfo.getDay() >= 2 && args.agi.latestGameInfo.getAttackedAgent() == null ){
			// �P����̌��𓾂�
			List<Integer> maxVoteAgentList = Common.getMaxVoteAgentNo( args.agi.latestGameInfo.getAttackVoteList() );

			// ���݂ƒ݂�̔��̉\�����Ȃ�
			if( maxVoteAgentList.indexOf( args.agi.latestGameInfo.getExecutedAgent().getAgentIdx() ) == -1 ){
				// GJ�ʒu�͑����ďP�����Ȃ�
				for( Integer agent : maxVoteAgentList ){
					workReq = new Request( agent );
					workReq.attack = 0.2;
					Requests.add(workReq);
				}
				// ����P�����Ȃ�
				for( int medium : mediums ){
					workReq = new Request( medium );
					workReq.attack = 0.2;
					Requests.add(workReq);
				}
//				// ���̏P���v����������
//				for( int seer : seers ){
//					workReq = new Request( seer );
//					workReq.attack = 0.1;
//					Requests.add(workReq);
//				}
//				for( int medium : mediums ){
//					workReq = new Request( medium );
//					workReq.attack = 0.1;
//					Requests.add(workReq);
//				}
			}
		}


		return Requests;
	}

}
