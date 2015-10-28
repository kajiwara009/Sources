package wildCardTest;

public class WildClass<T> {
	private T wild;

	public void setWild(T value){
		wild = value;
		String str = wild.getClass().toString();
		System.out.println(str);
	}
}
