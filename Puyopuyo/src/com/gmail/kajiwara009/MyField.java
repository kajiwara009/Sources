package com.gmail.kajiwara009;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.gmail.kajiwara009.util.TwoValue;

import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.AbstractPlayer;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.Action;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.Field;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.Puyo;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.Puyo.PuyoDirection;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.Puyo.PuyoNumber;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.storage.FieldPoint;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.storage.PuyoType;

/**
 * ぷよを設置するフィールドのクラスです。
 * フィールドの座標は1段目の左から1列目が(0, 0)となります。
 */
public final class MyField {
	/**
	 * フィールド情報
	 * field[高さ][幅]
	 */
	private PuyoType[][] field;
	
	
	private Set<FieldPoint>[][] chainSet;

	/**
	 * フィールドの幅
	 */
	private final int WIDTH;

	/**
	 * フィールドの高さ
	 */
	private final int HEIGHT;

	/**
	 * コンストラクタ
	 */
	public MyField(int width, int height) {
		WIDTH = width;
		HEIGHT = height;

		field = new PuyoType[HEIGHT][WIDTH];
	}

	/**
	 * コピーコンストラクタ
	 */
	public MyField(MyField originalField) {
		WIDTH = originalField.WIDTH;
		HEIGHT = originalField.HEIGHT;
		this.field = new PuyoType[HEIGHT][WIDTH];

		for (int i = 0; i < HEIGHT; i++) {
			for (int j = 0; j < WIDTH; j++) {
				this.field[i][j] = originalField.field[i][j];
			}
		}
	}
	
	public MyField(Field field){
		WIDTH = field.getWidth();
		HEIGHT = field.getHeight();
		
		this.field = new PuyoType[HEIGHT][WIDTH];
		
		for(int i = 0; i < HEIGHT; i++){
			for(int j = 0; j < WIDTH; j++){
				this.field[i][j] = field.getPuyoType(j, i);
			}
		}
		
	}

	/**
	 * 指定された座標のぷよの種類を取得します。
	 * フィールド外の座標が指定された場合や、
	 * 指定された座標にぷよが置かれていなかった場合nullが返ります。
	 * @param x 横
	 * @param y 高さ
	 * @return
	 */
	public PuyoType getPuyoType(int x, int y) {
		if (isOnField(x, y)) {
			return  field[y][x];
		} else {
			return null;
		}
	}

	/**
	 * 指定された座標のぷよの種類を取得します。
	 * フィールド外の座標が指定された場合や、
	 * 指定された座標にぷよが置かれていなかった場合nullが返ります。
	 * @param point
	 * @return
	 */
	public PuyoType getPuyoType(FieldPoint point) {
		return getPuyoType(point.getX(), point.getY());
	}

	/**
	 * 指定された座標のぷよの種類を設定します。
	 * @param x
	 * @param y
	 * @param color
	 */
	protected void setPuyoType(int x, int y, PuyoType type) {
		if (isOnField(x, y)) {
			field[y][x] = type;
		}
	}

	/**
	 * 指定された座標のぷよの種類を設定します。
	 * @param point
	 * @param color
	 */
	protected void setPuyoType(FieldPoint point, PuyoType type) {
		setPuyoType(point.getX(), point.getY(), type);
	}

	/**
	 * フィールドの最大座標を取得します。
	 * @return
	 */
	public FieldPoint getMaxPoint() {
		return new FieldPoint(WIDTH - 1, HEIGHT - 1);
	}

	/**
	 * フィールドの最小座標を取得します。
	 * @return
	 */
	public FieldPoint getMinPoint() {
		return new FieldPoint(0, 0);
	}

	/**
	 * フィールドの幅を取得します。
	 * @return
	 */
	public int getWidth() {
		return WIDTH;
	}

	/**
	 * フィールドの高さを取得します。
	 * @return
	 */
	public int getHeight() {
		return HEIGHT;
	}

	public Set<FieldPoint>[][] getChainSet() {
		return chainSet;
	}

	public void setChainSet(Set<FieldPoint>[][] chainSet) {
		this.chainSet = chainSet;
	}

	/**
	 * 全消し状態かどうかを取得します。
	 * @return
	 */
	public boolean isAllClear() {
		for(int w = 0; w < WIDTH; w++){
			if(field[0][w] != null){
				return false;
			}
		}
		
/*		for (int i = 0; i < HEIGHT; i++) {
			for (int j = 0; j < WIDTH; j++) {
				if (field[i][j] != null) {
					return false;
				}
			}
		}
*/
		return true;
	}

	/**
	 * 指定された列でぷよが置かれている中で
	 * 一番上にある座標の段数を取得します。
	 * 指定された列にぷよが一つも置かれていない場合や，
	 * 列番号がフィールド外だった場合，-1が返ります。
	 * @param colmNumber
	 * @return
	 */
	public int getTop(int colmNumber) {
		if (colmNumber >= 0 && colmNumber < WIDTH) {
			for(int i = 0; i < HEIGHT ; i++){
				if(field[i][colmNumber] == null){
					return i - 1;
				}
			}
			return HEIGHT;
		}

		return -1;
	}

	/**
	 * 指定された列でぷよが置かれている中で
	 * 一番上にある座標を取得します。
	 * 指定された列にぷよが一つも置かれていない場合や，
	 * 列番号がフィールド外だった場合，nullが返ります。
	 * @param colmNumber
	 * @return
	 */
	public FieldPoint getTopPoint(int colmNumber) {
		int top = getTop(colmNumber);

		if (top >= 0) {
			return new FieldPoint(colmNumber, top);
		} else {
			return null;
		}
	}

	/**
	 * 指定された行動でぷよが置けるかどうか調べます。
	 * 置いた後，プレイヤーがまだ生きているかどうかは考慮しません。
	 * @param action
	 * @return
	 */
	public boolean isEnable(Action action) {
		PuyoDirection direction = action.getDirection();
		int colmNumber = action.getColmNumber();

		return isEnable(direction, colmNumber);
	}

	/**
	 * 指定されたぷよが指定された列に置けるかどうか調べます。
	 * Puyoは向き情報のみ考慮され，色は無視されます。
	 * 置いた後，プレイヤーがまだ生きているかどうかは考慮しません。
	 * @param puyo
	 * @param colmNumber
	 * @return
	 */
	public boolean isEnable(Puyo puyo, int colmNumber) {
		PuyoDirection direction = puyo.getDirection();

		return isEnable(direction, colmNumber);
	}

	/**
	 * 指定された方向、列での設置行動でぷよが置けるかどうか調べます。
	 * 置いた後，プレイヤーがまだ生きているかどうかは考慮しません。
	 * @param direction
	 * @param colmNumber
	 * @return
	 */
	public boolean isEnable(PuyoDirection direction, int colmNumber) {
		if (isOverWidth(colmNumber)) {
			return false;
		} else if (direction == PuyoDirection.RIGHT || direction == PuyoDirection.LEFT) {
			int secondColmNumber =
				colmNumber + Puyo.getSecondColmNumber(direction) ;

			if (isOverWidth(secondColmNumber)) {
				return false;
			} else {
				return true;
			}
		} else {
			return true;
		}
	}

	/***
	 * フィールドにぷよが置けてかつ死なない状況ならtrueを返す
	 * @param field
	 * @param dir
	 * @param i
	 * @return
	 */
	public boolean isEnableAndAlive(PuyoDirection dir, int i) {
		//配置不能ならfalse
		if(!isEnable(dir, i)){
			return false;
		}

		if(dir == PuyoDirection.DOWN || dir == PuyoDirection.UP){
			if(getTop(i) >= getDeadLine()-2){
				return false;
			}
		}
		else if(dir == PuyoDirection.RIGHT){
			if(getTop(i) >= getDeadLine()-1 || getTop(i+1) >= getDeadLine()-1) {
				return false;
			}
		}
		else if(dir == PuyoDirection.LEFT){
			if(getTop(i) >= getDeadLine()-1 || getTop(i-1) >= getDeadLine()-1) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 指定された座標がフィールド上にあるかどうか調べます。
	 * @param point
	 * @return
	 */
	public boolean isOnField(FieldPoint point) {
		return isOnField(point.getX(), point.getY());
	}

	/**
	 * 指定された座標がフィールド上にあるかどうか調べます。
	 * @param x 横
	 * @param y 高さ
	 * @return
	 */
	public boolean isOnField(int x, int y) {
		if (isOverWidth(x)) {
			return false;
		} else if (isOverHeight(y)) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 指定された列番号がフィールドの幅をはみ出しているかどうか調べます。
	 * @param colmNumber
	 * @return
	 */
	public boolean isOverWidth(int colmNumber) {
		if (colmNumber < 0 || colmNumber >= WIDTH) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 指定された段番号がフィールドの高さをはみ出しているかどうか調べます。
	 * @param rowNumber
	 * @return
	 */
	public boolean isOverHeight(int rowNumber) {
		if (rowNumber < 0 || rowNumber >= HEIGHT) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * このフィールドのプレイヤーが死亡しているかどうか調べます。
	 * (13段目に達していたら死亡)
	 * @return
	 */
	public boolean isDead() {
		int deadLine = getDeadLine();

		for (int j = 0; j < WIDTH; j++) {
			if (field[deadLine][j] != null) {
				return true;
			}
		}

		return false;
	}

	/**
	 * そこにぷよを置いてもプレイヤーが死亡しない最大の段数を取得します。
	 * @return
	 */
	public int getDeadLine() {
		return HEIGHT - 2;
	}
	
	public void printField(){
		for(int i = HEIGHT - 1; i >= 0; i--){
			for(int j = 0; j < WIDTH; j++){
				
				String puyoStr = "-";
				PuyoType type = getPuyoType(j, i);
				if(type != null){
					switch (type) {
					case BLUE_PUYO:
						puyoStr = "b";
						break;
					case GREEN_PUYO:
						puyoStr = "g";
						break;
					case OJAMA_PUYO:
						puyoStr = "o";
						break;
					case PURPLE_PUYO:
						puyoStr = "p";
						break;
					case RED_PUYO:
						puyoStr = "r";
						break;
					case YELLOW_PUYO:
						puyoStr = "y";
						break;
					}
				}
				System.out.print(puyoStr + " ");
			}
			System.out.println();
		}
	}
	
	

	/**
	 * 指定された列に
	 * 指定されたぷよを落下させた状態のフィールドを取得します。
	 * 向きはpuyoに設定されている方向です。
	 * puyoがフィールドからはみ出す場合はnullを返します。
	 * @param colmNumber
	 * @return
	 */
	public MyField getNextField(Puyo puyo, int colmNumber) {
		PuyoDirection direction = puyo.getDirection();
		MyField nextField = new MyField(this);
		Integer chainNum = 0;
		Integer ojamaPoint = 0;
		
		TwoValue<Integer, Integer> chain_ojama = new TwoValue<Integer, Integer>(chainNum, ojamaPoint);
		
		
		if (!isEnable(puyo.getDirection(), colmNumber)) {
			System.out.println("getNextField:不適切な置き方");
			return null;
		} else if (direction == PuyoDirection.RIGHT || direction == PuyoDirection.LEFT) {
			setHorizontalCurrentPuyo(nextField, puyo, colmNumber);
		} else {
			setVerticalCurrentPuyo(nextField, puyo, colmNumber);
		}
		
		if(nextField.firstErase(puyo, colmNumber, chain_ojama)){
			chainNum++;
			while(nextField.erase(chain_ojama)){
				chainNum++;
			};

		}


		return nextField;
	}

	/**
	 * 水平方向の現在降っているぷよを次の状態のフィールドに降らせます。
	 * @param nextField
	 * @param colmNumber
	 */
	private void setHorizontalCurrentPuyo(MyField nextField, Puyo puyo, int colmNumber) {
		PuyoType firstPuyoType = puyo.getPuyoType(PuyoNumber.FIRST);
		int firstTop = getTop(colmNumber);
		nextField.setPuyoType(colmNumber, firstTop + 1, firstPuyoType);

		int secondColmNumber = colmNumber + puyo.getSecondColmNumber();
		PuyoType secondPuyoType = puyo.getPuyoType(PuyoNumber.SECOND);
		int secondTop = getTop(secondColmNumber);
		nextField.setPuyoType(secondColmNumber, secondTop + 1, secondPuyoType);
	}

	/**
	 * 垂直方向の現在降っているぷよを次の状態のフィールドに降らせます。
	 * @param nextField
	 * @param colmNumber
	 */
	private void setVerticalCurrentPuyo(MyField nextField, Puyo puyo, int colmNumber) {
		int top = getTop(colmNumber);
		PuyoType bottomPuyoType =
				puyo.getPuyoType(puyo.getBottomPuyoNumber());
		PuyoType topPuyoType =
				puyo.getPuyoType(puyo.getTopPuyoNumber());
		nextField.setPuyoType(colmNumber, top + 1, bottomPuyoType);
		nextField.setPuyoType(colmNumber, top + 2, topPuyoType);
	}

	private boolean firstErase(Puyo puyo, int column, TwoValue<Integer, Integer> chain_ojama) {
		
		return true;
	}
	
	/**
	 * 
	 * @param chain_ojama 連鎖数とお邪魔ポイント
	 * @return
	 */
	private boolean erase(TwoValue<Integer, Integer> chain_ojama) {
		Map<FieldPoint, PuyoType> erasedPuyoPointsMap = new HashMap<FieldPoint, PuyoType>();
		Set<FieldPoint> erasedOjamaPuyoPointsSet = new HashSet<FieldPoint>();

		searchErasedPuyo(erasedPuyoPointsMap, erasedOjamaPuyoPointsSet);

		if (erasedPuyoPointsMap.isEmpty()) {
			return false;
		} else {
			eraseField(erasedPuyoPointsMap, erasedOjamaPuyoPointsSet);

			return true;
		}
	}

	private void eraseField(Map<FieldPoint, PuyoType> erasedPuyoPointsMap,
							Set<FieldPoint> erasedOjamaPuyoPointsSet) {
		Set<FieldPoint> erasedPuyoPointsSet =
			new HashSet<FieldPoint>(erasedPuyoPointsMap.keySet());
		erasedPuyoPointsSet.addAll(erasedOjamaPuyoPointsSet);

		List<FieldPoint> falledPointsList = new ArrayList<FieldPoint>();
		erasePuyo(erasedPuyoPointsSet, falledPointsList);
		eraseFall(falledPointsList);
	}

	private void erasePuyo(Set<FieldPoint> erasedPuyoPointsSet,
			List<FieldPoint> falledPointsList) {
		for (FieldPoint erasedPoint : erasedPuyoPointsSet) {
			FieldPoint upPoint = erasedPoint.getUpPoint();
			checkFall(upPoint, falledPointsList,
					erasedPuyoPointsSet);

			setPuyoType(erasedPoint, null);
		}
	}

	private void checkFall(FieldPoint point,
			List<FieldPoint> falledPointsList,
			Set<FieldPoint> erasedPuyoPointsSet) {
		int lastCheck = getLastCheck(point.getX());

		if (isOnField(point) && point.getY() <= lastCheck) {
			if (!erasedPuyoPointsSet.contains(point)) {
				falledPointsList.add(point);
			}

			FieldPoint upPoint = point.getUpPoint();
			checkFall(upPoint, falledPointsList, erasedPuyoPointsSet);
		}
	}

	private int getLastCheck(int x) {
		for (int i = HEIGHT - 1; i >= 0; i--) {
			if (getPuyoType(x, i) != null) {
				return i;
			}
		}

		return -1;
	}

	private void searchErasedPuyo(Map<FieldPoint, PuyoType> erasedPuyoPointsMap,
									Set<FieldPoint> erasedOjamaPuyoPointsSet) {
		for (int i = 0; i < HEIGHT - 2; i++) {
			for (int j = 0; j < WIDTH; j++) {
				FieldPoint point = new FieldPoint(j, i);
				PuyoType type = field[i][j];

				checkConnectedPoint(point, type, erasedPuyoPointsMap,
									erasedOjamaPuyoPointsSet);
			}
		}
	}

	private void eraseFall(List<FieldPoint> falledPointsList) {
		Map<FieldPoint, Integer> fallNumbersMap = new HashMap<FieldPoint,Integer>();
		calculateFallNumbers(fallNumbersMap, falledPointsList);

		int totalFallNumber = 0;

		boolean isBreak;
		do {
			isBreak = true;
				if (totalFallNumber < falledPointsList.size()) {
					int fallNumber = eraseFallEffect(fallNumbersMap);
					totalFallNumber += fallNumber;

					isBreak = false;
				}

				renewFallNumbersMap(fallNumbersMap);


		} while(!isBreak);
	}

	private void calculateFallNumbers(Map<FieldPoint, Integer> fallNumbersMap,
										List<FieldPoint> falledPointsList) {
		for (FieldPoint point : falledPointsList) {
			if (!fallNumbersMap.containsKey(point)) {
				fallNumbersMap.put(point, 1);
			} else {
				fallNumbersMap.put(point, fallNumbersMap.get(point) + 1);
			}
		}
	}

	/**
	 * 消去による落下のエフェクトです。
	 * @param board
	 * @param fallNumbersMap
	 * @return
	 */
	private int eraseFallEffect(Map<FieldPoint, Integer> fallNumbersMap) {
		Map<FieldPoint, PuyoType> refreshPointsMap = new HashMap<FieldPoint, PuyoType>();

		int currentFallNumber = 0;
		for (int i = 0; i < HEIGHT; i++) {
			for (FieldPoint point : fallNumbersMap.keySet()) {
				if (point.getY() == i && fallNumbersMap.get(point) > 0) {
					FieldPoint afterFallPoint = new FieldPoint(point.getX(), point.getY() - 1);
					setPuyoType(afterFallPoint, getPuyoType(point));
					refreshPointsMap.put(afterFallPoint, getPuyoType(point));
					setPuyoType(point, null);
					refreshPointsMap.put(point, null);

					currentFallNumber += 1;
				}
			}
		}

		return currentFallNumber;
	}

	/**
	 * 落下させる度に座標を更新します。
	 * @param fallNubmersMap
	 */
	private void renewFallNumbersMap(Map<FieldPoint, Integer> fallNubmersMap) {
		Map<FieldPoint, Integer> newFallNumbersMap = new HashMap<FieldPoint, Integer>();

		for (FieldPoint point : fallNubmersMap.keySet()) {
			int fallNumber = fallNubmersMap.get(point);
			if (fallNumber != 0) {
				FieldPoint newFalledPoint = new FieldPoint(point.getX(), point.getY() - 1);
				newFallNumbersMap.put(newFalledPoint, fallNumber - 1);
			}
		}
		fallNubmersMap.clear();
		fallNubmersMap.putAll(newFallNumbersMap);
	}

	/**
	 * 連結する座標を確認します。
	 *
	 * @param field
	 * @param point
	 * @param type
	 */
	private void checkConnectedPoint(FieldPoint point,
			PuyoType type, Map<FieldPoint, PuyoType> erasedPuyoPointsMap,
			Set<FieldPoint> erasedOjamaPuyoPointsSet) {
		if (type != null && type != PuyoType.OJAMA_PUYO
				&& !erasedPuyoPointsMap.containsKey(point)) {
			Map<FieldPoint, PuyoType> connectedPointsMap = new HashMap<FieldPoint, PuyoType>();
			checkConnection(point, type, connectedPointsMap);

			if (connectedPointsMap.size() >= 4) {
				renewErasedPointsMap(erasedPuyoPointsMap,
						erasedOjamaPuyoPointsSet, connectedPointsMap);
			}
		}
	}

	/**
	 * 四方との連結を確認します。
	 *
	 * @param field
	 * @param point
	 * @param type
	 * @param connectedPointsMap
	 */
	private void checkConnection(FieldPoint point, PuyoType type,
			Map<FieldPoint, PuyoType> connectedPointsMap) {
		if (getPuyoType(point) == type
				&& !connectedPointsMap.containsKey(point)
				&& point.getY() < HEIGHT - 2) {
			connectedPointsMap.put(point, type);

			FieldPoint leftPoint = point.getLeftPoint();
			if (isOnField(leftPoint)) {
				checkConnection(leftPoint, type, connectedPointsMap);
			}

			FieldPoint rightPoint = point.getRightPoint();
			if (isOnField(rightPoint)) {
				checkConnection(rightPoint, type, connectedPointsMap);
			}

			FieldPoint upPoint = point.getUpPoint();
			if (isOnField(upPoint)) {
				checkConnection(upPoint, type, connectedPointsMap);
			}

			FieldPoint downPoint = point.getDownPoint();
			if (isOnField(downPoint)) {
				checkConnection(downPoint, type, connectedPointsMap);
			}
		}
	}

	/**
	 * 消去対象のぷよを更新します。
	 *
	 * @param field
	 * @param connectedPointsMap
	 */
	private void renewErasedPointsMap(Map<FieldPoint, PuyoType> erasedPuyoPointsMap,
										Set<FieldPoint> erasedOjamaPuyoPointsSet,
										Map<FieldPoint, PuyoType> connectedPointsMap) {
		for (FieldPoint point : connectedPointsMap.keySet()) {
			erasedPuyoPointsMap.put(point, connectedPointsMap.get(point));

			checkErasedOjamaPuyo(erasedOjamaPuyoPointsSet, point);
		}
	}

	/**
	 * 消去対象のおじゃまぷよをチェックします。
	 *
	 * @param field
	 * @param point
	 */
	private void checkErasedOjamaPuyo(Set<FieldPoint> erasedOjamaPuyoPointsSet, FieldPoint point) {
		Set<FieldPoint> neighboringPointsSet = new HashSet<FieldPoint>();
		neighboringPointsSet.add(point.getLeftPoint());
		neighboringPointsSet.add(point.getRightPoint());
		neighboringPointsSet.add(point.getUpPoint());
		neighboringPointsSet.add(point.getDownPoint());

		for (FieldPoint neighboringPoint : neighboringPointsSet) {
			if (getPuyoType(neighboringPoint) == PuyoType.OJAMA_PUYO) {
				erasedOjamaPuyoPointsSet.add(neighboringPoint);
			}
		}
	}
}
