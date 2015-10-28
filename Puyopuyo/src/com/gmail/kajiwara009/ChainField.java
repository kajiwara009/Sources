package com.gmail.kajiwara009;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.Puyo;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.Puyo.PuyoNumber;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.storage.FieldPoint;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.storage.PuyoType;

public class ChainField {
	
	private int rensaPoint = 0;
	
	private int chainNum = 0;
	
	//連鎖後のフィールド
	private MyField field;
	

	public ChainField(MyField oriField, Puyo puyo, int columnNumber) {
		this.field = new MyField(oriField);
		
		
	}
	
}
