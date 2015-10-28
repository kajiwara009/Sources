package takata.player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.aiwolf.client.base.player.AbstractPossessed;
import org.aiwolf.client.lib.TemplateTalkFactory;
import org.aiwolf.client.lib.Utterance;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Species;
import org.aiwolf.common.data.Talk;
import org.aiwolf.common.net.GameInfo;

public class TakataPossessedPlayer_ver025 extends AbstractPossessed {

	//インポート
	COMapInfo comapInfo = new COMapInfo();
	//既に役職のカミングアウトをしているか
	boolean isComingOut = false;
    //投票
    boolean vote = false;
    //偽占い済みプレイヤー
    Agent DivinedAgent[] = new Agent[20];
    //偽占い結果の役職
    Species FakeResult[] = new Species[20];
    //偽占い回数
    int divinenum = 0;
    //スキップ回数
    int skipnum = 0;
    //
    int TalkTurnNum = 0;
    //あるプレイヤーの1ゲーム当たりのCO回数
    int[] CONum = new int[16];
    //偽占い結果リスト
    List<Agent> FakeWhiteAgentList = new ArrayList<Agent>(), //白判定だったプレイヤー
            	FakeBlackAgentList = new ArrayList<Agent>(); //黒判定だったプレイヤー

	@Override
	public void dayStart() {
		vote = false;
	}

	public void update(GameInfo gameInfo) {
		super.update(gameInfo);
	    //今日のログを取得
	    List<Talk> talkList = gameInfo.getTalkList();

	    //全体会話に関する会話ログの処理
	    for(int ID = TalkTurnNum; ID < talkList.size(); ID++){
	        Talk talk = talkList.get(ID);
	        //発話をパース
	        Utterance utterance = new Utterance(talk.getContent());

	        //自分の発言はスルー
	        if(talk.getAgent() == getMe()) continue;

	        //発話のトピックごとに処理
	        switch (utterance.getTopic()) {
			case AGREE:
				break;
			case ATTACK:
				break;
			case COMINGOUT:
		    	//カミングアウト結果を保存
		    	comapInfo.putCOMap(getAgentNum(talk.getAgent()), utterance.getRole(), CONum[getAgentNum(talk.getAgent())]);
		    	CONum[getAgentNum(talk.getAgent())]++;
		    	break;
			case DISAGREE:
				break;
			case DIVINED:
				break;
			case ESTIMATE:
				break;
			case GUARDED:
				break;
			case INQUESTED:
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
	    }
	    TalkTurnNum = talkList.size();
	}

	@Override
	public String talk() {
		//0日目は発言しない
		if(getLatestDayGameInfo().getDay() == 0){
			return Talk.OVER;
		}

	    //占い師をカミングアウト
	    if(!isComingOut){
	    	String comingoutTalk = TemplateTalkFactory.comingout(getMe(), Role.SEER);
	        isComingOut = true;
	        return comingoutTalk;
	    }

	    //カミングアウトした後は，まだ言っていない占い結果を順次報告
	    if(isComingOut && divinenum < getLatestDayGameInfo().getDay()){
	    	DivinedAgent[divinenum] = fakedivine();
		    FakeResult[divinenum] = fakejudge();
		    String ResultTalk = TemplateTalkFactory.divined(DivinedAgent[divinenum], FakeResult[divinenum]);
	        divinenum++;
		    return ResultTalk;
	    }

	    //何度かスキップしたのち投票対象を述べる
    	int i = new Random().nextInt(6);
    	skipnum = i;
	    if(skipnum < 2) {
	    	return Talk.SKIP;
	    }

	    //投票
	    if(!vote){
	    	String VoteTalk = TemplateTalkFactory.vote(vote());
	    	vote = true;
	    	return VoteTalk;
	    }

	    //話すことが無ければ会話終了
	    return Talk.OVER;
	}

	@Override
	public Agent vote() {
	    //占い師カミングアウトリスト
	    List<Agent> SeerCOedAgentList = new ArrayList<Agent>();
	    for(Map.Entry<Integer, Role> comap: comapInfo.getCOMap().entrySet()){
	    	if(comap.getValue() == Role.SEER) {
	    		SeerCOedAgentList.add(getLatestDayGameInfo().getAgentList().get(comap.getKey()));
	    	}
	    }
	    if(FakeBlackAgentList.size() > 0){
	        return randomSelect(FakeBlackAgentList);
	    }else if(SeerCOedAgentList.size() > 0) {
	        return randomSelect(SeerCOedAgentList);
	    }else {
	        //投票対象の候補者リスト
	        List<Agent> voteCandidates = new ArrayList<Agent>();

	        //生きているプレイヤーを候補者リストに加える
	        voteCandidates.addAll(getLatestDayGameInfo().getAliveAgentList());

	        //自分自身と白判定のプレイヤーは候補から外す
	        voteCandidates.remove(getMe());
	        voteCandidates.removeAll(FakeWhiteAgentList);

	        //null対策
	        if(voteCandidates.size() > 0) {
	        	return randomSelect(voteCandidates);
	        }else {
	        	return getMe();
	        }
	    }
	}

	@Override
	public void finish() {
		// TODO 自動生成されたメソッド・スタブ

	}

	//偽占い対象者選定
	public Agent fakedivine(){
		//占い対象の候補者リスト
	    List<Agent> fakedivineCandidates = new ArrayList<Agent>();

	    //生きているプレイヤーを候補者リストに加える
	    fakedivineCandidates.addAll(getLatestDayGameInfo().getAliveAgentList());
	    //自分自身は候補から外す
	    fakedivineCandidates.remove(getMe());
	    //すでに占ったプレイヤーを外す
    	for(int i=1; i<divinenum; i++){
	    	fakedivineCandidates.remove(DivinedAgent[i]);
	    }

	    if(fakedivineCandidates.size() > 0){
	        //候補者リストからランダムに選択
	        return randomSelect(fakedivineCandidates);
	    }else{
	    	//候補者がいない場合は自分
	        return getMe();
	    }
	}
	//偽占い対象者の判定結果をセット
	public Species fakejudge(){
		if(divinenum == 3){
			FakeBlackAgentList.add(DivinedAgent[divinenum]);
			return Species.WEREWOLF;
		}else if(divinenum == 5){
			FakeBlackAgentList.add(DivinedAgent[divinenum]);
			return Species.WEREWOLF;
		}
		else{
			FakeWhiteAgentList.add(DivinedAgent[divinenum]);
			return Species.HUMAN;
		}
	}

	//エージェント番号の取得
	private Integer getAgentNum(Agent agent) {
		return getLatestDayGameInfo().getAgentList().indexOf(agent);
	}
	//引数のAgentのリストからランダムにAgentを選択する
	private Agent randomSelect(List<Agent> agentList){
	    int num = new Random().nextInt(agentList.size());
	    return agentList.get(num);
	}

}