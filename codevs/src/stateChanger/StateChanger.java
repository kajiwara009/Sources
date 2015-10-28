package stateChanger;

import codevs.God;
import data.Unit;
import data.UnitType;
import data.Vector;

public abstract class StateChanger {
	protected God god;
	public boolean isSilber = false;
	
	
	public StateChanger(God god) {
		this.god = god;
	}
	
	public abstract void think();
	
	public void checkIsSilber(){
		if(isSilber) return;
		if(god.getCurrentTurn() < 150){
			int sum = 0;
			for(Unit u: god.getOpUnits().values()){
				if(u.type() == UnitType.KNIGHT && u.point().getMhtDist(new Vector(0, 0)) < 100){
					sum++;
				}
			}
			if(sum > 8){
				isSilber = true;
				System.err.println("こいつはSilberや！");
			}
		}
	}

}
