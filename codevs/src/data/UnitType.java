package data;

import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.Port;

public enum UnitType{
	WORKER(2000, 4, 2, 40, 0),
	KNIGHT(5000, 4 ,2 ,20, 1),
	FIGHTER(5000, 4, 2, 40, 2),
	ASSASSIN(5000, 4, 2, 60, 3),
	CASTLE(50000, 10, 10, Integer.MAX_VALUE, 4),
	VILLAGE(20000, 10, 2, 100, 5),
	BASE(20000, 4, 2, 500, 6);
	private final int maxHP;
	private final int sight;
	private final int attackRenge;
	private final int cost;
	private final int typeNum;
	
	private final int[][] damegeTable = 
		   {{100, 100, 100, 100, 100, 100, 100},
			{100, 500, 200, 200, 200, 200, 200},
			{500, 1600, 500, 200, 200, 200, 200},
			{1000, 500, 1000, 500, 200, 200, 200},
			{100, 100, 100, 100, 100, 100, 100},
			{100, 100, 100, 100, 100, 100, 100},
			{100, 100, 100, 100, 100, 100, 100},};
	
	UnitType(int maxHP, int sight, int attackRenge, int cost, int typeNum){
		this.maxHP = maxHP;
		this.sight = sight;
		this.attackRenge = attackRenge;
		this.cost = cost;
		this.typeNum = typeNum;
	}
	
	public static UnitType getType(int typeNum){
		for(UnitType t: UnitType.values()){
			if(t.typeNum == typeNum){
				return t;
			}
		}
		System.err.println(typeNum + "番タイプのユニットは存在しません．");
		return null;
	}
	
	public int damegeTo(UnitType type){
		return damegeTable[this.getOrderNum()][type.getOrderNum()];
	}
	
	public int damegeFrom(UnitType type){
		return damegeTable[type.getOrderNum()][this.getOrderNum()];
	}
	
	public boolean isMovable(){
		switch (this) {
		case WORKER:
		case KNIGHT:
		case FIGHTER:
		case ASSASSIN:
			return true;
		default:
			return false;
		}
	}
	
	public static List<UnitType> getProducableUnitTypes(UnitType produce){
		List<UnitType> producer = new ArrayList<>();
		switch (produce) {
		case CASTLE:
			break;
		case VILLAGE:
		case BASE:
			producer.add(WORKER);
			break;
		case ASSASSIN:
		case FIGHTER:
		case KNIGHT:
			producer.add(BASE);
			break;
		case WORKER:
			producer.add(CASTLE);
			producer.add(VILLAGE);
			break;
		}
		return producer;
	}
	
	public static UnitType getStrongestAttacker(int cost){
		if(cost < KNIGHT.cost){
			return null;
		}else if(cost < FIGHTER.cost){
			return KNIGHT;
		}else if(cost < ASSASSIN.cost){
			return FIGHTER;
		}else{
			return ASSASSIN;
		}
	}
	
	public static List<UnitType> getAttackerValues(){
		List<UnitType> attackers = new ArrayList<>();
		attackers.add(KNIGHT);
		attackers.add(FIGHTER);
		attackers.add(ASSASSIN);
		return attackers;
	}
	
	
	

	public int getMaxHP() {
		return maxHP;
	}

	public int getSight() {
		return sight;
	}

	public int getAttackRenge() {
		return attackRenge;
	}

	public int getCost() {
		return cost;
	}

	public int getOrderNum() {
		return typeNum;
	}
}
