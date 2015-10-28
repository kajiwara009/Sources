package com.gmail.yusatk.tests;

import static org.junit.Assert.*;

import com.gmail.yusatk.interfaces.*;
import com.gmail.yusatk.data.*;

import org.aiwolf.common.data.Agent;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestScoreMap {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testMerge() {
		ScoreMap s1 = new ScoreMap();
		s1.addAgent(Agent.getAgent(1), 1);
		s1.addAgent(Agent.getAgent(2), 2);
		s1.addAgent(Agent.getAgent(3), 3);
		s1.addAgent(Agent.getAgent(4), 4);

		ScoreMap s2 = new ScoreMap();
		s2.addAgent(Agent.getAgent(2), -2);
		s2.addAgent(Agent.getAgent(3), -3);
		s2.addAgent(Agent.getAgent(4), -4);
		s2.addAgent(Agent.getAgent(5), -5);
		
		IScoreMap merged = s1.merge(s2);
		
		assertEquals(1,	merged.getScore(Agent.getAgent(1)));
		assertEquals(0,	merged.getScore(Agent.getAgent(2)));
		assertEquals(0,	merged.getScore(Agent.getAgent(3)));
		assertEquals(0,	merged.getScore(Agent.getAgent(4)));
		assertEquals(-5,	merged.getScore(Agent.getAgent(5)));
	}

}
