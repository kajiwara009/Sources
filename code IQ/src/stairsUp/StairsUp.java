package stairsUp;

import java.util.HashMap;
import java.util.Map;

public class StairsUp {
	private static final int MAX_RANGE = 3;
	/**
	 * これまでに求めた上り方の場合の数
	 * <n, n段目の上り方の場合の数>
	 */
	private static Map<Integer, Integer> ansMap = new HashMap<Integer, Integer>(){
		{put(0, 1);}
	};

	public StairsUp(){
		ansMap.put(0, 1);
	}
	
	/**
	 * n段の階段を上る場合の数を返す．
	 * @param n
	 * @return
	 */
	private static int getPatternNumber(int n) {
		if(ansMap.containsKey(n)){
			return ansMap.get(n);
		}
		int patternNum = 0;
		// (n - MAX_RANGE)段目から(n - 1)段目からは，一歩でn段目まで行けるのでそれらの場合の数を足す．
		for (int i = 1; i <= MAX_RANGE && i <= n; i++) {
			patternNum += getPatternNumber(n - i);
		}
		ansMap.put(n, (int) patternNum);
		return patternNum;
	}

	/**
	 * 引数に階段の段数を入力
	 * 
	 * @param args
	 */
	public static void main(String[] args){
		try {
			int stairNum = Integer.parseInt(args[0]);
			System.out.println(stairNum + "段の階段の上り方：" + StairsUp.getPatternNumber(stairNum) + "通り");
		} catch (Exception e) {
			System.out.println("引数に階段の段数を整数で入力してください");
			e.printStackTrace();
		}
	}
}