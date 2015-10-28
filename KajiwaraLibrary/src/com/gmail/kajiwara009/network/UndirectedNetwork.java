package com.gmail.kajiwara009.network;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class UndirectedNetwork {
	private List<Node> nodes;
	
	/**
	 * 20
	 * id0
	 * id1
	 * id2
	 * id3
	 * ...
	 * ...
	 * id1 id2 id5 id8
	 * id3 id5 ...
	 * 
	 * @param path
	 * @throws IOException
	 */
	public UndirectedNetwork(String path) throws IOException{
		FileReader fr = new FileReader(path);
		BufferedReader br = new BufferedReader(fr);
		
		String str;
		while((str = br.readLine()) != null){
			String[] res = str.split(",");
			//nodeのインスタンス生成
			for(String id: res){
				if(!includeNode(id)){
					Node node = new UndirectedNode(id);
				}
			}
			//linkの生成
			Node node1 = getNode(res[0]);
			Node node2 = getNode(res[1]);
			
			node1.addLink(node2);
			node2.addLink(node1);
		}
		

	}
	
	public boolean includeNode(String id){
		if(id == null) return false;
		for(Node node: nodes){
			if(id.equals(node.getIdx())){
				return true;
			}
		}
		return false;
	}
	
	public Node getNode(String id){
		if(id == null) return null;
		for(Node node: nodes){
			if(id.equals(node.getIdx())){
				return node;
			}
		}
		return null;
	}
}
