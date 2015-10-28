package com.gmail.yusatk.plans;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.aiwolf.common.data.*;

import com.gmail.yusatk.interfaces.IAgentEx;
import com.gmail.yusatk.interfaces.IAnalyzer;
import com.gmail.yusatk.interfaces.IBattlePlan;
import com.gmail.yusatk.interfaces.ITalkEvent;
import com.gmail.yusatk.interfaces.IWorld;
import com.gmail.yusatk.interfaces.IWorldCache;

public class BodyguardMiddlePhasePlan extends DefaultBattlePlan {
	IAgentEx owner = null;
	IAnalyzer analyzer = null;
	IWorldCache worldCache = null;
	
	public BodyguardMiddlePhasePlan(IAgentEx owner, IAnalyzer analyzer, IWorldCache worldCache) {
		this.owner = owner;
		this.analyzer = analyzer;
		this.worldCache = worldCache;
	}
	
	@Override
	public IBattlePlan planUpdate(boolean dayStartUpdate) {
		worldCache.getWorlds().removeIf(world->{
			Agent bg = world.getBodyguard();
			if(bg != null && bg != owner.getAgent()) {
				return true;
			}
			Role myRole = world.getRoles().get(owner.getAgent());
			if(myRole != null && myRole != Role.BODYGUARD) {
				return true;
			}
			return false;
		});
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

	Agent selectGuardTarget(IWorld world) {
		// 決め打ちの占い師が生きているならそれを護衛
		// TODO: 仕事終了なら他を守るべき
		Agent seer = world.getSeer();
		List<Agent> alives = analyzer.getLatestGameInfo().getAliveAgentList();
		if(seer != null && alives.contains(seer)) {
			return seer;
		}

		// TODO: ちゃんと護衛候補を選ぶ選ぶ
		List<Agent> guardCandidates = new ArrayList<Agent>();
		guardCandidates.addAll(alives);
		guardCandidates.remove(owner.getAgent());
		
		return guardCandidates.get(0);
	}
	
	@Override
	public Agent getGuardPlan() {
		// TODO: 選択中のワールドの同期を取る
		return selectGuardTarget(worldCache.getWorlds().get(0));
	}

	@Override
	public Agent getDivinePlan() {
		return null;
	}

	@Override
	public void dayStart() {
		
	}

}
