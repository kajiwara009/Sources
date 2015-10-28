/**
 * 
 */
package com.gmail.yusatk.players;
import org.aiwolf.common.net.GameInfo;
import org.aiwolf.common.net.GameSetting;

import com.gmail.yusatk.data.WorldCache;
import com.gmail.yusatk.interfaces.*;
import com.gmail.yusatk.plans.AllSiteTrackPlan;
import com.gmail.yusatk.plans.FakeRoleSelectPlan;
import com.gmail.yusatk.plans.PossessedVotePlan;

/**
 * @author Yu
 *
 */
public class Possessed extends PlayerBase {
	IWorldCache worldCache = new WorldCache();
	@Override
	public void initialize(GameInfo gameInfo, GameSetting gameSetting) {
		super.initialize(gameInfo, gameSetting);
		addPlan(new AllSiteTrackPlan(this, analyzer, worldCache));
		addPlan(new FakeRoleSelectPlan(this, analyzer, worldCache));
		addPlan(new PossessedVotePlan(this, analyzer, worldCache));
	}
	
	
}
