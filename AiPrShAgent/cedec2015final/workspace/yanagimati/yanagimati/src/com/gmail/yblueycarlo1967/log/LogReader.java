package com.gmail.yblueycarlo1967.log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class LogReader {
	private static int PLAYER_NUM =15;
	public LogReader(){
		
	}
	public static void main(String[] args) throws IOException {
		File file = new File("log/aiwolf_1.log");
		BufferedReader br = new BufferedReader(new FileReader(file));
		StatusManager sm=new StatusManager();
		TalkManager tm=new TalkManager();
		String line = br.readLine();
		while(line != null){
		    //System.out.println(line);
		    //分割
		    String[] split = line.split(",", 0);
		    int day=Integer.valueOf(split[0]);
		    switch(split[1]){
		    case "status":
		    	sm.addPlayerStatus(day,split[2], split[3], split[4]);
		    	break;
		    
		    case "talk":
		    case "whisper":
		    	tm.addTalk(day, split[1],split[2],split[3], split[4]);
		    	break;
		    case "vote":
		    	break;
		    case "divine":
		    	break;
		    case "guard":
		    	break;
		    case "attackVote":
		    	break;
		    case "execute":
		    	break;
		    case "attack":
		    	break;
		    }
		    line = br.readLine();
	  }
	  //sm.printPlayerStatuses(2);
		tm.printTalk(1);
	  br.close();
	}

}

