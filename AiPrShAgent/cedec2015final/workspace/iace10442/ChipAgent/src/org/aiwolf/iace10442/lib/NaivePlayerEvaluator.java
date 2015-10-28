package org.aiwolf.iace10442.lib;


import java.util.*;

import org.aiwolf.client.lib.Utterance;
//import org.aiwolf.common.*;
import org.aiwolf.common.data.*;
import org.aiwolf.common.net.*;

// 
// 素朴(純朴)に他プレイヤーの白（人間サイド）と黒（人狼サイド）度合いを計算する
// 
// Executeで人狼人数上下

public class NaivePlayerEvaluator{
	
	// 自分のID、役職
	private int player_ID;
	private Role player_role;
	
	// 自分が知っている情報量
	//private double info_volume;
	
	// 関係性のネットワーク
	private ArrayList<RelationEdge> edges = new ArrayList<RelationEdge>();
	private ArrayList<ContradictionDetector> contra_detectors = new ArrayList<ContradictionDetector>();
	
	
	// 各プレイヤーの白度合い
	// 各プレイヤーの黒度合い
	private ArrayList< Double > white_level = new ArrayList<Double>();
	private ArrayList< Double > black_level = new ArrayList<Double>();
	
	
	// エッジ数
	
	
	// メソッド
	// トーク内容による更新
	public void addTalk( Agent src, Utterance utterance )
	{
		int srcID = src.getAgentIdx();
		
		RelationEdge e = getEdge( src, utterance );
		if( edges.contains(e) ) {
			return ;
		}
		edges.add( e );
		
		Contradiction contra = contra_detectors.get(srcID).addTalk( src, utterance );
		detectTalkContradiction( src, contra );
	}
	
	// 噛まれによる更新
	public void addAttacked( Agent attacked ) {
		for( int i=0; i< contra_detectors.size(); i++ ) {
			Contradiction contra = contra_detectors.get(i).addAttacked(attacked);
			detectAttackedContradiction(i, contra);
		}
		RelationEdge e = new RelationEdge(
				edges.size(), 
				attacked.getAgentIdx(), 
				attacked.getAgentIdx(), 
				RelationType.ATTACKED );
		edges.add(e);
	}
	
	
	protected void detectTalkContradiction( Agent src, Contradiction contra ) {
		int targID = src.getAgentIdx();
		double prev_white = white_level.get(targID);
		double prev_black = black_level.get(targID);
		
		switch( contra ) {
		case GOOD :
			white_level.set(targID, prev_white +10.0 );
			break;
		case BAD :
			black_level.set(targID, prev_black +100.0 );
			break;
		case NEW :
			break;
		default :
			break;
		}
	}
	
	protected void detectAttackedContradiction( int targID, Contradiction contra ) {
		double prev_white = white_level.get(targID);
		double prev_black = black_level.get(targID);
		
		switch( contra ) {
		case GOOD :
			white_level.set(targID, prev_white +10.0 );
			break;
		case BAD :
			black_level.set(targID, prev_black +100.0 );
			break;
		case NEW :
			break;
		default :
			break;
		}
	}
	
	// 情報量の取得
	
	// 本仕様ではないけど 各プレイヤーの疑い度合いの取得
	public double getWB( Agent agent ) {
		return (white_level.get(agent.getAgentIdx()) - black_level.get(agent.getAgentIdx()));
	}
	// 本仕様ではない
	public void setBlack( Agent agent ) {
		black_level.set(agent.getAgentIdx(), 100.0);
	}
	// forceDivine
	
	
	
	
	protected void bonus( Agent targ, Utterance utterance ) {
	}
	
	// コンストラクタ
	public NaivePlayerEvaluator( 
			GameSetting gamesetting, int player_ID_ , Role player_role_ ) 
	{
		for( int i=0; i< gamesetting.getPlayerNum() +1; i++ ) {
			contra_detectors.add( new ContradictionDetector(gamesetting) );
			white_level.add(0.0);
			black_level.add(0.0);
		}
		this.player_ID = player_ID_;
		this.player_role = player_role_;
		
		for( int i=0; i< contra_detectors.size(); i++ ) {
			contra_detectors.get(i).initializeSetting( this.player_ID, this.player_role );
		}
	}
	
	
	protected RelationEdge getEdge( Agent src, Utterance utterance )
	{
		int srcID = src.getAgentIdx();
		int dstID = srcID;
		RelationType type = RelationType.ESTIMATE;
		
		switch( utterance.getTopic() ) {
		case ESTIMATE :
			type = RelationType.ESTIMATE; dstID = utterance.getTarget().getAgentIdx(); break;
		case COMINGOUT:
			type = RelationType.COMINGOUT; dstID = srcID; break;
		//RESULT系
		case DIVINED:
			type = RelationType.DIVINED; dstID = utterance.getTarget().getAgentIdx(); break;
		case INQUESTED:
			type = RelationType.INQUESTED; dstID = utterance.getTarget().getAgentIdx(); break;
		case GUARDED:
			break;

		//投票系
		case ATTACK:
		case VOTE:
			break;
		default:
			break;
		}

		return new RelationEdge( edges.size(), srcID, dstID, type ); 
	}
}
