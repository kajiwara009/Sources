package takata.player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.aiwolf.client.base.player.AbstractBodyguard;
import org.aiwolf.client.lib.TemplateTalkFactory;
import org.aiwolf.client.lib.Utterance;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Judge;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Species;
import org.aiwolf.common.data.Talk;
import org.aiwolf.common.net.GameInfo;

public class TakataBodyGuardPlayer_ver025 extends AbstractBodyguard {

	AdvanceGameInfo agi = new AdvanceGameInfo();

	//今日投票しようと思っているプレイヤー
	Agent planningVoteAgent;
	//自分が最後に宣言した「投票しようと思っているプレイヤー」
	Agent declaredPlanningVoteAgent;
	//会話をどこまで読んだか
	int readTalkListNum;
	//CO済み判定
	boolean COed = false;
	//護衛結果発言
	boolean EstimateTalk = false;
	//狩人宣言
	boolean BodyGuardCOed = false;
	//ガードしたプレイヤー
	Agent guardAgent;
	//ガードが成功したプレイヤー
	Agent GuardSuccessAgent;
	//ガードが成功したプレイヤーリスト
	List<Agent> GuardSuccessAgentList = new ArrayList<Agent>();
	//GJ発言回数
	int TalkGJNum = 0;
	//偽占い師リスト
	List<Agent> FakeSeerCOedList = new ArrayList<Agent>();

	@Override
	public void dayStart() {
		declaredPlanningVoteAgent = null;
		planningVoteAgent = null;
		EstimateTalk = false;
		GuardSuccessAgent = null;
		setPlanningVoteAgent();
		if(getLatestDayGameInfo().getAttackedAgent() == null && guardAgent != null) {
			GuardSuccessAgent = guardAgent;
			GuardSuccessAgentList.add(guardAgent);
		}
		readTalkListNum = 0;
		agi.getVoteMap().clear();

	}

	@Override
	public void update(GameInfo gameInfo) {
		super.update(gameInfo);

		List<Talk> talkList = gameInfo.getTalkList();
		boolean existInspectResult = false;

		/*
		 * talkListからCO，占い結果の抽出
		 */
		for(int i = readTalkListNum; i < talkList.size(); i++){
			Talk talk = talkList.get(i);
			Utterance utterance = new Utterance(talk.getContent());
			switch (utterance.getTopic()) {

			//カミングアウトの発話の場合
			case COMINGOUT:
				agi.putComingoutMap(talk.getAgent(), utterance.getRole());
				break;

			//占い結果の発話の場合
			case DIVINED:
				//AGIのJudgeListに結果を加える
				Agent seerAgent = talk.getAgent();
				Agent inspectedAgent = utterance.getTarget();
				Species inspectResult = utterance.getResult();
				Judge judge = new Judge(getDay(), seerAgent, inspectedAgent, inspectResult);
				agi.addInspectJudgeList(judge);
				existInspectResult =true;
				break;

			//投票の発言の場合
			case VOTE:
				agi.putVoteMap(talk.getAgent(), utterance.getTarget());
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
				break;
			case OVER:
				break;
			case SKIP:
				break;
			default:
				break;
			}
		}
		readTalkListNum =talkList.size();

		/*
		 * 新しい占い結果があれば投票先を変える．(新たに黒判定が出た，または投票先のプレイヤーに白判定が出た場合)
		 */
		if(existInspectResult){
			setPlanningVoteAgent();
			existInspectResult = false;
		}
	}

	@Override
	public String talk() {
		//0日目は村人カミングアウトが半分以上なら村人カミングアウトしてあとは発言しない
		if(getLatestDayGameInfo().getDay() == 0) {
			int VCONum = 0;
			for(Role CORole :agi.getComingoutMap().values()) {
				switch (CORole) {
				case VILLAGER:
					VCONum++;
					break;
				default:
					break;
				}
			}
			if(VCONum > getLatestDayGameInfo().getAliveAgentList().size() / 2) {
				if(!COed) {
					String CO = TemplateTalkFactory.comingout(getMe(), Role.VILLAGER);
					COed = true;
					return CO;
				}else {
					return Talk.OVER;
				}
			}
			return Talk.OVER;
		}

		//発言した投票先と投票予定先が異なっていれば投票先を発言
		if(declaredPlanningVoteAgent != planningVoteAgent){
			String string = TemplateTalkFactory.vote(planningVoteAgent);
			declaredPlanningVoteAgent = planningVoteAgent;
			return string;
		}

		//人狼と疑われたら狩人と明かす
		if(!BodyGuardCOed) {
			//人狼と占われたら
			for(Judge judge: agi.getInspectJudgeList()){
				if(judge.getTarget() == getMe() && judge.getResult() == Species.WEREWOLF){
					FakeSeerCOedList.add(judge.getAgent());
					String CO = TemplateTalkFactory.comingout(getMe(), Role.BODYGUARD);
					BodyGuardCOed = true;
					return CO;
				}
			}
			//投票先の過半数が自分かつGJがあるなら
			Agent VotedAgent = getMe();
			int[] VoteNum = new int[getLatestDayGameInfo().getAgentList().size()];
			for(Agent agent: agi.getVoteMap().values()) {
				VoteNum[getLatestDayGameInfo().getAgentList().indexOf(agent)]++;
				if(VoteNum[getLatestDayGameInfo().getAgentList().indexOf(agent)] > VoteNum[getLatestDayGameInfo().getAgentList().indexOf(VotedAgent)]) {
					VotedAgent = agent;
				}
			}
			if(VotedAgent == getMe() && GuardSuccessAgentList.size() > 0) {
				String CO = TemplateTalkFactory.comingout(getMe(), Role.BODYGUARD);
				BodyGuardCOed = true;
				return CO;
			}
		}

		//ボディーガードをカミングアウトしたらGJ結果を発言
		if(BodyGuardCOed) {
			if(GuardSuccessAgentList.size() > TalkGJNum) {
				boolean GJ = true;
				for(int i=0; i<TalkGJNum; i++) {
					if(GuardSuccessAgentList.get(TalkGJNum) == GuardSuccessAgentList.get(i)) {
						GJ = false;
					}
				}
				if(GJ){
					String TalkGJ = TemplateTalkFactory.guarded(GuardSuccessAgentList.get(TalkGJNum));
					TalkGJNum++;
					return TalkGJ;
				}
			}
		}

		if(GuardSuccessAgent != null && !EstimateTalk) {
			Role estimateRole = Role.VILLAGER;
			String estimate = TemplateTalkFactory.estimate(GuardSuccessAgent, estimateRole);
			EstimateTalk = true;
			return estimate;
		}

		return TemplateTalkFactory.over();
	}

	@Override
	public Agent vote() {
		return planningVoteAgent;
	}

	@Override
	public Agent guard() {
		//占い師，もしくは霊能者COしているプレイヤーからランダムに選択

		List<Agent> guardAgentCandidate = new ArrayList<Agent>();
		List<Agent> aliveAgentList = getLatestDayGameInfo().getAliveAgentList();
		aliveAgentList.remove(getMe());
		aliveAgentList.removeAll(FakeSeerCOedList);

		for(Agent agent: aliveAgentList){
			if(agi.getComingoutMap().containsKey(agent)){
				List<Role> guardRoleList = Arrays.asList(Role.SEER);
				if(guardRoleList.contains(agi.getComingoutMap().get(agent))){
					guardAgentCandidate.add(agent);
				}
			}
		}

		if(guardAgentCandidate.size() > 0){
			guardAgent = randomSelect(guardAgentCandidate);
		}else{
			guardAgent = randomSelect(aliveAgentList);
		}
		return guardAgent;
	}

	public void setPlanningVoteAgent(){
		/*
		 * 人狼だと占われたプレイヤーを指定している場合はそのまま
		 */
		if(planningVoteAgent != null){
			for(Judge judge: agi.getInspectJudgeList()){
				if(judge.getTarget().equals(planningVoteAgent)){
					return;
				}
			}
		}

		/*
		 * 投票先を未設定，または人狼だと占われたプレイヤー以外を投票先にしている場合
		 * 人狼だと占われたプレイヤーがいれば，投票先をそのプレイヤーに設定
		 * いなければ生存プレイヤーからランダムに選択
		 */
		List<Agent> voteAgentCandidate = new ArrayList<Agent>();

		List<Agent> aliveAgentList = getLatestDayGameInfo().getAliveAgentList();
		aliveAgentList.remove(getMe());

		for(Judge judge: agi.getInspectJudgeList()){
			if(aliveAgentList.contains(judge.getTarget()) && judge.getResult() == Species.WEREWOLF){
				voteAgentCandidate.add(judge.getTarget());
			}
		}

		if(voteAgentCandidate.size() > 0){
			Random rand = new Random();
			planningVoteAgent = voteAgentCandidate.get(rand.nextInt(voteAgentCandidate.size()));
		}else{
			Random rand = new Random();
			planningVoteAgent = aliveAgentList.get(rand.nextInt(aliveAgentList.size()));
		}
		return;
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
