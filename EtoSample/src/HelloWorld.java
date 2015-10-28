
public class HelloWorld {
	public static void main(String[] args){
		
		
		System.out.println(  minus(10,5)    );
		
		
		
	}
	
	public static int minus(int a, int b){
		
		
		return a - b;
	}
	public static int sigma(int firstNumber, int lastNumber){
		int answer = 0;
		for(int i = firstNumber; i <= lastNumber; i++){
			if(i % 2 == 0){
				answer += i;
			}
			
		}
		
		return answer;
	}

}
