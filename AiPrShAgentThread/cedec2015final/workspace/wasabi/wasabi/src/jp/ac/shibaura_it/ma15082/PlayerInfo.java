package jp.ac.shibaura_it.ma15082;

import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Species;


public class PlayerInfo {
	private Agent agent;

	private Role role;
	private ListMap<Agent,Colour> seer_data;
	private ListMap<Agent,Colour> medium_data;
	private double certain;
	private boolean checked;
	private boolean voted;
	private boolean vited;
	
	public PlayerInfo(Agent p){
		agent=p;
		role=Role.VILLAGER;
		seer_data=new ListMap<Agent,Colour>();
		medium_data=new ListMap<Agent,Colour>();
		certain=0;
		checked=false;
		voted=false;
		vited=false;
	}
	
	public PlayerInfo(PlayerInfo pi){
		agent=pi.agent;
		role=pi.role;
		seer_data=new ListMap<Agent,Colour>(pi.seer_data);
		medium_data=new ListMap<Agent,Colour>(pi.medium_data);
		certain=pi.certain;
		checked=pi.checked;
		voted=pi.voted;
		vited=pi.vited;
	}
	
	public String toString(){
		return agent+" "+role+" "+certain;
	}
	
	public void setVited(){
		vited=true;
	}
	public void setVoted(){
		voted=true;
	}
	public void setCertain(double c){
		certain=c;
		
	}
	public boolean setRole(Role r){
		if(role==Role.VILLAGER || role==r){
			role=r;
			return false;
		}
		else{
			role=Role.WEREWOLF;
			return true;
		}
	}
	
	
	
	public Role getRole(){
		return role;
	}
	
	public Agent getAgent(){
		return agent;
	}
	public double getCertain(){
		return certain;
	}

	public boolean isVited(){
		return vited;
	}
	public boolean isVoted(){
		return voted;
	}
	public boolean isDead(){
		return (voted || vited);
	}
	public boolean isAlive(){
		return !isDead();
	}
	
	public void setSeer(Agent from,Species s){
		Colour c=(s==Species.HUMAN?Colour.WHITE:Colour.BLACK);
		seer_data.put(from,c);
		checked=true;
	}
	public void setMedium(Agent from,Species s){
		Colour c=(s==Species.HUMAN?Colour.WHITE:Colour.BLACK);		
		medium_data.put(from,c);
		checked=true;
	}

	public boolean isChecked() {
		return checked;
	}
	public boolean isChecked(Agent from){
		if(seer_data.containsKey(from) || medium_data.containsKey(from)){
			return true;
		}
		return false;
	}
	
	
	
	
}
