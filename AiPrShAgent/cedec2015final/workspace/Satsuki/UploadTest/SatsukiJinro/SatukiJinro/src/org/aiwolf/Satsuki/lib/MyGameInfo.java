package org.aiwolf.Satsuki.lib;

import org.aiwolf.common.data.Role;

/*
 *	@brief		ゲームに関する情報を計算、所得できるUtil的なクラス 
 */
public class MyGameInfo 
{
	/*
	 * @brief		ゲーム開始時の役職ごとのエージェント数を返します。
	 * @return		エージェント数
	 */
	public static int getMaxAgentNum(Role role)
	{
		switch(role)
		{
		case SEER: return 1;
		case MEDIUM: return 1;
		case BODYGUARD: return 1;

		case POSSESSED: return 1;
		case WEREWOLF: return 3;
		
		case VILLAGER: return 8;
		}
		return 0;
	}
	
	/*
	 * @brief		敵の最大数を取得
	 * @return		敵の最大数
	 */
	public static int getMaxEnemyNum()
	{
		return 4;
	}
	
	public static boolean IS_PRINT()
	{
		return true;
	}
}
