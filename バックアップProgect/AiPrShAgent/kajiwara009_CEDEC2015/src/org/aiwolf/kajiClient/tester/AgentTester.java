package org.aiwolf.kajiClient.tester;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import javax.swing.text.ChangedCharSetException;

import org.aiwolf.common.data.Player;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.net.GameSetting;
import org.aiwolf.kajiClient.player.KajiRoleAssignPlayer;
import org.aiwolf.kajiPlayer.profitSharing.PrShAssignPlayer;
import org.aiwolf.kajiPlayer.profitSharing.PrShBasePlayer;
import org.aiwolf.laern.lib.LearningControler;
import org.aiwolf.laern.lib.Observe;
import org.aiwolf.laern.lib.ObservePool;
import org.aiwolf.laern.lib.SituationPool;
import org.aiwolf.laern.lib.LearningControler.Strategy;
import org.aiwolf.server.AIWolfGame;
import org.aiwolf.server.net.DirectConnectServer;

/**
 * エージェントをテストするためのクラス<br>
 * 自作エージェントをPlayerとして指定し，ランダムプレイヤーと対戦することで
 * 相手が予想外の行動を行った際に発生するExceptionを探すことが出来ます．
 * @author tori
 *
 */
public class AgentTester {

	/**
	 * @param args
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
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
		

		learningPlayerMap.put(new KajiRoleAssignPlayer(), Role.POSSESSED);
		learningPlayerMap.put(new KajiRoleAssignPlayer(), Role.WEREWOLF);
		learningPlayerMap.put(new KajiRoleAssignPlayer(), Role.WEREWOLF);
		learningPlayerMap.put(new KajiRoleAssignPlayer(), Role.WEREWOLF);
		
		changeLearningSelect(learningPlayerMap, Strategy.RANDOM);
		
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
				game.start();
				playNum++;
				if(playNum % 100 == 0){
					long time = System.currentTimeMillis();
					System.out.println(playNum + "回：" + (float)(time - timePre)/1000f );
					timePre = time;
					if(playNum % 1000 == 0){
						if(playNum % 2000 == 0){
							changeLearningSelect(learningPlayerMap, Strategy.RANDOM);
							System.out.println("RANDOM_SELECTスタート");
						}else{
							changeLearningSelect(learningPlayerMap, Strategy.GREEDY);
							System.out.println("GREEDY_SELECTスタート");
						}
						System.out.println("アウトプット中");
						output(learningPlayerMap, playNum);
						System.out.println("アウトプット完了");
					}
				}
				
			}
		}
		
		
		
		
		System.out.println(playNum);
		System.out.println((System.currentTimeMillis() - timePre));
	}
	
	private static void output(Map<Player, Role> learningPlayerMap, int playNum){
		
		PrintWriter pw = null;
		try {
			File file = new File("PlayNum.txt");
			FileWriter filewriter = new FileWriter(file);
			BufferedWriter bw = new BufferedWriter(filewriter);
			pw = new PrintWriter(bw);
		} catch (Exception e) {
		}
		pw.println(playNum);
		pw.close();

		
		boolean isVillagerFinished = false;
		for(Entry<Player, Role> set: learningPlayerMap.entrySet()){
			if(set.getValue() == Role.VILLAGER){
				if(isVillagerFinished){
					continue;
				}
				PrShAssignPlayer prshPlayer = (PrShAssignPlayer)set.getKey();
				PrShBasePlayer base = (PrShBasePlayer)prshPlayer.getVillagerPlayer();
				base.outputData();
				isVillagerFinished = true;
			}else if(set.getValue() == Role.SEER){
				PrShAssignPlayer prshPlayer = (PrShAssignPlayer)set.getKey();
				PrShBasePlayer base = (PrShBasePlayer)prshPlayer.getSeerPlayer();
				base.outputData();
			}else if(set.getValue() == Role.MEDIUM){
				PrShAssignPlayer prshPlayer = (PrShAssignPlayer)set.getKey();
				PrShBasePlayer base = (PrShBasePlayer)prshPlayer.getMediumPlayer();
				base.outputData();
			}else if(set.getValue() == Role.BODYGUARD){
				PrShAssignPlayer prshPlayer = (PrShAssignPlayer)set.getKey();
				PrShBasePlayer base = (PrShBasePlayer)prshPlayer.getBodyguardPlayer();
				base.outputData();
			}
		}
	}
	
	private static void changeLearningSelect(Map<Player, Role> map, LearningControler.Strategy str){
		for(Entry<Player, Role> set: map.entrySet()){
			if(set.getKey() instanceof PrShAssignPlayer){
				PrShAssignPlayer prshPlayer = (PrShAssignPlayer)set.getKey();
				PrShBasePlayer base = null;
				switch (set.getValue()) {
				case VILLAGER:
					base = (PrShBasePlayer)prshPlayer.getVillagerPlayer();
					break;
				case BODYGUARD:
					base = (PrShBasePlayer)prshPlayer.getBodyguardPlayer();
					break;
				case MEDIUM:
					base = (PrShBasePlayer)prshPlayer.getMediumPlayer();
					break;
				case SEER:
					base = (PrShBasePlayer)prshPlayer.getSeerPlayer();
					break;
				default:
					break;
				}
				base.getLearningControler().setStrategy(str);
			}
		}
	}

}
