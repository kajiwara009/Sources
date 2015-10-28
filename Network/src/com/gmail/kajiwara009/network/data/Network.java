package com.gmail.kajiwara009.network.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public interface Network {
	//List<Node> nodes = new ArrayList<Node>();
	
	public List<Node> getNodes();
	public boolean containNode(Node node);
	public boolean containNode(String nodeName);
	
	public Node getNode(String nodeName);
	public void addNode(Node node);
	public void addNodes(Collection<Node> nodes);
	
	public String output();
}

//クラスタ係数・ネットワーク密度