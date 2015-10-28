package com.gmail.yusatk.talks;

import org.aiwolf.client.lib.TemplateTalkFactory;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Species;

import com.gmail.yusatk.interfaces.ITalkEvent;

public class DivineTalk implements ITalkEvent {
	Agent target = null;
	Species result = null;
	public DivineTalk(Agent target, Species result) {
		this.target = target;
		this.result = result;
	}
	@Override
	public String getTalk() {
		return TemplateTalkFactory.divined(target, result);
	}

}
