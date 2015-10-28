package jp.ac.nagoya_u.is.ss.kishii.usui.system.viewer;

import java.awt.Color;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.PlayerInfo;




public class MakeJamaCardPanel {
	JPanel[] jamaPanel;
	public MakeJamaCardPanel(PlayerInfo playerInfo, String directory){
		jamaPanel = new JPanel[7];
		ImageIcon[] color = new ImageIcon[6];

		color[0] = new ImageIcon(directory + "/ojama.gif");
		color[1] = new ImageIcon(directory + "/small.gif");
		color[2] = new ImageIcon(directory + "/big.gif");
		color[3] = new ImageIcon(directory + "/rock.gif");
		color[4] = new ImageIcon(directory + "/star.gif");
		color[5] = new ImageIcon(directory + "/moon.gif");
		color[6] = new ImageIcon(directory + "/crown.gif");

		for(int i = 0; i < 6; i++){
			jamaPanel[i] = new JPanel();
			jamaPanel[i].setOpaque(false);
			jamaPanel[i].setBackground(Color.BLACK);
		}

		for(int i = 1; i < 6; i++){
			JLabel colorLabel = new JLabel();
			colorLabel.setIcon(color[i-1]);
			jamaPanel[i].add(colorLabel);

		}

	}

}
