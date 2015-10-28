package com.gmail.kajiwara009;

import java.util.Map;

import com.gmail.kajiwara009.strategy.DebugStrategy;
import com.gmail.kajiwara009.strategy.PuyoStrategy;

import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.AbstractPlayer;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.Action;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.Field;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.Puyo;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.Puyo.PuyoDirection;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.Puyo.PuyoNumber;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.PuyoPuyo;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.storage.PuyoType;

public class KajiwaraAngel extends AbstractPlayer {

	public KajiwaraAngel(String playerName) {
		super(playerName);
		// TODO 自動生成されたコンストラクター・スタブ
	}

	@Override
	public Action doMyTurn() {
		
		Action action = new Action(PuyoDirection.DOWN,0);
		
		MyField curField = new MyField(getMyBoard().getField());
		
		PuyoStrategy str = new DebugStrategy(this);
		action = str.getAction(this);
		
		
/*		Field field = getMyBoard().getField();
		System.out.println(field.getHeight());
		Map<PuyoNumber, PuyoType> puyoTypesMap = null;
		puyoTypesMap.put(PuyoNumber.FIRST, PuyoType.BLUE_PUYO);
		puyoTypesMap.put(PuyoNumber.SECOND, PuyoType.RED_PUYO);
		Puyo puyo = new Puyo(puyoTypesMap);
*/		
		// TODO 自動生成されたメソッド・スタブ
		return action;
	}
	


	@Override
	public void initialize() {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public void inputResult() {
		// TODO 自動生成されたメソッド・スタブ

	}
	
	public static void main(String[] args){
		AbstractPlayer player = new KajiwaraAngel("Newプレイヤー");
		PuyoPuyo puyopuyo = new PuyoPuyo(player);
		
		puyopuyo.puyoPuyo();
	}

}
