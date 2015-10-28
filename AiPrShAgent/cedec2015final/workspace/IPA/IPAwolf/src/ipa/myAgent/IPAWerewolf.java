package ipa.myAgent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.aiwolf.client.base.player.AbstractWerewolf;
import org.aiwolf.client.lib.TemplateTalkFactory;
import org.aiwolf.client.lib.TemplateWhisperFactory;
import org.aiwolf.client.lib.Utterance;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Species;
import org.aiwolf.common.data.Talk;
import org.aiwolf.common.net.GameInfo;

public class IPAWerewolf extends AbstractWerewolf {

	int readTalkNum = 0;
	boolean aboutTrueSeer;
	boolean noTalk;
	boolean isComingOut = false;
	boolean toldResult = false;
	boolean toldPossessed = false;
	Role fakeRole = Role.VILLAGER;
	List<Agent> bodyguardCOAgent = new ArrayList<Agent>();
	List<Agent> attackVoteList = new ArrayList<Agent>();
	List<Agent> executeVoteList = new ArrayList<Agent>();

	@Override
	public Agent attack() {
		List<Agent> attackList = new ArrayList<Agent>();
		if(bodyguardCOAgent.size() > 0){
			return selectByPartner(bodyguardCOAgent, attackVoteList);
		}
		else{
		if(COResult.getSeerCOAgent().size() < 3 && COResult.getMediumCOAgent().size() == 1 &&
				getLatestDayGameInfo().getAliveAgentList().contains(COResult.getMediumCOAgent().get(0))){
			return COResult.getMediumCOAgent().get(0);
		}
		else{
			if(COResult.getSeerCOAgent().size() > 1 && toldPossessed){
				attackList.addAll(COResult.getSeerCOAgent());
				for(Agent agent: COResult.getSeerCOAgent()){
					if(!getLatestDayGameInfo().getAliveAgentList().contains(agent)){
						attackList.remove(agent);
					}
				}
				attackList.removeAll(getWolfList());
				attackList.remove(COResult.findPossessed(getLatestDayGameInfo().getAgentList(), getWolfList()));
				if(attackList.size() == 1){
					return attackList.get(0);
				}
			}
			for(Agent seer: COResult.trueSeerForAll()){
				attackList.addAll(COResult.aliveWhite(getLatestDayGameInfo().getAliveAgentList(), seer));
			}
			attackList.removeAll(getWolfList());
			if(attackList.size() > 0){
				return selectByPartner(attackList, attackVoteList);
			}
		}
		attackList.addAll(getLatestDayGameInfo().getAliveAgentList());
		attackList.removeAll(getWolfList());
		if(attackList.size() > 0){
			return selectByPartner(attackList, attackVoteList);
		}
		else{
			return getMe();
		}}
	}

	@Override
	public void dayStart() {
		readTalkNum = 0;
		aboutTrueSeer = false;
		noTalk = false;
		toldResult = false;
		toldPossessed = false;
		attackVoteList.removeAll(attackVoteList);
		executeVoteList.removeAll(executeVoteList);
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
				case BODYGUARD:
					if(talk.getAgent() == utterance.getTarget() && !getWolfList().contains(utterance.getTarget())){
						bodyguardCOAgent.add(utterance.getTarget());
					}
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
				attackVoteList.add(utterance.getTarget());
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
				if(getWolfList().contains(talk.getAgent())){
					executeVoteList.add(utterance.getTarget());
				}
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
	public String talk(){
		if(getDay() == 0){
			return Talk.OVER;
		}
		else{
			if(!isComingOut && getDay() == 1){
				boolean seerCO = false;
				if(!noTalk){
					noTalk = true;
					return Talk.SKIP;
				}
				isComingOut = true;
				for(Agent wolf: getWolfList()){
					if(COResult.getSeerCOAgent().contains(wolf)){
						seerCO = true;
					}
				}
				if(!seerCO && COResult.getSeerCOAgent().size() < 2){
					String comingoutTalk = TemplateTalkFactory.comingout(getMe(), Role.SEER);
					fakeRole = Role.SEER;
					return comingoutTalk;
				}
				else{
					return Talk.SKIP;
				}
			}
			else{
				if(fakeRole == Role.VILLAGER){
					if(COResult.getSeerCOAgent().size() > 1 && COResult.trueSeerForMe(getMe()).size() == 1 && aboutTrueSeer == false){
						aboutTrueSeer = true;
						String trueSeer = TemplateTalkFactory.estimate(COResult.trueSeerForMe(getMe()).get(0), Role.SEER);
						return trueSeer;
					}
					if(!noTalk){
						noTalk = true;
						return Talk.SKIP;
					}
					else{
						return Talk.OVER;
					}
				}
				else if(fakeRole == Role.SEER && !toldResult){
					toldResult = true;
					int numSeer = COResult.getSeerCOAgent().size();
					int numMedium = COResult.getMediumCOAgent().size();
					if(getDay() == 0){
						return Talk.OVER;
					}
					else{
						if(numSeer + numMedium > 3){
							if(numMedium < 2){
								if(getLatestDayGameInfo().getAliveAgentList().size()/2 - getWolfList().size()>1){
									switch(getDay()){
									case 1:
										return whiteResultToWolf();
									case 2:
										return whiteResultToVillager();
									case 3:
										return blackResult();
									default:
										return whiteResultToVillager();
									}
								}
								else{
									return blackResult();
								}
							}
							else{
								if(getLatestDayGameInfo().getAliveAgentList().size()/2 - getWolfList().size()>1){
									switch(getDay()){
									case 1:
										return whiteResultToVillager();
									case 2:
										return blackResult();
									case 3:
										return whiteResultToWolf();
									default:
										return whiteResultToVillager();
									}
								}
								else{
									return blackResult();
								}
							}
						}
						else{
							if(getLatestDayGameInfo().getAliveAgentList().size()/2 - getWolfList().size()>1){
								switch(getDay()){
								case 1:
									return blackResult();
								case 2:
									return whiteResultToWolf();
								case 3:
									return whiteResultToVillager();
								default:
									return whiteResultToVillager();
								}
							}
							else{
								return blackResult();
							}
						}
					}
				}
				else{
					return Talk.OVER;
				}
			}
		}
	}

	@Override
	public Agent vote() {
		List<Agent> voteCandidates = new ArrayList<Agent>();

		voteCandidates.addAll(COResult.aliveBlack(getLatestDayGameInfo().getAliveAgentList(), getMe()));
		if(voteCandidates.size() > 0){
			if(voteCandidates.size() > 1){
				voteCandidates.removeAll(getWolfList());
			}
			if(voteCandidates.size() > 0){
				return selectByPartner(voteCandidates, executeVoteList);
			}
		}

		else{
		for(Agent fake: COResult.fakeSeerForMe(getMe())){
			if(getLatestDayGameInfo().getAliveAgentList().contains(fake)){
				voteCandidates.add(fake);
			}
		}
		if(voteCandidates.size() > 0){
			return selectByPartner(voteCandidates, executeVoteList);
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
			if(voteCandidates.size() > 1){
				voteCandidates.removeAll(getWolfList());
			}
			if(voteCandidates.size() > 0){
				return selectByPartner(voteCandidates, executeVoteList);
			}
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
			if(voteCandidates.size() > 1){
				voteCandidates.removeAll(getWolfList());
			}
			if(voteCandidates.size() > 0){
				return selectByPartner(voteCandidates, executeVoteList);
			}
		}

		else{
		voteCandidates.addAll(COResult.aliveGrayForMe(getLatestDayGameInfo().getAliveAgentList(), getMe()));
		if(voteCandidates.size() > 0){
			if(voteCandidates.size() > 1){
				voteCandidates.removeAll(getWolfList());
			}
			if(voteCandidates.size() > 0){
				return selectByPartner(voteCandidates, executeVoteList);
			}
		}
		}}}}
		voteCandidates.addAll(getLatestDayGameInfo().getAliveAgentList());
		voteCandidates.remove(getMe());
		if(voteCandidates.size() > 0){
			return selectByPartner(voteCandidates, executeVoteList);
		}
		else{
			return getMe();
		}
	}

	@Override
	public String whisper() {
		if(COResult.foundPossessed(getLatestDayGameInfo().getAgentList(), getWolfList()) && !toldPossessed){
			toldPossessed = true;
			Agent possessed = COResult.findPossessed(getLatestDayGameInfo().getAgentList(), getWolfList());
			String foundPossessed = TemplateWhisperFactory.estimate(possessed, Role.POSSESSED);
			return foundPossessed;
		}
		else{
			return Talk.OVER;
		}
	}

	private Agent randomSelect(List<Agent> agentList) {
		int num = new Random().nextInt(agentList.size());
		return agentList.get(num);
	}
	
	private Agent selectByPartner(List<Agent> selectList, List<Agent> partnersSelect){
		List<Agent> candidates = new ArrayList<Agent>();
		for(Agent agent: selectList){
			if(partnersSelect.contains(agent)){
				candidates.add(agent);
			}
		}
		if(candidates.size() > 0){
			return randomSelect(candidates);
		}
		else{
			return randomSelect(selectList);
		}
	}

	private String blackResult(){
		Agent target;
		List<Agent> blackList = new ArrayList<Agent>();
		blackList.addAll(COResult.aliveGrayForAll(getLatestDayGameInfo().getAliveAgentList()));
		blackList.removeAll(getWolfList());
		if(blackList.size() > 0){
			target = randomSelect(blackList);
		}
		else{
			blackList.addAll(COResult.myGrayForSeer(getLatestDayGameInfo().getAliveAgentList(), getMe()));
			blackList.removeAll(getWolfList());
			if(blackList.size() > 0){
				target = randomSelect(blackList);
			}
			else{
				blackList.addAll(COResult.getSeerCOAgent());
				blackList.removeAll(COResult.getAttackedAgent());
				blackList.removeAll(COResult.getExecutedAgent());
				blackList.remove(getMe());
				if(blackList.size() > 0){
					target = randomSelect(blackList);
				}
				else{
					List<Agent> whiteList = new ArrayList<Agent>();
					for(Agent wolf: getWolfList()){
						if(COResult.myGrayForSeer(getLatestDayGameInfo().getAliveAgentList(), getMe()).contains(wolf)){
							whiteList.add(wolf);
						}
					}
					whiteList.remove(getMe());
					if(whiteList.size() > 0){
						String resultTalk = TemplateTalkFactory.divined(randomSelect(whiteList), Species.HUMAN);
						return resultTalk;
					}
					else{
						whiteList.addAll(COResult.myGrayForSeer(getLatestDayGameInfo().getAliveAgentList(), getMe()));
						if(whiteList.size() > 0){
							String resultTalk = TemplateTalkFactory.divined(randomSelect(whiteList), Species.HUMAN);
							return resultTalk;
						}
						else{
							return TemplateTalkFactory.divined(getMe(), Species.HUMAN);
						}
					}
				}
			}
		}
		String resultTalk = TemplateTalkFactory.divined(target, Species.WEREWOLF);
		return resultTalk;
	}

	private String whiteResultToVillager(){
		List<Agent> aliveGray = new ArrayList<Agent>();
		aliveGray.addAll(COResult.myGrayForSeer(getLatestDayGameInfo().getAliveAgentList(), getMe()));
		if(getLatestDayGameInfo().getAttackedAgent() != null){
			aliveGray.add(getLatestDayGameInfo().getAttackedAgent());
		}
		aliveGray.remove(getWolfList());
		if(aliveGray.size() > 0){
			String resultTalk = TemplateTalkFactory.divined(randomSelect(aliveGray), Species.HUMAN);
			return resultTalk;
		}
		else{
			return blackResult();
		}
	}

	private String whiteResultToWolf(){
		List<Agent> aliveWolf = new ArrayList<Agent>();
		for(Agent wolf: getWolfList()){
			if(getLatestDayGameInfo().getAliveAgentList().contains(wolf)){
				aliveWolf.add(wolf);
			}
		}
		aliveWolf.remove(getMe());
		if(aliveWolf.size() > 0){
			String resultTalk = TemplateTalkFactory.divined(randomSelect(aliveWolf), Species.HUMAN);
			return resultTalk;
		}
		else{
			return whiteResultToVillager();
		}
	}

}
