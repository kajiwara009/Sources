package com.gmail.kajiwara009.network.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UndirectedNetwork implements Network {
	private List<Node> nodes = new ArrayList<Node>();
	
	
	public void addNode(Node node){
		nodes.add(node);
	}
	
	public void addNodes(Collection<Node> nodes){
		nodes.addAll(nodes);
	}

	@Override
	public List<Node> getNodes() {
		return nodes;
	}

	@Override
	public boolean containNode(Node node) {
		if(node == null){
			return false;
		}
		for(Node n: nodes){
			if(node.equals(n)){
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean containNode(String nodeName) {
		for(Node node: nodes){
			if(node.getName().equals(nodeName)){
				return true;
			}
		}
		return false;
	}

	@Override
	public Node getNode(String nodeName) {
		for(Node node: nodes){
			if(node.getName().equals(nodeName)){
				return node;
			}
		}
		return null;
	}

	@Override
	public String output() {
		String str = "";
		Set<Node> finishedNodes = new HashSet<Node>();
		
		for(Node node: nodes){
			for(Node link: node.getLinkNodes()){
				if(!finishedNodes.contains(link)){
					str += node.getName() + "," + link.getName() + "\n";
				}
			}
			finishedNodes.add(node);
		}
		return str.substring(0, str.length() - 1);
//		return str;
	}
	
	
	
}
