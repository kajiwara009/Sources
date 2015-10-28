package jp.halfmoon.inaba.aiwolf.request;

import java.util.ArrayList;

import org.aiwolf.common.data.Agent;
import org.aiwolf.common.net.GameInfo;


/**
 * �s����p�u�ӌ��򂢁v
 */
public final class AttackObstacle extends AbstractActionStrategy {

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


		// �T�ɓ��[���Ă���҂��P������悤�v������
		for( int i = 1; i < voteTarget.length; i++ ){
			if( voteTarget[i] != null && args.agi.isWolf(voteTarget[i]) ){
				workReq = new Request(i);
				workReq.attack = 1.1;
				Requests.add(workReq);
			}
		}



		return Requests;
	}

}
