package com.gmail.yusatk.plans;

import java.util.*;

import org.aiwolf.client.lib.TemplateTalkFactory.TalkType;
import org.aiwolf.common.data.*;

import com.gmail.yusatk.interfaces.*;
import com.gmail.yusatk.talks.*;

public class AllSiteTrackPlan extends DefaultBattlePlan {
	IAgentEx owner;
	IAnalyzer analyzer;
	IWorldCache worldCache;
	Agent votePlan = null;
	
	public AllSiteTrackPlan(IAgentEx owner, IAnalyzer analyzer, IWorldCache worldCache) {
		this.owner = owner;
		this.analyzer = analyzer;
		this.worldCache = worldCache;
	}

	boolean canPerformPlan() {
		if(!analyzer.hasExtraExecution()){
			return false;
		}
		
		List<Agent> gray = analyzer.getGrayAgents();
		gray.remove(owner.getAgent());
		if(gray.size() == 0) {
			return false;
		}
		return true;
	}
	
	@Override
	public IBattlePlan planUpdate(boolean dayStartUpdate) {
		// TODO: 吊り回数の計算をきちんとする。今回は１５人配役決め打ち
		if(analyzer.getNakedEnemyCount() >= analyzer.getEnemyCount() && 
				analyzer.getRestExecutionCount() >= 6) {
			return new RollerPlan(owner, analyzer, worldCache);
		}
		if(!canPerformPlan()) {
			return new OneSiteTrackPlan(owner, analyzer, worldCache);
		}
		return this;
	}

	
	private Agent calcVotePlan() {
		// 全視点詰めを前提とした評価の結果から投票先を決定する
		List<Agent> candidates = analyzer.getAliveAgents();
		IScoreMap scoreMap = GrayEvaluationHelper.evaluateGrayForAllSite(analyzer, candidates, owner.getTimeWatcher());
		
		List<Agent> calcedCandidates = scoreMap.getAgentsByDesc();
		calcedCandidates.remove(owner.getAgent());
		return calcedCandidates.get(0);
	}
	
	@Override
	public Queue<ITalkEvent> getTalkPlan(int restTalkCount) {
		Queue<ITalkEvent> talks = new LinkedList<ITalkEvent>();
		Agent newPlan = getVotePlan();
		Agent oldPlan = this.votePlan;
		if(newPlan != oldPlan) {
			votePlan = newPlan;
			talks.add(new VoteTalk(TalkType.TALK, newPlan));
		}
		return talks;
	}

	@Override
	public Agent getVotePlan() {
		return calcVotePlan();
	}

	@Override
	public void dayStart() {
		votePlan = null;
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
	public Queue<ITalkEvent> getWhisperPlan(int restWhisperCount) {
		return new LinkedList<ITalkEvent>();
	}
}
