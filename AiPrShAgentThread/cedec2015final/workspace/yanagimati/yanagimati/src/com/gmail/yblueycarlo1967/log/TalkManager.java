package com.gmail.yblueycarlo1967.log;

import java.util.ArrayList;
import java.util.List;

public class TalkManager {
	private List<Talk> talkList;
	public TalkManager(){
		talkList=new ArrayList<Talk>();
	}
	public void addTalk(int day,String talkType,String index,String speaker,String content){
		boolean isWhisper=false;
		if(talkType.equals("whisper")) isWhisper=true;
		Talk talk=new Talk(day,isWhisper,Integer.valueOf(index),Integer.valueOf(speaker),content);
		talkList.add(talk);
	}
	public void printTalk(int day){
		for(Talk talk:talkList){
			if(talk.getDay()==day) System.out.println(talk);
		}
	}

}
