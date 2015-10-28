package jp.ac.shibaura_it.ma15082;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;


public class Tools {

	private Tools(){}
	
	public static int rand(int max){
		Random rand=new Random();
		return rand.nextInt(max);
	}
	
	
	public static double random(){
		return Math.random();
	}
	
	public static double random(double max){
		return random()*max;
	}
	
	
	public static <T> void sort(ListMap<T,Double> target,boolean flag){
		
		if(flag){
		for(int i=1;i<target.size();i++){
			for(int j=0;j<target.size()-i;j++){
				if(target.getValue(j)<target.getValue(j+1)){
					target.exchange(j, j+1);
				}
			}
		}
		}
		else{
			for(int i=1;i<target.size();i++){
				for(int j=0;j<target.size()-i;j++){
					if(target.getValue(j)>target.getValue(j+1)){
						target.exchange(j, j+1);
					}
				}
			}
		}
		return;
	}
	
	
	public static <T> List<T> limit(List<T> target,int limit){
		int t;
		while(target.size()>limit){
			t=Tools.rand(target.size());
			target.remove(t);
		}
		
		return target;
				
	}
	
	

	public static <K> K selectMaxKey(ListMap<K,Double> ss){
		K ret=null;
		double max=0;
		double value;
		for(K key : ss.keyList()){
			value=ss.get(key);
			if(max<value){
				max=value;
				ret=key;
			}
		}
		return ret;
	}
	

	public static <K> K selectKey(ListMap<K,Double> ss){
		K ret=null;
		double sum=0;
		for(Double s : ss.valueList()){
			sum+=s;
		}
		if(sum<0.0){
			return ret;
		}
		double level=random(sum);
		for(int i=0;i<ss.size();i++){
			if(level<ss.getValue(i) && ss.getValue(i)>0){
				ret=ss.getKey(i);
				break;
			}
			else{
				level-=ss.getValue(i);
			}
		}
		
		return ret;
	}
	
	
	public static ListMap<Integer,Double> clustering(List<Double> level){
		Collections.sort(level);
		ListMap<Integer,Double> ret=new ListMap<Integer,Double>();
		List<Integer> rem=new ArrayList<Integer>();
		int zero=0;
		while(
				zero<level.size() && 
				level.get(zero)<=0.0
				){
			zero++;
		}
		if(zero>=level.size()){
			return null;
		}
		if(zero!=0){
			rem.add(0);
		}
		for(int i=zero;i<=level.size();i++){
			rem.add(i);
		}
		for(int i=0;i<zero;i++){
			ret.put(i,0.0);
		}

		List<Double> dif=new ArrayList<Double>();
		for(int i=1;i<rem.size()-1;i++){
			dif.add(ward(level,rem.get(i-1),rem.get(i),rem.get(i+1)));
		}
		while(dif.size()>0){
			int index=0;
			double min=dif.get(0);
			for(int i=1;i<dif.size();i++){
				if(dif.get(i)<min){
					index=i;
					min=dif.get(i);
				}
			}
			
			if((index-1)>=0 && (index+2)<rem.size()){
				dif.set(index-1, ward(level,rem.get(index-1),rem.get(index),rem.get(index+2)));
			}
			if((index)>=0 && (index+3)<rem.size()){
				dif.set(index+1, ward(level,rem.get(index),rem.get(index+2),rem.get(index+3)));
			}
			int key=rem.remove(index+1);
			double value=dif.remove(index);
			ret.put(key, value);
		}
		
		return ret;
	}
	
	public static <T> void unit(ListMap<T,Double> v){
		double sum=0;
		for(double s : v.valueList()){
			sum+=s;
		}
		if(sum==0.0){
			return;
		}
		for(T k : v.keyList()){
			v.put(k,v.get(k)/sum);
		}
		return;
	}


	public static <T> void cut(ListMap<T,Double> v,int num){
		List<Integer> list=new ArrayList<Integer>();
		
		while(list.size()<num){
			double max=0;
			int index=-1;
			for(int i=0;i<v.size();i++){
				if(list.contains(i)){
					continue;
				}
				if(v.getValue(i)>max){
					index=i;
					max=v.getValue(i);
				}
			
			}
			
			if(index<0){
				break;
			}
			list.add(index);
		}
		
		for(int i=0;i<v.size();i++){
			if(!list.contains(i)){
				v.put(v.getKey(i), 0.0);
			}
		}
		
		return;		
	}
	
	public static <T> boolean cutList(ListMap<T,Double> list){
		return cutList(list,5,2);
	}
	
	public static <T> boolean cutList(ListMap<T,Double> list,int cluster,int num){
		int size=calcLimitSize(new ArrayList<Double>(list.valueList()),cluster,num);
		if(size<0){
			return false;
		}
		Tools.cut(list, size);
		return true;
	}
	
		
	public static int calcLimitSize(List<Double> level,int cluster,int num){
		ListMap<Integer,Double> ans=Tools.clustering(level);
		if(ans==null){
			return -1;
		}
		List<Integer> temp=ans.keyList();
		List<Double> val=ans.valueList();
		
		int c_max=1;
		for(int i=0;i<val.size();i++){
			if(val.get(i)>0.0){
				c_max++;
			}
		}
		if(c_max<cluster){
			cluster=c_max;
		}
		if(cluster<=num){
			num=c_max-1;
		}
		int[] max=new int[cluster];
		for(int i=1;i<cluster;i++){
			max[i]=temp.get(temp.size()-i);
		}
		
		for(int i=1;i<cluster;i++){
			for(int j=i+1;j<cluster;j++){
				if(max[i]<max[j]){
					int buf=max[i];
					max[i]=max[j];
					max[j]=buf;
				}
			}
		}
		return level.size()-max[num];
		
	}
	
	
	
	

	
	public static double calcCertain(double a,double b,double r){
		double f=3*r*a*(1-a);
		return f*b+(1-f)*a;
	}
	
	

	
	public static double twice(double t){
		return t*t;
	}
	
	
	
	
	public static double ward(List<Double> level,int from,int mid,int to){
		double score=0;
		double m1=0,m2=0,m=0;
		for(int i=from;i<mid;i++){
			m1+=level.get(i);
		}
		for(int i=mid;i<to;i++){
			m2+=level.get(i);
		}
		m=(m1+m2)/(to-from);
		m1/=(mid-from);
		m2/=(to-mid);
		
		for(int i=from;i<to;i++){
			double temp1=level.get(i)-m;
			double temp2=level.get(i);
			if(i<mid){
				temp2-=m1;
			}
			else{
				temp2-=m2;
			}
			
			score+=twice(temp1)-twice(temp2);
			
			
		}
		
		
		return score;
	}
	
	
	


	
	
}
