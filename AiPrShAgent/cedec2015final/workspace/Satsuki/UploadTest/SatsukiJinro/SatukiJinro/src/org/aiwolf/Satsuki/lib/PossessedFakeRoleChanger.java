package org.aiwolf.Satsuki.lib;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.aiwolf.common.data.Role;

public class PossessedFakeRoleChanger implements Serializable {

	public PossessedFakeRoleChanger() {
		// TODO 自動生成されたコンストラクター・スタブ
	}
	
	public Role getInitial() {
		return initial;
	}

	public void setInitial(Role initial) {
		this.initial = initial;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((initial == null) ? 0 : initial.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PossessedFakeRoleChanger other = (PossessedFakeRoleChanger) obj;
		if (initial != other.initial)
			return false;
		return true;
	}

	public static List<Role> getFakeroles() {
		return fakeRoles;
	}

	public static Map<Role, Integer> getRolehash() {
		return roleHash;
	}

	@SuppressWarnings("serial")
	private static final List<Role> fakeRoles = new ArrayList<Role>(){
		{
			add(Role.SEER);
			add(Role.MEDIUM);
		}
	};
	
	@SuppressWarnings("serial")
	private static final Map<Role, Integer> roleHash = new HashMap<Role, Integer>(){
		{
			put(Role.VILLAGER, 0);
			put(Role.SEER, 1);
			put(Role.MEDIUM, 2);
		}
	};
	
//	private List<Role> changers = new ArrayList<Role>();
	
	private Role 
		initial = Role.SEER;//最初に設定しておく役職
		
	/**
	 * randomにchangerを取得する
	 * @return
	 */
	public static PossessedFakeRoleChanger getRandomChanger(){
		PossessedFakeRoleChanger newChanger = new PossessedFakeRoleChanger();
		newChanger.initial = fakeRoles.get(new Random().nextInt(fakeRoles.size()));
		
		return newChanger;
	}

}
