package stateDirector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import twinDirector.FighterDirector;
import twinDirector.WorkerDirector;
import unitDirector.worker.ResourceCollectDirector;
import codevs.God;
import data.Unit;
import data.UnitType;
import data.Vector;

public class InvadeDirector extends StateDirector {

	public InvadeDirector(God god, StateDirector sd) {
		super(god, sd);
		workerD.getFirstSearchD().removeAllUnit();
		setInvadeUnits(workerD);
	}
	
	protected void setInvadeUnits(WorkerDirector wd){
		invaderHelm();
		invaderHelm();
	}
	
	private void invaderHelm(){
		Unit invader = null;
		int minDist = Integer.MAX_VALUE;
		
		for(Unit u: god.getUnits(UnitType.WORKER)){
			int distCastle = u.point().getMhtDist(workerD.getInvationD().TARGET);
			for(Unit inv: workerD.getInvationD().getUnits()){
				if(inv.point().getMhtDist(u.point()) < 30){
					distCastle = Integer.MAX_VALUE;
				}
			}
			if(distCastle < minDist){
				invader = u;
				minDist = distCastle;
			}
		}
		if(invader != null){
			if(invader.getUnitDirector() != null){
				invader.getUnitDirector().removeUnit(invader);
			}
			workerD.getInvationD().addUnit(invader);
		}
	}

	/**
	 * 資源上にワーカーが足りてない時は，周りから集める
	 * インベーダーが1人以下になったら，近いやつを追加
	 */
	@Override
	protected void unitHelm() {
		newResourceHelm(15);
		while(workerD.getInvationD().getUnits(UnitType.WORKER).size() < 2){
			int pre = workerD.getInvationD().getUnits(UnitType.WORKER).size();
			invaderHelm();
			if(pre - workerD.getInvationD().getUnits(UnitType.WORKER).size() == 0) break;
		}
		notEnoughResourceHelm();
		neetToSearcher();
	}

	private void neetToSearcher() {
		for(Unit u: god.getUnits()){
			if(u.isNeet()){
				workerD.getSearchD().addUnit(u);
			}
		}
	}

	protected void notEnoughResourceHelm() {
		Map<Unit, ResourceCollectDirector> dispachMap = new HashMap<Unit, ResourceCollectDirector>();
		for(Unit u: workerD.getSearchD().getUnits()){
			int minDist = Integer.MAX_VALUE;
			for(ResourceCollectDirector rescol: workerD.getResourceCollectDs()){
				if(rescol.getUnits(UnitType.WORKER).size() < 5){
					int dist = u.point().getMhtDist(rescol.getRes());
					if(dist < 20 && dist < minDist){
						dispachMap.put(u, rescol);
					}
				}
			}
		}
		for(Entry<Unit, ResourceCollectDirector> set: dispachMap.entrySet()){
			unitDispatch(set.getKey(), set.getValue());
		}
	}

	@Override
	protected void resourceDistribute(int currentResource) {
		if(currentResource < 40) return;
		/**
		 * 資源集めのユニットが5人以下のクラスタが会ったら，ワーカーを生成
		 */
		List<ResourceCollectDirector> restResCol = new ArrayList<>();
		for(ResourceCollectDirector rescol: workerD.getResourceCollectDs()){
			if(rescol.wantMakeWorker()){
				if(rescol.hasVillage()){
					currentResource -= rescol.makeWorker();
				}else{
					restResCol.add(rescol);
				}
			}
			if(currentResource < 40) return;
		}

		/**
		 * 資源の上にワーカーがいて，村がなければ村をたてる
		 */
		for(ResourceCollectDirector rescol: workerD.getResourceCollectDs()){
			if(rescol.wantBuildVillage()){
				currentResource -= rescol.buildVillage();
				if(currentResource < 40) return;
			}
		}

		
		/**
		 * 資源上に村を持っていない場合は，優先度低い
		 */
		for(ResourceCollectDirector rescol: restResCol){
			currentResource -= rescol.makeWorker();
			if(currentResource < 40) return;
		}


	}

}
