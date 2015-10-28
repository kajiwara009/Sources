package com.gmail.kajiwara009.network;

import java.util.List;

public class UndirectedNode implements Node{
	private String id;
	
	private List<Node> link;

	public UndirectedNode(String id) {
		this.id = id;
	}
	
	@Override
	public String getIdx() {
		return id;
	}

	@Override
	public void setIdx(String idx) {
		this.id = idx;
	}

	@Override
	public List<Node> getLink() {
		return link;
	}

	@Override
	public void setLink(List<Node> link) {
		this.link = link;
	}

	@Override
	public void addLink(Node node) {
		link.add(node);
	}
	
}
