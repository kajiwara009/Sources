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
import java.util.StringTokenizer;

import org.aiwolf.common.data.Role;

public class ObservePool {
	private Map<Integer, Observe> observes = new HashMap<Integer, Observe>();

	public ObservePool() {
	}
	
	public void importObserves(String filePath, String inputFileName, Role role){
		try {
			// ファイルを読み込む
			FileReader fr = new FileReader(filePath + "input/" + role + inputFileName);
			BufferedReader br = new BufferedReader(fr);
			
			System.out.println("importStart:" + role + " ObservePool");
			// 読み込んだファイルを１行ずつ処理する
			String line;
			StringTokenizer token;
			while ((line = br.readLine()) != null) {

				// 区切り文字","で分割する
				token = new StringTokenizer(line, ",");
				Observe obs = new Observe(token);
				observes.put(obs.hashCode(), obs);
			}
			br.close();
			System.out.println("importFinish:" + role + " ObservePool");

		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public void outputObserves(String path, String outputFileName, Role role){
		PrintWriter pw = null;
		try {
			File file = new File(path + "output/" + role + outputFileName);
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
			observes.put(hash, observe);
		}
		return observes.get(hash);
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
