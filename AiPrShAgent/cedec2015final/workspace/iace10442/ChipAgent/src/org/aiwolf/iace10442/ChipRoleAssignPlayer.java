package org.aiwolf.iace10442;

import org.aiwolf.client.base.player.AbstractRoleAssignPlayer;


public class ChipRoleAssignPlayer extends AbstractRoleAssignPlayer {


	public ChipRoleAssignPlayer(){
		setVillagerPlayer(new ChipVillagerPlayer());
		setBodyguardPlayer(new ChipBodyGuardPlayer());
		setMediumPlayer(new ChipMediumPlayer());
		setPossessedPlayer(new ChipPossessedPlayer());
		setSeerPlayer(new ChipSeerPlayer());
		setWerewolfPlayer(new ChipWereWolfPlayer());
	}

	@Override
	public String getName() {
		// TODO 自動生成されたメソッド・スタブ
		return "ChipAgent";
	}


}
