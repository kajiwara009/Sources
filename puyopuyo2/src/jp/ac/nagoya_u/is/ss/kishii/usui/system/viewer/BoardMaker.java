package jp.ac.nagoya_u.is.ss.kishii.usui.system.viewer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.PlayerInfo;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.GameInfo.PlayerNumber;



public class BoardMaker extends JPanel{

	public static final int CELL_SIZE = PrefFrame.DISPLAYHEIGHT/23;

	private final int MAX_WIDTH;
	private final int MAX_HEIGHT;



	private CenterPanel center;
	GameField myField;



	public BoardMaker(int width, int height, PlayerInfo playerInfo, String directory) {
		MAX_WIDTH = width;
		MAX_HEIGHT = height - 1;

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
//		setBackground(Color.BLUE);
		setOpaque(false);

		PlayerNamePanel playerNamePanel = new PlayerNamePanel(playerInfo);
		add(playerNamePanel);


		GamePanel myPanel = new GamePanel();

		this.center = new CenterPanel(MAX_HEIGHT,playerInfo, directory);


		add(myPanel);

		myField = new GameField(MAX_HEIGHT,MAX_WIDTH,playerInfo, directory);

		if (playerInfo.getPlayerNumber() == PlayerNumber.ONE) {
			myPanel.add(myField);
			myPanel.add(new SpaceMaker());
			myPanel.add(this.center);
		} else {
			myPanel.add(this.center);
			myPanel.add(new SpaceMaker());
			myPanel.add(myField);
		}

	}


	public void setCenter(CenterPanel center) {
		this.center = center;
	}

	public CenterPanel getCenter() {
		return center;
	}


	public GridMaker getPanel() {
		return myField.getPanel();
	}

	public GridMaker getOjama() {
		return myField.getOjama();
	}

}
