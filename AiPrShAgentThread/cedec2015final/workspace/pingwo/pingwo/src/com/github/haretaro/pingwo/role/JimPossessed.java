package com.github.haretaro.pingwo.role;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.aiwolf.client.base.player.AbstractPossessed;
import org.aiwolf.client.lib.TemplateTalkFactory;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Species;
import org.aiwolf.common.data.Talk;
import org.aiwolf.common.net.GameInfo;

public class JimPossessed extends AbstractPossessed {
	private boolean isComingOut = false;
	private boolean divinedThisTurn = false;
	private List<Agent> divinedList;

	@Override
	public void finish() {
	}
	
	public JimPossessed(){
		divinedList = new ArrayList<Agent>();
	}
	
	@Override
	public void update(GameInfo gameInfo){
		super.update(gameInfo);
	}
	
	@Override
	public void dayStart(){
		divinedThisTurn = false;
	}

	@Override
	public String talk() {
		if(isComingOut){
			if(divinedThisTurn == false){
				divinedThisTurn = true;
				List<Agent> candidates = getLatestDayGameInfo().getAliveAgentList();
				for(Agent a : divinedList){
					candidates.remove(a);
				}
				if(candidates.size() > 0){
					Agent target = randomSelect(candidates);
					String resultTalk = TemplateTalkFactory.divined(target,Species.HUMAN);
					divinedList.add(target);
					return resultTalk;
				}
			}
		}else if(getDay() > 0){
			String comingoutTalk = TemplateTalkFactory.comingout(getMe(),Role.SEER);
			isComingOut = true;
			return comingoutTalk;
		}

		return Talk.OVER;
	}

	@Override
	public Agent vote() {
		List<Agent> candidates = getLatestDayGameInfo().getAliveAgentList();
		return randomSelect(candidates);
	}
	
	private Agent randomSelect(List<Agent> agentList){
		int num = new Random().nextInt(agentList.size());
		return agentList.get(num);
	}

}
