package jp.ac.aitech.k13009kk.aiwolf.client.player;

import java.util.ArrayList;
import java.util.List;

import org.aiwolf.client.lib.TemplateTalkFactory;
import org.aiwolf.client.lib.Topic;
import org.aiwolf.client.lib.Utterance;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Talk;

import jp.ac.aitech.k13009kk.aiwolf.client.player.base.AbstractAndoBodyguard;

/**
 * 狩人の行動
 * @author keisuke 愛知工業大学 K13009 安藤圭祐
 * @version AndoAgent 1.0
 *
 */
public class AndoBodyguard extends AbstractAndoBodyguard {

	@Override
	public Agent guard() {
		List<Agent> guardCandidates = new ArrayList<>();
		Agent guardTarget;
		for (Talk talk : this.getTalkList(Topic.COMINGOUT)) {
			Utterance utterance = new Utterance(talk.getContent());
			if (utterance.getRole() == Role.SEER) {
				guardCandidates.add(talk.getAgent());
			}
		}
		removeDeadAgent(guardCandidates);
		if (!guardCandidates.isEmpty()) {
			guardTarget = randomSelect(guardCandidates);
			return guardTarget;
		}

		guardCandidates = getLatestDayGameInfo().getAliveAgentList();
		guardTarget = randomSelect(guardCandidates);
		return guardTarget;
	}

	@Override
	public String talk() {

		String estimateRealSeer = estimateRealSeer();

		if (estimateRealSeer != null) {
			if (!this.getMyTalkContents().contains(estimateRealSeer))
				return estimateRealSeer;
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
		voteCandidates.addAll(this.getLatestDayGameInfo().getAliveAgentList());
		voteCandidates.remove(getMe());
		voteTarget = randomSelect(voteCandidates);
	}

	@Override
	public void finish() {
		for (int day : this.getDayGuardedAgentMap().keySet()) {
			Agent guardedAgent = getDayGuardedAgentMap().get(day);
			String successOrFailure = this.getDayGuardedSuccessMap().get(day) ? "success" : "failure";
			System.out.printf("On Day%d, I'm guarded %s. So, result is %s\n", day, guardedAgent, successOrFailure);
		}
		
		System.out.printf("========%s_TALK========\n", getMe());
		getTalkList(getMe()).forEach(talk -> System.out.println(talk));
	}

}
