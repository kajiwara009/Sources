package jp.ac.nagoya_u.is.ss.mase.usui.manipulater;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

public class MainPanel extends JPanel{

	
	private GridMaker gridMaker;
	private Manipulater manipulater;
	public GridMaker getGridMaker() {
		return gridMaker;
	}
	
	JButton leftButton;
	JButton rightButton;
	JButton turnButton;
	JButton enterButton;
	public MainPanel(Manipulater manipulater){
		this.manipulater = manipulater;
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		gridMaker = new GridMaker(4, 6);
		add(gridMaker);
//		JPanel buttonPanel = new JPanel();
//		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
//		leftButton = new JButton("LEFT");
//		leftButton.addActionListener(this);
//		rightButton = new JButton("RIGHT");
//		rightButton.addActionListener(this);
//		turnButton = new JButton("TURN");
//		turnButton.addActionListener(this);
//		enterButton = new JButton("ENTER");
//		enterButton.addActionListener(this);
//		buttonPanel.add(leftButton);
//		buttonPanel.add(rightButton);
//		buttonPanel.add(turnButton);
//		buttonPanel.add(enterButton);
//		add(buttonPanel);
	}
	
//	@Override
//	public void actionPerformed(ActionEvent e) {
//	    if(e.getSource() == leftButton){
//	    	manipulater.moveLeft();
//	    }
//	    if(e.getSource() == rightButton){
//	    	manipulater.moveright();
//	    }
//	    if(e.getSource() == turnButton){
//	    	manipulater.turn();
//	    }
//	    if(e.getSource() == enterButton){
//	    	manipulater.enter();
//	    }
//	}

}
