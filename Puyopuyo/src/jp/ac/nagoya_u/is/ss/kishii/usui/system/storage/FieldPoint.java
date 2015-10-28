package jp.ac.nagoya_u.is.ss.kishii.usui.system.storage;

/**
 * フィールドの座標を表すクラスです。
 *
 */
public final class FieldPoint {
	/**
	 * x座標
	 */
	private int x;

	/**
	 * y座標
	 */
	private int y;

	/**
	 * コンストラクタ
	 * @param x
	 * @param y
	 */
	public FieldPoint(int x, int y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * x座標を取得します。
	 * @return
	 */
	public int getX() {
		return x;
	}

	/**
	 * x座標を設定します。
	 * @param x
	 */
	public void setX(int x) {
		this.x = x;
	}

	/**
	 * y座標を取得します。
	 * @return
	 */
	public int getY() {
		return y;
	}

	/**
	 * y座標を設定します。
	 * @param y
	 */
	public void setY(int y) {
		this.y = y;
	}

	/**
	 * 左の座標を取得します。
	 * @return
	 */
	public FieldPoint getLeftPoint() {
		return new FieldPoint(x - 1, y);
	}

	/**
	 * 右の座標を取得します。
	 * @return
	 */
	public FieldPoint getRightPoint() {
		return new FieldPoint(x + 1, y);
	}

	/**
	 * 上の座標を取得します。
	 * @return
	 */
	public FieldPoint getUpPoint() {
		return new FieldPoint(x, y + 1);
	}

	/**
	 * 下の座標を取得します。
	 * @return
	 */
	public FieldPoint getDownPoint() {
		return new FieldPoint(x, y - 1);
	}

	/**
	 * 座標の足し算を行います。
	 * @param point
	 * @return
	 */
	public FieldPoint add(FieldPoint point) {
		return new FieldPoint(x + point.getX(),
						 y + point.getY());
	}

	/**
	 * 座標の引き算を行います。
	 * @param point
	 * @return
	 */
	public FieldPoint sub(FieldPoint point) {
		return new FieldPoint(x - point.getX(),
						 y - point.getY());
	}

	public boolean equals(Object obj) {
		if (obj instanceof FieldPoint) {
			FieldPoint point = (FieldPoint)obj;

			if (x == point.x && y == point.y) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public int hashCode() {
		return x + y * 10;
	}

	public String toString() {
		return "[" + x + ", " + y + "]";
	}
}
