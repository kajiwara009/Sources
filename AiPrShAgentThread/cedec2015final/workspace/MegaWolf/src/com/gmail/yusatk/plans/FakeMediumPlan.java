package com.gmail.yusatk.plans;

import java.util.*;

import org.aiwolf.common.data.*;

import com.gmail.yusatk.interfaces.IAgentEx;
import com.gmail.yusatk.interfaces.IAnalyzer;
import com.gmail.yusatk.interfaces.IBattlePlan;
import com.gmail.yusatk.interfaces.ITalkEvent;
import com.gmail.yusatk.interfaces.IWorldCache;
import com.gmail.yusatk.talks.ComingOutTalk;
import com.gmail.yusatk.talks.InquestTalk;

public class FakeMediumPlan extends DefaultBattlePlan {
	IAgentEx owner = null;
	IAnalyzer analyzer = null;
	IWorldCache worldCache = null;
	
	List<Judge> fakeResults = new ArrayList<Judge>();
	List<Judge> toldResults = new ArrayList<Judge>();
	List<Agent> executedAgents = new ArrayList<Agent>();
	List<Agent> fakedTargets = new ArrayList<Agent>();
	boolean isComingOut = false;
	
	private boolean shouldComingOut() {
		if(isComingOut) {
			return false;
		}
		// TODO: 対抗の黒出しがあるなら様子見で潜伏する
		if(analyzer.getDay() >= 1) {
			return true;
		}
		return false;
	}		
	
	Judge createFakeJudge(Agent exed) {
		// TODO: 偽結果をちゃんと作る
		if(exed == null) {
			return null;
		}
		return new Judge(analyzer.getLatestGameInfo().getDay(), owner.getAgent(), exed, Species.HUMAN);
	}
	
	void createFakeResult() {
		while(fakeResults.size() < analyzer.getDay() + 1) {
			for(Agent exed : executedAgents) {
				if(exed == null && fakeResults.size() < 2) {
					fakeResults.add(null);
				}
				
				if(!fakedTargets.contains(exed)) {
					fakedTargets.add(exed);
					Judge fakeJudge = createFakeJudge(exed);
					fakeResults.add(fakeJudge);
				}
			}
		}
	}
	
	FakeMediumPlan(IAgentEx owner, IAnalyzer analyzer, IWorldCache worldCache){
		this.owner = owner;
		this.analyzer = analyzer;
		this.worldCache = worldCache;
	}
	@Override
	public IBattlePlan planUpdate(boolean dayStartUpdate) {
		return this;
	}

	@Override
	public Queue<ITalkEvent> getTalkPlan(int restTalkCount) {
		Queue<ITalkEvent> talks = new LinkedList<ITalkEvent>();
		if(shouldComingOut()) {
			isComingOut = true;
			talks.add(new ComingOutTalk(owner.getAgent(), Role.MEDIUM));
		}
		if(analyzer.getDay() >= 2) {
			createFakeResult();
			for(Judge fake : fakeResults) {
				if(fake == null) {
					continue;
				}
				if(!toldResults.contains(fake)) {
					toldResults.add(fake);
					talks.add(new InquestTalk(fake.getTarget(), fake.getResult()));
				}
			}
		}
		return talks;
	}

	@Override
	public Agent getVotePlan() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Agent getAttackPlan() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Agent getGuardPlan() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Agent getDivinePlan() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void dayStart() {
		executedAgents = analyzer.getExecutedAgents();
	}

}
