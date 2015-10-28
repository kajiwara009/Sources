package com.gmail.kajiwara009.strategy;

import com.gmail.kajiwara009.MyField;

import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.AbstractPlayer;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.Action;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.Puyo;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.Puyo.PuyoDirection;

public class DebugStrategy extends  PuyoStrategy{
	


	public DebugStrategy(AbstractPlayer player) {
		super(player);
		// TODO 自動生成されたコンストラクター・スタブ
	}

	@Override
	public Action getAction(AbstractPlayer player) {
		
		PuyoDirection dir;

		dir = PuyoDirection.DOWN;
		
		int top = field.getTop(0);
		
		field.printField();
		
		if(field.isEnableAndAlive(dir, 0)){
			System.out.println("置けるアクション");
			return new Action(dir, 0);
		}
		else {
			System.out.println("置けないAction");
			return new Action(dir, field.getWidth()-2);
		}
	}

}
