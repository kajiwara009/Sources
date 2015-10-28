package jp.halfmoon.inaba.aiwolf.learn;

public final class LearnData {


	/** ƒQ[ƒ€” */
	public static int gameCount;


	/** ƒQ[ƒ€”(‘ºw‰c) */
	public static int villagerSide_gameCount;

	/** ƒQ[ƒ€”(˜Tw‰c) */
	public static int wolfSide_gameCount;


	/** ¶‘¶” */
	public static int aliveCount;

	/** ˆŒY” */
	public static int executeCount;

	/** ”íPŒ‚” */
	public static int attackCount;



	/** ÅI„—‚Å˜T³‰ğ” idx=³‰ğ‚µ‚½˜T‚Ì” */
	public static int[] wolfCorrectCount = new int[10];

	/** update()‚ÌÅ’·ŠÔ */
	public static long maxUpdateTime;


	/**
	 * ŠwKƒf[ƒ^‚Ì‰æ–Êo—Í
	 */
	public static void printData(){

		System.out.println("ƒQ[ƒ€”:" + gameCount);
		//System.out.println("‘º‘¤:" + "/" + villagerSide_gameCount + "Ÿ");
		//System.out.println("˜T‘¤:" + "/" + wolfSide_gameCount + "Ÿ");
		System.out.println("––˜H:" + "(¶‘¶:" + aliveCount + " PŒ‚:" + attackCount + " ˆŒY:" + executeCount + ")");
		System.out.println("˜T³‰ğ”:" + "(3:" + wolfCorrectCount[3] + " 2:" + wolfCorrectCount[2] + " 1:" + wolfCorrectCount[1] + " 0:" + wolfCorrectCount[0] + ")");

		System.out.println("update()Å’·:" + maxUpdateTime + "ms");

	}


}
