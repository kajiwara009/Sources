package com.gmail.tydmskz;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.aiwolf.client.base.player.AbstractWerewolf;
import org.aiwolf.client.lib.TemplateTalkFactory;
import org.aiwolf.client.lib.Utterance;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Judge;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Species;
import org.aiwolf.common.data.Talk;
import org.aiwolf.common.net.GameInfo;
import org.aiwolf.common.net.GameSetting;

public class WerewolfPlayer extends AbstractWerewolf {

	//COする日にち
	int comingoutDay;

	//CO済みか否か
	boolean isCameout;
	boolean isWolfCameout;

	//全体に偽占い(霊能)結果を報告済みのJudge
	ArrayList<Judge> declaredFakeJudgedAgentList = new ArrayList<Judge>();

	/*//全体に占い結果を報告済みのプレイヤー
	ArrayList<Agent> declaredFakeResultAgent = new ArrayList<>();*/
	boolean isSaidAllFakeResult;

	AdvanceGameInfo agi = new AdvanceGameInfo();
	VillageInformation vi = new VillageInformation();
	GameSetting GameSetting = null;

	//今日投票しようと思っているプレイヤー
	Agent planningVoteAgent;

	//自分が最後に宣言した「投票しようと思っているプレイヤー」
	Agent declaredPlanningVoteAgent;

	//会話をどこまで読んだか
	int readTalkListNum;

	//騙る役職
	Role fakeRole;

	//偽の占い(or霊能)結果
	List<Judge> fakeJudgeList = new ArrayList<Judge>();
	int fakeJudgeBlackCount = 0; // 誰かを人狼だとという占い結果を出した数
/*
	//偽の占い(or霊能)結果
	Map<Agent, Species> fakeResultMap = new HashMap<Agent, Species>();
*/
	//狂人だと思うプレイヤー
	Agent maybePossesedAgent = null;

	// 現在の状態でパワープレイ可
	boolean canPp = false;

	PlayerParamaters param;
	public WerewolfPlayer(PlayerParamaters p) {
		param = p;
	}



	@Override
	public void initialize(GameInfo gameInfo, GameSetting gameSetting){
		try{
		super.initialize(gameInfo, gameSetting);

		GameSetting = gameSetting;
/*		List<Role> fakeRoleList = Arrays.asList(Role.SEER, Role.MEDIUM, Role.VILLAGER);
		fakeRole = fakeRoleList.get(new Random().nextInt(fakeRoleList.size()));
*/
		List<Role> fakeRoles = new ArrayList(gameSetting.getRoleNumMap().keySet());
		List<Role> nonFakeRoleList = Arrays.asList(Role.BODYGUARD, Role.FREEMASON, Role.POSSESSED, Role.WEREWOLF);
		fakeRoles.removeAll(nonFakeRoleList);
		//fakeRole = fakeRoles.get(new Random().nextInt(fakeRoles.size()));
		fakeRole = Role.VILLAGER; // 人狼は騙らない設定



		//占い師，or霊能者なら1~3日目からランダムに選択してCO．村人ならCOしない．
		if(fakeRole == Role.VILLAGER){
			comingoutDay = 1000;
		}
		else
		{
			comingoutDay = new Random().nextInt(3)+1;
		}
		isCameout = false;
		isWolfCameout = false;
		}catch (Exception e){}
	}


	@Override
	public void dayStart() {
		try{
		vi.DayStart();
		//投票するプレイヤーの初期化，設定
		declaredPlanningVoteAgent = null;
		planningVoteAgent = null;
		setPlanningVoteAgent();

		if(getDay() >= 1){
			setFakeResult();
		}
		isSaidAllFakeResult = false;


		readTalkListNum =0;
		}catch (Exception e){}
	}

	@Override
	public String talk() {
		try{
		// PP, CO,霊能結果，投票先の順に発話の優先度高

		// PP　人狼であることをCO
		if(canPp)
		{
			if(!isWolfCameout)
			{
				String string = TemplateTalkFactory.comingout(getMe(), Role.WEREWOLF);
				isWolfCameout = true;
				return string;
			}
		}
		/*
		 * 未CO，かつ設定したCOする日にちを過ぎていたらCO
		 */
		else if(!isCameout && getDay() >= comingoutDay){
			String string = TemplateTalkFactory.comingout(getMe(), fakeRole);
			isCameout = true;
			return string;
		}
		/*
		 * COしているなら偽占い，霊能結果の報告
		 */
		else if(isCameout && !isSaidAllFakeResult){
			for(Judge judge: getMyFakeJudgeList()){
				if(!declaredFakeJudgedAgentList.contains(judge)){
					if(fakeRole == Role.SEER){
						String string = TemplateTalkFactory.divined(judge.getTarget(), judge.getResult());
						declaredFakeJudgedAgentList.add(judge);
						return string;
					}else if(fakeRole == Role.MEDIUM){
						String string = TemplateTalkFactory.inquested(judge.getTarget(), judge.getResult());
						declaredFakeJudgedAgentList.add(judge);
						return string;
					}
				}
			}
			isSaidAllFakeResult = true;
		}

		/*
		 * 今日投票するプレイヤーの報告
		 * 前に報告したプレイヤーと同じ場合は報告なし
		 */
		if(declaredPlanningVoteAgent != planningVoteAgent){
			String string = TemplateTalkFactory.vote(planningVoteAgent);
			declaredPlanningVoteAgent = planningVoteAgent;
			return string;
		}

		else{
			return Talk.OVER;
		}

		}catch (Exception e){ return TemplateTalkFactory.over();}
	}

	@Override
	public String whisper() {
		//何も発しない
		return TemplateTalkFactory.over();
	}

	@Override
	public Agent vote() {
		return planningVoteAgent;
	}

	@Override
	public Agent attack() {
		try{
		/*
		 * 襲撃候補がいればその中からランダムに選択
		 * 襲撃候補がいなければ全体からランダム
		 * （ただし，いずれの場合も人狼と狂人(暫定)は襲撃対象から除く）
		 */
		List<Agent> aliveAgentList = getLatestDayGameInfo().getAliveAgentList();
		aliveAgentList.removeAll(getWolfList());
		aliveAgentList.remove(maybePossesedAgent);

		// 狩人CO者が一人なら襲撃
		List<Agent> bodyGuards = Util.AndList(agi.getComingoutAgents(Role.BODYGUARD), aliveAgentList);
		if(bodyGuards.size() > 1) { return bodyGuards.get(0); }

		// ４日目以降で、占い師や霊媒師のCO者が一人なら襲撃
		// （狂人が潜伏？）
		if(getDay() >= 4)
		{
			List<Agent> allseears = agi.getComingoutAgents(Role.SEER);
			if(allseears.size() == 1)
			{
				if(aliveAgentList.contains(allseears.get(0)))
				{
					return allseears.get(0);
				}
			}

			List<Agent> allmediums = agi.getComingoutAgents(Role.MEDIUM);
			if(allmediums.size() == 1)
			{
				if(aliveAgentList.contains(allmediums.get(0)))
				{
					return allmediums.get(0);
				}
			}
		}

		List<Agent> attackCandidatePlayer = new ArrayList<Agent>();

		for(Agent agent: aliveAgentList){
			Role role = agi.getComingoutMap().get(agent);
			if(role == null || (role != Role.SEER && role != Role.MEDIUM)){
				attackCandidatePlayer.add(agent);
			}
		}

		Agent attackAgent;

		if(attackCandidatePlayer.size() > 0){
			attackAgent = attackCandidatePlayer.get(Util.Rand.nextInt(attackCandidatePlayer.size()));
		}else{
			attackAgent = aliveAgentList.get(Util.Rand.nextInt(aliveAgentList.size()));
		}

		return attackAgent;
		}catch (Exception e){ return getMe();}
	}

	@Override
	public void finish() {
	}

	/**
	 * 今日投票予定のプレイヤーを設定する．
	 */
	public void setPlanningVoteAgent(){

		/*
		 * 下記のいずれの場合も人狼は投票候補に入れない．狂人が分かれば狂人も除く．
		 * 村人騙り、またはPP可能なら，人狼と狂人以外からランダム
		 * それ以外の場合↓
		 * 対抗CO，もしくは自分が黒だと判定したプレイヤーからランダム
		 * いなければ白判定を出したプレイヤー以外からランダム
		 * それもいなければ生存プレイヤーからランダム
		 */

		List<Agent> aliveAgentList = getLatestDayGameInfo().getAliveAgentList();
		aliveAgentList.removeAll(getWolfList());
		aliveAgentList.remove(maybePossesedAgent);

		Map<Agent, Agent> voteTarget = vi.VoteTarget();
		voteTarget.remove(getMe());

		if(fakeRole == Role.VILLAGER || canPp){
			if(aliveAgentList.contains(planningVoteAgent)){
				return;
			}else if(aliveAgentList.size()>0){
				planningVoteAgent = Util.SelectVoteTargetByOtherVote(aliveAgentList, voteTarget);
				return;
			}
		}

		//偽占いで人間だと判定したプレイヤーのリスト
		List<Agent> fakeHumanList = new ArrayList<Agent>();

		List<Agent> voteAgentCandidate = new ArrayList<Agent>();
		for(Agent a: aliveAgentList){
			if(agi.getComingoutMap().containsKey(a) && agi.getComingoutMap().get(a) == fakeRole){
				voteAgentCandidate.add(a);
			}
		}
		for(Judge judge: getMyFakeJudgeList()){
			if(judge.getResult() == Species.HUMAN){
				fakeHumanList.add(judge.getTarget());
			}else{
				voteAgentCandidate.add(judge.getTarget());
			}
		}

		if(voteAgentCandidate.contains(planningVoteAgent)){
			return;
		}


		if(voteAgentCandidate.size() > 0){
			planningVoteAgent = Util.SelectVoteTargetByOtherVote(voteAgentCandidate, voteTarget);
			//planningVoteAgent = voteAgentCandidate.get(rand.nextInt(voteAgentCandidate.size()));
		}else{
			//自分が白判定を出していないプレイヤーのリスト
			List<Agent> aliveAgentExceptHumanList = getLatestDayGameInfo().getAliveAgentList();
			aliveAgentExceptHumanList.removeAll(fakeHumanList);

			if(aliveAgentExceptHumanList.size() > 0){
				planningVoteAgent = Util.SelectVoteTargetByOtherVote(aliveAgentExceptHumanList, voteTarget);

				//Random rand = new Random();
				//planningVoteAgent = aliveAgentExceptHumanList.get(rand.nextInt(aliveAgentExceptHumanList.size()));
			}else{
				planningVoteAgent = Util.SelectVoteTargetByOtherVote(aliveAgentList, voteTarget);


				//Random rand = new Random();
				//planningVoteAgent = aliveAgentList.get(rand.nextInt(aliveAgentList.size()));
			}
		}
		return;
	}



	@Override
	public void update(GameInfo gameInfo) {
		try{
		super.update(gameInfo);
		vi.Update(gameInfo);

		List<Talk> talkList = gameInfo.getTalkList();
		boolean voteTalked = false;

		/*
		 * talkListからCO，占い結果の抽出
		 */
		for(int i = readTalkListNum; i < talkList.size(); i++){
			Talk talk = talkList.get(i);
			Utterance utterance = new Utterance(talk.getContent());
			switch (utterance.getTopic()) {

			/*
			 * カミングアウトの発話の場合
			 * 自分以外で占い師COするプレイヤーが出たら投票先を変える
			 * 狂人/人狼とCOする人がいたら、狂人を設定
			 */
			case COMINGOUT:
				agi.getComingoutMap().put(talk.getAgent(), utterance.getRole());
				if(utterance.getRole() == fakeRole){
					setPlanningVoteAgent();
				}
				else if(utterance.getRole() == Role.POSSESSED)
				{
					maybePossesedAgent = utterance.getTarget();
				}
				else if(utterance.getRole() == Role.WEREWOLF)
				{
					if(!getWolfList().contains(utterance.getTarget()))
					{
						maybePossesedAgent = utterance.getTarget();
					}
				}
				break;

			/*
			 * 占い結果の発話の場合
			 * 人狼以外の占い，霊能結果で嘘だった場合は狂人だと判断
			 */
			case DIVINED:
				//AGIのJudgeListに結果を加える
				Agent seerAgent = talk.getAgent();
				Agent inspectedAgent = utterance.getTarget();
				Species inspectResult = utterance.getResult();
				Judge judge = new Judge(getDay(), seerAgent, inspectedAgent, inspectResult);
				agi.addInspectJudgeList(judge);

				//ジャッジしたのが人狼以外の場合
				if(!getWolfList().contains(judge.getAgent())){
					Species judgeSpecies = judge.getResult();
					Species realSpecies;
					if(getWolfList().contains(judge.getTarget())){
						realSpecies = Species.WEREWOLF;
					}else{
						realSpecies = Species.HUMAN;
					}
					if(judgeSpecies != realSpecies){
						maybePossesedAgent = judge.getAgent();
						setPlanningVoteAgent();
					}
				}

				break;
			case VOTE:
				if(talk.getAgent()!=getMe())
				{
					//他の人が投票先を変更した場合考えなおす
					voteTalked = true;
				}
				break;
			}
		}
		readTalkListNum =talkList.size();

		// パワープレイ可能か
		UpdateCanPp();

		if(voteTalked)
		{
			setPlanningVoteAgent();
		}
		}catch (Exception e){}
	}

	// パワープレイ可能か
	private void UpdateCanPp()
	{
		if(canPp) return;

		// 生存者
		List<Agent> alivers = getLatestDayGameInfo().getAliveAgentList();
		int aliversNum = alivers.size();

		// 偶数進行、８人以上はPP不可
		if(aliversNum > getGameSetting().getRoleNum(Role.WEREWOLF) * 2 + 1 || (aliversNum % 2) == 0)
		{
			canPp = false;
			return;
		}

		// 人狼
		List<Agent> aliveWolves = Util.AndList(getWolfList(), alivers);

		// 人間
		List<Agent> aliveHumans = getLatestDayGameInfo().getAliveAgentList();
		aliveHumans.removeAll(aliveWolves);

		// 狂人が生存者の中に存在するか
		boolean existsPossesedInAlivers = false;
		// 前から目星つけてたやつが、生存者の中にいる
		if(aliveHumans.contains(maybePossesedAgent))
		{
			existsPossesedInAlivers = true;
		}
		else
		{
			// ルール上の役職者数より多いCO者が、人間の中にいる
			if(Util.AndList(agi.getComingoutAgents(Role.SEER), aliveHumans).size() > getGameSetting().getRoleNum(Role.SEER)
					|| Util.AndList(agi.getComingoutAgents(Role.MEDIUM), aliveHumans).size() > getGameSetting().getRoleNum(Role.MEDIUM)
					|| Util.AndList(agi.getComingoutAgents(Role.BODYGUARD), aliveHumans).size() > getGameSetting().getRoleNum(Role.BODYGUARD))
			{
				existsPossesedInAlivers = true;
			}
		}

		// 狂人がいて、かつ人狼の数と人狼以外の数が同じとき、PP可
		if(existsPossesedInAlivers)
		{
			if(aliveWolves.size() == aliveHumans.size() - 1)
			{
				canPp = true;
				setPlanningVoteAgent();
				return;
			}
		}

		canPp = false;
	}

	/**
	 * )能力者騙りをする際に，偽の占い(or霊能)結果を作成する．
	 */
	public void setFakeResult(){
		/*
		 * 村人騙りなら不必要
		 */

		//偽占い(or霊能)の候補．以下，偽占い候補
		List<Agent> fakeGiftTargetCandidateList = new ArrayList<Agent>();

		Agent fakeGiftTarget;

		Species fakeResult;

		if(fakeRole == Role.VILLAGER){
			return;
		}
		else if(fakeRole == Role.SEER){


			List<Agent> aliveAgentList = getLatestDayGameInfo().getAliveAgentList();
			aliveAgentList.remove(getMe());

			for(Agent agent: aliveAgentList){
				//まだ偽占いしてないプレイヤー，かつ対抗CO者じゃないプレイヤーは偽占い候補
				if(!isJudgedAgent(agent) && fakeRole != agi.getComingoutMap().get(agent)){
					fakeGiftTargetCandidateList.add(agent);
				}
			}

			if(fakeGiftTargetCandidateList.size() > 0){
				Random rand = new Random();
				fakeGiftTarget = fakeGiftTargetCandidateList.get(rand.nextInt(fakeGiftTargetCandidateList.size()));
			}else{
				aliveAgentList.removeAll(fakeGiftTargetCandidateList);
				Random rand = new Random();
				fakeGiftTarget = aliveAgentList.get(rand.nextInt(aliveAgentList.size()));
			}

			/*
			 * 人狼が偽占い対象の場合
			 */
			if(getWolfList().contains(fakeGiftTarget)){
				fakeResult = Species.HUMAN;
			}
			/*
			 * 人間が偽占い対象の場合
			 */
			else{
				//狂人(暫定)，または非COプレイヤー
				if(fakeGiftTarget == maybePossesedAgent || !agi.getComingoutMap().containsKey(fakeGiftTarget)){
					if(Math.random() < param.WerewolfJudgeDivineBlackRatio){
						fakeResult = Species.WEREWOLF;
					}else{
						fakeResult = Species.HUMAN;
					}
				}
				//能力者CO，かつ人間，非狂人(暫定)
				else{
					fakeResult = Species.WEREWOLF;
				}
			}
		}

		else if(fakeRole == Role.MEDIUM){
			fakeGiftTarget = getLatestDayGameInfo().getExecutedAgent();
			if(fakeGiftTarget == null){
				return;
			}
			/*
			 * 人狼が偽占い対象の場合
			 */
			if(getWolfList().contains(fakeGiftTarget)){
				fakeResult = Species.HUMAN;
			}
			/*
			 * 人間が偽占い対象の場合
			 */
			else{
				//狂人(暫定)，または非COプレイヤー
				if(fakeGiftTarget == maybePossesedAgent || !agi.getComingoutMap().containsKey(fakeGiftTarget)){
					if(ShouldJudgeWhiteIfIamFakeSeer(fakeGiftTarget)){
						fakeResult = Species.HUMAN;
					}else{
						fakeResult = Species.WEREWOLF;
						fakeJudgeBlackCount++;
					}
				}
				//能力者CO，かつ人間，非狂人(暫定)
				else{
					fakeResult = Species.WEREWOLF;
				}
			}
		}else{
			return;
		}

		if(fakeGiftTarget != null){
			fakeJudgeList.add(new Judge(getDay(), getMe(), fakeGiftTarget, fakeResult));
		}
	}

	public List<Judge> getMyFakeJudgeList(){
		return fakeJudgeList;
	}

	private boolean ShouldJudgeWhiteIfIamFakeSeer(Agent a)
	{
		if(GameSetting.getRoleNum(Role.WEREWOLF) <= fakeJudgeBlackCount) return true;

		// なんとなくありそうな、でもちょっと高めの確率で、人間であると占う
		return Math.random() < (double)Math.max(2 - fakeJudgeBlackCount, 0) / (double)getLatestDayGameInfo().getAliveAgentList().size();
	}

	/**
	 * すでに占い(or霊能)対象にしたプレイヤーならtrue,まだ占っていない(霊能していない)ならばfalseを返す．
	 * @param myJudgeList
	 * @param agent
	 * @return
	 */
	public boolean isJudgedAgent(Agent agent){
		for(Judge judge: getMyFakeJudgeList()){
			if(judge.getTarget() == agent){
				return true;
			}
		}
		return false;
	}

}
