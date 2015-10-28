package com.example.pigdig;

import java.util.LinkedList;

import com.example.pigdig.data.Field;
import com.example.pigdig.task.FpsController;
import com.example.pigdig.task.Map;
import com.example.pigdig.task.Pig;
import com.example.pigdig.task.Task;

import android.app.backup.SharedPreferencesBackupHelper;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.MotionEvent;

public class GM {
	//scene定数
	public static final int
		S_MOVE_STOP = 0,
		S_MOVE_MOVING = 1,
		S_MOVE_DIGGING = 2,
		S_DIG_STOP = 3,
		S_DIG_DIGGING =4,
		S_FIND = 5,
		S_GAMEOVER = 6;
	
	//ゲームの状態を表す
	private static int scene = S_MOVE_STOP;
	
	//StageSelectで選択されたステージのField
	private static Field field;
	
	//GameSurfaceViewで得たMotionEvent
	private static MotionEvent event;
	
	private static Pig pig;
	
	private static Map map;
	
	private static Resources r;
	
	//セーブデータ．鼻の強さや足の速さ等
	public static SharedPreferences preferences;
	
	// タスクリスト
	private LinkedList<Task> _taskList = new LinkedList<Task>();

	GM(SharedPreferences preferences, Resources r) {
		this.preferences = preferences;
		this.r = r;
		this.field = new Field(r, preferences);
		field.setRandomTreasure();
		this.pig = new Pig(preferences);
		this.map = new Map(field);
	}
	

	public void setTaskList(){
		_taskList.add(map);
		_taskList.add(pig);
		_taskList.add(new FpsController());
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean onUpdate() {
		for (int i = 0; i < _taskList.size(); i++) {
			if (_taskList.get(i).onUpdate() == false) { // 更新失敗なら
				_taskList.remove(i); // そのタスクを消す
				i--;
			}
		}
		event = null;
		return true;
	}

	public void onDraw(Canvas c) {
		c.drawColor(Color.WHITE); // 白で塗りつぶす
		for (int i = 0; i < _taskList.size(); i++) {
			_taskList.get(i).onDraw(c);// 描画
		}
	}

	
	
	
	
	
	
	
	
	
	
	
	
	//以下，setterとgetter

	public static MotionEvent getEvent() {
		return event;
	}
	
	public static void setEvent(MotionEvent event) {
		GM.event = event;
	}

	public static int getScene() {
		return scene;
	}

	public static void setScene(int scene) {
		GM.scene = scene;
		if(scene == S_MOVE_DIGGING){
			pig.setDigNum(Pig.MAX_DIG_NUM);
		}
		else if(scene == S_FIND){
			field.setRandomTreasure();
		}
	}

	public static Pig getPig() {
		return pig;
	}

	public static void setPig(Pig pig) {
		GM.pig = pig;
	}

	public static Map getMap() {
		return map;
	}

	public static void setMap(Map map) {
		GM.map = map;
	}

	public static SharedPreferences getPreferences() {
		return preferences;
	}

	public static void setPreferences(SharedPreferences preferences) {
		GM.preferences = preferences;
	}

	public static Field getField() {
		return field;
	}

	public void setField(Field field){
		GM.field = field;
	}

	public static Resources getR() {
		return r;
	}

	public static void setR(Resources r) {
		GM.r = r;
	}


}