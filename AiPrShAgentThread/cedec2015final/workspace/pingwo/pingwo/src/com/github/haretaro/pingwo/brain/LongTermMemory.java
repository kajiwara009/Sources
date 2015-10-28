package com.github.haretaro.pingwo.brain;

import static java.util.Comparator.comparing;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.aiwolf.client.lib.Topic;
import org.aiwolf.client.lib.Utterance;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Species;
import org.aiwolf.common.data.Talk;
import org.aiwolf.common.data.Vote;
import org.aiwolf.common.net.GameInfo;

import com.github.haretaro.pingwo.brain.LongTermPlayerInfo.State;
import com.github.haretaro.pingwo.brain.util.MyCollectors;
import com.github.haretaro.pingwo.brain.util.Util;

/**
 * 長期的な記憶の管理をするクラス
 * @author Haretaro
 *
 */
public class LongTermMemory {
	private Map<Agent,LongTermPlayerInfo> playerInfo;
	private List<Agent> seerCOAgents;
	private List<Agent> mediumCOAgents;
	private List<Agent> expectedTargets;
	protected List<Agent> denyList;//投票から外すエージェントのリスト
	protected final Agent me;
	protected GameInfo gameInfo;
	
	public LongTermMemory(GameInfo gameInfo){
		this.gameInfo = gameInfo;
		playerInfo = new HashMap<Agent,LongTermPlayerInfo>();
		List<Agent> agents = gameInfo.getAgentList();
		agents.stream()
		.forEach(agent->playerInfo.put(agent, new LongTermPlayerInfo(agent,agents)));
		me = gameInfo.getAgent();
		seerCOAgents = new ArrayList<Agent>();
		mediumCOAgents = new ArrayList<Agent>();
		expectedTargets = new ArrayList<Agent>();
		denyList = new ArrayList<Agent>();
		denyList.add(me);
	}
	
	public void dayStart(GameInfo gameInfo){
		this.gameInfo = gameInfo;
		
		gameInfo.getAgentList()
			.stream()
			.map(a->playerInfo.get(a))
			.forEach(a->Util.printout(""+a.getAgent().toString() + " reliability" + a.getReliability()));
			
		List<Vote> voteList = gameInfo.getVoteList();
		voteList.stream().forEach(a->playerInfo.get(a.getAgent()).votedTo(a.getTarget()));
		
		//投票先が同じなら繋がりがあると考える
		/*
		List<Agent> targetList = new ArrayList<Agent>();
		voteList.stream()
			.map(a->a.getTarget())
			.forEach(a->{
				if(targetList.contains(a) == false){
					targetList.add(a);
				}
			});
		targetList.stream()
			.forEach(target->{
				List<Agent> sameVoteGroupe = gameInfo
					.getAliveAgentList()
					.stream()
					.map(a->playerInfo.get(a))
					.filter(a->a.getLatestVote().equals(target))
					.map(a->a.getAgent())
					.collect(Collectors.toList());
				sameVoteGroupe.stream()
					.map(a->playerInfo.get(a))
					.forEach(a->a.addConnectionScores(sameVoteGroupe,3d/sameVoteGroupe.size()));
			});
			*/
		

		Optional<Agent> attackedAgent = Optional.ofNullable(gameInfo.getAttackedAgent());
		attackedAgent.ifPresent(a->{
			playerInfo.get(a).attacked();
			if(expectedTargets.contains(a)){
				expectedTargets.remove(a);
			}
		});
		
		Optional<Agent> executedAgent = Optional.ofNullable(gameInfo.getExecutedAgent());
		executedAgent.ifPresent(a->{
			playerInfo.get(a).executed();
			if(expectedTargets.contains(a)){
				expectedTargets.remove(a);
			}
		});
		
		markSuspiciousSeers();
		
		Util.printout("---------expectedtargets---------");
		expectedTargets.stream()
			.forEach(a->Util.printout(a.toString()));
		Util.printout("---------expected end------------");
	}

	private void markSuspiciousSeers() {
		//占い師が噛まれたら他の占い師を疑う
		List<Agent> attackedSeerList = seerCOAgents.stream()
			.map(a->playerInfo.get(a))
			.filter(a->a.getState() == State.ATTACKED)
			.map(a->a.getAgent())
			.collect(Collectors.toList());
		if(attackedSeerList.size() > 0){
			seerCOAgents.stream()
				.filter(a->attackedSeerList.contains(a) == false)
				.map(a->playerInfo.get(a))
				.forEach(a->{
					a.setSuspicion(1d);
					a.finalizeSuspicion();
				});
		}
	}
	
	public void listen(Talk talk){
		Agent speaker = talk.getAgent();
		Utterance utterance = new Utterance(talk.getContent());
		
		//発言回数を記録
		if(utterance.getTopic() != Topic.OVER
				&& utterance.getTopic() != Topic.SKIP){
			playerInfo.get(speaker).incrementTalkCount();
		}
		
		if(utterance.getTopic() == Topic.COMINGOUT
				&& utterance.getRole() == Role.SEER
				&& seerCOAgents.contains(speaker)==false){
			seerCOAgents.add(speaker);
			double suspicionValue;
			if(seerCOAgents.contains(me)){
				suspicionValue = 1d;
			}else{
				suspicionValue = 1d - 1d/seerCOAgents.size();
			}
			seerCOAgents.stream()
			.filter(a->a.equals(me) == false)
			.forEach(a->playerInfo.get(a).setSuspicion(suspicionValue));
			playerInfo.get(speaker).COSeer();

			markSuspiciousSeers();
			seerRoller();
			
		}
		
		if(utterance.getTopic() == Topic.COMINGOUT
				&& utterance.getRole() == Role.MEDIUM
				&& mediumCOAgents.contains(speaker)==false){
			mediumCOAgents.add(speaker);
			double suspicionValue;
			if(mediumCOAgents.contains(me)){
				suspicionValue = 1d;
			}else{
				suspicionValue = 1d - 1d/mediumCOAgents.size();
			}
			mediumCOAgents.stream()
			.filter(a->a.equals(me) == false)
			.forEach(a->playerInfo.get(a).setSuspicion(suspicionValue));
			mediumRoller();
		}
		
		if(utterance.getTopic() == Topic.DIVINED
				&& seerCOAgents.contains(speaker)
				&& utterance.getResult() == Species.HUMAN){
			Agent divined = utterance.getTarget();
			playerInfo.get(speaker).divinedHuman(divined);
		}
		
		//投票すると宣言したエージェントとの関係度を下げる
		if(utterance.getTopic() == Topic.VOTE){
			Agent target = utterance.getTarget();
			playerInfo.get(speaker).addConnectionScore(target, 0);
		}
	}

	public void update(GameInfo gameInfo) {
		this.gameInfo = gameInfo;

		gameInfo.getAgentList().stream()
			.forEach(a->updateSuspicion(a));
		
		seerCOAgents.stream()
			.filter(a -> getReliabilityOf(a) > 0.4 )
			.filter(a -> denyList.contains(a)==false)
			.forEach(a -> denyList.add(a));
		
		seerCOAgents.stream()
			.filter(a -> getReliabilityOf(a) < 0.4)
			.filter(a -> denyList.contains(a))
			.forEach(a -> denyList.remove(a));
		
		if(denyList.contains(me) == false){
			denyList.add(me);
		}
		
		findBadGuys();
		//findEnemiesOfWhites();
	}
	
	private void updateSuspicion(Agent agent){
		//占い師が全員黒だししてたら黒
		Optional<Boolean> allBlack = seerCOAgents.stream()
				.map(a->playerInfo.get(a).doesDivinedWolf(agent))
				.reduce((a,b) -> Boolean.logicalAnd(a, b));
		allBlack.ifPresent(b->{
			if(b){
				playerInfo.get(agent).setSuspicion(1d);
			}
		});
		
		//占い師が全員白だししてたら白
		Optional<Boolean> allWhite = seerCOAgents.stream()
				.map(a->playerInfo.get(a).doesDivinedHuman(agent))
				.reduce((a,b) -> Boolean.logicalAnd(a, b));
		allWhite.ifPresent(b->{
			if(b){
				playerInfo.get(agent).setSuspicion(0d);
			}
		});
		
		//信頼できる占い師の占いを信じる
		seerCOAgents.stream()
			.map(a->playerInfo.get(a))
			.filter(a -> a.getReliability() > 0.9)
			.forEach(a->{
				if(a.doesDivinedHuman(agent)){
					playerInfo.get(agent).setSuspicion(0d);
				}
				if(a.doesDivinedWolf(agent)){
					playerInfo.get(agent).setSuspicion(1d);
				}
			});
		
		if(playerInfo.get(agent).getReliability() > 0.9
				&& playerInfo.get(agent).isAlive()
				&& denyList.contains(agent)==false){
			denyList.add(agent);
		}
		
		if(playerInfo.get(agent).getReliability() < 0.6
				&& denyList.contains(agent)){
			denyList.remove(agent);
		}
		
		if(playerInfo.get(agent).getReliability() < 0.2
				&& playerInfo.get(agent).isAlive()
				&& expectedTargets.contains(agent)==false){
			expectedTargets.add(agent);
			Util.printout(Reason.TOO_SUSPICOUS.toString());
		}
		
	}

	//霊媒師二人以上ならローラー（全吊り）。全員吊り希望リストへ
	private void mediumRoller(){
		if(mediumCOAgents.size() > 1){
			mediumCOAgents.stream()
			.filter(a->a.equals(me) == false)
			.filter(a->expectedTargets.contains(a)==false)
			.filter(a->playerInfo.get(a).isAlive())
			.forEach(a->expectedTargets.add(a));
			Util.printout(Reason.MEDIUM_ROLLER.toString());
		}
	}
	
	private void seerRoller(){
		if(seerCOAgents.size() > 2){
			seerCOAgents.stream()
				.filter(a->a.equals(me) == false)
				.filter(a->expectedTargets.contains(a)==false)
				.filter(a->playerInfo.get(a).isAlive())
				.forEach(a->expectedTargets.add(a));
			Util.printout(Reason.SEER_ROLLER.toString());
		}
	}
	
	public Optional<Agent> getExpectedTarget(){
		if(hasExpectedTarget()){
			Optional<Agent> target = expectedTargets.stream()
					.map(a->playerInfo.get(a))
					.sorted(comparing(LongTermPlayerInfo::getReliability))
					.findFirst()
					.map(a->a.getAgent());
			return target;
		}
		
		List<Agent> candidates = gameInfo.getAliveAgentList()
				.stream()
				.filter(a->denyList.contains(a)==false)
				.collect(Collectors.toList());
		return getLeastReliableAgentOf(candidates);
	}
	
	public Optional<Agent> getMostReliableSeer(){
		Map<Double, List<LongTermPlayerInfo>> map = seerCOAgents.stream()
			.map(a->playerInfo.get(a))
			.filter(a->a.isAlive())
			.collect(Collectors.groupingBy( a -> a.getReliability()));
		
		Optional<Double> max = map.keySet().stream()
			.max(Comparator.naturalOrder());
		
		if(max.isPresent()){
			return map.get(max.get()).stream()
					.map(a -> a.getAgent())
					.collect(MyCollectors.collectRandomly);
		}else{
			return Optional.ofNullable(null);
		}
	}
	
	public List<Agent> getAliveMediums(){
		return mediumCOAgents.stream()
				.map(a->playerInfo.get(a))
				.filter(a->a.isAlive())
				.map(a->a.getAgent())
				.collect(Collectors.toList());
	}
	
	public Optional<Agent> getLeastReliableAgentOf(List<Agent> agents){
		return agents.stream()
			.filter(a->a.equals(me)==false)
			.map(a->playerInfo.get(a))
			.sorted(comparing(LongTermPlayerInfo::getReliability))
			.findFirst()
			.map(a->a.getAgent());
	}
	
	public boolean hasExpectedTarget(){
		return expectedTargets.size() > 0;
	}
	
	public List<Agent> getExpectedTargets(){
		return expectedTargets;
	}
	
	public boolean isReliable(Agent agent){
		return playerInfo.get(agent).getReliability() > 0.45;
	}
	
	public double getReliabilityOf(Agent agent){
		assert(agent!=null);
		return playerInfo.get(agent)
				.getReliability();
	}
	
	public List<Agent> getDenyList(){
		return denyList;
	}
	
	public List<Agent> getMediumCOAgents(){
		return mediumCOAgents;
	}
	
	public Agent getMostReliableAgent(){
		return getMostReliableAgentOf(gameInfo.getAliveAgentList());
	}
	
	public Agent getMostReliableAgentOf(List<Agent> list){
		assert(list.size() > 0);
		Agent result = list.stream()
			.filter(a->a.equals(me) == false)
			.map(a->playerInfo.get(a))
			.sorted(comparing(LongTermPlayerInfo::getReliability).reversed())
			.findFirst()
			.map(a->a.getAgent())
			.orElse(null);
		assert(result!=null);
		return result;
	}
	
	public void setSuspicion(Agent agent, double value){
		assert(0 <= value && value <= 1);
		playerInfo.get(agent).setSuspicion(value);
	}
	
	public void finalizeSuspicion(Agent agent){
		playerInfo.get(agent).finalizeSuspicion();
	}
	
	/**
	 * 黒確のプレイヤーと関係のあるプレイヤーを探す
	 */
	public void findBadGuys(){
		List<Agent> badGuys = gameInfo
				.getAgentList()
				.stream()
				.map(a->playerInfo.get(a))
				.filter(a->a.getReliability() < 0.1)
				.map(a->a.getAgent())
				.collect(Collectors.toList());
		

		gameInfo.getAgentList().stream()
			.map(a->playerInfo.get(a))
			.forEach(a->a.resetBadFriendScore());
		
		badGuys.stream()
			.map(a->playerInfo.get(a))
			.forEach(aBadGuy->{
				gameInfo.getAgentList().stream()
					.filter(a->a.equals(aBadGuy) == false)
					.forEach(agent->{
						Util.printout(""+aBadGuy.getAgent()+"connection to "+agent+":"+aBadGuy.connectionTo(agent));
						playerInfo.get(agent).addBadFriendScore((aBadGuy.connectionTo(agent) - 0.5)*10);
					});
			});
	}
	
	/**
	 * agentに対して否定的なプレイヤーを見つける
	 * @param agent
	 */
	public void findEnemy(Agent agent){
		gameInfo.getAgentList()
			.stream()
			.map(a->playerInfo.get(a))
			.forEach(a->{
				Util.printout("connection "+a.getAgent()+" to " + agent + ":" + a.connectionTo(agent));
			});
	}
	
	public void findEnemiesOfWhites(){
		gameInfo.getAgentList()
			.stream()
			.filter(a->this.getReliabilityOf(a)>0.9)
			.forEach(a -> findEnemy(a));
	}
}
