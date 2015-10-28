package equalTest;

/**
 * Objectの比較はあくまで参照の比較
 * ＝＝だろうとequals()だろうと．
 * 中の数値が同じならば同じものとみなす　っていうのは自分でメソッドを書かないとだめ
 * @author kajiwarakengo
 *
 */
public class Main {
	public static void main(String[] args){
		ComparisonObject c1 = new ComparisonObject(1);
		ComparisonObject c2 = new ComparisonObject(1);
		if(c1 == c2){
			System.out.println("c1 == c2");
		}else{
			System.out.println("c1 != c2");
		}
		
		if(c1.equals(c2)){
			System.out.println("c1.equals(c2)");
		}else{
			System.out.println("!(c1.equals(c2))");
		}
		
		if(c1.compare(c2)){
			System.out.println("独自メソッドでの比較は成功");
		}
	}

}
