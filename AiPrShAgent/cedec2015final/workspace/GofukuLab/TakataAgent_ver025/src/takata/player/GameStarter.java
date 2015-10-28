package takata.player;

import inaba.player.InabaPlayer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.aiwolf.common.data.Player;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Team;
import org.aiwolf.common.net.GameSetting;
import org.aiwolf.server.AIWolfGame;
import org.aiwolf.server.net.DirectConnectServer;
import org.aiwolf.server.net.GameServer;

/**
 * 指定した回数ゲームを実行し，各陣営の勝利数を表示するMainクラス
 * @author inaba
 *
 */
public class GameStarter {

	 /**
	  * 参加エージェントの数
	  */
	 static protected int PLAYER_NUM = 15;

	 /**
	 * 1回の実行で行うゲーム数
	 */
	 static protected int GAME_NUM = 1000;

	 public static void main(String[] args) throws InstantiationException, IllegalAccessException, ClassNotFoundException, IOException {
		 //村人側勝利数
		 int villagerWinNum = 0;
		 //人狼側勝利数
		 int werewolfWinNum = 0;
		 //村人側勝利数
		 int villagerWinNum2 = 0;
		 //人狼側勝利数
		 int werewolfWinNum2 = 0;

		 for(int i = 0;i<GAME_NUM;i++){
			 //役職指定ない
/*			  List<Player> playerList = new ArrayList<Player>();

			  for(int j=0;j<PLAYER_NUM;j++){
				  playerList.add(new TakataRoleAssignPlayer()); //ここで作成したエージェントを指定
			  }*/

			  //役職指定あり TakataAgent（狼）vsInabaPlayer（村）
			  Map<Player, Role> playerMap = new HashMap<Player, Role>();
			  playerMap.put(new TakataRoleAssignPlayer(), Role.WEREWOLF);
			  playerMap.put(new TakataRoleAssignPlayer(), Role.WEREWOLF);
			  playerMap.put(new TakataRoleAssignPlayer(), Role.WEREWOLF);
			  playerMap.put(new TakataRoleAssignPlayer(), Role.POSSESSED);
			  playerMap.put(new InabaPlayer(), Role.VILLAGER);
			  playerMap.put(new InabaPlayer(), Role.VILLAGER);
			  playerMap.put(new InabaPlayer(), Role.VILLAGER);
			  playerMap.put(new InabaPlayer(), Role.VILLAGER);
			  playerMap.put(new InabaPlayer(), Role.VILLAGER);
			  playerMap.put(new InabaPlayer(), Role.VILLAGER);
			  playerMap.put(new InabaPlayer(), Role.VILLAGER);
			  playerMap.put(new InabaPlayer(), Role.VILLAGER);
			  playerMap.put(new InabaPlayer(), Role.SEER);
			  playerMap.put(new InabaPlayer(), Role.MEDIUM);
			  playerMap.put(new InabaPlayer(), Role.BODYGUARD);

			  GameServer gameServer = new DirectConnectServer(playerMap);
			  GameSetting gameSetting = GameSetting.getDefaultGame(PLAYER_NUM);

			  AIWolfGame game = new AIWolfGame(gameSetting, gameServer);
			  game.setRand(new Random(gameSetting.getRandomSeed()));
			  game.start();
			  if(game.getWinner() == Team.VILLAGER){
				  villagerWinNum++;
			  }else{
				  werewolfWinNum++;
			  }
			  System.out.println("村人側勝利:" + villagerWinNum + " 人狼側勝利:" + werewolfWinNum);
		 }
		 for(int i = 0;i<GAME_NUM;i++){

			  //役職指定あり TakataAgent（村）vsInabaPlayer（狼）
			  Map<Player, Role> playerMap = new HashMap<Player, Role>();
			  playerMap.put(new TakataRoleAssignPlayer(), Role.VILLAGER);
			  playerMap.put(new TakataRoleAssignPlayer(), Role.VILLAGER);
			  playerMap.put(new TakataRoleAssignPlayer(), Role.VILLAGER);
			  playerMap.put(new TakataRoleAssignPlayer(), Role.VILLAGER);
			  playerMap.put(new TakataRoleAssignPlayer(), Role.VILLAGER);
			  playerMap.put(new TakataRoleAssignPlayer(), Role.VILLAGER);
			  playerMap.put(new TakataRoleAssignPlayer(), Role.VILLAGER);
			  playerMap.put(new TakataRoleAssignPlayer(), Role.VILLAGER);
			  playerMap.put(new TakataRoleAssignPlayer(), Role.SEER);
			  playerMap.put(new TakataRoleAssignPlayer(), Role.MEDIUM);
			  playerMap.put(new TakataRoleAssignPlayer(), Role.BODYGUARD);
			  playerMap.put(new InabaPlayer(), Role.WEREWOLF);
			  playerMap.put(new InabaPlayer(), Role.WEREWOLF);
			  playerMap.put(new InabaPlayer(), Role.WEREWOLF);
			  playerMap.put(new InabaPlayer(), Role.POSSESSED);

			  GameServer gameServer = new DirectConnectServer(playerMap);
			  GameSetting gameSetting = GameSetting.getDefaultGame(PLAYER_NUM);

			  AIWolfGame game = new AIWolfGame(gameSetting, gameServer);
			  game.setRand(new Random(gameSetting.getRandomSeed()));
			  game.start();
			  if(game.getWinner() == Team.VILLAGER){
				  villagerWinNum2++;
			  }else{
				  werewolfWinNum2++;
			  }
			  System.out.println("村人側勝利:" + villagerWinNum2 + " 人狼側勝利:" + werewolfWinNum2);
		 }
		 System.out.println("村人側勝利1:" + villagerWinNum + " 人狼側勝利1:" + werewolfWinNum);
		 System.out.println("村人側勝利2:" + villagerWinNum2 + " 人狼側勝利2:" + werewolfWinNum2);
		 int TakataAgentWon = werewolfWinNum + villagerWinNum2;
		 int InabaPlayerWon = villagerWinNum + werewolfWinNum2;
		 System.out.println("TakataAgent勝利数:" + TakataAgentWon +  " InabaPlayer勝利数:" + InabaPlayerWon);
	 }
 }

