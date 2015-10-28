package takata.player;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Judge;
import org.aiwolf.common.data.Role;


public class AdvanceGameInfo {

	/**
	 * 発話で伝えられた占い結果のリスト．今回のプロトコルでは何日目に占ったのか分からないので，発話日に設定．
	 */
	private List<Judge> inspectJudgeList = new ArrayList<Judge>();

	/**
	 * 発話で伝えられた霊能結果のリスト．今回のプロトコルでは何日目に霊能したのか分からないので，発話日に設定．
	 */
	private List<Judge> mediumJudgeList = new ArrayList<Judge>();

	private Map<Agent, Role> comingoutMap = new HashMap<Agent, Role>();

	//発話で伝えられた投票結果のマップ
	private Map<Agent, Agent> voteMap = new HashMap<Agent, Agent>();

	public Map<Agent, Role> getComingoutMap() {
		return comingoutMap;
	}

	/**
	 * COしたプレイヤーをcomingoutMapに加える．
	 * @param agent
	 * @param role
	 */
	public void putComingoutMap(Agent agent, Role role) {
		comingoutMap.put(agent, role);
	}

	public void setComingoutMap(Map<Agent, Role> comingoutMap) {
		this.comingoutMap = comingoutMap;
	}

	//誰（キー）が誰（バリュー）に投票
	public Map<Agent, Agent> getVoteMap() {
		return voteMap;
	}
	public void putVoteMap(Agent agent1, Agent agent2) {
		voteMap.put(agent1, agent2);
	}
	public void setVoteMap(Map<Agent, Agent> voteMap) {
		this.voteMap = voteMap;
	}

	public List<Judge> getInspectJudgeList() {
		return inspectJudgeList;
	}

	public void setInspectJudgeList(List<Judge> inspectJudgeList) {
		this.inspectJudgeList = inspectJudgeList;
	}

	public void addInspectJudgeList(Judge judge) {
		this.inspectJudgeList.add(judge);
	}

	public List<Judge> getMediumJudgeList() {
		return mediumJudgeList;
	}

	public void setMediumJudgeList(List<Judge> mediumJudgeList) {
		this.mediumJudgeList = mediumJudgeList;
	}

	public void addMediumJudgeList(Judge judge) {
		this.mediumJudgeList.add(judge);
	}

}
