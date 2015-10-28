package jp.ac.aitech.k13009kk.aiwolf.server.bin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Player;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Team;
import org.aiwolf.common.net.GameSetting;
import org.aiwolf.common.util.CalendarTools;
import org.aiwolf.common.util.Pair;
import org.aiwolf.server.AIWolfGame;
import org.aiwolf.server.net.DirectConnectServer;
import org.aiwolf.server.net.GameServer;

/**
 * 繰り返し回数を指定して、人狼ゲームの実行ができるクラスです。</br>
 * </br>
 * <b>REPEAT_NUM</b> : 繰り返し回数
 *
 * @author keisuke  愛知工業大学 K13009 安藤圭祐
 *
 */
public class RoleRequestRepeatStarter {

	public static final int REPEAT_NUM = 1000;

	public static void main(String[] args)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, IOException {

		// 各陣営の勝利回数
		int villagerWinCount = 0;
		int werewolfWinCount = 0;
		//何回勝利陣営に入ったか
		Map<String, Integer> playerClassPointMap = new HashMap<String, Integer>();
		// 同じプレイヤークラスが何人参加しているか
		Map<String, Integer> playerClassNumMap = new HashMap<String, Integer>();
		// エージェントのリスト
		List<Agent> agentList = new ArrayList<Agent>();
		// プレイヤーリスト
		List<Pair<String, Role>> playerRoleList = new ArrayList<Pair<String, Role>>();
		// デフォルトプレイヤークラス
		String defaultClsName = Class.forName("org.aiwolf.client.base.smpl.SampleRoleAssignPlayer").getName();
		// 村の人数
		int playerNum = -1;
		// ログを保存するディレクトリ
		@SuppressWarnings("unused")
		String logDir = null;

		// 引数に対する処理
		for (int i = 0; i < args.length; i++) {
			if (args[i].startsWith("-")) {
				// ゲームに参加するプレイヤーを設定する
				if (args[i].equals("-c")) {
					// プレイヤークラス名を取得
					i++;
					String clsName = args[i];
					// 役職を取得
					i++;
					try {
						// 役職指定なし
						if ((args.length - 1 < i) || (args[i].startsWith("-"))) {
							i--;
							playerRoleList.add(new Pair<String, Role>(clsName, null));
						}
						// 役職指定あり
						else {
							Role role = Role.valueOf(args[i].toUpperCase());
							playerRoleList.add(new Pair<String, Role>(clsName, role));
						}
					} catch (IllegalArgumentException e) {
						System.err.println("No such role as " + args[i]);
						return;
					}
				}

				// 人数を設定する
				if (args[i].equals("-n")) {
					i++;
					playerNum = Integer.parseInt(args[i]);
				}
			}
			// プレイヤーが設定されていない
			if (playerNum < 0) {
				System.err.println("Usage:" + RoleRequestRepeatStarter.class
						+ " -n agentNum -c playerClass role [-c playerClass role...] [-t repeatGameNum] ");
				return;
			}
		}

		// ゲームを複数回実行
		for (int trial = 0; trial < REPEAT_NUM; trial++) {

			// プレイヤーと役職のマッピング
			Map<Player, Role> playerMap = new HashMap<Player, Role>();
			// 引数で指定したプレイヤーをマッピング
			for (Pair<String, Role> pair : playerRoleList) {
				Player player = (Player) Class.forName((String) pair.getKey()).newInstance();
				playerMap.put(player, (Role) pair.getValue());
			}
			// デフォルトプレイヤーをマッピング
			while (playerMap.size() < playerNum) {
				playerMap.put((Player) Class.forName(defaultClsName).newInstance(), null);
			}

			// ゲームサーバーを作成
			DirectConnectServer gameServer = new DirectConnectServer(playerMap);

			// ゲームを開始
			AIWolfGame game = start(playerMap, gameServer);

			// 各陣営の勝敗数をカウント
			Team winnerTeam = game.getWinner();
			if (winnerTeam == Team.VILLAGER) {
				villagerWinCount++;
			} else {
				werewolfWinCount++;
			}

			// エージェントの取得
			agentList = game.getGameData().getAgentList();

			// ポイントマップの初期化
			if (playerClassPointMap.isEmpty()) {
				for (Agent agent : agentList) {
					// エージェントのクラス
					String playerClass = gameServer.requestName(agent);
					playerClassPointMap.put(playerClass, 0);

					// 同じクラス名がいくつあるか数える
					if (!playerClassNumMap.containsKey(playerClass)) {
						playerClassNumMap.put(playerClass, 1);
					} else {
						playerClassNumMap.put(playerClass, playerClassNumMap.get(playerClass) + 1);
					}
				}
			}

			// 各エージェントのポイントをカウント
			for (Agent agent : agentList) {
				// エージェントのチーム
				Team agentTeam = game.getGameData().getRole(agent).getTeam();
				// エージェントのクラス
				String playerClass = gameServer.requestName(agent);
				// 勝利陣営に入っていたら加点
				if (agentTeam == winnerTeam) {
					playerClassPointMap.put(playerClass, playerClassPointMap.get(playerClass) + 1);
				}
			}
		}

		// 各陣営の勝利回数を表示
		System.out.println("============");
		System.out.println("VILLAGER\t" + villagerWinCount);
		System.out.println("WEREWOLF\t" + werewolfWinCount);
		// 各プレイヤーの勝利回数を表示
		System.out.println("============");
		System.out.println("NAME\t\tPOINT");
		for (String playerClass : playerClassPointMap.keySet()) {
			int point = playerClassPointMap.get(playerClass);
			int playerClassNum = playerClassNumMap.get(playerClass);
			double pointAve = point / playerClassNum;
			double winPercentage = (pointAve / REPEAT_NUM) * 100;
			System.out.println(playerClass + "\t" + winPercentage);
		}
		System.out.println("============");
	}

	/**
	 * playerMapにマッピングされたプレイヤー情報を元にゲームを開始する.ゲームのログをlogDirのディレクトリに保存する.
	 *
	 * @param playerMap
	 *            プレイヤーをキーに役職をマッピングしたハッシュマップ
	 * @param logDir
	 *            ログを出力するディレクトリのアドレス
	 * @return 人狼ゲームのオブジェクト
	 * @throws IOException ログファイルの入出力エラー
	 */
	public static AIWolfGame start(Map<Player, Role> playerMap, String logDir) throws IOException {
		// ログに付ける時間
		String timeString = CalendarTools.toDateTime(System.currentTimeMillis()).replaceAll("[\\s-/:]", "");

		DirectConnectServer gameServer = new DirectConnectServer(playerMap);
		GameSetting gameSetting = GameSetting.getDefaultGame(playerMap.size());
		AIWolfGame game = new AIWolfGame(gameSetting, gameServer);
		// logDirがnullでなければログを記録する
		if (logDir != null) {
			File logFile = new File(String.format("%s/contest%s.log", new Object[] { logDir, timeString }));
			game.setLogFile(logFile);
		}
		game.setRand(new Random());

		game.start();
		return game;
	}

	/**
	* playerMapにマッピングされたプレイヤー情報を元にゲームを開始する.ゲームのログをlogDirのディレクトリに保存する.
	*
	* @param playerMap
	*            プレイヤーをキーに役職をマッピングしたハッシュマップ
	* @param logDir
	*            ログを出力するディレクトリのアドレス
	* @return 人狼ゲームのオブジェクト
	*/
	public static AIWolfGame start(Map<Player, Role> playerMap, GameServer gameServer) {
		GameSetting gameSetting = GameSetting.getDefaultGame(playerMap.size());
		AIWolfGame game = new AIWolfGame(gameSetting, gameServer);
		game.setRand(new Random());

		game.start();
		return game;
	}

}
