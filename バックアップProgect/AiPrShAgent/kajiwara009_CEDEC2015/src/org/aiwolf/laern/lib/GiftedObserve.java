package org.aiwolf.laern.lib;

public class GiftedObserve {
	private boolean isAlive;
	//人狼だと判定した数．3人以上の場合は3で固定
	private int wolfJudgeNum;
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (isAlive ? 1231 : 1237);
		result = prime * result + wolfJudgeNum;
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
		GiftedObserve other = (GiftedObserve) obj;
		if (isAlive != other.isAlive)
			return false;
		if (wolfJudgeNum != other.wolfJudgeNum)
			return false;
		return true;
	}


	public boolean isAlive() {
		return isAlive;
	}


	public void setAlive(boolean isAlive) {
		this.isAlive = isAlive;
	}


	public int getWolfJudgeNum() {
		return wolfJudgeNum;
	}


	public void setWolfJudgeNum(int wolfJudgeNum) {
		this.wolfJudgeNum = wolfJudgeNum;
	}


	public GiftedObserve() {
		// TODO 自動生成されたコンストラクター・スタブ
	}
	
	public GiftedObserve(boolean isAlive, int wolfJudgeNum){
		this.isAlive = isAlive;
		this.wolfJudgeNum = wolfJudgeNum;
	}

}
