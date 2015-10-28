package unitDirector.fighter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import stateDirector.AssaultDirector;
import unitDirector.UnitDirector;
import unitDirector.worker.ResourceCollectDirector;
import codevs.God;
import data.Composite;
import data.Direction;
import data.Unit;
import data.UnitListFactory;
import data.UnitType;
import data.Vector;

public class CastleAttackDirector extends UnitDirector {
	public static final int SEARCH_VALUE_RANGE = 20;
	
	private List<Unit>[][] myUnitMap = new ArrayList[100][100];
	private List<Unit>[][] opUnitMap = new ArrayList[100][100];
	
	public CastleAttackDirector(God god) {
		super(god);
	}

	@Override
	public void moveUnits() {
		for(int x = 0; x < 100; x++) for(int y = 0; y < 100; y++){
			myUnitMap[x][y] = new ArrayList<Unit>();
			opUnitMap[x][y] = new ArrayList<Unit>();
		}
		for(Unit u: getUnits()){
			myUnitMap[u.x()][u.y()].add(u);
		}
		for(Unit u: god.getOpUnits().values()){
			opUnitMap[u.x()][u.y()].add(u);
		}
		
/*		for(Unit base: god.getUnits(UnitType.BASE)){
			if(myUnitMap[base.x()][base.y()].size() < 9){
				for(Unit u: myUnitMap[base.x()][base.y()]){
					u.setStay(true);
				}
			}
		}
*/		
		if(god.hasFoundOpCastle()){
			attackCastle();
		}else{
			searchCastle();
		}
	}
	
	protected List<Unit> getOpUnitsAround(Vector pos, int range){
		List<Unit> ops = new ArrayList<>();
		int stanX = pos.getX();
		int stanY = pos.getY();
		for(int x = stanX - range; x < stanX + range; x++){
			int yRange = range - Math.abs(stanX - x);
			for(int y = stanY - yRange; y < stanY + yRange; y++){
				if(x >= 0 && y >= 0 && x < 100 && y < 100){
					ops.addAll(opUnitMap[x][y]);
				}
			}
		}
		return ops;
	}
	
	/**
	 * ダメージ/HPで判断(拠点，城，村　除く)
	 * @param pos
	 * @param dir
	 * @return
	 */
	protected double getMoveValue(Vector pos, Direction dir){
		List<Unit> mys = getUnitCrowd(pos, dir);
		List<Unit> ops = getOpUnitsAround(pos.plus(dir.getVector()), 3);
		
		if(mys.size() > 3 && ops.size() > 3){
//			System.err.println("mysdd:" + culcDamage(mys, ops) + "  opsdd:" + culcDamage(ops, mys));
		}
		
		return culcDamage(mys, ops) / culcDamage(ops, mys);
		
	}
	
	protected List<Unit> getUnitCrowd(Vector pos, Direction dir){
		Vector zero = new Vector(0, 0);
 		Vector b = dir.getReverse().getVector();
		Vector r = dir.getAntiClockWise().getVector();
		Vector l = dir.getClockWise().getVector();
		Vector[] vecs = {zero, r, l, r.plus(b), b, l.plus(b)};
		List<Unit> mys = new ArrayList<>();
		for(Vector v: vecs){
			int x = pos.getX() + v.getX();
			int y = pos.getY() + v.getY();
			if(x >= 0 && x < 100 && y >= 0 && y < 100){
				mys.addAll(myUnitMap[pos.getX() + v.getX()][pos.getY() + v.getY()]);
			}
		}
		return mys;
	}
	
	protected double culcDamage(List<Unit> attacker, List<Unit> defencer){
		Map<UnitType, Integer> attack = new HashMap<UnitType, Integer>();
		Map<UnitType, Integer> defence = new HashMap<UnitType, Integer>();
		attack.put(UnitType.KNIGHT, UnitListFactory.getUnitsTypeOf(attacker, UnitType.KNIGHT).size());
		attack.put(UnitType.FIGHTER, UnitListFactory.getUnitsTypeOf(attacker, UnitType.FIGHTER).size());
		attack.put(UnitType.ASSASSIN, UnitListFactory.getUnitsTypeOf(attacker, UnitType.ASSASSIN).size());
		defence.put(UnitType.KNIGHT, UnitListFactory.getUnitsTypeOf(defencer, UnitType.KNIGHT).size());
		defence.put(UnitType.FIGHTER, UnitListFactory.getUnitsTypeOf(defencer, UnitType.FIGHTER).size());
		defence.put(UnitType.ASSASSIN, UnitListFactory.getUnitsTypeOf(defencer, UnitType.ASSASSIN).size());
		
		int dNum = defencer.size();
		int damage = 0;
		
		for(Entry<UnitType, Integer> aSet: attack.entrySet()){
			for(Entry<UnitType, Integer> dSet: defence.entrySet()){
				damage += aSet.getValue() * dSet.getValue() * aSet.getKey().damegeTo(dSet.getKey());
			}
		}
		return (double)damage / (double)dNum;
	}
	
	protected void attackCastle(){
		
		Vector opCastle = god.getOpCastle().point();
		List<Unit> opCastles = getOpUnitsAround(opCastle, 4);
		List<Unit> myCastleAttacker = UnitListFactory.getUnitsAround(units, opCastle, 4);
		
		if(culcDamage(myCastleAttacker, opCastles) > culcDamage(opCastles, myCastleAttacker)){
			for(Unit u: myCastleAttacker){
				Vector v = opCastle.minus(u.point());
				u.setDir(v.getAboutDirection());
				if(u.point().getMhtDist(opCastle) < 2){
					Direction randD = Direction.values()[new Random().nextInt(Direction.values().length)];
					u.setDir(randD);
				}
			}
		}
		
		for(int x = 99; x >= 0; x--) for(int y = 99; y >= 0; y--){
			if(myUnitMap[x][y].size() == 0) continue;
			else if(myUnitMap[x][y].get(0).isMoving()) continue;
			else if(god.getOpCastle().point().equals(new Vector(x, y))) continue;
			
			Vector vec = god.getOpCastle().point().minus(new Vector(x, y));
			Direction dir = Direction.getAboutDirection(vec);
//			Direction dir = vec.getAboutDirection();
			if(getOpUnitsAround(new Vector(x, y).plus(dir.getVector()), 3).size() != 0){
//				System.err.println("most:" + getMoveValue(new Vector(x, y), dir));
				if(getMoveValue(new Vector(x, y), dir) < 0.8){
					continue;
				}else{
					for(Unit u: getUnitCrowd(new Vector(x, y), dir)){
						u.setDir(dir);
					}
				}
				//前に敵が居なかったとき
			}else{
				int newX = x + dir.getX();
				int newY = y + dir.getY();
				if(myUnitMap[newX][newY].size() >= 10){
					
/*					if(myUnitMap[x + dir.getX()][y + dir.getY()].size() <= 10){
						for(Unit u: myUnitMap[x][y]){
							u.setDir(dir);
						}
					}else{
*/						Vector[] vs = {dir.getClockWise().getVector(), dir.getAntiClockWise().getVector(), new Vector(0, 0)};
						int maxNum = -1;
						for(Vector v: vs){
							if(newX + v.getX() >= 100 || newY + v.getY() >= 100) continue;
							int num = myUnitMap[newX + v.getX()][newY + v.getY()].size();
							if(num < 10 && num > maxNum){
								dir = Direction.getDirection(v);
								maxNum = num;
							}
						}
//					}
				}else{
					for(Unit u: myUnitMap[x][y]){
						u.setDir(dir);
					}
					
/*					for(Unit u: getUnitCrowd(new Vector(x, y), dir)){
						u.setDir(dir);
					}
*/					
				}
				
			}

		}
	}
		
/*		for(Unit u: units){
			if(u.isMoving()) continue;
			
			Vector vec = god.getOpCastle().point().minus(u.point());
			Direction dir = Direction.getAboutDirection(vec);
			u.setDir(dir);
			List<Unit> unitsAt = getUnitsAt(u.point());
			for(Unit uAt: unitsAt){
				u.setDir(dir);
			}
		}*/
	
	protected List<Unit> getUnitsAt(Vector pos){
		return UnitListFactory.getUnitsAt(units, pos);
	}
	
	protected void searchCastle(){
		for(Unit u: units){
			if(u.isMoving()) continue;
			setOptimalCastleSearchDirection(u);
			for(Unit unit: getUnitsAt(u.point())){
				unit.setDir(u.getDir());
			}
		}
	}
	
	protected void setOptimalCastleSearchDirection(Unit u){
		
		Direction dir = null;
		
		Map<Direction, Double> damageValueMap = new HashMap<>();
		for(Direction d: Direction.values()){
			double damage = getMoveValue(u.point(), d);
			damageValueMap.put(d, damage);
		}
		
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
				dir = Direction.getAboutDirection(AssaultDirector.target.minus(u.point()));
			}
		}
		
		u.setDir(dir);
	}
	
	protected int getSearchValue(Vector pos, Direction dir){
		int value = 0;
		int dirUnit = 0; //そっち方向にいるUnit数
		boolean[][] castlePos = god.getCastlePosible();
		int microSVR = SEARCH_VALUE_RANGE / 2;
		int nearAttackerRange = SEARCH_VALUE_RANGE * 3 / 8;
		Vector middlePoint = pos.plus(dir.getVector().multi(microSVR));
		Vector nearJudge = pos.plus(dir.getVector().multi(nearAttackerRange));
		/*
		 * もし探索方向10マスにワーカーが居た場合は，そいつが探索するだろうから探索の価値がないと考える
		 */
		for(Unit unit: god.getUnits()){
			if(nearJudge.getMhtDist(unit.point()) < nearAttackerRange){
				dirUnit++;
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
				if(!castlePos[x][y]){
					continue;
				}
				value += getMicroValue(pos, new Vector(x, y));
			}
		}
		return value / Math.max(dirUnit, 1);
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

	/**
	 * 城の攻撃をうまく感知出来るようになったら，話が変わる
	 * @param pos
	 * @param dir
	 * @return
	 */
	protected int getViewWiden(Vector pos, Direction dir){
		int widen = 0;
		int round = UnitType.WORKER.getSight() + 1;
		boolean[][] castleSee = god.getCastlePosible();
		
		Vector top = pos.plus(dir.getVector().multi( round));
		if(top.isOnField()){
			if(castleSee[top.getX()][top.getY()]) widen++;
		}else{
			//壁までの距離が小さすぎるときは無駄にそっちに行かない
			return 0;
		}
		for(int i = 1; i < round; i++){
			Vector standard = pos.plus(dir.getVector().multi(i));
			Vector addPoint = dir.getClockWise().getVector().multi(round - i);
			Vector point1 = standard.plus(addPoint);
			Vector point2 = standard.minus(addPoint);
			if(point1.isOnField() && castleSee[point1.getX()][point1.getY()]) widen++;
			if(point2.isOnField() && castleSee[point2.getX()][point2.getY()]) widen++;
		}
		return widen;
	}


}
