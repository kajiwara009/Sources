package ipa.myAgent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.aiwolf.client.base.player.AbstractSeer;
import org.aiwolf.client.lib.TemplateTalkFactory;
import org.aiwolf.client.lib.Utterance;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Judge;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Talk;
import org.aiwolf.common.net.GameInfo;

public class IPASeer extends AbstractSeer {

	boolean isComingOut = false;
	int readTalkNum = 0;
	List<Judge> myToldJudgeList = new ArrayList<Judge>();
	List<Agent> fakeSeerCOAgent = new ArrayList<Agent>();

	@Override
	public void dayStart(){
		super.dayStart();
		readTalkNum = 0;
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
	public Agent divine() {
		List<Agent> divineCandidates = new ArrayList<Agent>();
		divineCandidates.addAll(COResult.aliveGrayForMe(getLatestDayGameInfo().getAliveAgentList(), getMe()));
		divineCandidates.removeAll(COResult.aliveBlack(getLatestDayGameInfo().getAliveAgentList(), getMe()));
		for(Judge judge: getMyJudgeList()){
			divineCandidates.remove(judge.getTarget());
		}
		if(divineCandidates.size() > 0){
			return randomSelect(divineCandidates);
		}
		else{
			divineCandidates.addAll(getLatestDayGameInfo().getAliveAgentList());
			divineCandidates.removeAll(COResult.aliveBlack(getLatestDayGameInfo().getAliveAgentList(), getMe()));
			for(Judge judge: getMyJudgeList()){
				divineCandidates.remove(judge.getTarget());
			}
			if(divineCandidates.size() > 0){
				return randomSelect(divineCandidates);
			}
			else{
				divineCandidates.addAll(COResult.aliveWhite(getLatestDayGameInfo().getAliveAgentList(), getMe()));
				for(Judge judge: getMyJudgeList()){
					divineCandidates.remove(judge.getTarget());
				}
				if(divineCandidates.size() > 0){
					return randomSelect(divineCandidates);
				}
				else{
					for(Agent seer: COResult.getSeerCOAgent()){
						divineCandidates.add(seer);
					}
					divineCandidates.remove(getMe());
					for(Judge judge: getMyJudgeList()){
						divineCandidates.remove(judge.getTarget());
					}
					if(divineCandidates.size() > 0){
						return randomSelect(divineCandidates);
					}
					else{
						return getMe();
					}
				}
			}
		}
	}

	@Override
	public void finish() {
		COResult.deleteAll();
	}

	@Override
	public String talk() {
		if(!isComingOut){
			String comingoutTalk = TemplateTalkFactory.comingout(getMe(), getMyRole());
			isComingOut = true;
			return comingoutTalk;
		}
		else{
			for(Judge judge: getMyJudgeList()){
				if(!myToldJudgeList.contains(judge)){
					String resultTalk = TemplateTalkFactory.divined(judge.getTarget(), judge.getResult());
					myToldJudgeList.add(judge);
					return resultTalk;
				}
			}
		}
		return Talk.OVER;
	}

	@Override
	public void update(GameInfo gameInfo){
		super.update(gameInfo);
		List<Talk> talkList = gameInfo.getTalkList();

		for(int i = readTalkNum; i < talkList.size(); i++){
			Talk talk  = talkList.get(i);
			Utterance utterance = new Utterance(talk.getContent());

			switch (utterance.getTopic()){
			case COMINGOUT:
				if(utterance.getRole() == Role.SEER && !talk.getAgent().equals(getMe())){
					fakeSeerCOAgent.add(utterance.getTarget());
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
	public Agent vote() {
		List<Agent> whiteAgent = new ArrayList<Agent>();
		List<Agent> blackAgent = new ArrayList<Agent>();
		for(Judge judge: getMyJudgeList()){
			if(getLatestDayGameInfo().getAliveAgentList().contains(judge.getTarget())){
				switch(judge.getResult()){
				case HUMAN:
					whiteAgent.add(judge.getTarget());
					break;
				case WEREWOLF:
					blackAgent.add(judge.getTarget());
					break;
				}
			}
		}
		if(blackAgent.size() > 0){
			return randomSelect(blackAgent);
		}

		else{
		List<Agent> voteCandidates = new ArrayList<Agent>();
		if(fakeSeerCOAgent.size() > 0 && getLatestDayGameInfo().getAgentList().size() < 9){
			for(Agent fake: fakeSeerCOAgent){
				if(getLatestDayGameInfo().getAliveAgentList().contains(fake)){
					voteCandidates.add(fake);
				}
			}
		}
		if(voteCandidates.size() > 0){
			return randomSelect(voteCandidates);
		}

		else{
		voteCandidates.addAll(COResult.aliveGrayForMe(getLatestDayGameInfo().getAliveAgentList(), getMe()));
		voteCandidates.removeAll(whiteAgent);
		if(voteCandidates.size() > 0){
			return randomSelect(voteCandidates);
		}

		else{
		voteCandidates.addAll(getLatestDayGameInfo().getAliveAgentList());
		voteCandidates.removeAll(COResult.getSeerCOAgent());
		voteCandidates.removeAll(COResult.getMediumCOAgent());
		voteCandidates.removeAll(whiteAgent);
		if(voteCandidates.size() > 0){
			return randomSelect(voteCandidates);
		}

		else{
		for(Agent seer: COResult.getSeerCOAgent()){
			if(getLatestDayGameInfo().getAliveAgentList().contains(seer)){
				voteCandidates.add(seer);
			}
			voteCandidates.remove(getMe());
		}
		if(voteCandidates.size() > 0){
			return randomSelect(voteCandidates);
		}

		else{
			voteCandidates.addAll(getLatestDayGameInfo().getAliveAgentList());
			voteCandidates.remove(getMe());
			return randomSelect(voteCandidates);
		}}}}}
	}

	private Agent randomSelect(List<Agent> agentList) {
		int num = new Random().nextInt(agentList.size());
		return agentList.get(num);
	}

}
