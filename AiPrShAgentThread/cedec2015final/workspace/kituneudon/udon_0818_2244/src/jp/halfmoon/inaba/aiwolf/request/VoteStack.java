package jp.halfmoon.inaba.aiwolf.request;

import java.util.ArrayList;

import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.net.GameInfo;


/**
 * �s����p�u�[�d�ˁv
 * �{���z�肷�铮���́A�ʂ�\��������݂���Ă��邱�ƁB
 */
public final class VoteStack extends AbstractActionStrategy {

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
			// �����ȊO�̓��[�錾���J�E���g����
			if( i != gameInfo.getAgent().getAgentIdx() && voteTarget[i] != null ){
				voteReceiveNum[voteTarget[i]]++;
			}
		}

		// ���[���ɉ����ē��[�v���x���グ��
		for( int i = 1; i < voteReceiveNum.length; i++ ){
			workReq = new Request(i);
			workReq.vote = 1.0 + voteReceiveNum[i] * 0.02 * (1 + voteReceiveNum[gameInfo.getAgent().getAgentIdx()] * 0.05);
			Requests.add(workReq);
		}


		// ���������l���̏���
		if( args.agi.latestGameInfo.getRole() == Role.POSSESSED ){
			for( Agent agent : gameInfo.getAliveAgentList() ){
				// ���������_�Ŋm����
				if( args.agi.selfRealRoleViewInfo.isFixBlack(agent.getAgentIdx()) ){
					// ���[���錾���Ă��邩
					if( voteTarget[agent.getAgentIdx()] != null ){
						// �T�l�Ɠ����ꏊ�ɓ��[����
						workReq = new Request(voteTarget[agent.getAgentIdx()]);
						workReq.vote = 1.2;
						Requests.add(workReq);
					}
				}
			}
		}


		return Requests;

	}

}
