package sever;

public enum Phase {
	PRE_FLOP, FLOP, TURN, RIVER, END;
	
	public Phase getNextPhase(){
		switch (this) {
		case PRE_FLOP:
			return FLOP;
		case FLOP:
			return TURN;
		case TURN:
			return RIVER;
		case RIVER:
			return END;
		default:
			return null;
		}
	}

}
