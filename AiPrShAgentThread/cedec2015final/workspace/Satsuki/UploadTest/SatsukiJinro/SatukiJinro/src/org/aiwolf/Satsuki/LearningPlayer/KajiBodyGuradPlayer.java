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

	// 占い師護衛思考
	public Agent guard_Seer(List<Agent> enemyAgent, List<Agent> blackAgent)
	{
		Random rand = new Random();
		Agent target = null;
		
		List<Agent> priorityAgent = new ArrayList<Agent>();
		for(Entry<Agent, Role> param: advanceGameInfo.getComingoutMap().entrySet())
		{
			if (
				param.getValue() == Role.SEER 
				&& enemyAgent.contains(param.getKey()) == false												// 敵確
				&& blackAgent.contains(param.getKey()) == false												// 黒判定を受けていたら、殺す必要性が無い(まず殺されないため)
				&& getLatestDayGameInfo().getAliveAgentList().contains(param.getKey())						// 生きているユニットしか判定しない
			)
			{
				priorityAgent.add(param.getKey());
			}
		}

		
		if (priorityAgent.size() > 0)
		{
			// "自分から見て"grayが最も多い占い師を守る
			int maxGrayCount = -1;
			Agent maxEnemyCountAgent = null;
			
			for (Agent seerAgent: priorityAgent)
			{
				int graySize = 0;
				
				// この占い師の占い結果を取得
				// スペル怪しいけど気にしない
				List<Agent> seeredAgent = new ArrayList<Agent>();
				for(Judge judge: advanceGameInfo.getInspectJudges())
				{
					if (judge.getAgent().equals(seerAgent))
					{
						seeredAgent.add(judge.getTarget());
					}
				}
				
				// 占い師パターンを取得
				List<Pattern> thisSeerPattern = new ArrayList<Pattern>();
				for(Pattern pattern: myPatterns)
				{
					if (pattern.getSeerAgent() == seerAgent)
					{
						thisSeerPattern.add(pattern);
					}
				}
				
				// 1パターンしかなく、enemyMapがカンストしている場合,敵リストのgrayの数がgrayリストになる。
				if (thisSeerPattern.size() == 1 && thisSeerPattern.get(0).getEnemyMap().size() == MyGameInfo.getMaxEnemyNum())
				{
					for(Entry<Agent, EnemyCase> em: thisSeerPattern.get(0).getEnemyMap().entrySet())
					{
						// 生存かつgrayならgrayに加算
						if (getLatestDayGameInfo().getAliveAgentList().contains(em.getKey()) && em.getValue() == EnemyCase.gray)
						{
							graySize++;
						}
					}
				}
				// 複数パターンあるか、enemyMapがカンストしていない場合は、生存者内で、
				// 1 黒確敵の数
				// 2 白確敵の数
				// 3 1,2に当てはまらないユニットで占い済みでない数
				// を求めて、grayを算出
				else
				{
					List<Agent> wolfDetermineAgent = new ArrayList<Agent>(getLatestDayGameInfo().getAliveAgentList());
					List<Agent> possessedDetermineAgent = new ArrayList<Agent>(getLatestDayGameInfo().getAliveAgentList());
					
					// 取得開始
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
			
			// 残りGrayが2人以下なら護衛しない
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

		// 敵確定エージェント
		List<Agent> enemyAgent = new ArrayList<Agent>(getLatestDayGameInfo().getAliveAgentList());
		PatternMaker.getDetermineEnemyAgent(myPatterns, enemyAgent);

		List<Agent> generalenemyAgent = new ArrayList<Agent>(getLatestDayGameInfo().getAliveAgentList());
		PatternMaker.getDetermineEnemyAgent(generalPatterns, generalenemyAgent);
		
		// (周知の敵確以外から)黒をもらったエージェント
		List<Agent> blackAgent = new ArrayList<Agent>();
		for(Judge judge: advanceGameInfo.getInspectJudges())
		{
			if (judge.getResult() == Species.WEREWOLF && blackAgent.contains(judge.getAgent()) == false && generalenemyAgent.contains(judge.getAgent()) == false)
			{
				blackAgent.add(judge.getAgent());
			}
		}
		
		
		// 占い師護衛思考
		target = guard_Seer(enemyAgent, blackAgent);

		if (target != null)
		{
			return target;
		}
		
		
		// 適当に守る
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
