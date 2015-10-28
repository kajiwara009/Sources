package com.gmail.tydmskz;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aiwolf.client.lib.Topic;
import org.aiwolf.client.lib.Utterance;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Talk;
import org.aiwolf.common.net.GameInfo;

public class VillageInformation {

	int readTalk = 0;
	final int nPlayer = 15;
	final int ARRAY_SIZE = nPlayer + 1;//1スタートなので

	Agent[] vote = new Agent[ARRAY_SIZE];
	List<Agent> overed = new ArrayList<Agent>();

	public void Update(GameInfo gi)
	{
		//CO状況更新
		List<Talk> talks = gi.getTalkList();
		List<Agent> agents = gi.getAgentList();


		for(;readTalk<talks.size();readTalk++)
		{
			Talk t = talks.get(readTalk);
			Utterance u = new Utterance(t.getContent());
			Topic topic = u.getTopic();

			Agent a = t.getAgent();//誰は
			Agent b = u.getTarget();//誰に
			Agent x = Agent.getAgent(a.getAgentIdx());
			int aid = a.getAgentIdx();
			int bid = -1;

			if(b!=null)
			{
				bid = b.getAgentIdx();
			}

			if(topic==Topic.VOTE)
			{
				vote[aid] = b;
			}
			if(topic==Topic.OVER)
			{
				overed.add(a);
			}

		}
	}

	public void DayStart()
	{
		readTalk = 0;

		overed.clear();
	}


	final public List<Agent> OveredAgents()
	{
		return overed;
	}

	final public Map<Agent, Agent> VoteTarget()
	{
		Map<Agent, Agent> ret = new HashMap<Agent, Agent>();

		for(int i=1;i<ARRAY_SIZE;i++)
		{
			ret.put(Agent.getAgent(i), vote[i]);
		}

		return ret;
	}
}
