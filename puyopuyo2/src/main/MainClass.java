package main;

import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.AbstractPlayer;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.CppPlayer;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.PuyoPuyo;
import jp.ac.nagoya_u.is.ss.mase.usui.manipulater.Manipulater;
import SamplePlayer.SamplePlayer;
//import UsuiPlayerLv2.UsuiPlayerLv2;
import UsuiPlayer.UsuiPlayer;

/**
 * 繝｡繧､繝ｳ繧ｯ繝ｩ繧ｹ
 */

public class MainClass {

	public static void main(String args[]) {
		/*
		 * 繝励Ξ繧､繝､繝ｼ縺ｮ隱ｭ縺ｿ霎ｼ縺ｿ
		 */
		AbstractPlayer samplePlayer = new SamplePlayer("Sample");
		AbstractPlayer randomPlayer = new UsuiPlayer("usui");

		/*
		 * 莠ｺ謇九〒謫堺ｽ懊☆繧句�蜷�
		 */
		AbstractPlayer manipulater = new Manipulater("Manipulater");

		/*
		 * C++縺ｧ菴懈�縺励◆繝励Ξ繧､繝､繝ｼ縺ｮ隱ｭ縺ｿ霎ｼ縺ｿ
		 */
		AbstractPlayer kenshoPlayer = new CppPlayer("Kensho", "KenshoPlayer");

		/*
		 * 繧ｲ繝ｼ繝�ｒ螳溯｡�
		 * 閾ｪ蜑阪�逕ｻ蜒上ｒ菴ｿ逕ｨ縺励◆縺��蜷医�繝�ぅ繝ｬ繧ｯ繝医Μ蜷阪ｒ繧ｳ繝ｳ繧ｹ繝医Λ繧ｯ繧ｿ縺ｫ貂｡縺励※縺上□縺輔＞縲�
		 */
		PuyoPuyo puyopuyo = new PuyoPuyo(samplePlayer);

		/*
		 * 縺薙■繧峨�荳�ｺｺ逕ｨ
		 */
		//PuyoPuyo puyopuyo = new PuyoPuyo(kenshoPlayer);

		puyopuyo.puyoPuyo();
	}
}
