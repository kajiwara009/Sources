package com.gmail.yusatk.data;

import java.util.List;

import com.gmail.yusatk.interfaces.IWorld;
import com.gmail.yusatk.interfaces.IWorldCache;

public class WorldCache implements IWorldCache {
	List<IWorld> cache = null;
	@Override
	public List<IWorld> getWorlds() {
		return cache;
	}

	@Override
	public void setWorlds(List<IWorld> worlds) {
		this.cache = worlds;
	}
	
}
