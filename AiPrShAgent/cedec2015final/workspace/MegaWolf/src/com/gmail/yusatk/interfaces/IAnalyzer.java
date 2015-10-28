/**
 * 
 */
package com.gmail.yusatk.interfaces;
import org.aiwolf.common.net.*;
import org.aiwolf.common.data.*;

import java.util.*;

/**
 * @author Yu
 *
 */
public interface IAnalyzer {
	public IGameInfo getLatestGameInfo();
	public void update(IGameInfo gameInfo);
	
	public int getDay();
	
	public int getRestExecutionCount();
	public List<Agent> getAliveAgents();

	public List<Agent> getAgents();
	public List<Agent> getGrayAgents();
	public List<Agent> getGrayAgentsBySeerSide(Agent seer);
	public Map<Agent, List<Agent>> getGrayAgentsBySeerSide();

	/**
	 * 対象のエージェントに投票しているエージェントの取得
	 * @param target 調査対象のエージェント
	 * @return target に投票を行っているエージェントのリスト
	 */
	public List<Agent> getVotedAgent(Agent target);

	/**
	 * 対象のエージェントに投票しているエージェントの取得
	 * @param day 調査対象の日
	 * @param target 調査対象のエージェント
	 * @return target に投票を行っているエージェントのリスト
	 */
	public List<Agent> getVotedAgent(int day, Agent target);
	
	public List<Vote> getVotePlan();
	public List<Vote> getVotePlan(int day);
	public Vote getVotePlan(Agent agent);
	public Vote getVotePlan(int day, Agent agent);
	
	public void Dump();
	
	public int getNakedEnemyCount();
	public List<Agent> getSeers();
	public List<Agent> getMediums();
	public List<Agent> getBodyguards();
	public List<Agent> getWolves();
	public List<Agent> getPossesseds();
	
	public List<Agent> getAliveWolfListBySeerSide(Agent seer);
	public GameSetting getSetting();

	public List<Agent> getAttackedAgents();
	public List<Agent> getExecutedAgents();
	public Map<Agent, List<Judge>> getDivineResults();
	public Map<Agent, List<Judge>> getInquestResults();
	public List<Judge> getDivineResultBySeer(Agent seer) ;
	public List<Judge> getInquestResultByMedium(Agent medium) ;
	
	public List<Agent> getCoAgents();
	public int getEnemyCount();
	public List<Agent> getBuddyWolves();
	public List<Agent> getAliveBuddyWolves();
	
	public Map<Integer, ISuggestion> getWolfSuggestions();
	public Map<Integer, ISuggestion> getHumanSuggestions();
	
	public boolean hasExtraExecution();
	
	public List<Talk> getTalks();
}
