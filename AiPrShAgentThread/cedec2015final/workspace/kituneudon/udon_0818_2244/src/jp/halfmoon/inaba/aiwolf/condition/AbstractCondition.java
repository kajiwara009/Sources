package jp.halfmoon.inaba.aiwolf.condition;

import java.util.ArrayList;

import jp.halfmoon.inaba.aiwolf.lib.WolfsidePattern;


/**
 * ������\�����ۃN���X
 */
public abstract class AbstractCondition {


	/**
	 * �����𖞂�����
	 * @return
	 */
	abstract public boolean isValid( WolfsidePattern pattern );


	/**
	 * �ΏۂƂȂ�G�[�W�F���g�̔ԍ��ꗗ���擾����
	 * @return
	 */
	abstract public ArrayList<Integer> getTargetAgentNo();


}
