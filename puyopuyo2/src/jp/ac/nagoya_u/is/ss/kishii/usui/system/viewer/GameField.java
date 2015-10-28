package jp.ac.nagoya_u.is.ss.kishii.usui.system.viewer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ByteLookupTable;
import java.awt.image.LookupOp;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.border.Border;

import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.PlayerInfo;



public class GameField extends JPanel{
	private GridMaker ojama;
	private GridMaker panel;
	public GameField(int maxHeight,int maxWidth,PlayerInfo playerInfo, String directory){
		super(new BorderLayout());


		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
//		setBackground(Color.BLUE);


		setOpaque(false);


		this.ojama = new GridMaker(3, maxWidth,playerInfo, directory);
		this.panel = new GridMaker(maxHeight, maxWidth, playerInfo, directory);

		BackPanel back = new BackPanel("back.jpg");

		back.add(panel);
		JPanel backOjama = new JPanel();
		backOjama.add(ojama);
		add(backOjama);

		add(back);
//		add(panel);


	}


	public void setPanel(GridMaker panel) {
		this.panel = panel;
	}
	public GridMaker getPanel() {
		return panel;
	}
	public void setOjama(GridMaker ojama) {
		this.ojama = ojama;
	}
	public GridMaker getOjama() {
		return ojama;
	}

}
