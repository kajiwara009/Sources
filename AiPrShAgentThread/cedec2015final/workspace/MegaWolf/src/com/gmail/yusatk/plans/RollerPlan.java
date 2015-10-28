package com.gmail.yusatk.plans;

import java.util.*;

import org.aiwolf.client.lib.TemplateTalkFactory.TalkType;
import org.aiwolf.common.data.Agent;

import com.gmail.yusatk.interfaces.*;
import com.gmail.yusatk.talks.VoteTalk;

/**
 * 人外が全露呈している場合に遷移してくるプランです。
 * 役職吊りきりで勝ち確定になります。
 * 人外側の票合わせに負けないように、意思統一をちゃんとやる必要があります。
 * @author Yu
 *
 */
public class RollerPlan extends DefaultBattlePlan {
	IAgentEx owner = null;
	IAnalyzer analyzer = null;
	IWorldCache worldCache = null;
	Agent votePlan = null;
	
	public RollerPlan(IAgentEx owner, IAnalyzer analyzer, IWorldCache worldCache) {
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
		Agent newVotePlan = getVotePlan();
		Agent oldVotePlan = votePlan;
		if(newVotePlan != oldVotePlan) {
			votePlan = newVotePlan;
			talks.add(new VoteTalk(TalkType.TALK, votePlan));
		}
		
		return talks;
	}


	@Override
	public Agent getVotePlan() {
		List<Agent> coAgents = analyzer.getCoAgents();
		List<Agent> candidates = new LinkedList<Agent>();
		List<Agent> alives = analyzer.getAliveAgents();
		for(Agent co : coAgents) {
			if(alives.contains(co)) {
				candidates.add(co);
			}
		}
		
		assert candidates.size() > 0;
		// TODO: 投票先をきっちり合わせるようにする
		return candidates.get(0);
	}

	@Override
	public void dayStart() {
		votePlan = null;
	}
}
