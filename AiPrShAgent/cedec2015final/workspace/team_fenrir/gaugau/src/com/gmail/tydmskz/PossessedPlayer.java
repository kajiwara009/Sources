package com.gmail.tydmskz;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.aiwolf.client.base.player.AbstractPossessed;
import org.aiwolf.client.lib.TemplateTalkFactory;
import org.aiwolf.client.lib.Utterance;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Judge;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Species;
import org.aiwolf.common.data.Talk;
import org.aiwolf.common.net.GameInfo;
import org.aiwolf.common.net.GameSetting;

import com.gmail.tydmskz.GameInfoGaFormatter.SpeciesEnum;

public class PossessedPlayer extends AbstractPossessed {

	//COする日にち
	int comingoutDay;

	//CO済みか否か
	boolean isCameout;
	boolean isPossessedCameout;

	//全体に偽占い(霊能)結果を報告済みのJudge
	ArrayList<Judge> declaredFakeJudgedAgentList = new ArrayList<Judge>();

	//全体に占い結果を報告済みのプレイヤー
//	ArrayList<Agent> declaredFakeResultAgent = new ArrayList<>();
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
	//Map<Agent, Species> fakeResultMap = new HashMap<Agent, Species>();

	//　人狼だと思うプレイヤー　PP用
	List<Agent> wolfAgents = null;

	// 現在の状態でパワープレイ可
	boolean canPp = false;

	PlayerParamaters param;


	public PossessedPlayer(PlayerParamaters p) {
		try{
		param = p;
		if(param.possesedShouldJudgeWhiteIfIamFakeSeer == null
				|| param.possesedShouldJudgeWhiteIfIamFakeMedium == null)
		{
			// throw new IllegalArgumentException("Please give me my parameters!");

			// てきとうにしょきか
			param.possesedShouldJudgeWhiteIfIamFakeSeer = new float[ParametersNum];
			param.possesedShouldJudgeWhiteIfIamFakeMedium = new float[ParametersNum];
			float ini = 1.0f / ParametersNum;
			for(int i=0; i<ParametersNum; i++)
			{
				param.possesedShouldJudgeWhiteIfIamFakeSeer[i] =
				param.possesedShouldJudgeWhiteIfIamFakeMedium[i] = ini;
			}
		}
		}catch (Exception e){}
	}

	@Override
	public void initialize(GameInfo gameInfo, GameSetting gameSetting){
		try{
		super.initialize(gameInfo, gameSetting);

		GameSetting = gameSetting;
//		List<Role> fakeRoleList = Arrays.asList(Role.SEER, Role.MEDIUM, Role.VILLAGER);
		List<Role> fakeRoles = new ArrayList(gameSetting.getRoleNumMap().keySet());
		List<Role> nonFakeRoleList = Arrays.asList(Role.BODYGUARD, Role.FREEMASON, Role.POSSESSED, Role.WEREWOLF);
		fakeRoles.removeAll(nonFakeRoleList);

		// カジ論文　狂人は占い師を騙るのが最適　霊媒師もそこまで悪くないので時々騙る
		// 騙りなしはダメ
		//fakeRole = fakeRoles.get(new Random().nextInt(fakeRoles.size()));
		fakeRole = Math.random() < 0.9 ? Role.SEER : Role.MEDIUM;

		//占い師，or霊能者なら1~3日目からランダムに選択してCO．村人ならCOしない．
		if(fakeRole == Role.VILLAGER){
			comingoutDay = 1000;
		}
		else{
			comingoutDay = new Random().nextInt(3)+1;
		}

		isCameout = false;
		isPossessedCameout = false;
		wolfAgents = new ArrayList<>();
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
		//PP, CO,霊能結果，投票先の順に発話の優先度高

		// PP　狂人であることをCO
		if(canPp)
		{
			if(!isPossessedCameout)
			{
				String string = TemplateTalkFactory.comingout(getMe(), Role.POSSESSED);
				isPossessedCameout = true;
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
		}catch (Exception e){return Talk.OVER;}
	}

	@Override
	public Agent vote() {
		return planningVoteAgent;
	}

	@Override
	public void finish() {
		// TODO 自動生成されたメソッド・スタブ

	}


	/**
	 * 今日投票予定のプレイヤーを設定する．
	 */
	public void setPlanningVoteAgent(){
		/*
		 * 村人騙りなら自分以外からランダム
		 * それ以外↓
		 * 対抗CO，もしくは自分が黒だと占ったプレイヤーからランダム
		 * いなければ白判定を出したプレイヤー以外からランダム
		 * それもいなければ生存プレイヤーからランダム
		 */

		List<Agent> aliveAgentList = getLatestDayGameInfo().getAliveAgentList();
		aliveAgentList.remove(getMe());

		if(fakeRole == Role.VILLAGER || canPp){
			if(aliveAgentList.contains(planningVoteAgent)){
				return;
			}else{
				Random rand = new Random();
				planningVoteAgent = aliveAgentList.get(rand.nextInt(aliveAgentList.size()));
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
			Map<Agent, Agent> voteTarget = vi.VoteTarget();
			voteTarget.remove(getMe());

			planningVoteAgent = Util.SelectVoteTargetByOtherVote(voteAgentCandidate, voteTarget);
			//planningVoteAgent = voteAgentCandidate.get(rand.nextInt(voteAgentCandidate.size()));
		}else{
			//自分が白判定を出していないプレイヤーのリスト
			List<Agent> aliveAgentExceptHumanList = getLatestDayGameInfo().getAliveAgentList();
			aliveAgentExceptHumanList.removeAll(fakeHumanList);

			if(aliveAgentExceptHumanList.size() > 0){
				Random rand = new Random();
				planningVoteAgent = aliveAgentExceptHumanList.get(rand.nextInt(aliveAgentExceptHumanList.size()));
			}else{
				Random rand = new Random();
				planningVoteAgent = aliveAgentList.get(rand.nextInt(aliveAgentList.size()));
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
		boolean existInspectResult = false;
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
			 * 狼または狂人だとCOする人は、狼認定
			 */
			case COMINGOUT:
				agi.getComingoutMap().put(talk.getAgent(), utterance.getRole());
				if(utterance.getRole() == fakeRole){
					setPlanningVoteAgent();
				}
				else if((utterance.getRole() == Role.WEREWOLF || utterance.getRole() == Role.POSSESSED)
						&& !wolfAgents.contains(utterance.getTarget()))
				{
					wolfAgents.add(utterance.getTarget());
				}
				break;

			//占い結果の発話の場合
			case DIVINED:
				//AGIのJudgeListに結果を加える
				Agent seerAgent = talk.getAgent();
				Agent inspectedAgent = utterance.getTarget();
				Species inspectResult = utterance.getResult();
				Judge judge = new Judge(getDay(), seerAgent, inspectedAgent, inspectResult);
				agi.addInspectJudgeList(judge);

				// 黒だと占われたら、その占い師は狼
				if(inspectedAgent == getMe() && inspectResult == Species.WEREWOLF
						&& !wolfAgents.contains(seerAgent))
				{
					wolfAgents.add(seerAgent);
				}

				existInspectResult =true;
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


		/*
		 * 新しい占い結果があれば投票先を変える．(新たに黒判定が出た，または投票先のプレイヤーに白判定が出た場合)
		 */
		if(existInspectResult || voteTalked){
			setPlanningVoteAgent();
		}

		UpdateCanPp();

		m_agiValues = null;
		}catch (Exception e){}
	}

	private void UpdateCanPp()
	{
		if(canPp) return;

		List<Agent> alivers = getLatestDayGameInfo().getAliveAgentList();
		int aliversNum = alivers.size();

		if((aliversNum % 2) == 0) canPp = false; // 偶数進行はPP不可

		List<Agent> aliveWolves = Util.AndList(wolfAgents, alivers);
		int aliveWolvesNum = aliveWolves.size();

		List<Agent> unknownRoleAlivePeople = getLatestDayGameInfo().getAliveAgentList();
		unknownRoleAlivePeople.remove(getMe());
		unknownRoleAlivePeople.removeAll(aliveWolves);

		// 生存者の中に人狼がいるとしたら、その人数を取得
		// ルール上の役職者数より多いCO者が、生存者の中にいる
		int seersNum = Util.AndList(agi.getComingoutAgents(Role.SEER), unknownRoleAlivePeople).size();
		if(seersNum > getGameSetting().getRoleNum(Role.SEER))
		{
			aliveWolvesNum += seersNum - getGameSetting().getRoleNum(Role.SEER);
		}

		int mediumsNum = Util.AndList(agi.getComingoutAgents(Role.MEDIUM), unknownRoleAlivePeople).size();
		if(mediumsNum > getGameSetting().getRoleNum(Role.MEDIUM))
		{
			aliveWolvesNum += mediumsNum - getGameSetting().getRoleNum(Role.MEDIUM);
		}

		int guardsNum = Util.AndList(agi.getComingoutAgents(Role.BODYGUARD), unknownRoleAlivePeople).size();
		if(guardsNum > getGameSetting().getRoleNum(Role.BODYGUARD))
		{
			aliveWolvesNum += guardsNum - getGameSetting().getRoleNum(Role.BODYGUARD);
		}

		// 人狼の数と人狼以外の数が同じとき、PP可
		if(aliveWolvesNum == aliversNum - aliveWolvesNum)
		{
			canPp = true;
			setPlanningVoteAgent();
			return;
		}

		canPp = false;
	}

	/**
	 * 能力者騙りをする際に，偽の占い(or霊能)結果を作成する．
	 */
	private boolean didPandaInspect = false; // パンダをやるのは１回のみ
	public void setFakeResult(){
		Agent fakeGiftTarget = null;

		Species fakeResult = null;

		if(fakeRole == Role.SEER){
			//偽占い(or霊能)の候補．以下，偽占い候補
			List<Agent> fakeGiftTargetCandidateList = new ArrayList<Agent>();

			List<Agent> aliveAgentList = getLatestDayGameInfo().getAliveAgentList();
			aliveAgentList.remove(getMe());

			// パンダ占い
			// 霊媒師とCOした人は全て死亡して、生きている霊媒師がいない場合、
			// 他の占い師が白と占った人をランダムで黒と占う
			if(!didPandaInspect)
			{
				boolean mediumDead;
				List<Agent> allMedium = agi.getComingoutAgents(Role.MEDIUM);
				if(!allMedium.isEmpty())
				{
					List<Agent> aliveMedium = Util.AndList(allMedium, aliveAgentList);
					if(aliveMedium.isEmpty()) mediumDead = true;
					else mediumDead = false;
				}
				else{ mediumDead = false; }

				if(mediumDead)
				{
					List<Agent> otherJudgeWhiteAgents = new ArrayList<>();
					for(Judge judge:agi.getInspectJudgeList())
					{
						if(judge.getAgent() != getMe()
								&& judge.getResult() == Species.HUMAN)
						{
							otherJudgeWhiteAgents.add(judge.getTarget());
						}
					}
					if(otherJudgeWhiteAgents.size() > 0)
					{
						fakeGiftTarget = otherJudgeWhiteAgents.get(Util.Rand.nextInt(otherJudgeWhiteAgents.size()));
						fakeResult = Species.WEREWOLF;
						fakeJudgeBlackCount++;
					}
				}
			}
			else
			{
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

				if(ShouldJudgeWhiteIfIamFakeSeer(fakeGiftTarget)){
					fakeResult = Species.HUMAN;
				}else{
					fakeResult = Species.WEREWOLF;
					fakeJudgeBlackCount++;
				}
			}
		}
		else if(fakeRole == Role.MEDIUM){
			fakeGiftTarget = getLatestDayGameInfo().getExecutedAgent();
			if(ShouldJudgeWhiteIfIamFakeMedium(fakeGiftTarget)){
				fakeResult = Species.HUMAN;
			}else{
				fakeResult = Species.WEREWOLF;
				fakeJudgeBlackCount++;
			}
		}
		else{
			return;
		}

		if(fakeGiftTarget != null){
			fakeJudgeList.add(new Judge(getDay(), getMe(), fakeGiftTarget, fakeResult));
		}
	}

	private final float m_threasholdWhiteJudgeIfIamFakeSeer = 0.7f;
	private final float m_threasholdWhiteJudgeIfIamFakeMedium = 0.7f;

	private boolean ShouldJudgeWhiteIfIamFakeSeer(Agent a)
	{
		if(GameSetting.getRoleNum(Role.WEREWOLF) <= fakeJudgeBlackCount) return true;

		float[] values = GetAgiValues(a);
		float res = Util.LinearCombine(param.possesedShouldJudgeWhiteIfIamFakeSeer, values);

		return res > m_threasholdWhiteJudgeIfIamFakeSeer;
	}

	private boolean ShouldJudgeWhiteIfIamFakeMedium(Agent a)
	{
		if(GameSetting.getRoleNum(Role.WEREWOLF) <= fakeJudgeBlackCount) return true;

		float[] values = GetAgiValues(a);
		float res = Util.LinearCombine(param.possesedShouldJudgeWhiteIfIamFakeMedium, values);

		return res > m_threasholdWhiteJudgeIfIamFakeMedium;
	}

	private float[] m_agiValues = null;
	private float[] GetAgiValues(Agent targetAgent)
	{
		if(m_agiValues != null) return m_agiValues;

		// coマップを参照して配列作成
		// そのロールであるとcoしてれば1.0, してなければ0.0
		float[] co = GameInfoGaFormatter.GetCoArray(agi, targetAgent);

		// 占い結果の配列
		float otherInspects[], myInspects[];
		otherInspects = GameInfoGaFormatter.GetInspectArrayExceptMe(agi, getMe(), targetAgent);
		myInspects = GetFakeInspectArray(targetAgent);

		m_agiValues = new float[ParametersNum];
		int j=0;
		for(j=0; j<co.length; j++)
		{
			m_agiValues[j] = co[j];
		}
		for(int k=0; k<SpeciesEnum.Count; j++, k++)
		{
			m_agiValues[j] = myInspects[k];
		}
		for(int k=0; k<SpeciesEnum.Count; j++, k++)
		{
			m_agiValues[j] = otherInspects[k];
		}
		return m_agiValues;
	}

	public static final int ParametersNum = Role.values().length + SpeciesEnum.Count * 2;


	public List<Judge> getMyFakeJudgeList(){
		return fakeJudgeList;
	}

	// 自分がtargetAgentを占った結果を、長さ3のfloat配列に入れて返す
	// 要素0:人間　要素1:人狼 要素2:占われていない
	// 占われていれば、対応する要素が1.0、その他が0.0
	// 占われていなければ、要素2が1.0、その他が0.0
	private float[] GetFakeInspectArray(Agent targetAgent)
	{
		float[] isp = new float[SpeciesEnum.Count];
		for(int i=0; i<isp.length; i++) isp[i] = 0.0f;

		Judge judge = null;
		for(Judge j:fakeJudgeList)
		{
			if(j.getTarget().equals(targetAgent))
			{
				judge = j;
				break;
			}
		}

		if(judge != null)
		{
			if(judge.getResult().equals(Species.HUMAN)) isp[SpeciesEnum.Human] = 1.0f;
			else isp[SpeciesEnum.Werewolf] = 1.0f;
		}
		else
		{
			isp[SpeciesEnum.Unknown] = 1.0f;
		}

		return isp;
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
