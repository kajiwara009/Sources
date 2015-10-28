package org.aiwolf.iace10442.lib;


import java.util.*;

import org.aiwolf.client.lib.Utterance;
//import org.aiwolf.common.*;
import org.aiwolf.common.data.*;
import org.aiwolf.common.net.*;

// 発話に基づく単純な矛盾検出器
// あるプレイヤーの発言を記録するだけ
// 競合の検知にも使用できる

public class ContradictionDetector {
	
	private ArrayList< Role > role = new ArrayList< Role >();
	private ArrayList< Species > species = new ArrayList< Species >();
	
	private HashMap<Role, Integer > num_role = new HashMap<Role,Integer>();
	private HashMap<Role, Integer > max_role = new HashMap<Role,Integer>();
	
	private HashMap<Species, Integer> num_species = new HashMap<Species, Integer>();
	private HashMap<Species, Integer> max_species = new HashMap<Species, Integer>();
	
	
	// トークのチェック
	public Contradiction checkTalk( Agent src, Utterance utterance ) {
		int dstID = 0;
		if( utterance.getTarget() != null ) dstID = utterance.getTarget().getAgentIdx();
		int srcID = src.getAgentIdx(); // 基本的にはインスタンス単位で不変
		
		Role topic_role = utterance.getRole();
		Species topic_species = utterance.getResult();
		
		switch( utterance.getTopic() ) {
		//case ESTIMATE : break;
		
		case COMINGOUT :
			if( role.get(srcID) == null ) { 
				if( num_role.get( topic_role ) +1 > max_role.get( topic_role ) ) return Contradiction.BAD;  
				return Contradiction.NEW; 
			}
			if( role.get(srcID) != utterance.getRole() ) { return Contradiction.BAD; }
			break;
		
		// case VOTED : break;
		case DIVINED :
			if( role.get(srcID) != Role.SEER ) { 
				if( num_role.get( Role.SEER ) +1 > max_role.get(Role.SEER) ) return Contradiction.BAD;
				return Contradiction.NEW; 
			}
			if( species.get(dstID) == null ) {
				if( num_species.get( topic_species ) +1 > max_species.get( topic_species ) ) return Contradiction.BAD;
				return Contradiction.NEW; 
			}
			if( species.get(dstID) != utterance.getResult() ) { return Contradiction.BAD; }
			break;
		
		case INQUESTED :
			if( role.get(srcID) != Role.MEDIUM ) { 
				if( num_role.get( Role.MEDIUM ) +1 > max_role.get(Role.MEDIUM) ) return Contradiction.BAD;
				return Contradiction.NEW; 
			}
			if( species.get(dstID) == null ) { 
				if( num_species.get( topic_species ) +1 > max_species.get( topic_species ) ) return Contradiction.BAD;
				return Contradiction.NEW; 
			}
			if( species.get(dstID) != utterance.getResult() ) { return Contradiction.BAD; }
			break;
			
		default :
			break;
		}
		
		return Contradiction.DONTCARE;
	}
	
	
	// トーク情報をチェックして上書き
	public Contradiction addTalk( Agent src, Utterance utterance ) {
		Contradiction result = checkTalk( src, utterance );
		
		int dstID = 0;
		if( utterance.getTarget() != null ) dstID = utterance.getTarget().getAgentIdx();
		int srcID = src.getAgentIdx(); // 基本的にはインスタンス単位で不変
		Role topic_role = utterance.getRole();
		Species topic_species = utterance.getResult();
		
		switch( utterance.getTopic() ) {
		//case ESTIMATE : break;
		
		case COMINGOUT :
			role.set( srcID,  topic_role );
			int prev_num = num_role.get( topic_role );
			if( result == Contradiction.NEW ) num_role.put( topic_role, prev_num+1 );
			break;
		// case VOTED : break;
		case DIVINED :
			if( role.get(srcID) == null ) {
				role.set(srcID, Role.SEER);
				prev_num = num_role.get(Role.SEER);
				if( result == Contradiction.NEW ) num_role.put( Role.SEER, prev_num+1 );
			}
			species.set( dstID, topic_species );
			prev_num = num_species.get(topic_species);
			if( result == Contradiction.NEW ) num_species.put( topic_species, prev_num+1 );
			break;
		
		case INQUESTED :
			if( role.get(srcID) == null ) {
				role.set(srcID, Role.MEDIUM);
				prev_num = num_role.get(Role.MEDIUM);
				if( result == Contradiction.NEW ) num_role.put( Role.MEDIUM, prev_num+1 );
			}
			species.set(dstID, topic_species );
			prev_num = num_species.get(topic_species);
			if( result == Contradiction.NEW ) num_species.put( topic_species, prev_num+1 );
			break;
			
		default :
			break;
		}
		
		return result;
	}
	
	// 占い結果の追加
	public Contradiction addDivine( Agent divined, Species divine_result ) {
		Contradiction result = Contradiction.DONTCARE;
		int divID = divined.getAgentIdx();
		
		if( species.get(divID) != null ) {
			if( species.get(divID) != divine_result ) {
				result = Contradiction.BAD;
			}
			if( species.get(divID) == divine_result ) {
				result = Contradiction.GOOD;
			}
		}
		else {
			result = Contradiction.NEW;
		}
		
		species.set(divID, divine_result);
		
		return result;
	}
	
	// 占い結果の追加
	public Contradiction addInquested( Agent inquested, Species inq_result ) {
		Contradiction result = Contradiction.DONTCARE;
		int inqID = inquested.getAgentIdx();
		
		if( species.get(inqID) != null ) {
			if( species.get(inqID) != inq_result ) {
				result = Contradiction.BAD;
			}
			if( species.get(inqID) == inq_result ) {
				result = Contradiction.GOOD;
			}
		}
		else {
			result = Contradiction.NEW;
		}
		
		species.set(inqID, inq_result);
		
		return result;
	}
	
	// 噛まれた情報をチェックして追加
	public Contradiction addAttacked( Agent attacked ) {
		Contradiction result = Contradiction.DONTCARE;
		int atkID = attacked.getAgentIdx();
		
		if( species.get(atkID) != null ) {
			if( species.get(atkID) != Species.HUMAN ) {
				result = Contradiction.BAD;
			}
			if( species.get(atkID) == Species.HUMAN ) {
				result = Contradiction.GOOD;
			}
		}
		else {
			result = Contradiction.NEW;
		}
		
		species.set(atkID, Species.HUMAN);
		
		return result;
	}
	
	
	
	//　単純なセット
	public void initializeSetting( int ID, Role role ) {
		this.role.set(ID, role);
		this.species.add( ID, role.getSpecies() );
		this.num_role.put( role, 1 );
		this.num_species.put( role.getSpecies(), 1);
	}
	
	
	// コンストラクタ
	public ContradictionDetector( GameSetting gamesetting )
	{
		for( int i=0; i< gamesetting.getPlayerNum()+1; i++ ) {
			this.role.add( null );
			this.species.add( null );
		}
		
		
		Role[] roles = Role.values();
		for(int i = 0; i < roles.length; i++){
			max_role.put(roles[i], gamesetting.getRoleNum(roles[i]));
			num_role.put(roles[i], 0);
		}
		
		max_species.put( Species.HUMAN , gamesetting.getPlayerNum() - gamesetting.getRoleNum(Role.WEREWOLF));
		max_species.put( Species.WEREWOLF , gamesetting.getRoleNum(Role.WEREWOLF));
		num_species.put( Species.HUMAN, 0);
		num_species.put( Species.WEREWOLF, 0);
	}	
}
