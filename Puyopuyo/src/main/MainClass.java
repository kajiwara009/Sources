package main;

import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.AbstractPlayer;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.CppPlayer;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.PuyoPuyo;
import jp.ac.nagoya_u.is.ss.mase.usui.manipulater.Manipulater;
import SamplePlayer.Nohoho;
import SamplePlayer.RandomPlayer;
import SamplePlayer.SamplePlayer;

/**
 * メインクラス
 */

public class MainClass {

	public static void main(String args[]) {
		/*
		 * プレイヤーの読み込み
		 */
		AbstractPlayer samplePlayer = new SamplePlayer("Sample");
		AbstractPlayer randomPlayer = new RandomPlayer("Random");

		/*
		 * 人手で操作する場合
		 */
		AbstractPlayer manipulater = new Manipulater("Manipulater");

		/*
		 * C++で作成したプレイヤーの読み込み
		 */
		AbstractPlayer kenshoPlayer = new CppPlayer("Kensho", "KenshoPlayer");

		/*
		 * ゲームを実行
		 * 自前の画像を使用したい場合はディレクトリ名をコンストラクタに渡してください。
		 */
		PuyoPuyo puyopuyo = new PuyoPuyo(randomPlayer);

		/*
		 * こちらは一人用
		 */
		//PuyoPuyo puyopuyo = new PuyoPuyo(kenshoPlayer);

		puyopuyo.puyoPuyo();
	}
}
