package org.aiwolf.Satsuki.lib;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.aiwolf.common.data.Role;

/**
 * changers‚ÉF‚ñ‚Èó‹µ‰º‚Å‚Ç‚Ì–ğE‚ğéx‚é‚©‚Ìƒf[ƒ^‚ğ•Û‘¶‚µ‚Ä‚¨‚­
 * @author kajiwarakengo
 *
 */
public class WolfFakeRoleChanger implements Serializable{
	@SuppressWarnings("serial")
	private static final List<Role> fakeRoles = new ArrayList<Role>(){
		{
			add(Role.VILLAGER);
			add(Role.SEER);
			add(Role.MEDIUM);
		}
	};
	
//	private List<Role> changers = new ArrayList<Role>();
	
	private Role 
		initial = Role.VILLAGER,//Å‰‚Éİ’è‚µ‚Ä‚¨‚­–ğE
		existVillagerWolf = Role.VILLAGER,//‘Š•û‚Ìl˜T‚ª‘ºl‚ğéx‚é‚Æ‚¢‚Á‚½‚Ééx‚é–ğE
		existSeerWolf = Role.VILLAGER,
		existMediumWolf = Role.VILLAGER;
/*		seerCO = Role.VILLAGER,//è‚¢t‚ªo‚Ä‚«‚½‚Æ‚«‚Ééx‚é–ğE
		mediumCO = Role.VILLAGER,
*/
	public WolfFakeRoleChanger() 
	{
	}
	
	/**
	 * random‚Échanger‚ğæ“¾‚·‚é
	 * @return
	 */
	public static WolfFakeRoleChanger getRandomChanger()
	{
		WolfFakeRoleChanger newChanger = new WolfFakeRoleChanger();
		newChanger.initial = fakeRoles.get(new Random().nextInt(fakeRoles.size()));
		newChanger.existVillagerWolf = fakeRoles.get(new Random().nextInt(fakeRoles.size()));
		newChanger.existSeerWolf = fakeRoles.get(new Random().nextInt(fakeRoles.size()));
		newChanger.existMediumWolf = fakeRoles.get(new Random().nextInt(fakeRoles.size()));
		
		return newChanger;
	}
	
	
	
	
	public Role getInitial() {
		return initial;
	}

	public void setInitial(Role initial) {
		this.initial = initial;
	}


	public Role getExistVillagerWolf() {
		return existVillagerWolf;
	}

	public void setExistVillagerWolf(Role existVillagerWolf) {
		this.existVillagerWolf = existVillagerWolf;
	}

	public Role getExistSeerWolf() {
		return existSeerWolf;
	}

	public void setExistSeerWolf(Role existSeerWolf) {
		this.existSeerWolf = existSeerWolf;
	}

	public Role getExistMediumWolf() {
		return existMediumWolf;
	}

	public void setExistMediumWolf(Role existMediumWolf) {
		this.existMediumWolf = existMediumWolf;
	}

/*	public Role getSeerCO() {
		return seerCO;
	}

	public void setSeerCO(Role seerCO) {
		this.seerCO = seerCO;
	}

	public Role getMediumCO() {
		return mediumCO;
	}

	public void setMediumCO(Role mediumCO) {
		this.mediumCO = mediumCO;
	}
*/
	public static List<Role> getFakeroles() {
		return fakeRoles;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((existMediumWolf == null) ? 0 : existMediumWolf.hashCode());
		result = prime * result
				+ ((existSeerWolf == null) ? 0 : existSeerWolf.hashCode());
		result = prime
				* result
				+ ((existVillagerWolf == null) ? 0 : existVillagerWolf
						.hashCode());
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
		WolfFakeRoleChanger other = (WolfFakeRoleChanger) obj;
		if (existMediumWolf != other.existMediumWolf)
			return false;
		if (existSeerWolf != other.existSeerWolf)
			return false;
		if (existVillagerWolf != other.existVillagerWolf)
			return false;
		if (initial != other.initial)
			return false;
		return true;
	}

}
