package com.gmail.yusatk.interfaces;

import java.util.Queue;

import org.aiwolf.common.data.Agent;

public interface IBattlePlan {
	IBattlePlan planUpdate(boolean dayStaryUpdate);
	Queue<ITalkEvent> getTalkPlan(int restTalkCount);
	Queue<ITalkEvent> getWhisperPlan(int restWhisperCount);
	Agent getVotePlan();
	Agent getAttackPlan();
	Agent getGuardPlan();
	Agent getDivinePlan();
	void dayStart();
}
