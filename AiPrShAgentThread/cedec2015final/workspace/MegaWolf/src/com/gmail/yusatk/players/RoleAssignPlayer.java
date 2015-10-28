package com.gmail.yusatk.players;

import org.aiwolf.client.base.player.AbstractRoleAssignPlayer;

public class RoleAssignPlayer extends AbstractRoleAssignPlayer {

	public RoleAssignPlayer() {
		setSeerPlayer(new Seer());
		setPossessedPlayer(new Possessed());
		setVillagerPlayer(new Villager());
		setMediumPlayer(new Medium());
		setWerewolfPlayer(new Wolf());
		setBodyguardPlayer(new Bodyguard());
	}
	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

}
