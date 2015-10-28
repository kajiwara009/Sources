package jp.halfmoon.inaba.aiwolf.strategyplayer;

import jp.halfmoon.inaba.aiwolf.lib.Judge;

import org.aiwolf.client.lib.TemplateTalkFactory;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Role;


public class StrategySeer extends AbstractBaseStrategyPlayer {


	@Override
	public String talk() {

		String workString;

		// ��CO�̏ꍇ
		if( !isCameOut ){
			// CO����
			isCameOut = true;

			// ���b
			workString = TemplateTalkFactory.comingout(getMe(), Role.SEER);
			return workString;
		}

		// CO�ς̏ꍇ
		if( isCameOut ){

			// ���񍐂̌��ʂ�񍐂���
			if( agi.reportSelfResultCount < agi.selfInspectList.size() ){

				Judge reportJudge = agi.selfInspectList.get( agi.selfInspectList.size() - 1 );

				// �񍐍ς݂̌����𑝂₷
				agi.reportSelfResultCount++;

				// ���b
				workString = TemplateTalkFactory.divined( Agent.getAgent(reportJudge.targetAgentNo), reportJudge.result );
				return workString;
			}

		}

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
			workString = TemplateTalkFactory.vote( Agent.getAgent(planningVoteAgent) );
			return workString;
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
