package org.aiwolf.Satsuki.reinforcementLearning;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Role;

public class COParam 
{
	// フラグ
	public static int FLAG_ATTACKED_DEAD = 1 << 0;		// 襲撃による死亡
	public static int FLAG_EXECUTED_DEAD = 1 << 1;		// 処刑による死亡
	
	// 宣言した日にち
	public int turn;
	
	// その職種を宣言した順番
	public int order;
	
	// その職種として意味のある発言を宣言した順番 -1は意味のある発言をしていない状態
	public int meaningOrder;
	
	// このparamの登録回数
	public int count;
	
	// この日に処刑された回数
	public int executedCount;
	
	// この日に襲撃された回数
	public int attackedCount;
	
	// 自分が人狼のときに攻撃した回数
	public int myAttackCount;
	
	// 自分が人狼のときに攻撃したが、ガードされた回数
	public int myAttackFailedCount;
	
	// (占い師専用)自分が生きている人狼を占ったと宣言した回数。
	public int callAliveWolfDivinedCount;

	// callAliveWolfDivinedCount の内、実際にその日に処刑された回数
	public int executedCallAliveWolfDivinedCount;
	
	// フラグ
	public int flag;
	
	// COした役割
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
		// カウンタを増やす
		++count;
		executedCount += gameCOParam.executedCount;
		attackedCount += gameCOParam.attackedCount;
		myAttackCount += gameCOParam.myAttackCount;
		myAttackFailedCount += gameCOParam.myAttackFailedCount;
		callAliveWolfDivinedCount += gameCOParam.callAliveWolfDivinedCount;
		executedCallAliveWolfDivinedCount += gameCOParam.executedCallAliveWolfDivinedCount;
		
		// 現在のパラメータにマージ
	    for (Role key : gameCOParam.realRoleCount.keySet()) 
	    {
	    	if (realRoleCount.get(key) == null)
	    	{
	    		realRoleCount.put(key, 0);
	    	}
	    	realRoleCount.put(key, realRoleCount.get(key) + 1);
	    }
	    
	    // すべてのパラメータのマージ終了
	    if (gameCOParam.nextParams.size() == 0) return;
	    
	    // 次のパラメータのマージ
	    for (int i = 0; i < nextParams.size(); ++i)
	    {
	    	if (nextParams.get(i).CheckEq(gameCOParam.nextParams.get(0)) == true)
	    	{
	    		nextParams.get(i).Marge(gameCOParam.nextParams.get(0));
	    		return;
	    	}
	    }
	    
	    // マージ先が無ければ作る
	    nextParams.add(gameCOParam.nextParams.get(0));
	}
}
