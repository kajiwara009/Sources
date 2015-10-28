package org.aiwolf.Satsuki.reinforcementLearning;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Role;

public class COParam 
{
	// �t���O
	public static int FLAG_ATTACKED_DEAD = 1 << 0;		// �P���ɂ�鎀�S
	public static int FLAG_EXECUTED_DEAD = 1 << 1;		// ���Y�ɂ�鎀�S
	
	// �錾�������ɂ�
	public int turn;
	
	// ���̐E���錾��������
	public int order;
	
	// ���̐E��Ƃ��ĈӖ��̂��锭����錾�������� -1�͈Ӗ��̂��锭�������Ă��Ȃ����
	public int meaningOrder;
	
	// ����param�̓o�^��
	public int count;
	
	// ���̓��ɏ��Y���ꂽ��
	public int executedCount;
	
	// ���̓��ɏP�����ꂽ��
	public int attackedCount;
	
	// �������l�T�̂Ƃ��ɍU��������
	public int myAttackCount;
	
	// �������l�T�̂Ƃ��ɍU���������A�K�[�h���ꂽ��
	public int myAttackFailedCount;
	
	// (�肢�t��p)�����������Ă���l�T�������Ɛ錾�����񐔁B
	public int callAliveWolfDivinedCount;

	// callAliveWolfDivinedCount �̓��A���ۂɂ��̓��ɏ��Y���ꂽ��
	public int executedCallAliveWolfDivinedCount;
	
	// �t���O
	public int flag;
	
	// CO��������
	public Role role;
	
	public Agent agent;
	public Map<Role, Integer> realRoleCount = new HashMap<Role, Integer>();
	public List<COParam> nextParams;

	public void AddCOToTail(COParam param, int _order)
	{		
		if (nextParams.size() == 0)
		{
			nextParams.add(param);
		}
		else
		{
			if (nextParams.get(0).role == param.role) _order++;
			
			nextParams.get(0).AddCOToTail(param,_order);
		}
	}
	
	public void SetMeaningOrder(Agent _agent, int _order)
	{
		if (agent == _agent)
		{
			meaningOrder = _order;
		}
		
		if (nextParams.size() == 0)
		{
			return;
		}
		else
		{
			if (nextParams.get(0).meaningOrder >= _order) _order = nextParams.get(0).meaningOrder + 1;
			
			nextParams.get(0).SetMeaningOrder(_agent, _order);
		}		
	}
	
	public boolean CheckEq(COParam param)
	{
		if (param.role != role) return false;
		if (param.turn != turn) return false;
		if (param.order != order) return false;
		if (param.meaningOrder != meaningOrder) return false;
		if (param.flag != flag) return false;
		
		return true;
	}
	
	public COParam(int _turn, Role _role, int _flag)
	{
		turn = _turn;
		order = 0;
		meaningOrder = -1;
		count = 1;
		executedCount = 0;
		attackedCount = 0;
		myAttackCount = 0;
		callAliveWolfDivinedCount = 0;
		executedCallAliveWolfDivinedCount = 0;
		myAttackFailedCount = 0;
		flag          = _flag;
		role = _role;
		nextParams = new ArrayList<COParam>();
		agent = null;
	}

	public void Marge(COParam gameCOParam) 
	{
		// �J�E���^�𑝂₷
		++count;
		executedCount += gameCOParam.executedCount;
		attackedCount += gameCOParam.attackedCount;
		myAttackCount += gameCOParam.myAttackCount;
		myAttackFailedCount += gameCOParam.myAttackFailedCount;
		callAliveWolfDivinedCount += gameCOParam.callAliveWolfDivinedCount;
		executedCallAliveWolfDivinedCount += gameCOParam.executedCallAliveWolfDivinedCount;
		
		// ���݂̃p�����[�^�Ƀ}�[�W
	    for (Role key : gameCOParam.realRoleCount.keySet()) 
	    {
	    	if (realRoleCount.get(key) == null)
	    	{
	    		realRoleCount.put(key, 0);
	    	}
	    	realRoleCount.put(key, realRoleCount.get(key) + 1);
	    }
	    
	    // ���ׂẴp�����[�^�̃}�[�W�I��
	    if (gameCOParam.nextParams.size() == 0) return;
	    
	    // ���̃p�����[�^�̃}�[�W
	    for (int i = 0; i < nextParams.size(); ++i)
	    {
	    	if (nextParams.get(i).CheckEq(gameCOParam.nextParams.get(0)) == true)
	    	{
	    		nextParams.get(i).Marge(gameCOParam.nextParams.get(0));
	    		return;
	    	}
	    }
	    
	    // �}�[�W�悪������΍��
	    nextParams.add(gameCOParam.nextParams.get(0));
	}
}
