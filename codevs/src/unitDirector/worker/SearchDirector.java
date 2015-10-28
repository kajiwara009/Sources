package unitDirector.worker;

import java.util.List;
import java.util.Random;

import unitDirector.UnitDirector;
import codevs.God;
import data.Direction;
import data.Unit;
import data.UnitType;
import data.Vector;

public class SearchDirector extends UnitDirector {
	public static final int SEARCH_VALUE_RANGE = 20;
	private static final int MIN_SEARCH_VALUE = 50;

	public SearchDirector(God god) {
		super(god);
		// TODO 自動生成されたコンストラクター・スタブ
	}

	@Override
	public void moveUnits() {
		for(Unit u: units){
			setOptimalSearchDirection(u);
		}
	}
	
	
	public int makeWorker(){
		Unit producer = getBestWorkerProducer();
		if(producer != null){
			producer.setProduce(UnitType.WORKER);
			return UnitType.WORKER.getCost();
		}else return 0;
	}
	
	protected int getViewWiden(Vector pos, Direction dir){
		int widen = 0;
		int round = UnitType.WORKER.getSight() + 1;
		boolean[][] see = god.getSee();
		
		Vector top = pos.plus(dir.getVector().multi( round));
		if(top.isOnField()){
			if(!see[top.getX()][top.getY()]) widen++;
		}else{
			//壁までの距離が小さすぎるときは無駄にそっちに行かない
			return 0;
		}
		for(int i = 1; i < round; i++){
			Vector standard = pos.plus(dir.getVector().multi(i));
			Vector addPoint = dir.getClockWise().getVector().multi(round - i);
			Vector point1 = standard.plus(addPoint);
			Vector point2 = standard.minus(addPoint);
			if(point1.isOnField() && !see[point1.getX()][point1.getY()]) widen++;
			if(point2.isOnField() && !see[point2.getX()][point2.getY()]) widen++;
		}
		return widen;
	}
	
	protected int getSearchValue(Vector pos, Direction dir){
		int value = 0;
		boolean[][] see = god.getSee();
		int microSVR = SEARCH_VALUE_RANGE / 2;
		int nearWorkerRange = SEARCH_VALUE_RANGE * 3 / 8;
		Vector middlePoint = pos.plus(dir.getVector().multi(microSVR));
		Vector nearJudge = pos.plus(dir.getVector().multi(nearWorkerRange));
		/*
		 * もし探索方向10マスにワーカーが居た場合は，そいつが探索するだろうから探索の価値がないと考える
		 */
		for(Unit worker: god.getUnits(UnitType.WORKER)){
			if(nearJudge.getMhtDist(worker.point()) < nearWorkerRange){
				if(!(worker.getUnitDirector() instanceof ResourceCollectDirector)) return 0;
			}
		}
		/*
		 * そっち方向にワーカーがいなかった場合
		 */
		int startX = Math.max(middlePoint.getX() - microSVR, 0);
		int endX = Math.min(middlePoint.getX() + microSVR, god.MAP_END);
		for(int x = startX; x <= endX; x++){
			int yRange = microSVR - Math.abs(x - middlePoint.getX());
			int startY = Math.max(middlePoint.getY() - yRange, 0);
			int endY = Math.min(middlePoint.getY() + yRange, god.MAP_END);
			for(int y = startY; y <= endY; y++){
				if(see[x][y]){
					continue;
				}
				value += getMicroValue(pos, new Vector(x, y));
			}
		}
		return value;
	}
	
	private int getMicroValue(Vector v1, Vector v2){
		int dist = v1.getMhtDist(v2);
		int microValue = 0;
		if(dist <= SEARCH_VALUE_RANGE / 4){
			microValue = 4;
		}else if(dist <= SEARCH_VALUE_RANGE / 4 * 2){
			microValue = 3;
		}else if(dist <= SEARCH_VALUE_RANGE / 4 * 3){
			microValue = 2;
		}else if(dist <= SEARCH_VALUE_RANGE / 4 * 4){
			microValue = 1;
		}
		return microValue;
	}
	
	protected Unit getBestWorkerProducer(){
		int maxValue = MIN_SEARCH_VALUE;
		Unit bestProducer = null;
		for(Unit u: god.getUnits(UnitType.getProducableUnitTypes(UnitType.WORKER))){
			if(u.isProducing()) continue;
			for(Unit searcher: units){
				if(u.point().equals(searcher.point())){
					continue;
				}
			}
			for(Direction dir: Direction.values()){
				int value = getSearchValue(u.point(), dir);
				if(maxValue < value){
					bestProducer = u;
					maxValue = value;
				}
			}
		}
//		System.err.println(maxValue);
		return bestProducer;
	}
	
	protected void setOptimalSearchDirection(Unit u){
		Direction dir = null;
		
		//一歩で視野が最も広がる方向
		int maxWiden = 0;
		for(Direction d: Direction.values()){
			int widen = getViewWiden(u.point(), d);
			if(widen > maxWiden){
				dir = d;
				maxWiden = widen;
			}
		}
		//一歩で視野の広がりがなかった場合
		if(dir == null){
			int maxValue = Integer.MIN_VALUE;
			for (Direction d : Direction.values()) {
				int value = getSearchValue(u.point(), d);
				if (maxValue < value) {
					dir = d;
					maxValue = value;
				}
			}
			if(maxValue == 0){
				Direction[] dirs = {Direction.DOWN, Direction.RIGHT};
				dir = dirs[new Random().nextInt(dirs.length)];
			}
		}
		
		u.setDir(dir);
	}
	

}
