package com.gmail.kajiwara009.network.util;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import com.gmail.kajiwara009.network.data.Network;
import com.gmail.kajiwara009.network.data.Node;

public class PathLengthCulculator {
	
	public static Map<Node, Integer> dijkstraMethod(Network network, Node nodeFrom){
		
		Map<Node, Integer> pathLengths = new HashMap<Node, Integer>();
		List<Node> nodes = network.getNodes();
		if(!nodes.contains(nodeFrom)){
			return null;
		}
		
		for(Node node: nodes){
			pathLengths.put(node, Integer.MAX_VALUE);
		}
		pathLengths.put(nodeFrom, 0);
		
		Queue<Node> culcNodes = new ArrayDeque<Node>();
		culcNodes.add(nodeFrom);
		
		while(!culcNodes.isEmpty()){
			Node node = culcNodes.poll();
			Set<Node> links = node.getLinkNodes();
			for(Node linkNode: links){
				int preInt = pathLengths.get(linkNode);
				int postInt = pathLengths.get(node);
				if(postInt + 1 < preInt){
					pathLengths.put(linkNode, postInt + 1);
					culcNodes.add(linkNode);
				}
			}
		}
		
		return pathLengths;
	}
	
	

}
