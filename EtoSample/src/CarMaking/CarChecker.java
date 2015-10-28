package CarMaking;

public class CarChecker {
	public static void main(String[] args){
		Handle myHandle;
		myHandle = new Handle(10, 8);
		
		Engine myEngine;
		myEngine = new Engine(1000000);
		
		Car myCar;
		myCar=new Car(myHandle,myEngine);
		
		System.out.println("馬力は" + myCar.myEngine.power);
	}

}
