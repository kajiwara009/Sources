package jp.ac.aitech.k13009kk.aiwolf.client.player;

import org.aiwolf.client.base.player.AbstractRoleAssignPlayer;

/**
 * エージェントへの役職の割り当て
 * @author keisuke 愛知工業大学 K13009 安藤圭祐
 * @version AndoAgent 1.0
 *
 */
public class AndoRoleAssignPlayer extends AbstractRoleAssignPlayer {

	public AndoRoleAssignPlayer() {
		this.setSeerPlayer(new AndoSeer());
		this.setMediumPlayer(new AndoMedium());
		this.setBodyguardPlayer(new AndoBodyguard());
		this.setPossessedPlayer(new AndoPossessed());
		this.setWerewolfPlayer(new KamiwadaWerewolf());
		this.setVillagerPlayer(new AndoVillager());
		/*
		Random rand = new Random();
		double randNum = rand.nextDouble();
		AbstractAndoWerewolf werewolf = randNum < 0.5 ? new AndoWerewolf() : new KamiwadaWerewolf();
		this.setWerewolfPlayer(werewolf);
		*/

	}

	@Override
	public String getName() {
		return null;
	}

}
