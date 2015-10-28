package kainoueAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.aiwolf.client.base.player.AbstractPossessed;
import org.aiwolf.client.lib.TemplateTalkFactory;
import org.aiwolf.client.lib.Utterance;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Species;
import org.aiwolf.common.data.Talk;
import org.aiwolf.common.net.GameInfo;

public class MyRoleAssignPlayerPossessed extends AbstractPossessed {
	List<Agent> fakeSeerCOAgent = new ArrayList<Agent>();
	ArrayList<Talk> AlltalkList = new ArrayList<Talk>();
	List<Agent> whiteAgent = new ArrayList<Agent>(),
			blackAgent = new ArrayList<Agent>(),
			comming_out = new ArrayList<Agent>();
	List<Agent> alive = new ArrayList<Agent>(),death = new ArrayList<Agent>();
	List<Role> checkRole = new ArrayList<Role>();
	Map<Agent,Agent>vote_cnt = new HashMap<Agent,Agent>();
	Map<Agent,Role>comingoutRole = new HashMap<Agent,Role>();
	Role myRole=null;
	int readTalkNum = 0;
	Agent pri = null,isTalk;
	List<Agent> judged = new ArrayList<Agent>();
	boolean talk,talked;
	boolean isComingOut = false,com=false;
	Map<Agent,Integer> talkCnt = new HashMap<Agent,Integer>();
	@Override
	public void finish() {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public String talk() {
		// TODO 自動生成されたメソッド・スタブ
	//	System.out.println("==============================talk=============================");
	//	System.out.println(isComingOut+"==============================talk============================="+com);
		if(!isComingOut && getDay()==1){
			String comingoutTalk = TemplateTalkFactory.comingout(getMe(), Role.SEER);
			isComingOut=true;
			return comingoutTalk;
		}else if(!talked){
			Map<Agent,Integer>cnt=get_cnt();
			talked=true;
			boolean check=true;
			for(int i=0;i<alive.size();i++){
				if(cnt.get(getMe())==null) check = false;
				if(cnt.get(alive.get(i))!=null &&cnt.get(getMe())!=null && cnt.get(getMe()) < cnt.get(alive.get(i))){
					check=false;
				}
			}
			if(getDay()==1 || check){
				List<Agent> w=alive;
				w.remove(getMe());
				Agent t=randomSelect(w);
				String resultTalk = TemplateTalkFactory.divined(t, Species.WEREWOLF);
				judged.add(t);
				pri = t;
				return resultTalk;
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
		for(int i=0;i<comming_out.size();i++){
			if(!alive.contains(comming_out.get(i))) continue;
			if(comingoutRole.get(comming_out.get(i))!=Role.MEDIUM && comingoutRole.get(comming_out.get(i))!=Role.SEER) continue;
			Integer c = talkCnt.get(comming_out.get(i));
			if(c==null) c=0;
			if(comingoutRole.get(comming_out.get(i))==Role.MEDIUM) c=c++;
			if(getDay()!=c && !blackAgent.contains(comming_out.get(i))){
				blackAgent.add(comming_out.get(i));
			}
		}
		if(alive.size()<=4){
			Agent a=getMe();
			if(a!=null) return a;
		}
		Map<Agent,Integer>cnt;
		List<Agent> w=whiteAgent,b=alive;
		cnt = get_cnt();
		w.removeAll(death);
		int max=-1;
		Agent am=null;
		for(int i=0;i<w.size();i++){
			if(cnt.get(w.get(i))!=null && max<=cnt.get(w.get(i))){
				max=cnt.get(w.get(i));
				am=w.get(i);
			}
		}
		if(pri!=null && alive.contains(pri)) return pri;
		if(am!=null) return am;

		b.removeAll(blackAgent);
		for(int i=0;i<b.size();i++){
			if(cnt.get(b.get(i))!=null && max<=cnt.get(b.get(i))){
				max=cnt.get(b.get(i));
				am=b.get(i);
			}
		}
		if(am!=null) return am;
		if(alive!=null && alive.size()!=0) randomSelect(alive);
		return getMe();
	}
	public Agent lowAgent(){
		Map<Agent,Integer>cnt;
		cnt = get_cnt();
		int max=-1;
		Agent am=null;
		for(int i=0;i<alive.size();i++){
			if(comingoutRole.get(alive.get(i))!=null && cnt.get(alive.get(i))!=null && max<=cnt.get(alive.get(i))){
				max=cnt.get(alive.get(i));
				am=alive.get(i);
			}
		}
		return am;
	}
	public void dayStart(){
		readTalkNum = 0;
		//System.out.print("start day--------------------------\n");
		readTalkNum = 0;
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

		myRole=getMyRole();
		pri=null;talk=false;
		if(!blackAgent.contains(getMe())){
			blackAgent.add(getMe());
		}
	}

	public Map<Agent,Integer> get_cnt(){
		Map<Agent,Integer> cnt = new HashMap<Agent,Integer>();
//		System.out.println(alive.size());
		for(int i=0;i<alive.size();i++){
			if(vote_cnt.get(alive.get(i))==null || getMe()==alive.get(i)) continue;
			Integer c=cnt.get(vote_cnt.get(alive.get(i)));
			if(c==null) c=0;
			cnt.put(vote_cnt.get(alive.get(i)),c+1);
		}
		return cnt;
	}

	public void updateTalkInfo(List<Talk> talkList){
		for(int i=0; i<talkList.size();i++){
			Talk talk = talkList.get(i);
			if(AlltalkList.contains(talkList.get(i))){
				AlltalkList.add(talkList.get(i));
				Utterance u = new Utterance(talk.getContent());
				switch (u.getTopic()){
				case DIVINED:
					Integer c = talkCnt.get(talkList.get(i).getAgent());
					if(c==null) c=0;
					talkCnt.put(talkList.get(i).getAgent(), c+1);
					break;

				case INQUESTED:
					Integer cnt = talkCnt.get(talkList.get(i).getAgent());
					if(cnt==null) cnt=0;
					talkCnt.put(talkList.get(i).getAgent(), cnt+1);
					break;
				default:
					break;
				}
			}
			Utterance utterance = new Utterance(talk.getContent());
		//	System.out.print("topics  "+utterance.getTopic()+" Role="+utterance.getRole()+" talkAgent= "+talkList.get(i).getAgent()+" TargetAgent="+utterance.getTarget()+" result="+utterance.getResult()+" TokenId="+utterance.getTalkID()+"\n");

			Map<Agent,Integer> mcnt = new HashMap<Agent,Integer>(),scnt = new HashMap<Agent,Integer>();
			Map<Agent,Role>ccnt = new HashMap<Agent,Role>();
			Map<Role,Agent>rcnt= new HashMap<Role,Agent>();

			switch (utterance.getTopic()){
			case COMINGOUT:
				if(utterance.getRole() == myRole && !talk.getAgent().equals(getMe()) && !blackAgent.contains(utterance.getTarget()) ) {
					blackAgent.add(utterance.getTarget());
				}
				if(whiteAgent.contains(talk.getAgent())){
					checkRole.add(utterance.getRole());
				}
				if(!whiteAgent.contains(talk.getAgent()) && checkRole.contains(utterance.getRole()) && !blackAgent.contains(utterance.getTarget())){
					blackAgent.add(utterance.getTarget());
				}
				if(!alive.contains(utterance.getTarget())){
					com=true;
				}
				if(ccnt.get(utterance.getTarget())!=null){
					if(ccnt.get(utterance.getRole())!=utterance.getRole()&&!blackAgent.contains(utterance.getTarget())) blackAgent.add(utterance.getTarget());
				}
				if(ccnt.get(utterance.getTarget())==null) ccnt.put(utterance.getTarget(), utterance.getRole());
				if(whiteAgent.contains(utterance.getTarget())) rcnt.put(utterance.getRole(), utterance.getTarget());
				if(!whiteAgent.contains(utterance.getTarget()) &&rcnt.get(utterance.getRole())!=null ){
					if(!blackAgent.contains(utterance.getTarget())){
						blackAgent.add(utterance.getTarget());
					}
				}
				if(!comming_out.contains(utterance.getTarget())){
					comming_out.add(utterance.getTarget());
				}
				comingoutRole.put(utterance.getTarget(),utterance.getRole());
				break;
			case DIVINED:
				if(whiteAgent.contains(talkList.get(i).getAgent())){
					if(utterance.getResult() == Species.HUMAN){
						if(!whiteAgent.contains(utterance.getTarget())){
							whiteAgent.add(utterance.getTarget());
						}
					}else{
						Integer c = scnt.get(talkList.get(i).getAgent());
						if(c==null) c=0;
						scnt.put(talkList.get(i).getAgent(),c+1);
						if(c==4 && !blackAgent.contains(utterance.getTarget())){
							blackAgent.add(utterance.getTarget());
						}
						if(!blackAgent.contains(utterance.getTarget())){
							blackAgent.add(utterance.getTarget());
						}
					}
				}
				if(whiteAgent.contains(utterance.getTarget()) && utterance.getResult() == Species.WEREWOLF){
					if(!blackAgent.contains(talkList.get(i).getAgent())){
						blackAgent.add(talkList.get(i).getAgent());
					}
				}
				break;
			case INQUESTED:
				//霊能の結果
				if(myRole!=Role.MEDIUM &&whiteAgent.contains(talkList.get(i).getAgent())){
					if(utterance.getResult() == Species.WEREWOLF){
						Integer c = mcnt.get(talkList.get(i).getAgent());
						if(c==null) c=0;
						mcnt.put(talkList.get(i).getAgent(),c+1);
						if(c==2 && !blackAgent.contains(utterance.getTarget())){
							blackAgent.add(utterance.getTarget());
						}
						if(!whiteAgent.contains(utterance.getTarget())){
							whiteAgent.add(utterance.getTarget());
						}
					}else{
						if(!blackAgent.contains(utterance.getTarget())){
							blackAgent.add(utterance.getTarget());
						}
					}
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
		//System.out.print("update--------------------------\n");
		List<Talk> talkList = gameInfo.getTalkList();
		Agent b=gameInfo.getAttackedAgent();
		if(b!=null && !whiteAgent.contains(b)){
			whiteAgent.add(b);
		}
		b=gameInfo.getGuardedAgent();
		pri=null;
		updateTalkInfo(talkList);
		updateTalkInfo(AlltalkList);
		Map<Agent,Integer> cnt = new HashMap<Agent,Integer>();
		cnt = get_cnt();
		int max=0;
		for(int i=0;i<alive.size();i++){
			if(cnt.get(alive.get(i))!=null && max<=cnt.get(alive.get(i)) ) max=cnt.get(alive.get(i));
		}
		if(cnt.get(getMe())!=null && max == cnt.get(getMe()) && getDay()!=0){
			com=true;
		}
	}
	private Agent randomSelect(List<Agent> agentList){
		int num = new Random().nextInt(agentList.size());
		return agentList.get(num);
	}

}
