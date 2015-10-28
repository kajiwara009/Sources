package com.gmail.kajiwara009.strategy;

import com.gmail.kajiwara009.MyField;

import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.AbstractPlayer;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.Action;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.Board;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.Puyo;

public abstract class PuyoStrategy {
	protected AbstractPlayer player;
	protected Board board;
	protected MyField field;
	protected Puyo puyo;
	
	public PuyoStrategy(AbstractPlayer player){
		this.player = player;
		board = player.getMyBoard();
		field = new MyField(board.getField());
		puyo = board.getCurrentPuyo();
	}
		
	public abstract Action getAction(AbstractPlayer player);

}
