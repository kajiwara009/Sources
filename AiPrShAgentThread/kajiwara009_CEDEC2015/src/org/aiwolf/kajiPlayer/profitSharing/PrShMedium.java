package org.aiwolf.kajiPlayer.profitSharing;

import org.aiwolf.common.data.Role;

public class PrShMedium extends PrShBasePlayer{

	public PrShMedium() {
		super(Role.MEDIUM);
	}
	
	
	@Override
	public String talk() {
		return getDefaultTalk();
	}



}
