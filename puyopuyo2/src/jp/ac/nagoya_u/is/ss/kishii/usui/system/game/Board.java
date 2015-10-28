package jp.ac.nagoya_u.is.ss.kishii.usui.system.game;

import java.util.ArrayList;
import java.util.List;

import jp.ac.nagoya_u.is.ss.kishii.usui.system.viewer.BoardMaker;


/**
 * ぷよぷよのボードを表すクラスです。
 * ぷよを設置するフィールドと貯まっているおじゃまぷよ，現在，ネクスト，ネクネクのぷよを表しています。
 */
public final class Board {
	/**
	 * 連鎖からおじゃまぷよが落ちるまでのターン数
	 */
	private final int OJAMA_FALL_TURN;

	/**
	 * 一度に降るおじゃまぷよ数の最大値
	 */
	private final int MAX_NUMBER_OF_FALLING_OJAMA_PUYO;

	/**
	 * フィールド
	 */
	private Field field;

	/**
	 * 現在降っているぷよ
	 */
	private Puyo currentPuyo;

	/**
	 * 次に降ってくるぷよ
	 */
	private Puyo nextPuyo;

	/**
	 * 次の次に降ってくるぷよ
	 */
	private Puyo nextNextPuyo;

	/**
	 * 貯まっているおじゃまぷよ数のリスト
	 * 各ターンに降るおじゃまぷよ数が先頭から格納されている
	 */
	private List<Integer> numbersOfOjamaList;

	/**
	 * GUI
	 */
	private BoardMaker boardMaker;

	/**
	 * コンストラクタ
	 */
	public Board(PuyoPuyoSettingData settingData) {
		field = new Field(settingData.getWidth(), settingData.getHeight());
		numbersOfOjamaList = new ArrayList<Integer>();

		OJAMA_FALL_TURN = settingData.getOjamaFallTurn();
		MAX_NUMBER_OF_FALLING_OJAMA_PUYO = settingData.getMaxNumberOfFallingOjamaPuyo();

		for (int i = 0; i <= OJAMA_FALL_TURN; i++) {
			numbersOfOjamaList.add(0);
		}
	}

	/**
	 * コピーコンストラクタ
	 * @param board
	 */
	protected Board(Board board) {
		field = new Field(board.getField());
		numbersOfOjamaList = new ArrayList<Integer>(board.getNumbersOfOjamaList());

		currentPuyo = new Puyo(board.getCurrentPuyo());
		nextPuyo = new Puyo(board.getNextPuyo());
		nextNextPuyo = new Puyo(board.getNextNextPuyo());

		OJAMA_FALL_TURN = board.getOjamaFallTurn();
		MAX_NUMBER_OF_FALLING_OJAMA_PUYO = board.getMaxNumberOfFallingOjamaPuyo();
	}

	/**
	 * フィールドを取得します。
	 * @return
	 */
	public Field getField() {
		return field;
	}

	/**
	 * 現在降っているぷよを取得します。
	 * @return
	 */
	public Puyo getCurrentPuyo() {
		return currentPuyo;
	}

	/**
	 * 現在降っているぷよを設定します。
	 * @param currentPuyo
	 */
	protected void setCurrentPuyo(Puyo currentPuyo) {
		this.currentPuyo = currentPuyo;
	}

	/**
	 * 次に降ってくるぷよを取得します。
	 * @return
	 */
	public Puyo getNextPuyo() {
		return nextPuyo;
	}

	/**
	 * 次に降ってくるぷよを設定します。
	 * @param nextPuyo
	 */
	protected void setNextPuyo(Puyo nextPuyo) {
		this.nextPuyo = nextPuyo;
	}

	/**
	 * 次の次に降ってくるぷよを取得します。
	 * @return
	 */
	public Puyo getNextNextPuyo() {
		return nextNextPuyo;
	}

	/**
	 * 次の次に降ってくるぷよを設定します。
	 * @param nextNextPuyo
	 */
	protected void setNextNextPuyo(Puyo nextNextPuyo) {
		this.nextNextPuyo = nextNextPuyo;
	}

	/**
	 * 貯まっているおじゃまぷよ数のリストを取得します。
	 * 各ターンに降るおじゃまぷよ数が先頭から格納されています。
	 * 先頭の値が現在のターンに降ってくる数です。
	 * @return
	 */
	public List<Integer> getNumbersOfOjamaList() {
		return numbersOfOjamaList;
	}

	/**
	 * 貯まっているおじゃまぷよ数のリストを設定します。
	 * @param numberOfOjamasList
	 */
	protected void setNumbersOfOjamaList(List<Integer> numbersOfOjamaList) {
		this.numbersOfOjamaList = numbersOfOjamaList;
	}

	private int getOjamaFallTurn() {
		return OJAMA_FALL_TURN;
	}

	private int getMaxNumberOfFallingOjamaPuyo() {
		return MAX_NUMBER_OF_FALLING_OJAMA_PUYO;
	}

	/**
	 * GUI用のパネルを取得します。
	 * @return
	 */
	protected BoardMaker getBoardMaker() {
		return boardMaker;
	}

	/**
	 * GUI用のパネルを設定します。
	 * @param boardMaker
	 */
	protected void setBoardMaker(BoardMaker boardMaker) {
		this.boardMaker = boardMaker;
	}

	/**
	 * ボードをゲームの最初の状態に初期化します。
	 */
	protected void initBoard() {
		field = new Field(field.getWidth(), field.getHeight());
		numbersOfOjamaList = new ArrayList<Integer>();
		for (int i = 0; i <= OJAMA_FALL_TURN; i++) {
			numbersOfOjamaList.add(0);
		}

		currentPuyo = null;
		nextPuyo = null;
		nextNextPuyo = null;
	}

	/**
	 * 現在のターンに降ってくるおじゃまぷよ数を取得します。
	 * @return
	 */
	public int getCurrentTurnNumberOfOjama() {
		return numbersOfOjamaList.get(0);
	}

	/**
	 * 貯まっているおじゃまぷよの総数を取得します。
	 * @return
	 */
	public int getTotalNumberOfOjama() {
		int totalNumberOfOjama = 0;
		for (int numberOfOjama : numbersOfOjamaList) {
			totalNumberOfOjama += numberOfOjama;
		}

		return totalNumberOfOjama;
	}

	/**
	 * numberOfOjamasListの先頭を消します。
	 * fieldをいじるメソッドではありません。
	 */
	protected void fallOjamaPuyo() {
		numbersOfOjamaList.remove(0);

		while(numbersOfOjamaList.size() <= OJAMA_FALL_TURN) {
			numbersOfOjamaList.add(0);
		}
	}

	/**
	 * 貯まっているおじゃまぷよを指定されたおじゃまぷよ数で相殺します。
	 * 余った分のおじゃまぷよ数を返します。
	 * @param numberOfOjama
	 * @return
	 */
	protected int sousai(int numberOfOjama) {
		int restNumberOfOjama = numberOfOjama;

		for (int i = 0; i < numbersOfOjamaList.size(); i++) {
			if (numbersOfOjamaList.get(i) > restNumberOfOjama) {
				numbersOfOjamaList.set(i, numbersOfOjamaList.get(i) - restNumberOfOjama);
				restNumberOfOjama = 0;

				break;
			} else {
				restNumberOfOjama -= numbersOfOjamaList.get(i);
				numbersOfOjamaList.set(i, 0);
			}
		}

		return restNumberOfOjama;
	}

	/**
	 * 指定されたおじゃまぷよ数分貯まります。
	 * @param numberOfOjama
	 */
	protected void storeOjamaPuyo(int numberOfOjama) {
		while(numbersOfOjamaList.size() <= OJAMA_FALL_TURN) {
			numbersOfOjamaList.add(0);
		}

		numbersOfOjamaList.set(OJAMA_FALL_TURN,
								numbersOfOjamaList.get(OJAMA_FALL_TURN) + numberOfOjama);

		overOjamaPuyo();
	}

	/**
	 * 1ターンに降る最大値を越えていたら次のターンに持ち越します。
	 */
	private void overOjamaPuyo() {
		int index = OJAMA_FALL_TURN;
		for(; ; ) {
			int overNumberOfOjama =
				numbersOfOjamaList.get(index) - MAX_NUMBER_OF_FALLING_OJAMA_PUYO;
			if (overNumberOfOjama < 0) {
				break;
			}

			if (index + 1 >= numbersOfOjamaList.size()) {
				numbersOfOjamaList.add(overNumberOfOjama);
			} else {
				numbersOfOjamaList.set(index + 1, numbersOfOjamaList.get(index + 1) + overNumberOfOjama);
			}

			numbersOfOjamaList.set(index, MAX_NUMBER_OF_FALLING_OJAMA_PUYO);
			index += 1;
		}
	}
}
