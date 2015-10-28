package org.aiwolf.client.base.smpl;

import org.aiwolf.client.base.player.AbstractRoleAssignPlayer;
/**
 * Sampleのみを使って起動するPlayer
 * @author tori
 *
 */
public class SampleRoleAssignPlayer extends AbstractRoleAssignPlayer {

	public SampleRoleAssignPlayer(){
		setVillagerPlayer(new SampleVillagerPlayer());
		setSeerPlayer(new SampleSeerPlayer());
		setMediumPlayer(new SampleMediumPlayer());
		setBodyGuardPlayer(new SampleBodyGuardPlayer());
		setPossesedPlayer(new SamplePossesedPlayer());
		setWerewolfPlayer(new SampleWereWolfPlayer());
	}

	@Override
	public String getName() {
		return SampleRoleAssignPlayer.class.getSimpleName();
	}	
}
