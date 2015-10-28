package jp.ac.nagoya_u.is.ss.kishii.usui.system.game;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.Puyo.PuyoDirection;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.storage.PuyoType;

/**
 * ゲーム情報を表すクラスです。
 */

public final class GameInfo {
	/**
	 * プレイヤー番号を表す列挙型クラスです
	 */
	public static enum PlayerNumber {
		ONE,
		TWO;

		/**
		 * 相手プレイヤーのプレイヤー番号を取得します
		 * @return 相手プレイヤーのプレイヤー番号
		 */
		public PlayerNumber getEnemyNumber() {
			if (this == ONE) {
				return TWO;
			} else {
				return ONE;
			}
		}

		/**
		 * 相手プレイヤーのプレイヤー番号を取得します
		 * @return 相手プレイヤーのプレイヤー番号
		 */
		public static PlayerNumber getEnemyNumber(PlayerNumber playerNumber) {
			if (playerNumber == PlayerNumber.ONE) {
				return TWO;
			} else {
				return ONE;
			}
		}
	}

	/**
	 * 各プレイヤーのボード
	 */
	private Map<PlayerNumber, Board> boardsMap;

	/**
	 * 各プレイヤーの
	 */
	private Map<PlayerNumber, PlayerInfo> playersInfoMap;

	/**
	 * 各プレイヤーの勝ち数
	 */
	private Map<PlayerNumber, Integer> numbersOfWinningMap;

	/**
	 * 現在のターン数
	 */
	private int turn;

	/**
	 * 現在のゲーム数
	 */
	private int gameCount;

	/**
	 * ゲームの設定値
	 */
	private PuyoPuyoSettingData settingData;

	private Random rnd;

	private boolean isTokoPuyo;

	/**
	 * コンストラクタ
	 * @param playersInfoMap
	 */
	public GameInfo(Map<PlayerNumber, PlayerInfo> playersInfoMap, PuyoPuyoSettingData settingData){
		this.playersInfoMap = playersInfoMap;

		this.settingData = settingData;

		rnd = new Random();

		boardsMap = new EnumMap<PlayerNumber, Board>(PlayerNumber.class);
		if (playersInfoMap.get(PlayerNumber.ONE) != null) {
			boardsMap.put(PlayerNumber.ONE, new Board(settingData));
		} else {
			boardsMap.put(PlayerNumber.ONE, null);
		}
		if (playersInfoMap.get(PlayerNumber.TWO) != null) {
			boardsMap.put(PlayerNumber.TWO, new Board(settingData));
		} else {
			boardsMap.put(PlayerNumber.TWO, null);
		}

		numbersOfWinningMap = new EnumMap<PlayerNumber, Integer>(PlayerNumber.class);
		numbersOfWinningMap.put(PlayerNumber.ONE, 0);
		numbersOfWinningMap.put(PlayerNumber.TWO, 0);

		turn = 0;
		gameCount = 0;

		isTokoPuyo = false;
	}

	/**
	 * コンストラクタ
	 * @param playersInfoMap
	 */
	public GameInfo(PlayerInfo playerInfo, PuyoPuyoSettingData settingData){
		playersInfoMap = new EnumMap<PlayerNumber, PlayerInfo>(PlayerNumber.class);
		playersInfoMap.put(PlayerNumber.ONE, playerInfo);

		this.settingData = settingData;

		rnd = new Random();

		boardsMap = new EnumMap<PlayerNumber, Board>(PlayerNumber.class);
		boardsMap.put(PlayerNumber.ONE, new Board(settingData));

		numbersOfWinningMap = new EnumMap<PlayerNumber, Integer>(PlayerNumber.class);
		numbersOfWinningMap.put(PlayerNumber.ONE, 0);

		turn = 0;
		gameCount = 0;

		isTokoPuyo = true;
	}

	protected GameInfo(GameInfo gameInfo) {
		playersInfoMap = new EnumMap<PlayerNumber, PlayerInfo>(PlayerNumber.class);
		boardsMap = new EnumMap<PlayerNumber, Board>(PlayerNumber.class);
		numbersOfWinningMap = new EnumMap<PlayerNumber, Integer>(PlayerNumber.class);
		if (!gameInfo.isTokoPuyo()) {
			for (PlayerNumber playerNumber : PlayerNumber.values()) {
				playersInfoMap.put(playerNumber, new PlayerInfo(gameInfo.getPlayerInfo(playerNumber)));
				boardsMap.put(playerNumber, new Board(gameInfo.getBoard(playerNumber)));
				numbersOfWinningMap.put(playerNumber, gameInfo.getNumberOfWinning(playerNumber));
			}
		} else {
			playersInfoMap.put(PlayerNumber.ONE, new PlayerInfo(gameInfo.getPlayerInfo(PlayerNumber.ONE)));
			boardsMap.put(PlayerNumber.ONE, new Board(gameInfo.getBoard(PlayerNumber.ONE)));
			numbersOfWinningMap.put(PlayerNumber.ONE, gameInfo.getNumberOfWinning(PlayerNumber.ONE));
		}

		turn = gameInfo.getTurn();
		gameCount = gameInfo.getGameCount();

		settingData = new PuyoPuyoSettingData(gameInfo.getSettingData());

		isTokoPuyo = gameInfo.isTokoPuyo;
	}

	/**
	 * 指定されたプレイヤー番号のプレイヤーのボードを取得します。
	 * @param playerNumber
	 * @return
	 */
	public Board getBoard(PlayerNumber playerNumber) {
		if (!isTokoPuyo) {
			return boardsMap.get(playerNumber);
		} else {
			if (playerNumber == PlayerNumber.ONE) {
				return boardsMap.get(playerNumber);
			} else {
				return null;
			}
		}
	}

	/**
	 * 指定されたプレイヤー情報を持つプレイヤーのボードを取得します。
	 * @param playerInfo
	 * @return
	 */
	public Board getBoard(PlayerInfo playerInfo) {
		if (!isTokoPuyo) {
			return boardsMap.get(playerInfo.getPlayerNumber());
		} else {
			if (playerInfo.getPlayerNumber() == PlayerNumber.ONE) {
				return boardsMap.get(playerInfo.getPlayerNumber());
			} else {
				return null;
			}
		}
	}

	/**
	 * 指定されたプレイヤー番号のプレイヤーのボードを設定します。
	 * @param board
	 * @param playerNumber
	 */
	protected void setBoard(Board board, PlayerNumber playerNumber) {
		if (!isTokoPuyo) {
			boardsMap.put(playerNumber, board);
		} else {
			if (playerNumber == PlayerNumber.ONE) {
				 boardsMap.put(playerNumber, board);
			} else {
				return;
			}
		}
	}

	/**
	 * 各プレイヤーのボードを持つMapを取得します。
	 * @return
	 */
	public Map<PlayerNumber, Board> getBoardsMap() {
		return boardsMap;
	}

	/**
	 * 各プレイヤー情報のMapを取得します。
	 * @return
	 */
	public Map<PlayerNumber, PlayerInfo> getPlayersInfoMap() {
		return playersInfoMap;
	}

	/**
	 * 指定されたプレイヤー番号のプレイヤーの情報を取得します。
	 * @param playerNumber
	 * @return
	 */
	public PlayerInfo getPlayerInfo(PlayerNumber playerNumber) {
		if (!isTokoPuyo) {
			return playersInfoMap.get(playerNumber);
		} else {
			if (playerNumber == PlayerNumber.ONE) {
				return playersInfoMap.get(playerNumber);
			} else {
				return null;
			}
		}
	}

	/**
	 * 指定されたプレイヤー番号のプレイヤーの勝ち数を取得します。
	 * @param playerNumber
	 * @return
	 */
	public int getNumberOfWinning(PlayerNumber playerNumber) {
		if (!isTokoPuyo) {
			return numbersOfWinningMap.get(playerNumber);
		} else {
			if (playerNumber == PlayerNumber.ONE) {
				return numbersOfWinningMap.get(playerNumber);
			} else {
				return 0;
			}
		}
	}

	/**
	 * 指定されたプレイヤー番号のプレイヤーの勝ち数を設定します。
	 * @param playerNumber
	 * @param winningNumber
	 */
	protected void setNumberOfWinning(PlayerNumber playerNumber, int numberOfWinning) {
		if (!isTokoPuyo) {
			numbersOfWinningMap.put(playerNumber, numberOfWinning);
		} else {
			if (playerNumber == PlayerNumber.ONE) {
				 numbersOfWinningMap.put(playerNumber, numberOfWinning);
			} else {
				return;
			}
		}
		numbersOfWinningMap.put(playerNumber, numberOfWinning);
	}

	/**
	 * 現在のターン数を取得します。
	 * @return
	 */
	public int getTurn() {
		return turn;
	}


	/**
	 * 現在のターン数を設定します。
	 * @param turn
	 */
	protected void setTurn(int turn) {
		this.turn = turn;
	}

	/**
	 * 現在のゲーム数を取得します。
	 * @return
	 */
	public int getGameCount() {
		return gameCount;
	}

	/**
	 * 現在のゲーム数を設定します。
	 * @param gameCount
	 */
	protected void setGameCount(int gameCount) {
		this.gameCount = gameCount;
	}

	/**
	 * ゲームの設定値を取得します。
	 * @return
	 */
	public PuyoPuyoSettingData getSettingData() {
		return settingData;
	}

	private boolean isTokoPuyo() {
		return isTokoPuyo;
	}

	/**
	 * 次のゲームに進む準備をします。
	 */
	protected void nextGame() {
		gameCount += 1;
		turn = 0;

		for (PlayerNumber playerNumber : PlayerNumber.values()) {
			if (!isTokoPuyo) {
				boardsMap.get(playerNumber).initBoard();
				playersInfoMap.get(playerNumber).initPlayerInfo();
			} else {
				boardsMap.get(PlayerNumber.ONE).initBoard();
				playersInfoMap.get(PlayerNumber.ONE).initPlayerInfo();
			}
		}
	}

	/**
	 * nextぷよとnextnextぷよを生成します。
	 * この関数はゲームの開始時のみ利用します。
	 */
	protected void makeNextPuyo() {
		Map<Puyo.PuyoNumber, PuyoType> puyoTypesMap =
				new HashMap<Puyo.PuyoNumber, PuyoType>();
		for (Puyo.PuyoNumber puyoNumber : Puyo.PuyoNumber.values()) {
			int color = rnd.nextInt(settingData.getNumberOfColors());
			puyoTypesMap.put(puyoNumber, makePuyoType(color));
		}
		Puyo nextPuyo = new Puyo(puyoTypesMap);

		Map<Puyo.PuyoNumber, PuyoType> puyoTypesMap2 =
				new HashMap<Puyo.PuyoNumber, PuyoType>();
		for (Puyo.PuyoNumber puyoNumber : Puyo.PuyoNumber.values()) {
			int color = rnd.nextInt(settingData.getNumberOfColors());
			while (makePuyoType(color) == nextPuyo.getPuyoType(puyoNumber)) {
				color = rnd.nextInt(settingData.getNumberOfColors());
			}

			puyoTypesMap2.put(puyoNumber, makePuyoType(color));
		}
		Puyo nextNextPuyo = new Puyo(puyoTypesMap2);

		for (PlayerNumber playerNumber : PlayerNumber.values()) {
			Board board = boardsMap.get(playerNumber);
			if (board != null) {
				board.setNextPuyo(new Puyo(nextPuyo));
				board.setNextNextPuyo(new Puyo(nextNextPuyo));
			}
		}
	}

	/**
	 * nextnextぷよを生成します。
	 */
	protected void makeNextNext() {
		Map<Puyo.PuyoNumber, PuyoType> puyoTypesMap =
				new HashMap<Puyo.PuyoNumber, PuyoType>();
		for (Puyo.PuyoNumber puyoNumber : Puyo.PuyoNumber.values()) {
			int color = rnd.nextInt(settingData.getNumberOfColors());
			puyoTypesMap.put(puyoNumber, makePuyoType(color));
		}

		for (PlayerNumber playerNumber : PlayerNumber.values()) {
			Board board = boardsMap.get(playerNumber);
			if (board != null) {
				Puyo currentPuyo = board.getNextPuyo();
				currentPuyo.setDirection(PuyoDirection.UP);
				board.setCurrentPuyo(currentPuyo);

				Puyo nextPuyo = board.getNextNextPuyo();
				nextPuyo.setDirection(PuyoDirection.UP);
				board.setNextPuyo(nextPuyo);

				board.setNextNextPuyo(new Puyo(puyoTypesMap));
			}
		}

	}

	/**
	 * int型の数値からPuyoColorを生成します。
	 * @param color
	 * @return
	 */
	private PuyoType makePuyoType(int color) {
		switch (color) {
		case 0:
			return PuyoType.BLUE_PUYO;
		case 1:
			return PuyoType.RED_PUYO;
		case 2:
			return PuyoType.GREEN_PUYO;
		case 3:
			return PuyoType.YELLOW_PUYO;
		case 4:
			return PuyoType.PURPLE_PUYO;
		default :
			return null;
		}
	}
}
