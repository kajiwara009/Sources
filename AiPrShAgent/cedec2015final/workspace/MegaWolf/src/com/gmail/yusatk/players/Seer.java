/**
 * 
 */
package com.gmail.yusatk.players;

import org.aiwolf.common.net.*;

import com.gmail.yusatk.data.*;
import com.gmail.yusatk.interfaces.IWorldCache;
import com.gmail.yusatk.plans.*;

public class Seer extends PlayerBase{
	IWorldCache worldCache = new WorldCache();
	
	@Override
	public void initialize(GameInfo gameInfo, GameSetting gameSetting){
		super.initialize(gameInfo, gameSetting);
		addPlan(new AllSiteTrackPlan(this, analyzer, worldCache));
		addPlan(new SeerPlan(this, analyzer, worldCache));
	}

}
