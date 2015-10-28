package unitDirector.worker;

import unitDirector.UnitDirector;
import codevs.God;
import data.Direction;
import data.Unit;
import data.UnitListFactory;
import data.Vector;

public class InvationDirector extends UnitDirector {
	public static final Vector TARGET = new Vector(80, 80);


	public InvationDirector(God god) {
		super(god);
		// TODO 自動生成されたコンストラクター・スタブ
	}
	
	public boolean isArrived(){
		for(Unit u: units){
			if(u.point().getMhtDist(TARGET) < 2){
				return true;
			}
		}
		return false;
	}

	
	@Override
	public void moveUnits() {
		for(Unit u: units){
			Vector target = TARGET;
			for(Vector v: god.getResources()){
				if(god.getStateDirector().getWorkerDirector().getResColDirectorAt(v) == null){
					System.err.println(v.getX() + "  " + v.getY());
				}
				if(god.getStateDirector().getWorkerDirector().getResColDirectorAt(v).getUnits().size() == 0){
					if(v.getMhtDist(u.point()) <= u.type().getSight() && god.getOppUnitsAt(v).size() <= 2){
						if(UnitListFactory.getUnitsAt(god.getVillagesAndCastle(), v).size() == 0){
							target = v;
							break;
						}
					}
				}
			}
			
			
			Direction dir = null;
			if(!u.point().equals(target)){
				Direction first = target.minus(u.point()).getAboutDirection();
				Direction second =target.minus(u.point()).getSubAboutDirection();
				if(second == null) second = first == Direction.DOWN? Direction.RIGHT: Direction.DOWN;
				int firstDamage = god.getMaxDamageAt(u.point().plus(first.getVector()), u.type());
				int secondDamage = god.getMaxDamageAt(u.point().plus(second.getVector()), u.type());
				
				if(!target.equals(TARGET)){
					if(firstDamage <= 400 || secondDamage <= 400){
						dir = first;
					}
				}
				else if(firstDamage <= 100 || secondDamage <= 100){
					dir = firstDamage <= secondDamage? first: second;
				}else{
					int thirdDamage = god.getMaxDamageAt(u.point().plus(second.getReverse().getVector()), u.type());
					int fourthDamage = god.getMaxDamageAt(u.point().plus(first.getReverse().getVector()), u.type());
					if(thirdDamage <= 100 || fourthDamage <= 100){
						dir = thirdDamage <= fourthDamage? second.getReverse(): first.getReverse();
					}
				}
			}
			u.setDir(dir);
		}
	}
}
