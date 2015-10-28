package jp.halfmoon.inaba.aiwolf.strategyplayer;

import java.util.List;

import jp.halfmoon.inaba.aiwolf.lib.DayInfo;
import jp.halfmoon.inaba.aiwolf.lib.Judge;

import org.aiwolf.client.lib.TemplateTalkFactory;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Role;


public class StrategyMedium extends AbstractBaseStrategyPlayer {


	@Override
	public String talk() {

		String workString;

		// ��CO�̏ꍇ
		if( !isCameOut ){

//			// ���CO���K�v�Ȃ���CO����
//			if( isAvoidance() ){
//				isCameOut = true;
//
//				// ���b
//				workString = TemplateTalkFactory.comingout(getMe(), Role.MEDIUM);
//				return workString;
//			}
//
//			// ����CO���K�v�Ȃ�CO����
//			if( isVoluntaryComingOut() ){
//				isCameOut = true;
//
//				// ���b
//				workString = TemplateTalkFactory.comingout(getMe(), Role.MEDIUM);
//				return workString;
//			}

			// CO����
			isCameOut = true;

			// ���b
			workString = TemplateTalkFactory.comingout(getMe(), Role.MEDIUM);
			return workString;
		}

		// CO�ς̏ꍇ
		if( isCameOut ){

			// ���񍐂̌��ʂ�񍐂���
			if( agi.reportSelfResultCount < agi.selfInquestList.size() ){

				Judge reportJudge = agi.selfInquestList.get( agi.selfInquestList.size() - 1 );

				// �񍐍ς݂̌����𑝂₷
				agi.reportSelfResultCount++;

				// ���b
				workString = TemplateTalkFactory.inquested( Agent.getAgent(reportJudge.targetAgentNo), reportJudge.result );
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


	/**
	 * �����I��CO���邩
	 * @return
	 */
	private boolean isVoluntaryComingOut(){

		// �e��E��CO�҂��擾
		List<Integer> seers = agi.getEnableCOAgentNo(Role.SEER);
		List<Integer> mediums = agi.getEnableCOAgentNo(Role.MEDIUM);
		List<Integer> bodyguards = agi.getEnableCOAgentNo(Role.BODYGUARD);

		// 2����
		if( getDay() >= 2 ){
			// CO����
			return true;
		}

		// ���x�肪����
		if( !mediums.isEmpty() ){
			// CO����
			return true;
		}

		// �S�I�o
		if( seers.size() >= 5 ||
		    bodyguards.size() >= 5 ||
		    seers.size() + bodyguards.size() >= 6 ){
			// CO����
			return true;
		}

		// ���ꂽ�i���Ȃ�CCO�A���Ȃ犚�܂�₷���̂ŏ�����h�~�j
		for( Judge judge : agi.getSeerJudgeList() ){
			if( judge.isEnable() && judge.targetAgentNo == getMe().getAgentIdx() ){
				// CO����
				return true;
			}
		}

		return false;

	}

}
