package com.github.haretaro.pingwo.brain;

import java.util.List;

import org.aiwolf.client.lib.Topic;
import org.aiwolf.client.lib.Utterance;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Talk;
import org.aiwolf.common.net.GameInfo;

import com.github.haretaro.pingwo.brain.util.Util;

public class ShortTermWerewolfMemory extends ShortTermMemory {
	

	public ShortTermWerewolfMemory(GameInfo gameInfo) {
		super(gameInfo);
	}
	
	public void setWerewolfAgents(List<Agent> wolfs){
		wolfs.stream()
			.forEach(a->playerInfo.get(a).setWerewolf());
	}
	
	@Override
	public void listenToWhisper(Talk w){
		//Agent speaker = w.getAgent();
		Utterance utterance = new Utterance(w.getContent());
		
		if(utterance.getTopic() == Topic.VOTE){
			//votelist.add(utterance.getTarget());
			Util.printout("whisperd target = "+utterance.getTarget());
		}
	}
	
	@Override
	protected ShortTermPlayerInfo instantiatePlayerInfo(Agent agent){
		return new ShortTermWerewolfPlayerInfo(agent);
	}

}
