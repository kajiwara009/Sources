package equalTest;

public class ComparisonObject {
	int i;
	
	public ComparisonObject(int i){
		this.i = i;
	}
	
	public boolean compare(ComparisonObject c){
		if(i == c.i){
			return true;
		}else{
			return false;
		}
	}

}
