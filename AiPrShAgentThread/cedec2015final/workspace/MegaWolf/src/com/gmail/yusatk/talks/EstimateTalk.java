package com.gmail.yusatk.talks;

import org.aiwolf.client.lib.TemplateTalkFactory;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Role;

import com.gmail.yusatk.interfaces.ITalkEvent;

public class EstimateTalk implements ITalkEvent {
	Agent target = null;
	Role role = null;
	public EstimateTalk(Agent target, Role role) {
		this.target = target;
		this.role = role;
	}
	@Override
	public String getTalk() {
		return TemplateTalkFactory.estimate(target, role);
	}

}
