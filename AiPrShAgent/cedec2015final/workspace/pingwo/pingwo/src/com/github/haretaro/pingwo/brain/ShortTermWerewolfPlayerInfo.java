package com.github.haretaro.pingwo.brain;

import org.aiwolf.common.data.Agent;

public class ShortTermWerewolfPlayerInfo extends ShortTermPlayerInfo {
	
	private boolean isWerewolf = false;

	public ShortTermWerewolfPlayerInfo(Agent agent) {
		super(agent);
	}
	
	public boolean isWerewolf(){
		return isWerewolf;
	}
	
	@Override
	public void setWerewolf(){
		isWerewolf = true;
	}

}
