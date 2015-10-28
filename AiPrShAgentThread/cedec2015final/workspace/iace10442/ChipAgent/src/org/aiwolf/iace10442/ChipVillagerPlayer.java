package org.aiwolf.iace10442;

import org.aiwolf.client.lib.TemplateTalkFactory;
import org.aiwolf.common.data.Talk;
import org.aiwolf.common.net.GameInfo;
import org.aiwolf.common.net.GameSetting;

public class ChipVillagerPlayer extends AbstractChipBasePlayer {
	
	private boolean is_comingout;

	@Override
	public void initialize(GameInfo gameinfo, GameSetting gamesetting) {
		super.initialize(gameinfo, gamesetting);
		is_comingout = false;
	}
	
	
	@Override
	public String talk() {
		if( is_comingout == false ) {
			if( will_explanation == true ) {
				will_explanation = false;
				is_comingout = true;
				return TemplateTalkFactory.comingout(getMe(), getMyRole() );
			}
			if( (getLatestDayGameInfo().getAliveAgentList().size() < 8 ) ) {
				is_comingout = true;
				return TemplateTalkFactory.comingout(getMe(), getMyRole() );
			}
		}
		
		return Talk.OVER;
	}
	
	
}
