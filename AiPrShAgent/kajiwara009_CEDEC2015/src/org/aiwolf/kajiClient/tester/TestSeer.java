package org.aiwolf.kajiClient.tester;


import java.util.ArrayList;
import java.util.List;

import org.aiwolf.client.base.player.AbstractSeer;
import org.aiwolf.client.lib.TemplateTalkFactory;
import org.aiwolf.client.lib.TemplateTalkFactory.TalkType;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Talk;
import org.aiwolf.common.net.GameInfo;

public class TestSeer extends AbstractSeer {

	private List<Agent> nakama = new ArrayList<Agent>();
	
	public TestSeer() {
		// TODO 自動生成されたコンストラクター・スタブ
	}

	@Override
	public String talk() {
		
		for(Talk talk: getLatestDayGameInfo().getTalkList()){
			if(nakama.contains(talk.getAgent())){
			TemplateTalkFactory.agree(TalkType.TALK,talk.getDay(), talk.getIdx());
			}
		}
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	public Agent vote() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	public Agent divine() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	public void finish() {
		// TODO 自動生成されたメソッド・スタブ

	}
	
	@Override
	public void update(GameInfo gameInfo){
		super.update(gameInfo);
	}

}
