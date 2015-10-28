package com.example.pigdig;

import android.os.CountDownTimer;

public class Timer extends CountDownTimer{

	long restTime;
	public Timer(long millisInFuture, long countDownInterval) {
		super(millisInFuture, countDownInterval);
		restTime = millisInFuture;
	}

	@Override
	public void onTick(long millisUntilFinished) {
		restTime -= millisUntilFinished;
		
	}

	@Override
	public void onFinish() {
		// TODO 自動生成されたメソッド・スタブ
		
	}
	
}
