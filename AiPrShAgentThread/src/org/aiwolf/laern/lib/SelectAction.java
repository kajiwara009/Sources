package org.aiwolf.laern.lib;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Judge;
import org.aiwolf.common.data.Species;

public enum SelectAction {
/*	SEER1, SEER2, SEER3,
	MEDIUM1, MEDIUM2, MEDIUM3,
	JUDGED_WOLF_FROM_1, JUDGED_WOLF_FROM_2, JUDGED_WOLF_FROM_3,
	JUDGED_HUMAN_FROM_1, JUDGED_HUMAN_FROM_2, JUDGED_HUMAN_FROM_3,
	OTHERS,RANDOM
*/	
	S1, S2, S3,
	M1, M2, M3,
	JW1, JW2, JW3,
	JH1, JH2, JH3,
	OT,RA

	;
	
	public static Set<SelectAction> getAbleSelectActions(Information info){
		Set<SelectAction> actions = new HashSet<SelectAction>();
		
		List<Agent> seerList = info.getSeerCOList();
		List<Agent> mediumList = info.getMediumCOList();

		//Otherがいるかどうかの判定のためのSet
		Set<Agent> taged = new HashSet<Agent>();
		
		Set<Agent> surviver = (Set<Agent>)info.getSurviver();
		
		//占い師をタグ付けのセットに入れる
		if(seerList.size() > 0){
			Agent seer = seerList.get(0);
			if(surviver.contains(seer)){
				actions.add(S1);
				taged.add(seer);
			}
			Set<Judge> judges = info.getJudgeSets().get(seer);
			if(judges != null){
				for(Judge judge: judges){
					if(surviver.contains(judge.getTarget())){
						if(judge.getResult() == Species.HUMAN){
							actions.add(JH1);
						}else{
							actions.add(JW1);
						}
						taged.add(judge.getTarget());
					}
				}
			}
		}
		if(seerList.size() > 1){
			Agent seer = seerList.get(1);
			if(surviver.contains(seer)){
				actions.add(S2);
				taged.add(seer);
			}
			Set<Judge> judges = info.getJudgeSets().get(seer);
			if(judges != null){
				for(Judge judge: judges){
					if(surviver.contains(judge.getTarget())){
						if(judge.getResult() == Species.HUMAN){
							actions.add(JH2);
						}else{
							actions.add(JW2);
						}
						taged.add(judge.getTarget());
					}
				}
			}
		}
		if(seerList.size() > 2){
			Agent seer = seerList.get(2);
			if(surviver.contains(seer)){
				actions.add(S3);
				taged.add(seer);
			}
			Set<Judge> judges = info.getJudgeSets().get(seer);
			if(judges != null){
				for(Judge judge: judges){
					if(surviver.contains(judge.getTarget())){
						if(judge.getResult() == Species.HUMAN){
							actions.add(JH3);
						}else{
							actions.add(JW3);
						}
						taged.add(judge.getTarget());
					}
				}
			}
		}
		
		if(mediumList.size() > 0 && surviver.contains(mediumList.get(0))){
			actions.add(M1);
			taged.add(mediumList.get(0));
		}
		if(mediumList.size() > 1 && surviver.contains(mediumList.get(1))){
			actions.add(M2);
			taged.add(mediumList.get(1));
		}
		if(mediumList.size() > 2 && surviver.contains(mediumList.get(2))){
			actions.add(M3);
			taged.add(mediumList.get(2));
		}
		
		if(!taged.containsAll(surviver)){
			actions.add(OT);
		}
		actions.add(RA);
		return actions;
	}
	
	public List<Agent> getSelectedAgents(Information info){
		
		
		List<Agent> agents = new ArrayList<Agent>();
		try {
			switch (this) {
			case S1:
			case JW1:
			case JH1:
				Agent seer = info.getSeerCOList().get(0);
				if(this == S1){
					agents.add(seer);
					break;
				}
				for(Judge judge: info.getJudgeSets().get(seer)){
					if(this == JH1 && judge.getResult() == Species.HUMAN){
						agents.add(judge.getTarget());
					}else if(this == JW1 && judge.getResult() == Species.WEREWOLF){
						agents.add(judge.getTarget());
					}
				}
				break;
				
			case S2:
			case JW2:
			case JH2:
				Agent seer2 = info.getSeerCOList().get(1);
				if(this == S2){
					agents.add(seer2);
					break;
				}
				for(Judge judge: info.getJudgeSets().get(seer2)){
					if(this == JH2 && judge.getResult() == Species.HUMAN){
						agents.add(judge.getTarget());
					}else if(this == JW2 && judge.getResult() == Species.WEREWOLF){
						agents.add(judge.getTarget());
					}
				}
				break;
				
			case S3:
			case JW3:
			case JH3:
				Agent seer3 = info.getSeerCOList().get(2);
				if(this == S3){
					agents.add(seer3);
					break;
				}
				for(Judge judge: info.getJudgeSets().get(seer3)){
					if(this == JH3 && judge.getResult() == Species.HUMAN){
						agents.add(judge.getTarget());
					}else if(this == JW3 && judge.getResult() == Species.WEREWOLF){
						agents.add(judge.getTarget());
					}
				}
				break;
				
			case M1:
				agents.add(info.getMediumCOList().get(0));
				break;
			case M2:
				agents.add(info.getMediumCOList().get(1));
				break;
			case M3:
				agents.add(info.getMediumCOList().get(2));
				break;
				
			case OT:
				agents.addAll(info.getSurviver());

				List<Agent> remover = new ArrayList<Agent>();
				for(int i = 0; i < info.getSeerCOList().size(); i++){
					if(i >= 3){
						break;
					}
					Agent a = info.getSeerCOList().get(i);
					remover.add(a);
					if(info.getJudgeSets().get(a) != null){
						for(Judge judge: info.getJudgeSets().get(a)){
							remover.add(judge.getTarget());
						}
					}
				}
				
				for(int i = 0; i < info.getMediumCOList().size(); i++){
					if(i >= 3){
						break;
					}
					Agent a = info.getMediumCOList().get(i);
					remover.add(a);

				}
				
				agents.removeAll(remover);
				if(agents.size() == 0){
					System.out.println("a");
				}
				return agents;
				
			case RA:
				agents.addAll(info.getSurviver());
				if(agents.size() == 0){
					System.out.println("aaaa");
				}
				return agents;

			default:
				break;
			}

		} catch (Exception e) {
			System.out.println("不適切なActionの選択：" + this);
			e.printStackTrace();
		}
		
		
		Set<Agent> surviver = (Set<Agent>) info.getSurviver();
		List<Agent> remover = new ArrayList<Agent>();
		for(Agent agent: agents){
			if(!surviver.contains(agent)){
				remover.add(agent);
			}
		}
		agents.removeAll(remover);
		if(agents.size() == 0){
			System.out.println("aa");
		}
		return agents;
		
	}

	
/*	public boolean ableSelect(Information info){
		//TODO 
		int seerCOnum = info.getSeerCOList().size();
		switch (this) {
		case SEER1:
			if(info.getSeerCOList().size() > 0){
				return true;
			}

		default:
			break;
		}
		return false;
	}
	
*/	
}
