//ハンデ用
//		try{
//			Thread.sleep(500);
//		}catch(Exception e){
//
//		}


//		long start = System.currentTimeMillis();

//		long stop = System.currentTimeMillis();
//		System.out.println("実行にかかった時間は " + (stop - start) + " ミリ秒です。");
//   }


package moc.liamtoh900ognek;


import java.util.*;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.AbstractPlayer;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.Action;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.Board;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.Field;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.GameInfo;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.PlayerInfo;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.Puyo;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.Puyo.PuyoDirection;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.PuyoPuyo;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.PuyoPuyoSettingData;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.storage.PuyoType;

/** width = 6, height = 14, deadline = 12, maxpoint = [5][13]*/
public class KajiGodGod extends AbstractPlayer {
	Action action;
	private final int width = 6;
	private final int height = 14;
	private final int FIELDOJAMASUM = 12;
	private final int ToOiuchiSum = 12;
	private int NormalClear = 4 * 4;
	private final int OiuchiClear = 4 * 3;
	private final int AllClearClear = 4 * 1;
	private final int AttackedClear = 4 * 2;
	private final int HoriHoriClear = 4 * 2;
	private final int CLEARPENALTY = 0;
	private int ableClearSum, ableClear1, ableClear2, ableClear3, aimClear, enemyAbleClearSum = 0;
	private int clearPenalty;
	boolean clearFlag, attacked, horihori;
	PlayerInfo puyoInfo = new PlayerInfo(getPlayerName(), 0);;
	PuyoPuyoSettingData setting = new PuyoPuyoSettingData();
	GameInfo gameInfo = new GameInfo(puyoInfo, setting);
//	GameInfo.PlayerNumber playerNum;
//	PuyoPuyoSettingData a = gameInfo.getSettingData();

	Board board;
	Puyo puyo1, puyo2, puyo3, enemyPuyo1, enemyPuyo2, enemyPuyo3;
	Field field, nextField1, nextField2, nextField3, enemyField, enemyNextField1, enemyNextField2, enemyNextField3;
	PuyoDirection dir1, dir2, dir3, enemyDir1, enemyDir2, enemyDir3;
	int ojamaSum;
	int fieldOjamaSum, sumPuyoOnField, sumPuyoOnNextField1, sumPuyoOnNextField2, sumPuyoOnNextField3;

	int point,whenToFire;
	
	long startTime;

	public KajiGodGod(String playerName) {
		super(playerName);
	}
	@Override
	public Action doMyTurn() {
		startTime = System.currentTimeMillis();
		action = null;
//		System.out.println( setting.getMaxNumberOfWinning()	);
		getInfo();
/** おじゃまぷよリスト作成 */
		List<Integer> list = board.getNumbersOfOjamaList();
		int[] ojamaPuyoList = new int[list.size()];/** iが0でこのアクション終えたら降ってくる */
		for(int i = 0; i < list.size(); i++){
			ojamaPuyoList[i] = list.get(i);
//			System.out.print(ojamaPuyoList[i] + " ");
		}
		System.out.println(" 溜まってるおじゃまの合計は" + ojamaSum);
/** Action決定 */
		if(ojamaSum >= 4){
/** 相手が攻撃してきたとき */
			attacked = true;

		}else if(fieldOjamaSum >= FIELDOJAMASUM){
/** Fieldにおじゃまぷよが溜まっているとき */
			horihori = true;
		}

		action = getNormalAction(ojamaPuyoList);
		long pre = System.currentTimeMillis();
		System.out.println("ato" + (pre - startTime));
		startTime = pre;

		return action;
	}




	/***
	 * 危ないときのActionを求める
	 * @param ojamaPuyoList
	 * @return
	 */
	private Action getDangerAction(int[] ojamaPuyoList){
		return null;
	}


	/***
	 * おじゃまぷよが溜まっているときのActionを求める
	 * @return
	 */
	private Action getHoriHoriAction(){



		return null;
	}




	/***
	 * 普通のときのActionを求める（追い打ちモードも含む）
	 * @return
	 */
	private Action getNormalAction(int[] ojamaPuyoList){
		
/** 状況に応じて目標連鎖数を決める */
		if(getFieldOjamaSum(enemyField) > ToOiuchiSum){
			aimClear = OiuchiClear;
		}else if(attacked){
			aimClear = AttackedClear;
		}else if(puyoInfo.isAllClearBonus()){
			aimClear = AllClearClear;
		}else if(horihori){
			aimClear = HoriHoriClear;
		}else{
			aimClear = NormalClear;
			if(enemyField != field){

				enemyAbleClearSum = getEnemyAbleClearSum();

				System.out.println("相手は" + enemyAbleClearSum + "個消せる");
			}
			if(enemyAbleClearSum > NormalClear){
				aimClear = enemyAbleClearSum;
			}
		}

/**
 * 全消し判定→→→大量消し判定→→→(大量消しが無ければ)点数判定
 */
		
		for(int c1 = 0; c1 < width; c1++){
			for(int rotateSum1 = 0; rotateSum1 < 4; rotateSum1++, dir1 = dir1.rotate(), puyo1.setDirection(dir1)){
				if(!(isEnable(field, puyo1, c1))) continue;
				nextField1 = field.getNextField(puyo1, c1);
				sumPuyoOnNextField1 = getSumPuyoOnField(nextField1);
				ableClear1 =  sumPuyoOnField + 2 - sumPuyoOnNextField1;
				
				if(nextField1.isAllClear()){
					System.out.println("全消し！");
					return new Action(puyo1, c1);
				}else if(ableClear1 >= aimClear  && ableClear1 >= ableClearSum ){
					if(ableClearSum == ableClear1){
						if(point < getPoint(nextField1) || whenToFire >= 1){
							point = getPoint(nextField1);
							action = new Action(puyo1, c1);
							whenToFire = 0;
						}
					}else{
						point = getPoint(nextField1);
						ableClearSum = ableClear1;
						action = new Action(puyo1, c1);
						whenToFire = 0;
					}
					clearFlag = true;
				}else if(ableClear1 >= 4){
					clearPenalty = CLEARPENALTY;
				}else{
					clearPenalty = 0;
				}



				for(int c2 = 0; c2 < width; c2++){
/*
					long now = System.currentTimeMillis();
					if(now - pre > 970){
						//System.out.println("妥協");
						continue;
					}
*/					
					for(int rotateSum2 = 0; rotateSum2 < 4; rotateSum2++, dir2 = dir2.rotate(), puyo2.setDirection(dir2)){
						
						if(!(isEnable(nextField1, puyo2, c2))) continue;
						nextField2 = nextField1.getNextField(puyo2, c2);
						sumPuyoOnNextField2 = getSumPuyoOnField(nextField2);
						ableClear2 =  sumPuyoOnNextField1 + 2 - sumPuyoOnNextField2 - clearPenalty;

						if(nextField2.isAllClear()){
							if(attacked){
								if(ojamaPuyoList[0] != 0){
								continue;
								}
							}
							System.out.println("次のターンに全消しするよ！");
							return new Action(puyo1, c1);
						}else if(ableClear2 >= aimClear  && ableClear2 >= ableClearSum ){
							if(attacked){
								if(ojamaPuyoList[0] != 0){
								continue;
								}
							}
							if(ableClearSum == ableClear2){
								if(whenToFire == 0) continue;
								if(point < getPoint(nextField2) || whenToFire == 2 ){
									point = getPoint(nextField2);
									action = new Action(puyo1, c1);
									whenToFire = 1;
								}
							}else{
								point = getPoint(nextField2);
								ableClearSum = ableClear2;
								action = new Action(puyo1, c1);
								whenToFire = 1;
							}
							clearFlag = true;
						}else if(ableClear2 >= 4){
							clearPenalty = CLEARPENALTY;
						}

						for(int c3 = 0; c3 < width; c3++){
							
							for(int rotateSum3 = 0; rotateSum3 < 4; rotateSum3++, dir3 = dir3.rotate(), puyo3.setDirection(dir3)){
								if(!(isEnable(nextField2, puyo3, c3))) continue;
								nextField3 = nextField2.getNextField(puyo3, c3);
/*								if(nextField3 == null){
									System.err.println(nextField2 + "   " + nextField1);
									System.err.println("getNormalActionでnextField3がNULL");
								}
*/
								sumPuyoOnNextField3 = getSumPuyoOnField(nextField3);
								ableClear3 =  sumPuyoOnNextField2 + 2 - sumPuyoOnNextField3 - clearPenalty;

								if(nextField3.isAllClear()){
									if(attacked){
										if(ojamaPuyoList[1] != 0 || ojamaPuyoList[0] != 0){
										continue;
										}
									}
									System.out.println("次の次のターンに全消しするよ！");
									return new Action(puyo1, c1);
								}else if(ableClear3 >= aimClear  && ableClear3 >= ableClearSum ){
									if(attacked){
										if(ojamaPuyoList[1] != 0 || ojamaPuyoList[0] != 0){
										continue;
										}
									}
									if(ableClearSum == ableClear3){
										if(whenToFire <= 1) continue;
										if(point < getPoint(nextField3)){
											point = getPoint(nextField3);
											action = new Action(puyo1, c1);
											whenToFire = 2;
										}
									}else{
										point = getPoint(nextField3);
										ableClearSum = ableClear3;
										action = new Action(puyo1, c1);
										whenToFire = 2;
									}
									clearFlag = true;
								}
/**
 * ここまで到達したら３手先まで全消し無し。大量消しの場合はフラグがtrue
 */
								if(sumPuyoOnNextField3 - sumPuyoOnField < 6) continue;
								if(clearFlag) continue;
								if(getPoint(nextField3) > point){
									point = getPoint(nextField3);
									action = new Action(puyo1, c1);
								}



							}
						}


					}
				}


			}
		}

		if(clearFlag) System.out.println(whenToFire + "ターン後に" + ableClearSum + "個消すよ");
		return action;
	}


















	/***
	 * フィールドの様々な情報を取得する
	 */
	private void getInfo(){
		board = getMyBoard();
		field = board.getField();
		puyo1 = board.getCurrentPuyo();
		puyo2 = board.getNextPuyo();
		puyo3 = board.getNextNextPuyo();
		enemyPuyo1 = board.getCurrentPuyo();
		enemyPuyo2 = board.getNextPuyo();
		enemyPuyo3 = board.getNextNextPuyo();
		puyoInfo = new PlayerInfo(getPlayerName(), 0);
		sumPuyoOnField = getSumPuyoOnField(field);
		ojamaSum = board.getTotalNumberOfOjama();
		fieldOjamaSum = getFieldOjamaSum(field);
		dir1 = PuyoDirection.DOWN;	puyo1.setDirection(dir1);
		dir2 = PuyoDirection.DOWN;	puyo2.setDirection(dir2);
		dir3 = PuyoDirection.DOWN;	puyo3.setDirection(dir3);
		enemyDir1 = PuyoDirection.DOWN;	enemyPuyo1.setDirection(enemyDir1);
		enemyDir2 = PuyoDirection.DOWN;	enemyPuyo2.setDirection(enemyDir2);
		enemyDir3 = PuyoDirection.DOWN;	enemyPuyo3.setDirection(enemyDir3);
		ableClearSum = 0;
		clearFlag = false;
		attacked = false;
		horihori = false;
		point = 0;
		try{
			enemyField = getEnemyBoard().getField();
		}catch(Exception e){
			enemyField = field;
		}
	}


	/***
	 * フィールド上のおじゃまぷよ数を取得する
	 * @param field
	 * @return
	 */
	private int getFieldOjamaSum(Field field){
		int count = 0;
		for(int i = 0; i < width; i++){
			for(int j = 0; j < height; j++){
				if(field.getPuyoType(i, j) == PuyoType.OJAMA_PUYO){
					count++;
				}
			}
		}
		return count;
	}


	/***
	 * フィールド上のぷよの合計を求める
	 * @param field
	 * @return
	 */
	public int getSumPuyoOnField(Field field){
		int sum = 0;
		for(int i = 0; i < width; i++){
			sum += field.getTop(i)+1;
		}
		return sum;
	}


	private int getEnemyAbleClearSum(){
		int enemySumPuyoOnField = getSumPuyoOnField(enemyField);
		int enemyAbleClearSum = 0;
		int ableSumClear1, ableSumClear2, ableSumClear3;
		for(int c1 = 0; c1 < width; c1++){
			for(int rotateSum1 = 0; rotateSum1 < 4; rotateSum1++, enemyDir1 = enemyDir1.rotate(), enemyPuyo1.setDirection(enemyDir1)){
				if(!(isEnable(enemyField, enemyPuyo1, c1))) continue;
				enemyNextField1 = enemyField.getNextField(enemyPuyo1, c1);
				ableSumClear1 = enemySumPuyoOnField + 2 - getSumPuyoOnField(enemyNextField1);
				if(ableSumClear1 > 0 ){
					enemyAbleClearSum = Math.max( ableSumClear1 , enemyAbleClearSum);;
					continue;
				}


				for(int c2 = 0; c2 < width; c2++){
					for(int rotateSum2 = 0; rotateSum2 < 4; rotateSum2++, enemyDir2 = enemyDir2.rotate(), enemyPuyo2.setDirection(enemyDir2)){
						if(!(isEnable(enemyNextField1, enemyPuyo2, c2))) continue;
						enemyNextField2 = enemyNextField1.getNextField(enemyPuyo2, c2);
						ableSumClear2 = enemySumPuyoOnField + 4 - getSumPuyoOnField(enemyNextField2);
						if(ableSumClear2 > 0 ){
							enemyAbleClearSum = Math.max( ableSumClear2 , enemyAbleClearSum);;
							continue;
						}

						for(int c3 = 0; c3 < width; c3++){
							for(int rotateSum3 = 0; rotateSum3 < 4; rotateSum3++, enemyDir3 = enemyDir3.rotate(), enemyPuyo3.setDirection(enemyDir3)){
								if(!(isEnable(enemyNextField2, enemyPuyo3, c3))) continue;
								enemyNextField3 = enemyNextField2.getNextField(enemyPuyo3, c3);
								ableSumClear3 = enemySumPuyoOnField + 6 - getSumPuyoOnField(enemyNextField3);
								if(ableSumClear3 > 6 ){
									enemyAbleClearSum = Math.max( ableSumClear3, enemyAbleClearSum);;
									continue;
								}

							}
						}


					}
				}


			}
		}

		return enemyAbleClearSum;
	}

	/***
	 * フィールドにぷよが置けてかつ死なない状況ならtrueを返す
	 * @param field
	 * @param dir
	 * @param i
	 * @return
	 */
	private boolean isEnable(Field field, Puyo puyo, int i) {
		PuyoDirection dir = puyo.getDirection();
		//配置不能ならfalse
		if(!field.isEnable(dir, i)){
			return false;
		}

		if(dir == PuyoDirection.DOWN || dir == PuyoDirection.UP){
			if(field.getTop(i) >= field.getDeadLine()-2){
				return false;
			}
		}
		else if(dir == PuyoDirection.RIGHT){
			if(field.getTop(i) >= field.getDeadLine()-1 || field.getTop(i+1) >= field.getDeadLine()-1) {
				return false;
			}
		}
		else if(dir == PuyoDirection.LEFT){
			if(field.getTop(i) >= field.getDeadLine()-1 || field.getTop(i-1) >= field.getDeadLine()-1) {
				return false;
			}
		}
		return true;
	}

	/***
	 * フィールドの状態の点数を返す
	 * @param field
	 * @return
	 */
	private int getPoint(Field field){
/***
 * pointConnectは ０～１００
 * pointIsolateMinusは ０～８０
 * pointOverは ０～４０
 * pointFireは ０～４０
 */
		int pointFinal = 0, pointConnect = 0, pointIsolateMinus = 0, pointOver = 0, pointFire = 0, pointDivMinus = 0, pointDivTopMinus = 0;
		int isolateRoom;
		int sumPuyoOnField = getSumPuyoOnField(field);
		KajiField kajiField = new KajiField(field);
		int[][] connectPuyo = kajiField.getConnectPuyoSum();
		PuyoType[][] puyoType = kajiField.getPTField();
		boolean countOverFlag, countFireFrag;

		for(int c = 0; c < width; c++){
			for(int r = 0; r < height; r++){
				pointConnect += Math.pow(connectPuyo[c][r], 3);
				if(connectPuyo[c][r] == 1){
					isolateRoom = 0;
					if(r < height - 1 && connectPuyo[c][r + 1] == 0) isolateRoom++;
					if(c >= 1){
						if(connectPuyo[c - 1][r] == 0) isolateRoom++;
					}
					if(c <= 4){
						if(connectPuyo[c + 1][r] == 0) isolateRoom++;
					}

					switch(isolateRoom){
					case 0: pointIsolateMinus -= 100; break;
					case 1: pointIsolateMinus -= 3; break;
					case 2: pointIsolateMinus -= 0; break;
					case 3: pointIsolateMinus -= 0; break;
					}
				}

				if(connectPuyo[c][r] == 3){
					for(int c2 = c -1; c2 <= c + 1; c2++){
						if(c2 == -1 || c2 == 6) continue;
						countOverFlag = false;

						for(int r2 = r; r2 < height ; r2++){
							if(countOverFlag){
								if(puyoType[c2][r2] == puyoType[c][r]){
									pointOver += 8 / (r2 - r + 1);
									break;
								}else if(puyoType[c2][r2] == null) break;
							}else if(puyoType[c2][r2] != puyoType[c][r]){
								countOverFlag = true;
							}
						}
					}
					countFireFrag = false;
					if(c != 0){
						if(connectPuyo[c - 1][r] == 0 && field.getTop(c - 1) + 2 <= r ){
							countFireFrag = true;
						}
					}if(c != 5){
						if(connectPuyo[c + 1][r] == 0 && field.getTop(c + 1) + 2 <= r){
							countFireFrag = true;
						}
					}
					if(countFireFrag){
						pointFire += Math.min((field.getTop(c) - r), 3) ;
					}
				}

			}
		}
		if( (field.getTop(2) + field.getTop(3)) * 3 < sumPuyoOnField){
			pointDivMinus -= 5 * (sumPuyoOnField - (field.getTop(2) + field.getTop(3))) ;
		}
		for(int c = 0; c < width - 1; c++){
			int div =Math.abs( field.getTop(c) - field.getTop(c + 1) );
			if(div > 3){
				pointDivTopMinus -= div * 4;
			}
		}

		pointFinal = pointConnect + pointIsolateMinus + pointOver + pointFire + pointDivMinus + pointDivTopMinus;

		return pointFinal;
	}






























	@Override
	public void initialize() {
//		System.out.println("頑張るぞ！よろしく！");
//		System.out.println(gameInfo.getNumberOfWinning(puyoInfo.getPlayerNumber()));
//		System.out.println(puyoInfo.getPlayerNumber());
//		if(gameInfo.getNumberOfWinning(puyoInfo.getPlayerNumber()) * 0 < gameInfo.getGameCount() ){
//			if(gameInfo.getGameCount() >= 0){
//				System.out.println("連鎖数変えるぜぇぇぇぇぇ\n\n\n\n\n\n\n\n");
//				if(Math.random() > 0.5){
//					NormalClear = 4 * 4;
//				}else{
//					NormalClear = 4 * 5;
//				}
//			}
//		}
	}
	@Override
	public void inputResult() {

	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		AbstractPlayer player = new KajiGodGod("KajiGodGod");

		PuyoPuyo puyopuyo = new PuyoPuyo(player);
		puyopuyo.puyoPuyo();
	}

}
