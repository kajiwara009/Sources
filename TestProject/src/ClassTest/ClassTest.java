package ClassTest;

public class ClassTest {
	
	static int i = 0;
	
	public ClassTest(){}
	
	public ClassTest(int in){
		i = in;
	}
	
	public static void main(String[] args){
		try {
			Class.forName("ClassTest.ClassTest").newInstance();
			System.out.println(i);
		} catch (Exception e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}
}
