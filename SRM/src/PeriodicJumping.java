// BEGIN CUT HERE

// END CUT HERE
import java.util.*;
public class PeriodicJumping {
	public int minimalTime(int x, int[] jumpLengths) {
		
		//取りうる距離の最小値と最大値
		int minL = x, maxL = x;
		//ジャンプした回数
		int jumpTime = 0;
		
		//minL,maxLの間にjumpLengthが入っていれば，直接ジャンプ可能
		//入っていない場合は，maxLは簡単，minLは負にならなければ普通に，負になる場合はmaxLから飛んだ地点の方が近い
		
		while(minL > 0){
			int nextJump = jumpLengths[jumpTime % jumpLengths.length];
			
			if(minL <= nextJump && maxL >= nextJump){
				minL = 0;
				continue;
			}else{
				maxL = maxL + nextJump;
				if(minL - nextJump > 0){
					minL = minL - nextJump;
				}else{
					minL = nextJump - maxL;
				}
			}
		}
		
		return jumpTime;
	}
	public static void main(String[] args) {
		PeriodicJumping temp = new PeriodicJumping();
//		System.out.println(temp.minimalTime(int x, int[] jumpLengths));
	}
}
