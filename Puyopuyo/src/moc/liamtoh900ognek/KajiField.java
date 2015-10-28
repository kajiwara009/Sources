package moc.liamtoh900ognek;

import java.util.concurrent.ConcurrentHashMap;

import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.AbstractPlayer;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.Action;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.Board;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.Field;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.Puyo;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.Puyo.PuyoDirection;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.Puyo.PuyoNumber;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.PuyoPuyo;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.storage.PuyoType;

public class KajiField{
	private final int width = 6, height = 14;
	int[][] nextNum = new int[width][height];
	int[][] connectNum = new int[width][height];
	Field field;
	PuyoType[][] ptField = new PuyoType[width][height];

	public KajiField(Field f) {
		field = f;
		ptField = this.getPTField();
	}

	public KajiField(PuyoType[][] puyoTypeField){
		/** field は isOnField とかを利用するためだけに利用 */
		field = new Field(width, height);
		ptField = puyoTypeField;
	}

	/**
	 * フィールドのぷよタイプ配列を返す
	 * @return
	 */
	public PuyoType[][] getPTField(){
		for(int column = 0; column < width;column++){
			for(int row = 0; row < height; row++){
				ptField[column][row] = field.getPuyoType(column, row);
			}
		}
		return ptField;
	}

	/**
	 * 隣接しているぷよ数の配列を返す
	 * @author kengo
	 * @return
	 */
	private int[][] getNextPuyoSum(){

		for(int column = 0; column < width;column++){
			for(int row = 0; row < height; row++){
				if(ptField[column][row] != null){
					nextNum[column][row] += 1;
					
					if(field.isOnField(column - 1, row)){
						if(ptField[column - 1][row] == ptField[column][row]){
							nextNum[column][row]++;
						}
					}
					if(field.isOnField(column, row - 1)){
						if(ptField[column][row - 1] == ptField[column][row]){
							nextNum[column][row]++;
						}
					}
					if(field.isOnField(column + 1, row)){
						if(ptField[column + 1][row] == ptField[column][row]){
							nextNum[column][row]++;
						}
					}
					if(field.isOnField(column, row + 1)){
						if(ptField[column][row + 1] == ptField[column][row]){
							nextNum[column][row]++;
						}
					}
				}
			}
		}
		return nextNum;
	}

	public int[][] getConnectPuyoSum(){
		getNextPuyoSum();

		for(int c = 0; c < width;c++){
			for(int r = 0; r < height; r++){
				connectNum[c][r] = nextNum[c][r];
			}
		}

		for(int c = 0; c < width;c++){
			for(int r = 0; r < height; r++){

				if(connectNum[c][r] == 3){

					if(field.isOnField(c - 1, r)){
						if(ptField[c -1][r] == ptField[c][r]  && connectNum[c - 1][r] ==3 ){
							roundToN(c, r, 4);
							roundToN(c - 1, r, 4);
							break;
						}
					}

					if(field.isOnField(c, r - 1)){
						if(ptField[c][r - 1] == ptField[c][r]  && connectNum[c][r - 1] ==3 ){
							roundToN(c, r, 4);
							roundToN(c, r - 1, 4);
							break;
						}
					}
					roundToN(c, r, 3);
				}


			}
		}


		return connectNum;
	}

	public void setAPuyoType(PuyoType puyotype, int x, int y){
		if( field.isOnField(x, y) ){
			ptField[x][y] = puyotype;
		}
	}

	/***
	 * ぷよを落とした後の状況（消す前）
	 * @param puyo
	 * @param x
	 * @return
	 */
	public KajiField setPuyo(Puyo puyo, int x){
		PuyoType[][] puyoset = new PuyoType[width][height];
		/** ptFieldをコピー */
		for(int column = 0; column < width;column++){
			for(int row = 0; row < height; row++){
				puyoset[column][row] = ptField[column][row];
			}
		}






		return new KajiField(puyoset);
	}

	/***
	 * column列のぷよが存在する最大座標を返す。１つもないときは-1を返す。
	 * @param int column
	 * @return int
	 */
	public int getTopPoint(int column){
		int top = 0;
		while( ptField[column][top] != null ){
			top++;
		}
		top--;
		return top;
	}

	public KajiField getNextKajiField(Puyo puyo, Puyo.PuyoDirection dir){



		return null;
	}


	private void roundToN(int c, int r, int n){
		connectNum[c][r] = n;

		if(field.isOnField(c - 1, r)){
			if(ptField[c - 1][r] == ptField[c][r]){
				connectNum[c - 1][r] = n;
			}
		}

		if(field.isOnField(c, r - 1)){
			if(ptField[c][r - 1] == ptField[c][r]){
				connectNum[c][r - 1] = n;
			}
		}

		if(field.isOnField(c + 1, r)){
			if(ptField[c + 1][r] == ptField[c][r]){
				connectNum[c + 1][r] = n;
			}
		}

		if(field.isOnField(c, r + 1)){
			if(ptField[c][r + 1] == ptField[c][r]){
				connectNum[c][r + 1] = n;
			}
		}
	}

}