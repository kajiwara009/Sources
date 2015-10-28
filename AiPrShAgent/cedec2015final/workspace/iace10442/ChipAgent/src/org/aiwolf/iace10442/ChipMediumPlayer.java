package org.aiwolf.iace10442;

import java.util.*;

import org.aiwolf.client.lib.TemplateTalkFactory;
import org.aiwolf.common.data.*;
import org.aiwolf.common.net.GameInfo;
import org.aiwolf.common.net.GameSetting;

public class ChipMediumPlayer extends AbstractChipBasePlayer {

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
		Judge inquested = getLatestDayGameInfo().getMediumResult(); 
		
		if( inquested != null){
			judge_list.add( inquested );
			information.addInquest(inquested.getTarget(), inquested.getResult() );
		}
	}

	
	
	@Override
	public String talk()
	{
		//　とりあえずカミングアウトする
		if(!is_comingout){
			String result_talk = TemplateTalkFactory.comingout(getMe(), getMyRole());
			is_comingout = true;
			return result_talk;
		}//カミングアウトした後は，まだ言っていない占い結果を順次報告
		else{
			if( judge_told < judge_list.size() ){
				Judge judge = judge_list.get(judge_told);
				String result_talk = TemplateTalkFactory.inquested(judge.getTarget(), judge.getResult());
				judge_told ++;
				return result_talk;
			}
		}
		//話すことが無ければ会話終了
		return Talk.OVER;
	}

	
	@Override
	public void finish() {
		// TODO 自動生成されたメソッド・スタブ

	}

	
}
