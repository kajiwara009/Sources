package jp.ac.nagoya_u.is.ss.kishii.usui.system.viewer;

import java.awt.Color;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.PlayerInfo;




public class MakeCardPanel{
	private JPanel[] colorPanel;
	JPanel[] jamaPanel;

	public MakeCardPanel(PlayerInfo playerInfo, String directory){
		colorPanel=new JPanel[13];
		ImageIcon[] color = new ImageIcon[13];
		color[0] = new ImageIcon(directory + "./ao.gif");
		color[1] = new ImageIcon(directory + "./aka.gif");
		color[2] = new ImageIcon(directory + "./midori.gif");
		color[3] = new ImageIcon(directory + "./ki.gif");
		color[4] = new ImageIcon(directory + "./murasaki.gif");
		color[5] = new ImageIcon(directory + "./ojama.gif");
		color[6] = new ImageIcon(directory + "./small.gif");
		color[7] = new ImageIcon(directory + "./big.gif");
		color[8] = new ImageIcon(directory + "./rock.gif");
		color[9] = new ImageIcon(directory + "./star.gif");
		color[10] = new ImageIcon(directory + "./moon.gif");
		color[11] = new ImageIcon(directory + "./crown.gif");

		for(int i = 0; i < 13; i++){
			colorPanel[i] = new JPanel();
			colorPanel[i].setOpaque(false);
			colorPanel[i].setBackground(Color.BLACK);
		}

		for(int i = 0; i < 12; i++){
			if(color[i].getIconWidth() > 0){
				Image smallIMG = color[i].getImage().getScaledInstance(BoardMaker.CELL_SIZE, BoardMaker.CELL_SIZE-7, Image.SCALE_SMOOTH);	
				color[i] = new ImageIcon(smallIMG);
			}
		}
		
		for(int i = 1; i < 13; i++){
			JLabel colorLabel = new JLabel();
			colorLabel.setIcon(color[i-1]);
			colorPanel[i].add(colorLabel);

		}



//		colorPanel[7].setBackground(Color.darkGray);
//		colorPanel[8].setBackground(Color.RED);
//		colorPanel[9].setBackground(Color.MAGENTA);
//		colorPanel[10].setBackground(Color.ORANGE);
//		colorPanel[11].setBackground(Color.YELLOW);


	}
	
	public JPanel[] getColorPanel(){
		return colorPanel;
	}


}
