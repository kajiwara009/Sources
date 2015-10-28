package org.aiwolf.Satsuki.reinforcementLearning;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Role;


/*
 * @brief		各エージェントの行動ログを保存し、判定に使用します。
 */
public class ActionLog 
{
	Map<Integer,COParam> gameCOParam = new HashMap<Integer,COParam>();
	Map<Integer,List<COParam>> saveCOParam = new HashMap<Integer,List<COParam>>();
	
	private static Map<Integer, ActionLog> inst = new HashMap<Integer, ActionLog>();
	
	private static int DAY_MAX = 16;
	private static int ROLE_KEY_MAX = 2;
	
	public static int GetKey(int day, Role role)
	{
		switch(role)
		{
		case SEER: return GetKeyInt(day,0);
		case MEDIUM: return GetKeyInt(day,1);
		case BODYGUARD: return GetKeyInt(day,2);
		}
		
		// エラー
		return -1;
	}
	
	public static int GetKeyInt(int day, int role)
	{
		return (day << 2) + role;
	}

	ActionLog()
	{
		for (int i = 0; i < DAY_MAX; ++i)
		{
			saveCOParam.put(GetKey(i,Role.SEER), new ArrayList<COParam>());
			saveCOParam.put(GetKey(i,Role.MEDIUM), new ArrayList<COParam>());
			saveCOParam.put(GetKey(i,Role.BODYGUARD), new ArrayList<COParam>());			
		}
		
		
	}

	public void gameStart()
	{
		gameCOParam.clear();
		for (int i = 0; i < DAY_MAX; ++i)
		{
			gameCOParam.put(GetKey(i,Role.SEER), null);
			gameCOParam.put(GetKey(i,Role.MEDIUM), null);
			gameCOParam.put(GetKey(i,Role.BODYGUARD), null);
		}
	}
	
	public boolean isValidRole(Role role)
	{
		if (role != Role.SEER && role != Role.MEDIUM && role != Role.BODYGUARD) return false;
		
		return true;
	}
	
	public void AddCO(Agent agent, int day, int flag, Role coRole)
	{
		if (isValidRole(coRole) == false) return;
		
		COParam newParam = new COParam(day,coRole, flag);
		newParam.agent = agent;
		
		int key = GetKey(day,coRole);
		
		if (gameCOParam.get(key) == null)
		{
			gameCOParam.put(key,newParam);
		}
		else
		{
			gameCOParam.get(key).AddCOToTail(newParam, 0);
		}
	}
	
	/*
	 * @brief		現在のCOParamと同じCOParamを取得(未実装)
	 */
	public void GetSaveCOParam(COParam out, int day, Agent agent, Role role)
	{
		out = null;
		if (isValidRole(role) == false) return;
		int key = GetKey(day,role);
		
		List<COParam> listParam = saveCOParam.get(key);
		COParam gameListParam = gameCOParam.get(key);

		// gameParam の COParamを取得
		while(true)
		{
			if (gameListParam.agent == agent)
			{
				break;
			}
			if (gameListParam.nextParams.size() != 0)
			{
				gameListParam = gameListParam.nextParams.get(0);						
			}
			else
			{
				return;
			}
		}
		
		// 同じパラメータがsaveCOParamに登録されていたら取得
		for(int i = 0; i < listParam.size(); ++i)
		{
			if (listParam.get(i).CheckEq(gameListParam))
			{
				out = listParam.get(i);
				System.out.println("GET COParam Successed.");
				return;
			}
		}
	}
	
	/*
	 * @brief	その日に意味のある発言をした順番を格納
	 */
	public void SetMeaningTalkOrder(Agent agent, int day, Role coRole)
	{
		if (isValidRole(coRole) == false) return;
		int key = GetKey(day,coRole);
		gameCOParam.get(key).SetMeaningOrder(agent, 0);		
	}
	
	public void finishCOParam(Map<Agent,Role> trueRoleMap)
	{
		for(Integer key: gameCOParam.keySet())
		{
			COParam param = gameCOParam.get(key);
			while(true)
			{
				if (param == null) break;
				
				param.realRoleCount.put(trueRoleMap.get(param.agent),1);
				
				if (param.nextParams.size() == 0) break;
				param = param.nextParams.get(0);
			}
		}

		MargeCO();
	}
	
	private void MargeCO()
	{
		for(Integer key: saveCOParam.keySet())
		{
			if (gameCOParam.get(key) != null)
			{
				List<COParam> saveParam = saveCOParam.get(key);
				for(int i = 0; i < saveParam.size(); ++i)
				{
					if (saveParam.get(i).CheckEq(gameCOParam.get(key)))
					{
						saveParam.get(i).Marge(gameCOParam.get(key));
						return;
					}
				}
				saveParam.add(gameCOParam.get(key));				
			}
		}
		return;
	}
	
	/*
	 * @brief		日の開始時に走る更新処理。
	 * 				前日の死亡情報を書き込む
	 */
	public void dayUpdate(int day, Agent attackedAgent, Agent executedAgent)
	{
		if (day == 0) return;
		
		for (int i = 0; i <= ROLE_KEY_MAX; ++i)
		{
			int key = GetKeyInt(day - 1,i);
			COParam param = gameCOParam.get(key);
			while(true)
			{
				if (param == null) break;
				
				int flag = param.flag;
				
				if (attackedAgent != null && param.agent == attackedAgent)
				{
					param.attackedCount++;
					flag |= COParam.FLAG_ATTACKED_DEAD;
				}
				
				if (executedAgent != null && param.agent == executedAgent)
				{
					param.executedCount++;
					flag |= COParam.FLAG_EXECUTED_DEAD;
				}
				
				AddCO(param.agent,day,flag,param.role);

				if (param.nextParams.size() == 0) break;
				param = param.nextParams.get(0);
			}
		}
		
		return;
	}
	
	public static ActionLog GetInstance(int ldNum)
	{
		if(! inst.containsKey(ldNum))
		{
			inst.put(ldNum, new ActionLog());
		}
		return inst.get(ldNum);
	}
}
