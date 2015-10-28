package com.github.haretaro.pingwo.role;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.aiwolf.client.lib.TemplateTalkFactory;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Talk;
import org.aiwolf.common.net.GameInfo;
import org.aiwolf.common.net.GameSetting;

import com.github.haretaro.pingwo.brain.LongTermWerewolfMemory;
import com.github.haretaro.pingwo.brain.ShortTermWerewolfMemory;
import com.github.haretaro.pingwo.brain.util.MyCollectors;


public class PingwoWerewolf extends PingwoBasePlayer {
	
	private int readWhisperNumber = 0;
	
	public PingwoWerewolf(){
		try {
			shortTermMemoryConstructor = ShortTermWerewolfMemory.class.getConstructor(GameInfo.class);
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void initialize(GameInfo gameInfo,GameSetting gameSetting){
		this.gameInfo = gameInfo;
		longTermMemory = new LongTermWerewolfMemory(gameInfo);
		me = gameInfo.getAgent();
	}

	@Override
	public Agent attack() {
		List<Agent> attackList = gameInfo.getAliveAgentList();
		if(attackList.size() > shortTermMemory.getPotentialCandidates().size()){
			attackList.removeAll(shortTermMemory.getPotentialCandidates());
		}
		attackList.removeAll(getWolfAgents());
		return longTermMemory.getMostReliableAgentOf(attackList);
	}
	
	@Override
	public void dayStart(){
		super.dayStart();
		shortTermMemory.setWerewolfAgents(getWolfAgents());
		readWhisperNumber = 0;
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
	protected void listen(){
		super.listen();
		List<Talk> whisperList = gameInfo.getWhisperList();
		whisperList.stream()
			.skip(readWhisperNumber)
			.forEach(w->{
				shortTermMemory.listenToWhisper(w);
			});
	}
	
	@Override
	protected void think(){
		if(decoy()){
			return;
		}
		this.agreeToMajority();
	}
	
	private boolean decoy(){
		if(gameInfo.getDay()%2==0
				&& talkCount == 0
				&& gameInfo.getAliveAgentList().size() > 6){
			Optional<Agent> target = longTermMemory.getDenyList()
					.stream()
					.filter(a -> a.equals(me) == false)
					.filter(a -> gameInfo.getAliveAgentList().contains(a))
					.collect(MyCollectors.collectRandomly);
			target.ifPresent(t -> talkQue.add(TemplateTalkFactory.vote(t)));
			if(target.isPresent()){
				return true;
			}
		}
		return false;
	}

	@Override
	public String whisper() {
		return Talk.OVER;
	}
	
	private List<Agent> getWolfAgents(){
		Map<Agent,Role> roleMap = gameInfo.getRoleMap();
		return roleMap.keySet()
				.stream()
				.filter(a->roleMap.get(a)==Role.WEREWOLF)
				.collect(Collectors.toList());
	}
	
	
}
