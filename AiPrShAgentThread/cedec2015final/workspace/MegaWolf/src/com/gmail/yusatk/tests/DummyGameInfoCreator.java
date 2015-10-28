package com.gmail.yusatk.tests;
import org.aiwolf.client.lib.TemplateTalkFactory;
import org.aiwolf.common.data.*;

import com.gmail.yusatk.interfaces.IGameInfo;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DummyGameInfoCreator {

	List<Agent> agentList = null;
	Map<Agent, Role> roleMap = new HashMap<Agent, Role>();
	int day;
	
	Map<Agent, List<Judge>> divines = new HashMap<Agent, List<Judge>>();
	
	public void setAgents(String agentDesc) {
		String [] roles = agentDesc.split(",");
		
		HashMap<String, Role> roleTable = new HashMap<String, Role>();
		roleTable.put("v", Role.VILLAGER);
		roleTable.put("b", Role.BODYGUARD);
		roleTable.put("s", Role.SEER);
		roleTable.put("m", Role.MEDIUM);
		roleTable.put("p", Role.POSSESSED);
		roleTable.put("w", Role.WEREWOLF);
		
		agentList = new ArrayList<Agent>();
		for(int i = 0; i < roles.length; ++i) {
			String roleString = roles[i];
			Role role = roleTable.get(roleString.trim().toLowerCase());
			Agent agent = Agent.getAgent(i + 1);
			agentList.add(agent);
			roleMap.put(agent, role);
		}
	}
	
	
	public void setDivine(String divineDesc) {
		if(divineDesc == null || divineDesc == ""){
			return;
		}
		
		String [] divineDescs = divineDesc.split("/");
		for(String desc : divineDescs) {
			desc = desc.trim();
			int index = Integer.parseInt(desc.split(":")[0]);
			String divineString = desc.split(":")[1];
			Agent seer = Agent.getAgent(index);
			
			String [] divines = divineString.split(",");
			List<Judge> divineList = new ArrayList<Judge>();
			Pattern p = Pattern.compile("([\\d]+)([ox])");
			int day = 1;
			for(String divine : divines) {
				Matcher m = p.matcher(divine.trim());
				m.find();
				int target = Integer.parseInt(m.group(1));
				Species result = m.group(2).trim() == "o" ? Species.HUMAN : Species.WEREWOLF;
				divineList.add(new Judge(day, seer, Agent.getAgent(target), result));
				day++;
			}
			this.divines.put(seer, divineList);
		}
	}
	
	List<Talk> createCoTalkList(int firstTalkIndex) {
		List<Talk> talkList = new ArrayList<Talk>();
		int talkIndex = firstTalkIndex;
		for(Agent agent : agentList){
			Role role = roleMap.get(agent);
			Talk talk = new Talk(talkIndex++, day, agent, TemplateTalkFactory.comingout(agent, role));
			talkList.add(talk);
		}
		return talkList;
	}
	
	List<Talk> createDivineTalkList(int firstTalkIndex) {
		List<Talk> talkList = new ArrayList<Talk>();
		int talkIndex = firstTalkIndex;
		
		for(Map.Entry<Agent, List<Judge>> e : divines.entrySet()){
			for(Judge judge : e.getValue()) {
				Talk talk = new Talk(talkIndex++, day, e.getKey(), TemplateTalkFactory.divined(judge.getTarget(), judge.getResult()));
				talkList.add(talk);
			}
		}
		return talkList;
	}
	
	public IGameInfo getGameInfo() {
		DummyGameInfo gameInfo = new DummyGameInfo();
		
		gameInfo.setAgentList(agentList);
		gameInfo.setAliveAgentList(agentList);

		List<Talk> talkList = createCoTalkList(0);
		talkList.addAll(createDivineTalkList(talkList.size()));

		gameInfo.setTalkList(talkList);
		
		return gameInfo;
	}
	
}
