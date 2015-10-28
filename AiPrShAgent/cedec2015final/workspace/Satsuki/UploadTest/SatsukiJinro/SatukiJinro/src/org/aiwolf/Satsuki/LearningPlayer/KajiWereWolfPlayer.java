package org.aiwolf.Satsuki.LearningPlayer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.aiwolf.Satsuki.LearningPlayer.*;
import org.aiwolf.Satsuki.lib.CauseOfDeath;
import org.aiwolf.Satsuki.lib.EnemyCase;
import org.aiwolf.Satsuki.lib.MyGameInfo;
import org.aiwolf.Satsuki.lib.Pattern;
import org.aiwolf.Satsuki.lib.PatternMaker;
import org.aiwolf.Satsuki.lib.WolfFakeRoleChanger;
import org.aiwolf.Satsuki.lib.DeadCondition;
import org.aiwolf.Satsuki.reinforcementLearning.AgentPattern;
import org.aiwolf.Satsuki.reinforcementLearning.COPercent;
import org.aiwolf.Satsuki.reinforcementLearning.COtimingNeo;
import org.aiwolf.Satsuki.reinforcementLearning.Qvalues;
import org.aiwolf.Satsuki.reinforcementLearning.ReinforcementLearning;
import org.aiwolf.Satsuki.reinforcementLearning.SelectStrategy;
import org.aiwolf.Satsuki.reinforcementLearning.WolfRolePattern;
import org.aiwolf.client.lib.TemplateTalkFactory;
import org.aiwolf.client.lib.TemplateWhisperFactory;
import org.aiwolf.client.lib.Utterance;
import org.aiwolf.client.lib.TemplateTalkFactory.TalkType;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Judge;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Species;
import org.aiwolf.common.data.Talk;
import org.aiwolf.common.data.Vote;
import org.aiwolf.common.net.GameInfo;
import org.aiwolf.common.net.GameSetting;

public class KajiWereWolfPlayer extends AbstractKajiWolfSideAgent 
{
	private static int WOLF_MAX = 3;
	private static int WOLFPROTECT_MASK_MAX = 4;
	private static int OPPOSITION_DELAY = 3;
	
	// 狂人のAgent．不確定の時はnull
	Agent possessedAgent = null;
	
	// 人狼のエージェント
	List<Agent> wolfAgents= new ArrayList<Agent>();
	boolean wolfProtect[] = new boolean[WOLF_MAX];

	// Whisperで自分の騙る役職を伝えたか
	boolean hasWhisperedFakeRole = false;

	// 人狼達のfakeRoleに矛盾が起こらないPatterns
	List<Pattern> wolfsPatterns;

	// 仲間人狼のfakeRole
	Map<Agent, Role> wolfsFakeRoleMap = new HashMap<Agent, Role>();

	// Whisperをどこまで読んだか
	int readWhisperNumber = 0;
	
	// 対抗COディレイ
	int oppsitionCODelay = OPPOSITION_DELAY;

	// 今日の嘘JudgeをWhisperで伝えたか
	boolean hasWhisperTodaysFakeJudge;

	// 今日の嘘Judge
	Judge todaysFakeJudge;

	// WhisperされたJudgeのリスト
	List<Judge> whisperedJudges = new ArrayList<Judge>();
	
	// Whisperされた攻撃のリスト(攻撃→被攻撃)
	Map<Agent, Agent> whisperedAttacks = new HashMap<Agent, Agent>();
	Agent attackAgent;
	Agent whisperedAttackAgent;
	
	int coFlag_NTurn = 0;
	int coFlag_Turn = 0;
	
	WolfFakeRoleChanger changer;
	
	private boolean
		wolfJudged = false,			// 人狼だと占われた時に騙る役職
		existVillagerWolf = false,	// 相方の人狼が村人を騙るといった時に騙る役職
		existSeerWolf = false,
		existMediumWolf = false,
		seerCO = false,				// 占い師が出てきたときに騙る役職
		mediumCO = false,
		isVoteTarget = false;		// 投票対象になった時に騙る役職


	@Override
	public void initialize(GameInfo gameInfo, GameSetting gameSetting) 
	{
		super.initialize(gameInfo, gameSetting);
		
		for (int i = 0; i < WOLF_MAX; ++i)
		{
			wolfProtect[i] = true;
		}

		// myPatternsに仲間の人狼をセットする
		for(Entry<Agent, Role> set: gameInfo.getRoleMap().entrySet())
		{
			if(!set.getKey().equals(getMe()))
			{
				PatternMaker.settleAgentRole(myPatterns, set.getKey(), Role.WEREWOLF);
				wolfAgents.add(set.getKey());
			}
		}
		wolfAgents.add(getMe());
		
		// いらないきがする
		wolfsFakeRoleMap.put(getMe(), fakeRole);
		wolfsPatterns = new ArrayList<Pattern>(otherPatterns);

		coFlag_NTurn = coPercent.getMaxFlag_Wolf_NTurn();
		coFlag_Turn = coPercent.getMaxFlag_Wolf_Turn();
	}

	@Override
	public void update(GameInfo gameInfo)
	{
		boolean exeTalk = false;
		
		if (gameInfo.getTalkList().size() != readTalkNumber)
		{
			exeTalk = true;
		}
		
		
		super.update(gameInfo);
		
		if (dayTalkCount % 2 == 0 && exeTalk)
		{
			calcFakeRole();			
		}

		// whisperの処理

		List<Talk> whisperList = gameInfo.getWhisperList();

		/*
		 * 各発話についての処理
		 * カミングアウトについてはパターンの拡張
		 * 能力結果の発話についてはパターン情報の更新
		 */
		for(; readWhisperNumber < whisperList.size(); readWhisperNumber++)
		{
			Talk talk = whisperList.get(readWhisperNumber);
			Utterance utterance = new Utterance(talk.getContent());
			switch (utterance.getTopic()) 
			{
			case COMINGOUT:
				comingoutWhisperDealing(talk, utterance);
				break;

			case DIVINED:
				divinedWhisperDealing(talk, utterance);
				break;

			case INQUESTED:
				inquestedWhisperDealing(talk, utterance);
				break;

			case VOTE:
				voteWhisperDealing(talk, utterance);
				break;
				
			case ATTACK:
				attackWhisperDealing(talk,utterance);
				break;

			case AGREE:
				agreeWhisperDealing(talk,utterance);
				break;
			//上記以外
			default:
				break;
			}
		}

		if (dayTalkCount % 2 == 1 && exeTalk)
		{
			attackAgent = calcAttack();
		}
	}

	private void attackWhisperDealing(Talk talk, Utterance utterance) 
	{
		whisperedAttacks.put(talk.getAgent(), utterance.getTarget());
	}
	
	private void agreeWhisperDealing(Talk talk, Utterance utterance)
	{
		if (whisperedAttacks.containsKey(utterance.getTarget()))
		{
			whisperedAttacks.put(talk.getAgent(), whisperedAttacks.get(utterance.getTarget()));			
		}
	}

	@Override
	public void dayStart()
	{
		super.dayStart();	
		
		whisperedAttacks.clear();
		attackAgent = null;
		whisperedAttackAgent = null;
		
		if (isComingout)
		{
			switch (fakeRole) 
			{
			//占い師騙りの場合，2日目以降fakeJudgeをいれる
			case SEER:
				if(getDay() >= 2){
					setFakeDivineJudge();
				}
				break;

			//霊能者騙りの場合，襲撃されたAgentがいればfakeJudgeをいれる
			case MEDIUM:
				if(getLatestDayGameInfo().getExecutedAgent() != null){
					setFakeInquestJudge(getLatestDayGameInfo().getExecutedAgent());
				}
				break;

			//村人騙りの場合，何もしない
			case VILLAGER:
				break;
			}			
		}
		readWhisperNumber = 0;
		hasWhisperTodaysFakeJudge = false;
		
		// PPCheck
		PPCheck();		
		
		long end = System.currentTimeMillis();
		if (end - updateStartTime >= 200)
		{
			int time = (int)(end - updateStartTime);
			System.err.printf("長い：0041:%d\n",time);
		}
	}
	
	public void PPCheck()
	{
		// 本CO処理終了
		if (this.bRealCoFinish == true)
		{
			return;
		}
		
		// 敵の数と村人の数を出す。
		int enemyNum = 0;
		int villagerNum = 0;
		if (possessedAgent != null && getLatestDayGameInfo().getAliveAgentList().contains(possessedAgent))
		{
			enemyNum++;
		}
		
		for(Agent agent: wolfAgents)
		{
			if(getLatestDayGameInfo().getAliveAgentList().contains(agent))
			{
				enemyNum++;
			}
		}
		
		villagerNum = getLatestDayGameInfo().getAliveAgentList().size() - enemyNum;
		
		if (enemyNum > villagerNum)
		{
			bRealCo = true;
		}
		
		// RPPでも勝率が高いならok
		if (enemyNum == villagerNum && GetAliveWolfNum() >= 2)
		{
			bRealCo = true;
		}
	}


	@Override
	public String whisper()
	{
		if (System.currentTimeMillis() - updateStartTime >= 100)
		{
			int time = (int)(System.currentTimeMillis() - updateStartTime);
			System.err.printf("長い：0050:%d\n",time);
		}
		if (this.attackAgent != null && whisperedAttackAgent != attackAgent)
		{
			whisperedAttackAgent = attackAgent;
			return TemplateWhisperFactory.attack(attackAgent);
		}
		return TemplateWhisperFactory.over();
	}
	
	private void calcFakeRole()
	{
		this.doComingout = false;
		
		// カミングアウト済みなら逃げられない。
		if (this.isComingout) return;

		ArrayList<Pattern> calcPatterns = new ArrayList<Pattern>(otherPatterns);
		
		// もうバレバレならあきらめたい・・・
		if (calcPatterns.size() <= 1) return;
		
		// 他の人狼がカミングアウトしている職種にはカミングアウトしない。
		boolean isSeerCO = true;
		boolean isMediumCO = true;
		boolean isBodyGuardCO = true;
		int seerNum = 0, mediumNum = 0, bodyGuardNum = 0;
		
		Map<Agent, Role> comingoutMap = advanceGameInfo.getComingoutMap();
		for(Entry<Agent, Role> set: comingoutMap.entrySet())
		{
			for (int i = 0; i < WOLF_MAX; ++i)
			{
				if (this.wolfAgents.contains(set.getKey()))
				{
					switch(set.getValue())
					{
					case SEER: isSeerCO = false; break;
					case MEDIUM: isMediumCO = false; break;
					case BODYGUARD: isBodyGuardCO = false; break;
					}
					break;
				}
			}

			switch(set.getValue())
			{
			case SEER: ++seerNum; break;
			case MEDIUM: ++mediumNum; break;
			case BODYGUARD: ++bodyGuardNum; break;
			}
		}
		int trueSeerNum = seerNum;

		// あと一人でCOロールで負ける状態になっていたらカミングアウトしない
		if (seerNum >= 1) --seerNum;
		if (mediumNum >= 1) --mediumNum;
		if (bodyGuardNum >= 1) --bodyGuardNum;
		
		if (seerNum + mediumNum + bodyGuardNum - 1 >= MyGameInfo.getMaxAgentNum(Role.WEREWOLF) + MyGameInfo.getMaxAgentNum(Role.POSSESSED))
		{
			return;
		}
		
		// 碌な事にならないので、余剰COの発生している所には突っ込んでいかない。
		if (seerNum >= 1) isSeerCO = false;
		if (mediumNum >= 1) isMediumCO = false;
		if (bodyGuardNum >= 1) isBodyGuardCO = false;
		
		
		// 擁護する人狼混みのパターン数を考える
		ProtectPatterns(calcPatterns);
		
		// 擁護できるケースが無い・・・
		if (calcPatterns.size() == 0) return;
		
		// 筋を通すには自分自身を切り捨てなければならない
		if (this.wolfProtect[WOLF_MAX - 1] == false) return;
		
		// 筋の通るパターンからさらに絞込みを行う。
		ArrayList<Pattern> calcPatterns2 = new ArrayList<Pattern>(calcPatterns);
		
		// 各職ごとのチェック
		if (isSeerCO)
		{
			if (fakeTestSeer(calcPatterns))
			{
				if(MyGameInfo.IS_PRINT()) System.out.println("##SEER");
				fakeRole = Role.SEER;
				notToldjudges = new ArrayList<Judge>(fakeJudges);
				doComingout = true;
				
				if (this.dayTalkCount == 0 && trueSeerNum == 0 && coFlag_Turn > coFlag_NTurn)
				{
					if ((coFlag_Turn & COPercent.TURN_0) != 0 && getDay() == 0) priorityComingout = true;
					if ((coFlag_Turn & COPercent.TURN_1) != 0 && getDay() == 1) priorityComingout = true;
					if ((coFlag_Turn & COPercent.TURN_2) != 0 && getDay() == 2) priorityComingout = true;
					if ((coFlag_Turn & COPercent.TURN_3) != 0 && getDay() == 3) priorityComingout = true;
					if ((coFlag_Turn & COPercent.TURN_4) != 0 && getDay() == 4) priorityComingout = true;
				}
				if (trueSeerNum >= 1 && (coFlag_NTurn & COPercent.OPPOSION_THROW) != 0)
				{
					for (Entry<Agent,Integer>set: coPercent.coDay.entrySet())
					{
						if (set.getValue() != getDay())
						{
							priorityComingout = true;
							break;
						}						
					}
				}
				
				/*
				if (this.dayTalkCount == 0 && getDay() >= 2)
				{
					priorityComingout = true;
				}
				*/
				return;
			}
		}
		if (isMediumCO)
		{
			// 今まで処刑されたユニットのうち、最後の一人を除いて白、最後の一人を黒に出来たらok
			if (fakeTestMedium(calcPatterns))
			{
				if(MyGameInfo.IS_PRINT()) System.out.println("##MEDIUM");
				fakeRole = Role.MEDIUM;
				notToldjudges = new ArrayList<Judge>(fakeJudges);
				doComingout = true;

				// 信頼度を得るため、2日目以降、初会話時にカミングアウトする。
				if (this.dayTalkCount == 0 && getDay() >= 2)
				{
					priorityComingout = true;
				}
				
				return;
			}
		}
		if (isBodyGuardCO)
		{
			// bodyguardはどうするか・・・
		}
	}

	@Override
	public String getComingoutText() 
	{
		int day = getDay();
		/*
		 * カミングアウトする日数になる
		 * 他に同じ能力者COが出る
		 * 人狼を見つける
		 * 投票先に選ばれそう（全体の2/3が投票かつ全投票中で1/4以上が自分に投票）
		 */
		if(isComingout || !doComingout)
		{
			return null;
		}
		else
		{
			// 優先的なカミングアウトの実行
			if (priorityComingout)
			{
				return comingoutFakeRole();				
			}
			// 日数によるカミングアウト
			/*
			if(coTiming.getDay() == getDay())
			{
				return comingoutFakeRole();
			}
			*/

			// ================================================================
			// 偽CO出現
			int saveCODelay = oppsitionCODelay;
			if((coFlag_NTurn & COPercent.OPPOSION) != 0)
			{
				Map<Agent, Role> comingoutMap = advanceGameInfo.getComingoutMap();
				for(Entry<Agent, Role> set: comingoutMap.entrySet())
				{
					if(set.getValue() == fakeRole && !set.getKey().equals(getMe()))
					{
						// 狂人が判明している場合は即対抗CO
						if (this.possessedAgent != null)
						{
							return comingoutFakeRole();							
						}
						
						// 一定時間後にCO
						--oppsitionCODelay;
						if (oppsitionCODelay <= 0)
						{
							return comingoutFakeRole();														
						}
						break;
					}
				}
			}
			
			// カウントダウンされなければ逆にカウンタをリセット
			if (oppsitionCODelay == saveCODelay)
			{
				oppsitionCODelay = OPPOSITION_DELAY;
			}
			
			// ================================================================
			// 投票先に選ばれそう
			if(/*coTiming.isVoted()*/true)
			{
				List<Vote> votes = advanceGameInfo.getVoteList(getDay());
				if((double)votes.size() * 1.5 > getLatestDayGameInfo().getAliveAgentList().size())
				{
					int voteToMe = 0;
					for(Vote vote: votes)
					{
						if(vote.getTarget().equals(getMe()))
						{
							voteToMe++;
						}
					}
					if((double)voteToMe * 4 > votes.size() || voteToMe >= 3)
					{
						return comingoutFakeRole();
					}
				}
			}
			
			// 人狼だと占われた
			if(/*coTiming.isWolfJudged()*/true)
			{
				for(Judge judge: advanceGameInfo.getInspectJudges())
				{
					if(getMe().equals(judge.getTarget()) && judge.getResult() == Species.WEREWOLF)
					{
						return comingoutFakeRole();
					}
				}
			}
		}
		return null;
	}
	
	// 占い師になろうとしてみる
	// 日にちの数-1の人族の占いと1の生きている人狼の占いで筋を通せばok
	private boolean fakeTestSeer(ArrayList<Pattern> calcPattern)
	{
		Random rand = new Random();
		ArrayList<Pattern> calcPatterns2 = new ArrayList<Pattern>(calcPattern);
		PatternMaker.settleAgentRole(calcPatterns2, getMe(), Role.SEER);
		
		if (calcPatterns2.size() == 0) return false;
		
		fakeJudges = new ArrayList<Judge>();
		int day = getDay();
		List<Agent> agentList = new ArrayList<Agent>(getLatestDayGameInfo().getAgentList());
		Pattern p = calcPatterns2.get(rand.nextInt(calcPatterns2.size()));
		
		// 自分は占わない
		agentList.remove(getMe());
		
		// 黒確エージェントは基本占わない。
		for(Entry<Agent, EnemyCase> set: p.getEnemyMap().entrySet())
		{
			if (set.getValue() == EnemyCase.black)
			{
				agentList.remove(set.getKey());						
			}
		}
		
		for (int i = 0; i < day; ++i)
		{					
			// 前日までに死亡しているユニとを占い対象からはずす。
			for (DeadCondition dc: advanceGameInfo.getDeadConditions())
			{
				if (dc.getDateOfDeath() == i + 1)
				{
					agentList.remove(dc.getDeadAgent());
				}
			}
			
			if (i == day - 1 && dayTalkCount == 0 && (coFlag_NTurn & COPercent.FIND_WOLF) != 0)
			{
				// ============================================================
				// 切り捨てる人狼がいれば、そいつを占った事にする。
				for (int j = 0; j < WOLF_MAX - 1; ++j)
				{
					if (
							wolfProtect[j] == false 
							&& this.getLatestDayGameInfo().getAliveAgentList().contains(wolfAgents.get(j))
					)
					{
						if(MyGameInfo.IS_PRINT()) System.out.println("初期占いラス1:1");
						Agent judgeAgent = wolfAgents.get(j);
						Judge newJudge = new Judge(i, getMe(), judgeAgent, Species.WEREWOLF);
						fakeJudges.add(newJudge);
						return true;
					}
				}

				// ============================================================
				// 現在のパターンに黒確エージェントがもしいればそいつを占った事にする。(パターン少ないので先頭でいい)
				for(Entry<Agent, EnemyCase> set: p.getEnemyMap().entrySet())
				{
					if (
						set.getValue() == EnemyCase.black 
						&& this.getLatestDayGameInfo().getAliveAgentList().contains(set.getKey())
					)
					{
						if (this.isProtectAgent(set.getKey())) continue;
						if(MyGameInfo.IS_PRINT()) System.out.println("初期占いラス1:2");
						Agent judgeAgent = set.getKey();		
						Judge newJudge = new Judge(i, getMe(), judgeAgent, Species.WEREWOLF);
						fakeJudges.add(newJudge);	
						return true;
					}
				}

				// ============================================================
				// 白確以外の人狼以外から適当に人狼を決定する
				List<Agent> notWhiteAgent = new ArrayList<Agent>(agentList);
				for(Agent whiteAgent: p.getWhiteAgentSet())
				{
					notWhiteAgent.remove(whiteAgent);
				}

				// 守護対象は人狼にしない
				{
					List<Agent> subAgents = new ArrayList<Agent>();
					for(Agent whiteAgent: notWhiteAgent)
					{
						if (isProtectAgent(whiteAgent))
						{
							subAgents.add(whiteAgent);
						}
					}				
					notWhiteAgent.removeAll(subAgents);
				}

				if (notWhiteAgent.size() != 0)
				{
					if(MyGameInfo.IS_PRINT()) System.out.println("初期占いラス1:3");
					Agent judgeAgent = notWhiteAgent.get(rand.nextInt(notWhiteAgent.size()));
					Judge newJudge = new Judge(i, getMe(), judgeAgent, Species.WEREWOLF);
					fakeJudges.add(newJudge);
					return true;
				}

				// ============================================================
				// 適当に人間として登録(もし占いに失敗した場合は、占い師にはなれなかった扱い)
				if (agentList.size() == 0)
				{
					return false;
				}
				// 人間
				if(MyGameInfo.IS_PRINT()) System.out.println("初期占いラス1:4");
				Agent judgeAgent = agentList.get(rand.nextInt(agentList.size()));
				Judge newJudge = new Judge(i, getMe(), judgeAgent, Species.HUMAN);
				fakeJudges.add(newJudge);
				agentList.remove(judgeAgent);
				return true;
			}
			else
			{
				// 一応ケア
				if (agentList.size() == 0)
				{
					return false;
				}
				
				// 人間
				Agent judgeAgent = agentList.get(rand.nextInt(agentList.size()));
				Judge newJudge = new Judge(i, getMe(), judgeAgent, Species.HUMAN);
				fakeJudges.add(newJudge);
				agentList.remove(judgeAgent);
			}
		}
		return false;
	}
	
	// 霊能者になろうとしてみる
	private boolean fakeTestMedium(ArrayList<Pattern> calcPattern)
	{
		fakeJudges = new ArrayList<Judge>();
		
		ArrayList<Pattern> calcPatterns2 = new ArrayList<Pattern>(calcPattern);
		int day = getDay();

		// 今までの占い一覧
		List<Judge> prevJudges = advanceGameInfo.getInspectJudges();
		
		for (DeadCondition dc: advanceGameInfo.getDeadConditions())
		{
			if (dc.getCause() == CauseOfDeath.executed)
			{
				// 優先宣言のリセット
				priorityComingout = false;
				
				// 処刑したユニットに対して行っていた占い
				List<Judge> executeJudges = new ArrayList<Judge>();
				
				for(Judge judge: prevJudges)
				{
					if (judge.getTarget() == dc.getDeadAgent())
					{
						executeJudges.add(judge);
					}
				}

				Judge newJudge = null;
				for(Judge judge: executeJudges)
				{
					// 占い元ユニットとの連携
					if (isProtectAgent(judge.getAgent()))
					{
						if (judge.getResult() == Species.HUMAN)
						{
							PatternMaker.RemoveEnemyPattern(calcPatterns2, dc.getDeadAgent(), true);		
							newJudge = new Judge(dc.getDateOfDeath() - 1, getMe(), dc.getDeadAgent(), Species.HUMAN);
							fakeJudges.add(newJudge);										
						}
						else
						{
							PatternMaker.RemoveEnemyPattern(calcPatterns2, dc.getDeadAgent(), false);
							newJudge = new Judge(dc.getDateOfDeath() - 1, getMe(), dc.getDeadAgent(), Species.WEREWOLF);
							fakeJudges.add(newJudge);							
						}
						
						// もし、最後の仮判定が連携思考の場合は優先的にカミングアウトをして、占い師をサポート
						priorityComingout = true;
						break;
					}
				}
				
				if (newJudge == null)
				{
					if (dc.getDateOfDeath() == day)
					{	
						PatternMaker.RemoveEnemyPattern(calcPatterns2, dc.getDeadAgent(), false);
						newJudge = new Judge(dc.getDateOfDeath() - 1, getMe(), dc.getDeadAgent(), Species.WEREWOLF);
						fakeJudges.add(newJudge);
					}
					else
					{
						PatternMaker.RemoveEnemyPattern(calcPatterns2, dc.getDeadAgent(), true);		
						newJudge = new Judge(dc.getDateOfDeath() - 1, getMe(), dc.getDeadAgent(), Species.HUMAN);
						fakeJudges.add(newJudge);			
					}					
				}
			}
		}
		PatternMaker.removeContradictPatterns(calcPatterns2);
		// 筋が通っていればok
		if (calcPatterns2.size() != 0)
		{
			return true;
		}
		priorityComingout = false;
		return false;
	}
	
	// 守る(=協調)するべきエージェントかどうか取得
	private boolean isProtectAgent(Agent agent)
	{
		if (wolfAgents.contains(agent))
		{
			int idx = wolfAgents.indexOf(agent);			
			
			return wolfProtect[idx];
		}
		
		return false;
	}
	/*
	 * 人狼が最低一人以上白確で筋の通るパターンと誰を白確にするかの設定を行います。
	 */
	private void ProtectPatterns(List<Pattern> patterns)
	{
		int protectPattern = 1;
		for (int i = 0; i < WOLF_MAX; ++i)
		{
			protectPattern *= 2;
		}
		for (int j = 0; j < protectPattern - 1; ++j)
		{
			ProtectPatternsWithMask(patterns, j);
			// 筋の通るパターンが見つかった
			if (patterns.size() >= 1) break;
		}
		return;
	}
	private void ProtectPatternsWithMask(List<Pattern> patterns, int mask)
	{
		patterns.clear();
		patterns.addAll(otherPatterns);
		for(int i = 0; i < WOLF_MAX; ++i)
		{
			wolfProtect[i] = false;
			
			if (((1 << i) & mask) == 0)
			{
				PatternMaker.RemoveEnemyPattern(patterns, wolfAgents.get(i), true);
				wolfProtect[i] = true;
			}
		}
	}

	private void comingoutWhisperDealing(Talk talk, Utterance utterance)
	{
		// あまり意味を感じないので無効化 @石岡
		if (true)
		{
			return;
		}
		
		/*
		 * wolfsPatternsを更新する
		 */
		wolfsFakeRoleMap.put(talk.getAgent(), utterance.getRole());
		if(getDay() == 0)
		{
		//	patternChange(utterance.getRole());
		}
		PatternMaker.settleAgentRole(wolfsPatterns, talk.getAgent(), utterance.getRole());
	}

	private void divinedWhisperDealing(Talk talk, Utterance utterance){
		judgeWhisperDealing(talk, utterance);
	}

	private void inquestedWhisperDealing(Talk talk, Utterance utterance){
		judgeWhisperDealing(talk, utterance);
	}


	private void judgeWhisperDealing(Talk talk, Utterance utterance)
	{
		/*
		 * 人狼同士が協調可能Patternがあるときに，それが消えたら自分のJudgeを書き換え
		 * 村人騙りなら何もしない
		 */
		if(getDay() == 0)
		{
			return;
		}
		Judge judge = new Judge(getDay(), talk.getAgent(), utterance.getTarget(), utterance.getResult());
		whisperedJudges.add(judge);
		PatternMaker.updateJudgeData(wolfsPatterns, judge);
	}



	private void voteWhisperDealing(Talk talk, Utterance utterance) {
		// TODO 自動生成されたメソッド・スタブ
	}
	
	@Override
	public void comingoutTalkDealing(Talk talk, Utterance utterance)
	{
		super.comingoutTalkDealing(talk, utterance);

		// 狼のとき(RPP or PP)
		if (
				(utterance.getRole() == Role.WEREWOLF || utterance.getRole() == Role.POSSESSED) 
				&& this.wolfAgents.contains(talk.getAgent())
		)
		{
			this.bRealCo = false;
			this.bRealCoFinish = true;
		}
	}
	@Override
	public void divinedTalkDealing(Talk talk, Utterance utterance){
		super.divinedTalkDealing(talk, utterance);
		confirmPossessedAgent();
		if(utterance.getTarget() == getMe() && utterance.getResult() == Species.WEREWOLF){
			wolfJudgedDealing();
		}
	}

	@Override
	public void inquestedTalkDealing(Talk talk, Utterance utterance){
		super.inquestedTalkDealing(talk, utterance);
		confirmPossessedAgent();
		if(utterance.getTarget() == getMe() && utterance.getResult() == Species.WEREWOLF){
			wolfJudgedDealing();
		}
	}
	
	private void wolfJudgedDealing(){
/*		if(isComingout) return;
		if(coTiming.isWolfJudged()){
			
		}
*/	}

	/**
	 * 狂人確定のAgentがいるか確かめる
	 * いた場合はpossessedAgentにいれる
	 */
	private void confirmPossessedAgent()
	{
		loop1:for(Entry<Agent, Role> set: advanceGameInfo.getComingoutMap().entrySet())
		{
			if(set.getValue() == Role.SEER || set.getValue() == Role.MEDIUM || set.getValue() == Role.BODYGUARD)
			{
				for(Pattern pattern: myPatterns)
				{
					if(set.getKey().equals(pattern.getSeerAgent()) || set.getKey().equals(pattern.getMediumAgent()))
					{
						continue loop1;
					}
				}
			}
			//全てのPatternにおいて真能力者とされていないカミングアウトしたプレイヤー＝狂人
			possessedAgent = set.getKey();
		}
	}

	/**
	 * 狂人が分かっているかを返す
	 * @return
	 */
	private boolean knowsPossessed()
	{
		if(possessedAgent == null)
		{
			return false;
		}
		else
		{
			return true;
		}
	}

	/**
	 * 狂人が分かっている場合はFakeRoleを返す
	 * 分かっていない場合はnull
	 * @return
	 */
	private Role possessedFakeRole(){
		if(!knowsPossessed()){
			return null;
		}else{
			return advanceGameInfo.getComingoutMap().get(possessedAgent);
		}
	}

	private List<Agent> getWolfList(){

		List<Agent> wolfList = new ArrayList<Agent>();
		for(Entry<Agent, Role> set: getLatestDayGameInfo().getRoleMap().entrySet()){
			if(set.getValue() == Role.WEREWOLF){
				wolfList.add(set.getKey());
			}
		}
		return wolfList;
	}

	public Agent calcAttack()
	{
		// 投票可能なのは生存者 - 狼
		List<Agent> baseList = new ArrayList<Agent>(getLatestDayGameInfo().getAliveAgentList());
		baseList.removeAll(wolfAgents);
		
		// 1人以上投票が可能なケースがあり、味方を守れるルートを探す。
		int protectPattern = 0;
		for (; protectPattern < (1 << WOLF_MAX); ++protectPattern)
		{		
			// 高優先襲撃リスト
			List<Agent> hPriorityList = new ArrayList<Agent>();
		
			List<Pattern> patterns = new ArrayList<Pattern>();
			
			ProtectPatternsWithMask(patterns,protectPattern);
			
			// 守れるパターンが無い
			if (patterns.size() == 0) continue;

			// ================================================================
			// １体以上矛盾を生じさせずに殺せるキャラクタがいるかどうか
			List<Agent> wolfDetermineAgent = new ArrayList<Agent> (getLatestDayGameInfo().getAliveAgentList());
			
			// 狂人は高速化のため空のデータ。
			List<Agent> possessedDetermineAgent = new ArrayList<Agent> ();
			PatternMaker.getDetermineEnemyAgent(patterns, wolfDetermineAgent, possessedDetermineAgent);
			
			hPriorityList.clear();
			for(Agent agent: baseList)
			{
				if (wolfDetermineAgent.contains(agent) == false)
				{
					hPriorityList.add(agent);
				}
			}
			
			if (hPriorityList.size() == 0) continue;
			
			// 低優先襲撃リスト
			List<Agent> lPriorityList = new ArrayList<Agent>(hPriorityList);

			// ================================================================
			// 3人以上同COが生存している環境ではそのCOを狙わない。(高・中)
			Map<Role,Integer> roleCount = new HashMap<Role,Integer>();
			roleCount.put(Role.SEER, 0);
			roleCount.put(Role.MEDIUM, 0);
			roleCount.put(Role.BODYGUARD, 0);
			
			for(Entry<Agent,Role> set: advanceGameInfo.getComingoutMap().entrySet())
			{
				if (set.getValue() != Role.SEER && set.getValue() != Role.MEDIUM && set.getValue() != Role.BODYGUARD) continue;
				if (hPriorityList.contains(set.getKey()) == false) continue;
				roleCount.put(set.getValue(), roleCount.get(set.getValue()) + 1);
			}

			for(Entry<Agent,Role> set: advanceGameInfo.getComingoutMap().entrySet())
			{
				if (set.getValue() != Role.SEER && set.getValue() != Role.MEDIUM && set.getValue() != Role.BODYGUARD) continue;
				if (hPriorityList.contains(set.getKey()) == false) continue;
				
				if (roleCount.get(set.getValue()) >= 3)
				{
					hPriorityList.remove(set.getKey());
				}
			}
			// ================================================================
			// 今回攻撃されてそうなエージェントには攻撃しない(高・中)
			// 狂人、2日以内段階の単一COユニットは襲撃しない(高・中)
			Map<Agent,Integer> voteCount = new HashMap<Agent,Integer>();
			for (Vote vote: advanceGameInfo.getVoteList(getDay()))
			{
				if (voteCount.containsKey(vote.getTarget()) == false)
				{
					voteCount.put(vote.getTarget(), 0);					
				}
				voteCount.put(vote.getTarget(), voteCount.get(vote.getTarget()) + 1);					
			}
			
			int maxCount = 0;
			List<Agent> subAgent = new ArrayList<Agent>();
			for (Entry<Agent,Integer> set: voteCount.entrySet())
			{
				if (set.getValue() > maxCount)
				{
					subAgent.clear();
					maxCount = set.getValue();
				}

				if (set.getValue() == maxCount)
				{
					subAgent.add(set.getKey());
				}
			}

			// 狂人、2日以内段階のCOユニットは襲撃しない(高・中)
			if(possessedAgent != null && subAgent.contains(possessedAgent) == false)
			{
				subAgent.add(possessedAgent);
			}
			
			if (getDay() <= 2)
			{
				for(Entry<Agent, Role> set: advanceGameInfo.getComingoutMap().entrySet())
				{
					if (set.getValue() == Role.SEER || set.getValue() == Role.MEDIUM || set.getValue() == Role.BODYGUARD)
					{
						if (subAgent.contains(set.getKey()) == false)
						{
							subAgent.add(set.getKey());
						}
					}
				}
			}
			
			hPriorityList.removeAll(subAgent);
			
			// 中優先襲撃リスト
			List<Agent> mPriorityList = new ArrayList<Agent>(hPriorityList);
			
			// ================================================================
			// いずれかの占い師から白をもらっていないと襲撃しない(高)
			List<Agent> judgeWhiteAgents = new ArrayList<Agent>();
			
			for (Judge judge: advanceGameInfo.getInspectJudges())
			{
				// 狼
				if (judge.getResult() == Species.WEREWOLF) continue;
				
				// 高優先リストに入っていない
				if (hPriorityList.contains(judge.getTarget()) == false) continue;

				// すでに登録済み
				if (judgeWhiteAgents.contains(judge.getTarget()) == true) continue;
				
				judgeWhiteAgents.add(judge.getTarget());
			}
			hPriorityList = new ArrayList<Agent>(judgeWhiteAgents);
			
			// 優先度順に投票
			
			// whisperリストに登録されているか？
			// 高優先
			if (hPriorityList.size() != 0)
			{
				for(Entry<Agent,Agent> set: whisperedAttacks.entrySet())
				{
					if (set.getKey() != getMe())
					{
						if (hPriorityList.contains(set.getValue()))
						{
							return set.getValue();
						}
					}
				}
				return hPriorityList.get(0);
			}

			// 中優先
			if (mPriorityList.size() != 0)
			{
				for(Entry<Agent,Agent> set: whisperedAttacks.entrySet())
				{
					if (set.getKey() != getMe())
					{
						if (mPriorityList.contains(set.getValue()))
						{
							return set.getValue();
						}
					}
				}
				return mPriorityList.get(0);
			}

			// 低優先
			if (lPriorityList.size() != 0)
			{
				for(Entry<Agent,Agent> set: whisperedAttacks.entrySet())
				{
					if (set.getKey() != getMe())
					{
						if (lPriorityList.contains(set.getValue()))
						{
							return set.getValue();
						}
					}
				}
				return lPriorityList.get(0);
			}
		}
		
		if (baseList.size() == 0)
		{
			return null;
		}
		
		return baseList.get(0);		
	}

	@Override
	public Agent attack()
	{
		return attackAgent;
	}


	@Override
	// fake占い。
	protected void setFakeDivineJudge() 
	{
		for (int i = 0; i < WOLFPROTECT_MASK_MAX; ++i)
		{
			// パターンに矛盾が生じないようにする
			List<Pattern> patterns = new ArrayList<Pattern>();
			ProtectPatternsWithMask(patterns,i);
			if (patterns.size() == 0) continue;
			
			Agent judgeTarget = getDivineAgent(patterns);
			if (judgeTarget == null) continue;

			int minEnemyNum = MyGameInfo.getMaxEnemyNum();
			for(Pattern pattern: patterns)
			{
				if (pattern.getEnemyMap().size() < minEnemyNum)
				{
					minEnemyNum = pattern.getEnemyMap().size();
				}
			}

			int aliveSize = getLatestDayGameInfo().getAliveAgentList().size();
			boolean canBlack = true, canWhite = true;

			// 黒チェック
			{
				List<Pattern> patterns2 = new ArrayList<Pattern>(patterns);
				PatternMaker.RemoveEnemyPattern(patterns2, judgeTarget, false);
				
				// 矛盾が発生
				if (patterns2.size() == 0) canBlack = false;
				
				// 12人くらいまではあわてない
				if (aliveSize >= 12)  canBlack = false;
				
				// 6人以上のとき、もしこの決定を行うと・・・ゲームクリアになる
				if (aliveSize >= 6 && minEnemyNum == 3)  canBlack = false;
			}
			
			// 白チェック
			{
				List<Pattern> patterns2 = new ArrayList<Pattern>(patterns);
				PatternMaker.RemoveEnemyPattern(patterns2, judgeTarget, true);
				
				// 矛盾が発生
				if (patterns2.size() == 0) canWhite = false;
			}

			Species species = null;
			if (canBlack)
			{
				species = Species.WEREWOLF;
			}
			else if (canWhite)
			{
				species = Species.HUMAN;			
			}
			else
			{
				species = Species.WEREWOLF;			
			}
			todaysFakeJudge = new Judge(getDay()-1, getMe(), judgeTarget, species);
			notToldjudges.add(todaysFakeJudge);
			fakeJudges.add(todaysFakeJudge);
			return;
		}
	}
	
	//  1-1  人狼以外の占い師または霊媒師ユニットを黒と宣言
	protected Judge setFakeDivineJudge_SameCODestroy(List<Agent> judgeTargets)
	{
		Judge fakeJudge = null;
		List<Agent> judgeTargets2 = new ArrayList<Agent>(judgeTargets);
		Random rand = new Random();
		
		for (int i = 0; i < WOLF_MAX; ++i)
		{
			if (this.wolfProtect[i])
			{
				judgeTargets2.remove(this.wolfAgents.get(i));						
			}	
		}

		List<Agent> judgeTargets3 = new ArrayList<Agent>();
		for (Agent agent: judgeTargets2)
		{
			if (advanceGameInfo.getComingoutMap().containsKey(agent))
			{
				if (advanceGameInfo.getComingoutMap().get(agent) == Role.SEER || advanceGameInfo.getComingoutMap().get(agent) == Role.MEDIUM)
				{
					judgeTargets3.add(agent);
				}					
			}
		}
		judgeTargets3.removeAll(Collections.singleton(null));
		
		if (judgeTargets3.size() != 0)
		{
			fakeJudge = new Judge(getDay()-1, getMe(), judgeTargets3.get(rand.nextInt(judgeTargets3.size())), Species.WEREWOLF);
		}		
		
		return fakeJudge;
	}
	
	//  1-2  残りプレイヤーが9人以上なら、適当なユニットを白と宣言
	protected Judge setFakeDivineJudge_RandomUnitWhite(List<Agent> judgeTargets)
	{
		Judge fakeJudge = null;
		if (this.getLatestDayGameInfo().getAliveAgentList().size() < 9) return fakeJudge;
		
		fakeJudge = setFakeDivineJudge_RandomUnitWhiteCore(judgeTargets);
		
		return fakeJudge;
	}
	
	// 1-3 確定でない適当なユニットを黒と宣言(守護対象の人狼除く)
	protected Judge setFakeDivineJudge_RandomUnitBlack(List<Agent> judgeTargets)
	{
		return setFakeDivineJudge_RandomUnitBlackCore(judgeTargets);
	}

	//  2-1 人狼 - 人間の数が2以下
	//   2-1-1 白確定でないエージェントがいればそれを黒と宣言
	protected Judge setFakeDivineJudge_RandomUnitBlack2(List<Agent> judgeTargets)
	{
		int wolfNum = GetAliveWolfNum();
		int humanNum = this.getLatestDayGameInfo().getAliveAgentList().size() - wolfNum;
		
		if (humanNum - wolfNum > 2) return null;
				
		return setFakeDivineJudge_RandomUnitBlackCore(judgeTargets);
	}

	//  2-2 確定でない適当なユニットを白と宣言
	protected Judge setFakeDivineJudge_RandomUnitWhite2(List<Agent> judgeTargets)
	{
		return setFakeDivineJudge_RandomUnitWhiteCore(judgeTargets);		
	}

	//  確定でない適当なユニットを黒と宣言
	protected Judge setFakeDivineJudge_RandomUnitBlackCore(List<Agent> judgeTargets)
	{
		Judge fakeJudge = null;
		List<Agent> judgeTargets2 = new ArrayList<Agent>(judgeTargets);
		Random rand = new Random();
		
		for (int i = 0; i < WOLF_MAX; ++i)
		{
			if (this.wolfProtect[i])
			{
				judgeTargets2.remove(this.wolfAgents.get(i));						
			}	
		}

		judgeTargets2.removeAll(Collections.singleton(null));
		
		if (judgeTargets2.size() != 0)
		{
			fakeJudge = new Judge(getDay()-1, getMe(), judgeTargets2.get(rand.nextInt(judgeTargets2.size())), Species.WEREWOLF);
		}
		return fakeJudge;
	}
	
	//  確定でない適当なユニットを白と宣言
	protected Judge setFakeDivineJudge_RandomUnitWhiteCore(List<Agent> judgeTargets)
	{
		Judge fakeJudge = null;
		List<Agent> judgeTargets2 = new ArrayList<Agent>(judgeTargets);
		Random rand = new Random();

		judgeTargets2.removeAll(Collections.singleton(null));
		if (judgeTargets2.size() != 0)
		{
			fakeJudge = new Judge(getDay()-1, getMe(), judgeTargets2.get(rand.nextInt(judgeTargets2.size())), Species.HUMAN);
		}
		return fakeJudge;
	}
	
	/*
	 *	@brief		生きている狼の数を取得 
	 */
	private int GetAliveWolfNum()
	{
		int ret = 0;

		for (int i = 0; i < WOLF_MAX; ++i)
		{
			if (getLatestDayGameInfo().getAliveAgentList().contains(wolfAgents.get(i)))
			{
				++ret;
			}
		}		
		return ret;
	}

	@Override
	protected void setFakeInquestJudge(Agent executedAgent) 
	{
		Judge fakeJudge = null;
		
		// ゲームが終わっていない矛盾をはらまないように。
		if (this.getBlackJudgeNum() >= MyGameInfo.getMaxAgentNum(Role.WEREWOLF) - 1)
		{
			if(MyGameInfo.IS_PRINT()) System.out.println("霊能 pt1");
			fakeJudge = new Judge(getDay()-1, getMe(), getLatestDayGameInfo().getExecutedAgent(), Species.HUMAN);
		}

		// 協調ユニットがいれば、協調した発言
		// 守護パターンごとにhuman登録、wolf登録が可能かのチェック
		boolean []humanRegSafe = new boolean [WOLFPROTECT_MASK_MAX];
		boolean []wolfRegSafe = new boolean [WOLFPROTECT_MASK_MAX];
		
		List<Pattern> pattern = new ArrayList<Pattern>();
		
		for (int i = 0; i < WOLFPROTECT_MASK_MAX; ++i)
		{
			humanRegSafe[i] = false;
			wolfRegSafe[i] = false;
			ProtectPatternsWithMask(pattern, i);
			PatternMaker.RemoveEnemyPattern(pattern, getLatestDayGameInfo().getExecutedAgent(), true);
			if (pattern.size() != 0)
			{
				humanRegSafe[i] = true;
			}

			ProtectPatternsWithMask(pattern, i);
			PatternMaker.RemoveEnemyPattern(pattern, getLatestDayGameInfo().getExecutedAgent(), false);
			if (pattern.size() != 0)
			{
				wolfRegSafe[i] = true;
			}
			
			// 黒でも白でも矛盾が発生するなら次の守護パターン
			if (humanRegSafe[i] == false && wolfRegSafe[i] == false) continue;
			

			// 協調エージェントがいればそいつに合わせる。
			// 今までの占い一覧
			List<Judge> prevJudges = advanceGameInfo.getInspectJudges();
			
			// 処刑したユニットに対して行っていた占い
			List<Judge> executeJudges = new ArrayList<Judge>();
			
			for(Judge judge: prevJudges)
			{
				if (judge.getTarget() == getLatestDayGameInfo().getExecutedAgent())
				{
					executeJudges.add(judge);
				}
			}

			for(Judge judge: executeJudges)
			{
				// 占い元ユニットとの連携
				if (isProtectAgent(judge.getAgent()))
				{
					if (judge.getResult() == Species.HUMAN)
					{	
						if(MyGameInfo.IS_PRINT()) System.out.println("霊能 pt2");
						fakeJudge = new Judge(getDay()-1, getMe(), getLatestDayGameInfo().getExecutedAgent(), Species.HUMAN);
					}
					else
					{
						if(MyGameInfo.IS_PRINT()) System.out.println("霊能 pt3");
						fakeJudge = new Judge(getDay()-1, getMe(), getLatestDayGameInfo().getExecutedAgent(), Species.WEREWOLF);
					}
					break;
				}
			}
			
			if (fakeJudge != null) break;
		}

		// 矛盾が生じないデータを調べていって発見次第登録
		// 人 > 狼
		if (fakeJudge == null)
		{
			for (int i = 0; i < WOLFPROTECT_MASK_MAX; ++i)
			{
				ProtectPatternsWithMask(pattern, i);
				if (humanRegSafe[i] == true)
				{
					if(MyGameInfo.IS_PRINT()) System.out.println("霊能 pt4");
					fakeJudge = new Judge(getDay()-1, getMe(), getLatestDayGameInfo().getExecutedAgent(), Species.HUMAN);							
				}
				else if (wolfRegSafe[i] == true)
				{
					if(MyGameInfo.IS_PRINT()) System.out.println("霊能 pt5");
					fakeJudge = new Judge(getDay()-1, getMe(), getLatestDayGameInfo().getExecutedAgent(), Species.WEREWOLF);							
				}
				if (fakeJudge != null) break;
			}			
		}

		// とりあえず人(余りここまで来てほしくない)
		if (fakeJudge == null)
		{
			if(MyGameInfo.IS_PRINT())  System.out.println("霊能 pt6");
			fakeJudge = new Judge(getDay()-1, getMe(), getLatestDayGameInfo().getExecutedAgent(), Species.HUMAN);			
		}

		notToldjudges.add(fakeJudge);
		fakeJudges.add(fakeJudge);
		todaysFakeJudge = fakeJudges.get(fakeJudges.size()-1);
	}

	@Override
	public void setVoteTarget() 
	{
		// RPP or PP
		if (bRealCoFinish)
		{
			List<Vote> votes = advanceGameInfo.getVoteList(getDay());
			List<Agent> voteAgents = new ArrayList<Agent>();
			
			for(Vote vote: votes)
			{
				if(wolfAgents.contains(vote.getAgent()) && voteAgents.contains(vote.getTarget()) == false)
				{
					voteAgents.add(vote.getTarget());
				}
			}
			
			Agent target = this.getMostVoteAgent(voteAgents);
			
			if (target != null) return;
		}
		List<Pattern> pattern = new ArrayList<Pattern>(this.otherPatterns);
		ProtectPatternsWithMask(pattern, (1 << 2) - 1);
		setVoteTargetTemplate(pattern);
	}

	@Override
	void updatePreConditionQVal(boolean isVillagerWin)
	{
		//偽役職のCO
		updateCOElements(isVillagerWin);
	}

	@Override
	protected void initializeFakeRole() 
	{
		Map<WolfFakeRoleChanger, Double> map = ld.getWolfFakeRoleChanger();
		changer = selectRandomTarget(map);
		fakeRole = changer.getInitial();
	}

	@Override
	void updateCOElements(boolean isVillagerWin) 
	{
		Map<COtimingNeo, Double> map = getCOMap();
		double q = map.get(coTiming);
		double reward = (isVillagerWin)? 100.0: 0;
		double learnedQ = ReinforcementLearning.reInforcementLearn(q, reward, 0);
		map.put(coTiming, learnedQ);
		
		Map<WolfFakeRoleChanger, Double> changerMap = ld.getWolfFakeRoleChanger();
		double qW = changerMap.get(changer);
		// double learnedQW = ReinforcementLearning.reInforcementLearn(qW, reward, 0);
		changerMap.put(changer, learnedQ);
	}
}