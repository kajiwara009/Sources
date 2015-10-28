package org.aiwolf.Satsuki.lib;

import java.util.Random;

import org.aiwolf.common.data.Role;

public abstract class AbstractFakeRoleChanger {

	public AbstractFakeRoleChanger() 
	{
		// TODO �����������ꂽ�R���X�g���N�^�[�E�X�^�u
	}
	/**
	 * changers��3�i���ŕ\���D initial��1���ځD
	 * @return
	 */
	public abstract int toHash();
	
	public abstract WolfFakeRoleChanger getChanger(int hash);

	/**
	 * random��changer���擾����
	 * @return
	 */
	public abstract WolfFakeRoleChanger getRandomChanger();
	
	public abstract Role getChangerAt(int column);
	
	public abstract void setOneChanger(int column, Role role);
}
