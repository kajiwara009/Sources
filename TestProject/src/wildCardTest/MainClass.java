package wildCardTest;

/**
 * ワイルドカードはインスタンス生成時にワイルドカードクラスを指定しなければ，どんな型を入れても，入れ直しても怒られない
 * @author kajiwarakengo
 *
 */
public class MainClass {
	public static void main(String[] args){
		WildClass wild = new WildClass();
		wild.setWild(0);
		wild.setWild("str");
		
		WildClass<Integer> intWild = new WildClass<Integer>();
		intWild.setWild(0);
//		intWild.setWild("str"); //コンパイルエラー
	}

}
