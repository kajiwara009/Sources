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

public class kaji2 extends AbstractPlayer {

	public kaji2(String playerName) {
		super(playerName);
		// TODO 自動生成されたコンストラクター・スタブ
	}
//全パターンでgetnextfieldだけで6でタイムアウト
	@Override
	public Action doMyTurn() {
		 // 現在のフィールドの状況
		Board board = getMyBoard();
		Field field = board.getField();
		Field nextfield = null , next2field = null , next3field = null;
		int ojamaSUM = getMyBoard().getTotalNumberOfOjama();
		 // 今降ってきているぷよ
		Puyo puyo = getMyBoard().getCurrentPuyo();
		puyo.setDirection(PuyoDirection.DOWN);
		Puyo puyo2 = getMyBoard().getNextPuyo();
		puyo2.setDirection(PuyoDirection.DOWN);
		Puyo puyo3 = getMyBoard().getNextNextPuyo();
		puyo3.setDirection(PuyoDirection.DOWN);
		int sum = 0 , sum2 = 0 , sum3 = 0 , sum4 =0 , SUM_CLEAR = 0 , iA=0 , m =0 , n =3; //ｎが連鎖数 , mが端数
		int sum_ojama=0;
		double delta_RL;
		double sumP = -1000 , sumNOW = 0;
		double point_C = 0 ,point2 , point3 , point4 , point5 , point6,point6_2, point7, point8 ,point9;
		Action action = null;
		boolean danger = false , BIG_CLEAR = false;
		PuyoDirection dirA = null , dir=null , dir2=null , dir3 = null;
		int[][] connect = Get_connect(field);
//		for(int i=10; i>=0 ; i--){
//			System.out.println(connect[0][i] + "" + connect[1][i] + "" + connect[2][i] + "" + connect[3][i] + "" + connect[4][i] + "" + connect[5][i]);
//		}
		for(int i=0 ; i < field.getWidth() ; i++){
			for(int j=0 ; j < field.getHeight() ; j++){
				if(field.getPuyoType(i, j) == PuyoType.OJAMA_PUYO) sum_ojama++;
			}
		}
		//危機判断
		if(ojamaSUM >= 2){
			danger = true;
		}
		if(sum_ojama >10){
			danger = true;
		}
		if(this.SumField(field) > 30){
			danger = true;
		}
/*		for(int i=0 ; i < field.getWidth() ; i++){
			if(field.getTop(i) > 12){
				danger = true;
				break;
			}
		}*/
//		危機判断終了
		if(danger == true){//すなわち危険状態
			System.out.println("やばいよやばいよ！");
			action = DangerAction();
		}
//		ここから危機じゃない場合
		if(danger == false || action == null){
			sum = SumField(field);
			//全ての可能性に対して
loop1:		for(int i = 0; i < field.getWidth(); i++){
//				for(PuyoDirection dir:PuyoDirection.values()){
				for(int amount_of_DIRECTION = 0; amount_of_DIRECTION<4 ; amount_of_DIRECTION++){
					puyo.rotate();
					dir = puyo.getDirection();
					if(!isEnable(field, dir, i)){//不可能な置き方の時はcontinueで流す。
						continue;
					}
					nextfield = field.getNextField(puyo, i);
//消えちゃうなら考慮しない。めっちゃ消えるならそれでもいいかも。
					sum2 = SumField(nextfield);
					if(sum2 == 0){
						dirA = dir;
						iA = i;
						break loop1;
					}
					if(sum - sum2 + 2  >= 4*n + m && sum - sum2 + 2 > SUM_CLEAR ){//12個以上消えるなら(sum2は何もしないとsumより2大きい)
						dirA = dir;
						iA = i;
						SUM_CLEAR = sum - sum2 + 2;
	//					System.out.println("めっちゃ消すでー！");
						BIG_CLEAR = true;
						continue;
					}else if(sum - sum2 + 2 >= 4 ){
						continue;
					}else{
						if(BIG_CLEAR){
							continue;
						}
					}
					//ぷよが消えるかどうか判断するもの getnextfieldが機能してなかったらこれを
										if( dir == PuyoDirection.RIGHT ){
					if(CLEAR(field , connect, i, 0 , puyo.getPuyoType(PuyoNumber.FIRST)) |
					CLEAR(field , connect, i+1, 0, puyo.getPuyoType(PuyoNumber.SECOND))){
//						System.out.println("飛ばされました");
						continue;
					}
					}
					else if( dir == PuyoDirection.LEFT ){
					if(CLEAR(field , connect, i, 0, puyo.getPuyoType(PuyoNumber.FIRST)) |
					CLEAR(field , connect, i-1, 0, puyo.getPuyoType(PuyoNumber.SECOND))){
//						System.out.println("飛ばされました２");
						continue;
					}
					}
					else if( dir == PuyoDirection.UP ){
					if(CLEAR(field , connect, i, 0, puyo.getPuyoType(PuyoNumber.FIRST)) |
					CLEAR(field , connect, i, 1, puyo.getPuyoType(PuyoNumber.SECOND))){
//						System.out.println("飛ばされました２");
						continue;
					}
					}
					else if( dir == PuyoDirection.DOWN ){
					if(CLEAR(field , connect, i, 1, puyo.getPuyoType(PuyoNumber.FIRST)) |
					CLEAR(field , connect, i, 0, puyo.getPuyoType(PuyoNumber.SECOND))){
//						System.out.println("飛ばされました２");
						continue;
					}
					}

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
		AbstractPlayer player = new kaji2("Kaji's agent");

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