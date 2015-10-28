package com.gmail.yusatk.data;

import org.aiwolf.common.data.*;

import java.util.*;

public class RoleMap {
	Map<Agent, Role> roleMap = new HashMap<Agent, Role>();
	Map<Role, Integer> masterNumMap = null;
	public RoleMap(Map<Role, Integer> masterNumMap) {
		this.masterNumMap = masterNumMap;
	}
	
	private Agent getSingleAgent(Role role) {
		for(Map.Entry<Agent, Role> e : roleMap.entrySet()){
			if(e.getValue() == role) {
				return e.getKey();
			}
		}
		return null;
	}
	
	public Agent getSeer() {
		return getSingleAgent(Role.SEER);
	}
	
	public Agent getMedium() {
		return getSingleAgent(Role.MEDIUM);
	}
	
	public Agent getBodyguard() {
		return getSingleAgent(Role.BODYGUARD);
	}
	
	public Agent getPossessed() {
		return getSingleAgent(Role.POSSESSED);
	}
	
	public List<Agent> getWolves() {
		List<Agent> wolves = new ArrayList<Agent>();
		for(Map.Entry<Agent, Role> e : roleMap.entrySet()){
			if(e.getValue() == Role.WEREWOLF) {
				wolves.add(e.getKey());
			}
		}
		return wolves;
	}
	
	public void registerAgent(Agent agent, Role role) {
		// TODO: 内訳がおかしくなったら止める
		roleMap.put(agent, role);
	}
}
