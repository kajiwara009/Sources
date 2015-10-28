/**
 * 
 */
package com.gmail.yusatk.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.aiwolf.client.lib.Utterance;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Judge;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Species;
import org.aiwolf.common.data.Talk;
import org.aiwolf.common.data.Vote;
import org.aiwolf.common.net.GameSetting;

import com.gmail.yusatk.interfaces.IAnalyzer;
import com.gmail.yusatk.interfaces.IGameInfo;
import com.gmail.yusatk.interfaces.ISuggestion;
import com.gmail.yusatk.utils.DebugLog;

/**
 * @author Yu
 *
 */
public class Analyzer implements IAnalyzer {
	int currentDay = -1;
	IGameInfo latestInfo;
	Logger logger = Logger.getGlobal();
	
	List<Agent> seerList = new ArrayList<Agent>();
	List<Agent> mediumList = new ArrayList<Agent>();
	List<Agent> bodyguardList = new ArrayList<Agent>();
	List<Agent> possessedsList = new ArrayList<Agent>();
	List<Agent> wolfList = new ArrayList<Agent>(); // 自称狼
	List<Agent> wolfCalledList = new ArrayList<Agent>(); // 他称狼
	
	List<Agent> executedList = new ArrayList<Agent>();
	List<Agent> killedList = new ArrayList<Agent>();

	List<Agent> buddyWolves = new ArrayList<Agent>();
	
	
	List<List<Talk>> talkList = new ArrayList<List<Talk>>();
	List<List<Talk>> whisperList = new ArrayList<List<Talk>>();
	
	Map<Agent, List<Judge>> divineList = new HashMap<Agent, List<Judge>>();
	Map<Agent, List<Judge>> inquestList = new HashMap<Agent, List<Judge>>();
	Map<Agent, List<Judge>> guardedList = new HashMap<Agent, List<Judge>>();
	
	List<List<Vote>> voteList = new ArrayList<List<Vote>>();
	
	List<List<Vote>> votePlanList = new ArrayList<List<Vote>>();
	
	List<Map<Integer, ISuggestion>> wolfSuggestions = new ArrayList<Map<Integer, ISuggestion>>();
	List<Map<Integer, ISuggestion>> humanSuggestions = new ArrayList<Map<Integer, ISuggestion>>();
	
	
	GameSetting setting;
	ITalkParser talkParser = new TalkParser();
	ITalkParser whisperParser = new WhisperParser();
	
	void updateDay(IGameInfo info) {
		if(currentDay == info.getDay()){
			return;
		}
		
		currentDay = info.getDay();
		talkList.add(new ArrayList<Talk>());
		whisperList.add(new ArrayList<Talk>());
		
		killedList.add(info.getAttackedAgent());
		executedList.add(info.getExecutedAgent());
		voteList.add(info.getVoteList());
		votePlanList.add(new ArrayList<Vote>());
		
		wolfSuggestions.add(new HashMap<Integer, ISuggestion>());
		humanSuggestions.add(new HashMap<Integer, ISuggestion>());
	}
	

	@SuppressWarnings("serial")
	Map<Role, List<Agent>> coListMap = new HashMap<Role, List<Agent>>() {
		{put(Role.SEER, seerList);}
		{put(Role.MEDIUM, mediumList); }
		{put(Role.BODYGUARD, bodyguardList); }
		{put(Role.POSSESSED, possessedsList); }
		{put(Role.WEREWOLF, wolfList); }
	};
	@SuppressWarnings("serial")
	Map<Role, Map<Agent, List<Judge>>> judgeMap = new HashMap<Role, Map<Agent,List<Judge>>>() {
		{put(Role.SEER, divineList);}
		{put(Role.MEDIUM, inquestList);}
	};
	void parseComingOut(Agent owner, Utterance utter) {
		Agent target = utter.getTarget();
		Role role = utter.getRole();
		if(!coListMap.containsKey(role)){
			return;
		}
		List<Agent> coList = coListMap.get(role);
		if(!coList.contains(target) && target == owner){
			coList.add(target);
		}
		Map<Agent, List<Judge>> judgeList = judgeMap.get(role);
		if(judgeList != null) {
			judgeList.put(target, new ArrayList<Judge>());
		}
		
		// 狼の場合は他人からCOされた場合には別のリストに格納しておく
		if(role == Role.WEREWOLF) {
			if(!wolfCalledList.contains(target)) {
				wolfCalledList.add(target);
			}
		}
	}
	
	void parseJudge(Agent agent, Utterance utter, Map<Agent, List<Judge>> judgeMap){
		Judge judge = new Judge(currentDay, agent, utter.getTarget(), utter.getResult());
		if(!judgeMap.containsKey(agent)) {
			judgeMap.put(agent, new ArrayList<Judge>());
		}
		List<Judge> judgeList = judgeMap.get(agent);
		if(!judgeList.contains(judge)){
			judgeList.add(judge);
		}
	}
	
	void parseVotePlan(Agent agent, Utterance utter) {
		List<Vote> votePlan = votePlanList.get(currentDay);
		Vote vote = new Vote(currentDay, agent, utter.getTarget());
		
		for(Vote plan : votePlan) {
			if(plan.getAgent() == agent){
				votePlan.remove(plan);
				break;
			}
		}
		votePlan.add(vote);
	}

	interface ITalkParser {
		public void parse(Talk talk);
	}
	
	class TalkParser implements ITalkParser {
		@Override
		public void parse(Talk talk) {
			parseTalk(talk);
		}
	}
	
	class WhisperParser implements ITalkParser {
		@Override
		public void parse(Talk talk) {
			parseWhisper(talk);
		}
	}

	void parseAttackSuggestion(Agent owner, int ownerTalkId, Utterance uttr, Map<Integer, ISuggestion> suggestions) {
		ISuggestion.Action action = new ISuggestion.Action(owner, uttr.getTarget(), ISuggestion.ActionTypes.Attack);
		ISuggestion suggestion = new Suggestion(owner, action);
		suggestions.put(ownerTalkId, suggestion);
	}

	void parseVoteSuggestion(Agent owner, int ownerTalkId, Utterance uttr, Map<Integer, ISuggestion> suggestions) {
		ISuggestion.Action action = new ISuggestion.Action(owner, uttr.getTarget(), ISuggestion.ActionTypes.Vote);
		ISuggestion suggestion = new Suggestion(owner, action);
		suggestions.put(ownerTalkId, suggestion);
	}
	
	void parseAgree(Agent owner, Utterance uttr, Map<Integer, ISuggestion> suggestions) {
		int targetId = uttr.getTalkID();
		ISuggestion targetSuggestion = suggestions.get(targetId);
		if(targetSuggestion != null) {
			targetSuggestion.agree(owner);
		}
	}

	void parseDisagree(Agent owner, Utterance uttr, Map<Integer, ISuggestion> suggestions) {
		int targetId = uttr.getTalkID();
		ISuggestion targetSuggestion = suggestions.get(targetId);
		if(targetSuggestion != null) {
			targetSuggestion.disagree(owner);
		}
	}
	
	void parseWhisper(Talk talk) {
		Utterance utter = new Utterance(talk.getContent());
		Agent t = talk.getAgent();
		if(!buddyWolves.contains(t)) {
			buddyWolves.add(t);
		}
		switch(utter.getTopic()){
		case COMINGOUT:
			break;
		case AGREE:
			parseAgree(talk.getAgent(), utter, wolfSuggestions.get(currentDay));
			break;
		case ATTACK:
			parseAttackSuggestion(talk.getAgent(), 
					talk.getIdx(), utter, wolfSuggestions.get(currentDay));
			break;
		case DISAGREE:
			parseDisagree(talk.getAgent(), utter, wolfSuggestions.get(currentDay));
			break;
		case DIVINED:
			break;
		case ESTIMATE:
			break;
		case GUARDED:
			break;
		case INQUESTED:
			break;
		case OVER:
			break;
		case SKIP:
			break;
		case VOTE:
			parseVoteSuggestion(talk.getAgent(), 
					talk.getIdx(), utter, wolfSuggestions.get(currentDay));
			break;
		default:
			break;
		}
	}
	
	void parseTalk(Talk talk) {
		Utterance utter = new Utterance(talk.getContent());
		switch(utter.getTopic()){
		case COMINGOUT:
			parseComingOut(talk.getAgent(), utter);
			break;
		case AGREE:
			break;
		case ATTACK:
			break;
		case DISAGREE:
			break;
		case DIVINED:
			parseJudge(talk.getAgent(), utter, divineList);
			break;
		case ESTIMATE:
			break;
		case GUARDED:
			parseJudge(talk.getAgent(), utter, guardedList);
			break;
		case INQUESTED:
			parseJudge(talk.getAgent(), utter, inquestList);
			break;
		case OVER:
			break;
		case SKIP:
			break;
		case VOTE:
			parseVotePlan(talk.getAgent(), utter);
			break;
		default:
			break;
		}
	}
	
	void updateTalk(IGameInfo info, List<List<Talk>> targetTalkList, List<Talk> talks, ITalkParser parser){
		List<Talk> savedTalks = targetTalkList.get(currentDay);
		List<Talk> latestTalks = talks;
		if(latestTalks.isEmpty()) {
			return;
		}
		
		for(int i = savedTalks.size(); i < latestTalks.size(); ++i) {
			Talk talk = latestTalks.get(i);
			parser.parse(talk);
			savedTalks.add(talk);
		}
	}
	
	void printAgentList(String subject, List<Agent> agents){
		DebugLog.log("%s: ", subject);
		for(Agent a : agents) {
			if(a == null) {
				DebugLog.log("-- ");
			} else {
				DebugLog.log("%2d ", a.getAgentIdx());
			}
		}
		DebugLog.log("\n");
	}
	
	void printJudgeList(Map<Agent, List<Judge>> judgeList) {
		for(Map.Entry<Agent, List<Judge>> e : judgeList.entrySet()){
			DebugLog.log("  %2d: ", e.getKey().getAgentIdx());
			for(Judge judge : e.getValue()){
				DebugLog.log("%2d%s ", judge.getTarget().getAgentIdx(), 
						judge.getResult() == Species.HUMAN ? "o" : "x");
			}
			DebugLog.log("\n");
		}
	}
	
	// --------------------------------------------------------------------
	
	public Analyzer(GameSetting setting){
		this.setting = setting;
	}
	
	@Override
	public void update(IGameInfo info) {
		updateDay(info);
		updateTalk(info, talkList, info.getTalkList(), talkParser);
		updateTalk(info, whisperList, info.getWhisperList(), whisperParser);
		latestInfo = info;
		
		
	}	
	
	@Override
	public void Dump(){
		if(!DebugLog.isEnable()) {
			return;
		}
		DebugLog.log("*** Analyzer Dump ***\n");
		DebugLog.log("Day: %d\n", currentDay);
		DebugLog.log("AliveAgentCount   : %2d\n", getAliveCount());
		DebugLog.log("RestExecutionCount: %2d\n", getRestExecutionCount());
		DebugLog.log("NakedEnemyCount: %2d\n", getNakedEnemyCount());
		DebugLog.log("\n");
		
		printAgentList("Alive   ", latestInfo.getAliveAgentList());
		printAgentList("Executed", executedList);
		printAgentList("Killed  ", killedList);
		DebugLog.log("\n");
		printAgentList("Seer     ", seerList);
		printAgentList("Medium   ", mediumList);
		printAgentList("Bodyguard", bodyguardList);
		printAgentList("Crazy    ", possessedsList);
		DebugLog.log("\n");		
		
		DebugLog.log("Divines:\n");
		printJudgeList(divineList);
		
		DebugLog.log("Inquests:\n");
		printJudgeList(inquestList);
		
		
		printAgentList("Gray", getGrayAgents());
		Map<Agent, List<Agent>> grayBySeers = getGrayAgentsBySeerSide();
		for(Map.Entry<Agent, List<Agent>> entry : grayBySeers.entrySet()) {
			printAgentList(String.format("%2d ", entry.getKey().getAgentIdx()), entry.getValue());
		}
		
		if(wolfSuggestions.get(currentDay).size() > 0) {
			DebugLog.log("WolfSuggestions\n");
			wolfSuggestions.get(currentDay).forEach((id, s)->{
				DebugLog.log("id: %d ", id);
				s.dump();
			});
		}
	}
	
	public int getRestExecutionCount() {
		return (latestInfo.getAliveAgentList().size() - 1) / 2;
	}
	
	public int getAliveCount() {
		return latestInfo.getAliveAgentList().size();
	}
	
	private List<Agent> getGrayAgents(List<Agent> divineApplySeers, 
			List<Agent> seers, List<Agent> mediums, List<Agent> bodyGuards,
			List<Agent> crazies) {
		List<Agent> agents = new ArrayList<Agent>();
		agents.addAll(latestInfo.getAliveAgentList());
		
		agents.removeAll(seers);
		agents.removeAll(mediums);
		agents.removeAll(bodyGuards);
		agents.removeAll(crazies);		
		
		for(Agent seer : divineApplySeers){
			List<Judge> judges = divineList.get(seer);
			if(judges == null) {
				continue;
			}
			for(Judge judge : judges){
				agents.remove(judge.getTarget());
			}
		}
		
		return agents;
	}
	
	@Override
	public List<Agent> getSeers() {
		return seerList;
	}
	
	@Override
	public List<Agent> getMediums() {
		return mediumList;
	}
	
	@Override
	public List<Agent> getBodyguards() {
		return bodyguardList;
	}
	
	@Override
	public List<Agent> getWolves() {
		return wolfList;
	}
	
	List<Agent> getVoteAgentList(Agent target, List<Vote> votes) {
		List<Agent> voteAgents = new ArrayList<Agent>();
		for(Vote vote : votes){
			if(vote.getTarget() == target){
				voteAgents.add(vote.getAgent());
			}
		}
		return voteAgents;
	}
	
	/**
	 * �Ώۂ̃G�[�W�F���g�ɑO�����[�����G�[�W�F���g�̃��X�g��Ԃ�
	 * @param target
	 * @return target �ɓ��[�����G�[�W�F���g�̃��X�g
	 */
	public List<Agent> getVotedAgentList(Agent target) {
		return getVoteAgentList(target,  voteList.get(currentDay));
	}
	
	/**
	 * �Ώۂ̃G�[�W�F���g�ɓ��[�\��̃G�[�W�F���g�̃��X�g��Ԃ�
	 * @param target
	 * @return
	 */
	public List<Agent> getWillVoteAgentList(Agent target) {
		return getVoteAgentList(target,  votePlanList.get(currentDay));
	}
	
	@Override
	public int getNakedEnemyCount() {
		int fakeSeerCount = seerList.size() > 1 ? seerList.size() - 1 : 0;
		int fakeMediumCount = mediumList.size() > 1 ? mediumList.size() - 1 : 0;
		int fakeBodyGuardCount = bodyguardList.size() > 1 ? bodyguardList.size() -1 : 0;
		
		return fakeSeerCount + fakeMediumCount + fakeBodyGuardCount;
	}
	
	public int getWolfCount() {
		return setting.getRoleNum(Role.WEREWOLF);
	}
	
	public int getEnemyCount() {
		return setting.getRoleNum(Role.WEREWOLF) +
				setting.getRoleNum(Role.POSSESSED);
	}
	
	public List<Agent> getAliveList(List<Agent> agents) {
		List<Agent> aliveList = new ArrayList<Agent>();
		for(Agent agent : agents) {
			if(latestInfo.getAliveAgentList().contains(agent)) {
				aliveList.add(agent);
			}
		}
		return aliveList;
	}
	
	public int getCurrentDay() {
		return currentDay;
	}
	
	public Agent getMinimumSuspectedAgent(List<Agent> agentList) {
		return getMinimumSuspectedAgent(agentList, null);
	}

	public Agent getMinimumSuspectedAgent(List<Agent> agentList, Agent expected) {
		Map<Agent, Integer> suspectMap = new HashMap<Agent, Integer>();
		List<Vote> votePlan = votePlanList.get(currentDay);
		for(Vote vote : votePlan) {
			if(vote.getAgent() == expected){
				continue;
			}
			Agent agent = vote.getTarget();
			if(!suspectMap.containsKey(agent)) {
				suspectMap.put(agent, 0);
			}
			suspectMap.put(agent, suspectMap.get(agent) + 1);
		}
		
		Agent minimumSuspect = null;
		int minimumCount = 99;
		for(Agent agent : agentList) {
			if(!suspectMap.containsKey(agent)) {
				return agent;
			}
			
			int count = suspectMap.get(agent);
			if(count < minimumCount) {
				minimumCount = count;
				minimumSuspect = agent;
			}
		}
		return minimumSuspect;
	}
	
	
	public List<Agent> getWolfListBySeer(Agent seer) {
		try{
			List<Judge> divines = divineList.get(seer);
			List<Agent> wolfList = new ArrayList<Agent>();
			for(Judge judge : divines){
				if(judge.getResult() == Species.WEREWOLF) {
					wolfList.add(judge.getTarget());
				}
			}
			return wolfList;
		}catch(Exception e) {
			return new ArrayList<Agent>();
		}
	}
	
	@Override
	public List<Agent> getAliveWolfListBySeerSide(Agent seer) {
		try{
			List<Agent> wolfList = getWolfListBySeer(seer);
			List<Agent> aliveWolves = new ArrayList<Agent>();
			
			for(Agent wolf : wolfList) {
				if(latestInfo.getAliveAgentList().contains(wolf)){
					aliveWolves.add(wolf);
				}
			}
			return aliveWolves;
		}catch(Exception e) {
			return new ArrayList<Agent>();
		}
	}
	
	public boolean NoComingOut(Agent agent) {
		if(seerList.contains(agent)) {
			return false;
		}
		if(mediumList.contains(agent)) {
			return false;
		}
		if(bodyguardList.contains(agent)) {
			return false;
		}
		return true;
	}
	
	public List<Judge> getDivineResultBySeer(Agent seer) {
		List<Judge> judges = divineList.get(seer);
		if(judges == null) {
			judges = new ArrayList<Judge>();
		}
		return judges;
	}
	
	public List<Judge> getInquestResultByMedium(Agent medium) {
		List<Judge> judges = inquestList.get(medium);
		if(judges == null) {
			judges = new ArrayList<Judge>();
		}
		return judges;
	}

	public Agent getLatestExecutedAgent() {
		return latestInfo.getExecutedAgent();
	}

	@Override 
	public int getDay() {
		return latestInfo.getDay();
	}
	
	@Override
	public IGameInfo getLatestGameInfo() {
		return latestInfo;
	}


	@Override
	public List<Agent> getAliveAgents() {
		return latestInfo.getAliveAgentList();
	}

	@Override
	public List<Agent> getGrayAgents() {
		return getGrayAgents(seerList, seerList, mediumList, bodyguardList, possessedsList);
	}
	
	@Override
	public List<Agent> getGrayAgentsBySeerSide(Agent seer) {
		List<Agent> gray = getGrayAgentsBySeerSide().get(seer);
		if(gray == null) {
			return getGrayAgents();
		} else {
			return gray;
		}
	}

	@Override
	public Map<Agent, List<Agent>> getGrayAgentsBySeerSide() {
		HashMap<Agent, List<Agent>> maps = new HashMap<Agent, List<Agent>>();
		for(Agent seer : seerList){
			List<Agent> divineApply = new ArrayList<Agent>();
			divineApply.add(seer);
			maps.put(seer, getGrayAgents(divineApply, seerList, mediumList, bodyguardList, possessedsList));
		}
		return maps;
	}

	@Override
	public List<Agent> getVotedAgent(Agent target) {
		return getVoteAgentList(target,  voteList.get(currentDay));
	}

	@Override
	public List<Agent> getVotedAgent(int day, Agent target) {
		return getVoteAgentList(target,  voteList.get(day));
	}

	@Override
	public List<Vote> getVotePlan() {
		return getVotePlan(currentDay);
	}
	
	@Override
	public List<Vote> getVotePlan(int day) {
		return votePlanList.get(day);
	}

	@Override
	public Vote getVotePlan(Agent agent) {
		return getVotePlan(currentDay, agent);
	}

	@Override
	public Vote getVotePlan(int day, Agent agent) {
		List<Vote> plan = votePlanList.get(day);
		for(Vote vote : plan) {
			if(vote.getAgent() == agent) {
				return vote;
			}
		}
		return null;
	}

	@Override
	public GameSetting getSetting() {
		return this.setting;
	}

	@Override
	public List<Agent> getAttackedAgents() {
		return killedList;
	}

	@Override
	public Map<Agent, List<Judge>> getDivineResults() {
		return divineList;
	}

	@Override
	public Map<Agent, List<Judge>> getInquestResults() {
		return inquestList;
	}

	@Override
	public List<Agent> getExecutedAgents() {
		return executedList;
	}

	@Override
	public List<Agent> getCoAgents() {
		List<Agent> agents = new ArrayList<Agent>();
		agents.addAll(this.seerList);
		agents.addAll(this.mediumList);
		agents.addAll(this.wolfList);
		agents.addAll(this.bodyguardList);
		agents.addAll(this.possessedsList);
		return agents;
	}
	
	@Override
	public boolean hasExtraExecution() {
		int restExeCount = getRestExecutionCount();
		int enemyCount = getEnemyCount();
		return restExeCount > enemyCount;
	}

	@Override
	public List<Agent> getBuddyWolves() {
		return buddyWolves;
	}
	
	@Override
	public List<Agent> getAgents() {
		return latestInfo.getAgentList();
	}
	
	@Override
	public List<Talk> getTalks() {
		return talkList.get(currentDay);
	}

	@Override
	public List<Agent> getPossesseds() {
		return possessedsList;
	}

	@Override
	public List<Agent> getAliveBuddyWolves() {
		List<Agent> wolves = new LinkedList<Agent>();
		wolves.addAll(getBuddyWolves());
		wolves.removeIf(wolf->{
			return !getLatestGameInfo().getAliveAgentList().contains(wolf);
		});
		return wolves;
	}

	@Override
	public Map<Integer, ISuggestion> getWolfSuggestions() {
		return wolfSuggestions.get(currentDay);
	}

	@Override
	public Map<Integer, ISuggestion> getHumanSuggestions() {
		return humanSuggestions.get(currentDay);
	}
}
