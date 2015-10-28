package player;

import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.AbstractPlayer;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.Action;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.Puyo.PuyoDirection;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.PuyoPuyo;
import sp.AbstractSamplePlayer;



/**
 * 一番左にだけ積み上げるエージェント
 * 
 * @author tori
 *
 */
public class SamplePlayer01 extends AbstractSamplePlayer {



	@Override
	public Action doMyTurn() {
		/**
		 * ぷよを置く場所
		 */
		int columnNum = 0;

		/**
		 * 縦にぷよを配置する命令
		 */
		Action action = new Action(PuyoDirection.DOWN, columnNum);

		return action;
	}


	/**
	 * おまじない
	 * @param args
	 */
	public static void main(String args[]) {
		AbstractPlayer player1 = new SamplePlayer01();

		PuyoPuyo puyopuyo = new PuyoPuyo(player1);
		puyopuyo.puyoPuyo();
	}
}
