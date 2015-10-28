package data;

import codevs.God;

public class Vector {
	private int x;
	private int y;
	
	public Vector(int x, int y){
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
	
	public Vector clone(){
		return new Vector(x, y);
	}
	
	public boolean equals(Vector v){
		if(v.x == x && v.y == y){
			return true;
		}else{
			return false;
		}
	}
	
	public Vector minus(Vector v){
		int newX = this.x - v.x;
		int newY = this.y - v.y;
		return new Vector(newX, newY);
	}
	
	public Vector plus(Vector v){
		int newX = this.x + v.x;
		int newY = this.y + v.y;
		return new Vector(newX, newY);
	}
	
	public Vector multi(int multi){
		int newX = this.x * multi;
		int newY = this.y * multi;
		return new Vector(newX, newY);
	}
	
	public boolean isOnField(){
		if(x >= 0 && x <= God.MAP_END){
			if(y >= 0 && y <= God.MAP_END){
				return true;
			}
		}
		return false;
	}
	
	public Direction getAboutDirection(){
		Vector about;
		if(x == 0 && y == 0) return null;
		if(x * x > y * y) about = new Vector(x / Math.abs(x), 0);
		else about = new Vector(0, y / Math.abs(y));
		return Direction.getDirection(about);
	}
	
	public Direction getSubAboutDirection(){
		Vector about;
		if(x == 0 || y == 0) return null;
		if(x * x > y * y) about = new Vector(0, y / Math.abs(y));
		else about = new Vector(x / Math.abs(x), 0);
		return Direction.getDirection(about);
	}
	
	/**
	 * マンハッタン距離を返す
	 */
	public int getMhtDist(Vector v){
		return Math.abs(x - v.x) + Math.abs(y - v.y);
	}
}
