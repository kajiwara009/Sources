package org.aiwolf.iace10442;

import java.util.ArrayList;
import java.util.List;

import org.aiwolf.client.lib.TemplateTalkFactory;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Talk;

public class ChipBodyGuardPlayer extends AbstractChipBasePlayer {
	
	@Override
	public String talk() {
		if( will_explanation == true ) {
			will_explanation = false;
			return TemplateTalkFactory.comingout(getMe(), getMyRole() );
		}
		return Talk.OVER;
	}
	
	@Override
	public Agent guard() {
		double info_min = 1000.0;
		List<Agent> vote_candidates = new ArrayList<Agent>();
		vote_candidates.addAll( getLatestDayGameInfo().getAliveAgentList() );
		
		for( Agent v : vote_candidates ) {
			double score = information.getSimExecute(v) - information.getVolume();
			info_min = Double.min(info_min, score );
		}
		
		// とりあえず最小値の一番最初のやつ（テキトー）
		for( Agent v : vote_candidates ) {
			double score = information.getSimExecute(v) - information.getVolume();
			if( score == info_min ) return v;
		}
		
		return null;
	}
}
