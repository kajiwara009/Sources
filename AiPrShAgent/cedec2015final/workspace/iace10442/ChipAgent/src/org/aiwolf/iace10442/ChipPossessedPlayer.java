package org.aiwolf.iace10442;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.aiwolf.client.lib.*;
import org.aiwolf.common.data.*;
import org.aiwolf.common.net.*;

public class ChipPossessedPlayer extends AbstractChipBasePlayer {
	// 占い師をテキトー騙り

	private boolean is_comingout;
	private ArrayList< Judge > judge_list = new ArrayList<Judge>();
	private int judge_told;
	
	@Override
	public void initialize(GameInfo gameinfo, GameSetting gamesetting) {
		super.initialize(gameinfo, gamesetting);
		is_comingout = false;
		judge_told = 0;
	}
	
	@Override
	public void dayStart() {
		super.dayStart();
		int day = getLatestDayGameInfo().getDay(); 
		if( day > 0 ){
			List<Agent> divineCandidates = new ArrayList<Agent>();
			divineCandidates.addAll( getLatestDayGameInfo().getAliveAgentList() );
			divineCandidates.remove( getMe() );
			for( Judge judge: judge_list ) {
				if( divineCandidates.contains( judge.getTarget() )) {
					divineCandidates.remove( judge.getTarget() );
				}
			}
			Agent targ = null;
			if( divineCandidates.size() > 0 ) {
				targ = randomSelect( divineCandidates );
			}else {
				targ = getMe();
			}
			Judge divined = new Judge(day, getMe(), targ, Species.HUMAN );
			judge_list.add( divined );
			information.addDivine(divined.getTarget(), divined.getResult() );
		}
	}
	
	@Override
	public String talk() {
		//とりあえずカミングアウトする
		if(!is_comingout){
			String result_talk = TemplateTalkFactory.comingout(getMe(), Role.SEER );
			is_comingout = true;
			return result_talk;
		}//カミングアウトした後は，まだ言っていない占い結果を順次報告
		else{
			if( judge_told < judge_list.size() ){
				Judge judge = judge_list.get(judge_told);
				String result_talk = TemplateTalkFactory.divined(judge.getTarget(), judge.getResult());
				judge_told ++;
				return result_talk;
			}
		}
		//話すことが無ければ会話終了
		return null;
	}
	
	
	@Override
	public void finish() {
		// TODO 自動生成されたメソッド・スタブ
		
	}
	
	

	private Agent randomSelect( List<Agent> agentlist ) {
		int num = new Random().nextInt( agentlist.size() );
		return agentlist.get(num);
	}
	
}
