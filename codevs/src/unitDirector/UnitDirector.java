package unitDirector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import twinDirector.TwinDirector;
import codevs.God;
import data.Unit;
import data.UnitListFactory;
import data.UnitType;

/**
 * 小団体の命令を出すDirector
 * StateDirectorに管理されている
 * @author kajiwarakengo
 *
 */
public abstract class UnitDirector {
	protected God god;
	protected List<Unit> units = new ArrayList<Unit>();
	protected int usableResource = 0;
	
	public UnitDirector(God god){
		this.god = god;
	}
	
	public void order(){
		moveUnits();
	}
	
	
	public abstract void moveUnits();
	
	
	public List<Unit> getUnits(){
		return new ArrayList<>(units);
//		return units;
	}
	
	/**
	 * 同じIDのユニットがリストに存在しなければ加える
	 * @param unit
	 */
	public void addUnit(Unit unit){
		if(unit.getUnitDirector() == null){
			if (unit.type().isMovable()) {
				units.add(unit);
				unit.setUnitDirector(this);
			} else {
//				System.err.println("UnitDirectorクラス:" + unit.type() + "はUnitDirectorに所属出来ません");
			}
		}
	}
	
	public void addUnit(Collection<Unit> units){
		for(Unit u: units){
			addUnit(u);
		}
	}
	
	/**
	 * StateDirectorのUnitDispatchの中で呼ばれる
	 * Unit.resetUnitDirectorでTrueならば，UnitDirectorから削除する
	 * @param unit
	 */
	public boolean removeUnit(Unit unit){
		if(unit.resetUnitDirector(this)){
			return units.remove(unit);
		}else{
			return false;
		}
	}

	public void removeUnit(Collection<Unit> units){
		for(Unit u: units){
			removeUnit(u);
		}
	}
	
	public void removeAllUnit(){
		for(Unit u: units){
			u.resetUnitDirector(this);
		}
		units.clear();
	}
	
	
	public List<Unit> getUnits(UnitType type){
		return UnitListFactory.getUnitsTypeOf(units, type);
	}
	
	public List<Unit> getUnits(Collection<UnitType> types){
		return UnitListFactory.getUnits(units, types);
	}
	
	public int getUsableResource() {
		return usableResource;
	}

	public void setUsableResource(int usableResource) {
		this.usableResource = usableResource;
	}

	public int addUsableResource(int usableResource) {
		this.usableResource += usableResource;
		return usableResource;
	}


}
