package com.gmail.kajiwara009.network.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PreDestroy;

import com.gmail.kajiwara009.network.data.Network;
import com.gmail.kajiwara009.network.data.Node;
import com.gmail.kajiwara009.network.data.UndirectedNetwork;

public class NetworkIndexCulculator {
	private Network network;
	
	public NetworkIndexCulculator(Network network){
		this.network = network;
	}
	
	/**
	 * 平均次数
	 * @return
	 */
	public double getAverageDegree(){
		double sum = 0;
		List<Double> degrees = new ArrayList<Double>();
		for(Node node: network.getNodes()){
			double degree = (double)node.getDegree();
			sum += degree;
		}
		return sum / (double)network.getNodes().size();
	}
	
	/**
	 * 最大次数
	 * @return
	 */
	public int getMaxDegree(){
		int max = Integer.MIN_VALUE;
		for(Node node: network.getNodes()){
			int tmp = node.getDegree();
			max = Math.max(max, tmp);
		}
		return max;
	}
	
	/**
	 * 平均経路長（特定ノードについて）
	 * @param node
	 * @return
	 */
	public double getAveragePathLength(Node node){
		double sum = 0;
		List<Node> nodes = network.getNodes();
		
		Map<Node, Integer> pathLengths = getPathLengths(node);
		for(Node nodeTo: nodes){
			if(node.equals(nodeTo)){
				continue;
			}
			sum += pathLengths.get(nodeTo);
		}
		
		return sum / Math.max((double)(nodes.size() - 1), 1.0);
	}
	
	/**
	 * 1ノードにおける各ノードへの経路長
	 * @param nodeFrom
	 * @return
	 */
	public Map<Node, Integer> getPathLengths(Node nodeFrom){
//		Map<Node, Integer> pathLengths = new HashMap<Node, Integer>();
		return PathLengthCulculator.dijkstraMethod(network, nodeFrom);
	}
	
	/**
	 * 平均経路長（全ノード）
	 * @return
	 */
	public double getAverageNetworkPathLength(){
		double sum = 0;
		for(Node node: network.getNodes()){
			sum += getAveragePathLength(node);
		}
		return sum / (double)network.getNodes().size();
	}
	
	/**
	 * 直径
	 * @return
	 */
	public int getDiameter(){
		int maxPathLength = Integer.MIN_VALUE;
		for(Node nodeFrom: network.getNodes()){
			Map<Node, Integer> pathLengths = PathLengthCulculator.dijkstraMethod(network, nodeFrom);
			for(Node nodeTo: network.getNodes()){
				if(pathLengths.get(nodeTo) > maxPathLength){
					maxPathLength = pathLengths.get(nodeTo);
				}
			}
		}
		 return maxPathLength;
	}
	
	/**
	 * リンク密度
	 * @return
	 */
	public double getDensity(){
		List<Node> nodes = network.getNodes();
		int nodeSum = nodes.size();
		
		int linkNum = 0;
		for(Node node: nodes){
			linkNum += node.getLinkNodes().size();
		}
		return (double)linkNum / (double)(nodeSum * (nodeSum - 1));
	}
	
	/**
	 * クラスタ係数
	 * @return
	 */
	public double getClusterCoefficient(){
		List<Double> clusterCoefs = new ArrayList<Double>();
		for(Node node: network.getNodes()){
			clusterCoefs.add(getSoleClusterCoefficient(node));
		}
		double sum = 0;
		for(Double val: clusterCoefs){
			sum += val;
		}
		return sum / (double)network.getNodes().size();
	}
	
	/**
	 * クラスタ係数(1ノード分)
	 * @param node
	 * @return
	 */
	public double getSoleClusterCoefficient(Node node){
		Set<Node> links = node.getLinkNodes();
		int linkNum = links.size();
		int clusterNum = 0;
		for(Node linkNode1: links){
			for(Node linkNode2: links){
				if(linkNode1.getLinkNodes().contains(linkNode2)){
					clusterNum ++;
				}
			}
		}
		double sum = Math.max(1, (double)( linkNum * (linkNum - 1)));
		return (double)(clusterNum / 2) / sum;
	}
	
	/**
	 * 次数分布
	 * @return Map<次数, ノード数>
	 */
	public Map<Integer, Integer> getDegreeDistribution(){
		Map<Integer, Integer> degreeDist = new HashMap<Integer, Integer>();
		for(Node node: network.getNodes()){
			int degree = node.getDegree();
			if(!degreeDist.containsKey(degree)){
				degreeDist.put(degree, 0);
			}
			int preSum = degreeDist.get(degree);
			degreeDist.put(degree, preSum + 1);
		}
		return degreeDist;
	}
	
	/**
	 * クリーク
	 * @return
	 */
	public List<Network> getCreek(){
		return getK_plex(1);
	}
	
	/**
	 * K-クリーク
	 * 最大経路がk以下となる中で最大のネットワーク  の集合
	 * @param k
	 * @return
	 */
	public List<Network> getK_Creek(int k){
		//TODO
		return null;
	}
	
	/**
	 * k-クラン
	 * ｋークランのコミュニティの外のノードは使わないVer
	 * @param k
	 * @return
	 */
	public List<Network> getK_Clan(int k){
		return null;
	}
	
	/**
	 * k-プレックス
	 * 同じ集合内のノードは全て，n-k以上のリンクを持つ．っていう集合たち
	 * @param k
	 * @return
	 */
	public List<Network> getK_plex(int k){
		//TODO
		return null;
	}
	
	/**
	 * ネットワーク上のモチーフ
	 * @return
	 */
	public Map<Network, Integer> getMotif(){
		// TODO
		return null;
	}

	/**
	 * 引数のノードの部分ネットワーク
	 * @param nodes
	 * @return
	 */
	public Network getPartialNetwork(List<Node> nodes){
		Network net = new UndirectedNetwork();
		Class<? extends Network> c = this.network.getClass();
		try {
			net = c.newInstance();
			net.getNodes().clear();
		} catch (InstantiationException e) {
			e.printStackTrace();
			return null;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return null;
		}
		
		int n = net.getNodes().size();
		for(Node node: nodes){
			Node clone = node.clone();
			for(Node link: node.getLinkNodes()){
				if(!nodes.contains(link)){
					clone.removeLinkNode(link);
				}
			}
			net.addNode(clone);
		}
		return net;
	}
}
