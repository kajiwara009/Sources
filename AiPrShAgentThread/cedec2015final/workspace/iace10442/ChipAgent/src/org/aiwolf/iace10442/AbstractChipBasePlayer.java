package org.aiwolf.iace10442;

import java.util.*;
import org.aiwolf.client.base.player.AbstractRole;
import org.aiwolf.client.lib.*;
import org.aiwolf.common.data.*;
import org.aiwolf.common.net.*;
import org.aiwolf.iace10442.lib.Information;



public abstract class AbstractChipBasePlayer extends AbstractRole {

	//
	protected Information information;
	
	//トークをどこまで読んだか
	protected int read_talk_number = 0;
	
	// その日の投票意思リスト
	protected ArrayList<Agent> vote_list = new ArrayList<Agent>();
	
	// 弁明フラグ
	protected boolean will_explanation;

	@Override
	public void initialize(GameInfo gameinfo, GameSetting gamesetting) {
		super.initialize(gameinfo, gamesetting);
		
		Role role = gameinfo.getRole();
		if( role == Role.WEREWOLF ) role = Role.VILLAGER;
		if( role == Role.POSSESSED ) role = Role.SEER;
		
		information = new Information(
				gamesetting,
				gameinfo.getAgent().getAgentIdx(),
				role );
		
		read_talk_number = 0;
		will_explanation = false;
		
		this.vote_list.clear();
		
	}

	@Override
	public void update(GameInfo gameinfo){
		super.update(gameinfo);
		
		List<Talk> talkList = gameinfo.getTalkList();
		
		for(; read_talk_number < talkList.size(); read_talk_number++){
			Talk talk = talkList.get(read_talk_number);
			Utterance utterance = new Utterance(talk.getContent());
			information.addTalk(talk.getAgent(), utterance);
			
			if( utterance.getTopic() == Topic.VOTE ) {
				vote_list.add( utterance.getTarget() );
			}
			
			if( utterance.getTopic() == Topic.DIVINED ) {
				if( (utterance.getTarget() == getMe()) && 
					(utterance.getResult() == Species.WEREWOLF ) ) 
				{
					will_explanation = true;
				}
			}
		}
	}

	@Override
	public void dayStart() {
		Agent attacked_agent = getLatestDayGameInfo().getAttackedAgent();
		information.addAttacked( attacked_agent );

		Agent executed_agent = getLatestDayGameInfo().getExecutedAgent();
		information.addExecuted( executed_agent );
		
		vote_list.clear();
		
		read_talk_number = 0;
	}
	
	
	@Override
	public Agent vote() {
		
		double info_max = 0.0;
		List<Agent> vote_candidates = new ArrayList<Agent>();
		vote_candidates.addAll( getLatestDayGameInfo().getAliveAgentList() );
		vote_candidates.remove( getMe() );
		vote_list.remove(getMe());
		
		for( Agent v : vote_candidates ) {
			if( v== null ) continue;
			double score = information.getSimExecute(v) - information.getVolume();
			info_max = Double.max(info_max, score );
		}
		
		// vote_list内にあればそれにする
		for( Agent v : vote_list ) {
			if( v == null ) continue;
			double score = information.getSimExecute(v) - information.getVolume();
			if( score == info_max ) return v;
		}
		
		// 残っていたら一番最初のやつ（テキトー）
		for( Agent v : vote_candidates ) {
			double score = information.getSimExecute(v) - information.getVolume();
			if( score == info_max ) return v;
		}
		
		//throw new IllegalStateException("f*ck1");
		return null;
	}
	
	
	@Override
	public String talk() {
		return Talk.OVER;
	}
	
	
	
	@Override
	public Agent attack() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	public Agent divine() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	public void finish() {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public Agent guard() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	

	

	@Override
	public String whisper() {
		return Talk.OVER;
	}

}
