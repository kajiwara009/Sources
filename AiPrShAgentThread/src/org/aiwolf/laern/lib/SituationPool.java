package org.aiwolf.laern.lib;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.aiwolf.common.data.Role;

public class SituationPool {
	//ハッシュ値とそのSituation
	private Map<Integer, Situation> situations = new HashMap<Integer, Situation>();
	

	public SituationPool() {
		// TODO 自動生成されたコンストラクター・スタブ
	}
	
	
	public void importSituations(String dir, Role role){
		try {
			FileReader fr = new FileReader(dir + "situation/" + role);
			BufferedReader br = new BufferedReader(fr);
			
//			System.out.println("importStart:" + role + " SituationPool");

			// 読み込んだファイルを１行ずつ処理する
			String line;
			StringTokenizer token;
			while ((line = br.readLine()) != null) {
				// 区切り文字","で分割する
				token = new StringTokenizer(line, ",");
				Situation sit = new Situation(token);
				situations.put(sit.hashCode(), sit);
			}
			br.close();
//			System.out.println("importFinish:" + role + " SituationPool");

		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	public void outputSituations(String dir, Role role){
		String completePath = dir + "situation/" + role;
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
		}
		for(Situation situation: situations.values()){
			String str = situation.toDataString();
			pw.println(str);
		}
		pw.close();
	}

	public Map<Integer, Situation> getSituations() {
		return situations;
	}
	
	public Situation getSituation(Situation situation){
		int hash = situation.hashCode();
		if(!situations.containsKey(hash)){
			putSituation(hash, situation);
		}
		return situations.get(hash);
	}
	
	synchronized private void putSituation(int hash, Situation situation){
		if(!situations.containsKey(hash)){
			situations.put(hash, situation);
		}
	}

	public Situation getSituation(int hash){
		return situations.get(hash);
	}

	
	public void setSituations(Map<Integer, Situation> situations) {
		this.situations = situations;
	}
	
	

}
