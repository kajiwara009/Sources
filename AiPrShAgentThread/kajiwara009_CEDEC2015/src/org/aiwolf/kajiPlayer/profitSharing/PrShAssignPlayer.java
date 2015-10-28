package org.aiwolf.kajiPlayer.profitSharing;

import org.aiwolf.client.base.player.AbstractRoleAssignPlayer;

public class PrShAssignPlayer extends AbstractRoleAssignPlayer {

	public PrShAssignPlayer() {
		setVillagerPlayer(new PrShVillager());
		setBodyguardPlayer(new PrShGuard());
		setMediumPlayer(new PrShMedium());
		setSeerPlayer(new PrShSeer());
		//TODO 村人以外もセットする．
	}

	@Override
	public String getName() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

}
