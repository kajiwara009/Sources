package jp.halfmoon.inaba.aiwolf.request;

import java.util.List;

/**
 * 行動リクエストを表すクラス
 */
public final class Request {

	/* 各行動要求の強度を、1を基準とした倍率で指定する。 */

	public int agentNo;			// エージェント番号

	public double vote = 1;		// 投票(処刑と同義)
	public double attack = 1;	// 襲撃
	public double guard = 1;	// 護衛
	public double inspect = 1;	// 占い

	/**
	 * コンストラクタ
	 * @param agentNo エージェント番号
	 */
	public Request(int agentNo){
		this.agentNo = agentNo;
	}


	/**
	 * 複数の行動要求のうち、投票要求が最大のものを取得する
	 * @param requests
	 * @return
	 */
	public static Request getMaxVoteRequest(List<Request> requests){

		Request result = null;
		double maxValue = Double.NEGATIVE_INFINITY;

		for( Request req : requests ){
			if( req.vote > maxValue ){
				result = req;
				maxValue = req.vote;
			}
		}

		return result;

	}


	/**
	 * 複数の行動要求のうち、占い要求が最大のものを取得する
	 * @param requests
	 * @return
	 */
	public static Request getMaxInspectRequest(List<Request> requests){

		Request result = null;
		double maxValue = Double.NEGATIVE_INFINITY;

		for( Request req : requests ){
			if( req.inspect > maxValue ){
				result = req;
				maxValue = req.inspect;
			}
		}

		return result;

	}


	/**
	 * 複数の行動要求のうち、護衛要求が最大のものを取得する
	 * @param requests
	 * @return
	 */
	public static Request getMaxGuardRequest(List<Request> requests){

		Request result = null;
		double maxValue = Double.NEGATIVE_INFINITY;

		for( Request req : requests ){
			if( req.guard > maxValue ){
				result = req;
				maxValue = req.guard;
			}
		}

		return result;

	}


	/**
	 * 複数の行動要求のうち、襲撃要求が最大のものを取得する
	 * @param requests
	 * @return
	 */
	public static Request getMaxAttackRequest(List<Request> requests){

		Request result = null;
		double maxValue = Double.NEGATIVE_INFINITY;

		for( Request req : requests ){
			if( req.attack > maxValue ){
				result = req;
				maxValue = req.attack;
			}
		}

		return result;

	}




}
