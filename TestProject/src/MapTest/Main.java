package MapTest;

import java.util.HashMap;
import java.util.Map;
/**
 * 代入の　＝　は，アドレスをさす
 * インスタンスはオブジェクトであれば，参照する場所だけを指している
 * 新たに　＝　をすると，参照場所が変わるだけ
 * @author kajiwarakengo
 *
 */
public class Main {

	public Main() {
		// TODO 自動生成されたコンストラクター・スタブ
	}
	
	Map<Object, Double> map = new HashMap<Object, Double>();
	
	public static void main(String[] args){
		Main main = new Main();
		main.map.put("First", 1.0);
		Map<Object, Double> newMap = main.getMap();
		newMap.put("First", 2.0);
		newMap = null;
		System.out.println(main.getMap().get("First"));
	}

	private Map<Object, Double> getMap() {
		
		return map;
	}
	

}
