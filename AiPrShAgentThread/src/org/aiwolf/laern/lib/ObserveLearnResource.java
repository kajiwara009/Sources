package org.aiwolf.laern.lib;

import org.aiwolf.common.data.Role;

public class ObserveLearnResource {
	private Role role;
	private Observe observe;
	private Situation situation;

	public ObserveLearnResource(Role role, Observe observe, Situation situation) {
		this.role = role;
		this.observe = observe;
		this.situation = situation;
	}

	
	public Role getRole() {
		return role;
	}


	public void setRole(Role role) {
		this.role = role;
	}


	public Observe getObserve() {
		return observe;
	}

	public void setObserve(Observe observe) {
		this.observe = observe;
	}

	public Situation getSituation() {
		return situation;
	}

	public void setSituation(Situation situation) {
		this.situation = situation;
	}
	
	
}
