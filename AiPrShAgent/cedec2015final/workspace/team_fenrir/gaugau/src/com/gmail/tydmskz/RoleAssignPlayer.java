package com.gmail.tydmskz;

import org.aiwolf.client.base.player.AbstractRoleAssignPlayer;

public class RoleAssignPlayer extends AbstractRoleAssignPlayer{

	public RoleAssignPlayer() {



		PlayerParamaters param = new PlayerParamaters();

		setBodyguardPlayer(new BodyguradPlayer(param));
		setMediumPlayer(new MediumPlayer(param));
		setVillagerPlayer(new VillagerPlayer(param));
		setSeerPlayer(new SeerPlayer(param));
		setPossessedPlayer(new PossessedPlayer(param));
		setWerewolfPlayer(new WerewolfPlayer(param));

	}

	@Override
	public String getName() {
		// TODO 自動生成されたメソッド・スタブ

		String agentName = "deepFenrir";//だったっけ・・？
		return agentName;
	}

}
