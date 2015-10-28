package jp.ac.shibaura_it.ma15082;

import java.util.Map.Entry;

public class Pair<L,R> implements Entry<L,R>{
	L left;
	R right;
		
	public Pair(L key,R value){
		left=key;
		right=value;
	}
	
	public String toString(){
		return "["+left+","+right+"]";
	}
	@Override
	public L getKey() {
		return left;
	}

	@Override
	public R getValue() {
		return right;
	}

	@Override
	public R setValue(R arg0) {
		R ret=right;
		right=arg0;
		return ret;
	}

}
