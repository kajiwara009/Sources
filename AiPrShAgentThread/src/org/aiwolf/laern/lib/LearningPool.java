package org.aiwolf.laern.lib;

import java.util.ArrayList;
import java.util.List;

public class LearningPool {
	private List<ObserveLearnResource> obsLearnRes = new ArrayList<ObserveLearnResource>();
	private List<SituationLearnResource> sitLearnRes = new ArrayList<SituationLearnResource>();

	
	public List<ObserveLearnResource> getObsLearnRes() {
		return obsLearnRes;
	}
	public void setObsLearnRes(List<ObserveLearnResource> obsLearnRes) {
		this.obsLearnRes = obsLearnRes;
	}
	synchronized public void addObsLearnRes(ObserveLearnResource olr){
		obsLearnRes.add(olr);
	}
	
	public List<SituationLearnResource> getSitLearnRes() {
		return sitLearnRes;
	}
	public void setSitLearnRes(List<SituationLearnResource> sitLearnRes) {
		this.sitLearnRes = sitLearnRes;
	}
	synchronized public void addSitLearnRes(SituationLearnResource slr){
		sitLearnRes.add(slr);
	}
	

}
