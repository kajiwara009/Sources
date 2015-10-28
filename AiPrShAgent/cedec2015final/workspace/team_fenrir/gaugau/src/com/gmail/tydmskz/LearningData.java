package com.gmail.tydmskz;

import java.util.Map;

import org.aiwolf.common.net.GameInfo;

public class LearningData {

	static LearningData ld = null;
	
	int x = 0;
	public int Test()
	{
		return ++x;
	}
	
	public void Record(Map<Integer, GameInfo> gameInfo)
	{
		//占い師COタイミング
		//狼同士の連携
	}
	
	//シングルトンもどき
	public static LearningData GetInstance()
	{
		if(ld==null)
			ld = new LearningData();
		
		return ld;
	}
}
