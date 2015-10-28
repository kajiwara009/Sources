package com.gmail.yusatk.talks;

import org.aiwolf.client.lib.TemplateTalkFactory;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Species;

import com.gmail.yusatk.interfaces.ITalkEvent;

public class InquestTalk implements ITalkEvent {
	Agent target = null;
	Species result = null;
	public InquestTalk(Agent target, Species result) {
		this.target = target;
		this.result = result;
		assert target != null;
		assert result != null;
	}
	@Override
	public String getTalk() {
		return TemplateTalkFactory.inquested(target, result);
	}

}
