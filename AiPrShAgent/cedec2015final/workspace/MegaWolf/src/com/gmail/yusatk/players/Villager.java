/**
 * 
 */
package com.gmail.yusatk.players;

import org.aiwolf.common.net.*;

import com.gmail.yusatk.data.WorldCache;
import com.gmail.yusatk.interfaces.IWorldCache;
import com.gmail.yusatk.plans.AllSiteTrackPlan;

/**
 * @author Yu
 *
 */
public class Villager extends PlayerBase {
	
	IWorldCache worldCache = new WorldCache();
	@Override
	public void initialize(GameInfo gameInfo, GameSetting gameSetting) {
		super.initialize(gameInfo, gameSetting);
		AllSiteTrackPlan plan = new AllSiteTrackPlan(this, analyzer, worldCache);
		this.addPlan(plan);
	}

}
