package jp.ac.cu.hiroshima.info.cm.nakamura.base.player;

import org.aiwolf.client.base.player.UnsuspectedMethodCallException;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Role;

public abstract class NoriAbstractBodyguard extends NoriAbstractRole{

	@Override
	public abstract void dayStart();

	@Override
	public abstract String talk();

	@Override
	final public String whisper(){
		throw new UnsuspectedMethodCallException();
	}

	@Override
	public abstract Agent vote();

	@Override
	final public Agent attack(){
		throw new UnsuspectedMethodCallException();
	}

	@Override
	final public Agent divine(){
		throw new UnsuspectedMethodCallException();
	}

	@Override
	public abstract Agent guard();

	@Override
	public abstract void finish();

	public NoriAbstractBodyguard(){
		myRole = Role.BODYGUARD;
	}



}
