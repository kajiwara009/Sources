package com.gmail.yblueycarlo1967.player;


import java.util.List;

import org.aiwolf.client.base.player.AbstractWerewolf;
import org.aiwolf.client.lib.TemplateWhisperFactory;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Talk;
import org.aiwolf.common.net.GameInfo;
import org.aiwolf.common.net.GameSetting;

import com.gmail.yblueycarlo1967.brain.WWGameBrain;

public class YanagimatiWerewolf extends AbstractWerewolf {
	private WWGameBrain gameBrain=new WWGameBrain(this);
	private boolean didTalkMyFakeRole=false;

	@Override
	public Agent attack() {
		//COした狩人がいるなら狩人を噛む
		List<Agent> aliveBodyguard=gameBrain.getAliveBodyguardCOAgents();
		aliveBodyguard.removeAll(getWolfList());
		if(aliveBodyguard.size()>0){
			return gameBrain.randomSelect(aliveBodyguard);
		}
		//確定白がいるならそれを噛む
		List<Agent> aliveWhiteDefinite=gameBrain.getAliveAgentList(gameBrain.getWhiteDefiniteAgent());
		if(aliveWhiteDefinite.size()>0){
			return gameBrain.randomSelect(aliveWhiteDefinite);
		}
		//村視点占い真が決まってて生きてる場合
		Agent truthSeer=gameBrain.getTruthSeer();
		//護衛されなかったら噛む
		if(truthSeer!=null && gameBrain.isAgentAlive(truthSeer) && (gameBrain.todayAttackVotedAgent()!=truthSeer ||  !gameBrain.wasGuardedToday())){
			//それが狂人や人狼でないなら噛む
			if(gameBrain.getMaybePossessedAgent()!=truthSeer && !getWolfList().contains(truthSeer)){
				return truthSeer;
			}
		}
		//霊能１展開なら霊能噛みたい
		if(gameBrain.getMediumCOAgent().size()==1 && gameBrain.getAliveMediumCOAgents().size()==1 && this.getDay()>3){
			Agent medium=gameBrain.getMediumCOAgent().get(0);
			if((gameBrain.todayAttackVotedAgent()!=medium ||  !gameBrain.wasGuardedToday())){
				if(gameBrain.getMaybePossessedAgent()!=medium && !getWolfList().contains(medium)){
					return medium;
				}
			}
			
		}
		
		//占い即噛み戦略
		/*
		//襲撃失敗時
		if(this.getDay()>1 && gameBrain.isTodayNoAttacked()){
			Agent attacked=gameBrain.todayAttackVotedAgent();
			//襲撃対象がnullでなくて、生きているなら護衛されたことになる
			if(attacked!=null &&  gameBrain.isAgentAlive(attacked)){
			}
			//護衛じゃなければ
			else{
				//占い噛み
				if(gameBrain.getMaybeSeerAgent()!=null &&gameBrain.getAliveAgents().contains(gameBrain.getMaybeSeerAgent())){
					return gameBrain.getMaybeSeerAgent();
				}
			}
		}
		else{
			//占いが判明したら即噛み
			if(gameBrain.getMaybeSeerAgent()!=null &&gameBrain.getAliveAgents().contains(gameBrain.getMaybeSeerAgent())){
				return gameBrain.getMaybeSeerAgent();
			}
		}
		*/
		//暫定白から噛む
		List<Agent> candidate=gameBrain.getWhiteAliveAgents();
		candidate.removeAll(gameBrain.getWereWolfAgents());
		if(candidate.size()>0){
			return gameBrain.randomSelect(candidate);
		}
		//暫定白がなければ役職持ちを除いて噛む
		candidate=gameBrain.getAliveAgentExceptWerewolf();
		candidate.removeAll(gameBrain.getCOAgents());
		candidate.remove(gameBrain.getMaybePossessedAgent()); //狂人は噛まない
		if(candidate.size()>0){
			return gameBrain.randomSelect(candidate);
		}
		//それもいなければ人狼、狂人を除いて噛む
		candidate=gameBrain.getAliveAgentExceptWerewolf();
		candidate.remove(gameBrain.getMaybePossessedAgent()); //狂人は噛まない
		return gameBrain.randomSelect(candidate);
	}

	@Override
	public void dayStart() {
		gameBrain.dayStart();
		// TODO 自動生成されたメソッド・スタブ

	}
	@Override
	public void update(GameInfo gameInfo){
		super.update(gameInfo);
		gameBrain.update(gameInfo);
	}
	@Override
	public void finish() {
		// TODO 自動生成されたメソッド・スタブ

	}
	@Override
	public void initialize(GameInfo gameInfo,GameSetting gameSetting){
		super.initialize(gameInfo, gameSetting);
		gameBrain.setWerewolfFellow(getWolfList());
	}

	@Override
	public String talk() {
		//村人騙りの時に吊られそうで自分一匹のみは狩人CO
		//やめた
		if(gameBrain.getFakeRole()==Role.VILLAGER){
			/*
			if(gameBrain.getJudgeIsDivinedWolf()!=null){
				if(gameBrain.didComingOut()==false) return gameBrain.comingOut();
			}
			*/
			//皆の投票先予想がだいたい出揃っていて、1日目以降で投票で処刑されそうならCO
			/*
			if(gameBrain.getTodayVote().size()>gameBrain.getAliveAgents().size()/2){
				List<Agent> candidate=gameBrain.mostVotedAgents();
				if(candidate.contains(getMe())&& this.getDay()>3  ){
					if(gameBrain.didComingOut()==false) return gameBrain.comingOut();
				}
			}
			*/
		}
		//村人以外を騙る予定なら、その動きに従う
		if(gameBrain.getFakeRole()!=Role.VILLAGER){
		String talk=gameBrain.talkSpecial();
		if(talk!=null) return talk;
		}
		return Talk.OVER;
	}

	@Override
	public Agent vote() {
		//得票数が多くて、人狼でないのに合わせる
		List<Agent> candidate=gameBrain.mostVotedAgents();
		candidate.removeAll(this.getWolfList());
		if(candidate.size()==0) candidate=gameBrain.getAliveAgentExceptWerewolf();
		return gameBrain.randomSelect(candidate);
	}

	@Override
	public String whisper(){
		//村人以外を騙る予定なら、伝える
		if(didTalkMyFakeRole==false && gameBrain.getFakeRole()!=Role.VILLAGER){
			didTalkMyFakeRole=true;
			return TemplateWhisperFactory.comingout(getMe(), gameBrain.getFakeRole());
		}
		return Talk.OVER;
	}

}
