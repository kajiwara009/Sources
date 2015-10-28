/**
 * 
 */
package com.gmail.yusatk.players;

import java.util.ArrayList;
import java.util.List;

import org.aiwolf.common.data.Judge;
import org.aiwolf.common.net.GameInfo;
import org.aiwolf.common.net.GameSetting;

import com.gmail.yusatk.data.WorldCache;
import com.gmail.yusatk.interfaces.*;
import com.gmail.yusatk.plans.AllSiteTrackPlan;
import com.gmail.yusatk.plans.MediumPlan;

/**
 * @author Yu
 *
 */
public class Medium extends PlayerBase {
	IWorldCache worldCache = new WorldCache();
	
	@Override
	public void initialize(GameInfo gameInfo, GameSetting gameSetting) {
		super.initialize(gameInfo, gameSetting);
		addPlan(new MediumPlan(this, analyzer));
		addPlan(new AllSiteTrackPlan(this, analyzer, worldCache));
	}
	
	class DayStatus {
		int talkVotePlanCount = 0;
	}
	
	DayStatus currentDayStatus = new DayStatus();
	
	List<Judge> inquestResults = new ArrayList<Judge>();
	List<Judge> toldInquestResults = new ArrayList<Judge>();
	
	/* (non-Javadoc)
	 * @see org.aiwolf.client.base.player.AbstractVillager#dayStart()
	 */
	@Override
	public void dayStart() {
		super.dayStart();

	}
	

	
}
