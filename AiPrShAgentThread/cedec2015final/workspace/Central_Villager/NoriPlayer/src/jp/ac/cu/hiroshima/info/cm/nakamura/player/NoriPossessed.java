package jp.ac.cu.hiroshima.info.cm.nakamura.player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import jp.ac.cu.hiroshima.info.cm.nakamura.base.player.NoriAbstractPossessed;

import org.aiwolf.client.lib.TemplateTalkFactory;
import org.aiwolf.client.lib.Utterance;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Judge;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Species;
import org.aiwolf.common.data.Talk;
import org.aiwolf.common.net.GameInfo;
import org.aiwolf.common.net.GameSetting;

public class NoriPossessed extends NoriAbstractPossessed {

	//COする日にち
	int comingoutDay;

	int wolfcount;
	int wolfcount2;
	//CO済みか否か
	boolean isCameout;
	boolean isCameout2;

	//全体に偽占い(霊能)結果を報告済みのJudge
	ArrayList<Judge> declaredFakeJudgedAgentList = new ArrayList<Judge>();

	//全体に占い結果を報告済みのプレイヤー
//	ArrayList<Agent> declaredFakeResultAgent = new ArrayList<>();
	boolean isSaidAllFakeResult;
	boolean wolfCOcheck;
	
	AdvanceGameInfo agi = new AdvanceGameInfo();
	
	
	//まだ偽の占い（霊能）結果を報告していないエージェント
	ArrayList<Agent> norepoList = new ArrayList<Agent>();
	//報告済みのエージェント
	ArrayList<Agent> repoList = new ArrayList<Agent>();
	//今日投票しようと思っているプレイヤー
	Agent planningVoteAgent;
	
	
	//占い師だと思っているエージェント
	Agent maybeSEERAgent = null;
	//霊能者だと思っているエージェント
	Agent maybeMEDAgent = null;
	//偽の占い師だと思っているエージェント
	Agent maybeFSEERAgent = null;
	
	//占い師COしたエージェント
	ArrayList<Agent> SeerList = new ArrayList<Agent>();
	
	
	//各占い師COエージェントに人狼だと占われたエージェントリスト
	ArrayList<Agent> divwolfList0 = new ArrayList<Agent>();
	ArrayList<Agent> divwolfList1 = new ArrayList<Agent>();
	ArrayList<Agent> divwolfList2 = new ArrayList<Agent>();
	ArrayList<Agent> divwolfList3 = new ArrayList<Agent>();
	ArrayList<Agent> divwolfList4 = new ArrayList<Agent>();
	
	//自分が最後に宣言した「投票しようと思っているプレイヤー」
	Agent declaredPlanningVoteAgent;

	//会話をどこまで読んだか
	int readTalkListNum;
	
	
	//偽の占い師の時人間(人狼)と占ったエージェントのリスト
	ArrayList<Agent> FHUMANAgent = new ArrayList<>();
	ArrayList<Agent> FWOLFAgent = new ArrayList<>();
	//殺されたエージェントのリスト
	ArrayList<Agent> DEADAgentList = new ArrayList<>();
	
	//白であるエージェントのリスト
	ArrayList<Agent> WHITEList = new ArrayList<>();
	//おそらく人狼だと思っているエージェントのリスト
	ArrayList<Agent> maybeWOLFList = new ArrayList<>();
	
	//人狼COしたエージェントのリスト
	ArrayList<Agent> COWOLFList = new ArrayList<>();
	//占い師のCOの数
	int SeerCO;
	
	//騙る役職
	Role fakeRole;

	//偽の占い(or霊能)結果
	List<Judge> fakeJudgeList = new ArrayList<Judge>();
	
	
	

	public void initialize(GameInfo gameInfo, GameSetting gameSetting){
		super.initialize(gameInfo, gameSetting);


		
		List<Agent> aliveAgentList = getLatestDayGameInfo().getAliveAgentList();
		aliveAgentList.remove(getMe());

		fakeRole = Role.SEER;

		
	
		isCameout = false;
		isCameout2 = false;
		wolfCOcheck =false;
		
		for(int i = 0;i<psymodel.length;i++ ){
			psymodel[i][0] = 0.3;
			psymodel[i][3] = 0.0;
			psymodel[i][4] = 0.3;
		}
		
		SeerCO = 0;
		wolfcount = 0;
		wolfcount2 = 0;
		norepoList.addAll(aliveAgentList);
	}


	@Override
	public void dayStart() {
		
		List<Agent> AgentList = getLatestDayGameInfo().getAgentList();
		AgentList.remove(getMe());
		
	
		int Deadnum1 = AgentList.indexOf(getLatestDayGameInfo().getAttackedAgent());
		int Deadnum2 = AgentList.indexOf(getLatestDayGameInfo().getExecutedAgent());
		
		//襲撃されたエージェント
		if(Deadnum1 != -1){
		psymodel[Deadnum1][5] = 0.0;
		norepoList.remove(AgentList.get(Deadnum1));
		DEADAgentList.add(AgentList.get(Deadnum1));
		WHITEList.add(AgentList.get(Deadnum1));
		}
		//投票により吊られたエージェント
		if(Deadnum2 != -1){
		psymodel[Deadnum2][5] = 0.0;
		norepoList.remove(AgentList.get(Deadnum2));
		DEADAgentList.add(AgentList.get(Deadnum2));
		}
		//投票するプレイヤーの初期化，設定
		declaredPlanningVoteAgent = null;
		planningVoteAgent = null;
		
		if(getDay() == 4 && SeerCO >= 2){
		for(int i = 0;i< SeerList.size();i++){
			if(maybeSEERAgent != SeerList.get(i)){
				maybeFSEERAgent = SeerList.get(i);
			}
		}
		}
		
		//４日目以降に占い師COしているエージェントが２人以上なら偽の役職を霊能者に
		if(getDay() >= 4 && SeerCO >= 2){
			fakeRole = Role.MEDIUM;
			fakeJudgeList.clear();
		}
		
		setAgent();
		
		if(getDay() >= 1){
			setFakeResult();
		}
		isSaidAllFakeResult = false;

		readTalkListNum =0;
	}

	public void setAgent(){
		List<Agent> AgentList = getLatestDayGameInfo().getAgentList();
		AgentList.remove(getMe());
		
		
		double k1 = 0.0;
		double k2 = 0.0;
		for(int i = 0;i<psymodel.length;i++){
		
			//maybeSEERとmaybeMEDのセット
			if(k1 < psymodel[i][1]){
				maybeSEERAgent = AgentList.get(i);
				k1 = psymodel[i][1];
			}
			if(k2 < psymodel[i][2]){
				maybeMEDAgent = AgentList.get(i);
				k2 = psymodel[i][2];
			}
			
		}
		
	}
	
	@Override
	public String talk() {
		//CO,霊能結果，投票先の順に発話の優先度高

		/*
		 * 未CO，かつ設定したCOする日にちを過ぎていたらCO
		 */

		if(!isCameout && getDay() == 4){
			String string = TemplateTalkFactory.comingout(getMe(), fakeRole);
			isCameout = true;
			return string;
		}
		
		if(wolfCOcheck && !isCameout2){
			String string = TemplateTalkFactory.comingout(getMe(), getMyRole());
			isCameout2 = true;
			return string;
		}
		
		
		/*
		 * COしているなら偽占い，霊能結果の報告
		 */
		else if(isCameout && !isSaidAllFakeResult){
			for(Judge judge: getMyFakeJudgeList()){
				if(!declaredFakeJudgedAgentList.contains(judge)){
					if(fakeRole == Role.SEER){
						String string = TemplateTalkFactory.divined(judge.getTarget(), judge.getResult());
						declaredFakeJudgedAgentList.add(judge);
						return string;
					}else if(fakeRole == Role.MEDIUM){
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

		else{
			return Talk.OVER;
		}

	}
	
	
	
	//投票先の決定
	public void AbstractRole(){
		List<Agent> aliveAgentList = getLatestDayGameInfo().getAgentList();
		aliveAgentList.remove(getMe());
		
		List<Agent> aliveAgentList2 = getLatestDayGameInfo().getAliveAgentList();
		aliveAgentList2.remove(getMe());
		
		
		if(!wolfCOcheck){
	if(planningVoteAgent == null){
		double k = (psymodel[0][1]+psymodel[0][2])/2;
	
		for(int i = 1 ;i<psymodel.length;i++){
			if(psymodel[i][5] == 1.0){
			if(k <= (psymodel[i][1]+psymodel[i][2])/2){
				if(!maybeWOLFList.contains(aliveAgentList.get(i))){
				planningVoteAgent = aliveAgentList.get(i);
				k = (psymodel[i][1]+psymodel[i][2])/2;
				}
				
			}
			}
			}
		
		if(getDay() >= 5){
			for(int j = 0;j < aliveAgentList2.size();j++){
				if(!maybeWOLFList.contains(aliveAgentList2.get(j))){
					planningVoteAgent = aliveAgentList2.get(j);
				}
			}
		}
	}
		
	}
			
		
	}
	@Override
	public void update(GameInfo gameInfo) {
		super.update(gameInfo);
		List<Agent> AgentList = getLatestDayGameInfo().getAgentList();
		AgentList.remove(getMe());
		List<Talk> talkList = gameInfo.getTalkList();
		//boolean existInspectResult = false;

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
				
				
				if(utterance.getRole() == Role.SEER){
				int COnum1 = AgentList.indexOf(talk.getAgent());
				if(COnum1 != -1){
				psymodel[COnum1][0] = 0.0;
				psymodel[COnum1][1] = 0.5;
				psymodel[COnum1][2] = 0.0;
				psymodel[COnum1][3] = 0.0;
				psymodel[COnum1][4] = 0.5;
				psymodel[COnum1][6] = 1.0;
				}
				if(talk.getAgent() != getMe()){
					SeerCO++;
					if(!SeerList.contains(talk.getAgent())){
					SeerList.add(talk.getAgent());
					}
				}
				}
				
				if(utterance.getRole() == Role.MEDIUM){
					int COnum2 = AgentList.indexOf(talk.getAgent());
					if(COnum2 != -1){
					psymodel[COnum2][0] = 0.0;
					psymodel[COnum2][1] = 0.0;
					psymodel[COnum2][2] = 0.5;
					psymodel[COnum2][3] = 0.0;
					psymodel[COnum2][4] = 0.5;
					psymodel[COnum2][6] = 2.0;
					}
					}
				
				if(utterance.getRole() == Role.WEREWOLF){
					if(!COWOLFList.contains(talk.getAgent())){
						COWOLFList.add(talk.getAgent());
						wolfCOcheck = true;
					}
				}
				//break;

			//占い結果の発話の場合
			case DIVINED:
				//AGIのJudgeListに結果を加える
				Agent seerAgent = talk.getAgent();
				Agent inspectedAgent = utterance.getTarget();
				Species inspectResult = utterance.getResult();
				Judge judge = new Judge(getDay(), seerAgent, inspectedAgent, inspectResult);
				agi.addInspectJudgeList(judge);
				
				if(utterance.getResult() == Species.WEREWOLF){
				int DIVnum1 = AgentList.indexOf(inspectedAgent);
				int SEERnum = SeerList.indexOf(seerAgent);
				if(DIVnum1 != -1){
					if(psymodel[DIVnum1][6] == 0.0){
					psymodel[DIVnum1][0] = 0.3;
					psymodel[DIVnum1][1] = 0.1;
					psymodel[DIVnum1][2] = 0.1;
					psymodel[DIVnum1][3] = 0.0;
					psymodel[DIVnum1][4] = 0.5;
					}
					if(psymodel[DIVnum1][6] == 1.0){
						psymodel[DIVnum1][0] = 0.0;
						psymodel[DIVnum1][1] = 0.5;
						psymodel[DIVnum1][2] = 0.0;
						psymodel[DIVnum1][3] = 0.0;
						psymodel[DIVnum1][4] = 0.5;
						}
					if(psymodel[DIVnum1][6] == 2.0){
						psymodel[DIVnum1][0] = 0.0;
						psymodel[DIVnum1][1] = 0.0;
						psymodel[DIVnum1][2] = 0.5;
						psymodel[DIVnum1][3] = 0.0;
						psymodel[DIVnum1][4] = 0.5;
						}
					}
				if(SEERnum == 0){
					if(!divwolfList0.contains(inspectedAgent)){
					divwolfList0.add(inspectedAgent);
					}
				}
				
				if(SEERnum == 1){
					if(!divwolfList1.contains(inspectedAgent)){
					divwolfList1.add(inspectedAgent);
					}
				}
				
				if(SEERnum == 2){
					if(!divwolfList2.contains(inspectedAgent)){
					divwolfList2.add(inspectedAgent);
					}
				}
				
				if(SEERnum == 3){
					if(!divwolfList3.contains(inspectedAgent)){
					divwolfList3.add(inspectedAgent);
					}
				}
				
				if(SEERnum == 4){
					if(!divwolfList4.contains(inspectedAgent)){
					divwolfList4.add(inspectedAgent);
					}
				}
				}
				if(utterance.getResult() == Species.HUMAN){
					int DIVnum1 = AgentList.indexOf(inspectedAgent);
					if(DIVnum1 != -1){
						if(psymodel[DIVnum1][6] == 0.0){
							psymodel[DIVnum1][0] = 0.5;
							psymodel[DIVnum1][1] = 0.1;
							psymodel[DIVnum1][2] = 0.1;
							psymodel[DIVnum1][3] = 0.0;
							psymodel[DIVnum1][4] = 0.3;
							}
						if(psymodel[DIVnum1][6] == 1.0){
							psymodel[DIVnum1][0] = 0.0;
							psymodel[DIVnum1][1] = 0.6;
							psymodel[DIVnum1][2] = 0.0;
							psymodel[DIVnum1][3] = 0.0;
							psymodel[DIVnum1][4] = 0.4;
							}
						if(psymodel[DIVnum1][6] == 2.0){
							psymodel[DIVnum1][0] = 0.0;
							psymodel[DIVnum1][1] = 0.0;
							psymodel[DIVnum1][2] = 0.6;
							psymodel[DIVnum1][3] = 0.0;
							psymodel[DIVnum1][4] = 0.4;
							}
						}
					}
				
				if(inspectedAgent == getMe() && utterance.getResult() == Species.WEREWOLF){
					int DIVnum2 = AgentList.indexOf(seerAgent);
					if(DIVnum2 != -1){
						psymodel[DIVnum2][0] = 0.0;
						psymodel[DIVnum2][1] = 0.3;
						psymodel[DIVnum2][2] = 0.0;
						psymodel[DIVnum2][3] = 0.0;
						psymodel[DIVnum2][4] = 0.7;
					}
					if(!maybeWOLFList.contains(seerAgent)){
						maybeWOLFList.add(seerAgent);
					}
				}
				else if(inspectedAgent == getMe() && utterance.getResult() == Species.HUMAN){
					int DIVnum3 = AgentList.indexOf(seerAgent);
					if(DIVnum3 != -1){
						psymodel[DIVnum3][0] = 0.0;
						psymodel[DIVnum3][1] = 0.5;
						psymodel[DIVnum3][2] = 0.0;
						psymodel[DIVnum3][3] = 0.0;
						psymodel[DIVnum3][4] = 0.5;
					}
				}
				
				if(maybeSEERAgent == seerAgent && utterance.getResult() == Species.WEREWOLF && inspectedAgent != getMe()){
					if(!maybeWOLFList.contains(inspectedAgent)){
					maybeWOLFList.add(inspectedAgent);
					}
				}
				
			case VOTE:
				Agent voteAgent = talk.getAgent();
				Agent inspectedAgent3 = utterance.getTarget();
				
				if(COWOLFList.contains(voteAgent)){
					planningVoteAgent = inspectedAgent3;
				}
				
				//break;
			}
		}
		readTalkListNum =talkList.size();

		
		
		AbstractRole();
		
	}

	@Override
	public Agent vote() {
		return planningVoteAgent;
	}

	@Override
	public void finish() {
		// TODO 自動生成されたメソッド・スタブ

	}


	/**
	 * 今日投票予定のプレイヤーを設定する．
	 */

	

	/**
	 * 能力者騙りをする際に，偽の占い(or霊能)結果を作成する．
	 */
	public void setFakeResult(){
		Agent fakeGiftTarget = null;

		Species fakeResult = null;

		if(fakeRole == Role.SEER){
			if(norepoList.size() == 0){
				return;
			}
			if(wolfcount == 3){
				fakeGiftTarget = norepoList.get(0);
				fakeResult = Species.HUMAN;
				FHUMANAgent.add(fakeGiftTarget);
				norepoList.remove(fakeGiftTarget);
				fakeJudgeList.add(new Judge(getDay(), getMe(), fakeGiftTarget, fakeResult));
				return;
				
			}
			if(getDay() <= 3){
				for(int i = 0;i<norepoList.size();i++){
				if(norepoList.get(i) != maybeSEERAgent &&norepoList.get(i) != maybeMEDAgent ){
					fakeGiftTarget =norepoList.get(i);
					fakeResult = Species.HUMAN;
					FHUMANAgent.add(fakeGiftTarget);
					norepoList.remove(fakeGiftTarget);
					fakeJudgeList.add(new Judge(getDay(), getMe(), fakeGiftTarget, fakeResult));
					break;
				}
				}
				return;
				}else if(getDay() == 4){
				
						if(norepoList.get(0) == maybeSEERAgent || norepoList.get(0) == maybeMEDAgent){
							fakeGiftTarget = norepoList.get(0);
							fakeResult = Species.WEREWOLF;
							FWOLFAgent.add(fakeGiftTarget);
							norepoList.remove(fakeGiftTarget);
							wolfcount++;
							fakeJudgeList.add(new Judge(getDay(), getMe(), fakeGiftTarget, fakeResult));
							return;
							
						}else{
							fakeGiftTarget = norepoList.get(0);
							fakeResult = Species.HUMAN;
							FHUMANAgent.add(fakeGiftTarget);
							norepoList.remove(fakeGiftTarget);
							fakeJudgeList.add(new Judge(getDay(), getMe(), fakeGiftTarget, fakeResult));
							return;
						}
			
				}else{
					if(norepoList.size() == 0){
						return;
					}
					if(norepoList.get(0) == maybeSEERAgent || norepoList.get(0) == maybeMEDAgent){
						fakeGiftTarget = norepoList.get(0);
						fakeResult = Species.WEREWOLF;
						FWOLFAgent.add(fakeGiftTarget);
						norepoList.remove(fakeGiftTarget);
						wolfcount++;
						fakeJudgeList.add(new Judge(getDay(), getMe(), fakeGiftTarget, fakeResult));
						return;
						
					}else{
						fakeGiftTarget = norepoList.get(0);
						fakeResult = Species.HUMAN;
						FHUMANAgent.add(fakeGiftTarget);
						norepoList.remove(fakeGiftTarget);
						fakeJudgeList.add(new Judge(getDay(), getMe(), fakeGiftTarget, fakeResult));
						return;
						
					}
				}
			}
		else if(fakeRole == Role.MEDIUM){
			
			//fakeGiftTarget = getLatestDayGameInfo().getExecutedAgent();
			int snum = SeerList.indexOf(maybeFSEERAgent);
			
			
			for(int i = 0;i<DEADAgentList.size();i++){
				if(wolfcount2 == 2 && !repoList.contains(DEADAgentList.get(i))){
					fakeGiftTarget = DEADAgentList.get(i);
					fakeResult = Species.HUMAN;
					repoList.add(DEADAgentList.get(i));
					fakeJudgeList.add(new Judge(getDay(), getMe(), fakeGiftTarget, fakeResult));
					return;
				}	
				
				
			if(WHITEList.contains(DEADAgentList.get(i)) && !repoList.contains(DEADAgentList.get(i))){
				fakeGiftTarget = DEADAgentList.get(i);
				fakeResult = Species.HUMAN;
				repoList.add(DEADAgentList.get(i));
				fakeJudgeList.add(new Judge(getDay(), getMe(), fakeGiftTarget, fakeResult));
				
				
			}else{
				if(DEADAgentList.get(i) == maybeMEDAgent  && !repoList.contains(DEADAgentList.get(i))){
					fakeGiftTarget = DEADAgentList.get(i);
					fakeResult = Species.WEREWOLF;
					repoList.add(DEADAgentList.get(i));
					wolfcount2++;
					fakeJudgeList.add(new Judge(getDay(), getMe(), fakeGiftTarget, fakeResult));
				}
				
				if(DEADAgentList.get(i) == maybeSEERAgent  && !repoList.contains(DEADAgentList.get(i))){
					fakeGiftTarget = DEADAgentList.get(i);
					fakeResult = Species.WEREWOLF;
					repoList.add(DEADAgentList.get(i));
					wolfcount2++;
					fakeJudgeList.add(new Judge(getDay(), getMe(), fakeGiftTarget, fakeResult));
				}
				
				if(snum == 0){
					if(divwolfList0.contains(DEADAgentList.get(i))  && !repoList.contains(DEADAgentList.get(i))){
						fakeGiftTarget = DEADAgentList.get(i);
						fakeResult = Species.WEREWOLF;
						repoList.add(DEADAgentList.get(i));
						wolfcount2++;
						fakeJudgeList.add(new Judge(getDay(), getMe(), fakeGiftTarget, fakeResult));
						
					}else if(!divwolfList0.contains(DEADAgentList.get(i))  && !repoList.contains(DEADAgentList.get(i))){
						Random rnd = new Random();
						int ran = rnd.nextInt(3);
						if(ran == 0 || ran == 1){
						fakeGiftTarget = DEADAgentList.get(i);
						fakeResult = Species.HUMAN;
						repoList.add(DEADAgentList.get(i));
						fakeJudgeList.add(new Judge(getDay(), getMe(), fakeGiftTarget, fakeResult));
						}else{
							fakeGiftTarget = DEADAgentList.get(i);
							fakeResult = Species.WEREWOLF;
							repoList.add(DEADAgentList.get(i));
							wolfcount2++;
							fakeJudgeList.add(new Judge(getDay(), getMe(), fakeGiftTarget, fakeResult));
						}
					}
				}
				if(snum == 1){
					if(divwolfList1.contains(DEADAgentList.get(i))  && !repoList.contains(DEADAgentList.get(i))){
						fakeGiftTarget = DEADAgentList.get(i);
						fakeResult = Species.WEREWOLF;
						repoList.add(DEADAgentList.get(i));
						wolfcount2++;
						fakeJudgeList.add(new Judge(getDay(), getMe(), fakeGiftTarget, fakeResult));
						
					}else if(!divwolfList1.contains(DEADAgentList.get(i))  && !repoList.contains(DEADAgentList.get(i))){
						Random rnd = new Random();
						int ran = rnd.nextInt(3);
						if(ran == 0 || ran == 1){
						fakeGiftTarget = DEADAgentList.get(i);
						fakeResult = Species.HUMAN;
						repoList.add(DEADAgentList.get(i));
						fakeJudgeList.add(new Judge(getDay(), getMe(), fakeGiftTarget, fakeResult));
						}else{
							fakeGiftTarget = DEADAgentList.get(i);
							fakeResult = Species.WEREWOLF;
							repoList.add(DEADAgentList.get(i));
							wolfcount2++;
							fakeJudgeList.add(new Judge(getDay(), getMe(), fakeGiftTarget, fakeResult));
						}
					}
				}
				if(snum == 2){
					if(divwolfList2.contains(DEADAgentList.get(i))  && !repoList.contains(DEADAgentList.get(i))){
						fakeGiftTarget = DEADAgentList.get(i);
						fakeResult = Species.WEREWOLF;
						repoList.add(DEADAgentList.get(i));
						wolfcount2++;
						fakeJudgeList.add(new Judge(getDay(), getMe(), fakeGiftTarget, fakeResult));
						
					}else if(!divwolfList2.contains(DEADAgentList.get(i))  && !repoList.contains(DEADAgentList.get(i))){
						Random rnd = new Random();
						int ran = rnd.nextInt(3);
						if(ran == 0 || ran == 1){
						fakeGiftTarget = DEADAgentList.get(i);
						fakeResult = Species.HUMAN;
						repoList.add(DEADAgentList.get(i));
						fakeJudgeList.add(new Judge(getDay(), getMe(), fakeGiftTarget, fakeResult));
						}else{
							fakeGiftTarget = DEADAgentList.get(i);
							fakeResult = Species.WEREWOLF;
							repoList.add(DEADAgentList.get(i));
							wolfcount2++;
							fakeJudgeList.add(new Judge(getDay(), getMe(), fakeGiftTarget, fakeResult));
						}
					}
				}
				
				if(snum == 3){
					if(divwolfList3.contains(DEADAgentList.get(i))  && !repoList.contains(DEADAgentList.get(i))){
						fakeGiftTarget = DEADAgentList.get(i);
						fakeResult = Species.WEREWOLF;
						repoList.add(DEADAgentList.get(i));
						wolfcount2++;
						fakeJudgeList.add(new Judge(getDay(), getMe(), fakeGiftTarget, fakeResult));
						
					}else if(!divwolfList3.contains(DEADAgentList.get(i))  && !repoList.contains(DEADAgentList.get(i))){
						Random rnd = new Random();
						int ran = rnd.nextInt(3);
						if(ran == 0 || ran == 1){
						fakeGiftTarget = DEADAgentList.get(i);
						fakeResult = Species.HUMAN;
						repoList.add(DEADAgentList.get(i));
						fakeJudgeList.add(new Judge(getDay(), getMe(), fakeGiftTarget, fakeResult));
						}else{
							fakeGiftTarget = DEADAgentList.get(i);
							fakeResult = Species.WEREWOLF;
							repoList.add(DEADAgentList.get(i));
							wolfcount2++;
							fakeJudgeList.add(new Judge(getDay(), getMe(), fakeGiftTarget, fakeResult));
						}
					}
				}
				
				if(snum == 4){
					if(divwolfList4.contains(DEADAgentList.get(i))  && !repoList.contains(DEADAgentList.get(i))){
						fakeGiftTarget = DEADAgentList.get(i);
						fakeResult = Species.WEREWOLF;
						repoList.add(DEADAgentList.get(i));
						wolfcount2++;
						fakeJudgeList.add(new Judge(getDay(), getMe(), fakeGiftTarget, fakeResult));
						
					}else if(!divwolfList4.contains(DEADAgentList.get(i))  && !repoList.contains(DEADAgentList.get(i))){
						Random rnd = new Random();
						int ran = rnd.nextInt(3);
						if(ran == 0 || ran == 1){
						fakeGiftTarget = DEADAgentList.get(i);
						fakeResult = Species.HUMAN;
						repoList.add(DEADAgentList.get(i));
						fakeJudgeList.add(new Judge(getDay(), getMe(), fakeGiftTarget, fakeResult));
						}else{
							fakeGiftTarget = DEADAgentList.get(i);
							fakeResult = Species.WEREWOLF;
							repoList.add(DEADAgentList.get(i));
							wolfcount2++;
							fakeJudgeList.add(new Judge(getDay(), getMe(), fakeGiftTarget, fakeResult));
						}
					}
				}
				
			}
			
			}
			return;
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
