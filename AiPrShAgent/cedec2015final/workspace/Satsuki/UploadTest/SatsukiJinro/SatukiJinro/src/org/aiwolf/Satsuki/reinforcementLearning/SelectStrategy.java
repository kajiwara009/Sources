package org.aiwolf.Satsuki.reinforcementLearning;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

public class SelectStrategy {

	public SelectStrategy() {
		// TODO 自動生成されたコンストラクター・スタブ
	}
	
	public static <T>T randomSelect(Map<T, Double> map)
	{
		Set<T> set = map.keySet();
		T[] array = (T[]) set.toArray();
		return array[new Random().nextInt(array.length)];
	}
	
	public static <T> T softMaxSelect(Map<T, Double> map, double temp){
		double sum = 0;
		Map<T, Double> boltsmanMap = new HashMap<T, Double>();
		for(Entry<T, Double> set: map.entrySet()){
			boltsmanMap.put(set.getKey(), getBoltsManValue(set.getValue(), temp));
		}
		
		for(Double value: boltsmanMap.values()){
			sum += value;
		}
		double rand = Math.random() * sum;
		T ans = null;
		for(Entry<T, Double> set: boltsmanMap.entrySet()){
			rand -= set.getValue();
			if(rand < 0){
				ans = set.getKey();
				break;
			}
		}
		if(ans == null){
			System.err.println("SelectStrategy: softMaxSelect:おかしい");
		}
		return ans;
	}
	
	private static double getBoltsManValue(double q, double temp){
		return Math.pow(Math.E, q/temp);
	}
	
	
	public static <T>T greedyselect(Map<T, Double> map, double epsilon)
	{
		if(new Random().nextDouble() > epsilon)
		{
			return getMaxDoubleValueKey(map);
		}
		else
		{
			int randomInt = new Random().nextInt(map.size());
			T ans = null;
			for(Entry<T, Double> set: map.entrySet())
			{
				if(randomInt == 0)
				{
					ans = set.getKey();
					break;
				}
				else
				{
					randomInt--;
				}
			}
			return ans;
		}
	}

	/**
	 * ValueがDoubleであるMapについて，その値が最大となるKeyを返す
	 * @param map
	 * @return
	 */
	public static <T>T getMaxDoubleValueKey(Map<T, Double> map){
		T maxValueT = null;
		double maxValue = -10000.0;

		double randValue = new Random().nextDouble();
		for(Entry<T, Double> set: map.entrySet()){
			if(set.getValue() > maxValue){
				maxValueT = set.getKey();
				maxValue = set.getValue();
			}else if(set.getValue() == maxValue){
				double newRand = new Random().nextDouble();
				if(randValue < newRand){
					maxValueT = set.getKey();
					maxValue = set.getValue();
					randValue = newRand;
				}
			}
		}
		return maxValueT;
	}

	public static <T>T getMaxIntValueKey(Map<T, Integer> map){
		Map<T, Double> parsedDoubleMap = new HashMap<T, Double>();

		for(Entry<T, Integer> set: map.entrySet()){
			parsedDoubleMap.put(set.getKey(), (double)set.getValue());
		}
		return getMaxDoubleValueKey(parsedDoubleMap);
	}

	/**
	 * ValueがDoubleであるMapについて，その値が最小となるKeyを返す
	 * @param map
	 * @return
	 */
	public static <T>T getMinDoubleValueKey(Map<T, Double> map){
		T minValueT = null;
		double minValue = 10000.0;

		double randValue = new Random().nextDouble();
		for(Entry<T, Double> set: map.entrySet()){
			if(set.getValue() < minValue){
				minValueT = set.getKey();
				minValue = set.getValue();
			}else if(set.getValue() == minValue){
				double newRand = new Random().nextDouble();
				if(randValue > newRand){
					minValueT = set.getKey();
					minValue = set.getValue();
					randValue = newRand;
				}
			}
		}
		return minValueT;
	}

	public static <T>T getMinIntValueKey(Map<T, Integer> map){
		Map<T, Double> parsedDoubleMap = new HashMap<T, Double>();

		for(Entry<T, Integer> set: map.entrySet()){
			parsedDoubleMap.put(set.getKey(), (double)set.getValue());
		}
		return getMinDoubleValueKey(parsedDoubleMap);
	}


}
