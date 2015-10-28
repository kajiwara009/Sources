package org.aiwolf.Satsuki.LearningPlayer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aiwolf.Satsuki.LearningPlayer.AbstractKajiBasePlayer;
import org.aiwolf.Satsuki.lib.Pattern;
import org.aiwolf.client.lib.Utterance;
import org.aiwolf.common.data.Agent;

public class KajiVillagerPlayer extends AbstractKajiBasePlayer {



	@Override
	public void setVoteTarget() {
		setVoteTargetTemplate(myPatterns);
	}

	@Override
	public String getJudgeText() {
		return null;
	}

	@Override
	public String getComingoutText() {
		return null;
	}



}
