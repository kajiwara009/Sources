package org.aiwolf.Satsuki.LearningPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.aiwolf.Satsuki.lib.MyGameInfo;
import org.aiwolf.Satsuki.lib.Pattern;
import org.aiwolf.Satsuki.lib.PatternMaker;
import org.aiwolf.Satsuki.reinforcementLearning.AgentPattern;
import org.aiwolf.Satsuki.reinforcementLearning.Qvalues;
import org.aiwolf.Satsuki.reinforcementLearning.ReinforcementLearning;
import org.aiwolf.client.lib.TemplateTalkFactory;
import org.aiwolf.client.lib.Utterance;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Judge;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Species;
import org.aiwolf.common.data.Talk;
import org.aiwolf.common.data.Vote;
import org.aiwolf.common.net.GameInfo;
import org.aiwolf.common.net.GameSetting;

public abstract class AbstractKajiWolfSideAgent extends AbstractGiftedPlayer {


	//�x���E
	Role fakeRole = null;
	
	// �����𖞂�������J�~���O�A�E�g���邩�ǂ���
	boolean doComingout = false;
	
	// �D��I�ȃJ�~���O�A�E�g�t���O
	boolean priorityComingout = false;

	// ���肩��ǂ�����邩
	List<Pattern> otherPatterns = new ArrayList<Pattern>();

	List<Judge> fakeJudges = new ArrayList<Judge>();
	
	// �{�ECO�t���O
	boolean bRealCo			= false;
	
	// �{�ECO�I���t���O
	boolean bRealCoFinish	= false;

	@Override
	public void initialize(GameInfo gameInfo, GameSetting gameSetting) 
	{
		super.initialize(gameInfo, gameSetting);
		
		//fakeRole�������_���őI��
		initializeFakeRole();
		
		otherPatterns.add(new Pattern(null, null, null, new HashMap<Agent, Role>(), gameInfo.getAliveAgentList()));
		PatternMaker.settleAgentRole(myPatterns, getMe(), getMyRole());		
	}
	
	protected abstract void initializeFakeRole();

	@Override
	public void dayStart() 
	{
		super.dayStart();

		PatternMaker.updateAttackedData(otherPatterns, getLatestDayGameInfo().getAttackedAgent());
		PatternMaker.updateExecutedData(otherPatterns, getLatestDayGameInfo().getExecutedAgent());
	}
	
	@Override
	public void update(GameInfo gameInfo)
	{
		super.update(gameInfo);
	}


	/**
	 * 2���ڈȍ~��dayStart�ŌĂ΂��
	 * �U�肢���ʂ����
	 */
	abstract protected void setFakeDivineJudge();

//	abstract protected void fakeRoleChanger();
	
	protected void setTemplateFakeDivineJudge() 
	{
		// �肢�t�Ɠ����v�l���s���A�肢�Ώۂ����肷��B
		List<Pattern> basePatterns = new ArrayList<Pattern>(otherPatterns);
		PatternMaker.settleAgentRole(basePatterns, getMe(), Role.SEER);
		
		Agent target = getDivineAgent(basePatterns);
		
		if (target == null)
		{
			return;
		}

		// �����̉\�`�F�b�N
		int aliveSize = getLatestDayGameInfo().getAliveAgentList().size();

		List<Agent> wolfDetermineAgent = new ArrayList<Agent>(getLatestDayGameInfo().getAgentList());
		List<Agent> possessedDetermineAgent = new ArrayList<Agent>();
		List<Agent> enemyDetermineAgent = new ArrayList<Agent>(getLatestDayGameInfo().getAgentList());
		PatternMaker.getDetermineEnemyAgent(basePatterns, wolfDetermineAgent, possessedDetermineAgent);
		PatternMaker.getDetermineEnemyAgent(basePatterns, enemyDetermineAgent);
		
		wolfDetermineAgent.removeAll(getLatestDayGameInfo().getAliveAgentList());
		int deadWolf = wolfDetermineAgent.size();
		
		// �]��CO�̎擾
		// int getSurplusCO = advanceGameInfo.getSurplusCO();
		boolean canBlack = true, canWhite = true;

		// ���`�F�b�N
		{
			List<Pattern> patterns = new ArrayList<Pattern>(basePatterns);
			PatternMaker.RemoveEnemyPattern(patterns, target, false);
			
			// ����������
			if (patterns.size() == 0) canBlack = false;
			
			// 10�l���炢�܂ł͂���ĂȂ�
			if (aliveSize >= 10)  canBlack = false;
			
			// 6�l�ȏ�̂Ƃ��A�������̌�����s���ƁE�E�E�Q�[���N���A�ɂȂ�
			if (aliveSize >= 6 && deadWolf == 2)  canBlack = false;
		}
		
		// ���`�F�b�N
		{
			List<Pattern> patterns = new ArrayList<Pattern>(basePatterns);
			PatternMaker.RemoveEnemyPattern(patterns, target, true);
			
			// ����������
			if (patterns.size() == 0) canWhite = false;
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

		Judge fakeJudge = new Judge(getDay()-1, getMe(), target, species);

		notToldjudges.add(fakeJudge);
		fakeJudges.add(fakeJudge);

	}
	/**
	 * ���Y���ꂽ�v���C���[���������ɌĂ΂��
	 * �U��\���ʂ����
	 * @param executedAgent
	 */
	abstract protected void setFakeInquestJudge(Agent executedAgent);

	protected void setTemplateFakeInquestJudge() 
	{
		List<Pattern> basePatterns = new ArrayList<Pattern>(otherPatterns);
		PatternMaker.settleAgentRole(basePatterns, getMe(), Role.MEDIUM);
		
		Agent target = getLatestDayGameInfo().getExecutedAgent();
		
		if (target == null)
		{
			return;
		}

		// �����̉\�`�F�b�N
		int aliveSize = getLatestDayGameInfo().getAliveAgentList().size();

		List<Agent> wolfDetermineAgent = new ArrayList<Agent>(getLatestDayGameInfo().getAgentList());
		List<Agent> possessedDetermineAgent = new ArrayList<Agent>();
		List<Agent> enemyDetermineAgent = new ArrayList<Agent>(getLatestDayGameInfo().getAgentList());
		PatternMaker.getDetermineEnemyAgent(basePatterns, wolfDetermineAgent, possessedDetermineAgent);
		PatternMaker.getDetermineEnemyAgent(basePatterns, enemyDetermineAgent);
		
		wolfDetermineAgent.removeAll(getLatestDayGameInfo().getAliveAgentList());
		int deadWolf = wolfDetermineAgent.size();
		
		// �]��CO�̎擾
		boolean canBlack = true, canWhite = true;

		// ���`�F�b�N
		{
			List<Pattern> patterns = new ArrayList<Pattern>(basePatterns);
			PatternMaker.RemoveEnemyPattern(patterns, target, false);
			
			// ����������
			if (patterns.size() == 0) canBlack = false;
			
			// 10�l���炢�܂ł͂���ĂȂ�
			if (aliveSize >= 10)  canBlack = false;
			
			// 6�l�ȏ�̂Ƃ��A�������̌�����s���ƁE�E�E�Q�[���N���A�ɂȂ�
			if (aliveSize >= 6 && deadWolf == 2)  canBlack = false;
		}
		
		// ���`�F�b�N
		{
			List<Pattern> patterns = new ArrayList<Pattern>(basePatterns);
			PatternMaker.RemoveEnemyPattern(patterns, target, true);
			
			// ����������
			if (patterns.size() == 0) canWhite = false;
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

		Judge fakeJudge = new Judge(getDay()-1, getMe(), target, species);

		notToldjudges.add(fakeJudge);
		fakeJudges.add(fakeJudge);
	}



	@Override
	public void comingoutTalkDealing(Talk talk, Utterance utterance)
	{
		super.comingoutTalkDealing(talk, utterance);
		
		// �l�T�A���lCO�͂܂Ƃ��Ȕ�������Ȃ�
		if (utterance.getRole() == Role.WEREWOLF || utterance.getRole() == Role.POSSESSED) return;
		
		PatternMaker.extendPatternList(otherPatterns, talk.getAgent(), utterance.getRole(), advanceGameInfo);
	}

	@Override
	public void divinedTalkDealing(Talk talk, Utterance utterance)
	{
		super.divinedTalkDealing(talk, utterance);
		Judge inspectJudge = new Judge(getDay(), talk.getAgent(), utterance.getTarget(), utterance.getResult());
		PatternMaker.updateJudgeData(otherPatterns, inspectJudge);
	}

	@Override
	public void inquestedTalkDealing(Talk talk, Utterance utterance)
	{
		super.inquestedTalkDealing(talk, utterance);
		Judge tellingJudge = new Judge(getDay(), talk.getAgent(), utterance.getTarget(), utterance.getResult());
		PatternMaker.updateJudgeData(otherPatterns, tellingJudge);
	}

	@Override
	public String getJudgeText() 
	{
		if(isComingout && notToldjudges.size() != 0)
		{
			if (fakeRole == Role.SEER)
			{
				String talk = TemplateTalkFactory.divined(notToldjudges.get(0).getTarget(), notToldjudges.get(0).getResult());
				toldjudges.add(notToldjudges.get(0));
				notToldjudges.remove(0);
				return talk;				
			}
			else if (fakeRole == Role.MEDIUM)
			{
				String talk = TemplateTalkFactory.inquested(notToldjudges.get(0).getTarget(), notToldjudges.get(0).getResult());
				toldjudges.add(notToldjudges.get(0));
				notToldjudges.remove(0);
				return talk;					
			}
		}
		return null;
	}
	



	@Override
	public String getComingoutText() 
	{
		// �{CO�����I��
		if (bRealCoFinish)
		{
			return null;
		}
		
		// �{CO���s��
		if (bRealCo)
		{
			bRealCoFinish = true;
			return TemplateTalkFactory.comingout(getMe(), getMyRole());
		}
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
			if (priorityComingout)
			{
				return comingoutFakeRole();				
			}
			// �����ɂ��J�~���O�A�E�g
			if(coTiming.getDay() == getDay())
			{
				return comingoutFakeRole();
			}

			// �UCO�o��
			// �{���̉\���������B�s���������B
			/*
			if(coTiming.isAgainst())
			{
				Map<Agent, Role> comingoutMap = advanceGameInfo.getComingoutMap();
				for(Entry<Agent, Role> set: comingoutMap.entrySet())
				{
					if(set.getValue() == fakeRole && !set.getKey().equals(getMe()))
					{
						return comingoutFakeRole();
					}
				}
			}
			*/

			// �l�T������
			/*
			 * �����̔����Ȃ̂Ŕ���ɓ���Ȃ���������
			if(coTiming.isHasFoundWolf())
			{
				for(Judge judge: notToldjudges)
				{
					if(judge.getResult() == Species.WEREWOLF)
					{
						return comingoutFakeRole();
					}
				}
			}
			*/

			// ���[��ɑI�΂ꂻ��
			if(/*coTiming.isVoted()*/true)
			{
				List<Vote> votes = advanceGameInfo.getVoteList(getDay());

				int voteToMe = 0;
				for(Vote vote: votes)
				{
					if(vote.getTarget().equals(getMe()))
					{
						voteToMe++;
					}
				}
				if((double)votes.size() * 1.5 > getLatestDayGameInfo().getAliveAgentList().size() && (double)voteToMe * 4 > votes.size())
				{
					return comingoutFakeRole();
				}
				else if (voteToMe >= 3)
				{
					return comingoutFakeRole();					
				}
			}
			
			// �l�T���Ɛ��ꂽ
			if(/*coTiming.isWolfJudged()*/ true)
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
	

	/**
	 * fakeRole���J�~���O�A�E�g����
	 * @return
	 */
	public String comingoutFakeRole()
	{
		if (fakeRole == Role.VILLAGER) return null;
		isComingout = true;
		
		// �b������e������
		if (notToldjudges.size() == 0)
		{
			return TemplateTalkFactory.comingout(getMe(), fakeRole);
		}
				
		// �J�~���O�A�E�g�͏ȗ����Ē���divine,insq���s��
		return getJudgeText();
	}

	/**
	 * ���܂łɏo����������̐���Ԃ�
	 * @return
	 */
	protected int getBlackJudgeNum()
	{
		int blackJudgeNum = 0;
		for(Judge judge: toldjudges)
		{
			if(judge.getResult() == Species.WEREWOLF)
			{
				blackJudgeNum++;
			}
		}
		for(Judge judge: notToldjudges)
		{
			if(judge.getResult() == Species.WEREWOLF)
			{
				blackJudgeNum++;
			}
		}
		return blackJudgeNum;
	}

	Judge getFakeJudge(int day)
	{
		for(Judge j: fakeJudges)
		{
			if(j.getDay() == day)
			{
				return j;
			}
		}
		return null;
	}

	abstract void updateCOElements(boolean isVillagerWin);
	/*{
		
		if(fakeRole == Role.SEER || fakeRole == Role.MEDIUM){
			Map<Integer, Double> map = getCOMap();
			
			Map<Integer, Double> map = null;
			switch (fakeRole) {
			case SEER:
				if(getMyRole() == Role.WEREWOLF){
					map = ld.getWolfFakeSeerCO();
				}else if(getMyRole() == Role.POSSESSED){
					map = ld.getPossessedFakeSeerCO();
				}
				break;
			case MEDIUM:
				if(getMyRole() == Role.WEREWOLF){
					map = ld.getWolfFakeMediumCO();
				}else if(getMyRole() == Role.POSSESSED){
					map = ld.getPossessedFakeMediumCO();
				}
				break;
			}
			
			double q = map.get(coTiming.toHash());
			double reward = (isVillagerWin)? 100.0: 0;
			double learnedQ = ReinforcementLearning.reInforcementLearn(q, reward, 0);
			map.put(coTiming.toHash(), learnedQ);
		}

		Map<Integer, Double> map = ld.getWolfFakeMediumCO();
		double q = map.get(coTiming.toHash());
		double reward = (isVillagerWin)? 100.0: 0;
		double learnedQ = ReinforcementLearning.reInforcementLearn(q, reward, 0);
	}
*/

	@Override
	public boolean isVillager()
	{
		return false;
	}

	public Role getFakeRole() {
		return fakeRole;
	}

	public void setFakeRole(Role fakeRole) {
		this.fakeRole = fakeRole;
	}

	/*
	void updateMiddleFakeInquest(int day, Pattern patternPresent,
			Pattern patternNext, Scene scenePresent, Scene sceneNext) {

		if(day < 2){
			return;
		}
		Qvalues qVal = ld.getQvalue(scenePresent.getHashNum()),
				qValNext = ld.getQvalue(sceneNext.getHashNum());

		Judge fakeJudge = getFakeJudge(day-1);
		AgentPattern ap = patternPresent.getAgentPattern(fakeJudge.getTarget());
		Species judgeResult = fakeJudge.getResult();

		Map<AgentPattern, Map<Species, Double>> map = null;
		Map<AgentPattern, Double> mapNext = null;
		if(getMyRole() == Role.WEREWOLF){
			map = qVal.getWolfInquest();
			mapNext = qValNext.getWolfAttack();
		}else if(getMyRole() == Role.POSSESSED){
			map = qVal.getPossessedInquest();
			mapNext = qValNext.getPossessedVote();
		}

		double nextMaxQVal = Qvalues.getMaxQValue(mapNext);
		double learnedQ = ReinforcementLearning.reInforcementLearn(map.get(ap).get(judgeResult), 0.0, nextMaxQVal);
		map.get(ap).put(judgeResult, learnedQ);
	}


	void updateMiddleFakeDivine(int day, Pattern patternPresent,
			Pattern patternNext, Scene scenePresent, Scene sceneNext) {
		if(day < 2){
			return;
		}
		Qvalues qVal = ld.getQvalue(scenePresent.getHashNum()),
				qValNext = ld.getQvalue(sceneNext.getHashNum());

		Judge fakeJudge = getFakeJudge(day-1);
		AgentPattern ap = patternPresent.getAgentPattern(fakeJudge.getTarget());
		Species judgeResult = fakeJudge.getResult();

		Map<AgentPattern, Map<Species, Double>> map = null;
		Map<AgentPattern, Double> mapNext = null;
		if(getMyRole() == Role.WEREWOLF){
			map = qVal.getWolfDivine();
			mapNext = qValNext.getWolfAttack();
		}else if(getMyRole() == Role.POSSESSED){
			map = qVal.getPossessedDivine();
			mapNext = qValNext.getPossessedVote();
		}

		double nextMaxQVal = Qvalues.getMaxQValue(mapNext);
		double learnedQ = ReinforcementLearning.reInforcementLearn(map.get(ap).get(judgeResult), 0.0, nextMaxQVal);
		map.get(ap).put(judgeResult, learnedQ);
	}

*/


}
