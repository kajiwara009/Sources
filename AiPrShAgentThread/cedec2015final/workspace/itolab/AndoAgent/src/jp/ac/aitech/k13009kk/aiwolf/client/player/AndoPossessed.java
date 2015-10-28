package jp.ac.aitech.k13009kk.aiwolf.client.player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.aiwolf.client.lib.TemplateTalkFactory;
import org.aiwolf.client.lib.Topic;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Judge;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Species;
import org.aiwolf.common.data.Talk;

import jp.ac.aitech.k13009kk.aiwolf.client.player.base.AbstractAndoPossessed;

/**
 * 狂人の行動
 * @author keisuke 愛知工業大学 K13009 安藤圭祐
 * @version AndoAgent 1.0
 */
public class AndoPossessed extends AbstractAndoPossessed {

	private ArrayList<Judge> fakeJudgeList = new ArrayList<>();
	private ArrayList<Agent> judgedAgentList = new ArrayList<>();

	@Override
	public void dayStart() {
		super.dayStart();
		List<Agent> judgeCandidates = this.getLatestDayGameInfo().getAliveAgentList();
		judgeCandidates.removeAll(judgedAgentList);
		if (!judgeCandidates.isEmpty()) {
			Agent fakeJudgeTarget = randomSelect(judgeCandidates);
			Species fakeJudgeResult = new Random().nextBoolean() ? Species.HUMAN : Species.WEREWOLF;
			Judge fakeJudge = new Judge(getDay(), getMe(), fakeJudgeTarget, fakeJudgeResult);
			judgedAgentList.add(fakeJudgeTarget);
			fakeJudgeList.add(fakeJudge);
		}
	}

	@Override
	public String talk() {
		if (!isFakeComingout) {
			isFakeComingout = true;
			String fakeSeerCO = TemplateTalkFactory.comingout(getMe(), Role.SEER);
			return fakeSeerCO;
		} else {
			Judge fakeJudge = null;
			if (!fakeJudgeList.isEmpty()) {
				fakeJudge = fakeJudgeList.get(0);
			}
			if (fakeJudge != null) {
				fakeJudgeList.remove(0);
				return TemplateTalkFactory.divined(fakeJudge.getTarget(), fakeJudge.getResult());
			}
		}

		if(!isTalkedAboutVote()){
			setVoteTarget();
			return TemplateTalkFactory.vote(voteTarget);
		}

		return Talk.OVER;
	}

	@Override
	public Agent vote() {
		return voteTarget;
	}

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
	public void setVoteTarget() {
		List<Agent> voteCandidates = new ArrayList<>();
		if (!getDivinedCorrectlyAgentList().isEmpty()) {
			voteCandidates = getDivinedCorrectlyAgentList();
		}
		removeDeadAgent(voteCandidates);
		voteCandidates.remove(getMe());
		if (!voteCandidates.isEmpty()) {
			voteTarget = randomSelect(voteCandidates);
		} else {
			voteCandidates = getLatestDayGameInfo().getAliveAgentList();
			voteCandidates.remove(getMe());
			voteTarget = randomSelect(voteCandidates);
		}
	}
}
