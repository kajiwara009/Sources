package jp.ac.cu.hiroshima.info.cm.nakamura.player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import jp.ac.cu.hiroshima.info.cm.nakamura.base.player.NoriAbstractSeer;

import org.aiwolf.client.lib.TemplateTalkFactory;
import org.aiwolf.client.lib.Utterance;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Judge;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Species;
import org.aiwolf.common.data.Talk;
import org.aiwolf.common.net.GameInfo;
import org.aiwolf.common.net.GameSetting;

public class NoriSeer extends NoriAbstractSeer{

	//COする日にち
	int comingoutDay;

	//CO済みか否か
	boolean isCameout;
	boolean cocheck;
	 boolean check;
	//全体に占い結果を報告済みのプレイヤー
	ArrayList<Judge> declaredJudgedAgentList = new ArrayList<Judge>();

	boolean isSaidAllDivineResult;

	AdvanceGameInfo agi = new AdvanceGameInfo();

	//今日投票しようと思っているプレイヤー
	Agent planningVoteAgent;

	//自分が最後に宣言した「投票しようと思っているプレイヤー」
	Agent declaredPlanningVoteAgent;

	List<Agent> declaredblackAgent = new ArrayList<Agent>();
	//会話をどこまで読んだか
	int readTalkListNum;

	//白のエージェントのリスト
	List<Agent> WhiteList = new ArrayList<Agent>();
	//黒のエージェントのリスト
	List<Agent> BlackList = new ArrayList<Agent>();

	public Agent[][] todayvoteAgent = new Agent[14][2];


	@Override
	public void initialize(GameInfo gameInfo, GameSetting gameSetting) {
		super.initialize(gameInfo, gameSetting);

		List<Agent> AgentList = getLatestDayGameInfo().getAgentList();
		AgentList.remove(getMe());

		comingoutDay = new Random().nextInt(3)+1;
		isCameout = false;
		cocheck = false;
		check = false;

		for(int i = 0;i<todayvoteAgent.length;i++){
			todayvoteAgent[i][0] = AgentList.get(i);
		}

		for(int i = 0;i<psymodel.length;i++ ){
			psymodel[i][0] = 0.3;
			psymodel[i][1] = 0.0;
			psymodel[i][4] = 0.3;
		}
	}





	@Override
	public void dayStart() {
		super.dayStart();

		List<Agent> AgentList = getLatestDayGameInfo().getAgentList();
		AgentList.remove(getMe());

		int Deadnum1 = AgentList.indexOf(getLatestDayGameInfo().getAttackedAgent());
		int Deadnum2 = AgentList.indexOf(getLatestDayGameInfo().getExecutedAgent());

		//襲撃されたエージェント
		//WhiteListに追加
		if(Deadnum1 != -1){
		psymodel[Deadnum1][5] = 0.0;
		if(!WhiteList.contains(AgentList.get(Deadnum1))){
			WhiteList.add(AgentList.get(Deadnum1));
			}
		}

		//投票により吊られたエージェント
		if(Deadnum2 != -1){
		psymodel[Deadnum2][5] = 0.0;
		}

		
		
		for(int i = 0;i<todayvoteAgent.length;i++){
			todayvoteAgent[i][1] = null;
		}

		//投票するプレイヤーの初期化，設定
		declaredPlanningVoteAgent = null;
		planningVoteAgent = null;
	

		isSaidAllDivineResult = false;
		AbstractRole();
		
		readTalkListNum =0;

		if(check == true){
			cocheck = true;
		}

	}


	//投票するエージェントの選択
	public void AbstractRole(){
		List<Agent> aliveAgentList = getLatestDayGameInfo().getAgentList();
		aliveAgentList.remove(getMe());

		List<Agent> aliveAgentList2 = getLatestDayGameInfo().getAliveAgentList();
		aliveAgentList2.remove(getMe());

		planningVoteAgent = aliveAgentList.get(0);

		if(BlackList.size() != 0){
			for(int i = 0;i<BlackList.size();i++){
				if(aliveAgentList2.contains(BlackList.get(i))){
			planningVoteAgent = BlackList.get(i);
				break;
				}

			}
		}else{
		double k = psymodel[0][4] - (psymodel[0][1]+psymodel[0][3]);

		for(int i = 1 ;i<psymodel.length;i++){
			if(psymodel[i][5] == 1.0){
			if(k <= psymodel[i][4]-(psymodel[i][1]+psymodel[i][3])){
				planningVoteAgent = aliveAgentList.get(i);
				k = psymodel[i][4]-(psymodel[i][1]+psymodel[i][3]);


			}
			}
			}
		}

	}


	//会話から心理モデルの変更
	@Override
	public void update(GameInfo gameInfo) {
		super.update(gameInfo);

		List<Agent> AgentList = getLatestDayGameInfo().getAgentList();
		AgentList.remove(getMe());
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

				//占い師のカミングアウトの場合
				if(utterance.getRole() == Role.SEER){
				int COnum1 = AgentList.indexOf(talk.getAgent());
				if(COnum1 != -1){
					if(psymodel[COnum1][4] == 0.0){
						psymodel[COnum1][0] = 0.0;
						psymodel[COnum1][1] = 0.0;
						psymodel[COnum1][2] = 0.0;
						psymodel[COnum1][3] = 1.0;
						psymodel[COnum1][4] = 0.0;
						psymodel[COnum1][6] = 1.0;
					}else if(psymodel[COnum1][4] == 1.0){

						psymodel[COnum1][6] = 1.0;
					}else{
					psymodel[COnum1][0] = 0.0;
					psymodel[COnum1][1] = 0.0;
					psymodel[COnum1][2] = 0.0;
					psymodel[COnum1][3] = 0.3;
					psymodel[COnum1][4] = 0.7;
					psymodel[COnum1][6] = 1.0;
					}
				if(talk.getAgent() != getMe() && !BlackList.contains(talk.getAgent())){
					BlackList.add(talk.getAgent());
				}
				}
				}

				//霊能者のカミングアウトの場合
				if(utterance.getRole() == Role.MEDIUM){
					int COnum2 = AgentList.indexOf(talk.getAgent());
					if(COnum2 != -1){

						if(psymodel[COnum2][4] == 0.0){
							psymodel[COnum2][0] = 0.0;
							psymodel[COnum2][1] = 0.0;
							psymodel[COnum2][2] = 0.5;
							psymodel[COnum2][3] = 0.5;
							psymodel[COnum2][4] = 0.0;
							psymodel[COnum2][6] = 2.0;
						}else if(psymodel[COnum2][4] == 1.0){
							psymodel[COnum2][0] = 0.0;
							psymodel[COnum2][1] = 0.0;
							psymodel[COnum2][2] = 0.0;
							psymodel[COnum2][3] = 0.0;
							psymodel[COnum2][4] = 1.0;
							psymodel[COnum2][6] = 2.0;
						}else{
							psymodel[COnum2][0] = 0.0;
							psymodel[COnum2][1] = 0.0;
							psymodel[COnum2][2] = 0.5;
							psymodel[COnum2][3] = 0.2;
							psymodel[COnum2][4] = 0.3;
							psymodel[COnum2][6] = 2.0;
						}
						}

					}


				//人狼のカミングアウトの場合
				if(utterance.getRole() == Role.WEREWOLF){
					int COnum3 = AgentList.indexOf(talk.getAgent());
					if(COnum3 != -1){
						if(psymodel[COnum3][4] == 1.0){
							psymodel[COnum3][0] = 0.0;
							psymodel[COnum3][1] = 0.0;
							psymodel[COnum3][2] = 0.0;
							psymodel[COnum3][3] = 0.0;
							psymodel[COnum3][4] = 1.0;
							psymodel[COnum3][6] = 3.0;
						}else{
							psymodel[COnum3][0] = 0.0;
							psymodel[COnum3][1] = 0.0;
							psymodel[COnum3][2] = 0.0;
							psymodel[COnum3][3] = 0.1;
							psymodel[COnum3][4] = 0.9;
							psymodel[COnum3][6] = 3.0;
						}
					}
					planningVoteAgent = talk.getAgent();
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


				//自分が人間だと占ったエージェントの心理モデルの変更
				if(seerAgent == getMe() && inspectResult == Species.HUMAN){
					int DIV1num = AgentList.indexOf(inspectedAgent);
					if(DIV1num != -1){
						if(psymodel[DIV1num][6] == 0.0){
						psymodel[DIV1num][0] = 0.3;
						psymodel[DIV1num][1] = 0.0;
						psymodel[DIV1num][2] = 0.2;
						psymodel[DIV1num][3] = 0.2;
						psymodel[DIV1num][4] = 0.3;
						}
						if(psymodel[DIV1num][6] == 1.0){
							psymodel[DIV1num][0] = 0.0;
							psymodel[DIV1num][1] = 0.0;
							psymodel[DIV1num][2] = 0.0;
							psymodel[DIV1num][3] = 1.0;
							psymodel[DIV1num][4] = 0.0;
							}
						if(psymodel[DIV1num][6] == 2.0){
							psymodel[DIV1num][0] = 0.0;
							psymodel[DIV1num][1] = 0.0;
							psymodel[DIV1num][2] = 0.5;
							psymodel[DIV1num][3] = 0.5;
							psymodel[DIV1num][4] = 0.0;
							}

					}
				}

				//自分が人狼だと占ったエージェントの心理モデルの変更
				if(seerAgent == getMe() && inspectResult == Species.WEREWOLF){
					int DIVnum = AgentList.indexOf(inspectedAgent);
					if(DIVnum != -1){
						psymodel[DIVnum][0] = 0.0;
						psymodel[DIVnum][1] = 0.0;
						psymodel[DIVnum][2] = 0.0;
						psymodel[DIVnum][3] = 0.0;
						psymodel[DIVnum][4] = 1.0;
						}
					if(!BlackList.contains(talk.getAgent())){
						BlackList.add(talk.getAgent());
					}
					
				}

				//自分ではない占い師COしたエージェントが人狼と占ったエージェント
				if(seerAgent != getMe() && inspectResult == Species.WEREWOLF){
				int DIVnum1 = AgentList.indexOf(inspectedAgent);
				if(DIVnum1 != -1){
					if(psymodel[DIVnum1][6] == 0.0){
					psymodel[DIVnum1][0] = 0.7;
					psymodel[DIVnum1][1] = 0.0;
					psymodel[DIVnum1][2] = 0.0;
					psymodel[DIVnum1][3] = 0.2;
					psymodel[DIVnum1][4] = 0.1;
					}
					if(psymodel[DIVnum1][6] == 2.0){
						psymodel[DIVnum1][0] = 0.0;
						psymodel[DIVnum1][1] = 0.0;
						psymodel[DIVnum1][2] = 0.7;
						psymodel[DIVnum1][3] = 0.2;
						psymodel[DIVnum1][4] = 0.1;
						}

				}
				}

				//自分のことを人狼だと占ったエージェント
				if(inspectedAgent == getMe() && utterance.getResult() == Species.WEREWOLF){
					int DIVnum2 = AgentList.indexOf(seerAgent);
					if(DIVnum2 != -1){
						psymodel[DIVnum2][0] = 0.0;
						psymodel[DIVnum2][1] = 0.0;
						psymodel[DIVnum2][2] = 0.0;
						psymodel[DIVnum2][3] = 0.3;
						psymodel[DIVnum2][4] = 0.7;
					}
				}
				break;

			case VOTE:
				Agent voteAgent = talk.getAgent();
				Agent inspectedAgent3 = utterance.getTarget();

				for(int j = 0;j<todayvoteAgent.length;j++){
					if(todayvoteAgent[j][0] == voteAgent){
						todayvoteAgent[j][1] = inspectedAgent3;
					}
				}

			}
		}
		readTalkListNum =talkList.size();



		AbstractRole();

	}

	

	//会話
	@Override
	public String talk() {
		//CO,霊能結果，投票先の順に発話の優先度高

		/*
		 * 未CO，かつ設定したCOする日にちを過ぎていたらCO
		 */
		/*!isCameout && getDay() >= comingoutDay*/


		/*
		 * 人狼を見つけるか偽COの出現でCO
		 */



		   	List<Agent> AgentList = getLatestDayGameInfo().getAgentList();
			AgentList.remove(getMe());

			List<Agent> aliveAgentList = getLatestDayGameInfo().getAliveAgentList();
			aliveAgentList.remove(getMe());
			
			
			int mycount;
			mycount = 0;
			for(int i = 0;i<todayvoteAgent.length;i++){
				if(todayvoteAgent[i][1] == getMe()){
					mycount++;
				}
			}

			if(mycount >= 3){
				cocheck = true;
			}

			for(Agent agent: aliveAgentList){
				if(agi.getComingoutMap().containsKey(agent) && agi.getComingoutMap().get(agent) == getMyRole()){
					check = true;

				}else{
					for(Judge judge: getMyJudgeList()){
						if(judge.getTarget().equals(agent) && judge.getResult() == Species.WEREWOLF){
							cocheck = true;

						}
					}
				}
			}

		if(cocheck && !isCameout){
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



			for(int i = 0;i<psymodel.length;i++){

				if(psymodel[i][4] == 1.0 && !declaredblackAgent.contains(AgentList.get(i))){
			String string = TemplateTalkFactory.estimate(AgentList.get(i), Role.WEREWOLF);
			declaredblackAgent.add(AgentList.get(i));
			return string;
				}
			}

			for(int i = 0;i<psymodel.length;i++){

				if(psymodel[i][3] == 1.0 && !declaredblackAgent.contains(AgentList.get(i))){
			String string = TemplateTalkFactory.estimate(AgentList.get(i), Role.POSSESSED);
			declaredblackAgent.add(AgentList.get(i));
			return string;
				}
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

		return TemplateTalkFactory.over();
	}


	//投票
	@Override
	public Agent vote() {
		return planningVoteAgent;
	}


	//占い先の決定
	@Override
	public Agent divine() {
		List<Agent> nonInspectedAgentList = new ArrayList<Agent>();

		List<Agent> AgentList = getLatestDayGameInfo().getAgentList();
		AgentList.remove(getMe());

		List<Agent> aliveAgentList = getLatestDayGameInfo().getAliveAgentList();
		aliveAgentList.remove(getMe());

		
		Agent divinedAgent = null;

		for(Agent agent: aliveAgentList){

			if(!isJudgedAgent(agent)){
				nonInspectedAgentList.add(agent);
			}
		}
		
		Collections.shuffle(nonInspectedAgentList);

		if(nonInspectedAgentList.size() != 0){
		for(int i = 0;i<nonInspectedAgentList.size();i++){
			int nonnum = AgentList.indexOf(nonInspectedAgentList.get(i));

			if(psymodel[nonnum][6] == 0.0){
				divinedAgent = nonInspectedAgentList.get(i);
			}
		}

		if(divinedAgent == null){
			divinedAgent = nonInspectedAgentList.get(0);
		}
		}else{
			divinedAgent = aliveAgentList.get(0);
		}

		/*int [] j = new int[nonInspectedAgentList.size()];
		for(int i = 0;i<nonInspectedAgentList.size();i++){
			j[i] = AgentList.indexOf(nonInspectedAgentList.get(i));
		}

		divinedAgent = aliveAgentList.get(0);
		int m = AgentList.indexOf(aliveAgentList.get(0));


		double l = psymodel[m][4];
		for(int k = 1; k<j.length;k++){
			if(j[k] != -1){
			if(l <= psymodel[j[k]][4]){
				divinedAgent = AgentList.get(j[k]);
				l = psymodel[j[k]][4];
			}
			}
		}*/

		return divinedAgent;

	}


	@Override
	public void finish() {
	}






	}



