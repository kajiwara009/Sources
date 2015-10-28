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
	
	// ���l��Agent�D�s�m��̎���null
	Agent possessedAgent = null;
	
	// �l�T�̃G�[�W�F���g
	List<Agent> wolfAgents= new ArrayList<Agent>();
	boolean wolfProtect[] = new boolean[WOLF_MAX];

	// Whisper�Ŏ������x���E��`������
	boolean hasWhisperedFakeRole = false;

	// �l�T�B��fakeRole�ɖ������N����Ȃ�Patterns
	List<Pattern> wolfsPatterns;

	// ���Ԑl�T��fakeRole
	Map<Agent, Role> wolfsFakeRoleMap = new HashMap<Agent, Role>();

	// Whisper���ǂ��܂œǂ񂾂�
	int readWhisperNumber = 0;
	
	// �΍RCO�f�B���C
	int oppsitionCODelay = OPPOSITION_DELAY;

	// �����̉RJudge��Whisper�œ`������
	boolean hasWhisperTodaysFakeJudge;

	// �����̉RJudge
	Judge todaysFakeJudge;

	// Whisper���ꂽJudge�̃��X�g
	List<Judge> whisperedJudges = new ArrayList<Judge>();
	
	// Whisper���ꂽ�U���̃��X�g(�U������U��)
	Map<Agent, Agent> whisperedAttacks = new HashMap<Agent, Agent>();
	Agent attackAgent;
	Agent whisperedAttackAgent;
	
	int coFlag_NTurn = 0;
	int coFlag_Turn = 0;
	
	WolfFakeRoleChanger changer;
	
	private boolean
		wolfJudged = false,			// �l�T���Ɛ��ꂽ�����x���E
		existVillagerWolf = false,	// �����̐l�T�����l���x��Ƃ����������x���E
		existSeerWolf = false,
		existMediumWolf = false,
		seerCO = false,				// �肢�t���o�Ă����Ƃ����x���E
		mediumCO = false,
		isVoteTarget = false;		// ���[�ΏۂɂȂ��������x���E


	@Override
	public void initialize(GameInfo gameInfo, GameSetting gameSetting) 
	{
		super.initialize(gameInfo, gameSetting);
		
		for (int i = 0; i < WOLF_MAX; ++i)
		{
			wolfProtect[i] = true;
		}

		// myPatterns�ɒ��Ԃ̐l�T���Z�b�g����
		for(Entry<Agent, Role> set: gameInfo.getRoleMap().entrySet())
		{
			if(!set.getKey().equals(getMe()))
			{
				PatternMaker.settleAgentRole(myPatterns, set.getKey(), Role.WEREWOLF);
				wolfAgents.add(set.getKey());
			}
		}
		wolfAgents.add(getMe());
		
		// ����Ȃ���������
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

		// whisper�̏���

		List<Talk> whisperList = gameInfo.getWhisperList();

		/*
		 * �e���b�ɂ��Ă̏���
		 * �J�~���O�A�E�g�ɂ��Ă̓p�^�[���̊g��
		 * �\�͌��ʂ̔��b�ɂ��Ă̓p�^�[�����̍X�V
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
			//��L�ȊO
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
			//�肢�t�x��̏ꍇ�C2���ڈȍ~fakeJudge�������
			case SEER:
				if(getDay() >= 2){
					setFakeDivineJudge();
				}
				break;

			//��\���x��̏ꍇ�C�P�����ꂽAgent�������fakeJudge�������
			case MEDIUM:
				if(getLatestDayGameInfo().getExecutedAgent() != null){
					setFakeInquestJudge(getLatestDayGameInfo().getExecutedAgent());
				}
				break;

			//���l�x��̏ꍇ�C�������Ȃ�
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
			System.err.printf("�����F0041:%d\n",time);
		}
	}
	
	public void PPCheck()
	{
		// �{CO�����I��
		if (this.bRealCoFinish == true)
		{
			return;
		}
		
		// �G�̐��Ƒ��l�̐����o���B
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
		
		// RPP�ł������������Ȃ�ok
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
			System.err.printf("�����F0050:%d\n",time);
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
		
		// �J�~���O�A�E�g�ς݂Ȃ瓦�����Ȃ��B
		if (this.isComingout) return;

		ArrayList<Pattern> calcPatterns = new ArrayList<Pattern>(otherPatterns);
		
		// �����o���o���Ȃ炠����߂����E�E�E
		if (calcPatterns.size() <= 1) return;
		
		// ���̐l�T���J�~���O�A�E�g���Ă���E��ɂ̓J�~���O�A�E�g���Ȃ��B
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

		// ���ƈ�l��CO���[���ŕ������ԂɂȂ��Ă�����J�~���O�A�E�g���Ȃ�
		if (seerNum >= 1) --seerNum;
		if (mediumNum >= 1) --mediumNum;
		if (bodyGuardNum >= 1) --bodyGuardNum;
		
		if (seerNum + mediumNum + bodyGuardNum - 1 >= MyGameInfo.getMaxAgentNum(Role.WEREWOLF) + MyGameInfo.getMaxAgentNum(Role.POSSESSED))
		{
			return;
		}
		
		// ��Ȏ��ɂȂ�Ȃ��̂ŁA�]��CO�̔������Ă��鏊�ɂ͓˂�����ł����Ȃ��B
		if (seerNum >= 1) isSeerCO = false;
		if (mediumNum >= 1) isMediumCO = false;
		if (bodyGuardNum >= 1) isBodyGuardCO = false;
		
		
		// �i�삷��l�T���݂̃p�^�[�������l����
		ProtectPatterns(calcPatterns);
		
		// �i��ł���P�[�X�������E�E�E
		if (calcPatterns.size() == 0) return;
		
		// �؂�ʂ��ɂ͎������g��؂�̂ĂȂ���΂Ȃ�Ȃ�
		if (this.wolfProtect[WOLF_MAX - 1] == false) return;
		
		// �؂̒ʂ�p�^�[�����炳��ɍi���݂��s���B
		ArrayList<Pattern> calcPatterns2 = new ArrayList<Pattern>(calcPatterns);
		
		// �e�E���Ƃ̃`�F�b�N
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
			// ���܂ŏ��Y���ꂽ���j�b�g�̂����A�Ō�̈�l�������Ĕ��A�Ō�̈�l�����ɏo������ok
			if (fakeTestMedium(calcPatterns))
			{
				if(MyGameInfo.IS_PRINT()) System.out.println("##MEDIUM");
				fakeRole = Role.MEDIUM;
				notToldjudges = new ArrayList<Judge>(fakeJudges);
				doComingout = true;

				// �M���x�𓾂邽�߁A2���ڈȍ~�A����b���ɃJ�~���O�A�E�g����B
				if (this.dayTalkCount == 0 && getDay() >= 2)
				{
					priorityComingout = true;
				}
				
				return;
			}
		}
		if (isBodyGuardCO)
		{
			// bodyguard�͂ǂ����邩�E�E�E
		}
	}

	@Override
	public String getComingoutText() 
	{
		int day = getDay();
		/*
		 * �J�~���O�A�E�g��������ɂȂ�
		 * ���ɓ����\�͎�CO���o��
		 * �l�T��������
		 * ���[��ɑI�΂ꂻ���i�S�̂�2/3�����[���S���[����1/4�ȏオ�����ɓ��[�j
		 */
		if(isComingout || !doComingout)
		{
			return null;
		}
		else
		{
			// �D��I�ȃJ�~���O�A�E�g�̎��s
			if (priorityComingout)
			{
				return comingoutFakeRole();				
			}
			// �����ɂ��J�~���O�A�E�g
			/*
			if(coTiming.getDay() == getDay())
			{
				return comingoutFakeRole();
			}
			*/

			// ================================================================
			// �UCO�o��
			int saveCODelay = oppsitionCODelay;
			if((coFlag_NTurn & COPercent.OPPOSION) != 0)
			{
				Map<Agent, Role> comingoutMap = advanceGameInfo.getComingoutMap();
				for(Entry<Agent, Role> set: comingoutMap.entrySet())
				{
					if(set.getValue() == fakeRole && !set.getKey().equals(getMe()))
					{
						// ���l���������Ă���ꍇ�͑��΍RCO
						if (this.possessedAgent != null)
						{
							return comingoutFakeRole();							
						}
						
						// ��莞�Ԍ��CO
						--oppsitionCODelay;
						if (oppsitionCODelay <= 0)
						{
							return comingoutFakeRole();														
						}
						break;
					}
				}
			}
			
			// �J�E���g�_�E������Ȃ���΋t�ɃJ�E���^�����Z�b�g
			if (oppsitionCODelay == saveCODelay)
			{
				oppsitionCODelay = OPPOSITION_DELAY;
			}
			
			// ================================================================
			// ���[��ɑI�΂ꂻ��
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
			
			// �l�T���Ɛ��ꂽ
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
	
	// �肢�t�ɂȂ낤�Ƃ��Ă݂�
	// ���ɂ��̐�-1�̐l���̐肢��1�̐����Ă���l�T�̐肢�ŋ؂�ʂ���ok
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
		
		// �����͐��Ȃ�
		agentList.remove(getMe());
		
		// ���m�G�[�W�F���g�͊�{���Ȃ��B
		for(Entry<Agent, EnemyCase> set: p.getEnemyMap().entrySet())
		{
			if (set.getValue() == EnemyCase.black)
			{
				agentList.remove(set.getKey());						
			}
		}
		
		for (int i = 0; i < day; ++i)
		{					
			// �O���܂łɎ��S���Ă��郆�j�Ƃ�肢�Ώۂ���͂����B
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
				// �؂�̂Ă�l�T������΁A�������������ɂ���B
				for (int j = 0; j < WOLF_MAX - 1; ++j)
				{
					if (
							wolfProtect[j] == false 
							&& this.getLatestDayGameInfo().getAliveAgentList().contains(wolfAgents.get(j))
					)
					{
						if(MyGameInfo.IS_PRINT()) System.out.println("�����肢���X1:1");
						Agent judgeAgent = wolfAgents.get(j);
						Judge newJudge = new Judge(i, getMe(), judgeAgent, Species.WEREWOLF);
						fakeJudges.add(newJudge);
						return true;
					}
				}

				// ============================================================
				// ���݂̃p�^�[���ɍ��m�G�[�W�F���g����������΂������������ɂ���B(�p�^�[�����Ȃ��̂Ő擪�ł���)
				for(Entry<Agent, EnemyCase> set: p.getEnemyMap().entrySet())
				{
					if (
						set.getValue() == EnemyCase.black 
						&& this.getLatestDayGameInfo().getAliveAgentList().contains(set.getKey())
					)
					{
						if (this.isProtectAgent(set.getKey())) continue;
						if(MyGameInfo.IS_PRINT()) System.out.println("�����肢���X1:2");
						Agent judgeAgent = set.getKey();		
						Judge newJudge = new Judge(i, getMe(), judgeAgent, Species.WEREWOLF);
						fakeJudges.add(newJudge);	
						return true;
					}
				}

				// ============================================================
				// ���m�ȊO�̐l�T�ȊO����K���ɐl�T�����肷��
				List<Agent> notWhiteAgent = new ArrayList<Agent>(agentList);
				for(Agent whiteAgent: p.getWhiteAgentSet())
				{
					notWhiteAgent.remove(whiteAgent);
				}

				// ���Ώۂ͐l�T�ɂ��Ȃ�
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
					if(MyGameInfo.IS_PRINT()) System.out.println("�����肢���X1:3");
					Agent judgeAgent = notWhiteAgent.get(rand.nextInt(notWhiteAgent.size()));
					Judge newJudge = new Judge(i, getMe(), judgeAgent, Species.WEREWOLF);
					fakeJudges.add(newJudge);
					return true;
				}

				// ============================================================
				// �K���ɐl�ԂƂ��ēo�^(�����肢�Ɏ��s�����ꍇ�́A�肢�t�ɂ͂Ȃ�Ȃ���������)
				if (agentList.size() == 0)
				{
					return false;
				}
				// �l��
				if(MyGameInfo.IS_PRINT()) System.out.println("�����肢���X1:4");
				Agent judgeAgent = agentList.get(rand.nextInt(agentList.size()));
				Judge newJudge = new Judge(i, getMe(), judgeAgent, Species.HUMAN);
				fakeJudges.add(newJudge);
				agentList.remove(judgeAgent);
				return true;
			}
			else
			{
				// �ꉞ�P�A
				if (agentList.size() == 0)
				{
					return false;
				}
				
				// �l��
				Agent judgeAgent = agentList.get(rand.nextInt(agentList.size()));
				Judge newJudge = new Judge(i, getMe(), judgeAgent, Species.HUMAN);
				fakeJudges.add(newJudge);
				agentList.remove(judgeAgent);
			}
		}
		return false;
	}
	
	// ��\�҂ɂȂ낤�Ƃ��Ă݂�
	private boolean fakeTestMedium(ArrayList<Pattern> calcPattern)
	{
		fakeJudges = new ArrayList<Judge>();
		
		ArrayList<Pattern> calcPatterns2 = new ArrayList<Pattern>(calcPattern);
		int day = getDay();

		// ���܂ł̐肢�ꗗ
		List<Judge> prevJudges = advanceGameInfo.getInspectJudges();
		
		for (DeadCondition dc: advanceGameInfo.getDeadConditions())
		{
			if (dc.getCause() == CauseOfDeath.executed)
			{
				// �D��錾�̃��Z�b�g
				priorityComingout = false;
				
				// ���Y�������j�b�g�ɑ΂��čs���Ă����肢
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
					// �肢�����j�b�g�Ƃ̘A�g
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
						
						// �����A�Ō�̉����肪�A�g�v�l�̏ꍇ�͗D��I�ɃJ�~���O�A�E�g�����āA�肢�t���T�|�[�g
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
		// �؂��ʂ��Ă����ok
		if (calcPatterns2.size() != 0)
		{
			return true;
		}
		priorityComingout = false;
		return false;
	}
	
	// ���(=����)����ׂ��G�[�W�F���g���ǂ����擾
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
	 * �l�T���Œ��l�ȏ㔒�m�ŋ؂̒ʂ�p�^�[���ƒN�𔒊m�ɂ��邩�̐ݒ���s���܂��B
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
			// �؂̒ʂ�p�^�[������������
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
		// ���܂�Ӗ��������Ȃ��̂Ŗ����� @�Ή�
		if (true)
		{
			return;
		}
		
		/*
		 * wolfsPatterns���X�V����
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
		 * �l�T���m�������\Pattern������Ƃ��ɁC���ꂪ�������玩����Judge����������
		 * ���l�x��Ȃ牽�����Ȃ�
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
		// TODO �����������ꂽ���\�b�h�E�X�^�u
	}
	
	@Override
	public void comingoutTalkDealing(Talk talk, Utterance utterance)
	{
		super.comingoutTalkDealing(talk, utterance);

		// �T�̂Ƃ�(RPP or PP)
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
	 * ���l�m���Agent�����邩�m���߂�
	 * �����ꍇ��possessedAgent�ɂ����
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
			//�S�Ă�Pattern�ɂ����Đ^�\�͎҂Ƃ���Ă��Ȃ��J�~���O�A�E�g�����v���C���[�����l
			possessedAgent = set.getKey();
		}
	}

	/**
	 * ���l���������Ă��邩��Ԃ�
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
	 * ���l���������Ă���ꍇ��FakeRole��Ԃ�
	 * �������Ă��Ȃ��ꍇ��null
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
		// ���[�\�Ȃ̂͐����� - �T
		List<Agent> baseList = new ArrayList<Agent>(getLatestDayGameInfo().getAliveAgentList());
		baseList.removeAll(wolfAgents);
		
		// 1�l�ȏ㓊�[���\�ȃP�[�X������A���������郋�[�g��T���B
		int protectPattern = 0;
		for (; protectPattern < (1 << WOLF_MAX); ++protectPattern)
		{		
			// ���D��P�����X�g
			List<Agent> hPriorityList = new ArrayList<Agent>();
		
			List<Pattern> patterns = new ArrayList<Pattern>();
			
			ProtectPatternsWithMask(patterns,protectPattern);
			
			// ����p�^�[��������
			if (patterns.size() == 0) continue;

			// ================================================================
			// �P�̈ȏ㖵���𐶂��������ɎE����L�����N�^�����邩�ǂ���
			List<Agent> wolfDetermineAgent = new ArrayList<Agent> (getLatestDayGameInfo().getAliveAgentList());
			
			// ���l�͍������̂��ߋ�̃f�[�^�B
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
			
			// ��D��P�����X�g
			List<Agent> lPriorityList = new ArrayList<Agent>(hPriorityList);

			// ================================================================
			// 3�l�ȏ㓯CO���������Ă�����ł͂���CO��_��Ȃ��B(���E��)
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
			// ����U������Ă����ȃG�[�W�F���g�ɂ͍U�����Ȃ�(���E��)
			// ���l�A2���ȓ��i�K�̒P��CO���j�b�g�͏P�����Ȃ�(���E��)
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

			// ���l�A2���ȓ��i�K��CO���j�b�g�͏P�����Ȃ�(���E��)
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
			
			// ���D��P�����X�g
			List<Agent> mPriorityList = new ArrayList<Agent>(hPriorityList);
			
			// ================================================================
			// �����ꂩ�̐肢�t���甒��������Ă��Ȃ��ƏP�����Ȃ�(��)
			List<Agent> judgeWhiteAgents = new ArrayList<Agent>();
			
			for (Judge judge: advanceGameInfo.getInspectJudges())
			{
				// �T
				if (judge.getResult() == Species.WEREWOLF) continue;
				
				// ���D�惊�X�g�ɓ����Ă��Ȃ�
				if (hPriorityList.contains(judge.getTarget()) == false) continue;

				// ���łɓo�^�ς�
				if (judgeWhiteAgents.contains(judge.getTarget()) == true) continue;
				
				judgeWhiteAgents.add(judge.getTarget());
			}
			hPriorityList = new ArrayList<Agent>(judgeWhiteAgents);
			
			// �D��x���ɓ��[
			
			// whisper���X�g�ɓo�^����Ă��邩�H
			// ���D��
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

			// ���D��
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

			// ��D��
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
	// fake�肢�B
	protected void setFakeDivineJudge() 
	{
		for (int i = 0; i < WOLFPROTECT_MASK_MAX; ++i)
		{
			// �p�^�[���ɖ����������Ȃ��悤�ɂ���
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

			// ���`�F�b�N
			{
				List<Pattern> patterns2 = new ArrayList<Pattern>(patterns);
				PatternMaker.RemoveEnemyPattern(patterns2, judgeTarget, false);
				
				// ����������
				if (patterns2.size() == 0) canBlack = false;
				
				// 12�l���炢�܂ł͂���ĂȂ�
				if (aliveSize >= 12)  canBlack = false;
				
				// 6�l�ȏ�̂Ƃ��A�������̌�����s���ƁE�E�E�Q�[���N���A�ɂȂ�
				if (aliveSize >= 6 && minEnemyNum == 3)  canBlack = false;
			}
			
			// ���`�F�b�N
			{
				List<Pattern> patterns2 = new ArrayList<Pattern>(patterns);
				PatternMaker.RemoveEnemyPattern(patterns2, judgeTarget, true);
				
				// ����������
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
	
	//  1-1  �l�T�ȊO�̐肢�t�܂��͗�}�t���j�b�g�����Ɛ錾
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
	
	//  1-2  �c��v���C���[��9�l�ȏ�Ȃ�A�K���ȃ��j�b�g�𔒂Ɛ錾
	protected Judge setFakeDivineJudge_RandomUnitWhite(List<Agent> judgeTargets)
	{
		Judge fakeJudge = null;
		if (this.getLatestDayGameInfo().getAliveAgentList().size() < 9) return fakeJudge;
		
		fakeJudge = setFakeDivineJudge_RandomUnitWhiteCore(judgeTargets);
		
		return fakeJudge;
	}
	
	// 1-3 �m��łȂ��K���ȃ��j�b�g�����Ɛ錾(���Ώۂ̐l�T����)
	protected Judge setFakeDivineJudge_RandomUnitBlack(List<Agent> judgeTargets)
	{
		return setFakeDivineJudge_RandomUnitBlackCore(judgeTargets);
	}

	//  2-1 �l�T - �l�Ԃ̐���2�ȉ�
	//   2-1-1 ���m��łȂ��G�[�W�F���g������΂�������Ɛ錾
	protected Judge setFakeDivineJudge_RandomUnitBlack2(List<Agent> judgeTargets)
	{
		int wolfNum = GetAliveWolfNum();
		int humanNum = this.getLatestDayGameInfo().getAliveAgentList().size() - wolfNum;
		
		if (humanNum - wolfNum > 2) return null;
				
		return setFakeDivineJudge_RandomUnitBlackCore(judgeTargets);
	}

	//  2-2 �m��łȂ��K���ȃ��j�b�g�𔒂Ɛ錾
	protected Judge setFakeDivineJudge_RandomUnitWhite2(List<Agent> judgeTargets)
	{
		return setFakeDivineJudge_RandomUnitWhiteCore(judgeTargets);		
	}

	//  �m��łȂ��K���ȃ��j�b�g�����Ɛ錾
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
	
	//  �m��łȂ��K���ȃ��j�b�g�𔒂Ɛ錾
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
	 *	@brief		�����Ă���T�̐����擾 
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
		
		// �Q�[�����I����Ă��Ȃ��������͂�܂Ȃ��悤�ɁB
		if (this.getBlackJudgeNum() >= MyGameInfo.getMaxAgentNum(Role.WEREWOLF) - 1)
		{
			if(MyGameInfo.IS_PRINT()) System.out.println("��\ pt1");
			fakeJudge = new Judge(getDay()-1, getMe(), getLatestDayGameInfo().getExecutedAgent(), Species.HUMAN);
		}

		// �������j�b�g������΁A������������
		// ���p�^�[�����Ƃ�human�o�^�Awolf�o�^���\���̃`�F�b�N
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
			
			// ���ł����ł���������������Ȃ玟�̎��p�^�[��
			if (humanRegSafe[i] == false && wolfRegSafe[i] == false) continue;
			

			// �����G�[�W�F���g������΂����ɍ��킹��B
			// ���܂ł̐肢�ꗗ
			List<Judge> prevJudges = advanceGameInfo.getInspectJudges();
			
			// ���Y�������j�b�g�ɑ΂��čs���Ă����肢
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
				// �肢�����j�b�g�Ƃ̘A�g
				if (isProtectAgent(judge.getAgent()))
				{
					if (judge.getResult() == Species.HUMAN)
					{	
						if(MyGameInfo.IS_PRINT()) System.out.println("��\ pt2");
						fakeJudge = new Judge(getDay()-1, getMe(), getLatestDayGameInfo().getExecutedAgent(), Species.HUMAN);
					}
					else
					{
						if(MyGameInfo.IS_PRINT()) System.out.println("��\ pt3");
						fakeJudge = new Judge(getDay()-1, getMe(), getLatestDayGameInfo().getExecutedAgent(), Species.WEREWOLF);
					}
					break;
				}
			}
			
			if (fakeJudge != null) break;
		}

		// �����������Ȃ��f�[�^�𒲂ׂĂ����Ĕ�������o�^
		// �l > �T
		if (fakeJudge == null)
		{
			for (int i = 0; i < WOLFPROTECT_MASK_MAX; ++i)
			{
				ProtectPatternsWithMask(pattern, i);
				if (humanRegSafe[i] == true)
				{
					if(MyGameInfo.IS_PRINT()) System.out.println("��\ pt4");
					fakeJudge = new Judge(getDay()-1, getMe(), getLatestDayGameInfo().getExecutedAgent(), Species.HUMAN);							
				}
				else if (wolfRegSafe[i] == true)
				{
					if(MyGameInfo.IS_PRINT()) System.out.println("��\ pt5");
					fakeJudge = new Judge(getDay()-1, getMe(), getLatestDayGameInfo().getExecutedAgent(), Species.WEREWOLF);							
				}
				if (fakeJudge != null) break;
			}			
		}

		// �Ƃ肠�����l(�]�肱���܂ŗ��Ăق����Ȃ�)
		if (fakeJudge == null)
		{
			if(MyGameInfo.IS_PRINT())  System.out.println("��\ pt6");
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
		//�U��E��CO
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