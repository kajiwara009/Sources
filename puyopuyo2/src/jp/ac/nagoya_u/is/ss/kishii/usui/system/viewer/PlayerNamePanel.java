package jp.ac.nagoya_u.is.ss.kishii.usui.system.viewer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.PlayerInfo;




public class PlayerNamePanel extends JPanel{
	public PlayerNamePanel(PlayerInfo playerInfo){
		setPreferredSize(new Dimension(BoardMaker.CELL_SIZE * 1,
				BoardMaker.CELL_SIZE));
		setBackground(Color.WHITE);
		setBorder(BorderFactory.createLineBorder(Color.BLACK));
		JLabel player1 = new JLabel(playerInfo.getPlayerName());
		player1.setFont(new Font("MS ゴシック", Font.PLAIN, BoardMaker.CELL_SIZE /2));

		add(player1);
	}

}
