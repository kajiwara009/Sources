package data;

import java.util.Collection;

public class UnitSelectFactory {

	public static Unit getNearestUnit(Vector pos, Collection<Unit> units, UnitType type){
		Unit nearest = null;
		int dist = Integer.MAX_VALUE;
		for(Unit u: UnitListFactory.getUnitsTypeOf(units, type)){
			int newDist = u.point().getMhtDist(pos);
			if(newDist < dist){
				nearest = u;
				dist = newDist;
			}
		}
		return nearest;
	}



}
