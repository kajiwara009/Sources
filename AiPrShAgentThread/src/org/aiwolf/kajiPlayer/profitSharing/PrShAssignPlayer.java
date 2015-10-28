package org.aiwolf.kajiPlayer.profitSharing;

import java.io.ObjectInputStream.GetField;

import javax.swing.text.Position;

import org.aiwolf.client.base.player.AbstractRole;
import org.aiwolf.client.base.player.AbstractRoleAssignPlayer;
import org.aiwolf.common.data.Role;
import org.aiwolf.kajiPlayer.noAction.NoActionRole;

public class PrShAssignPlayer extends AbstractRoleAssignPlayer {

	public PrShAssignPlayer() {
		
//		setVillagerPlayer(new NoActionPlayer());
//		setBodyguardPlayer(new NoActionPlayer());
//		setMediumPlayer(new NoActionPlayer());
//		setSeerPlayer(new NoActionPlayer());
		setWerewolfPlayer(new NoActionRole());
		setPossessedPlayer(new NoActionRole());

		
		setVillagerPlayer(new PrShVillager());
		setBodyguardPlayer(new PrShGuard());
		setMediumPlayer(new PrShMedium());
		setSeerPlayer(new PrShSeer());
//		setWerewolfPlayer(new KajiWerewolf());
//		setPossessedPlayer(new KajiPossessed());
		//TODO 村人以外もセットする．
	}
	
	public AbstractRole getRolePlayer(Role role){
		switch (role) {
		case VILLAGER:
			return getVillagerPlayer();
		case BODYGUARD:
			return getBodyguardPlayer();
		case FREEMASON:
			return null;
		case MEDIUM:
			return getMediumPlayer();
		case POSSESSED:
			return getPossessedPlayer();
		case SEER:
			return getSeerPlayer();
		case WEREWOLF:
			return getWerewolfPlayer();
		default:
			return null;
		}
	}

	@Override
	public String getName() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

}
