package com.gmail.tydmskz;

public class PlayerParamaters {

	// 狂人
	public float[] possesedShouldJudgeWhiteIfIamFakeSeer = null;
	public float[] possesedShouldJudgeWhiteIfIamFakeMedium = null;
	
	// 村人
	public float[] villagerWhoShouldIVote = null;

	//狂人が他の人を判定するときにどれ位の割合で黒判定するか
	public float PossesedJudgeWhiteRatio = 0.3f;

	//人狼が非CO人間を占うときに狼と判定する割合
	public float WerewolfJudgeDivineBlackRatio = 0.5f;
	//人狼が非CO人間を霊媒するときに狼と判定する割合
	public float WerewolfJudgeInquestBlackRatio = 0.5f;

}
