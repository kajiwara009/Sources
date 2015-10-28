package player;



import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.AbstractPlayer;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.Action;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.Board;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.Field;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.Puyo;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.Puyo.PuyoDirection;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.PuyoPuyo;
import sp.AbstractSamplePlayer;



/**
 * 次のFieldを見て，もっともぷよの数が少なくなるような配置を行う．
 * @author tori
 */
public class SamplePlayer07 extends AbstractSamplePlayer {


	@Override
	public Action doMyTurn() {

		/**
		 * 現在のフィールドの状況
		 */
		Field field = getMyBoard().getField();
		/**
		 * 今降ってきているぷよ
		 */
		Puyo puyo = getMyBoard().getCurrentPuyo();

		/**
		 * 最初actionは空っぽ
		 */
		Action action = null;

		int puyoNum = getPuyoNum(field);
		//全部の列で，全部の回転方向について次のフィールドの状態を確認する．
		//その中で一番次のフィールドでのぷよ数が少なくなるものを今回の一手とする．
		for(int i = 0; i < field.getWidth(); i++){
			for(PuyoDirection dir:PuyoDirection.values()){
				if(field.isEnable(dir, i)){
					//現在のぷよを回転させる
					puyo.setDirection(dir);

					//もし現在のpuyoをi列目に落としたら，その後のフィールドの状態がnextFieldになる
					Field nextField = field.getNextField(puyo, i);
					if(nextField != null){
						int next = getPuyoNum(nextField);
						if(next < puyoNum){
							puyoNum = next;
							action = new Action(dir, i);
						}
					}
				}
			}
		}

		if(action != null){
			System.out.println("Delete Action!");
			return action;
		}
		else{
			//消せるところがなければ，DefaultのActionを返す
			System.out.println("Deafault Action!");
			return getDefaultAction();
		}
	}

	/**
	 * 指定したフィールドのぷよ数を返す
	 * @param field
	 * @return
	 */
	int getPuyoNum(Field field){
		int num = 0;
		//ここでぷよの数を数える．
		//field.getTop(columnNum)で，ぷよが存在する場所を返すので，
		//それより1大きい数のぷよがその列には存在する
		//ぷよが一つもない列は-1が返ってくることに注意．

		for(int i = 0; i < field.getWidth(); i++){
			num+=field.getTop(i)+1;
		}

		return num;
	}

	/**
	 * 特に配置する場所がなかった場合の基本行動
	 * @return
	 */
	Action getDefaultAction(){
		Board board = getGameInfo().getBoard(getMyPlayerInfo());
		Field field = board.getField();
		int minColumn = 0;
		for(int i = 0; i < field.getWidth(); i++){
			if(field.getTop(i) < field.getTop(minColumn)){
				minColumn = i;
			}
		}

		Action action = new Action(PuyoDirection.DOWN, minColumn);


		return action;
	}



	public static void main(String args[]) {
		AbstractPlayer player1 = new SamplePlayer07();

		PuyoPuyo puyopuyo = new PuyoPuyo(player1);
		puyopuyo.puyoPuyo();
	}
}
