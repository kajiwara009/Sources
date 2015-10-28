package SamplePlayer;

import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.AbstractPlayer;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.Action;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.Board;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.Field;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.Puyo.PuyoDirection;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.storage.PuyoType;

/**
 * かえる積みを行うのほほです。
 * @author Hara
 *
 */
public class Nohoho extends AbstractPlayer{
	public Nohoho(String playerName) {
		super(playerName);
	}

	@Override
	public Action doMyTurn() {
		Action action = new Action();

		Board board = getMyBoard();
		Field field = board.getField();

		for (int i = field.getWidth() - 1; i >= 0; i--) {
			if (i <= 2) {
				PuyoDirection direction = decideDirection(board, i);
				action.setColmNumber(i);
				action.setDirection(direction);
				if (field.isEnable(action)) {
					return action;
				}
			} else if (field.getTop(i) < field.getHeight() - 2) {
				action.setColmNumber(i);
				action.setDirection(PuyoDirection.UP);
				if (field.isEnable(action) &&
						board.getField().getTop(i) < field.getDeadLine() - 2) {
					return action;
				}
			}
		}

		return new Action();
	}

	/**
	 * 右から3列積み上げたら，4列目でぷよを消せるように置きます。
	 * @param board
	 * @param colmNumber
	 * @return
	 */
	private PuyoDirection decideDirection(Board board, int colmNumber) {
		PuyoType type =
			board.getCurrentPuyo().getPuyoType(board.getCurrentPuyo().getBottomPuyoNumber());
		int top = board.getField().getTop(colmNumber);

		if (board.getField().getPuyoType(colmNumber + 1, top + 1) == type) {
			return PuyoDirection.UP;
		} else {
			return PuyoDirection.DOWN;
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
