package com.gmail.yusatk.players;

import org.aiwolf.common.net.GameInfo;
import org.aiwolf.common.net.GameSetting;

import com.gmail.yusatk.data.WorldCache;
import com.gmail.yusatk.interfaces.IWorldCache;
import com.gmail.yusatk.plans.*;

public class Bodyguard extends PlayerBase {
	IWorldCache worldCache = new WorldCache();
	@Override
	public void initialize(GameInfo gameInfo, GameSetting gameSetting) {
		super.initialize(gameInfo, gameSetting);
		addPlan(new BodyguardPlan(this, analyzer, worldCache));
		addPlan(new AllSiteTrackPlan(this, analyzer, worldCache));
	}
}
