package jp.halfmoon.inaba.aiwolf.request;

import java.util.ArrayList;
import java.util.List;

import jp.halfmoon.inaba.aiwolf.guess.InspectedWolfsidePattern;
import jp.halfmoon.inaba.aiwolf.lib.Judge;

import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Species;
import org.aiwolf.common.net.GameInfo;


/**
 * �s����p�u��{���p�v
 */
public final class BasicGuard extends AbstractActionStrategy {

	@Override
	public ArrayList<Request> getRequests(ActionStrategyArgs args) {

		GameInfo gameInfo = args.agi.latestGameInfo;

		ArrayList<Request> Requests = new ArrayList<Request>();
		Request workReq;



		List<Integer> seers = args.agi.getEnableCOAgentNo(Role.SEER);
		List<Integer> mediums = args.agi.getEnableCOAgentNo(Role.MEDIUM);

		// �U�p�^�[���̍ő�X�R�A���ŏ��̂��̂����߂�
		double minScore = Double.MAX_VALUE;
		for( int seer : seers ){
			InspectedWolfsidePattern wolfPattern = args.aguess.getMostValidWolfPattern(seer);
			InspectedWolfsidePattern posPattern = args.aguess.getMostValidPossessedPattern(seer);

			double score = ( wolfPattern != null ? wolfPattern.score : 0.0 ) + ( posPattern != null ? posPattern.score : 0.0 );

			minScore = Math.min(score, minScore);
		}

		// �U�X�R�A�̍����傫������U�ł�����
		int falseCount = 0;
		for( int seer : seers ){
			InspectedWolfsidePattern wolfPattern = args.aguess.getMostValidWolfPattern(seer);
			InspectedWolfsidePattern posPattern = args.aguess.getMostValidPossessedPattern(seer);
			double score = ( wolfPattern != null ? wolfPattern.score : 0.0 ) + ( posPattern != null ? posPattern.score : 0.0 );

			if( score > minScore * 1.6 ){
				falseCount++;
			}
		}

		// �P�l�����ċU�ł���
		if( falseCount == seers.size() - 1 ){
			// �^�ł�������̌�q����������
			for( int seer : seers ){
				InspectedWolfsidePattern wolfPattern = args.aguess.getMostValidWolfPattern(seer);
				InspectedWolfsidePattern posPattern = args.aguess.getMostValidPossessedPattern(seer);

				double score = ( wolfPattern != null ? wolfPattern.score : 0.0 ) + ( posPattern != null ? posPattern.score : 0.0 );

				if( Double.compare(score, minScore) == 0 ){
					workReq = new Request(seer);
					workReq.guard = 3.0;
					Requests.add(workReq);
				}else{
					workReq = new Request(seer);
					workReq.guard = 0.5;
					Requests.add(workReq);
				}
			}
		}


		//TODO ���Ґ��Ή��E�����@�œ���or�s�݂����������p�^�[���̑Ή�(�e�莋�_������΁A�����D�̑S���ɐF�����Ă��邩�Ŕ��f�\)
		// �d���I��������͌�q���Ȃ�
		for( int seer : seers ){
			// ��E��E����ȊO�̐F�����������l�O�����J�E���g
			int seerEnemyCnt = seers.size() - 1;
			int mediumEnemyCnt = ( mediums.size() > 1 ) ? (mediums.size() - 1) : 0;
			int hitGrayBlackCnt = 0;
			for( Judge judge : args.agi.getSeerJudgeList() ){
				if( judge.isEnable() &&
				    judge.agentNo == seer &&
				    judge.result == Species.WEREWOLF ){
					// ���肪���ȊO��
					if( args.agi.agentState[judge.targetAgentNo].comingOutRole == null ||
						(args.agi.agentState[judge.targetAgentNo].comingOutRole != Role.SEER && args.agi.agentState[judge.targetAgentNo].comingOutRole != Role.MEDIUM ) ){
						hitGrayBlackCnt++;
					}
				}
			}

			if( seerEnemyCnt + mediumEnemyCnt + hitGrayBlackCnt >= 4 ){
				workReq = new Request(seer);
				workReq.guard = 0.001;
				Requests.add(workReq);
			}
		}



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


		// ���Ă���[���ɉ����Č�q�𔖂�����
		for( int i = 1; i < voteReceiveNum.length; i++ ){
			workReq = new Request(i);
			workReq.guard = 1.00 - voteReceiveNum[i] * 0.03;
			Requests.add(workReq);
		}

		// �ő��[�𓾂Ă���G�[�W�F���g�͌�q�悩�珜�O����
		for( int i = 1; i < voteReceiveNum.length; i++ ){
			if( voteReceiveNum[i] >= maxVoteCount ){
				workReq = new Request(i);
				workReq.guard = 0.01;
				Requests.add(workReq);
			}
		}


		return Requests;
	}

}
