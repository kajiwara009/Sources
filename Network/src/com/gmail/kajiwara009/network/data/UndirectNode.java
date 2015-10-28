package com.gmail.kajiwara009.network.data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class UndirectNode extends Node{
	
	public UndirectNode(){
	}

	@Override
	public Node clone() {
		Node node = new UndirectNode();
		node.linkNodes = new HashSet<Node>(this.linkNodes);
		node.name = this.name;
		return node;
	}
	
}
