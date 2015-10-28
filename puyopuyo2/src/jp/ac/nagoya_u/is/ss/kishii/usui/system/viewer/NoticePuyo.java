package jp.ac.nagoya_u.is.ss.kishii.usui.system.viewer;

/**
 * 予告ぷよを表す列挙型クラスです。
 *
 */
public enum NoticePuyo {
	CROWN(720),
	MOON(360),
	STAR(180),
	ROCK(30),
	BIG(6),
	SMALL(1);

	private int ojamaNumber;

	private NoticePuyo(int ojamaNumber) {
		this.ojamaNumber = ojamaNumber;
	}

	public int getOjamaNumber() {
		return ojamaNumber;
	}
}
