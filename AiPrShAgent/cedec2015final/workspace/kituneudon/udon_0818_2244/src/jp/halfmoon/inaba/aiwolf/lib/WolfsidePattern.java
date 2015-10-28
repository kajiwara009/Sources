package jp.halfmoon.inaba.aiwolf.lib;

import java.util.ArrayList;


/**
 * 狼陣営を表現するクラス
 */
public final class WolfsidePattern{

	/** 人狼のエージェント番号(昇順で設定すること) */
	public final ArrayList<Integer> wolfAgentNo;

	/** 狂人のエージェント番号(昇順で設定すること) */
	public final ArrayList<Integer> possessedAgentNo;

	/** 狼陣営の名称 */
	public String wolfSideName;


	/**
	 * コンストラクタ
	 * @param wolfAgentNo 人狼のエージェント番号(昇順で設定すること)
	 * @param possessedAgentNo 狂人のエージェント番号(昇順で設定すること)
	 */
	public WolfsidePattern(ArrayList<Integer> wolfAgentNo, ArrayList<Integer> possessedAgentNo){
		this.wolfAgentNo = wolfAgentNo;
		this.possessedAgentNo = possessedAgentNo;

		// 狼陣営の名称の設定
		setWolfSideName();
	}


	/**
	 * 特定のエージェントが狼か
	 * @param agentno
	 * @return
	 */
	public boolean isWolf(int agentno){

		// いずれかの狼番号と一致すれば狼
		if( wolfAgentNo.contains(agentno) ){
			return true;
		}

		return false;

	}


	/**
	 * 特定のエージェントが狂人か
	 * @param agentno
	 * @return
	 */
	public boolean isPossessed(int agentno){

		// いずれかの狂人番号と一致すれば狼
		if( possessedAgentNo.contains(agentno) ){
			return true;
		}

		return false;

	}


	/**
	 * 特定のエージェントが狼陣営か
	 * @param agentno
	 * @return
	 */
	public boolean isWolfSide(int agentno){

		// いずれかの狼番号と一致すれば狼陣営
		if( wolfAgentNo.contains(agentno) ){
			return true;
		}
		// いずれかの狂人番号と一致すれば狼陣営
		if( possessedAgentNo.contains(agentno) ){
			return true;
		}

		return false;

	}

	/**
	 * 狼陣営の名称の設定
	 */
	private void setWolfSideName(){

		StringBuilder sb = new StringBuilder();

		// 狼を一覧表示　例）狼[1,2,3]
		sb.append("狼[");
		for( int wolf : wolfAgentNo ){
			sb.append(wolf).append(",");
		}
		if( !wolfAgentNo.isEmpty() ){
			sb.deleteCharAt(sb.length()-1);
		}
		sb.append("]");

		// 狂人を一覧表示　例）狂人[1,2,3]
		sb.append(" 狂人[");
		for( int pos : possessedAgentNo ){
			sb.append(pos).append(",");
		}
		if( !possessedAgentNo.isEmpty() ){
			sb.deleteCharAt(sb.length()-1);
		}
		sb.append("]");

		wolfSideName = sb.toString();

	}

	/**
	 * 文字列化
	 * @return 文字列化した狼陣営
	 */
	public String toString(){
		return wolfSideName;
	}

}
