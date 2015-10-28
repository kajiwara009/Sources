package jp.ac.shibaura_it.ma15082;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ListMap<K,V> implements Map<K,V>{
	private List<K> keys;
	private List<V> values;
	
	public ListMap(){
		keys=new ArrayList<K>();
		values=new ArrayList<V>();
	}
	
	public ListMap(int size){
		keys=new ArrayList<K>(size);
		values=new ArrayList<V>(size);
	}
	
	public String toString(){
		StringBuffer sb=new StringBuffer();
		for(int i=0;i<keys.size();i++){
			sb.append("["+keys.get(i)+","+values.get(i)+"]");
		}	
		
		return sb.toString();
	}
	
	

	public ListMap(Map<K,V> map){
		clear();
		for(K key : map.keySet()){
			keys.add(key);
			values.add(map.get(key));
		}
	}

	public K getKey(int i){
		if(keys.size()<=i || i<0){
			return null;
		}
		return keys.get(i);
	}
	public V getValue(int i){
		if(values.size()<=i || i<0){
			return null;
		}
		return values.get(i);
	}	
	public List<K> keyList(){
		return new ArrayList<K>(keys);
	}
	public List<V> valueList(){
		return new ArrayList<V>(values);
	}

	
	@Override
	public void clear() {
		if(keys==null || values==null){
			keys=new ArrayList<K>();
			values=new ArrayList<V>();
		}
		else{
			keys.clear();
			values.clear();
		}
	}

	@Override
	public V put(K key,V value){
		V prev=null;
		int index=keys.indexOf(key);
		if(index < 0){
			keys.add(key);
			values.add(value);			
		}
		else{
			prev=values.get(index);
			values.set(index,value);
		}
		return prev;
	}
	@Override
	public V get(Object key){
		int index=keys.indexOf(key);
		if(index < 0){
			return null;
		}
		else{
			return values.get(index);
		}
	}
	@Override
	public int size(){
		return keys.size();
	}


	@Override
	public boolean containsKey(Object arg0) {
		return keys.contains(arg0);
	}


	@Override
	public boolean containsValue(Object arg0) {
		return values.contains(arg0);
	}


	@Override
	public Set<Entry<K, V>> entrySet() {
		Set<Entry<K,V>> ret=new HashSet<Entry<K,V>>();
		for(int i=0;i<keys.size();i++){
			ret.add(new Pair<K,V>(keys.get(i),values.get(i)));
		}
		return ret;
	}


	@Override
	public boolean isEmpty() {
		return (keys.size()<=0);
	}


	@Override
	public Set<K> keySet() {
		return new HashSet<K>(keys);
	}


	@Override
	public void putAll(Map<? extends K, ? extends V> arg0) {
		for(K key : arg0.keySet()){
			put(key,arg0.get(key));
		}
		
	}


	@Override
	public V remove(Object key) {
		int index=keys.indexOf(key);
		if(index < 0){
			return null;
		}
		else{
			V ret=values.remove(index);
			keys.remove(index);
			return ret;
		}
	}

	public boolean delete(int i){
		if(keys.size()<=i){
			return false;
		}
		values.remove(i);
		keys.remove(i);
		return true;
	}

		
	@Override
	public Collection<V> values() {
		return valueList();
	}

	public boolean exchange(int i,int j){
		K key1,key2;
		V val1,val2;
		key1=keys.get(i);
		key2=keys.get(j);
		val1=values.get(i);
		val2=values.get(j);
		if(key1==null || key2==null  || val1==null || val2==null){
			return false;
		}
		keys.set(j, key1);
		keys.set(i, key2);
		values.set(j,val1);
		values.set(i,val2);
		return true;
	}
	
	
	

	
}
