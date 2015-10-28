package com.github.haretaro.pingwo.brain;

import org.aiwolf.common.data.Agent;

/**
 * プレイヤー毎の短期的な情報
 * 主にそのプレイヤーがどこに投票するつもりなのかを覚える
 * @author SEN
 *
 */
public class ShortTermPlayerInfo {
	private Agent target;//このプレイヤーの投票先
	private int voteCount=0;//このプレイヤーの得票数
	private boolean isOver = false;
	private boolean isAlive = false;
	private Agent agent;
	
	public ShortTermPlayerInfo(Agent agent){
		this.agent = agent;
	}
	
	public void incrementVoteCount(){
		voteCount++;
	}
	
	public void resetVoteCount(){
		voteCount = 0;
	}
	
	public Agent getTarget(){
		return target;
	}
	
	public void setTarget(Agent target){
		this.target = target;
	}
	
	public int getVoteCount(){
		return voteCount;
	}
	
	public boolean isOver(){
		return isOver;
	}
	
	public void setOver(){
		isOver = true;
	}
	
	public boolean isAlive(){
		return isAlive;
	}

	public void setAive() {
		isAlive  = true;
	}

	public Agent getAgent() {
		return agent;
	}

	public void setWerewolf() {
		assert(false);
	}
	
}
