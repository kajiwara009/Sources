package jp.halfmoon.inaba.aiwolf.request;

import java.util.ArrayList;
import java.util.List;

import jp.halfmoon.inaba.aiwolf.strategyplayer.ReceivedRequest;





public final class AnalysisOfRequest {

	/** 各エージェント単体に対する行動要求 */
	private List<List<ReceivedRequest>> requestForSingleAgent = new ArrayList<List<ReceivedRequest>>();

	/** 各エージェントに対する行動要求の集計結果 */
	public ArrayList<Request> TotalRequest;


	public AnalysisOfRequest(int agentNum, List<ReceivedRequest> requests){

		for( int i = 0; i <= agentNum; i++ ){
			requestForSingleAgent.add(new ArrayList<ReceivedRequest>());
		}

		// エージェント毎に集計
		TotalRequest = new ArrayList<Request>();
		for( int i = 1; i <= agentNum; i++ ){
			TotalRequest.add( new Request(i) );
		}
		for( ReceivedRequest request : requests ){
			int AgentNo = request.request.agentNo;
			if( AgentNo >= 1 && AgentNo <= agentNum ){
				Request workReq = TotalRequest.get(AgentNo - 1);

				workReq.vote *= Math.pow(request.request.vote, request.weight);
				workReq.inspect *= Math.pow(request.request.inspect, request.weight);
				workReq.guard *= Math.pow(request.request.guard, request.weight);
				workReq.attack *= Math.pow(request.request.attack, request.weight);
			}
		}

	}



	/**
	 * 投票要求が最大のエージェントの集計済行動要求を取得する
	 * @return
	 */
	public Request getMaxVoteRequest(){

		Request result = null;
		double maxValue = Double.NEGATIVE_INFINITY;

		for( Request req : TotalRequest ){
			if( req.vote > maxValue ){
				result = req;
				maxValue = req.vote;
			}
		}

		return result;

	}


	/**
	 * 占い要求が最大のエージェントの集計済行動要求を取得する
	 * @return
	 */
	public Request getMaxInspectRequest(){

		Request result = null;
		double maxValue = Double.NEGATIVE_INFINITY;

		for( Request req : TotalRequest ){
			if( req.inspect > maxValue ){
				result = req;
				maxValue = req.inspect;
			}
		}

		return result;

	}


	/**
	 * 護衛要求が最大のエージェントの集計済行動要求を取得する
	 * @return
	 */
	public Request getMaxGuardRequest(){

		Request result = null;
		double maxValue = Double.NEGATIVE_INFINITY;

		for( Request req : TotalRequest ){
			if( req.guard > maxValue ){
				result = req;
				maxValue = req.guard;
			}
		}

		return result;

	}


	/**
	 * 襲撃要求が最大のエージェントの集計済行動要求を取得する
	 * @return
	 */
	public Request getMaxAttackRequest(){

		Request result = null;
		double maxValue = Double.NEGATIVE_INFINITY;

		for( Request req : TotalRequest ){
			if( req.attack > maxValue ){
				result = req;
				maxValue = req.attack;
			}
		}

		return result;

	}




}
