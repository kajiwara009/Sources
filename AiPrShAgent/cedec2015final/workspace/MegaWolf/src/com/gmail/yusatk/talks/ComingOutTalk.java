package com.gmail.yusatk.talks;

import org.aiwolf.client.lib.TemplateTalkFactory;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Role;

import com.gmail.yusatk.interfaces.ITalkEvent;

public class ComingOutTalk implements ITalkEvent {
	Agent target = null;
	Role role = null;
	
	public ComingOutTalk(Agent target, Role role) {
		this.target = target;
		this.role = role;
	}
	
	@Override
	public String getTalk() {
		return TemplateTalkFactory.comingout(target, role);
	}
}
