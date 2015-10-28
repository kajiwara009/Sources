package jp.ac.nagoya_u.is.ss.mase.usui.manipulater;

import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JFrame;

public class PrefFrame extends JFrame implements KeyListener {

	Manipulater manipulater;

	public PrefFrame(String playerName, Manipulater manipulater) {
		setTitle(playerName);
		this.manipulater = manipulater;
		addKeyListener(this);
		setLocationRelativeTo(null);
		setLayout(new FlowLayout());
		setSize(250, 200);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

	}

	private Container cp;
	private MainPanel mainPanel;

	public MainPanel getMainPanel() {
		return mainPanel;
	}

	public void makeMainPanel() {
		cp = getContentPane();
		mainPanel = new MainPanel(manipulater);
		cp.add(mainPanel);
	}

	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			manipulater.enter();
		} else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
			manipulater.moveLeft();
		} else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
			manipulater.moveRight();
		} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
			manipulater.turnRight();
		} else if (e.getKeyCode() == KeyEvent.VK_UP) {
			manipulater.turnLeft();
		} 


	}

	public void keyReleased(KeyEvent e) {
	}

	public void keyTyped(KeyEvent e) {
	}

}
