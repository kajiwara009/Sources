package jp.ac.nagoya_u.is.ss.kishii.usui.system.game;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.JPanel;

import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.GameInfo.PlayerNumber;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.PlayerInfo.PlayerStatus;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.Puyo.PuyoDirection;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.storage.FieldPoint;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.storage.PuyoType;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.viewer.BoardMaker;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.viewer.CenterPanel;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.viewer.PlayerNamePanel;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.viewer.PrefFrame;

/**
 * 実際にゲームを行うクラスです。 MainClassでこのクラスのインスタンスが作成され，ゲームが実行されます。
 */
public final class PuyoPuyo extends Thread {
	/**
	 * 各プレイヤー
	 */
	private Map<PlayerNumber, AbstractPlayer> playersMap;

	/**
	 * 各プレイヤーのオブジェクト C++プレイヤー用
	 */
	private Map<PlayerNumber, Object> playerObjectsMap;

	/**
	 * 各プレイヤーがC++プレイヤーかどうか
	 */
	private Map<PlayerNumber, Boolean> isCppMap;

	/**
	 * ぷよぷよのいろんな命令クラス
	 */
	private PuyoOperator puyoOperator;

	/**
	 * ゲームの情報
	 */
	private GameInfo gameInfo;

	/**
	 * ゲームの設定値
	 */
	private PuyoPuyoSettingData settingData;

	/**
	 * GUI
	 */
	private PrefFrame frm;
	private Map<PlayerNumber, String> imageDirectoryMap;

	private Action action;

	public PuyoPuyo(AbstractPlayer player) {
		tokoPuyoConstructor(player, null);
	}

	public PuyoPuyo(AbstractPlayer player, String playerImageDirectory) {
		tokoPuyoConstructor(player, playerImageDirectory);
	}

	public PuyoPuyo(AbstractPlayer player1, AbstractPlayer player2) {
		puyoPuyoConstructor(player1, null, player2, null);
	}

	public PuyoPuyo(AbstractPlayer player1, String player1ImageDirectory,
			AbstractPlayer player2, String player2ImageDirectory) {
		puyoPuyoConstructor(player1, player1ImageDirectory, player2,
				player2ImageDirectory);
	}

	private void initField() {
		settingData = new PuyoPuyoSettingData();
		isCppMap = new EnumMap<PlayerNumber, Boolean>(PlayerNumber.class);
		playerObjectsMap = new EnumMap<PlayerNumber, Object>(PlayerNumber.class);
		playersMap = new EnumMap<PlayerNumber, AbstractPlayer>(
				PlayerNumber.class);
		imageDirectoryMap = new EnumMap<PlayerNumber, String>(
				PlayerNumber.class);
		puyoOperator = new PuyoOperator(settingData.getWeight(),
				settingData.isMotion());
		frm = new PrefFrame("PuyoPuyo", settingData.getDisplayHeight());
		action = null;
	}

	private void tokoPuyoConstructor(AbstractPlayer player,
			String playerImageDirectory) {
		initField();

		if (!makePlayerObject(player, PlayerNumber.ONE)) {
			System.err.print("PlayerObjectError");
			System.exit(0);
		}
		playerObjectsMap.put(PlayerNumber.TWO, null);
		isCppMap.put(PlayerNumber.TWO, false);
		playersMap.put(PlayerNumber.ONE, player);
		playersMap.put(PlayerNumber.TWO, null);

		String playerName = getPlayerName(PlayerNumber.ONE);
		int playerID = 0;

		PlayerInfo playerInfo = new PlayerInfo(playerName, playerID);
		playerInfo.setPlayerNumber(PlayerNumber.ONE);

		gameInfo = new GameInfo(playerInfo, settingData);

		JPanel mainPanel = new JPanel();
		frm.mainPanel(mainPanel);

		if (playerImageDirectory == null) {
			imageDirectoryMap.put(PlayerNumber.ONE, "image");
		} else {
			imageDirectoryMap.put(PlayerNumber.ONE, playerImageDirectory);
		}

		BoardMaker boardMaker = new BoardMaker(settingData.getWidth(),
				settingData.getHeight(), playerInfo,
				imageDirectoryMap.get(PlayerNumber.ONE));
		gameInfo.getBoard(playerInfo).setBoardMaker(boardMaker);

		mainPanel.add(boardMaker);

		frm.setVisible(true);
	}

	private void puyoPuyoConstructor(AbstractPlayer player1,
			String player1ImageDirectory, AbstractPlayer player2,
			String player2ImageDirectory) {
		initField();

		if (!makePlayerObject(player1, PlayerNumber.ONE)
				|| !makePlayerObject(player2, PlayerNumber.TWO)) {
			System.err.print("PlayerObjectError");
			System.exit(0);
		}

		playersMap.put(PlayerNumber.ONE, player1);
		playersMap.put(PlayerNumber.TWO, player2);

		imageDirectoryMap.put(PlayerNumber.ONE, player1ImageDirectory);
		imageDirectoryMap.put(PlayerNumber.TWO, player2ImageDirectory);

		Map<PlayerNumber, PlayerInfo> playersInfoMap = new EnumMap<PlayerNumber, PlayerInfo>(
				PlayerNumber.class);
		for (PlayerNumber playerNumber : PlayerNumber.values()) {
			String playerName = getPlayerName(playerNumber);
			int playerID;
			if (playerNumber == PlayerNumber.ONE) {
				playerID = 0;
			} else {
				playerID = 1;
			}

			PlayerInfo playerInfo = new PlayerInfo(playerName, playerID);
			playersInfoMap.put(playerNumber, playerInfo);
			playerInfo.setPlayerNumber(playerNumber);
		}
		gameInfo = new GameInfo(playersInfoMap, settingData);

		JPanel mainPanel = new JPanel();
		frm.mainPanel(mainPanel);

		for (PlayerNumber playerNumber : PlayerNumber.values()) {
			if (playersMap.get(playerNumber) != null) {
				if (imageDirectoryMap.get(playerNumber) == null) {
					imageDirectoryMap.put(playerNumber, "image");
				}

				BoardMaker boardMaker = new BoardMaker(settingData.getWidth(),
						settingData.getHeight(),
						playersInfoMap.get(playerNumber),
						imageDirectoryMap.get(playerNumber));
				gameInfo.getBoard(playerNumber).setBoardMaker(boardMaker);

				mainPanel.add(boardMaker);
			}
		}

		frm.setVisible(true);
	}

	public PlayerNumber puyoPuyo() {
		int maxNumberOfWinning = settingData.getMaxNumberOfWinning();

		setPlayerNumber(PlayerNumber.ONE);
		setPlayerNumber(PlayerNumber.TWO);

		while (gameInfo.getNumberOfWinning(PlayerNumber.ONE) < maxNumberOfWinning
				&& gameInfo.getNumberOfWinning(PlayerNumber.TWO) < maxNumberOfWinning) {
			BoardMaker boardMaker1 = gameInfo.getBoard(PlayerNumber.ONE)
					.getBoardMaker();
			BoardMaker boardMaker2;
			if (gameInfo.getBoard(PlayerNumber.TWO) != null) {
				boardMaker2 = gameInfo.getBoard(PlayerNumber.TWO)
						.getBoardMaker();
			} else {
				boardMaker2 = null;
			}

			oneGame();

			addNumberOfWinning();
			if (boardMaker1 != null) {
				boardMaker1.getCenter().putScore(
						gameInfo.getNumberOfWinning(PlayerNumber.ONE));
			}
			if (boardMaker2 != null) {
				boardMaker2.getCenter().putScore(
						gameInfo.getNumberOfWinning(PlayerNumber.TWO));
			}
			gameInfo.nextGame();
			if (gameInfo.getBoard(PlayerNumber.TWO) != null) {
				for (PlayerNumber playerNumber : PlayerNumber.values()) {
					frm.getConsolePanel()
							.addConsole(
									getPlayerName(playerNumber)
											+ " win "
											+ gameInfo
													.getNumberOfWinning(playerNumber));
				}
			}
		}

		if (gameInfo.getBoard(PlayerNumber.TWO) != null) {
			if (gameInfo.getNumberOfWinning(PlayerNumber.ONE) >= maxNumberOfWinning) {
				System.out.println(getPlayerName(PlayerNumber.ONE));
				gameInfo.getBoard(PlayerNumber.TWO)
						.getBoardMaker()
						.getPanel()
						.deadEffect(
								gameInfo.getBoard(PlayerNumber.TWO).getField());
				frm.getConsolePanel().addConsole(
						"WINNER is " + getPlayerName(PlayerNumber.ONE));
				// frm.setVisible(false);
				return PlayerNumber.ONE;
			} else {
				System.out.println(getPlayerName(PlayerNumber.TWO));
				gameInfo.getBoard(PlayerNumber.ONE)
						.getBoardMaker()
						.getPanel()
						.deadEffect(
								gameInfo.getBoard(PlayerNumber.TWO).getField());
				frm.getConsolePanel().addConsole(
						"WINNER is " + getPlayerName(PlayerNumber.TWO));
				// frm.setVisible(false);
				return PlayerNumber.TWO;
			}
		}
		return PlayerNumber.ONE;
	}

	private void initializePlayer() {
		for (final PlayerNumber playerNumber : PlayerNumber.values()) {
			new Thread() {
				public void run() {
					initialize(playerNumber);
				}

				public synchronized void start() {
					super.start();
					try {
						join(settingData.getTimeLimit());// 制限時間
					} catch (InterruptedException e) {
					}
					if (isAlive()) {
						interrupt();
						System.out.println(getPlayerName(playerNumber));
						System.out.println("Initialise Time Out");
					}
				}
			}.start();
		}
	}

	private void inputResultPlayer() {
		for (final PlayerNumber playerNumber : PlayerNumber.values()) {
			new Thread() {
				public void run() {
					inputResult(playerNumber);
				}

				public synchronized void start() {
					super.start();
					try {
						join(settingData.getTimeLimit());// 制限時間
					} catch (InterruptedException e) {
					}
					if (isAlive()) {
						interrupt();
						System.out.println(getPlayerName(playerNumber));
						System.out.println("Initialize Time Out");
					}
				}
			}.start();
		}
	}

	private static boolean isStop = false;

	public static void setIsStop(boolean stop) {
		isStop = stop;
	}

	private void oneGame() {
		for (PlayerNumber playerNumber : PlayerNumber.values()) {
			if (playersMap.get(playerNumber) != null) {
				Board board = gameInfo.getBoard(playerNumber);
				board.getBoardMaker().getPanel().fieldRefresh(board.getField());
				board.getBoardMaker().getOjama()
						.ojamaDisplay(board.getNumbersOfOjamaList());
			}
		}

		initializePlayer();

		gameInfo.makeNextPuyo();
		for (PlayerNumber playerNumber : PlayerNumber.values()) {
			if (playersMap.get(playerNumber) != null) {
				Board board = gameInfo.getBoard(playerNumber);

				CenterPanel center = board.getBoardMaker().getCenter();
				center.getNextPanel().putNext(board.getNextPuyo());
				center.getNextNextPanel().putNextNext(board.getNextNextPuyo());
			}
		}
		Wait.wait(gameInfo.getSettingData().getWeight() * 2.0);

		while (!checkEndGame()) {
			oneTurn();

			gameInfo.setTurn(gameInfo.getTurn() + 1);
			if (isStop) {
				buttonStop();
			}
		}

		inputResultPlayer();

	}

	private void buttonStop() {
		while (isStop) {
			timer(100);
		}
		return;
	}

	private void timer(int milsec) {
		long start = System.currentTimeMillis();
		long stop = System.currentTimeMillis();
		while (stop - start < milsec) {
			stop = System.currentTimeMillis();
		}
	}

	private void oneTurn() {
		renewPuyoInfo();

		for (PlayerNumber playerNumber : PlayerNumber.values()) {
			setGameInfo(playerNumber);
		}

		executeAction();

		ojamaPuyo();
	}

	private void renewPuyoInfo() {
		gameInfo.makeNextNext();
		for (PlayerNumber playerNumber : PlayerNumber.values()) {
			if (playersMap.get(playerNumber) != null) {
				Board board = gameInfo.getBoard(playerNumber);

				CenterPanel center = board.getBoardMaker().getCenter();
				center.getNextPanel().putNext(board.getNextPuyo());
				center.getNextNextPanel().putNextNext(board.getNextNextPuyo());
			}
		}
	}

	private void executeAction() {
		Map<PlayerNumber, Action> actionsMap = new EnumMap<PlayerNumber, Action>(
				PlayerNumber.class);
		decidePlayersAction(actionsMap);

		putPuyo(actionsMap);
		gameInfo.getPlayerInfo(PlayerNumber.ONE).setLastNumberOfChain(0);
		if (gameInfo.getPlayerInfo(PlayerNumber.TWO) != null) {
			gameInfo.getPlayerInfo(PlayerNumber.TWO).setLastNumberOfChain(0);
		}

		Map<PlayerNumber, Map<FieldPoint, PuyoType>> erasedPuyoPointsMapMap = new EnumMap<GameInfo.PlayerNumber, Map<FieldPoint, PuyoType>>(
				PlayerNumber.class);
		Map<PlayerNumber, Set<FieldPoint>> erasedOjamaPuyoPointsSetMap = new EnumMap<GameInfo.PlayerNumber, Set<FieldPoint>>(
				PlayerNumber.class);

		int player1EraseNum = 0;
		int player2EraseNum = 0;

		for (;;) {
			prepareErase(erasedPuyoPointsMapMap, erasedOjamaPuyoPointsSetMap);
			if (erasedPuyoPointsMapMap.get(PlayerNumber.ONE).isEmpty()
					&& erasedPuyoPointsMapMap.get(PlayerNumber.TWO).isEmpty()) {
				break;
			}
			player1EraseNum++;
			player2EraseNum++;
			if (!erasedPuyoPointsMapMap.get(PlayerNumber.ONE).isEmpty()) {
				coment(PlayerNumber.ONE, player1EraseNum);
			} else if (!erasedPuyoPointsMapMap.get(PlayerNumber.TWO).isEmpty()) {
				coment(PlayerNumber.TWO, player2EraseNum);
			}

			Map<PlayerNumber, Integer> numbersOfOjamaMap = puyoOperator
					.eraseBoards(gameInfo.getBoardsMap(),
							gameInfo.getPlayersInfoMap(),
							erasedPuyoPointsMapMap, erasedOjamaPuyoPointsSetMap);

			if (playersMap.get(PlayerNumber.ONE) != null
					&& playersMap.get(PlayerNumber.TWO) != null) {
				for (PlayerNumber playerNumber : PlayerNumber.values()) {
					int numberOfOjama = numbersOfOjamaMap.get(playerNumber);
					int restNumberOfOjama = gameInfo.getBoard(playerNumber)
							.sousai(numberOfOjama);
					gameInfo.getBoard(playerNumber.getEnemyNumber())
							.storeOjamaPuyo(restNumberOfOjama);
				}
				Board board1 = gameInfo.getBoard(PlayerNumber.ONE);
				Board board2 = gameInfo.getBoard(PlayerNumber.TWO);
				board1.getBoardMaker().getOjama()
						.ojamaDisplay(board1.getNumbersOfOjamaList());
				board2.getBoardMaker().getOjama()
						.ojamaDisplay(board2.getNumbersOfOjamaList());
			}
		}
		if (player1EraseNum != 0) {
			frm.getConsolePanel().addConsole(
					playersMap.get(PlayerNumber.ONE).getPlayerName() + "："
							+ player1EraseNum + "連鎖");
		} else if (player2EraseNum != 0) {
			frm.getConsolePanel().addConsole(
					playersMap.get(PlayerNumber.TWO).getPlayerName() + "："
							+ player2EraseNum + "連鎖");
		}
	}

	private void coment(PlayerNumber playerNumber, int eraseNum) {
		switch (eraseNum) {
		case 1:
			frm.getConsolePanel().addConsole(
					playersMap.get(playerNumber).getPlayerName() + "：えい");
			break;
		case 2:
			frm.getConsolePanel().addConsole(
					playersMap.get(playerNumber).getPlayerName() + "：ファイアー");
			break;
		case 3:
			frm.getConsolePanel().addConsole(
					playersMap.get(playerNumber).getPlayerName() + "：アイスストーム");
			break;
		case 4:
			frm.getConsolePanel().addConsole(
					playersMap.get(playerNumber).getPlayerName() + "：ダイアキュート");
			break;
		case 5:
			frm.getConsolePanel().addConsole(
					playersMap.get(playerNumber).getPlayerName() + "：グレインダブド");
			break;
		case 6:
			frm.getConsolePanel().addConsole(
					playersMap.get(playerNumber).getPlayerName() + "：ジュゲム");
			break;
		default:
			frm.getConsolePanel().addConsole(
					playersMap.get(playerNumber).getPlayerName() + "：バイオウェーン");
			break;
		}

	}

	private void decidePlayersAction(Map<PlayerNumber, Action> ActionsMap) {
		for (final PlayerNumber playerNumber : PlayerNumber.values()) {
			action = null;

			// 別スレッドによる制限時間の測定と実行
			new Thread() {
				public void run() {
					action = doMyTurn(playerNumber);
				}

				public synchronized void start() {
					super.start();
					try {
						join(settingData.getTimeLimit());// 制限時間
					} catch (InterruptedException e) {
					}
					if (isAlive()) {
						interrupt();
						System.out.println(getPlayerName(playerNumber));
						System.out.println("DoMyTurn Time Out");

						action = null;
					}
				}
			}.start();

			if (action != null) {
				ActionsMap.put(playerNumber, new Action(action.getDirection(),
						action.getColmNumber()));
			} else {
				ActionsMap.put(playerNumber, new Action(PuyoDirection.UP, 2));
			}
		}
	}

	private void putPuyo(Map<PlayerNumber, Action> actionsMap) {
		Map<PlayerNumber, Board> boardsMap = gameInfo.getBoardsMap();
		Map<PlayerNumber, Integer> colmNumbersMap = new EnumMap<PlayerNumber, Integer>(
				PlayerNumber.class);
		for (PlayerNumber playerNumber : PlayerNumber.values()) {
			if (playersMap.get(playerNumber) != null) {
				Board board = boardsMap.get(playerNumber);
				PuyoDirection direction = actionsMap.get(playerNumber)
						.getDirection();
				int colmNumber = actionsMap.get(playerNumber).getColmNumber();

				if (board.getField().isEnable(direction, colmNumber)) {
					board.getCurrentPuyo().setDirection(direction);
					colmNumbersMap.put(playerNumber, colmNumber);
				} else {
					board.getCurrentPuyo().setDirection(PuyoDirection.UP);
					colmNumbersMap.put(playerNumber, 2);
				}
			}
		}

		puyoOperator.fallCurrentPuyo(boardsMap, colmNumbersMap);
	}

	private void prepareErase(
			Map<PlayerNumber, Map<FieldPoint, PuyoType>> erasedPuyoPointsMapMap,
			Map<PlayerNumber, Set<FieldPoint>> erasedOjamaPuyoPointsSetMap) {
		for (PlayerNumber playerNumber : PlayerNumber.values()) {
			if (playersMap.get(playerNumber) != null) {
				Field field = gameInfo.getBoard(playerNumber).getField();
				PlayerInfo playerInfo = gameInfo.getPlayerInfo(playerNumber);

				Map<FieldPoint, PuyoType> erasedPuyoPointsMap = new HashMap<FieldPoint, PuyoType>();
				Set<FieldPoint> erasedOjamaPuyoPointsSet = new HashSet<FieldPoint>();
				puyoOperator.searchErasedPuyo(field, playerInfo,
						erasedPuyoPointsMap, erasedOjamaPuyoPointsSet);

				erasedPuyoPointsMapMap.put(playerNumber, erasedPuyoPointsMap);
				erasedOjamaPuyoPointsSetMap.put(playerNumber,
						erasedOjamaPuyoPointsSet);
			} else {
				erasedPuyoPointsMapMap.put(playerNumber,
						new HashMap<FieldPoint, PuyoType>());
				erasedOjamaPuyoPointsSetMap.put(playerNumber,
						new HashSet<FieldPoint>());
			}
		}
	}

	private void ojamaPuyo() {
		for (PlayerNumber playerNumber : PlayerNumber.values()) {
			if (playersMap.get(playerNumber) != null) {
				Board board = gameInfo.getBoard(playerNumber);
				puyoOperator.fallOjamaPuyo(board);

				board.getBoardMaker().getOjama()
						.ojamaDisplay(board.getNumbersOfOjamaList());
			}
		}
	}

	private boolean checkEndGame() {
		boolean isEnd = false;

		for (PlayerNumber playerNumber : PlayerNumber.values()) {
			if (isDead(playerNumber)) {
				gameInfo.getPlayerInfo(playerNumber).setStatus(
						PlayerStatus.DEAD);
				isEnd = true;
			}
		}

		return isEnd;
	}

	private void addNumberOfWinning() {
		if (playersMap.get(PlayerNumber.TWO) == null) {
			PlayerStatus onePlayerStatus = gameInfo.getPlayerInfo(
					PlayerNumber.ONE).getStatus();
			if (onePlayerStatus == PlayerStatus.DEAD) {
				int numberOfWinnning = gameInfo
						.getNumberOfWinning(PlayerNumber.ONE);
				gameInfo.setNumberOfWinning(PlayerNumber.ONE,
						numberOfWinnning + 1);
			}
		} else {
			PlayerStatus onePlayerStatus = gameInfo.getPlayerInfo(
					PlayerNumber.ONE).getStatus();
			PlayerStatus twoPlayerStatus = gameInfo.getPlayerInfo(
					PlayerNumber.TWO).getStatus();
			if (onePlayerStatus != PlayerStatus.DEAD
					&& twoPlayerStatus == PlayerStatus.DEAD) {
				int numberOfWinnning = gameInfo
						.getNumberOfWinning(PlayerNumber.ONE);
				gameInfo.setNumberOfWinning(PlayerNumber.ONE,
						numberOfWinnning + 1);
			} else if (onePlayerStatus == PlayerStatus.DEAD
					&& twoPlayerStatus != PlayerStatus.DEAD) {
				int numberOfWinnning = gameInfo
						.getNumberOfWinning(PlayerNumber.TWO);
				gameInfo.setNumberOfWinning(PlayerNumber.TWO,
						numberOfWinnning + 1);
			}
		}
	}

	private boolean isDead(PlayerNumber playerNumber) {
		Field field;
		Board board = gameInfo.getBoard(playerNumber);
		if (board != null) {
			field = board.getField();
		} else {
			field = null;
		}
		if (field != null && field.isDead()) {
			return true;
		} else {
			return false;
		}
	}

	private Boolean makePlayerObject(AbstractPlayer player,
			PlayerNumber playerNumber) {
		if (player.getClass() == CppPlayer.class) {
			isCppMap.put(playerNumber, true);
			URL[] urls = new URL[1];
			try {
				// ////ここのURLのjarファイル名と実際のjarファイル名を一致させる/////
				// ///////jarファイルはプロジェクトフォルダのルートに置くこと////////
				urls[0] = new URL("jar:file:puyopuyoVer.jar!/");
				// //////////////////////////////////////////////////////////////////
				CppClassLoader cppClassLoader = new CppClassLoader(urls);
				Class<?> clazz = cppClassLoader
						.findClass("jp.ac.nagoya_u.is.ss.kishii.usui.system.game.CppPlayer");
				Class<?>[] types = { String.class, String.class };
				Constructor<?> constructor = clazz.getConstructor(types);
				String libraryName = (String) player.getClass()
						.getMethod("getLibraryName").invoke(player);
				Object[] args = { player.getPlayerName(), libraryName };
				Object playerObject = constructor.newInstance(args);
				Method cppLoadLibrary = clazz.getMethod("loadLibrary");
				cppLoadLibrary.invoke(playerObject);

				playerObjectsMap.put(playerNumber, playerObject);
				return true;
			} catch (MalformedURLException e) {
				e.printStackTrace();
				return false;
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				return false;
			} catch (SecurityException e) {
				e.printStackTrace();
				return false;
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
				return false;
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				return false;
			} catch (InstantiationException e) {
				e.printStackTrace();
				return false;
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				return false;
			} catch (InvocationTargetException e) {
				e.printStackTrace();
				return false;
			}

		} else {
			isCppMap.put(playerNumber, false);
			playerObjectsMap.put(playerNumber, player);

			return true;
		}
	}

	private void setPlayerNumber(PlayerNumber playerNumber) {
		if (playersMap.get(playerNumber) == null) {
			return;
		} else if (isCppMap.get(playerNumber)) {
			Object playerObject = playerObjectsMap.get(playerNumber);
			try {
				Class<?>[] types = { PlayerNumber.class };
				Method playerSetGameInfo = playerObject.getClass().getMethod(
						"setPlayerNumberToCpp", types);
				playerSetGameInfo.invoke(playerObject, playerNumber);
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		} else {
			playersMap.get(playerNumber).setPlayerNumber(playerNumber);
		}
	}

	private void setGameInfo(PlayerNumber playerNumber) {
		if (playersMap.get(playerNumber) == null) {
			return;
		} else if (isCppMap.get(playerNumber)) {
			Object playerObject = playerObjectsMap.get(playerNumber);
			try {
				Class<?>[] types = { GameInfo.class };
				Method playerSetGameInfo = playerObject.getClass().getMethod(
						"setGameInfoToCpp", types);
				playerSetGameInfo.invoke(playerObject, gameInfo);
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		} else {
			playersMap.get(playerNumber).setGameInfo(new GameInfo(gameInfo));
		}
	}

	private String getPlayerName(PlayerNumber playerNumber) {
		if (playersMap.get(playerNumber) == null) {
			return null;
		} else if (isCppMap.get(playerNumber)) {
			Object playerObject = playerObjectsMap.get(playerNumber);
			try {
				Method playerGetPlayerName = playerObject.getClass().getMethod(
						"getPlayerName");
				return (String) playerGetPlayerName.invoke(playerObject);
			} catch (SecurityException e) {
				e.printStackTrace();
				return null;
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
				return null;
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				return null;
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				return null;
			} catch (InvocationTargetException e) {
				e.printStackTrace();
				return null;
			}
		} else {
			return playersMap.get(playerNumber).getPlayerName();
		}
	}

	private void initialize(PlayerNumber playerNumber) {
		if (playersMap.get(playerNumber) == null) {
			return;
		} else if (isCppMap.get(playerNumber)) {
			Object playerObject = playerObjectsMap.get(playerNumber);
			try {
				Method playerInitialize = playerObject.getClass().getMethod(
						"initialize");
				playerInitialize.invoke(playerObject);
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		} else {
			playersMap.get(playerNumber).initialize();
		}
	}

	private void inputResult(PlayerNumber playerNumber) {
		if (playersMap.get(playerNumber) == null) {
			return;
		} else if (isCppMap.get(playerNumber)) {
			Object playerObject = playerObjectsMap.get(playerNumber);
			try {
				Method playerInputResult = playerObject.getClass().getMethod(
						"inputResult");
				playerInputResult.invoke(playerObject);
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		} else {
			playersMap.get(playerNumber).inputResult();
		}
	}

	private Action doMyTurn(PlayerNumber playerNumber) {
		if (playersMap.get(playerNumber) == null) {
			return null;
		} else if (isCppMap.get(playerNumber)) {
			Object playerObject = playerObjectsMap.get(playerNumber);
			try {
				Method playerGetPlayerName = playerObject.getClass().getMethod(
						"doMyTurn");
				return (Action) playerGetPlayerName.invoke(playerObject);
			} catch (SecurityException e) {
				e.printStackTrace();
				return null;
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
				return null;
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				return null;
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				return null;
			} catch (InvocationTargetException e) {
				e.printStackTrace();
				return null;
			}
		} else {
			return playersMap.get(playerNumber).doMyTurn();
		}
	}
}
