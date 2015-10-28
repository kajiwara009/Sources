package data;

import java.util.Map;

import unitDirector.UnitDirector;

public class Unit implements Composite{
	// ユニットの現在のステータス。
	// 与えられる入力により input() で設定される。
	private final int id;
	private Vector point;
	private int hp;
	private final UnitType type;
	private Direction dir;
	private boolean stay;
	private UnitType produce;//生産しようとしているユニット．移動より優先
	
	private Vector lastPoint;
	private int lastHP;
	
	private UnitDirector unitDirector;

	/**
	 * 必要となればダメージテーブルも加える
	 * @author kajiwarakengo
	 *
	 */
/*	public enum UnitType{
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
		
	}*/
	
	
	public Unit(int id, int x, int y, UnitType type){
		this.id = id;
		this.point = new Vector(x, y);
		this.type = type;
		this.hp = type.getMaxHP();
		this.dir = null;
		this.produce = null;
		this.unitDirector = null;
	}
	
	public Unit update(int x, int y, int hp){
		this.lastPoint = point.clone();
		this.lastHP = this.hp;
		this.point = new Vector(x, y);
		this.hp = hp;
		free();
		return this;
	}
	
	public boolean isMoving(){
		if(dir != null && !isProducing()){
			return true;
		}else{
			if(stay){
				return true;
			}return false;
		}
	}
	
	public boolean isProducing() {
		return produce != null;
	}
	public boolean isFree() {
		return !isMoving() && !isProducing();
	}
	public void free() {
		dir = null;
		produce = null;
		stay = false;
	}
	
	/**
	 * どのUnitDirectorにも所属していないとNeet
	 * @return
	 */
	public boolean isNeet(){
		if(unitDirector == null){
			return true;
		}else{
			return false;
		}
	}

	// 現在設定されている行動を出力用の文字列に変換する。
	// 行動が設定されてない場合は長さ 0 の文字列を返す。
	public String toOrderString(boolean isTopLeft) {
		if (isProducing()) {
			if(type == UnitType.WORKER){
//				System.err.println("Unit:" + getUnitDirector().getClass().getName() + "で" + produce.toString());
			}
			return id + " " + produce.getOrderNum();
		}
		else if (isMoving() && !isStay()) {
			Direction trueDirection = isTopLeft? dir: dir.getReverse();
			return id + " " + trueDirection.toOrderString();
		}
		else{
			return "";
		}
	}
	
	/**
	 * UnitDirectorのaddとセットだから単体では使わない
	 * @param ud
	 */
	public boolean setUnitDirector(UnitDirector ud){
		if(unitDirector == null){
			unitDirector = ud;
			return true;
		}else{
			System.err.println("Unitクラス:既にUnitDirectorに所属");
			return false;
		}
	}
	
	public boolean resetUnitDirector(UnitDirector ud){
		if(unitDirector == ud){
			unitDirector = null;
			return true;
		}else{
			System.err.println("Unitクラス:指定されたUnitDirectorには所属していません");
			return false;
		}
		
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public int id() {
		return id;
	}
	
	public Vector point(){
		return point;
	}
	
	public void setCoordinates(Vector point){
		this.point = point;
	}

	public int y() {
		return point.getY();
	}

	public void setY(int y) {
		this.point.setY(y);
	}

	public int x() {
		return point.getX();
	}

	public void setX(int x) {
		this.point.setX(x);
	}

	public int hp() {
		return hp;
	}

	public void setHp(int hp) {
		this.hp = hp;
	}

	public UnitType type() {
		return type;
	}

	public Direction getDir() {
		return dir;
	}

	public void setDir(Direction dir) {
		this.dir = dir;
	}

	public UnitType getProduce() {
		return produce;
	}

	public void setProduce(UnitType produce) {
		this.produce = produce;
	}

	public int getLastX() {
		return lastPoint.getX();
	}

	public void setLastX(int lastX) {
		this.setLastX(lastX);
	}

	public int getLastY() {
		return lastPoint.getY();
	}

	public void setLastY(int lastY) {
		this.lastPoint.setY(lastY);
	}

	public int getLastHP() {
		return lastHP;
	}

	public void setLastHP(int lastHP) {
		this.lastHP = lastHP;
	}

	public Vector getLastPoint() {
		return lastPoint;
	}

	public void setLastPoint(Vector lastPoint) {
		this.lastPoint = lastPoint;
	}

	public UnitDirector getUnitDirector() {
		return unitDirector;
	}

	public boolean isStay() {
		return stay;
	}

	public void setStay(boolean stay) {
		this.stay = stay;
	}
	
}
