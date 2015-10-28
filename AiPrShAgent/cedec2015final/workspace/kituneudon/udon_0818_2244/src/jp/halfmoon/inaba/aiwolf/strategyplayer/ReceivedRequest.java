package jp.halfmoon.inaba.aiwolf.strategyplayer;

import java.util.ArrayList;
import java.util.List;

import jp.halfmoon.inaba.aiwolf.request.AbstractActionStrategy;
import jp.halfmoon.inaba.aiwolf.request.Request;

/**
 * 各行動戦術クラスから受け取った行動要求
 */
public class ReceivedRequest {

	/** 行動要求の内容 */
	public Request request;

	/** 要求を行った戦術 */
	public AbstractActionStrategy strategy;

	/** 要求の重み(要求係数^要求の重み)　全種の要求に対してかかる */
	public double weight = 1.0;


	public ReceivedRequest(Request request, AbstractActionStrategy strategy){
		this.request = request;
		this.strategy = strategy;
	}

	public ReceivedRequest(Request request, AbstractActionStrategy strategy, double weight){
		this.request = request;
		this.strategy = strategy;
		this.weight = weight;
	}

	public static List<ReceivedRequest> newRequests(ArrayList<Request> requests, AbstractActionStrategy strategy){

		ArrayList<ReceivedRequest> rguesses = new ArrayList<ReceivedRequest>();

		for(Request request: requests){
			ReceivedRequest rrequest = new ReceivedRequest(request, strategy);
			rguesses.add(rrequest);
		}

		return rguesses;

	}

	public static List<ReceivedRequest> newRequests(ArrayList<Request> requests, AbstractActionStrategy strategy, double weight){

		ArrayList<ReceivedRequest> rguesses = new ArrayList<ReceivedRequest>();

		for(Request request: requests){
			ReceivedRequest rrequest = new ReceivedRequest(request, strategy, weight);
			rguesses.add(rrequest);
		}

		return rguesses;

	}

}
