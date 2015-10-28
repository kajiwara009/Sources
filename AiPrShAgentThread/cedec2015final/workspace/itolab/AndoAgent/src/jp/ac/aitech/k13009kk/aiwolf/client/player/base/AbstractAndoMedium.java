package jp.ac.aitech.k13009kk.aiwolf.client.player.base;

import java.util.ArrayList;

import org.aiwolf.client.base.player.UnsuspectedMethodCallException;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Judge;
import org.aiwolf.common.data.Role;

/**
 * 霊能者の基本となる行動
 * @author keisuke 愛知工業大学 K13009 安藤圭祐
 * @version AndoAgent 1.0
 *
 */
public abstract class AbstractAndoMedium extends AbstractAndoBase {

	Role myRole;
	private ArrayList<Judge> myJudgeList = new ArrayList<>();

	public AbstractAndoMedium() {
		this.myRole = Role.MEDIUM;
	}


	public ArrayList<Judge> getMyJudgeList() {
		return this.myJudgeList;
	}

	/**
	 *指定されたエージェントがすでに霊能済みかどうかを返します。
	 * @param agent 霊能済みかどうかが返されるエージェント
	 * @return 指定されたエージェントが霊能済みならtrue、そうでない場合はfalse
	 */
	public boolean isJudgedAgent(Agent agent) {
		for (Judge judge : this.myJudgeList) {
			if (judge.getTarget() == agent) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void dayStart() {
		super.dayStart();
		updateMyJudgeList();
	}


	private void updateMyJudgeList() {
		if (this.getGameInfoMap().get(getDay()).getMediumResult() != null) {
			this.myJudgeList.add(getLatestDayGameInfo().getMediumResult());
		}
	}

	@Override
	public final Agent attack() {
		throw new UnsuspectedMethodCallException();
	}

	@Override
	public final Agent divine() {
		throw new UnsuspectedMethodCallException();
	}

	@Override
	public final Agent guard() {
		throw new UnsuspectedMethodCallException();
	}

	@Override
	public final String whisper() {
		throw new UnsuspectedMethodCallException();
	}

}
