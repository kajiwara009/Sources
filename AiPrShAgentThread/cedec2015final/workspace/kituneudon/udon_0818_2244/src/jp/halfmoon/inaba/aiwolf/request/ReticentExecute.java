package jp.halfmoon.inaba.aiwolf.request;

import java.util.ArrayList;

import org.aiwolf.client.lib.Utterance;
import org.aiwolf.common.data.Talk;


/**
 * 行動戦術「寡黙吊り」
 */
public final class ReticentExecute extends AbstractActionStrategy {

	@Override
	public ArrayList<Request> getRequests(ActionStrategyArgs args) {

		ArrayList<Request> Requests = new ArrayList<Request>();
		Request workReq;


		// 人物毎の推理発言の回数 idx=AgentNum
		int estimateNums[] = new int[ args.agi.gameSetting.getPlayerNum() + 1 ];

		// 全ての発言リストを確認し、人物毎の推理発言数をカウントする
		for( int day = 1; day < args.agi.latestGameInfo.getDay(); day++ ){
			for( Talk talk : args.agi.getTalkList(day) ){

				Utterance utterance = args.agi.getUtterance( talk.getContent() );

				switch( utterance.getTopic() ){
					case ESTIMATE:
						// 対象が存在しない者
						if( !args.agi.isValidAgentNo( utterance.getTarget().getAgentIdx() ) ){
							continue;
						}
						// 対象が自分
						if( utterance.getTarget().getAgentIdx() == talk.getAgent().getAgentIdx() ){
							continue;
						}

						// 推理発言数をカウント
						estimateNums[talk.getAgent().getAgentIdx()]++;

					case AGREE:
						// 発言の意味を取得
						Utterance refutterance = args.agi.getMeanFromAgreeTalk( talk, 0 );

						if( refutterance != null ){
							switch( refutterance.getTopic() ){
								case ESTIMATE:
									// 対象が存在しない者
									if( !args.agi.isValidAgentNo( refutterance.getTarget().getAgentIdx() ) ){
										continue;
									}
									// 対象が自分
									if( refutterance.getTarget().getAgentIdx() == talk.getAgent().getAgentIdx() ){
										continue;
									}

									// 推理発言数をカウント
									estimateNums[talk.getAgent().getAgentIdx()]++;

								default:
									break;
							}
						}

						break;
					default:
				}
			}
		}


		// 推理発言の少ないプレイヤーは価値を下げる
		for( int i = 1; i <= args.agi.gameSetting.getPlayerNum(); i++ ){
			if( estimateNums[i] < args.agi.latestGameInfo.getDay() ){
				workReq = new Request( i );
				workReq.vote = 1.05;
				workReq.guard = 0.95;

				// 行動要求の登録
				Requests.add(workReq);
			}
		}


		return Requests;

	}

}
