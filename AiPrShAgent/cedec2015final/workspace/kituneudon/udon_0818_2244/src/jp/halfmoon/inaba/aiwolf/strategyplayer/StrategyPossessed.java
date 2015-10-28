package jp.halfmoon.inaba.aiwolf.strategyplayer;

import java.util.ArrayList;
import java.util.List;

import jp.halfmoon.inaba.aiwolf.lib.AgentState;
import jp.halfmoon.inaba.aiwolf.lib.CauseOfDeath;
import jp.halfmoon.inaba.aiwolf.lib.Judge;
import jp.halfmoon.inaba.aiwolf.lib.ViewpointInfo;

import org.aiwolf.client.lib.TemplateTalkFactory;
import org.aiwolf.client.lib.TemplateTalkFactory.TalkType;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Species;
import org.aiwolf.common.net.GameInfo;


public class StrategyPossessed extends AbstractBaseStrategyPlayer {


	@Override
	public void dayStart() {

		super.dayStart();

		// �x�蔻��̒ǉ�
		if( agi.latestGameInfo.getDay() > 0 ){
			addFakeSeerJudge();
		}

	}

	@Override
	public String talk() {

		// PP���̔���
		if( agi.isEnablePowerPlay_Possessed() ){
			// ���[���ύX����
			actionUI.voteAgent = planningVoteAgent;
			declaredPlanningVoteAgent = planningVoteAgent;

			// ���b
			String ret = TemplateTalkFactory.vote( Agent.getAgent(planningVoteAgent) );
			return ret;
		}

		// ��CO�̏ꍇ
		if( !isCameOut ){
			isCameOut = true;

			String ret = TemplateTalkFactory.comingout( getMe(), agi.fakeRole );
			return ret;
		}

		// CO�ς̏ꍇ
		if( isCameOut ){

			// ���񍐂̌��ʂ�񍐂���
			if( agi.reportSelfResultCount < agi.selfInspectList.size() ){

				Judge reportJudge = agi.selfInspectList.get( agi.selfInspectList.size() - 1 );

				// �񍐍ς݂̌����𑝂₷
				agi.reportSelfResultCount++;

				// ���b
				String ret = TemplateTalkFactory.divined( Agent.getAgent(reportJudge.targetAgentNo), reportJudge.result );
				return ret;
			}

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


	/**
	 * �肢�����ǉ�����
	 */
	private void addFakeSeerJudge(){

		GameInfo gameInfo = agi.latestGameInfo;

		// �����E���茋�ʂ̉��ݒ�
		int inspectAgentNo = latestRequest.getMaxInspectRequest().agentNo;
		Species result = Species.HUMAN;

		// �P�����ꂽ�G�[�W�F���g�̎擾
		Agent attackedAgent = agi.latestGameInfo.getAttackedAgent();


		// �j�]��
		if( agi.selfViewInfo.wolfsidePatterns.isEmpty() ){
			// �����Ă��鋶���_�̊m����T��
			List<Integer> whiteList = new ArrayList<Integer>();
			for( int i = 1; i <= agi.gameSetting.getPlayerNum(); i++ ){
				if( agi.agentState[i].causeofDeath == CauseOfDeath.ALIVE && agi.selfRealRoleViewInfo.isFixWhite(i) ){
					whiteList.add(i);
				}
			}
			// �m���ɍ��o��
			if( !whiteList.isEmpty() ){
				inspectAgentNo = whiteList.get(0);
				result = Species.WEREWOLF;

				Judge newJudge = new Judge( getMe().getAgentIdx(),
                        inspectAgentNo,
                        result,
                        null );

				agi.addFakeSeerJudge(newJudge);
				return;
			}

		}

		//TODO ��U����ŏ������Ĕ��f
//		// �����͍��o���ŋ��A�s(4CO�ȉ��̏ꍇ)
//		List<Integer> seers = agi.getEnableCOAgentNo(Role.SEER);
//		List<Integer> mediums = agi.getEnableCOAgentNo(Role.MEDIUM);
//		if( agi.latestGameInfo.getDay() == 1 && seers.size() + mediums.size() <= 4 ){
//			result = Species.WEREWOLF;
//		}

		// �����o�����ꍇ�̎��_�����肷��
		ViewpointInfo future = new ViewpointInfo(agi.selfViewInfo);
		future.removeWolfPattern(inspectAgentNo);

		// ���o���œ��󂪔j�]����ꍇ�A���o�����s��
		if( future.wolfsidePatterns.isEmpty() ){
			result = Species.WEREWOLF;
		}

		// �肨���Ƃ����悪���܂ꂽ
		if( attackedAgent != null && attackedAgent.getAgentIdx() == inspectAgentNo ){
			// ���ݐ�ɂ͐l�Ԕ�����o��
			result = Species.HUMAN;
		}

		// �����҂T���ȉ��i�����P�l�݂��PP�m��j
		if( gameInfo.getAliveAgentList().size() <= 5 ){

			// �����Ă���m����T��
			List<Integer> whiteList = new ArrayList<Integer>();
			for( int i = 1; i <= agi.gameSetting.getPlayerNum(); i++ ){
				if( agi.agentState[i].causeofDeath == CauseOfDeath.ALIVE && agi.selfRealRoleViewInfo.isFixWhite(i) ){
					whiteList.add(i);
				}
			}

			// ���������_�̊m���ɍ����o��
			for( Integer white : whiteList ){
				// �����莋�_�ŐF���m�肵�Ă���ꍇ�͐��Ȃ�
				if( agi.selfViewInfo.isFixWhite(white) ||
				    agi.selfViewInfo.isFixBlack(white) ){
					continue;
				}

				// �����o�����ꍇ�̎��_�����肷��
				future = new ViewpointInfo(agi.selfViewInfo);
				future.removePatternFromJudge( getMe().getAgentIdx(), white, Species.WEREWOLF );

				// �����o���Ĕj�]���Ȃ��Ȃ炻���ɍ����o��
				if( !future.wolfsidePatterns.isEmpty() ){
					inspectAgentNo = white;
					result = Species.WEREWOLF;
					break;
				}
			}

		}

		Judge newJudge = new Judge( getMe().getAgentIdx(),
		                            inspectAgentNo,
		                            result,
		                            null );

		agi.addFakeSeerJudge(newJudge);

	}



}
