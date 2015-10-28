package com.gmail.tydmskz;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.aiwolf.client.base.player.AbstractMedium;
import org.aiwolf.client.lib.TemplateTalkFactory;
import org.aiwolf.client.lib.Utterance;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Judge;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Species;
import org.aiwolf.common.data.Talk;
import org.aiwolf.common.net.GameInfo;
import org.aiwolf.common.net.GameSetting;

public class MediumPlayer extends AbstractMedium {

	//COする日にち
	int comingoutDay;

	//CO済みか否か
	boolean isCameout;

	//全体に霊能結果を報告済みのJudge
	ArrayList<Judge> declaredJudgedAgentList = new ArrayList<Judge>();

//	ArrayList<Agent> declaredMediumTellResultAgent = new ArrayList<>();
	boolean isSaidAllInquestResult;

	AdvanceGameInfo agi = new AdvanceGameInfo();
	VillageInformation vi = new VillageInformation();

	//今日投票しようと思っているプレイヤー
	Agent planningVoteAgent;

	//自分が最後に宣言した「投票しようと思っているプレイヤー」
	Agent declaredPlanningVoteAgent;

	//会話をどこまで読んだか
	int readTalkListNum;

	PlayerParamaters param;

	List<Agent> attackedAgentList = new ArrayList<Agent>();//あとでagiに統合するかも


	public MediumPlayer(PlayerParamaters p) {
		param = p;
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
		}catch (Exception e){ return false;}
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

		isSaidAllInquestResult = false;

		readTalkListNum =0;

		{
			Agent attacked = getLatestDayGameInfo().getAttackedAgent();
			if(attacked!=null)
			{
				attackedAgentList.add(attacked);
			}
		}
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
		else if(isCameout && !isSaidAllInquestResult){
			for(Judge judge: getMyJudgeList()){
				if(!declaredJudgedAgentList.contains(judge)){
					String string = TemplateTalkFactory.inquested(judge.getTarget(), judge.getResult());
					declaredJudgedAgentList.add(judge);
					return string;
				}
			}
			isSaidAllInquestResult = true;
		}

		/*
		 * 今日投票するプレイヤーの報告
		 * 前に報告したプレイヤーと同じ場合は報告なし
		 */
		if(declaredPlanningVoteAgent != planningVoteAgent){
			String string = TemplateTalkFactory.vote(planningVoteAgent);
			declaredPlanningVoteAgent = planningVoteAgent;
			return string;
		}

		else{
			return TemplateTalkFactory.over();
		}
		}catch (Exception e){ return TemplateTalkFactory.over();}

	}

	@Override
	public Agent vote() {
		return planningVoteAgent;
	}

	@Override
	public void finish() {
		// TODO 自動生成されたメソッド・スタブ

	}


	@Override
	public void update(GameInfo gameInfo) {
		try{
		super.update(gameInfo);
		vi.Update(gameInfo);

		List<Talk> talkList = gameInfo.getTalkList();
		boolean existInspectResult = false;
		boolean voteTalked = false;

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

			//占い結果の発話の場合
			case DIVINED:
				//AGIのJudgeListに結果を加える
				Agent seerAgent = talk.getAgent();
				Agent inspectedAgent = utterance.getTarget();
				Species inspectResult = utterance.getResult();
				Judge judge = new Judge(getDay(), seerAgent, inspectedAgent, inspectResult);
				agi.addInspectJudgeList(judge);

				existInspectResult =true;
				break;
			case VOTE:
				if(talk.getAgent()!=getMe())
				{
					//他の人が投票先を変更した場合考えなおす
					voteTalked = true;
				}
				break;
			}
		}
		readTalkListNum =talkList.size();


		/*
		 * 新しい占い結果があれば投票先を変える．(新たに黒判定が出た，または投票先のプレイヤーに白判定が出た場合)
		 */
		if(existInspectResult || voteTalked){
			setPlanningVoteAgent();
		}
		}catch (Exception e){}
	}


	public void setPlanningVoteAgent(){
		// 人狼、狂人、狩人とCOした人、または序盤に霊媒師とCOした人がいれば確定投票
		List<Agent> aliveAgentList = getLatestDayGameInfo().getAliveAgentList();
		aliveAgentList.remove(getMe());

		Agent suspecious = Util.coAnySuspeciousAgent(aliveAgentList, agi, getDay());
		if(suspecious != null)
		{
			planningVoteAgent = suspecious;
			return;
		}


		/*
		 * 投票先を未設定，または人狼だと占われたプレイヤー以外を投票先にしている場合
		 * 人狼だと占われたプレイヤーがいれば，投票先をそのプレイヤーに設定
		 * いなければ生存プレイヤーからランダムに選択
		 */

		List<Agent> voteAgentCandidate = new ArrayList<Agent>();

		for(Agent agent: aliveAgentList){
			/*
			 * 自分以外に霊能COしているプレイヤーがいれば投票候補
			 */
			if(agi.getComingoutMap().containsKey(agent) && agi.getComingoutMap().get(agent) == Role.MEDIUM){
				voteAgentCandidate.add(agent);
			}
		}

		for(Judge myJudge: getMyJudgeList()){
			for(Judge otherJudge: agi.getInspectJudgeList()){

				if(!aliveAgentList.contains(otherJudge.getAgent())){
					continue;
				}
				/*
				 * 自分と同じ相手について占っている場合
				 */
				if(myJudge.getTarget().equals(otherJudge.getTarget())){
					/*
					 * 自分の占い(霊能)結果と異なる結果を出していたら投票候補
					 */
					if(myJudge.getResult() != otherJudge.getResult()){
						voteAgentCandidate.add(otherJudge.getAgent());
					}
				}
			}
		}

		//嘘つき占い師達
		{
			//自分を人狼と占った占い師は狂人or人狼なので投票対象
			List<Agent> fakeSeers = new ArrayList<Agent>();
			for(Judge j : agi.getInspectJudgeList())
			{
				if(j.getTarget()==getMe() && j.getResult()==Species.WEREWOLF)
				{
					if(!fakeSeers.contains(j.getAgent()))
					{
						fakeSeers.add(j.getAgent());
					}
				}
			}

			//占い師が黒って占った人が、狼に噛まれたら、その占い師は人狼or狂人
			for(Agent a : attackedAgentList)
			{
				for(Judge j : agi.getInspectJudgeList())
				{
					if(j.getTarget()==a && j.getResult()==Species.WEREWOLF)
					{
						if(!fakeSeers.contains(j.getAgent()))
						{
							fakeSeers.add(j.getAgent());
						}
					}
				}
			}

			for(Agent a : fakeSeers)
			{
				if(!voteAgentCandidate.contains(a))
				{
					voteAgentCandidate.add(a);
				}
			}
		}

		/*
		 * すでに投票先に指定しているプレイヤーが投票候補内に含まれていたらそのまま
		 */
		if(planningVoteAgent != null && voteAgentCandidate.contains(planningVoteAgent)){
			return;
		}else{
			if (voteAgentCandidate.size() > 0) {
				Map<Agent, Agent> voteTarget = vi.VoteTarget();
				voteTarget.remove(getMe());
				planningVoteAgent = Util.SelectVoteTargetByOtherVote(voteAgentCandidate, voteTarget);
			} else {

				/*
				 * 投票候補がいない場合は占いで黒判定されているプレイヤーからランダムに選択
				 */
				ArrayList<Agent> subVoteAgentCandidate = new ArrayList<Agent>();

				for(Judge judge: agi.getInspectJudgeList()){
					if(aliveAgentList.contains(judge.getTarget()) && judge.getResult() == Species.WEREWOLF){
						subVoteAgentCandidate.add(judge.getTarget());
					}
				}

				if(subVoteAgentCandidate.size() > 0){
					Map<Agent, Agent> voteTarget = vi.VoteTarget();
					voteTarget.remove(getMe());

					planningVoteAgent = Util.SelectVoteTargetByOtherVote(subVoteAgentCandidate, voteTarget);
				}else{
					/*
					 * 黒判定されているプレイヤーもいなければ生存プレイヤーからランダムに選択
					 * だたし複数の占い師から白判定されているagentは除く
					 */

					List<Agent> aliveTmp = getLatestDayGameInfo().getAliveAgentList();
					{
						//
						for(Judge jb : agi.getInspectJudgeList())
						{
							for(Judge ja : agi.getInspectJudgeList())
							{
								if(ja==jb)
								{
									continue;
								}

								if(	ja.getAgent()!=jb.getAgent() && //別の人が
									ja.getTarget()==jb.getTarget() &&	//同じ人に対して
									ja.getResult()==Species.HUMAN && jb.getResult()==Species.HUMAN
										)
								{
									aliveTmp.remove(ja.getTarget());//白確定？
								}
							}
						}
					}

					Random rand = new Random();
					planningVoteAgent = aliveTmp.get(rand.nextInt(aliveTmp.size()));
				}
			}
		}
	}
}
