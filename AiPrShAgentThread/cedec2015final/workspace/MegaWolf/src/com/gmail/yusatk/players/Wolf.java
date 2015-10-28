package com.gmail.yusatk.players;

import org.aiwolf.common.net.GameInfo;
import org.aiwolf.common.net.GameSetting;

import com.gmail.yusatk.data.WorldCache;
import com.gmail.yusatk.interfaces.IWorldCache;
import com.gmail.yusatk.plans.AllSiteTrackPlan;
import com.gmail.yusatk.plans.FakeRoleSelectPlan;
import com.gmail.yusatk.plans.WolfBasePlan;

public class Wolf extends PlayerBase {
	IWorldCache worldCache = new WorldCache();
	@Override
	public void initialize(GameInfo gameInfo, GameSetting gameSetting) {
		super.initialize(gameInfo, gameSetting);
		addPlan(new FakeRoleSelectPlan(this, analyzer, worldCache));
		addPlan(new AllSiteTrackPlan(this, analyzer, worldCache));
		addPlan(new WolfBasePlan(this, analyzer, worldCache));
	}
}
