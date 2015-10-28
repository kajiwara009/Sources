package org.aiwolf.laern.lib;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Judge;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Species;

public class Information {
//	private int playerNum = 0;
	private Set<Agent> players = new HashSet<Agent>();
	private Map<Agent, Role> COmap = new HashMap<Agent, Role>();
	private Map<Agent, Set<Judge>> judgeSets = new HashMap<Agent, Set<Judge>>();
	private Set<Agent> attacked = new HashSet<Agent>();
	private Set<Agent> executed = new HashSet<Agent>();

	private List<Agent> seerCOList = new ArrayList<Agent>();
	private List<Agent> mediumCOList = new ArrayList<Agent>();
	
	public Information() {
	}
	
	
	public Observe getObserve(ObservePool pool){
		//TODO Information から observeを作成
		//poolに存在すれば，取ってくるだけ，なければ新しく生成して，poolに入れる
		Observe newObserve = createNewObserve();
		return pool.getObserve(newObserve);
	}
	
	private Observe createNewObserve(){
		Observe newObserve = new Observe();
		
		for(int num = 0; num < seerCOList.size(); num++){
			if(num >= 3){
				break;
			}
			Agent seer = seerCOList.get(num);
			GiftedObserve giftObs = getGiftedObserve(seer);
			switch (num) {
			case 0:
				newObserve.setFirstSeer(giftObs);
				break;
			case 1:
				newObserve.setSecondSeer(giftObs);
				break;
			case 2:
				newObserve.setThirdSeer(giftObs);;
				break;
			default:
				break;
			}
		}
		for(int num = 0; num < mediumCOList.size(); num++){
			if(num >= 3){
				break;
			}
			Agent medium = mediumCOList.get(num);
			GiftedObserve giftObs = getGiftedObserve(medium);
			switch (num) {
			case 0:
				newObserve.setFirstMedium(giftObs);
				break;
			case 1:
				newObserve.setSecondMedium(giftObs);
				break;
			case 2:
				newObserve.setThirdMedium(giftObs);;
				break;
			default:
				break;
			}
		}
		return newObserve;

	}
	
	private GiftedObserve getGiftedObserve(Agent agent){
		boolean isAlive;
		int wolfJudgeNum = 0;
		if(attacked.contains(agent) || executed.contains(agent)){
			isAlive = false;
		}else{
			isAlive = true;
		}
		Set<Judge> judges = judgeSets.get(agent);
		if(judges != null){
			for(Judge judge: judges){
				if(judge.getResult() == Species.WEREWOLF){
					wolfJudgeNum++;
				}
			}
		}
		
		return new GiftedObserve(isAlive, wolfJudgeNum);

	}
	
	public Collection<Agent> getSurviver(){
		Collection<Agent> surviver = new HashSet<Agent>(players);
		for(Agent attack: attacked){
			surviver.remove(attack);
		}
		for(Agent execute: executed){
			surviver.remove(execute);
		}
		return surviver;
	}


	/**
	 * finish時にroleMapを見て，その時の本当のSituationを生成する
	 * @param roleMap
	 * @return
	 */
	public Situation getTrueSituation(Map<Agent, Role> roleMap, SituationPool pool) {
		Situation situation = new Situation();
		Observe obs = createNewObserve();
		for(int num = 0; num < seerCOList.size(); num++){
			if(num >= 3){
				break;
			}
			Agent seer = seerCOList.get(num);
			Role trueRole = roleMap.get(seer);
			switch (num) {
			case 0:
				situation.setFirstSeer(trueRole);
				break;
			case 1:
				situation.setSecondSeer(trueRole);
				break;
			case 2:
				situation.setThirdSeer(trueRole);
				break;
			default:
				break;
			}
		}
		for(int num = 0; num < mediumCOList.size(); num++){
			if(num >= 3){
				break;
			}
			Agent medium = mediumCOList.get(num);
			Role trueRole = roleMap.get(medium);
			switch (num) {
			case 0:
				situation.setFirstMedium(trueRole);
				break;
			case 1:
				situation.setSecondMedium(trueRole);
				break;
			case 2:
				situation.setThirdMedium(trueRole);
				break;
			default:
				break;
			}
		}
		return pool.getSituation(situation);
	}
	
	public boolean hasJudge(Agent talker, Judge judge){
		Set<Judge> judges = judgeSets.get(talker);
		if(judges == null){
			return false;
		}else{
			for(Judge setJudge: judges){
				if(isSameJudges(judge, setJudge)){
					return true;
				}
			}
			return false;
		}
	}
	
	private boolean isSameJudges(Judge judge1, Judge judge2){
		boolean target = judge1.getTarget() == judge2.getTarget();
		boolean agent = judge1.getAgent() == judge2.getAgent();
		boolean result = judge1.getResult() == judge2.getResult();
		if(target && agent && result){
			return true;
		}else{
			return false;
		}
	}
	
	public Information copy(){
		Information copy = new Information();
		copy.attacked = new HashSet<Agent>(attacked);
		copy.COmap = new HashMap<Agent, Role>(COmap);
		copy.executed = new HashSet<Agent>(executed);
		//copy.judgeSets = new HashMap<Agent, Set<Judge>>(judgeSets);
		copy.mediumCOList = new ArrayList<Agent>(mediumCOList);
		copy.seerCOList = new ArrayList<Agent>(seerCOList);
		
		HashMap<Agent, Set<Judge>> sets = new HashMap<Agent, Set<Judge>>();
		for(Entry<Agent, Set<Judge>> set: judgeSets.entrySet()){
			sets.put(set.getKey(), new HashSet<Judge>(set.getValue()));
		}
		copy.judgeSets = sets;
		
		return copy;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	public Map<Agent, Set<Judge>> getJudgeSets() {
		return judgeSets;
	}


	public void setJudgeSets(Map<Agent, Set<Judge>> judgeLists) {
		this.judgeSets = judgeLists;
	}




	public Map<Agent, Role> getCOmap() {
		return COmap;
	}


	public void setCOmap(Map<Agent, Role> cOmap) {
		COmap = cOmap;
	}


	public Set<Agent> getAttacked() {
		return attacked;
	}


	public void setAttacked(Set<Agent> attacked) {
		this.attacked = attacked;
	}


	public Set<Agent> getExecuted() {
		return executed;
	}


	public void setExecuted(Set<Agent> executed) {
		this.executed = executed;
	}


	public List<Agent> getSeerCOList() {
		return seerCOList;
	}


	public void setSeerCOList(List<Agent> seerCOList) {
		this.seerCOList = seerCOList;
	}


	public List<Agent> getMediumCOList() {
		return mediumCOList;
	}


	public void setMediumCOList(List<Agent> mediumCOList) {
		this.mediumCOList = mediumCOList;
	}


	public Set<Agent> getPlayers() {
		return players;
	}


	public void setPlayers(Set<Agent> players) {
		this.players = players;
	}



}
