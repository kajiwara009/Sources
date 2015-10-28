package org.aiwolf.Satsuki.reinforcementLearning;

import java.io.Serializable;
//import java.util.ArrayList;
//import java.util.List;
import java.util.Random;

/**
 * 要素は
 * isVoted =　//投票対象になる
 * isWolfJudged = //人狼だと占われた
 * hasFoundWolf = //人狼を見つけた
 * isAgainst = //対抗が出てきた
 * day
 * @author kajiwarakengo
 *
 */
public class COtimingNeo implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6887157948863211607L;



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + day;
		result = prime * result + (hasFoundWolf ? 1231 : 1237);
		result = prime * result + (isAgainst ? 1231 : 1237);
		result = prime * result + (isVoted ? 1231 : 1237);
		result = prime * result + (isWolfJudged ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		COtimingNeo other = (COtimingNeo) obj;
		if (day != other.day)
			return false;
		if (hasFoundWolf != other.hasFoundWolf)
			return false;
		if (isAgainst != other.isAgainst)
			return false;
		if (isVoted != other.isVoted)
			return false;
		if (isWolfJudged != other.isWolfJudged)
			return false;
		return true;
	}
	
	private boolean isVoted = false,//投票対象になる
					isWolfJudged = false,//人狼だと占われた
					hasFoundWolf = false,//人狼を見つけた
					isAgainst = false;//対抗が出てきた
	
	private int day = 0;//日数(max = 5, 0なら日数によるカミングアウトはしない)

	

	public COtimingNeo(){}
	
	public COtimingNeo(int day, boolean isVoted, boolean isWolfJudged, boolean hasFoundWolf, boolean isAgainst){
		this.day = day;
		this.isVoted = isVoted;
		this.isWolfJudged = isWolfJudged;
		this.hasFoundWolf = hasFoundWolf;
		this.isAgainst = isAgainst;
	}
	
	
	/**
	 * ランダムにインスタンスを取得
	 * @return
	 */
	public static COtimingNeo getRandomTiming(){
		COtimingNeo ans = new COtimingNeo();
		ans.day = new Random().nextInt(7);
		ans.isVoted = new Random().nextBoolean();
		ans.isWolfJudged = new Random().nextBoolean();
		ans.hasFoundWolf = new Random().nextBoolean();
		ans.isAgainst = new Random().nextBoolean();
		return ans;
	}
	
	public boolean doComingout(){
		return day == 6? false: true;
	}
	

	public boolean isVoted() {
		return isVoted;
	}

	public boolean isWolfJudged() {
		return isWolfJudged;
	}

	public boolean isHasFoundWolf() {
		return hasFoundWolf;
	}

	public boolean isAgainst() {
		return isAgainst;
	}

	public int getDay() {
		return day;
	}
	

}
