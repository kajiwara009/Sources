////ファイル名：TakataSimpleVillagerPlayer
////作成者：高田和磨
////作成日時：2014.11.26
////バージョン：0.1.1
////種類：ロールプレイヤー
////特徴：他プレイヤーを白黒グレーリストに分けて，黒リストから順にランダムに投票を行う．
////	人狼判定や矛盾行為者を黒リストへ振り分ける．


package takata.player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.aiwolf.client.base.player.AbstractVillager;
import org.aiwolf.client.lib.TemplateTalkFactory;
import org.aiwolf.client.lib.TemplateTalkFactory.TalkType;
import org.aiwolf.client.lib.Utterance;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Species;
import org.aiwolf.common.data.Talk;
import org.aiwolf.common.net.GameInfo;

public class TakataVillagerPlayer_ver025 extends AbstractVillager {

	//その日の最初の発言
	boolean FirstTalk = true;
    //投票
    boolean vote = false;
    //カミングアウト
    boolean COed = false;
    //同調回数
    int AgreeNum = 0;
    //同調関係
    List<TemplateTalkFactory.TalkType> AgreeTalkTypeList = new ArrayList<TemplateTalkFactory.TalkType>();
    List<Integer> 	AgreeTalkDayList = new ArrayList<Integer>(),
    				AgreeTalkIDList = new ArrayList<Integer>();

    //その日の投票対象プレイヤー
    Agent PlanningVoteAgent = null;
    //回ってきたTalk回数
    int TalkTurnNum = 0;
    //
    List<Agent> AliveSeerCOList = new ArrayList<Agent>(),
    			AliveMediumCOList = new ArrayList<Agent>(),
				AliveBodyguardCOList = new ArrayList<Agent>(),
				AlivePossessedCOList = new ArrayList<Agent>(),
				AliveVillagerCOList = new ArrayList<Agent>(),
				AliveWerewolfCOList = new ArrayList<Agent>();
    //CO済みプレイヤーリストおよび役職リスト
	List<Agent> COedAgentList = new ArrayList<Agent>();
	List<Role>  AgentRoleList = new ArrayList<Role>();
	//死亡プレイヤーリスト
	//List<Agent> DeadAgentList = new ArrayList<Agent>();
	//判定リスト
    List<Agent> WhiteAgentList = new ArrayList<Agent>(), //白判定だったプレイヤー
				BlackAgentList = new ArrayList<Agent>(), //黒判定だったプレイヤー
    			GrayAgentList = new ArrayList<Agent>(), //グレー判定となったプレイヤー
    			WhiteDeadAgentList = new ArrayList<Agent>(), //人間判定だった死亡プレイヤー
    			BlackDeadAgentList = new ArrayList<Agent>(), //人狼判定だった死亡プレイヤー
    			GrayDeadAgentList = new ArrayList<Agent>(); //グレー判定となった死亡プレイヤー

	@Override
  	public void dayStart() {
		//super.dayStart();
		//各初期化
		FirstTalk = true;
		vote = false;
		PlanningVoteAgent = null;
		TalkTurnNum = 0;
		//白黒グレーリストから死者を排除
		WhiteAgentList.remove(getLatestDayGameInfo().getExecutedAgent());
		WhiteAgentList.remove(getLatestDayGameInfo().getAttackedAgent());
		BlackAgentList.remove(getLatestDayGameInfo().getExecutedAgent());
		BlackAgentList.remove(getLatestDayGameInfo().getAttackedAgent());
		GrayAgentList.remove(getLatestDayGameInfo().getExecutedAgent());
		GrayAgentList.remove(getLatestDayGameInfo().getAttackedAgent());
		//
		AliveSeerCOList.clear();
		AliveMediumCOList.clear();
		AliveBodyguardCOList.clear();
		AlivePossessedCOList.clear();
		AliveVillagerCOList.clear();
		AliveWerewolfCOList.clear();
		for(Agent Agent: COedAgentList) {
			switch (AgentRoleList.get(COedAgentList.indexOf(Agent))) {
			case SEER:
				AliveSeerCOList.add(Agent);
				break;
			case MEDIUM:
				AliveMediumCOList.add(Agent);
				break;
			case BODYGUARD:
				AliveBodyguardCOList.add(Agent);
				break;
			case POSSESSED:
				AlivePossessedCOList.add(Agent);
				break;
			case VILLAGER:
				AliveVillagerCOList.add(Agent);
				break;
			case WEREWOLF:
				AliveWerewolfCOList.add(Agent);
				break;
			default:
				break;
			}
		}
	}

	@Override
	public void update(GameInfo gameInfo) {

		super.update(gameInfo);
	    //今日のログを取得
	    List<Talk> talkList = gameInfo.getTalkList();

	    for(int ID = TalkTurnNum; ID < talkList.size(); ID++){
	        Talk talk = talkList.get(ID);
	        //発話をパース
	        Utterance utterance = new Utterance(talk.getContent());

	        //自分の発言はスルー
	        if(talk.getAgent() == getMe()) continue;

	        //発話のトピックごとに処理
	        switch (utterance.getTopic()) {
		    case COMINGOUT:
		       	//プレイヤー役職を占い済みリストに追加
		       	COedAgentList.add(utterance.getTarget());
		       	AgentRoleList.add(utterance.getRole());
		       	break;
		    case DIVINED:
		    	if(utterance.getTarget().equals(getMe()) && utterance.getResult().equals(Species.WEREWOLF)) {
		        	//自分を占った偽占い師を黒リストへ追加（移動）．
	         		if(!BlackAgentList.contains(talk.getAgent())) {
	         			BlackAgentList.add(talk.getAgent());
	         			WhiteAgentList.remove(talk.getAgent());
	         			GrayAgentList.remove(talk.getAgent());
	         		}
		    	}else {
			        //占い結果をリストに貯蔵
			        switch (utterance.getResult()) {
			        case HUMAN:
			        	//もし既に黒判定が出ていたらグレーへ．そうでなければ白リストに追加．
		         		if(BlackAgentList.contains(utterance.getTarget())) {
			           		GrayAgentList.add(utterance.getTarget());
			           		BlackAgentList.remove(utterance.getTarget());
		         		}else if(GrayAgentList.contains(utterance.getTarget())) {
		         			//何もしない
		         		}else {
			           		WhiteAgentList.add(utterance.getTarget());
			           	}
			           	break;
			        case WEREWOLF:
			        	//もし既に白判定が出ていたらグレーへ．そうでなければ黒リストへ追加．
			           	if(WhiteAgentList.contains(utterance.getTarget())) {
			           		GrayAgentList.add(utterance.getTarget());
			           		WhiteAgentList.remove(utterance.getTarget());
		         		}else if(GrayAgentList.contains(utterance.getTarget())) {
		         			//何もしない
			           	}else {
			           		BlackAgentList.add(utterance.getTarget());
			           	}
			           	break;
			        }
			        vote = false;
		    	}
		        break;
			case AGREE:
				break;
			case DISAGREE:
				break;
			case ESTIMATE:
				if(WhiteAgentList.contains(utterance.getTarget()) && utterance.getRole() == Role.VILLAGER) {
					int rand = new Random().nextInt(20);
					if(rand > 17) {
						AgreeTalkTypeList.add(TalkType.TALK);
						AgreeTalkDayList.add(talk.getDay());
						AgreeTalkIDList.add(talk.getIdx());
					}
				}
				break;
			case GUARDED:
				break;
			case INQUESTED:
				//霊能結果
				switch (utterance.getResult()) {
				case HUMAN:
					break;
				case WEREWOLF:
					if(COedAgentList.contains(utterance.getTarget())) {
						switch (AgentRoleList.get(COedAgentList.indexOf(utterance.getTarget()))) {
						case SEER:
							if(AliveSeerCOList.size() == 1 ) {
								WhiteAgentList.addAll(AliveSeerCOList);
								BlackAgentList.remove(AliveSeerCOList);
								GrayAgentList.remove(AliveSeerCOList);
							}
							break;
						case MEDIUM:
							if(AliveMediumCOList.size() == 1 ) {
								WhiteAgentList.addAll(AliveMediumCOList);
								BlackAgentList.remove(AliveMediumCOList);
								GrayAgentList.remove(AliveMediumCOList);
							}
							break;
						case BODYGUARD:
							if(AliveBodyguardCOList.size() == 1 ) {
								WhiteAgentList.addAll(AliveBodyguardCOList);
								BlackAgentList.remove(AliveBodyguardCOList);
								GrayAgentList.remove(AliveBodyguardCOList);
							}
							break;
						default:
							break;
						}
					}
					break;
				}
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
	        TalkTurnNum++;
	    }
	}

	@Override
	public String talk() {
		//初日の行動
		if(getLatestDayGameInfo().getDay() == 0){
			//たまにCO
			int rand = new Random().nextInt(20);
			if(rand > 16 && !COed){
				String CO = TemplateTalkFactory.comingout(getMe(), Role.VILLAGER);
				COed = true;
				return CO;
			}else {
				return Talk.OVER;
			}
		}

		//たまに予想
		int rand = new Random().nextInt(20);
		if(rand > 17) {
			if(BlackAgentList.size() > 0) {
				String Estimate = TemplateTalkFactory.estimate(randomSelect(BlackAgentList), Role.WEREWOLF);
				return Estimate;
			}else if(WhiteAgentList.size() > 0) {
				String Estimate = TemplateTalkFactory.estimate(randomSelect(WhiteAgentList), Role.VILLAGER);
				return Estimate;
			}
		}

		//同意
		if(AgreeTalkTypeList.size() > 0) {
			if(AgreeNum < AgreeTalkTypeList.size()) {
				TemplateTalkFactory.TalkType talktype = AgreeTalkTypeList.get(AgreeNum);
				int day = AgreeTalkDayList.get(AgreeNum);
				int id = AgreeTalkIDList.get(AgreeNum);
				AgreeNum++;
				String AgreeTalk = TemplateTalkFactory.agree(talktype, day, id);
			    return AgreeTalk;
			}
		}

	    //投票
	    if(!vote){
	    	String VoteTalk = TemplateTalkFactory.vote(PlanningVoteAgent = setPlanningVoteAgent());
	    	vote = true;
	    	return VoteTalk;
	    }

	    //話すことが無ければ会話終了
	    return Talk.OVER;
	}

	@Override
	public Agent vote() {
		return PlanningVoteAgent;
	}

	//投票方法
	private Agent setPlanningVoteAgent() {
		//ブラックリストから順に選定
		if(BlackAgentList.size() > 0){
			return randomSelect(BlackAgentList);
		}else if(GrayAgentList.size() > 0){
			return randomSelect(GrayAgentList);
		}else {
			List<Agent> AliveAgentlist = new ArrayList<Agent>();
			AliveAgentlist.addAll(getLatestDayGameInfo().getAliveAgentList());
			AliveAgentlist.removeAll(WhiteAgentList);
			AliveAgentlist.remove(getMe());
			if(AliveAgentlist.size() > 0) {
				return randomSelect(AliveAgentlist);
			}else {
				return randomSelect(getLatestDayGameInfo().getAliveAgentList());
			}
		}
	}

	@Override
	public void finish() {
		// TODO 自動生成されたメソッド・スタブ
	}

	//引数のAgentのリストからランダムにAgentを選択する
	private Agent randomSelect(List<Agent> agentList){
	    int num = new Random().nextInt(agentList.size());
	    return agentList.get(num);
	}
}
