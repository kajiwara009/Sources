package twinDirector;

import java.util.ArrayList;
import java.util.List;

import stateDirector.StateDirector;
import unitDirector.UnitDirector;
import unitDirector.worker.FirstSearchDirector;
import unitDirector.worker.InvationDirector;
import unitDirector.worker.ResourceCollectDirector;
import unitDirector.worker.SearchDirector;
import codevs.God;
import data.Vector;

public class WorkerDirector extends TwinDirector {
	
	protected FirstSearchDirector firstSearchD;
	protected List<ResourceCollectDirector> resourceCollectDs;
	protected SearchDirector searchD;
	protected InvationDirector invationD;

	public WorkerDirector(God god) {
		super(god);
		firstSearchD = new FirstSearchDirector(god);
		resourceCollectDs = new ArrayList<ResourceCollectDirector>();
		searchD = new SearchDirector(god);
		invationD = new InvationDirector(god);
	}


	@Override
	public void think(StateDirector sd) {
		order();

	}

/*
	@Override
	protected void order() {
		firstSearchD.think(this);
		for(ResourceCollectDirector resCol: resourceCollectDs){
			resCol.think(this);
		}
		searchD.think(this);
		invationD.think(this);
		
	}
*/	
	/**
	 * 指定したVectorにResColDirectorがいれば，そのDirectorを返す
	 * いなければnullを返す
	 * @param vec
	 * @return
	 */
	public ResourceCollectDirector getResColDirectorAt(Vector vec){
		for(ResourceCollectDirector resD: resourceCollectDs){
			if(resD.getRes().equals(vec)){
				return resD;
			}
		}
		return null;
	}
	

	public FirstSearchDirector getFirstSearchD() {
		return firstSearchD;
	}

	public List<ResourceCollectDirector> getResourceCollectDs() {
		return resourceCollectDs;
	}

	public SearchDirector getSearchD() {
		return searchD;
	}

	public InvationDirector getInvationD() {
		return invationD;
	}

	@Override
	public List<UnitDirector> getUnitDirectors() {
		List<UnitDirector> uds = new ArrayList<>();
		uds.add(firstSearchD);
		for(ResourceCollectDirector resCol: resourceCollectDs){
			uds.add(resCol);
		}
		uds.add(searchD);
		uds.add(invationD);
		return uds;
	}


	
}
