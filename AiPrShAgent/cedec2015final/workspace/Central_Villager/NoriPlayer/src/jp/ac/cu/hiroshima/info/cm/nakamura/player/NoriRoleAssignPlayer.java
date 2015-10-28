package jp.ac.cu.hiroshima.info.cm.nakamura.player;

import jp.ac.cu.hiroshima.info.cm.nakamura.base.player.NoriAbstractRoleAssignPlayer;
/**
 * Sampleのみを使って起動するPlayer
 * @author tori
 *
 */
public class NoriRoleAssignPlayer extends NoriAbstractRoleAssignPlayer {

	public NoriRoleAssignPlayer(){
		setVillagerPlayer(new NoriVillager());
		setSeerPlayer(new NoriSeer());
		setMediumPlayer(new NoriMedium());
		setBodyguardPlayer(new NoriBodyguard());
		setPossessedPlayer(new NoriPossessed());
		setWerewolfPlayer(new NoriWerewolf());
	}

	@Override
	public String getName() {
		return "NoriRoleAssignPlayer";
	}
}
