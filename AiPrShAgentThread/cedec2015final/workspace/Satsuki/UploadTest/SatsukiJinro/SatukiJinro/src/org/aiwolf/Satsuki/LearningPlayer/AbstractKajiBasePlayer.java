package org.aiwolf.Satsuki.LearningPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.aiwolf.Satsuki.lib.AdvanceGameInfo;
import org.aiwolf.Satsuki.lib.CauseOfDeath;
import org.aiwolf.Satsuki.lib.DeadCondition;
import org.aiwolf.Satsuki.lib.EnemyCase;
import org.aiwolf.Satsuki.lib.MyGameInfo;
import org.aiwolf.Satsuki.lib.Pattern;
import org.aiwolf.Satsuki.lib.PatternMaker;
import org.aiwolf.Satsuki.reinforcementLearning.ActionLog;
import org.aiwolf.Satsuki.reinforcementLearning.AgentPattern;
import org.aiwolf.Satsuki.reinforcementLearning.COPercent;
import org.aiwolf.Satsuki.reinforcementLearning.LearningData;
import org.aiwolf.Satsuki.reinforcementLearning.Qvalues;
import org.aiwolf.Satsuki.reinforcementLearning.ReinforcementLearning;
import org.aiwolf.Satsuki.reinforcementLearning.SelectStrategy;
import org.aiwolf.client.base.player.AbstractRole;
import org.aiwolf.client.lib.TemplateTalkFactory;
import org.aiwolf.client.lib.Utterance;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Judge;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Species;
import org.aiwolf.common.data.Status;
import org.aiwolf.common.data.Talk;
import org.aiwolf.common.data.Vote;
import org.aiwolf.common.net.GameInfo;
import org.aiwolf.common.net.GameSetting;

/**
 * 全役職共通部分のアルゴリズム
 * initialize：初期パターン作成
 * update：発話ログからAGI更新，Pattern更新
 * dayStart：AGIの死亡プレイヤーを更新
 * @author kengo
 *
 */
public abstract class AbstractKajiBasePlayer extends AbstractRole {

	private boolean IS_LEARNING = true;

	private static double EPSILON = 0.1;
	private static double TEMP = 1.0;


	// CO,能力の結果などのデータ集合
	protected AdvanceGameInfo advanceGameInfo = new AdvanceGameInfo();

	// ありうるパターン全て
	protected List<Pattern> generalPatterns = new ArrayList<Pattern>();

	// 自分の役職を入れたパターン
	protected List<Pattern> myPatterns = new ArrayList<Pattern>();

	// トークをどこまで読んだか
	protected int readTalkNumber = 0;

	// 今日投票するプレイヤー(暫定)
	protected Agent voteTarget = null;

	// 最新の発話で言った投票先プレイヤー
	protected Agent toldVoteTarget = null;
	
	// 直前に攻撃されたエージェント
	protected Agent lastAttackedAgent = null;

	// 直前に処刑されたエージェント
	protected Agent lastExecutedAgent = null;
	
	// 信頼できる占い師
	protected Agent believeAgent = null;
	
	// 投票しないエージェントリスト
	protected List<Agent> notVoteAgentList = new ArrayList<Agent>();

	// 人間はmyPatternを入れたもの，人狼側はfakePatternsを入れたもの．
	protected List<List<Pattern>> myPatternLists = new ArrayList<List<Pattern>>();

	// 学習データ
	protected LearningData ld = LearningData.getInstance(0);
	
	// COログ？
	protected COPercent coPercent = COPercent.GetInstance(0);
	
	// COした狼の内本物っぽいもの
	List<Agent> coWolfPlayers = new ArrayList<Agent>();
	
	long		updateStartTime;
	
	//その日何回目のtalkか
	protected int dayTalkCount;

	public boolean isWolf(Agent agent)
	{
		return false;
	}

	@Override
	public void initialize(GameInfo gameInfo, GameSetting gameSetting) 
	{
		/*
		 * パターン生成
		 */
		super.initialize(gameInfo, gameSetting);

		// 初期パターンの作成
		
		// CO,能力の結果などのデータ集合
		advanceGameInfo = new AdvanceGameInfo();

		// ありうるパターン全て
		generalPatterns = new ArrayList<Pattern>();

		// 自分の役職を入れたパターン
		myPatterns = new ArrayList<Pattern>();

		// トークをどこまで読んだか
		readTalkNumber = 0;

		// 今日投票するプレイヤー(暫定)
		voteTarget = null;

		// 最新の発話で言った投票先プレイヤー
		toldVoteTarget = null;

		myPatternLists = new ArrayList<List<Pattern>>();

		List<Agent> aliveAgents = gameInfo.getAliveAgentList();
		
		generalPatterns.add(new Pattern(null, null, null, new HashMap<Agent, Role>(), aliveAgents));
		Pattern initialPattern;
		switch (getMyRole()) 
		{
		case SEER:
			initialPattern = new Pattern(getMe(), null, null, new HashMap<Agent, Role>(), aliveAgents);
			break;
		case MEDIUM:
			initialPattern = new Pattern(null, getMe(), null, new HashMap<Agent, Role>(), aliveAgents);
			break;
		case BODYGUARD:
			initialPattern = new Pattern(null, null, getMe(), new HashMap<Agent, Role>(), aliveAgents);
			break;
		default:
			initialPattern = new Pattern(null, null, null, new HashMap<Agent, Role>(), aliveAgents);
			break;
		}
		myPatterns.add(initialPattern);
		
		PatternMaker.settleAgentRole(myPatterns, getMe(), getMyRole());	
		
		coPercent.gameStart();
	//	actionLog.gameStart();
	}
	
	/**
	 * patternにおけるagentが各役職に何パーセントでなっているか返す
	 * @param pattern
	 * @param agent
	 * @param aliveAgents
	 * @return
	 */
	public Map<Role, Double> getRoleProbabilitys(Pattern pattern, Agent agent, List<Agent> aliveAgents)
	{
		Map<Role, Double> roleProbabilitys = new HashMap<Role, Double>();

		Map<Role, Integer> roleNumMap = new HashMap<Role, Integer>(getGameSetting().getRoleNumMap());

		if(pattern.getSeerAgent() != null)
		{
			if(pattern.getSeerAgent().equals(agent))
			{
				roleProbabilitys.put(Role.SEER, 1.0);
				return roleProbabilitys;
			}
			roleNumMap.put(Role.SEER, 0);
		}
		if(pattern.getMediumAgent() != null)
		{
			if(pattern.getMediumAgent().equals(agent))
			{
				roleProbabilitys.put(Role.MEDIUM, 1.0);
				return roleProbabilitys;
			}
			roleNumMap.put(Role.MEDIUM, 0);
		}

		Map<Agent, EnemyCase> enemyMap = pattern.getEnemyMap();
		if(enemyMap.size() != 0)
		{
			int restBlackNum = roleNumMap.get(Role.WEREWOLF);
			int restWhiteNum = roleNumMap.get(Role.POSSESSED);
			for(Entry<Agent, EnemyCase> set: enemyMap.entrySet())
			{
				if(set.getValue() == EnemyCase.black)
				{
					restBlackNum--;
				}
				else if(set.getValue() == EnemyCase.white)
				{
					restWhiteNum--;
				}
			}
			roleNumMap.put(Role.WEREWOLF, restBlackNum);
			roleNumMap.put(Role.POSSESSED, restWhiteNum);

			if(enemyMap.containsKey(agent))
			{
				switch (enemyMap.get(agent)) 
				{
				case black:
					roleProbabilitys.put(Role.WEREWOLF, 1.0);
					break;
				case white:
					roleProbabilitys.put(Role.POSSESSED, 1.0);
					break;
				case gray:
					roleProbabilitys.put(Role.WEREWOLF, (double)restBlackNum/((double)restBlackNum + (double)restWhiteNum));
					roleProbabilitys.put(Role.POSSESSED, (double)restWhiteNum/((double)restBlackNum + (double)restWhiteNum));
					break;
				}
				return roleProbabilitys;
			}
		}
		int restRoleNum = 0;
		for(Entry<Role, Integer> set: roleNumMap.entrySet())
		{
			restRoleNum += set.getValue();
		}

		//白確リストに入っている場合
		if(pattern.getWhiteAgentSet().contains(agent))
		{
			for(Entry<Role, Integer> set: roleNumMap.entrySet())
			{
				if(set.getKey() != Role.WEREWOLF)
				{
					roleProbabilitys.put(set.getKey(), (double)roleNumMap.get(set.getKey())/((double)restRoleNum - (double)roleNumMap.get(Role.WEREWOLF)));
				}
			}
		}
		//白確リストにも入っていない場合
		else
		{
			for(Entry<Role, Integer> set: roleNumMap.entrySet())
			{
				roleProbabilitys.put(set.getKey(), (double)roleNumMap.get(set.getKey())/((double)restRoleNum));
			}
		}
		return roleProbabilitys;
	}

	@Override
	public void update(GameInfo gameInfo)
	{
		updateStartTime = System.currentTimeMillis();
		
		// 100ms対策のgc。いつもは切っておく
        //Runtime rt = Runtime.getRuntime();
        //rt.gc();
		// System.gc();
        // gcタイムチェック
		
		if(MyGameInfo.IS_PRINT()) System.out.printf("%d:", getMe().getAgentIdx());
		/*
		 * 会話の処理
		 * 暫定投票先の更新
		 */
		super.update(gameInfo);

		List<Talk> talkList = gameInfo.getTalkList();

		/*
		 * 各発話についての処理
		 * カミングアウトについてはパターンの拡張
		 * 能力結果の発話についてはパターン情報の更新
		 */
		boolean patternChanged = false;
		boolean beliaveChanged = false;
		for(; readTalkNumber < talkList.size(); readTalkNumber++)
		{
			Talk talk = talkList.get(readTalkNumber);
			Utterance utterance = new Utterance(talk.getContent());
			switch (utterance.getTopic()) 
			{
			case COMINGOUT:
			{
				long start3 = System.currentTimeMillis();
				comingoutTalkDealing(talk, utterance);
				long end3 = System.currentTimeMillis();
				if (end3 - start3 >= 50)
				{
					int time = (int)(end3 - start3);
					System.err.printf("長い：0012:%d\n",time);
				}
				patternChanged = true;				
				beliaveChanged = true;
			}
				break;

			case DIVINED:
			{
				long start3 = System.currentTimeMillis();
				divinedTalkDealing(talk, utterance);
				long end3 = System.currentTimeMillis();
				if (end3 - start3 >= 50)
				{
					int time = (int)(end3 - start3);
					System.err.printf("長い：0013:%d\n",time);
				}
				patternChanged = true;
				beliaveChanged = true;
			}
				break;

			case INQUESTED:
			{
				long start3 = System.currentTimeMillis();
				inquestedTalkDealing(talk, utterance);
				long end3 = System.currentTimeMillis();
				if (end3 - start3 >= 50)
				{
					int time = (int)(end3 - start3);
					System.err.printf("長い：0014:%d\n",time);
				}
				patternChanged = true;
			}
				break;

			case GUARDED:
			{
				long start3 = System.currentTimeMillis();
				guardedTalkDealing(talk, utterance);
				long end3 = System.currentTimeMillis();
				if (end3 - start3 >= 50)
				{
					int time = (int)(end3 - start3);
					System.err.printf("長い：0015:%d\n",time);
				}
				patternChanged = true;
			}
				patternChanged = true;
				break;
				
			case VOTE:
			{
				long start3 = System.currentTimeMillis();
				voteTalkDealing(talk, utterance);
				long end3 = System.currentTimeMillis();
				if (end3 - start3 >= 50)
				{
					int time = (int)(end3 - start3);
					System.err.printf("長い：0016:%d\n",time);
				}
				patternChanged = true;
			}
				break;
				//上記以外
			default:
				break;
			}
		}
		
		// (村人専用スキル) 信頼できる占い師の更新
		if (isVillager())
		{
			if (getDay() >= 3 && beliaveChanged)
			{
				List<Agent> seerAgents = new ArrayList<Agent>(PatternMaker.getAllSeerAgents(myPatterns));
				believeAgent = coPercent.getBeliaveSeer(seerAgents);				
			}
		}
		
		// 投票先を更新(更新する条件などはサブクラスで記載)
		long start2 = System.currentTimeMillis();
		long end2 = 0;
		if(patternChanged)
		{
			setVoteTarget();
			end2 = System.currentTimeMillis();
			if (end2 - start2 >= 200)
			{
				int time = (int)(end2 - start2);
				System.err.printf("長い：0010:%d\n",time);
			}
		}
		long end = System.currentTimeMillis();
		
		if (end - updateStartTime >= 200)
		{
			int time = (int)(end - updateStartTime);
			System.err.printf("長い：0011:%d\n",time);
		}
	}

	/**
	 * カミングアウトの発話の処理
	 * @param talk
	 * @param utterance
	 */
	public void comingoutTalkDealing(Talk talk, Utterance utterance)
	{
		long start = System.currentTimeMillis();
		// 人狼、狂人COはまともな判定をしない
		if (utterance.getRole() == Role.WEREWOLF || utterance.getRole() == Role.POSSESSED)
		{
			// wolfsideの場合は、ここでは処理しない。
			if (isVillager())
			{
				if (coWolfPlayers.contains(talk.getAgent()) == false)
				{
					coWolfPlayers.add(talk.getAgent());					
				}
			}
			long end = System.currentTimeMillis();
			
			if (end - start >= 500)
			{
				System.err.println("長い");
			}
			return;
		}
		
		if (utterance.getRole() == Role.SEER)
		{
			coPercent.memoryCODay(talk.getAgent(), getDay(), advanceGameInfo.getComingoutMap());
		}

		advanceGameInfo.putComingoutMap(talk.getAgent(), utterance.getRole());
		long end = System.currentTimeMillis();
		if (end - start >= 500)
		{
			int time = (int)(end - start);
			System.err.printf("長い：0031:%d\n",time);
		}
		PatternMaker.extendPatternList(generalPatterns, talk.getAgent(), utterance.getRole(), advanceGameInfo);
		PatternMaker.extendPatternList(myPatterns, talk.getAgent(), utterance.getRole(), advanceGameInfo);
		
		end = System.currentTimeMillis();
		
		if (end - start >= 500)
		{
			int time = (int)(end - start);
			System.err.printf("長い：0030:%d\n",time);
		}

	//	actionLog.AddCO(talk.getAgent(), getDay(), 0, utterance.getRole());

	}

	/**
	 * 引数が違うバージョン
	 * @param talk
	 * @param utterance
	 */
	public void comingoutTalkDealing(Agent talker, Role role)
	{
		if (role == Role.SEER)
		{
			coPercent.memoryCODay(talker, getDay(), advanceGameInfo.getComingoutMap());
		}
		advanceGameInfo.putComingoutMap(talker, role);
		PatternMaker.extendPatternList(generalPatterns, talker, role, advanceGameInfo);
		PatternMaker.extendPatternList(myPatterns, talker, role, advanceGameInfo);

	//	actionLog.AddCO(talker, getDay(), 0, role);
	}

	/**
	 * 占い結果の発話の処理
	 * @param talk
	 * @param utterance
	 */
	public void divinedTalkDealing(Talk talk, Utterance utterance)
	{
		if(advanceGameInfo.getComingoutMap().get(talk.getAgent()) != Role.SEER)
		{
			comingoutTalkDealing(talk.getAgent(), Role.SEER);
		}
		if (utterance.getResult() == Species.WEREWOLF)
		{
			coPercent.judgeCODay(talk.getAgent(), getDay(), advanceGameInfo.getComingoutMap(), true);			
		}
		else
		{
			coPercent.judgeCODay(talk.getAgent(), getDay(), advanceGameInfo.getComingoutMap(), false);			
		}
		
		Judge inspectJudge = new Judge(getDay(), talk.getAgent(), utterance.getTarget(), utterance.getResult());
		advanceGameInfo.addInspectJudges(inspectJudge);
		PatternMaker.updateJudgeData(generalPatterns, inspectJudge);
		PatternMaker.updateJudgeData(myPatterns, inspectJudge);

	//	actionLog.SetMeaningTalkOrder(talk.getAgent(), getDay(), Role.SEER);
	}

	/**
	 * 霊能結果の発話の処理
	 * @param talk
	 * @param utterance
	 */
	public void inquestedTalkDealing(Talk talk, Utterance utterance)
	{
		if(advanceGameInfo.getComingoutMap().get(talk.getAgent()) != Role.MEDIUM)
		{
			comingoutTalkDealing(talk.getAgent(), Role.MEDIUM);
		}
		Judge tellingJudge = new Judge(getDay(), talk.getAgent(), utterance.getTarget(), utterance.getResult());
		advanceGameInfo.addMediumJudges(tellingJudge);
		PatternMaker.updateJudgeData(generalPatterns, tellingJudge);
		PatternMaker.updateJudgeData(myPatterns, tellingJudge);

	//	actionLog.SetMeaningTalkOrder(talk.getAgent(), getDay(), Role.MEDIUM);
	}

	/**
	 * ガード結果の発話の処理
	 * @param talk
	 * @param utterance
	 */
	public void guardedTalkDealing(Talk talk, Utterance utterance)
	{
		if(advanceGameInfo.getComingoutMap().get(talk.getAgent()) != Role.BODYGUARD)
		{
			comingoutTalkDealing(talk.getAgent(), Role.BODYGUARD);
		}
		
		// 直前の襲撃が存在しなくて、ガードしたと宣言しているユニットが生きていたらpatternの作成を行う
		if (
			lastAttackedAgent == null 
			&& getLatestDayGameInfo().getAliveAgentList().contains(utterance.getTarget())				
		)
		{
			PatternMaker.UpdateGuardedData(generalPatterns, talk.getAgent(), utterance.getTarget());
			PatternMaker.UpdateGuardedData(myPatterns, talk.getAgent(), utterance.getTarget());
		}
	}
	
	/**
	 * 投票意思の発話の処理
	 * @param talk
	 * @param utterance
	 */
	public void voteTalkDealing(Talk talk, Utterance utterance)
	{
		Vote vote = new Vote(getDay(), talk.getAgent(), utterance.getTarget());
		advanceGameInfo.addVote(getDay(), vote);
	}

	@Override
	public void dayStart() 
	{
		// 昨日のmyPatternsを学習用に保存
		addCopyToMyPatternLists();
		
		// talkカウンタの初期化
		dayTalkCount = 0;
		
		/*
		 * 死亡プレイヤー情報の更新
		 * 暫定投票先の更新
		 */
		readTalkNumber = 0;
		
		// 死亡したプレイヤーをAGIに記録
		// 処刑
		lastExecutedAgent = getLatestDayGameInfo().getExecutedAgent();
		PatternMaker.updateExecutedData(generalPatterns, lastExecutedAgent);
		PatternMaker.updateExecutedData(myPatterns, lastExecutedAgent);
		if(lastExecutedAgent != null)
		{
			DeadCondition executeddAgentCondition = new DeadCondition(lastExecutedAgent, getDay(), CauseOfDeath.executed);
			advanceGameInfo.addDeadConditions(executeddAgentCondition);
		}
		
		// 襲撃
		lastAttackedAgent = getLatestDayGameInfo().getAttackedAgent();
		PatternMaker.updateAttackedData(generalPatterns, lastAttackedAgent);
		PatternMaker.updateAttackedData(myPatterns, lastAttackedAgent);
		if(lastAttackedAgent != null)
		{
			DeadCondition attackedAgentCondition = new DeadCondition(lastAttackedAgent, getDay(), CauseOfDeath.attacked);
			advanceGameInfo.addDeadConditions(attackedAgentCondition);
		}

		//今日の暫定投票先
		toldVoteTarget = null;
		voteTarget = null;
		setVoteTarget();

	//	actionLog.dayUpdate(getDay(), lastAttackedAgent, lastExecutedAgent);
		
		// 村人系専用処理
		if (isVillager())
		{
			if (getDay() >= 1)
			{
				// coWolfPlayersと同じキャラに投票しているユニットをcoWolfPlayersに追加
				List<Vote> votes = getLatestDayGameInfo().getVoteList();
				List<Agent> coWolfTargetList = new ArrayList<Agent>();
				
				for (Vote vote: votes)
				{
					if (coWolfPlayers.contains(vote.getAgent()) && coWolfTargetList.contains(vote.getTarget()) == false)
					{
						coWolfTargetList.add(vote.getTarget());
					}
				}

				for (Vote vote: votes)
				{
					if (coWolfPlayers.contains(vote.getAgent()) == false && coWolfTargetList.contains(vote.getTarget()))
					{
						coWolfPlayers.add(vote.getAgent());
					}
				}				
				
				// coWolfPlayersから白確を除きたい。
			}
			
			// (村人専用スキル) 信頼できる占い師を統計的に出す
			if (getDay() >= 3)
			{
				List<Agent> seerAgents = new ArrayList<Agent>(PatternMaker.getAllSeerAgents(myPatterns));
				believeAgent = coPercent.getBeliaveSeer(seerAgents);				
			}
		}

		long end = System.currentTimeMillis();

		if (end - updateStartTime >= 200)
		{
			int time = (int)(end - updateStartTime);
			System.err.printf("長い：0040:%d\n",time);
		}
	}
	
	protected void addCopyToMyPatternLists() 
	{
		List<Pattern> copyPatterns = new ArrayList<Pattern>();
		
		for(Pattern p: myPatterns){
			copyPatterns.add(p.clone());
		}
		myPatternLists.add(copyPatterns);
	}
	

	@Override
	public String talk() 
	{
		long start = System.currentTimeMillis();

		String ret = onTalk();
		long end = System.currentTimeMillis();
		
		if (end - start >= 200)
		{
			int time = (int)(end - start);
			System.err.printf("長い：0020:%d\n",time);
		}
		return ret;
	}

	public String onTalk() 
	{
		// talkカウンタの増加。
		++dayTalkCount;
		
		/*
		 * 発話順序の優先度
		 * カミングアウト＞能力結果の発話＞投票先の発話
		 */
		// カミングアウトの発話
		{
			long start = System.currentTimeMillis();
			String comingoutReport = getComingoutText();
			long end = System.currentTimeMillis();
			if (end - start >= 500)
			{
				if(MyGameInfo.IS_PRINT()) 			System.err.println("長い");
			}
			if(comingoutReport != null)
			{
				return comingoutReport;
			}			
		}

		// 占い，霊能結果の発話
		{
			long start = System.currentTimeMillis();
			String judgeReport = getJudgeText();
			long end = System.currentTimeMillis();
			if (end - start >= 500)
			{
				if(MyGameInfo.IS_PRINT()) 			System.err.println("長い");
			}
			if(judgeReport != null)
			{
				return judgeReport;
			}			
		}


		// 投票先の発話
		if(toldVoteTarget != voteTarget && voteTarget != null)
		{
			String voteReport = TemplateTalkFactory.vote(voteTarget);
			toldVoteTarget = voteTarget;
			return voteReport;
		}

		//話すことが何もなければ
		return Talk.OVER;
	}

	/**
	 * 占い or 霊能結果の発話を行う．結果の報告をしない場合はnullを返す
	 * @return
	 */
	public abstract String getJudgeText();

	/**
	 * カミングアウトの発話を行う．COしない場合はnullを返す
	 * @return
	 */
	public abstract String getComingoutText();

	/**
	 * 今日投票予定のプレイヤーを決定する
	 * updateとdayStartの最後によばれる
	 * @return
	 */
	public abstract void setVoteTarget();

	
	// 投票するべきか否か
	public boolean CheckVote(Agent agent)
	{
		// 絶対投票しない
		if (notVoteAgentList.contains(agent))
		{
			return false;
		}
		
		// 死んでる
		if (getLatestDayGameInfo().getAliveAgentList().contains(agent) == false)
		{
			return false;
		}
		return true;
	}
	
	// CheckVoteが有効なエージェントの中で最も投票数の多いエージェントを取得
	public Agent getMostVoteAgent(List<Agent> agents)
	{
		Agent target = null;
		int votePoint = -1;
		
		// 後半の会話は、0票の思考を行わないように(なるべく)
		if (this.dayTalkCount >= 4)
		{
			votePoint = 0;
		}
		
		for(Agent agent: agents)
		{
			if (CheckVote(agent) == false) continue;
			int _votePoint = advanceGameInfo.getVoteNum(getDay(), agent);
			
			if (_votePoint > votePoint)
			{
				votePoint = _votePoint;
				target = agent;
			}
		}		
		return target;
	}
	
	public void setNotVoteAgentList(List<Pattern> patterns)
	{
		notVoteAgentList.clear();
		
		// すべてのパターンで占い師、霊媒師、狩人がもしあればそいつには投票しない。
		
		if (patterns.size() > 0)
		{
			Agent seerAgent = patterns.get(0).getSeerAgent();
			if (seerAgent != null)
			{
				for(Pattern p: patterns)
				{			
					if (p.getSeerAgent() != seerAgent)
					{
						seerAgent = null;
						break;
					}
				}			
			}		
			Agent mediumAgent = patterns.get(0).getMediumAgent();
			if (mediumAgent != null)
			{
				for(Pattern p: patterns)
				{			
					if (p.getMediumAgent() != mediumAgent)
					{
						mediumAgent = null;
						break;
					}
				}			
			}		
			Agent bodyGuardAgent = patterns.get(0).getBodyGuardAgent();
			if (bodyGuardAgent != null)
			{
				for(Pattern p: patterns)
				{			
					if (p.getBodyGuardAgent() != bodyGuardAgent)
					{
						bodyGuardAgent = null;
						break;
					}
				}			
			}			
			if (seerAgent != null)
			{	
				notVoteAgentList.add(seerAgent);
			}		
			if (mediumAgent != null)
			{	
				notVoteAgentList.add(mediumAgent);
			}		
			if (bodyGuardAgent != null)
			{	
				notVoteAgentList.add(bodyGuardAgent);
			}		
		}
				

		// 自分自身を追加
		if (notVoteAgentList.contains(getMe()) == false)
		{
			notVoteAgentList.add(getMe());
		}
	}
	
	// 2日以内でCO総数が3以下のとき、COユニットを追加
	public void setNotVoteAgentList2(List<Pattern> patterns)
	{
		if (getDay() > 2) return;
		if (advanceGameInfo.getComingoutMap().size() > 3)
		{
			return;
		}
		
		for(Entry<Agent, Role> co:advanceGameInfo.getComingoutMap().entrySet())
		{
			if (co.getValue() == Role.SEER || co.getValue() == Role.MEDIUM || co.getValue() == Role.BODYGUARD)
			{
				if (notVoteAgentList.contains(co.getKey()) == false)
				{
					notVoteAgentList.add(co.getKey());
				}
			}
		}
		
		return;
	}
	
	// 1-1	人狼確ユニットがいればそれに投票。
	// 1-2	人狼or狂人確ユニットがいればそれに投票。
	
	// 以下の処理は 2日以内でCO総数が3以下のとき、COユニットに対しては実行しない。
	// 1-3	COの内、最低一人は真占い師がいたと仮定した上で人狼確ユニットがいればそれに投票。
	// 1-4	COの内、最低一人は真占い師がいたと仮定した上で人狼or狂人確ユニットがいればそれに投票。
	// 1-5	人狼と占われているユニットのうち、最も票数が多くなりそうなものに投票
	public void setVoteTargetTemplate(List<Pattern> patterns)
	{
		// 矛盾データの削除(不要？)
		/*
		int tmpSize = patterns.size();
		PatternMaker.removeContradictPatterns(patterns);
		if (tmpSize != patterns.size())
		{
			System.out.print("ええ；；");
		}
		*/
		
		// 信頼できる占い師がいればそのキャラクタを使って思考する
		List<Pattern> basePatterns = new ArrayList<Pattern>(patterns);
		if (believeAgent != null)
		{
			PatternMaker.settleAgentRole(basePatterns, believeAgent, Role.SEER);
		}
		
		// 投票しないユニットの登録
		setNotVoteAgentList(basePatterns);
		
		// 変数初期化
		int aliveSize = getLatestDayGameInfo().getAliveAgentList().size();
		Agent target = null;

		// ====================================================================
		// 1-1 人狼確ユニットがいればそれに投票。
		
		// wolfCOキャラ
		if (coWolfPlayers.size() == 0)
		{
			List<Agent> voteTargetList = new ArrayList<Agent>(coWolfPlayers);		
			target = getMostVoteAgent(voteTargetList);

			if (target != null)
			{
				if(MyGameInfo.IS_PRINT()) 	System.out.println("vote: 1-1-1");
				voteTarget = target;
				return;
			}
		}

		// 人狼、狂人確定エージェントを取得。
		if (basePatterns.size() != 0)
		{
			List<Agent> wolfDetermineAgent = new ArrayList<Agent>(getLatestDayGameInfo().getAliveAgentList());
			List<Agent> possessedDetermineAgent = new ArrayList<Agent>(getLatestDayGameInfo().getAliveAgentList());
			
			// 取得開始
			PatternMaker.getDetermineEnemyAgent(basePatterns, wolfDetermineAgent, possessedDetermineAgent);

			target = getMostVoteAgent(wolfDetermineAgent);
			
			if (target != null)
			{
				if(MyGameInfo.IS_PRINT()) System.out.println("vote: 1-1-2");
				voteTarget = target;
				return;
			}			
		}

		// ====================================================================
		// 1-2 人狼or狂人確ユニットがいればそれに投票。
		if (basePatterns.size() != 0)
		{
			List<Agent> aliveEnemyAgent = new ArrayList<Agent>(getLatestDayGameInfo().getAliveAgentList());
			PatternMaker.getDetermineEnemyAgent(basePatterns, aliveEnemyAgent);

			target = getMostVoteAgent(aliveEnemyAgent);
			
			if (target != null)
			{
				if(MyGameInfo.IS_PRINT()) System.out.println("vote: 1-2");
				voteTarget = target;
				return;
			}			
		}
		
		setNotVoteAgentList2(basePatterns);

		// ====================================================================
		// 1-3 COに真占い師がいたと仮定した上で人狼確ユニットがいればそれに投票。
		// 1-4 COに真占い師がいたと仮定した上で人狼or狂人確ユニットがいればそれに投票。
		{
			// 1-3 COの内、最低一人は真占い師がいたと仮定した上で人狼確ユニットがいればそれに投票。
		
			// 真占い師付パターンの用意
			List<Pattern> hasSeerPatterns = new ArrayList<Pattern>(basePatterns);
			List<Pattern> subPatterns = new ArrayList<Pattern>();
			
			for (Pattern pattern: hasSeerPatterns)
			{
				if (pattern.getSeerAgent() == null) subPatterns.add(pattern);
			}
			hasSeerPatterns.removeAll(subPatterns);
	
			if (hasSeerPatterns.size() != 0)
			{
				List<Agent> wolfDetermineAgent = new ArrayList<Agent>(getLatestDayGameInfo().getAliveAgentList());
				List<Agent> possessedDetermineAgent = new ArrayList<Agent>(getLatestDayGameInfo().getAliveAgentList());

				// 取得開始
				PatternMaker.getDetermineEnemyAgent(hasSeerPatterns, wolfDetermineAgent, possessedDetermineAgent);
				target = getMostVoteAgent(wolfDetermineAgent);
				
				if (target != null)
				{
					if(MyGameInfo.IS_PRINT()) System.out.println("vote: 1-3");
					voteTarget = target;
					return;
				}			
				
				// 1-4 COの内、最低一人は真占い師がいたと仮定した上で人狼or狂人確ユニットがいればそれに投票。
				List<Agent> aliveEnemyAgent = new ArrayList<Agent>(getLatestDayGameInfo().getAliveAgentList());
				PatternMaker.getDetermineEnemyAgent(hasSeerPatterns, aliveEnemyAgent);

				target = getMostVoteAgent(aliveEnemyAgent);
				
				if (target != null)
				{
					if(MyGameInfo.IS_PRINT()) System.out.println("vote: 1-4");
					voteTarget = target;
					return;
				}					
			}
		}
		// ====================================================================
		// 1-5　人狼と占われているユニットのうち、最も票数が多くなりそうなものに投票
		// 全ての占い師ユニットの取得
		List<Agent> seerAgents = new ArrayList<Agent>(PatternMaker.getAllSeerAgents(patterns));
		List<Agent> judgedWolfs = new ArrayList<Agent>();
		
		for (Judge judge: advanceGameInfo.getInspectJudges())
		{
			if (judge.getResult() == Species.WEREWOLF && seerAgents.contains(judge.getAgent()))
			{
				judgedWolfs.add(judge.getTarget());
			}
		}
		target = getMostVoteAgent(judgedWolfs);
		
		if (target != null)
		{
			if(MyGameInfo.IS_PRINT()) System.out.println("vote: 1-5");
			voteTarget = target;
			return;
		}

		// ====================================================================
		// 一定数以上同職で宣誓しているユニットがいれば適当に投票
		Role []roleList = new Role[3];
		int []roleCONum = new int[3];
		roleList[0] = Role.MEDIUM;
		roleList[1] = Role.SEER;
		roleList[2] = Role.BODYGUARD;
		
		int killNum = 4;
		if (aliveSize < 12) killNum = 3;
		if (aliveSize < 8) killNum = 2;
		if (aliveSize < 4) killNum = 1;
		
		for (int i = 0; i < 3; ++i)
		{
			roleCONum[i] = 0;
			List<Agent> coAgent = new ArrayList<Agent>();
			for (Entry<Agent, Role> co: advanceGameInfo.getComingoutMap().entrySet())
			{
				if (co.getValue() == roleList[i])
				{
					coAgent.add(co.getKey());
					roleCONum[i]++;
				}
			}
			if (roleCONum[i] >= killNum)
			{
				target = getMostVoteAgent(coAgent);
				if (target != null)
				{
					if(MyGameInfo.IS_PRINT()) System.out.println("vote: 1-a");
					voteTarget = target;
					return;
				}
			}
		}

		// ====================================================================
		// roleCONumの結果、全COを倒せばいいと判明したらCOを順番に倒していく。
		int restCO = 0;
		int allCO = 0;
		for (int i = 0; i < 3; ++i)
		{
			if (roleCONum[i] >= 2)
			{
				restCO += roleCONum[i] - 1;
			}
			allCO += roleCONum[i];
		}

		// ====================================================================
		// 3人以上あまりがいれば状況に応じて全COの処刑も必要だと判定。
		// 4人以上なら全COの処刑が確定(正CO確定は除く(まず見つからないけど・・・))
		if (
			(restCO >= 3 && aliveSize >= allCO * 2 + 3)
			|| restCO >= 4
		)
		{
			List<Agent> coAgent = new ArrayList<Agent>();
			for (Entry<Agent, Role> co: advanceGameInfo.getComingoutMap().entrySet())
			{
				if (co.getValue() == Role.SEER || co.getValue() == Role.MEDIUM || co.getValue() == Role.BODYGUARD)
				{
					coAgent.add(co.getKey());
				}
			}
			
			target = getMostVoteAgent(coAgent);
			if (target != null)
			{
				if(MyGameInfo.IS_PRINT()) System.out.println("vote: 1-b");
				voteTarget = target;
				return;
			}
		}
		
		// ====================================================================
		// グレから一番票を稼いでいるユニットに投票
		List<Agent> allGrayAgents = new ArrayList<Agent>(getLatestDayGameInfo().getAliveAgentList());
		for (Judge judge: advanceGameInfo.getInspectJudges())
		{
			if (seerAgents.contains(judge.getAgent()))
			{
				allGrayAgents.remove(judge.getAgent());
			}
		}
		target = getMostVoteAgent(allGrayAgents);
		if (target != null)
		{
			if(MyGameInfo.IS_PRINT()) System.out.println("vote: 1-c");
			voteTarget = target;
			return;
		}

		// ====================================================================
		// 最も投票を稼いでいるユニットに投票
		if(MyGameInfo.IS_PRINT()) System.out.println("vote: 1-d");
		target = getMostVoteAgent(getLatestDayGameInfo().getAliveAgentList());
		voteTarget = target;
		return;
	}

	public static <T>T selectRandomTarget(Map<T, Double> map)
	{
		return SelectStrategy.randomSelect(map);
	}
	public <T>T selectGreedyTarget(Map<T, Double> map)
	{
		if(IS_LEARNING)
		{
			return SelectStrategy.greedyselect(map, EPSILON);
		}
		else
		{
			return SelectStrategy.getMaxDoubleValueKey(map);
		}
	}
	public <T>T selectSoftMaxTarget(Map<T, Double> map)
	{
		if(IS_LEARNING)
		{
			return SelectStrategy.greedyselect(map, EPSILON);
		}
		else
		{
			return SelectStrategy.softMaxSelect(map, TEMP);
		}
	}
	
	

	@Override
	public String whisper() 
	{
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	public Agent attack() 
	{
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	public Agent divine() 
	{
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	public Agent guard() 
	{
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	public Agent vote() 
	{
		return voteTarget;
	}

	@Override
	public void finish() 
	{
		if(!IS_LEARNING ) return;

	//	actionLog.finishCOParam(getLatestDayGameInfo().getRoleMap());
		
		/**
		 * 昨日のmyPatternsを学習用に保存
		 */
		List<Pattern> copyPatterns = new ArrayList<Pattern>();
		
		for(Pattern p: myPatterns)
		{
			copyPatterns.add(p.clone());
		}
		myPatternLists.add(copyPatterns);
		
		learn();
	}

	public void learn()
	{
		boolean isVillagerWin = true;
		for(Entry<Agent, Role> set: getLatestDayGameInfo().getRoleMap().entrySet())
		{
			//finish()時に人狼が生きていたら人狼側の勝ち
			if(set.getValue() == Role.WEREWOLF && getLatestDayGameInfo().getAliveAgentList().contains(set.getKey()))
			{
				isVillagerWin = false;
				break;
			}
		}

		updatePreConditionQVal(isVillagerWin);
		
		coPercent.gameFinishUpdate(isVillagerWin, getMe(), advanceGameInfo.getComingoutMap(), getLatestDayGameInfo().getRoleMap());
	}

	void updatePreConditionQVal(boolean isVillagerWin) 
	{
		// TODO 自動生成されたメソッド・スタブ
	}
	
	// RPPが可能な可能性があるか。
	public boolean canRPP()
	{
		List<Agent> dieEnemyAgents = getLatestDayGameInfo().getAgentList();
		dieEnemyAgents.removeAll(getLatestDayGameInfo().getAliveAgentList());
		PatternMaker.getDetermineEnemyAgent(myPatterns, dieEnemyAgents);
		
		int dieEnemyNum = dieEnemyAgents.size();
		
		int maxAliveEnemyNum = MyGameInfo.getMaxEnemyNum() - dieEnemyNum;
		int minAliveHumanNum = getLatestDayGameInfo().getAliveAgentList().size() - maxAliveEnemyNum;
		
		if (maxAliveEnemyNum >= minAliveHumanNum)
		{
			return true;
		}
		return false;
	}
	
	//　自分が村側かどうかのチェック
	public boolean isVillager()
	{
		return true;
	}

	public void setLD(int ldNum)
	{
		ld = LearningData.getInstance(ldNum);
		coPercent = COPercent.GetInstance(ldNum);
	}

	public boolean isIS_LEARNING() {
		return IS_LEARNING;
	}

	public void setIS_LEARNING(boolean iS_LEARNING) {
		IS_LEARNING = iS_LEARNING;
	}
}
