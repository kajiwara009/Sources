package jp.ac.nagoya_u.is.ss.kishii.usui.system.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;


import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.GameInfo.PlayerNumber;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.Puyo.PuyoDirection;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.Puyo.PuyoNumber;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.storage.FieldPoint;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.storage.PuyoType;


/**
 * ぷよの消去や落下などの操作に関するクラスです。
 */
final class PuyoOperator {
	/**
	 * 全消し得点
	 */
	protected static final int ALL_CLEAR_BONUS = 2100;

	/**
	 * 消去ぷよ数×ERASE_RATEが消去得点
	 */
	protected static final int ERASE_RATE = 10;

	/**
	 * おじゃまぷよ数のレート
	 */
	protected static final int OJAMA_RATE = 70;

	private final double WEIGHT;

	private final boolean MOTION;

	/**
	 * コンストラクタ
	 * @param playerInfo
	 * @param boardMaker
	 */
	public PuyoOperator(double weight, boolean motion) {
		WEIGHT = weight;
		MOTION = motion;
	}

	/**
	 * 指定されたボードに現在降っているぷよを落下させます。
	 * @param board
	 */
	protected void fallCurrentPuyo(Map<PlayerNumber, Board> boardsMap,
									Map<PlayerNumber, Integer> colmNumbersMap) {
		Map<PlayerNumber, Vector<List<PuyoType>>> fallTypesListVectorMap =
			new EnumMap<GameInfo.PlayerNumber, Vector<List<PuyoType>>>(PlayerNumber.class);

		int maxFall = 0;
		for (PlayerNumber playerNumber : PlayerNumber.values()) {
			Board board = boardsMap.get(playerNumber);
			if (board != null) {
				int width = board.getField().getWidth();

				Vector<List<PuyoType>> fallTypesListVector =
						new Vector<List<PuyoType>>(width);

				for (int i = 0; i < width; i++) {
					fallTypesListVector.add(null);
				}

				prepareFall(board, colmNumbersMap.get(playerNumber), fallTypesListVector);
				fallTypesListVectorMap.put(playerNumber, fallTypesListVector);

				for (int i = 0; i < width; i++) {
					maxFall = calculateMaxFall(board.getField(), i, maxFall);
				}
			}
		}

		if (MOTION == true) {
			for (int i = 0; i < maxFall; i++) {
				int current = boardsMap.get(PlayerNumber.ONE).getField().getHeight() - 1 - i;

				for (PlayerNumber playerNumber : PlayerNumber.values()) {
					Board board = boardsMap.get(playerNumber);
					if (board != null) {
						fallEffect(board, fallTypesListVectorMap.get(playerNumber), current);
					}
				}
				Wait.wait(WEIGHT);
			}
		} else {
			for (PlayerNumber playerNumber : PlayerNumber.values()) {
				Board board = boardsMap.get(playerNumber);
				if (board != null) {
					fallNoEffect(boardsMap.get(playerNumber),
								fallTypesListVectorMap.get(playerNumber));
				}
			}
		}
	}

	private int calculateMaxFall(Field field, int colmNumber, int maxFall) {
		int top = field.getTop(colmNumber);
		if (!field.isOverWidth(colmNumber)) {
			int fall = field.getHeight() - top - 1;

			if (maxFall < fall) {
				return fall;
			}
		}

		return maxFall;
	}


	/**
	 * ぷよ落下の準備を行います。
	 * 各列毎に落下するぷよの色をListに格納します。
	 * @param board
	 * @param fallColorsListMap 列番号と色のListのMap(関数内で変化する)
	 */
	protected void prepareFall(Board board, int fallingColmNumber,
								Vector<List<PuyoType>> fallTypesListVector) {
		Puyo currentPuyo = board.getCurrentPuyo();
		PuyoDirection direction = currentPuyo.getDirection();

		if (direction == PuyoDirection.RIGHT || direction == PuyoDirection.LEFT) {
			List<PuyoType> firstTypeList = new ArrayList<PuyoType>();
			firstTypeList.add(currentPuyo.getPuyoType(PuyoNumber.FIRST));
			fallTypesListVector.set(fallingColmNumber,
									firstTypeList);

			List<PuyoType> secondTypeList = new ArrayList<PuyoType>();
			secondTypeList.add(currentPuyo.getPuyoType(PuyoNumber.SECOND));
			fallTypesListVector.set(fallingColmNumber + currentPuyo.getSecondColmNumber(),
									secondTypeList);
		} else {
			List<PuyoType> fallTypesList = new ArrayList<PuyoType>();
			PuyoType bottomType =
					currentPuyo.getPuyoType(currentPuyo.getBottomPuyoNumber());
			fallTypesList.add(bottomType);

			int top = board.getField().getTop(fallingColmNumber);
			if (top < 12) {
				PuyoType topType =
						currentPuyo.getPuyoType(currentPuyo.getTopPuyoNumber());
				fallTypesList.add(topType);
			}

			fallTypesListVector.set(fallingColmNumber, fallTypesList);
		}
	}

	private void fallNoEffect(Board board, Vector<List<PuyoType>> fallTypesListVector) {
		Map<FieldPoint, PuyoType> refreshPointsMap =
			new HashMap<FieldPoint, PuyoType>();

		Field field = board.getField();

		for (int i = 0; i < fallTypesListVector.size(); i++) {
			List<PuyoType> fallTypesList = fallTypesListVector.get(i);

			if (fallTypesList != null) {
				int top = field.getTop(i);
				for (PuyoType type : fallTypesList) {
					field.setPuyoType(i, top + 1, type);
					refreshPointsMap.put(new FieldPoint(i, top + 1), type);
					top += 1;
				}
			}
		}

		for (FieldPoint point : refreshPointsMap.keySet()) {
			PuyoType type = refreshPointsMap.get(point);
			board.getBoardMaker().getPanel().partRefresh(type, point.getY(), point.getX());
		}
		Wait.wait(WEIGHT);
	}

	/**
	 * 落下のエフェクトです。
	 * 一段落下します。
	 * @param field
	 * @param fallTypesListMap
	 * @param current
	 */
	private void fallEffect(Board board, Vector<List<PuyoType>> fallTypesListVector,
							int current) {
		Map<FieldPoint, PuyoType> refreshPointsMap =
			new HashMap<FieldPoint, PuyoType>();

		Field field = board.getField();

		for (int i = 0; i < fallTypesListVector.size(); i++) {
			List<PuyoType> fallTypesList = fallTypesListVector.get(i);
			if (fallTypesList != null && current > field.getTop(i)) {
				if (fallTypesList.size() > 1) {
					fallMultiPuyoEffect(field, fallTypesList, i, current, refreshPointsMap);
				} else {
					fallSinglePuyoEffect(field, fallTypesList, i, current, refreshPointsMap);
				}
			}
		}

		for (FieldPoint point : refreshPointsMap.keySet()) {
			PuyoType type = refreshPointsMap.get(point);
			board.getBoardMaker().getPanel().partRefresh(type, point.getY(), point.getX());
		}
	}

	/**
	 * 一列で複数のぷよが落下する場合のエフェクトです。
	 * @param field
	 * @param fallTypesList
	 * @param colmNumber
	 * @param current
	 */
	private void fallMultiPuyoEffect(Field field, List<PuyoType> fallTypesList,
										int colmNumber, int current, Map<FieldPoint, PuyoType> refreshPointsMap) {
		for (int i = 0; i < fallTypesList.size(); i++) {
			if (current + i < field.getHeight()) {
				if (current < field.getHeight() - (i + 1)) {
					field.setPuyoType(colmNumber, current + i + 1, null);
					refreshPointsMap.put(new FieldPoint(colmNumber, current + i + 1), null);
				}
	 			PuyoType fallType = fallTypesList.get(i);
				field.setPuyoType(colmNumber, current + i, fallType);
				refreshPointsMap.put(new FieldPoint(colmNumber, current + i), fallType);
			}
		}
	}

	/**
	 * 一列でひとつのぷよが落下する場合のエフェクトです。
	 * @param field
	 * @param fallTypesList
	 * @param colmNumber
	 * @param current
	 */
	private void fallSinglePuyoEffect(Field field, List<PuyoType> fallTypesList,
									int colmNumber, int current, Map<FieldPoint, PuyoType> refreshPointsMap) {
		if (current < field.getHeight() - 1) {
			field.setPuyoType(colmNumber, current + 1, null);
			refreshPointsMap.put(new FieldPoint(colmNumber, current + 1), null);
		}
		PuyoType fallType = fallTypesList.get(0);
		field.setPuyoType(colmNumber, current, fallType);
		refreshPointsMap.put(new FieldPoint(colmNumber, current), fallType);
	}

	/**
	 * 指定されたボードに貯まっているおじゃまぷよを落下させます。
	 * @param board
	 */
	protected void fallOjamaPuyo(Board board) {
		int[] fallNumbersArray = new int[board.getField().getWidth()];
		for (int i = 0; i < fallNumbersArray.length; i++) {
			fallNumbersArray[i] = 0;
		}

		prepareOjamaFall(board, fallNumbersArray);

		Field field = board.getField();
		int maxFall = 0;
		for (int i = 0; i < fallNumbersArray.length; i++) {
			if (fallNumbersArray[i] > 0) {
				maxFall = calculateMaxFall(field, i, maxFall);
			}
		}

		ojamaFallEffect(board, fallNumbersArray, maxFall);

		board.fallOjamaPuyo();
	}

	/**
	 * おじゃまぷよの落下のエフェクトです。
	 * @param field
	 * @param fallNumbersArray
	 * @param current
	 */
	private void ojamaFallEffect(Board board, int[] fallNumbersArray, int maxFall) {
		Vector<List<PuyoType>> fallTypesListVector =
				new Vector<List<PuyoType>>(fallNumbersArray.length);

		for (int i = 0; i < fallNumbersArray.length; i++) {
			fallTypesListVector.add(null);

			int fallNumber = fallNumbersArray[i];
			List<PuyoType> fallTypesList = new ArrayList<PuyoType>();

			for (int j = 0; j < fallNumber; j++) {
				fallTypesList.add(PuyoType.OJAMA_PUYO);
			}
			if (fallTypesList.size() > 0) {
				fallTypesListVector.set(i, fallTypesList);
			}
		}

		if (MOTION == true) {
			for (int i = 0; i < maxFall; i++) {
				int current = board.getField().getHeight() - 1 - i;
				fallEffect(board, fallTypesListVector, current);
				Wait.wait(WEIGHT);
			}
		} else {
			fallNoEffect(board, fallTypesListVector);
		}
	}

	/**
	 * おじゃまぷよ落下の準備を行います。
	 * @param board
	 * @param fallNumbersArray 各列にいくつおじゃまぷよが落下するかを表すArray(関数内で変化)
	 */
	private void prepareOjamaFall(Board board, int[] fallNumbersArray) {
		int numbersOfOjama = board.getCurrentTurnNumberOfOjama();
		while (numbersOfOjama > 0) {
			if (Math.abs(numbersOfOjama) >= board.getField().getWidth()) {
				prepareFallManyOjamaPuyo(board, fallNumbersArray);

				numbersOfOjama -= board.getField().getWidth();
			} else {
				prepareFallFewOjamaPuyo(board, fallNumbersArray,
										Math.abs(numbersOfOjama));

				numbersOfOjama = 0;
			}
		}
	}

	/**
	 * 一列分以上のおじゃまぷよが降る場合の準備です。
	 * @param board
	 * @param fallNumbersArray 各列にいくつおじゃまぷよが落下するかを表すArray(関数内で変化)
	 */
	private void prepareFallManyOjamaPuyo(Board board, int[] fallNumbersArray) {
		for (int i = 0; i < fallNumbersArray.length; i++) {
			int whiteSpace =
				board.getField().getHeight() - board.getField().getTop(i) - 1;
			if (fallNumbersArray[i] < whiteSpace - 2){
				fallNumbersArray[i] += 1;
			}
		}
	}

	/**
	 * 一列分未満のおじゃまぷよが降る場合の準備です。
	 * @param board
	 * @param fallNumbersArray 各列にいくつおじゃまぷよが落下するかを表すArray(関数内で変化)
	 * @param number 降る個数
	 */
	private void prepareFallFewOjamaPuyo(Board board, int[] fallNumbersArray,
											int number) {
		List<Integer> lineList = new ArrayList<Integer>();
		for (int i = 0; i < board.getField().getWidth(); i++) {
			lineList.add(i);
		}
		Collections.shuffle(lineList);

		int falledNumbersOfOjama = 0;
		for (int colmNumber : lineList) {
			int whiteSpace =
				board.getField().getHeight() - board.getField().getTop(colmNumber) - 1;

			if (falledNumbersOfOjama >= number) {
				break;
			} else if (fallNumbersArray[colmNumber] < whiteSpace - 2) {
				fallNumbersArray[colmNumber] += 1;
				falledNumbersOfOjama += 1;
			}
		}
	}

	/**
	 * 指定されたボードからルールに従ってぷよを消去します。
	 * @param board
	 * @param playerInfo
	 * @param erasedPuyoPointsMap
	 * @param erasedOjamaPuyoPointsSet
	 * @return
	 */
	protected Map<PlayerNumber, Integer> eraseBoards(Map<PlayerNumber, Board> boardsMap, Map<PlayerNumber, PlayerInfo> playersInfoMap ,
								Map<PlayerNumber, Map<FieldPoint, PuyoType>> erasedPuyoPointsMapMap,
								Map<PlayerNumber, Set<FieldPoint>> erasedOjamaPuyoPointsSetMap) {
		Map<PlayerNumber, Integer> numbersOfOjamaMap =
			new EnumMap<GameInfo.PlayerNumber, Integer>(PlayerNumber.class);
		Map<PlayerNumber, List<FieldPoint>> falledPointsListMap =
			new EnumMap<GameInfo.PlayerNumber, List<FieldPoint>>(PlayerNumber.class);
		for (PlayerNumber playerNumber : PlayerNumber.values()) {
			Board board = boardsMap.get(playerNumber);
			Set<FieldPoint> erasedPuyoPointsSet =
				new HashSet<FieldPoint>(erasedPuyoPointsMapMap.get(playerNumber).keySet());
			erasedPuyoPointsSet.addAll(erasedOjamaPuyoPointsSetMap.get(playerNumber));

			List<FieldPoint> falledPointsList = new ArrayList<FieldPoint>();
			erasePuyo(board, erasedPuyoPointsSet, falledPointsList);

			falledPointsListMap.put(playerNumber, falledPointsList);
			for (FieldPoint point : erasedPuyoPointsSet) {
				board.getBoardMaker().getPanel().partRefresh(null, point.getY(), point.getX());
			}

			if (!erasedPuyoPointsSet.isEmpty()) {
				PlayerInfo playerInfo = playersInfoMap.get(playerNumber);
				int ojamaScore = calculateOjamaScore(board.getField(), playerInfo,
														erasedPuyoPointsMapMap.get(playerNumber));
				playerInfo.setOjamaScore(ojamaScore % OJAMA_RATE);

				numbersOfOjamaMap.put(playerNumber, ojamaScore / OJAMA_RATE);
			} else {
				numbersOfOjamaMap.put(playerNumber, 0);
			}
		}
		Wait.wait(WEIGHT);

		eraseFall(boardsMap, falledPointsListMap);

		return numbersOfOjamaMap;
	}

	/**
	 * 指定されたボードから指定された座標にあるぷよを消去します。
	 * 消去後，落下するぷよが存在する座標をチェックします。
	 * @param board
	 * @param erasedPoint
	 * @param falledPointsList 落下する座標のList(関数内で変化)
	 */
	private void erasePuyo(Board board, Set<FieldPoint> erasedPuyoPointsSet,
						   List<FieldPoint> falledPointsList) {
		for (FieldPoint erasedPoint : erasedPuyoPointsSet) {
			FieldPoint upPoint = erasedPoint.getUpPoint();
			checkFall(board.getField(), upPoint, falledPointsList, erasedPuyoPointsSet);

			board.getField().setPuyoType(erasedPoint, null);
		}
	}

	/**
	 * 消去後に落下するかどうかのチェックを行います。
	 * @param field
	 * @param point チェック対象の座標
	 * @param falledPointsList 落下する座標のList(関数内で変化)
	 */
	private void checkFall(Field field, FieldPoint point,
						   List<FieldPoint> falledPointsList,
						   Set<FieldPoint> erasedPuyoPointsSet) {
		int lastCheck = getLastCheck(field, point.getX());

		if (field.isOnField(point) &&
				point.getY() <= lastCheck) {
			if (!erasedPuyoPointsSet.contains(point)) {
				falledPointsList.add(point);
			}

			FieldPoint upPoint = point.getUpPoint();
			checkFall(field, upPoint, falledPointsList, erasedPuyoPointsSet);
		}
	}

	/**
	 * 最後に落下チェックを行う座標の段数を取得します。
	 * @param field
	 * @param x
	 * @return
	 */
	private int getLastCheck(Field field, int x) {
		for (int i = field.getHeight() - 1; i >= 0; i--) {
			if (field.getPuyoType(x, i) != null) {
				return i;
			}
		}

		return -1;
	}

	/**
	 * 消去による落下を行います。
	 * @param board
	 * @param falledPointsList
	 */
	private void eraseFall(Map<PlayerNumber, Board> boardsMap, Map<PlayerNumber, List<FieldPoint>> falledPointsListMap) {
		Map<PlayerNumber, Map<FieldPoint, Integer>> fallNumbersMapMap =
			new EnumMap<GameInfo.PlayerNumber, Map<FieldPoint,Integer>>(PlayerNumber.class);
		calculateFallNumbers(fallNumbersMapMap, falledPointsListMap);

		Map<PlayerNumber, Integer> totalFallNumbersMap =
			new EnumMap<PlayerNumber, Integer>(PlayerNumber.class);
		totalFallNumbersMap.put(PlayerNumber.ONE, 0);
		totalFallNumbersMap.put(PlayerNumber.TWO, 0);

		boolean isBreak;
		do {
			isBreak = true;
			for (PlayerNumber playerNumber : PlayerNumber.values()) {
				if (totalFallNumbersMap.get(playerNumber) < falledPointsListMap.get(playerNumber).size()) {
					int fallNumber = eraseFallEffect(boardsMap.get(playerNumber), fallNumbersMapMap.get(playerNumber));
					totalFallNumbersMap.put(playerNumber, fallNumber + totalFallNumbersMap.get(playerNumber));

					isBreak = false;
				}

				renewFallNumbersMap(fallNumbersMapMap.get(playerNumber));
			}

			if (MOTION == true) {
				Wait.wait(WEIGHT);
			}
		} while(!isBreak);

		if (MOTION == false) {
			eraseFallNoEffect(boardsMap, falledPointsListMap);
			Wait.wait(WEIGHT);
		}
	}

	private void calculateFallNumbers(Map<PlayerNumber, Map<FieldPoint, Integer>> fallNumbersMapMap,
										Map<PlayerNumber, List<FieldPoint>> falledPointsListMap) {
		for (PlayerNumber playerNumber : PlayerNumber.values()) {
			Map<FieldPoint, Integer> fallNumbersMap = new HashMap<FieldPoint, Integer>();
			for (FieldPoint point : falledPointsListMap.get(playerNumber)) {
				if (!fallNumbersMap.containsKey(point)) {
					fallNumbersMap.put(point, 1);
				} else {
					fallNumbersMap.put(point, fallNumbersMap.get(point) + 1);
				}
			}

			fallNumbersMapMap.put(playerNumber, fallNumbersMap);
		}
	}

	/**
	 * 消去による落下のエフェクトです。
	 * @param board
	 * @param fallNumbersMap
	 * @return
	 */
	private int eraseFallEffect(Board board, Map<FieldPoint, Integer> fallNumbersMap) {
		Map<FieldPoint, PuyoType> refreshPointsMap = new HashMap<FieldPoint, PuyoType>();

		Field field = board.getField();

		int currentFallNumber = 0;
		for (int i = 0; i < field.getHeight(); i++) {
			for (FieldPoint point : fallNumbersMap.keySet()) {
				if (point.getY() == i && fallNumbersMap.get(point) > 0) {
					FieldPoint afterFallPoint = new FieldPoint(point.getX(), point.getY() - 1);
					field.setPuyoType(afterFallPoint, field.getPuyoType(point));
					refreshPointsMap.put(afterFallPoint, field.getPuyoType(point));
					field.setPuyoType(point, null);
					refreshPointsMap.put(point, null);

					currentFallNumber += 1;
				}
			}
		}

		if (MOTION == true) {
			for (FieldPoint point : refreshPointsMap.keySet()) {
				PuyoType type = refreshPointsMap.get(point);
				board.getBoardMaker().getPanel().partRefresh(type, point.getY(), point.getX());
			}
		}

		return currentFallNumber;
	}

	private void eraseFallNoEffect(Map<PlayerNumber, Board> boardsMap,
									Map<PlayerNumber, List<FieldPoint>> falledPointsListMap) {
		for (PlayerNumber playerNumber : PlayerNumber.values()) {
			Board board = boardsMap.get(playerNumber);
			if (board != null) {
				for (int i = 0; i < board.getField().getWidth(); i++) {
					for (FieldPoint point : falledPointsListMap.get(playerNumber)) {
						if (point.getX() == i) {
							board.getBoardMaker().getPanel().lineRefresh(board.getField(), i);
							break;
						}
					}
				}
			}
		}
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
	 * おじゃまぷよに関する得点の計算を行います。
	 * @return
	 */
	private int calculateOjamaScore(Field field, PlayerInfo playerInfo,
									Map<FieldPoint, PuyoType> erasedPuyoPointsMap) {
		int chainNumber = playerInfo.getLastNumberOfChain();
		int typeNumber = calculateTypeNumber(erasedPuyoPointsMap);
		Map<PuyoType, Integer> connectionNumbers = calculateConnectionNumbers(erasedPuyoPointsMap);

		int chainBonus = calculateChainBonus(chainNumber);
		int typeBonus = calculateTypeBonus(typeNumber);
		int connectionBonus = 0;
		for (PuyoType type : connectionNumbers.keySet()) {
			int connectionNumber = connectionNumbers.get(type);
			connectionBonus += calculateConnectionBonus(connectionNumber);
		}

		int bonus = chainBonus + typeBonus + connectionBonus;
		if (bonus == 0) {
			bonus = 1;
		}

		int erasedPuyoNumber = erasedPuyoPointsMap.size();
		int ojamaScore = playerInfo.getOjamaScore() +
							erasedPuyoNumber * ERASE_RATE * bonus;

		if (playerInfo.isAllClearBonus()) {
			ojamaScore += ALL_CLEAR_BONUS;
			playerInfo.setAllClearBonus(false);
		} else if (field.isAllClear()) {
			playerInfo.setAllClearBonus(true);
		}

		return ojamaScore;
	}

	/**
	 * 消去したぷよの色数を計算します。
	 * @return
	 */
	private int calculateTypeNumber(Map<FieldPoint, PuyoType> erasedPuyoPointsMap) {
		Set<PuyoType> existTypesSet = new HashSet<PuyoType>();
		for (FieldPoint point : erasedPuyoPointsMap.keySet()) {
			PuyoType type = erasedPuyoPointsMap.get(point);
			if (!existTypesSet.contains(type)) {
				existTypesSet.add(type);
			}
		}

		return existTypesSet.size();
	}

	/**
	 * 消去したぷよの連結数を計算します。
	 * @return
	 */
	private Map<PuyoType, Integer> calculateConnectionNumbers(Map<FieldPoint, PuyoType> erasedPuyoPointsMap) {
		Map<PuyoType, Integer> connectionNumbers = new HashMap<PuyoType, Integer>();
		for (FieldPoint point : erasedPuyoPointsMap.keySet()) {
			PuyoType type = erasedPuyoPointsMap.get(point);
			if (!connectionNumbers.containsKey(type)) {
				connectionNumbers.put(type, 1);
			} else {
				int connectionNumber = connectionNumbers.get(type);
				connectionNumbers.put(type, connectionNumber + 1);
			}
		}

		return connectionNumbers;
	}

	/**
	 * 連鎖ボーナスを計算します。
	 * @param chainNumber
	 * @return
	 */
	private int calculateChainBonus(int chainNumber) {
		switch (chainNumber) {
		case 1:
			return 0;
		case 2:
			return 8;
		case 3:
			return 16;
		default :
			return (chainNumber - 3) * 32;
		}
	}

	/**
	 * 多色ボーナスを計算します。
	 * @param typeNumber
	 * @return
	 */
	private int calculateTypeBonus(int typeNumber) {
		switch (typeNumber) {
		case 1:
			return 0;
		case 2:
			return 3;
		case 3:
			return 6;
		case 4:
			return 12;
		default :
			return 12;
		}
	}

	/**
	 * 多連結ボーナスを計算します。
	 * @param connectionNumber
	 * @return
	 */
	private int calculateConnectionBonus(int connectionNumber) {
		if (connectionNumber <= 4) {
			return 0;
		} else if (connectionNumber >= 11) {
			return 10;
		} else {
			return (connectionNumber - 3);
		}
	}

	/**
	 * 指定されたフィールドにルールに従って消去するぷよがあるかどうかチェックします。
	 * 消去対象のぷよはerasedPuyoPointsMap, erasedOjamaPuyoPointsSetに格納されます。
	 * @param field
	 * @return
	 */
	protected void searchErasedPuyo(Field field, PlayerInfo playerInfo,
									Map<FieldPoint, PuyoType> erasedPuyoPointsMap,
									Set<FieldPoint> erasedOjamaPuyoPointsSet) {
		for (int i = 0; i < field.getHeight() - 2; i++) {
			for (int j = 0; j < field.getWidth(); j++) {
				FieldPoint point = new FieldPoint(j, i);
				PuyoType type = field.getPuyoType(point);

				checkConnectedPoint(field, point, type, erasedPuyoPointsMap, erasedOjamaPuyoPointsSet);
			}
		}

		if (!erasedPuyoPointsMap.isEmpty()) {
			playerInfo.setLastNumberOfChain(playerInfo.getLastNumberOfChain() + 1);
		}
	}

	/**
	 * 連結する座標を確認します。
	 * @param field
	 * @param point
	 * @param type
	 */
	private void checkConnectedPoint(Field field, FieldPoint point, PuyoType type,
										Map<FieldPoint, PuyoType> erasedPuyoPointsMap,
										Set<FieldPoint> erasedOjamaPuyoPointsSet) {
		if (type != null &&
				type != PuyoType.OJAMA_PUYO &&
				!erasedPuyoPointsMap.containsKey(point)) {
			Map<FieldPoint, PuyoType> connectedPointsMap = new HashMap<FieldPoint, PuyoType>();
			checkConnection(field, point, type, connectedPointsMap);

			if (connectedPointsMap.size() >= 4) {
				renewErasedPointsMap(erasedPuyoPointsMap, erasedOjamaPuyoPointsSet,
										field, connectedPointsMap);
			}
		}
	}

	/**
	 * 四方との連結を確認します。
	 * @param field
	 * @param point
	 * @param type
	 * @param connectedPointsMap
	 */
	private void checkConnection(Field field, FieldPoint point, PuyoType type,
								 Map<FieldPoint, PuyoType> connectedPointsMap) {
		if (field.getPuyoType(point) == type &&
				!connectedPointsMap.containsKey(point) &&
				point.getY() < field.getHeight() - 2) {
			connectedPointsMap.put(point, type);

			FieldPoint leftPoint = point.getLeftPoint();
			if (field.isOnField(leftPoint)) {
				checkConnection(field, leftPoint, type, connectedPointsMap);
			}

			FieldPoint rightPoint = point.getRightPoint();
			if (field.isOnField(rightPoint)) {
				checkConnection(field, rightPoint, type, connectedPointsMap);
			}

			FieldPoint upPoint = point.getUpPoint();
			if (field.isOnField(upPoint)) {
				checkConnection(field, upPoint, type, connectedPointsMap);
			}

			FieldPoint downPoint = point.getDownPoint();
			if (field.isOnField(downPoint)) {
				checkConnection(field, downPoint, type, connectedPointsMap);
			}
		}
	}

	/**
	 * 消去対象のぷよを更新します。
	 * @param field
	 * @param connectedPointsMap
	 */
	private void renewErasedPointsMap(Map<FieldPoint, PuyoType> erasedPuyoPointsMap, Set<FieldPoint> erasedOjamaPuyoPointsSet,
										Field field, Map<FieldPoint, PuyoType> connectedPointsMap) {
		for (FieldPoint point : connectedPointsMap.keySet()) {
			erasedPuyoPointsMap.put(point, connectedPointsMap.get(point));

			checkErasedOjamaPuyo(erasedOjamaPuyoPointsSet, field, point);
		}
	}

	/**
	 * 消去対象のおじゃまぷよをチェックします。
	 * @param field
	 * @param point
	 */
	private void checkErasedOjamaPuyo(Set<FieldPoint> erasedOjamaPuyoPointsSet,
										Field field, FieldPoint point) {
		Set<FieldPoint> neighboringPointsSet = new HashSet<FieldPoint>();
		neighboringPointsSet.add(point.getLeftPoint());
		neighboringPointsSet.add(point.getRightPoint());
		neighboringPointsSet.add(point.getUpPoint());
		neighboringPointsSet.add(point.getDownPoint());

		for (FieldPoint neighboringPoint : neighboringPointsSet) {
			if (field.getPuyoType(neighboringPoint) == PuyoType.OJAMA_PUYO ) {
				erasedOjamaPuyoPointsSet.add(neighboringPoint);
			}
		}
	}
}