package ipa.myAgent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.aiwolf.client.base.player.AbstractPossessed;
import org.aiwolf.client.lib.TemplateTalkFactory;
import org.aiwolf.client.lib.Utterance;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Species;
import org.aiwolf.common.data.Talk;
import org.aiwolf.common.net.GameInfo;

public class IPAPossessed extends AbstractPossessed {

	boolean isComingOut = false;
	boolean toldResult = false;
	Role fakeRole = Role.VILLAGER;
	int readTalkNum = 0;
	List<Agent> wolfCOAgent = new ArrayList<Agent>();

	@Override
	public void dayStart() {
		readTalkNum = 0;
		toldResult = false;
		if(getLatestDayGameInfo().getExecutedAgent() != null){
			Agent executed = getLatestDayGameInfo().getExecutedAgent();
			COResult.setExecutedAgent(executed);
		}
		if(getLatestDayGameInfo().getAttackedAgent() != null){
			Agent attacked = getLatestDayGameInfo().getAttackedAgent();
			COResult.setAttackedAgent(attacked);
		}
	}

	@Override
	public void update(GameInfo gameInfo){
		super.update(gameInfo);
		List<Talk> talkList = gameInfo.getTalkList();

		for(int i = readTalkNum; i < talkList.size(); i++){
			Talk talk  = talkList.get(i);
			Utterance utterance = new Utterance(talk.getContent());

			switch(utterance.getTopic()){
			case COMINGOUT:
				switch(utterance.getRole()){
				case SEER:
					if(talk.getAgent() == utterance.getTarget()){
						COResult.setSeerCOAgent(utterance.getTarget());
					}
					break;
				case MEDIUM:
					if(talk.getAgent() == utterance.getTarget()){
						COResult.setMediumCOAgent(utterance.getTarget());
					}
					break;
/*				case BODYGUARD:
					bodyguardCOAgent.add(utterance.getTarget());
					break;*/
				case WEREWOLF:
					wolfCOAgent.add(utterance.getTarget());
					break;
				default:
					break;
				}
				break;
			case DIVINED:
				COResult.setDevineResult(talk.getAgent(), utterance.getTarget(), utterance.getResult());
				break;
			case AGREE:
				break;
			case ATTACK:
				break;
			case DISAGREE:
				break;
			case ESTIMATE:
				break;
			case GUARDED:
				break;
			case INQUESTED:
				COResult.setInquestResult(talk.getAgent(), utterance.getTarget(), utterance.getResult());
				break;
			case OVER:
				break;
			case SKIP:
				break;
			case VOTE:
				break;
			default:
				break;
			}
			readTalkNum++;
		}
	}

	@Override
	public void finish() {
		COResult.deleteAll();
	}

	@Override
	public String talk() {
		if(!isComingOut){
			if(COResult.getSeerCOAgent().size() < 3){
				String comingoutTalk = TemplateTalkFactory.comingout(getMe(), Role.SEER);
				isComingOut = true;
				fakeRole = Role.SEER;
				return comingoutTalk;
			}
			else{
				String comingoutTalk = TemplateTalkFactory.comingout(getMe(), Role.MEDIUM);
				isComingOut = true;
				fakeRole = Role.MEDIUM;
				return comingoutTalk;
			}
		}
		else if(!toldResult){
			toldResult = true;
			switch(fakeRole){
			case MEDIUM:
				if(getLatestDayGameInfo().getExecutedAgent() != null && getDay() > 1){
					Agent target = getLatestDayGameInfo().getExecutedAgent();
					if(target != null){
						String resultTalk = TemplateTalkFactory.inquested(target, Species.WEREWOLF);
						return resultTalk;
					}
				}
				break;
			case SEER:
				if(getDay() > 0){
					List<Agent> divineCandidates = new ArrayList<Agent>();
					divineCandidates.addAll(COResult.aliveGrayForMe(getLatestDayGameInfo().getAliveAgentList(), getMe()));
					if(divineCandidates.size() > 0){
						String resultTalk;
						if(getDay() == 1){
							resultTalk = TemplateTalkFactory.divined(randomSelect(divineCandidates), Species.WEREWOLF);
						}
						else{
							if(getLatestDayGameInfo().getAliveAgentList().size() > 10){
								if(getLatestDayGameInfo().getAttackedAgent() != null){
									divineCandidates.add(getLatestDayGameInfo().getAttackedAgent());
								}
								resultTalk = TemplateTalkFactory.divined(randomSelect(divineCandidates), Species.HUMAN);
							}
							else{
								divineCandidates.removeAll(divineCandidates);
								for(Agent seer: COResult.getSeerCOAgent()){
									if(seer != getMe()){
										for(Agent white: COResult.aliveWhite(getLatestDayGameInfo().getAliveAgentList(), seer)){
											if(COResult.aliveGrayForMe(getLatestDayGameInfo().getAliveAgentList(), getMe()).contains(white)){
												divineCandidates.add(white);
											}
										}
									}
								}
								if(divineCandidates.size() > 0){
									resultTalk = TemplateTalkFactory.divined(randomSelect(divineCandidates), Species.WEREWOLF);
								}
								else{
									divineCandidates.addAll(COResult.aliveGrayForMe(getLatestDayGameInfo().getAliveAgentList(), getMe()));
									if(divineCandidates.size() > 0){
										resultTalk = TemplateTalkFactory.divined(randomSelect(divineCandidates), Species.WEREWOLF);
									}
									else{
										if(getLatestDayGameInfo().getAttackedAgent() != null){
											resultTalk = TemplateTalkFactory.divined(getLatestDayGameInfo().getAttackedAgent(), Species.HUMAN);
										}
										else{
											resultTalk = TemplateTalkFactory.divined(getMe(), Species.HUMAN);
										}
									}
								}
							}
						}
						return resultTalk;
					}
					else{
						if(getLatestDayGameInfo().getAttackedAgent() != null){
							return TemplateTalkFactory.divined(getLatestDayGameInfo().getAttackedAgent(), Species.HUMAN);
						}
						return TemplateTalkFactory.divined(getMe(), Species.HUMAN);
					}
				}
				break;
			default:
				break;

			}
		}
		return Talk.OVER;
	}

	@Override
	public Agent vote() {
		List<Agent> voteCandidates = new ArrayList<Agent>();

		if(wolfCOAgent.size() + 1 > getLatestDayGameInfo().getAliveAgentList().size()/2){
			voteCandidates.addAll(getLatestDayGameInfo().getAliveAgentList());
			voteCandidates.removeAll(wolfCOAgent);
			voteCandidates.remove(getMe());
		}
		if(voteCandidates.size() > 0){
			return randomSelect(voteCandidates);
		}

		else{
		voteCandidates.addAll(COResult.aliveBlack(getLatestDayGameInfo().getAliveAgentList(), getMe()));
		if(voteCandidates.size() > 0){
			return randomSelect(voteCandidates);
		}

		else{
		for(Agent fake: COResult.fakeSeerForMe(getMe())){
			if(getLatestDayGameInfo().getAliveAgentList().contains(fake)){
				voteCandidates.add(fake);
			}
		}
		if(voteCandidates.size() > 0){
			return randomSelect(voteCandidates);
		}

		else{
		if(COResult.getMediumCOAgent().size() > 1){
			for(Agent medium: COResult.getMediumCOAgent()){
				if(getLatestDayGameInfo().getAliveAgentList().contains(medium)){
					voteCandidates.add(medium);
				}
			}
		}
		if(voteCandidates.size() > 0){
			return randomSelect(voteCandidates);
		}

		else{
		boolean voteSeer = false;
		for(Agent seer: COResult.getSeerCOAgent()){
			if(COResult.getAttackedAgent().contains(seer)){
				voteSeer = true;
			}
		}
		if(voteSeer && COResult.trueSeerForMe(getMe()).size() > 1){
			for(Agent seer: COResult.getSeerCOAgent()){
				if(getLatestDayGameInfo().getAliveAgentList().contains(seer)){
					voteCandidates.add(seer);
				}
			}
		}
		if(voteCandidates.size() > 0){
			return randomSelect(voteCandidates);
		}

		else{
		voteCandidates.addAll(COResult.aliveGrayForMe(getLatestDayGameInfo().getAliveAgentList(), getMe()));
		if(voteCandidates.size() > 0){
			return randomSelect(voteCandidates);
		}
		else{
			voteCandidates.addAll(getLatestDayGameInfo().getAliveAgentList());
			voteCandidates.remove(getMe());
			return randomSelect(voteCandidates);
		}
		}}}}}
	}

	private Agent randomSelect(List<Agent> agentList) {
		int num = new Random().nextInt(agentList.size());
		return agentList.get(num);
	}


}
