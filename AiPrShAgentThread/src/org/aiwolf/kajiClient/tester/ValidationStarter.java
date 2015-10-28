package org.aiwolf.kajiClient.tester;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import jp.ac.shibaura_it.ma15082.WasabiPlayer;
import jp.halfmoon.inaba.aiwolf.strategyplayer.StrategyPlayer;

import org.aiwolf.common.data.Player;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Team;
import org.aiwolf.common.net.GameSetting;
import org.aiwolf.kajiClient.player.KajiRoleAssignPlayer;
import org.aiwolf.kajiPlayer.profitSharing.PrShAssignPlayer;
import org.aiwolf.kajiPlayer.profitSharing.PrShBasePlayer;
import org.aiwolf.laern.lib.ObservePool;
import org.aiwolf.laern.lib.SituationPool;
import org.aiwolf.laern.lib.LearningControler.Strategy;
import org.aiwolf.server.AIWolfGame;
import org.aiwolf.server.net.DirectConnectServer;

public class ValidationStarter {

	public ValidationStarter() {
		// TODO 自動生成されたコンストラクター・スタブ
	}
	
	public static void main(String[] args) throws InstantiationException, IllegalAccessException {

		Map<Player, Role> learningPlayerMap = new HashMap<Player, Role>();
		
		ObservePool op = new ObservePool();
		SituationPool sp = new SituationPool();
		for(int i = 0; i < GameSetting.getDefaultGame(15).getRoleNumMap().get(Role.VILLAGER); i++){
			PrShAssignPlayer player = new PrShAssignPlayer();
			((PrShBasePlayer)( player.getVillagerPlayer())).setObservePool(op);
			((PrShBasePlayer)( player.getVillagerPlayer())).setSituationPool(sp);;
			learningPlayerMap.put(player, Role.VILLAGER);
		}
		learningPlayerMap.put(new PrShAssignPlayer(), Role.SEER);
		learningPlayerMap.put(new PrShAssignPlayer(), Role.MEDIUM);
		learningPlayerMap.put(new PrShAssignPlayer(), Role.BODYGUARD);
		

/*		learningPlayerMap.put(new KajiRoleAssignPlayer(), Role.POSSESSED);
		learningPlayerMap.put(new KajiRoleAssignPlayer(), Role.WEREWOLF);
		learningPlayerMap.put(new KajiRoleAssignPlayer(), Role.WEREWOLF);
		learningPlayerMap.put(new KajiRoleAssignPlayer(), Role.WEREWOLF);
*/		
		learningPlayerMap.put(new WasabiPlayer(), Role.POSSESSED);
		learningPlayerMap.put(new WasabiPlayer(), Role.WEREWOLF);
		learningPlayerMap.put(new WasabiPlayer(), Role.WEREWOLF);
		learningPlayerMap.put(new WasabiPlayer(), Role.WEREWOLF);
		
		
//		changeLearningSelect(learningPlayerMap, Strategy.GREEDY);
		
		//ここにテストしたい自分のPlayerを指定してください．
//		Player player = new PrShAssignPlayer();
		Player player = new KajiRoleAssignPlayer();
		
		/////////////////////////////////////////////
		//これ以降は変更しないでください．
		
		Class<Player> pcls = (Class<Player>) player.getClass();
		player = pcls.newInstance();
		int playNum = 0;
		long timePre = System.currentTimeMillis();
		//10億回 = 1000 * 1000 * 1000
		
		int villagerWin = 0;
		int wolfWin = 0;
		
		for(int j = 0; j <= 1000 * 1000 * 1000; j++){
			for(Role requestRole:Role.values()){
				if(requestRole == Role.FREEMASON){
					continue;
				}
				
//				player = pcls.newInstance();
				
				Map<Player, Role> playerMap = new HashMap<Player, Role>();
//				playerMap.put(player, Role.SEER);
				playerMap.put(player, requestRole);
				for(int i = 0; i < 14; i++){
					playerMap.put(new RandomPlayer(), null);
				}
				
				DirectConnectServer gameServer = new DirectConnectServer(learningPlayerMap);
				GameSetting gameSetting = GameSetting.getDefaultGame(learningPlayerMap.size());
				AIWolfGame game = new AIWolfGame(gameSetting, gameServer);
				game.setRand(new Random());
				game.setShowConsoleLog(false);
//				System.out.println("Start:" + System.currentTimeMillis());
				game.start();
//				System.out.println("End" + System.currentTimeMillis());
//				game.finish();
				if(game.getWinner() == Team.VILLAGER){
					villagerWin++;
				}else{
					wolfWin++;
				}
				
				
				playNum++;
				if(playNum % 10 == 0){
					System.out.println("全部：" + playNum + "     Villagerの勝利数：" + villagerWin);
					long time = System.currentTimeMillis();
//					System.out.println(playNum + "回：" + (float)(time - timePre)/1000f );
					timePre = time;
				}
				
			}
		}
		
		
		
		
		System.out.println(playNum);
		System.out.println((System.currentTimeMillis() - timePre));
	}
	

}
