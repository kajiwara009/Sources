package main;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Agent {

	int status; // ０ならS,1ならI
	int id;
	static Random r = new Random();
	
	//double C ;
	double C ;
	final static double P = 0.8;
	final static double R = 0.4;
	final double ratioOfStatus1 = 1.0;
	//List<Agent> otherAgents = new ArrayList<Agent>();

	Agent(int input) {
		if (r.nextDouble() < ratioOfStatus1)
			status = 1;
		else
			status = 0;
		id = input;
		//setC();
	}

	public void step1(List<Agent> agentList) {
		
		
		if (r.nextDouble() < C  ) {
			
			//Agent targetAgent = otherAgents.get(r.nextInt(otherAgents.size())); // ランダムに選ぶ
			Agent targetAgent = getOtherAgent(agentList);
			
			if (r.nextDouble() < P) {

				if (status == 0 && targetAgent.status == 1) {
					status = 1;
				}

				//if (status == 1 && targetAgent.status == 0) {
					//targetAgent.status = 1;
				//}
			}
		
			//
		}
	}
	
	/**
	 * 引数のagentListから自分以外のAgentを返す
	 * @param agentList
	 * @return
	 */
	private Agent getOtherAgent(List<Agent> agentList){
		Agent other = null;
		
		/**
		 * agentListからランダムに取ってきたエージェント otherTemp が，
		 * ・もし自分自身だったら→またランダムにエージェントを取り出す
		 * ・もし自分自身以外だったら→そのエージェントをotherに代入する．
		 * んで，otherを返す．A
		 */
		while(other == null){
			Random rand = new Random();
			Agent otherTemp = agentList.get(rand.nextInt(agentList.size()));
			
			
			if(otherTemp != this){
				other = otherTemp;
			}else{
				if(agentList.size() == 1){ //配列の中身が自分自身しか無かった場合はnullを返す
					return null;
				}
			}
		}
		return other;
	}

	

	// int x = (int) (100 * Math.random());
	// for (int i = 0; i < 100; i++) {
	// if (list.get(i) == 0 && list.get(x) == 1) {
	// list.set(i, 1);
	// }
	// if (list.get(i) == 1 && list.get(x) == 0) {
	// list.set(x, 1);
	// }
	// }

	public void step2() {
		if (r.nextDouble() < R) {
			if (status == 1) {
				status = 0;
			}
		}
	//	System.out.println("Agent:\t" + id + "\t" + status);
	}
	
	
/*	public void setC(){
		
			if(id<50){				
				C = 0.0;				
			}else {				
				C = 1.0;				
			}						
		
	}
 */	
	
}