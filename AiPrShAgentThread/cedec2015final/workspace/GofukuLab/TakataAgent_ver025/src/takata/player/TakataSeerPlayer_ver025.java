package takata.player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

import org.aiwolf.client.base.player.AbstractSeer;
import org.aiwolf.client.lib.TemplateTalkFactory;
import org.aiwolf.client.lib.Utterance;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Judge;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Species;
import org.aiwolf.common.data.Talk;
import org.aiwolf.common.net.GameInfo;

public class TakataSeerPlayer_ver025 extends AbstractSeer {

	//インポート
	AdvanceGameInfo agi = new AdvanceGameInfo();
	//今日投票しようと思っているプレイヤー
	Agent planningVoteAgent;
	//自分が最後に宣言した「投票しようと思っているプレイヤー」
	Agent declaredPlanningVoteAgent;
	//発言回数
	int TalkTrunNum = 0;
	//agree
	int agree = 0;
	//既に役職のカミングアウトをしているか
	boolean isComingOut = false;
	//最初の投票
	boolean FirstVote = false;
	//占いリスト
	List<Judge> DivineList = new ArrayList<Judge>();
	//占い結果の報告状況
	List<Judge> myToldJudgeList = new ArrayList<Judge>();


    @Override
    public void dayStart() {
		declaredPlanningVoteAgent = null;
		planningVoteAgent = null;
		FirstVote = true;
    	//占い結果をjudgeListに格納
		if(getLatestDayGameInfo().getDay() > 0) {
			DivineList.add(getLatestDayGameInfo().getDivineResult());
		}
    }

	@Override
	public void update(GameInfo gameInfo) {
	    super.update(gameInfo);
	    //今日のログを取得
	    List<Talk> talkList = gameInfo.getTalkList();

	    for(int i = TalkTrunNum; i < talkList.size(); i++){
	        Talk talk = talkList.get(i);
	        //発話をパース
	        Utterance utterance = new Utterance(talk.getContent());

	        //自分の発言はスルー
	        if(talk.getAgent() == getMe()) continue;

	        //発話のトピックごとに処理
	        switch (utterance.getTopic()) {
	        case COMINGOUT:
		    	//カミングアウト結果を保存
	        	agi.putComingoutMap(talk.getAgent(), utterance.getRole());
	        	if(utterance.getRole() == Role.VILLAGER) {
	        		//無視
	        	}else {
	        		//setPlanningVoteAgent();
	        	}
	            break;
	        case DIVINED:
	            // 占い結果の発話の処理
	            break;
			case AGREE:
				break;
			case ATTACK:
				break;
			case DISAGREE:
				break;
			case ESTIMATE:
				break;
			case GUARDED:
				break;
			case INQUESTED:
		    	//カミングアウト結果を保存
				Judge mediumjudge = new Judge(getLatestDayGameInfo().getDay(), talk.getAgent(), utterance.getTarget(), utterance.getResult());
	        	agi.addMediumJudgeList(mediumjudge);
	        	//setPlanningVoteAgent();
				break;
			case OVER:
				break;
			case SKIP:
				break;
			case VOTE:
				break;
			default:
				break;
	        }
	        TalkTrunNum++;
	    }
	}


    @Override
	public Agent divine() {
		//占い対象の候補者リスト
	    List<Agent> divineCandidates = new ArrayList<Agent>();

	    //生きているプレイヤーを候補者リストに加える
	    divineCandidates.addAll(getLatestDayGameInfo().getAliveAgentList());
	    //自分自身と既に占ったことのあるプレイヤーは候補から外す
	    divineCandidates.remove(getMe());
	    for(Judge judge: DivineList){
	        if(divineCandidates.contains(judge.getTarget())){
	            divineCandidates.remove(judge.getTarget());
	        }
	    }
	    if(divineCandidates.size() > 0){
	        //候補者リストからランダムに選択
	        return randomSelect(divineCandidates);
	    }else {
	        //候補者がいない場合は自分を占う
	        return getMe();
	    }
	}

	@Override
	public String talk() {
		//0日目は発言しない
		if(getLatestDayGameInfo().getDay() == 0) return Talk.OVER;

	    //占いで人狼を見つけたらカミングアウトする
	    if(!isComingOut){
	        for(Judge judge: DivineList){
	            if(judge.getResult() == Species.WEREWOLF){ //占い結果が人狼の場合
	                String comingoutTalk = TemplateTalkFactory.comingout(getMe(), getMyRole());
	                isComingOut = true;
	                return comingoutTalk;
	            }
	        }
	        //二人偽物が出てきたら自分も明かす
	        int fake = 0;
		    for(Entry<Agent, Role> comap: agi.getComingoutMap().entrySet()) {
		    	if(comap.getValue() == Role.SEER  && comap.getKey() != getMe()){
		    		fake++;
		    	}
		    }
	        if(fake > 1) {
                String comingoutTalk = TemplateTalkFactory.comingout(getMe(), getMyRole());
                isComingOut = true;
                return comingoutTalk;
	        }
	        //2日目になったらとりあえずCO
	        if(getLatestDayGameInfo().getDay() > 2) {
                String comingoutTalk = TemplateTalkFactory.comingout(getMe(), getMyRole());
                isComingOut = true;
                return comingoutTalk;
	        }
	    }

	    //カミングアウトした後は，まだ言っていない占い結果を順次報告
	    else {
	        for(Judge judge: DivineList){
	        	//まだ報告していないJudgeの報告
	            if(!myToldJudgeList.contains(judge)){
	                String resultTalk = TemplateTalkFactory.divined(judge.getTarget(), judge.getResult());
	                myToldJudgeList.add(judge);
	                return resultTalk;
	            }
	        }
	    }

	    //
	    if(FirstVote) {
	    	setPlanningVoteAgent();
			FirstVote = false;
	    }

	    //投票先発言と投票予定先が異なれば発言
		if(declaredPlanningVoteAgent != planningVoteAgent){
			String string = TemplateTalkFactory.vote(planningVoteAgent);
			declaredPlanningVoteAgent = planningVoteAgent;
			return string;
		}

	    //話すことが無ければ会話終了
	    return Talk.OVER;
	}

	@Override
	public Agent vote() {
		return planningVoteAgent;
	}

	public void setPlanningVoteAgent(){
		//リスト
		List<Agent> BlackAgentList = new ArrayList<Agent>(),
					WhiteAgentList = new ArrayList<Agent>(),
					DeadWerewolfList = new ArrayList<Agent>();

		//偽占い師カミングアウトを保存
		for(Entry<Agent, Role> comap: agi.getComingoutMap().entrySet()) {
			if(comap.getValue() == Role.SEER  && comap.getKey() != getMe()){
				BlackAgentList.add(comap.getKey());
			}
		}

		for(Judge judge: DivineList) {
			//自分の占いと異なる霊能を行ったプレイヤーをブラックリストへ追加
			for(Judge mediumjudge: agi.getMediumJudgeList()) {
				if(judge.getTarget() == mediumjudge.getAgent() && judge.getResult() != mediumjudge.getResult()) {
					BlackAgentList.add(mediumjudge.getAgent());
				}
			}
			//占い結果を反映
		    if(getLatestDayGameInfo().getAliveAgentList().contains(judge.getTarget())){
		        switch (judge.getResult()) {
		        case HUMAN:
		            WhiteAgentList.add(judge.getTarget());
		            break;
		        case WEREWOLF:
		            BlackAgentList.add(judge.getTarget());
		        }
		    }
		    //死んだ狼
		    else {
		    	if(judge.getResult() == Species.WEREWOLF) {
		    		DeadWerewolfList.add(judge.getTarget());
		    	}
		    }
		}

		if(BlackAgentList.size() > 0){
			planningVoteAgent = randomSelect(BlackAgentList);
		}else {
		    //投票対象の候補者リスト
		    List<Agent> voteCandidates = new ArrayList<Agent>();

		    //生きているプレイヤーを候補者リストに加える
		    voteCandidates.addAll(getLatestDayGameInfo().getAliveAgentList());

		    //自分自身と白判定のプレイヤーは候補から外す
		    voteCandidates.remove(getMe());
		    voteCandidates.removeAll(WhiteAgentList);
		    //生存プレイヤーが9人以上ならCOローラーしない
		    if(getLatestDayGameInfo().getAliveAgentList().size() > 8) {
		        for(Entry<Agent, Role> comap: agi.getComingoutMap().entrySet()){
			        if(comap.getValue() == Role.MEDIUM || comap.getValue() == Role.BODYGUARD) {
			        	voteCandidates.remove(comap.getKey());
			        }
		        }
		    }
		    //生存プレイヤーが6人以下かつ人狼生存の可能性が高いなら
		    else if(getLatestDayGameInfo().getAliveAgentList().size() < 7) {
				//信頼できる霊媒師の結果を反映
				for(Judge mediumjudge: agi.getMediumJudgeList()) {
					if(!BlackAgentList.contains(mediumjudge.getAgent())){
						if(mediumjudge.getResult() == Species.WEREWOLF && !DeadWerewolfList.contains(mediumjudge.getTarget())) {
							DeadWerewolfList.add(mediumjudge.getTarget());
						}
					}
				}
		    	if(DeadWerewolfList.size() < 2) {
		    		//村人COとCOなしは除外
		    		List<Agent> NoCOedAgentList = new ArrayList<Agent>();
		    		NoCOedAgentList.addAll(getLatestDayGameInfo().getAliveAgentList());
		    		for(Entry<Agent, Role> comap: agi.getComingoutMap().entrySet()) {
		    			if(comap.getValue() == Role.VILLAGER ){
		    				voteCandidates.remove(comap.getKey());
		    			}
		    			NoCOedAgentList.remove(comap.getKey());
		    		}
		    		voteCandidates.removeAll(NoCOedAgentList);
		    	}
		    }
		    //生存プレイヤーが8人以下かつ人狼生存の可能性が高いなら
		    else if(getLatestDayGameInfo().getAliveAgentList().size() < 9) {
				//信頼できる霊媒師の結果を反映
				for(Judge mediumjudge: agi.getMediumJudgeList()) {
					if(!BlackAgentList.contains(mediumjudge.getAgent())){
						if(mediumjudge.getResult() == Species.WEREWOLF && !DeadWerewolfList.contains(mediumjudge.getTarget())) {
							DeadWerewolfList.add(mediumjudge.getTarget());
						}
					}
				}
		    	if(DeadWerewolfList.size() < 1) {
		    		//村人COとCOなしは除外
		    		List<Agent> NoCOedAgentList = new ArrayList<Agent>();
		    		NoCOedAgentList.addAll(getLatestDayGameInfo().getAliveAgentList());
		    		for(Entry<Agent, Role> comap: agi.getComingoutMap().entrySet()) {
		    			if(comap.getValue() == Role.VILLAGER ){
		    				voteCandidates.remove(comap.getKey());
		    			}
		    			NoCOedAgentList.remove(comap.getKey());
		    		}
		    		voteCandidates.removeAll(NoCOedAgentList);
		    	}
		    }
		    //null対策
		    if(voteCandidates.size() > 0) {
		    	planningVoteAgent = randomSelect(voteCandidates);
		    }else {
		    	planningVoteAgent = randomSelect(getLatestDayGameInfo().getAliveAgentList());
		    }
		}
	}

	/**
	 * 引数のAgentのリストからランダムにAgentを選択する
	 */
	private Agent randomSelect(List<Agent> agentList){
	    int num = new Random().nextInt(agentList.size());
	    return agentList.get(num);
	}

	@Override
	public void finish() {
		// TODO 自動生成されたメソッド・スタブ
	}
}
