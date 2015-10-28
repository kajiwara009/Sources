package com.example.pigdig.task;

import java.util.Random;

import com.example.pigdig.GM;
import com.example.pigdig.data.Field;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public class Map implements Task {
	private Field field;
	
	//現在描写している部分の左上の絶対座標
	private double pointX, pointY;
	
	
	public Map(Field field) {
		this.field = field;
		
	}

	@Override
	public boolean onUpdate() {
		//描写する位置をPigの座標にあわせて更新
		return true;
	}
	

	@Override
	public void onDraw(Canvas c) {
		double	imageWidth	= field.getWidth(),
				imageHeight	= field.getHeight(),
				cWidth		= (double)c.getWidth(),
				cHeight		= (double)c.getHeight(),
				pigX		= GM.getPig().getX(),
				pigY		= GM.getPig().getY();
		//描写する部分の左上端の絶対座標
//		System.out.println("imagewidth:" + imageWidth + "   imageheight:"+ imageHeight );
//		System.out.println("cWidth:" + cWidth + "   cHeight" + cHeight);
//		System.out.println("pigX:" + pigX + "   pigY" + pigY);

		pointX = pigX - cWidth/2;
		pointY = pigY - cHeight/2;

		//画像とキャラが近すぎたら
		if(pointX < 0){
			pointX = 0;
		}else if(pointX + cWidth > imageWidth){
			pointX = imageWidth - cWidth;
		}
		if(pointY < 0){
			pointY = 0;
		}else if(pointY + cHeight > imageHeight){
			pointY = imageHeight - cHeight;
		}
		
		Rect src = new Rect((int)pointX, (int)pointY, (int)(pointX + cWidth), (int)(pointY + cHeight));
		Rect dst = new Rect(0, 0, (int)cWidth, (int)cHeight);
		c.drawBitmap(field.getImage(), src, dst, null);
		c.drawCircle((float)(field.getHiddenTreasure().getX()-pointX), (float)(field.getHiddenTreasure().getY()-pointY), 10, new Paint());
	}

	public double getPointX() {
		return pointX;
	}

	public void setPointX(double pointX) {
		this.pointX = pointX;
	}

	public double getPointY() {
		return pointY;
	}

	public void setPointY(double pointY) {
		this.pointY = pointY;
	}

	public Field getField() {
		return field;
	}

	public void setField(Field field) {
		this.field = field;
	}
}
