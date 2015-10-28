package com.gmail.yusatk.interfaces;

import org.aiwolf.common.data.*;
import java.util.*;

public interface IScoreMap {
	public List<Agent> getAgentsByAsc();
	public List<Agent> getAgentsByDesc();
	public List<Agent> getAgents();
	public void addAgent(Agent agent, int score);
	public void addScore(Agent agent, int diff);
	public void removeAgent(Agent agent);
	public void dump(String name);
	public IScoreMap merge(IScoreMap scoreMap);
	public int getScore(Agent agent);
}
