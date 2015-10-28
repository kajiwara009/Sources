package com.gmail.tydmskz;

import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Judge;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Species;

public class GameInfoGaFormatter {

	//////////////////////////////////
	// GA用にフォーマットした情報を返すメソッド
	//////////////////////////////////
	
	// Roleの数と同じ長さのfloat配列を返す
	// tagetAgentがCOしたロールは1.0, それ以外は0.0が格納される
	public static float[] GetCoArray(AdvanceGameInfo agi, Agent targetAgent)
	{
		float[] co = new float[Util.GetRoleNumber()];
		int i=0;
		Role coRole = agi.getComingoutMap().get(targetAgent);
		for(Role role:Role.values())
		{
			co[i] = !role.equals(coRole) ? 0.0f : 1.0f;
		}
		return co;
	}
	
	// seerがtargetAgentを占った結果を、長さ3のfloat配列に入れて返す
	// 要素0:人間　要素1:人狼 要素2:占われていない
	// 占われていれば、対応する要素が1.0、その他が0.0
	// 占われていなければ、要素2が1.0、その他が0.0 
	public static float[] GetInspectArray(AdvanceGameInfo agi, Agent seer, Agent targetAgent)
	{
		float[] isp = new float[SpeciesEnum.Count];
		for(int i=0; i<isp.length; i++) isp[i] = 0.0f;
		
		Judge judge = null;
		for(Judge j:agi.getInspectJudgeList())
		{
			if(j.getTarget().equals(targetAgent) && j.getAgent().equals(seer))
			{
				judge = j;
				break;
				// １人の占い師が同じ人に対して違う占い結果を出す、ということがあり得るよねー
				// どうするんだろ？とりあえず無視
			}
		}
		
		if(judge != null)
		{
			if(judge.getResult().equals(Species.HUMAN)) isp[SpeciesEnum.Human] = 1.0f;
			else isp[SpeciesEnum.Werewolf] = 1.0f;
		}
		else
		{
			isp[SpeciesEnum.Unknown] = 1.0f;
		}

		return isp;
	}

	// me以外のAgentがtargetAgentを占った結果を、長さ3のfloat配列に入れて返す
	// (meにnullを入れることで、全ての占い師が対象となる）
	// （該当する占い師が2人以上いる場合は、値が1.0より大きくなる可能性もある）
	// 要素0:人間　要素1:人狼 要素2:占われていない
	// 占われていれば、対応する要素に+1.0、その他が0.0
	// 占われていなければ、要素2が1.0、その他が0.0 
	public static float[] GetInspectArrayExceptMe(AdvanceGameInfo agi, Agent me, Agent targetAgent)
	{
		float[] isp = new float[SpeciesEnum.Count];
		for(int i=0; i<isp.length; i++) isp[i] = 0.0f;
		
		Judge judge = null;
		for(Judge j:agi.getInspectJudgeList())
		{
			if(j.getTarget().equals(targetAgent) && !j.getAgent().equals(me))
			{
				judge = j;
				if(judge.getResult().equals(Species.HUMAN)) isp[SpeciesEnum.Human] += 1.0f;
				else isp[SpeciesEnum.Werewolf] += 1.0f;
			}
		}
		
		if(Math.abs(isp[SpeciesEnum.Human]) < Float.MIN_NORMAL &&
				Math.abs(isp[SpeciesEnum.Werewolf]) < Float.MIN_NORMAL)
		{
			isp[SpeciesEnum.Unknown] = 1.0f;
		}

		return isp;
	}
	
	public class SpeciesEnum
	{
		static public final int Human = 0;
		static public final int Werewolf = 1;
		static public final int Unknown = 2;
		
		static public final int Count = 3; 
	}
}
