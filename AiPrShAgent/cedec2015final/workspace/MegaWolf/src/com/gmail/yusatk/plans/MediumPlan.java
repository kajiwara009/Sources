package com.gmail.yusatk.plans;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.aiwolf.client.lib.TemplateTalkFactory;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Judge;

import com.gmail.yusatk.interfaces.IAgentEx;
import com.gmail.yusatk.interfaces.IAnalyzer;
import com.gmail.yusatk.interfaces.IBattlePlan;
import com.gmail.yusatk.interfaces.ITalkEvent;

public class MediumPlan extends DefaultBattlePlan{
	IAgentEx owner;
	IAnalyzer analyzer;

	List<Judge> inquestResults = new LinkedList<Judge>();
	List<Judge> toldInquestResults = new LinkedList<Judge>();
	
	public MediumPlan(IAgentEx owner, IAnalyzer analyzer) {
		this.owner = owner;
		this.analyzer = analyzer;
	}
	
	@Override
	public IBattlePlan planUpdate(boolean dayStartUpdate) {
		return this;
	}

	
	boolean isComingOutTiming() {
		return analyzer.getDay() >= 1;
	}
	boolean isComingOut = false;

	@Override
	public Queue<ITalkEvent> getTalkPlan(int restTalkCount) {
		Queue<ITalkEvent> talks = new LinkedList<ITalkEvent>();
		
		if(!isComingOut && isComingOutTiming()) {
			isComingOut = true;
			talks.add(new ITalkEvent() {
				@Override
				public String getTalk() {
					return TemplateTalkFactory.comingout(owner.getAgent(), owner.getRole());
				}
			});
		}
		
		for(Judge inquest : inquestResults) {
			if(!toldInquestResults.contains(inquest)) {
				toldInquestResults.add(inquest);
				
				if(!isComingOut) {
					isComingOut = true;
					talks.add(new ITalkEvent() {
						@Override
						public String getTalk() {
							return TemplateTalkFactory.comingout(owner.getAgent(), owner.getRole());
						}
					});
				}
				
				talks.add(new ITalkEvent() {
					@Override
					public String getTalk() {
						return TemplateTalkFactory.inquested(inquest.getTarget(), inquest.getResult());
					}
				});
			}
		}
		return talks;
	}

	@Override
	public Agent getVotePlan() {
		return null;
	}

	@Override
	public void dayStart() {
		Judge latestInquest = analyzer.getLatestGameInfo().getMediumResult();
		if(latestInquest != null) {
			inquestResults.add(latestInquest);
		}
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
