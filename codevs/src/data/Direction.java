package data;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;

public enum Direction {
	UP,
	DOWN,
	LEFT,
	RIGHT;
	
	private final static Map<Direction, Vector> vectorMap = new HashMap<Direction, Vector>(){
		{put(DOWN, new Vector(0, 1));}
		{put(LEFT, new Vector(-1, 0));}
		{put(RIGHT, new Vector(1, 0));}
		{put(UP, new Vector(0, -1));}
	};

	
	public String toOrderString() {
		String order = this.toString();
		return Character.toString(order.toCharArray()[0]);
	}
	
	/**
	 * インスタンスそのものは変化させない
	 * @return
	 */
	public Direction getReverse(){
		switch (this) {
		case DOWN:
			return UP;
		case LEFT:
			return RIGHT;
		case RIGHT:
			return LEFT;
		case UP:
			return DOWN;
		default:
			return null;
		}
	}
	
	public Direction getClockWise(){
		switch (this) {
		case DOWN:
			return LEFT;
		case LEFT:
			return UP;
		case RIGHT:
			return DOWN;
		case UP:
			return RIGHT;
		default:
			return null;
		}
	}
	
	public Direction getAntiClockWise(){
		return this.getReverse().getClockWise();
	}
	
	public Vector getVector(){
		Vector v = vectorMap.get(this);
		return v.clone();
	}
	
	public static Direction getDirection(Vector v){
		for(Entry<Direction, Vector> set: vectorMap.entrySet()){
			if(set.getValue().equals(v)){
				return set.getKey();
			}
		}
		System.err.println("Directionクラス:不適切なVectorの入力");
		return null;
	}
	
	/**
	 * 第1ベクトルか第2ベクトルをランダムで返す
	 * @param vec
	 * @return
	 */
	public static Direction getAboutDirection(Vector vec){
		if(vec.getAboutDirection() == null) return null;
		else{
			Direction v1 = vec.getAboutDirection();
			Direction v2 = vec.getSubAboutDirection();
			if(v2 == null || new Random().nextBoolean()) return v1;
			else return v2;
		}
	}
	
	public int getX(){
		return this.getVector().getX();
	}
	
	public int getY(){
		return this.getVector().getY();
	}

}
