package jp.ac.shibaura_it.ma15082;

import org.aiwolf.common.data.Agent;

public class Message {
	private Agent from;
	private Agent subject;
	private Colour object;
	
	public Message(Agent f,Agent t,Colour c){
		from=f;
		subject=t;
		object=c;
	}
	
	public Agent getFrom(){
		return from;
	}
	public Agent getSubject(){
		return subject;
	}
	public Colour getObject(){
		return object;
	}
	
	

}
