package com.gmail.yusatk.plans;

import java.util.*;

import org.aiwolf.client.lib.TemplateTalkFactory.TalkType;
import org.aiwolf.common.data.*;

import com.gmail.yusatk.interfaces.*;
import com.gmail.yusatk.talks.ComingOutTalk;
import com.gmail.yusatk.talks.VoteTalk;
import com.gmail.yusatk.utils.LotHelper;



public class PossessedVotePlan extends DefaultBattlePlan {
	IAgentEx owner = null;
	IAnalyzer analyzer = null;
	
	IWorld assumedWorld;
	IWorldCache worldCache;
	
	Agent votePlan = null;
	
	public PossessedVotePlan(IAgentEx owner, IAnalyzer analyzer, IWorldCache worldCache)  {
		this.owner = owner;
		this.analyzer = analyzer;
		this.worldCache = worldCache;
	}

	boolean lastWolfCO = false;
	
	@Override
	public Queue<ITalkEvent> getTalkPlan(int restTalkCount) {
		Queue<ITalkEvent> talks = new LinkedList<ITalkEvent>();

		// 最終日
		if(analyzer.getRestExecutionCount() == 1) {
			if(!lastWolfCO) {
				lastWolfCO = true;
				if(LotHelper.chance(50)) { // とりあえず 50% の確率で狼CO
					talks.add(new ComingOutTalk(owner.getAgent(), Role.WEREWOLF));
				}
			}
		}
		
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
		if(analyzer.hasExtraExecution()) {
			// 決め打ちをしないタイミングの場合は、上位のプランの投票に従う。
			return null;
		}
		
		// 決め打ちしているタイミングの場合は
		// 基本的に狼を吊らないようにする。
		
		// 狼CO がある場合狼CO者の投票先に投票先を合わせる
		List<Agent> wolves = analyzer.getWolves();
		if(wolves.size() > 0) {
			for(Agent wolf : wolves) {
				if(wolf == owner.getAgent()) {
					continue;
				}
				Vote wolfVotePlan = analyzer.getVotePlan(wolf);
				if(wolfVotePlan != null) {
					return wolfVotePlan.getTarget();
				}
			};
		}
		
		// 狼CO がなく自分以外の占いCOが1名の場合はそれを真占いと判断して
		// 真占いの白に対して積極的に投票する
		List<Agent> seerCandidates = new LinkedList<Agent>();
		seerCandidates.addAll(analyzer.getSeers());
		seerCandidates.remove(owner.getAgent());
		if(seerCandidates.size() == 1) {
			List<Agent> humans = new LinkedList<Agent>();
			List<Judge> judges = analyzer.getDivineResultBySeer(seerCandidates.get(0));
			judges.forEach(judge->{
				if(judge.getResult() == Species.HUMAN){
					humans.add(judge.getTarget());
				}
			});
			// TODO: ちゃんと選べ
			humans.get(0);
		}
		
		// 自分が狂人として当てはまる仮想世界を選択
		IWorld world = null;
		for(IWorld w : worldCache.getWorlds()) {
			// TODO: ちゃんと選べ
			if(w.getRoles().get(owner.getAgent()) == Role.POSSESSED) {
				world = w;
				break;
			}
		}
		
		List<Agent> candidates = new LinkedList<Agent>();
		candidates.addAll(analyzer.getAliveAgents());
		candidates.remove(owner.getAgent());
		// 仮想世界で狼が確定している人には投票しない
		if(world != null) {
			candidates.remove(owner.getAgent());
			candidates.removeAll(world.getAliveWolves());
			// TODO: ちゃんと選べ
			return candidates.get(0);
		}
		
		// ここまできたら狼っぽいのがどこかわかっていない。
		// ランダム投票にする
		Random r = new Random();
		return candidates.get(r.nextInt(candidates.size()));
	}
}
