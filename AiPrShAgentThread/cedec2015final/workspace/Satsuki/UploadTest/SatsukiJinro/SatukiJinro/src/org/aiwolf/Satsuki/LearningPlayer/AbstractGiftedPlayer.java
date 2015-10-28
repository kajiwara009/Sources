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
	// 占い済みエージェントを格納
	protected List<Agent> divinedAgents = new ArrayList<Agent>();
	
	//まだ報告していないjudge
	List<Judge> notToldjudges = new ArrayList<Judge>();

	//既に報告したjudge
	List<Judge> toldjudges = new ArrayList<Judge>();

	//カミングアウトしたか
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
		// 生きてない
		if (getLatestDayGameInfo().getAliveAgentList().contains(agent) == false) return false;
		
		// 自分自身
		if (agent == getMe()) return false;
		
		// すでに占い済み
		if (divinedAgents.contains(agent)) return false;
		
		// 人狼確定は占う必要が無い。(低優先では占う？/天然狂人への対策をしたいが・・・)
		if (wolfDetermineAgent.contains(agent)) return false;
		
		// 狂人確定は占う必要が無い。(低優先では占う？/天然狂人への対策をしたいが・・・)
		if (possessedDetermineAgent.contains(agent)) return false;
		
		return true;
	}
	
	// 占うエージェントの取得
	public Agent getDivineAgent(List<Pattern> patterns)
	{
		Random rand = new Random();
		
		// 人狼、狂人確定エージェントは占わない。
		List<Agent> wolfDetermineAgent = new ArrayList<Agent>(getLatestDayGameInfo().getAliveAgentList());
		List<Agent> possessedDetermineAgent = new ArrayList<Agent>(getLatestDayGameInfo().getAliveAgentList());
		PatternMaker.getDetermineEnemyAgent(patterns, wolfDetermineAgent, possessedDetermineAgent);
		
		// 余剰CO数を取得
		List<Agent> grayAgents = new ArrayList<Agent>();
		
		// この占い師の占い結果を取得
		// スペル怪しいけど気にしない
		List<Agent> seeredAgent = new ArrayList<Agent>();
		for(Judge judge: advanceGameInfo.getInspectJudges())
		{
			if (judge.getAgent().equals(getMe()))
			{
				seeredAgent.add(judge.getTarget());
			}
		}
		
		// 占い師パターンを取得
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
		
		// grayのエージェントを出す
		// 1パターンしかなく、enemyMapがカンストしている場合,敵リストのgrayの数がgrayリストになる。
		if (thisSeerPattern.size() == 1 && thisSeerPattern.get(0).getEnemyMap().size() == MyGameInfo.getMaxEnemyNum())
		{
			for(Entry<Agent, EnemyCase> em: thisSeerPattern.get(0).getEnemyMap().entrySet())
			{
				// 生存かつgrayならgrayに加算
				if (getLatestDayGameInfo().getAliveAgentList().contains(em.getKey()) && em.getValue() == EnemyCase.gray)
				{
					grayAgents.add(em.getKey());
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
			for(Agent agent:getLatestDayGameInfo().getAliveAgentList())
			{
				if (wolfDetermineAgent.contains(agent)) continue;
				if (possessedDetermineAgent.contains(agent)) continue;
				if (seeredAgent.contains(agent)) continue;
				
				grayAgents.add(agent);
			}
		}
		
		// patternの最小の敵の数を出す。
		// 3以下 →COなしのgrayを占う。
		// 4 → COのgrayを占う。
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
		// 占い師を判定
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
		// 複数霊媒師を判定
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
		// まだ占っていないユニットから適当に・・・		
		
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
		// 誰でもいいや・・・		
		priorityAgent.clear();
		for(Agent agent: getLatestDayGameInfo().getAliveAgentList())
		{
			priorityAgent.add(agent);
		}
		
		if (priorityAgent.size() > 0)
		{
			return priorityAgent.get(rand.nextInt(priorityAgent.size()));
		}
		
		if(MyGameInfo.IS_PRINT()) System.out.println("占い失敗");
		return null;
	}

	public List<Pattern> getHypotheticalPatterns(List<Pattern> originPatterns, Judge judge){
		List<Pattern> hypotheticalPatterns = PatternMaker.clonePatterns(originPatterns);
		PatternMaker.updateJudgeData(hypotheticalPatterns, judge);
		return hypotheticalPatterns;
	}

	public String getTemplateComingoutText(){
		/*
		 * カミングアウトする日数になる
		 * 他に同じ能力者COが出る
		 * 人狼を見つける
		 * 投票先に選ばれそう（全体の2/3が投票かつ全投票中でマックスが自分または３票以上）
		 */
		if(isComingout)
		{
			return null;
		}
		else
		{
			//日数によるカミングアウト
			if(getDay() == coTiming.getDay() && coTiming.doComingout())
			{
				isComingout = true;
				return TemplateTalkFactory.comingout(getMe(), getMyRole());
			}

			//偽CO出現
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

			//人狼見つける
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

			//投票先に選ばれそう
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
