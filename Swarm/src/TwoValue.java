

import java.util.ArrayList;
import java.util.List;

/**
 * 2つの値を格納するクラス
 * @author kajiwarakengo
 *
 * @param <One>
 * @param <Two>
 */
public class TwoValue<One, Two> {
	private One one;
	private Two two;

	public TwoValue() {
		// TODO 自動生成されたコンストラクター・スタブ
	}
	
	public TwoValue(One one, Two two){
		this.one = one;
		this.two = two;
	}

	
	
	
	
	
	
	
	
	
	
	
	
	public One getOne() {
		return one;
	}

	public void setOne(One one) {
		this.one = one;
	}

	public Two getTwo() {
		return two;
	}

	public void setTwo(Two two) {
		this.two = two;
	}
	
	
	public static void main(String[] args){
		List<TwoValue<Integer, Double>> twoValueList = new ArrayList<TwoValue<Integer,Double>>();
	}

}
