package com.example.pigdig;

import com.example.pigdig.data.Field;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameSurfaceView extends SurfaceView implements
		SurfaceHolder.Callback, Runnable {
	private GM _gameMgr;
	private Thread _thread;
	private MotionEvent event;
	private boolean isEventUsed;

	public GameSurfaceView(Context context) {
		super(context);
		getHolder().addCallback(this);
		
		SharedPreferences preferences = context.getSharedPreferences("CONFIG", Context.MODE_PRIVATE);
		
		/**
		 * TODO
		 * 本当は記録にあわせてかえる
		 */
		SharedPreferences.Editor editor = preferences.edit();
		editor.putInt("NOSE_POWER", 5);
		editor.putInt("SPEED", 10);
		editor.putInt("DIG_QUICKNESS", 10);
		editor.putInt("DISCERN", 0);
		editor.commit();
		
		_gameMgr = new GM(preferences, getResources());
		//指定されたステージの画像ファイルを取得
		_gameMgr.setTaskList();
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// 解像度情報変更通知
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		_thread = new Thread(this); // 別スレッドでメインループを作る
		_thread.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		_thread = null;
	}

	@Override
	public void run() {
		while (_thread != null) { // メインループ
			GM.setEvent(event);
			isEventUsed = true;
			
			//Taskを更新
			_gameMgr.onUpdate();
			
			//画面描写
			onDraw(getHolder());
			
			
			//ゲームオーバーなら
			if(GM.getScene() == GM.S_GAMEOVER){
				if(event != null){
					/*
					 * TODO
					 * ゲームオーバー後，どこかタッチされたら次の画面に遷移
					 * おそらくResultActivityに遷移
					 * ゲーム内で得たアイテムの情報などをインテントに含む？
					 */
					
				}
			}
			if(isEventUsed){
				event = null;
			}
/*			try {
				_thread.sleep(1);
			} catch (InterruptedException ex) {
				// TODO 自動生成された catch ブロック
				ex.printStackTrace();
			}
*/		}
	}

	private void onDraw(SurfaceHolder holder) {
		Canvas c = holder.lockCanvas();
		if (c == null) {
			return;
		}
		_gameMgr.onDraw(c);
		holder.unlockCanvasAndPost(c);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		System.out.println("画面がタッチされました");
		this.event = event;
		this.isEventUsed = false;
		return true;
	}
}