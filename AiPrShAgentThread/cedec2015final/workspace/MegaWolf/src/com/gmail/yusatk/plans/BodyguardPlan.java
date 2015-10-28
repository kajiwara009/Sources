package com.gmail.yusatk.plans;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.aiwolf.common.data.Agent;

import com.gmail.yusatk.interfaces.IAgentEx;
import com.gmail.yusatk.interfaces.IAnalyzer;
import com.gmail.yusatk.interfaces.IBattlePlan;
import com.gmail.yusatk.interfaces.ITalkEvent;
import com.gmail.yusatk.interfaces.IWorldCache;
import com.gmail.yusatk.utils.DebugLog;

public class BodyguardPlan extends DefaultBattlePlan {
	IAgentEx owner = null;
	IAnalyzer analyzer = null;
	IWorldCache worldCache = null;

	List<Agent> guardedAgents = new ArrayList<Agent>();
	List<Boolean> guardSuccessList = new ArrayList<Boolean>();
	public BodyguardPlan(IAgentEx owner, IAnalyzer analyzer, IWorldCache worldCache) {
		this.owner = owner;
		this.analyzer = analyzer;
		this.worldCache = worldCache;
	}
	@Override
	public IBattlePlan planUpdate(boolean dayStartUpdate) {
		if(!analyzer.hasExtraExecution()) {
			return new BodyguardMiddlePhasePlan(owner, analyzer, worldCache);
		}
		return this;
	}

	@Override
	public Queue<ITalkEvent> getTalkPlan(int restTalkCount) {
		return new LinkedList<ITalkEvent>();
	}

	@Override
	public Agent getVotePlan() {
		return null;
	}

	@Override
	public Agent getAttackPlan() {
		return null;
	}
	
	Agent selectGuardSeer(List<Agent> seers) {
		// TODO: 黒出しの状況、破綻等を見て優先度を決める
		return seers.get(0);
	}
	
	Agent selectGuardMedium(List<Agent> mediums) {
		// TODO: 黒出しの状況、破綻等を見て優先度を決める
		return mediums.get(0);
	}

	Agent selectGuardAlives(List<Agent> alives) {
		// TODO: 白の出され具合等を見て優先度を決める

		List<Agent> candidates = new ArrayList<Agent>();
		candidates.addAll(alives);
		candidates.remove(owner.getAgent());
		return candidates.get(0);
	}
	
	@Override
	public Agent getGuardPlan() {
		// TODO: 状況に応じてどの役職を守るかを判断
		
		List<Agent> alives = analyzer.getLatestGameInfo().getAliveAgentList();
		List<Agent> aliveSeers = new ArrayList<Agent>();
		aliveSeers.addAll(analyzer.getSeers());
		aliveSeers.removeIf(s->{
			return !alives.contains(s);
		});

		if(!aliveSeers.isEmpty()){
			return selectGuardSeer(aliveSeers);
		}
		
		List<Agent> aliveMediums = new ArrayList<Agent>();
		aliveMediums.addAll(analyzer.getMediums());
		aliveMediums.removeIf(m->{
			return !alives.contains(m);
		});
		
		if(!aliveMediums.isEmpty()){
			return selectGuardMedium(aliveMediums);
		}
		
		return selectGuardAlives(alives);
	}

	@Override
	public Agent getDivinePlan() {
		return null;
	}
	
	@Override
	public void dayStart() {
		guardedAgents.add(analyzer.getLatestGameInfo().getGuardedAgent());
		Agent guarded = analyzer.getLatestGameInfo().getGuardedAgent();
		
		this.guardedAgents.add(guarded);
		
		if(guarded == null){
			guardSuccessList.add(false);
			DebugLog.log("Not guard\n");
			return;
		}
		
		DebugLog.log("***** Guard: %d\n", guarded.getAgentIdx());
		if(this.analyzer.getLatestGameInfo().getAttackedAgent() != null) {
			DebugLog.log("***** failed \n");
			guardSuccessList.add(false);
		}
		else {
			DebugLog.log("***** SUCCESS \n");
			guardSuccessList.add(true);
		}
	}
}
