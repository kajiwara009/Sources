package com.gmail.yusatk.plans;

import com.gmail.yusatk.data.ScoreMap;
import com.gmail.yusatk.interfaces.*;
import com.gmail.yusatk.utils.DebugLog;
import com.gmail.yusatk.utils.TimeWatcher;

import java.util.*;

import org.aiwolf.common.data.*;

public class GrayEvaluationHelper {
	private static void evaluateTalk(IScoreMap scoreMap, IAnalyzer analyzer) {
		// 発言をしている場合はポイントダウン
		List<Talk> talks = analyzer.getTalks();
		List<Agent> candidates = scoreMap.getAgents();
		// TODO: 内容の検討
		talks.forEach(talk->{
			if(candidates.contains(talk.getAgent())){
				scoreMap.addScore(talk.getAgent(), -1);
			}
		});
	}
	
	private static void evaluateVote(IScoreMap scoreMap, IAnalyzer analyzer) {
		List<Vote> votePlans = analyzer.getVotePlan();
		List<Agent> coAgents = analyzer.getCoAgents();
		List<Agent> seers = analyzer.getSeers();
		votePlans.forEach(votePlan->{
			// 役職に投票しようとしている人はポイントアップ
			coAgents.forEach(co->{
				if(votePlan.getTarget() == co) {
					scoreMap.addScore(votePlan.getAgent(), 5);
				}
			});
			
			// 白が出ている人に投票しようとしている人はポイントアップ
			seers.forEach(seer->{
				analyzer.getDivineResultBySeer(seer).forEach(judge->{
					if(judge.getTarget() == votePlan.getTarget() &&
							judge.getResult() == Species.HUMAN){
						scoreMap.addScore(votePlan.getAgent(), 5);
					}
				});
			});
		});
	}
	
	private static void evaluateGray(IScoreMap scoreMap, IAnalyzer analyzer) {
		// 完全灰ならポイントアップ
		List<Agent> gray = analyzer.getGrayAgents();
		gray.forEach(agent->{
			scoreMap.addScore(agent, 30);
		});
	}
	
	private static void evaluateBlacked(IScoreMap scoreMap, IAnalyzer analyzer) {
		List<Agent> candidates = scoreMap.getAgents();
		// 黒出されならポイントアップ
		analyzer.getSeers().forEach(seer->{
			List<Judge> judges = analyzer.getDivineResultBySeer(seer);
			judges.forEach(judge->{
				if(candidates.contains(judge.getTarget())) {
					if(judge.getResult() == Species.WEREWOLF) {
						scoreMap.addScore(judge.getTarget(), 50);
					}
				}
			});
		});
	}
	
	// 全視点詰めを前提とした評価を行う
	public static IScoreMap evaluateGrayForAllSite(IAnalyzer analyzer, List<Agent> candidates, TimeWatcher timeWatcher) {
		IScoreMap scoreMap = new ScoreMap();
		candidates.forEach(agent->{
			scoreMap.addAgent(agent, 0);
		});
		
		evaluateBlacked(scoreMap, analyzer);
		if(!timeWatcher.hasExtraTime()) {
			return scoreMap;
		}

		evaluateGray(scoreMap, analyzer);
		if(!timeWatcher.hasExtraTime()) {
			return scoreMap;
		}
		
		evaluateTalk(scoreMap, analyzer);
		if(!timeWatcher.hasExtraTime()) {
			return scoreMap;
		}

		evaluateVote(scoreMap, analyzer);
		if(!timeWatcher.hasExtraTime()) {
			return scoreMap;
		}
		
		// TODO: 黒出しを吊りに行かないならポイントアップ
		// TODO: 投票先が不自然に被っているようならポイントアップ

		
		// 狼COしてるならポイントアップ
		// TODO: 最終日の狂人の狼COは吊ってはいけない
		List<Agent> wolves = analyzer.getWolves();
		wolves.forEach(wolf->{
			if(!candidates.contains(wolf)) {
				candidates.add(wolf);
				scoreMap.addAgent(wolf, 0);
			}
			scoreMap.addScore(wolf, 200);
		});
		if(!timeWatcher.hasExtraTime()) {
			return scoreMap;
		}

		// 狂人COしてるならポイントアップ
		List<Agent> possesseds = analyzer.getPossesseds();
		possesseds.forEach(possessed->{
			if(!candidates.contains(possessed)) {
				candidates.add(possessed);
				scoreMap.addAgent(possessed, 0);
			}
			scoreMap.addScore(possessed, 100);
		});
		
		return scoreMap;
	}
	
	// 決め打ち視点を前提とした評価を行う
	public static IScoreMap evaluateForOneSite(IAnalyzer analyzer, 
			IWorld world, List<Agent> candidates, TimeWatcher timeWatcher) {
		IScoreMap scoreMap = new ScoreMap();
		candidates.forEach(agent->{
			scoreMap.addAgent(agent, 0);
		});
		
		// 既に死亡した狼候補に投票しているならポイントダウン
		List<Agent> deadWolves = world.getDeadWolves();
		List<Agent> executedAgents = analyzer.getExecutedAgents();
		for(Agent wolf : deadWolves) {
			int day = executedAgents.indexOf(wolf);
			List<Agent> wolfKilledAgents = analyzer.getVotedAgent(day, wolf);
			wolfKilledAgents.forEach(agent->{
				if(candidates.contains(agent)) {
					scoreMap.addScore(agent, -2);
				}
			});
			if(!timeWatcher.hasExtraTime()) {
				return scoreMap;
			}
		}
		
		// 決め打ちの占い結果によってポイント増減
		Agent seer = world.getSeer();
		if(seer != null) {
			List<Judge> judges = analyzer.getDivineResultBySeer(seer);
			for(Judge judge : judges){
				Agent target = judge.getTarget();
				if(candidates.contains(target)) {
					if(judge.getResult() == Species.HUMAN) {
						scoreMap.addScore(target, -30);
					} else {
						scoreMap.addScore(target, 30);
					}
				}
			}
		}
		
		return scoreMap;
	}
	
	private static void evaluateWhitedOfRivalSeers(IScoreMap scoreMap, IAnalyzer analyzer, Agent ownerSeer, TimeWatcher timeWatcher) {
		List<Agent> candidates = scoreMap.getAgents();
		
		for(Agent seer : analyzer.getSeers()) {
			if(seer == ownerSeer) {
				continue;
			}
			
			// 対抗の白の優先度を上げて、対抗の黒の優先度を下げる
			List<Judge> judges = analyzer.getDivineResultBySeer(seer);
			for(Judge judge : judges) {
				Agent target = judge.getTarget();
				if(candidates.contains(target) && judge.getResult() == Species.HUMAN) {
					scoreMap.addScore(target, 10);
				}
				if(candidates.contains(target) && judge.getResult() == Species.WEREWOLF) {
					scoreMap.addScore(target, -10);
				}
				if(!timeWatcher.hasExtraTime()) {
					return;
				}
			}
		}
	}
	
	
	private static void evaluateRivalSeers(IScoreMap scoreMap, IAnalyzer analyzer, Agent ownerSeer, TimeWatcher timeWatcher) {
		List<Agent> candidates = scoreMap.getAgents();
		for(Agent seer : analyzer.getSeers()){
			if(seer == ownerSeer || !candidates.contains(seer)){
				continue;
			}
			
			scoreMap.addScore(seer, 100);
			if(!timeWatcher.hasExtraTime()) {
				return;
			}
		}
	}
	
	private static void evaluateMediums(IScoreMap scoreMap, IAnalyzer analyzer, Agent ownerSeer, TimeWatcher timeWatcher) {
		List<Judge> divines = analyzer.getDivineResultBySeer(ownerSeer);
		List<Agent> candidates = scoreMap.getAgents();
		for(Agent medium : analyzer.getMediums()) {
			if(!candidates.contains(medium)){
				continue;
			}
			boolean line = true;
			List<Judge> inquests = analyzer.getInquestResultByMedium(medium);
			
			for(Judge divine : divines) {
				for(Judge inquest : inquests) {
					if(divine.getTarget() == inquest.getTarget() && 
							divine.getResult() != inquest.getResult()) {
						line = false;
					}
				}
			}

			// ラインがつながっている霊を優先する
			if(line) {
				scoreMap.addScore(medium, 70);
			} else {
				scoreMap.addScore(medium, 50);
			}
			if(!timeWatcher.hasExtraTime()) {
				return;
			}
		}
	}

	public static IScoreMap evaluateGrayForFakeSeer(IAnalyzer analyzer, Agent seer, TimeWatcher timeWatcher) {
		IScoreMap scoreMap = new ScoreMap();
		List<Agent> candidates = new LinkedList<Agent>();
		candidates.addAll(analyzer.getAliveAgents());
		candidates.forEach(agent->{
			scoreMap.addAgent(agent, 0);
		});
		
		// 完全灰の優先度を上げる
		List<Agent> grayAgents = analyzer.getGrayAgents();
		for(Agent grayAgent : grayAgents) {
			if(candidates.contains(grayAgent)) {
				scoreMap.addScore(grayAgent, 30);
			}
		}
		return scoreMap;
	}
	
	// 占い視点で占い対象を検討する際の評価
	public static IScoreMap evaluateGrayForSeer(IAnalyzer analyzer, Agent seer, TimeWatcher timeWatcher) {
		IScoreMap scoreMap = new ScoreMap();
		List<Agent> candidates = new LinkedList<Agent>();
		candidates.addAll(analyzer.getAliveAgents());
		candidates.forEach(agent->{
			scoreMap.addAgent(agent, 0);
		});

		
		// 露出人外数か未発見済みの潜伏人外数を計算
		// 未発見の潜伏人外が居る場合は、灰から占う。
		// 残り人外数 = 露出人外数なら役職を占って行く
		int nakedEnemyCount = analyzer.getNakedEnemyCount();
		int foundEnemyCountFromGray = 0;
		List<Judge> judges = analyzer.getDivineResultBySeer(seer);
		List<Agent> coAgents = analyzer.getCoAgents();
		for(Judge judge : judges) {
			if(!coAgents.contains(judge.getTarget()) &&
					judge.getResult() == Species.WEREWOLF) {
				foundEnemyCountFromGray++;
			}
		}
		if(!timeWatcher.hasExtraTime()) {
			return scoreMap;
		}
		
		if(nakedEnemyCount + foundEnemyCountFromGray >= analyzer.getEnemyCount()) {
			DebugLog.log("*** DIVINE FROM ROLED AGENTS\n");
			// 役職から占う
			// 役職者の優先度を上げる
			scoreMap.getAgents().forEach(agent->{
				if(coAgents.contains(agent)) {
					scoreMap.addScore(agent, 30);
				}
			});
			// 占いの優先度を計算
			evaluateRivalSeers(scoreMap, analyzer, seer, timeWatcher);
			if(!timeWatcher.hasExtraTime()) {
				return scoreMap;
			}
			
			// 霊能の優先度を計算
			evaluateMediums(scoreMap, analyzer, seer, timeWatcher);
			if(!timeWatcher.hasExtraTime()) {
				return scoreMap;
			}
		}else {
			DebugLog.log("*** DIVINE FROM GRAYS\n");
			// 非役職を占う
			
			// 対抗の白ならポイントアップ、対抗の黒ならポイントダウン
			evaluateWhitedOfRivalSeers(scoreMap, analyzer, seer, timeWatcher);
			if(!timeWatcher.hasExtraTime()) {
				return scoreMap;
			}

			// 役職に投票しようとしているならポイントアップ
			evaluateTalk(scoreMap, analyzer);
			if(!timeWatcher.hasExtraTime()) {
				return scoreMap;
			}
			
			// 役職なら優先度を下げる
			for(Agent agent : coAgents) {
				if(candidates.contains(agent)) {
					scoreMap.addScore(agent, -50);
				}
				if(!timeWatcher.hasExtraTime()) {
					return scoreMap;
				}
			}
		}

		return scoreMap;
	}
	
}
