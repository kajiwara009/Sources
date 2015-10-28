package com.example.pigdig.task;

import java.util.Random;

import com.example.pigdig.GM;

public abstract class OnMapItem {
	double x, y;
	
	public void setAtRandomPoint(){
		boolean canEnter = false;
		while(!canEnter){
			int randX = new Random().nextInt(GM.getField().getEnterableMap().getWidth());
			int randY = new Random().nextInt(GM.getField().getEnterableMap().getHeight());
			if(GM.getField().canEnter(randX, randY)){
				x = randX;
				y = randY;
				canEnter = true;
			}
		}
	}
	
	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}


}
