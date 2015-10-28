package org.aiwolf.iace10442;

import java.util.*;
import org.aiwolf.common.data.Agent;
import org.aiwolf.client.lib.TemplateTalkFactory;
import org.aiwolf.client.lib.TemplateWhisperFactory;
import org.aiwolf.client.lib.Topic;
import org.aiwolf.client.lib.Utterance;
import org.aiwolf.common.data.*;
import org.aiwolf.common.net.*;


public class ChipWereWolfPlayer extends AbstractChipBasePlayer {
	
	private boolean is_whisp_comingout;
	private boolean is_comingout;
	private int whisper_count;
	private ArrayList<Agent> attack_list = new ArrayList<Agent>();
	private ArrayList<Agent> werewolf_list = new ArrayList<Agent>();
	
	@Override
	public void initialize(GameInfo gameinfo, GameSetting gamesetting) {
		super.initialize(gameinfo, gamesetting);
		is_whisp_comingout = false;
		is_comingout = false;
		whisper_count = 0;
		
		this.attack_list.clear();
		this.werewolf_list.add(getMe());
	}
	
	@Override
	public String whisper() {
		// TODO 自動生成されたメソッド・スタブ
		if( is_whisp_comingout == false ) {
			is_whisp_comingout = true;
			return TemplateWhisperFactory.comingout(getMe(), getMyRole());
		}
		return Talk.OVER;
	}
	
	@Override
	public String talk() {
		if( is_comingout == false ) {
			if( will_explanation == true ) {
				will_explanation = false;
				is_comingout = true;
				return TemplateTalkFactory.comingout(getMe(), Role.VILLAGER );
			}
		}
		return Talk.OVER;
	}
	
	@Override
	public void update(GameInfo gameinfo){
		super.update(gameinfo);
		
		List<Talk> whisp_list = gameinfo.getWhisperList();
		
		for(; whisper_count < whisp_list.size(); whisper_count++){
			Talk whisp = whisp_list.get(whisper_count);
			Utterance utterance = new Utterance(whisp.getContent());
			
			if( werewolf_list.contains(whisp.getAgent()) == false ) {
				werewolf_list.add(whisp.getAgent());
			}
			
			if( utterance.getTopic() == Topic.ATTACK ) {
				attack_list.add( utterance.getTarget() );
			}
		}
	}
	
	@Override
	public void dayStart() {
		super.dayStart();
		whisper_count = 0;
		attack_list.clear();
	}
	
	@Override
	public Agent vote() {
		
		double info_max = 0.0;
		List<Agent> vote_candidates = new ArrayList<Agent>();
		vote_candidates.addAll( getLatestDayGameInfo().getAliveAgentList() );
		vote_candidates.removeAll( werewolf_list );
		vote_list.removeAll( werewolf_list );
		
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
	public Agent attack() {
		// 基本は他の狼に合わせる
		for( Agent a : attack_list ) {
			if( a == null ) continue;
			return a;
		}
		
		double info_min = 1000.0;
		List<Agent> attack_candidates = new ArrayList<Agent>();
		attack_candidates.addAll( getLatestDayGameInfo().getAliveAgentList() );
		attack_candidates.removeAll( werewolf_list );
		
		for( Agent a : attack_candidates ) {
			if( a == null ) continue;
			double score = information.getSimExecute(a) - information.getVolume();
			info_min = Double.min(info_min, score );
		}
		
		// 一番最初の最小のやつ（テキトー）
		for( Agent a : attack_candidates ) {
			double score = information.getSimExecute(a) - information.getVolume();
			if( score == info_min ) return a;
		}
		
		return null;
	}
}
