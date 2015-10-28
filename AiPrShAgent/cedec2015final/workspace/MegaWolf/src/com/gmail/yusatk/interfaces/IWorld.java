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
public interface IWorld {
	public void setRole(Agent agent, Role role);
	public void setRoles(Map<Agent, Role> roles);
	public boolean isValidWorld();
	public void Dump();
	public Map<Agent, Role> getRoles();
	public IWorld clone();
	public void DumpWolves();

	public Agent getBodyguard();
	public Agent getSeer();
	public Agent getMedium();
	
	/**
	 * この世界での狼候補を返す。
	 * （この世界での占い師視点での灰）
	 * @return 狼候補のリスト。
	 */
	public List<Agent> getWolfCandidates(); 

	/**
	 * この世界での確定狼を返す。
	 * （この世界での占い師、霊媒師視点での黒）
	 * @return 狼のリスト。
	 */
	public List<Agent> getWolves();			

	/**
	 * この世界で生存している確定狼を返す。
	 * @return 狼のリスト。
	 */
	public List<Agent> getAliveWolves();

	/**
	 * この世界で死亡している確定狼を返す。
	 * @return 狼のリスト。
	 */
	public List<Agent> getDeadWolves();	
	
	public Agent getRoledAgent(Role role);
}
