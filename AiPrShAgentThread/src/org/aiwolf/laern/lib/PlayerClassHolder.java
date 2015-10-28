package org.aiwolf.laern.lib;

import ipa.myAgent.IPARoleAssignPlayer;

import java.util.HashMap;
import java.util.Map;

import org.aiwolf.Satsuki.LearningPlayer.AIWolfMain;
import org.aiwolf.iace10442.ChipRoleAssignPlayer;
import org.aiwolf.kajiPlayer.profitSharing.PrShAssignPlayer;

import takata.player.TakataRoleAssignPlayer;

import com.canvassoft.Agent.CanvasRoleAssignPlayer;
import com.github.haretaro.pingwo.role.PingwoRoleAssignPlayer;
import com.gmail.tydmskz.RoleAssignPlayer;
import com.gmail.yblueycarlo1967.player.YanagimatiRoleAssignPlayer;

import jp.ac.aitech.k13009kk.aiwolf.client.player.AndoRoleAssignPlayer;
import jp.ac.cu.hiroshima.info.cm.nakamura.player.NoriRoleAssignPlayer;
import jp.ac.shibaura_it.ma15082.WasabiRoleAssignPlayer;
import jp.halfmoon.inaba.aiwolf.strategyplayer.StrategyPlayer;
import kainoueAgent.MyRoleAssignPlayer;

public class PlayerClassHolder {
	private Map<String, Class> players = new HashMap<String, Class>();
	
	public Map<String, Class> getPlayerClasses() {
		return players;
	}
	
	public Class getClass(String name){
		if(players.containsKey(name)){
			return players.get(name);
		}else{
			return null;
		}
	}

	public void setPlayers(Map<String, Class> players) {
		this.players = players;
	}

	public PlayerClassHolder(){
//		players.put(, );
		players.put("kituneudon", StrategyPlayer.class);
		players.put("wasabi", WasabiRoleAssignPlayer.class);
		players.put("iace10442", ChipRoleAssignPlayer.class);
		players.put("Satsuki", AIWolfMain.class);
		players.put("IPA", IPARoleAssignPlayer.class);
		players.put("yanagimati", YanagimatiRoleAssignPlayer.class);
		players.put("swingby", MyRoleAssignPlayer.class);
		players.put("lunch_break", com.gmail.octobersky.MyRoleAssignPlayer.class);
		players.put("pingwo", PingwoRoleAssignPlayer.class);
		players.put("canvas", CanvasRoleAssignPlayer.class);
		players.put("itolab", AndoRoleAssignPlayer.class);
		players.put("GofukuLab", TakataRoleAssignPlayer.class);
		players.put("team_fenrir", RoleAssignPlayer.class);
		players.put("Central_Villager", NoriRoleAssignPlayer.class);
//		players.put("MegaWolf", com.gmail.yusatk.players.RoleAssignPlayer.class);
		players.put("kajiwara", PrShAssignPlayer.class);
	}
}

//kituneudon
//wasabi
//iace10442
//Satsuki
//IPA
//yanagimati
//swingby
//lunch_break
//pingwo
//canvas
//itolab
//GofukuLab
//team_fenrir
//Central_Villager
//MegaWolf