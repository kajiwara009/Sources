package jp.ac.aitech.k13009kk.aiwolf.client.player.base;

import java.util.ArrayList;
import java.util.List;

import org.aiwolf.client.base.player.UnsuspectedMethodCallException;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Judge;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.net.GameInfo;
import org.aiwolf.common.net.GameSetting;

/**
 * 占い師の基本の行動
 * @author keisuke 愛知工業大学 K13009 安藤圭祐
 * @version AndoAgent 1.0
 *
 */
public abstract class AbstractAndoSeer extends AbstractAndoBase {

	Role myRole;
	private ArrayList<Judge> myJudgeList = new ArrayList<>();
	private ArrayList<Agent> whiteAgentList = new ArrayList<>();
	private ArrayList<Agent> blackAgentList = new ArrayList<>();
	private ArrayList<Agent> grayAgentList = new ArrayList<>();

	public AbstractAndoSeer() {
		this.myRole = Role.SEER;
	}

	public ArrayList<Judge> getMyJudgeList() {
		return this.myJudgeList;
	}

	public ArrayList<Agent> getWhiteAgentList() {
		return this.whiteAgentList;
	}

	public ArrayList<Agent> getBlackAgentList() {
		return this.blackAgentList;
	}

	public ArrayList<Agent> getGrayAgentList() {
		return this.grayAgentList;
	}

	public List<Agent> getFakeSeerList() {
		ArrayList<Agent> fakeSeerList = new ArrayList<>();
		getAgentComingoutRoleMap().forEach((agent, comingoutRole) -> {
			if (comingoutRole == Role.SEER && agent != getMe()) {
				if (!fakeSeerList.contains(agent))
					fakeSeerList.add(agent);
			}
		});
		return fakeSeerList;
	}

	/**
	 *指定されたエージェントがすでに占い済みかどうかを返します。
	 * @param agent 占い済みかどうかが返されるエージェント
	 * @return 指定されたエージェントが占い済みならtrue、そうでない場合はfalse
	 */
	public boolean isJudgeAgent(Agent agent) {
		for (Judge judge : this.myJudgeList) {
			if (judge.getTarget() == agent) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void initialize(GameInfo gameInfo, GameSetting gameSetting) {
		super.initialize(gameInfo, gameSetting);
		this.grayAgentList.addAll(gameInfo.getAgentList());
	}

	@Override
	public void dayStart() {
		super.dayStart();
		updateMyJudgeList();
		divideBlackOrWhite();
	}

	private void divideBlackOrWhite() {
		if (getGameInfo(getDay()).getDivineResult() != null) {
			Judge divineResult = this.getLatestDayGameInfo().getDivineResult();
			switch (divineResult.getResult()) {
			case HUMAN:
				whiteAgentList.add(divineResult.getTarget());
				break;
			case WEREWOLF:
				blackAgentList.add(divineResult.getTarget());
				break;
			}
			grayAgentList.remove(divineResult.getTarget());
		}
	}

	private void updateMyJudgeList() {
		if (getGameInfo(getDay()).getDivineResult() != null) {
			myJudgeList.add(this.getLatestDayGameInfo().getDivineResult());
		}
	}

	public final String whisper() {
		throw new UnsuspectedMethodCallException();
	}

	public final Agent attack() {
		throw new UnsuspectedMethodCallException();
	}

	public final Agent guard() {
		throw new UnsuspectedMethodCallException();
	}

	@Override
	public void update(GameInfo gameInfo) {
		super.update(gameInfo);
	}
}
