package com.gmail.yblueycarlo1967.log;

import java.util.ArrayList;
import java.util.List;

public class StatusManager {
	private List<Status> playerStatuses;
	public StatusManager(){
		this.playerStatuses=new ArrayList<Status>();
	}
	public void addPlayerStatus(int day,String index,String role,String alive){
		boolean isAlive=false;
		if(alive.equals("ALIVE")) isAlive=true;
		Status status=new Status(day,Integer.valueOf(index),role,isAlive);
		playerStatuses.add(status);
	}
	public void printPlayerStatuses(int day){
		for(Status status:playerStatuses){
			if(status.getDay()==day) System.out.println(status);
		}
	}

}
