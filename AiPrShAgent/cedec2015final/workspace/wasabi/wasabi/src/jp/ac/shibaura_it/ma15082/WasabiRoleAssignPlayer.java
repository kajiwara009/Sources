package jp.ac.shibaura_it.ma15082;


import org.aiwolf.client.base.player.AbstractRoleAssignPlayer;



public class WasabiRoleAssignPlayer extends AbstractRoleAssignPlayer{

	public WasabiRoleAssignPlayer(){
		
		setVillagerPlayer(new WasabiPlayer());
		setBodyguardPlayer(new WasabiPlayer());
		setMediumPlayer(new WasabiPlayer());
		setPossessedPlayer(new WasabiPlayer());
		setSeerPlayer(new WasabiPlayer());
		setWerewolfPlayer(new WasabiPlayer());
	}
	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "wasabi";
	}

}
