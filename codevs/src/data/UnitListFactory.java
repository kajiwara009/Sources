package data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class UnitListFactory {
	
	public static List<Unit> getUnitsTypeOf(Collection<Unit> myUnits, UnitType type){
		List<Unit> units = new ArrayList<>();
		for(Unit unit: myUnits){
			if(unit.type() == type){
				units.add(unit);
			}
		}
		return units;
	}
	
	public static List<Unit> getUnits(Collection<Unit> myUnits, Collection<UnitType> types){
		List<Unit> units = new ArrayList<>();
		for(Unit unit: myUnits){
			if(types.contains(unit.type())){
				units.add(unit);
			}
		}
		return units;
	}
	
	public static List<Unit> getMovableUnits(Collection<Unit> myUnits){
		List<UnitType> types = new ArrayList<>();
		for(UnitType type: UnitType.values()){
			if(type.isMovable()) types.add(type);
		}
		return getUnits(myUnits, types);
	}
	
	public static List<Unit> getUnMovableUnits(Collection<Unit> myUnits){
		List<UnitType> types = new ArrayList<>();
		for(UnitType type: UnitType.values()){
			if(!type.isMovable()) types.add(type);
		}
		return getUnits(myUnits, types);
	}
	
	public static List<Unit> getVillagesAndCastle(Collection<Unit> myUnits){
		List<UnitType> types = new ArrayList<>();
		types.add(UnitType.VILLAGE);
		types.add(UnitType.CASTLE);
		return getUnits(myUnits, types);
	}
	
	public static List<Unit> getUnitsAt(Collection<Unit> units, Vector pos){
		return getUnitsAround(units, pos, 0);
/*		List<Unit> us = new ArrayList<>();
		for(Unit u: units){
			if(u.point().equals(pos)) us.add(u);
		}
		return us;
*/	
	}
	
	/**
	 * range以内のUnitsを返す
	 * @param units
	 * @param pos
	 * @param range
	 * @return
	 */
	public static List<Unit> getUnitsAround(Collection<Unit> units, Vector pos, int range){
		List<Unit> us = new ArrayList<>();
		for(Unit u: units){
			if(u.point().getMhtDist(pos) <= range) us.add(u);
		}
		return us;
	}
	
	/**
	 * 引数がTrueならば，UnitDirectorに所属していないUnit集合．
	 * Falseならば，所属しているUnit集合
	 * どちらもmovableなもののみ
	 * @param isNeet
	 * @return
	 */
	public static List<Unit> getNeetUnits(Collection<Unit> myUnits, boolean isNeet){
		List<Unit> units = new ArrayList<>();
		for(Unit unit: myUnits){
			if( (unit.isNeet() == isNeet) && unit.type().isMovable()){
				units.add(unit);
			}
		}
		return units;
	}

}
