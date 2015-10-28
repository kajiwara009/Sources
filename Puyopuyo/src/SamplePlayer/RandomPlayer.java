package SamplePlayer;

import java.util.Random;

import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.AbstractPlayer;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.Action;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.Board;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.Field;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.Puyo.PuyoDirection;




/**
 * ランダムでぷよを設置するプレイヤーです。
 * @author Hara
 *
 */
public class RandomPlayer extends AbstractPlayer{
	private Random rnd;

	public RandomPlayer(String playerName) {
		super(playerName);

		rnd = new Random();
	}

	@Override
	public Action doMyTurn() {
		Action action = new Action();

		for (int i = 0; i < 500; i++) {
			Board board = getMyBoard();

			int directionNumber = rnd.nextInt(PuyoDirection.values().length);
			PuyoDirection direction = decideDirection(directionNumber);
			int colmNumber = rnd.nextInt(board.getField().getWidth());

			action.setColmNumber(colmNumber);
			action.setDirection(direction);
			if (board.getField().isEnable(action)) {
				board.getCurrentPuyo().setDirection(direction);
				Field nextField = board.getField().getNextField(board.getCurrentPuyo(), colmNumber);
				if (nextField == null) {
					System.out.println("null");
				}
				return action;
			}
		}

		return new Action();
	}

	private PuyoDirection decideDirection(int directionNumber) {
		switch (directionNumber) {
		case 0:
			return PuyoDirection.UP;
		case 1:
			return PuyoDirection.RIGHT;
		case 2:
			return PuyoDirection.DOWN;
		case 3:
			return PuyoDirection.LEFT;
		default :
			return PuyoDirection.UP;
		}
	}

	@Override
	public void initialize() {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public void inputResult() {
		// TODO 自動生成されたメソッド・スタブ

	}
}
