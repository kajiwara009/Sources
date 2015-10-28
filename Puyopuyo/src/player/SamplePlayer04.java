package player;



import java.util.Random;

import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.AbstractPlayer;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.Action;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.Field;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.Puyo.PuyoDirection;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.PuyoPuyo;
import sp.AbstractSamplePlayer;



/**
 * 現在のフィールド状態を表示しながら，ランダムに配置を行う．
 * @author tori
 *
 */
public class SamplePlayer04 extends AbstractSamplePlayer {

	/**
	 * 乱数を扱う変数を作成する
	 */
	Random rand = new Random();

	@Override
	public Action doMyTurn() {
		/**
		 * 現在のフィールドの状況
		 */
		Field field = getMyBoard().getField();

		//すべての列に対して
		for(int i = 0; i < field.getWidth(); i++){
			//列の高さを表示する
			System.out.println(i+":"+field.getTop(i));
		}
		System.out.println();

		/*
		 * 配置する列をランダムに決める．
		 * TODO
		 * 配置する列をフィールドの右半分だけにだけにしてみよう
		 */
		int columnNum = rand.nextInt(field.getWidth());
		Action action = new Action(PuyoDirection.DOWN, columnNum);

		return action;
	}





	public static void main(String args[]) {
		AbstractPlayer player1 = new SamplePlayer04();

		PuyoPuyo puyopuyo = new PuyoPuyo(player1);
		puyopuyo.puyoPuyo();
	}
}
