package org.aiwolf.iace10442.lib;


import java.util.*;

import org.aiwolf.client.lib.*;
//import org.aiwolf.common.*;
import org.aiwolf.common.data.*;
import org.aiwolf.common.net.*;

//
// 思いっきりテキトーにメタを気取って情報量とやらを算出する
//

public class Information {
	
	private class PlayerData {
		// 各プレイヤーの信頼度
		public double reliability;
		// 各プレイヤーの発話数
		public int remark_num;
	 	// 各プレイヤーの自称役職
		public Role self_role;
		// 生きているか
		public boolean is_living;
		
		/*
		public PlayerData(double reliability, int remark_num, Role self_role) {
			super();
			this.reliability = reliability;
			this.remark_num = remark_num;
			this.self_role = self_role;
			this.is_living = true;
		}
		*/
		public PlayerData() {
			super();
			this.reliability = 0.0;
			this.remark_num = 0;
			this.self_role = null;
			this.is_living = true;
		}
	}
	
	// 各プレイヤーのデータ
	private ArrayList<PlayerData> players = new ArrayList<PlayerData>();
	
	// 信頼度計算の例外的補助器
	private ArrayList<ContradictionDetector> contra_detectors = new ArrayList<ContradictionDetector>();
	
	// 発言のログ
	private HashSet<RelationEdge> talk_log = new HashSet<RelationEdge>();
	
	
	
	// メソッド
	// トーク内容による更新
	public void addTalk( Agent src, Utterance utterance )
	{
		int srcID = src.getAgentIdx();
		
		{
			RelationEdge e = getEdge( src, utterance );
			if( talk_log.contains(e) ) {
				// 既出なら返る
				return ;
			}
			talk_log.add( e );
		}
			
		// 発話数の追加
		players.get(srcID).remark_num ++;
		
		// 特例の発話内容に関しての処理
		switch( utterance.getTopic() ) {
		case COMINGOUT :
			if( players.get(srcID).self_role == null ) {
				players.get(srcID).reliability += 5.0;
				players.get(srcID).self_role = utterance.getRole();
			}
			break;
		case DIVINED :
			if( utterance.getResult() == Species.WEREWOLF ) {
				players.get(utterance.getTarget().getAgentIdx()).reliability -= 5.0;
			}
			if( players.get(srcID).self_role == null ) {
				players.get(srcID).self_role = Role.SEER;
				players.get(srcID).reliability += 5.0;
			}
			break;
		case INQUESTED : // 価値なしとする
			if( players.get(srcID).self_role == null ) {
				players.get(srcID).self_role = Role.MEDIUM;
				players.get(srcID).reliability += 5.0;
			}
			break;
		case GUARDED : // 価値なしとする
			if( players.get(srcID).self_role == null ) {
				players.get(srcID).self_role = Role.BODYGUARD;
				players.get(srcID).reliability += 5.0;
			}
			break;
		default :
			break;
		}
		
		// 例外的な矛盾検出
		Contradiction contra = contra_detectors.get(srcID).addTalk( src, utterance );
		if( contra == Contradiction.BAD ) {
			players.get(srcID).reliability -= 10.0;
		}
	}
	
	// 噛まれによる更新
	public void addAttacked( Agent attacked ) {
		if( attacked == null ) return ;
		for( int i=0; i< contra_detectors.size(); i++ ) {
			Contradiction contra = contra_detectors.get(i).addAttacked(attacked);
			if( contra == Contradiction.BAD ) {
				players.get(i).reliability -= 10.0;
			}
		}
	}
	
	// 追放による更新
	public void addExecuted( Agent executed ) {
		if( executed == null ) return ;
		players.get(executed.getAgentIdx()).is_living = false;
	}
	
	// 占いによる強制更新
	public void addDivine( Agent divined, Species result ) {
		if( divined == null ) return ;
		for( int i=0; i< contra_detectors.size(); i++ ) {
			Contradiction contra = contra_detectors.get(i).addDivine(divined,result);
			if( (contra == Contradiction.BAD) && 
				(players.get(i).reliability > -10.0) ) 
			{
				players.get(i).reliability -= 10.0;
			}
		}
	}
	// 霊媒による強制更新
	public void addInquest( Agent inquested, Species result ) {
		if( inquested == null ) return ;
		for( int i=0; i< contra_detectors.size(); i++ ) {
			Contradiction contra = contra_detectors.get(i).addInquested(inquested,result);
			if( (contra == Contradiction.BAD) && 
				(players.get(i).reliability > -10.0) ) 
			{
				players.get(i).reliability -= 10.0;
			}
		}
	}
	
	
	
	// 現在の情報量を返す
	public double getVolume() {
		double sum = 0.0;
		for( int i=0; i< players.size(); i++ ) {
			if( players.get(i).is_living == false ) continue; 
			sum += players.get(i).reliability * (double)players.get(i).remark_num;
		}
		return sum;
	}
	
	// プレイヤーの追放に成功した場合の予想される次の状態の情報量を返す
	public double getSimExecute( Agent execute ) {
		double sum = 0.0;
		for( int i=0; i< players.size(); i++ ) {
			if( players.get(i).is_living == false ) continue;
			if( i == execute.getAgentIdx() ) continue; 
			
			int expect_remark_num = players.get(i).remark_num +1; // 標準で何かしら1は発言するとする
			if( players.get(i).self_role != null ) {
				if( players.get(i).self_role == Role.SEER ) expect_remark_num += 2; // 謎のバイアスをかける
				if( players.get(i).self_role == Role.MEDIUM ) expect_remark_num += 1; // 謎のバイアスをかける
			}
			sum += players.get(i).reliability * (double)expect_remark_num;
		}
		return sum;
	}
	
	// 占いをし、それを発言した場合の予想される次の状態の情報量を返す
	public double getSimDivine( Agent devine ) {
		// 保留
		return 0.0;
	}
	
	// 襲撃に成功した場合の予想される次の状態の情報量を返す
	public double getSimAttacked( Agent attack ) {
		double sum = 0.0;
		for( int i=0; i< players.size(); i++ ) {
			if( players.get(i).is_living == false ) continue;
			if( i == attack.getAgentIdx() ) continue; 
			
			int expect_remark_num = players.get(i).remark_num +1; // 標準で何かしら1は発言するとする
			double werewolf_reliability = Double.max(players.get(i).reliability, 0.0);
			sum += werewolf_reliability * (double)expect_remark_num;
		}
		return sum;
	}
	
	// コンストラクタ
	public Information( 
			GameSetting gamesetting , int player_ID, Role player_role ) 
	{
		for( int i=0; i< gamesetting.getPlayerNum() +1; i++ ) {
			players.add( new PlayerData() );
			contra_detectors.add( new ContradictionDetector(gamesetting) );
		}
		
		for( int i=0; i< contra_detectors.size(); i++ ) {
			contra_detectors.get(i).initializeSetting( player_ID, player_role );
		}
	}
	
	
	
	
	// エッジ変換
	protected RelationEdge getEdge( Agent src, Utterance utterance )
	{
		int srcID = src.getAgentIdx();
		int dstID = srcID;
		RelationType type = RelationType.DONTCARE;
		
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
			type = RelationType.VOTED; dstID = utterance.getTarget().getAgentIdx(); break;
		default :
			break;
		}
		
		return new RelationEdge( talk_log.size(), srcID, dstID, type ); 
	}
	
}
