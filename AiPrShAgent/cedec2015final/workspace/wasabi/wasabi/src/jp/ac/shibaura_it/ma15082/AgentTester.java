package jp.ac.shibaura_it.ma15082;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.aiwolf.common.data.Player;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.net.GameSetting;
import org.aiwolf.kajiClient.player.KajiRoleAssignPlayer;
import org.aiwolf.server.AIWolfGame;
import org.aiwolf.server.net.DirectConnectServer;


public class AgentTester {

	/**
	 * @param args
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public static void main(String[] args) throws InstantiationException, IllegalAccessException {

		
		while(true){
		Player player = new WasabiPlayer();
		
		
		
		Class<Player> pcls = (Class<Player>) player.getClass();
		for(int j = 0; j < 10; j++){
			for(Role requestRole:Role.values()){
				if(requestRole == Role.FREEMASON){
					continue;
				}
				
				player = pcls.newInstance();
				
				Map<Player, Role> playerMap = new HashMap<Player, Role>();
				playerMap.put(player, requestRole);
				for(int i = 0; i < 14; i++){
					playerMap.put(new RandomPlayer(), null);
				}
				
				DirectConnectServer gameServer = new DirectConnectServer(playerMap);
				GameSetting gameSetting = GameSetting.getDefaultGame(playerMap.size());
				AIWolfGame game = new AIWolfGame(gameSetting, gameServer);
				game.setRand(new Random());
				game.start();
			}
		}
	}

}
}