package com.gmail.kajiwara009.collectionOperation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import java.util.Set;

public class CollectionOperator {

	public CollectionOperator() {
		// TODO 自動生成されたコンストラクター・スタブ
	}
	
	public static <T extends Comparable<T>> T getMaxValue(Collection<T> collection){
		
		if(collection.size() == 0){
			return null;
		}
		
		T max = null;
		List<T> maxCollection = new ArrayList<T>();
		for (Iterator iterator = collection.iterator(); iterator.hasNext();) {
			T t = (T) iterator.next();
			if(maxCollection.size() == 0 || t.compareTo(max) == 0){
				maxCollection.add(t);
				max = t;
			}else if(t.compareTo(max) > 0){
				maxCollection.clear();
				maxCollection.add(t);
				max = t;
			}
		}
		Collections.shuffle(maxCollection);
		return maxCollection.get(0);
	}
	
	public static <T extends Comparable<T>> T getMinValue(Collection<T> collection){
		
		if(collection.size() == 0){
			return null;
		}
		
		T min = null;
		List<T> minCollection = new ArrayList<T>();
		for (Iterator iterator = collection.iterator(); iterator.hasNext();) {
			T t = (T) iterator.next();
			if(minCollection.size() == 0 || t.compareTo(min) == 0){
				minCollection.add(t);
				min = t;
			}else if(t.compareTo(min) < 0){
				minCollection.clear();
				minCollection.add(t);
				min = t;
			}
		}
		Collections.shuffle(minCollection);
		return minCollection.get(0);
	}
	
	public static <T, C extends Comparable<C>>  T getMaxKey(Map<T, C > map ){
		if(map.size() == 0){
			return null;
		}
		
		C max = null;
		List<T> maxCollection = new ArrayList<T>();
		for(Entry<T, C> set: map.entrySet()){
			T t = set.getKey();
			C c = set.getValue();
			if(maxCollection.size() == 0 || c.compareTo(max) == 0){
				maxCollection.add(t);
				max = c;
			}else if(c.compareTo(max) > 0){
				maxCollection.clear();
				maxCollection.add(t);
				max = c;
			}
		}
		Collections.shuffle(maxCollection);
		return maxCollection.get(0);
	}

	
	public static <T, C extends Comparable<C>>  T getMinKey(Map<T, C > map ){
		if(map.size() == 0){
			return null;
		}
		
		C min = null;
		List<T> minCollection = new ArrayList<T>();
		for(Entry<T, C> set: map.entrySet()){
			T t = set.getKey();
			C c = set.getValue();
			if(minCollection.size() == 0 || c.compareTo(min) == 0){
				minCollection.add(t);
				min = c;
			}else if(c.compareTo(min) < 0){
				minCollection.clear();
				minCollection.add(t);
				min = c;
			}
		}
		Collections.shuffle(minCollection);
		return minCollection.get(0);
	}
	
	public static <T> T getRandom(List<T> list){
		try {
			List<T> tmpList = new ArrayList<T>(list);
			Collections.shuffle(tmpList);
			return tmpList.get(0);
		} catch (ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static <T> T getRandom(Set<T> set){
		try {
			T ans = null;
			int size = set.size();
			int rand = new Random().nextInt(size);
			for(T value: set){
				if(rand <= 0){
					ans = value;
					break;
				}else{
					rand--;
				}
			}
			return ans;
			
		} catch (ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	public static void main(String[] args){
		
		List<Double> list = new ArrayList<>();
		String sample = "goiagnaionriogieorajgbuivreugryvhnfweohvruweoaopo";
		for(int i = 0; i < 10; i++){
			//Double d = Math.random();
			Double d = 1.0;
			list.add(d);
		}
		Double max = getMaxValue(list);
		System.out.println(max + " " +  list.get(0).equals(max));
	}
	

}
