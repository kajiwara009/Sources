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
 * List<Pattern>の更新，拡張に用いる。Util
 * @author kengo
 *
 */
public class PatternMaker {
	/**
	 * COの発言を元にパターンを作成，更新する．
	 * @param patternList
	 * @param coAgent
	 * @param coRole
	 * @param gameInfo
	 * @return
	 */
	//必要データ：元のパターンリスト，COしたエージェントと役職，死人リスト，
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
			// 真能力者がいる場合
			if(isExistGenuineCO)
			{
				Pattern newPattern = pattern.clone();
				// enemyMapにまだ入っていない
				if(!newPattern.getEnemyMap().containsKey(coAgent))
				{
					// 白確Agentに入っている
					if(newPattern.getWhiteAgentSet().contains(coAgent))
					{
						newPattern.getEnemyMap().put(coAgent, EnemyCase.white);
					}
					// 白確Agentに入っていない
					else
					{
						newPattern.getEnemyMap().put(coAgent, EnemyCase.gray);
					}
				}
				newPatterns.add(newPattern);
			}
			// 真能力者がいない
			else
			{
				// 新しいCO者を真とするPattern
				Pattern newPattern1 = pattern.clone();
				// 新しいCO者も灰色とするPattern
				Pattern newPattern2 = pattern.clone();
				// newPattern1について
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

				// newPattern2について
				newPattern2.getEnemyMap().put(coAgent, EnemyCase.gray);
				
				newPatterns.add(newPattern1);
				newPatterns.add(newPattern2);
			}
		}
		long end = System.currentTimeMillis();
		
		if (end - start >= 300)
		{
			System.out.println("長い");
		}

		// newPatternsの矛盾ないものをpatternに入れる．
		
		// clonePatternsは何してる？ @石岡
		// List<Pattern> clonePatterns = new ArrayList<Pattern>(newPatterns);
		start = System.currentTimeMillis();
		removeContradictPatterns(newPatterns);
		end = System.currentTimeMillis();
		
		if (end - start >= 300)
		{
			System.out.println("長い");
		}
		
		patterns.clear();
		patterns.addAll(newPatterns);
		return;
	}

	// 襲撃によって死んだプレイヤーを白確，真能力者によって白確か黒確
	/**
	 * 襲撃されたプレイヤーを白確にする
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

				// 必要な機がするので追加@石岡
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
	 * @brief		パターンから、人狼・狂人確定のユニットを出す。
	 */
	public static void getDetermineEnemyAgent(List<Pattern> patterns, List<Agent> wolfAgent, List<Agent> possessedAgent)
	{
		// wolf,possessed片方だけの高速版があってもいいかも？
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
	 * @brief		パターンから、敵確定のユニットを出す。
	 */
	public static void getDetermineEnemyAgent(List<Pattern> patterns, List<Agent> enemyAgent)
	{
		// wolf,possessed片方だけの高速版があってもいいかも？
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
	 * 占い，霊能によって得られた情報を付加する
	 * @param patterns
	 * @param judge
	 */
	public static void updateJudgeData(List<Pattern> patterns, Judge judge)
	{
		for(Pattern pattern: patterns)
		{
			// Judge者が真能力者とされている場合
			if(judge.getAgent().equals(pattern.getSeerAgent()) || judge.getAgent().equals(pattern.getMediumAgent()))
			{
				switch (judge.getResult()) 
				{
				// 白判定の場合
				case HUMAN:
					pattern.getWhiteAgentSet().add(judge.getTarget());
					break;

				// 黒判定の場合
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
	 * @brief 狩人によって得られた情報を付加
	 */
	public static void UpdateGuardedData(List<Pattern> patterns, Agent bodyGuard, Agent target)
	{
		for(Pattern pattern: patterns)
		{
			// 真能力者とされている場合
			if(bodyGuard.equals(pattern.getBodyGuardAgent()))
			{
				pattern.getWhiteAgentSet().add(target);
			}
		}
		removeContradictPatterns(patterns);
		return;		
	}

	/**
	 * PatternのListから矛盾したPatternを除外する
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
				// 真占い師が真霊媒師
				isContradict = true;
			}
			else if (pattern.getSeerAgent() != null && pattern.getSeerAgent().equals(pattern.getBodyGuardAgent()))
			{
				// 真占い師が真狩人
				isContradict = true;
			}
			else if (pattern.getMediumAgent() != null && pattern.getMediumAgent().equals(pattern.getBodyGuardAgent()))
			{
				// 真霊媒師が真狩人
				isContradict = true;
			}
			// enemyMapに真占い師，霊媒師,真狩人が含まれている
			else if(
					enemyMap.containsKey(pattern.getSeerAgent()) || enemyMap.containsKey(pattern.getMediumAgent()) || enemyMap.containsKey(pattern.getBodyGuardAgent())
			)
			{
				isContradict = true;
			}
			else
			{
				// 白確エージェントかつ，enemyMapで黒確
				for(Agent agent: pattern.getWhiteAgentSet())
				{
					if(enemyMap.containsKey(agent) && enemyMap.get(agent) == EnemyCase.black)
					{
						isContradict = true;
					}
				}
				// enemyMapが限界数を超えている，人狼と狂人がそれぞれ多すぎる．
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
						// 生きている狼と人の数を比較してゲームの存在の確からさを確認
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
						
						// 狼の方がすでに多い。ゲーム終了じゃね・・・？
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
	 * agentが設定されたroleとならないPatternを除外する．
	 * 例えば自分が真占い師の時に，自分を人狼，狂人とするパターン，他のAgentが真占い師となるパターンを除外
	 * @param patterns
	 * @param agent
	 * @param role
	 */
	public static void settleAgentRole(List<Pattern> patterns, Agent agent, Role role)
	{
		//除外するパターン
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
			 * enemyMapに自分が含まれている場合
			 * 他のAgentがSeerとなっている場合(nullなら書き換え)
			 * TODO
			 * （真霊能者が自分の占い結果と異なる結果を出しているとき）もともとなってる説
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
			 * 自分が真の能力者に含まれている時
			 * 確定黒になっている時
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
			 * 自分が真の能力者に含まれている時
			 * 確定白になっている時
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
	 * bRemoveEnemyがtrueの時は人狼、狂人のケースを、falseの時はそれ以外のケースを除外
	 */
	public static void RemoveEnemyPattern(List<Pattern> patterns, Agent agent, boolean bRemoveEnemy)
	{
		//除外するパターン
		List<Pattern> subPatterns = new ArrayList<Pattern>();
		for(Pattern pattern: patterns)
		{
			if (bRemoveEnemy)
			{
				// 黒のパターンを除外
				if (pattern.getEnemyMap().containsKey(agent))
				{
					subPatterns.add(pattern);										
				}
			}
			else
			{
				// 白のパターンを除外
				if (
					(pattern.getEnemyMap().size() == MyGameInfo.getMaxEnemyNum() && pattern.getEnemyMap().containsKey(agent) == false)	// すでに人狼、狂人の枠がいっぱい
					|| pattern.getWhiteAgentSet().contains(agent)																		// 白確枠に登録されている
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
