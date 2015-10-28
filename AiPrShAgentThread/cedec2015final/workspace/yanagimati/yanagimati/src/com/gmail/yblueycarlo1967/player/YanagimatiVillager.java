package com.gmail.yblueycarlo1967.player;

import java.util.*;

import org.aiwolf.client.base.player.AbstractVillager;
import org.aiwolf.client.lib.TemplateTalkFactory;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Judge;
import org.aiwolf.common.net.GameInfo;

import com.gmail.yblueycarlo1967.brain.GameBrain;

public class YanagimatiVillager extends AbstractVillager {
	private GameBrain gameBrain=new GameBrain(this);

	public void update(GameInfo gameInfo){
		super.update(gameInfo);
		gameBrain.update(gameInfo);
	}
	@Override
	public void dayStart() {
		// TODO 自動生成されたメソッド・スタブ
		gameBrain.dayStart();

	}

	@Override
	public void finish() {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	/*
	public String talk() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}*/
	public String talk() {
		//人狼だと言われた場合
		if(gameBrain.getJudgeIsDivinedWolf()!=null){
			if(gameBrain.didComingOut()==false) return gameBrain.comingOut();;
		}
		if(gameBrain.haveVoteSpeaking()){
			return gameBrain.speakAboutVoteing();
		}
		return TemplateTalkFactory.over();
	}

	@Override
	public Agent vote() {
		return gameBrain.getTodayVoteTarget();
	}

}
