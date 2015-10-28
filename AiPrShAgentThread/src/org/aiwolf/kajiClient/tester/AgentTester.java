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

import jp.ac.shibaura_it.ma15082.WasabiPlayer;

import org.aiwolf.common.data.Player;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.net.GameSetting;
import org.aiwolf.kajiClient.player.KajiRoleAssignPlayer;
import org.aiwolf.kajiPlayer.noAction.NoActionPlayer;
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
	 * @throws ClassNotFoundException 
	 */
	public static void main(String[] args) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		//学習データを保存するディレクトリ，学習相手の設定
		String dir = "default/";
		String className = RandomPlayer.class.getName();
		for(int i = 0; i < args.length; i++){
			if(args[i].equals("-d")){
				i++;
				dir = args[i];
				if(!dir.endsWith("/")){
					dir += "/";
				}
				System.out.println("学習データの保存場所：" + dir);
			}else if(args[i].equals("-p")){
				i++;
				try {
					Class c = Class.forName(args[i]);
					className = c.getName();
					System.out.println("対戦相手:" + c.getName());
				} catch (Exception e) {
					System.out.println("そんなクラスは存在しましぇーん:" + args[i]);
				}
			}
			
		}

		Map<Player, Role> playerMap = new HashMap<Player, Role>();
		
		ObservePool op = new ObservePool();
		SituationPool sp = new SituationPool();
		for(int i = 0; i < GameSetting.getDefaultGame(15).getRoleNumMap().get(Role.VILLAGER); i++){
			PrShAssignPlayer player = new PrShAssignPlayer();
			((PrShBasePlayer)( player.getVillagerPlayer())).setObservePool(op);
			((PrShBasePlayer)( player.getVillagerPlayer())).setSituationPool(sp);;
			playerMap.put(player, Role.VILLAGER);
		}
		playerMap.put(new PrShAssignPlayer(), Role.SEER);
		playerMap.put(new PrShAssignPlayer(), Role.MEDIUM);
		playerMap.put(new PrShAssignPlayer(), Role.BODYGUARD);
		
//
//		learningPlayerMap.put(new NoActionPlayer(), Role.POSSESSED);
//		learningPlayerMap.put(new NoActionPlayer(), Role.WEREWOLF);
//		learningPlayerMap.put(new NoActionPlayer(), Role.WEREWOLF);
//		learningPlayerMap.put(new NoActionPlayer(), Role.WEREWOLF);
//		
		
		playerMap.put((Player) Class.forName(className).newInstance(), Role.POSSESSED);
		playerMap.put((Player) Class.forName(className).newInstance(), Role.WEREWOLF);
		playerMap.put((Player) Class.forName(className).newInstance(), Role.WEREWOLF);
		playerMap.put((Player) Class.forName(className).newInstance(), Role.WEREWOLF);
		
		changeLearningSelect(playerMap, Strategy.GREEDY);
		
		//ここにテストしたい自分のPlayerを指定してください．
//		Player player = new PrShAssignPlayer();
//		Player player = new KajiRoleAssignPlayer();
		
		/////////////////////////////////////////////
		//これ以降は変更しないでください．
		
//		Class<Player> pcls = (Class<Player>) player.getClass();
//		player = pcls.newInstance();

		int playNum = 0;
		long timePre = System.currentTimeMillis();
		
		DirectConnectServer gameServer = new DirectConnectServer(playerMap);
		GameSetting gameSetting = GameSetting.getDefaultGame(playerMap.size());
		AIWolfGame game = new AIWolfGame(gameSetting, gameServer);
		game.setShowConsoleLog(false);
		
		//10億回 = 1000 * 1000 * 1000
		for(int j = 0; j <= 1000 * 1000 * 1000; j++){
			for(Role requestRole:Role.values()){
				if(requestRole == Role.FREEMASON){
					continue;
				}
				
//				player = pcls.newInstance();
				
//				Map<Player, Role> playerMap = new HashMap<Player, Role>();
//				playerMap.put(player, requestRole);
//				for(int i = 0; i < 14; i++){
//					playerMap.put(new RandomPlayer(), null);
//				}
				

				game.setRand(new Random());
				game.start();
				playNum++;
				if(playNum % 100 == 0){
					long time = System.currentTimeMillis();
					System.out.println(playNum + "回：" + (float)(time - timePre)/1000f );
					timePre = time;
					if(playNum % 100 == 0){
						if(playNum % 200 == 0){
							changeLearningSelect(playerMap, Strategy.RANDOM);
							System.out.println("RANDOM_SELECTスタート");
						}else{
							changeLearningSelect(playerMap, Strategy.GREEDY);
							System.out.println("GREEDY_SELECTスタート");
						}
						System.out.println("アウトプット中");
						output(playerMap, playNum, dir);
						System.out.println("アウトプット完了");
					}
				}
				
			}
		}
		
		
		
		
		System.out.println(playNum);
		System.out.println((System.currentTimeMillis() - timePre));
	}
	
	private static void output(Map<Player, Role> learningPlayerMap, int playNum, String dir){
		
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

		String dir_num = dir + playNum + "/";
		boolean isVillagerFinished = false;
		for(Entry<Player, Role> set: learningPlayerMap.entrySet()){
			if(set.getValue() == Role.VILLAGER){
				if(isVillagerFinished){
					continue;
				}
				PrShAssignPlayer prshPlayer = (PrShAssignPlayer)set.getKey();
				PrShBasePlayer base = (PrShBasePlayer)prshPlayer.getVillagerPlayer();
				base.outputData(dir_num);
				isVillagerFinished = true;
			}else if(set.getValue() == Role.SEER){
				PrShAssignPlayer prshPlayer = (PrShAssignPlayer)set.getKey();
				PrShBasePlayer base = (PrShBasePlayer)prshPlayer.getSeerPlayer();
				base.outputData(dir_num);
			}else if(set.getValue() == Role.MEDIUM){
				PrShAssignPlayer prshPlayer = (PrShAssignPlayer)set.getKey();
				PrShBasePlayer base = (PrShBasePlayer)prshPlayer.getMediumPlayer();
				base.outputData(dir_num);
			}else if(set.getValue() == Role.BODYGUARD){
				PrShAssignPlayer prshPlayer = (PrShAssignPlayer)set.getKey();
				PrShBasePlayer base = (PrShBasePlayer)prshPlayer.getBodyguardPlayer();
				base.outputData(dir_num);
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
					continue;
					//break;
				}
				base.getLearningControler().setStrategy(str);
			}
		}
	}

}
