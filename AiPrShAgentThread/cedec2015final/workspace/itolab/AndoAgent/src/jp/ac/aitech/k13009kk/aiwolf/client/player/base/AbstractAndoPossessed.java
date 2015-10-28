package jp.ac.aitech.k13009kk.aiwolf.client.player.base;

import org.aiwolf.client.base.player.UnsuspectedMethodCallException;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Role;

/**
 * 狂人の基本となる行動
 * @author keisuke 愛知工業大学 K13009 安藤圭祐
 * @version AndoAgent 1.0
 *
 */
public abstract class AbstractAndoPossessed extends AbstractAndoBase {

	protected Role myRole;
	protected boolean isFakeComingout = false;

	public AbstractAndoPossessed() {
		myRole = Role.POSSESSED;
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
