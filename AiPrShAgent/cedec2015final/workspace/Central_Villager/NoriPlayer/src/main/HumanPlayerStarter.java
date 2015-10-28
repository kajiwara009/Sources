package main;



import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jp.ac.cu.hiroshima.info.cm.nakamura.player.NoriRoleAssignPlayer;

import org.aiwolf.client.ui.HumanPlayer;
import org.aiwolf.common.data.Player;
import org.aiwolf.common.data.Team;
import org.aiwolf.common.net.GameSetting;
import org.aiwolf.server.AIWolfGame;
import org.aiwolf.server.net.DirectConnectServer;
import org.aiwolf.server.net.GameServer;

public class HumanPlayerStarter {
	 /**
	  * 参加エージェントの数
	  */
	 static protected int PLAYER_NUM = 15;

	 /**
	 * 1回の実行で行うゲーム数
	 */
	 static protected int GAME_NUM = 1;

	 public static void main(String[] args) throws InstantiationException, IllegalAccessException, ClassNotFoundException, IOException {
		 //村人側勝利数
		 int villagerWinNum = 0;
		 //人狼側勝利数
		 int werewolfWinNum = 0;


		 for(int i = 0;i<GAME_NUM;i++){
			 List<Player> playerList = new ArrayList<Player>();
			 playerList.add(new HumanPlayer());
			 for(int j=0;j<14;j++){
				// playerList.add(new InabaAgent());
				 playerList.add(new NoriRoleAssignPlayer());
				 //playerList.add(new RoleAssignPlayer());
			 }

			 GameServer gameServer = new DirectConnectServer(playerList);
			 GameSetting gameSetting = GameSetting.getDefaultGame(PLAYER_NUM);
			 AIWolfGame game = new AIWolfGame(gameSetting, gameServer);

			 game.start();
			 if(game.getWinner() == Team.VILLAGER){
				 villagerWinNum++;
			 }else{
				 werewolfWinNum++;
			 }
		 }


		 System.out.println("Villager:" + villagerWinNum + " Werewolf:" + werewolfWinNum);

	}

}
