package jp.ac.aitech.k13009kk.aiwolf.client.player.base;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.aiwolf.client.base.player.UnsuspectedMethodCallException;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Role;

/**
 * 人狼の基本となる行動
 * @author keisuke 愛知工業大学 K13009 安藤圭祐
 * @version AndoAgent 1.0
 *
 */
public abstract class AbstractAndoWerewolf extends AbstractAndoBase {

	Role myRole;

	public AbstractAndoWerewolf() {
		this.myRole = Role.WEREWOLF;
	}

	@Override
	public final Agent divine() {
		throw new UnsuspectedMethodCallException();
	}

	@Override
	public final Agent guard() {
		throw new UnsuspectedMethodCallException();
	}

	/**
	 * 自分を含めた人狼のエージェントのリストを返す
	 * @return 自分を含めた人狼のエージェントのリスト
	 */
	public List<Agent> getWolfList() {
		List<Agent> wolfList = new ArrayList<>();
		Map<Agent, Role> wolfMap = getLatestDayGameInfo().getRoleMap();
		for (Map.Entry<Agent, Role> set : wolfMap.entrySet()) {
			if (set.getValue() == Role.WEREWOLF) {
				wolfList.add((Agent) set.getKey());
			}
		}
		return wolfList;
	}
}
