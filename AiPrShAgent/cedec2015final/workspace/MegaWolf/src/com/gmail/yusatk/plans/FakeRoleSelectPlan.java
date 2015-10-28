package com.gmail.yusatk.plans;

import java.util.*;

import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Role;

import com.gmail.yusatk.interfaces.*;
import com.gmail.yusatk.utils.DebugLog;

public class FakeRoleSelectPlan extends DefaultBattlePlan {
	IAgentEx owner = null;
	IAnalyzer analyzer = null;
	IWorldCache worldCache = null;
	
	boolean mediumHighPriority = false;
	
	private void setProperties() {
		String mediumPriorityString = java.lang.System.getProperty("com.gmail.yusatk.plan.FakeRoleSelectPlan.mediumHighPriority");
		if(mediumPriorityString != null) {
			mediumHighPriority = Boolean.parseBoolean(mediumPriorityString);
		}
	}
	
	public FakeRoleSelectPlan(IAgentEx owner, IAnalyzer analyzer, IWorldCache worldCache) {
		this.owner = owner;
		this.analyzer = analyzer;
		this.worldCache = worldCache;
		
		setProperties();
	}

	private IBattlePlan getFakeSeerPlan() {
		if(owner.getRole() == Role.WEREWOLF) {
			return new FakeSeerForWolfPlan(owner, analyzer, worldCache);
		} else if(owner.getRole() == Role.POSSESSED) {
			return new FakeSeerPlan(owner, analyzer, worldCache);
		}
		assert false;
		return null;
	}
	
	private IBattlePlan getFakeMediumPlan() {
		if(owner.getRole() == Role.WEREWOLF) {
			return new FakeMediumForWolfPlan(owner, analyzer, worldCache);
		} else if(owner.getRole() == Role.POSSESSED) {
			return new FakeMediumPlan(owner, analyzer, worldCache);
		}
		assert false;
		return null;
	}

	boolean shouldBeHide() {
		if(analyzer.getDay() == 0){
			return true;
		}
		int nakedEnemyCount = analyzer.getNakedEnemyCount();
		int wolfCount = analyzer.getSetting().getRoleNum(Role.WEREWOLF);
		int possessedCount = analyzer.getSetting().getRoleNum(Role.POSSESSED);
		int enemyCount = wolfCount + possessedCount;
		if(nakedEnemyCount >= enemyCount - 1) {
			return true;
		}
		return false;
	}
	
	boolean shouldBeFakeSeer() {
		int seerCount = analyzer.getSeers().size();
		//TODO: 人外数に合わせて計算するようにする。とりあえず3狼、1狂決め打ち
		if(seerCount < 3) {
			DebugLog.log("**DECIDING FAKE SEER**\n");
			analyzer.Dump();
			// もし仲間狼が出ているようなら占い騙りはやめる
			List<Agent> seers = analyzer.getSeers();
			List<Agent> buddies = analyzer.getBuddyWolves();
			for(Agent buddy : buddies) {
				if(seers.contains(buddy)) {
					return false;
				}
			}
			return true;
		}
		return false;
	}
	
	boolean shouldBeFakeMedium() {
		int mediumCount = analyzer.getMediums().size();
		//TODO: 人外数に合わせて計算するようにする。とりあえず3狼、1狂決め打ち
		if(mediumCount < 3) {
			// もし仲間狼が出ているようなら霊能騙りはやめる
			List<Agent> mediums = analyzer.getMediums();
			List<Agent> buddies = analyzer.getBuddyWolves();
			for(Agent buddy : buddies) {
				if(mediums.contains(buddy)) {
					return false;
				}
			}
			return true;
		}
		return false;
	}
	
	@Override
	public IBattlePlan planUpdate(boolean dayStartUpdate) {
		if(dayStartUpdate) {
			return this;
		}
		if(shouldBeHide()) {
			owner.setAssumptionRole(Role.VILLAGER);
			return this;
		}
		if(mediumHighPriority && shouldBeFakeMedium()) {
			owner.setAssumptionRole(Role.MEDIUM);
			IBattlePlan plan = getFakeMediumPlan();
			plan = plan.planUpdate(dayStartUpdate);
			return plan;
		}
		if(shouldBeFakeSeer()) {
			owner.setAssumptionRole(Role.SEER);
			IBattlePlan plan = getFakeSeerPlan();
			plan = plan.planUpdate(dayStartUpdate);
			return plan;
		}
		if(shouldBeFakeMedium()) {
			owner.setAssumptionRole(Role.MEDIUM);
			IBattlePlan plan = getFakeMediumPlan();
			plan = plan.planUpdate(dayStartUpdate);
			return plan;
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

	@Override
	public Agent getGuardPlan() {
		return null;
	}

	@Override
	public Agent getDivinePlan() {
		return null;
	}

	@Override
	public void dayStart() {
	}

}
