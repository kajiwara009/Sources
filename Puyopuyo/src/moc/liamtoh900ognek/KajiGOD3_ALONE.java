
package moc.liamtoh900ognek;

import java.util.*;

import java.lang.*;
import sp.AbstractSamplePlayer;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.AbstractPlayer;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.Action;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.Board;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.Field;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.Puyo;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.Puyo.PuyoDirection;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.Puyo.PuyoNumber;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.PuyoPuyo;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.storage.PuyoType;

public class KajiGOD3_ALONE extends AbstractPlayer {

	public KajiGOD3_ALONE(String playerName) {
		super(playerName);
	}
//時々400でタイムアウト（ホントに時々）
	@Override
	public Action doMyTurn() {
		 // 現在のフィールドの状況
		Board board = getMyBoard();
		Field field = board.getField();
//		Field Enemyfield = getEnemyBoard().getField();
		Field nextfield = null , nextfield2 = null , nextfield3 = null;
//		Field Enextfield = null , Enextfield2 = null , Enextfield3 = null;
		int ojamafuture = getMyBoard().getTotalNumberOfOjama();
		Puyo puyo = getMyBoard().getCurrentPuyo();	puyo.setDirection(PuyoDirection.DOWN);
		Puyo puyo2 = getMyBoard().getNextPuyo();	puyo2.setDirection(PuyoDirection.DOWN);
		Puyo puyo3 = getMyBoard().getNextNextPuyo();puyo3.setDirection(PuyoDirection.DOWN);
		int sum0 =0 , sum1 = 0 , sum2 = 0 , sum3 = 0 , sum4 =0 , SUM_CLEAR = 0 , iA=0 , m =0 , n =7; //ｎが連鎖数 , mが端数
		int Esum0 = 0, Esum1 = 0 , Esum2 = 0, Esum3 =0 , ESumClear=0;
		int CLEAR1 , CLEAR2 , CLEAR3 , minCLEAR;
		int ECLEAR1 , ECLEAR2 , ECLEAR3 , Eclear=12 ;
		int sum_ojama=0 , EnemySumOjama=0;
		double delta_RL;
		double sumP = -1000 , SumNow = 0;
		double point_C = 0 , Point_clear = -1000;
		Action action = null;
		boolean danger = false , BIG_CLEAR = false;
		PuyoDirection dirA = null , dir=null , dir2=null , dir3 = null;
		PuyoDirection Edir = null , Edir2=null , Edir3= null;
		int[][] connect = Get_connect(field);
		int[][] connect2;
		int[][] connect3;
		int[][] connect_CLEAR;
		List<Integer> list = board.getNumbersOfOjamaList();
		int[] ojama_list = {list.get(0) , list.get(1) , list.get(2)};
		for(int i=0 ; i < field.getWidth() ; i++){
			for(int j=0 ; j < field.getHeight() ; j++){
				if(field.getPuyoType(i, j) == PuyoType.OJAMA_PUYO) sum_ojama++;
//				if(Enemyfield.getPuyoType(i, j) == PuyoType.OJAMA_PUYO) EnemySumOjama++;
			}
		}
//危機判断
		if(ojamafuture >= 6){
			danger = true;
			n--;
			System.out.println(ojamafuture);
			if(ojamafuture >= 12){
				n--;
			}
		}
		if(sum_ojama >12){
			danger = true;
			n--;
		}

/*		if(EnemySumOjama >= 20){
			n = 3;
			System.out.println("CHANCE");
//相手のフィールドにたくさんあって、かつ発火が出来ない状況なら畳み掛ける。
		}else if(SumField(Enemyfield) >= 25){
			Esum0 = SumField(Enemyfield);
loop0:		for(int i = 0; i < Enemyfield.getWidth(); i++){
				for(int amount_of_DIRECTION = 0; amount_of_DIRECTION<4 ; amount_of_DIRECTION++){
					puyo.rotate();
					Edir = puyo.getDirection();
					if(!isEnable(Enemyfield, Edir, i)){//不可能な置き方の時はcontinueで流す。
						continue;
					}
					Enextfield = Enemyfield.getNextField(puyo, i);
					Esum1 = SumField(Enextfield);
					ECLEAR1 = Esum0 - Esum1 + 2;
					if(ECLEAR1 >= 4){
/*大量消し			if(ECLEAR1 > Eclear && ECLEAR1 > ESumClear){
							ESumClear = ECLEAR1;
							continue;
						}
						continue;
					}

//ここからは２手先を読む！！
					for(int i2=0; i2<Enextfield.getWidth(); i2++){
						for(int amount_of_DIRECTION2 = 0; amount_of_DIRECTION2<4 ; amount_of_DIRECTION2++){
							puyo2.rotate();
							Edir2 = puyo2.getDirection();
							if(!isEnable(Enextfield, Edir2, i2)){//不可能な置き方の時はcontinueで流す。
								continue;
							}
							Enextfield2 = Enextfield.getNextField(puyo2, i2);
							Esum2 = SumField(Enextfield2);
							ECLEAR2 = Esum1 - Esum2 + 2;

							if(ECLEAR2 >= 4){
/*大量消し					if(ECLEAR2 > Eclear && ECLEAR2 >= ESumClear){
									ESumClear = ECLEAR2;
									continue;
								}
								continue;
							}

//ここからは３手先を読む！！！
							for(int i3=0; i3<Enextfield2.getWidth(); i3++){
								for(int amount_of_DIRECTION3 = 0; amount_of_DIRECTION3<4 ; amount_of_DIRECTION3++){
									puyo3.rotate();
									Edir3 = puyo3.getDirection();
									if(!isEnable(Enextfield2, Edir3, i3)){//不可能な置き方の時はcontinueで流す。
										continue;
									}
									Enextfield3 = Enextfield2.getNextField(puyo3, i3);
									Esum3 = SumField(Enextfield3);
//　全消しならAction確定、　大量に消えるならそれ、　消えないのはまた後で。
									ECLEAR3 = Esum2 - Esum3 + 2;
									if(ECLEAR3 >= 4){
/*大量消し							if(ECLEAR3 > Eclear && ECLEAR3 >= ESumClear){
											ESumClear = ECLEAR3;
											continue;
										}
										continue;
									}
								}
							}
						}
					}
				}
			}
			if(ESumClear < 12){
				n = 3;
			}else if(ESumClear >= 16){
				n = 4;
			}

		}
*/		if(this.SumField(field) > 50){
			danger = true;
			n = 1;
		}
		if(n <= 0) n = 2;
		System.out.println(n + "連鎖目標");
		minCLEAR = 4* n ;

//危機の時
		if(danger == true){//すなわち危険状態
			System.out.println("やばいよやばいよ！");
		}
//危機じゃない場合
		if(danger == false || action == null){
			sum0 = SumField(field);
loop1:		for(int i = 0; i < field.getWidth(); i++){
				for(int amount_of_DIRECTION = 0; amount_of_DIRECTION<4 ; amount_of_DIRECTION++){
					puyo.rotate();
					dir = puyo.getDirection();
					if(!isEnable(field, dir, i)){//不可能な置き方の時はcontinueで流す。
						continue;
					}
					nextfield = field.getNextField(puyo, i);
					sum1 = SumField(nextfield);
//　全消しならAction確定、　大量に消えるならそれ、　消えないのはまた後で。
/*全消し*/			if(sum1 == 0){
						dirA = dir;
						iA = i;
						break loop1;
					}
					CLEAR1 = sum0 - sum1 + 2 + 3;
/*消えない*//*		if(CLEAR1 == 0){
						if(BIG_CLEAR){
							continue;
						}
					}else */if(CLEAR1 >= 4){
/*大量消し*/			if(CLEAR1 > minCLEAR + 3 && CLEAR1 >= SUM_CLEAR){
							if(CLEAR1 == SUM_CLEAR){
								if(POINT_CONNECT(field, i, puyo) < Point_clear){
									continue;
								}
							}
							dirA = dir;
							iA = i;
							SUM_CLEAR = CLEAR1;
							Point_clear = POINT_CONNECT(field, i, puyo);
							BIG_CLEAR = true;
							continue;
						}
						continue;
					}

//ここからは２手先を読む！！
					for(int i2=0; i2<nextfield.getWidth(); i2++){
						for(int amount_of_DIRECTION2 = 0; amount_of_DIRECTION2<4 ; amount_of_DIRECTION2++){
							puyo2.rotate();
							dir2 = puyo2.getDirection();
							if(!isEnable(nextfield, dir2, i2)){//不可能な置き方の時はcontinueで流す。
								continue;
							}
							nextfield2 = nextfield.getNextField(puyo2, i2);
							sum2 = SumField(nextfield2);
//　全消しならAction確定、　大量に消えるならそれ、　消えないのはまた後で。
/*全消し*/					if(sum2 == 0){
								dirA = dir;
								iA = i;
								break loop1;
							}
							CLEAR2 = sum1 - sum2 + 2 + 1;
/*消えない*//*				if(CLEAR2 == 0){
								if(BIG_CLEAR){
									continue;
								}
							}else */if(CLEAR2 >= 4){
/*大量消し*/					if(ojama_list[1] <= 6 &&CLEAR2 > minCLEAR + 1 && CLEAR2 >= SUM_CLEAR){
									if(CLEAR2 == SUM_CLEAR){
										if(POINT_CONNECT(nextfield, i2, puyo2) < Point_clear){
											continue;
										}
									}
									dirA = dir;
									iA = i;
									SUM_CLEAR = CLEAR2;
									Point_clear = POINT_CONNECT(nextfield, i2, puyo2);
									BIG_CLEAR = true;
									continue;
								}
								continue;
							}

//ここからは３手先を読む！！！
							for(int i3=0; i3<nextfield2.getWidth(); i3++){
								for(int amount_of_DIRECTION3 = 0; amount_of_DIRECTION3<4 ; amount_of_DIRECTION3++){
									puyo3.rotate();
									dir3 = puyo3.getDirection();
									if(!isEnable(nextfield2, dir3, i3)){//不可能な置き方の時はcontinueで流す。
										continue;
									}
									nextfield3 = nextfield2.getNextField(puyo3, i3);
									sum3 = SumField(nextfield3);
//　全消しならAction確定、　大量に消えるならそれ、　消えないのはまた後で。
/*全消し*/							if(sum3 == 0){
										dirA = dir;
										iA = i;
										break loop1;
									}
									CLEAR3 = sum2 - sum3 + 2;
/*消えない*/						if(CLEAR3 == 0){
										if(BIG_CLEAR){
											continue;
										}
									}else if(CLEAR3 >= 4){
/*大量消し*/							if(ojama_list[1] <= 6 && ojama_list[2] <= 6 &&CLEAR3 > minCLEAR && CLEAR3 >= SUM_CLEAR){
											if(CLEAR3 == SUM_CLEAR){
												if(POINT_CONNECT(nextfield2, i3, puyo3) < Point_clear){
													continue;
												}
											}
											dirA = dir;
											iA = i;
											SUM_CLEAR = CLEAR3;
											POINT_CONNECT(nextfield2, i3, puyo3);
											BIG_CLEAR = true;
											continue;
										}
										continue;
									}

//ここからは採点しますよー。

									SumNow = POINT_CONNECT(nextfield2, i3, puyo3) + POINT_BELOW(nextfield2, i3, puyo3);
									if(SumField(nextfield3) > 26){
										SumNow += POINT_HAKKA(nextfield2, i3, puyo3);
									}
									if(SumNow > sumP){
										sumP = SumNow;
										dirA = dir;
										iA = i;
//										System.out.println(SumNow);
									}
								}
							}//i3のループ終わり
						}
					}//i2のループ終わり
				}
			}
		}
		if(dirA != null){
			puyo.setDirection(dirA);
			action = new Action(dirA ,iA);
		}
		return action;
	}
/*
//ここからは点数計算に値する場合。
//point2 = 隣接ぷよ数
					point2 = getNpuyoNumSum(field, puyo, i);
//point3 = MAX高さ
					point3 = 0;
					for(int j=0 ; j<field.getWidth() ;j++){
						if(nextfield.getTop(j)+1 > point3)
						point3 = nextfield.getTop(j)+1;
					}
//point4 均し率。高いとダメ。凸凹ってこと。
					point4 = 0;
					for(int j=0 ;j<field.getWidth() ;j++){
						point4 +=Math.abs(nextfield.getTop(j) - SumField(nextfield) * 0.2);
					}
//下に3個連結があるとポイントアップ
					point5 = Anspoint4(field , connect , puyo , i);
//真ん中の方はあんまりおいてほしくない
					point6 = 0;
					if(SumField(field) > 25 && ( i ==3 || i ==2)){
						point6 = -4;
					}
					point6_2 = 0;
					delta_RL = 0;//プラスならば左側にぷよが寄ってる
					for(int j =0 ; j<field.getWidth() ; j++){
						if(j<3){
							delta_RL += field.getTop(j);
						}else{
							delta_RL -= field.getTop(j);
						}
					}
					if(i<3){
						point6_2 = - delta_RL * Math.abs(delta_RL) / 30;
					}else
						point6_2 = delta_RL * Math.abs(delta_RL) / 30 ;
//point7 = 3つ連結を横からつぶすのはマイナス
					point7 = 0;
					if((dir == PuyoDirection.UP || dir == PuyoDirection.DOWN) ){
						if(i>0){
							if(connect[i-1][field.getTop(i)+1] == 3) point7 -= 5;
							if(connect[i-1][field.getTop(i)+2] == 3) point7 -= 5;
						}
						if(i<5){
							if(connect[i+1][field.getTop(i)+1] == 3) point7 -= 5;
							if(connect[i+1][field.getTop(i)+2] == 3) point7 -= 5;
						}
					}
					if(dir == PuyoDirection.RIGHT){
						if(i>0){
							if(connect[i-1][field.getTop(i)+1] == 3) point7 -= 5;
						}
						if(i<5){
							if(connect[i+1][field.getTop(i)+1] == 3) point7 -= 5;
						}
						if(connect[i][field.getTop(i+1)+1] == 3) point7 -= 5;
						if(i<4){
							if(connect[i+2][field.getTop(i+1)+1] == 3) point7 -= 5;
						}
					}

					if(dir == PuyoDirection.LEFT){
						if(i>0){
							if(connect[i-1][field.getTop(i)+1] == 3) point7 -= 5;
						}
						if(i<5){
							if(connect[i+1][field.getTop(i)+1] == 3) point7 -= 5;
						}
						if(connect[i][field.getTop(i-1)+1] == 3) point7 -= 5;
						if(i>1){
							if(connect[i-2][field.getTop(i-1)+1] == 3) point7 -= 5;
						}
					}
//縦３つつながりはポイントアップ
					point8 =0;
					if(dir != PuyoDirection.DOWN){
						if(field.getPuyoType(i, field.getTop(i)) == puyo.getPuyoType(PuyoNumber.FIRST) &&
						field.getPuyoType(i, field.getTop(i)-1) == puyo.getPuyoType(PuyoNumber.FIRST)	){
							point8 +=30;
						}
					}
					else{
						if(field.getPuyoType(i, field.getTop(i)) == puyo.getPuyoType(PuyoNumber.SECOND) &&
						field.getPuyoType(i, field.getTop(i)-1) == puyo.getPuyoType(PuyoNumber.SECOND)	){
							point8 +=30;
						}
					}
					if(dir == PuyoDirection.RIGHT){
						if(field.getPuyoType(i+1, field.getTop(i+1)) == puyo.getPuyoType(PuyoNumber.SECOND) &&
						field.getPuyoType(i+1, field.getTop(i+1)-1) == puyo.getPuyoType(PuyoNumber.SECOND)	){
							point8 +=30;
						}
					}
					if(dir == PuyoDirection.LEFT){
						if(field.getPuyoType(i-1, field.getTop(i-1)) == puyo.getPuyoType(PuyoNumber.SECOND) &&
						field.getPuyoType(i-1, field.getTop(i-1)-1) == puyo.getPuyoType(PuyoNumber.SECOND)	){
							point8 +=30;
						}
					}
					if(dir == PuyoDirection.DOWN || dir == PuyoDirection.UP){
						if(puyo.getPuyoType(PuyoNumber.FIRST) == puyo.getPuyoType(PuyoNumber.SECOND)){
							point8 += 30;
						}
						if(field.getPuyoType(i, field.getTop(i)) == puyo.getPuyoType(PuyoNumber.FIRST)){
							point8 += 30;
						}
					}

					point9 = 0;
					point9 += pre_point9(field , i , puyo.getPuyoType(PuyoNumber.FIRST));
					if(dir == PuyoDirection.DOWN || dir == PuyoDirection.UP){
						point9 += pre_point9(field , i , puyo.getPuyoType(PuyoNumber.SECOND));
					}else if(dir == PuyoDirection.RIGHT){
						point9 += pre_point9(field , i+1 , puyo.getPuyoType(PuyoNumber.SECOND));
					}else
						point9 += pre_point9(field , i-1 , puyo.getPuyoType(PuyoNumber.SECOND));

//合計値
					sumNOW = Math.pow(20, point2) - 0.2 * Math.pow(point3, 2) - 1 *  point4 +
							0.3 * point5 + point6 + point6_2 + point7 +  point8 + point9;
					if(sumNOW > sumP){
						sumP = sumNOW;
//						System.out.println(sumP);
						dirA = dir;
						iA = i;
					}
				}//ぷよ方向のforループ締め
			}//ぷよ置き列のforループ締め
			if(dirA != null){
				puyo.setDirection(dirA);
				action = new Action(dirA ,iA);
	//			System.out.println("ちゃんとしたプログラム発動！！！");
//				System.out.println(sum + "と" + sum2);
				nextfield = field.getNextField(puyo, iA);
	//			printField(nextfield);
			}
			else{
//				System.out.println("うそやん！これが選択されるのはひどい");
				action = getDefaultAction();
			}
		}
//		System.out.println("次のターン！");
		return action;
	}
*/






//	ここからユーザ関数
	Action getDefaultAction(){
		//TODO 一番低い場所にぷよを配置するようにする
		//Sample05参照．
		Field field = getMyBoard().getField();
		int columnNum = 0;
		int minPuyoNum = field.getTop(0)+1;

		for(int i = 0; i < field.getWidth(); i++){
			//ここで，各列の高さを調べ，これまで一番低かった列よりも低ければ，columnNumをiにする．
			/**
			 * puyoNum=i列目のぷよの数
			 */
			int puyoNum = field.getTop(i)+1;
			if(puyoNum <= minPuyoNum){
				//これまで一番ぷよが少なかった数より，i番目の列のぷよが少なかったら
				//minPuyoNumにi番目のぷよの数を入れて
				//配置する列(columnNum)にiを指定する
				minPuyoNum = puyoNum;
				columnNum = i;
			}
		}
		//一番ぷよの数が少ない列(columnNum)にぷよを配置する．
		Action action = new Action(PuyoDirection.DOWN, columnNum);

		return action;
	}

	Action DangerAction(){
		Field field = getMyBoard().getField();
//		Field nextfield  = null;
//		int columnNum = 0;
//		int minPuyoNum = field.getTop(0)+1;
		int sum = SumField(field) , sum2 , sumA = SumField(field) , iA = 0;
		Puyo puyo = getMyBoard().getCurrentPuyo();
		//下のpuyotypeの定義の位置をずらした（サンプル８を参照）
		//first が軸ぷよ、secondが組ぷよ
//		PuyoType firstPuyo = puyo.getPuyoType(PuyoNumber.FIRST);
//		PuyoType secondPuyo = puyo.getPuyoType(PuyoNumber.SECOND);
		double sumP = 1;
		double pointDANGER;
		Action action = null;
		boolean danger = false;
		PuyoDirection dirA = null , dir=null;
		int[][] connect = Get_connect(field);

		//全ての可能性に対して
		for(int i = 0; i < field.getWidth(); i++){
			for(int amount_of_DIRECTION = 0; amount_of_DIRECTION<4 ; amount_of_DIRECTION++){
				puyo.rotate();
				dir = puyo.getDirection();
				if(!isEnable(field, dir, i)){//不可能な置き方の時はcontinueで流す。
					continue;
				}
				pointDANGER = 0;
				if(dir != PuyoDirection.DOWN){
					if(CLEAR(field, connect ,i ,0 ,puyo.getPuyoType(PuyoNumber.FIRST))) pointDANGER += 50;
				}else{
					if(CLEAR(field, connect ,i ,1 ,puyo.getPuyoType(PuyoNumber.FIRST)))	pointDANGER += 50;
				}

				if(dir == PuyoDirection.UP){
					if(CLEAR(field, connect ,i ,1 ,puyo.getPuyoType(PuyoNumber.SECOND))) pointDANGER += 50;
				}else if(dir == PuyoDirection.DOWN){
					if(CLEAR(field, connect ,i ,0 ,puyo.getPuyoType(PuyoNumber.SECOND))) pointDANGER += 50;
				}else if(dir == PuyoDirection.RIGHT){
					if(CLEAR(field, connect ,i+1 ,0 ,puyo.getPuyoType(PuyoNumber.SECOND))) pointDANGER += 50;
				}else if(dir == PuyoDirection.LEFT){
					if(CLEAR(field, connect ,i-1 ,0 ,puyo.getPuyoType(PuyoNumber.SECOND))) pointDANGER += 50;
				}
				if(pointDANGER > sumP ){
					sumP = pointDANGER;
					dirA = dir;
					iA = i;
				}
			}
		}
		if(dirA != null){
			action = new Action(dirA,iA);
//			System.out.println("消すよ！");
		}
		else{
			action = null;
//			System.out.println("これやん！");
		}
		return action;
	}

	//フィールドのぷよの総数を返す。
	public int SumField(Field field){
		int sum = 0;
		for(int i = 0; i < field.getWidth(); i++){
			sum += field.getTop(i)+1;
		}
		return sum;
	}

	private boolean isEnable(Field field, PuyoDirection dir, int i) {
//		Field field = getMyBoard().getField();

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
			if(field.getTop(i) >= field.getDeadLine()-2 || field.getTop(i+1) >= field.getDeadLine()-2) {
				return false;
			}
		}
		else if(dir == PuyoDirection.LEFT){
			if(field.getTop(i) >= field.getDeadLine()-2 || field.getTop(i-1) >= field.getDeadLine()-2) {
				return false;
			}
		}
		return true;
	}

	public int getNeighborPuyoNum(Field field, int x, int y, PuyoType puyoType) {
		int count = 0;
		if(field.isOnField(x+1, y)){
			if(field.getPuyoType(x+1, y) == puyoType){
				count++;
			}
		}
		if(field.isOnField(x-1, y)){
			if(field.getPuyoType(x-1, y) == puyoType){
				count++;
			}
		}
		if(field.isOnField(x, y+1)){
			if(field.getPuyoType(x, y+1) == puyoType){
				count++;
			}
		}
		if(field.isOnField(x, y-1)){
			if(field.getPuyoType(x, y-1) == puyoType){
				count++;
			}
		}

		return count;
	}

	//ぷよを落としたときに、隣接が何個になるか数えるメソッド
	public int getNpuyoNumSum(Field field, Puyo puyo, int i){//iは落とす場所
		int sum = 0;
		PuyoType firstPuyo = puyo.getPuyoType(PuyoNumber.FIRST);
		PuyoType secondPuyo = puyo.getPuyoType(PuyoNumber.SECOND);
		PuyoDirection dir = puyo.getDirection();
		int firstNeighbor = 0;
		int secondNeighbor = 0;
		//最初のぷよの周りに存在する同色ぷよ数を数える
		if(dir == PuyoDirection.DOWN){
			//二番目のぷよが下にある場合は，topの二つ上がy座標
			int y = field.getTop(i)+2;
			firstNeighbor = getNeighborPuyoNum(field, i, y, firstPuyo);
		}
		else{
			//二番目のぷよが下にある場合以外は，topの1つ上がy座標
			int y = field.getTop(i)+1;
			firstNeighbor = getNeighborPuyoNum(field, i, y, firstPuyo);
		}
		//二番目のぷよの周りに存在する同色ぷよを数える
		if(dir == PuyoDirection.DOWN){
			int y = field.getTop(i)+1;
			secondNeighbor = getNeighborPuyoNum(field, i, y, secondPuyo);
		}
		else if(dir == PuyoDirection.UP){
			int y = field.getTop(i)+2;
			secondNeighbor = getNeighborPuyoNum(field, i, y, secondPuyo);
		}
		else if(dir == PuyoDirection.RIGHT){
			int y = field.getTop(i+1)+1;
			secondNeighbor = getNeighborPuyoNum(field, i+1, y, secondPuyo);
			//二番目のぷよが右にある場合
		}
		else if(dir == PuyoDirection.LEFT){
			int y = field.getTop(i-1)+1;
			secondNeighbor = getNeighborPuyoNum(field, i-1, y, secondPuyo);
			//二番目のぷよが左にある場合
		}
		sum = firstNeighbor + secondNeighbor;
		return sum;
	}

	public boolean CLEAR(Field field , int[][] connect , int x ,int p,  PuyoType puyoType){
		boolean clear = false;
		int y = field.getTop(x) + 1+ p;
		if( x == 0){
			if(y != 0){
				if((connect[x+1][y] == 3 && field.getPuyoType(x+1, y) == puyoType) ||
					(connect[x][y-1] == 3 && field.getPuyoType(x, y-1) == puyoType)){
					clear = true;
				}
			}
			else{
				if((connect[x+1][y] == 3 && field.getPuyoType(x+1, y) == puyoType)){
					clear = true;
				}
			}
		}
		else if( x == 5 ){
			if(y != 0){
				if((connect[x-1][y] == 3 && field.getPuyoType(x-1, y) == puyoType) ||
					(connect[x][y-1] == 3 && field.getPuyoType(x, y-1) == puyoType)){
					clear = true;
				}
			}
			else{
				if( ( connect[x-1][y] == 3 && field.getPuyoType(x-1, y) == puyoType)){
					clear = true;
				}
			}
		}
		else{
			if(y != 0){
				if( (connect[x+1][y] == 3 && field.getPuyoType(x+1, y) == puyoType) ||
					(connect[x-1][y] == 3 && field.getPuyoType(x-1, y) == puyoType) ||
					(connect[x][y-1] == 3 && field.getPuyoType(x, y-1) == puyoType)){
					clear = true;
				}
			}
			else{
				if( (connect[x+1][y] == 3 && field.getPuyoType(x+1, y) == puyoType) ||
					(connect[x-1][y] == 3 && field.getPuyoType(x-1, y) == puyoType)){
					clear = true;
				}
			}
		}
		return clear;
	}

	public int[][] Get_connect(Field field){
		int[][] connect = new int[6][13];
		for(int x = 0 ; x <= 5 ; x++){
			for(int y = 0 ; y <= field.getTop(x) ; y++){
				int count = 1;
				PuyoType puyoType = field.getPuyoType(x, y);
				if(field.isOnField(x+1, y)){
					if(field.getPuyoType(x+1, y) == puyoType){
						count++;
					}
				}
				if(field.isOnField(x-1, y)){
					if(field.getPuyoType(x-1, y) == puyoType){
						count++;
					}
				}
				if(field.isOnField(x, y+1)){
					if(field.getPuyoType(x, y+1) == puyoType){
						count++;
					}
				}
				if(field.isOnField(x, y-1)){
					if(field.getPuyoType(x, y-1) == puyoType){
						count++;
					}
				}
				connect[x][y] = count;
			}
		}
		for(int x = 0 ; x <= 5 ; x++){
			for(int y = 0 ; y <= field.getTop(x) ; y++){
				PuyoType puyoType = field.getPuyoType(x, y);
				if( x == 0){
					if( connect[x][y] == 2 ){
						if(y != 0){
							if( ( connect[x+1][y] == 3 && field.getPuyoType(x+1, y) == puyoType) ||
								( connect[x][y+1] == 3 && field.getPuyoType(x, y+1) == puyoType) ||
								( connect[x][y-1] == 3 && field.getPuyoType(x, y-1) == puyoType)){
								connect[x][y] = 3;
							}
						}
						else{
							if( ( connect[x+1][y] == 3 && field.getPuyoType(x+1, y) == puyoType) ||
								( connect[x][y+1] == 3 && field.getPuyoType(x, y+1) == puyoType)){
								connect[x][y] = 3;
							}
						}
					}
				}
				else if( x == 5 ){
					if(y != 0){
						if( ( connect[x-1][y] == 3 && field.getPuyoType(x-1, y) == puyoType) ||
						( connect[x][y+1] == 3 && field.getPuyoType(x, y+1) == puyoType) ||
						( connect[x][y-1] == 3 && field.getPuyoType(x, y-1) == puyoType)){
						connect[x][y] = 3;
						}
					}
					else{
						if( ( connect[x-1][y] == 3 && field.getPuyoType(x-1, y) == puyoType) ||
							( connect[x][y+1] == 3 && field.getPuyoType(x, y+1) == puyoType)){
							connect[x][y] = 3;
						}
					}
				}
				else{
					if( connect[x][y] == 2 ){
						if(y != 0){
							if( (connect[x+1][y] == 3 && field.getPuyoType(x+1, y) == puyoType) ||
								(connect[x-1][y] == 3 && field.getPuyoType(x-1, y) == puyoType) ||
								(connect[x][y+1] == 3 && field.getPuyoType(x, y+1) == puyoType) ||
								(connect[x][y-1] == 3 && field.getPuyoType(x, y-1) == puyoType)){
								connect[x][y] = 3;
							}
						}
						else{
							if( (connect[x+1][y] == 3 && field.getPuyoType(x+1, y) == puyoType) ||
								(connect[x-1][y] == 3 && field.getPuyoType(x-1, y) == puyoType) ||
								(connect[x][y+1] == 3 && field.getPuyoType(x, y+1) == puyoType)){
								connect[x][y] = 3;
							}
						}
					}
				}
			}
		}
		return connect;
	}

	public void printField(Field field){
		for(int y = field.getHeight(); y >= 0 ; y--){
			for(int x = 0; x < field.getWidth(); x++){
				if(field.getPuyoType(x, y) != null){
					System.out.print(field.getPuyoType(x, y).toString().substring(0, 1));
				}
				else{
					System.out.print(".");
				}
			}
			System.out.println();
		}
	}

	double Anspoint4(Field field , int[][] connect , Puyo puyo ,int i){
		double point = 0;
		if(puyo.getDirection() == PuyoDirection.RIGHT){
			point += pre_point4(field , connect , puyo.getPuyoType(PuyoNumber.FIRST) , i);
			point += pre_point4(field , connect , puyo.getPuyoType(PuyoNumber.SECOND) , i+1);
		}
		if(puyo.getDirection() == PuyoDirection.LEFT){
			point += pre_point4(field , connect , puyo.getPuyoType(PuyoNumber.FIRST) , i);
			point += pre_point4(field , connect , puyo.getPuyoType(PuyoNumber.SECOND) , i-1);
		}
		if(puyo.getDirection() == PuyoDirection.UP || puyo.getDirection() == PuyoDirection.DOWN ){
			point += pre_point4(field , connect , puyo.getPuyoType(PuyoNumber.FIRST) , i);
			point += pre_point4(field , connect , puyo.getPuyoType(PuyoNumber.SECOND) , i);
		}
		return point;
	}

	double pre_point4(Field field , int[][] connect , PuyoType Type , int i ){
		double point = 0;
		if(field.getTop(i) >= 1 ){
			if(connect[i][field.getTop(i) - 1] == 3){//トップの1個下が3個連結（すぐに消せるってこと）
				if(field.getPuyoType(i, field.getTop(i) - 1) == Type){//その3個連結が落ちてくるぷよと同色
					point = 100;
				}
				else{
					if(field.getTop(i) >= 2){
						if(connect[i][field.getTop(i) - 2] == 3 || 	connect[i][field.getTop(i)] == 3 ||
						field.getPuyoType(i, field.getTop(i) - 2) == Type){//トップとその下が異色3個連結、さらにその下がぷよと同色
							point = 50;
						}
					}
				}
			}
		}
		return point;
	}
//POINT_BELOWのフィールドは置く前のフィールドを引数にする。
	double POINT_BELOW(Field field , int i , Puyo puyo){
		double PointBelow = 0;
		PuyoDirection dir = puyo.getDirection();
		PointBelow += pre_point9(field , i , puyo.getPuyoType(PuyoNumber.FIRST));
		if(dir == PuyoDirection.DOWN || dir == PuyoDirection.UP){
			PointBelow += pre_point9(field , i , puyo.getPuyoType(PuyoNumber.SECOND));
		}else if(dir == PuyoDirection.RIGHT){
			PointBelow += pre_point9(field , i+1 , puyo.getPuyoType(PuyoNumber.SECOND));
		}else
			PointBelow += pre_point9(field , i-1 , puyo.getPuyoType(PuyoNumber.SECOND));
		return PointBelow;
	}

	double pre_point9(Field field , int i, PuyoType puyotype){
		double point9 =0;
		for(int j=0 ; j<=field.getTop(i)-1 ; j++){
			if(field.getPuyoType(i, j) == puyotype){
				point9 += 10 / (field.getTop(i) - j);
			}
		}
		if(i > 0){
			for(int j=0 ; j<=Math.min(field.getTop(i-1)-1 , field.getTop(i)-1) ; j++){
				if(field.getPuyoType(i-1, j) == puyotype){
					point9 += 10 / (field.getTop(i) - j);
				}
			}
		}
		if(i < 4){
			for(int j=0 ; j<=Math.min(field.getTop(i+1)-1 , field.getTop(i)-1) ; j++){
				if(field.getPuyoType(i+1, j) == puyotype){
					point9 += 10 / (field.getTop(i) - j);
				}
			}
		}
		return point9;
	}


//fieldは置く前のものを指定する
	double POINT_CONNECT(Field field , int i , Puyo puyo ){
		double SUMpoint , point_connect=0 , point_isolate=0;
		Field nextfield = field.getNextField(puyo, i);
		int[][] connect = Get_connect(nextfield);
//point_connectはフィールドのコネクト数が多ければ多いほど高得点
		for(int i_c=0 ; i_c < nextfield.getWidth() ; i_c++){
			for(int j=0 ; j < nextfield.getTop(i_c) ; j++){
				if(connect[i_c][j] == 3) point_connect += 50;
				else if(connect[i_c][j] == 2) point_connect += 20;
//point_isolateは連結無しのぷよの周りに多色ぷよがあったらマイナス(もとから負の値を返す)
				else if(connect[i_c][j] == 1){
					if(nextfield.isOnField(i_c-1, j)){
						if(nextfield.getPuyoType(i_c-1, j) != null) point_isolate -=30;
					}
					if(nextfield.isOnField(i_c+1, j)){
						if(nextfield.getPuyoType(i_c+1, j) != null) point_isolate -=30;
					}
					if(nextfield.isOnField(i_c, j+1)){
						if(nextfield.getPuyoType(i_c, j+1) != null) point_isolate -=50;
					}
				}
			}
		}
/*
		PuyoDirection dir = puyo.getDirection();
		PointBelow = 0;
		PointBelow += pre_point9(field , i , puyo.getPuyoType(PuyoNumber.FIRST));
		if(dir == PuyoDirection.DOWN || dir == PuyoDirection.UP){
			PointBelow += pre_point9(field , i , puyo.getPuyoType(PuyoNumber.SECOND));
		}else if(dir == PuyoDirection.RIGHT){
			PointBelow += pre_point9(field , i+1 , puyo.getPuyoType(PuyoNumber.SECOND));
		}else
			PointBelow += pre_point9(field , i-1 , puyo.getPuyoType(PuyoNumber.SECOND));
*/
		SUMpoint = (point_connect + point_isolate) / SumField(nextfield);

		return SUMpoint;
	}
//発火点をふさぐと点数ダウン
	double POINT_HAKKA(Field field , int i, Puyo puyo ){
		double HAKKA=0 , Point_Hakka=0;
		PuyoDirection dir = puyo.getDirection();
		Field nextfield = field.getNextField(puyo, i);
		int[][] connect = Get_connect(nextfield);
		for(int iC=0 ; iC < field.getWidth() ; iC++){
			if(iC < 5){
				for(int j = nextfield.getTop(iC)+1 ; j < nextfield.getTop(iC+1) ; j++){
					if(connect[iC+1][j] == 3) HAKKA++;
				}
			}
			if(iC > 0){
				for(int j=nextfield.getTop(iC)+1 ; j < nextfield.getTop(iC-1) ; j++){
					if(connect[iC-1][j] == 3) HAKKA++;
				}
			}
		}
		if(HAKKA < 3){
			Point_Hakka = -30;
		}


		return Point_Hakka;
	}

	@Override
	public void initialize() {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public void inputResult() {
		// TODO 自動生成されたメソッド・スタブ

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		AbstractPlayer player = new KajiGOD3_ALONE("kajiwara");

		PuyoPuyo puyopuyo = new PuyoPuyo(player);
		puyopuyo.puyoPuyo();
		// TODO 自動生成されたメソッド・スタブ

	}
}

//ぷよが消えるかどうか判断するもの getnextfieldが機能してなかったらこれを
/*						if( dir == PuyoDirection.RIGHT ){
if(CLEAR(field , connect, i, 0 , puyo.getPuyoType(PuyoNumber.FIRST)) |
CLEAR(field , connect, i+1, 0, puyo.getPuyoType(PuyoNumber.SECOND))){
//	System.out.println("飛ばされました");
	continue;
}
}
else if( dir == PuyoDirection.LEFT ){
if(CLEAR(field , connect, i, 0, puyo.getPuyoType(PuyoNumber.FIRST)) |
CLEAR(field , connect, i-1, 0, puyo.getPuyoType(PuyoNumber.SECOND))){
//	System.out.println("飛ばされました２");
	continue;
}
}
else if( dir == PuyoDirection.UP ){
if(CLEAR(field , connect, i, 0, puyo.getPuyoType(PuyoNumber.FIRST)) |
CLEAR(field , connect, i, 1, puyo.getPuyoType(PuyoNumber.SECOND))){
//	System.out.println("飛ばされました２");
	continue;
}
}
else if( dir == PuyoDirection.DOWN ){
if(CLEAR(field , connect, i, 1, puyo.getPuyoType(PuyoNumber.FIRST)) |
CLEAR(field , connect, i, 0, puyo.getPuyoType(PuyoNumber.SECOND))){
//	System.out.println("飛ばされました２");
	continue;
}
}*/