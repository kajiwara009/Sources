package com.gmail.yblueycarlo1967.player;

import org.aiwolf.client.base.player.AbstractRoleAssignPlayer;
import org.aiwolf.kajiClient.LearningPlayer.KajiBodyGuradPlayer;
import org.aiwolf.kajiClient.LearningPlayer.KajiMediumPlayer;
import org.aiwolf.kajiClient.LearningPlayer.KajiPossessedPlayer;
import org.aiwolf.kajiClient.LearningPlayer.KajiSeerPlayer;
import org.aiwolf.kajiClient.LearningPlayer.KajiVillagerPlayer;
import org.aiwolf.kajiClient.LearningPlayer.KajiWereWolfPlayer;
/**
 *  村側のみ自作プレイヤー
 * @author info
 *
 */
public class TestPlayer extends AbstractRoleAssignPlayer {
	public TestPlayer(){
		/*
		setSeerPlayer(new YanagimatiSeer());
		setVillagerPlayer(new YanagimatiVillager());
		setBodyguardPlayer(new YanagimatiBodyguard());
		setMediumPlayer(new YanagimatiMedium());
		*/
		
		setVillagerPlayer(new KajiVillagerPlayer());
		setBodyguardPlayer(new KajiBodyGuradPlayer());
		setMediumPlayer(new KajiMediumPlayer());
		setSeerPlayer(new KajiSeerPlayer());
		
		/*
		setWerewolfPlayer(new KajiWereWolfPlayer());
		setPossessedPlayer(new KajiPossessedPlayer());
		*/
		setWerewolfPlayer(new YanagimatiWerewolf());
		setPossessedPlayer(new YanagimatiPossessed());
		
	}

	@Override
	public String getName() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

}
