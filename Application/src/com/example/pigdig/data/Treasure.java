package com.example.pigdig.data;

public enum Treasure {
	NORMAL_TREASURE(0, 100),
	RARE_TREASURE(1, 1000),;
	
	private int treasureNum;
	private int standardDepth;
	
	/** 出現頻度を表すテーブル．テーブルレベルがあがるとレアなアイテムが出やすくなる．*/
	private static double[][] tables = 
		{{0.8, 0.2},
		 {0.7, 0.3}};
	Treasure(int treasureNum, int depth){
		this.treasureNum = treasureNum;
		standardDepth =depth;
	}

	public static Treasure getRandomTreasure(int discern) {
		double[] table = tables[discern];
		int treasureNum = StaticMethods.rouletSelect(table);
		Treasure treasure = null;
		for(Treasure t: Treasure.values()){
			if(t.treasureNum == treasureNum){
				treasure = t;
				break;
			}
		}
		return treasure;
	}
	
	private Treasure getTreasure(int treasureNum){
		Treasure treasure = null;
		for(Treasure t: Treasure.values()){
			if(t.getTreasureNum() == treasureNum){
				treasure = t;
				break;
			}
		}
		return treasure;
	}

	public int getDepth() {
		
		switch (this) {
		case NORMAL_TREASURE:
			
			break;

		case RARE_TREASURE:
			break;
		}
		// TODO 自動生成されたメソッド・スタブ
		return 0;
	}
	
	public int getTreasureNum(){
		return treasureNum;
	}

}
