package revolution;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

//何回も試行を行うシミュレーションでは，Random().nextDouble()はランダム性が弱いのか
//Math.random()の方が適切な値が得られる
public class Starter {
	private static double revolutionRate = 16.0;
	private static int unitBallNum = 250;
	private static int trialNum = 10;
	
	public static void main(String[] args){
		
		List<Integer> revolutionNum = new ArrayList<Integer>();
		
		for(int i = 0; i < trialNum; i++){
			revolutionNum.add(trial());
		}
		
		int sum = 0;
		int currentTrialNum = 0;
		
		int[] frequency = new int[100];
		
		for(Integer num: revolutionNum){
			
			if(num < 100){
				frequency[num] = frequency[num] + 1;
			}else{
				System.err.println();
			}
			
			currentTrialNum++;
			sum += num;
			System.out.println(num + "\t現在の平均：" + (double)sum/(double)currentTrialNum);
		}
		for(int i = 0; i < frequency.length; i++){
//			System.out.println(i + "\t" + frequency[i]);
		}
		
		System.out.println("平均は" + (double)sum/(double)trialNum);
	}
	
	public static int trial(){
		double p = getProbability(revolutionRate);
		int revolveNum = 0;
		int ballNum = unitBallNum;
		while(ballNum > 0){
			ballNum--;
			if(Math.random() < p){
				revolveNum++;
				ballNum += 3;
			}
		}
		return revolveNum;
	}
	
	public static double getProbability(double rate){
		double virtualBallNum = (double)unitBallNum + rate * 3.0;
		return rate / virtualBallNum;
	}

}
