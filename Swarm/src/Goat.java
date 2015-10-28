import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.swing.plaf.metal.MetalIconFactory.FolderIcon16;

import swarm.defobj.Zone;
import swarm.gui.Raster;
import swarm.space.Grid2d;


public class Goat extends Animal {
	FoodSpace foodSpace;
	


	public Goat() {
		// TODO 自動生成されたコンストラクター・スタブ
	}

	public Goat(Zone arg0, FoodSpace foodSpace) {
		super(arg0);
		this.foodSpace = foodSpace;
		// TODO 自動生成されたコンストラクター・スタブ
	}

	@Override
	protected void breed() {
		// TODO 自動生成されたメソッド・スタブ

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
				int food = foodSpace.getValueAtX$Y(pointX, pointY);
				if(food == 1 && world.getObjectAtX$Y(pointX, pointY) == null){
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
			
			move(foodPos.x, foodPos.y);
			
			foodSpace.putValue$atX$Y(0, foodPos.x, foodPos.y);
			power += recovery;
		}
		
	}
	
	public void setFoodSpace(FoodSpace foodSpace){
		this.foodSpace = foodSpace;
	}

	@Override
	protected void bearNewAnimal(Position nullPos) {
		Goat child = clone();
		this.power = afterBreedPower;
		child.power = childInitPower;
		createAnimalOnWorld(nullPos, child);
	}
	
	public Goat clone(){
		Goat clone = new Goat(getZone(), foodSpace);
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
		r.drawPointX$Y$Color(xPos,yPos,ObserverSwarm.GOAT_COLOR);
		return this;
	}


}
