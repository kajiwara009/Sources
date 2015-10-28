package jp.ac.nagoya_u.is.ss.kishii.usui.system.viewer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.PlayerInfo;




public class Score extends JPanel{
	JLabel player3;
	public Score(int nextHeight,int nextWidth,int maxHeight,PlayerInfo playerInfo,String directory){
		setPreferredSize(new Dimension(BoardMaker.CELL_SIZE * nextWidth,
				BoardMaker.CELL_SIZE * (maxHeight - ((nextHeight-1) * 2))));
		setBackground(Color.WHITE);
		setBorder(BorderFactory.createLineBorder(Color.BLACK));
		JLabel player1 = new JLabel(playerInfo.getPlayerName());
		player1.setFont(new Font("MS ゴシック", Font.PLAIN, BoardMaker.CELL_SIZE/2));
		add(player1);

		ImageIcon icon = new ImageIcon("./" + directory +"/user.gif");
		Image smallIMG = icon.getImage().getScaledInstance(BoardMaker.CELL_SIZE* 4, -1, Image.SCALE_SMOOTH);	
		icon = new ImageIcon(smallIMG);
		JLabel player2 = new JLabel();
		player2.setIcon(icon);
		add(player2);

		player3 = new JLabel();
		add(player3);
	}



}
