package main;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Result {
	private final String VILLAGER_WIN_SUFFIX = ":VILLAGER_WIN";
	private final String VILLAGER_LOSE_SUFFIX = ":VILLAGER_LOSE";

	private final String WEREWOLF_WIN_SUFFIX = ":WEREWOLF_WIN";
	private final String WEREWOLF_LOSE_SUFFIX = ":WEREWOLF_LOSE";
	private Map<String, Integer> resultMap = new HashMap<String, Integer>();

	private Set<String> nameSet = new HashSet<String>();

	public Result() {

	}

	public void addVillagerWinNum(String user) {
		nameSet.add(user);
		if(this.resultMap.containsKey(user + this.VILLAGER_WIN_SUFFIX)){
			this.resultMap.put(user + this.VILLAGER_WIN_SUFFIX, this.resultMap.get(user + this.VILLAGER_WIN_SUFFIX) + 1);
		}else{
			this.resultMap.put(user + this.VILLAGER_WIN_SUFFIX, 1);
		}
	}

	public void addVillagerLoseNum(String user) {
		nameSet.add(user);
		if(this.resultMap.containsKey(user + this.VILLAGER_LOSE_SUFFIX)){
			this.resultMap.put(user + this.VILLAGER_LOSE_SUFFIX, this.resultMap.get(user + this.VILLAGER_LOSE_SUFFIX) + 1);
		}else{
			this.resultMap.put(user + this.VILLAGER_LOSE_SUFFIX, 1);
		}
	}

	public void addWerewolfWinNum(String user) {
		nameSet.add(user);
		if(this.resultMap.containsKey(user + this.WEREWOLF_WIN_SUFFIX)){
			this.resultMap.put(user + this.WEREWOLF_WIN_SUFFIX, this.resultMap.get(user + this.WEREWOLF_WIN_SUFFIX) + 1);
		}else{
			this.resultMap.put(user + this.WEREWOLF_WIN_SUFFIX, 1);
		}
	}

	public void addWerewolfLoseNum(String user) {
		nameSet.add(user);
		if(this.resultMap.containsKey(user + this.WEREWOLF_LOSE_SUFFIX)){
			this.resultMap.put(user + this.WEREWOLF_LOSE_SUFFIX, this.resultMap.get(user + this.WEREWOLF_LOSE_SUFFIX) + 1);
		}else{
			this.resultMap.put(user + this.WEREWOLF_LOSE_SUFFIX, 1);
		}
	}

	public double getVillagerWinRate(String user) {
		if(this.resultMap.get(user + this.VILLAGER_WIN_SUFFIX) == null || this.resultMap.get(user + this.VILLAGER_LOSE_SUFFIX) == null){
			return 0.0;
		}

		int win = this.resultMap.get(user + this.VILLAGER_WIN_SUFFIX);
		int lose = this.resultMap.get(user + this.VILLAGER_LOSE_SUFFIX);

		return win*1.0 / (win + lose)*1.0;
	}

	public double getWerewolfWinRate(String user) {
		if(this.resultMap.get(user + this.WEREWOLF_WIN_SUFFIX) == null || this.resultMap.get(user + this.WEREWOLF_LOSE_SUFFIX) == null){
			return 0.0;
		}

		int win = this.resultMap.get(user + this.WEREWOLF_WIN_SUFFIX);
		int lose = this.resultMap.get(user + this.WEREWOLF_LOSE_SUFFIX);

		return win*1.0 / (win + lose)*1.0;
	}

	public double getTotalWinRate(String user) {
		if(this.resultMap.get(user + this.WEREWOLF_WIN_SUFFIX) == null || this.resultMap.get(user + this.WEREWOLF_LOSE_SUFFIX) == null ||
				this.resultMap.get(user + this.VILLAGER_WIN_SUFFIX) == null || this.resultMap.get(user + this.VILLAGER_LOSE_SUFFIX) == null	){
			return 0.0;
		}

		int win = this.resultMap.get(user + this.WEREWOLF_WIN_SUFFIX) + this.resultMap.get(user + this.VILLAGER_WIN_SUFFIX) ;
		int lose = this.resultMap.get(user + this.WEREWOLF_LOSE_SUFFIX) + this.resultMap.get(user + this.VILLAGER_LOSE_SUFFIX);

		return win*1.0 / (win + lose)*1.0;
	}

	public Set<String> getNameSet() {
		return this.nameSet;
	}
}
