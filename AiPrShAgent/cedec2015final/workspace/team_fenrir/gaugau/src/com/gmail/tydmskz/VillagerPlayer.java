package com.gmail.tydmskz;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.aiwolf.client.base.player.AbstractVillager;
import org.aiwolf.client.lib.TemplateTalkFactory;
import org.aiwolf.client.lib.Utterance;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Judge;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Species;
import org.aiwolf.common.data.Talk;
import org.aiwolf.common.net.GameInfo;

import com.gmail.tydmskz.GameInfoGaFormatter.SpeciesEnum;

public class VillagerPlayer extends AbstractVillager{
	/*
	 * 投票アルゴリズム：人狼だと占われたエージェント，いなければ，ランダム
	 * 発話：その日に投票しようとしているエージェントを報告．変化すれば報告．
	 */

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

	public VillagerPlayer(PlayerParamaters p) {
		param = p;
		if(param == null || param.villagerWhoShouldIVote == null)
		{
			// throw new IllegalArgumentException("Please give me my parameters!");

			param.villagerWhoShouldIVote = new float[]{
					0.8232f, 0.3449f, -0.2105f, -0.0939f, -0.1929f, -0.5665f, -0.0144f, -0.1813f, -0.1708f, 0.8016f,
					// ボディーガードであるとCOした人が怪しい
					// 占い師・霊媒師であるとCOした人には、投票すべきではない
					// 占い師に占われていない人に投票した方が良い
			};

//			// てきとうにしょきか
//			float ini = 1.0f / ParametersNum;
//			for(int i=0; i<ParametersNum; i++)
//			{
//				param.villagerWhoShouldIVote[i] = ini;
//			}
		}
	}

	@Override
	public void dayStart() {
		try{
		vi.DayStart();

		declaredPlanningVoteAgent = null;
		planningVoteAgent = null;
		setPlanningVoteAgent();

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
		if(declaredPlanningVoteAgent != planningVoteAgent){

			String string = TemplateTalkFactory.vote(planningVoteAgent);
			declaredPlanningVoteAgent = planningVoteAgent;
			return string;
		}else{
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
		List<Agent> aliveAgentList = getLatestDayGameInfo().getAliveAgentList();
		aliveAgentList.remove(getMe());

		// 人狼、狂人、狩人とCOした人、または序盤に霊媒師とCOした人がいれば確定投票
		Agent suspecious = Util.coAnySuspeciousAgent(aliveAgentList, agi, getDay());
		if(suspecious != null)
		{
			planningVoteAgent = suspecious;
			return;
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

			if(fakeSeers.size()>0)
			{
				Map<Agent, Agent> voteTargets = vi.VoteTarget();
				voteTargets.remove(getMe());
				planningVoteAgent = Util.SelectVoteTargetByOtherVote(fakeSeers, voteTargets);
				return;
			}
			else
			{
				/*
				 * 人狼だと占われたプレイヤーを指定している場合はそのまま
				 */
				//ここで対象となる占いは上の偽物確定占い師以外の占いの内容を対象とする
				if(planningVoteAgent != null){
					for(Judge judge: agi.getInspectJudgeList()){
						if(judge.getTarget().equals(planningVoteAgent)){
							return;
						}
					}
				}
			}
		}


		/*
		 * 投票先を未設定，または人狼だと占われたプレイヤー以外を投票先にしている場合
		 * 人狼だと占われたプレイヤーがいれば，投票先をそのプレイヤーに設定 // ←それはもしや、狂人のいいなりでは？
		 * いなければ生存プレイヤーからランダムに選択
		 */


//		List<Agent> voteAgentCandidate = new ArrayList<Agent>();
//
//		for(Judge judge: agi.getInspectJudgeList()){
//			if(aliveAgentList.contains(judge.getTarget()) && judge.getResult() == Species.WEREWOLF){
//				voteAgentCandidate.add(judge.getTarget());
//			}
//		}
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
						aliveAgentList.remove(ja.getTarget());//白確定？
					}
				}
			}
		}

		// 生存者がいれば、各生存者に対して評価値を計算
		planningVoteAgent = aliveAgentList.get(0); // 0番目のAgentで初期化
		float max = - 100000000;
		for(Agent agent:aliveAgentList)
		{
			float[] co, inspects;
			co = GameInfoGaFormatter.GetCoArray(agi, agent);
			inspects = GameInfoGaFormatter.GetInspectArray(agi, null, agent);

			float[] combination = new float[ParametersNum];
			System.arraycopy(co, 0, combination, 0, co.length);
			System.arraycopy(inspects, 0, combination, co.length, inspects.length);

			float val = Util.LinearCombine(param.villagerWhoShouldIVote, combination);
			if(max < val);
			{
				planningVoteAgent = agent;
				max = val;
			}
		}

		return;
	}

	public static final int ParametersNum = Role.values().length + SpeciesEnum.Count;

}
