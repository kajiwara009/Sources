// BEGIN CUT HERE

// END CUT HERE
import java.util.*;
public class Bonuses {
	public int[] getDivision(int[] points) {
		
		int length = points.length;
		
		int[] ans = new int[length];
		
		int sum = 0;
		for(int i: points){
			sum += i;
		}
		
		int rest = 100;
		for(int i = 0; i < length; i++){
			ans[i] = points[i] * 100 / sum;
			rest -= ans[i];
		}
		
		int currentMax = Integer.MAX_VALUE;
		while(rest > 0){
			int max = getMaxWithLimit(points, currentMax);
			currentMax = max;
			
			for(int i = 0; i < length; i++){
				if(points[i] == max){
					ans[i] = ans[i] + 1;
					rest--;
					if(rest <= 0){
						break;
					}
				}
			}
		}
		
		
		return ans;
	}
	
	private static int getMaxWithLimit(int[] array, int limit){
		int max = Integer.MIN_VALUE;
		for(int i: array){
			if(i > max && i < limit){
				max = i;
			}
		}
		return max;
	}
	
	public static void main(String[] args) {
		Bonuses temp = new Bonuses();
		
		int[] sample = {1};
		
		System.out.println(temp.getDivision(sample));
	}
}
