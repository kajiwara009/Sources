package org.aiwolf.Satsuki.LearningPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import java.util.Set;

import org.aiwolf.Satsuki.lib.EnemyCase;
import org.aiwolf.Satsuki.lib.MyGameInfo;
import org.aiwolf.Satsuki.lib.Pattern;
import org.aiwolf.Satsuki.lib.PatternMaker;
import org.aiwolf.Satsuki.reinforcementLearning.COtimingNeo;
import org.aiwolf.client.lib.TemplateTalkFactory;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Judge;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Species;
import org.aiwolf.common.data.Vote;
import org.aiwolf.common.net.GameInfo;
import org.aiwolf.common.net.GameSetting;

public abstract class AbstractGiftedPlayer extends AbstractKajiBasePlayer
{
	// �肢�ς݃G�[�W�F���g���i�[
	protected List<Agent> divinedAgents = new ArrayList<Agent>();
	
	//�܂��񍐂��Ă��Ȃ�judge
	List<Judge> notToldjudges = new ArrayList<Judge>();

	//���ɕ񍐂���judge
	List<Judge> toldjudges = new ArrayList<Judge>();

	//�J�~���O�A�E�g������
	boolean isComingout = false;

	COtimingNeo coTiming;

	@Override
	public void initialize(GameInfo gameInfo, GameSetting gameSetting) {
		super.initialize(gameInfo, gameSetting);
		Map<COtimingNeo, Double> map = getCOMap();
		coTiming = selectRandomTarget(map);
	}

	public boolean isJudged(Agent agent){

		Set<Agent> judgedAgents = new HashSet<Agent>();
		for(Judge judge: toldjudges){
			judgedAgents.add(judge.getTarget());
		}
		for(Judge judge: notToldjudges){
			judgedAgents.add(judge.getTarget());
		}

		if(judgedAgents.contains(agent)){
			return true;
		}else{
			return false;
		}

	}
	
	public Map<COtimingNeo, Double> getCOMap()
	{
		switch (getMyRole()) 
		{
		case SEER:
			return ld.getSeerCO();
		case MEDIUM:
			return ld.getMediumCO();
		case POSSESSED:
			return ld.getPossessedCO();
		case WEREWOLF:
			return ld.getWolfCO();
		default:
			return null;
		}
	}

	public boolean CheckDivine(Agent agent, List<Agent> wolfDetermineAgent, List<Agent> possessedDetermineAgent)
	{
		// �����ĂȂ�
		if (getLatestDayGameInfo().getAliveAgentList().contains(agent) == false) return false;
		
		// �������g
		if (agent == getMe()) return false;
		
		// ���łɐ肢�ς�
		if (divinedAgents.contains(agent)) return false;
		
		// �l�T�m��͐肤�K�v�������B(��D��ł͐肤�H/�V�R���l�ւ̑΍�����������E�E�E)
		if (wolfDetermineAgent.contains(agent)) return false;
		
		// ���l�m��͐肤�K�v�������B(��D��ł͐肤�H/�V�R���l�ւ̑΍�����������E�E�E)
		if (possessedDetermineAgent.contains(agent)) return false;
		
		return true;
	}
	
	// �肤�G�[�W�F���g�̎擾
	public Agent getDivineAgent(List<Pattern> patterns)
	{
		Random rand = new Random();
		
		// �l�T�A���l�m��G�[�W�F���g�͐��Ȃ��B
		List<Agent> wolfDetermineAgent = new ArrayList<Agent>(getLatestDayGameInfo().getAliveAgentList());
		List<Agent> possessedDetermineAgent = new ArrayList<Agent>(getLatestDayGameInfo().getAliveAgentList());
		PatternMaker.getDetermineEnemyAgent(patterns, wolfDetermineAgent, possessedDetermineAgent);
		
		// �]��CO�����擾
		List<Agent> grayAgents = new ArrayList<Agent>();
		
		// ���̐肢�t�̐肢���ʂ��擾
		// �X�y�����������ǋC�ɂ��Ȃ�
		List<Agent> seeredAgent = new ArrayList<Agent>();
		for(Judge judge: advanceGameInfo.getInspectJudges())
		{
			if (judge.getAgent().equals(getMe()))
			{
				seeredAgent.add(judge.getTarget());
			}
		}
		
		// �肢�t�p�^�[�����擾
		List<Pattern> thisSeerPattern = new ArrayList<Pattern>();
		int minEnemyNum = MyGameInfo.getMaxEnemyNum();
		for(Pattern pattern: patterns)
		{
			if (pattern.getSeerAgent() == getMe())
			{
				thisSeerPattern.add(pattern);
				if (pattern.getEnemyMap().size() < minEnemyNum)
				{
					minEnemyNum = pattern.getEnemyMap().size();
				}
			}
		}
		
		// gray�̃G�[�W�F���g���o��
		// 1�p�^�[�������Ȃ��AenemyMap���J���X�g���Ă���ꍇ,�G���X�g��gray�̐���gray���X�g�ɂȂ�B
		if (thisSeerPattern.size() == 1 && thisSeerPattern.get(0).getEnemyMap().size() == MyGameInfo.getMaxEnemyNum())
		{
			for(Entry<Agent, EnemyCase> em: thisSeerPattern.get(0).getEnemyMap().entrySet())
			{
				// ��������gray�Ȃ�gray�ɉ��Z
				if (getLatestDayGameInfo().getAliveAgentList().contains(em.getKey()) && em.getValue() == EnemyCase.gray)
				{
					grayAgents.add(em.getKey());
				}
			}
		}
		// �����p�^�[�����邩�AenemyMap���J���X�g���Ă��Ȃ��ꍇ�́A�����ғ��ŁA
		// 1 ���m�G�̐�
		// 2 ���m�G�̐�
		// 3 1,2�ɓ��Ă͂܂�Ȃ����j�b�g�Ő肢�ς݂łȂ���
		// �����߂āAgray���Z�o
		else
		{
			for(Agent agent:getLatestDayGameInfo().getAliveAgentList())
			{
				if (wolfDetermineAgent.contains(agent)) continue;
				if (possessedDetermineAgent.contains(agent)) continue;
				if (seeredAgent.contains(agent)) continue;
				
				grayAgents.add(agent);
			}
		}
		
		// pattern�̍ŏ��̓G�̐����o���B
		// 3�ȉ� ��CO�Ȃ���gray��肤�B
		// 4 �� CO��gray��肤�B
		List<Agent> coAgents = new ArrayList<Agent>();
		for(Entry<Agent, Role> set: advanceGameInfo.getComingoutMap().entrySet())
		{
			coAgents.add(set.getKey());
		}
		
		if (minEnemyNum == MyGameInfo.getMaxEnemyNum())
		{
			List<Agent> aliveList = new ArrayList<Agent>(getLatestDayGameInfo().getAliveAgentList());
			aliveList.removeAll(coAgents);
			grayAgents.removeAll(aliveList);
		}
		else
		{
			grayAgents.removeAll(coAgents);
		}
		
		if (grayAgents.size() != 0)
		{
			return grayAgents.get(0);
		}
		
		// ====================================================================
		// �肢�t�𔻒�
		List<Agent> priorityAgent = new ArrayList<Agent>();
		for(Entry<Agent, Role> param: advanceGameInfo.getComingoutMap().entrySet())
		{
			if (param.getValue() == Role.SEER)
			{
				priorityAgent.add(param.getKey());
			}
		}
	
		for(Agent agent: priorityAgent)
		{
			if (CheckDivine(agent,wolfDetermineAgent,possessedDetermineAgent) == false) continue;
			return agent;
		}

		// ====================================================================
		// ������}�t�𔻒�
		priorityAgent.clear();
		for(Entry<Agent, Role> param: advanceGameInfo.getComingoutMap().entrySet())
		{
			if (param.getValue() == Role.MEDIUM)
			{
				priorityAgent.add(param.getKey());
			}
		}

		if (priorityAgent.size() >= 2)
		{
			for(Agent agent: priorityAgent)
			{
				if (CheckDivine(agent,wolfDetermineAgent,possessedDetermineAgent) == false) continue;
				return agent;		
			}				
		}

		// ====================================================================
		// �܂�����Ă��Ȃ����j�b�g����K���ɁE�E�E		
		
		priorityAgent.clear();
		for(Agent agent: getLatestDayGameInfo().getAliveAgentList())
		{
			if (CheckDivine(agent,wolfDetermineAgent,possessedDetermineAgent) == false)
			{
				continue;
			}
			priorityAgent.add(agent);
		}
		
		if (priorityAgent.size() > 0)
		{
			return priorityAgent.get(rand.nextInt(priorityAgent.size()));
		}

		// ====================================================================
		// �N�ł�������E�E�E		
		priorityAgent.clear();
		for(Agent agent: getLatestDayGameInfo().getAliveAgentList())
		{
			priorityAgent.add(agent);
		}
		
		if (priorityAgent.size() > 0)
		{
			return priorityAgent.get(rand.nextInt(priorityAgent.size()));
		}
		
		if(MyGameInfo.IS_PRINT()) System.out.println("�肢���s");
		return null;
	}

	public List<Pattern> getHypotheticalPatterns(List<Pattern> originPatterns, Judge judge){
		List<Pattern> hypotheticalPatterns = PatternMaker.clonePatterns(originPatterns);
		PatternMaker.updateJudgeData(hypotheticalPatterns, judge);
		return hypotheticalPatterns;
	}

	public String getTemplateComingoutText(){
		/*
		 * �J�~���O�A�E�g��������ɂȂ�
		 * ���ɓ����\�͎�CO���o��
		 * �l�T��������
		 * ���[��ɑI�΂ꂻ���i�S�̂�2/3�����[���S���[���Ń}�b�N�X�������܂��͂R�[�ȏ�j
		 */
		if(isComingout)
		{
			return null;
		}
		else
		{
			//�����ɂ��J�~���O�A�E�g
			if(getDay() == coTiming.getDay() && coTiming.doComingout())
			{
				isComingout = true;
				return TemplateTalkFactory.comingout(getMe(), getMyRole());
			}

			//�UCO�o��
			if(coTiming.isAgainst())
			{
				Map<Agent, Role> comingoutMap = advanceGameInfo.getComingoutMap();
				for(Entry<Agent, Role> set: comingoutMap.entrySet())
				{
					if(set.getValue() == getMyRole() && !set.getKey().equals(getMe()))
					{
						isComingout = true;
						return TemplateTalkFactory.comingout(getMe(), getMyRole());
					}
				}
			}

			//�l�T������
			if(coTiming.isHasFoundWolf())
			{
				for(Judge judge: notToldjudges)
				{
					if(judge.getResult() == Species.WEREWOLF)
					{
						isComingout = true;
						return TemplateTalkFactory.comingout(getMe(), getMyRole());
					}
				}
			}

			//���[��ɑI�΂ꂻ��
			if(coTiming.isVoted())
			{
				List<Vote> votes = advanceGameInfo.getVoteList(getDay());
				if((double)votes.size() * 1.5 > getLatestDayGameInfo().getAliveAgentList().size())
				{
					int voteToMe = 0;
					for(Vote vote: votes){
						if(vote.getTarget().equals(getMe())){
							voteToMe++;
						}
					}
					if((double)voteToMe * 4 > votes.size() || voteToMe >= 3)
					{
						isComingout = true;
						return TemplateTalkFactory.comingout(getMe(), getMyRole());
					}
				}
			}
		}
		return null;
	}

}
