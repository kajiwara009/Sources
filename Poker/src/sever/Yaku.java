package sever;

public enum Yaku{
	NO_PAIR(1),
	ONE_PAIR(2),
	TWO_PAIR(3),
	THREE_CARDS(4),
	STREIGHT(5),
	FLUSH(6),
	FULLHOUSE(7),
	STREIGHT_FLUSH(8),
	FOUR_CARDS(9),
	ROYAL_STREIGHT_FLUSH(10);
	
	private int strength;
	private Yaku(int strength){
		this.strength = strength;
	}
	
	/**
	 * targetと対戦した時の役ベースでの勝敗を返す
	 * @param target
	 * @return
	 */
	public Syohai getResult(Yaku target){
		if(this.strength > target.strength){
			return Syohai.WIN;
		}else if(this.strength == target.strength){
			return Syohai.DRAW;
		}else{
			return Syohai.LOSE;
		}
	}

}
