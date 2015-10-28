package jp.ac.nagoya_u.is.ss.kishii.usui.system.game;

import java.util.EnumMap;
import java.util.Map;

import jp.ac.nagoya_u.is.ss.kishii.usui.system.storage.PuyoType;


/**
 * 降ってくるぷよを表すクラスです。
 * 2つのぷよから構成されます。
 *
 */
public final class Puyo {
	/**
	 * 2つのぷよの番号を表す列挙型クラスです。
	 *
	 */
	public static enum PuyoNumber {
		FIRST,
		SECOND
	}

	/**
	 * ぷよの向きを表す列挙型クラスです。
	 * 軸ぷよに対する組みぷよの位置を表しています。
	 */
	public static enum PuyoDirection {
		UP,
		RIGHT,
		DOWN,
		LEFT;

		/**
		 * 時計回りに回転させた方向を取得します。
		 * @return
		 */
		public PuyoDirection rotate() {
			switch (this) {
			case UP:
				return PuyoDirection.RIGHT;
			case RIGHT:
				return PuyoDirection.DOWN;
			case DOWN:
				return PuyoDirection.LEFT;
			case LEFT:
				return PuyoDirection.UP;
			default:
				return null;
			}
		}

		/**
		 * 反時計回りに回転させた方向を取得します。
		 * @return
		 */
		public PuyoDirection rotateReverse() {
			switch (this) {
			case UP:
				return PuyoDirection.LEFT;
			case RIGHT:
				return PuyoDirection.UP;
			case DOWN:
				return PuyoDirection.RIGHT;
			case LEFT:
				return PuyoDirection.DOWN;
			default:
				return null;
			}
		}

		/**
		 * 指定した方向を時計回りに回転させた方向を取得します。
		 * @param direction
		 * @return
		 */
		public static PuyoDirection rotate(PuyoDirection direction) {
			switch (direction) {
			case UP:
				return PuyoDirection.RIGHT;
			case RIGHT:
				return PuyoDirection.DOWN;
			case DOWN:
				return PuyoDirection.LEFT;
			case LEFT:
				return PuyoDirection.UP;
			default:
				return null;
			}
		}

		/**
		 * 指定した方向を反時計回りに回転させた方向を取得します。
		 * @param direction
		 * @return
		 */
		public static PuyoDirection rotateReverse(PuyoDirection direction) {
			switch (direction) {
			case UP:
				return PuyoDirection.LEFT;
			case RIGHT:
				return PuyoDirection.UP;
			case DOWN:
				return PuyoDirection.RIGHT;
			case LEFT:
				return PuyoDirection.DOWN;
			default:
				return null;
			}
		}
	}

	/**
	 * ぷよの色情報
	 */
	private Map<PuyoNumber, PuyoType> puyoTypesMap;

	/**
	 * ぷよの向き
	 */
	private PuyoDirection direction;

	/**
	 * コンストラクタ
	 * @param puyoTypesMap
	 */
	public Puyo(Map<PuyoNumber, PuyoType> puyoTypesMap) {
		this.puyoTypesMap = puyoTypesMap;
		setDirection(PuyoDirection.UP);
	}

	/**
	 * コピーコンストラクタ
	 * @param puyo
	 */
	protected Puyo(Puyo puyo) {
		puyoTypesMap = new EnumMap<PuyoNumber, PuyoType>(PuyoNumber.class);
		puyoTypesMap.putAll(puyo.getPuyoTypesMap());

		direction = puyo.getDirection();
	}

	/**
	 * ぷよの色情報を取得します。
	 * @return
	 */
	public Map<PuyoNumber, PuyoType> getPuyoTypesMap() {
		return puyoTypesMap;
	}

	/**
	 * ぷよの色情報を設定します。
	 * @parampPuyoTypesMap
	 */
	protected void setPuyoTypesMap(Map<PuyoNumber, PuyoType> puyoTypesMap) {
		this.puyoTypesMap = puyoTypesMap;
	}

	/**
	 * 指定された番号のぷよの色を取得します。
	 * @param puyoNumber
	 * @return
	 */
	public PuyoType getPuyoType(PuyoNumber puyoNumber) {
		return puyoTypesMap.get(puyoNumber);
	}

	/**
	 * ぷよの向きを取得します。
	 * @return
	 */
	public PuyoDirection getDirection() {
		return direction;
	}

	/**
	 * ぷよの向きを設定します。
	 * @param direction
	 */
	public void setDirection(PuyoDirection direction) {
		this.direction = direction;
	}

	/**
	 * 組ぷよのx座標の相対位置を取得します。
	 *
	 * @return
	 */
	public int getSecondColmNumber() {
		switch (direction) {
		case UP:
		case DOWN:
			return 0;
		case RIGHT:
			return 1;
		case LEFT:
			return -1;
		default :
			return 0;
		}
	}

	/**
	 * 指定された向きの組ぷよのx座標の相対位置を取得します。
	 * @param direction
	 * @param colmNumber
	 * @return
	 */
	public static int getSecondColmNumber(PuyoDirection direction) {
		switch (direction) {
		case UP:
		case DOWN:
			return 0;
		case RIGHT:
			return 1;
		case LEFT:
			return -1;
		default :
			return 0;
		}
	}

	/**
	 * 下の位置にあるぷよの番号を取得します。
	 * 横向きのぷよの場合，FIRSTが返ります。
	 * @return
	 */
	public PuyoNumber getBottomPuyoNumber() {
		switch (direction) {
		case UP:
			return PuyoNumber.FIRST;
		case DOWN:
			return PuyoNumber.SECOND;
		default :
			return PuyoNumber.FIRST;
		}
	}

	/**
	 * 下の位置にあるぷよの番号を取得します。
	 * 横向きのぷよの場合，FIRSTが返ります。
	 * @param direction
	 * @return
	 */
	public static PuyoNumber getBottomPuyoNumber(PuyoDirection direction) {
		switch (direction) {
		case UP:
			return PuyoNumber.FIRST;
		case DOWN:
			return PuyoNumber.SECOND;
		default :
			return PuyoNumber.FIRST;
		}
	}

	/**
	 * 上の位置にあるぷよの番号を取得します。
	 * 横向きのぷよの場合，FIRSTが返ります。
	 * @return
	 */
	public PuyoNumber getTopPuyoNumber() {
		switch (direction) {
		case UP:
			return PuyoNumber.SECOND;
		case DOWN:
			return PuyoNumber.FIRST;
		default :
			return PuyoNumber.FIRST;
		}
	}

	/**
	 * 上の位置にあるぷよの番号を取得します。
	 * 横向きのぷよの場合，FIRSTが返ります。
	 * @param direction
	 * @return
	 */
	public static PuyoNumber getTopPuyoNumber(PuyoDirection direction) {
		switch (direction) {
		case UP:
			return PuyoNumber.SECOND;
		case DOWN:
			return PuyoNumber.FIRST;
		default :
			return PuyoNumber.FIRST;
		}
	}

	/**
	 * ぷよを時計回りに回転させます。
	 */
	public void rotate() {
		switch (direction) {
		case UP:
			direction = PuyoDirection.RIGHT;
			break;
		case RIGHT:
			direction = PuyoDirection.DOWN;
			break;
		case DOWN:
			direction = PuyoDirection.LEFT;
			break;
		case LEFT:
			direction = PuyoDirection.UP;
			break;
		}
	}

	/**
	 * ぷよを反時計回りに回転させます。
	 */
	public void rotateReverse() {
		switch (direction) {
		case UP:
			direction = PuyoDirection.LEFT;
			break;
		case RIGHT:
			direction = PuyoDirection.UP;
			break;
		case DOWN:
			direction = PuyoDirection.RIGHT;
			break;
		case LEFT:
			direction = PuyoDirection.DOWN;
			break;
		}
	}
}
