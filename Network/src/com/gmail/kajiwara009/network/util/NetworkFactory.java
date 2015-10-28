package com.gmail.kajiwara009.network.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

import com.gmail.kajiwara009.network.data.UndirectNode;
import com.gmail.kajiwara009.network.data.UndirectedNetwork;

public class NetworkFactory {
	
	/**
	 * nodeName1 nodeName2
	 * ...
	 * ...
	 * 
	 * @param filePath
	 * @return
	 */
	public static UndirectedNetwork makeUndirectedNetwork(String filePath){
		UndirectedNetwork network = new UndirectedNetwork();
		BufferedReader br = null;
		try {
			FileReader fr = new FileReader(filePath);
			br = new BufferedReader(fr);
			String line;
			StringTokenizer token;
			while ((line = br.readLine()) != null) {
				token = new StringTokenizer(line, ",");
				if(token.countTokens() != 2){
					return null;
				}
				String nodeName1 = token.nextToken();
				String nodeName2 = token.nextToken();
				
				if(!network.containNode(nodeName1)){
					UndirectNode node = new UndirectNode();
					node.setName(nodeName1);
					network.addNode(node);
				}
				if(!network.containNode(nodeName2)){
					UndirectNode node = new UndirectNode();
					node.setName(nodeName2);
					network.addNode(node);
				}
				UndirectNode node1 = (UndirectNode)network.getNode(nodeName1);
				UndirectNode node2 = (UndirectNode)network.getNode(nodeName2);
				node1.addLinkNode(node2);
				node2.addLinkNode(node1);
			}
			br.close();
			return network;

		} catch (IOException ex) {
			ex.printStackTrace();
			return null;
		}
	}
}
