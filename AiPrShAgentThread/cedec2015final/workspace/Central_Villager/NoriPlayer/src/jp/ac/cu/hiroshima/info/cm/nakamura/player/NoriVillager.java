package jp.ac.cu.hiroshima.info.cm.nakamura.player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import jp.ac.cu.hiroshima.info.cm.nakamura.base.player.NoriAbstractVillager;

import org.aiwolf.client.lib.TemplateTalkFactory;
import org.aiwolf.client.lib.Utterance;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Judge;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Species;
import org.aiwolf.common.data.Talk;
import org.aiwolf.common.net.GameInfo;
import org.aiwolf.common.net.GameSetting;


public class NoriVillager extends NoriAbstractVillager{
	/*
	 * 投票アルゴリズム：人狼だと占われたエージェント，いなければ，ランダム
	 * 発話：その日に投票しようとしているエージェントを報告．変化すれば報告．
	 */

	AdvanceGameInfo agi = new AdvanceGameInfo();

	//今日投票しようと思っているプレイヤー
	Agent planningVoteAgent;

	//自分が最後に宣言した「投票しようと思っているプレイヤー」
	Agent declaredPlanningVoteAgent;
	Agent declaredseerAgent;
	List<Agent> declaredblackAgent = new ArrayList<Agent>();

	//会話をどこまで読んだか
	int readTalkListNum;


	//占い師だと思っているエージェント
	Agent maybeSEER;
	//霊能者だと思っているエージェント
	Agent maybeMED;
	//白であるエージェントのリスト
	List<Agent> WhiteList = new ArrayList<Agent>();
	//黒だと確定したエージェント
	List<Agent> BlackList = new ArrayList<Agent>();

	//一人を占い師と仮定した時のブラックリスト
	List<Agent> maybeBlackList0 = new ArrayList<Agent>();
	List<Agent> maybeBlackList1 = new ArrayList<Agent>();
	List<Agent> maybeBlackList2 = new ArrayList<Agent>();
	List<Agent> maybeBlackList3 = new ArrayList<Agent>();
	List<Agent> maybeBlackList4 = new ArrayList<Agent>();


	
	
	
	
	@Override
	public void initialize(GameInfo gameInfo, GameSetting gameSetting) {
		super.initialize(gameInfo, gameSetting);




	}

	@Override
	public void dayStart() {
		List<Agent> AgentList = getLatestDayGameInfo().getAgentList();
		AgentList.remove(getMe());

		declaredPlanningVoteAgent = null;
		planningVoteAgent = null;
		setAgent();
		AbstractRole();
		BlackListadd();
		readTalkListNum =0;
}





	//変数への代入
	public void setAgent(){

		List<Agent> AgentList = getLatestDayGameInfo().getAgentList();
		AgentList.remove(getMe());




		int Deadnum1 = AgentList.indexOf(getLatestDayGameInfo().getAttackedAgent());
		int Deadnum2 = AgentList.indexOf(getLatestDayGameInfo().getExecutedAgent());
		//襲撃されたエージェント
		if(Deadnum1 != -1){
		psymodel[Deadnum1][5] = 0.0;
		seermodel0[Deadnum1][5] = 0.0;
		seermodel1[Deadnum1][5] = 0.0;
		seermodel2[Deadnum1][5] = 0.0;
		seermodel3[Deadnum1][5] = 0.0;
		seermodel4[Deadnum1][5] = 0.0;


		if(seermodel0[Deadnum1][4] == 1.0){
			if(!BlackList.contains(seerAgentList.get(0))){
			BlackList.add(seerAgentList.get(0));
			}
		}else if(maybeBlackList0.contains(AgentList.get(Deadnum1))){
			seermodel0[Deadnum1][0] = 0.0;
			seermodel0[Deadnum1][1] = 0.0;
			seermodel0[Deadnum1][2] = 0.0;
			seermodel0[Deadnum1][3] = 1.0;
			seermodel0[Deadnum1][4] = 0.0;
		}else if(seermodel0[Deadnum1][6] == 2.0){
			seermodel0[Deadnum1][0] = 0.0;
			seermodel0[Deadnum1][1] = 0.0;
			seermodel0[Deadnum1][2] = 0.5;
			seermodel0[Deadnum1][3] = 0.5;
			seermodel0[Deadnum1][4] = 0.0;
		}

		if(seermodel1[Deadnum1][4] == 1.0){
			if(!BlackList.contains(seerAgentList.get(1))){
			BlackList.add(seerAgentList.get(1));
			}
		}else if(maybeBlackList1.contains(AgentList.get(Deadnum1))){
			seermodel1[Deadnum1][0] = 0.0;
			seermodel1[Deadnum1][1] = 0.0;
			seermodel1[Deadnum1][2] = 0.0;
			seermodel1[Deadnum1][3] = 1.0;
			seermodel1[Deadnum1][4] = 0.0;
		}else if(seermodel1[Deadnum1][6] == 2.0){
			seermodel1[Deadnum1][0] = 0.0;
			seermodel1[Deadnum1][1] = 0.0;
			seermodel1[Deadnum1][2] = 0.5;
			seermodel1[Deadnum1][3] = 0.5;
			seermodel1[Deadnum1][4] = 0.0;
		}

		 if(seermodel2[Deadnum1][4] == 1.0){
				if(!BlackList.contains(seerAgentList.get(2))){
				BlackList.add(seerAgentList.get(2));
				}
			}
		 else if(maybeBlackList2.contains(AgentList.get(Deadnum1))){
			seermodel2[Deadnum1][0] = 0.0;
			seermodel2[Deadnum1][1] = 0.0;
			seermodel2[Deadnum1][2] = 0.0;
			seermodel2[Deadnum1][3] = 1.0;
			seermodel2[Deadnum1][4] = 0.0;
		}else if(seermodel2[Deadnum1][6] == 2.0){
			seermodel2[Deadnum1][0] = 0.0;
			seermodel2[Deadnum1][1] = 0.0;
			seermodel2[Deadnum1][2] = 0.5;
			seermodel2[Deadnum1][3] = 0.5;
			seermodel2[Deadnum1][4] = 0.0;
		}

		if(seermodel3[Deadnum1][4] == 1.0){
			if(!BlackList.contains(seerAgentList.get(3))){
			BlackList.add(seerAgentList.get(3));
			}
		}else if(maybeBlackList3.contains(AgentList.get(Deadnum1))){
			seermodel3[Deadnum1][0] = 0.0;
			seermodel3[Deadnum1][1] = 0.0;
			seermodel3[Deadnum1][2] = 0.0;
			seermodel3[Deadnum1][3] = 1.0;
			seermodel3[Deadnum1][4] = 0.0;
		}else if(seermodel3[Deadnum1][6] == 2.0){
			seermodel3[Deadnum1][0] = 0.0;
			seermodel3[Deadnum1][1] = 0.0;
			seermodel3[Deadnum1][2] = 0.5;
			seermodel3[Deadnum1][3] = 0.5;
			seermodel3[Deadnum1][4] = 0.0;
		}

		if(seermodel4[Deadnum1][4] == 1.0){
			if(!BlackList.contains(seerAgentList.get(4))){
			BlackList.add(seerAgentList.get(4));
			}
		}else if(maybeBlackList4.contains(AgentList.get(Deadnum1))){
			seermodel4[Deadnum1][0] = 0.0;
			seermodel4[Deadnum1][1] = 0.0;
			seermodel4[Deadnum1][2] = 0.0;
			seermodel4[Deadnum1][3] = 1.0;
			seermodel4[Deadnum1][4] = 0.0;
		}else if(seermodel4[Deadnum1][6] == 2.0){
			seermodel4[Deadnum1][0] = 0.0;
			seermodel4[Deadnum1][1] = 0.0;
			seermodel4[Deadnum1][2] = 0.5;
			seermodel4[Deadnum1][3] = 0.5;
			seermodel4[Deadnum1][4] = 0.0;
		} if(seermodel4[Deadnum1][4] == 1.0){
			if(!BlackList.contains(seerAgentList.get(4))){
			BlackList.add(seerAgentList.get(4));
			}
		}


		if(!WhiteList.contains(AgentList.get(Deadnum1))){
		WhiteList.add(AgentList.get(Deadnum1));
		}
		}

		//投票により吊られたエージェント
		if(Deadnum2 != -1){
		psymodel[Deadnum2][5] = 0.0;
		seermodel0[Deadnum2][5] = 0.0;
		seermodel1[Deadnum2][5] = 0.0;
		seermodel2[Deadnum2][5] = 0.0;
		seermodel3[Deadnum2][5] = 0.0;
		seermodel4[Deadnum2][5] = 0.0;

		}

		for(int m = 0; m<BlackList.size();m++){
			int bnum = AgentList.indexOf(BlackList.get(m));
			if(bnum != -1){
				psymodel[bnum][0] = 0.0;
				psymodel[bnum][1] = 0.0;
				psymodel[bnum][2] = 0.0;
				psymodel[bnum][3] = 0.3;
				psymodel[bnum][4] = 0.7;
			}
		}

		double k = 0.0;
		double k1 = 0.0;
		for(int i = 0;i < psymodel.length;i++){
			if(psymodel[i][1] > k && !BlackList.contains(AgentList.get(i))){
				maybeSEER = AgentList.get(i);
				k = psymodel[i][1];
			}
			if(psymodel[i][2] > k1 && !BlackList.contains(AgentList.get(i))){
				maybeMED = AgentList.get(i);
				k1 = psymodel[i][2];
			}
		}



	}


	//ブラックリストへの追加
	public void BlackListadd(){
		List<Agent> AgentList = getLatestDayGameInfo().getAgentList();
		AgentList.remove(getMe());

		List<Agent> aliveAgentList = getLatestDayGameInfo().getAliveAgentList();
		aliveAgentList.remove(getMe());


		if(Cn >= 1){
			for(int i0 = 0;i0< seermodel0.length;i0++){
				if(seermodel0[i0][4] == 1.0 && WhiteList.contains(AgentList.get(i0)) && !BlackList.contains(seerAgentList.get(0))){
				BlackList.add(seerAgentList.get(0));

				}
				if((seermodel0[i0][0] + seermodel0[i0][1] + seermodel0[i0][2]) < (seermodel0[i0][3]+seermodel0[i0][4]) && !maybeBlackList0.contains(AgentList.get(i0))){
					maybeBlackList0.add(AgentList.get(i0));
				}
			}

			if(maybeBlackList0.size() > 4 && !BlackList.contains(seerAgentList.get(0))){
				BlackList.add(seerAgentList.get(0));
			}

			int c0 = 0;
			for(int j0 = 0;j0 < maybeBlackList0.size();j0++){
				if(!aliveAgentList.contains(maybeBlackList0.get(j0))){
					c0++;
				}
			}

			if(maybeBlackList0.size() == 4  && c0 == 4 &&  !BlackList.contains(seerAgentList.get(0))){
				BlackList.add(seerAgentList.get(0));
			}

			int d0 = 0;
			for(int k0 = 0;k0 < seermodel0.length;k0++){
				if(seermodel0[k0][3] == 1.0){
					d0++;
				}
			}

			if(d0 >= 2 && !BlackList.contains(seerAgentList.get(0))){
				BlackList.add(seerAgentList.get(0));
			}

			}


			if(Cn >= 2){
			for(int i1 = 0;i1< seermodel1.length;i1++){
				if(seermodel1[i1][4] == 1.0 && WhiteList.contains(AgentList.get(i1)) && !BlackList.contains(seerAgentList.get(1))){
				BlackList.add(seerAgentList.get(1));

				}
				if((seermodel1[i1][0] + seermodel1[i1][1] + seermodel1[i1][2]) < (seermodel1[i1][3]+seermodel1[i1][4]) && !maybeBlackList1.contains(AgentList.get(i1))){
					maybeBlackList1.add(AgentList.get(i1));
				}
			}


			if(maybeBlackList1.size() > 4 && !BlackList.contains(seerAgentList.get(1))){
				BlackList.add(seerAgentList.get(1));
			}

			int c1 = 0;
			for(int j1 = 0;j1 < maybeBlackList1.size();j1++){
				if(!aliveAgentList.contains(maybeBlackList1.get(j1))){
					c1++;
				}
			}

			if(maybeBlackList1.size() == 4  && c1 == 4 &&  !BlackList.contains(seerAgentList.get(1))){
				BlackList.add(seerAgentList.get(1));
			}

			int d1 = 0;
			for(int k1 = 0;k1 < seermodel1.length;k1++){
				if(seermodel1[k1][3] == 1.0){
					d1++;
				}
			}

			if(d1 >= 2 && !BlackList.contains(seerAgentList.get(1))){
				BlackList.add(seerAgentList.get(1));
			}

			}


			if(Cn >= 3){
			for(int i2 = 0;i2< seermodel2.length;i2++){
				if(seermodel2[i2][4] == 1.0 && WhiteList.contains(AgentList.get(i2)) && !BlackList.contains(seerAgentList.get(2))){
				BlackList.add(seerAgentList.get(2));

				}
				if((seermodel2[i2][0] + seermodel2[i2][1] + seermodel2[i2][2]) < (seermodel2[i2][3]+seermodel2[i2][4]) && !maybeBlackList2.contains(AgentList.get(i2))){
					maybeBlackList2.add(AgentList.get(i2));
				}
			}
			if(maybeBlackList2.size() > 4 && !BlackList.contains(seerAgentList.get(2))){
				BlackList.add(seerAgentList.get(2));
			}

			int c2 = 0;
			for(int j2 = 0;j2 < maybeBlackList2.size();j2++){
				if(!aliveAgentList.contains(maybeBlackList2.get(j2))){
					c2++;
				}
			}

			if(maybeBlackList2.size() == 4  && c2 == 4 &&  !BlackList.contains(seerAgentList.get(2))){
				BlackList.add(seerAgentList.get(2));
			}

			int d2 = 0;
			for(int k2= 0;k2 < seermodel2.length;k2++){
				if(seermodel2[k2][3] == 1.0){
					d2++;
				}
			}

			if(d2 >= 2 && !BlackList.contains(seerAgentList.get(2))){
				BlackList.add(seerAgentList.get(2));
			}


			}





			if(Cn >= 4){
			for(int i3 = 0;i3< seermodel3.length;i3++){
				if(seermodel3[i3][4] == 1.0 && WhiteList.contains(AgentList.get(i3)) && !BlackList.contains(seerAgentList.get(3))){
				BlackList.add(seerAgentList.get(3));

				}
				if((seermodel3[i3][0] + seermodel3[i3][1] + seermodel3[i3][2]) < (seermodel3[i3][3]+seermodel3[i3][4]) && !maybeBlackList3.contains(AgentList.get(i3))){
					maybeBlackList3.add(AgentList.get(i3));
				}
			}
			if(maybeBlackList3.size() > 4 && !BlackList.contains(seerAgentList.get(3))){
				BlackList.add(seerAgentList.get(3));
			}

			int c3 = 0;
			for(int j3 = 0;j3 < maybeBlackList3.size();j3++){
				if(!aliveAgentList.contains(maybeBlackList3.get(j3))){
					c3++;
				}
			}

			if(maybeBlackList3.size() == 4  && c3 == 4 &&  !BlackList.contains(seerAgentList.get(3))){
				BlackList.add(seerAgentList.get(3));
			}

			int d3 = 0;
			for(int k3 = 0;k3 < seermodel3.length;k3++){
				if(seermodel3[k3][3] == 1.0){
					d3++;
				}
			}

			if(d3 >= 2 && !BlackList.contains(seerAgentList.get(3))){
				BlackList.add(seerAgentList.get(3));
			}


			}


			if(Cn >= 5){
			for(int i4 = 0;i4< seermodel4.length;i4++){
				if(seermodel4[i4][4] == 1.0 && WhiteList.contains(AgentList.get(i4)) && !BlackList.contains(seerAgentList.get(4))){
				BlackList.add(seerAgentList.get(4));

				}
				if((seermodel4[i4][0] + seermodel4[i4][1] + seermodel4[i4][2]) < (seermodel4[i4][3]+seermodel4[i4][4]) && !maybeBlackList4.contains(AgentList.get(i4))){
					maybeBlackList4.add(AgentList.get(i4));
				}
			}
			if(maybeBlackList4.size() > 4 && !BlackList.contains(seerAgentList.get(4))){
				BlackList.add(seerAgentList.get(4));
			}


			int c4 = 0;
			for(int j4 = 0;j4 < maybeBlackList4.size();j4++){
				if(!aliveAgentList.contains(maybeBlackList4.get(j4))){
					c4++;
				}
			}

			if(maybeBlackList4.size() == 4  && c4 == 4 &&  !BlackList.contains(seerAgentList.get(4))){
				BlackList.add(seerAgentList.get(4));
			}


			int d4 = 0;
			for(int k4 = 0;k4 < seermodel4.length;k4++){
				if(seermodel4[k4][3] == 1.0){
					d4++;
				}
			}

			if(d4 >= 2 && !BlackList.contains(seerAgentList.get(4))){
				BlackList.add(seerAgentList.get(4));
			}

			}


	}








	//投票するエージェントの選択

	public void AbstractRole(){
		List<Agent> aliveAgentList = getLatestDayGameInfo().getAgentList();
		aliveAgentList.remove(getMe());

		List<Agent> aliveAgentList2 = getLatestDayGameInfo().getAliveAgentList();
		aliveAgentList2.remove(getMe());

		//planningVoteAgent = aliveAgentList2.get(0);


		//ブラックリストにエージェントがいる場合
		if(BlackList.size() != 0){
			for(int i = 0;i<BlackList.size();i++){
				if(aliveAgentList2.contains(BlackList.get(i))){
			planningVoteAgent = BlackList.get(i);
				break;
				}

			}
		}




		//３日目までの投票
		if(getDay() < 3){
			while(planningVoteAgent == null){
			Random rnd = new Random();
			int ran = rnd.nextInt(aliveAgentList2.size());
			if(!seerAgentList.contains(aliveAgentList2.get(ran))){
			planningVoteAgent = aliveAgentList2.get(ran);
			}
			}
		}
		//３日目以上の投票
		//maybeSEERのmaybeBlackListの中の生きているエージェントに投票
		if(getDay() >= 3){
			int snum = seerAgentList.indexOf(maybeSEER);
			if(snum != -1){
				if(snum == 0){
					for(int i = 0;i < maybeBlackList0.size();i++){
						if(aliveAgentList2.contains(maybeBlackList0.get(i))){
							planningVoteAgent = maybeBlackList0.get(i);
						}
					}
				}

				if(snum == 1){
					for(int i = 0;i < maybeBlackList1.size();i++){
						if(aliveAgentList2.contains(maybeBlackList1.get(i))){
							planningVoteAgent = maybeBlackList1.get(i);
						}
					}
				}
				if(snum == 2){
					for(int i = 0;i < maybeBlackList2.size();i++){
						if(aliveAgentList2.contains(maybeBlackList2.get(i))){
							planningVoteAgent = maybeBlackList2.get(i);
						}
					}
				}
				if(snum == 3){
					for(int i = 0;i < maybeBlackList3.size();i++){
						if(aliveAgentList2.contains(maybeBlackList3.get(i))){
							planningVoteAgent = maybeBlackList3.get(i);
						}
					}
				}

				if(snum == 4){
					for(int i = 0;i < maybeBlackList4.size();i++){
						if(aliveAgentList2.contains(maybeBlackList4.get(i))){
							planningVoteAgent = maybeBlackList4.get(i);
						}
					}
				}

			}

		}

		//投票しようとしているエージェントがいない場合
		if(planningVoteAgent == null){
		double k = psymodel[0][4] - (psymodel[0][1]+psymodel[0][3]);

		for(int i = 1 ;i<psymodel.length;i++){
			if(psymodel[i][5] == 1.0 && aliveAgentList.get(i) != maybeSEER && aliveAgentList.get(i) != maybeMED){
			if(k <= psymodel[i][4]-(psymodel[i][1]+psymodel[i][3])){
				planningVoteAgent = aliveAgentList.get(i);
				k = psymodel[i][4]-(psymodel[i][1]+psymodel[i][3]);


			}
			}
			}
		}

		//７日目以降の投票

		if(getDay() >= 7){
			if(aliveAgentList2.contains(maybeSEER)){
				planningVoteAgent = maybeSEER;
			}else if(aliveAgentList2.contains(maybeMED)){
				planningVoteAgent = maybeMED;
			}
		}

	}



	//会話の読み取り・心理モデルの更新
	List<Agent> seerAgentList = new ArrayList<Agent>();
	List<Agent> medAgentList = new ArrayList<Agent>();
	public int Cn = 0;
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

				//占い師COの場合
				if(utterance.getRole() == Role.SEER){
				int COnum1 = AgentList.indexOf(talk.getAgent());
				if(COnum1 != -1){
				psymodel[COnum1][0] = 0.0;
				psymodel[COnum1][1] = 0.5;
				psymodel[COnum1][2] = 0.0;
				psymodel[COnum1][3] = 0.2;
				psymodel[COnum1][4] = 0.3;
				psymodel[COnum1][6] = 1.0;
				}
				Cn++;
				seerAgentList.add(talk.getAgent());
				seermodel(talk.getAgent(),Cn ,seerAgentList,medAgentList);



				if(Cn == 2){
					seermodel0[COnum1][0] = 0.0;
					seermodel0[COnum1][1] = 0.0;
					seermodel0[COnum1][2] = 0.0;
					seermodel0[COnum1][3] = 0.5;
					seermodel0[COnum1][4] = 0.5;
					seermodel0[COnum1][6] = 1.0;
				}

				if(Cn == 3){
					seermodel0[COnum1][0] = 0.0;
					seermodel0[COnum1][1] = 0.0;
					seermodel0[COnum1][2] = 0.0;
					seermodel0[COnum1][3] = 0.5;
					seermodel0[COnum1][4] = 0.5;
					seermodel0[COnum1][6] = 1.0;

					seermodel1[COnum1][0] = 0.0;
					seermodel1[COnum1][1] = 0.0;
					seermodel1[COnum1][2] = 0.0;
					seermodel1[COnum1][3] = 0.5;
					seermodel1[COnum1][4] = 0.5;
					seermodel1[COnum1][6] = 1.0;
				}

				if(Cn == 4){
					seermodel0[COnum1][0] = 0.0;
					seermodel0[COnum1][1] = 0.0;
					seermodel0[COnum1][2] = 0.0;
					seermodel0[COnum1][3] = 0.5;
					seermodel0[COnum1][4] = 0.5;
					seermodel0[COnum1][6] = 1.0;

					seermodel1[COnum1][0] = 0.0;
					seermodel1[COnum1][1] = 0.0;
					seermodel1[COnum1][2] = 0.0;
					seermodel1[COnum1][3] = 0.5;
					seermodel1[COnum1][4] = 0.5;
					seermodel1[COnum1][6] = 1.0;

					seermodel2[COnum1][0] = 0.0;
					seermodel2[COnum1][1] = 0.0;
					seermodel2[COnum1][2] = 0.0;
					seermodel2[COnum1][3] = 0.5;
					seermodel2[COnum1][4] = 0.5;
					seermodel2[COnum1][6] = 1.0;
				}

				if(Cn == 5){
					seermodel0[COnum1][0] = 0.0;
					seermodel0[COnum1][1] = 0.0;
					seermodel0[COnum1][2] = 0.0;
					seermodel0[COnum1][3] = 0.5;
					seermodel0[COnum1][4] = 0.5;
					seermodel0[COnum1][6] = 1.0;

					seermodel1[COnum1][0] = 0.0;
					seermodel1[COnum1][1] = 0.0;
					seermodel1[COnum1][2] = 0.0;
					seermodel1[COnum1][3] = 0.5;
					seermodel1[COnum1][4] = 0.5;
					seermodel1[COnum1][6] = 1.0;

					seermodel2[COnum1][0] = 0.0;
					seermodel2[COnum1][1] = 0.0;
					seermodel2[COnum1][2] = 0.0;
					seermodel2[COnum1][3] = 0.5;
					seermodel2[COnum1][4] = 0.5;
					seermodel2[COnum1][6] = 1.0;

					seermodel3[COnum1][0] = 0.0;
					seermodel3[COnum1][1] = 0.0;
					seermodel3[COnum1][2] = 0.0;
					seermodel3[COnum1][3] = 0.5;
					seermodel3[COnum1][4] = 0.5;
					seermodel3[COnum1][6] = 1.0;
				}

				}


				//霊能者COの場合
				if(utterance.getRole() == Role.MEDIUM){
					int COnum2 = AgentList.indexOf(talk.getAgent());
					if(COnum2 != -1){
					psymodel[COnum2][0] = 0.0;
					psymodel[COnum2][1] = 0.0;
					psymodel[COnum2][2] = 0.5;
					psymodel[COnum2][3] = 0.2;
					psymodel[COnum2][4] = 0.3;
					psymodel[COnum2][6] = 2.0;

					medAgentList.add(talk.getAgent());

					if(seermodel0[COnum2][4] != 1.0){
					seermodel0[COnum2][0] = 0.0;
					seermodel0[COnum2][1] = 0.0;
					seermodel0[COnum2][2] = 0.5;
					seermodel0[COnum2][3] = 0.2;
					seermodel0[COnum2][4] = 0.3;
					seermodel0[COnum2][6] = 2.0;
					}
					if(seermodel1[COnum2][4] != 1.0){
					seermodel1[COnum2][0] = 0.0;
					seermodel1[COnum2][1] = 0.0;
					seermodel1[COnum2][2] = 0.5;
					seermodel1[COnum2][3] = 0.2;
					seermodel1[COnum2][4] = 0.3;
					seermodel1[COnum2][6] = 2.0;
					}
					if(seermodel2[COnum2][4] != 1.0){
					seermodel2[COnum2][0] = 0.0;
					seermodel2[COnum2][1] = 0.0;
					seermodel2[COnum2][2] = 0.5;
					seermodel2[COnum2][3] = 0.2;
					seermodel2[COnum2][4] = 0.3;
					seermodel2[COnum2][6] = 2.0;
					}
					if(seermodel3[COnum2][4] != 1.0){
					seermodel3[COnum2][0] = 0.0;
					seermodel3[COnum2][1] = 0.0;
					seermodel3[COnum2][2] = 0.5;
					seermodel3[COnum2][3] = 0.2;
					seermodel3[COnum2][4] = 0.3;
					seermodel3[COnum2][6] = 2.0;
					}
					if(seermodel4[COnum2][4] != 1.0){
					seermodel4[COnum2][0] = 0.0;
					seermodel4[COnum2][1] = 0.0;
					seermodel4[COnum2][2] = 0.5;
					seermodel4[COnum2][3] = 0.2;
					seermodel4[COnum2][4] = 0.3;
					seermodel4[COnum2][6] = 2.0;
					}
					}
					}

				//人狼COの場合
				if(utterance.getRole() == Role.WEREWOLF){
					int COnum3 = AgentList.indexOf(talk.getAgent());
					if(COnum3 != -1){
					psymodel[COnum3][0] = 0.0;
					psymodel[COnum3][1] = 0.0;
					psymodel[COnum3][2] = 0.0;
					psymodel[COnum3][3] = 0.1;
					psymodel[COnum3][4] = 0.9;
					psymodel[COnum3][6] = 3.0;

					}
					planningVoteAgent = talk.getAgent();
				}


				//break;

			//占い結果の発話の場合
			case DIVINED:
				//AGIのJudgeListに結果を加える
				Agent seerAgent = talk.getAgent();
				Agent inspectedAgent = utterance.getTarget();
				Species inspectResult = utterance.getResult();
				Judge judge = new Judge(getDay(), seerAgent, inspectedAgent, inspectResult);
				int seernum = seerAgentList.indexOf(seerAgent);
				agi.addInspectJudgeList(judge);



				//人狼だと占われたエージェントの心理モデル変更
				if(utterance.getResult() == Species.WEREWOLF){
				int DIVnum1 = AgentList.indexOf(inspectedAgent);
				if(DIVnum1 != -1){
					if(psymodel[DIVnum1][6] == 0.0){
					psymodel[DIVnum1][0] = 0.2;
					psymodel[DIVnum1][1] = 0.1;
					psymodel[DIVnum1][2] = 0.1;
					psymodel[DIVnum1][3] = 0.1;
					psymodel[DIVnum1][4] = 0.5;
					}
				if(psymodel[DIVnum1][6] == 1.0){
					psymodel[DIVnum1][0] = 0.0;
					psymodel[DIVnum1][1] = 0.3;
					psymodel[DIVnum1][2] = 0.0;
					psymodel[DIVnum1][3] = 0.2;
					psymodel[DIVnum1][4] = 0.5;
					}

				if(psymodel[DIVnum1][6] == 2.0){
					psymodel[DIVnum1][0] = 0.0;
					psymodel[DIVnum1][1] = 0.0;
					psymodel[DIVnum1][2] = 0.3;
					psymodel[DIVnum1][3] = 0.2;
					psymodel[DIVnum1][4] = 0.5;
					}

				}

				}

				//人間だと占われたエージェントの心理モデル変更
				if(utterance.getResult() == Species.HUMAN ){
					int DIVnum1 = AgentList.indexOf(inspectedAgent);
					if(DIVnum1 != -1){
						if(psymodel[DIVnum1][6] == 0.0){
						psymodel[DIVnum1][0] = 0.3;
						psymodel[DIVnum1][1] = 0.2;
						psymodel[DIVnum1][2] = 0.2;
						psymodel[DIVnum1][3] = 0.2;
						psymodel[DIVnum1][4] = 0.1;
						}
						if(psymodel[DIVnum1][6] == 1.0){
							psymodel[DIVnum1][0] = 0.0;
							psymodel[DIVnum1][1] = 0.5;
							psymodel[DIVnum1][2] = 0.0;
							psymodel[DIVnum1][3] = 0.3;
							psymodel[DIVnum1][4] = 0.2;
							}
						if(psymodel[DIVnum1][6] == 2.0){
							psymodel[DIVnum1][0] = 0.0;
							psymodel[DIVnum1][1] = 0.0;
							psymodel[DIVnum1][2] = 0.5;
							psymodel[DIVnum1][3] = 0.3;
							psymodel[DIVnum1][4] = 0.2;
							}
					}
					}


				//一人を占い師と仮定した時の心理モデルの変更
				if(seernum == 0 && BlackList.contains(seerAgentList.get(0)) == false){
					int DIVnum1 = AgentList.indexOf(inspectedAgent);
					if(DIVnum1 != -1){
					if(utterance.getResult() == Species.WEREWOLF){

							seermodel0[DIVnum1][0] = 0.0;
							seermodel0[DIVnum1][1] = 0.0;
							seermodel0[DIVnum1][2] = 0.0;
							seermodel0[DIVnum1][3] = 0.0;
							seermodel0[DIVnum1][4] = 1.0;

						}


					if(utterance.getResult() == Species.HUMAN){

							if(seermodel0[DIVnum1][6] == 0.0 ){
							seermodel0[DIVnum1][0] = 0.4;
							seermodel0[DIVnum1][1] = 0.0;
							seermodel0[DIVnum1][2] = 0.3;
							seermodel0[DIVnum1][3] = 0.3;
							seermodel0[DIVnum1][4] = 0.0;

						}

						else if(seermodel0[DIVnum1][6] == 1.0){

								seermodel0[DIVnum1][0] = 0.0;
								seermodel0[DIVnum1][1] = 0.0;
								seermodel0[DIVnum1][2] = 0.0;
								seermodel0[DIVnum1][3] = 1.0;
								seermodel0[DIVnum1][4] = 0.0;
							}

						else if(seermodel0[DIVnum1][6] == 2.0){

								seermodel0[DIVnum1][0] = 0.0;
								seermodel0[DIVnum1][1] = 0.0;
								seermodel0[DIVnum1][2] = 0.5;
								seermodel0[DIVnum1][3] = 0.5;
								seermodel0[DIVnum1][4] = 0.0;
							}
						}
					}
				}

				if(seernum == 1 && BlackList.contains(seerAgentList.get(1)) == false){
					int DIVnum1 = AgentList.indexOf(inspectedAgent);
					if(DIVnum1 != -1){
					if(utterance.getResult() == Species.WEREWOLF){

							seermodel1[DIVnum1][0] = 0.0;
							seermodel1[DIVnum1][1] = 0.0;
							seermodel1[DIVnum1][2] = 0.0;
							seermodel1[DIVnum1][3] = 0.0;
							seermodel1[DIVnum1][4] = 1.0;

						}


					if(utterance.getResult() == Species.HUMAN){

							if(seermodel1[DIVnum1][6] == 0.0 ){
							seermodel1[DIVnum1][0] = 0.4;
							seermodel1[DIVnum1][1] = 0.0;
							seermodel1[DIVnum1][2] = 0.3;
							seermodel1[DIVnum1][3] = 0.3;
							seermodel1[DIVnum1][4] = 0.0;

						}

						else if(seermodel1[DIVnum1][6] == 1.0){

								seermodel1[DIVnum1][0] = 0.0;
								seermodel1[DIVnum1][1] = 0.0;
								seermodel1[DIVnum1][2] = 0.0;
								seermodel1[DIVnum1][3] = 1.0;
								seermodel1[DIVnum1][4] = 0.0;
							}

						else if(seermodel1[DIVnum1][6] == 2.0){

								seermodel1[DIVnum1][0] = 0.0;
								seermodel1[DIVnum1][1] = 0.0;
								seermodel1[DIVnum1][2] = 0.5;
								seermodel1[DIVnum1][3] = 0.5;
								seermodel1[DIVnum1][4] = 0.0;
							}
						}
					}
				}

				if(seernum == 2 && BlackList.contains(seerAgentList.get(2)) == false){
					int DIVnum1 = AgentList.indexOf(inspectedAgent);
					if(DIVnum1 != -1){
					if(utterance.getResult() == Species.WEREWOLF){

							seermodel2[DIVnum1][0] = 0.0;
							seermodel2[DIVnum1][1] = 0.0;
							seermodel2[DIVnum1][2] = 0.0;
							seermodel2[DIVnum1][3] = 0.0;
							seermodel2[DIVnum1][4] = 1.0;

						}


					if(utterance.getResult() == Species.HUMAN){

							if(seermodel2[DIVnum1][6] == 0.0 ){
							seermodel2[DIVnum1][0] = 0.4;
							seermodel2[DIVnum1][1] = 0.0;
							seermodel2[DIVnum1][2] = 0.3;
							seermodel2[DIVnum1][3] = 0.3;
							seermodel2[DIVnum1][4] = 0.0;

						}

						else if(seermodel2[DIVnum1][6] == 1.0){

								seermodel2[DIVnum1][0] = 0.0;
								seermodel2[DIVnum1][1] = 0.0;
								seermodel2[DIVnum1][2] = 0.0;
								seermodel2[DIVnum1][3] = 1.0;
								seermodel2[DIVnum1][4] = 0.0;
							}

						else if(seermodel2[DIVnum1][6] == 2.0){

								seermodel2[DIVnum1][0] = 0.0;
								seermodel2[DIVnum1][1] = 0.0;
								seermodel2[DIVnum1][2] = 0.5;
								seermodel2[DIVnum1][3] = 0.5;
								seermodel2[DIVnum1][4] = 0.0;
							}
						}
					}
				}
				if(seernum == 3 && BlackList.contains(seerAgentList.get(3)) == false){
					int DIVnum1 = AgentList.indexOf(inspectedAgent);

					if(DIVnum1 != -1){
					if(utterance.getResult() == Species.WEREWOLF){

							seermodel3[DIVnum1][0] = 0.0;
							seermodel3[DIVnum1][1] = 0.0;
							seermodel3[DIVnum1][2] = 0.0;
							seermodel3[DIVnum1][3] = 0.0;
							seermodel3[DIVnum1][4] = 1.0;

						}


					if(utterance.getResult() == Species.HUMAN){

							if(seermodel3[DIVnum1][6] == 0.0 ){
							seermodel3[DIVnum1][0] = 0.4;
							seermodel3[DIVnum1][1] = 0.0;
							seermodel3[DIVnum1][2] = 0.3;
							seermodel3[DIVnum1][3] = 0.3;
							seermodel3[DIVnum1][4] = 0.0;

						}

						else if(seermodel3[DIVnum1][6] == 1.0){

								seermodel3[DIVnum1][0] = 0.0;
								seermodel3[DIVnum1][1] = 0.0;
								seermodel3[DIVnum1][2] = 0.0;
								seermodel3[DIVnum1][3] = 1.0;
								seermodel3[DIVnum1][4] = 0.0;
							}

						else if(seermodel3[DIVnum1][6] == 2.0){

								seermodel3[DIVnum1][0] = 0.0;
								seermodel3[DIVnum1][1] = 0.0;
								seermodel3[DIVnum1][2] = 0.5;
								seermodel3[DIVnum1][3] = 0.5;
								seermodel3[DIVnum1][4] = 0.0;
							}
						}
					}
				}
				if(seernum == 4 && BlackList.contains(seerAgentList.get(4)) == false){
					int DIVnum1 = AgentList.indexOf(inspectedAgent);
					if(DIVnum1 != -1){
					if(utterance.getResult() == Species.WEREWOLF){

							seermodel4[DIVnum1][0] = 0.0;
							seermodel4[DIVnum1][1] = 0.0;
							seermodel4[DIVnum1][2] = 0.0;
							seermodel4[DIVnum1][3] = 0.0;
							seermodel4[DIVnum1][4] = 1.0;

						}


					if(utterance.getResult() == Species.HUMAN){

							if(seermodel4[DIVnum1][6] == 0.0 ){
							seermodel4[DIVnum1][0] = 0.4;
							seermodel4[DIVnum1][1] = 0.0;
							seermodel4[DIVnum1][2] = 0.3;
							seermodel4[DIVnum1][3] = 0.3;
							seermodel4[DIVnum1][4] = 0.0;

						}

						else if(seermodel4[DIVnum1][6] == 1.0){

								seermodel4[DIVnum1][0] = 0.0;
								seermodel4[DIVnum1][1] = 0.0;
								seermodel4[DIVnum1][2] = 0.0;
								seermodel4[DIVnum1][3] = 1.0;
								seermodel4[DIVnum1][4] = 0.0;
							}

						else if(seermodel4[DIVnum1][6] == 2.0){

								seermodel4[DIVnum1][0] = 0.0;
								seermodel4[DIVnum1][1] = 0.0;
								seermodel4[DIVnum1][2] = 0.5;
								seermodel4[DIVnum1][3] = 0.5;
								seermodel4[DIVnum1][4] = 0.0;
							}
						}
					}
				}

				//自分を人狼だと占ったエージェントの心理モデル変更
				if(inspectedAgent == getMe() && utterance.getResult() == Species.WEREWOLF){
					int DIVnum2 = AgentList.indexOf(seerAgent);
					if(DIVnum2 != -1){
						psymodel[DIVnum2][0] = 0.0;
						psymodel[DIVnum2][1] = 0.0;
						psymodel[DIVnum2][2] = 0.0;
						psymodel[DIVnum2][3] = 0.3;
						psymodel[DIVnum2][4] = 0.7;
					}
					if(!BlackList.contains(seerAgent)){
					BlackList.add(seerAgent);
					}
					/*if(seernum == 0){
						for(int a = 0;a<seermodel0.length;a++){
							if(seermodel0[a][4] == 1.0){
								WhiteList.add(AgentList.get(a));
							}
						}
					}*/



				}
				//自分を人間だと占ったエージェントの心理モデル変更
				else if(inspectedAgent == getMe() && utterance.getResult() == Species.HUMAN){
					int DIVnum3 = AgentList.indexOf(seerAgent);
					if(DIVnum3 != -1){
						psymodel[DIVnum3][0] = 0.0;
						psymodel[DIVnum3][1] = 0.7;
						psymodel[DIVnum3][2] = 0.0;
						psymodel[DIVnum3][3] = 0.1;
						psymodel[DIVnum3][4] = 0.2;
					}
				}


				break;

				//霊能結果の場合
			case INQUESTED:

				Agent medAgent = talk.getAgent();
				Agent inspectedAgent2 = utterance.getTarget();
				Species inspectResult2 = utterance.getResult();
				Judge judge2 = new Judge(getDay(), medAgent, inspectedAgent2, inspectResult2);
				agi.addInspectJudgeList(judge2);
				int insnum = AgentList.indexOf(inspectedAgent2);
				int inqnum = AgentList.indexOf(medAgent);
				if(inqnum != -1){
					//霊能結果が人間でターゲットが白だった時
				if(inspectResult2 == Species.HUMAN && WhiteList.contains(inspectResult2)){

						psymodel[inqnum][0] = 0.0;
						psymodel[inqnum][1] = 0.0;
						psymodel[inqnum][2] = 0.6;
						psymodel[inqnum][3] = 0.2;
						psymodel[inqnum][4] = 0.2;

					}
				//霊能結果が人狼でターゲットが白だった場合
				if(inspectResult2 == Species.WEREWOLF && WhiteList.contains(inspectResult2)){
						psymodel[inqnum][0] = 0.0;
						psymodel[inqnum][1] = 0.0;
						psymodel[inqnum][2] = 0.0;
						psymodel[inqnum][3] = 0.3;
						psymodel[inqnum][4] = 0.7;
					if(!BlackList.contains(medAgent)){
					BlackList.add(medAgent);
					}
					}

				//一人を占い師と仮定した時の占い結果と霊能結果の矛盾探し
				if(insnum != -1){
				if(inspectResult2 == Species.WEREWOLF && seermodel0[insnum][4] == 0.0){

					if(seermodel0[inqnum][4] == 0.0){
						seermodel0[inqnum][0] = 0.0;
						seermodel0[inqnum][1] = 0.0;
						seermodel0[inqnum][2] = 0.0;
						seermodel0[inqnum][3] = 1.0;
						seermodel0[inqnum][4] = 0.0;
					}else if(seermodel0[inqnum][4] == 1.0){
						seermodel0[inqnum][0] = 0.0;
						seermodel0[inqnum][1] = 0.0;
						seermodel0[inqnum][2] = 0.0;
						seermodel0[inqnum][3] = 0.0;
						seermodel0[inqnum][4] = 1.0;
					}else{
						seermodel0[inqnum][0] = 0.0;
						seermodel0[inqnum][1] = 0.0;
						seermodel0[inqnum][2] = 0.0;
						seermodel0[inqnum][3] = 0.5;
						seermodel0[inqnum][4] = 0.5;
					}



				}
				if(inspectResult2 == Species.HUMAN && seermodel0[insnum][4] == 1.0){
					if(seermodel0[inqnum][4] == 0.0){
						seermodel0[inqnum][0] = 0.0;
						seermodel0[inqnum][1] = 0.0;
						seermodel0[inqnum][2] = 0.0;
						seermodel0[inqnum][3] = 1.0;
						seermodel0[inqnum][4] = 0.0;
					}else if(seermodel0[inqnum][4] == 1.0){
						seermodel0[inqnum][0] = 0.0;
						seermodel0[inqnum][1] = 0.0;
						seermodel0[inqnum][2] = 0.0;
						seermodel0[inqnum][3] = 0.0;
						seermodel0[inqnum][4] = 1.0;
					}else{
						seermodel0[inqnum][0] = 0.0;
						seermodel0[inqnum][1] = 0.0;
						seermodel0[inqnum][2] = 0.0;
						seermodel0[inqnum][3] = 0.5;
						seermodel0[inqnum][4] = 0.5;
					}



				}
				if(inspectResult2 == Species.WEREWOLF && seermodel1[insnum][4] == 0.0){
					if(seermodel1[inqnum][4] == 0.0){
						seermodel1[inqnum][0] = 0.0;
						seermodel1[inqnum][1] = 0.0;
						seermodel1[inqnum][2] = 0.0;
						seermodel1[inqnum][3] = 1.0;
						seermodel1[inqnum][4] = 0.0;
					}else if(seermodel1[inqnum][4] == 1.0){
						seermodel1[inqnum][0] = 0.0;
						seermodel1[inqnum][1] = 0.0;
						seermodel1[inqnum][2] = 0.0;
						seermodel1[inqnum][3] = 0.0;
						seermodel1[inqnum][4] = 1.0;
					}else{
						seermodel1[inqnum][0] = 0.0;
						seermodel1[inqnum][1] = 0.0;
						seermodel1[inqnum][2] = 0.0;
						seermodel1[inqnum][3] = 0.5;
						seermodel1[inqnum][4] = 0.5;
					}



				}
				if(inspectResult2 == Species.HUMAN && seermodel1[insnum][4] == 1.0){
					if(seermodel1[inqnum][4] == 0.0){
						seermodel1[inqnum][0] = 0.0;
						seermodel1[inqnum][1] = 0.0;
						seermodel1[inqnum][2] = 0.0;
						seermodel1[inqnum][3] = 1.0;
						seermodel1[inqnum][4] = 0.0;
					}else if(seermodel1[inqnum][4] == 1.0){
						seermodel1[inqnum][0] = 0.0;
						seermodel1[inqnum][1] = 0.0;
						seermodel1[inqnum][2] = 0.0;
						seermodel1[inqnum][3] = 0.0;
						seermodel1[inqnum][4] = 1.0;
					}else{
						seermodel1[inqnum][0] = 0.0;
						seermodel1[inqnum][1] = 0.0;
						seermodel1[inqnum][2] = 0.0;
						seermodel1[inqnum][3] = 0.5;
						seermodel1[inqnum][4] = 0.5;
					}


				}
				if(inspectResult2 == Species.WEREWOLF && seermodel2[insnum][4] == 0.0){
					if(seermodel2[inqnum][4] == 0.0){
						seermodel2[inqnum][0] = 0.0;
						seermodel2[inqnum][1] = 0.0;
						seermodel2[inqnum][2] = 0.0;
						seermodel2[inqnum][3] = 1.0;
						seermodel2[inqnum][4] = 0.0;
					}else if(seermodel2[inqnum][4] == 1.0){
						seermodel2[inqnum][0] = 0.0;
						seermodel2[inqnum][1] = 0.0;
						seermodel2[inqnum][2] = 0.0;
						seermodel2[inqnum][3] = 0.0;
						seermodel2[inqnum][4] = 1.0;
					}else{
						seermodel2[inqnum][0] = 0.0;
						seermodel2[inqnum][1] = 0.0;
						seermodel2[inqnum][2] = 0.0;
						seermodel2[inqnum][3] = 0.5;
						seermodel2[inqnum][4] = 0.5;
					}


				}
				if(inspectResult2 == Species.HUMAN && seermodel2[insnum][4] == 1.0){
					if(seermodel2[inqnum][4] == 0.0){
						seermodel2[inqnum][0] = 0.0;
						seermodel2[inqnum][1] = 0.0;
						seermodel2[inqnum][2] = 0.0;
						seermodel2[inqnum][3] = 1.0;
						seermodel2[inqnum][4] = 0.0;
					}else if(seermodel2[inqnum][4] == 1.0){
						seermodel2[inqnum][0] = 0.0;
						seermodel2[inqnum][1] = 0.0;
						seermodel2[inqnum][2] = 0.0;
						seermodel2[inqnum][3] = 0.0;
						seermodel2[inqnum][4] = 1.0;
					}else{
						seermodel2[inqnum][0] = 0.0;
						seermodel2[inqnum][1] = 0.0;
						seermodel2[inqnum][2] = 0.0;
						seermodel2[inqnum][3] = 0.5;
						seermodel2[inqnum][4] = 0.5;
					}



				}
				if(inspectResult2 == Species.WEREWOLF && seermodel3[insnum][4] == 0.0){
					if(seermodel3[inqnum][4] == 0.0){
						seermodel3[inqnum][0] = 0.0;
						seermodel3[inqnum][1] = 0.0;
						seermodel3[inqnum][2] = 0.0;
						seermodel3[inqnum][3] = 1.0;
						seermodel3[inqnum][4] = 0.0;
					}else if(seermodel3[inqnum][4] == 1.0){
						seermodel3[inqnum][0] = 0.0;
						seermodel3[inqnum][1] = 0.0;
						seermodel3[inqnum][2] = 0.0;
						seermodel3[inqnum][3] = 0.0;
						seermodel3[inqnum][4] = 1.0;
					}else{
						seermodel3[inqnum][0] = 0.0;
						seermodel3[inqnum][1] = 0.0;
						seermodel3[inqnum][2] = 0.0;
						seermodel3[inqnum][3] = 0.5;
						seermodel3[inqnum][4] = 0.5;
					}


				}
				if(inspectResult2 == Species.HUMAN && seermodel3[insnum][4] == 1.0){
					if(seermodel3[inqnum][4] == 0.0){
						seermodel3[inqnum][0] = 0.0;
						seermodel3[inqnum][1] = 0.0;
						seermodel3[inqnum][2] = 0.0;
						seermodel3[inqnum][3] = 1.0;
						seermodel3[inqnum][4] = 0.0;
					}else if(seermodel3[inqnum][4] == 1.0){
						seermodel3[inqnum][0] = 0.0;
						seermodel3[inqnum][1] = 0.0;
						seermodel3[inqnum][2] = 0.0;
						seermodel3[inqnum][3] = 0.0;
						seermodel3[inqnum][4] = 1.0;
					}else{
						seermodel3[inqnum][0] = 0.0;
						seermodel3[inqnum][1] = 0.0;
						seermodel3[inqnum][2] = 0.0;
						seermodel3[inqnum][3] = 0.5;
						seermodel3[inqnum][4] = 0.5;
					}
				}
				if(inspectResult2 == Species.WEREWOLF && seermodel4[insnum][4] == 0.0){
					if(seermodel4[inqnum][4] == 0.0){
						seermodel4[inqnum][0] = 0.0;
						seermodel4[inqnum][1] = 0.0;
						seermodel4[inqnum][2] = 0.0;
						seermodel4[inqnum][3] = 1.0;
						seermodel4[inqnum][4] = 0.0;
					}else if(seermodel4[inqnum][4] == 1.0){
						seermodel4[inqnum][0] = 0.0;
						seermodel4[inqnum][1] = 0.0;
						seermodel4[inqnum][2] = 0.0;
						seermodel4[inqnum][3] = 0.0;
						seermodel4[inqnum][4] = 1.0;
					}else{
						seermodel4[inqnum][0] = 0.0;
						seermodel4[inqnum][1] = 0.0;
						seermodel4[inqnum][2] = 0.0;
						seermodel4[inqnum][3] = 0.5;
						seermodel4[inqnum][4] = 0.5;
					}
				}
				if(inspectResult2 == Species.HUMAN && seermodel4[insnum][4] == 1.0){
					if(seermodel4[inqnum][4] == 0.0){
						seermodel4[inqnum][0] = 0.0;
						seermodel4[inqnum][1] = 0.0;
						seermodel4[inqnum][2] = 0.0;
						seermodel4[inqnum][3] = 1.0;
						seermodel4[inqnum][4] = 0.0;
					}else if(seermodel4[inqnum][4] == 1.0){
						seermodel4[inqnum][0] = 0.0;
						seermodel4[inqnum][1] = 0.0;
						seermodel4[inqnum][2] = 0.0;
						seermodel4[inqnum][3] = 0.0;
						seermodel4[inqnum][4] = 1.0;
					}else{
						seermodel4[inqnum][0] = 0.0;
						seermodel4[inqnum][1] = 0.0;
						seermodel4[inqnum][2] = 0.0;
						seermodel4[inqnum][3] = 0.5;
						seermodel4[inqnum][4] = 0.5;
					}
				}
				}
				}

				break;


			}
		}
		readTalkListNum =talkList.size();
		//会話後の情報の更新
		setAgent();
		BlackListadd();
		AbstractRole();

	}

	@Override
	public String talk() {



		//占い師だと思っている人を発話
		if(seerAgentList.size() != 0){
			if(declaredseerAgent != maybeSEER){
			String string = TemplateTalkFactory.estimate(maybeSEER, Role.SEER);
			declaredseerAgent = maybeSEER;
			return string;
		}
		}

		//ブラックリストに追加されたエージェントを人狼として発話
		if(BlackList.size() != 0){
			for(int i = 0; i< BlackList.size(); i++){
				if(!declaredblackAgent.contains(BlackList.get(i))){
			String string = TemplateTalkFactory.estimate(BlackList.get(i), Role.WEREWOLF);
			declaredblackAgent.add(BlackList.get(i));
			return string;
				}
			}
		}

		//投票しようとしているエージェントの発話
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

	@Override
	public void finish() {
		// TODO 自動生成されたメソッド・スタブ

	}













}
