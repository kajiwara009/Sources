import java.util.ArrayList;
import java.util.List;

import swarm.space.*;
import swarm.*;
import swarm.defobj.*;

public class FoodSpace extends Discrete2dImpl{
	
	double generateProb;
	
	public FoodSpace(Zone aZone,int x,int y){
		super(aZone,x,y);
	}
	
	public void setGenerateProb(double generateProb){
		this.generateProb = generateProb;
	}
	
	public Object seedFoodWithProb(double seedProb){
		int x,y;
		int xsize,ysize;
		
		xsize=this.getSizeX();
		ysize=this.getSizeY();
		
		for (y = 0; y < ysize; y++)
			for (x = 0; x < xsize; x++)
				if(Globals.env.uniformDblRand.getDoubleWithMin$withMax(0.0,1.0)
				<seedProb)
					this.putValue$atX$Y(1,x,y);
		return this;
	}
	
	public void step(){
		for(Position pos: getAllPos()){
			if(getValueAtX$Y(pos.x, pos.y) == 0){
				generateGrass(pos.x, pos.y);
			}
		}
	}
	
	private void generateGrass(int x, int y){
		if(Math.random() < generateProb){
			putValue$atX$Y(1, x, y);
		}
	}
	
	private List<Position> getAllPos(){
		List<Position> all = new ArrayList<>();
		for(int x = 0; x < getSizeX(); x++){
			for(int y = 0; y < getSizeY(); y++){
				all.add(new Position(x, y));
			}
		}
		return all;
	}
}

