package org.aiwolf.laern.lib;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.Map.Entry;

public class Observe {
	
	private GiftedObserve firstSeer;
	private GiftedObserve secondSeer;
	private GiftedObserve thirdSeer;
	// private GiftedObserve forthSeer;

	private GiftedObserve firstMedium;
	private GiftedObserve secondMedium;
	private GiftedObserve thirdMedium;
//	GiftedObserve forthMedium;
	

	//Situationのハッシュ値，出現回数
	private Map<Integer, Integer> situationMap = new HashMap<Integer, Integer>();
	
/*	private SituationPool situationPool;
	//TODO どこかでSetすること

	
	public SituationPool getSituationPool() {
		return situationPool;
	}

	public void setSituationPool(SituationPool situationPool) {
		this.situationPool = situationPool;
	}*/

	
	// TODO 観測を一意に決めるためのフィールドを作成
	//占い師の状態を作るために，新しいクラスを生成する可能性も
	
	public Observe() {
		// TODO 自動生成されたコンストラクター・スタブ
	}
	
	public Observe(StringTokenizer token){
		try {
			int count = 0;
			while (token.hasMoreTokens()) {
				if(count < 6){
					String str = token.nextToken();
					if(str.equals("n")){
						token.nextToken();
						count++;
						continue;
					}
					boolean isAlive = StringConberter.parseBoolean(str);
					int wolfJudgeNum = Integer.parseInt(token.nextToken());
					if(count == 0){
						firstSeer = new GiftedObserve(isAlive, wolfJudgeNum);
					}else if(count == 1){
						secondSeer = new GiftedObserve(isAlive, wolfJudgeNum);
					}else if(count == 2){
						thirdSeer = new GiftedObserve(isAlive, wolfJudgeNum);
					}else if(count == 3){
						firstMedium = new GiftedObserve(isAlive, wolfJudgeNum);
					}else if(count == 4){
						secondMedium = new GiftedObserve(isAlive, wolfJudgeNum);
					}else if(count == 5){
						thirdMedium = new GiftedObserve(isAlive, wolfJudgeNum);
					}
					
				}else{
					//situationMapの生成
					int hash = Integer.parseInt(token.nextToken());
					int appearance = Integer.parseInt(token.nextToken());
					situationMap.put(hash, appearance);
				}
				count++;
			}
		} catch (NoSuchElementException e) {
			e.printStackTrace();
		}

	}
	
	public String toDataString(){
		StringBuilder builder = new StringBuilder();
		GiftedObserve[] gs = {firstSeer, secondSeer, thirdSeer, firstMedium, secondMedium, thirdMedium};
		for(int i = 0; i < gs.length; i++){
			GiftedObserve go = gs[i];
			if(go == null){
				builder.append("n,n,");
			}else{
				builder.append(StringConberter.toString(go.isAlive()) + "," + go.getWolfJudgeNum() + ",");
			}
		}
		
		for(Entry<Integer, Integer> set: situationMap.entrySet()){
			builder.append(set.getKey() + "," + set.getValue() + ",");
		}
		return builder.toString();
	}

	public Map<Integer, Integer> getSituationMap() {
		return situationMap;
	}

	public void setSituationMap(Map<Integer, Integer> situations) {
		this.situationMap = situations;
	}
	
	public Map<Situation, Integer> getDecodeSituationMap(SituationPool pool){
		Map<Situation, Integer> map = new HashMap<Situation, Integer>();
		for(Entry<Integer, Integer> set: situationMap.entrySet()){
			Situation decode = pool.getSituation(set.getKey());
			if(decode == null){
				continue;
			}
			//Situation decode = Situation.decodeHash(set.getKey());
			map.put(decode, set.getValue());
		}
		return map;
	}

	public void updateSituationMap(Situation situation, SituationPool pool){
		int hash = situation.hashCode();
		int value;
		if(situationMap.containsKey(hash)){
			value = situationMap.get(hash) + 1;
		}else{
			pool.getSituations().put(hash, situation);
			value = 1;
		}
		situationMap.put(hash, value);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((firstMedium == null) ? 0 : firstMedium.hashCode());
		result = prime * result
				+ ((firstSeer == null) ? 0 : firstSeer.hashCode());
		result = prime * result
				+ ((secondMedium == null) ? 0 : secondMedium.hashCode());
		result = prime * result
				+ ((secondSeer == null) ? 0 : secondSeer.hashCode());
		result = prime * result
				+ ((thirdMedium == null) ? 0 : thirdMedium.hashCode());
		result = prime * result
				+ ((thirdSeer == null) ? 0 : thirdSeer.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Observe other = (Observe) obj;
		if (firstMedium == null) {
			if (other.firstMedium != null)
				return false;
		} else if (!firstMedium.equals(other.firstMedium))
			return false;
		if (firstSeer == null) {
			if (other.firstSeer != null)
				return false;
		} else if (!firstSeer.equals(other.firstSeer))
			return false;
		if (secondMedium == null) {
			if (other.secondMedium != null)
				return false;
		} else if (!secondMedium.equals(other.secondMedium))
			return false;
		if (secondSeer == null) {
			if (other.secondSeer != null)
				return false;
		} else if (!secondSeer.equals(other.secondSeer))
			return false;
		if (thirdMedium == null) {
			if (other.thirdMedium != null)
				return false;
		} else if (!thirdMedium.equals(other.thirdMedium))
			return false;
		if (thirdSeer == null) {
			if (other.thirdSeer != null)
				return false;
		} else if (!thirdSeer.equals(other.thirdSeer))
			return false;
		return true;
	}

	public GiftedObserve getFirstSeer() {
		return firstSeer;
	}

	public void setFirstSeer(GiftedObserve firstSeer) {
		this.firstSeer = firstSeer;
	}

	public GiftedObserve getSecondSeer() {
		return secondSeer;
	}

	public void setSecondSeer(GiftedObserve secondSeer) {
		this.secondSeer = secondSeer;
	}

	public GiftedObserve getThirdSeer() {
		return thirdSeer;
	}

	public void setThirdSeer(GiftedObserve thirdSeer) {
		this.thirdSeer = thirdSeer;
	}

	public GiftedObserve getFirstMedium() {
		return firstMedium;
	}

	public void setFirstMedium(GiftedObserve firstMedium) {
		this.firstMedium = firstMedium;
	}

	public GiftedObserve getSecondMedium() {
		return secondMedium;
	}

	public void setSecondMedium(GiftedObserve secondMedium) {
		this.secondMedium = secondMedium;
	}

	public GiftedObserve getThirdMedium() {
		return thirdMedium;
	}

	public void setThirdMedium(GiftedObserve thirdMedium) {
		this.thirdMedium = thirdMedium;
	}

}
