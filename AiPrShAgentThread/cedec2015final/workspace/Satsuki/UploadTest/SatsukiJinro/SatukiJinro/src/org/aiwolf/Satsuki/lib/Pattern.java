package org.aiwolf.Satsuki.lib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Judge;
import org.aiwolf.common.data.Role;
import org.aiwolf.Satsuki.lib.EnemyCase;
import org.aiwolf.Satsuki.lib.Pattern;
import org.aiwolf.Satsuki.reinforcementLearning.AgentPattern;

/**
 * ��E��CO�󋵂̃p�^�[��
 * @author kengo
 *
 */
public class Pattern 
{
	/*
	 * �O��ƂȂ���(�v���C���[�Ɩ�E�̃Z�b�g)
	 * �m��G�̏��(�v���C���[�Ƌ��l���ǂ����D���m�Ɣ��m(�����S�����m))
	 * �O�񂩂猈�肷����(�ǂ̑O��ł��m�肷����͂ǂ����邩)
	 * �ޓx
	 * �@�����ڂɊe��E������ł�m��(CO�^�C�~���O�ȍ~�͈Ӑ}�I�ɎE���邩�甽�f����)
	 * �@�����ڂɐ肢�C��\�ŉ��l�l�T��������m��
	 */

	//�O��Ƃ���肢�t�Ɨ�\�҂̃G�[�W�F���g
	private Agent seerAgent = null;
	private Agent mediumAgent = null;
	private Agent bodyGuardAgent = null;

	//�G�T�C�h�m��ƂȂ�G�[�W�F���g
	private Map<Agent, EnemyCase> enemyMap = new HashMap<Agent, EnemyCase>();

	/**
	 * TODO
	 * �p�^�[�����Ƃ��ɋU������
	 */
	private Set<Agent> fakeSeers = new HashSet<Agent>();
	private Set<Agent> fakeMediums = new HashSet<Agent>();
	
	private List<Agent> aliveAgents;
	//���m�G�[�W�F���g�D(�^�\�͎҂��甒���� or �P����)
	private Set<Agent> whiteAgentSet = new HashSet<Agent>();

	//�ޓx
	private double likelifood = 0.0;

	//�O���ɏ��Y�C�P�����ꂽ�v���C���[
	private Agent executedAgent;
	private Agent attackedAgent;


	/**
	 *
	 * @param seerAgent
	 * @param mediumAgent
	 * @param comingoutMap
	 */
	public Pattern(Agent seerAgent, Agent mediumAgent, Agent bodyGuardAgent, Map<Agent, Role> comingoutMap, List<Agent> aliveAgents)
	{
		this.seerAgent = seerAgent;
		this.mediumAgent = mediumAgent;
		this.bodyGuardAgent = bodyGuardAgent;
		for(Entry<Agent, Role> entry: comingoutMap.entrySet())
		{
			if(entry.getValue() != Role.SEER && entry.getValue() != Role.MEDIUM)
			{
				continue;
			}
			if(!entry.getKey().equals(seerAgent) && !entry.getKey().equals(mediumAgent))
			{
				enemyMap.put(entry.getKey(), EnemyCase.gray);
			}
		}
		this.aliveAgents = aliveAgents;
	}

	public Pattern()
	{
		return;
	}

	/**
	 * �V�����肢�C��\���ʂ�p���ăp�^�[�����X�V����D�����������Ȃ��ꍇ��false��Ԃ�
	 * @param judge
	 */
	public boolean updatePattern(Judge judge)
	{
		Agent judgment = judge.getAgent();
		if(judgment == seerAgent || judgment == mediumAgent)
		{
			switch (judge.getResult()) 
			{
			case HUMAN:
				Agent target = judge.getTarget();
				whiteAgentSet.add(target);
				/**
				 * �G�w�c�̃v���C���[�Ȃ狶�l�m��D���̓G��l�T�Ɗm��D
				 */
				if(enemyMap.containsKey(target)){
					Map<Agent, EnemyCase> enemyMapNew = new HashMap<Agent, EnemyCase>();
					for(Entry<Agent, EnemyCase> entry: enemyMap.entrySet()){
						if(entry.getKey().equals(target)){
							enemyMapNew.put(entry.getKey(), EnemyCase.white);
						}else{
							enemyMapNew.put(entry.getKey(), EnemyCase.black);
						}
					}
					enemyMap = enemyMapNew;
				}
				break;
			case WEREWOLF:
				enemyMap.put(judge.getTarget(), EnemyCase.black);
				break;
			}
		}

		if(!isPatternMatched()){
			return false;
		}
		/*
		 * �ޓx���X�V����A���S���Y�����K�v��
		 */

		return true;
	}

	/**
	 * roleMap�Ɛ�������p�^�[���̏ꍇ��true��Ԃ�
	 * @param roleMap
	 * @return
	 */
	public boolean isPatternMatched(Map<Agent, Role> roleMap)
	{
		if(seerAgent != null && roleMap.get(seerAgent) != Role.SEER)
		{
			return false;
		}
		else if(mediumAgent != null && roleMap.get(mediumAgent) != Role.MEDIUM)
		{
			return false;
		}
		else if(bodyGuardAgent != null && roleMap.get(bodyGuardAgent) != Role.BODYGUARD)
		{
			return false;
		}
		else
		{
			for(Entry<Agent, EnemyCase> set: enemyMap.entrySet())
			{
				if(roleMap.get(set.getKey()) != Role.WEREWOLF && roleMap.get(set.getKey()) != Role.POSSESSED)
				{
					return false;
				}
			}
		}
		return true;
	}
	
	public boolean isPatternMatched()
	{
		int enmeyNumber = MyGameInfo.getMaxEnemyNum();

		/**
		 * �G�̐����ߑ��Ȃ�R�D�l�T�̐����Q�[���ݒ�̐l�T���𒴂��Ă��R(���l��1�l�ݒ�)�D
		 */
		if(enemyMap.size() > enmeyNumber){
			return false;
		}else if(enemyMap.size() == enmeyNumber){
			int blackNumber = 0;
			for(Entry<Agent, EnemyCase> entry: enemyMap.entrySet()){
				if(entry.getValue() == EnemyCase.black){
					blackNumber++;
				}
			}
			if(blackNumber > enmeyNumber - 1){
				return false;
			}
		}

		/**
		 * �G�̐����ߑ��Ȃ�R�D�l�T�̐����Q�[���ݒ�̐l�T���𒴂��Ă��R(���l��1�l�ݒ�)�D
		 */
		for(Entry<Agent, EnemyCase> entry: enemyMap.entrySet()){

		}
		return true;
	}
	
	public AgentPattern getAgentPattern(Agent agent)
	{
		if(agent == null)
		{
			return AgentPattern.NULL;
		}
		else if(agent.equals(seerAgent))
		{
			return AgentPattern.SEER;
		}
		else if(agent.equals(mediumAgent))
		{
			return AgentPattern.MEDIUM;
		}
		else if(agent.equals(bodyGuardAgent))
		{
			return AgentPattern.BODYGUARD;
		}
		else if(fakeSeers.contains(agent))
		{
			if(enemyMap.containsKey(agent))
			{
				switch (enemyMap.get(agent)) 
				{
				case black:
					return AgentPattern.FAKE_SEER_BLACK;
				case gray:
					return AgentPattern.FAKE_SEER_GRAY;
				case white:
					return AgentPattern.FAKE_SEER_WHITE;
				}
			}
			else
			{
				return AgentPattern.FAKE_SEER_GRAY;
			}
		}
		else if(fakeMediums.contains(agent))
		{
			if(enemyMap.containsKey(agent))
			{
				switch (enemyMap.get(agent)) 
				{
				case black:
					return AgentPattern.FAKE_MEDIUM_BLACK;
				case gray:
					return AgentPattern.FAKE_MEDIUM_GRAY;
				case white:
					return AgentPattern.FAKE_MEDIUM_WHITE;
				}
			}
			else
			{
				return AgentPattern.FAKE_MEDIUM_GRAY;
			}
		}
		else if(enemyMap.containsKey(agent))
		{
			return AgentPattern.JUDGED_BLACK;
		}
		else if(whiteAgentSet.contains(agent))
		{
			return AgentPattern.WHITE_AGENT;
		}
		else if(agent.equals(executedAgent))
		{
			return AgentPattern.EXECUTED_AGENT;
		}
		else if(agent.equals(attackedAgent))
		{
			return AgentPattern.ATTACKED_AGENT;
		}
		return AgentPattern.NULL;
	}


	public Agent getSeerAgent() {
		return seerAgent;
	}

	public void setSeerAgent(Agent seerAgent) {
		this.seerAgent = seerAgent;
	}

	public Agent getMediumAgent() {
		return mediumAgent;
	}

	public void setMediumAgent(Agent mediumAgent) {
		this.mediumAgent = mediumAgent;
	}
	
	public Agent getBodyGuardAgent() {
		return bodyGuardAgent;
	}
	
	public void setBodyGuardAgent(Agent _bodyGuardAgent) {
		this.bodyGuardAgent = _bodyGuardAgent;
	}
	
	public Map<Agent, EnemyCase> getEnemyMap() {
		return enemyMap;
	}

	public void setEnemyMap(Map<Agent, EnemyCase> enemyMap) {
		this.enemyMap = enemyMap;
	}

	public Set<Agent> getWhiteAgentSet() {
		return whiteAgentSet;
	}

	public void setWhiteAgentSet(Set<Agent> whiteAgentSet) {
		this.whiteAgentSet = whiteAgentSet;
	}
/*
	public List<Agent> getWhiteAgentList() {
		return whiteAgentList;
	}

	public void setWhiteAgentList(List<Agent> whiteAgentList) {
		this.whiteAgentList = whiteAgentList;
	}*/

	public double getLikelifood() {
		return likelifood;
	}

	public void setLikelifood(double likelifood) {
		this.likelifood = likelifood;
	}

	@Override
	public Pattern clone(){
		Pattern clonePattern = new Pattern();
		clonePattern.setSeerAgent(seerAgent);
		clonePattern.setMediumAgent(mediumAgent);
		clonePattern.setBodyGuardAgent(bodyGuardAgent);
		clonePattern.setEnemyMap(new HashMap<Agent, EnemyCase>(enemyMap));
		clonePattern.setFakeSeers(fakeSeers);
		clonePattern.setFakeMediums(fakeMediums);
		clonePattern.setAliveAgents(new ArrayList<Agent>(aliveAgents));
		clonePattern.setWhiteAgentSet(new HashSet<Agent>(whiteAgentSet));
		clonePattern.setLikelifood(likelifood);
		clonePattern.setExecutedAgent(executedAgent);
		clonePattern.setAttackedAgent(attackedAgent);
		return clonePattern;
	}

	public Set<Agent> getFakeSeers() {
		return fakeSeers;
	}

	public void setFakeSeers(Set<Agent> fakeSeers) {
		this.fakeSeers = fakeSeers;
	}

	public Set<Agent> getFakeMediums() {
		return fakeMediums;
	}

	public void setFakeMediums(Set<Agent> fakeMediums) {
		this.fakeMediums = fakeMediums;
	}

	public List<Agent> getAliveAgents() {
		return aliveAgents;
	}

	public void setAliveAgents(List<Agent> aliveAgents) {
		this.aliveAgents = aliveAgents;
	}

	public Agent getExecutedAgent() 
	{
		return executedAgent;
	}

	public void setExecutedAgent(Agent executedAgent) {
		this.executedAgent = executedAgent;
	}

	public Agent getAttackedAgent() {
		return attackedAgent;
	}

	public void setAttackedAgent(Agent attackedAgent) {
		this.attackedAgent = attackedAgent;
	}


}
