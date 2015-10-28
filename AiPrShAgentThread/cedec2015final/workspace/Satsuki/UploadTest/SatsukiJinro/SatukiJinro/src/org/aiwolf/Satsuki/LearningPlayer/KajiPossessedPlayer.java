package org.aiwolf.Satsuki.LearningPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import java.util.Set;

import org.aiwolf.Satsuki.LearningPlayer.AbstractKajiWolfSideAgent;
import org.aiwolf.Satsuki.lib.MyGameInfo;
import org.aiwolf.Satsuki.lib.Pattern;
import org.aiwolf.Satsuki.lib.PatternMaker;
import org.aiwolf.Satsuki.lib.PossessedFakeRoleChanger;
import org.aiwolf.Satsuki.lib.WolfFakeRoleChanger;
import org.aiwolf.Satsuki.reinforcementLearning.COPercent;
import org.aiwolf.Satsuki.reinforcementLearning.COtimingNeo;
import org.aiwolf.Satsuki.reinforcementLearning.ReinforcementLearning;
import org.aiwolf.Satsuki.reinforcementLearning.WolfRolePattern;
import org.aiwolf.client.lib.TemplateTalkFactory;
import org.aiwolf.client.lib.Utterance;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Judge;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Species;
import org.aiwolf.common.data.Talk;
import org.aiwolf.common.data.Vote;
import org.aiwolf.common.net.GameInfo;
import org.aiwolf.common.net.GameSetting;

public class KajiPossessedPlayer extends AbstractKajiWolfSideAgent 
{
	PossessedFakeRoleChanger changer;
	int coFlag_NTurn = 0;
	int coFlag_Turn = 0;
	
	@Override
	public void initialize(GameInfo gameInfo, GameSetting gameSetting) 
	{
		super.initialize(gameInfo, gameSetting);
		//�J�~���O�A�E�g��������������_���ɐݒ�(0�Ȃ�����o�߂ł̓J�~���O�A�E�g���Ȃ�)
	
		doComingout = true;
		
		if (fakeRole == Role.SEER)
		{
			coFlag_NTurn = coPercent.getMaxFlag_Possessed_NTurn();
			coFlag_Turn = coPercent.getMaxFlag_Possessed_Turn();	
		}
	}


	@Override
	protected void setFakeDivineJudge() 
	{
		setTemplateFakeDivineJudge();
	}


	@Override
	public void dayStart()
	{
		super.dayStart();	
		if (isComingout)
		{
			switch (fakeRole) 
			{
			//�肢�t�x��̏ꍇ�C2���ڈȍ~fakeJudge�������
			case SEER:
				if(getDay() >= 2){
					setFakeDivineJudge();
				}
				break;

			//��\���x��̏ꍇ�C�P�����ꂽAgent�������fakeJudge�������
			case MEDIUM:
				if(getLatestDayGameInfo().getExecutedAgent() != null){
					setFakeInquestJudge(getLatestDayGameInfo().getExecutedAgent());
				}
				break;

			//���l�x��̏ꍇ�C�������Ȃ�
			case VILLAGER:
				doComingout = false;
				break;
			}			
		}
		if (fakeRole == Role.SEER)
		{
			if ((coFlag_Turn & COPercent.TURN_0) != 0 && getDay() == 0) priorityComingout = true;
			if ((coFlag_Turn & COPercent.TURN_1) != 0 && getDay() == 1) priorityComingout = true;
			if ((coFlag_Turn & COPercent.TURN_2) != 0 && getDay() == 2) priorityComingout = true;
			if ((coFlag_Turn & COPercent.TURN_3) != 0 && getDay() == 3) priorityComingout = true;
			if ((coFlag_Turn & COPercent.TURN_4) != 0 && getDay() == 4) priorityComingout = true;			
		}
	}

	@Override
	protected void setFakeInquestJudge(Agent executedAgent) 
	{
		setTemplateFakeInquestJudge();
	}
	
	// rpp,pp�v���C��
	public Agent getVoteRPPAgent()
	{
		Agent target = null;
		List<Vote> votes = advanceGameInfo.getVoteList(getDay());
		List<Agent> voteAgents = new ArrayList<Agent>();
		
		for(Vote vote: votes)
		{
			// ���b�҂��l�TCO + �܂��ǉ�����Ă��Ȃ��L��
			if(coWolfPlayers.contains(vote.getAgent()) && voteAgents.contains(vote.getTarget()) == false)
			{
				List<Pattern> excludePatterns = new ArrayList<Pattern>(myPatterns);
				
				// ���m�̃p�^�[�������O
				PatternMaker.RemoveEnemyPattern(excludePatterns, vote.getAgent(), false);
				
				// ���m�̃p�^�[�������O
				PatternMaker.RemoveEnemyPattern(excludePatterns, vote.getTarget(), true);
			
				// ����ŉ����̃p�^�[��������Α����R����Ȃ�
				if (excludePatterns.size() != 0)
				{
					voteAgents.add(vote.getTarget());					
				}
			}
		}
		target = this.getMostVoteAgent(voteAgents);
		
		return target;
	}

	@Override
	public void setVoteTarget() 
	{
		// RPP or PP
		if (coWolfPlayers.size() != 0)
		{
			Agent target = getVoteRPPAgent();	
			if (target != null) return;
		}
		List<Pattern> pattern = new ArrayList<Pattern>(this.otherPatterns);
		setVoteTargetTemplate(pattern);
	}


	@Override
	void updatePreConditionQVal(boolean isVillagerWin)
	{
		//�U��E��CO
		updateCOElements(isVillagerWin);
	}

	@Override
	protected void initializeFakeRole() 
	{
		Map<PossessedFakeRoleChanger, Double> map = ld.getPossessedFakeRoleChanger();
		changer = selectSoftMaxTarget(map);
		fakeRole = changer.getInitial();
	}

	@Override
	public void comingoutTalkDealing(Talk talk, Utterance utterance)
	{
		super.comingoutTalkDealing(talk, utterance);
		
		// �T�̂Ƃ�(���lCO�̎����@����)
		if (utterance.getRole() == Role.WEREWOLF || utterance.getRole() == Role.POSSESSED)
		{
			List<Pattern> excludePatterns = new ArrayList<Pattern>(myPatterns);
			
			// ���m�̃p�^�[�������O
			PatternMaker.RemoveEnemyPattern(excludePatterns, talk.getAgent(), false);
			
			// ���m�ł͂Ȃ��ARPP�ȏオ�\��
			if (excludePatterns.size() == 0 && canRPP())
			{
				if (coWolfPlayers.contains(talk.getAgent()) == false)
				{
					coWolfPlayers.add(talk.getAgent());
					bRealCo = true;
				}				
			}
		}
	}

	@Override
	public String getComingoutText() 
	{
		// �{CO�����I��
		if (bRealCoFinish)
		{
			return null;
		}
		
		// �{CO���s��
		if (bRealCo)
		{
			bRealCoFinish = true;
			return TemplateTalkFactory.comingout(getMe(), getMyRole());
		}
		/*
		 * �J�~���O�A�E�g��������ɂȂ�
		 * ���ɓ����\�͎�CO���o��
		 * �l�T��������
		 * ���[��ɑI�΂ꂻ���i�S�̂�2/3�����[���S���[����1/4�ȏオ�����ɓ��[�j
		 */
		if(isComingout || !doComingout)
		{
			return null;
		}
		else
		{
			if (priorityComingout)
			{
				return comingoutFakeRole();				
			}
			// �����ɂ��J�~���O�A�E�g
			if(coTiming.getDay() == getDay())
			{
				return comingoutFakeRole();
			}

			// �UCO�o��
			// �{���̉\���������B�s���������B
			if(true)
			{
				if (fakeRole != Role.SEER || (coFlag_Turn & coPercent.OPPOSION) != 0)
				{
					Map<Agent, Role> comingoutMap = advanceGameInfo.getComingoutMap();
					for(Entry<Agent, Role> set: comingoutMap.entrySet())
					{
						if(set.getValue() == fakeRole && !set.getKey().equals(getMe()))
						{
							return comingoutFakeRole();
						}
					}					
				}
			}

			// �l�T������
			if (fakeRole != Role.SEER || (coFlag_Turn & coPercent.FIND_WOLF) != 0)
			{
				for(Judge judge: notToldjudges)
				{
					if(judge.getResult() == Species.WEREWOLF)
					{
						return comingoutFakeRole();
					}
				}
			}

			// ���[��ɑI�΂ꂻ��
			if(true)
			{
				List<Vote> votes = advanceGameInfo.getVoteList(getDay());

				int voteToMe = 0;
				for(Vote vote: votes)
				{
					if(vote.getTarget().equals(getMe()))
					{
						voteToMe++;
					}
				}
				if((double)votes.size() * 1.5 > getLatestDayGameInfo().getAliveAgentList().size() && (double)voteToMe * 4 > votes.size())
				{
					return comingoutFakeRole();
				}
				else if (voteToMe >= 3)
				{
					return comingoutFakeRole();					
				}
			}
			
			// �l�T���Ɛ��ꂽ
			if(true)
			{
				for(Judge judge: advanceGameInfo.getInspectJudges())
				{
					if(getMe().equals(judge.getTarget()) && judge.getResult() == Species.WEREWOLF)
					{
						return comingoutFakeRole();
					}
				}
			}
		}
		return null;
	}
	@Override
	void updateCOElements(boolean isVillagerWin) 
	{
		Map<COtimingNeo, Double> map = getCOMap();
		double q = map.get(coTiming);
		double reward = (isVillagerWin)? 100.0: 0;
		double learnedQ = ReinforcementLearning.reInforcementLearn(q, reward, 0);
		map.put(coTiming, learnedQ);
		
		Map<PossessedFakeRoleChanger, Double> changerMap = ld.getPossessedFakeRoleChanger();
		double qW = changerMap.get(changer);
		// double learnedQW = ReinforcementLearning.reInforcementLearn(qW, reward, 0);
		changerMap.put(changer, learnedQ);

	}
}
