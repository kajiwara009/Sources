/**
 * 
 */
package com.gmail.yusatk.data;

import java.util.*;

import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Judge;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Species;

import com.gmail.yusatk.interfaces.*;
import com.gmail.yusatk.utils.DebugLog;


/**
 * @author Yu
 *
 */
public class World implements IWorld {
	IAnalyzer analyzer;
	Map<Role, Integer> masterRoleNumMap = null;
	Map<Agent, Role> roleMap = new HashMap<Agent, Role>();
	Map<Role, Integer> roleNumMap = new HashMap<Role, Integer>();
	
	/* (non-Javadoc)
	 * @see com.gmail.yusatk.interfaces.IWorld#setup(java.util.Map, java.util.List)
	 */
	public void setup(IAnalyzer analyzer) {
		this.analyzer = analyzer;
		this.masterRoleNumMap = analyzer.getSetting().getRoleNumMap();
		roleNumMap.put(Role.BODYGUARD, 0);
		roleNumMap.put(Role.MEDIUM, 0);
		roleNumMap.put(Role.SEER, 0);
		roleNumMap.put(Role.POSSESSED, 0);
		roleNumMap.put(Role.WEREWOLF, 0);
		roleNumMap.put(Role.VILLAGER, 0);
	}

	/* (non-Javadoc)
	 * @see com.gmail.yusatk.interfaces.IWorld#mapRole(org.aiwolf.common.data.Agent, org.aiwolf.common.data.Role)
	 */
	@Override
	public void setRole(Agent target, Role role) {
		Role targetRole = roleMap.get(target);
		if(roleMap.containsKey(target)) {
			roleMap.remove(target);
			if(targetRole != null) {
				roleNumMap.put(targetRole, roleNumMap.get(targetRole) - 1);
			}
		}
		roleMap.put(target, role);
		roleNumMap.put(role, roleNumMap.get(role) + 1);
	}
	
	/* (non-Javadoc)
	 * @see com.gmail.yusatk.interfaces.IWorld#validWorld()
	 */
	@Override
	public boolean isValidWorld() {
		List<Agent> wolfList = getWolves();
		int maxWolfCount = masterRoleNumMap.get(Role.WEREWOLF);
		int maxPossessedCount = masterRoleNumMap.get(Role.POSSESSED);
		int maxEnemyCount = maxWolfCount + maxPossessedCount;

		// 役職の数の内訳がおかしければ不正
		for(Map.Entry<Role, Integer> e : roleNumMap.entrySet()) {
			if(e.getValue() > masterRoleNumMap.get(e.getKey())) {
				return false;
			}
		}
		
		// 確定狼の数がおかしければ不正
		if(wolfList.size() > masterRoleNumMap.get(Role.WEREWOLF)) {
			DebugLog.log("InvalidWorld 8\n");
			Dump();
			return false;
		}
		
		
		// 襲撃対象に狼が含まれていれば不正
		List<Agent> attackedAgents = analyzer.getAttackedAgents();
		for(Agent wolf : wolfList) {
			if(attackedAgents.contains(wolf)) {
				DebugLog.log("InvalidWorld 1\n");
				Dump();
				return false;
			}
		}
		
		// 狼全員が確定していて、それらがすべて処刑されているのに処理が続く場合は不正
		if(getDeadWolves().size() >= masterRoleNumMap.get(Role.WEREWOLF)) {
			DebugLog.log("InvalidWorld 2\n");
			Dump();
			return false;
		}

		Agent seer = getSeer();
		Agent medium = getMedium();
		
		// 霊能と占いで結果が割れている場合は不正
		List<Judge> seerJudges = analyzer.getDivineResultBySeer(seer);
		List<Judge> mediumJudges = analyzer.getInquestResultByMedium(medium);
		for(Judge seerJudge : seerJudges) {
			for(Judge mediumJudge : mediumJudges) {
				if(seerJudge.getTarget() == mediumJudge.getTarget()) {
					if(seerJudge.getResult() != mediumJudge.getResult()) {
						DebugLog.log("InvalidWorld 9\n");
						Dump();
						return false;
					}
				}
			}
		}
		
		
		// 占い結果に矛盾があれば不正
		int foundHideWolfCount = 0;
		int nakedEnemyCount = analyzer.getNakedEnemyCount();
		List<Agent> coAgents = analyzer.getCoAgents();
		for(Judge seerJudge : seerJudges) {
			if(wolfList.contains(seerJudge.getTarget()) && seerJudge.getResult() == Species.HUMAN) {
				DebugLog.log("InvalidWorld 10\n");
				Dump();
				return false;
			}
			
			if(seerJudge.getResult() == Species.WEREWOLF) {
				if(!coAgents.contains(seerJudge.getTarget())) {
					foundHideWolfCount++;
				}
			}
		}
		// 露出人外数 + 発見した潜伏人外数が人外総数より多ければ破綻
		if(seerJudges.size() >= analyzer.getDay()) {
			if(nakedEnemyCount + foundHideWolfCount > maxEnemyCount) {
				return false;
			}
		}
		
		// 霊結果に矛盾があれば不正
		int exedBlackCount = 0;
		int exedHideWolfCount = 0;
		for(Judge mediumJudge : mediumJudges) {
			if(wolfList.contains(mediumJudge.getTarget()) && mediumJudge.getResult() == Species.HUMAN) {
				DebugLog.log("InvalidWorld 11\n");
				Dump();
				return false;
			}
			if(mediumJudge.getResult() == Species.WEREWOLF) {
				exedBlackCount++;
				if(!coAgents.contains(mediumJudge.getTarget())) {
					exedHideWolfCount++;
				}
			}
		}
		// 霊視点の残り狼数 > 残り狼数なら不正
		if(mediumJudges.size() >= analyzer.getDay() - 1) {
			int restExeCount = analyzer.getRestExecutionCount();
			int restWolfMediumSide = masterRoleNumMap.get(Role.WEREWOLF) - exedBlackCount;
			if(restWolfMediumSide > restExeCount) {
				return false;
			}
		}
		// 霊視点の処刑済み狼数 + 露出人外数が人外最大数を超えるなら破綻
		if(exedHideWolfCount + nakedEnemyCount > maxEnemyCount) {
			return false;
		}
		
		// 狼候補が 0 の場合は不正
		if(getWolfCandidates().size() == 0 && wolfList.size() != masterRoleNumMap.get(Role.WEREWOLF)){
			DebugLog.log("InvalidWorld 6\n");
			Dump();
			return false;
		}
		
		return true;
	}
	


	@Override
	public List<Agent> getWolfCandidates() {
		List<Agent> candidates = analyzer.getLatestGameInfo().getAliveAgentList();
		Agent seer = getSeer();
		if(seer != null) {
			candidates.remove(seer);
			List<Judge> judges = analyzer.getDivineResults().get(seer);
			for(Judge judge : judges) {
				candidates.remove(judge.getTarget());
			}
		}
		Agent medium = getMedium();
		if(medium != null) {
			candidates.remove(medium);
		}
		return candidates;
	}

	@Override
	public List<Agent> getWolves() {
		// TODO: 遅いようならキャッシュを検討する
		List<Agent> wolves = new LinkedList<Agent>();
		for(Map.Entry<Agent, Role> e : roleMap.entrySet()){
			if(e.getValue() == Role.WEREWOLF) {
				wolves.add(e.getKey());
			}
		}
		Agent seer = getSeer();
		if(seer != null) {
			List<Judge> divines = analyzer.getDivineResultBySeer(seer);
			for(Judge judge : divines){
				if(judge.getResult() == Species.WEREWOLF && !wolves.contains(judge.getTarget())) {
					wolves.add(judge.getTarget());
				}
			}
		}
		Agent medium = getMedium();
		if(medium != null) {
			List<Judge> inquests = analyzer.getInquestResultByMedium(medium);
			for(Judge judge : inquests){
				if(judge.getResult() == Species.WEREWOLF && !wolves.contains(judge.getTarget())) {
					wolves.add(judge.getTarget());
				}
			}
		}
		return wolves;
	}
	
	@Override
	public Agent getRoledAgent(Role role) {
		// TODO: 遅いようならキャッシュを検討する
		for(Map.Entry<Agent, Role> e : roleMap.entrySet()){
			if(e.getValue() == role) {
				return e.getKey();
			}
		}
		return null;		
	}
	
	@Override
	public Agent getSeer() {
		return getRoledAgent(Role.SEER);
	}
	
	@Override
	public Agent getMedium()  {
		return getRoledAgent(Role.MEDIUM);
	}
	
	@Override
	public void Dump() {
		if(!DebugLog.isEnable()) {
			return;
		}
		List<Agent> agents = analyzer.getLatestGameInfo().getAgentList();
		for(Agent agent : agents) {
			Role role = roleMap.get(agent);
			DebugLog.log("%s ", role == null ? "*" : role.toString().charAt(0));
		}
		
		DebugLog.log("\n");
	}

	@Override
	public void setRoles(Map<Agent, Role> roles) {
		assert false;
	}

	@Override
	public Map<Agent, Role> getRoles() {
		return roleMap;
	}

	@Override
	public boolean equals(Object t) {
		if(t == null) {
			return false;
		}
		if(t.getClass() != this.getClass()) {
			return false;
		}
		
		World w = (World)t;
		if(w.roleMap.size() != this.roleMap.size()) {
			return false;
		}
		
		for(Map.Entry<Agent, Role> m : w.roleMap.entrySet()) {
			if(this.roleMap.get(m.getKey()) != m.getValue()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public World clone() {
		World w = new World();
		w.setup(analyzer);
		for(Map.Entry<Agent, Role> e : this.roleMap.entrySet()) {
			w.setRole(e.getKey(), e.getValue());
		}
		return w;
	}

	@Override
	public void DumpWolves() {
		if(!DebugLog.isEnable()) {
			return;
		}
		DebugLog.log("Wolves in this world:\n");
		for(Agent wolf : getWolves()) {
			DebugLog.log("%2d ", wolf.getAgentIdx());
		}
		DebugLog.log("\n");
	}

	@Override
	public List<Agent> getAliveWolves() {
		List<Agent> aliveWolves = new LinkedList<Agent>();
		List<Agent> alives = analyzer.getAliveAgents();
		for(Agent wolf : getWolves()) {
			if(alives.contains(wolf)) {
				aliveWolves.add(wolf);
			}
		}
		return aliveWolves;
	}

	@Override
	public List<Agent> getDeadWolves() {
		List<Agent> deadWolves = new LinkedList<Agent>();
		List<Agent> alives = analyzer.getAliveAgents();
		for(Agent wolf : getWolves()) {
			if(!alives.contains(wolf)) {
				deadWolves.add(wolf);
			}
		}
		return deadWolves;
	}
	
	@Override
	public Agent getBodyguard() {
		return getRoledAgent(Role.BODYGUARD);
	}
	
}
