package org.aiwolf.Satsuki.LearningPlayer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;

import org.aiwolf.Satsuki.LearningPlayer.AbstractGiftedPlayer;
import org.aiwolf.Satsuki.lib.MyGameInfo;
import org.aiwolf.Satsuki.lib.Pattern;
import org.aiwolf.Satsuki.lib.PatternMaker;
import org.aiwolf.Satsuki.reinforcementLearning.AgentPattern;
import org.aiwolf.Satsuki.reinforcementLearning.COtiming;
import org.aiwolf.Satsuki.reinforcementLearning.COtimingNeo;
import org.aiwolf.Satsuki.reinforcementLearning.Qvalues;
import org.aiwolf.Satsuki.reinforcementLearning.ReinforcementLearning;
import org.aiwolf.client.lib.TemplateTalkFactory;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Judge;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Species;
import org.aiwolf.common.data.Vote;
import org.aiwolf.common.net.GameInfo;
import org.aiwolf.common.net.GameSetting;

// �񖇐�肢�t
// �������猩�Đl�T�E���l�m��͐��Ȃ��B
// �l�����Ă��܂����Ƃ��l�T�m�肪�����炻��ɍ����ւ���B
// �Ԉ���Ă��Ă��ӔC�͎��Ȃ��B(���l�̃G�[�W�F���g����ɂԂ�񂵂Ăǂ��Ȃ邩�͎����������E�E�E)
public class KajiSeerPlayer extends AbstractGiftedPlayer 
{	
	@Override
	public void initialize(GameInfo gameInfo, GameSetting gameSetting) 
	{
		//�@�J�~���O�A�E�g��������������_���ɐݒ�(0�Ȃ�����o�߂ł̓J�~���O�A�E�g���Ȃ�)
		super.initialize(gameInfo, gameSetting);
	}

	@Override
	public void dayStart() 
	{
		// �X�[�p�[�N���X�̎��s
		super.dayStart();
		
		// �l�T�A���l�m��G�[�W�F���g���擾�B
		List<Agent> wolfDetermineAgent = new ArrayList<Agent> (getLatestDayGameInfo().getAliveAgentList());
		List<Agent> possessedDetermineAgent = new ArrayList<Agent> (getLatestDayGameInfo().getAliveAgentList());
		
		// ���łɉ�b���ɓ����Ă���Ȃ��菜��
		wolfDetermineAgent.remove(notToldjudges);
		wolfDetermineAgent.remove(toldjudges);
		possessedDetermineAgent.remove(notToldjudges);
		possessedDetermineAgent.remove(toldjudges);
		
		// �擾�J�n
		PatternMaker.getDetermineEnemyAgent(myPatterns, wolfDetermineAgent, possessedDetermineAgent);
		
		Judge realRet = getLatestDayGameInfo().getDivineResult();
		if(getLatestDayGameInfo().getDivineResult() != null)
		{
			if (
				getLatestDayGameInfo().getDivineResult().getResult() == Species.HUMAN
				&& wolfDetermineAgent.size() != 0
			)
			{
				// Judge��s������B
				Judge judge = new Judge(realRet.getDay(), getMe(), wolfDetermineAgent.get(0), Species.WEREWOLF);
				notToldjudges.add(judge);
			}
			else
			{
				notToldjudges.add(getLatestDayGameInfo().getDivineResult());				
			}
		}
	}

	@Override
	public String getJudgeText() 
	{
		if(isComingout && notToldjudges.size() != 0)
		{
			String talk = TemplateTalkFactory.divined(notToldjudges.get(0).getTarget(), notToldjudges.get(0).getResult());
			toldjudges.add(notToldjudges.get(0));
			notToldjudges.remove(0);
			return talk;
		}
		return null;
	}

	@Override
	public String getComingoutText() {
		return getTemplateComingoutText();
	}

	@Override
	public void setVoteTarget() 
	{
		setVoteTargetTemplate(myPatterns);
	}

	@Override
	public Agent divine() 
	{
		Agent target = getDivineAgent(myPatterns);
		divinedAgents.add(target);
		return target;
	}

	@Override
	void updatePreConditionQVal(boolean isVillagerWin)
	{
		Map<COtimingNeo, Double> map = ld.getSeerCO();
		double q = map.get(coTiming);
		double reward = (isVillagerWin)? 100.0: 0;
		double learnedQ = ReinforcementLearning.reInforcementLearn(q, reward, 0);
		map.put(coTiming, learnedQ);
	}
}
