package jp.ac.aitech.k13009kk.aiwolf.client.player.base;

import org.aiwolf.client.base.player.UnsuspectedMethodCallException;
import org.aiwolf.common.data.Agent;

public abstract class AbstractAndoVillager extends AbstractAndoBase {



	@Override
	public Agent attack() {
		throw new UnsuspectedMethodCallException();
	}

	@Override
	public Agent divine() {
		throw new UnsuspectedMethodCallException();
	}

	@Override
	public Agent guard() {
		throw new UnsuspectedMethodCallException();
	}

	@Override
	public String whisper() {
		throw new UnsuspectedMethodCallException();
	}

}
