package com.github.haretaro.pingwo.role;

import java.util.ArrayDeque;
import java.util.Optional;

import org.aiwolf.client.lib.TemplateTalkFactory;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Judge;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Species;
import org.aiwolf.common.net.GameInfo;
import org.aiwolf.common.net.GameSetting;

public class PingwoMedium extends PingwoBasePlayer{
	
	private boolean isComingout = false;
	private ArrayDeque<String> inquestQue;
	
	public PingwoMedium(){
		super();
		inquestQue = new ArrayDeque<String>();
	}
	
	@Override
	public void initialize(GameInfo gameInfo, GameSetting gameSetting){
		super.initialize(gameInfo, gameSetting);

		longTermMemory.setSuspicion(me, 0d);
		longTermMemory.finalizeSuspicion(me);
	}
	
	@Override
	public void dayStart(){
		super.dayStart();

		Optional<Judge> judge =  Optional.ofNullable(gameInfo.getMediumResult());
		judge.ifPresent(j->{
			Agent target = j.getTarget();
			Species result = j.getResult();
			inquestQue.add(TemplateTalkFactory.inquested(target, result));
		});
		
	}
	
	@Override
	protected void think(){
		
		if(this.sayOverOnFirstDay()){
			return;
		}
		
		if(isComingout
				&& inquestQue.size() > 0
				&& talkCount < 12){
			talkQue.add(inquestQue.poll());
			return;
		}
		
		if(isComingout == false
				&& longTermMemory.getMediumCOAgents().size() > 0){
			talkQue.add(TemplateTalkFactory.comingout(me, Role.MEDIUM));
			isComingout = true;
			return;
		}
		
		if(isComingout == false
				&& gameInfo.getDay() > 0
				&& shortTermMemory.getPotentialCandidates().contains(me)
				&& shortTermMemory.getMaxVoteCount() > gameInfo.getAliveAgentList().size() / 2){
			talkQue.add(TemplateTalkFactory.comingout(me, Role.MEDIUM));
			isComingout = true;
			return;
		}
		
		if(isComingout == false
				&& gameInfo.getAliveAgentList().size() < 15){
			talkQue.add(TemplateTalkFactory.comingout(me, Role.MEDIUM));
			isComingout = true;
			return;
		}
			
		
		if(this.declareToVoteToExpectedTarget()==true){
			return;
		}
		
		if(this.persuade() == true){
			return;
		}
		
		this.agreeToMajority();
	}

	@Override
	public Agent attack() {
		return null;
	}

	@Override
	public Agent divine() {
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
