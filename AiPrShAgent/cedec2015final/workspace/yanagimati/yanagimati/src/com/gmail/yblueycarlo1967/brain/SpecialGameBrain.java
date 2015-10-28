package com.gmail.yblueycarlo1967.brain;

import java.util.ArrayList;
import java.util.List;

import org.aiwolf.client.base.player.AbstractMedium;
import org.aiwolf.client.base.player.AbstractRole;
import org.aiwolf.client.base.player.AbstractSeer;
import org.aiwolf.client.lib.TemplateTalkFactory;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Judge;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Species;
/**
 * 占い・霊能用のGameBrain
 * 人狼の騙りにも使う
 * @author info
 *
 */
public class SpecialGameBrain extends GameBrain {
	protected ArrayList<Judge> myToldJudgeList=new ArrayList<Judge>();//すでに発言した自分のJudge
	/** 騙る役職 */
	protected Role fakeRole=null;
	/**偽の占い(霊能)結果  */
	ArrayList<Judge> fakeJudgeList=new ArrayList<Judge>(); 

	public SpecialGameBrain(AbstractRole mine) {
		super(mine);
		// TODO 自動生成されたコンストラクター・スタブ
	}
	/** 
	 * 自分の役職、もしくは騙る役職をCOする文字列を返す
	 * @return COの文字列
	 */
	@Override
	public String comingOut(){
		didComingOut=true;
		Role coRole=null;
		if(this.getFakeRole()==null) coRole=mine.getMyRole();
		else {
			coRole=getFakeRole();
			if(coRole==Role.VILLAGER) coRole=Role.BODYGUARD;
		}
		return TemplateTalkFactory.comingout(mine.getMe(),coRole);
	}
	/**
	 * COや能力の結果報告などが必要ならそのTalkを返す<br>
	 * なにもなけれなnullを返す<br>
	 * 2日になる,黒を打たれる,黒引き,処刑されそうならCO
	 * @return
	 */
	public String talkSpecial(){
		//COしていない場合
		if(!didComingOut){
			//2日になったらカミングアウトする
			/*
			if(mine.getDay()==1 && this.fakeRole==null) {
				return comingOut();
			}
			*/
			
			if(mine.getDay()==2){
				return comingOut();
			}
			//黒を打たれたらCO
			if(getJudgeIsDivinedWolf()!=null){
				return comingOut();
			}
			//黒引きなら CO
			for(Judge judge:getMyRoleJudgeList()){
				if(judge.getResult()==Species.WEREWOLF){
					return comingOut();
				}
			}
			//投票されそうならCO 
			if(mine.getDay()>0 && getTodayVote().size()>(getAliveAgents().size()/2)){
				List<Agent> candidate=mostVotedAgents();
				if(candidate.contains(mine.getMe())){
					if(didComingOut()==false) return comingOut();
				}
			}
		//COしている場合
		}else{
			for(Judge judge:getMyRoleJudgeList()){
				//しゃべってないCO結果があるならしゃべる
				if(!myToldJudgeList.contains(judge)){
					myToldJudgeList.add(judge);
					return talkJudgeResult(judge);
				}
			}
		}
		return null;
	}
	/**
	 * 	自分のクラスによってキャストされたJudgeListを返す
	 * @return
	 */
	public ArrayList<Judge> getMyRoleJudgeList(){
		if(mine.getMyRole()==Role.SEER){
			return ((AbstractSeer)mine).getMyJudgeList();
		}
		else if(mine.getMyRole()==Role.MEDIUM){
			return ((AbstractMedium)mine).getMyJudgeList();
		}
		else if(mine.getMyRole()==Role.WEREWOLF ||mine.getMyRole()==Role.POSSESSED){
			return getFakeJudgeList();
		}
		return new ArrayList<Judge>();
	}
	/**
	 * Judgeの結果をしゃべる
	 * @param judge
	 * @return
	 */
	public String talkJudgeResult(Judge judge){
		if(mine.getMyRole()==Role.SEER ||fakeRole==Role.SEER){
			return TemplateTalkFactory.divined(judge.getTarget(), judge.getResult());
		}
		else if(mine.getMyRole()==Role.MEDIUM || fakeRole==Role.MEDIUM){
			return TemplateTalkFactory.inquested(judge.getTarget(), judge.getResult());
		}
		return null;
	}
	public Role getFakeRole(){
		return fakeRole;
	}
	public ArrayList<Judge> getFakeJudgeList(){
		return fakeJudgeList;
	}
	/** 自分が占ったor霊能した対象をリストで返す */
	public List<Agent> getFakeJudgeTargets(){
		List<Agent> targets=new ArrayList<Agent>();
		for(Judge judge:fakeJudgeList){
			targets.add(judge.getTarget());
		}
		return targets;
	}
	/** 黒判定を出した数を数える(重複は見ない。fakeのみ) */
	public int countBlackResultForJudgeList(){
		int black=0;
		for(Judge judge:fakeJudgeList){
			if(judge.getResult()==Species.WEREWOLF) black++;
		}
		return black;
	}

}
