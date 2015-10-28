package jp.halfmoon.inaba.aiwolf.request;

import java.util.ArrayList;

import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.net.GameInfo;


/**
 * s“®ípuPP(l˜T”Å)v
 */
public final class PowerPlay_Werewolf extends AbstractActionStrategy {

	@Override
	public ArrayList<Request> getRequests(ActionStrategyArgs args) {

		ArrayList<Request> Requests = new ArrayList<Request>();
		Request workReq;

		GameInfo gameInfo = args.agi.latestGameInfo;


		// PP“Ë“üÏ‚Å‚È‚¯‚ê‚Î‹óƒŠƒXƒg‚ğ•Ô‚·
		if( !args.agi.isEnablePowerPlay() ){
			return Requests;
		}


		// l˜T‚Í“Š•[‘ÎÛ‚É‚µ‚È‚¢
		for( Agent agent : gameInfo.getAgentList() ){
			Role role = gameInfo.getRoleMap().get(agent);
			if( role == Role.WEREWOLF){
				workReq = new Request( agent.getAgentIdx() );
				workReq.vote = 0.0001;
				Requests.add(workReq);
			}
		}

		// ’‡ŠÔ‚ª“Š•[æ‚ğéŒ¾ÏA‚©‚Â“Š•[æ‚ªlŠÔ‚È‚ç‡‚í‚¹‚é
		for( int agent : args.agi.getAliveWolfList() ){

			Integer target = args.agi.getSaidVoteAgent(agent);

			if( target != null && !args.agi.isWolf(target) ){
				workReq = new Request( target );
				workReq.vote = 1000000.0;

				// s“®—v‹‚Ì“o˜^
				Requests.add(workReq);

				return Requests;
			}

		}



		return Requests;

	}

}
