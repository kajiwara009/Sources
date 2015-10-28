package jp.ac.nagoya_u.is.ss.kishii.usui.system.game;

import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.GameInfo.PlayerNumber;

/**
 * プレイヤーの状態を表すクラスです。
 *
 */
public final class PlayerInfo {
	/**
	 * プレイヤーの状態を表す列挙型クラスです。
	 *
	 */
	public enum PlayerStatus {
		NORMAL,
		DEAD;
	}

	/**
	 * プレイヤーの名前
	 */
	private String playerName;

	/**
	 * プレイヤーの番号
	 */
	private PlayerNumber playerNumber;

	/**
	 * プレイヤーのID
	 */
	private int playerID;

	/**
	 * プレイヤーの状態
	 */
	private PlayerStatus status;

	/**
	 * 前のターンに行った連鎖数
	 * 0の時は消してない
	 */
	private int lastNumberOfChain;

	/**
	 * おじゃまぷよ計算の得点
	 */
	private int ojamaScore;

	/**
	 * 全消しボーナス
	 */
	private boolean isAllClearBonus;


	/**
	 * コンストラクタ
	 * @param playerName
	 * @param playerID
	 * @param directory
	 */
	public PlayerInfo(String playerName, int playerID) {
		this.playerName = playerName;
		this.playerID = playerID;

		status = PlayerStatus.NORMAL;
		lastNumberOfChain = 0;
		ojamaScore = 0;
		isAllClearBonus = false;
	}

	protected PlayerInfo(PlayerInfo playerInfo) {
		playerName = playerInfo.getPlayerName();
		playerID = playerInfo.getPlayerID();
		playerNumber = playerInfo.getPlayerNumber();
		status = playerInfo.getStatus();
		lastNumberOfChain = playerInfo.getLastNumberOfChain();
		ojamaScore = playerInfo.getOjamaScore();
		isAllClearBonus = playerInfo.isAllClearBonus();
	}

	/**
	 * プレイヤー情報をゲームの最初の状態に初期化します。
	 */
	protected void initPlayerInfo() {
		status = PlayerStatus.NORMAL;
		lastNumberOfChain = 0;
		ojamaScore = 0;
		isAllClearBonus = false;
	}

	/**
	 * プレイヤー名を取得します。
	 * @return
	 */
	public String getPlayerName() {
		return playerName;
	}

	/**
	 * プレイヤー番号を取得します。
	 * @return
	 */
	public PlayerNumber getPlayerNumber() {
		return playerNumber;
	}

	/**
	 * プレイヤー番号を設定します。
	 * @param playerNumber
	 */
	protected void setPlayerNumber(PlayerNumber playerNumber) {
		this.playerNumber = playerNumber;
	}

	/**
	 * プレイヤーIDを取得します。
	 * @return
	 */
	public int getPlayerID() {
		return playerID;
	}

	/**
	 * プレイヤーIDを設定します。
	 * @param playerID
	 */
	protected void setPlayerID(int playerID) {
		this.playerID = playerID;
	}

	/**
	 * プレイヤーの状態を取得します。
	 * @return
	 */
	public PlayerStatus getStatus() {
		return status;
	}

	/**
	 * プレイヤーの状態を設定します。
	 * @param status
	 */
	protected void setStatus(PlayerStatus status) {
		this.status = status;
	}

	/**
	 * 前ターンの連鎖数を取得します。
	 * この値が0の場合，前のターンはぷよを消去していないことになります。
	 * @return
	 */
	public int getLastNumberOfChain() {
		return lastNumberOfChain;
	}

	/**
	 * 前のターンの連鎖数を設定します。
	 * @param chainNumber
	 */
	protected void setLastNumberOfChain(int lastChainNumber) {
		this.lastNumberOfChain = lastChainNumber;
	}

	/**
	 * 現在のおじゃまぷよ計算の得点を取得します。
	 * プレイヤーが利用する際，このメソッドで得られる値は前回の連鎖の余り得点です。
	 * @return
	 */
	public int getOjamaScore() {
		return ojamaScore;
	}

	/**
	 * 現在のおじゃまぷよ計算の得点を設定します。
	 * @param ojamaScore
	 */
	protected void setOjamaScore(int ojamaScore) {
		this.ojamaScore = ojamaScore;
	}

	/**
	 * 全消しボーナスがあるかどうかを取得します。
	 * @return
	 */
	public boolean isAllClearBonus() {
		return isAllClearBonus;
	}

	/**
	 * 全消しボーナスがあるかどうかを設定します。
	 * @param isAllClearBonus
	 */
	protected void setAllClearBonus(boolean isAllClearBonus) {
		this.isAllClearBonus = isAllClearBonus;
	}

	public boolean equals(Object obj) {
		if (obj instanceof PlayerInfo) {
			PlayerInfo playerInfo = (PlayerInfo)obj;

			if (playerID == playerInfo.getPlayerID()) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public int hashCode() {
		return playerID;
	}
}
