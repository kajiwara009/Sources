/**
 * 
 */
package com.gmail.yusatk.data;

import java.util.List;
import java.util.Map;

import org.aiwolf.common.data.*;
import org.aiwolf.common.net.*;
import com.gmail.yusatk.interfaces.IGameInfo;

/**
 * @author Yu
 *
 */
public class GameInfoEx implements IGameInfo {
	GameInfo gameInfo = null;
	public GameInfoEx(GameInfo gameInfo) {
		this.gameInfo = gameInfo;
	}
	/* (non-Javadoc)
	 * @see com.gmail.yusatk.interfaces.IGameInfo#getAgent()
	 */
	@Override
	public Agent getAgent() {
		return gameInfo.getAgent();
	}

	/* (non-Javadoc)
	 * @see com.gmail.yusatk.interfaces.IGameInfo#getAgentList()
	 */
	@Override
	public List<Agent> getAgentList() {
		return gameInfo.getAgentList();
	}

	/* (non-Javadoc)
	 * @see com.gmail.yusatk.interfaces.IGameInfo#getAliveAgentList()
	 */
	@Override
	public List<Agent> getAliveAgentList() {
		return gameInfo.getAliveAgentList();
	}

	/* (non-Javadoc)
	 * @see com.gmail.yusatk.interfaces.IGameInfo#getAttackedAgent()
	 */
	@Override
	public Agent getAttackedAgent() {
		return gameInfo.getAttackedAgent();
	}

	/* (non-Javadoc)
	 * @see com.gmail.yusatk.interfaces.IGameInfo#getAttackVoteList()
	 */
	@Override
	public List<Vote> getAttackVoteList() {
		return gameInfo.getAttackVoteList();
	}

	/* (non-Javadoc)
	 * @see com.gmail.yusatk.interfaces.IGameInfo#getDay()
	 */
	@Override
	public int getDay() {
		return gameInfo.getDay();
	}

	/* (non-Javadoc)
	 * @see com.gmail.yusatk.interfaces.IGameInfo#getDivineResult()
	 */
	@Override
	public Judge getDivineResult() {
		return gameInfo.getDivineResult();
	}

	/* (non-Javadoc)
	 * @see com.gmail.yusatk.interfaces.IGameInfo#getExecutedAgent()
	 */
	@Override
	public Agent getExecutedAgent() {
		return gameInfo.getExecutedAgent();
	}

	/* (non-Javadoc)
	 * @see com.gmail.yusatk.interfaces.IGameInfo#getGuardedAgent()
	 */
	@Override
	public Agent getGuardedAgent() {
		return gameInfo.getGuardedAgent();
	}

	/* (non-Javadoc)
	 * @see com.gmail.yusatk.interfaces.IGameInfo#getMediumResult()
	 */
	@Override
	public Judge getMediumResult() {
		return gameInfo.getMediumResult();
	}

	/* (non-Javadoc)
	 * @see com.gmail.yusatk.interfaces.IGameInfo#getRole()
	 */
	@Override
	public Role getRole() {
		return gameInfo.getRole();
	}

	/* (non-Javadoc)
	 * @see com.gmail.yusatk.interfaces.IGameInfo#getRoleMap()
	 */
	@Override
	public Map<Agent, Role> getRoleMap() {
		return gameInfo.getRoleMap();
	}

	/* (non-Javadoc)
	 * @see com.gmail.yusatk.interfaces.IGameInfo#getStatusMap()
	 */
	@Override
	public Map<Agent, Status> getStatusMap() {
		return gameInfo.getStatusMap();
	}

	/* (non-Javadoc)
	 * @see com.gmail.yusatk.interfaces.IGameInfo#getTalkList()
	 */
	@Override
	public List<Talk> getTalkList() {
		return gameInfo.getTalkList();
	}

	/* (non-Javadoc)
	 * @see com.gmail.yusatk.interfaces.IGameInfo#getVoteList()
	 */
	@Override
	public List<Vote> getVoteList() {
		return gameInfo.getVoteList();
	}

	/* (non-Javadoc)
	 * @see com.gmail.yusatk.interfaces.IGameInfo#getWhisperList()
	 */
	@Override
	public List<Talk> getWhisperList() {
		return gameInfo.getWhisperList();
	}

}
