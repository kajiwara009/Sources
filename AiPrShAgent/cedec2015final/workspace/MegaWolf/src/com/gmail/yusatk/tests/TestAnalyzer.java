/**
 * 
 */
package com.gmail.yusatk.tests;

import static org.junit.Assert.*;

import java.util.*;

import org.aiwolf.common.data.Agent;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.gmail.yusatk.data.*;

/**
 * @author Yu
 *
 */
public class TestAnalyzer {

	Analyzer analyzer = null;
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		analyzer = new Analyzer(null);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link com.gmail.yusatk.data.Analyzer#update(com.gmail.yusatk.interfaces.IGameInfo)}.
	 */
	public void testUpdate() {
		DummyGameInfoCreator gc = new DummyGameInfoCreator();
		gc.setAgents("S,S,M,V,V,V,V");
		analyzer.update(gc.getGameInfo());
		analyzer.Dump();
	}
	
	class GrayTestSet {
		String agents;
		String divines;
		String gray;
		GrayTestSet(String agents, String divines, String gray){
			this.agents = agents;
			this.divines = divines;
			this.gray = gray;
		}
		void test() {
			DummyGameInfoCreator gc = new DummyGameInfoCreator();
			gc.setAgents(agents);
			gc.setDivine(divines);
			Analyzer analyzer = new Analyzer(null);
			analyzer.update(gc.getGameInfo());
			
			List<Agent> grayAgents = analyzer.getGrayAgents();
			List<Agent> expected = new ArrayList<Agent>();
			for(String indexString : gray.split(",")) {
				expected.add(Agent.getAgent(Integer.parseInt(indexString)));
			}
			assertArrayEquals(expected.toArray(), grayAgents.toArray());
		}
	}
	
	@Test
	public void testGrayAgents () {
		GrayTestSet [] testSet = new GrayTestSet[] {
			new GrayTestSet("S,V,V,V", "", "2,3,4"),
			new GrayTestSet("S,S,V,V", "", "3,4"),
			new GrayTestSet("S,S,V,V", "1:3o", "4"),
			new GrayTestSet("S,S,V,V", "1:3x", "4"),
			new GrayTestSet("S,S,V,V,V", "1:3x / 2:4o", "5"),
			new GrayTestSet("S,S,V,V,V,V,V,V,V,V", "1:3x,4o / 2:4o,7o,8o", "5,6,9,10"),
		};
		
		for(GrayTestSet test : testSet) {
			test.test();
		}
	}

}
