package jp.ac.nagoya_u.is.ss.mase.usui.manipulater;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.AbstractPlayer;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.Action;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.Board;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.Puyo;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.Puyo.PuyoDirection;

public class Manipulater extends AbstractPlayer {

	PrefFrame frm;

	public Manipulater(String playerName) {
		super(playerName);
		frm = new PrefFrame(playerName, this);
		frm.makeMainPanel();
	}

	private int colmNumber;
	private Action action;
	private Puyo currentPuyo;
	private boolean isTurnEnd;

	@Override
	public Action doMyTurn() {
		frm.setVisible(true);
		Board board = getMyBoard();
		currentPuyo = board.getCurrentPuyo();
		PuyoDirection direction = currentPuyo.getDirection();
		colmNumber = 2;
		action = new Action(direction, colmNumber);
		frm.getMainPanel().getGridMaker().puyo(currentPuyo, action);

		isTurnEnd = false;
		while (!isTurnEnd) {

		}
		return action;
	}

	public void moveLeft() {
		if (!isTurnEnd) {
			if ((colmNumber > 0 && currentPuyo.getDirection() != PuyoDirection.LEFT)
					|| (colmNumber > 1 && currentPuyo.getDirection() == PuyoDirection.LEFT)) {
				colmNumber--;
			}

			action.setColmNumber(colmNumber);
			frm.getMainPanel().getGridMaker().reset();
			frm.getMainPanel().getGridMaker().puyo(currentPuyo, action);
		}
	}

	public void moveRight() {
		if (!isTurnEnd) {
			if ((colmNumber < 5 && currentPuyo.getDirection() != PuyoDirection.RIGHT)
					|| (colmNumber < 4 && currentPuyo.getDirection() == PuyoDirection.RIGHT)) {
				colmNumber++;
			}
			action.setColmNumber(colmNumber);
			frm.getMainPanel().getGridMaker().reset();
			frm.getMainPanel().getGridMaker().puyo(currentPuyo, action);
		}
	}

	public void turnRight() {
		if (!isTurnEnd) {
			if (!(colmNumber == 0 && currentPuyo.getDirection() == PuyoDirection.DOWN)
					&& !(colmNumber == 5 && currentPuyo.getDirection() == PuyoDirection.UP))
				currentPuyo.rotate();
			action.setDirection(currentPuyo.getDirection());
			frm.getMainPanel().getGridMaker().reset();
			frm.getMainPanel().getGridMaker().puyo(currentPuyo, action);
		}
	}

	public void turnLeft() {
		if (!isTurnEnd) {
			if (!(colmNumber == 0 && currentPuyo.getDirection() == PuyoDirection.UP)
					&& !(colmNumber == 5 && currentPuyo.getDirection() == PuyoDirection.DOWN))
				currentPuyo.rotateReverse();
			action.setDirection(currentPuyo.getDirection());
			frm.getMainPanel().getGridMaker().reset();
			frm.getMainPanel().getGridMaker().puyo(currentPuyo, action);
		}
	}
	
	
	public void enter() {
		if (!isTurnEnd) {
			frm.getMainPanel().getGridMaker().reset();
			isTurnEnd = true;
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
