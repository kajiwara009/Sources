package ipa.myAgent;



import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.aiwolf.client.lib.TemplateTalkFactory;
import org.aiwolf.client.lib.TemplateTalkFactory.TalkType;
import org.aiwolf.client.lib.TemplateWhisperFactory;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Player;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Species;
import org.aiwolf.common.net.GameInfo;
import org.aiwolf.common.net.GameSetting;

public class RandomPlayer implements Player {
	Map<Integer, GameInfo> gameInfoMap = new HashMap<Integer, GameInfo>();

	int day;

	Agent me;

	Role myRole;

	GameSetting gameSetting;

	List<Agent> aliveAgent;

	@Override
	public Agent attack() {
		Collections.shuffle(aliveAgent);
		return aliveAgent.get(0);
	}

	@Override
	public void dayStart() {
		this.aliveAgent = this.getLatestDayGameInfo().getAliveAgentList();
		this.aliveAgent.remove(this.getLatestDayGameInfo().getAgent());
	}

	@Override
	public Agent divine() {
		Collections.shuffle(aliveAgent);
		return aliveAgent.get(0);
	}

	@Override
	public void finish() {

	}

	@Override
	public String getName() {
		return "RandomAgent";
	}

	@Override
	public Agent guard() {
		Collections.shuffle(aliveAgent);
		return aliveAgent.get(0);
	}

	@Override
	public void initialize(GameInfo gameInfo, GameSetting gameSetting) {
		gameInfoMap.clear();
		this.gameSetting = gameSetting;
		day = gameInfo.getDay();
		gameInfoMap.put(day, gameInfo);
		myRole = gameInfo.getRole();
		me = gameInfo.getAgent();
		return;

	}

	public GameInfo getLatestDayGameInfo(){
		return gameInfoMap.get(day);
	}

	@Override
	public String talk() {
		Random r = new Random();

		TalkType[] talkTypes = TalkType.values();
		List<Agent> allAgent = this.getLatestDayGameInfo().getAgentList();
		Collections.shuffle(allAgent);
		Species[] species = Species.values();
		Role[] roles = Role.values();

		switch(r.nextInt(10)){

		case 0:
			return TemplateTalkFactory.agree(talkTypes[r.nextInt(talkTypes.length)], r.nextInt(100), r.nextInt(100));
		case 1:
			return TemplateTalkFactory.comingout(allAgent.get(0), roles[r.nextInt(roles.length)]);
		case 2:
			return TemplateTalkFactory.disagree(talkTypes[r.nextInt(talkTypes.length)], r.nextInt(100), r.nextInt(100));
		case 3:
			return TemplateTalkFactory.divined(allAgent.get(0), species[r.nextInt(species.length)]);
		case 4:
			return TemplateTalkFactory.estimate(allAgent.get(0), roles[r.nextInt(roles.length)]);
		case 5:
			return TemplateTalkFactory.guarded(allAgent.get(0));
		case 6:
			return TemplateTalkFactory.inquested(allAgent.get(0), species[r.nextInt(species.length)]);
		case 7:
			return TemplateTalkFactory.over();
		case 8:
			return TemplateTalkFactory.skip();
		case 9:
			return TemplateTalkFactory.vote(allAgent.get(0));
		}
		return TemplateTalkFactory.over();
	}

	@Override
	public void update(GameInfo gameInfo) {
		day = gameInfo.getDay();

		gameInfoMap.put(day, gameInfo);
	}

	@Override
	public Agent vote() {
		Collections.shuffle(aliveAgent);
		return aliveAgent.get(0);
	}

	@Override
	public String whisper() {
		Random r = new Random();

		TalkType[] talkTypes = TalkType.values();
		List<Agent> allAgent = this.getLatestDayGameInfo().getAgentList();
		Collections.shuffle(allAgent);
		Species[] species = Species.values();
		Role[] roles = Role.values();


		switch(r.nextInt(10)){

		case 0:
			return TemplateWhisperFactory.agree(talkTypes[r.nextInt(talkTypes.length)], r.nextInt(100), r.nextInt(100));
		case 1:
			return TemplateWhisperFactory.comingout(allAgent.get(0), roles[r.nextInt(roles.length)]);
		case 2:
			return TemplateWhisperFactory.disagree(talkTypes[r.nextInt(talkTypes.length)], r.nextInt(100), r.nextInt(100));
		case 3:
			return TemplateWhisperFactory.divined(allAgent.get(0), species[r.nextInt(species.length)]);
		case 4:
			return TemplateWhisperFactory.estimate(allAgent.get(0), roles[r.nextInt(roles.length)]);
		case 5:
			return TemplateWhisperFactory.guarded(allAgent.get(0));
		case 6:
			return TemplateWhisperFactory.inquested(allAgent.get(0), species[r.nextInt(species.length)]);
		case 7:
			return TemplateWhisperFactory.over();
		case 8:
			return TemplateWhisperFactory.skip();
		case 9:
			return TemplateWhisperFactory.vote(allAgent.get(0));
		case 10:
			return TemplateWhisperFactory.attack(allAgent.get(0));
		}


		return TemplateTalkFactory.over();
	}

}
