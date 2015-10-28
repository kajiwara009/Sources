package com.github.haretaro.pingwo.brain.util;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class Timer {
	private long start;
	private String name;
	private List<String> results;

	public static final String LOGFILE = "timer.log";


	public Timer(){
		results = new ArrayList<String>();
	}

	public void start(){
		if(Util.DEBUG){
			this.name = "";
			start = System.nanoTime();
		}
	}

	public void start(String name){
		if(Util.DEBUG){
			this.start();
			this.name = name;
		}
	}

	public void end(){
		if(Util.DEBUG){
			double result = (System.nanoTime() - start)/1e6;
			if(result > 60){
				results.add(name + "\t" + result + " <<<");
			}else{
				results.add(name + "\t" + result);
			}
		}
	}

	public void print(){
		if(Util.DEBUG){

			File file = new File(LOGFILE);
			try {
				FileWriter fileWriter = new FileWriter(file,true);
				//FileWriter fileWriter = new FileWriter(file);
				fileWriter.write("hogehogehogehoge\n");
				results.stream()
				.forEach(a->{
					try {
						fileWriter.write(a+"\n");
					} catch (Exception e) {
						e.printStackTrace();
					}
				});
				fileWriter.close();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}
}
