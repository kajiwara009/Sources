package com.gmail.yusatk.data;

import java.util.*;

import org.aiwolf.common.data.Agent;

import com.gmail.yusatk.interfaces.*;
import com.gmail.yusatk.utils.DebugLog;

public class Suggestion implements ISuggestion {

	Action action = null;
	List<Agent> agreedAgents = new ArrayList<Agent>();
	List<Agent> disagreedAgents = new ArrayList<Agent>();
	Agent owner = null;
	
	public Suggestion(Agent owner, Action action) {
		this.owner = owner;
		this.action = action;
	}
	
	@Override
	public void agree(Agent agent) {
		agreedAgents.remove(agent);
		disagreedAgents.remove(agent);
		
		agreedAgents.add(agent);
	}

	@Override
	public void disagree(Agent agent) {
		agreedAgents.remove(agent);
		disagreedAgents.remove(agent);
		
		disagreedAgents.add(agent);
	}

	@Override
	public Action getAction() {
		return action;
	}

	@Override
	public void setAction(Action action) {
		this.action = action;
	}

	@Override
	public List<Agent> getAgreedAgents() {
		return agreedAgents;
	}

	@Override
	public List<Agent> getDisagreedAgents() {
		return disagreedAgents;
	}

	@Override
	public boolean answered(Agent agent) {
		boolean answered = false;
		answered |= agreedAgents.contains(agent);
		answered |= disagreedAgents.contains(agent);
		return answered;
	}

	@Override
	public boolean isMine(Agent agent) {
		return owner == agent;
	}

	@Override
	public void dump(){
		if(!DebugLog.isEnable()) {
			return;
		}
		DebugLog.log("Suggestion: Type: %s, Owner: %d, Target: %d\n", 
				this.action.getActionType().toString(),
				this.action.getOwner().getAgentIdx(),
				this.action.getTarget().getAgentIdx());
		DebugLog.log("  Agree: ");
		agreedAgents.forEach(agent->{
			DebugLog.log("%2d ", agent.getAgentIdx());
		});
		DebugLog.log("\n");
		DebugLog.log("  Disagree: ");
		disagreedAgents.forEach(agent->{
			DebugLog.log("%2d ", agent.getAgentIdx());
		});
		DebugLog.log("\n");		
	}
}
