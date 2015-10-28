package com.gmail.tydmskz;

import java.util.List;

import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Species;

//
public class TalkParser {

	enum TalkType { AGREE, COMINGOUT, DISAGREE, DIVINED, ESTIMATE, GURARDED, INQUESTED, OVER, SKIP, VOTE  };
	enum TalkArea { TALK, WHISPER };

	static final int AGREE_HASH = ("AGREE").hashCode();
	static final int COMINGOUT_HASH = ("COMINGOUT").hashCode();
	static final int DISAGREE_HASH = ("DISAGREE").hashCode();
	static final int DIVINED_HASH = ("DIVINED").hashCode();
	static final int ESTIMATE_HASH = ("ESTIMATE").hashCode();
	static final int GUARDED_HASH = ("GUARDED").hashCode();
	static final int INQUESTED_HASH = ("INQUESTED").hashCode();
	static final int OVER_HASH = ("Over").hashCode();
	static final int SKIP_HASH = ("Skip").hashCode();
	static final int VOTE_HASH = ("VOTE").hashCode();
	
	
	static public TalkType GetTalkType(String t)
	{
		
		String[] sp = t.split(" ");
		String talkType = sp[0];

		final int H = talkType.hashCode(); 
		
		if(H==AGREE_HASH)
		{
			return TalkType.AGREE;
		}
		else if(H==COMINGOUT_HASH)
		{
			return TalkType.COMINGOUT;
		}
		else if(H==DISAGREE_HASH)
		{
			return TalkType.DISAGREE;
		}
		else if(H==DIVINED_HASH)
		{
			return TalkType.DIVINED;
		}
		else if(H==ESTIMATE_HASH)
		{
			return TalkType.ESTIMATE;
		}
		else if(H==GUARDED_HASH)
		{
			return TalkType.GURARDED;
		}
		else if(H==INQUESTED_HASH)
		{
			return TalkType.INQUESTED;
		}
		else if(H==OVER_HASH)
		{
			return TalkType.OVER;
		}
		else if(H==SKIP_HASH)
		{
			return TalkType.SKIP;
		}
		else if(H==VOTE_HASH)
		{
			return TalkType.VOTE;
		}
		
		return null;//来ないはず
	}

	static public Agent GetTalkTarget(String t, List<Agent> agents)
	{
		String[] sp = t.split(" ");
		String id = sp[1].substring(6, 8);
		
		int x = Integer.parseInt(id);
		Agent ret =  agents.get(x-1);
		
		return ret;
	}

	static final int BODYGUARD_HASH = ("BODYGUARD").hashCode();
	static final int MEDIUM_HASH = ("MEDIUM").hashCode();
	static final int POSSESSED_HASH = ("POSSESSED").hashCode();
	static final int SEER_HASH = ("SEER").hashCode();
	static final int VILLAGER_HASH = ("VILLAGER").hashCode();
	static final int WEREWOLF_HASH = ("WEREWOLF").hashCode();
	
	static public Role GetTalkComingoutRole(String t)
	{
		String[] sp = t.split(" ");
		String r = sp[2];

		final int H = r.hashCode();
		
		if(H == BODYGUARD_HASH)
		{
			return Role.BODYGUARD;
		}
		else if(H==MEDIUM_HASH)
		{
			return Role.MEDIUM;
		}
		else if(H==POSSESSED_HASH)
		{
			return Role.POSSESSED;
		}
		else if(H==SEER_HASH)
		{
			return Role.SEER;
		}
		else if(H==WEREWOLF_HASH)
		{
			return Role.WEREWOLF;
		}
		else if(H==VILLAGER_HASH)
		{
			return Role.VILLAGER;
		}



		return null;
	}

	//agree disagreeで使う
	static public TalkArea GetTalkArea(String t)
	{
		t = t.toLowerCase();
		String[] sp = t.split(" ");
		String r = sp[1];
		
		if(r.equals("talk"))
		{
			return TalkArea.TALK;
		}
		else if(r.equals("whisper"))
		{
			return TalkArea.WHISPER;
		}
		
		return null;
	}
	static public int GetTalkDay(String t)
	{
		t = t.toLowerCase();
		String[] sp = t.split(" ");
		String r = sp[2];
		r = r.substring(3, r.length());
		
		return Integer.valueOf(r);
		
	}

	static public int GetTalkID(String t)
	{
		t = t.toLowerCase();
		String[] sp = t.split(" ");
		String r = sp[3];
		r = r.substring(3, r.length());
		
		return Integer.valueOf(r);
	}
	
	static final int HUMAN_HASH = ("HUMAN").hashCode();
	//static final int WEREWOLF_HASH = ("WEREWOLF").hashCode();
	static public Species GetTalkSpecies(String t)
	{
		String[] sp = t.split(" ");
		String r = sp[2];
		int H = r.hashCode();
		
		assert H!=0;
		if(H==HUMAN_HASH)
		{
			return Species.HUMAN;
		}
		else if(H==WEREWOLF_HASH)
		{
			return Species.WEREWOLF;
		}

		return null;
	}
}
