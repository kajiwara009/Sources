package org.aiwolf.Satsuki.reinforcementLearning;

import java.util.List;

import org.aiwolf.common.data.Role;

public enum WolfRolePattern {
//	
//	VVV(3,0,0),
//	VVS(2,1,0),
//	VVM(2,0,1),
//	VSS(1,2,0),
//	VSM(1,1,1),
//	VMM(1,0,2),
//	SSS(0,3,0),
//	SSM(0,2,1),
//	SMM(0,1,2),
//	MMM(0,0,3);
//	
	VV(2,0,0),
	VS(1,1,0),
	VM(1,0,1),
	SS(0,2,0),
	SM(0,1,1),
	MM(0,0,2);
	
	private int villagerNum;
	private int seerNum;
	private int mediumNum;
	
	WolfRolePattern(int v, int s, int m){
		villagerNum = v;
		seerNum = s;
		mediumNum = m;
	}
	
	public static WolfRolePattern getWolfRolePattern(List<Role> wolfsFaleRoles){
		int v = 0,
			s = 0,
			m = 0;
		for(Role r: wolfsFaleRoles){
			switch (r) {
			case VILLAGER:
				v++;
				break;
			case SEER:
				s++;
				break;
			case MEDIUM:
				m++;
				break;
			}
		}
		
		
		for(WolfRolePattern w: WolfRolePattern.values()){
			if(w.villagerNum == v && w.seerNum == s && w.mediumNum == m){
				return w;
			}
		}
		return null;
		
	}
}
