package com.example.pigdig.task;

import android.graphics.Canvas;

public interface Task {
	public boolean onUpdate();

	public void onDraw(Canvas c);
}
