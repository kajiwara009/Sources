package org.aiwolf.Satsuki.reinforcementLearning;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Role;

public class COPercent
{
	public static int FIND_WOLF			= 1 << 0;		// 狼を見つけた事によるCO(COターン中最後の審判発言か黒)
	public static int OPPOSION			= 1 << 1;		// 対抗CO(偽占い師が出てきたからの同ターン内CO)
	public static int OPPOSION_THROW	= 1 << 2;		// 対抗スルーCO(偽占い師が出てきたが、同ターンCOはしない)
	
	public static int TURN_0			= 1 << 3;		// 上記以外の時の0ターンCO
	public static int TURN_1			= 1 << 4;		// 上記以外の時の1ターンCO
	public static int TURN_2			= 1 << 5;		// 上記以外の時の2ターンCO
	public static int TURN_3			= 1 << 6;		// 上記以外の時の3ターンCO
	public static int TURN_4			= 1 << 7;		// 上記以外の時の4ターンCO
	
	public static int JUDGE_INVALID		= 1 << 8;		// 一時的なフラグ。ジャッジの無効
	
	public static int INV_TURN_MASK		= 0xFFFFFFFF - TURN_0 - TURN_1 - TURN_2 - TURN_3 - TURN_4;
	
	// CO数評価点
	public static double CO_COUNT_POINT		= 0.5f;
	
	// プレイヤー戦の目指す評価店
	public static double BASE_PLAYER_POINT	= 0.5f;
	
	// 人狼戦の目指す評価点
	public static double BASE_WOLF_POINT	= 0.65f;

	// 各評価となるために必要な最小限のポイント
	public static double NEED_MIN_POINT		= 0.12f;
	
	// 占い師の占いCO評価点<フラグ,評価点>
	public Map<Integer,Double> seerCounterPoint  = new HashMap<Integer,Double>();
	// 占い師以外の占いCO評価点<フラグ,評価点>
	public Map<Integer,Double> nSeerCounterPoint = new HashMap<Integer,Double>();
	
	// 占い師の占いCOタイミングによる狼、村勝利評価点<フラグ,評価点>
	public Map<Integer,Double> seerCO_wolfWinCounter = new HashMap<Integer,Double>();
	public Map<Integer,Double> seerCO_nWolfWinCounter = new HashMap<Integer,Double>();
	public Map<Integer,Double> seerCO_wolfWinCounter_mine = new HashMap<Integer,Double>();
	public Map<Integer,Double> seerCO_nWolfWinCounter_mine = new HashMap<Integer,Double>();

	// 狼の占いCOタイミングによる、狼、村勝利評価点<フラグ,評価点>
	public Map<Integer,Double> wolfCO_wolfWinCounter = new HashMap<Integer,Double>();
	public Map<Integer,Double> wolfCO_nWolfWinCounter = new HashMap<Integer,Double>();
	public Map<Integer,Double> wolfCO_wolfWinCounter_mine = new HashMap<Integer,Double>();
	public Map<Integer,Double> wolfCO_nWolfWinCounter_mine = new HashMap<Integer,Double>();
	
	// 狂人の占いCOタイミングによる、狼、村勝利評価点<フラグ,評価点>
	public Map<Integer,Double> possessedCO_wolfWinCounter = new HashMap<Integer,Double>();
	public Map<Integer,Double> possessedCO_nWolfWinCounter = new HashMap<Integer,Double>();
	public Map<Integer,Double> possessedCO_wolfWinCounter_mine = new HashMap<Integer,Double>();
	public Map<Integer,Double> possessedCO_nWolfWinCounter_mine = new HashMap<Integer,Double>();

	private static Map<Integer, COPercent> inst = new HashMap<Integer, COPercent>();
	
	// ここからは一試合ごとの記憶
	public Map<Agent,Integer> coDay = new HashMap<Agent,Integer>();
	public Map<Agent,Integer> coFlag = new HashMap<Agent,Integer>();
	
	
	COPercent()
	{
		initializeParam(seerCounterPoint,CO_COUNT_POINT);
		initializeParam(nSeerCounterPoint,CO_COUNT_POINT);
		
		initializeParam(seerCO_wolfWinCounter,BASE_WOLF_POINT);
		initializeParam(seerCO_nWolfWinCounter,BASE_PLAYER_POINT);
		initializeParam(seerCO_wolfWinCounter_mine,BASE_WOLF_POINT);
		initializeParam(seerCO_nWolfWinCounter_mine,BASE_PLAYER_POINT);
		
		initializeParam(wolfCO_wolfWinCounter,BASE_WOLF_POINT);
		initializeParam(wolfCO_nWolfWinCounter,BASE_PLAYER_POINT);
		initializeParam(wolfCO_wolfWinCounter_mine,BASE_WOLF_POINT);
		initializeParam(wolfCO_nWolfWinCounter_mine,BASE_PLAYER_POINT);

		initializeParam(possessedCO_wolfWinCounter,BASE_WOLF_POINT);
		initializeParam(possessedCO_nWolfWinCounter,BASE_PLAYER_POINT);
		initializeParam(possessedCO_wolfWinCounter_mine,BASE_WOLF_POINT);
		initializeParam(possessedCO_nWolfWinCounter_mine,BASE_PLAYER_POINT);
	}
	
	public double calcPoint(double pt, double minePt)
	{
		return pt * minePt * minePt; 
	}
	
	public int getNTurnFlag(int idx)
	{
		switch(idx)
		{
		case 0: return FIND_WOLF;
		case 1: return FIND_WOLF | OPPOSION;
		case 2: return FIND_WOLF | OPPOSION_THROW;
		case 3: return OPPOSION;
		case 4: return OPPOSION_THROW;
		}
		return 0;
	}
	public int getTurnFlag(int idx)
	{
		switch(idx)
		{
		case 0: return TURN_0;
		case 1: return TURN_1;
		case 2: return TURN_2;
		case 3: return TURN_3;
		case 4: return TURN_4;
		}
		return 0;
	}
	public int getNTurnFlagNum()
	{
		return 5;
	}
	public int getTurnFlagNum()
	{
		return 5;
	}
	
	// ターン系以外の狼の占いCOのうち最も優秀なフラグを返す
	public int getMaxFlag_Wolf_NTurn()
	{
		double maxPoint = NEED_MIN_POINT;
		int maxFlag = 0;
		for (int i = 0; i< getNTurnFlagNum(); ++i)
		{
			int flag = getNTurnFlag(i);
			double point = calcPoint(wolfCO_wolfWinCounter.get(flag), wolfCO_wolfWinCounter_mine.get(flag));
			
			if (point > maxPoint)
			{
				maxPoint = point;
				maxFlag = flag;
			}			
		}
		return maxFlag;
	}

	// ターン系以外の狂の占いCOのうち最も優秀なフラグを返す
	public int getMaxFlag_Possessed_Turn()
	{
		double maxPoint = NEED_MIN_POINT;
		int maxFlag = 0;
		for (int i = 0; i < getTurnFlagNum(); ++i)
		{
			int flag = getTurnFlag(i);
			double point = calcPoint(possessedCO_wolfWinCounter.get(flag), possessedCO_wolfWinCounter_mine.get(flag));
			
			if (point > maxPoint)
			{
				maxPoint = point;
				maxFlag = flag;
			}			
		}
		return maxFlag;
	}

	public int getMaxFlag_Possessed_NTurn()
	{
		double maxPoint = NEED_MIN_POINT;
		int maxFlag = 0;
		for (int i = 0; i< getNTurnFlagNum(); ++i)
		{
			int flag = getNTurnFlag(i);
			double point = calcPoint(possessedCO_wolfWinCounter.get(flag), possessedCO_wolfWinCounter_mine.get(flag));
			
			if (point > maxPoint)
			{
				maxPoint = point;
				maxFlag = flag;
			}			
		}
		return maxFlag;
	}
	
	public int getMaxFlag_Wolf_Turn()
	{
		double maxPoint = NEED_MIN_POINT;
		int maxFlag = 0;
		for (int i = 0; i < getTurnFlagNum(); ++i)
		{
			int flag = getTurnFlag(i);
			double point = calcPoint(wolfCO_wolfWinCounter.get(flag), wolfCO_wolfWinCounter_mine.get(flag));
			
			if (point > maxPoint)
			{
				maxPoint = point;
				maxFlag = flag;
			}			
		}
		return maxFlag;
	}
	public void gameStart()
	{
		coDay.clear();
		coFlag.clear();
	}
	
	// CO記憶
	public void memoryCODay(Agent agent, int day, Map<Agent, Role> comingoutMap)
	{
		if (coDay.containsKey(agent) == false)
		{
			int flag = 0;
			if (coDay.size() >= 1)
			{
				for(Entry<Agent, Integer> set: coDay.entrySet())
				{
					if (set.getValue() != day)
					{
						flag |= OPPOSION_THROW;
						break;
					}
				}
				
				if ((flag & OPPOSION_THROW) == 0) flag |= OPPOSION;
			}
			
			switch(day)
			{
			case 0: flag |= TURN_0; break;
			case 1: flag |= TURN_1; break;
			case 2: flag |= TURN_2; break;
			case 3: flag |= TURN_3; break;
			default: flag |= TURN_4; break;
			}
			
			coDay.put(agent, day);
			coFlag.put(agent, flag);
		}
	}
	
	// ジャッジ記憶
	public void judgeCODay(Agent agent, int day, Map<Agent, Role> comingoutMap, boolean bWolf)
	{
		// 同日にすでに狼発言をしているのに、また占いを発言したとき、
		// 最新日以外で狼を見つけているのに占いを言わなかったと判定し、発言を行わない。
		if (coDay.get(agent) == day && (coFlag.get(agent) & FIND_WOLF) != 0)
		{
			coFlag.put(agent, (coFlag.get(agent) ^ FIND_WOLF) | JUDGE_INVALID);
		}
		
		// 狼
		if (bWolf && coDay.get(agent) == day && (coFlag.get(agent) & JUDGE_INVALID) == 0)
		{
			coFlag.put(agent, coFlag.get(agent) | FIND_WOLF);
		}
	}
	
	private void winUpdate(Map<Integer,Double> map, int flag)
	{
		double newPoint = ReinforcementLearning.reInforcementLearn(map.get(flag), 1.0f, 0);
		map.put(flag, newPoint);
	}
	private void loseUpdate(Map<Integer,Double> map, int flag, double basePoint)
	{
		//　今のフラグは下げ、他のフラグはあげる
		for(Entry<Integer, Double> set: map.entrySet())
		{
			if(set.getKey() == flag)
			{
				double newPoint = ReinforcementLearning.reInforcementLearn(set.getValue(), 0.0f, 0);
				set.setValue(newPoint);					
			}
			else if (set.getValue() < basePoint)
			{
				double newPoint = ReinforcementLearning.reInforcementLearn(set.getValue(), basePoint, 0);
				set.setValue(newPoint);
			}
		}
	}
	
	// 渡されたエージェントの種類から最も信用できる占い師を返却します。
	public Agent getBeliaveSeer(List<Agent> seerAgents)
	{
		double maxPoint = 0.0f;
		Agent maxAgent = null;
		for (Agent agent: seerAgents)
		{
			if (coDay.containsKey(agent) == false) continue;
			if (coFlag.containsKey(agent) == false) continue;

			// 不要なフラグの抹消
			int flag = coFlag.get(agent);
			if ((flag & JUDGE_INVALID) != 0)
			{
				flag = flag ^ JUDGE_INVALID;
			}
			
			//TURNフラグ以外のフラグがあれば、ターンフラグ抹消
			if ((flag & INV_TURN_MASK) != 0)
			{
				flag = flag & INV_TURN_MASK;
			}
			
			double point = seerCounterPoint.get(flag);
			
			if (maxPoint > point)
			{
				maxPoint = point;
				maxAgent = agent;
			}
		}
		
		return maxAgent;
	}
	
	// 学習
	public void gameFinishUpdate(boolean isVillagerWin, Agent myAgent, Map<Agent, Role> comingoutMap, Map<Agent, Role> trueRoleMap)
	{
		// フラグをセットアップ
		for(Entry<Agent, Integer> set: coFlag.entrySet())
		{
			// 不要なフラグの抹消
			if ((set.getValue() & JUDGE_INVALID) != 0)
			{
				set.setValue(set.getValue() ^ JUDGE_INVALID);
			}
			
			//TURNフラグ以外のフラグがあれば、ターンフラグ抹消
			if ((set.getValue() & INV_TURN_MASK) != 0)
			{
				set.setValue(set.getValue() & INV_TURN_MASK);
			}
		}
		
		for(Entry<Agent, Role> set: comingoutMap.entrySet())
		{
			Agent agent = set.getKey();
			
			// 占い師以外は処理しない。
			if (set.getValue() != Role.SEER) continue;
			if (coFlag.containsKey(agent) == false) continue;

			Role trueRole = trueRoleMap.get(agent);
			int flag = coFlag.get(agent);
			if (trueRole == Role.SEER)
			{
				seerCounterPoint.put(flag, ReinforcementLearning.reInforcementLearn(seerCounterPoint.get(flag), 1.0f, 0));
				nSeerCounterPoint.put(flag, ReinforcementLearning.reInforcementLearn(seerCounterPoint.get(flag), 0.0f, 0));
				if (isVillagerWin)
				{
					winUpdate(seerCO_nWolfWinCounter,flag);
					loseUpdate(seerCO_wolfWinCounter,flag,BASE_WOLF_POINT);
					if (agent == myAgent)
					{
						winUpdate(seerCO_nWolfWinCounter_mine,flag);
						loseUpdate(seerCO_wolfWinCounter_mine,flag,BASE_WOLF_POINT);						
					}
				}
				else
				{
					loseUpdate(seerCO_nWolfWinCounter,flag,BASE_PLAYER_POINT);
					winUpdate(seerCO_wolfWinCounter,flag);
					if (agent == myAgent)
					{
						loseUpdate(seerCO_nWolfWinCounter_mine,flag,BASE_PLAYER_POINT);
						winUpdate(seerCO_wolfWinCounter_mine,flag);						
					}	
				}
			}
			else if (trueRole == Role.WEREWOLF)
			{
				seerCounterPoint.put(flag, ReinforcementLearning.reInforcementLearn(seerCounterPoint.get(flag), 0.0f, 0));
				nSeerCounterPoint.put(flag, ReinforcementLearning.reInforcementLearn(seerCounterPoint.get(flag), 1.0f, 0));
				if (isVillagerWin)
				{
					winUpdate(wolfCO_nWolfWinCounter,coFlag.get(agent));
					loseUpdate(wolfCO_wolfWinCounter,coFlag.get(agent),BASE_WOLF_POINT);
					if (agent == myAgent)
					{
						winUpdate(wolfCO_nWolfWinCounter_mine,coFlag.get(agent));
						loseUpdate(wolfCO_wolfWinCounter_mine,coFlag.get(agent),BASE_WOLF_POINT);						
					}
				}
				else
				{
					loseUpdate(wolfCO_nWolfWinCounter,coFlag.get(agent),BASE_PLAYER_POINT);
					winUpdate(wolfCO_wolfWinCounter,coFlag.get(agent));
					if (agent == myAgent)
					{
						loseUpdate(wolfCO_nWolfWinCounter_mine,coFlag.get(agent),BASE_PLAYER_POINT);
						winUpdate(wolfCO_wolfWinCounter_mine,coFlag.get(agent));						
					}	
				}
				
			}
			else if (trueRole == Role.POSSESSED)
			{
				seerCounterPoint.put(flag, ReinforcementLearning.reInforcementLearn(seerCounterPoint.get(flag), 0.0f, 0));
				nSeerCounterPoint.put(flag, ReinforcementLearning.reInforcementLearn(seerCounterPoint.get(flag), 1.0f, 0));
				if (isVillagerWin)
				{
					winUpdate(possessedCO_nWolfWinCounter,coFlag.get(agent));
					loseUpdate(possessedCO_wolfWinCounter,coFlag.get(agent),BASE_WOLF_POINT);
					if (agent == myAgent)
					{
						winUpdate(possessedCO_nWolfWinCounter_mine,coFlag.get(agent));
						loseUpdate(possessedCO_wolfWinCounter_mine,coFlag.get(agent),BASE_WOLF_POINT);						
					}
				}
				else
				{
					loseUpdate(possessedCO_nWolfWinCounter,coFlag.get(agent),BASE_PLAYER_POINT);
					winUpdate(possessedCO_wolfWinCounter,coFlag.get(agent));
					if (agent == myAgent)
					{
						loseUpdate(possessedCO_nWolfWinCounter_mine,coFlag.get(agent),BASE_PLAYER_POINT);
						winUpdate(possessedCO_wolfWinCounter_mine,coFlag.get(agent));						
					}	
				}
			}
		}
	}
	
	public void initializeParam(Map<Integer,Double> param, double basePoint)
	{
		param.put(FIND_WOLF,basePoint);
		param.put(FIND_WOLF | OPPOSION,basePoint);
		param.put(FIND_WOLF | OPPOSION_THROW,basePoint);
		param.put(OPPOSION,basePoint);
		param.put(OPPOSION_THROW,basePoint);
		param.put(TURN_0,basePoint);
		param.put(TURN_1,basePoint);
		param.put(TURN_2,basePoint);
		param.put(TURN_3,basePoint);
		param.put(TURN_4,basePoint);
	}
	
	public static COPercent GetInstance(int ldNum)
	{
		if(! inst.containsKey(ldNum))
		{
			inst.put(ldNum, new COPercent());
		}
		return inst.get(ldNum);
	}
}