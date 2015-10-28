package com.gmail.yusatk.plans;

import java.util.*;

import org.aiwolf.client.lib.TemplateTalkFactory.TalkType;
import org.aiwolf.common.data.*;

import com.gmail.yusatk.data.RoleMap;
import com.gmail.yusatk.data.ScoreMap;
import com.gmail.yusatk.interfaces.*;
import com.gmail.yusatk.interfaces.ISuggestion.ActionTypes;
import com.gmail.yusatk.talks.AnswerTalk;
import com.gmail.yusatk.talks.ComingOutTalk;
import com.gmail.yusatk.talks.OverTalk;
import com.gmail.yusatk.talks.VoteTalk;
import com.gmail.yusatk.talks.AnswerTalk.AnswerType;
import com.gmail.yusatk.utils.DebugLog;

public class WolfBasePlan extends DefaultBattlePlan {
	IAgentEx owner = null;
	IAnalyzer analyzer = null;
	IWorldCache worldCache = null;
	RoleMap roleMap = null;
	
	public WolfBasePlan(IAgentEx owner, IAnalyzer analyzer, IWorldCache worldCache) {
		this.owner = owner;
		this.analyzer = analyzer;
		this.worldCache = worldCache;

		this.roleMap = new RoleMap(analyzer.getSetting().getRoleNumMap());
	}
	
	
	Agent votePlan = null;
	Agent whisperVotePlan = null;
	Agent trueSeer = null;
	Agent possessed = null;

	Queue<ITalkEvent> suggestionReplies = new LinkedList<ITalkEvent>();
	
	private void findPossessed() {
		if(possessed != null) {
			return;
		}
		List<Agent> seers = analyzer.getSeers();
		List<Agent> buddies = analyzer.getBuddyWolves();
		for(Agent seer : seers) {
			if(buddies.contains(seer)){
				continue;
			}
			List<Judge> judges = analyzer.getDivineResultBySeer(seer);
			for(Judge judge : judges){
				if(buddies.contains(judge.getTarget()) && judge.getResult() == Species.HUMAN) {
					possessed = seer;
					if(trueSeer == possessed) {
						trueSeer = null; // 黒出し誤爆 -> 白囲いの場合は trueSeer に狂人がセットされているのでこの時点で一度クリアする。
					}
					return;
				}
			}
		}
		
		// 霊結果が間違ってるなら狂人とみなす
		List<Agent> mediums = analyzer.getMediums();
		for(Agent medium : mediums) {
			if(buddies.contains(medium)){
				continue;
			}
			List<Judge> judges = analyzer.getInquestResultByMedium(medium);
			for(Judge judge : judges){
				if(buddies.contains(judge.getTarget()) && judge.getResult() == Species.HUMAN) {
					possessed = medium;
					return;
				}
			}
		}
	}
	
	private boolean shouldPerformPP() {
		if(possessed == null) {
			return false;
		}
		if(!analyzer.getAliveAgents().contains(possessed)) {
			return false;
		}
		List<Agent> aliveBuddies = analyzer.getAliveBuddyWolves();
		int wolfSideCount = aliveBuddies.size() + 1;
		int humanSideCount = analyzer.getAliveAgents().size() - wolfSideCount;
		
		return wolfSideCount > humanSideCount;
	}
	
	private void findTrueSeer() {
		List<Agent> candidates = new ArrayList<Agent>();
		candidates.addAll(analyzer.getSeers());
		candidates.removeAll(analyzer.getBuddyWolves());
		candidates.remove(possessed);
		
		if(candidates.size() == 1) {
			trueSeer = candidates.get(0);
			return;
		}
		
		// 黒出しが当たっているならとりあえず真認定しておく
		candidates.forEach(seer->{
			List<Judge> judges = this.analyzer.getDivineResultBySeer(seer);
			judges.forEach(judge->{
				this.analyzer.getBuddyWolves().forEach(buddy->{
					if(judge.getTarget() == buddy && judge.getResult() == Species.WEREWOLF) {
						trueSeer = seer;
						return;
					}
				});
			});
		});
	}
	
	@Override
	public IBattlePlan planUpdate(boolean dayStartUpdate) {
		findPossessed();
		findTrueSeer();
		return this;
	}

	
	boolean ppAnnounced = false;
	@Override
	public Queue<ITalkEvent> getTalkPlan(int restTalkCount) {
		Queue<ITalkEvent> talks = new LinkedList<ITalkEvent>();
		if(shouldPerformPP() && !ppAnnounced) {
			ppAnnounced = true;
			List<Agent> wolves = analyzer.getBuddyWolves();
			wolves.forEach(wolf->{
				talks.add(new ComingOutTalk(wolf, Role.WEREWOLF));
			});
		}
		
		if(shouldPerformPP()) {
			Agent oldVotePlan = votePlan;
			Agent newVotePlan = getVotePlan();
			if(oldVotePlan != newVotePlan) {
				votePlan = newVotePlan;
				talks.add(new VoteTalk(TalkType.TALK, newVotePlan));
			}
		}
		return talks;
	}

	@Override
	public Queue<ITalkEvent> getWhisperPlan(int restWhisperCount) {
		Queue<ITalkEvent> whispers = new LinkedList<ITalkEvent>();
		if(analyzer.getDay() == 0) {
			whispers.add(new OverTalk(TalkType.WHISPER));
		}
		
		// 提案への回答をささやく
		whispers.addAll(suggestionReplies);
		suggestionReplies.clear();
		
		Agent oldVotePlan = whisperVotePlan;
		Agent newVotePlan = getVotePlan();
		if(oldVotePlan != newVotePlan) {
			whisperVotePlan = newVotePlan;
			whispers.add(new VoteTalk(TalkType.WHISPER, newVotePlan));
		}
		
		return whispers;
	}	

	private Agent calcVotePlanForPP() {
		DebugLog.log("+++++  WOLF TEAM CAN PERFORM PP! +++++\n");
		// PP 時は味方の狼と投票を合わせるようにする
		Map<Integer, ISuggestion> suggestions = analyzer.getWolfSuggestions();
		
		// 既に味方狼の投票提案があるかどうかを確認
		for(Map.Entry<Integer, ISuggestion> e : suggestions.entrySet()) {
			ISuggestion suggestion = e.getValue();
			int suggestionId = e.getKey();
			if(suggestion.isMine(owner.getAgent())) {
				// 自分の提案がある場合
				int wolfCount = analyzer.getBuddyWolves().size();
				int rejectCount = wolfCount / 2 + 1; // しきい値を過半数とする
				// 過半数が反対していないならとりあえず維持。
				if(suggestion.getDisagreedAgents().size() < rejectCount) {
					return suggestion.getAction().getTarget();
				}
				// 過半数が反対してるならいったん取り消して、他の人の提案を見る
				whisperVotePlan = null;
				continue;
			}
			// 回答済みの提案ならパス
			if(suggestion.answered(owner.getAgent())) {
				continue;
			}
			ISuggestion.Action action = suggestion.getAction();
			if(action.getActionType() == ActionTypes.Vote) {
				Agent target = action.getTarget();
				if(analyzer.getBuddyWolves().contains(target) ||
					target == possessed) {
					// もし仲間か狂人に投票しようとしているなら
					// 反対の意思表示をする
					suggestion.disagree(owner.getAgent());
					suggestionReplies.add(new AnswerTalk(TalkType.WHISPER, 
							AnswerType.DISAGREE, analyzer.getDay(), suggestionId));
				}else{
					// 人間サイドへ投票しているならそこに票を合わせる
					suggestion.agree(owner.getAgent());
					suggestionReplies.add(new AnswerTalk(TalkType.WHISPER, 
							AnswerType.AGREE, analyzer.getDay(), suggestionId));
					return action.getTarget();
				}
			}
		}
		
		// 賛同できる投票がなかった場合は自分が人間サイドへの投票を提案する。
		List<Agent> candidates = new ArrayList<Agent>();
		candidates.addAll(analyzer.getAliveAgents());
		candidates.removeAll(analyzer.getBuddyWolves());
		candidates.remove(possessed);
		return candidates.get(0);
	}
	
	private Agent calcVotePlanForNotPP() {
		// PP なし
		// TODO: ちゃんと選ぶ
		// 占いの黒出し以外で仲間が吊られそうな場合は
		// 他に票を重ねることで回避できないかどうかを試みる
		List<Agent> candidates = new ArrayList<Agent>();
		candidates.addAll(analyzer.getLatestGameInfo().getAliveAgentList());
		candidates.removeAll(analyzer.getBuddyWolves());
		candidates.remove(possessed);
		return candidates.get(0);
	}
	
	@Override
	public Agent getVotePlan() {
		if(shouldPerformPP()) {
			return calcVotePlanForPP();
		}else{
			return calcVotePlanForNotPP();
		}
	}
	
	private List<Agent> getAliveBodyguardCandidates() {
		List<Agent> candidates = new ArrayList<Agent>();
		candidates.addAll(analyzer.getAliveAgents());
		candidates.removeAll(analyzer.getSeers());
		candidates.removeAll(analyzer.getMediums());
		candidates.removeAll(analyzer.getBuddyWolves());
		return candidates;
	}
	
	private List<Agent> calcAttackPriority() {
		// TODO: 内訳破綻しない噛み筋を考える必要がある。
		List<Agent> candidates = new ArrayList<Agent>();
		candidates.addAll(analyzer.getAliveAgents());
		candidates.removeAll(analyzer.getBuddyWolves());
		
		IScoreMap candidatesMap = new ScoreMap();
		candidates.forEach((c)->{
			candidatesMap.addAgent(c, 0);
		});
		
		boolean trueSeerAlive = (trueSeer != null) && (candidates.contains(trueSeer));
		
		// 占い師から白をもらっている場合はスコアアップ
		List<Agent> seers = analyzer.getSeers();
		candidates.forEach((c)->{
			seers.forEach(seer->{
				List<Judge> judges = analyzer.getDivineResultBySeer(seer);
				judges.forEach(judge->{
					if(judge.getTarget() == c && judge.getResult() == Species.HUMAN) {
						candidatesMap.addScore(c, 5);
					}
				});
			});
		});
		
		// ボディガード候補はスコアアップ
		List<Agent> bgCandidates = this.getAliveBodyguardCandidates();
		bgCandidates.forEach(bg->{
			candidatesMap.addScore(bg, 15);
		});
		
		int restExeCount = analyzer.getRestExecutionCount();
		// 序盤でのボディガードCO者はスコアアップ
		// TODO: 終盤は襲撃してはいけないパターンがあるので対応が必要
		if(restExeCount >= 4){
			List<Agent> bgs = analyzer.getBodyguards();
			bgs.forEach(bg->{
				candidatesMap.addScore(bg, 15);
			});
		}
		
		// 初日に占い3COで真が判明していて、且つ黒出しされてない場合噛みにいく
		// TODO: 占い 2CO でも霊能が 1CO のみとかなら占い噛みでもよいかもしれない。メタ次第。
		if(analyzer.getDay() == 1 && trueSeerAlive && seers.size() >= 3) {
			List<Judge> judges = analyzer.getDivineResultBySeer(trueSeer);
			boolean doAttack = true;
			for(Judge judge : judges) {
				for(Agent buddy : analyzer.getBuddyWolves()) {
					if(judge.getTarget() == buddy && judge.getResult() == Species.WEREWOLF) {
						doAttack = false;
					}
				}
			}
			if(doAttack) {
				candidatesMap.addScore(trueSeer, 40);
			}
		}
		
		// 中盤〜終盤以降の真占い師はスコアアップ
		// TODO: ボディガードを襲撃できている確信があって、詰まない状況ならすぐ噛む
		if(restExeCount <= 4) {
			if(trueSeerAlive) {
				candidatesMap.addScore(trueSeer, 30);
			}
		}

		candidatesMap.dump("AttackPriority\n");
		return candidatesMap.getAgentsByDesc();
	}
	
	@Override
	public Agent getAttackPlan() {
		List<Agent> candidates = calcAttackPriority();
		return candidates.get(0);
	}	
	
	@Override
	public void dayStart() {
		votePlan = null;
		suggestionReplies.clear();
		ppAnnounced = false;
		whisperVotePlan = null;
	}
}
