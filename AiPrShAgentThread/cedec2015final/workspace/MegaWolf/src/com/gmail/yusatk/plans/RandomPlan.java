package com.gmail.yusatk.plans;

import org.aiwolf.common.data.*;

import java.util.*;

import com.gmail.yusatk.interfaces.IAgentEx;
import com.gmail.yusatk.interfaces.IAnalyzer;
import com.gmail.yusatk.interfaces.IWorldCache;
import com.gmail.yusatk.utils.DebugLog;

/**
 * 通常このプランが使われることはない。
 * 決め打ち進行で決め打ちすべき世界がないような不正な状態の場合にこのプランに遷移してくる。
 * このプランでは自分以外のランダムに投票を行う。
 * 会話は行わない。
 * @author Yu
 *
 */
public class RandomPlan extends DefaultBattlePlan {
	IAgentEx owner;
	IAnalyzer analyzer;
	IWorldCache worldCache;

	public RandomPlan(IAgentEx owner, IAnalyzer analyzer, IWorldCache worldCache) {
		this.owner = owner;
		this.analyzer = analyzer;
		this.worldCache = worldCache;
		DebugLog.log("*****************************\n");
		DebugLog.log("* RANDOM PLAN IS USED!!!    *\n");
		DebugLog.log("*****************************\n");
	}
	
	@Override
	public Agent getVotePlan() {
		List<Agent> candidates = new LinkedList<Agent>();
		candidates.addAll(analyzer.getAliveAgents());
		candidates.remove(owner.getAgent());
		Random r = new Random();
		int index = r.nextInt(candidates.size());
		return candidates.get(index);
	}
}
