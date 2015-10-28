package ipa.myAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Species;

public class COResult{
	static private List<Agent> seerCOAgent = new ArrayList<Agent>();
	static private List<Agent> mediumCOAgent = new ArrayList<Agent>();
	static private List<Agent> attackedAgent = new ArrayList<Agent>();
	static private List<Agent> executedAgent = new ArrayList<Agent>();

	static private HashMap<Agent, COResultMap> seersResult = new HashMap<Agent, COResultMap>();
	static private HashMap<Agent, COResultMap> mediumsResult = new HashMap<Agent, COResultMap>();


	static public void deleteAll(){
		for(Agent seer: seerCOAgent){
			seersResult.remove(seer);
		}
		for(Agent medium: mediumCOAgent){
			mediumsResult.remove(medium);
		}
		seerCOAgent.removeAll(seerCOAgent);
		mediumCOAgent.removeAll(mediumCOAgent);
	}

	static public void setSeerCOAgent(Agent seer){
		if(!seerCOAgent.contains(seer)){
			seerCOAgent.add(seer);
			COResultMap result = new COResultMap();
			seersResult.put(seer, result);
		}
	}
	static public void setMediumCOAgent(Agent medium){
		if(!mediumCOAgent.contains(medium)){
			mediumCOAgent.add(medium);
			COResultMap result = new COResultMap();
			mediumsResult.put(medium, result);
		}
	}
	static public List<Agent> getSeerCOAgent(){
		return seerCOAgent;
	}
	static public List<Agent> getMediumCOAgent(){
		return mediumCOAgent;
	}

	static public void setDevineResult(Agent seer, Agent target, Species species){
		if(seersResult.containsKey(seer)){
			seersResult.get(seer).put(target, species);
		}
	}
	static public void setInquestResult(Agent medium, Agent target, Species species){
		if(mediumsResult.containsKey(medium)){
			mediumsResult.get(medium).put(target, species);
		}
	}

	static public void setAttackedAgent(Agent attacked){
		if(!attackedAgent.contains(attacked)){
			attackedAgent.add(attacked);
		}
	}
	static public void setExecutedAgent(Agent executed){
		if(!executedAgent.contains(executed)){
			executedAgent.add(executed);
		}
	}

	static public List<Agent> getAttackedAgent(){
		return attackedAgent;
	}
	static public List<Agent> getExecutedAgent(){
		return executedAgent;
	}


	static public List<Agent> fakeSeerForAll(){
		List<Agent> fakeSeer = new ArrayList<Agent>();
		if(seerCOAgent.size() == 0){
			return fakeSeer;
		}
		for(Agent attacked: attackedAgent){
			for(Agent seer: seerCOAgent){
				if(seersResult.containsKey(seer) && seersResult.get(seer).get(attacked) == Species.WEREWOLF){
					if(!fakeSeer.contains(seer)){
						fakeSeer.add(seer);
					}
				}
			}
		}
		for(Agent executed: executedAgent){
			int numWhite = 0;
			int numBlack = 0;
			for(Agent medium: mediumCOAgent){
				if(mediumsResult.containsKey(medium)){
					if(mediumsResult.get(medium).get(executed) == Species.HUMAN){
						numWhite++;
					}
					if(mediumsResult.get(medium).get(executed) == Species.WEREWOLF){
						numBlack++;
					}
				}
			}
			if(numWhite == mediumCOAgent.size()){
				for(Agent seer: seerCOAgent){
					if(seersResult.containsKey(seer) && seersResult.get(seer).get(executed) == Species.WEREWOLF){
						if(!fakeSeer.contains(seer)){
							fakeSeer.add(seer);
						}
					}
				}
			}
			if(numBlack == mediumCOAgent.size()){
				for(Agent seer: seerCOAgent){
					if(seersResult.containsKey(seer) && seersResult.get(seer).get(executed) == Species.HUMAN){
						if(!fakeSeer.contains(seer)){
							fakeSeer.add(seer);
						}
					}
				}
			}
		}
		return fakeSeer;
	}
	static public List<Agent> fakeSeerForMe(Agent me){
		List<Agent> fakeSeer = new ArrayList<Agent>();
		if(seerCOAgent.size() == 0){
			return fakeSeer;
		}
		for(Agent seer: seerCOAgent){
			for(Agent attacked: attackedAgent){
				if(seersResult.containsKey(seer) && seersResult.get(seer).get(attacked) == Species.WEREWOLF){
					if(!fakeSeer.contains(seer)){
						fakeSeer.add(seer);
					}
				}
			}
			if(seersResult.containsKey(seer) && seersResult.get(seer).get(me) == Species.WEREWOLF){
				if(!fakeSeer.contains(seer)){
					fakeSeer.add(seer);
				}
			}
		}
		for(Agent executed: executedAgent){
			int numWhite = 0;
			int numBlack = 0;
			for(Agent medium: mediumCOAgent){
				if(mediumsResult.containsKey(medium)){
					if(mediumsResult.get(medium).get(executed) == Species.HUMAN){
						numWhite++;
					}
					if(mediumsResult.get(medium).get(executed) == Species.WEREWOLF){
						numBlack++;
					}
				}
			}
			if(numWhite == mediumCOAgent.size()){
				for(Agent seer: seerCOAgent){
					if(seersResult.containsKey(seer) && seersResult.get(seer).get(executed) == Species.WEREWOLF){
						if(!fakeSeer.contains(seer)){
							fakeSeer.add(seer);
						}
					}
				}
			}
			if(numBlack == mediumCOAgent.size()){
				for(Agent seer: seerCOAgent){
					if(seersResult.containsKey(seer) && seersResult.get(seer).get(executed) == Species.HUMAN){
						if(!fakeSeer.contains(seer)){
							fakeSeer.add(seer);
						}
					}
				}
			}
		}
		return fakeSeer;
	}

	static public  List<Agent> trueSeerForAll(){
		List<Agent> trueSeer = new ArrayList<Agent>();
		if(seerCOAgent.size() == 0){
			return trueSeer;
		}
		trueSeer.addAll(seerCOAgent);
		for(Agent fakeSeer: fakeSeerForAll()){
			trueSeer.remove(fakeSeer);
		}
		return trueSeer;
	}
	static public  List<Agent> trueSeerForMe(Agent me){
		List<Agent> trueSeer = new ArrayList<Agent>();
		if(seerCOAgent.size() == 0){
			return trueSeer;
		}
		trueSeer.addAll(seerCOAgent);
		for(Agent fakeSeer: fakeSeerForMe(me)){
			trueSeer.remove(fakeSeer);
		}
		return trueSeer;
	}

	static public List<Agent> aliveBlack(List<Agent> aliveAgent, Agent me){
		List<Agent> aliveBlack = new ArrayList<Agent>();
		for(Agent seer: trueSeerForMe(me)){
			for(Agent target: aliveAgent){
				if(seersResult.containsKey(seer) && seersResult.get(seer).get(target) == Species.WEREWOLF){
					aliveBlack.add(target);
				}
			}
		}
		aliveBlack.remove(me);
		return aliveBlack;
	}
	static public List<Agent> aliveWhite(List<Agent> aliveAgent, Agent me){
		List<Agent> aliveWhite = new ArrayList<Agent>();
		for(Agent seer: trueSeerForMe(me)){
			for(Agent target: aliveAgent){
				if(seersResult.containsKey(seer) && seersResult.get(seer).get(target) == Species.HUMAN){
					aliveWhite.add(target);
				}
			}
		}
		aliveWhite.remove(me);
		return aliveWhite;
	}
	static public List<Agent> aliveGrayForMe(List<Agent> aliveAgent, Agent me){
		List<Agent> aliveGray = new ArrayList<Agent>();
		aliveGray.addAll(aliveAgent);
		aliveGray.removeAll(seerCOAgent);
		for(Agent seer: trueSeerForMe(me)){
			for(Agent target: aliveAgent){
				if(seersResult.containsKey(seer) && seersResult.get(seer).containsKey(target)){
					aliveGray.remove(target);
				}
			}
		}
		for(Agent medium: mediumCOAgent){
			if(aliveAgent.contains(medium)){
				aliveGray.remove(medium);
			}
		}
		aliveGray.remove(me);
		return aliveGray;
	}
	static public List<Agent> aliveGrayForAll(List<Agent> aliveAgent){
		List<Agent> aliveGray = new ArrayList<Agent>();
		aliveGray.addAll(aliveAgent);
		aliveGray.removeAll(seerCOAgent);
		for(Agent seer: trueSeerForAll()){
			for(Agent target: aliveAgent){
				if(seersResult.containsKey(seer) && seersResult.get(seer).containsKey(target)){
					aliveGray.remove(target);
				}
			}
		}
		for(Agent medium: mediumCOAgent){
			if(aliveAgent.contains(medium)){
				aliveGray.remove(medium);
			}
		}
		return aliveGray;
	}





	static public List<Agent> fakeSeerForMedium(List<Agent> aliveAgent, Agent medium){
		List<Agent> fakeSeers = new ArrayList<Agent>();
		if(!mediumsResult.containsKey(medium)){
			return null;
		}
		else{
			for(Agent seer: seerCOAgent){
				for(Agent target: aliveAgent){
					if(seersResult.containsKey(seer) && seersResult.get(seer).containsKey(target) && mediumsResult.get(medium).containsKey(target)){
						if(seersResult.get(seer).get(target) != mediumsResult.get(medium).get(target)){
							fakeSeers.add(seer);
						}
					}
				}
			}
		}
		return fakeSeers;
	}
	static public List<Agent> trueSeerForMedium(List<Agent> aliveAgent, Agent medium){
		List<Agent> trueSeer = new ArrayList<Agent>();
		if(seerCOAgent.size() == 0){
			return trueSeer;
		}
		trueSeer.addAll(trueSeerForMe(medium));
		trueSeer.removeAll(fakeSeerForMedium(aliveAgent, medium));
		return trueSeer;
	}

	static public List<Agent> myGrayForSeer(List<Agent> aliveAgent, Agent me){
		List<Agent> gray = new ArrayList<Agent>();
		gray.addAll(aliveAgent);
		gray.removeAll(seerCOAgent);
		gray.removeAll(mediumCOAgent);
		for(Agent divined: aliveAgent){
			if(seersResult.containsKey(me) && seersResult.get(me).containsKey(divined)){
				gray.remove(divined);
			}
		}
		return gray;
	}

	static public Agent findPossessed(List<Agent> allAgent, List<Agent> wolfList){
		for(Agent seer: seerCOAgent){
			for(Agent target: allAgent){
				if(seersResult.containsKey(seer) && seersResult.get(seer).containsKey(target)){
					if(seersResult.get(seer).get(target) == Species.HUMAN && wolfList.contains(target)){
						if(!wolfList.contains(seer)){
							return seer;
						}
					}
					else if(seersResult.get(seer).get(target) == Species.WEREWOLF && !wolfList.contains(target)){
						if(!wolfList.contains(seer)){
							return seer;
						}
					}
				}
			}
		}
		return null;
	}
	static public boolean foundPossessed(List<Agent> allAgent, List<Agent> wolfList){
		if(findPossessed(allAgent, wolfList) != null){
			return true;
		}
		else{
			return false;
		}
	}


}

class COResultMap extends HashMap<Agent, Species> {
}
