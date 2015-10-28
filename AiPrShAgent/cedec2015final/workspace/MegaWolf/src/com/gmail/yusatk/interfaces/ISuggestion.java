package com.gmail.yusatk.interfaces;

import org.aiwolf.common.data.*;
import java.util.*;

public interface ISuggestion {
	public enum ActionTypes {
		Vote,
		Attack,
	}
	public class Action {
		Agent owner = null;
		Agent target = null;
		ActionTypes actionType = null;
		public Action(Agent owner, Agent target, ActionTypes actionType) {
			this.owner = owner;
			this.target = target;
			this.actionType = actionType;
		}
		public Agent getOwner() {
			return owner;
		}
		public Agent getTarget() {
			return target;
		}
		public ActionTypes getActionType() {
			return actionType;
		}
	}
	
	public void agree(Agent agent);
	public void disagree(Agent agent);
	public Action getAction();
	public void setAction(Action action);
	
	public List<Agent> getAgreedAgents();
	public List<Agent> getDisagreedAgents();

	public boolean answered(Agent agent);
	
	public boolean isMine(Agent agent);
	public void dump();
}
