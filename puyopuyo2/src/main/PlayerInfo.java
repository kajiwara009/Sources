package main;

import java.util.HashMap;

import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.AbstractPlayer;

public class PlayerInfo {
	HashMap<Integer,String> playerNameMap;
	HashMap<String, AbstractPlayer> playerClassMap;
	HashMap<String,String> playerDirectoryNameMap;
	
	public PlayerInfo(){
		playerNameMap = new HashMap<Integer, String>();
		playerClassMap = new HashMap<String, AbstractPlayer>();
		playerDirectoryNameMap = new HashMap<String, String>();
	}
	
	public void addPlayerInfo(Integer index,String name,AbstractPlayer className,String directory){
		playerNameMap.put(index, name);
		playerClassMap.put(name, className);
		playerDirectoryNameMap.put(name, directory);
	}
	
}
