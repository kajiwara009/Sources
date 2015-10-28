package com.gmail.yblueycarlo1967.player;

import org.aiwolf.client.base.player.AbstractRoleAssignPlayer;

public class YanagimatiRoleAssignPlayer extends AbstractRoleAssignPlayer {
	public YanagimatiRoleAssignPlayer(){
		setSeerPlayer(new YanagimatiSeer());
		setVillagerPlayer(new YanagimatiVillager());
		setBodyguardPlayer(new YanagimatiBodyguard());
		setMediumPlayer(new YanagimatiMedium());
		setWerewolfPlayer(new YanagimatiWerewolf());
		setPossessedPlayer(new YanagimatiPossessed());
	}

	@Override
	public String getName() {
		return "YanagimatiAssignPlayer";
	}

}
