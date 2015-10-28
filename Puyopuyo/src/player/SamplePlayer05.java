package player;



import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.AbstractPlayer;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.Action;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.Field;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.Puyo.PuyoDirection;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.PuyoPuyo;
import sp.AbstractSamplePlayer;



/**
 * フィールドで一番ぷよの数が少ない場所へ配置する
 * @author tori
 *
 */
public class SamplePlayer05 extends AbstractSamplePlayer {


	@Override
	public Action doMyTurn() {
		/**
		 * 現在のフィールドの状況
		 */
		Field field = getMyBoard().getField();
		int columnNum = 0;
		int minPuyoNum = field.getTop(0)+1;

		//全列について調べる
		for(int i = 0; i < field.getWidth(); i++){
			//ここで，各列の高さを調べ，これまで一番低かった列よりも低ければ，columnNumをiにする．
			/**
			 * puyoNum=i列目のぷよの数としている．<br>
			 * field.getTop(列番号)で，指定列の一番上にあるぷよのy座標が返ってくる．
			 */
			int puyoNum = field.getTop(i)+1;
			if(puyoNum < minPuyoNum){
				//TODO
				//これまで一番ぷよが少なかった数より，i番目の列のぷよが少なかったら
				//minPuyoNumにi番目のぷよの数を入れて
				//配置する列(columnNum)にiを指定する
			}

		}

		//一番ぷよの数が少ない列(columnNum)にぷよを配置する．
		Action action = new Action(PuyoDirection.DOWN, columnNum);

		return action;
	}





	public static void main(String args[]) {
		AbstractPlayer player1 = new SamplePlayer05();

		PuyoPuyo puyopuyo = new PuyoPuyo(player1);
		puyopuyo.puyoPuyo();
	}
}
