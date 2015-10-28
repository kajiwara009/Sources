package main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import jp.ac.cu.hiroshima.inaba.agent.InabaAgent;
import jp.ac.cu.hiroshima.info.cm.nakamura.player.NoriRoleAssignPlayer;

import org.aiwolf.client.base.smpl.SampleRoleAssignPlayer;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Player;
import org.aiwolf.common.data.Team;
import org.aiwolf.common.net.GameSetting;
import org.aiwolf.kajiClient.player.KajiRoleAssignPlayer;
import org.aiwolf.server.AIWolfGame;
import org.aiwolf.server.GameData;
import org.aiwolf.server.net.DirectConnectServer;
import org.aiwolf.server.net.GameServer;

public class CalcWinRate {

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




		 Result result = new Result();

		 List<String> output = new ArrayList<String>();

		 for(int i = 0;i<GAME_NUM;i++){
			 List<Player> playerList = new ArrayList<Player>();

			 //Target1とtarget2の設定
			 //playerList.add(new SampleRoleAssignPlayer());

			 for(int j=0;j<3;j++){
				playerList.add(new InabaAgent());
				playerList.add(new KajiRoleAssignPlayer());
				// playerList.add(new jp.ac.cu.hiroshima.info.cm.nakamura.player.NoriRoleAssignPlayer());
				playerList.add(new SampleRoleAssignPlayer());
				// playerList.add(new RandomAgent());
				 playerList.add(new NoriRoleAssignPlayer());
			 }

			playerList.add(new InabaAgent());
			playerList.add(new KajiRoleAssignPlayer());
			playerList.add(new NoriRoleAssignPlayer());
//			 for(int j=0;j<6;j++){
//				 //playerList.add(new InabaPlayer());
//				 playerList.add(new KajiRoleAssignPlayer());
//				 //playerList.add(new SampleRoleAssignPlayer());
//			 }
//
	/*if(i%2 == 0) {
			 playerList.add(new NoriRoleAssignPlayer());
				// playerList.add(new InabaAgent());
				 //playerList.add(new KajiRoleAssignPlayer());
				 //playerList.add(new SampleRoleAssignPlayer());
		 }else{
			//playerList.add(new InabaAgent());
			// playerList.add(new KajiRoleAssignPlayer());
			// playerList.add(new NoriRoleAssignPlayer());
			 //playerList.add(new SampleRoleAssignPlayer());
			 playerList.add(new RandomAgent());
		 }*/
		Collections.shuffle(playerList);
			 GameServer gameServer = new DirectConnectServer(playerList);
			 GameSetting gameSetting = GameSetting.getDefaultGame(PLAYER_NUM);

			 AIWolfGame game = new AIWolfGame(gameSetting, gameServer);
			 game.setRand(new Random(gameSetting.getRandomSeed()));
			 game.start();



			 GameData gd = game.getGameData().getDayBefore();

			 Team winner = game.getWinner();



			 for(Agent a : gd.getAgentList()) {
				 switch (gd.getRole(a)){
				 case VILLAGER:
					 if(winner == Team.VILLAGER) {
						 result.addVillagerWinNum(gameServer.requestName(a));
					 }else{
						 result.addVillagerLoseNum(gameServer.requestName(a));
					 }
					 break;

				 case SEER:
					 if(winner == Team.VILLAGER) {
						 result.addVillagerWinNum(gameServer.requestName(a));
					 }else{
						 result.addVillagerLoseNum(gameServer.requestName(a));
					 }
					 break;

				 case MEDIUM:
					 if(winner == Team.VILLAGER) {
						 result.addVillagerWinNum(gameServer.requestName(a));
					 }else{
						 result.addVillagerLoseNum(gameServer.requestName(a));
					 }
					 break;

				 case BODYGUARD:
					 if(winner == Team.VILLAGER) {
						 result.addVillagerWinNum(gameServer.requestName(a));
					 }else{
						 result.addVillagerLoseNum(gameServer.requestName(a));
					 }
					 break;


				 case WEREWOLF:
					 if(winner == Team.VILLAGER) {
						 result.addWerewolfLoseNum(gameServer.requestName(a));
					 }else{
						 result.addWerewolfWinNum(gameServer.requestName(a));
					 }
					 break;

				 case POSSESSED:
					 if(winner == Team.VILLAGER) {
						 result.addWerewolfLoseNum(gameServer.requestName(a));
					 }else{
						 result.addWerewolfWinNum(gameServer.requestName(a));
					 }
					 break;
				 default:
					 System.err.println("想定外の役職です");
				 }

			 }




			 if(game.getWinner() == Team.VILLAGER){
				 villagerWinNum++;
			 }else{
				 werewolfWinNum++;
			 }
			 System.out.println(i);
		 }

		 System.out.println("Villager:" + villagerWinNum + " Werewolf:" + werewolfWinNum);

		 for(String name : result.getNameSet()) {
			 System.out.println(name + " Villager Win Rate " + result.getVillagerWinRate(name));
		 }

		 for(String name : result.getNameSet()) {
			 System.out.println(name + " Werewolf Win Rate " + result.getWerewolfWinRate(name));
		 }

		 for(String name : result.getNameSet()) {
			 System.out.println(name + " Total Win Rate " + result.getTotalWinRate(name));
		 }



	}

}
