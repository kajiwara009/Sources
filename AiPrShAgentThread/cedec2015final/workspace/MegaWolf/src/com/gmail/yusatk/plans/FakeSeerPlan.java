package com.gmail.yusatk.plans;

import java.util.*;

import org.aiwolf.client.lib.TemplateTalkFactory;
import org.aiwolf.common.data.*;

import com.gmail.yusatk.interfaces.IAgentEx;
import com.gmail.yusatk.interfaces.IAnalyzer;
import com.gmail.yusatk.interfaces.IBattlePlan;
import com.gmail.yusatk.interfaces.IScoreMap;
import com.gmail.yusatk.interfaces.ITalkEvent;
import com.gmail.yusatk.interfaces.IWorld;
import com.gmail.yusatk.interfaces.IWorldCache;
import com.gmail.yusatk.talks.ComingOutTalk;
import com.gmail.yusatk.talks.DivineTalk;

public class FakeSeerPlan extends DefaultBattlePlan {

	IAgentEx owner = null;
	IAnalyzer analyzer = null;
	IWorldCache worldCache = null;
	
	FakeSeerPlan(IAgentEx owner, IAnalyzer analyzer, IWorldCache worldCache){
		this.owner = owner;
		this.analyzer = analyzer;
		this.worldCache = worldCache;
	}

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
	
	@Override
	public IBattlePlan planUpdate(boolean dayStartUpdate) {
		return this;
	}

	List<Judge> toldResults = new ArrayList<Judge>();	
	List<Judge> fakeResults = new ArrayList<Judge>();
	List<Agent> alreadyDivined = new ArrayList<Agent>();
	
	private Agent getFakeTarget() {
		IScoreMap scoreMap = GrayEvaluationHelper.evaluateGrayForSeer(analyzer, owner.getAgent(), owner.getTimeWatcher());
		IScoreMap fakeScoreMap = GrayEvaluationHelper.evaluateGrayForFakeSeer(analyzer, owner.getAgent(), owner.getTimeWatcher());
		IScoreMap merged = scoreMap.merge(fakeScoreMap);
		List<Agent> targets = merged.getAgentsByDesc();

		assert targets.size() > 0;
		for(Agent target : targets) {
			if(!alreadyDivined.contains(target)) {
				return target;
			}
		}

		assert false;
		return targets.get(0);
	}
	
	int foundHideWolfCount = 0;
	
	private void addFakeResult(Agent target, Species result) {
		fakeResults.add(new Judge(analyzer.getDay(), owner.getAgent(), target, result));
		alreadyDivined.add(target);
	}
	
	private void createFakeResult() {
		if(fakeResults.size() == 0) {
			fakeResults.add(null);
		}
		
		while(fakeResults.size() < analyzer.getDay() + 1) {
			if(analyzer.hasExtraExecution()) {
				Agent target = getFakeTarget();
				addFakeResult(target, Species.HUMAN);
			}
			else
			{
				int nakedEnemyCount = analyzer.getNakedEnemyCount();
				IWorld assumeWorld = null;
				List<IWorld> worlds = worldCache.getWorlds();
				for(IWorld world : worlds) {
					// TODO: ちゃんと選ぶ
					if(world.getSeer() == owner.getAgent()) {
						assumeWorld = world;
						break;
					}
				}
				if(assumeWorld != null) {
					int hideWolfCount = nakedEnemyCount - 1 - foundHideWolfCount;
					if(hideWolfCount >= 2) {
						List<Agent> candidates = assumeWorld.getWolfCandidates();
						Agent fakeTarget = candidates.get(0);
						addFakeResult(fakeTarget, Species.WEREWOLF);
						foundHideWolfCount++;
						return;
					}
				}
				List<Agent> candidates = new ArrayList<Agent>();
				candidates.addAll(analyzer.getLatestGameInfo().getAliveAgentList());
				candidates.remove(owner.getAgent());
				if(candidates.size() == 0) {
					addFakeResult(owner.getAgent(), Species.WEREWOLF); // 破綻している場合
				}else {
					addFakeResult(getFakeTarget(), Species.HUMAN);
				}
			}
		}		
	}
	
	@Override
	public Queue<ITalkEvent> getTalkPlan(int restTalkCount) {
		Queue<ITalkEvent> talks = new LinkedList<ITalkEvent>();
		if(shouldComingOut()) {
			isComingOut = true;
			talks.add(new ComingOutTalk(owner.getAgent(), Role.SEER));
		}
		if(restTalkCount > 5){
			talks.add(new ITalkEvent() {
				@Override
				public String getTalk() {
					return TemplateTalkFactory.skip();
				}
			});
		}else {
			// TODO: タイミングをちゃんとする
			// 他の占いの様子を見てから騙り結果を作る
			createFakeResult();
			for(Judge fake : fakeResults) {
				if(fake == null) {
					continue;
				}
				if(!toldResults.contains(fake)){
					toldResults.add(fake);
					talks.add(new DivineTalk(fake.getTarget(), fake.getResult()));
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
		// TODO Auto-generated method stub

	}

}
