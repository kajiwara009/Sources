package com.gmail.yblueycarlo1967.log;

public class Status {
	private int index;
	private String role;
	private boolean alive;
	private int day;
	public Status(int day,int index,String role,boolean alive){
		this.index=index;
		this.role=role;
		this.alive=alive;
		this.day=day;
	}
	public int getDay(){
		return day;
	}
	public String toString(){
		return day+","+index+","+role+","+alive;
	}
}
