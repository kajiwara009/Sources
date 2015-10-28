package jp.ac.nagoya_u.is.ss.kishii.usui.system.viewer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.PlayerInfo;





public class CenterPanel extends JPanel{

	JPanel[][] blockArray;

	public static final int NEXT_WIDTH = 4;
	public static final int NEXT_HEIGHT = 2;


	private GridMaker nextPanel;
	private GridMaker nextnextPanel;
	Score playerScore;
	public CenterPanel(int maxHeight,PlayerInfo playerInfo, String directory) {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setOpaque(false);

//		JLabel next = new JLabel("NEXT");
//		next.setFont(new Font("Arial", Font.PLAIN, 20));
//		JLabel nextnext = new JLabel("NEXTNEXT");
//		nextnext.setFont(new Font("Arial", Font.PLAIN, 20));

		setNextPanel(new GridMaker(NEXT_HEIGHT, NEXT_WIDTH, playerInfo, directory));

		GridMaker spacePanel = new GridMaker(1, NEXT_WIDTH, playerInfo, directory);

		nextnextPanel = new GridMaker(NEXT_HEIGHT, NEXT_WIDTH, playerInfo, directory);

		playerScore = new Score(NEXT_HEIGHT,NEXT_WIDTH,maxHeight,playerInfo, directory);



		add(nextPanel);
		add(spacePanel);
		add(nextnextPanel);
		add(new SpaceMaker());
		add(playerScore);
	}

	public void putScore(int win){
		playerScore.player3.setText("\n" + win);
		playerScore.player3.setFont(new Font("MS ゴシック", Font.PLAIN, BoardMaker.CELL_SIZE * 2));
	}


	public void setNextPanel(GridMaker nextPanel) {
		this.nextPanel = nextPanel;
	}

	public GridMaker getNextPanel() {
		return nextPanel;
	}

	public void setNextNextPanel(GridMaker nextPanel) {
		this.nextnextPanel = nextPanel;
	}

	public GridMaker getNextNextPanel() {
		return nextnextPanel;
	}

}
