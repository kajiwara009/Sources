import sp.ScoringPlayer;
import sp.ScoringPlayer2;
import hira.player.lib4basic.HiraLib4Basic;

import com.gmail.kajiwara009.KajiwaraAngel;
import com.gmail.sego0301.Babadevil1;

import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.AbstractPlayer;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.PuyoPuyo;
import jp.ac.nagoya_u.is.ss.mase.usui.manipulater.Manipulater;
import moc.liamtoh900ognek.KajiGOD3;
import moc.liamtoh900ognek.KajiGodGod;
import UsuiPlayer.UsuiPlayer;



/**
 * 任意の二体のエージェント同士を対戦させるためのクラス
 */
public class VSMode {

	public static void main(String args[]) {
		/**
		 * 任意の二つのエージェントを読み込む．<br>
		 */
		AbstractPlayer player1 = new maou2014.Maou("魔王");
		AbstractPlayer player2 = new KajiGOD3("TA1");
		AbstractPlayer playerKajiGodGod = new KajiGodGod("梶原");
		AbstractPlayer player3 = new UsuiPlayer("TA2");
		AbstractPlayer playerManu = new Manipulater("おてて");
		AbstractPlayer playerMe = new KajiwaraAngel("aaa");
		AbstractPlayer playerBaba = new Babadevil1("");
		
		AbstractPlayer playerHirayama = new HiraLib4Basic("平山君");
		
		
		PuyoPuyo puyopuyo = new PuyoPuyo(playerHirayama, playerKajiGodGod);
		puyopuyo.puyoPuyo();
		

	}
}
