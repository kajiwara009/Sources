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
 * �G�[�W�F���g���e�X�g���邽�߂̃N���X<br>
 * ����G�[�W�F���g��Player�Ƃ��Ďw�肵�C�����_���v���C���[�Ƒΐ킷�邱�Ƃ�
 * ���肪�\�z�O�̍s�����s�����ۂɔ�������Exception��T�����Ƃ��o���܂��D
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
		//�����Ƀe�X�g������������Player���w�肵�Ă��������D

		Player player = new AIWolfMain();
		/////////////////////////////////////////////
		//����ȍ~�͕ύX���Ȃ��ł��������D
		
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
