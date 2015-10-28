package com.gmail.yusatk.interfaces;

import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Role;

import com.gmail.yusatk.utils.TimeWatcher;

public interface IAgentEx {
	Agent getAgent();
	Role getRole();
	Role getAssumptionRole();
	void setAssumptionRole(Role role);
	TimeWatcher getTimeWatcher();
}
