package org.aiwolf.laern.lib;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Map.Entry;

import javax.swing.text.StringContent;

import org.aiwolf.common.data.Role;
import org.aiwolf.kajiClient.lib.CauseOfDeath;

import com.gmail.kajiwara009.Learning.ProfitSharing;
import com.gmail.kajiwara009.util.TwoValue;

public class Situation {
	
	private Role firstSeer;
	private Role secondSeer;
	private Role thirdSeer;
	
	private Role firstMedium;
	private Role secondMedium;
	private Role thirdMedium;
	
//	private static ProfitSharing ps = new ProfitSharing();
	
	Map<SelectAction, Float> voteValues = new HashMap<SelectAction, Float>();
	Map<SelectAction, Float> divineValues = new HashMap<SelectAction, Float>();
	Map<SelectAction, Float> guardValues = new HashMap<SelectAction, Float>();
	Map<COAction, Float> coActionValues = new HashMap<COAction, Float>();
	
	public Situation(StringTokenizer token){
		try {
			int count = 0;
			String mapSelector = null;
			Set<String> yoyakus = new HashSet<String>();
			yoyakus.add("v");
			yoyakus.add("d");
			yoyakus.add("g");
			yoyakus.add("c");
			while (token.hasMoreTokens()) {
				if(count < 6){
					Role role = StringConberter.parseRole(token.nextToken());
					
					if(count == 0){
						firstSeer = role;
					}else if(count == 1){
						secondSeer = role;
					}else if(count == 2){
						thirdSeer = role;
					}else if(count == 3){
						firstMedium = role;
					}else if(count == 4){
						secondMedium = role;
					}else if(count == 5){
						thirdMedium = role;
					}
					
				}else{
					String str = token.nextToken();
					if(yoyakus.contains(str)){
						mapSelector = str;
					}else{
						if(mapSelector.equals("v")){
							SelectAction selAct = SelectAction.valueOf(str);
							float value = Float.parseFloat(token.nextToken());
							voteValues.put(selAct, value);
						}else if(mapSelector.equals("d")){
							SelectAction selAct = SelectAction.valueOf(str);
							float value = Float.parseFloat(token.nextToken());
							divineValues.put(selAct, value);
						}else if(mapSelector.equals("g")){
							SelectAction selAct = SelectAction.valueOf(str);
							float value = Float.parseFloat(token.nextToken());
							guardValues.put(selAct, value);
						}else if(mapSelector.equals("c")){
							COAction coAct = COAction.valueOf(str);
							float value = Float.parseFloat(token.nextToken());
							coActionValues.put(coAct, value);
						}
					}
				}
				count++;
			}
		} catch (NoSuchElementException e) {
			e.printStackTrace();
		}

	}
	
	public static Situation decodeHash(int hash){
		//TODO
		System.out.println("hashからSituationを生成");
		return null;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((firstMedium == null) ? 0 : roleToHash(firstMedium));
		result = prime * result
				+ ((firstSeer == null) ? 0 : roleToHash(firstSeer));
		result = prime * result
				+ ((secondMedium == null) ? 0 : roleToHash(secondMedium));
		result = prime * result
				+ ((secondSeer == null) ? 0 : roleToHash(secondSeer));
		result = prime * result
				+ ((thirdMedium == null) ? 0 : roleToHash(thirdMedium));
		result = prime * result
				+ ((thirdSeer == null) ? 0 : roleToHash(thirdSeer));
		return result;
	}
	
	private static int roleToHash(Role role){
		switch (role) {
		case BODYGUARD:
			return 1;
		case FREEMASON:
			return 2;
		case MEDIUM:
			return 3;
		case POSSESSED:
			return 4;
		case SEER:
			return 5;
		case VILLAGER:
			return 6;
		case WEREWOLF:
			return 7;
		default:
			System.out.println("Situation: hashが不正 + " + role);
			return -1;
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Situation other = (Situation) obj;
		if (firstMedium != other.firstMedium)
			return false;
		if (firstSeer != other.firstSeer)
			return false;
		if (secondMedium != other.secondMedium)
			return false;
		if (secondSeer != other.secondSeer)
			return false;
		if (thirdMedium != other.thirdMedium)
			return false;
		if (thirdSeer != other.thirdSeer)
			return false;
		return true;
	}
	public Situation() {
		for(COAction act: COAction.values()){
			coActionValues.put(act, 0f);
		}
	}
	
	public<T, K> void updateActionValue(K set, boolean isWin, LearningControler lc){
		float reward = isWin? 1.0f: 0.0f;
		
		if(set instanceof COAction){
			float newValue = lc.getPs().learn(coActionValues.get(set), reward);
			coActionValues.put((COAction)set, newValue);
			return;
		}
		
		TwoValue<T, Action> actionSet = (TwoValue<T, Action>)set;
		
		T action = actionSet.getOne();
		
		
		if(action instanceof SelectAction){
			float newValue;
			switch (actionSet.getTwo()) {
			case VOTE:
				newValue = lc.getPs().learn(getVoteValue((SelectAction)action), reward);
				voteValues.put((SelectAction)action, newValue);
				break;
				
			case DIVINE:
				newValue = lc.getPs().learn(getDivineValue((SelectAction)action), reward);
				divineValues.put((SelectAction)action, newValue);
				break;
			case GUARD:
				newValue = lc.getPs().learn(getGuardValue((SelectAction)action), reward);
				guardValues.put((SelectAction)action, newValue);
				break;
			}
		}else if (action instanceof COAction){
			System.out.println("おかしい");
			float newValue =  lc.getPs().learn(getCOValue((COAction)action), reward);
			coActionValues.put((COAction)action, newValue);
		}
	}

	public String toDataString(){
		StringBuilder str = new StringBuilder();
		str.append(StringConberter.toString(firstSeer) + ",");
		str.append(StringConberter.toString(secondSeer) + ",");
		str.append(StringConberter.toString(thirdSeer) + ",");
		str.append(StringConberter.toString(firstMedium) + ",");
		str.append(StringConberter.toString(secondMedium) + ",");
		str.append(StringConberter.toString(thirdMedium) + ",");
		
		str.append("v" + ",");
		for(Entry<SelectAction, Float> set: voteValues.entrySet()){
			str.append(set.getKey().toString() + "," + set.getValue().toString() + ",");
		}
		str.append("d" + ",");
		for(Entry<SelectAction, Float> set: divineValues.entrySet()){
			str.append(set.getKey().toString() + "," + set.getValue().toString() + ",");
		}
		str.append("g" + ",");
		for(Entry<SelectAction, Float> set: guardValues.entrySet()){
			str.append(set.getKey().toString() + "," + set.getValue().toString() + ",");
		}
		str.append("c" + ",");
		for(Entry<COAction, Float> set: coActionValues.entrySet()){
			str.append(set.getKey().toString() + "," + set.getValue().toString() + ",");
		}
		
		return str.toString();
	}
	
	
	
	
	
	
	
	
	
	
/*	
	public Map<SelectAction, Float> getGuardValues() {
		return selectActionValues;
	}
*/
	
	public Float getVoteValue(SelectAction act){
		if(!voteValues.containsKey(act)){
			voteValues.put(act, 0f);
		}
		return voteValues.get(act);
	}
	
	public void setVoteValue(SelectAction act, float value){
		voteValues.put(act, value);
	}
	
	public Float getDivineValue(SelectAction act){
		if(!divineValues.containsKey(act)){
			divineValues.put(act, 0f);
		}
		return divineValues.get(act);
	}
	
	public void setDivineValue(SelectAction act, float value){
		divineValues.put(act, value);
	}

	public Float getGuardValue(SelectAction act){
		if(!guardValues.containsKey(act)){
			guardValues.put(act, 0f);
		}
		return guardValues.get(act);
	}
	
	public void setGuardValue(SelectAction act, float value){
		guardValues.put(act, value);
	}
	
	public Float getCOValue(COAction act){
		if(!coActionValues.containsKey(act)){
			coActionValues.put(act, 0f);
		}
		return coActionValues.get(act);
	}
	
	public void setCOValue(COAction act, float value){
		coActionValues.put(act, value);
	}


	
	public Map<COAction, Float> getCoActionValues() {
		return coActionValues;
	}

	public void setCoActionValues(Map<COAction, Float> coActionValues) {
		this.coActionValues = coActionValues;
	}

	public Role getFirstSeer() {
		return firstSeer;
	}

	public void setFirstSeer(Role firstSeer) {
		this.firstSeer = firstSeer;
	}

	public Role getSecondSeer() {
		return secondSeer;
	}

	public void setSecondSeer(Role secondSeer) {
		this.secondSeer = secondSeer;
	}

	public Role getThirdSeer() {
		return thirdSeer;
	}

	public void setThirdSeer(Role thirdSeer) {
		this.thirdSeer = thirdSeer;
	}

	public Role getFirstMedium() {
		return firstMedium;
	}

	public void setFirstMedium(Role firstMedium) {
		this.firstMedium = firstMedium;
	}

	public Role getSecondMedium() {
		return secondMedium;
	}

	public void setSecondMedium(Role secondMedium) {
		this.secondMedium = secondMedium;
	}

	public Role getThirdMedium() {
		return thirdMedium;
	}

	public void setThirdMedium(Role thirdMedium) {
		this.thirdMedium = thirdMedium;
	}

}
