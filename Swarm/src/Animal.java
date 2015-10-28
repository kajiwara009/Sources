import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import kawa.lib.trace;
import swarm.defobj.Zone;
import swarm.gui.Raster;
import swarm.objectbase.SwarmObjectImpl;
import swarm.space.Grid2d;


public abstract class Animal extends SwarmObjectImpl {
	protected int
		power,
		speed,
		recovery,
		toBreedPower,
		afterBreedPower,
		childInitPower,
		
		xPos, yPos,
		worldXSize, worldYSize;
	
	protected Grid2d world;
	protected ModelSwarm modelSwarm;
	
	
	public Animal() {
		// TODO 自動生成されたコンストラクター・スタブ
	}

	public Animal(Zone arg0) {
		super(arg0);
		// TODO 自動生成されたコンストラクター・スタブ
	}
	
	public Object SetParameters(int recovery, int breedPower, int worldXSize, int worldYSize){
		this.recovery = recovery;
		this.toBreedPower = breedPower;
		this.worldXSize = worldXSize;
		this.worldYSize = worldYSize;
		return this;
	}
	
	
	
	public void step(){
		getFood();
		dieJudge();
		breed();
		
	}
	
	protected void getFood(){
		List<Position> foods = findFood();
		if(foods.size() > 0){
			eatFood(foods);
		}else{
			randomWalk();
		}
	}
	
	protected abstract List<Position> findFood();
	
	protected void randomWalk(){
		List<Position> aroundPoses = getAround(speed, getPos());
		Collections.shuffle(aroundPoses);
		for(Position p: aroundPoses){
			if(world.getObjectAtX$Y(p.x, p.y) == null){
				move(p.x, p.y);
			}
		}
		
	}
	
	protected void move(int x, int y){
		if(world.getObjectAtX$Y(x, y) == null){
			world.putObject$atX$Y(null, xPos, yPos);
			world.putObject$atX$Y(this, x, y);
			xPos = x;
			yPos = y;
		}else{
			try {
				throw new ConflictException();
			} catch (Exception e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 移動，捕食，体力回復
	 * @param foods
	 */
	protected abstract void eatFood(List<Position> foods);

	protected void dieJudge(){
		power -= 1;
		if(power <= 0){
			die();
		}
	}
	protected void breed(){
		if(power >= toBreedPower){
			
			List<Position> nullPoses = new ArrayList<Position>();
			for(int x = -1; x <= 1; x++){
				for(int y = -1; y <= 1; y++){
					int pointX = (xPos + x) ;
					int pointY = (yPos + y) ;
					
					//格子からはみ出さなければ
					if(isOnWorld(pointX, pointY)){
						Object obj = world.getObjectAtX$Y(pointX, pointY);
						if(obj == null){
							Position p = new Position(pointX, pointY);
							nullPoses.add(p);
						}
					}
				}
			}
			
			if(nullPoses.size() > 0){
				Position nullPos = CollectionOperator.getRandom(nullPoses);
				bearNewAnimal(nullPos);
			}
			return;
		}
	}
	
	protected abstract void bearNewAnimal(Position nullPos);
	
	protected void createAnimalOnWorld(Position pos, Animal animal){
		if(world.getObjectAtX$Y(pos.x, pos.y) == null){
			world.putObject$atX$Y(animal, pos.x, pos.y);
			modelSwarm.animalList.addLast(animal);
		}else{
			try {
				throw new ConflictException();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	protected void die(){
		world.putObject$atX$Y(this, xPos, yPos);
		modelSwarm.animalList.remove(this);
	}
	
	/**
	 * 周囲半径nマスの座標Listをとる．自分の位置も含める
	 * @param radius
	 * @param pos
	 * @return
	 */
	public List<Position> getAround(int radius, Position pos){
		List<Position> poses = new ArrayList<Position>();
		
		for(int x = -radius; x <= radius; x++){
			for(int y = -radius; y <= radius; y++){
				poses.add(new Position(pos.x + x, pos.y + y));
			}
		}
		
		return poses;

	}
	
	public boolean isOnWorld(int x, int y){
		if(x >= 0 && y >= 0 && x < world.getSizeX() && y < world.getSizeY()){
			return true;
		}else{
			return false;
		}
	}

	
	public abstract Object drawSelfOn(Raster r);
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public Object setX$Y(int x, int y){
		xPos = x;
		yPos = y;
		world.putObject$atX$Y(this,x,y);
		return this;
	}

	public Object setWorldSizeX$Y(int xSize, int ySize){
		worldXSize=xSize;
		worldYSize=ySize;
		return this;
	}
	
	
	public Position getPos(){
		return new Position(xPos, yPos);
	}
	
	public Grid2d getWorld() {
		return world;
	}

	public void setWorld(Grid2d world) {
		this.world = world;
	}

	public ModelSwarm getModelSwarm() {
		return modelSwarm;
	}

	public void setModelSwarm(ModelSwarm modelSwarm) {
		this.modelSwarm = modelSwarm;
	}

}
