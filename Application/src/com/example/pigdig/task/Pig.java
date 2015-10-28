package com.example.pigdig.task;

import java.util.Random;

import com.example.pigdig.GM;
import com.example.pigdig.R;
import com.example.pigdig.data.Field;
import com.example.pigdig.data.Field.HiddenTreasure;
import com.example.pigdig.data.Vec;

import drawText.BalloonText;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;

public class Pig extends OnMapItem implements Task{
	
	public static final int MAX_DIG_NUM = 300,
							MOST_NEAR_DISTANCE = 20,
							SECOND_NEAR_DISTANCE = 40,
							THIRD_NEAR_DISTANCE = 60,
							FOURTH_NEAR_DISTANCE = 100;
	
//	private Bitmap pigImage = BitmapFactory.decodeResource(GM.getR(), R.drawable.pig);
	private Bitmap pigFrontImage = BitmapFactory.decodeResource(GM.getR(), R.drawable.pig_front);
	private Bitmap pigBackImage = BitmapFactory.decodeResource(GM.getR(), R.drawable.pig_back);
	private Bitmap pigRightImage = BitmapFactory.decodeResource(GM.getR(), R.drawable.pig_right);
	private Bitmap pigLeftImage = BitmapFactory.decodeResource(GM.getR(), R.drawable.pig_left);
	
	private Direction dir = Direction.FRONT;
	
	private double 
		//絶対座標
		targetX,
		targetY;
	private int 
		/** 鼻の強さ．一度に掘れる穴の深さ */
		nosePower,
		/** 足の速さ */
		speed,
		/** 穴を掘るのに必要な時間．捜索中も掘削中も */
		digQuickness;
	
	//掘るのに必要な時間
	private int digNum;
	
	/**
	 * 豚のステータスを入力
	 * @param preferences
	 */
	public Pig(SharedPreferences preferences) {
		nosePower = preferences.getInt("NOSE_POWER", 10);
		speed = preferences.getInt("SPEED", 10);
		digQuickness = preferences.getInt("DIG_QUICKNESS", 10);
		
		//初期位置をFieldの進入可能領域からランダムに選択
		setAtRandomPoint();
		targetX = x;
		targetY = y;
	}

	@Override
	public boolean onUpdate() {
		/*
		 * 捜索中，掘り出し中，掘り出し成功中，ゲームオーバー中
		 * 
		 */
		switch (GM.getScene()) {
		case GM.S_MOVE_STOP:
		case GM.S_MOVE_MOVING:
		case GM.S_MOVE_DIGGING:
			moveUpdate(GM.getScene());
			break;

		case GM.S_DIG_STOP:
			digUpdate();
		default:
			break;
		}
		return true;
	}
	
	private boolean moveUpdate(int scene){
		switch (GM.getScene()) {
		case GM.S_MOVE_STOP:
		case GM.S_MOVE_MOVING:
			//Target座標を書き換え
			MotionEvent event = GM.getEvent();
			if(event != null && event.getAction() == MotionEvent.ACTION_UP) {
				GM.setScene(GM.S_MOVE_MOVING);
				double touchX = GM.getMap().getPointX() + event.getRawX();
				double touchY = GM.getMap().getPointY() + event.getRawY();
				if(GM.getMap().getField().canEnter(touchX, touchY)){
					targetX = touchX;
					targetY = touchY;
				}
			}
			//TargetPointへ移動
			moveToTargetPoint();
			
			//TargetPointに着いたら状態遷移
			if(GM.getScene() == GM.S_MOVE_MOVING){
				if(targetX == x && targetY == y){
					GM.setScene(GM.S_MOVE_DIGGING);
				}
			}
			break;
		case GM.S_MOVE_DIGGING:
			digNum -= digQuickness;
			
			/**
			 * TODO
			 * 距離に応じてクエっ！の表示を出す．
			 * タイムラグを経て，S_MOVE_STOPに移行
			 * もし判定距離以内に宝があれば，タイムラグ後にS_DIG_STOPに移行
			 */
			
			if(digNum <= 0){
				double distance = GM.getField().checkTreasureDistance((double)x, (double)y);
				if(distance < MOST_NEAR_DISTANCE){
					GM.setScene(GM.S_DIG_STOP);
				}else{
					GM.setScene(GM.S_MOVE_STOP);
				}
			}
			break;
		}
		return true;
	}
	
	private boolean digUpdate(){
		/**
		 * TODO
		 * 掘る時のアップデート，
		 * ボタンを押された回数に応じて掘る深さをマイナスしていく
		 */
		MotionEvent event = GM.getEvent();
		if(event != null && event.getAction() == MotionEvent.ACTION_UP) {
			HiddenTreasure hiddenTreasure = GM.getField().getHiddenTreasure();
			hiddenTreasure.dig(nosePower);
			if(hiddenTreasure.getDepth() <= 0){
				/**
				 * TODO
				 * 掘り終わったら宝を見つけた状態に移動
				 */
				GM.setScene(GM.S_FIND);
				
			}
		}
		/**
		 * とりあえず動かすためにすぐにMOVE状態に移行
		 */
		GM.setScene(GM.S_MOVE_STOP);
		return true;
	}
	
	
	/**
	 * 目標座標に向かって移動する
	 * @return
	 */
	private boolean moveToTargetPoint(){
		Vec v = getVec();
		//既に目的地についているとき
		if(v.getMagnitude() == 0){
			return true;
		}
		
		//目的地に着く場合
		if(v.getMagnitude() <= (double)speed){
			x = targetX;
			y = targetY;
		}else{
			v = v.getUnitVec().multi((double)speed);
			double preX = x + v.getX();
			double preY = y + v.getY();

			if(GM.getMap().getField().canEnter(preX, preY)){
				//進んだ先が進入可能領域の場合
				x += v.getX();
				y += v.getY();
			}else{
				//行進ベクトルを左右に１０度ずつ回転していって，初めて進めるようになったベクトル方向に移動
				for(double d = 0; d < Math.PI/2; d += Math.PI/18){
					Vec vRight = v.rotate(d);
					Vec vLeft = v.rotate(-d);
					
					double rightX = x + vRight.getX();
					double rightY = y + vRight.getY();
					if(GM.getMap().getField().canEnter(rightX, rightY)){
						x = rightX;
						y = rightY;
						break;
					}else{
						double leftX = x + vLeft.getX();
						double leftY = y + vLeft.getY();
						if(GM.getMap().getField().canEnter(leftX, leftY)){
							x = leftX;
							y = leftY;
							break;
						}
					}
				}
			}
			
		}
		return true;
	}
	
	/**
	 * Pigから目的地へのベクトルを返す
	 * @return
	 */
	private Vec getVec(){
		return new Vec(targetX- x, targetY-y);
	}
	

	@Override
	public void onDraw(Canvas c) {
		double pigCenterX = x - GM.getMap().getPointX();
		double pigCenterY = y - GM.getMap().getPointY();

		//向いてる向きによってImageを変更
		Bitmap image = getImage();
		c.drawBitmap(image, (int)(pigCenterX - image.getWidth()/2), (int)(pigCenterY - image.getHeight()/2), null);
		
		if(GM.getScene() == GM.S_MOVE_DIGGING){
			drawMoveDiggingScene(c, pigCenterX, pigCenterY);
		}
		//targetを描写
		float startX = (float)(x - GM.getMap().getPointX());
		float startY = (float)(y - GM.getMap().getPointY());
		float stopX = (float)(targetX - GM.getMap().getPointX());
		float stopY = (float)(targetY - GM.getMap().getPointY());
		c.drawLine(startX, startY, stopX, stopY, new Paint());

	}
	
	private void drawMoveDiggingScene(Canvas c, double pigCenterX, double pigCenterY) {
		double distance = GM.getField().checkTreasureDistance((double)x, (double)y);
		
		String text;
		
		if(distance < MOST_NEAR_DISTANCE){
			text = "ブ，ブ，ブヒィィィィ！！！";
		}else{
			if(distance < SECOND_NEAR_DISTANCE){
				text = "ブヒヒィィッ！";
			}else if(distance < THIRD_NEAR_DISTANCE){
				text = "ブヒヒッ！";
			}else if(distance < FOURTH_NEAR_DISTANCE){
				text = "ブヒッ！";
			}else{
				text = "ブヒッ？";
			}
		}
		
		BalloonText cry = new BalloonText(text);
		cry.drawBalloonText(c, (float)pigCenterX, (float)pigCenterY - getImage().getHeight());
	}

	private Bitmap getImage(){
		Bitmap image = null;
		switch (dir) {
		case FRONT:
			image = pigFrontImage;
			break;
		case BACK:
			image = pigBackImage;
			break;
		case LEFT:
			image = pigLeftImage;
			break;
		case RIGHT:
			image = pigRightImage;
			break;
		}
		return image;
	}
	
	private enum Direction{
		FRONT, BACK, RIGHT, LEFT;
	}
	private void changeDirection(){
		Vec v = getVec();
		if(v.getMagnitude() == 0){
			return;
		}else{
			boolean flag1 = (v.getY() > v.getX())? true: false;
			boolean flag2 = (-v.getY() > v.getX())? true: false;
			if(flag1 && flag2){
				dir = Direction.LEFT;
			}else if(flag1 && !flag2){
				dir = Direction.FRONT;
			}else if(!flag1 && flag2){
				dir = Direction.BACK;
			}else{
				dir =Direction.RIGHT;
			}
		}
	}


	public int getDigNum() {
		return digNum;
	}

	public void setDigNum(int digNum) {
		this.digNum = digNum;
	}

	public int getNosePower() {
		return nosePower;
	}

	public void setNosePower(int nosePower) {
		this.nosePower = nosePower;
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public int getDigQuickness() {
		return digQuickness;
	}

	public void setDigQuickness(int digQuickness) {
		this.digQuickness = digQuickness;
	}

	
	

}
