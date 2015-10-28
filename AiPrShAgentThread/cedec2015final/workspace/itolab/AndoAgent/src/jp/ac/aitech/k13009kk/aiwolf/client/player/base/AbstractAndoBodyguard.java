package jp.ac.aitech.k13009kk.aiwolf.client.player.base;

import java.util.HashMap;
import java.util.Map;

import org.aiwolf.client.base.player.UnsuspectedMethodCallException;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Role;

/**
 * 狩人の基本となる行動
 * @author keisuke 愛知工業大学 K13009 安藤圭祐
 * @version AndoAgent 1.0
 */
public abstract class AbstractAndoBodyguard extends AbstractAndoBase {

	Role myRole;
	private Map<Integer, Agent> dayGuardedAgentMap = new HashMap<>();
	private Map<Integer, Boolean> dayGuardedSuccessMap = new HashMap<>();

	public AbstractAndoBodyguard() {
		this.myRole = Role.BODYGUARD;
	}

	@Override
	public void dayStart() {
		super.dayStart();
		if (this.getGameInfoMap().get(getDay()).getGuardedAgent() != null) {
			Agent guardedAgent = this.getLatestDayGameInfo().getGuardedAgent();
			this.dayGuardedAgentMap.put(getDay() - 1, guardedAgent);
			if (this.getLatestDayGameInfo().getAttackedAgent() == null) {
				this.dayGuardedSuccessMap.put(getDay() - 1, true);
			} else {
				this.dayGuardedSuccessMap.put(getDay() - 1, false);
			}
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
	public final String whisper() {
		throw new UnsuspectedMethodCallException();
	}

	public Map<Integer, Agent> getDayGuardedAgentMap() {
		return dayGuardedAgentMap;
	}

	public Map<Integer, Boolean> getDayGuardedSuccessMap() {
		return dayGuardedSuccessMap;
	}

}
