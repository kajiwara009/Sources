package jp.ac.aitech.k13009kk.aiwolf.client.player;

import java.util.ArrayList;
import java.util.List;

import org.aiwolf.client.lib.TemplateTalkFactory;
import org.aiwolf.client.lib.Topic;
import org.aiwolf.client.lib.Utterance;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Judge;
import org.aiwolf.common.data.Talk;
import org.aiwolf.common.net.GameInfo;

import jp.ac.aitech.k13009kk.aiwolf.client.player.base.AbstractAndoSeer;

/**
 * 占い師の行動
 * @author keisuke 愛知工業大学 K13009 安藤圭祐
 * @version AndoAgent 1.0
 *
 */
public class AndoSeer extends AbstractAndoSeer {

	private List<Judge> talkedJudgeList = new ArrayList<>();

	public AndoSeer() {
		super();
		this.comingoutDay = 0;
	}

	@Override
	public Agent divine() {
		List<Agent> divineCandidates = new ArrayList<>();

		this.getFakeSeerList().forEach(agent -> {
			if (this.getLatestDayGameInfo().getAliveAgentList().contains(agent)) {
				if (!this.isJudgeAgent(agent)) {
					divineCandidates.add(agent);
				}
			}
		});
		if (0 < divineCandidates.size()) {
			divineCandidates.remove(getMe());
			return randomSelect(divineCandidates);
		}

		this.getGrayAgentList().forEach(agent -> {
			if (this.getLatestDayGameInfo().getAliveAgentList().contains(agent)) {
				if (!this.isJudgeAgent(agent)) {
					divineCandidates.add(agent);
				}
			}
		});
		divineCandidates.remove(getMe());
		if (!divineCandidates.isEmpty()) {
			return randomSelect(divineCandidates);
		}

		return randomSelect(this.getLatestDayGameInfo().getAliveAgentList());
	}

	@Override
	public void finish() {
		System.out.printf("========%s Talk========\n", getMe());
		this.getTalkList(getMe()).forEach(talk -> System.out.println(talk));
	}

	@Override
	public String talk() {
		if (!isComingout) {
			if (getDay() == comingoutDay) {
				isComingout = true;
				return TemplateTalkFactory.comingout(getMe(), getMyRole());
			}
		} else {
			for (Judge judge : this.getMyJudgeList()) {
				if (!talkedJudgeList.contains(judge)) {
					talkedJudgeList.add(judge);
					return TemplateTalkFactory.divined(judge.getTarget(), judge.getResult());
				}
			}
		}
		for (Talk talk : getTalkList(getDay())) {
			if (talk.getAgent() == getMe() && new Utterance(talk.getContent()).getTopic() == Topic.VOTE) {
				return Talk.OVER;
			}
		}
		return TemplateTalkFactory.vote(selectVote());
	}

	@Override
	public Agent vote() {
		return selectVote();
	}

	private Agent selectVote() {
		List<Agent> voteCandidates = new ArrayList<>();
		voteCandidates.addAll(this.removeDeadAgent(getBlackAgentList()));
		if (0 < voteCandidates.size()) {
			return randomSelect(voteCandidates);
		}

		voteCandidates.addAll(this.removeDeadAgent(getFakeSeerList()));
		voteCandidates.removeAll(getWhiteAgentList());
		if (0 < voteCandidates.size()) {
			return randomSelect(voteCandidates);
		}

		return randomSelect(getGrayAgentList());
	}

	@Override
	public void update(GameInfo gameInfo) {
		super.update(gameInfo);
	}

	@Override
	public void setVoteTarget() {
	}

}
