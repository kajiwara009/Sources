package com.gmail.yblueycarlo1967.player;

import org.aiwolf.client.base.player.AbstractPossessed;
import org.aiwolf.client.lib.TemplateTalkFactory;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Judge;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.net.GameInfo;

import com.gmail.yblueycarlo1967.brain.GameBrain;
import com.gmail.yblueycarlo1967.brain.PGameBrain;

public class YanagimatiPossessed extends AbstractPossessed {

	private PGameBrain gameBrain=new PGameBrain(this);

	public void update(GameInfo gameInfo){
		super.update(gameInfo);
		gameBrain.update(gameInfo);
	}
	@Override
	public void dayStart() {
		gameBrain.dayStart();

	}

	@Override
	public void finish() {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public String talk() {
		//村人以外を騙る予定なら、その動きに従う
		if(gameBrain.getFakeRole()!=Role.VILLAGER){
			String talk=gameBrain.talkSpecial();
			if(talk!=null) return talk;
		}
		//ないなら投票先
		if(gameBrain.haveVoteSpeaking()){
			return gameBrain.speakAboutVoteing();
		}
		return TemplateTalkFactory.over();
	}

	@Override
	public Agent vote() {
		//return gameBrain.randomSelect(gameBrain.getAliveAgentsExceptMe());
		return gameBrain.getTodayVoteTarget();
	}

}
