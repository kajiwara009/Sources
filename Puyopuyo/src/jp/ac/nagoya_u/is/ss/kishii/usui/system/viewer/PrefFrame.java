package jp.ac.nagoya_u.is.ss.kishii.usui.system.viewer;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.PuyoPuyo;

public class PrefFrame extends JFrame implements ActionListener {
	Container cp;
	private ConsolePanel consolePanel;
	public static int DISPLAYHEIGHT;

	public PrefFrame(String title, int displayHeight) {
		// フレームのタイトル

		setTitle(title);
		setSize(getViewerSize());
		setLocationRelativeTo(null);
		setLayout(new FlowLayout());
		if (displayHeight > 0) {
			DISPLAYHEIGHT = displayHeight;
			setSize(DISPLAYHEIGHT, DISPLAYHEIGHT);
		}

		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
				// ダイアログボックスの表示
				// int ret = JOptionPane.showConfirmDialog(cp, "プログラムを終了しますか？",
				// "確認", JOptionPane.YES_NO_OPTION);
				//
				// if (ret == JOptionPane.YES_OPTION) {
				//
				// }

			}
		});
	}

	JButton startButton;
	JButton stopButton;

	public void mainPanel(JPanel panel) {
		// コンテンツペイン取得
		cp = getContentPane();
		// cp.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		// BackPanel back = new BackPanel("tokeidai.jpg");
		// back.add(panel);
		// cp.add(back);
		JPanel allPanel = new JPanel();
		BoxLayout layout = new BoxLayout(allPanel, BoxLayout.Y_AXIS);
		allPanel.setLayout(layout);
		// allPanel.setLayout(new FlowLayout());
		JPanel buttonPanel = new JPanel();
		startButton = new JButton("start");
		startButton.addActionListener(this);
		startButton.setPreferredSize(new Dimension(PrefFrame.DISPLAYHEIGHT / 5,
				BoardMaker.CELL_SIZE));
		stopButton = new JButton("stop");
		stopButton.addActionListener(this);
		stopButton.setPreferredSize(new Dimension(PrefFrame.DISPLAYHEIGHT / 5,
				BoardMaker.CELL_SIZE));
		buttonPanel.add(startButton);
		buttonPanel.add(stopButton);
		allPanel.add(buttonPanel);
		allPanel.add(panel);
		consolePanel = new ConsolePanel();
		allPanel.add(consolePanel);

		cp.add(allPanel);
	}

	private Dimension getViewerSize() {
		Dimension screenSize = null;
		int screenWidth = 1000;
		int screenHight = 2000;
		screenSize = new Dimension(screenWidth, screenHight);
		GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		GraphicsDevice graphicsDevice = graphicsEnvironment
				.getDefaultScreenDevice();
		Rectangle rect = graphicsDevice.getDefaultConfiguration().getBounds();
		screenSize = rect.getSize();
		screenWidth = (int) screenSize.getWidth();
		screenHight = (int) screenSize.getHeight();
		DISPLAYHEIGHT = screenHight;
		return screenSize;

	}

	public void setConsolePanel(ConsolePanel consolePanel) {
		this.consolePanel = consolePanel;
	}

	public ConsolePanel getConsolePanel() {
		return consolePanel;
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		if (ae.getSource() == startButton) {
			PuyoPuyo.setIsStop(false);
			consolePanel.addConsole("スタート");
		} else if (ae.getSource() == stopButton) {
			PuyoPuyo.setIsStop(true);
			consolePanel.addConsole("ストップ");
		}

	}

}
