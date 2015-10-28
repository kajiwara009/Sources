package com.gmail.yusatk.tests;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.aiwolf.common.data.*;
import org.junit.Test;

import com.gmail.yusatk.data.World;

public class TestWorld {

	class TestCaseSet {
		TestCaseSet(String v1, String v2, boolean expected) {
			this.v1 = v1;
			this.v2 = v2;
			this.expected = expected;
		}
		String v1;
		String v2;
		boolean expected;
	}
	
	Map<Agent, Role> createRoleMap(String roleString) {
		String [] roles = roleString.split(",");
		@SuppressWarnings("serial")
		Map<String, Role> table = new HashMap<String, Role>() {
			{put("s", Role.SEER);}
			{put("m", Role.MEDIUM);}
			{put("b", Role.BODYGUARD);}
			{put("p", Role.POSSESSED);}
			{put("w", Role.WEREWOLF);}
			{put("v", Role.VILLAGER);}
		};
		Map<Agent, Role> roleMap = new HashMap<Agent, Role>();
		for(int i = 0; i < roles.length; ++i) {
			Agent agent = Agent.getAgent(i + 1);
			Role role = table.get(roles[i].toLowerCase());
			roleMap.put(agent, role);
		}
		return roleMap;
	}
	
	void testEquals(TestCaseSet testCase) {
		World w1 = new World();
		w1.setRoles(createRoleMap(testCase.v1));

		World w2 = new World();
		w2.setRoles(createRoleMap(testCase.v2));
		
		assertEquals(testCase.expected, w1.equals(w2));
	}
	
	@Test
	public void testEquals() {
		TestCaseSet [] tests = new TestCaseSet[] {
			new TestCaseSet("S,M,B,P,W,V", "S,M,B,P,W,V", true),
			new TestCaseSet("S,M,B,P,W,V", "S,M,B,P,W", false),
			new TestCaseSet("S,M,B,P,W,V", "M,S,B,P,W,V", false),
			new TestCaseSet("S,M,B,P,W,V", "S,M,B,P,W,V,V", false),
		};
		
		for(TestCaseSet testCase : tests) {
			testEquals(testCase);
		}
	}

}
