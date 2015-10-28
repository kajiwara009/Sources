package com.gmail.tydmskz;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.aiwolf.client.base.player.AbstractSeer;
import org.aiwolf.client.lib.TemplateTalkFactory;
import org.aiwolf.client.lib.Utterance;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Judge;
import org.aiwolf.common.data.Species;
import org.aiwolf.common.data.Talk;
import org.aiwolf.common.net.GameInfo;
import org.aiwolf.common.net.GameSetting;

public class SeerPlayer extends AbstractSeer{

	//COする日にち
	int comingoutDay;

	//CO済みか否か
	boolean isCameout;

	//全体に占い結果を報告済みのプレイヤー
	ArrayList<Judge> declaredJudgedAgentList = new ArrayList<Judge>();

	boolean isSaidAllDivineResult;

	AdvanceGameInfo agi = new AdvanceGameInfo();
	VillageInformation vi = new VillageInformation();

	//今日投票しようと思っているプレイヤー
	Agent planningVoteAgent;

	//自分が最後に宣言した「投票しようと思っているプレイヤー」
	Agent declaredPlanningVoteAgent;

	//会話をどこまで読んだか
	int readTalkListNum;


	public SeerPlayer(PlayerParamaters param) {
		// TODO 自動生成されたコンストラクター・スタブ
	}

	//間違ってたらしいので
	@Override
	public boolean isJudgedAgent(Agent agent){
		try{
		boolean ret = getMyJudgeList().contains(agent);
		for(Judge judge: getMyJudgeList()){
			if(judge.getTarget() == agent){
				return true;
			}
		}
		return false;
		}catch (Exception e){return false;}
	}

	@Override
	public void initialize(GameInfo gameInfo, GameSetting gameSetting) {
		try{
		super.initialize(gameInfo, gameSetting);

		comingoutDay = new Random().nextInt(3)+1;
		isCameout = false;
		}catch (Exception e){}
	}


	@Override
	public void dayStart() {
		try{
		super.dayStart();
		vi.DayStart();

		//投票するプレイヤーの初期化，設定
		declaredPlanningVoteAgent = null;
		planningVoteAgent = null;
		setPlanningVoteAgent();

		isSaidAllDivineResult = false;

		readTalkListNum =0;
		}catch (Exception e){}
	}

	@Override
	public String talk() {
		try{
		//CO,霊能結果，投票先の順に発話の優先度高

		/*
		 * 未CO，かつ設定したCOする日にちを過ぎていたらCO
		 */

		if(!isCameout && getDay() >= comingoutDay){
			String string = TemplateTalkFactory.comingout(getMe(), getMyRole());
			isCameout = true;
			return string;
		}
		/*
		 * COしているなら占い結果の報告
		 */
		else if(isCameout && !isSaidAllDivineResult){
			for(Judge judge: getMyJudgeList()){
				if(!declaredJudgedAgentList.contains(judge)){
					String string = TemplateTalkFactory.divined(judge.getTarget(), judge.getResult());
					declaredJudgedAgentList.add(judge);
					return string;
				}
			}
			isSaidAllDivineResult = true;
		}

		/*
		 * 今日投票するプレイヤーの報告
		 * 前に報告したプレイヤーと同じ場合は報告なし
		 */
		if(declaredPlanningVoteAgent != planningVoteAgent)
		{
			String string = TemplateTalkFactory.vote(planningVoteAgent);
			declaredPlanningVoteAgent = planningVoteAgent;
			return string;
		}
		else
		{
			return Talk.OVER;
		}
		}catch (Exception e){ return TemplateTalkFactory.over();}
	}

	@Override
	public Agent vote() {
		return planningVoteAgent;
	}

	@Override
	public Agent divine() {
		try{
		/*
		 * まだ占っていないプレイヤーの中からランダムに選択
		 */
		List<Agent> nonInspectedAgentList = new ArrayList<Agent>();

		for(Agent agent: getLatestDayGameInfo().getAliveAgentList()){
			if(!isJudgedAgent(agent)){
				nonInspectedAgentList.add(agent);
			}
		}

		if(nonInspectedAgentList.size() == 0){
			return getMe();
		}else{
			return nonInspectedAgentList.get(new Random().nextInt(nonInspectedAgentList.size()));
		}
		}catch (Exception e){ return getMe();}
	}

	@Override
	public void finish() {
	}

	@Override
	public void update(GameInfo gameInfo) {
		try{
		super.update(gameInfo);
		vi.Update(gameInfo);

		List<Talk> talkList = gameInfo.getTalkList();
		/*
		 * talkListからCO，占い結果の抽出
		 */
		for(int i = readTalkListNum; i < talkList.size(); i++){
			Talk talk = talkList.get(i);
			Utterance utterance = new Utterance(talk.getContent());
			switch (utterance.getTopic()) {

			//カミングアウトの発話の場合
			case COMINGOUT:
				agi.getComingoutMap().put(talk.getAgent(), utterance.getRole());
				if(utterance.getRole() == getMyRole()){
					setPlanningVoteAgent();
				}
				break;
			case VOTE:
				if(talk.getAgent()!=getMe())
				{
					//他の人が投票先を変更した場合考えなおす
					setPlanningVoteAgent();
				}
				break;
			}
		}
		readTalkListNum =talkList.size();
		}catch (Exception e){}
	}

	public void setPlanningVoteAgent(){

		List<Agent> aliveAgentList = getLatestDayGameInfo().getAliveAgentList();
		aliveAgentList.remove(getMe());

		// 人狼、狂人、狩人とCOした人、または序盤に霊媒師とCOした人がいれば確定投票
		Agent suspecious = Util.coAnySuspeciousAgent(aliveAgentList, agi, getDay());
		if(suspecious != null)
		{
			planningVoteAgent = suspecious;
			return;
		}

		/*
		 * 自分以外の占い師COのプレイヤー．または自分が黒判定を出したプレイヤー
		 * いなければ，白判定を出したプレイヤー以外でランダム
		 */

		List<Agent> voteAgentCandidate = new ArrayList<Agent>();
		for(Agent agent: aliveAgentList){
			if(agi.getComingoutMap().containsKey(agent) && agi.getComingoutMap().get(agent) == getMyRole()){
				voteAgentCandidate.add(agent);
			}else{
				for(Judge judge: getMyJudgeList()){
					if(judge.getTarget().equals(agent) && judge.getResult() == Species.WEREWOLF){
						voteAgentCandidate.add(agent);
					}
				}
			}
		}

		if(voteAgentCandidate.contains(planningVoteAgent)){
			return;
		}


		if(voteAgentCandidate.size() > 0){
			Map<Agent, Agent> voteTarget = vi.VoteTarget();
			voteTarget.remove(getMe());

			planningVoteAgent = Util.SelectVoteTargetByOtherVote(voteAgentCandidate, voteTarget);
			//planningVoteAgent = voteAgentCandidate.get(rand.nextInt(voteAgentCandidate.size()));
		}else{
			Random rand = new Random();
			planningVoteAgent = aliveAgentList.get(rand.nextInt(aliveAgentList.size()));
		}
		return;
	}


}
