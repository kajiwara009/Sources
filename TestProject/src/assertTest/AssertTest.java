package assertTest;

/**
 * VM引数に -ea を書いて，assert構文を書いて用いる
 * @author kajiwarakengo
 *
 */
public class AssertTest {

	public AssertTest() {
		// TODO 自動生成されたコンストラクター・スタブ
	}

	public static void main(String[] str){
		int x = 10;
		assert x == 11 : "x=" + x;
		assert x != 11 : "x=" + x;
		assert x < 0 : "x=" + x;
		System.out.println("assert抜けた");
	}
}
