package jp.halfmoon.inaba.aiwolf.strategyplayer;

import java.util.ArrayList;
import java.util.List;

public class RequestManager {


	/** 全ての行動要求 */
	public List<ReceivedRequest> allRequest = new ArrayList<ReceivedRequest>();



	/**
	 * 行動要求を追加
	 * @param guess 追加する行動要求
	 */
	public void addRequest(ReceivedRequest guess){

		// 全ての行動要求として追加
		allRequest.add(guess);

	}

	/**
	 * 行動要求を追加
	 * @param guesses 追加する行動要求のリスト
	 */
	public void addRequest(List<ReceivedRequest> requests){

		// 単体追加メソッドを使い、１個ずつ追加
		for(ReceivedRequest request: requests){
			addRequest(request);
		}

	}









}
