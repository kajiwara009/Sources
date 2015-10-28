import java.util.ArrayList;
import java.util.Collections;

import swarm.*;
import swarm.activity.*;
import swarm.defobj.*;
import swarm.objectbase.*;
import swarm.space.*;
import swarm.collections.*;

public class ModelSwarm extends SwarmImpl{
	
	protected int
	worldXSize = 100, worldYSize = 100,
	gInitPower = 10, lInitPower = 10,
	gSpeed = 2, lSpeed = 2,
	gRecovery = 5, lRecovery = 10,
	gToBreedPower = 30, lToBreedPower = 30,
	gAfterBreedPower = 20, lAfterBreedPower = 20,
	gChildInitPower = 10, lChildInitPower = 10;
	
	protected double
	gInitDensity = 0.2, lInitDensity = 0.2,
	generateProb = 0.1;
	
	
	FoodSpace foodSpace;
	Grid2d world;
	
	List animalList;
	ActionGroup modelActions;
	Schedule modelSchedule;
	
	public java.util.List<java.lang.String> getVarList(){
		java.util.List<java.lang.String> varList = new ArrayList<java.lang.String>();
		
		varList.add("worldXSize");
		varList.add("worldYSize");
		varList.add("gInitPower");
		varList.add("lInitPower");
		varList.add("generateProb");
		
		java.util.List<java.lang.String> animalVarList = new ArrayList<java.lang.String>();
		animalVarList.add("InitPower");
		animalVarList.add("Speed");
		animalVarList.add("Recovery");
		animalVarList.add("ToBreedPower");
		animalVarList.add("AfterBreedPower");
		animalVarList.add("ChildInitPower");
		animalVarList.add("InitDensity");
		
		for(java.lang.String varStr: animalVarList){
			java.lang.String gStr = "g" + varStr;
		}
		for(java.lang.String varStr: animalVarList){
			java.lang.String lStr = "l" + varStr;
		}
		
		
		return varList;
	}
	
	public ModelSwarm(Zone aZone){
		super(aZone);
		
		
		
		EmptyProbeMap probeMap;
		probeMap=new EmptyProbeMapImpl(aZone,this.getClass());
		
		java.util.List<java.lang.String> varList = getVarList();
		for(java.lang.String varStr: varList){
			probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass
			          (varStr, this.getClass()));
		}

/*		
		probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass
          ("worldXSize",this.getClass()));
		probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass
          ("worldYSize",this.getClass()));
		probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass
          ("bugDensity",this.getClass()));
		probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass
          ("defaultSpeed",this.getClass()));
		probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass
          ("maxSpeed",this.getClass()));
		probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass
          ("minSpeed",this.getClass()));
		probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass
          ("accel",this.getClass()));
		probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass
          ("optDistance",this.getClass()));
		probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass
          ("searchSpace",this.getClass()));
		probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass
          ("gravityWeight",this.getClass()));
		probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass
          ("nearWeight",this.getClass()));
        
*/
		Globals.env.probeLibrary.setProbeMap$For(probeMap,this.getClass());
	}
	
	public Object buildObjects(){
		
		foodSpace=new FoodSpace(this,worldXSize,worldYSize);
		
		world=new Grid2dImpl(this,worldXSize,worldYSize);
		world.fillWithObject(null);
		
		initObjects();
		
		return this;
	}
	
	public void initObjects(){
		animalList=new ListImpl(this);
		
		Bug aBug;
		int x,y;
		for (y = 0; y < worldYSize; y++){
			for (x = 0; x < worldXSize; x++){
				
				// TODO オブジェクト初期生成
				
				int lNum = (int)((double)(worldXSize * worldYSize) * lInitDensity);
				int gNum = (int)((double)(worldXSize * worldYSize) * gInitDensity);
				
				java.util.List<Position> posList = new ArrayList<Position>();
				for(int xPos = 0; xPos < worldXSize; xPos++){
					for(int yPos = 0; yPos < worldYSize; yPos++){
						posList.add(new Position(xPos, yPos));
					}
				}
				
				java.util.List<Position> putList = new ArrayList<Position>();
				
				Collections.shuffle(posList);
				for(int num = 0; num < lNum + gNum; num++){
					putList.add(posList.get(num));
				}
				
				for(int i = 0; i < lNum + gNum; i++){
					Animal animal;
					Position pos = putList.get(i);
					if(i < lNum){
						animal = new Lion(this);
						animal.SetParameters(lRecovery, lToBreedPower, worldXSize, worldYSize);
						animal.power = lInitPower;
						animal.speed = lSpeed;
						animal.childInitPower = lChildInitPower;
						animal.setWorld(world);
						animal.setModelSwarm(this);
						animal.setX$Y(pos.x, pos.y);
					}else{
						animal = new Goat(this, foodSpace);
						animal.SetParameters(gRecovery, gToBreedPower, worldXSize, worldYSize);
						animal.power = gInitPower;
						animal.speed = gSpeed;
						animal.childInitPower = gChildInitPower;
						animal.setWorld(world);
						animal.setModelSwarm(this);
						animal.setX$Y(pos.x, pos.y);
					}
					animalList.addLast(animal);
				}
				
				
				
/*				if (Globals.env.uniformDblRand.getDoubleWithMin$withMax
				(0.0,1.0) < bugDensity){
					
					aBug=new Bug(this);
					aBug.setWorld$Food(world,foodSpace);
					aBug.setX$Y(x,y);
					aBug.setSpeed( (float)defaultSpeed );
					aBug.setDirection( (float)Globals.env.uniformDblRand.getDoubleWithMin$withMax(0.0,360.0) );
					aBug.SetParameters( (float)maxSpeed, (float)minSpeed, (float)accel, (float)optDistance,
								searchSpace, (float)gravityWeight, (float)nearWeight );
				}
				*/
			}
		}

	}
	
	public Object buildActions(){
		modelActions=new ActionGroupImpl(this);
		try{
			modelActions.createActionForEach$message(animalList,
				new Selector(Class.forName("Animal"),"step",false));
			modelActions.createActionTo$message(foodSpace, new Selector(Class.forName("FoodSpace"), "step", false));

		} catch (Exception e) {
			e.printStackTrace (System.err);
			System.exit(1);
		}
		
		
		modelSchedule=new ScheduleImpl(this,1);
		modelSchedule.at$createAction(0,modelActions);
		return this;
	}
	
	public Activity activateIn(Swarm context){
    	super.activateIn (context);
    	modelSchedule.activateIn(this);
		return getActivity();
	}
	
	public Grid2d getWorld(){
		return world;
	}
	
	public FoodSpace getFood(){
		return foodSpace;
	}
	
	public List getAnimalList(){
		return animalList;
	}
}
