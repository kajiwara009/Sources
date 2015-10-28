package jp.ac.nagoya_u.is.ss.mase.usui.manipulater;

import java.awt.Color;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.PlayerInfo;




public class MakeCardPanel{
	private JPanel[] colorPanel;
	JPanel[] jamaPanel;

	public MakeCardPanel(){
		colorPanel=new JPanel[6];
		ImageIcon[] color = new ImageIcon[5];
		color[0] = new ImageIcon("./image/ao.gif");
		color[1] = new ImageIcon("./image/aka.gif");
		color[2] = new ImageIcon("./image/midori.gif");
		color[3] = new ImageIcon("./image/ki.gif");
		color[4] = new ImageIcon("./image/murasaki.gif");

		for(int i = 0; i < 6; i++){
			getColorPanel()[i] = new JPanel();


		}
		colorPanel[0].setBackground(Color.WHITE);
		for(int i = 1; i < 6; i++){
			JLabel colorLabel = new JLabel();
			colorLabel.setIcon(color[i-1]);
			colorPanel[i].add(colorLabel);
		}

//		colorPanel[1].setBackground(Color.BLUE);
//		colorPanel[2].setBackground(Color.RED);
//		colorPanel[3].setBackground(Color.GREEN);
//		colorPanel[4].setBackground(Color.YELLOW);
//		colorPanel[5].setBackground(Color.PINK);
		
		

	}

	public JPanel[] getColorPanel() {
		return colorPanel;
	}

}
