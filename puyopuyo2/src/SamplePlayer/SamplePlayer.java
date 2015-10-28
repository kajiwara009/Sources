package SamplePlayer;

import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.AbstractPlayer;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.Action;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.Board;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.Field;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.Puyo;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.Puyo.PuyoDirection;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.Puyo.PuyoNumber;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.storage.FieldPoint;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.storage.PuyoType;

/**
 * ぷよが連結するように設置するプレイヤーです。
 * 単純に連結するかどうかを調べていき，
 * 最初に見つかったものを選択します。
 *
 * 複数のぷよを連結される組み合わせを優先することや，
 * 連鎖を組むことは考慮しません。
 * @author Hara
 *
 */
public class SamplePlayer extends AbstractPlayer{

	public SamplePlayer(String playerName) {
		super(playerName);
		// TODO 自動生成されたコンストラクター・スタブ
	}

	@Override
	public Action doMyTurn() {
		Board board = getMyBoard();
		Field field = board.getField();

		/*
		 * ぷよを連結させる設置が存在したら，その設置を行う
		 */
		for (int i = field.getWidth() - 1; i >= 0; i--) {
			PuyoDirection direction = getConnectionDirection(board, i);
			if (direction != null) {
				return new Action(board.getCurrentPuyo().getDirection(), i);
			}
		}

		/*
		 * ぷよを連結させる設置が存在しなければ，
		 * 右端から死なないように向きUPで設置
		 */
		for (int i = field.getWidth() - 1; i >= 0; i--) {
			if (field.isEnable(PuyoDirection.UP, i) &&
					(field.getTop(i) + 2) < field.getDeadLine()) {
				board.getCurrentPuyo().setDirection(PuyoDirection.UP);
				return new Action(PuyoDirection.UP, i);
			}
		}

		return null;
	}


	private void printField(Field field) {
		for (int i = field.getHeight() - 1; i >= 0; i--) {
			for (int j = 0; j < field.getWidth(); j++) {
				System.out.print(field.getPuyoType(j, i));
			}
			System.out.println();
		}
	}

	/**
	 * 指定された列に現在降っているぷよを落下させるとき，
	 * ぷよが連結し，設置してもプレイヤーが死亡しないような向きを返します。
	 * 連結するような向きが存在しない場合，nullを返します。
	 *
	 * @param board
	 * @param colmNumber
	 * @return
	 */
	private PuyoDirection getConnectionDirection(Board board, int colmNumber) {
		for (PuyoDirection direction : PuyoDirection.values()) {
			board.getCurrentPuyo().setDirection(direction);
			if (board.getField().isEnable(direction, colmNumber) &&
					isConnection(board, colmNumber) &&
					!isDead(board.getField(), direction, colmNumber)) {
				return direction;
			}
		}

		return null;
	}

	/**
	 * 指定された列に現在降っているぷよを設定されている向きで落下させた時，
	 * ぷよが連結するかどうか調べます。
	 * @param board
	 * @param colmNumber
	 * @return
	 */
	private boolean isConnection(Board board, int colmNumber) {
		if (isDownConnection(board, colmNumber) ||
				isRightConnection(board, colmNumber) ||
				isLeftConnection(board, colmNumber)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 指定された列に現在降っているぷよを設定されている向きで落下させた時，
	 * ぷよが下方向にあるぷよと連結するかどうか調べます。
	 * @param board
	 * @param colmNumber
	 * @return
	 */
	private boolean isDownConnection(Board board, int colmNumber) {
		Field field = board.getField();
		Puyo currentPuyo = board.getCurrentPuyo();
		PuyoDirection direction = currentPuyo.getDirection();

		if (direction == PuyoDirection.LEFT ||
				direction == PuyoDirection.RIGHT) {
			/*
			 * LEFTかRIGHTの場合は，
			 * 各ぷよが1つ下の座標のぷよと連結するかどうか調べる
			 */
			FieldPoint firstDownPoint = field.getTopPoint(colmNumber);
			PuyoType firstPuyoType = currentPuyo.getPuyoType(PuyoNumber.FIRST);
			FieldPoint secondDownTopPoint = field.getTopPoint(colmNumber + currentPuyo.getSecondColmNumber());
			PuyoType secondPuyoType = currentPuyo.getPuyoType(PuyoNumber.SECOND);

			if ((firstDownPoint != null && field.getPuyoType(firstDownPoint) == firstPuyoType) ||
					(secondDownTopPoint != null && field.getPuyoType(secondDownTopPoint) == secondPuyoType)) {
				return true;
			} else {
				return false;
			}
		} else {
			/*
			 * ぷよが縦方向の場合は，
			 * 2つのうち下の位置にあるぷよが
			 * その1つ下の座標のぷよと連結するかどうか調べる
			 */
			FieldPoint downPoint = field.getTopPoint(colmNumber);
			PuyoType bottomPuyoType = currentPuyo.getPuyoType(currentPuyo.getBottomPuyoNumber());

			if (downPoint != null && bottomPuyoType == field.getPuyoType(downPoint)) {
				return true;
			} else {
				return false;
			}
		}
	}

	/**
	 * 指定された列に現在降っているぷよを設定されている向きで落下させた時，
	 * ぷよが右方向にあるぷよと連結するかどうか調べます。
	 * @param board
	 * @param colmNumber
	 * @return
	 */
	private boolean isRightConnection(Board board, int colmNumber) {
		Field field = board.getField();
		Puyo currentPuyo = board.getCurrentPuyo();
		PuyoDirection direction = currentPuyo.getDirection();

		if (direction == PuyoDirection.LEFT) {
			/*
			 * LEFTの場合は右方向にあるぷよは軸ぷよの位置
			 * よってfalseとする
			 */
			return false;
		} else if (direction == PuyoDirection.RIGHT) {
			/*
			 * RIGHTの場合は，
			 * 1つ右の座標にあるぷよと連結するかどうか調べる
			 */
			PuyoType rightPuyoType = field.getPuyoType(colmNumber + 2, field.getTop(colmNumber) + 1);

			if (rightPuyoType != null &&
					currentPuyo.getPuyoType(PuyoNumber.SECOND) == rightPuyoType) {
				return true;
			} else {
				return false;
			}
		} else {
			/*
			 * ぷよが縦方向の場合は，
			 * 各ぷよとその1つ右の座標にあるぷよと連結するかどうか調べる
			 */
			PuyoType bottomRightPuyoType =
				field.getPuyoType(colmNumber + 1, field.getTop(colmNumber) + 1);
			PuyoType bottomPuyoType = currentPuyo.getPuyoType(currentPuyo.getBottomPuyoNumber());
			PuyoType topRightPuyoType =
				field.getPuyoType(colmNumber + 1, field.getTop(colmNumber) + 2);
			PuyoType topPuyoType = currentPuyo.getPuyoType(currentPuyo.getTopPuyoNumber());

			if ((bottomRightPuyoType != null && bottomRightPuyoType == bottomPuyoType) ||
					(topRightPuyoType != null && topRightPuyoType == topPuyoType)) {
				return true;
			} else {
				return false;
			}
		}
	}

	/**
	 * 指定された列に現在降っているぷよを設定されている向きで落下させた時，
	 * ぷよが左方向にあるぷよと連結するかどうか調べます。
	 * @param board
	 * @param colmNumber
	 * @return
	 */
	private boolean isLeftConnection(Board board, int colmNumber) {
		Field field = board.getField();
		Puyo currentPuyo = board.getCurrentPuyo();
		PuyoDirection direction = currentPuyo.getDirection();

		if (direction == PuyoDirection.RIGHT) {
			/*
			 * RIGHTの場合は左方向にあるぷよは軸ぷよの位置
			 * よってfalseとする
			 */
			return false;
		} else if (direction == PuyoDirection.LEFT) {
			/*
			 * LEFTの場合は，
			 * 1つ左の座標にあるぷよと連結するかどうか調べる
			 */
			PuyoType leftPuyoType = field.getPuyoType(colmNumber - 2, field.getTop(colmNumber) + 1);

			if (leftPuyoType != null &&
					currentPuyo.getPuyoType(PuyoNumber.SECOND) == leftPuyoType) {
				return true;
			} else {
				return false;
			}
		} else {
			/*
			 * ぷよが縦方向の場合は，
			 * 各ぷよとその1つ左の座標にあるぷよと連結するかどうか調べる
			 */
			PuyoType bottomRightPuyoType =
				field.getPuyoType(colmNumber + 1, field.getTop(colmNumber) + 1);
			PuyoType bottomPuyoType = currentPuyo.getPuyoType(currentPuyo.getBottomPuyoNumber());
			PuyoType topRightPuyoType =
				field.getPuyoType(colmNumber + 1, field.getTop(colmNumber) + 2);
			PuyoType topPuyoType = currentPuyo.getPuyoType(currentPuyo.getTopPuyoNumber());

			if ((bottomRightPuyoType != null && bottomRightPuyoType == bottomPuyoType) ||
					(topRightPuyoType != null && topRightPuyoType == topPuyoType)) {
				return true;
			} else {
				return false;
			}
		}
	}

	/**
	 * フィールドに指定された向きで指定された列にぷよを落下させた時，
	 * プレイヤーが死亡するかどうか調べます。
	 * @param field
	 * @param direction
	 * @param colmNumber
	 * @return
	 */
	private boolean isDead(Field field, PuyoDirection direction, int colmNumber) {
		int secondColmNumber =
			colmNumber + Puyo.getSecondColmNumber(direction);

		if (colmNumber == secondColmNumber) {
			if (field.getTop(colmNumber) + 2 >= field.getDeadLine()) {
				return true;
			} else {
				return false;
			}
		} else {
			if (field.getTop(colmNumber) + 1 >= field.getDeadLine() ||
					field.getTop(secondColmNumber) + 1 >= field.getDeadLine()) {
				return true;
			} else {
				return false;
			}
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
