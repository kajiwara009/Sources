package player;



import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.AbstractPlayer;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.Action;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.Board;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.Field;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.Puyo;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.Puyo.PuyoDirection;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.Puyo.PuyoNumber;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.PuyoPuyo;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.storage.PuyoType;
import sp.AbstractSamplePlayer;



/**
 * ぷよが同じ色と隣接するように配置する
 * @author tori
 */
public class SamplePlayer08 extends AbstractSamplePlayer {


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

		//TODO isEnableとgetNeighborPuyoNumを作る
		int maxNeighborPuyo = 0;
		for(int i = 0; i < field.getWidth(); i++){
			for(PuyoDirection dir:PuyoDirection.values()){

				/**
				 * 配置不可能，または負けてしまうような配置は最初から考慮しない
				 */
				if(!isEnable(dir, i)){
					continue;
				}

				PuyoType firstPuyo = puyo.getPuyoType(PuyoNumber.FIRST);
				PuyoType secondPuyo = puyo.getPuyoType(PuyoNumber.SECOND);

				int firstNeighbor = 0;
				int secondNeighbor = 0;

				//最初のぷよの周りに存在する同色ぷよ数を数える
				if(dir == PuyoDirection.DOWN){
					//二番目のぷよが下にある場合は，topの二つ上がy座標
					int y = field.getTop(i)+2;
					firstNeighbor = getNeighborPuyoNum(i, y, firstPuyo);
				}
				else{
					//二番目のぷよが下にある場合以外は，topの1つ上がy座標
					int y = field.getTop(i)+1;
					firstNeighbor = getNeighborPuyoNum(i, y, firstPuyo);
				}

				//二番目のぷよの周りに存在する同色ぷよを数える
				if(dir == PuyoDirection.DOWN){
					//二番目のぷよが下にある場合
				}
				else if(dir == PuyoDirection.UP){
					//二番目のぷよが上にある場合
				}
				else if(dir == PuyoDirection.RIGHT){
					//二番目のぷよが右にある場合
				}
				else if(dir == PuyoDirection.LEFT){
					//二番目のぷよが左にある場合
				}

				//一番目のぷよの周りの同色ぷよ数と二番目のぷよの周りの同色ぷよ数が最大の場所に落とすようにしたい．
				//TODO


			}
		}
		if(action != null){
			System.out.printf("%d\t%s\n", action.getColmNumber(), action.getDirection());
			return action;
		}
		else{
			//消せるところがなければ，DefaultのActionを返す
			System.out.println("Deafault Action!");
			return getDefaultAction();
		}
	}

	/**
	 * 配置可能か，あるいは死んでしなわないかをチェックする
	 * TODO
	 * 未完成なので完成させる
	 * @param i
	 * @param dir
	 * @return 配置不能または死んでしまう場合はfalse，それ以外はtrue
	 */
	private boolean isEnable(PuyoDirection dir, int i) {
		Field field = getMyBoard().getField();

		//最初に配置不能ならfalseを返す
//		if(条件式){
//			return false;
//		}

		//PuyoDirectionがUPかDOWNなら・・・
		if(dir == PuyoDirection.DOWN || dir == PuyoDirection.UP){
//			if(条件式){
//				return false;
//			}
		}
		else if(dir == PuyoDirection.RIGHT){
//			if(条件式1 || 条件式2) {
//				return false;
//			}
		}
		else if(dir == PuyoDirection.LEFT){
//			if(条件式1 || 条件式2) {
//				return false;
//			}
		}
		return true;
	}

	/**
	 * 指定した場所の周りに同色のぷよがいくつあるか
	 * @param x
	 * @param y
	 * @param puyoType
	 * @return
	 */
	private int getNeighborPuyoNum(int x, int y, PuyoType puyoType) {
		//数を記録する変数
		int count = 0;
		Field field = getMyBoard().getField();

		//上下左右のPuyoTypeを
		//field.getPuyoType(a, b)で取得して，
		//puyoTypeと等しいかを確認する
		//フィールドからはみ出すとエラーが起きることに注意
//		if(条件式){
//			count++;
//		}
//		if(条件式){
//			count++;
//		}
//		if(条件式){
//			count++;
//		}
//		if(条件式){
//			count++;
//		}

		return count;
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
		AbstractPlayer player = new SamplePlayer08();

		PuyoPuyo puyopuyo = new PuyoPuyo(player);
		puyopuyo.puyoPuyo();
	}
}
