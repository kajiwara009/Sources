package jp.ac.nagoya_u.is.ss.kishii.usui.system.viewer;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

public class GamePanel extends JPanel{
	public GamePanel(){
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		setOpaque(false);
	}
	
}
