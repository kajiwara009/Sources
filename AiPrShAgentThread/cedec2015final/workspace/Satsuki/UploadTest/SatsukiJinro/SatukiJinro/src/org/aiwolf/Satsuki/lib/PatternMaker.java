package org.aiwolf.Satsuki.lib;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Judge;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.net.GameInfo;
import org.aiwolf.common.net.GameSetting;
import org.aiwolf.Satsuki.lib.AdvanceGameInfo;
import org.aiwolf.Satsuki.lib.EnemyCase;
import org.aiwolf.Satsuki.lib.Pattern;

/**
 * List<Pattern>�̍X�V�C�g���ɗp����BUtil
 * @author kengo
 *
 */
public class PatternMaker {
	/**
	 * CO�̔��������Ƀp�^�[�����쐬�C�X�V����D
	 * @param patternList
	 * @param coAgent
	 * @param coRole
	 * @param gameInfo
	 * @return
	 */
	//�K�v�f�[�^�F���̃p�^�[�����X�g�CCO�����G�[�W�F���g�Ɩ�E�C���l���X�g�C
	public static void extendPatternList(List<Pattern> patterns, Agent coAgent, Role coRole, AdvanceGameInfo advanceGameInfo)
	{
		List<Pattern> newPatterns = new ArrayList<Pattern>();

		long start = System.currentTimeMillis();
		for(Pattern pattern: patterns)
		{
			boolean isExistGenuineCO = false;
			switch (coRole) 
			{
			case SEER:
				if(pattern.getSeerAgent() != null)
				{
					if(pattern.getSeerAgent().equals(coAgent))
					{
						return;
					}
					isExistGenuineCO = true;
				}
				break;

			case MEDIUM:
				if(pattern.getMediumAgent() != null)
				{
					if(pattern.getMediumAgent().equals(coAgent))
					{
						return;
					}
					isExistGenuineCO = true;
				}
				break;
				
			case BODYGUARD:
				if(pattern.getBodyGuardAgent() != null)
				{
					if(pattern.getBodyGuardAgent().equals(coAgent))
					{
						return;
					}
					isExistGenuineCO = true;
				}
				break;
			}
			// �^�\�͎҂�����ꍇ
			if(isExistGenuineCO)
			{
				Pattern newPattern = pattern.clone();
				// enemyMap�ɂ܂������Ă��Ȃ�
				if(!newPattern.getEnemyMap().containsKey(coAgent))
				{
					// ���mAgent�ɓ����Ă���
					if(newPattern.getWhiteAgentSet().contains(coAgent))
					{
						newPattern.getEnemyMap().put(coAgent, EnemyCase.white);
					}
					// ���mAgent�ɓ����Ă��Ȃ�
					else
					{
						newPattern.getEnemyMap().put(coAgent, EnemyCase.gray);
					}
				}
				newPatterns.add(newPattern);
			}
			// �^�\�͎҂����Ȃ�
			else
			{
				// �V����CO�҂�^�Ƃ���Pattern
				Pattern newPattern1 = pattern.clone();
				// �V����CO�҂��D�F�Ƃ���Pattern
				Pattern newPattern2 = pattern.clone();
				// newPattern1�ɂ���
				switch (coRole) 
				{
				case SEER:
					newPattern1.setSeerAgent(coAgent);
					break;
				case MEDIUM:
					newPattern1.setMediumAgent(coAgent);
					break;
				case BODYGUARD:
					newPattern1.setBodyGuardAgent(coAgent);
					break;
				}

				// newPattern2�ɂ���
				newPattern2.getEnemyMap().put(coAgent, EnemyCase.gray);
				
				newPatterns.add(newPattern1);
				newPatterns.add(newPattern2);
			}
		}
		long end = System.currentTimeMillis();
		
		if (end - start >= 300)
		{
			System.out.println("����");
		}

		// newPatterns�̖����Ȃ����̂�pattern�ɓ����D
		
		// clonePatterns�͉����Ă�H @�Ή�
		// List<Pattern> clonePatterns = new ArrayList<Pattern>(newPatterns);
		start = System.currentTimeMillis();
		removeContradictPatterns(newPatterns);
		end = System.currentTimeMillis();
		
		if (end - start >= 300)
		{
			System.out.println("����");
		}
		
		patterns.clear();
		patterns.addAll(newPatterns);
		return;
	}

	// �P���ɂ���Ď��񂾃v���C���[�𔒊m�C�^�\�͎҂ɂ���Ĕ��m�����m
	/**
	 * �P�����ꂽ�v���C���[�𔒊m�ɂ���
	 * @param patternList
	 * @param attackedAgent
	 */
	public static void updateAttackedData(List<Pattern> patterns, Agent attackedAgent)
	{
		for(Pattern pattern: patterns)
		{
			if(attackedAgent != null)
			{				
				pattern.getWhiteAgentSet().add(attackedAgent);

				// �K�v�ȋ@������̂Œǉ�@�Ή�
				pattern.getAliveAgents().remove(attackedAgent);
			}
			pattern.setAttackedAgent(attackedAgent);
		}
		removeContradictPatterns(patterns);
	}
	
	public static void updateExecutedData(List<Pattern> patterns, Agent executedAgent)
	{
		for(Pattern pattern: patterns)
		{
			pattern.getAliveAgents().remove(executedAgent);
			pattern.setExecutedAgent(executedAgent);
		}
		removeContradictPatterns(patterns);
	}
	
	/*
	 * @brief		�p�^�[������A�l�T�E���l�m��̃��j�b�g���o���B
	 */
	public static void getDetermineEnemyAgent(List<Pattern> patterns, List<Agent> wolfAgent, List<Agent> possessedAgent)
	{
		// wolf,possessed�Е������̍����ł������Ă����������H
		for(Pattern pattern: patterns)
		{
	    	for (int i = 0; i < wolfAgent.size(); ++i)
	    	{
	    		Agent chAgent = wolfAgent.get(i);
	    		if (
	    			pattern.getEnemyMap().containsKey(chAgent) == false	
	    			|| pattern.getEnemyMap().get(chAgent) != EnemyCase.black
	    		)
	    		{
	    			wolfAgent.remove(i);
	    			--i;
	    		}
	    	}

	    	for (int i = 0; i < possessedAgent.size(); ++i)
	    	{
	    		Agent chAgent = possessedAgent.get(i);
	    		if (
	    			pattern.getEnemyMap().containsKey(chAgent) == false	
	    			|| pattern.getEnemyMap().get(chAgent) != EnemyCase.white
	    		)
	    		{
	    			possessedAgent.remove(i);
	    			--i;
	    		}
	    	}
	    	
	    	if (wolfAgent.size() == 0 && possessedAgent.size() == 0) break;
		}
	}

	/*
	 * @brief		�p�^�[������A�G�m��̃��j�b�g���o���B
	 */
	public static void getDetermineEnemyAgent(List<Pattern> patterns, List<Agent> enemyAgent)
	{
		// wolf,possessed�Е������̍����ł������Ă����������H
		for(Pattern pattern: patterns)
		{
	    	for (int i = 0; i < enemyAgent.size(); ++i)
	    	{
	    		Agent chAgent = enemyAgent.get(i);
	    		if (pattern.getEnemyMap().containsKey(chAgent) == false)
	    		{
	    			enemyAgent.remove(i);
	    			--i;
	    		}
	    	}
	    	if (enemyAgent.size() == 0 ) break;
		}
	}

	/**
	 * �肢�C��\�ɂ���ē���ꂽ����t������
	 * @param patterns
	 * @param judge
	 */
	public static void updateJudgeData(List<Pattern> patterns, Judge judge)
	{
		for(Pattern pattern: patterns)
		{
			// Judge�҂��^�\�͎҂Ƃ���Ă���ꍇ
			if(judge.getAgent().equals(pattern.getSeerAgent()) || judge.getAgent().equals(pattern.getMediumAgent()))
			{
				switch (judge.getResult()) 
				{
				// ������̏ꍇ
				case HUMAN:
					pattern.getWhiteAgentSet().add(judge.getTarget());
					break;

				// ������̏ꍇ
				case WEREWOLF:
					pattern.getEnemyMap().put(judge.getTarget(), EnemyCase.black);
					break;
				}
			}
		}
		removeContradictPatterns(patterns);
		return;
	}
	
	/*
	 * @brief ��l�ɂ���ē���ꂽ����t��
	 */
	public static void UpdateGuardedData(List<Pattern> patterns, Agent bodyGuard, Agent target)
	{
		for(Pattern pattern: patterns)
		{
			// �^�\�͎҂Ƃ���Ă���ꍇ
			if(bodyGuard.equals(pattern.getBodyGuardAgent()))
			{
				pattern.getWhiteAgentSet().add(target);
			}
		}
		removeContradictPatterns(patterns);
		return;		
	}

	/**
	 * Pattern��List���疵������Pattern�����O����
	 * @param patterns
	 */
	public static void removeContradictPatterns(List<Pattern> patterns)
	{
		List<Pattern> subPatterns = new ArrayList<Pattern>();
		
		for(Pattern pattern: patterns)
		{
			boolean isContradict = false;
			Map<Agent, EnemyCase> enemyMap = pattern.getEnemyMap();

			if(pattern.getSeerAgent() != null && pattern.getSeerAgent().equals(pattern.getMediumAgent()))
			{
				// �^�肢�t���^��}�t
				isContradict = true;
			}
			else if (pattern.getSeerAgent() != null && pattern.getSeerAgent().equals(pattern.getBodyGuardAgent()))
			{
				// �^�肢�t���^��l
				isContradict = true;
			}
			else if (pattern.getMediumAgent() != null && pattern.getMediumAgent().equals(pattern.getBodyGuardAgent()))
			{
				// �^��}�t���^��l
				isContradict = true;
			}
			// enemyMap�ɐ^�肢�t�C��}�t,�^��l���܂܂�Ă���
			else if(
					enemyMap.containsKey(pattern.getSeerAgent()) || enemyMap.containsKey(pattern.getMediumAgent()) || enemyMap.containsKey(pattern.getBodyGuardAgent())
			)
			{
				isContradict = true;
			}
			else
			{
				// ���m�G�[�W�F���g���CenemyMap�ō��m
				for(Agent agent: pattern.getWhiteAgentSet())
				{
					if(enemyMap.containsKey(agent) && enemyMap.get(agent) == EnemyCase.black)
					{
						isContradict = true;
					}
				}
				// enemyMap�����E���𒴂��Ă���C�l�T�Ƌ��l�����ꂼ�ꑽ������D
				if(enemyMap.size() > MyGameInfo.getMaxEnemyNum())
				{
					isContradict = true;
				}
				else
				{
					int werewolfNum = 0;
					int possessedNum = 0;
					for(Entry<Agent, EnemyCase> set: enemyMap.entrySet())
					{
						switch (set.getValue()) 
						{
						case black:
							werewolfNum++;
							break;
						case white:
							possessedNum++;
							break;
						}
					}
					if(werewolfNum > MyGameInfo.getMaxAgentNum(Role.WEREWOLF) || possessedNum > MyGameInfo.getMaxAgentNum(Role.POSSESSED))
					{
						isContradict = true;
					}
					
					if (isContradict == false)
					{
						// �����Ă���T�Ɛl�̐����r���ăQ�[���̑��݂̊m���炳���m�F
						int aliveSize = pattern.getAliveAgents().size();
						int aliveWolf = 0;
						int aliveEnemy = 0;
						for(Entry<Agent, EnemyCase> set: enemyMap.entrySet())
						{
							if (pattern.getAliveAgents().contains(set.getKey()))
							{
								aliveEnemy++;
								switch (set.getValue()) 
								{
								case black:
									aliveWolf++;
									break;
								}
							}
							
							if (aliveEnemy - MyGameInfo.getMaxAgentNum(Role.POSSESSED) > aliveWolf)
							{
								aliveWolf = aliveEnemy - MyGameInfo.getMaxAgentNum(Role.POSSESSED);
							}
						}
						int aliveHuman = aliveSize - aliveWolf;
						
						// �T�̕������łɑ����B�Q�[���I������ˁE�E�E�H
						if (aliveHuman <= aliveWolf)
						{
							isContradict = true;
						}						
					}
				}
			}

			if(isContradict)
			{
				subPatterns.add(pattern);
			}
		}
		patterns.removeAll(subPatterns);
	}


	/**
	 * agent���ݒ肳�ꂽrole�ƂȂ�Ȃ�Pattern�����O����D
	 * �Ⴆ�Ύ������^�肢�t�̎��ɁC������l�T�C���l�Ƃ���p�^�[���C����Agent���^�肢�t�ƂȂ�p�^�[�������O
	 * @param patterns
	 * @param agent
	 * @param role
	 */
	public static void settleAgentRole(List<Pattern> patterns, Agent agent, Role role)
	{
		//���O����p�^�[��
		List<Pattern> subPatterns = new ArrayList<Pattern>();

		switch (role) 
		{
		case VILLAGER:
			for(Pattern pattern: patterns)
			{
				if(pattern.getEnemyMap().containsKey(agent))
				{
					subPatterns.add(pattern);
				}
				else
				{
					pattern.getWhiteAgentSet().add(agent);
				}
			}
			break;
		case BODYGUARD:
			for(Pattern pattern: patterns)
			{
				if(pattern.getEnemyMap().containsKey(agent) || (pattern.getBodyGuardAgent() != null && !pattern.getBodyGuardAgent().equals(agent)))
				{
					subPatterns.add(pattern);
				}
				else
				{
					pattern.setBodyGuardAgent(agent);
					pattern.getWhiteAgentSet().add(agent);
				}
			}
			break;

		case SEER:
			/*
			 * enemyMap�Ɏ������܂܂�Ă���ꍇ
			 * ����Agent��Seer�ƂȂ��Ă���ꍇ(null�Ȃ珑������)
			 * TODO
			 * �i�^��\�҂������̐肢���ʂƈقȂ錋�ʂ��o���Ă���Ƃ��j���Ƃ��ƂȂ��Ă��
			 */
			for(Pattern pattern: patterns)
			{
				if(pattern.getEnemyMap().containsKey(agent) || (pattern.getSeerAgent() != null && !pattern.getSeerAgent().equals(agent)))
				{
					subPatterns.add(pattern);
				}
				else
				{
					pattern.setSeerAgent(agent);
					pattern.getWhiteAgentSet().add(agent);
				}
			}
			break;

		case MEDIUM:
			for(Pattern pattern: patterns)
			{
				if(pattern.getEnemyMap().containsKey(agent) ||  (pattern.getMediumAgent() != null && !pattern.getMediumAgent().equals(agent)))
				{
					subPatterns.add(pattern);
				}
				else
				{
					pattern.setMediumAgent(agent);
					pattern.getWhiteAgentSet().add(agent);
				}
			}
			break;

		case POSSESSED:
			/*
			 * �������^�̔\�͎҂Ɋ܂܂�Ă��鎞
			 * �m�荕�ɂȂ��Ă��鎞
			 */
			for(Pattern pattern: patterns)
			{
				if(agent.equals(pattern.getSeerAgent()) || agent.equals(pattern.getMediumAgent()))
				{
					subPatterns.add(pattern);
				}
				else if(pattern.getEnemyMap().containsKey(agent) && pattern.getEnemyMap().get(agent) == EnemyCase.black)
				{
					subPatterns.add(pattern);
				}
				else
				{
					pattern.getEnemyMap().put(agent, EnemyCase.white);
					pattern.getWhiteAgentSet().add(agent);
				}
			}
			break;

		case WEREWOLF:
			/*
			 * �������^�̔\�͎҂Ɋ܂܂�Ă��鎞
			 * �m�蔒�ɂȂ��Ă��鎞
			 */
			for(Pattern pattern: patterns)
			{
				if(agent.equals(pattern.getSeerAgent()) || agent.equals(pattern.getMediumAgent()))
				{
					subPatterns.add(pattern);
				}
				else if(pattern.getEnemyMap().containsKey(agent) && pattern.getEnemyMap().get(agent) == EnemyCase.white)
				{
					subPatterns.add(pattern);
				}
				else if (pattern.getWhiteAgentSet().contains(agent))
				{
					subPatterns.add(pattern);
				}
				else
				{
					pattern.getEnemyMap().put(agent, EnemyCase.black);
				}
			}
			break;

		default:
			break;
		}

		patterns.removeAll(subPatterns);
		return;
	}	
	
	/**
	 * bRemoveEnemy��true�̎��͐l�T�A���l�̃P�[�X���Afalse�̎��͂���ȊO�̃P�[�X�����O
	 */
	public static void RemoveEnemyPattern(List<Pattern> patterns, Agent agent, boolean bRemoveEnemy)
	{
		//���O����p�^�[��
		List<Pattern> subPatterns = new ArrayList<Pattern>();
		for(Pattern pattern: patterns)
		{
			if (bRemoveEnemy)
			{
				// ���̃p�^�[�������O
				if (pattern.getEnemyMap().containsKey(agent))
				{
					subPatterns.add(pattern);										
				}
			}
			else
			{
				// ���̃p�^�[�������O
				if (
					(pattern.getEnemyMap().size() == MyGameInfo.getMaxEnemyNum() && pattern.getEnemyMap().containsKey(agent) == false)	// ���łɐl�T�A���l�̘g�������ς�
					|| pattern.getWhiteAgentSet().contains(agent)																		// ���m�g�ɓo�^����Ă���
				)
				{
					subPatterns.add(pattern);					
				}				
			}
		}
		patterns.removeAll(subPatterns);
		return;
	}
	
	public static List<Agent> getAllSeerAgents(List<Pattern> patterns)
	{
		List<Agent> ret = new ArrayList<Agent>();
		

		for(Pattern pattern: patterns)
		{
			if (pattern.getSeerAgent() != null && ret.contains(pattern.getSeerAgent()) == false)
			{
				ret.add(pattern.getSeerAgent());
			}
		}
		return ret;
	}

	public static List<Pattern> clonePatterns(List<Pattern> patterns)
	{
		List<Pattern> newPatterns = new ArrayList<Pattern>();
		for(Pattern pattern: patterns){
			newPatterns.add(pattern.clone());
		}
		return newPatterns;
	}

}
