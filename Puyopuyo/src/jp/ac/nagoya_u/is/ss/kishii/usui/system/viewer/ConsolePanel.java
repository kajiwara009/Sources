package jp.ac.nagoya_u.is.ss.kishii.usui.system.viewer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;


public class ConsolePanel extends JPanel{
	JScrollPane scrollPane;
	private JTextArea logArea;
	public ConsolePanel(){
		setPreferredSize(new Dimension(BoardMaker.CELL_SIZE * 20, BoardMaker.CELL_SIZE * 2));
//		setBorder(BorderFactory.createLineBorder(Color.BLACK));
		logArea = new JTextArea() {
			@Override
			public void append(String str) {
				super.append(str);
				logArea.setCaretPosition(logArea.getDocument().getLength());
			}
		};
		logArea.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		logArea.setEditable(false);
		logArea.setFont(new Font("MS ゴシック", Font.PLAIN, 14));
		scrollPane = new JScrollPane(logArea);
		scrollPane.setPreferredSize(new Dimension(BoardMaker.CELL_SIZE * 20, BoardMaker.CELL_SIZE * 2));
		add(scrollPane);
		logArea.setText("ゲーム開始");
	}
	
	public void addConsole(String str){
		logArea.append("\n" + str);
	}

}
