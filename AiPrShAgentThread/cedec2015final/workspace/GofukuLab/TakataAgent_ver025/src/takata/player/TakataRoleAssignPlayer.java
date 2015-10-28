package takata.player;

import org.aiwolf.client.base.player.AbstractRoleAssignPlayer;


public class TakataRoleAssignPlayer extends AbstractRoleAssignPlayer {

	public TakataRoleAssignPlayer(){
		setVillagerPlayer(new TakataVillagerPlayer_ver025());
		setSeerPlayer(new TakataSeerPlayer_ver025());
		setMediumPlayer(new TakataMediumPlayer_ver025());
		setBodyguardPlayer(new TakataBodyGuardPlayer_ver025());
		setPossessedPlayer(new TakataPossessedPlayer_ver025());
		setWerewolfPlayer(new TakataWerewolfPlayer_ver025());

	}

	@Override
	public String getName() {
		return TakataRoleAssignPlayer.class.getSimpleName();
	}

}
