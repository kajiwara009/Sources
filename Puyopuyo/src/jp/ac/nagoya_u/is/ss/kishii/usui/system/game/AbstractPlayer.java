package jp.ac.nagoya_u.is.ss.kishii.usui.system.game;

import java.util.List;

import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.GameInfo.PlayerNumber;



/**
 * AbstractPlayerです。
 * これを継承してPlayerを作成してください。
 *
 */
public abstract class AbstractPlayer {
	/**
	 * プレイヤー名
	 */
	private String playerName;

	/**
	 * ゲーム情報
	 */
	private GameInfo gameInfo;

	/**
	 * プレイヤー情報
	 */
	private PlayerNumber playerNumber;

	/**
	 * コンストラクタ
	 * @param playerName
	 * @param playerID
	 * @param directory
	 */
	public AbstractPlayer(String playerName) {
		this.playerName = playerName;
	}

	/**
	 * 自分のターン時に呼び出されます。
	 * @return
	 */
	public abstract Action doMyTurn();

	/**
	 * 1ゲームが始まる前に呼び出されます。
	 */
	public abstract void initialize();

	/**
	 * 1ゲームが終わった時点で呼び出されます。
	 */
	public abstract void inputResult();

	/**
	 * プレイヤー名を取得します。
	 * @return
	 */
	public final String getPlayerName() {
		return playerName;
	}

	/**
	 * 現在のゲーム情報を取得します。
	 * @return
	 */
	public final GameInfo getGameInfo() {
		return gameInfo;
	}

	/**
	 * 現在のゲーム情報を設定します。
	 * @param gameInfo
	 */
	final void setGameInfo(GameInfo gameInfo) {
		this.gameInfo = gameInfo;
	}

	/**
	 * プレイヤー番号を設定します。
	 *
	 * @param playerNumber
	 */
	final void setPlayerNumber(PlayerNumber playerNumber) {
		this.playerNumber = playerNumber;
	}

	/**
	 * 自分のプレイヤー情報を取得します。
	 * @return
	 */
	public final PlayerInfo getMyPlayerInfo() {
		return gameInfo.getPlayerInfo(playerNumber);
	}

	/**
	 * 相手プレイヤーのプレイヤー情報を取得します。
	 * @return
	 */
	public final PlayerInfo getEnemyPlayerInfo() {
		return gameInfo.getPlayerInfo(playerNumber.getEnemyNumber());
	}

	/**
	 * 自分のボードを取得します。
	 * @return
	 */
	public final Board getMyBoard() {
		return gameInfo.getBoard(getMyPlayerInfo());
	}

	/**
	 * 相手プレイヤーのボードを取得します。
	 * @return
	 */
	public final Board getEnemyBoard() {
		return gameInfo.getBoard(getEnemyPlayerInfo());
	}

	/**
	 * 指定された値から発生するおじゃまぷよ数を計算します。
	 * このメソッドでは1連鎖分の発生数を計算します。
	 * 例えばこのターンにぷよを置いた結果，3連鎖起こることが分かり，
	 * その連鎖でどれだけおじゃまぷよが発生するのか知りたい時，
	 * 3回各連鎖でのそれぞれの値をこのメソッドに渡し，
	 * 得られた値を合計することで連鎖全体での発生数を計算できます。
	 * それぞれの値というのは，例えば全消しボーナスの無い状態で，
	 * 3連鎖した時の2連鎖目で赤4つ青5つ同時に消したとすると，
	 * numberOfErasedPuyoが9（おじゃまぷよは無関係），
	 * numberOfChainが2
	 * numberOfColorが2
	 * numberOfConnectionListが(4, 5)
	 * isAllClearBonusがfalse
	 * となります。
	 * @param numberOfErasedPuyo 消去された色ぷよの数
	 * @param numberOfChain 連鎖数
	 * @param numberOfColor 同時に消えた色の数
	 * @param numbersOfConnectionList 各色毎の連結数（順番は無関係）
	 * @param isAllClearBonus 全消しボーナスの有無
	 * @return
	 */
	public static final int calculateNumberOfOjama(int numberOfErasedPuyo, int numberOfChain, int numberOfColor,
										List<Integer> numbersOfConnectionList, boolean isAllClearBonus) {
		int chainBonus = calculateChainBonus(numberOfChain);
		int colorBonus = calculateColorBonus(numberOfColor);
		int connectionBonus = 0;
		for (int numberOfConnection : numbersOfConnectionList) {
			connectionBonus += calculateConnectionBonus(numberOfConnection);
		}

		int bonus = chainBonus + colorBonus + connectionBonus;
		if (bonus == 0) {
			bonus = 1;
		}

		int ojamaScore = numberOfErasedPuyo * PuyoOperator.ERASE_RATE * bonus;

		if (isAllClearBonus && chainBonus == 1) {
			ojamaScore += PuyoOperator.ALL_CLEAR_BONUS;
		}

		return ojamaScore / PuyoOperator.OJAMA_RATE;
	}

	/**
	 * 連鎖ボーナスを計算します。
	 * @param numberOfChain
	 * @return
	 */
	private static final int calculateChainBonus(int numberOfChain) {
		switch (numberOfChain) {
		case 1:
			return 0;
		case 2:
			return 8;
		case 3:
			return 16;
		default :
			return (numberOfChain - 3) * 32;
		}
	}

	/**
	 * 多色ボーナスを計算します。
	 * @param numberOfColor
	 * @return
	 */
	private static final int calculateColorBonus(int numberOfColor) {
		switch (numberOfColor) {
		case 1:
			return 0;
		case 2:
			return 3;
		case 3:
			return 6;
		case 4:
			return 12;
		default :
			return 12;
		}
	}

	/**
	 * 多連結ボーナスを計算します。
	 * @param numberOfConnection
	 * @return
	 */
	private static final int calculateConnectionBonus(int numberOfConnection) {
		if (numberOfConnection <= 4) {
			return 0;
		} else if (numberOfConnection >= 11) {
			return 10;
		} else {
			return (numberOfConnection - 3);
		}
	}

}
