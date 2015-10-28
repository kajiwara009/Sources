package com.gmail.yblueycarlo1967.brain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.aiwolf.client.base.player.AbstractRole;
import org.aiwolf.client.lib.Utterance;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Judge;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Species;
import org.aiwolf.common.data.Talk;
import org.aiwolf.common.data.Vote;
import org.aiwolf.common.net.GameInfo;
import org.aiwolf.common.net.GameSetting;

/**
 * 人狼用のGameBrain
 * @author info
 *
 */
public class WWGameBrain extends SpecialGameBrain{
	/** 狂人だと思うエージェント*/
	Agent maybePossessedAgent = null;
	Agent maybeSeerAgent=null;
	/** 仲間 */
	List<Agent> werewolfFellow=new ArrayList<Agent>();
	private int readWhisperNum=0;
	public WWGameBrain(AbstractRole mine) {
		super(mine);
		//何の役職をCOするかを決める
		//int rnd=new Random().nextInt(3)+1;
		//1/3で占い
		List<Role> fakeRoles = Arrays.asList(Role.SEER,Role.VILLAGER,Role.VILLAGER);
		//List<Role> fakeRoles = Arrays.asList(Role.VILLAGER);
		fakeRole = fakeRoles.get(new Random().nextInt(fakeRoles.size()));
	}
	public void dayStart(){
		super.dayStart();
		//騙っている場合の処理
		if(fakeRole==Role.VILLAGER) return;
		if(fakeRole==Role.SEER){
			//初手黒
			/*
			if(mine.getDay()==1){
				Agent target=randomSelect(this.getAliveAgentsExceptMe());
				Judge judge=new Judge(mine.getDay(),mine.getMe(),target,Species.WEREWOLF);
				fakeJudgeList.add(judge);
			}
			*/
			if(mine.getDay()>0){
				Agent target=randomSelect(this.getAliveAgentsExceptMe());
				Judge judge=new Judge(mine.getDay(),mine.getMe(),target,Species.HUMAN);
				fakeJudgeList.add(judge);
			}
		}
	}
	public void update(GameInfo gameInfo){
		super.update(gameInfo);
		//ささやきの処理
		List<Talk> talkList= gameInfo.getWhisperList();
		
		for(int i=readWhisperNum;i<talkList.size();i++){
			Talk talk=talkList.get(i);
			Utterance utterance=new Utterance(talk.getContent());
			switch (utterance.getTopic()){
			case COMINGOUT:
				//自分以外の人が占いを騙ろうとしている場合、占いを騙るのをやめる
				if(utterance.getTarget()!=mine.getMe()){
					if(utterance.getRole()==Role.SEER &&fakeRole==Role.SEER){
						fakeRole=Role.VILLAGER;
					}
				}
				break;
			case DIVINED:
				break;
			}
			readWhisperNum++;
		}
		
		//狂人を探す
		searchPossessed();
		//真占いを探す
		if(this.maybePossessedAgent!=null){
			List<Agent> seerAgents=this.getSeerCOAgent();
			seerAgents.remove(maybePossessedAgent);
			seerAgents.removeAll(this.werewolfFellow);
			if(seerAgents.size()==1) this.maybeSeerAgent=seerAgents.get(0);
		}
		//if(isChangeEnvironment || todayVoteTarget==null) thinkTodayVoteAgent();
	}
	/** 仲間の設定。必ずすること。AbstractWerewolfの getWolfList()メソッドでできる  */
	public void setWerewolfFellow(List<Agent> werewolfs){
		if(werewolfFellow.size()==0) werewolfFellow.addAll(werewolfs);
	}
	/** 仲間の人狼を除く、生きているAgentを返す */
	public List<Agent> getAliveAgentExceptWerewolf(){
		List<Agent> candidate=new ArrayList<Agent>();
		candidate.addAll(getAliveAgentsExceptMe());
		candidate.removeAll(werewolfFellow);
		return candidate; 
	}
	public List<Agent> getWereWolfAgents(){
		List<Agent> werewolfs=new ArrayList<Agent>();
		werewolfs.addAll(this.werewolfFellow);
		return  werewolfs;
	}
	public Agent getMaybePossessedAgent(){
		return this.maybePossessedAgent;
	}
	public Agent getMaybeSeerAgent(){
		return this.maybeSeerAgent;
	}
	/** 今朝人狼が襲撃したAgentを返す。複数対象がいる場合はnull */
	public Agent todayAttackVotedAgent(){
		List<Agent> todayAttacked=this.todayAttackVotedAgents();
		if(todayAttacked.size()==1) return todayAttacked.get(0);
		else return null;
		
	}
	/** 今日の襲撃が狩人によって守られたかどうか。確実性も持たせるため、人狼の襲撃投票が同数になった場合は除く */
	public boolean wasGuardedToday(){
		boolean isGuard=false;
		if(mine.getDay()>1 && isTodayNoAttacked()){
			Agent attacked=todayAttackVotedAgent();
			//襲撃対象がnullでなくて、生きているなら護衛されたことになる
			if(attacked!=null &&  isAgentAlive(attacked)){
				isGuard=true;
			}
		}
		return isGuard;
	}
	/**今朝人狼が襲撃した可能性のあるAgentを返す*/
	public List<Agent> todayAttackVotedAgents(){
		List<Agent> targets=new ArrayList<Agent>();
		Map<Agent,Integer> voteMap=new HashMap<Agent,Integer>();
		for(Vote vote :gameInfo.getAttackVoteList()){
			//すでに登録されている場合
			if(voteMap.containsKey(vote.getTarget())){
				int num=voteMap.get(vote.getTarget())+1;
				voteMap.put(vote.getTarget(), num);
			}
			else{
				voteMap.put(vote.getTarget(),1);
			}
		}
		//票数の高いものを入れる
		int maxNum=0;
		Iterator<Entry<Agent, Integer>> entries = voteMap.entrySet().iterator();
		while(entries.hasNext()) {
			Map.Entry<Agent,Integer> entry = entries.next();
			Agent key = (Agent)entry.getKey();
			Integer val = (Integer)entry.getValue();
			if(val>maxNum){
				targets=new ArrayList<Agent>();
				maxNum=val;
				targets.add(key);
			}
			else if(val==maxNum){
				targets.add(key);
			}
		}
		return targets;
		
	}
	
	private void searchPossessed(){
		//占いから
		for(Agent seerAgent:this.getSeerCOAgent()){
			for(Judge judge:this.getSeerJudgeList(seerAgent)){
				if(this.werewolfFellow.contains(judge.getAgent())) continue;
				//占い師が人狼でない者に黒判定を出した場合
				if(judge.getResult()==Species.WEREWOLF && !this.werewolfFellow.contains(judge.getTarget())){
					this.maybePossessedAgent=seerAgent;
				}
				//占い師が人狼に白判定を出した場合
				else if(judge.getResult()==Species.HUMAN && this.werewolfFellow.contains(judge.getTarget())){
					this.maybePossessedAgent=seerAgent;
				}
			}
		}
		//霊能から
		for(Agent mediumAgent:this.getMediumCOAgent()){
			for(Judge judge:this.getMediumJudgeList(mediumAgent)){
				if(this.werewolfFellow.contains(judge.getAgent())) continue;
				//霊能者が人狼でない者に黒判定を出した場合
				if(judge.getResult()==Species.WEREWOLF && !this.werewolfFellow.contains(judge.getTarget())){
					this.maybePossessedAgent=mediumAgent;
				}
				//霊能者が人狼に白判定を出した場合
				else if(judge.getResult()==Species.HUMAN && this.werewolfFellow.contains(judge.getTarget())){
					this.maybePossessedAgent=mediumAgent;
				}
			}
		}
	}
}
