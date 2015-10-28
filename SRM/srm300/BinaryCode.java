

public class BinaryCode {
	
	private int getNumberAt(String str, int index){
		char c = str.charAt(index);
		return Character.getNumericValue(c);
	}
	
	public String[] decode(String str){
		String[] ans = {"0", "1"};
		for(int i = 0; i < str.length() - 1; i++){
			int sum  = getNumberAt(str, i);
			for(String origin: ans){
				if(origin == "NONE"){
					continue;
				}
				int clm = (origin.charAt(0) == '0')? 0: 1;
				int preNum = (i > 0)?getNumberAt(origin, i-1): 0;
				int corNum = getNumberAt(origin, i);
				int nextNum = sum - preNum - corNum;
				if(nextNum < 0 || nextNum > 1){
					origin = "NONE";
				}else{
					origin += nextNum;
				}
				ans[clm] = origin;
			}
		}
		//最後の文字を調べる必要あり
		int sum = getNumberAt(str, str.length()-1);
		
		for(String origin: ans){
			if(origin == "NONE"){
				continue;
			}
			int preNum = (str.length()-1 > 0)?getNumberAt(origin, str.length()-2): 0;
			int corNum = getNumberAt(origin, str.length()-1);
			if(preNum + corNum != sum){
				int clm = (origin.charAt(0) == '0')? 0: 1;
				ans[clm] = "NONE";
			}else{
			}
		}

		
		return ans;
	}

	public static void main(String[] args) {
		BinaryCode temp = new BinaryCode();
		System.out.println(temp.decode("123210122"));
	}
}
