package jp.halfmoon.inaba.aiwolf.strategyplayer;

import java.util.List;

import jp.halfmoon.inaba.aiwolf.lib.CauseOfDeath;
import jp.halfmoon.inaba.aiwolf.lib.DayInfo;

import org.aiwolf.client.lib.TemplateTalkFactory;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Role;


public class StrategyBodyGuard extends AbstractBaseStrategyPlayer {


	@Override
	public String talk() {

		String workString;

		// ��CO�̏ꍇ
		if( !isCameOut ){

			//TODO ������s�����͗v�����ikajiAgent�͎�CO��݂肽����̂ŁA����h�������������ł͈���ɂȂ�j
			// ���CO���K�v�Ȃ���CO����
			if( isAvoidance() ){
				isCameOut = true;

				// ���b
				workString = TemplateTalkFactory.comingout(getMe(), Role.BODYGUARD);
				return workString;
			}

			// ����CO���K�v�Ȃ�CO����
			if( isVoluntaryComingOut() ){
				isCameOut = true;

				// ���b
				workString = TemplateTalkFactory.comingout(getMe(), Role.BODYGUARD);
				return workString;
			}

		}

		// CO�ς̏ꍇ
		if( isCameOut ){

			// ���񍐂̌��ʂ�񍐂���
			if( agi.reportSelfResultCount < agi.selfGuardRecent.size() ){

				int reportDay = agi.reportSelfResultCount + 1;
				Agent agent = Agent.getAgent( agi.selfGuardRecent.get(reportDay) );

				// �񍐍ς݂̌����𑝂₷
				agi.reportSelfResultCount++;

				// ���b
				workString = TemplateTalkFactory.guarded( agent );
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

		// �^�����b�����͂��擾���A�擾�ł��Ă���Θb��
		workString = getSuspicionTalkString();
		if( workString != null ){
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

		// ���x�肪���݁A���S�Ă̐l�O���S�I�o���Ă���
		if( bodyguards.size() > 0 &&
		    seers.size() + mediums.size() + bodyguards.size() >= 6 ){
			// CO����
			return true;
		}

		// ���x�肪����(����)�A�������҂U�l�ȉ�
		if( bodyguards.size() > 0 &&
		    agi.latestGameInfo.getAliveAgentList().size() <= 6 ){
			for( int bodyguard : bodyguards ){
				if( agi.agentState[bodyguard].causeofDeath == CauseOfDeath.ALIVE ){
					return true;
				}
			}
		}

		// GJ�񐔂��J�E���g
		int gjCount = 0;
		for( DayInfo dayInfo : agi.dayInfoList ){
			if( dayInfo.day >= 2 && dayInfo.attackAgentNo == null ){
				gjCount++;
			}
		}

		//TODO ���Ґ��Ή��E�������m�����Ȃǂ��l�����ׂ��H
		// GJ�񐔂��Q��ȏォ�iG16�ł͕��a�Q��łP�ꑝ���j
		if( gjCount >= 2 ){
			// ��1CO�łȂ����CO����
			if( seers.size() > 1 ){
				return true;
			}
		}

		return false;

	}

}
