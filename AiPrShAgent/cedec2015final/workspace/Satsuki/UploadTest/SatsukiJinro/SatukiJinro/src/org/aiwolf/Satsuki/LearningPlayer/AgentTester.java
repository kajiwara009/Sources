package org.aiwolf.Satsuki.LearningPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.aiwolf.client.base.smpl.SampleRoleAssignPlayer;
import org.aiwolf.common.data.Player;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.net.GameSetting;
import org.aiwolf.kajiClient.player.KajiRoleAssignPlayer;
import org.aiwolf.server.AIWolfGame;
import org.aiwolf.server.net.DirectConnectServer;

/**
 * エージェントをテストするためのクラス<br>
 * 自作エージェントをPlayerとして指定し，ランダムプレイヤーと対戦することで
 * 相手が予想外の行動を行った際に発生するExceptionを探すことが出来ます．
 * @author tori
 *
 */
public class AgentTester {

	/**
	 * @param args
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public static void main(String[] args) throws InstantiationException, IllegalAccessException {

		/*
		{
			for(Role requestRole:Role.values())
			{
				if(requestRole == Role.FREEMASON)
				{
					continue;
				}
				
				Map<Player, Role> playerMap = new HashMap<Player, Role>();
				for(int i = 0; i < 15; i++)
				{
					if (requestRole == Role.WEREWOLF)
					{
						playerMap.put(new AIWolfMain(), requestRole);						
					}
					else
					{
						playerMap.put(new SampleRoleAssignPlayer(), requestRole);						
					}
				}
				
				DirectConnectServer gameServer = new DirectConnectServer(playerMap);
				GameSetting gameSetting = GameSetting.getDefaultGame(playerMap.size());
				AIWolfGame game = new AIWolfGame(gameSetting, gameServer);
				game.setRand(new Random());
				game.start();
			}
			if (true)
			{
				return;			
			}
		}
		*/
		//ここにテストしたい自分のPlayerを指定してください．

		Player player = new AIWolfMain();
		/////////////////////////////////////////////
		//これ以降は変更しないでください．
		
		Class<Player> pcls = (Class<Player>) player.getClass();
		for(int j = 0; j < 1000; j++)
		{
			for(Role requestRole:Role.values()){
				// System.gc();
				if(requestRole == Role.FREEMASON){
					continue;
				}
				
				player = pcls.newInstance();
				
				Map<Player, Role> playerMap = new HashMap<Player, Role>();
				playerMap.put(player, requestRole);
				for(int i = 0; i < 14; i++){
					AIWolfMain player2 = new AIWolfMain();
					player2.setLDNumber(i + 1);
					playerMap.put(player2, null);
					//playerMap.put(new SampleRoleAssignPlayer(), null);
				}
				
				DirectConnectServer gameServer = new DirectConnectServer(playerMap);
				GameSetting gameSetting = GameSetting.getDefaultGame(playerMap.size());
				AIWolfGame game = new AIWolfGame(gameSetting, gameServer);
				game.setShowConsoleLog(true);
				game.setRand(new Random());
				game.start();
				
				break;
			}
		}
	}

}
