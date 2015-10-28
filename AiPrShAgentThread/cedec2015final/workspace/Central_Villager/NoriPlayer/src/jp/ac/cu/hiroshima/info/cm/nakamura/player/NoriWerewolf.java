package jp.ac.cu.hiroshima.info.cm.nakamura.player;

import java.util.ArrayList;
import java.util.List;

import jp.ac.cu.hiroshima.info.cm.nakamura.base.player.NoriAbstractWerewolf;

import org.aiwolf.client.lib.TemplateTalkFactory;
import org.aiwolf.client.lib.Utterance;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Judge;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Species;
import org.aiwolf.common.data.Talk;
import org.aiwolf.common.net.GameInfo;
import org.aiwolf.common.net.GameSetting;

public class NoriWerewolf extends NoriAbstractWerewolf {

	//COする日にち
	int comingoutDay;
	int wolfcount;
	int wolfcount2;
	//CO済みか否か
	boolean isCameout;
	boolean isCameout2;

	//全体に偽占い(霊能)結果を報告済みのJudge
	ArrayList<Judge> declaredFakeJudgedAgentList = new ArrayList<Judge>();


	//全体に偽占い結果を報告していないエージェント
	ArrayList<Agent> norepoList = new ArrayList<Agent>();
	ArrayList<Agent> repoList = new ArrayList<Agent>();
	//全体に占い結果を報告済みのプレイヤー
	//ArrayList<Agent> declaredFakeResultAgent = new ArrayList<>();


	//偽の占いで人間と占ったエージェント
	ArrayList<Agent> FHUMANAgent = new ArrayList<>();
	//偽の占いで人狼と占ったエージェント
	ArrayList<Agent> FWOLFAgent = new ArrayList<>();

	ArrayList<Agent> SeerAgent = new ArrayList<>();

	ArrayList<Agent> MedAgent = new ArrayList<>();

	//自分が占い師と仮定した時のブラックリスト
	ArrayList<Agent> MymaybeBlackList = new ArrayList<>();
	//自分が占い師と仮定した時のブラックリスト内のエージェントの占い結果
	//H:人間　W:人狼
	ArrayList<String> DIVresultBL = new ArrayList<>();

	ArrayList<Agent> eDEADAgent = new ArrayList<>();

	boolean isSaidAllFakeResult;

	AdvanceGameInfo agi = new AdvanceGameInfo();

	//今日投票しようと思っているプレイヤー
	Agent planningVoteAgent;

	//自分が最後に宣言した「投票しようと思っているプレイヤー」
	Agent declaredPlanningVoteAgent;

	//会話をどこまで読んだか
	int readTalkListNum;
	int readWhisperListNum;
	//騙る役職
	Role fakeRole;

	//偽の占い(or霊能)結果
	List<Judge> fakeJudgeList = new ArrayList<Judge>();


	//占い師だと思っているエージェント
	Agent maybeSEERAgent = null;
	//maybeSEERが人狼だと占ったエージェント
	ArrayList<Agent> seermaybeBlackList = new ArrayList<>();
	//霊能者だと思っているエージェント
	Agent maybeMEDAgent = null;

	//偽の占いのターゲット
	Agent fakeGiftTarget = null;
	//偽の占い結果
	Species fakeResult = null;
/*

	//偽の占い(or霊能)結果
	Map<Agent, Species> fakeResultMap = new HashMap<Agent, Species>();
*/
	//狂人だと思うプレイヤー
	Agent maybePossesedAgent = null;


	boolean fakeco;
	boolean cocheck;
	//襲撃するエージェント
	Agent attackAgent;
	boolean check;
	boolean whischeck;
	boolean wolfCOcheck = false;
	boolean FSeerCO = false;
	boolean FMedCO = false;




	public void initialize(GameInfo gameInfo, GameSetting gameSetting){
		super.initialize(gameInfo, gameSetting);
		List<Agent> aliveAgentList = getLatestDayGameInfo().getAliveAgentList();
		aliveAgentList.remove(getMe());


		List<Agent> AgentList = getLatestDayGameInfo().getAgentList();
		AgentList.remove(getMe());
		AgentList.remove(getWolfList());






		norepoList.addAll(aliveAgentList);


		fakeRole = Role.SEER;
		isCameout = false;
		isCameout2 = false;
		fakeco = false;
		cocheck = false;
		wolfcount = 0;
		wolfcount2 = 0;
		check = false;

	}


	@Override
	public void dayStart() {
		List<Agent> AgentList = getLatestDayGameInfo().getAgentList();
		AgentList.remove(getMe());

		List<Agent> wolfList = new ArrayList<Agent>();
		wolfList.addAll(getWolfList());

		//味方の人狼の心理モデル変更
		for(int i = 0;i<wolfList.size();i++){
			int wolfnum = AgentList.indexOf(wolfList.get(i));
			if(wolfnum != -1){
				psymodel[wolfnum][0] = 0.0;
				psymodel[wolfnum][1] = 0.0;
				psymodel[wolfnum][2] = 0.0;
				psymodel[wolfnum][3] = 0.0;
				psymodel[wolfnum][4] = 1.0;
			}
		}

		int Deadnum1 = AgentList.indexOf(getLatestDayGameInfo().getAttackedAgent());
		int Deadnum2 = AgentList.indexOf(getLatestDayGameInfo().getExecutedAgent());

		//襲撃されたエージェントの心理モデル変更
		if(Deadnum1 != -1){
		psymodel[Deadnum1][5] = 0.0;
		norepoList.remove(AgentList.get(Deadnum1));
		}

		//投票により吊られたエージェントの心理モデル変更
		if(Deadnum2 != -1){
		psymodel[Deadnum2][5] = 0.0;
		norepoList.remove(AgentList.get(Deadnum2));
		if(!eDEADAgent.contains(AgentList.get(Deadnum2))){
			eDEADAgent.add(AgentList.get(Deadnum2));
		}
		}



		whischeck = true;

		//投票するプレイヤーの初期化，設定
		declaredPlanningVoteAgent = null;
		fakeGiftTarget = null;
		planningVoteAgent = null;
		attackAgent = null;
		//setPlanningVoteAgent();
		DIVresultBL.clear();
		setAgent();
		if(getDay() >= 1){
			setFakeResult();
		}


		isSaidAllFakeResult = false;
		AbstractRole();

		readTalkListNum =0;
		readWhisperListNum = 0;

	}





	//変数への代入
	public void setAgent(){
		List<Agent> AgentList = getLatestDayGameInfo().getAgentList();
		AgentList.remove(getMe());

		List<Agent> wolfList = new ArrayList<Agent>();
		wolfList.addAll(getWolfList());

		double k0 = 0.0;
		double k1 = 0.0;
		double k2 = 0.0;
		for(int i = 0;i<psymodel.length;i++){

			if(k0 < psymodel[i][3]){
				maybePossesedAgent = AgentList.get(i);
				k0 = psymodel[i][3];
				}
			if(k1 < psymodel[i][1] ){
				maybeSEERAgent = AgentList.get(i);
				k1 = psymodel[i][1];
			}
			if(k2 < psymodel[i][2]){
				maybeMEDAgent = AgentList.get(i);
				k2 = psymodel[i][2];
			}

		}

		for(int i = 0;i<MymaybeBlackList.size();i++){
			if(FWOLFAgent.contains(MymaybeBlackList.get(i))){
				DIVresultBL.add("W");
			}

			else if(FHUMANAgent.contains(MymaybeBlackList.get(i))){
				DIVresultBL.add("H");
			}else{
				DIVresultBL.add("N");
			}

		}





	}


	@Override
	public String talk() {
		//CO,霊能結果，投票先の順に発話の優先度高

		/*
		 * 未CO，かつ設定したCOする日にちを過ぎていたらCO
		 */



		if(getDay() != 0 && !isCameout  && cocheck && fakeRole != Role.VILLAGER ){
			String string = TemplateTalkFactory.comingout(getMe(), fakeRole);
			isCameout = true;
			fakeco = true;
			return string;
		}

		if(wolfCOcheck == true && !isCameout2){
			String string2 = TemplateTalkFactory.comingout(getMe(), Role.WEREWOLF);
			isCameout2 = true;
			return string2;
		}
		/*
		 * COしているなら偽占い，霊能結果の報告
		 */
		if(isCameout && !isSaidAllFakeResult){
			for(Judge judge: getMyFakeJudgeList()){
				if(!declaredFakeJudgedAgentList.contains(judge)){
					if(fakeRole == Role.SEER){
						String string = TemplateTalkFactory.divined(judge.getTarget(), judge.getResult());
						declaredFakeJudgedAgentList.add(judge);
						return string;
					}
					if(fakeRole == Role.MEDIUM){
						String string = TemplateTalkFactory.inquested(judge.getTarget(), judge.getResult());
						declaredFakeJudgedAgentList.add(judge);

						return string;
					}
				}
			}
			isSaidAllFakeResult = true;

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








		return Talk.OVER;
	}



	@Override
	public String whisper() {

		//もし偽の役職が占い師ならCO宣言
		if(fakeRole == Role.SEER && !fakeco && !whischeck){
			String string = TemplateTalkFactory.comingout(getMe(), fakeRole);

			cocheck = true;

			whischeck = true;
			return string;
		}

		if(fakeRole == Role.MEDIUM && !fakeco && !whischeck){
			String string = TemplateTalkFactory.comingout(getMe(), fakeRole);

			cocheck = true;

			whischeck = true;
			return string;
		}



		//何も発しない
		return TemplateTalkFactory.over();
	}






	//投票先の決定
	public void AbstractRole(){
		List<Agent> aliveAgentList = getLatestDayGameInfo().getAgentList();
		aliveAgentList.remove(getMe());

		List<Agent> aliveAgentList2 = getLatestDayGameInfo().getAliveAgentList();
		aliveAgentList2.remove(getMe());

		List<Agent> wolfList = new ArrayList<Agent>();
		wolfList.addAll(getWolfList());



		if(!wolfCOcheck){

		for(int i = 0;i<aliveAgentList2.size();i++){
			if(FWOLFAgent.contains(aliveAgentList2.get(i))){
				planningVoteAgent = aliveAgentList2.get(i);
			}
		}
		if(planningVoteAgent == null){
		double k = (psymodel[0][1]+psymodel[0][2])/2;

		for(int i = 1 ;i<psymodel.length;i++){
			if(psymodel[i][5] == 1.0 && !wolfList.contains(aliveAgentList.get(i))){
			if(k <= (psymodel[i][1]+psymodel[i][2])/2){
				planningVoteAgent = aliveAgentList.get(i);
				k = (psymodel[i][1]+psymodel[i][2])/2;


			}
			}
			}
		}
		}


	}


	//会話から心理モデルの更新
	@Override
	public void update(GameInfo gameInfo) {
		super.update(gameInfo);
		List<Agent> AgentList = getLatestDayGameInfo().getAgentList();
		AgentList.remove(getMe());
		AgentList.removeAll(getWolfList());

		List<Agent> wolfList = new ArrayList<Agent>();
		wolfList.addAll(getWolfList());

		List<Agent> aliveAgentList = getLatestDayGameInfo().getAliveAgentList();
		aliveAgentList.remove(getMe());

		List<Agent> AgentList1 = getLatestDayGameInfo().getAgentList();
		AgentList1.remove(getMe());
		//AgentList.remove(maybePossesedAgent);
		List<Talk> talkList = gameInfo.getTalkList();
		List<Talk> WhisperList = gameInfo.getWhisperList();


		//Whisperの発話
		for(int j = readWhisperListNum;j < WhisperList.size();j++){
			Talk whisper = WhisperList.get(j);
			Utterance utterancew = new Utterance(whisper.getContent());
			switch(utterancew.getTopic()){


			//カミングアウトの場合
			//霊能者なら偽の役職を占い師に、占い師なら村人に
		/*	case COMINGOUT:
				agi.getwhisComingoutMap().put(whisper.getAgent(),utterancew.getRole());



					if(whisper.getAgent() != getMe() && utterancew.getRole() == Role.MEDIUM){
							fakeRole = Role.SEER;


						}
					if(whisper.getAgent() != getMe() && utterancew.getRole() == Role.SEER){
							fakeRole = Role.VILLAGER;

					}
	*/



		 case VOTE:

				Agent inspectedAgent = utterancew.getTarget();

				if(inspectedAgent != getMe() && wolfCOcheck == true){
					planningVoteAgent = inspectedAgent;
				}

				if(getDay() >= 5){
					if(inspectedAgent != getMe()){
						planningVoteAgent = inspectedAgent;
					}
				}
				}


				}




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

				//占い師の場合
				if(utterance.getRole() == Role.SEER){
				int COnum1 = AgentList1.indexOf(talk.getAgent());
				if(COnum1 != -1){
				psymodel[COnum1][0] = 0.0;
				psymodel[COnum1][1] = 0.5;
				psymodel[COnum1][2] = 0.0;
				psymodel[COnum1][3] = 0.5;
				psymodel[COnum1][4] = 0.0;
				}
				/*if(wolfList.contains(talk.getAgent())){
					fakeco = true;
				}*/



				if(!wolfList.contains(talk.getAgent())){
				maybeSEERAgent = talk.getAgent();
				//cocheck = true;

				}else{
					FSeerCO = true;
				}

				if(talk.getAgent() != getMe() && !MymaybeBlackList.contains(talk.getAgent())){
					MymaybeBlackList.add(talk.getAgent());
				}
				}


				//霊能者の場合
				if(utterance.getRole() == Role.MEDIUM){
					int COnum2 = AgentList1.indexOf(talk.getAgent());
					if(COnum2 != -1){
					psymodel[COnum2][0] = 0.0;
					psymodel[COnum2][1] = 0.0;
					psymodel[COnum2][2] = 0.5;
					psymodel[COnum2][3] = 0.5;
					psymodel[COnum2][4] = 0.0;
					}
					if(!wolfList.contains(talk.getAgent())){
						maybeMEDAgent = talk.getAgent();
						}else{
							FMedCO = true;
						}

					}




				//狩人の場合
				if(utterance.getRole() == Role.BODYGUARD){
					if(!wolfList.contains(talk.getAgent())){
						if(!FWOLFAgent.contains(talk.getAgent())){
						attackAgent = talk.getAgent();
						}
						}
				}



				//人狼の場合
				if(utterance.getRole() == Role.WEREWOLF && wolfList.contains(talk.getAgent())){
					wolfCOcheck = true;
				}


			//占い結果の発話の場合
			case DIVINED:
				//AGIのJudgeListに結果を加える
				Agent seerAgent = talk.getAgent();
				Agent inspectedAgent = utterance.getTarget();
				Species inspectResult = utterance.getResult();
				Judge judge = new Judge(getDay(), seerAgent, inspectedAgent, inspectResult);
				agi.addInspectJudgeList(judge);


				if(!wolfList.contains(seerAgent)){


					//占い先が自分で人狼と占われた場合
				if(inspectedAgent == getMe() && utterance.getResult() == Species.WEREWOLF && !wolfList.contains(seerAgent)){
					int DIVnum2 = AgentList1.indexOf(seerAgent);
					if(DIVnum2 != -1){
						psymodel[DIVnum2][0] = 0.0;
						psymodel[DIVnum2][1] = 0.7;
						psymodel[DIVnum2][2] = 0.0;
						psymodel[DIVnum2][3] = 0.3;
						psymodel[DIVnum2][4] = 0.0;

						if(!FSeerCO){
							fakeRole = Role.SEER;
							whischeck = false;

						}else if(FSeerCO && !FMedCO){
							fakeRole = Role.MEDIUM;
							fakeJudgeList.clear();
							setFakeResult();
							whischeck = false;
						}else if(FSeerCO && FMedCO){
							fakeRole = Role.VILLAGER;
						}


					}



				}
				//占い先が自分で人間と占われた場合
				else if(inspectedAgent == getMe() && utterance.getResult() == Species.HUMAN && !wolfList.contains(seerAgent)){
					int DIVnum3 = AgentList1.indexOf(seerAgent);
					if(DIVnum3 != -1){
						psymodel[DIVnum3][0] = 0.0;
						psymodel[DIVnum3][1] = 0.0;
						psymodel[DIVnum3][2] = 0.0;
						psymodel[DIVnum3][3] = 1.0;
						psymodel[DIVnum3][4] = 0.0;
					}
				}
				}


				//占い師が人狼であり、人狼だと占われたエージェントをリストに追加
				if(wolfList.contains(seerAgent) && utterance.getResult() == Species.WEREWOLF){
					if(!FWOLFAgent.contains(inspectedAgent)){
					FWOLFAgent.add(inspectedAgent);
					}
				}

				if(seerAgent == maybeSEERAgent && utterance.getResult() == Species.WEREWOLF){
					if(!seermaybeBlackList.contains(inspectedAgent)){
						seermaybeBlackList.add(inspectedAgent);
					}
				}



				//霊能結果の発話
			case INQUESTED:

				Agent medAgent = talk.getAgent();
				Agent inspectedAgent2 = utterance.getTarget();
				Species inspectResult2 = utterance.getResult();
				int inqnum = AgentList1.indexOf(medAgent);


				//霊能結果が人狼であり、偽占い師が人間だと占っていた場合
				if(inspectResult2 == Species.WEREWOLF && FHUMANAgent.contains(inspectedAgent2)){
					if(!MymaybeBlackList.contains(medAgent)){
						MymaybeBlackList.add(medAgent);
					}
				}


				//霊能結果が人間であり、偽の占い師が人狼だと占っていた場合
				if(inspectResult2 == Species.HUMAN && FWOLFAgent.contains(inspectedAgent2)){
					if(!MymaybeBlackList.contains(medAgent)){
						MymaybeBlackList.add(medAgent);
					}
				}

				//霊能結果が人間であり、そのエージェントが人狼だった場合
				//狂人濃厚
				if(inspectResult2 == Species.HUMAN && wolfList.contains(inspectedAgent2) && !wolfList.contains(medAgent)){
					if(inqnum != -1){
						psymodel[inqnum][0] = 0.0;
						psymodel[inqnum][1] = 0.0;
						psymodel[inqnum][2] = 0.0;
						psymodel[inqnum][3] = 1.0;
						psymodel[inqnum][4] = 0.0;
					}
				}

				//霊能結果が人狼であり、そのエージェントが人狼の場合
				//狂人濃厚
				if(inspectResult2 == Species.WEREWOLF && !wolfList.contains(inspectedAgent2) && !wolfList.contains(medAgent)){
					if(inqnum != -1){
						psymodel[inqnum][0] = 0.0;
						psymodel[inqnum][1] = 0.0;
						psymodel[inqnum][2] = 0.0;
						psymodel[inqnum][3] = 1.0;
						psymodel[inqnum][4] = 0.0;
					}
				}


				//霊能結果が人狼であり、そのエージェントが人狼である場合
				if(inspectResult2 == Species.WEREWOLF && wolfList.contains(inspectedAgent2) && !wolfList.contains(medAgent)){
					if(inqnum != -1){
						psymodel[inqnum][0] = 0.0;
						psymodel[inqnum][1] = 0.0;
						psymodel[inqnum][2] = 0.6;
						psymodel[inqnum][3] = 0.4;
						psymodel[inqnum][4] = 0.0;
					}
				}


				//voteの発話の場合
			case VOTE:
				Agent voteAgent = talk.getAgent();
				Agent inspectedAgent3 = utterance.getTarget();

				if(wolfList.contains(voteAgent) && wolfCOcheck == true){
					planningVoteAgent = inspectedAgent3;
				}

				if(getDay() >= 6){
					if(wolfList.contains(voteAgent)){
						planningVoteAgent = inspectedAgent3;
					}
				}

			/*case ESTIMATE:

				Agent estimateAgent = talk.getAgent();
				Agent inspectedAgent4 = utterance.getTarget();
				Role inspectResult4 = utterance.getRole();

				if(inspectResult4 == Role.SEER && inspectedAgent4 == maybeSEERAgent){
					if(!FWOLFAgent.contains(inspectedAgent4)){
						attackAgent = talk.getAgent();
					}
				}*/



		}


		readTalkListNum =talkList.size();




		}
		setAgent();

		AbstractRole();



	}



	@Override
	public Agent vote() {
		return planningVoteAgent;
	}

	@Override
	public Agent attack() {


		List<Agent> AgentList = getLatestDayGameInfo().getAgentList();
		AgentList.removeAll(getWolfList());
		AgentList.remove(maybePossesedAgent);


		List<Agent> aliveAgentList = getLatestDayGameInfo().getAliveAgentList();
		aliveAgentList.remove(getMe());
		aliveAgentList.removeAll(getWolfList());
		aliveAgentList.remove(maybePossesedAgent);


		//偽の占いで人狼と占われていないエージェントから選択
		if(attackAgent == null){

		for(int i = 0;i<aliveAgentList.size();i++){
		if(psymodel[i][5] == 1.0 && !FWOLFAgent.contains(aliveAgentList.get(i))){
			if(aliveAgentList.get(i) != planningVoteAgent){
			attackAgent = aliveAgentList.get(i);
			break;
			}
		}
		}
		}

		return attackAgent;
	}

	@Override
	public void finish() {
	}

	/**
	 * 今日投票予定のプレイヤーを設定する．
	 */

	/**
	 * 能力者騙りをする際に，偽の占い(or霊能)結果を作成する．
	 */


	/*
	 * 矛盾が生じないように
	 * 人狼の人数が３人以上にならない
	 * 自分から見た怪しいエージェントの中に狂人が２人存在しない
	 */
	public void setFakeResult(){
		List<Agent> aliveAgentList = getLatestDayGameInfo().getAliveAgentList();
		aliveAgentList.remove(getMe());

		List<Agent> wolfList = new ArrayList<Agent>();
		wolfList.addAll(getWolfList());



		if(fakeRole == Role.VILLAGER){
			return;
		}

		if(fakeRole == Role.SEER){



			if(norepoList.size() == 0){
				return;
			}
			if(getDay() <= 2){
			for(int i = 0;i<norepoList.size();i++){
				if(!MymaybeBlackList.contains(norepoList.get(i))){
					fakeGiftTarget = norepoList.get(i);
					fakeResult = Species.HUMAN;
					FHUMANAgent.add(fakeGiftTarget);
					norepoList.remove(fakeGiftTarget);
					break;
				}
			}
			}

			if(getDay() >= 3){



				for(int j = 0;j<MymaybeBlackList.size();j++){

					if(aliveAgentList.contains(MymaybeBlackList.get(j)) && norepoList.contains(MymaybeBlackList.get(j))){
					if(DIVresultBL.contains("H")){
						if(DIVresultBL.get(j) == "N"){
							fakeGiftTarget = MymaybeBlackList.get(j);
							fakeResult = Species.WEREWOLF;
							FWOLFAgent.add(fakeGiftTarget);
							norepoList.remove(fakeGiftTarget);
							wolfcount++;
						}
					}else{

					if(DIVresultBL.get(j) == "N" && wolfcount != 3){
						fakeGiftTarget = MymaybeBlackList.get(j);
						fakeResult = Species.WEREWOLF;
						FWOLFAgent.add(fakeGiftTarget);
						norepoList.remove(fakeGiftTarget);
						wolfcount++;
					}else if(DIVresultBL.get(j) == "N" && wolfcount == 3){
						fakeGiftTarget =  MymaybeBlackList.get(j);
						fakeResult = Species.HUMAN;
						FHUMANAgent.add(fakeGiftTarget);
						norepoList.remove(fakeGiftTarget);
					}
					}
				}

			}

				if(fakeGiftTarget == null){
				if(aliveAgentList.size() >= 7){

					fakeGiftTarget = norepoList.get(0);
					fakeResult = Species.HUMAN;
					FHUMANAgent.add(fakeGiftTarget);
					norepoList.remove(fakeGiftTarget);

				}else{
					if(DIVresultBL.size() != 4 && wolfcount != 3){
						if(!wolfList.contains(norepoList.get(0))){
						fakeGiftTarget = norepoList.get(0);
						fakeResult = Species.WEREWOLF;
						FWOLFAgent.add(fakeGiftTarget);
						norepoList.remove(fakeGiftTarget);
						wolfcount++;
					}else if(DIVresultBL.size() == 4){
						fakeGiftTarget = norepoList.get(0);
						fakeResult = Species.HUMAN;
						FHUMANAgent.add(fakeGiftTarget);
						norepoList.remove(fakeGiftTarget);
					}
				}
				}



			}


		}


		if(fakeGiftTarget != null){
			fakeJudgeList.add(new Judge(getDay(), getMe(), fakeGiftTarget, fakeResult));
		}


	}
		if(fakeRole == Role.MEDIUM){
			for(int i = 0;i<eDEADAgent.size();i++){
				if(wolfcount2 == 2 && !repoList.contains(eDEADAgent.get(i))){
					fakeGiftTarget = eDEADAgent.get(i);
					fakeResult = Species.HUMAN;
					repoList.add(fakeGiftTarget);
					fakeJudgeList.add(new Judge(getDay(), getMe(), fakeGiftTarget, fakeResult));
				}else if(seermaybeBlackList.contains(eDEADAgent.get(i)) && !FWOLFAgent.contains(eDEADAgent.get(i)) && !repoList.contains(eDEADAgent.get(i))){
					fakeGiftTarget = eDEADAgent.get(i);
					fakeResult = Species.HUMAN;
					repoList.add(fakeGiftTarget);
					fakeJudgeList.add(new Judge(getDay(), getMe(), fakeGiftTarget, fakeResult));
				}else if(seermaybeBlackList.contains(eDEADAgent.get(i)) && FWOLFAgent.contains(eDEADAgent.get(i)) && !repoList.contains(eDEADAgent.get(i))){

					fakeGiftTarget = eDEADAgent.get(i);
					fakeResult = Species.WEREWOLF;
					repoList.add(fakeGiftTarget);
					wolfcount2++;
					fakeJudgeList.add(new Judge(getDay(), getMe(), fakeGiftTarget, fakeResult));
				}else if(!seermaybeBlackList.contains(eDEADAgent.get(i)) && FWOLFAgent.contains(eDEADAgent.get(i)) && !repoList.contains(eDEADAgent.get(i))){

						fakeGiftTarget = eDEADAgent.get(i);
						fakeResult = Species.WEREWOLF;
						repoList.add(fakeGiftTarget);
						wolfcount2++;
						fakeJudgeList.add(new Judge(getDay(), getMe(), fakeGiftTarget, fakeResult));
				}else if(!seermaybeBlackList.contains(eDEADAgent.get(i)) && !FWOLFAgent.contains(eDEADAgent.get(i)) && !repoList.contains(eDEADAgent.get(i))){
					if(getDay() >= 5 && wolfcount2 == 0){
						fakeGiftTarget = eDEADAgent.get(i);
						fakeResult = Species.WEREWOLF;
						repoList.add(fakeGiftTarget);
						wolfcount2++;
						fakeJudgeList.add(new Judge(getDay(), getMe(), fakeGiftTarget, fakeResult));
						}else{
							fakeGiftTarget = eDEADAgent.get(i);
							fakeResult = Species.HUMAN;
							repoList.add(fakeGiftTarget);
							fakeJudgeList.add(new Judge(getDay(), getMe(), fakeGiftTarget, fakeResult));
						}
					}
				}


			}

	}





	public List<Judge> getMyFakeJudgeList(){
		return fakeJudgeList;
	}


	/**
	 * すでに占い(or霊能)対象にしたプレイヤーならtrue,まだ占っていない(霊能していない)ならばfalseを返す．
	 * @param myJudgeList
	 * @param agent
	 * @return
	 */
	public boolean isJudgedAgent(Agent agent){
		for(Judge judge: getMyFakeJudgeList()){
			if(judge.getAgent() == agent){
				return true;
			}
		}
		return false;
	}

}
