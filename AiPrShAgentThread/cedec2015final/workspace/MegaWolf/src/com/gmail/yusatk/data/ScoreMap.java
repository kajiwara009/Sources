package com.gmail.yusatk.data;

import java.util.*;

import org.aiwolf.common.data.*;

import com.gmail.yusatk.interfaces.IScoreMap;
import com.gmail.yusatk.utils.DebugLog;

public class ScoreMap implements IScoreMap {
	Map<Agent, Integer> scoreMap = new HashMap<Agent, Integer>();
	List<Agent> agents = new LinkedList<Agent>();

/*	
	private List<Agent> createList() {
		List<Agent> list = new LinkedList<Agent>();
		scoreMap.forEach((k, v)-> {
			list.add(k);
		});
		return list;
	}
*/	
	@Override
	public List<Agent> getAgentsByAsc() {
		List<Agent> list = new LinkedList<Agent>();
		list.addAll(agents);
		list.sort((x, y) -> {
			return scoreMap.get(x) - scoreMap.get(y);
		});
		return list;
	}

	@Override
	public List<Agent> getAgentsByDesc() {
		List<Agent> list = new LinkedList<Agent>();
		list.addAll(agents);
		list.sort((x, y) -> {
			return scoreMap.get(y) - scoreMap.get(x);
		});
		return list;
	}

	@Override
	public void addAgent(Agent agent, int score) {
		assert !scoreMap.containsKey(agent);
		scoreMap.put(agent, score);
		agents.add(agent);
	}

	@Override
	public void addScore(Agent agent, int diff) {
		assert scoreMap.containsKey(agent);
		int score = scoreMap.get(agent);
		scoreMap.put(agent, score + diff);
	}

	@Override
	public void removeAgent(Agent agent) {
		assert scoreMap.containsKey(agent);
		scoreMap.remove(agent);
		agents.remove(agent);
	}

	@Override
	public void dump(String name) {
		DebugLog.log("ScoreMap: %s\n", name);
		List<Agent> agents = getAgentsByAsc();
		agents.forEach(agent->{
			DebugLog.log("%2d: %d\n", agent.getAgentIdx(), scoreMap.get(agent));
		});
	}
	
	@Override 
	public IScoreMap merge(IScoreMap target) {
		ScoreMap merged = new ScoreMap();
		this.scoreMap.forEach((k, v)->{
			merged.addAgent(k, v);
		});
		
		target.getAgentsByAsc().forEach(agent->{
			if(!merged.scoreMap.containsKey(agent)) {
				merged.addAgent(agent, 0);
			}
			merged.addScore(agent, target.getScore(agent));
		});
		
		return merged;
	}
	
	@Override
	public int getScore(Agent agent) {
		assert scoreMap.containsKey(agent);
		return scoreMap.get(agent);
	}
	
	@Override
	public List<Agent> getAgents() {
		return agents;
	}
}
