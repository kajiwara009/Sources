package kainoueAgent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.aiwolf.client.base.player.AbstractWerewolf;
import org.aiwolf.client.lib.TemplateTalkFactory;
import org.aiwolf.client.lib.Utterance;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Species;
import org.aiwolf.common.data.Talk;
import org.aiwolf.common.net.GameInfo;
import org.aiwolf.common.net.GameSetting;

public class MyRoleAssignPlayerWerewolf extends AbstractWerewolf {
	List<Agent> fakeSeerCOAgent = new ArrayList<Agent>();
	ArrayList<Talk> AlltalkList = new ArrayList<Talk>();
	List<Agent> blackAgent = new ArrayList<Agent>(),commingout = new ArrayList<Agent>(),judged = new ArrayList<Agent>();
	List<Agent> alive = new ArrayList<Agent>(),death = new ArrayList<Agent>();
	List<Role> checkRole = new ArrayList<Role>();
	Map<Agent,Agent>vote_cnt = new HashMap<Agent,Agent>();
	Map<Agent,Role>comingoutRole = new HashMap<Agent,Role>();
	int readTalkNum = 0,talkNum=0;
	Agent pri = null,isTalk;
	boolean talk,talked,isComingOut=false,comingOutDay=false,co=false;
	Agent possessedAgent = null;
	Map<Agent, Role> wolfsFakeRoleMap = new HashMap<Agent, Role>();
	@Override
	public void initialize(GameInfo gameInfo, GameSetting gameSetting) {
		super.initialize(gameInfo, gameSetting);
		//人狼をセットする
		Map<Agent,Role> wolf = gameInfo.getRoleMap();
		List<Agent> All = gameInfo.getAliveAgentList();
		for(int i=0;i<All.size();i++){
			if(wolf.get(All.get(i)) == Role.WEREWOLF ){
				blackAgent.add(All.get(i));
			}
		}
	}
	public Agent attack() {
		// TODO 自動生成されたメソッド・スタブ
		List<Agent> buf = getLatestDayGameInfo().getAliveAgentList(),c=commingout;
		Map<Agent,Integer>cnt = get_cnt();
		int max=-1;
		Agent am=null;
		buf.removeAll(blackAgent);

		for(int i=0;i<alive.size();i++){
			if(cnt.get(alive.get(i))!=null && max<=cnt.get(alive.get(i))){
				max=cnt.get(alive.get(i));
				am=alive.get(i);
			}
		}
		if(am!=null) buf.remove(am);
		if(possessedAgent!=null) buf.remove(possessedAgent);
		c.removeAll(blackAgent); c.removeAll(death);
		if(c!=null) c.remove(c);
		if(am!=null) buf.remove(am);
		if( (getDay()<=3 && c.size() == 1) || c.size()==0){
			if(buf.size()!=0) return randomSelect(buf);
			return am;
		}else{
			return randomSelect(c);
		}
	}

	public Map<Agent,Integer> get_cnt(){
		Map<Agent,Integer> cnt = new HashMap<Agent,Integer>();
	//	System.out.println(alive.size());
		for(int i=0;i<alive.size();i++){
			if(vote_cnt.get(alive.get(i))==null || getMe()==alive.get(i)) continue;
			Integer c=cnt.get(vote_cnt.get(alive.get(i)));
			if(c==null) c=0;
			cnt.put(vote_cnt.get(alive.get(i)),c+1);
		}
		return cnt;
	}

	@Override
	public void dayStart() {
		// TODO 自動生成されたメソッド・スタブ
		talked=false;
		alive = getLatestDayGameInfo().getAliveAgentList();
		Agent buffer ;
		buffer = getLatestDayGameInfo().getExecutedAgent();
		if(buffer!=null){
			death.add(buffer);
		}
		buffer = getLatestDayGameInfo().getAttackedAgent();
		if(buffer!=null){
			death.add(buffer);
		}
		co=true;
		talkNum++;
	}

	@Override
	public void finish() {
		// TODO 自動生成されたメソッド・スタブ

	}

	public boolean comingOutSeer(){
		Map<Agent,Integer>cnt=get_cnt();
		boolean check=true;
		for(int i=0;i<alive.size();i++){
			if(getMe() == alive.get(i)) continue;
			if(cnt.get(getMe())==null) check = false;
			if(cnt.get(alive.get(i))!=null &&cnt.get(getMe())!=null && cnt.get(getMe()) <= cnt.get(alive.get(i))){
				check=false;
			}
		}
		return check;
	}

	@Override
	public String talk() {
		// TODO 自動生成されたメソッド・スタブ
		if(!isComingOut && comingOutSeer() && getDay()!=0){
			String comingoutTalk = TemplateTalkFactory.comingout(getMe(), Role.SEER);
			isComingOut=true;
			comingOutDay=true;
			return comingoutTalk;
		}else if(isComingOut && talkNum!=0){
			talkNum--;
			if(comingOutSeer() && co){
				List<Agent> w=alive;
				w.remove(getMe());
				if(w!=null && w.size()!=0){
					Agent t=randomSelect(w);
					String resultTalk = TemplateTalkFactory.divined(t, Species.WEREWOLF);
					judged.add(t);
					pri = t;
					co=false;
					comingOutDay=false;
					return resultTalk;
				}
			}
			if(comingOutDay){
				Agent t = null;
				List<Agent> d = death , a=alive;
				d.removeAll(judged); d.remove(getMe());
				a.removeAll(judged); a.remove(getMe());

				if(talkNum < 5 && talkNum > 3 && d!=null && d.size()!=0) {
					t = randomSelect(d);
				}else{
					if(a!=null && a.size()!=0) t = randomSelect(a);
					else t = getMe();
				}
				judged.add(t);
			}
			else {
				List<Agent> w=alive;
				int i = new Random().nextInt(10);
				if(i%2==1 && (getLatestDayGameInfo().getAttackedAgent()!=null) ){
					String resultTalk = TemplateTalkFactory.divined(getLatestDayGameInfo().getAttackedAgent(), Species.HUMAN);
					judged.add(getLatestDayGameInfo().getAttackedAgent());
					return resultTalk;
				}
				if( getLatestDayGameInfo().getAttackedAgent()!=null ) w.add(getLatestDayGameInfo().getAttackedAgent());
				w.removeAll(judged);
				w.remove(getMe());

				if(w!=null && w.size()!=0){
					Agent t=randomSelect(w);
					String resultTalk = TemplateTalkFactory.divined(t, Species.HUMAN);
					judged.add(t);
					return resultTalk;
				}
			}
		}

		Agent a=vote();
		if(isTalk!=a){
			isTalk = a;
			return TemplateTalkFactory.vote(a);
		}
		return Talk.OVER;
	}

	@Override
	public Agent vote() {
		// TODO 自動生成されたメソッド・スタブ

		Map<Agent,Integer>cnt;
		List<Agent> w=alive;
		cnt = get_cnt();
		w.removeAll(death);
		w.removeAll(blackAgent);
		int max=-1;
		Agent am=null;
		for(int i=0;i<w.size();i++){
			if(cnt.get(w.get(i))!=null && max<=cnt.get(w.get(i))){
				max=cnt.get(w.get(i));
				am=w.get(i);
			}
		}
		if(am!=null) return am;
		if(pri!=null) return pri;
		if(alive!=null && alive.size()!=0) randomSelect(alive);
		return getMe();
	}

	@Override
	public String whisper() {
		// TODO 自動生成されたメソッド・スタブ
		return Talk.OVER;
	}

	private Agent randomSelect(List<Agent> agentList){
		int num = new Random().nextInt(agentList.size());
		return agentList.get(num);
	}
	public void updateTalkInfo(List<Talk> talkList){
		for(int i=0; i<talkList.size();i++){
			Talk talk = talkList.get(i);
			if(AlltalkList.contains(talkList.get(i))){
				AlltalkList.add(talkList.get(i));
			}
			Utterance utterance = new Utterance(talk.getContent());
		//	System.out.print("topics  "+utterance.getTopic()+" Role="+utterance.getRole()+" talkAgent= "+talkList.get(i).getAgent()+" TargetAgent="+utterance.getTarget()+" result="+utterance.getResult()+" TokenId="+utterance.getTalkID()+"\n");

			switch (utterance.getTopic()){
			case COMINGOUT:
				comingoutRole.put(utterance.getTarget(),utterance.getRole());
				commingout.add(utterance.getTarget());
				break;
			case DIVINED:
				if( blackAgent.contains(talk.getAgent()) ) continue;
				if(utterance.getResult() == Species.HUMAN && blackAgent.contains(utterance.getTarget())){
					possessedAgent = utterance.getTarget();
				}
				if(utterance.getResult() == Species.WEREWOLF && !blackAgent.contains(utterance.getTarget())){
					possessedAgent = utterance.getTarget();
				}
				break;
			case INQUESTED:
				//霊能の結果
				if( blackAgent.contains(talk.getAgent()) ) continue;
				if(utterance.getResult() == Species.HUMAN && blackAgent.contains(utterance.getTarget())){
					possessedAgent = utterance.getTarget();
				}
				if(utterance.getResult() == Species.WEREWOLF && !blackAgent.contains(utterance.getTarget())){
					possessedAgent = utterance.getTarget();
				}
				break;
			case VOTE:
				vote_cnt.put(talkList.get(i).getAgent(),utterance.getTarget());
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
			case OVER:
				break;
			case SKIP:
				break;
			default:
				break;

			}
		}
	}

	public void update(GameInfo gameInfo) {
		super.update(gameInfo);
	//	System.out.print("update--------------------------\n");
		List<Talk> talkList = gameInfo.getTalkList();
		pri=null;
		updateTalkInfo(talkList);
		updateTalkInfo(AlltalkList);
		Map<Agent,Integer> cnt = new HashMap<Agent,Integer>();
		cnt = get_cnt();
		int max=0;
		for(int i=0;i<alive.size();i++){
			if(cnt.get(alive.get(i))!=null && max<=cnt.get(alive.get(i)) ) max=cnt.get(alive.get(i));
		}

	}

}
