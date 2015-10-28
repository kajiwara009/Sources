package player;



import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import sp.AbstractSamplePlayer;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.AbstractPlayer;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.Action;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.Board;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.Field;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.Puyo;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.Puyo.PuyoDirection;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.Puyo.PuyoNumber;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.PuyoPuyo;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.storage.PuyoType;



/**
 * フィールド状態を表示しながらランダムに配置<br>
 * printFieldでコンソールに現在の状況を表示している．<br>
 * 必要に応じてこのメソッドをコピーすると便利
 * @author tori
 */
public class SamplePlayer10 extends AbstractSamplePlayer {


	Random rand = new Random();
	@Override
	public Action doMyTurn() {

		Map<PuyoNumber, PuyoType> puyoMap = new HashMap<Puyo.PuyoNumber, PuyoType>();
		puyoMap.put(PuyoNumber.FIRST, PuyoType.BLUE_PUYO);
		puyoMap.put(PuyoNumber.FIRST, PuyoType.BLUE_PUYO);
		Puyo myPuyo = new Puyo(puyoMap);
		myPuyo.setDirection(PuyoDirection.UP);
		getMyBoard().getField().getNextField(myPuyo, 2);

		
		/**
		 * 現在のフィールドの状況
		 */
		Field field = getMyBoard().getField();
		System.out.println(field.getHeight());


		printField(field);

		//Sample05と同じ
		int columnNum = 0;
		int minPuyoNum = field.getTop(0)+1;

		for(int i = 0; i < field.getWidth(); i++){
			int puyoNum = field.getTop(i)+1;
			if(puyoNum < minPuyoNum){
				columnNum = i;
				minPuyoNum = puyoNum;
			}

		}

		//一番ぷよの数が少ない列(columnNum)にぷよを配置する．
		Action action = new Action(PuyoDirection.DOWN, columnNum);


		return action;
	}

	/**
	 * フィールド状態を表示するメソッド
	 * @param field
	 */
	public void printField(Field field){
		for(int y = field.getHeight(); y >= 0 ; y--){
			for(int x = 0; x < field.getWidth(); x++){
				if(field.getPuyoType(x, y) != null){
					System.out.print(field.getPuyoType(x, y).toString().substring(0, 1));
				}
				else{
					System.out.print(".");
				}
			}
			System.out.println();
		}
	}

	public static void main(String args[]) {
		SamplePlayer10 player = new SamplePlayer10();

		PuyoPuyo puyopuyo = new PuyoPuyo(player);
		puyopuyo.puyoPuyo();
	}
}
