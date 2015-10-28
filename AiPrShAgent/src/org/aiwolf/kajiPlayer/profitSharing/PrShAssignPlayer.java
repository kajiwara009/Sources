package org.aiwolf.kajiPlayer.profitSharing;

import org.aiwolf.client.base.player.AbstractRoleAssignPlayer;
import org.aiwolf.kajiClient.player.KajiPossessed;
import org.aiwolf.kajiClient.player.KajiWerewolf;
import org.aiwolf.kajiPlayer.noAction.NoActionPlayer;
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

	@Override
	public String getName() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

}
