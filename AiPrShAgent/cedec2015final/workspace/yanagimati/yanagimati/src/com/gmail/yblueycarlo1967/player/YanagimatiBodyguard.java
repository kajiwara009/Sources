package com.gmail.yblueycarlo1967.player;

import java.util.List;

import org.aiwolf.client.base.player.AbstractBodyguard;
import org.aiwolf.client.lib.TemplateTalkFactory;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Judge;
import org.aiwolf.common.net.GameInfo;

import com.gmail.yblueycarlo1967.brain.GameBrain;

public class YanagimatiBodyguard extends AbstractBodyguard {
	private GameBrain gameBrain=new GameBrain(this);

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
	public Agent guard() {
		//真占いが見つかってるなら護衛
		if(gameBrain.getTruthSeer()!=null){
			Agent seer=gameBrain.getTruthSeer();
			if(gameBrain.isAgentAlive(seer)){
				return seer;
			}
		}
		int seerCount=gameBrain.getSeerCOAgent().size();
		int mediumCount=gameBrain.getMediumCOAgent().size();
		List<Agent> seerCOAgent=gameBrain.getAliveSeerCOAgents();
		List<Agent> mediumCOAgent=gameBrain.getAliveMediumCOAgents();
		
		//2-1や1-1などの展開なら占い護衛
		if(seerCount-mediumCount<2){
			if(seerCOAgent.size()>0){
				return gameBrain.randomSelect(seerCOAgent);
			}
			//いなければ霊
			if(mediumCOAgent.size()>0){
				return gameBrain.randomSelect(mediumCOAgent);
			}
		}
		//3-1なら霊能護衛
		else {
			if(mediumCOAgent.size()>0){
				return gameBrain.randomSelect(mediumCOAgent);
			}
		}
		//確定白がいれば確定白
		List<Agent> white=gameBrain.getWhiteDefiniteAgent();
		if(white.size()>0) return gameBrain.randomSelect(white);
		
		return gameBrain.randomSelect(gameBrain.getAliveAgentsExceptMe());
	}

	@Override
	public String talk() {
		if(gameBrain.getJudgeIsDivinedWolf()!=null){
			if(gameBrain.didComingOut()==false) return gameBrain.comingOut();
		}
		if(gameBrain.haveVoteSpeaking()){
			return gameBrain.speakAboutVoteing();
		}
		//皆の投票先予想がだいたい出揃っていて、1日目以降で投票で処刑されそうならCO
		if(gameBrain.getTodayVote().size()>gameBrain.getAliveAgents().size()/2){
			List<Agent> candidate=gameBrain.mostVotedAgents();
			if(candidate.contains(getMe())&& this.getDay()>0){
				if(gameBrain.didComingOut()==false) return gameBrain.comingOut();
			}
		}
		
		return TemplateTalkFactory.over();
	}

	@Override
	public Agent vote() {
		return gameBrain.getTodayVoteTarget();
	}

}
