package com.github.haretaro.pingwo.brain;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.net.GameInfo;

public class LongTermWerewolfMemory extends LongTermMemory {

	public LongTermWerewolfMemory(GameInfo gameInfo) {
		super(gameInfo);
		getWolfAgents().stream()
			.filter( a -> denyList.contains(a) == false)
			.forEach(a -> denyList.add(a));
	}
	
	private List<Agent> getWolfAgents(){
		Map<Agent,Role> roleMap = gameInfo.getRoleMap();
		return roleMap.keySet()
				.stream()
				.filter(a->roleMap.get(a)==Role.WEREWOLF)
				.collect(Collectors.toList());
	}
	
	@Override
	public void update(GameInfo gameInfo){
		this.gameInfo = gameInfo;
	}

}
