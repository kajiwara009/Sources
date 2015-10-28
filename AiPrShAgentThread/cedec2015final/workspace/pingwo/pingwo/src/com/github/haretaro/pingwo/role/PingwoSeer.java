package com.github.haretaro.pingwo.role;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.aiwolf.client.lib.TemplateTalkFactory;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Judge;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Species;
import org.aiwolf.common.net.GameInfo;
import org.aiwolf.common.net.GameSetting;

import com.github.haretaro.pingwo.brain.util.Util;

public class PingwoSeer extends PingwoBasePlayer {
	
	private List<Agent> divinedAgents;
	private List<Agent> divinedWhite;
	private List<Agent> divinedBlack;
	
	public PingwoSeer(){
		super();
		divinedAgents = new ArrayList<Agent>();
		divinedWhite = new ArrayList<Agent>();
		divinedBlack = new ArrayList<Agent>();
	}
	
	@Override
	public void initialize(GameInfo gameInfo, GameSetting gameSetting){
		super.initialize(gameInfo, gameSetting);

		longTermMemory.setSuspicion(me, 0d);
		longTermMemory.finalizeSuspicion(me);
	}
	
	@Override
	public Agent divine() {
		List<Agent> agents = gameInfo.getAliveAgentList();
		if(agents.contains(me)){
			agents.remove(me);
		}
		agents.removeAll(divinedAgents);
		if (agents.size() > 0){
			return Util.randomSelect(agents);
		}else{
			return longTermMemory
					.getLeastReliableAgentOf(gameInfo.getAliveAgentList())
					.orElse(null);
		}
	}
	
	@Override
	protected void think(){
		
		if(gameInfo.getDay() == 0
				&& talkCount == 0){
			talkQue.add(TemplateTalkFactory.comingout(me, Role.SEER));
		}

		if(declareToVoteToExpectedTarget()){
			return;
		}

		agreeToMajority();
	}
	
	@Override
	public void dayStart(){
		super.dayStart();
		Optional<Judge> judge = Optional.ofNullable(gameInfo.getDivineResult());
		judge.ifPresent(j->{
			Util.printout("divine target " + j.getTarget());
			Util.printout("result " + j.getResult());
			divinedAgents.add(j.getTarget());
			
			Agent target = j.getTarget();
			Species result = j.getResult();
			if(result == Species.HUMAN){
				divinedWhite.add(target);
			}
			
			if(result == Species.WEREWOLF){
				divinedBlack.add(target);
			}

			talkQue.add(TemplateTalkFactory.divined(target,result));
		});
	}

	@Override
	public void finish() {
	}

	@Override
	public Agent attack() {
		return null;
	}

	@Override
	public Agent guard() {
		return null;
	}

	@Override
	public String whisper() {
		return null;
	}

}
