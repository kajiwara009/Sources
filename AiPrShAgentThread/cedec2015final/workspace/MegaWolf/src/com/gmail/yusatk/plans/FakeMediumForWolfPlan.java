package com.gmail.yusatk.plans;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Judge;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Species;

import com.gmail.yusatk.interfaces.*;
import com.gmail.yusatk.talks.ComingOutTalk;
import com.gmail.yusatk.talks.InquestTalk;

public class FakeMediumForWolfPlan extends DefaultBattlePlan {
	IAgentEx owner = null;
	IAnalyzer analyzer = null;
	IWorldCache worldCache = null;

	List<Judge> fakeResults = new ArrayList<Judge>();
	List<Judge> toldResults = new ArrayList<Judge>();
	List<Agent> executedAgents = new ArrayList<Agent>();
	List<Agent> fakedTargets = new ArrayList<Agent>();
	
	public FakeMediumForWolfPlan(IAgentEx owner, IAnalyzer analyzer, IWorldCache worldCache) {
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

	Judge createFakeJudge(Agent exed) {
		if(exed == null) {
			return null;
		}
		
		//TODO: 残り人外数に注意して破綻しないように結果を出す。
		
		// 占い師の正しい黒出し吊りに対しては白判定を出してラインを割る
		// 偽の黒出しに対しては黒判定を出してラインをつなぐ
		List<Agent> seers = analyzer.getSeers();
		List<Agent> buddies = analyzer.getBuddyWolves();
		boolean wolfExed = buddies.contains(exed);
		for(Agent seer : seers) {
			List<Judge> judges = analyzer.getDivineResultBySeer(seer);
			for(Judge judge : judges) {
				if(judge.getTarget() == exed) {
					if(judge.getResult() == Species.HUMAN) {
						if(wolfExed) {
							// 白出された狼が処刑されているケース
							// 偽占い確定なのでラインをつなぐ
							return new Judge(analyzer.getDay(), owner.getAgent(), exed, Species.HUMAN);
						} else {
							// 白出された村が処刑されているケース
							// 真贋の確定が不明なのでとりあえずここでは結果を確定しない
						}
					}
					if(judge.getResult() == Species.WEREWOLF) {
						if(wolfExed) {
							// 黒出された狼が処刑されているケース
							// 高確率で真占いなので、ラインを割る
							return new Judge(analyzer.getDay(), owner.getAgent(), exed, Species.HUMAN);
						} else {
							// 黒出された村が処刑されているケース
							// 偽占い確定なのでラインをつなぐ
							return new Judge(analyzer.getDay(), owner.getAgent(), exed, Species.WEREWOLF);
						}
					}
				}
			}
		}
		
		// ラインを細工したいケース以外はとりあえず正しい結果を言ってみる
		return new Judge(analyzer.getLatestGameInfo().getDay(), owner.getAgent(), exed, wolfExed ? Species.WEREWOLF : Species.HUMAN);
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
	public void dayStart() {
		executedAgents = analyzer.getExecutedAgents();
//		executedAgents.add(analyzer.getLatestGameInfo().getExecutedAgent());
	}
}
