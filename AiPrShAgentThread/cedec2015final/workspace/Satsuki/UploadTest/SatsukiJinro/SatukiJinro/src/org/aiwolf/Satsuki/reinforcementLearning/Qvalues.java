package org.aiwolf.Satsuki.reinforcementLearning;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.aiwolf.common.data.Species;

public class Qvalues implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1098064560753220480L;

	/**
	 * ���[
	 * ���[�Ɋւ��ẮC�I�񂾍s���ł͂Ȃ��C���ۂɏ��Y���ꂽ�G�[�W�F���g�̎�ނŊw�K���s��
	 * �l�T����fakePatterns�ő��l�������悤�ɑ����Ċw�K
	 * �肢
	 * ��q
	 * �i�e�V�[���ɑ΂��āC�e�G�[�W�F���g��Ώۂɂ����ۂɁC�ڍs�����V�[���ł̓��[�̃}�b�N�XQ�l���w�K�ɗp����j
	 * 
	 * �J�~���O�A�E�g�i�����ɐݒ肵�Ă��܂��j
	 * 
	 * 
	 * �P���iWOLFS�p�^�[���ōl����j
	 * �U��E�i�����ɐݒ�D�l�T�͊w�K���Ȃ��D�Ƃ肠�������������ۑ����Ă����j
	 * �U�肢�ΏہC���ʁi�Ώۂƌ��ʂ�΂ɂ���D�E���ꂽ�G�[�W�F���g���Ώۂɓ����D�����f�B�X�^�[�g�Łj
	 * �U��\���ʁi�����f�B�X�^�[�g�j
	 * �i���l�v���C���[�j
	 * 
	 * ���l�F���[
	 * �肢�t�F�J�~���O�A�E�g�C���[�C�肢
	 * ��\�F�J�~���O�A�E�g�C���[
	 * ��l�F��q�C���[

	 * ���l�F�U��E�C�U�肢�C�U��\�C�J�~���O�A�E�g�i�Q��j�C���[
	 * �l�T�F�U��E�C�U�肢�C�U��\�C�J�~���O�A�E�g�C���[�C�P��
	 * 
	 * 
	 */
	private Map<AgentPattern, Double> 
		villagerVote = getNewQvalueMap(),
		
		seerVote= getNewQvalueMap(),
		seerDivine= getNewQvalueMap(),
	
		mediumVote= getNewQvalueMap(),
	
		hunterGuard= getNewQvalueMap(),
		hunterVote= getNewQvalueMap(),
	
		possessedVote= getNewQvalueMap(),
	
		wolfAttack= getNewQvalueMap(),
		wolfVote= getNewQvalueMap();
	
	private Map<AgentPattern, Map<Species, Double>> 
		wolfDivine = getNewQvalueJudgeMap(),
		possessedDivine = getNewQvalueJudgeMap(),
		wolfInquest = getNewQvalueJudgeMap(),
		possessedInquest = getNewQvalueJudgeMap();
	
	private int Likelihood = 0;
	
	public class VillagerQvalue{
		
	}
	
	private Map<AgentPattern, Double> getNewQvalueMap()
	{
		Map<AgentPattern, Double> map = new HashMap<AgentPattern, Double>();
		for(AgentPattern ap: AgentPattern.values())
		{
			map.put(ap, 50.0);
		}
		return map;
	}
	
	private Map<AgentPattern, Map<Species, Double>> getNewQvalueJudgeMap()
	{
		Map<AgentPattern, Map<Species, Double>> map = new HashMap<AgentPattern, Map<Species,Double>>();
		for(AgentPattern ap: AgentPattern.values())
		{
			Map<Species, Double> inMap = new HashMap<Species, Double>();
			for(Species s: Species.values())
			{
				inMap.put(s, 50.0);
			}
			map.put(ap, inMap);
		}
		return map;
	}
	
	public static double getMaxQValue(Map<?, Double> map)
	{
		double ans = -Double.MAX_VALUE;
		for(Entry<?, Double> set: map.entrySet()){
			if(ans < set.getValue())
			{
				ans = set.getValue();
			}
		}
		return ans;
	}

	public Map<AgentPattern, Double> getVillagerVote() {
		return villagerVote;
	}

	public void setVillagerVote(Map<AgentPattern, Double> villagerVote) {
		this.villagerVote = villagerVote;
	}

	public Map<AgentPattern, Double> getSeerVote() {
		return seerVote;
	}

	public void setSeerVote(Map<AgentPattern, Double> seerVote) {
		this.seerVote = seerVote;
	}

	public Map<AgentPattern, Double> getSeerDivine() {
		return seerDivine;
	}

	public void setSeerDivine(Map<AgentPattern, Double> seerDivine) {
		this.seerDivine = seerDivine;
	}

	public Map<AgentPattern, Double> getMediumVote() {
		return mediumVote;
	}

	public void setMediumVote(Map<AgentPattern, Double> mediumVote) {
		this.mediumVote = mediumVote;
	}

	public Map<AgentPattern, Double> getHunterGuard() {
		return hunterGuard;
	}

	public void setHunterGuard(Map<AgentPattern, Double> hunterGuard) {
		this.hunterGuard = hunterGuard;
	}

	public Map<AgentPattern, Double> getHunterVote() {
		return hunterVote;
	}

	public void setHunterVote(Map<AgentPattern, Double> hunterVote) {
		this.hunterVote = hunterVote;
	}


	public Map<AgentPattern, Double> getPossessedVote() {
		return possessedVote;
	}

	public void setPossessedVote(Map<AgentPattern, Double> possessedVote) {
		this.possessedVote = possessedVote;
	}



	public Map<AgentPattern, Map<Species, Double>> getWolfDivine() {
		return wolfDivine;
	}

	public void setWolfDivine(Map<AgentPattern, Map<Species, Double>> wolfDivine) {
		this.wolfDivine = wolfDivine;
	}

	public Map<AgentPattern, Map<Species, Double>> getPossessedDivine() {
		return possessedDivine;
	}

	public void setPossessedDivine(
			Map<AgentPattern, Map<Species, Double>> possessedDivine) {
		this.possessedDivine = possessedDivine;
	}

	

	public Map<AgentPattern, Map<Species, Double>> getWolfInquest() {
		return wolfInquest;
	}

	public void setWolfInquest(Map<AgentPattern, Map<Species, Double>> wolfInquest) {
		this.wolfInquest = wolfInquest;
	}

	public Map<AgentPattern, Map<Species, Double>> getPossessedInquest() {
		return possessedInquest;
	}

	public void setPossessedInquest(
			Map<AgentPattern, Map<Species, Double>> possessedInquest) {
		this.possessedInquest = possessedInquest;
	}

	public Map<AgentPattern, Double> getWolfAttack() {
		return wolfAttack;
	}

	public void setWolfAttack(Map<AgentPattern, Double> wolfAttack) {
		this.wolfAttack = wolfAttack;
	}

	public Map<AgentPattern, Double> getWolfVote() {
		return wolfVote;
	}

	public void setWolfVote(Map<AgentPattern, Double> wolfVote) {
		this.wolfVote = wolfVote;
	}

	public int getLikelihood() 
	{
		return Likelihood;
	}

	public void setLikelihood(int likelihood) 
	{
		Likelihood = likelihood;
	}
	
	public void addLikelihood()
	{
		Likelihood += 1;
	}
}
