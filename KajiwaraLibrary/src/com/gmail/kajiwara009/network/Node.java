package com.gmail.kajiwara009.network;

import java.util.List;

public interface Node {
	
	public String getIdx();
	public void setIdx(String idx);
	public List<Node> getLink();
	public void setLink(List<Node> link);
	public void addLink(Node node);
	
	
}
