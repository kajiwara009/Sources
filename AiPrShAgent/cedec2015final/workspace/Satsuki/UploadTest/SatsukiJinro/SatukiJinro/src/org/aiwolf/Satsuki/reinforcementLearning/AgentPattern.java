package org.aiwolf.Satsuki.reinforcementLearning;

import org.aiwolf.Satsuki.lib.Pattern;
import org.aiwolf.common.data.Agent;

public enum AgentPattern {
	NULL,
	
	SEER,
	MEDIUM,
	BODYGUARD,
	
	FAKE_SEER_BLACK,
	FAKE_SEER_WHITE,
	FAKE_SEER_GRAY,
	
	FAKE_MEDIUM_BLACK,
	FAKE_MEDIUM_WHITE,
	FAKE_MEDIUM_GRAY,
	
	FAKE_BODYGUARD_BLACK,
	FAKE_BODYGUARD_WHITE,
	FAKE_BODYGUARD_GRAY,
	
	JUDGED_BLACK,
	
	WHITE_AGENT,
	//襲撃されたエージェント
	ATTACKED_AGENT,
	//処刑されたエージェント
	EXECUTED_AGENT,
	
	ORDINARY_AGENT,
		
	END;

	public AgentPattern getAgentPattern(Pattern p, Agent a)
	{		
		if(a.equals(p.getSeerAgent()))
		{
			return SEER;
		}
		else if(a.equals(p.getMediumAgent())){
			return MEDIUM;
		}		
		return null;
	}
	
}
