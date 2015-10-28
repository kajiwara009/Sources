/**
 * 
 */
package com.gmail.yusatk.tests;

import java.util.List;
import java.util.Map;

import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Judge;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Status;
import org.aiwolf.common.data.Talk;
import org.aiwolf.common.data.Vote;

import com.gmail.yusatk.interfaces.IGameInfo;

/**
 * @author Yu
 *
 */
public class DummyGameInfo implements IGameInfo {

	Agent agent;
	List<Agent> agentList;
	List<Agent> aliveAgentList;
	Agent attackedAgent;
	int day;
	Judge divineResult;
	Agent executedAgent;
	Agent guardedAgent;
	Judge mediumResult;
	Role role;
	Map<Agent, Role> roleMap;
	Map<Agent, Status> statusMap;
	List<Talk> talkList;
	List<Vote> voteList;
	List<Talk> whisperList;
	List<Vote> attackVoteList;
	
	public List<Vote> getAttackVoteList() {
		return attackVoteList;
	}

	public void setAttackVoteList(List<Vote> attackVoteList) {
		this.attackVoteList = attackVoteList;
	}

	public void setAgent(Agent agent) {
		this.agent = agent;
	}

	public void setAgentList(List<Agent> agentList) {
		this.agentList = agentList;
	}

	public void setAliveAgentList(List<Agent> aliveAgentList) {
		this.aliveAgentList = aliveAgentList;
	}

	public void setAttackedAgent(Agent attackedAgent) {
		this.attackedAgent = attackedAgent;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public void setDivineResult(Judge divineResult) {
		this.divineResult = divineResult;
	}

	public void setExecutedAgent(Agent executedAgent) {
		this.executedAgent = executedAgent;
	}

	public void setGuardedAgent(Agent guardedAgent) {
		this.guardedAgent = guardedAgent;
	}

	public void setMediumResult(Judge mediumResult) {
		this.mediumResult = mediumResult;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public void setRoleMap(Map<Agent, Role> roleMap) {
		this.roleMap = roleMap;
	}

	public void setStatusMap(Map<Agent, Status> statusMap) {
		this.statusMap = statusMap;
	}

	public void setTalkList(List<Talk> talkList) {
		this.talkList = talkList;
	}

	public void setVoteList(List<Vote> voteList) {
		this.voteList = voteList;
	}

	public void setWhisperList(List<Talk> whisperList) {
		this.whisperList = whisperList;
	}

	public Agent getAgent() {
		return agent;
	}

	public List<Agent> getAgentList() {
		return agentList;
	}

	public List<Agent> getAliveAgentList() {
		return aliveAgentList;
	}

	public Agent getAttackedAgent() {
		return attackedAgent;
	}

	public int getDay() {
		return day;
	}

	public Judge getDivineResult() {
		return divineResult;
	}

	public Agent getExecutedAgent() {
		return executedAgent;
	}

	public Agent getGuardedAgent() {
		return guardedAgent;
	}

	public Judge getMediumResult() {
		return mediumResult;
	}

	public Role getRole() {
		return role;
	}

	public Map<Agent, Role> getRoleMap() {
		return roleMap;
	}

	public Map<Agent, Status> getStatusMap() {
		return statusMap;
	}

	public List<Talk> getTalkList() {
		return talkList;
	}

	public List<Vote> getVoteList() {
		return voteList;
	}

	public List<Talk> getWhisperList() {
		return whisperList;
	}


}
