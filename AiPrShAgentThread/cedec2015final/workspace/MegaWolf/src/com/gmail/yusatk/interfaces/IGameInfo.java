/**
 * 
 */
package com.gmail.yusatk.interfaces;
import org.aiwolf.common.data.*;
import java.util.*;
/**
 * @author Yu
 *
 */
public interface IGameInfo {
	public Agent getAgent();
	public List<Agent> getAgentList();
	public List<Agent> getAliveAgentList();
	public Agent getAttackedAgent();
	public List<Vote> getAttackVoteList();
	public int getDay();
	public Judge getDivineResult();
	public Agent getExecutedAgent();
	public Agent getGuardedAgent();
	public Judge getMediumResult();
	public Role getRole();
	public Map<Agent,Role> getRoleMap();
	public Map<Agent, Status> getStatusMap();
	public List<Talk> getTalkList();
	public List<Vote> getVoteList();
	public List<Talk> getWhisperList();
}
