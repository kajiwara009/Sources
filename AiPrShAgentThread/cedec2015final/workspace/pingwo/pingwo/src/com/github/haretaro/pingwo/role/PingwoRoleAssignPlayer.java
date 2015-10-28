package com.github.haretaro.pingwo.role;
import org.aiwolf.client.base.player.AbstractRoleAssignPlayer;

public class PingwoRoleAssignPlayer extends AbstractRoleAssignPlayer {
	
	public PingwoRoleAssignPlayer(){
		setSeerPlayer(new PingwoSeer());
		setVillagerPlayer(new PingwoVillager());
		setWerewolfPlayer(new PingwoWerewolf());
		setPossessedPlayer(new JimPossessed());
		setBodyguardPlayer(new PingwoGuard());
		setMediumPlayer(new PingwoMedium());
	}

	@Override
	public String getName() {
		return "Pingwo";
	}

}
