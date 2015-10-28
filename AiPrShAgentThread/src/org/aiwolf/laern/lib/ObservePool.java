package org.aiwolf.laern.lib;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;

import javax.sql.rowset.spi.SyncResolver;

import org.aiwolf.common.data.Role;

public class ObservePool {
	private Map<Integer, Observe> observes = new HashMap<Integer, Observe>();

	public ObservePool() {
	}
	
	public void importObserves(String dir, Role role, SituationPool pool){
		try {
			//POOLkarayobidasi
			// ファイルを読み込む
			FileReader fr = new FileReader(dir + "observe/" + role);
			BufferedReader br = new BufferedReader(fr);
			
//			System.out.println("importStart:" + role + " ObservePool");
			// 読み込んだファイルを１行ずつ処理する
			String line;
			StringTokenizer token;
			while ((line = br.readLine()) != null) {

				// 区切り文字","で分割する
				token = new StringTokenizer(line, ",");
				Observe obs = new Observe(token, pool);
					observes.put(obs.hashCode(), obs);
			}
			br.close();
//			System.out.println("importFinish:" + role + " ObservePool");

		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * dirは"random/3000/"みたいなもの
	 * @param dir
	 * @param outputFileName
	 * @param role
	 */
	public void outputObserves(String dir, Role role){
		String completePath = dir + "observe/" + role;
		PrintWriter pw = null;
		try {
			File file = new File(completePath);
			//親ディレクトリの作成
			String dirParent = file.getParent();
			new File(dirParent).mkdirs();
			
			FileWriter filewriter = new FileWriter(file);
			BufferedWriter bw = new BufferedWriter(filewriter);
			pw = new PrintWriter(bw);
		} catch (Exception e) {
			e.printStackTrace();
		}
		for(Observe observe: observes.values()){
			String str = observe.toDataString();
			pw.println(str);
		}
		pw.close();
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public Map<Integer, Observe> getObserves() {
		return observes;
	}
	
	public Observe getObserve(Observe observe){
		int hash = observe.hashCode();
		if(!observes.containsKey(hash)){
//			observes.put(hash, observe);
			putObserve(hash, observe);
		}
		return observes.get(hash);
	}
	
	synchronized private void putObserve(int hash, Observe observe){
		if(!observes.containsKey(hash)){
			observes.put(hash, observe);
		}
	}
	
/*	public Observe getObserve(int hash){
		return observes.get(hash);
	}
*/	
	public void addObserve(Observe observe){
		observes.put(observe.hashCode(), observe);
	}

	public void setObserves(Map<Integer, Observe> observes) {
		this.observes = observes;
	}
	
	

}
