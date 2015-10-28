package org.aiwolf.kajiPlayer.profitSharing;

import org.aiwolf.common.data.Role;
import org.aiwolf.laern.lib.Action;

public class PrShSeer extends PrShBasePlayer{

	public PrShSeer() {
		super(Role.SEER);
	}
	
	
	@Override
	public String talk() {
		return getDefaultTalk();
	}


}
