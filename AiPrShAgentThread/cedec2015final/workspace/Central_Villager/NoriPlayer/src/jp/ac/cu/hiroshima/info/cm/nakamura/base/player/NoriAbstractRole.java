package jp.ac.cu.hiroshima.info.cm.nakamura.base.player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.net.GameInfo;
import org.aiwolf.common.net.GameSetting;

public abstract class NoriAbstractRole{

	//Index:day, content:GameInfo MApで
	Map<Integer, GameInfo> gameInfoMap = new HashMap<Integer, GameInfo>();

	int day;

	Agent me;

	Role myRole;

	GameSetting gameSetting;


	public double[][] psymodel = new double[14][7];


	public NoriAbstractRole(){
		for(int i = 0;i<psymodel.length;i++){
			for(int j = 0;j < psymodel[i].length-2;j++){
				psymodel[i][j] = 0.2;
			}
			psymodel[i][5] = 1.0;
			psymodel[i][6] = 0.0;
		}
	}

	public double[][] seermodel0 = new double[14][7];
	public double[][] seermodel1 = new double[14][7];
	public double[][] seermodel2 = new double[14][7];
	public double[][] seermodel3 = new double[14][7];
	public double[][] seermodel4 = new double[14][7];


	public void seermodel(Agent x ,int y, List<Agent> seerAgentList, List<Agent> medAgentList){
		List<Agent> AgentList = getLatestDayGameInfo().getAgentList();
		AgentList.remove(getMe());

		List<Agent> aliveAgentList = getLatestDayGameInfo().getAliveAgentList();
		aliveAgentList.remove(getMe());

		int z = AgentList.indexOf(x);

		if(getMyRole() == Role.VILLAGER || getMyRole() == Role.BODYGUARD){
		if(y == 1){
			for(int i = 0;i<seermodel0.length;i++){
				for(int j = 0;j<seermodel0[i].length;j++){
					if(i == z){
						seermodel0[i][0] = 0.0;
						seermodel0[i][1] = 1.0;
						seermodel0[i][2] = 0.0;
						seermodel0[i][3] = 0.0;
						seermodel0[i][4] = 0.0;

						seermodel0[i][6] = 1.0;
					}else{

					if(j == 1 || j == 5 || j == 6){
						seermodel0[i][j] = 0.0;
					}else if(j == 0){
						seermodel0[i][j] = 0.3;
					}else if(j == 4){
						seermodel0[i][j] = 0.3;
					}
					else{
						seermodel0[i][j] = 0.2;
					}
					}
				}

			}
			for(int l = 0; l<aliveAgentList.size();l++){
				int alnum = AgentList.indexOf(aliveAgentList.get(l));
				if(alnum != -1){
					seermodel0[alnum][5] = 1.0;
				}
			}

			for(int n = 0;n<medAgentList.size();n++){
				int mnum = AgentList.indexOf(medAgentList.get(n));
				if(mnum != -1){
					seermodel0[mnum][0] = 0.0;
					seermodel0[mnum][1] = 0.0;
					seermodel0[mnum][2] = 0.5;
					seermodel0[mnum][3] = 0.2;
					seermodel0[mnum][4] = 0.3;
					seermodel0[mnum][6] = 2.0;
				}
			}
		}

		if(y == 2){
			for(int i = 0;i<seermodel1.length;i++){
				for(int j = 0;j<seermodel1[i].length;j++){
					if(i == z){
						seermodel1[i][0] = 0.0;
						seermodel1[i][1] = 1.0;
						seermodel1[i][2] = 0.0;
						seermodel1[i][3] = 0.0;
						seermodel1[i][4] = 0.0;

						seermodel1[i][6] = 1.0;
					}else{

					if(j == 1 || j == 5 || j == 6){
						seermodel1[i][j] = 0.0;
					}else if(j == 0){
						seermodel1[i][j] = 0.3;
					}else if(j == 4){
						seermodel1[i][j] = 0.3;
					}
					else{
						seermodel1[i][j] = 0.2;
					}
					}
				}

			}
			for(int l = 0; l<aliveAgentList.size();l++){
				int alnum = AgentList.indexOf(aliveAgentList.get(l));
				if(alnum != -1){
					seermodel1[alnum][5] = 1.0;
				}
			}
			for(int m = 0;m < seerAgentList.size()-1;m++){
				int snum = AgentList.indexOf(seerAgentList.get(m));
				if(snum != -1){
					seermodel1[snum][0] = 0.0;
					seermodel1[snum][1] = 0.0;
					seermodel1[snum][2] = 0.0;
					seermodel1[snum][3] = 0.5;
					seermodel1[snum][4] = 0.5;
					seermodel1[snum][6] = 1.0;

				}
			}
			for(int n = 0;n<medAgentList.size();n++){
				int mnum = AgentList.indexOf(medAgentList.get(n));
				if(mnum != -1){
					seermodel1[mnum][0] = 0.0;
					seermodel1[mnum][1] = 0.0;
					seermodel1[mnum][2] = 0.5;
					seermodel1[mnum][3] = 0.2;
					seermodel1[mnum][4] = 0.3;
					seermodel1[mnum][6] = 2.0;
				}
			}
		}
		if(y == 3){
			for(int i = 0;i<seermodel2.length;i++){
				for(int j = 0;j<seermodel2[i].length;j++){
					if(i == z){
						seermodel2[i][0] = 0.0;
						seermodel2[i][1] = 1.0;
						seermodel2[i][2] = 0.0;
						seermodel2[i][3] = 0.0;
						seermodel2[i][4] = 0.0;

						seermodel2[i][6] = 1.0;
					}else{

					if(j == 1 || j == 5 || j == 6){
						seermodel2[i][j] = 0.0;
					}else if(j == 0){
						seermodel2[i][j] = 0.3;
					}else if(j == 4){
						seermodel2[i][j] = 0.3;
					}else{
						seermodel2[i][j] = 0.2;
					}
					}
				}

			}
			for(int l = 0; l<aliveAgentList.size();l++){
				int alnum = AgentList.indexOf(aliveAgentList.get(l));
				if(alnum != -1){
					seermodel2[alnum][5] = 1.0;
				}
			}

			for(int m = 0;m < seerAgentList.size()-1;m++){
				int snum = AgentList.indexOf(seerAgentList.get(m));
				if(snum != -1){
					seermodel2[snum][0] = 0.0;
					seermodel2[snum][1] = 0.0;
					seermodel2[snum][2] = 0.0;
					seermodel2[snum][3] = 0.5;
					seermodel2[snum][4] = 0.5;
					seermodel2[snum][6] = 1.0;

				}
			}
			for(int n = 0;n<medAgentList.size();n++){
				int mnum = AgentList.indexOf(medAgentList.get(n));
				if(mnum != -1){
					seermodel2[mnum][0] = 0.0;
					seermodel2[mnum][1] = 0.0;
					seermodel2[mnum][2] = 0.5;
					seermodel2[mnum][3] = 0.2;
					seermodel2[mnum][4] = 0.3;
					seermodel2[mnum][6] = 2.0;
				}
			}
		}

		if(y == 4){
			for(int i = 0;i<seermodel3.length;i++){
				for(int j = 0;j<seermodel3[i].length;j++){
					if(i == z){
						seermodel3[i][0] = 0.0;
						seermodel3[i][1] = 1.0;
						seermodel3[i][2] = 0.0;
						seermodel3[i][3] = 0.0;
						seermodel3[i][4] = 0.0;

						seermodel3[i][6] = 1.0;
					}else{

					if(j == 1 || j == 5 || j == 6 ){
						seermodel3[i][j] = 0.0;
					}else if(j == 0){
						seermodel3[i][j] = 0.3;
					}else if(j == 4){
						seermodel3[i][j] = 0.3;
					}else{
						seermodel3[i][j] = 0.2;
					}
					}
				}

			}

			for(int l = 0; l<aliveAgentList.size();l++){
				int alnum = AgentList.indexOf(aliveAgentList.get(l));
				if(alnum != -1){
					seermodel3[alnum][5] = 1.0;
				}
			}
			for(int m = 0;m < seerAgentList.size()-1;m++){
				int snum = AgentList.indexOf(seerAgentList.get(m));
				if(snum != -1){
					seermodel3[snum][0] = 0.0;
					seermodel3[snum][1] = 0.0;
					seermodel3[snum][2] = 0.0;
					seermodel3[snum][3] = 0.5;
					seermodel3[snum][4] = 0.5;
					seermodel3[snum][6] = 1.0;

				}
			}
			for(int n = 0;n<medAgentList.size();n++){
				int mnum = AgentList.indexOf(medAgentList.get(n));
				if(mnum != -1){
					seermodel3[mnum][0] = 0.0;
					seermodel3[mnum][1] = 0.0;
					seermodel3[mnum][2] = 0.5;
					seermodel3[mnum][3] = 0.2;
					seermodel3[mnum][4] = 0.3;
					seermodel3[mnum][6] = 2.0;
				}
			}
		}
		if(y == 5){
			for(int i = 0;i<seermodel4.length;i++){
				for(int j = 0;j<seermodel4[i].length;j++){
					if(i == z){
						seermodel4[i][0] = 0.0;
						seermodel4[i][1] = 1.0;
						seermodel4[i][2] = 0.0;
						seermodel4[i][3] = 0.0;
						seermodel4[i][4] = 0.0;

						seermodel4[i][6] = 1.0;
					}else{

					if(j == 1 || j == 5 || j == 6){
						seermodel4[i][j] = 0.0;
					}else if(j == 0){
						seermodel4[i][j] = 0.3;
					}else if(j == 4){
						seermodel4[i][j] = 0.3;
					}else{
						seermodel4[i][j] = 0.2;
					}
					}
				}

			}

			for(int l = 0; l<aliveAgentList.size();l++){
				int alnum = AgentList.indexOf(aliveAgentList.get(l));
				if(alnum != -1){
					seermodel4[alnum][5] = 1.0;
				}
			}
			for(int m = 0;m < seerAgentList.size()-1;m++){
				int snum = AgentList.indexOf(seerAgentList.get(m));
				if(snum != -1){
					seermodel4[snum][0] = 0.0;
					seermodel4[snum][1] = 0.0;
					seermodel4[snum][2] = 0.0;
					seermodel4[snum][3] = 0.5;
					seermodel4[snum][4] = 0.5;
					seermodel4[snum][6] = 1.0;

				}
			}
			for(int n = 0;n<medAgentList.size();n++){
				int mnum = AgentList.indexOf(medAgentList.get(n));
				if(mnum != -1){
					seermodel4[mnum][0] = 0.0;
					seermodel4[mnum][1] = 0.0;
					seermodel4[mnum][2] = 0.5;
					seermodel4[mnum][3] = 0.2;
					seermodel4[mnum][4] = 0.3;
					seermodel4[mnum][6] = 2.0;
				}
			}
		}
		}

		if(getMyRole() == Role.MEDIUM){
			if(y == 1){
				for(int i = 0;i<seermodel0.length;i++){
					for(int j = 0;j<seermodel0[i].length;j++){
						if(i == z){
							seermodel0[i][0] = 0.0;
							seermodel0[i][1] = 1.0;
							seermodel0[i][2] = 0.0;
							seermodel0[i][3] = 0.0;
							seermodel0[i][4] = 0.0;

							seermodel0[i][6] = 1.0;
						}else{

						if(j == 1 || j == 2 || j == 5 || j == 6){
							seermodel0[i][j] = 0.0;
						}else if(j == 0){
							seermodel0[i][j] = 0.5;
						}else if(j == 4){
							seermodel0[i][j] = 0.3;
						}
						else {
							seermodel0[i][j] = 0.2;
						}
						}
					}

				}
				for(int l = 0; l<aliveAgentList.size();l++){
					int alnum = AgentList.indexOf(aliveAgentList.get(l));
					if(alnum != -1){
						seermodel0[alnum][5] = 1.0;
					}
				}

				for(int n = 0;n<medAgentList.size();n++){
					int mnum = AgentList.indexOf(medAgentList.get(n));
					if(mnum != -1){
						seermodel0[mnum][0] = 0.0;
						seermodel0[mnum][1] = 0.0;
						seermodel0[mnum][2] = 0.0;
						seermodel0[mnum][3] = 0.5;
						seermodel0[mnum][4] = 0.5;
						seermodel0[mnum][6] = 2.0;
					}
				}
			}

			if(y == 2){
				for(int i = 0;i<seermodel1.length;i++){
					for(int j = 0;j<seermodel1[i].length;j++){
						if(i == z){
							seermodel1[i][0] = 0.0;
							seermodel1[i][1] = 1.0;
							seermodel1[i][2] = 0.0;
							seermodel1[i][3] = 0.0;
							seermodel1[i][4] = 0.0;

							seermodel1[i][6] = 1.0;
						}else{

							if(j == 1 || j == 2 || j == 5 || j == 6){
								seermodel1[i][j] = 0.0;
							}else if(j == 0){
								seermodel1[i][j] = 0.5;
							}else if(j == 4){
								seermodel1[i][j] = 0.3;
							}
							else {
								seermodel1[i][j] = 0.2;
							}
						}
					}

				}
				for(int l = 0; l<aliveAgentList.size();l++){
					int alnum = AgentList.indexOf(aliveAgentList.get(l));
					if(alnum != -1){
						seermodel1[alnum][5] = 1.0;
					}
				}
				for(int m = 0;m < seerAgentList.size()-1;m++){
					int snum = AgentList.indexOf(seerAgentList.get(m));
					if(snum != -1){
						seermodel1[snum][0] = 0.0;
						seermodel1[snum][1] = 0.0;
						seermodel1[snum][2] = 0.0;
						seermodel1[snum][3] = 0.5;
						seermodel1[snum][4] = 0.5;
						seermodel1[snum][6] = 1.0;

					}
				}
				for(int n = 0;n<medAgentList.size();n++){
					int mnum = AgentList.indexOf(medAgentList.get(n));
					if(mnum != -1){
						seermodel1[mnum][0] = 0.0;
						seermodel1[mnum][1] = 0.0;
						seermodel1[mnum][2] = 0.0;
						seermodel1[mnum][3] = 0.5;
						seermodel1[mnum][4] = 0.5;
						seermodel1[mnum][6] = 2.0;
					}
				}
			}
			if(y == 3){
				for(int i = 0;i<seermodel2.length;i++){
					for(int j = 0;j<seermodel2[i].length;j++){
						if(i == z){
							seermodel2[i][0] = 0.0;
							seermodel2[i][1] = 1.0;
							seermodel2[i][2] = 0.0;
							seermodel2[i][3] = 0.0;
							seermodel2[i][4] = 0.0;

							seermodel2[i][6] = 1.0;
						}else{

							if(j == 1 || j == 2 || j == 5 || j == 6){
								seermodel2[i][j] = 0.0;
							}else if(j == 0){
								seermodel2[i][j] = 0.5;
							}else if(j == 4){
								seermodel2[i][j] = 0.3;
							}
							else {
								seermodel2[i][j] = 0.2;
							}
						}
					}

				}
				for(int l = 0; l<aliveAgentList.size();l++){
					int alnum = AgentList.indexOf(aliveAgentList.get(l));
					if(alnum != -1){
						seermodel2[alnum][5] = 1.0;
					}
				}

				for(int m = 0;m < seerAgentList.size()-1;m++){
					int snum = AgentList.indexOf(seerAgentList.get(m));
					if(snum != -1){
						seermodel2[snum][0] = 0.0;
						seermodel2[snum][1] = 0.0;
						seermodel2[snum][2] = 0.0;
						seermodel2[snum][3] = 0.5;
						seermodel2[snum][4] = 0.5;
						seermodel2[snum][6] = 1.0;

					}
				}
				for(int n = 0;n<medAgentList.size();n++){
					int mnum = AgentList.indexOf(medAgentList.get(n));
					if(mnum != -1){
						seermodel2[mnum][0] = 0.0;
						seermodel2[mnum][1] = 0.0;
						seermodel2[mnum][2] = 0.0;
						seermodel2[mnum][3] = 0.5;
						seermodel2[mnum][4] = 0.5;
						seermodel2[mnum][6] = 2.0;
					}
				}
			}

			if(y == 4){
				for(int i = 0;i<seermodel3.length;i++){
					for(int j = 0;j<seermodel3[i].length;j++){
						if(i == z){
							seermodel3[i][0] = 0.0;
							seermodel3[i][1] = 1.0;
							seermodel3[i][2] = 0.0;
							seermodel3[i][3] = 0.0;
							seermodel3[i][4] = 0.0;

							seermodel3[i][6] = 1.0;
						}else{

							if(j == 1 || j == 2 || j == 5 || j == 6){
								seermodel3[i][j] = 0.0;
							}else if(j == 0){
								seermodel3[i][j] = 0.5;
							}else if(j == 4){
								seermodel3[i][j] = 0.3;
							}
							else {
								seermodel3[i][j] = 0.2;
							}
						}
					}

				}

				for(int l = 0; l<aliveAgentList.size();l++){
					int alnum = AgentList.indexOf(aliveAgentList.get(l));
					if(alnum != -1){
						seermodel3[alnum][5] = 1.0;
					}
				}
				for(int m = 0;m < seerAgentList.size()-1;m++){
					int snum = AgentList.indexOf(seerAgentList.get(m));
					if(snum != -1){
						seermodel3[snum][0] = 0.0;
						seermodel3[snum][1] = 0.0;
						seermodel3[snum][2] = 0.0;
						seermodel3[snum][3] = 0.5;
						seermodel3[snum][4] = 0.5;
						seermodel3[snum][6] = 1.0;

					}
				}
				for(int n = 0;n<medAgentList.size();n++){
					int mnum = AgentList.indexOf(medAgentList.get(n));
					if(mnum != -1){
						seermodel3[mnum][0] = 0.0;
						seermodel3[mnum][1] = 0.0;
						seermodel3[mnum][2] = 0.0;
						seermodel3[mnum][3] = 0.5;
						seermodel3[mnum][4] = 0.5;
						seermodel3[mnum][6] = 2.0;
					}
				}
			}
			if(y == 5){
				for(int i = 0;i<seermodel4.length;i++){
					for(int j = 0;j<seermodel4[i].length;j++){
						if(i == z){
							seermodel4[i][0] = 0.0;
							seermodel4[i][1] = 1.0;
							seermodel4[i][2] = 0.0;
							seermodel4[i][3] = 0.0;
							seermodel4[i][4] = 0.0;

							seermodel4[i][6] = 1.0;
						}else{

							if(j == 1 || j == 2 || j == 5 || j == 6){
								seermodel4[i][j] = 0.0;
							}else if(j == 0){
								seermodel4[i][j] = 0.5;
							}else if(j == 4){
								seermodel4[i][j] = 0.3;
							}
							else {
								seermodel4[i][j] = 0.2;
							}
						}
					}

				}

				for(int l = 0; l<aliveAgentList.size();l++){
					int alnum = AgentList.indexOf(aliveAgentList.get(l));
					if(alnum != -1){
						seermodel4[alnum][5] = 1.0;
					}
				}
				for(int m = 0;m < seerAgentList.size()-1;m++){
					int snum = AgentList.indexOf(seerAgentList.get(m));
					if(snum != -1){
						seermodel4[snum][0] = 0.0;
						seermodel4[snum][1] = 0.0;
						seermodel4[snum][2] = 0.0;
						seermodel4[snum][3] = 0.5;
						seermodel4[snum][4] = 0.5;
						seermodel4[snum][6] = 1.0;

					}
				}
				for(int n = 0;n<medAgentList.size();n++){
					int mnum = AgentList.indexOf(medAgentList.get(n));
					if(mnum != -1){
						seermodel4[mnum][0] = 0.0;
						seermodel4[mnum][1] = 0.0;
						seermodel4[mnum][2] = 0.0;
						seermodel4[mnum][3] = 0.5;
						seermodel4[mnum][4] = 0.5;
						seermodel4[mnum][6] = 2.0;
					}
				}
			}
		}
		}


	public String getName() {
		return myRole.name() + "Player:ID=" + me.getAgentIdx();
	}


	public void update(GameInfo gameInfo) {
		day = gameInfo.getDay();

		gameInfoMap.put(day, gameInfo);
	}

	public GameInfo getLatestDayGameInfo(){
		return gameInfoMap.get(day);
	}

	public GameInfo getGameInfo(int day){
		try {
			return gameInfoMap.get(day);
		} catch (ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
			return null;
		}
	}

	public Map<Integer, GameInfo> getGameInfoMap(){
		return gameInfoMap;
	}

	public Role getMyRole(){
		return myRole;
	}

	public Agent getMe(){
		return me;
	}

	public int getDay(){
		return day;
	}

	public void setAgent(Agent agent){
		me = agent;
	}

	public GameSetting getGameSetting(){
		return gameSetting;
	}


	public void initialize(GameInfo gameInfo, GameSetting gameSetting){
		gameInfoMap.clear();
		this.gameSetting = gameSetting;
		day = gameInfo.getDay();
		gameInfoMap.put(day, gameInfo);
		myRole = gameInfo.getRole();
		me = gameInfo.getAgent();
		return;
	}

	public abstract void dayStart();


	public abstract String talk();


	public abstract String whisper();


	public abstract Agent vote();


	public abstract Agent attack();


	public abstract Agent divine();


	public abstract Agent guard();


	public abstract void finish();






}
