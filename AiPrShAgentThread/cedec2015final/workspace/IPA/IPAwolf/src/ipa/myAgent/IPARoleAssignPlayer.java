package ipa.myAgent;

import org.aiwolf.client.base.player.AbstractRoleAssignPlayer;

public class IPARoleAssignPlayer extends AbstractRoleAssignPlayer {

	public IPARoleAssignPlayer(){
		setBodyguardPlayer(new IPABodyguard());
		setMediumPlayer(new IPAMedium());
		setPossessedPlayer(new IPAPossessed());
		setSeerPlayer(new IPASeer());
		setVillagerPlayer(new IPAVillager());
		setWerewolfPlayer(new IPAWerewolf());
	}
	@Override
	public String getName() {
		return "IPA";
	}

}
