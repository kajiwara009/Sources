package com.gmail.kajiwara009.network.bin;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.gmail.kajiwara009.network.data.Network;
import com.gmail.kajiwara009.network.data.Node;
import com.gmail.kajiwara009.network.data.UndirectNode;
import com.gmail.kajiwara009.network.data.UndirectedNetwork;
import com.gmail.kajiwara009.network.util.NetworkFactory;
import com.gmail.kajiwara009.network.util.NetworkIndexCulculator;

public class NetworkIndexViewer {
	public static void main( String[] args){
		UndirectedNetwork network = NetworkFactory.makeUndirectedNetwork("network2.csv");
		
		List<Node> nodes = new ArrayList<Node>();
		for(Node node: network.getNodes()){
			if(new Random().nextBoolean()){
				nodes.add(node);
			}
		}
		
		System.out.println(network.output());
		NetworkIndexCulculator culc = new NetworkIndexCulculator(network);
		Network ne = culc.getPartialNetwork(nodes);
		
		System.out.println("平均次数:" + culc.getAverageDegree());
		System.out.println("平均経路長" + culc.getAverageNetworkPathLength());
		System.out.println("クラスタ係数:" + culc.getClusterCoefficient());
		System.out.println("リンク密度:" + culc.getDensity());
		System.out.println("直径:" + culc.getDiameter());
		System.out.println("最大次数:" + culc.getMaxDegree());
	}

}
