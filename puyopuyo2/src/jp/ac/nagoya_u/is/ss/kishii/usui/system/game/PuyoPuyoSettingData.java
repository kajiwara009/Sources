package jp.ac.nagoya_u.is.ss.kishii.usui.system.game;

import java.io.FileInputStream;
import java.util.Properties;

import jp.ac.nagoya_u.is.ss.kishii.usui.system.storage.FieldPoint;

/**
 * Setting情報のクラスです。
 *
 */
public final class PuyoPuyoSettingData {
	/**
	 * フィールドの最大座標
	 */
	private FieldPoint maxPoint;

	/**
	 * フィールドの最小座標
	 */
	private FieldPoint minPoint;

	/**
	 * 越えたら死亡
	 */
	private int deadLine;

	/**
	 * プレイヤーの人数
	 */
	private int numberOfPlayers;

	/**
	 * ぷよの色の数
	 */
	private int numberOfColors;

	/**
	 * 何本先取か
	 */
	private int maxNumberOfWinning;

	/**
	 * 連鎖からおじゃまぷよが落ちるまでのターン数
	 */
	private int ojamaFallTurn;

	/**
	 * 1ターンに降るおじゃまぷよ数の最大値
	 */
	private int maxNumberOfFallingOjamaPuyo;

	/**
	 * 思考可能時間
	 */
	private int timeLimit;

	/**
	 * 待ち時間
	 */
	private double weight;

	/**
	 * ぷよが落ちるモーション
	 */
	private boolean motion;
	/**
	 * 画面縦幅
	 */
	private int DisplayHeight;
	public PuyoPuyoSettingData(){
        try {
            //Propertiesオブジェクトを生成
            Properties prop = new Properties();
            // ファイルを読み込む
            prop.load(new FileInputStream("property.properties"));

            //初期化
            numberOfPlayers = 2;
            maxPoint = new FieldPoint(5, 13);
            minPoint = new FieldPoint(0, 0);
            maxNumberOfFallingOjamaPuyo = 30;

            numberOfColors = 5;
            maxNumberOfWinning = 5;
            weight = 0.1;
            motion = false;
            ojamaFallTurn = 5;
            timeLimit = 1000;

            // 値を取得
            numberOfColors = (Integer.parseInt(prop.getProperty("NumberOfColors")));
            maxNumberOfWinning = (Integer.parseInt(prop.getProperty("MaxNumberOfWinning")));
            weight = Double.parseDouble((prop.getProperty("Weight")));
            motion = (Boolean.parseBoolean(prop.getProperty("Motion")));
            ojamaFallTurn = (Integer.parseInt(prop.getProperty("OjamaFallTurn")));
            timeLimit = (Integer.parseInt(prop.getProperty("TimeLimit")));
            DisplayHeight = (Integer.parseInt(prop.getProperty("Height")));
            
          } catch (Exception e) {
            System.out.println("Exception : " + e);
          }
	}

	protected PuyoPuyoSettingData(PuyoPuyoSettingData settingData) {
		maxPoint = new FieldPoint(settingData.getMaxPoint().getX(), settingData.getMaxPoint().getY());
		minPoint = new FieldPoint(settingData.getMinPoint().getX(), settingData.getMinPoint().getY());
		deadLine = settingData.getDeadLine();
		numberOfPlayers = settingData.getNumberOfPlayers();
		numberOfColors = settingData.getNumberOfColors();
		maxNumberOfWinning = settingData.getMaxNumberOfWinning();
		ojamaFallTurn = settingData.getOjamaFallTurn();
		maxNumberOfFallingOjamaPuyo = settingData.getMaxNumberOfFallingOjamaPuyo();
		timeLimit = settingData.getTimeLimit();
		weight = settingData.getWeight();
		motion = settingData.isMotion();
	}

	/**
	 * ゲームに参加しているプレイヤー数を取得します。
	 * @return
	 */
	public int getNumberOfPlayers() {
		return numberOfPlayers;
	}

	/**
	 * ぷよの色の数を取得します。
	 * @return
	 */
	public int getNumberOfColors() {
		return numberOfColors;
	}

	/**
	 * 何回勝利したら終了するかを取得します。
	 * @return
	 */
	public int getMaxNumberOfWinning() {
		return maxNumberOfWinning;
	}

	/**
	 * 連鎖から何ターン後におじゃまぷよが降るのかを取得します。
	 * @return
	 */
	public int getOjamaFallTurn() {
		return ojamaFallTurn;
	}

	/**
	 * 一度に降るおじゃまぷよ数の最大値を取得します。
	 * @return
	 */
	public int getMaxNumberOfFallingOjamaPuyo() {
		return maxNumberOfFallingOjamaPuyo;
	}

	/**
	 * 思考可能時間を取得します。
	 * @return
	 */
	public int getTimeLimit() {
		return timeLimit;
	}

	/**
	 * ぷよの落下モーションを表示するかどうかを取得します。
	 * @return
	 */
	protected boolean isMotion() {
		return motion;
	}

	/**
	 * 動きの重さを取得します。
	 * @return
	 */
	protected double getWeight() {
		return weight;
	}

	/**
	 * フィールドの最大座標を取得します。
	 * @return
	 */
	public FieldPoint getMaxPoint() {
		return maxPoint;
	}

	/**
	 * フィールドの最小座標を取得します。
	 * @return
	 */
	public FieldPoint getMinPoint() {
		return minPoint;
	}

	/**
	 * フィールドの幅を取得します。
	 * @return
	 */
	public int getWidth() {
		return maxPoint.getX() + 1;
	}

	/**
	 * フィールドの高さを取得します。
	 * @return
	 */
	public int getHeight() {
		return maxPoint.getY() + 1;
	}

	/**
	 * どこまでいったら死亡となるかを取得します。
	 * @return
	 */
	public int getDeadLine() {
		return deadLine;
	}

	public int getDisplayHeight() {
		return DisplayHeight;
	}

}
