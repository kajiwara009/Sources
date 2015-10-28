package com.gmail.yusatk.plans;

import org.aiwolf.client.lib.TemplateTalkFactory;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Judge;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Species;

import com.gmail.yusatk.interfaces.IAgentEx;
import com.gmail.yusatk.interfaces.IAnalyzer;
import com.gmail.yusatk.interfaces.IBattlePlan;
import com.gmail.yusatk.interfaces.ITalkEvent;
import com.gmail.yusatk.interfaces.IWorldCache;
import com.gmail.yusatk.talks.ComingOutTalk;
import com.gmail.yusatk.talks.DivineTalk;

import java.util.*;

public class FakeSeerForWolfPlan extends DefaultBattlePlan {

	IWorldCache worldCache = null;
	IAnalyzer analyzer = null;
	IAgentEx owner = null;
	
	public FakeSeerForWolfPlan(IAgentEx owner, IAnalyzer analyzer, IWorldCache worldCache) {
		this.owner = owner;
		this.analyzer = analyzer;
		this.worldCache = worldCache;
	}

	boolean isComingOut = false;
	
	List<Agent> alreadyDivined = new ArrayList<Agent>();
	
	private boolean shouldComingOut() {
		if(isComingOut) {
			return false;
		}
		// TODO: 対抗の黒出しがあるなら様子見で潜伏する
		if(analyzer.getDay() >= 1) {
			return true;
		}
		return false;
	}	
	
	@Override
	public IBattlePlan planUpdate(boolean dayStartUpdate) {
		return this;
	}

	List<Judge> toldResults = new ArrayList<Judge>();	
	List<Judge> fakeResults = new ArrayList<Judge>();
	
	private Agent getFakeTarget(List<Agent> candidates) {
		assert candidates.size() > 0;
		return candidates.get(0);
	}
	

	private List<Agent> getGrayBuddies() {
		List<Agent> grayBuddies = new LinkedList<Agent>();
		List<Agent> gray = analyzer.getGrayAgents();
		for(Agent buddy : analyzer.getBuddyWolves()) {
			boolean judged = false;
			for(Judge fakeJudge : fakeResults) {
				if(fakeJudge == null) {
					continue;
				}
				if(fakeJudge.getTarget() == buddy) {
					judged = true;
					break;
				}
				
			}
			if(!judged) {
				if(gray.contains(buddy)) {
					grayBuddies.add(buddy);
				}
			}
		}
		return grayBuddies;
	}
	
	private void addFakeResult(Agent target, Species result) {
		fakeResults.add(new Judge(analyzer.getDay(), owner.getAgent(), target, result));
		alreadyDivined.add(target);
	}
	
	private void createFakeResult() {
		if(fakeResults.size() == 0) {
			fakeResults.add(null);
		}
		
		while(fakeResults.size() < analyzer.getDay() + 1) {
			// 序盤で完全グレー内の味方を囲う。
			List<Agent> gray = analyzer.getGrayAgents();
			List<Agent> grayBuddies = getGrayBuddies();
			if(analyzer.hasExtraExecution() && grayBuddies.size() >= 1) {
				for(Agent buddy : grayBuddies) {
					if(gray.contains(buddy)) {
						addFakeResult(buddy, Species.HUMAN);
						break;
					}
				}
			} else {
				// TODO: 残り吊り回数と、自分視点の狼候補の数を見てギリギリのタイミングで黒を打つ
				if(gray.size() > 0) {
					addFakeResult(gray.get(0), Species.HUMAN);
				}else{
					List<Agent> candidates = new ArrayList<Agent>();
					candidates.addAll(analyzer.getLatestGameInfo().getAliveAgentList());
					candidates.remove(owner.getAgent());
					candidates.removeAll(alreadyDivined);
					if(candidates.size() == 0) {
						addFakeResult(owner.getAgent(), Species.HUMAN); // 破綻してる場合
					}else{
						addFakeResult(getFakeTarget(candidates), Species.HUMAN);
					}
				}
			}
			
		}		
	}
	
	@Override
	public Queue<ITalkEvent> getTalkPlan(int restTalkCount) {
		Queue<ITalkEvent> talks = new LinkedList<ITalkEvent>();
		if(shouldComingOut()) {
			isComingOut = true;
			talks.add(new ComingOutTalk(owner.getAgent(), Role.SEER));
		}
		if(restTalkCount > 5){
			talks.add(new ITalkEvent() {
				@Override
				public String getTalk() {
					return TemplateTalkFactory.skip();
				}
			});
		}else {
			// TODO: タイミングをちゃんとする
			// 他の占いの様子を見てから騙り結果を作る
			createFakeResult();
			for(Judge fake : fakeResults) {
				if(fake == null) {
					continue;
				}
				if(!toldResults.contains(fake)){
					toldResults.add(fake);
					talks.add(new DivineTalk(fake.getTarget(), fake.getResult()));
				}
			}
		}
		return talks;
	}

	@Override
	public Agent getVotePlan() {
		// TODO Auto-generated method stub
		return null;
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

	@Override
	public void dayStart() {
		// TODO Auto-generated method stub

	}

}
