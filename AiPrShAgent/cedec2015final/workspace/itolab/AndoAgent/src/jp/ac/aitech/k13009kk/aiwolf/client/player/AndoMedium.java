package jp.ac.aitech.k13009kk.aiwolf.client.player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aiwolf.client.lib.TemplateTalkFactory;
import org.aiwolf.client.lib.Topic;
import org.aiwolf.client.lib.Utterance;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Judge;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Species;
import org.aiwolf.common.data.Talk;

import jp.ac.aitech.k13009kk.aiwolf.client.player.base.AbstractAndoMedium;

/**
 * 霊能者の行動
 * @author keisuke 愛知工業大学 K13009 安藤圭祐
 * @version AndoAgent 1.0
 *
 */
public class AndoMedium extends AbstractAndoMedium {

	List<String> talkedList = new ArrayList<>();
	private List<Agent> pandaAgentList = new ArrayList<>();
	private Map<Agent, List<Species>> divinedAgentResultListMap = new HashMap<>();

	@Override
	public void finish() {
		System.out.println("========MY_DIVINE========");
		getMyJudgeList().forEach(judge -> System.out.println(judge));
		System.out.printf("========%s_TALK========\n", getMe());
		getTalkList(getMe()).forEach(talk -> System.out.println(talk));
		System.out.println("========DIVINE========");
		this.divinedAgentResultListMap.forEach((agent, resultList) -> System.out.println(agent + "\t" + resultList));

	}

	@Override
	public String talk() {
		List<String> talkPlanList = new ArrayList<>();

		talkPlanList.addAll(this.getDisagreeDivineMeWrongList());
		talkPlanList.removeAll(talkedList);
		if (!talkPlanList.isEmpty()) {
			String talk = talkPlanList.get(0);
			talkedList.add(talk);
			return talk;
		}

		updatePandaAgentList();
		if (!pandaAgentList.isEmpty() && !isComingout) {
			isComingout = true;
			talkedList.add(TemplateTalkFactory.comingout(getMe(), getMyRole()));
			return TemplateTalkFactory.comingout(getMe(), getMyRole());
		}

		if (isSuspectedMe() && !isComingout) {
			isComingout = true;
			talkedList.add(TemplateTalkFactory.comingout(getMe(), getMyRole()));
			return TemplateTalkFactory.comingout(getMe(), getMyRole());
		}

		if (isComingout) {
			for (Judge judge : this.getMyJudgeList()) {
				talkPlanList.add(TemplateTalkFactory.inquested(judge.getTarget(), judge.getResult()));
				talkPlanList.removeAll(talkedList);
				if (!talkPlanList.isEmpty()) {
					String talk = talkPlanList.get(0);
					talkedList.add(talk);
					return talk;
				}
			}
		}

		if (!isTalkedAboutVote()) {
			setVoteTarget();
			return TemplateTalkFactory.vote(voteTarget);
		}

		return Talk.OVER;
	}

	private boolean isSuspectedMe() {
		for (Talk talk : getTalkList(Topic.ESTIMATE)) {
			Utterance utterance = new Utterance(talk.getContent());
			if (utterance.getTarget() == getMe() && utterance.getRole() == Role.WEREWOLF) {
				return true;
			}
		}
		for (Talk talk : getTalkList(Topic.DIVINED)) {
			Utterance utterance = new Utterance(talk.getContent());
			if (utterance.getTarget() == getMe() && utterance.getResult() == Species.WEREWOLF) {
				return true;
			}
		}
		for (Talk talk : getTalkList(Topic.INQUESTED)) {
			Utterance utterance = new Utterance(talk.getContent());
			if (utterance.getTarget() == getMe() && utterance.getRole() == Role.WEREWOLF) {
				return true;
			}
		}
		return false;
	}

	private void updatePandaAgentList() {
		updateRoleDivinedAgentListMap();
		for (Agent agent : divinedAgentResultListMap.keySet()) {
			if (1 < divinedAgentResultListMap.get(agent).size()) {
				pandaAgentList.add(agent);
			}
		}
	}

	private void updateRoleDivinedAgentListMap() {
		for (Talk talk : this.getTalkList(Topic.DIVINED)) {
			Utterance utterance = new Utterance(talk.getContent());
			Agent target = utterance.getTarget();
			List<Species> resultList = this.divinedAgentResultListMap.get(target);
			if (resultList != null) {
				if (!resultList.contains(utterance.getResult())) {
					resultList.add(utterance.getResult());
					this.divinedAgentResultListMap.put(target, resultList);
				}
			} else {
				resultList = new ArrayList<>();
				resultList.add(utterance.getResult());
				this.divinedAgentResultListMap.put(target, resultList);
			}
		}
	}

	@Override
	public Agent vote() {
		return voteTarget;
	}

	@Override
	public void setVoteTarget() {
		List<Agent> voteCandidates = new ArrayList<>();

		if (!pandaAgentList.isEmpty()) {
			voteCandidates = pandaAgentList;
		}
		removeDeadAgent(voteCandidates);
		voteCandidates.remove(getMe());

		if (!voteCandidates.isEmpty()) {
			voteTarget = randomSelect(voteCandidates);
		} else {
			voteCandidates = this.getLatestDayGameInfo().getAliveAgentList();
			voteCandidates.remove(getMe());
			voteTarget = randomSelect(voteCandidates);
		}
	}

}
