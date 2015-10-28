package com.gmail.yusatk.plans;

import java.util.*;

import org.aiwolf.client.lib.TemplateTalkFactory;
import org.aiwolf.common.data.*;

import com.gmail.yusatk.interfaces.*;
import com.gmail.yusatk.talks.ComingOutTalk;

public class SeerPlan extends DefaultBattlePlan {
	IAgentEx owner = null;
	IAnalyzer analyzer = null;
	IWorldCache worldCache = null;
	
	List<Judge> divineResults = new ArrayList<Judge>();
	List<Judge> toldResults = new ArrayList<Judge>();
	Judge latestJudgeResult = null;
	List<Agent> alreadyDiviend = new ArrayList<Agent>();
	
	boolean isComingOut = false;
	
	public SeerPlan(IAgentEx owner, IAnalyzer analyzer, IWorldCache worldCache) {
		this.owner = owner;
		this.analyzer = analyzer;
		this.worldCache = worldCache;
	}
	
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
	
	@Override
	public IBattlePlan planUpdate(boolean dayStartUpdate) {
		// TODO: 吊り回数の計算をきちんとする。今回は１５人配役決め打ち
		if(analyzer.getNakedEnemyCount() >= analyzer.getEnemyCount() && 
				analyzer.getRestExecutionCount() >= 6) {
			return new RollerPlan(owner, analyzer, worldCache);
		}
		return this;
	}

	@Override
	public Queue<ITalkEvent> getTalkPlan(int restTalkCount) {
		Queue<ITalkEvent> talks = new LinkedList<ITalkEvent>();
		
		if(shouldComingOut()) {
			talks.add(new ComingOutTalk(owner.getAgent(), owner.getRole()));
			isComingOut = true;
		}
		
		for(Judge result : divineResults) {
			if(toldResults.contains(result)) {
				continue;
			}
			toldResults.add(result);
			if(result != null) {
				talks.add(new ITalkEvent() {
					@Override
					public String getTalk() {
						return TemplateTalkFactory.divined(result.getTarget(), result.getResult());
					}
				});
			}
		}
		
		return talks;
	}

	@Override
	public Agent getVotePlan() {
		// 黒出しが生存してれば投票
		List<Agent> alives = analyzer.getLatestGameInfo().getAliveAgentList();
		for(Judge result : divineResults) {
			if(result == null){
				continue;
			}
			if(result.getResult() == Species.WEREWOLF && alives.contains(result.getTarget())){
				return result.getTarget();
			}
		}
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
	
	private Agent calcDivineTarget() {
		IScoreMap scoreMap = GrayEvaluationHelper.evaluateGrayForSeer(analyzer, owner.getAgent(), owner.getTimeWatcher());
		List<Agent> candidates = scoreMap.getAgentsByDesc();
		candidates.removeAll(alreadyDiviend);
		candidates.remove(owner.getAgent());
		return candidates.get(0);
	}
	
	@Override
	public Agent getDivinePlan() {
		Agent target = calcDivineTarget();
		alreadyDiviend.add(target);
		return target;
	}

	@Override
	public void dayStart() {
		latestJudgeResult = analyzer.getLatestGameInfo().getDivineResult();
		divineResults.add(latestJudgeResult);
	}

}
