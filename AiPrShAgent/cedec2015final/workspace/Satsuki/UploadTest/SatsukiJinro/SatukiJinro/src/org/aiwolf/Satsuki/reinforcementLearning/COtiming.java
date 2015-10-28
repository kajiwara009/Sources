package org.aiwolf.Satsuki.reinforcementLearning;
/**
 * Day_対抗_回避
 * @author kajiwarakengo
 *
 */
public enum COtiming {
	ONE_TRUE_TRUE(1, true, true),
	ONE_TRUE_FALSE(1, true, false),
	ONE_FALSE_TRUE(1, false, true),
	ONE_FALSE_FALSE(1, false, false),

	TWO_TRUE_TRUE(2, true, true),
	TWO_TRUE_FALSE(2, true, false),
	TWO_FALSE_TRUE(2, false, true),
	TWO_FALSE_FALSE(2, false, false),
	
	THREE_TRUE_TRUE(3, true, true),
	THREE_TRUE_FALSE(3, true, false),
	THREE_FALSE_TRUE(3, false, true),
	THREE_FALSE_FALSE(3, false, false),
	
	NO_TRUE_TRUE(0, true, true),
	NO_TRUE_FALSE(0, true, false),
	NO_FALSE_TRUE(0, false, true),
	NO_FALSE_FALSE(0, false, false);
	
	private int day;
	private boolean against;
	private boolean avoidExecuted;
	
	private COtiming(int day, boolean against, boolean findWolf){
		this.day = day;
		this.against = against;
		this.avoidExecuted = findWolf;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public boolean isAgainst() {
		return against;
	}

	public void setAgainst(boolean against) {
		this.against = against;
	}

	public boolean isFindWolf() {
		return avoidExecuted;
	}

	public void setFindWolf(boolean findWolf) {
		this.avoidExecuted = findWolf;
	}
	
}
