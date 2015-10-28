package jp.ac.aitech.k13009kk.aiwolf.client.player;

import java.util.ArrayList;
import java.util.List;

import org.aiwolf.client.lib.TemplateTalkFactory;
import org.aiwolf.client.lib.Topic;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Talk;

import jp.ac.aitech.k13009kk.aiwolf.client.player.base.AbstractAndoVillager;

/**
 * 村人の行動
 * @author keisuke 愛知工業大学 K13009 安藤圭祐
 * @version AndoAgent 1.0
 *
 */
public class AndoVillager extends AbstractAndoVillager {

	@Override
	public void finish() {

		System.out.printf("========%s_TALK========\n", getMe());
		getTalkList(getMe()).forEach(talk -> System.out.println(talk));
		System.out.println("========DIVINE_TALK========");
		getTalkList(Topic.DIVINED).forEach(talk -> System.out.println(talk));
		System.out.println("========DEAD_INFO========");
		System.out.println("AGENT\t\tDAY");
		getAgentDeadDayMap().forEach((agent, day) -> {
			System.out.printf("%s\t%d\n", agent, day);
		});
		System.out.println("====DIVINE_CORRECT_AGENT====");
		System.out.println(this.getDivinedCorrectlyAgentList());
	}

	@Override
	public String talk() {

		String estimateRealSeer = estimateRealSeer();

		if (!getDisagreeDivineMeWrongList().isEmpty()) {
			for (String disagreeDivine : getDisagreeDivineMeWrongList()) {
				if (!getMyTalkContents().contains(disagreeDivine)) {
					return disagreeDivine;
				}
			}
		}

		if (estimateRealSeer != null) {
			if (!this.getMyTalkContents().contains(estimateRealSeer))
				return estimateRealSeer;
		}

		if (getMyTalkCount() < 3) {
			return Talk.SKIP;
		}

		if (!isTalkedAboutVote()) {
			setVoteTarget();
			return TemplateTalkFactory.vote(voteTarget);
		}

		return Talk.OVER;
	}

	private String estimateRealSeer() {
		String estimateRealSeer = null;
		List<Agent> divinedCorrectlyAgentList = getDivinedCorrectlyAgentList();
		if (!divinedCorrectlyAgentList.isEmpty()) {
			for (Agent agent : divinedCorrectlyAgentList) {
				estimateRealSeer = TemplateTalkFactory.estimate(agent, Role.SEER);
				if (!this.getMyTalkContents().contains(estimateRealSeer))
					return estimateRealSeer;
			}
		}
		return null;
	}

	@Override
	public Agent vote() {
		return voteTarget;
	}

	@Override
	public void setVoteTarget() {
		List<Agent> voteCandidates = new ArrayList<>();

		if(getDay() == 1){
			this.voteTarget = this.getReticenceAgent();
			return;
		}

		if (!getComingoutAgents(Role.WEREWOLF).isEmpty()) {
			voteCandidates = getComingoutAgents(Role.WEREWOLF);
		} else
			if (!getExpectFakeSeer().isEmpty()) {
			voteCandidates = getExpectFakeSeer();
		}
		this.removeDeadAgent(voteCandidates);
		voteCandidates.remove(getMe());

		if (!voteCandidates.isEmpty()) {
			this.voteTarget = randomSelect(voteCandidates);
		} else {
			voteCandidates = this.getLatestDayGameInfo().getAliveAgentList();
			voteCandidates.remove(getMe());
			this.voteTarget = randomSelect(voteCandidates);
		}
	}

}
