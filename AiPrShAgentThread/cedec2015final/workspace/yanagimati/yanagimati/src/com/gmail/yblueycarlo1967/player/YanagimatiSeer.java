package com.gmail.yblueycarlo1967.player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.aiwolf.client.base.player.AbstractSeer;
import org.aiwolf.client.lib.TemplateTalkFactory;
import org.aiwolf.client.lib.Utterance;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Judge;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Species;
import org.aiwolf.common.data.Talk;
import org.aiwolf.common.net.GameInfo;

import com.gmail.yblueycarlo1967.brain.SpecialGameBrain;

public class YanagimatiSeer extends AbstractSeer {
	private SpecialGameBrain gameBrain=new SpecialGameBrain(this);
	@Override
	public Agent divine() {
		List<Agent> mostVotedAgents=gameBrain.mostVotedAgents();
		List<Agent> divineCandidates=null;
		//自分、対抗、霊媒、自分が占ったもの、投票されそうな人物を除いて占う
		divineCandidates=getAliveAgentExceptDivined();
		divineCandidates.removeAll(gameBrain.getSeerCOAgent());
		divineCandidates.removeAll(gameBrain.getMediumCOAgent());
		divineCandidates.removeAll(gameBrain.getBodyguardCOAgent());
		divineCandidates.removeAll(mostVotedAgents);
		if(divineCandidates.size()>0) return gameBrain.randomSelect(divineCandidates);
		
		
		//もしそれがいなければ自分と自分が占った者と投票されそうな人を除くランダム
		divineCandidates=getAliveAgentExceptDivined();
		divineCandidates.removeAll(mostVotedAgents);
		if(divineCandidates.size()>0) return gameBrain.randomSelect(divineCandidates);
		
		
		//何もいなければ自分を除くランダム
		return gameBrain.randomSelect(gameBrain.getAliveAgentsExceptMe());
		
	}

	@Override
	public void finish() {
		// TODO 自動生成されたメソッド・スタブ

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
	public void dayStart(){
		super.dayStart();
		gameBrain.dayStart();
	}
	@Override
	public void update(GameInfo gameInfo){
		super.update(gameInfo);
		gameBrain.update(gameInfo);
	}
	/** 自分と自分が占ったものは除いて、生きてる人物を返す 。listは生成*/
	private List<Agent> getAliveAgentExceptDivined(){
		List<Agent> divineCandidates=new ArrayList<Agent>();
		divineCandidates.addAll(gameBrain.getAliveAgentsExceptMe());
		for(Judge judge:getMyJudgeList()){
			if(divineCandidates.contains(judge.getTarget())){
				divineCandidates.remove(judge.getTarget());
			}
		}
		return divineCandidates;
	}
}
