package com.github.haretaro.pingwo.brain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.aiwolf.common.data.Agent;

import com.github.haretaro.pingwo.brain.util.Calc;

public class LongTermPlayerInfo {
	private final double voteScore = 1d;//投票による関係性評価の変化値
	private final double divineScore = 1d;//占いによる関係性評価の変化値
	private double suspicion = 4d/15;//人狼、狂人の数 / 人数 = 4/15 = 0.2666...
	private Agent agent;//このクラスが扱うエージェント
	//他のエージェントとの結びつきの強さ
	private Map<Agent,Double> connectionScore;
	private State state = State.ALIVE;
	private List<Agent> divinedHuman;
	private List<Agent> divinedWolf;
	private Agent latestVote;
	private double badFriendScore=0;
	private boolean isSuspicionFinalized = false;//trueなら疑惑度の変更を禁じる
	private int talkCount = 0;
	
	enum State {
		ALIVE,
		EXECUTED,
		ATTACKED
	}
	
	public LongTermPlayerInfo(Agent agent, List<Agent> agents){
		this.agent = agent;
		connectionScore = new HashMap<Agent,Double>();
		agents.stream().forEach(a->connectionScore.put(a, 0.0));
	}
	
	/** @return 信頼度.0から1*/
	public double getReliability(){
		return (1-suspicion)*Calc.sigmoid(3d-badFriendScore)*Calc.sigmoid(1d + talkCount*0.1);
	}
	
	public void votedTo(Agent target){
		addConnectionScore(target,-voteScore);
		latestVote = target;
	}
	
	public boolean isAlive(){
		return state == State.ALIVE;
	}

	public void executed() {
		state = State.EXECUTED;
	}
	
	public void attacked(){
		state = State.ATTACKED;
		suspicion = 0d;
		finalizeSuspicion();
	}
	
	public State getState(){
		return state;
	}
	
	public void setSuspicion(double suspicion){
		if(isSuspicionFinalized == false){
			this.suspicion = suspicion;
		}
	}
	
	public void addConnectionScore(Agent target, double score){
		connectionScore.put(target,
				connectionScore.get(target)+score);
	}
	
	public void addConnectionScores(List<Agent> agents, double score){
		agents.stream()
			.forEach(a->addConnectionScore(a,score));
	}
	
	public void COSeer(){
		divinedHuman = new ArrayList<Agent>();
		divinedWolf = new ArrayList<Agent>();
	}
	
	public Agent getAgent(){
		return agent;
	}
	
	public void divinedHuman(Agent agent){
		divinedHuman.add(agent);
		addConnectionScore(agent,divineScore*0.1);
	}
	
	public void divinedWolf(Agent agent){
		divinedWolf.add(agent);
		addConnectionScore(agent,-divineScore);//黒だししてたら多分仲間じゃない
	}
	
	/**
	 * @param target
	 * @return このエージェントがtargetを人だと占ったかどうか
	 */
	public boolean doesDivinedHuman(Agent target){
		return divinedHuman.contains(target);
	}
	
	/**
	 * 
	 * @param target
	 * @return このエージェントがtargetを狼だと占ったかどうか
	 */
	public boolean doesDivinedWolf(Agent target){
		return divinedWolf.contains(target);
	}
	
	public Agent getLatestVote(){
		return latestVote;
	}
	
	/**
	 * このエージェントからtargetへの関係
	 * @param target
	 * @return 0（否定的） から 1.0（肯定的）
	 */
	public double connectionTo(Agent target){
		double score = Calc.sigmoid(connectionScore.get(target));
		return score;
	}
	
	public List<Agent> getFriends(){
		return connectionScore.keySet()
			.stream()
			.filter(a->a.equals(getAgent())==false)
			.filter(a->connectionTo(a) > 0.65)
			.collect(Collectors.toList());
	}
	
	public void addBadFriendScore(double score){
		badFriendScore += score;
	}

	public void resetBadFriendScore() {
		badFriendScore = 0d;
	}

	public void finalizeSuspicion() {
		isSuspicionFinalized  = true;
	}
	
	public void incrementTalkCount(){
		talkCount ++;
	}
}
