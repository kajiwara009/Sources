package com.gmail.yblueycarlo1967.player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.aiwolf.client.base.player.AbstractMedium;
import org.aiwolf.client.base.smpl.AdvanceGameInfo;
import org.aiwolf.client.lib.TemplateTalkFactory;
import org.aiwolf.client.lib.Utterance;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Judge;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Species;
import org.aiwolf.common.data.Talk;
import org.aiwolf.common.net.GameInfo;
import org.aiwolf.common.net.GameSetting;

import com.gmail.yblueycarlo1967.brain.SpecialGameBrain;

public class YanagimatiMedium extends AbstractMedium {
	private SpecialGameBrain gameBrain=new SpecialGameBrain(this);


	@Override
	public void initialize(GameInfo gameInfo, GameSetting gameSetting) {
		super.initialize(gameInfo, gameSetting);

		//comingoutDay = new Random().nextInt(3)+1;
		//isCameout = false;
	}


	@Override
	public void dayStart() {
		super.dayStart();
		gameBrain.dayStart();

	}

	@Override
	public String talk() {
		//COや能力結果についてしゃべることがあるならしゃべる
		String talk=gameBrain.talkSpecial();
		if(talk!=null) return talk;
		//ないなら投票先
		if(gameBrain.haveVoteSpeaking()){
			return gameBrain.speakAboutVoteing();
		}
		return Talk.OVER;
	}

	@Override
	public Agent vote() {
		return gameBrain.getTodayVoteTarget();
	}

	@Override
	public void finish() {
		// TODO 自動生成されたメソッド・スタブ

	}


	@Override
	public void update(GameInfo gameInfo){
		super.update(gameInfo);
		gameBrain.update(gameInfo);
	}

	

}
