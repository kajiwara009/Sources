/**
 * 
 */
package com.gmail.yusatk.plans;

import java.util.*;

import org.aiwolf.client.lib.TemplateTalkFactory.TalkType;
import org.aiwolf.common.data.*;

import com.gmail.yusatk.data.World;
import com.gmail.yusatk.interfaces.*;
import com.gmail.yusatk.talks.EstimateTalk;
import com.gmail.yusatk.talks.VoteTalk;
import com.gmail.yusatk.utils.DebugLog;
import com.gmail.yusatk.utils.TimeWatcher;

/**
 * @author Yu
 *
 */
public class OneSiteTrackPlan extends DefaultBattlePlan {
	IAgentEx owner = null;
	IAnalyzer analyzer = null;
	
	IWorld assumedWorld;
	IWorldCache worldCache;
	
	Agent votePlan = null;
	
	List<IWorld> makeCombinations(List<IWorld> baseWorlds, Role[] roles, List<Agent> agents) {
		List<IWorld> worlds = new LinkedList<IWorld>();
		worlds.addAll(baseWorlds);
		for(Agent agent : agents){
			int count = worlds.size();
			for(int i = 0; i < count; ++ i) {
				IWorld baseWorld = worlds.get(0);
				worlds.remove(0);
				for(Role role : roles) {
					IWorld w = baseWorld.clone();
					w.setRole(agent, role);
					worlds.add(w);
				}
			}
		}
		return worlds;
	}
	
	private List<IWorld> generateWorldCandidates(Role myRole) {
		List<IWorld> worlds = new LinkedList<IWorld>();
		List<Agent> seers = analyzer.getSeers();
		List<Agent> mediums = analyzer.getMediums();
		List<Agent> wolves = analyzer.getWolves();
		
		Role [] mediumRoles = {
				Role.MEDIUM,
				Role.WEREWOLF,
				Role.POSSESSED,
		};
		Role [] seersRoles = {
				Role.SEER,
				Role.WEREWOLF,
				Role.POSSESSED,
		};
		Role [] wolfRoles = {
				Role.WEREWOLF,
				Role.POSSESSED,
		};
		
		List<IWorld> worldCandidates = new LinkedList<IWorld>();
		World blank = new World();
		blank.setup(analyzer);
		worldCandidates.add(blank);
		// 通常ありえないが、露出数が多い場合は処理が終わらないのではぶく。
		// このような状況でまともに思考させることは不可能
		if(seers.size() > 4) {
			seers = seers.subList(0, 3);
		}
		if(mediums.size() > 4) {
			mediums = mediums.subList(0, 3);
		}
		if(wolves.size() > 3) {
			wolves = wolves.subList(0, 2);
		}
		worldCandidates = makeCombinations(worldCandidates, seersRoles, seers);
		worldCandidates = makeCombinations(worldCandidates, mediumRoles, mediums);
		worldCandidates = makeCombinations(worldCandidates,	wolfRoles, wolves);
		
		for(IWorld w : worldCandidates) {
			if(w.isValidWorld()) {
				worlds.add(w);
			}
		}

		// 自分が役職の場合は、仮定世界をさらに限定する
		Role [] ownerRoles = {
				Role.BODYGUARD,
				Role.MEDIUM,
				Role.SEER,
		};
		
		// 自分の役職に対して他のエージェントが割り当てられている場合は候補から削除する
		for(Role ownerRole : ownerRoles) {
			if(owner.getRole() == ownerRole) {
				worlds.removeIf(w->{
					Agent roled = w.getRoledAgent(ownerRole);
					if(roled != null && roled != owner.getAgent()) {
						return true;
					}
					return false;
				});
			}
		}
		
		DebugLog.log("** VALID WORLD CANDIDATES **\n");
		for(IWorld w : worlds) {
			w.Dump();
		}
		
		return worlds;
	}
	
	public OneSiteTrackPlan(IAgentEx owner, IAnalyzer analyzer, IWorldCache worldCache) {
		this.owner = owner;
		this.analyzer = analyzer;
		this.worldCache = worldCache;
		
		worldCache.setWorlds(
				generateWorldCandidates(owner.getRole()));
	}
	
	private IWorld selectWorld() {
		return worldCache.getWorlds().get(0);
	}
	
	/* (non-Javadoc)
	 * @see com.gmail.yusatk.interfaces.IBattlePlan#planUpdate()
	 */
	@Override
	public IBattlePlan planUpdate(boolean dayStartUpdate) {
		worldCache.getWorlds().removeIf(w->{
			boolean isValid = w.isValidWorld();
			if(!isValid) {
				return true;
			}
			
			// 占い師が自分に黒を出しているケースは不正
			Agent seer = w.getSeer();
			if(seer != null) {
				List<Judge> judges = analyzer.getDivineResultBySeer(seer);
				for(Judge judge : judges) {
					if(judge.getTarget() == owner.getAgent() &&
							judge.getResult() == Species.WEREWOLF) {
						return true;
					}
				}
			}
			return false;
		});
		
		if(worldCache.getWorlds().size() == 0 && !dayStartUpdate) {
			return new RandomPlan(owner, analyzer, worldCache);
		}
		assert owner.getRole() == Role.WEREWOLF || !worldCache.getWorlds().isEmpty() || dayStartUpdate;
		return this;
	}

	/* (non-Javadoc)
	 * @see com.gmail.yusatk.interfaces.IBattlePlan#getTalkPlan(int)
	 */
	@Override
	public Queue<ITalkEvent> getTalkPlan(int restTalkCount) {
		TimeWatcher timeWatcher = owner.getTimeWatcher();
		Queue<ITalkEvent> talks = new LinkedList<ITalkEvent>();
		IWorld oldWorld = assumedWorld;
		IWorld newWorld = selectWorld();

		if(oldWorld != newWorld) {
			assumedWorld = newWorld;
			Map<Agent, Role> roleMap = newWorld.getRoles();
			for(Map.Entry<Agent, Role> roles : roleMap.entrySet()) {
				Role role = roles.getValue();
				Agent agent = roles.getKey();
				if(role == Role.SEER || role == Role.MEDIUM ||
						role == Role.POSSESSED ) {
					talks.add(new EstimateTalk(agent, role));
				}
			}
			
		}
		if(!timeWatcher.hasExtraTime()) {
			return talks;
		}
		
		Agent oldVotePlan = votePlan;
		Agent newVotePlan = getVotePlan();
		if(oldVotePlan != newVotePlan) {
			votePlan = newVotePlan;
			talks.add(new VoteTalk(TalkType.TALK, newVotePlan));
		}
		
		return talks;
	}

	private Agent selectVoteFromWolfCandidates(List<Agent> candidates) {
		// 決め打ち視点の評価
		IScoreMap oneSiteEvaluation = GrayEvaluationHelper.evaluateForOneSite(analyzer, this.assumedWorld, candidates, owner.getTimeWatcher());
		return oneSiteEvaluation.getAgentsByDesc().get(0);
	}
	
	/* (non-Javadoc)
	 * @see com.gmail.yusatk.interfaces.IBattlePlan#getVotePlan()
	 */
	@Override
	public Agent getVotePlan() {
		if(this.assumedWorld == null) {
			DebugLog.log("*** ASSUMED WORLD IS NOT EXIST ***\n");
			return null;
		}
		DebugLog.log("** CURRENT ASSUMED WORLD **\n");
		this.assumedWorld.Dump();
//		this.assumedWorld.DumpWolves();

		List<Agent> wolfCandidates = assumedWorld.getWolfCandidates();
		List<Agent> aliveWolves = assumedWorld.getAliveWolves();
//		List<Agent> deadWolves = assumedWorld.getDeadWolves();
		List<Agent> wolves = assumedWorld.getWolves();
		int maxWolfCount = analyzer.getSetting().getRoleNumMap().get(Role.WEREWOLF);
//		int restWolfCount = maxWolfCount - deadWolves.size();
		if(wolves.size() == maxWolfCount) {
			assert aliveWolves.size() > 0;
			return aliveWolves.get(0);
		}
		
		// TODO: 残り吊りすうや状況に応じてケア吊りを検討する
		if(aliveWolves.size() > 0) {
			return aliveWolves.get(0);
		}
		
		assert wolfCandidates.size() > 0;
		
		return selectVoteFromWolfCandidates(wolfCandidates);
	}

	@Override
	public void dayStart() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Agent getAttackPlan() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Agent getGuardPlan() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Agent getDivinePlan() {
		// TODO Auto-generated method stub
		return null;
	}

}
