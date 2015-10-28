package com.github.haretaro.pingwo.role;

import java.util.List;
import java.util.Optional;

import org.aiwolf.common.data.Agent;


public class PingwoGuard extends PingwoBasePlayer {
	@Override
	public Agent guard(){
		
		//信頼度の高い占い師を守る
		Optional<Agent> seer = longTermMemory.getMostReliableSeer();
		if (seer.isPresent()
				&& longTermMemory.getReliabilityOf(seer.get()) > 0.4
				&& longTermMemory.getExpectedTargets().contains(seer.get())==false){
			return seer.get();
		}
		
		//霊媒師COが一人なら守る
		List<Agent> mediums = longTermMemory.getAliveMediums();
		if(mediums.size() == 1 
				&& longTermMemory.isReliable(mediums.get(0))
				&& longTermMemory.getExpectedTargets().contains(mediums.get(0)) == false){
			return mediums.get(0);
		}
		
		//一番信頼度の高いプレイヤー
		List<Agent> list = gameInfo.getAliveAgentList();
		return longTermMemory.getMostReliableAgentOf(list);
		
	}

	@Override
	public Agent attack() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Agent divine() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String whisper() {
		// TODO Auto-generated method stub
		return null;
	}
}
