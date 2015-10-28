package player;

import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.AbstractPlayer;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.Action;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.Puyo.PuyoDirection;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.PuyoPuyo;
import sp.AbstractSamplePlayer;



/**
 * 左から順番にぷよを配置していく
 * @author tori
 *
 */
public class SamplePlayer03 extends AbstractSamplePlayer {

	/**
	 * ぷよを置く場所
	 */
	int columnNum = 0;

	/**
	 * 右に向かって行くかどうか
	 */
	boolean toRight=true;

	@Override
	public Action doMyTurn() {

		/**
		 * 縦にぷよを配置する命令
		 */
		Action action = new Action(PuyoDirection.DOWN, columnNum);

		/**
		 * toRightがtrueならば，ぷよを配置する位置を一つ右へずらす
		 * toRightがfalseならば，ぷよを配置する一を一つ左へずらす
		 */
		if(toRight){
			columnNum++;
		}
		else{
			columnNum--;
		}
		/*
		 * これだけだと，右端まで行ったらそれ以上は進めない
		 * 右端まで行ったら左に折り返すようにする
		*/
		/**
		 * フィールドの幅
		 */
		int width = getMyBoard().getField().getWidth();

		/*
		 * TODO
		 * もしcolumnNumがwidthと同じなら，columnNumを0に戻すところを作ってみよう
		 */

		/*
		 if(columnNum ....){
			columnNum = 0;
		 }
		*/


		return action;
	}





	public static void main(String args[]) {
		AbstractPlayer player1 = new SamplePlayer03();

		PuyoPuyo puyopuyo = new PuyoPuyo(player1);
		puyopuyo.puyoPuyo();
	}
}
