package com.gmail.kajiwara009.network.data;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class Node {
	protected Set<Node> linkNodes = new HashSet<Node>();
	protected String name;
	
	public void addLinkNode(Node node){
		linkNodes.add(node);
	}

	public void removeLinkNode(Node node){
		linkNodes.remove(node);
	}
	
	public int getDegree(){
		return linkNodes.size();
	}
	

	public Set<Node> getLinkNodes() {
		return linkNodes;
	}

	public void setLinkNodes(Set<Node> linkNodes) {
		this.linkNodes = linkNodes;
	}

	public String getName() {
		return name;
	}
	
	public abstract Node clone();

	public void setName(String name) {
		this.name = name;
	}
	
}
