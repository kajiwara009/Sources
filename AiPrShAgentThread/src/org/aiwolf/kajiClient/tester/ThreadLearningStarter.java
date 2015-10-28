package org.aiwolf.kajiClient.tester;

import ipa.myAgent.IPARoleAssignPlayer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import javax.swing.text.ChangedCharSetException;

import jinrou.lang.Group;
import jp.ac.shibaura_it.ma15082.WasabiPlayer;
import jp.ac.shibaura_it.ma15082.WasabiRoleAssignPlayer;
import jp.halfmoon.inaba.aiwolf.strategyplayer.StrategyPlayer;

import org.aiwolf.Satsuki.LearningPlayer.AIWolfMain;
import org.aiwolf.common.data.Player;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.net.GameSetting;
import org.aiwolf.iace10442.ChipRoleAssignPlayer;
import org.aiwolf.kajiClient.player.KajiRoleAssignPlayer;
import org.aiwolf.kajiPlayer.noAction.NoActionPlayer;
import org.aiwolf.kajiPlayer.profitSharing.PrShAssignPlayer;
import org.aiwolf.kajiPlayer.profitSharing.PrShBasePlayer;
import org.aiwolf.laern.lib.LearningControler;
import org.aiwolf.laern.lib.LearningPool;
import org.aiwolf.laern.lib.Observe;
import org.aiwolf.laern.lib.ObserveLearnResource;
import org.aiwolf.laern.lib.ObservePool;
import org.aiwolf.laern.lib.Situation;
import org.aiwolf.laern.lib.SituationLearnResource;
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
public class ThreadLearningStarter implements Runnable{

	private static Map<Role, SituationPool> sitPoolMap = new HashMap<Role, SituationPool>();
	private static Map<Role, ObservePool> obsPoolMap = new HashMap<Role, ObservePool>();
	private static String className = AIWolfMain.class.getName();
	private static LearningControler lc = new LearningControler();
	private static LearningPool learningPool;
	private static int loopPlayNum = 100;
	/**
	 * @param args
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws ClassNotFoundException 
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InstantiationException, IllegalAccessException, ClassNotFoundException, InterruptedException {
		//学習データを保存するディレクトリ，学習相手の設定
		System.out.println(className);
		String dir = "default_thread/";
		int threadNum = 10;
		
		int threadRepeatNum = 1000000;
		for(int i = 0; i < args.length; i++){
			if(args[i].equals("-d")){
				i++;
				dir = args[i];
				if(!dir.endsWith("/")){
					dir += "/";
				}
				System.out.println("学習データの保存場所：" + dir);
			}else if(args[i].equals("-c")){
				i++;
				try {
					Class c = Class.forName(args[i]);
					className = c.getName();
					System.out.println("対戦相手:" + c.getName());
				} catch (Exception e) {
					System.out.println("そんなクラスは存在しましぇーん:" + args[i]);
				}
			}else if(args[i].equals("-th")){
				i++;
				threadNum = Integer.parseInt(args[i]);
			}else if(args[i].equals("-h")){
				System.out.println("-d:データ保存ディレクトリ(thread版では無し), -c:対戦相手の絶対パス, -th:スレッド数");
				return;
			}
			
		}
		
		//複数スレッドの生成
		List<ThreadLearningStarter> starter = new ArrayList<ThreadLearningStarter>();
		for(int i = 0; i < threadNum; i++){
			starter.add(new ThreadLearningStarter());
		}
		
		makePools();
		System.out.println("free,total,time");
		//全スレッドを通した施行のループ
		for(int i = 0; i < threadRepeatNum; i++){
			resetLearningPool();
			List<Thread> threads = new ArrayList<Thread>();
			for(int starterIdx = 0; starterIdx < threadNum; starterIdx++){
				Thread thread = new Thread(starter.get(starterIdx));
				thread.start();
				
				threads.add(thread);
			}
			for(Thread thread: threads){
				thread.join();
			}
			learn();
			output(i, threadNum);
		}

//		Map<Player, Role> playerMap = makePlayerMap(className);
//		
//		changeLearningSelect(playerMap, Strategy.GREEDY);
//		
//		//ここにテストしたい自分のPlayerを指定してください．
////		Player player = new PrShAssignPlayer();
////		Player player = new KajiRoleAssignPlayer();
//		
//		/////////////////////////////////////////////
//		//これ以降は変更しないでください．
//		
////		Class<Player> pcls = (Class<Player>) player.getClass();
////		player = pcls.newInstance();
//
//		int playNum = 0;
//		long timePre = System.currentTimeMillis();
//		
//		//ゲームの設定
//		DirectConnectServer gameServer = new DirectConnectServer(playerMap);
//		GameSetting gameSetting = GameSetting.getDefaultGame(playerMap.size());
//		AIWolfGame game = new AIWolfGame(gameSetting, gameServer);
//		game.setShowConsoleLog(false);
//		
//		//ゲームのループ
//		//10億回 = 1000 * 1000 * 1000
//		for(int j = 0; j <= 1000 * 1000 * 1000; j++){
//			for(Role requestRole:Role.values()){
//				if(requestRole == Role.FREEMASON){
//					continue;
//				}
//				
////				player = pcls.newInstance();
//				
////				Map<Player, Role> playerMap = new HashMap<Player, Role>();
////				playerMap.put(player, requestRole);
////				for(int i = 0; i < 14; i++){
////					playerMap.put(new RandomPlayer(), null);
////				}
//				
//
//				game.setRand(new Random());
//				game.start();
//				playNum++;
//				if(playNum % 100 == 0){
//					long time = System.currentTimeMillis();
//					System.out.println(playNum + "回：" + (float)(time - timePre)/1000f );
//					timePre = time;
//					if(playNum % 100 == 0){
//						if(playNum % 200 == 0){
//							changeLearningSelect(playerMap, Strategy.RANDOM);
//							System.out.println("RANDOM_SELECTスタート");
//						}else{
//							changeLearningSelect(playerMap, Strategy.GREEDY);
//							System.out.println("GREEDY_SELECTスタート");
//						}
//						System.out.println("アウトプット中");
//						output(playerMap, playNum, dir);
//						System.out.println("アウトプット完了");
//					}
//				}
//				
//			}
//		}
//		System.out.println(playNum);
//		System.out.println((System.currentTimeMillis() - timePre));
	}
	
	private static void output(int threadLoopCount, int threadNum) {
		for(Entry<Role, SituationPool> set: sitPoolMap.entrySet()){
			set.getValue().outputSituations("threadLearn/" + className + "/" + ( (threadLoopCount+1) * loopPlayNum * threadNum), set.getKey());
		}
		for(Entry<Role, ObservePool> set: obsPoolMap.entrySet()){
			set.getValue().outputObserves("threadLearn/" + className + "/" + ( (threadLoopCount+1) * loopPlayNum * threadNum), set.getKey());
		}
	}

	private static void learn() {
		// TODO 自動生成されたメソッド・スタブ
		List<SituationLearnResource> slr = learningPool.getSitLearnRes();
		List<ObserveLearnResource> olr = learningPool.getObsLearnRes();
		//Situationの学習
		for(SituationLearnResource res: slr){
			Situation sit = res.getSituation();
			Object val = res.getActionValue();
			boolean isWin = res.isWin();
			sit.updateActionValue(val, isWin, lc);
		}

		//Observeの学習
		for(ObserveLearnResource res: olr){
			Observe obs = res.getObserve();
			Situation situation = res.getSituation();
			obs.updateSituationMap(situation);
			
		}
	}

	private static void resetLearningPool() {
		learningPool = new LearningPool();
	}

	private static void makePools() {
		Role[] roles = {Role.VILLAGER, Role.SEER, Role.MEDIUM, Role.BODYGUARD};
		for(Role role: roles){
			sitPoolMap.put(role, new SituationPool());
			obsPoolMap.put(role, new ObservePool());
		}
	}

	@Override
	public void run() {
		Map<Player, Role> playerMap = null;
		try {
			playerMap = makePlayerMap(className);
		} catch (InstantiationException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		
		changeLearningSelect(playerMap, Strategy.GREEDY);
		
		
		long timePre = System.currentTimeMillis();
		
		//ゲームの設定
		DirectConnectServer gameServer = new DirectConnectServer(playerMap);
		GameSetting gameSetting = GameSetting.getDefaultGame(playerMap.size());
		AIWolfGame game = new AIWolfGame(gameSetting, gameServer);
		game.setShowConsoleLog(false);
		
		//ゲームのループ
		for(int j = 0; j < loopPlayNum; j++){
			for(Role requestRole:Role.values()){
				if(requestRole == Role.FREEMASON){
					continue;
				}
			}
			game.setRand(new Random());
			game.start();
		}
		long timeAfter = System.currentTimeMillis();
		long time = System.currentTimeMillis() - timePre;
//		System.out.println(time/1000 + "." + time%1000);
		
		long free = Runtime.getRuntime().freeMemory();
		long total = Runtime.getRuntime().totalMemory();
		System.out.println(free + "," + total + "," + time/1000 + "." + time%1000);
	}

	private static Map<Player, Role> makePlayerMap(String className) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		Map<Player, Role> playerMap = new HashMap<Player, Role>();
		
//		ObservePool op = new ObservePool();
//		SituationPool sp = new SituationPool();
//		for(int i = 0; i < GameSetting.getDefaultGame(15).getRoleNumMap().get(Role.VILLAGER); i++){
//			PrShAssignPlayer player = new PrShAssignPlayer();
//			((PrShBasePlayer)( player.getVillagerPlayer())).setObservePool(op);
//			((PrShBasePlayer)( player.getVillagerPlayer())).setSituationPool(sp);;
//			playerMap.put(player, Role.VILLAGER);
//		}
//		playerMap.put(new PrShAssignPlayer(), Role.SEER);
//		playerMap.put(new PrShAssignPlayer(), Role.MEDIUM);
//		playerMap.put(new PrShAssignPlayer(), Role.BODYGUARD);
		
		List<Role> roles = new ArrayList<Role>();
		for(int i = 0; i < GameSetting.getDefaultGame(15).getRoleNumMap().get(Role.VILLAGER); i++){
			roles.add(Role.VILLAGER);
		}
		roles.add(Role.SEER);
		roles.add(Role.MEDIUM);
		roles.add(Role.BODYGUARD);
		
		for(Role role: roles){
			PrShAssignPlayer player = new PrShAssignPlayer();
			((PrShBasePlayer)( player.getRolePlayer(role))).setObservePool(obsPoolMap.get(role));
			((PrShBasePlayer)( player.getRolePlayer(role))).setSituationPool(sitPoolMap.get(role));
			((PrShBasePlayer)( player.getRolePlayer(role))).setLearningPool(learningPool);
			playerMap.put(player, role);
		}
		
		playerMap.put((Player) Class.forName(className).newInstance(), Role.POSSESSED);
		playerMap.put((Player) Class.forName(className).newInstance(), Role.WEREWOLF);
		playerMap.put((Player) Class.forName(className).newInstance(), Role.WEREWOLF);
		playerMap.put((Player) Class.forName(className).newInstance(), Role.WEREWOLF);

		return playerMap;
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

	public Map<Role, SituationPool> getSitPoolMap() {
		return sitPoolMap;
	}

	public void setSitPoolMap(Map<Role, SituationPool> sitPoolMap) {
		this.sitPoolMap = sitPoolMap;
	}

	public Map<Role, ObservePool> getObsPoolMap() {
		return obsPoolMap;
	}

	public void setObsPoolMap(Map<Role, ObservePool> obsPoolMap) {
		this.obsPoolMap = obsPoolMap;
	}

	public LearningPool getLearningPool() {
		return learningPool;
	}

	public void setLearningPool(LearningPool learningPool) {
		this.learningPool = learningPool;
	}


}
