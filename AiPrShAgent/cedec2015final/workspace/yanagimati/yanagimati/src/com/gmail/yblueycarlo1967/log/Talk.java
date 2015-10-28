package com.gmail.yblueycarlo1967.log;

import org.aiwolf.client.lib.Topic;
import org.aiwolf.client.lib.Utterance;

public class Talk {
	private int day;
	private boolean isWhisper;
	private int speaker;
	private int index;
	private String content;
	public Talk(int day,boolean isWhisper,int index,int speaker,String content){
		this.day=day;
		this.isWhisper=isWhisper;
		this.index=index;
		this.speaker=speaker;
		this.content=content;
	}
	public int getDay(){
		return day;
	}
	public Utterance getUtterance(){
		return new Utterance(this.content);
	}
	public String toString(){
		String talkType="talk";
		if(isWhisper) talkType="whisper";
		return day+","+talkType+","+index+","+speaker+","+content;
	}
	

}
