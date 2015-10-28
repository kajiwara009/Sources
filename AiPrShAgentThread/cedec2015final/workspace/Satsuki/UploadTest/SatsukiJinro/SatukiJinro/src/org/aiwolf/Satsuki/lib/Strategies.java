package org.aiwolf.Satsuki.lib;

import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;

public class Strategies {

	/**
	 * valueMapからルーレ�?��選択をする
	 * Doubleに�??値が�?って�?���?に変換
	 * @param valueMap
	 * @return
	 */
	public static <T>T rouletSelect(Map<T, Double> valueMap){
		double sumValue = 0.0;
		for(Entry<T, Double> set: valueMap.entrySet())
		{
			if(set.getValue() < 0.0)
			{
				continue;
			}
			sumValue += set.getValue();
		}

		double randomValue = new Random().nextDouble() * sumValue;
		for(Entry<T, Double> set: valueMap.entrySet())
		{
			if(set.getValue() < 0.0)
			{
				continue;
			}
			randomValue -= set.getValue();
			if(randomValue <= 0.0){
				return set.getKey();
			}
		}
		return null;
	}

}
