import java.util.ArrayList;
import java.util.List;

import swarm.defobj.Zone;
import swarm.gui.Raster;


public class Lion extends Animal {

	public Lion() {
		// TODO 自動生成されたコンストラクター・スタブ
	}

	public Lion(Zone arg0) {
		super(arg0);
		// TODO 自動生成されたコンストラクター・スタブ
	}

	@Override
	protected List<Position> findFood() {
		List<Position> foods = new ArrayList<Position>();
		
		List<Position> around = getAround(speed, getPos());
		for(Position p: around){
			int pointX = (p.x) ;
			int pointY = (p.y) ;
			
			//格子からはみ出さなければ
			if(isOnWorld(pointX, pointY)){
				Object food = world.getObjectAtX$Y(pointX, pointY);
				if(food instanceof Goat){
					Position foodPos = new Position(pointX, pointY);
					foods.add(foodPos);
				}
			}
		}
		return foods;
	}
	@Override
	protected void eatFood(List<Position> foods) {
		if(foods.size() > 0){
			Position foodPos = CollectionOperator.getRandom(foods);

			Animal food = (Animal)world.getObjectAtX$Y(foodPos.x, foodPos.y);
			food.die();
			move(foodPos.x, foodPos.y);
			power += recovery;
		}
	}


	@Override
	protected void bearNewAnimal(Position nullPos) {
		Lion child = clone();
		this.power = afterBreedPower;
		child.power = childInitPower;
		createAnimalOnWorld(nullPos, child);
	}
	
	public Lion clone(){
		Lion clone = new Lion(getZone());
		clone.toBreedPower = toBreedPower;
		clone.afterBreedPower = afterBreedPower;
		clone.childInitPower = childInitPower;
		clone.modelSwarm = modelSwarm;
		clone.power = power;
		clone.recovery = recovery;
		clone.speed = speed;
		clone.world = world;
		clone.worldXSize = worldXSize;
		clone.worldYSize = worldYSize;
		clone.xPos = xPos;
		clone.yPos = yPos;
		return clone;
	}

	@Override
	public Object drawSelfOn(Raster r) {
		r.drawPointX$Y$Color(xPos,yPos,ObserverSwarm.LION_COLOR);
		return this;
	}


}
