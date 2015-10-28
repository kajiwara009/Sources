package jp.halfmoon.inaba.aiwolf.strategyplayer;

import org.aiwolf.client.lib.TemplateTalkFactory;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Role;


public class StrategyVillager extends AbstractBaseStrategyPlayer {


	@Override
	public String talk() {

		String workString;


		// ���[��������Ă��Ȃ��ꍇ�A�V�������[���b��
		if( declaredPlanningVoteAgent == null ){
			// ���[���ύX����
			actionUI.voteAgent = planningVoteAgent;
			declaredPlanningVoteAgent = planningVoteAgent;

			// ���b
			String ret = TemplateTalkFactory.vote( Agent.getAgent(planningVoteAgent) );
			return ret;
		}

		// �^�����T�̐l���ȏ㌾���Ă��Ȃ���Θb��
		if( agi.talkedSuspicionAgentList.size() < agi.gameSetting.getRoleNumMap().get(Role.WEREWOLF) ){
			// �^�����b�����͂��擾���A�擾�ł��Ă���Θb��
			workString = getSuspicionTalkString();
			if( workString != null ){
				return workString;
			}
		}

		// ���[���ύX����ꍇ�A�V�������[���b��
		if( declaredPlanningVoteAgent != planningVoteAgent ){
			// ���[���ύX����
			actionUI.voteAgent = planningVoteAgent;
			declaredPlanningVoteAgent = planningVoteAgent;

			// ���b
			String ret = TemplateTalkFactory.vote( Agent.getAgent(planningVoteAgent) );
			return ret;
		}

		// �M�p���b�����͂��擾���A�擾�ł��Ă���Θb��
		workString = getTrustTalkString();
		if( workString != null ){
			return workString;
		}

		// �b�����������ꍇ�Aover��Ԃ�
		return TemplateTalkFactory.over();

	}



}
