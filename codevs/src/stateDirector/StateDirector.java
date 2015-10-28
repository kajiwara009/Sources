package stateDirector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import twinDirector.FighterDirector;
import twinDirector.WorkerDirector;
import unitDirector.UnitDirector;
import unitDirector.worker.ResourceCollectDirector;
import codevs.God;
import data.Unit;
import data.UnitType;
import data.Vector;

public abstract class StateDirector {
	God god;
//	List<UnitDirector> unitDirectors;
	
	WorkerDirector workerD;
	FighterDirector fightD;
	
	public StateDirector(God god, StateDirector sd){
		this.god = god;
		workerD = sd.getWorkerDirector();
		fightD = sd.getFightDirector();
	}
	
	public StateDirector(God god, WorkerDirector wd, FighterDirector fd){
		this.god = god;
		workerD = wd;
		fightD = fd;
	}
	
	public void think(){
		unitHelm();
		
		resourceDistribute(god.getCurrentResource());
		order();
	}
	
	protected abstract void unitHelm();
	protected abstract void resourceDistribute(int currentResource);
	
	protected void resetUsableResource(){
		
	}

	public WorkerDirector getWorkerDirector() {
		return workerD;
	}

	public FighterDirector getFightDirector() {
		return fightD;
	}
	
	protected void order(){
		workerD.think(this);
		fightD.think(this);
	}
	
	
	protected void unitDispatch(Unit unit, UnitDirector postDirector){
		if(!unit.isNeet()){
			unit.getUnitDirector().removeUnit(unit);
		}
		postDirector.addUnit(unit);
	}
	
	protected void unitDispatch(Collection<Unit> units, UnitDirector postDirector){
		for(Unit u: units){
			unitDispatch(u, postDirector);
		}
	}

	
	
	public void printHelm(){
		System.err.println("StateDirectorクラス");
		System.err.println("firstSearcherの人数：" + workerD.getFirstSearchD().getUnits().size());
		System.err.println("resColDirectorの数：" + workerD.getResourceCollectDs().size());
		for(ResourceCollectDirector rescol: workerD.getResourceCollectDs()){
			int i = 0;
			System.err.println("resCol" + i + "人数：" + rescol.getUnits().size());
			i++;
		}
	}
	
	/**
	 * 応援に駆けつけるユニットの許容最大距離
	 * @param maxDist
	 */
	protected void newResourceHelm(int maxDist){
		List<ResourceCollectDirector> resCols = getWorkerDirector().getResourceCollectDs();
		if(resCols.size() != god.getResources().size()){
			for(Vector res: god.getResources()){
				if(workerD.getResColDirectorAt(res) == null){
					ResourceCollectDirector newResColD = new ResourceCollectDirector(god, res);
					workerD.getResourceCollectDs().add(newResColD);
					
					if(god.getOppUnitsAt(res).size() > 2){
						continue;
					}
					/**
					 * ResColにニートか探索チームから一人派遣してあげる
					 */
					List<Unit> resColCandidates = new ArrayList<>();
					//FirstSearch, searchのワーカー，
					//ResColに属していない人以外
					resColCandidates.addAll(god.getUnits(UnitType.WORKER));
					for(ResourceCollectDirector resColD: workerD.getResourceCollectDs()){
						resColCandidates.removeAll(resColD.getUnits());
					}
					resColCandidates.removeAll(workerD.getInvationD().getUnits());
					//候補者の中で一番resに近いUnitを派遣する
					Unit selected = null;
					int distMin = maxDist;
					for(Unit unit: resColCandidates){
						int dist = unit.point().getMhtDist(res);
						if(dist < distMin){
							selected = unit;
							distMin = dist;
						}
					}
					//派遣出来る人が一人でも入れば
					if(selected != null){
						unitDispatch(selected, newResColD);
					}
					

				}
			}
		}
	}

/*	
	protected void removeUnitDirector(UnitDirector ud){
		if(unitDirectors.contains(ud)){
			ud.removeAllUnit();
		}
	}
	
	protected void orderUnitDirectors(){
		for(UnitDirector ud: unitDirectors){
			ud.think(this);
		}
	}

	protected List<ResourceCollectDirector> getResColDirectors(){
		List<ResourceCollectDirector> rescols = new ArrayList<>();
		for(UnitDirector ud: unitDirectors){
			if(ud instanceof ResourceCollectDirector){
				rescols.add((ResourceCollectDirector)ud);
			}
		}
		return rescols;
	}
	
	protected List<FirstSearchDirector> getFirstSearchDirectors(){
		List<FirstSearchDirector> rescols = new ArrayList<>();
		for(UnitDirector ud: unitDirectors){
			if(ud instanceof FirstSearchDirector){
				rescols.add((FirstSearchDirector)ud);
			}
		}
		return rescols;
	}

	
	protected List<UnitDirector> getUnitDirector(Class<?> className){
		List<UnitDirector> directors = new ArrayList<>();
		for(UnitDirector ud: unitDirectors){
			if(ud.getClass() == className){
				directors.add(ud);
			}
		}
		return directors;
	}
	
*/	
/*	protected List<UnitDirector> getUnitDirector(String className){
		Class.forName(className).cast(unitDirectors.get(0));
		List<UnitDirector> directors = new ArrayList<>();
		for(UnitDirector ud: unitDirectors){
			try {
				if(ud.getClass() == Class.forName(className)){
					directors.add(ud);
				}
			} catch (ClassNotFoundException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		}
		return directors;
	}
*/
}
