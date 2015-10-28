package org.aiwolf.Satsuki.lib;

import org.aiwolf.common.data.Role;

/*
 *	@brief		�Q�[���Ɋւ�������v�Z�A�����ł���Util�I�ȃN���X 
 */
public class MyGameInfo 
{
	/*
	 * @brief		�Q�[���J�n���̖�E���Ƃ̃G�[�W�F���g����Ԃ��܂��B
	 * @return		�G�[�W�F���g��
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
	 * @brief		�G�̍ő吔���擾
	 * @return		�G�̍ő吔
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
