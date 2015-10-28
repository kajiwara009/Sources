package org.aiwolf.Satsuki.reinforcementLearning;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.aiwolf.common.data.Species;

public class Qvalues implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1098064560753220480L;

	/**
	 * 投票
	 * 投票に関しては，選んだ行動ではなく，実際に処刑されたエージェントの種類で学習を行う
	 * 人狼側はfakePatternsで村人側が勝つように装って学習
	 * 占い
	 * 護衛
	 * （各シーンに対して，各エージェントを対象にした際に，移行したシーンでの投票のマックスQ値を学習に用いる）
	 * 
	 * カミングアウト（初日に設定してしまう）
	 * 
	 * 
	 * 襲撃（WOLFSパターンで考える）
	 * 偽役職（初日に設定．人狼は学習しない．とりあえず勝率だけ保存しておく）
	 * 偽占い対象，結果（対象と結果を対にする．殺されたエージェントも対象に入れる．翌日ディスタートで）
	 * 偽霊能結果（翌日ディスタート）
	 * （狂人プレイヤー）
	 * 
	 * 村人：投票
	 * 占い師：カミングアウト，投票，占い
	 * 霊能：カミングアウト，投票
	 * 狩人：護衛，投票

	 * 狂人：偽役職，偽占い，偽霊能，カミングアウト（２種），投票
	 * 人狼：偽役職，偽占い，偽霊能，カミングアウト，投票，襲撃
	 * 
	 * 
	 */
	private Map<AgentPattern, Double> 
		villagerVote = getNewQvalueMap(),
		
		seerVote= getNewQvalueMap(),
		seerDivine= getNewQvalueMap(),
	
		mediumVote= getNewQvalueMap(),
	
		hunterGuard= getNewQvalueMap(),
		hunterVote= getNewQvalueMap(),
	
		possessedVote= getNewQvalueMap(),
	
		wolfAttack= getNewQvalueMap(),
		wolfVote= getNewQvalueMap();
	
	private Map<AgentPattern, Map<Species, Double>> 
		wolfDivine = getNewQvalueJudgeMap(),
		possessedDivine = getNewQvalueJudgeMap(),
		wolfInquest = getNewQvalueJudgeMap(),
		possessedInquest = getNewQvalueJudgeMap();
	
	private int Likelihood = 0;
	
	public class VillagerQvalue{
		
	}
	
	private Map<AgentPattern, Double> getNewQvalueMap()
	{
		Map<AgentPattern, Double> map = new HashMap<AgentPattern, Double>();
		for(AgentPattern ap: AgentPattern.values())
		{
			map.put(ap, 50.0);
		}
		return map;
	}
	
	private Map<AgentPattern, Map<Species, Double>> getNewQvalueJudgeMap()
	{
		Map<AgentPattern, Map<Species, Double>> map = new HashMap<AgentPattern, Map<Species,Double>>();
		for(AgentPattern ap: AgentPattern.values())
		{
			Map<Species, Double> inMap = new HashMap<Species, Double>();
			for(Species s: Species.values())
			{
				inMap.put(s, 50.0);
			}
			map.put(ap, inMap);
		}
		return map;
	}
	
	public static double getMaxQValue(Map<?, Double> map)
	{
		double ans = -Double.MAX_VALUE;
		for(Entry<?, Double> set: map.entrySet()){
			if(ans < set.getValue())
			{
				ans = set.getValue();
			}
		}
		return ans;
	}

	public Map<AgentPattern, Double> getVillagerVote() {
		return villagerVote;
	}

	public void setVillagerVote(Map<AgentPattern, Double> villagerVote) {
		this.villagerVote = villagerVote;
	}

	public Map<AgentPattern, Double> getSeerVote() {
		return seerVote;
	}

	public void setSeerVote(Map<AgentPattern, Double> seerVote) {
		this.seerVote = seerVote;
	}

	public Map<AgentPattern, Double> getSeerDivine() {
		return seerDivine;
	}

	public void setSeerDivine(Map<AgentPattern, Double> seerDivine) {
		this.seerDivine = seerDivine;
	}

	public Map<AgentPattern, Double> getMediumVote() {
		return mediumVote;
	}

	public void setMediumVote(Map<AgentPattern, Double> mediumVote) {
		this.mediumVote = mediumVote;
	}

	public Map<AgentPattern, Double> getHunterGuard() {
		return hunterGuard;
	}

	public void setHunterGuard(Map<AgentPattern, Double> hunterGuard) {
		this.hunterGuard = hunterGuard;
	}

	public Map<AgentPattern, Double> getHunterVote() {
		return hunterVote;
	}

	public void setHunterVote(Map<AgentPattern, Double> hunterVote) {
		this.hunterVote = hunterVote;
	}


	public Map<AgentPattern, Double> getPossessedVote() {
		return possessedVote;
	}

	public void setPossessedVote(Map<AgentPattern, Double> possessedVote) {
		this.possessedVote = possessedVote;
	}



	public Map<AgentPattern, Map<Species, Double>> getWolfDivine() {
		return wolfDivine;
	}

	public void setWolfDivine(Map<AgentPattern, Map<Species, Double>> wolfDivine) {
		this.wolfDivine = wolfDivine;
	}

	public Map<AgentPattern, Map<Species, Double>> getPossessedDivine() {
		return possessedDivine;
	}

	public void setPossessedDivine(
			Map<AgentPattern, Map<Species, Double>> possessedDivine) {
		this.possessedDivine = possessedDivine;
	}

	

	public Map<AgentPattern, Map<Species, Double>> getWolfInquest() {
		return wolfInquest;
	}

	public void setWolfInquest(Map<AgentPattern, Map<Species, Double>> wolfInquest) {
		this.wolfInquest = wolfInquest;
	}

	public Map<AgentPattern, Map<Species, Double>> getPossessedInquest() {
		return possessedInquest;
	}

	public void setPossessedInquest(
			Map<AgentPattern, Map<Species, Double>> possessedInquest) {
		this.possessedInquest = possessedInquest;
	}

	public Map<AgentPattern, Double> getWolfAttack() {
		return wolfAttack;
	}

	public void setWolfAttack(Map<AgentPattern, Double> wolfAttack) {
		this.wolfAttack = wolfAttack;
	}

	public Map<AgentPattern, Double> getWolfVote() {
		return wolfVote;
	}

	public void setWolfVote(Map<AgentPattern, Double> wolfVote) {
		this.wolfVote = wolfVote;
	}

	public int getLikelihood() 
	{
		return Likelihood;
	}

	public void setLikelihood(int likelihood) 
	{
		Likelihood = likelihood;
	}
	
	public void addLikelihood()
	{
		Likelihood += 1;
	}
}
