package unitDirector.worker;

import java.util.List;

import stateDirector.StateDirector;
import twinDirector.TwinDirector;
import twinDirector.WorkerDirector;
import unitDirector.UnitDirector;
import codevs.God;
import data.Direction;
import data.Unit;
import data.UnitSelectFactory;
import data.UnitType;
import data.Vector;

public class ResourceCollectDirector extends UnitDirector {
	private final Vector res;
	public static final int DEFAULT_VILLAGE_RANGE = 10;

	public ResourceCollectDirector(God god, Vector res) {
		super(god);
		this.res = res;
	}
	
		
	@Override
	public void moveUnits() {
		List<Unit> workers = getUnits(UnitType.WORKER);
//		System.err.println(workers.size());
		for(Unit worker: workers){
			Direction dir = null;
			Vector v = res.minus(worker.point());
			if (v.getX() != 0) {
				dir = v.getX() > 0 ? Direction.RIGHT: Direction.LEFT;
			} else if (v.getY() != 0) {
				dir = v.getY() > 0 ? Direction.DOWN: Direction.UP;
			}
			worker.setDir(dir);
//			System.err.println(worker.getDir());
		}
	}
	
	public boolean wantBuildVillage(){
		if(!hasVillage(ResourceCollectDirector.DEFAULT_VILLAGE_RANGE) && getPatialIncome() > 0){
			return true;
		}else return false;
	}
	
	public boolean wantMakeWorker(){
		if(hasVillage(ResourceCollectDirector.DEFAULT_VILLAGE_RANGE) && getUnits(UnitType.WORKER).size() < 5){
			return true;
		}else return false;

	}
	
	public int buildVillage(){
		Unit nearest = getNearestWorker();
		if(nearest.getProduce() == null){
			nearest.setProduce(UnitType.VILLAGE);
			return UnitType.VILLAGE.getCost();
		}else return 0;
	}
	
	public int makeWorker(){
		Unit nearest = getNearestVillage();
		if(nearest.getProduce() == null){
			nearest.setProduce(UnitType.WORKER);
			return UnitType.WORKER.getCost();
		}else return 0;

	}
	
	public Unit getNearestWorker(){
		return UnitSelectFactory.getNearestUnit(res, god.getUnits(), UnitType.WORKER);
	}
	
	public Unit getNearestVillage(){
		return UnitSelectFactory.getNearestUnit(res, god.getUnits(), UnitType.VILLAGE);
	}
	
	/**
	 * Resourceの位置ベクトル
	 * @return
	 */
	public Vector getRes(){
		return res;
	}
	
	/**
	 * このクラスタから1ターンに得られる資源の数．
	 * すなわち，資源の上に乗っているワーカーの数
	 * @return
	 */
	public int getPatialIncome(){
		int num = 0;
		for(Unit u: god.getUnits(UnitType.WORKER)){
			if(u.point().equals(res)){
				num++;
			}
		}
		return num;
	}
	
	/**
	 * 資源の上に乗っているユニットがいるか否か．
	 * @return
	 */
	public boolean hasVillage(){
		return hasVillage(0);
	}
	
	/**
	 * range以内に村が存在するか
	 * @param range
	 * @return
	 */
	public boolean hasVillage(int range){
		List<Unit> villages = god.getUnits(UnitType.VILLAGE);
		for(Unit village: villages){
			if(village.point().getMhtDist(res) <= range){
				return true;
			}
		}
		return false;
	}

}
