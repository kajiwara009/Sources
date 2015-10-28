package ipa.myAgent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.aiwolf.client.base.player.AbstractVillager;
import org.aiwolf.client.lib.TemplateTalkFactory;
import org.aiwolf.client.lib.Utterance;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Talk;
import org.aiwolf.common.net.GameInfo;

public class IPAVillager extends AbstractVillager {

	int readTalkNum = 0;
	boolean aboutTrueSeer = false;
	boolean aboutTrueMedium = false;
	boolean noTalk = false;

	@Override
	public void dayStart(){
		readTalkNum = 0;
		aboutTrueSeer = false;
		aboutTrueMedium = false;
		noTalk = false;
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

	@Override
	public Agent vote() {
		List<Agent> voteCandidates = new ArrayList<Agent>();
		
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
		}}}}
	}

	private Agent randomSelect(List<Agent> agentList) {
		int num = new Random().nextInt(agentList.size());
		return agentList.get(num);
	}


}
