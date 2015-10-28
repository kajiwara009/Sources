package org.aiwolf.Satsuki.lib;

import java.util.*;
import java.util.Map.Entry;

import org.aiwolf.common.*;
import org.aiwolf.common.data.*;
import org.aiwolf.common.net.*;
import org.aiwolf.Satsuki.lib.DeadCondition;
/**
 * �T�[�o���瑗���Ă���GameInfo���ȒP�ɏW��
 * �肢�C��\���ʃ��X�g
 * CO�}�b�v
 * ���S���X�g
 * @author kengo
 *
 */
public class AdvanceGameInfo 
{

	//���[�ӎu�̃��X�g�D�����ƂɊǗ�
	@SuppressWarnings("serial")
	List<List<Vote>> voteLists = new ArrayList<List<Vote>>(){{
		add(new ArrayList<Vote>());
	}};

	/**
	 * ���b�œ`����ꂽ�肢���ʂ̃��X�g�D����̃v���g�R���ł͉����ڂɐ�����̂�������Ȃ��̂ŁC���b���ɐݒ�D
	 */
	private List<Judge> inspectJudges = new ArrayList<Judge>();

	/**
	 * ���b�œ`����ꂽ��\���ʂ̃��X�g�D����̃v���g�R���ł͉����ڂɗ�\�����̂�������Ȃ��̂ŁC���b���ɐݒ�D
	 */
	private List<Judge> mediumJudges = new ArrayList<Judge>();



	private Map<Agent, Role> comingoutMap = new HashMap<Agent, Role>();


	private List<DeadCondition> deadConditions = new ArrayList<DeadCondition>();


	public List<List<Vote>> getVoteLists() 
	{
		return voteLists;
	}
	
	public List<Vote> getVoteList(int day) 
	{
		if(day < voteLists.size())
		{
			return voteLists.get(day);
		}
		else
		{
			return new ArrayList<Vote>();
		}
		
	}

	public void setVoteLists(List<List<Vote>> voteLists) {
		this.voteLists = voteLists;
	}

	public void addVote(int day, Vote vote) {
		for(; voteLists.size() <= day;){
			voteLists.add(new ArrayList<Vote>());
		}
		List<Vote> theDayVoteList = voteLists.get(day);
		for(Vote v: theDayVoteList){
			if(v.getAgent().equals(vote.getAgent())){
				theDayVoteList.remove(v);
				break;
			}
		}
		theDayVoteList.add(vote);
	}
	
	// �]��CO���̎擾
	public int getSurplusCO()
	{
		Map<Role,Integer> roleCount = new HashMap<Role,Integer>();
		roleCount.put(Role.SEER, 0);
		roleCount.put(Role.MEDIUM, 0);
		roleCount.put(Role.BODYGUARD, 0);
		for (Entry<Agent, Role> set: getComingoutMap().entrySet())
		{
			if (set.getValue() == Role.SEER || set.getValue() == Role.MEDIUM || set.getValue() == Role.BODYGUARD)
			{
				roleCount.put(set.getValue(), roleCount.get(set.getValue()) + 1);
			}
		}
		

		int ret = 0;
		for(Entry<Role,Integer> set: roleCount.entrySet())
		{
			if (set.getValue() >= 1) ret += set.getValue() - 1;
		}
		return ret;
	}
	
	public int getVoteNum(int day, Agent target)
	{
		List<Vote> votes = getVoteList(day);

		int ret = 0;
		for(Vote vote: votes)
		{
			if(vote.getTarget().equals(target)){
				ret++;
			}
		}
		
		return ret;
	}

	public Map<Agent, Role> getComingoutMap() {
		return comingoutMap;
	}

	/**
	 * CO�����v���C���[��comingoutMap�ɉ�����D
	 * @param agent
	 * @param role
	 */
	public void putComingoutMap(Agent agent, Role role){
		comingoutMap.put(agent, role);
	}

	public List<Judge> getInspectJudges() {
		return inspectJudges;
	}

	public List<Judge> getInspectJudges(int day) 
	{
		List<Judge> ret = new ArrayList<Judge>();
		for(Judge judge: inspectJudges)
		{
			if (judge.getDay() != day) continue;
			
			ret.add(judge);
		}
		return ret;
	}		
	public void setInspectJudges(List<Judge> inspectJudgeList) {
		this.inspectJudges = inspectJudgeList;
	}

	public void addInspectJudges(Judge judge) {
		this.inspectJudges.add(judge);
	}

	public List<Judge> getMediumJudges() {
		return mediumJudges;
	}

	public void setMediumJudges(List<Judge> mediumJudgeList) {
		this.mediumJudges = mediumJudgeList;
	}

	public void addMediumJudges(Judge judge) {
		this.mediumJudges.add(judge);
	}

	public List<DeadCondition> getDeadConditions() {
		return deadConditions;
	}

	public void setDeadConditions(List<DeadCondition> deadConditions) {
		this.deadConditions = deadConditions;
	}

	public void addDeadConditions(DeadCondition deadCondition){
		this.deadConditions.add(deadCondition);
	}


}
