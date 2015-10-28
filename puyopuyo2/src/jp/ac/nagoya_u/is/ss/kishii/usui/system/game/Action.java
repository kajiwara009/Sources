package jp.ac.nagoya_u.is.ss.kishii.usui.system.game;

import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.Puyo.PuyoDirection;


/**
 * 行動を決めるActionクラスです。
 */

public final class Action {
	/**
	 * 設置するぷよの向き
	 */
	private PuyoDirection direction;

	/**
	 * 設置するぷよの位置
	 */
	private int colmNumber;

	/**
	 * コンストラクタ
	 * (初期位置，(D0, 2))
	 */
	public Action() {
		direction = PuyoDirection.UP;
		colmNumber = 2;
	}

	/**
	 * コンストラクタ
	 * @param direction
	 * @param colmNumber
	 */
	public Action(PuyoDirection direction,int colmNumber) {
		this.direction = direction;
		this.colmNumber = colmNumber;
	}

	/**
	 * コンストラクタ
	 * (Puyoは向き情報のみ利用される)
	 * @param puyo
	 * @param colmNumber
	 */
	public Action(Puyo puyo, int colmNumber) {
		direction = puyo.getDirection();
		this.colmNumber = colmNumber;
	}

	/**
	 * 設置するぷよの向きを取得します。
	 * @return
	 */
	public PuyoDirection getDirection() {
		return direction;
	}

	/**
	 * 設置するぷよの向きを設定します。
	 * @param direction
	 */
	public void setDirection(PuyoDirection direction) {
		this.direction = direction;
	}

	/**
	 * 設置するぷよの位置を取得します。
	 * @return
	 */
	public int getColmNumber() {
		return colmNumber;
	}

	/**
	 * 設置するぷよの位置を設定します。
	 * @param colmNumber
	 */
	public void setColmNumber(int colmNumber) {
		this.colmNumber = colmNumber;
	}
}
