package stateDirector;

import java.util.ArrayList;
import java.util.List;

import twinDirector.FighterDirector;
import twinDirector.WorkerDirector;
import codevs.God;
import data.Unit;
import data.UnitListFactory;
import data.UnitSelectFactory;
import data.UnitType;
import data.Vector;

public class AssaultDirector extends StateDirector {
	public static final Vector target = new Vector(85, 85);

	public AssaultDirector(God god, StateDirector sd) {
		super(god, sd);
		// TODO 自動生成されたコンストラクター・スタブ
	}

	@Override
	protected void unitHelm() {
		newResourceHelm(15);
		
		attackerHelm();
	}

	private void attackerHelm() {
		for(Unit u: god.getUnits(UnitType.getAttackerValues())){
			if(u.isNeet()){
				if(u.point().getMhtDist(new Vector(0, 0)) <= 40){
					fightD.getCdD().addUnit(u);
				}else{
					fightD.getCad().addUnit(u);
				}
			}
		}
	}
	
	
	@Override
	protected void resourceDistribute(int currentResource) {
		if(god.getStateChanger().isSilber){
			boolean hasBuiltDefence = false;
			int villageNum = 0;
			for(Unit village: god.getUnits(UnitType.VILLAGE)){
				if(village.point().equals(god.getMyCastle().point())){
					villageNum++;
				}
			}
			if(villageNum > 5){
				hasBuiltDefence = true;
			}
			if(!hasBuiltDefence){
				if(UnitListFactory.getUnitsAt(god.getUnits(UnitType.WORKER), god.getMyCastle().point()).size() == 0){
					god.getMyCastle().setProduce(UnitType.WORKER);
				}else{
					List<Unit> ws  = UnitListFactory.getUnitsAt(god.getUnits(UnitType.WORKER), god.getMyCastle().point());
					ws.get(0).setProduce(UnitType.VILLAGE);
				}
			}
		}
		
		if(UnitListFactory.getUnitsAround(god.getUnits(UnitType.FIGHTER), god.getMyCastle().point(), 4).size() < 10){
			for(Unit base: god.getUnits(UnitType.BASE)){
				if(base.point().equals(god.getMyCastle().point())){
					base.setProduce(UnitType.FIGHTER);
				}
			}
			
			if(god.getUnits(UnitType.BASE).size() < 2){
					Unit nearest = UnitSelectFactory.getNearestUnit(target, god.getUnits(), UnitType.WORKER);
					if(nearest != null){
						nearest.setProduce(UnitType.BASE);
					}
			}else{
				makeAttacker();
			}
		}
		
		if(god.getUnits(UnitType.BASE).size() < 1){
			if(god.getCurrentResource() + god.getUnits(UnitType.BASE).size() * 500  >= 1000){
				Unit nearest = UnitSelectFactory.getNearestUnit(target, god.getUnits(), UnitType.WORKER);
				if(nearest != null){
					nearest.setProduce(UnitType.BASE);
				}
			}
		}else{
			makeAttacker();
		}
		
	}

/*	@Override
	protected void resourceDistribute(int currentResource) {
		if(god.getUnits(UnitType.BASE).size() < 2){
			if(god.getCurrentResource() + god.getUnits(UnitType.BASE).size() * 500  >= 1000){
				Unit nearest = UnitSelectFactory.getNearestUnit(target, god.getUnits(), UnitType.WORKER);
				if(nearest != null){
					nearest.setProduce(UnitType.BASE);
				}
			}
		}else{
			makeAttacker();
		}
		
	}
*/	
	protected int makeAttacker(){
		int cost = 0;
		int res = god.getCurrentResource();
		int income = god.getIncome();
		int usableRes = res + income - 40;
		
		List<Unit> bases = god.getUnits(UnitType.BASE);
		int baseNum = bases.size();
		int[] costs = new int[baseNum];
		while(usableRes >= 20 && baseNum > 0){
			
			for(int i = 0; i < baseNum; i++){
				if(usableRes >= 20){
					costs[i] += 20;
					usableRes -= 20;
				}
				else break;
			}
		}
		
		for(int i = 0; i < baseNum; i++){
			UnitType type = UnitType.getStrongestAttacker(costs[i]);
			if(type != null){
				if(type == UnitType.ASSASSIN ){
					double rand = Math.random();
					if(rand < 0.3){
						type = UnitType.FIGHTER;
					}else if(rand < 0.5){
						type = UnitType.KNIGHT;
					}
				}
				bases.get(i).setProduce(type);
				cost += type.getCost();
			}
		}
		return cost;
	}

}
