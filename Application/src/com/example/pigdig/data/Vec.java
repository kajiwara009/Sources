package com.example.pigdig.data;

public class Vec {
	private double x;
	private double y;
	
	public Vec(double x, double y){
		this.x = x;
		this.y = y;
	}
	
	public Vec getUnitVec(){
		double magnitude = getMagnitude();
		return new Vec(x / magnitude, y / magnitude);
	}
	
	public double getMagnitude(){
		return Math.sqrt(x*x + y*y);
	}
	
	public double sin(){
		return y / ( x*x + y*y);
	}
	public double cos(){
		return x / ( x*x + y*y);
	}
	
	public double tan(){
		return y/x;
	}
	
	public Vec multi(double scale){
		return new Vec(x * scale, y * scale);
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
	
	public Vec clone(){
		return new Vec(x, y);
	}
	
	public Vec rotate(double sita){
		double newX = Math.cos(sita) * x - Math.sin(sita) * y;
		double newY = Math.sin(sita) * x + Math.cos(sita) * y;
		return new Vec(newX, newY);
	}

}
