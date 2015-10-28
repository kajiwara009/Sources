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
 * �S��E���ʕ����̃A���S���Y��
 * initialize�F�����p�^�[���쐬
 * update�F���b���O����AGI�X�V�CPattern�X�V
 * dayStart�FAGI�̎��S�v���C���[���X�V
 * @author kengo
 *
 */
public abstract class AbstractKajiBasePlayer extends AbstractRole {

	private boolean IS_LEARNING = true;

	private static double EPSILON = 0.1;
	private static double TEMP = 1.0;


	// CO,�\�͂̌��ʂȂǂ̃f�[�^�W��
	protected AdvanceGameInfo advanceGameInfo = new AdvanceGameInfo();

	// ���肤��p�^�[���S��
	protected List<Pattern> generalPatterns = new ArrayList<Pattern>();

	// �����̖�E����ꂽ�p�^�[��
	protected List<Pattern> myPatterns = new ArrayList<Pattern>();

	// �g�[�N���ǂ��܂œǂ񂾂�
	protected int readTalkNumber = 0;

	// �������[����v���C���[(�b��)
	protected Agent voteTarget = null;

	// �ŐV�̔��b�Ō��������[��v���C���[
	protected Agent toldVoteTarget = null;
	
	// ���O�ɍU�����ꂽ�G�[�W�F���g
	protected Agent lastAttackedAgent = null;

	// ���O�ɏ��Y���ꂽ�G�[�W�F���g
	protected Agent lastExecutedAgent = null;
	
	// �M���ł���肢�t
	protected Agent believeAgent = null;
	
	// ���[���Ȃ��G�[�W�F���g���X�g
	protected List<Agent> notVoteAgentList = new ArrayList<Agent>();

	// �l�Ԃ�myPattern����ꂽ���́C�l�T����fakePatterns����ꂽ���́D
	protected List<List<Pattern>> myPatternLists = new ArrayList<List<Pattern>>();

	// �w�K�f�[�^
	protected LearningData ld = LearningData.getInstance(0);
	
	// CO���O�H
	protected COPercent coPercent = COPercent.GetInstance(0);
	
	// CO�����T�̓��{�����ۂ�����
	List<Agent> coWolfPlayers = new ArrayList<Agent>();
	
	long		updateStartTime;
	
	//���̓�����ڂ�talk��
	protected int dayTalkCount;

	public boolean isWolf(Agent agent)
	{
		return false;
	}

	@Override
	public void initialize(GameInfo gameInfo, GameSetting gameSetting) 
	{
		/*
		 * �p�^�[������
		 */
		super.initialize(gameInfo, gameSetting);

		// �����p�^�[���̍쐬
		
		// CO,�\�͂̌��ʂȂǂ̃f�[�^�W��
		advanceGameInfo = new AdvanceGameInfo();

		// ���肤��p�^�[���S��
		generalPatterns = new ArrayList<Pattern>();

		// �����̖�E����ꂽ�p�^�[��
		myPatterns = new ArrayList<Pattern>();

		// �g�[�N���ǂ��܂œǂ񂾂�
		readTalkNumber = 0;

		// �������[����v���C���[(�b��)
		voteTarget = null;

		// �ŐV�̔��b�Ō��������[��v���C���[
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
	 * pattern�ɂ�����agent���e��E�ɉ��p�[�Z���g�łȂ��Ă��邩�Ԃ�
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

		//���m���X�g�ɓ����Ă���ꍇ
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
		//���m���X�g�ɂ������Ă��Ȃ��ꍇ
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
		
		// 100ms�΍��gc�B�����͐؂��Ă���
        //Runtime rt = Runtime.getRuntime();
        //rt.gc();
		// System.gc();
        // gc�^�C���`�F�b�N
		
		if(MyGameInfo.IS_PRINT()) System.out.printf("%d:", getMe().getAgentIdx());
		/*
		 * ��b�̏���
		 * �b�蓊�[��̍X�V
		 */
		super.update(gameInfo);

		List<Talk> talkList = gameInfo.getTalkList();

		/*
		 * �e���b�ɂ��Ă̏���
		 * �J�~���O�A�E�g�ɂ��Ă̓p�^�[���̊g��
		 * �\�͌��ʂ̔��b�ɂ��Ă̓p�^�[�����̍X�V
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
					System.err.printf("�����F0012:%d\n",time);
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
					System.err.printf("�����F0013:%d\n",time);
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
					System.err.printf("�����F0014:%d\n",time);
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
					System.err.printf("�����F0015:%d\n",time);
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
					System.err.printf("�����F0016:%d\n",time);
				}
				patternChanged = true;
			}
				break;
				//��L�ȊO
			default:
				break;
			}
		}
		
		// (���l��p�X�L��) �M���ł���肢�t�̍X�V
		if (isVillager())
		{
			if (getDay() >= 3 && beliaveChanged)
			{
				List<Agent> seerAgents = new ArrayList<Agent>(PatternMaker.getAllSeerAgents(myPatterns));
				believeAgent = coPercent.getBeliaveSeer(seerAgents);				
			}
		}
		
		// ���[����X�V(�X�V��������Ȃǂ̓T�u�N���X�ŋL��)
		long start2 = System.currentTimeMillis();
		long end2 = 0;
		if(patternChanged)
		{
			setVoteTarget();
			end2 = System.currentTimeMillis();
			if (end2 - start2 >= 200)
			{
				int time = (int)(end2 - start2);
				System.err.printf("�����F0010:%d\n",time);
			}
		}
		long end = System.currentTimeMillis();
		
		if (end - updateStartTime >= 200)
		{
			int time = (int)(end - updateStartTime);
			System.err.printf("�����F0011:%d\n",time);
		}
	}

	/**
	 * �J�~���O�A�E�g�̔��b�̏���
	 * @param talk
	 * @param utterance
	 */
	public void comingoutTalkDealing(Talk talk, Utterance utterance)
	{
		long start = System.currentTimeMillis();
		// �l�T�A���lCO�͂܂Ƃ��Ȕ�������Ȃ�
		if (utterance.getRole() == Role.WEREWOLF || utterance.getRole() == Role.POSSESSED)
		{
			// wolfside�̏ꍇ�́A�����ł͏������Ȃ��B
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
				System.err.println("����");
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
			System.err.printf("�����F0031:%d\n",time);
		}
		PatternMaker.extendPatternList(generalPatterns, talk.getAgent(), utterance.getRole(), advanceGameInfo);
		PatternMaker.extendPatternList(myPatterns, talk.getAgent(), utterance.getRole(), advanceGameInfo);
		
		end = System.currentTimeMillis();
		
		if (end - start >= 500)
		{
			int time = (int)(end - start);
			System.err.printf("�����F0030:%d\n",time);
		}

	//	actionLog.AddCO(talk.getAgent(), getDay(), 0, utterance.getRole());

	}

	/**
	 * �������Ⴄ�o�[�W����
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
	 * �肢���ʂ̔��b�̏���
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
	 * ��\���ʂ̔��b�̏���
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
	 * �K�[�h���ʂ̔��b�̏���
	 * @param talk
	 * @param utterance
	 */
	public void guardedTalkDealing(Talk talk, Utterance utterance)
	{
		if(advanceGameInfo.getComingoutMap().get(talk.getAgent()) != Role.BODYGUARD)
		{
			comingoutTalkDealing(talk.getAgent(), Role.BODYGUARD);
		}
		
		// ���O�̏P�������݂��Ȃ��āA�K�[�h�����Ɛ錾���Ă��郆�j�b�g�������Ă�����pattern�̍쐬���s��
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
	 * ���[�ӎv�̔��b�̏���
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
		// �����myPatterns���w�K�p�ɕۑ�
		addCopyToMyPatternLists();
		
		// talk�J�E���^�̏�����
		dayTalkCount = 0;
		
		/*
		 * ���S�v���C���[���̍X�V
		 * �b�蓊�[��̍X�V
		 */
		readTalkNumber = 0;
		
		// ���S�����v���C���[��AGI�ɋL�^
		// ���Y
		lastExecutedAgent = getLatestDayGameInfo().getExecutedAgent();
		PatternMaker.updateExecutedData(generalPatterns, lastExecutedAgent);
		PatternMaker.updateExecutedData(myPatterns, lastExecutedAgent);
		if(lastExecutedAgent != null)
		{
			DeadCondition executeddAgentCondition = new DeadCondition(lastExecutedAgent, getDay(), CauseOfDeath.executed);
			advanceGameInfo.addDeadConditions(executeddAgentCondition);
		}
		
		// �P��
		lastAttackedAgent = getLatestDayGameInfo().getAttackedAgent();
		PatternMaker.updateAttackedData(generalPatterns, lastAttackedAgent);
		PatternMaker.updateAttackedData(myPatterns, lastAttackedAgent);
		if(lastAttackedAgent != null)
		{
			DeadCondition attackedAgentCondition = new DeadCondition(lastAttackedAgent, getDay(), CauseOfDeath.attacked);
			advanceGameInfo.addDeadConditions(attackedAgentCondition);
		}

		//�����̎b�蓊�[��
		toldVoteTarget = null;
		voteTarget = null;
		setVoteTarget();

	//	actionLog.dayUpdate(getDay(), lastAttackedAgent, lastExecutedAgent);
		
		// ���l�n��p����
		if (isVillager())
		{
			if (getDay() >= 1)
			{
				// coWolfPlayers�Ɠ����L�����ɓ��[���Ă��郆�j�b�g��coWolfPlayers�ɒǉ�
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
				
				// coWolfPlayers���甒�m�����������B
			}
			
			// (���l��p�X�L��) �M���ł���肢�t�𓝌v�I�ɏo��
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
			System.err.printf("�����F0040:%d\n",time);
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
			System.err.printf("�����F0020:%d\n",time);
		}
		return ret;
	}

	public String onTalk() 
	{
		// talk�J�E���^�̑����B
		++dayTalkCount;
		
		/*
		 * ���b�����̗D��x
		 * �J�~���O�A�E�g���\�͌��ʂ̔��b�����[��̔��b
		 */
		// �J�~���O�A�E�g�̔��b
		{
			long start = System.currentTimeMillis();
			String comingoutReport = getComingoutText();
			long end = System.currentTimeMillis();
			if (end - start >= 500)
			{
				if(MyGameInfo.IS_PRINT()) 			System.err.println("����");
			}
			if(comingoutReport != null)
			{
				return comingoutReport;
			}			
		}

		// �肢�C��\���ʂ̔��b
		{
			long start = System.currentTimeMillis();
			String judgeReport = getJudgeText();
			long end = System.currentTimeMillis();
			if (end - start >= 500)
			{
				if(MyGameInfo.IS_PRINT()) 			System.err.println("����");
			}
			if(judgeReport != null)
			{
				return judgeReport;
			}			
		}


		// ���[��̔��b
		if(toldVoteTarget != voteTarget && voteTarget != null)
		{
			String voteReport = TemplateTalkFactory.vote(voteTarget);
			toldVoteTarget = voteTarget;
			return voteReport;
		}

		//�b�����Ƃ������Ȃ����
		return Talk.OVER;
	}

	/**
	 * �肢 or ��\���ʂ̔��b���s���D���ʂ̕񍐂����Ȃ��ꍇ��null��Ԃ�
	 * @return
	 */
	public abstract String getJudgeText();

	/**
	 * �J�~���O�A�E�g�̔��b���s���DCO���Ȃ��ꍇ��null��Ԃ�
	 * @return
	 */
	public abstract String getComingoutText();

	/**
	 * �������[�\��̃v���C���[�����肷��
	 * update��dayStart�̍Ō�ɂ�΂��
	 * @return
	 */
	public abstract void setVoteTarget();

	
	// ���[����ׂ����ۂ�
	public boolean CheckVote(Agent agent)
	{
		// ��Γ��[���Ȃ�
		if (notVoteAgentList.contains(agent))
		{
			return false;
		}
		
		// ����ł�
		if (getLatestDayGameInfo().getAliveAgentList().contains(agent) == false)
		{
			return false;
		}
		return true;
	}
	
	// CheckVote���L���ȃG�[�W�F���g�̒��ōł����[���̑����G�[�W�F���g���擾
	public Agent getMostVoteAgent(List<Agent> agents)
	{
		Agent target = null;
		int votePoint = -1;
		
		// �㔼�̉�b�́A0�[�̎v�l���s��Ȃ��悤��(�Ȃ�ׂ�)
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
		
		// ���ׂẴp�^�[���Ő肢�t�A��}�t�A��l����������΂����ɂ͓��[���Ȃ��B
		
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
				

		// �������g��ǉ�
		if (notVoteAgentList.contains(getMe()) == false)
		{
			notVoteAgentList.add(getMe());
		}
	}
	
	// 2���ȓ���CO������3�ȉ��̂Ƃ��ACO���j�b�g��ǉ�
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
	
	// 1-1	�l�T�m���j�b�g������΂���ɓ��[�B
	// 1-2	�l�Tor���l�m���j�b�g������΂���ɓ��[�B
	
	// �ȉ��̏����� 2���ȓ���CO������3�ȉ��̂Ƃ��ACO���j�b�g�ɑ΂��Ă͎��s���Ȃ��B
	// 1-3	CO�̓��A�Œ��l�͐^�肢�t�������Ɖ��肵����Ől�T�m���j�b�g������΂���ɓ��[�B
	// 1-4	CO�̓��A�Œ��l�͐^�肢�t�������Ɖ��肵����Ől�Tor���l�m���j�b�g������΂���ɓ��[�B
	// 1-5	�l�T�Ɛ���Ă��郆�j�b�g�̂����A�ł��[���������Ȃ肻���Ȃ��̂ɓ��[
	public void setVoteTargetTemplate(List<Pattern> patterns)
	{
		// �����f�[�^�̍폜(�s�v�H)
		/*
		int tmpSize = patterns.size();
		PatternMaker.removeContradictPatterns(patterns);
		if (tmpSize != patterns.size())
		{
			System.out.print("�����G�G");
		}
		*/
		
		// �M���ł���肢�t������΂��̃L�����N�^���g���Ďv�l����
		List<Pattern> basePatterns = new ArrayList<Pattern>(patterns);
		if (believeAgent != null)
		{
			PatternMaker.settleAgentRole(basePatterns, believeAgent, Role.SEER);
		}
		
		// ���[���Ȃ����j�b�g�̓o�^
		setNotVoteAgentList(basePatterns);
		
		// �ϐ�������
		int aliveSize = getLatestDayGameInfo().getAliveAgentList().size();
		Agent target = null;

		// ====================================================================
		// 1-1 �l�T�m���j�b�g������΂���ɓ��[�B
		
		// wolfCO�L����
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

		// �l�T�A���l�m��G�[�W�F���g���擾�B
		if (basePatterns.size() != 0)
		{
			List<Agent> wolfDetermineAgent = new ArrayList<Agent>(getLatestDayGameInfo().getAliveAgentList());
			List<Agent> possessedDetermineAgent = new ArrayList<Agent>(getLatestDayGameInfo().getAliveAgentList());
			
			// �擾�J�n
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
		// 1-2 �l�Tor���l�m���j�b�g������΂���ɓ��[�B
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
		// 1-3 CO�ɐ^�肢�t�������Ɖ��肵����Ől�T�m���j�b�g������΂���ɓ��[�B
		// 1-4 CO�ɐ^�肢�t�������Ɖ��肵����Ől�Tor���l�m���j�b�g������΂���ɓ��[�B
		{
			// 1-3 CO�̓��A�Œ��l�͐^�肢�t�������Ɖ��肵����Ől�T�m���j�b�g������΂���ɓ��[�B
		
			// �^�肢�t�t�p�^�[���̗p��
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

				// �擾�J�n
				PatternMaker.getDetermineEnemyAgent(hasSeerPatterns, wolfDetermineAgent, possessedDetermineAgent);
				target = getMostVoteAgent(wolfDetermineAgent);
				
				if (target != null)
				{
					if(MyGameInfo.IS_PRINT()) System.out.println("vote: 1-3");
					voteTarget = target;
					return;
				}			
				
				// 1-4 CO�̓��A�Œ��l�͐^�肢�t�������Ɖ��肵����Ől�Tor���l�m���j�b�g������΂���ɓ��[�B
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
		// 1-5�@�l�T�Ɛ���Ă��郆�j�b�g�̂����A�ł��[���������Ȃ肻���Ȃ��̂ɓ��[
		// �S�Ă̐肢�t���j�b�g�̎擾
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
		// ��萔�ȏ㓯�E�Ő鐾���Ă��郆�j�b�g������ΓK���ɓ��[
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
		// roleCONum�̌��ʁA�SCO��|���΂����Ɣ���������CO�����Ԃɓ|���Ă����B
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
		// 3�l�ȏ゠�܂肪����Ώ󋵂ɉ����đSCO�̏��Y���K�v���Ɣ���B
		// 4�l�ȏ�Ȃ�SCO�̏��Y���m��(��CO�m��͏���(�܂�������Ȃ����ǁE�E�E))
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
		// �O�������ԕ[���҂��ł��郆�j�b�g�ɓ��[
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
		// �ł����[���҂��ł��郆�j�b�g�ɓ��[
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
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		return null;
	}

	@Override
	public Agent attack() 
	{
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		return null;
	}

	@Override
	public Agent divine() 
	{
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		return null;
	}

	@Override
	public Agent guard() 
	{
		// TODO �����������ꂽ���\�b�h�E�X�^�u
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
		 * �����myPatterns���w�K�p�ɕۑ�
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
			//finish()���ɐl�T�������Ă�����l�T���̏���
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
		// TODO �����������ꂽ���\�b�h�E�X�^�u
	}
	
	// RPP���\�ȉ\�������邩�B
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
	
	//�@�������������ǂ����̃`�F�b�N
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
