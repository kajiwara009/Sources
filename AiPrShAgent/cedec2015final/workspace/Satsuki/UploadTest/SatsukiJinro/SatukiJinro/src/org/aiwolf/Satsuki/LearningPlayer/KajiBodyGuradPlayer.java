package org.aiwolf.Satsuki.LearningPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;

import org.aiwolf.Satsuki.LearningPlayer.AbstractKajiBasePlayer;
import org.aiwolf.Satsuki.lib.EnemyCase;
import org.aiwolf.Satsuki.lib.MyGameInfo;
import org.aiwolf.Satsuki.lib.Pattern;
import org.aiwolf.Satsuki.lib.PatternMaker;
import org.aiwolf.Satsuki.reinforcementLearning.AgentPattern;
import org.aiwolf.Satsuki.reinforcementLearning.Qvalues;
import org.aiwolf.Satsuki.reinforcementLearning.ReinforcementLearning;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Judge;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Species;

public class KajiBodyGuradPlayer extends AbstractKajiBasePlayer 
{
	@Override
	public String getJudgeText() 
	{
		return null;
	}

	@Override
	public String getComingoutText() 
	{
		return null;
	}

	@Override
	public void setVoteTarget() 
	{
		setVoteTargetTemplate(myPatterns);
	}

	// �肢�t��q�v�l
	public Agent guard_Seer(List<Agent> enemyAgent, List<Agent> blackAgent)
	{
		Random rand = new Random();
		Agent target = null;
		
		List<Agent> priorityAgent = new ArrayList<Agent>();
		for(Entry<Agent, Role> param: advanceGameInfo.getComingoutMap().entrySet())
		{
			if (
				param.getValue() == Role.SEER 
				&& enemyAgent.contains(param.getKey()) == false												// �G�m
				&& blackAgent.contains(param.getKey()) == false												// ��������󂯂Ă�����A�E���K�v��������(�܂��E����Ȃ�����)
				&& getLatestDayGameInfo().getAliveAgentList().contains(param.getKey())						// �����Ă��郆�j�b�g�������肵�Ȃ�
			)
			{
				priorityAgent.add(param.getKey());
			}
		}

		
		if (priorityAgent.size() > 0)
		{
			// "�������猩��"gray���ł������肢�t�����
			int maxGrayCount = -1;
			Agent maxEnemyCountAgent = null;
			
			for (Agent seerAgent: priorityAgent)
			{
				int graySize = 0;
				
				// ���̐肢�t�̐肢���ʂ��擾
				// �X�y�����������ǋC�ɂ��Ȃ�
				List<Agent> seeredAgent = new ArrayList<Agent>();
				for(Judge judge: advanceGameInfo.getInspectJudges())
				{
					if (judge.getAgent().equals(seerAgent))
					{
						seeredAgent.add(judge.getTarget());
					}
				}
				
				// �肢�t�p�^�[�����擾
				List<Pattern> thisSeerPattern = new ArrayList<Pattern>();
				for(Pattern pattern: myPatterns)
				{
					if (pattern.getSeerAgent() == seerAgent)
					{
						thisSeerPattern.add(pattern);
					}
				}
				
				// 1�p�^�[�������Ȃ��AenemyMap���J���X�g���Ă���ꍇ,�G���X�g��gray�̐���gray���X�g�ɂȂ�B
				if (thisSeerPattern.size() == 1 && thisSeerPattern.get(0).getEnemyMap().size() == MyGameInfo.getMaxEnemyNum())
				{
					for(Entry<Agent, EnemyCase> em: thisSeerPattern.get(0).getEnemyMap().entrySet())
					{
						// ��������gray�Ȃ�gray�ɉ��Z
						if (getLatestDayGameInfo().getAliveAgentList().contains(em.getKey()) && em.getValue() == EnemyCase.gray)
						{
							graySize++;
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
					List<Agent> wolfDetermineAgent = new ArrayList<Agent>(getLatestDayGameInfo().getAliveAgentList());
					List<Agent> possessedDetermineAgent = new ArrayList<Agent>(getLatestDayGameInfo().getAliveAgentList());
					
					// �擾�J�n
					PatternMaker.getDetermineEnemyAgent(thisSeerPattern, wolfDetermineAgent, possessedDetermineAgent);

					for(Agent agent:getLatestDayGameInfo().getAliveAgentList())
					{
						if (wolfDetermineAgent.contains(agent)) continue;
						if (possessedDetermineAgent.contains(agent)) continue;
						if (seeredAgent.contains(agent)) continue;
						
						++graySize;
					}
				}
				
				if (graySize > maxGrayCount)
				{
					maxGrayCount = graySize;
					maxEnemyCountAgent = seerAgent;
				}
			}
			
			// �c��Gray��2�l�ȉ��Ȃ��q���Ȃ�
			if (maxGrayCount > 2)
			{
				target = maxEnemyCountAgent;				
			}
		}	
		return target;	
	}

	@Override
	public Agent guard() 
	{
		Random rand = new Random();
		Agent target = null;

		// �G�m��G�[�W�F���g
		List<Agent> enemyAgent = new ArrayList<Agent>(getLatestDayGameInfo().getAliveAgentList());
		PatternMaker.getDetermineEnemyAgent(myPatterns, enemyAgent);

		List<Agent> generalenemyAgent = new ArrayList<Agent>(getLatestDayGameInfo().getAliveAgentList());
		PatternMaker.getDetermineEnemyAgent(generalPatterns, generalenemyAgent);
		
		// (���m�̓G�m�ȊO����)������������G�[�W�F���g
		List<Agent> blackAgent = new ArrayList<Agent>();
		for(Judge judge: advanceGameInfo.getInspectJudges())
		{
			if (judge.getResult() == Species.WEREWOLF && blackAgent.contains(judge.getAgent()) == false && generalenemyAgent.contains(judge.getAgent()) == false)
			{
				blackAgent.add(judge.getAgent());
			}
		}
		
		
		// �肢�t��q�v�l
		target = guard_Seer(enemyAgent, blackAgent);

		if (target != null)
		{
			return target;
		}
		
		
		// �K���Ɏ��
		List<Agent> priorityAgent = new ArrayList<Agent>();
		for(Agent agent: getLatestDayGameInfo().getAliveAgentList())
		{
			if (enemyAgent.contains(agent))
			{
				continue;
			}
			priorityAgent.add(agent);
		}
		
		if (priorityAgent.size() > 0)
		{
			target = priorityAgent.get(rand.nextInt(priorityAgent.size()));
			
			if (target != null)
			{
				return target;
			}
		}
		
		return null;
	}
}
