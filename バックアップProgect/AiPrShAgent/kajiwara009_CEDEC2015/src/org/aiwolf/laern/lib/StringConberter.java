package org.aiwolf.laern.lib;

import org.aiwolf.common.data.Role;

public class StringConberter {

	public StringConberter() {
		// TODO 自動生成されたコンストラクター・スタブ
	}
	
	public static String toString(Role role){
		if(role == null){
			return "n";
		}
		switch (role) {
		case BODYGUARD:
			return "B";
		case MEDIUM:
			return "M";
		case FREEMASON:
			return "F";
		case POSSESSED:
			return "P";
		case SEER:
			return "S";
		case VILLAGER:
			return "V";
		case WEREWOLF:
			return "W";
		default:
			return "n";
		}
	}
	
	public static Role parseRole(String s){
		if(s.equals("B")){
			return Role.BODYGUARD;
		}else if(s.equals("M")){
			return Role.MEDIUM;
		}else if(s.equals("F")){
			return Role.FREEMASON;
		}else if(s.equals("P")){
			return Role.POSSESSED;
		}else if(s.equals("S")){
			return Role.SEER;
		}else if(s.equals("V")){
			return Role.VILLAGER;
		}else if(s.equals("W")){
			return Role.WEREWOLF;
		}else if(s.equals("n")){
			return null;
		}else{
			return null;
		}
	}
	
	public static String toString(boolean bool){
		if(bool){
			return "T";
		}else{
			return "F";
		}
	}
	
	public static boolean parseBoolean(String str){
		if(str.equals("T")){
			return true;
		}else{
			return false;
		}
	}
	
	

}
