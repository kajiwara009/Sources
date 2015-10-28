package com.github.haretaro.pingwo.brain;

import static java.util.Comparator.*;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.aiwolf.client.lib.Topic;
import org.aiwolf.client.lib.Utterance;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Talk;
import org.aiwolf.common.net.GameInfo;

/**
 * 短期記憶
 * 他のプレイヤーの今日の投票先を覚える
 * @author Haretaro
 *
 */
public class ShortTermMemory {
	protected Map<Agent,ShortTermPlayerInfo> playerInfo;//プレイヤー毎の情報
	protected Agent me;

	public ShortTermMemory(GameInfo gameInfo){
		playerInfo = new HashMap<Agent,ShortTermPlayerInfo>();
		gameInfo.getAgentList().stream()
		.forEach(
				agent->{
					playerInfo.put(agent, instantiatePlayerInfo(agent));
				});
		me = gameInfo.getAgent();
		gameInfo.getAliveAgentList().stream()
		.forEach(agent->playerInfo.get(agent).setAive());
	}
	
	public void listen(Talk talk){
		Utterance utterance = new Utterance(talk.getContent());
		Agent speaker = talk.getAgent();
		
		if(utterance.getTopic() == Topic.VOTE){
			Agent target = utterance.getTarget();
			playerInfo.get(speaker).setTarget(target);
		}
		
		if(utterance.getTopic() == Topic.AGREE){
			/*
			Agent sameOpinion = utterance.getTarget();//同意する対象
			Agent voteTarget = playerInfo.get(sameOpinion).getTarget();//投票先
			playerInfo.get(speaker).setTarget(voteTarget);
			*/
		}
		
		if(utterance.getTopic() == Topic.OVER){
			playerInfo.get(speaker).setOver();
		}
	}
	
	//今日の吊り投票の有力候補を返す
	public List<Agent> getPotentialCandidates(){
		countVotes();
		
		Optional<Integer> max = playerInfo.values()
				.stream().map(a->a.getVoteCount())
				.max(Comparator.naturalOrder());
		List<Agent> candidates =  playerInfo.keySet().stream()
		.filter(key -> playerInfo.get(key).getVoteCount() == max.orElse(0))
		.collect(Collectors.toList());
		return candidates;
	}

	private void countVotes() {
		playerInfo.values().stream().forEach(a->a.resetVoteCount());
		playerInfo.values().stream()
		.filter(a->a.getTarget()!=null)
		.filter(a->a.equals(playerInfo.get(me))==false)
		.map(a->a.getTarget())
		.forEach(target -> playerInfo.get(target).incrementVoteCount());
	}
	
	public int getMaxVoteCount(){
		List<Agent> candidates = getPotentialCandidates();
		assert(candidates != null);
		if (candidates.size() == 0){
			return 0;
		}else{
			return playerInfo.get(candidates.get(0)).getVoteCount();
		}
	}
	
	/** @return 全員の会話が終わったかどうか
	 */
	public boolean isTalkOver(){
		long countOfTalkingPlayer = playerInfo.values().stream()
		.filter(a->a.isAlive())
		.filter(a->a.isOver()==false)
		.count();
		return countOfTalkingPlayer < 2;//自分だけまだ喋ってる時,1になる
	}
	
	/**
	 * 与えられたエージェントの中で現在最も得票数が多いエージェントを返す
	 * @param agentList
	 * @return 最も得票数が多いエージェント
	 */
	public Agent whoIsTheMostOf(List<Agent> agentList){
		countVotes();
		Optional<Agent> result = agentList.stream()
				.map(a->playerInfo.get(a))
				.sorted(comparing(ShortTermPlayerInfo::getVoteCount).reversed())
				.findFirst()
				.map(a->a.getAgent());
		return result.orElse(null);
	}
	
	public int getMaxVoteCountOf(List<Agent> agentList){
		countVotes();
		Optional<Integer> count = agentList.stream()
				.map(a->playerInfo.get(a))
				.sorted(comparing(ShortTermPlayerInfo::getVoteCount))
				.findFirst()
				.map(a->a.getVoteCount());
		return count.orElse(0);
	}
	
	protected ShortTermPlayerInfo instantiatePlayerInfo(Agent agent){
		return new ShortTermPlayerInfo(agent);
	}

	public void listenToWhisper(Talk w) {
		assert(false);
	}
	
	public void setWerewolfAgents(List<Agent> wolfs){
		assert(false);
	}
}
