package player;

import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.AbstractPlayer;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.Action;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.Puyo.PuyoDirection;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.PuyoPuyo;
import sp.AbstractSamplePlayer;



/**
 * 一番左にだけ横向きに積み上げる
 * @author tori
 *
 */
public class SamplePlayer02 extends AbstractSamplePlayer {

	@Override
	public Action doMyTurn() {
		/**
		 * ぷよを置く場所
		 */
		int columnNum = 0;

		/**
		 * 横向きにぷよを配置する命令<br>
		 * PuyoDirection.RIGHTで，メインぷよの右にサブぷよが設置される．<br>
		 * TODO
		 * PuyoDirection.LEFTで左に設置されるが，一番左にメインを設置して，
		 * その左にサブを設置しようとしているので実現不可能となる．<br>
		 * columnNumの値を変更すると実現可能になるので変更してみましょう．
		 */
		Action action = new Action(PuyoDirection.RIGHT, columnNum);

		return action;
	}





	public static void main(String args[]) {
		AbstractPlayer player1 = new SamplePlayer02();

		PuyoPuyo puyopuyo = new PuyoPuyo(player1);
		puyopuyo.puyoPuyo();
	}
}
