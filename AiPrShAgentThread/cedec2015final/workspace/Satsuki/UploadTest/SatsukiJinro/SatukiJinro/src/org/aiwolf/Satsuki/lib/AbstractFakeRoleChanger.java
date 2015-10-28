package org.aiwolf.Satsuki.lib;

import java.util.Random;

import org.aiwolf.common.data.Role;

public abstract class AbstractFakeRoleChanger {

	public AbstractFakeRoleChanger() 
	{
		// TODO 自動生成されたコンストラクター・スタブ
	}
	/**
	 * changersを3進数で表す． initialが1桁目．
	 * @return
	 */
	public abstract int toHash();
	
	public abstract WolfFakeRoleChanger getChanger(int hash);

	/**
	 * randomにchangerを取得する
	 * @return
	 */
	public abstract WolfFakeRoleChanger getRandomChanger();
	
	public abstract Role getChangerAt(int column);
	
	public abstract void setOneChanger(int column, Role role);
}
