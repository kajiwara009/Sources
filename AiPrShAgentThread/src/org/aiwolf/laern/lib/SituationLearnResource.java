package org.aiwolf.laern.lib;

import org.aiwolf.common.data.Role;

public class SituationLearnResource<T> {
	private Role role;
	private Situation situation;
	private T actionValue;
	private boolean isWin;
	
	
	public SituationLearnResource(Role role, Situation situation, T actionValue, boolean isWin) {
		super();
		this.role = role;
		this.situation = situation;
		this.actionValue = actionValue;
		this.isWin = isWin;
	}
	
	
	
	public Role getRole() {
		return role;
	}



	public void setRole(Role role) {
		this.role = role;
	}



	public Situation getSituation() {
		return situation;
	}
	public void setSituation(Situation situation) {
		this.situation = situation;
	}
	public T getActionValue() {
		return actionValue;
	}
	public void setActionValue(T actionValue) {
		this.actionValue = actionValue;
	}
	public boolean isWin() {
		return isWin;
	}
	public void setWin(boolean isWin) {
		this.isWin = isWin;
	}
	
	
	
}
