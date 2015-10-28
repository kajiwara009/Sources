package jp.halfmoon.inaba.aiwolf.strategyplayer;

import org.aiwolf.client.base.player.AbstractRoleAssignPlayer;

public class StrategyPlayer extends AbstractRoleAssignPlayer {

	public StrategyPlayer() {
		setVillagerPlayer(new StrategyVillager());
		setSeerPlayer(new StrategySeer());
		setMediumPlayer(new StrategyMedium());
		setBodyguardPlayer(new StrategyBodyGuard());
		setPossessedPlayer(new StrategyPossessed());
		setWerewolfPlayer(new StrategyWerewolf());
	}


	@Override
	public String getName() {
		return StrategyPlayer.class.getSimpleName();
	}


}
