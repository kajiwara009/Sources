package stateDirector;

import java.util.ArrayList;
import java.util.List;

import twinDirector.FighterDirector;
import twinDirector.WorkerDirector;
import unitDirector.worker.ResourceCollectDirector;
import codevs.God;
import data.Unit;
import data.UnitType;
import data.Vector;

public class OpeningDirector extends StateDirector {
	
	
	public OpeningDirector(God god, WorkerDirector wd, FighterDirector fd) {
		super(god, wd, fd);
	}

	@Override
	public void unitHelm() {
		firstTurnHelm(); //最初のターンの派遣
		
		
		newResourceHelm(10); //新しい資源を見つけた時の派遣
		
		neetHelm();//ニートをどこかに所属させる
		
		firstSearcherToInvation();
		
	}
	
	private void firstSearcherToInvation() {
		List<Unit> firstSearchers = workerD.getFirstSearchD().getUnits();
		if(firstSearchers.size() < 2){
			unitDispatch(firstSearchers, workerD.getInvationD());
		}
	}

	protected void firstTurnHelm(){
		if(god.getCurrentTurn() == 0){
			workerD.getFirstSearchD().addUnit(god.getUnits(UnitType.WORKER));
		}
	}
/*	
	protected void newResourceHelm(){
		List<ResourceCollectDirector> resCols = getWorkerDirector().getResourceCollectDs();
		if(resCols.size() != god.getResources().size()){
			for(Vector res: god.getResources()){
				if(workerD.getResColDirectorAt(res) == null){
					ResourceCollectDirector newResColD = new ResourceCollectDirector(god, res);
					
					*//**
					 * ResColにニートか探索チームから一人派遣してあげる
					 *//*
					List<Unit> resColCandidates = new ArrayList<>();
					//FirstSearch, invation, searchのワーカー，
					//ResColに属していない人以外
					resColCandidates.addAll(god.getUnits(UnitType.WORKER));
					for(ResourceCollectDirector resColD: workerD.getResourceCollectDs()){
						resColCandidates.removeAll(resColD.getUnits());
					}
					//候補者の中で一番resに近いUnitを派遣する
					Unit selected = null;
					int distMin = Integer.MAX_VALUE;
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
					
					workerD.getResourceCollectDs().add(newResColD);
				}
			}
		}
	}
*/	
	protected void neetHelm(){
		for(Unit neet: god.getNeetUnits(true)){
			ResourceCollectDirector rescol = workerD.getResColDirectorAt(neet.point());
			if(rescol != null && rescol.getUnits(UnitType.WORKER).size() < 5){
				rescol.addUnit(neet);
			}else{
				for(ResourceCollectDirector res: workerD.getResourceCollectDs()){
					//距離が5以下の資源で，ワーカーおらず，村もなかったら，派遣
					if(neet.point().getMhtDist(res.getRes()) < ResourceCollectDirector.DEFAULT_VILLAGE_RANGE){
						if(res.getUnits(UnitType.WORKER).size() < 5 && !res.hasVillage()){
							res.addUnit(neet);
							break;
						}
					}
				}
				workerD.getSearchD().addUnit(neet);
			}
		}
		
		
		/**
		 * まずは，資源の上に乗っているやつ
		 * その資源Dが人数不足しているならば，そこに
		 * 5人以上いるならば，探索にまわす
		 * 
		 * それ以外の場所にいるやつ
		 * 
		 */
	}

	@Override
	protected void resourceDistribute(int currentResource) {
		if(currentResource < 40) return;

		for(Unit invation: workerD.getInvationD().getUnits()){
			ResourceCollectDirector rescol = workerD.getResColDirectorAt(invation.point()); 
			if(rescol != null && !rescol.hasVillage(10)){
				currentResource -= rescol.buildVillage();
			}
		}
		
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
		
		/**
		 * もしあまりが200以上あったら，探索用ワーカーを増員
		 */
		int restRes = 300 - 4 * god.getIncome();
		while(40 <= currentResource - restRes){
			int cost = workerD.getSearchD().makeWorker();
			currentResource -= cost;
			if(cost == 0) break;
		}
		return;
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
/*	
 * TwinDirectorを作る前の残骸
 * @Override
	public void think() {
		if(god.getCurrentTurn() == 0){
			UnitDirector firstDirector = new FirstSearchDirector(god);
			firstDirector.addUnit(god.getUnits(UnitType.WORKER));
			unitDirectors.add(firstDirector);
		}
		
		//新しい資源を見つけたら資源回収Director
		List<ResourceCollectDirector> resCols = getResColDirectors();
		if(resCols.size() != god.getResources().size()){
			loop1: for(Vector res: god.getResources()){
				for(ResourceCollectDirector resCol: resCols){
					Vector v = resCol.getRes();
					continue loop1;
				}
				UnitDirector newResCol = new ResourceCollectDirector(god, res);
				unitDirectors.add(newResCol);
				//新しくrescolを作ったので一番近いワーカーを管理下に入れてあげる
				for(uni)
			}
		}
		orderUnitDirectors();
	}*/

}
