package player;



import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.AbstractPlayer;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.Action;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.Field;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.Puyo;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.Puyo.PuyoDirection;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.Puyo.PuyoNumber;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.PuyoPuyo;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.storage.FieldPoint;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.storage.PuyoType;
import sp.AbstractSamplePlayer;



/**
 * 今降ってきているぷよと同じ色が一番上にあれば，そこに重ねるように配置する<br>
 * ただし，このままだと配置したときに死んでしまうことがあるので，死なないようにする
 * @author tori
 */
public class SamplePlayer06 extends AbstractSamplePlayer {


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

		/*
		 *　全列を操作して今のぷよと同じ色を探す
		 *
		 */
		for(int i = 0; i < field.getWidth(); i++){
			/**
			 * i列目の一番上の点を取得．<br>
			 * FieldPointはフィールド内の一点を示すクラス．<br>
			 * field.getTopPoint(列番号)で，指定列で一番上にあるぷよの座標を返す
			 */
			FieldPoint point = field.getTopPoint(i);
			if(point != null){
				/**
				 * 一番上の点(point)にあるぷよの色を種類
				 */
				PuyoType puyoType = field.getPuyoType(point);
				/*
				 * puyo.getPuyoType(PuyoNumber.FIRST)でメインぷよの色(PuyoType)を返す
				 * puyo.getPuyoType(PuyoNumber.SECOND)でサブぷよの色(PuyoType)を返す
				 */
				if(puyoType == puyo.getPuyoType(PuyoNumber.FIRST)){
					//最初のぷよと同じ色だったら最初のぷよを下にする

					//TODO
					//このままだと高く積み過ぎて負ける可能性があるので，
					//積んだときに負けてしまう場合は，置かないようにする．
					//if(field.getTop(i) > ...)

					action = new Action(PuyoDirection.UP, i);
					//for文を強制終了する
					break;
				}
				else if(puyoType == puyo.getPuyoType(PuyoNumber.SECOND)){
					//最初のぷよと同じ色だったら最初のぷよを上にする

					//TODO
					//ただし，このままだと高く積み過ぎて負ける可能性があるので，
					//積んだときに負けてしまう場合は，置かないようにする．
					//if(field.getTop(i) > ...)

					action = new Action(PuyoDirection.DOWN, i);
					//for文を強制終了する
					break;
				}
			}
		}

		if(action != null){
			return action;
		}
		else{
			//消せるところがなければ，DefaultのActionを返す
			action = getDefaultAction();
			return action;
		}
	}

	/**
	 * 特に配置する場所がなかった場合の基本行動
	 * @return
	 */
	Action getDefaultAction(){
		//TODO 一番低い場所にぷよを配置するようにする
		//Sample04参照．

		Action action = new Action(PuyoDirection.DOWN, 0);
		return action;
	}



	public static void main(String args[]) {
		AbstractPlayer player1 = new SamplePlayer06();
		PuyoPuyo puyopuyo = new PuyoPuyo(player1);
		puyopuyo.puyoPuyo();
	}
}
