package com.gmail.yusatk.plans;

import java.util.*;

import org.aiwolf.common.data.Agent;

import com.gmail.yusatk.interfaces.IBattlePlan;
import com.gmail.yusatk.interfaces.ITalkEvent;

public class DefaultBattlePlan implements IBattlePlan {

	@Override
	public IBattlePlan planUpdate(boolean dayStartUpdate) {
		return this;
	}

	@Override
	public Queue<ITalkEvent> getTalkPlan(int restTalkCount) {
		return new LinkedList<ITalkEvent>();
	}

	@Override
	public Queue<ITalkEvent> getWhisperPlan(int restWhisperCount) {
		return new LinkedList<ITalkEvent>();
	}

	@Override
	public Agent getVotePlan() {
		return null;
	}

	@Override
	public Agent getAttackPlan() {
		return null;
	}

	@Override
	public Agent getGuardPlan() {
		return null;
	}

	@Override
	public Agent getDivinePlan() {
		return null;
	}

	@Override
	public void dayStart() {
	}

}
