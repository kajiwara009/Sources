package jp.ac.nagoya_u.is.ss.kishii.usui.system.viewer;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JPanel;


import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.Field;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.PlayerInfo;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.Puyo;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.Puyo.PuyoNumber;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.storage.PuyoType;



public class GridMaker extends JPanel{
	CardLayout layout;
	JPanel[][] blockArray;

	private final int MAX_HEIGHT;
	private final int MAX_WIDTH;


	public GridMaker(int maxHeight, int maxWidth,PlayerInfo playerInfo, String directory) {
			MAX_HEIGHT = maxHeight;
			MAX_WIDTH = maxWidth;


		setLayout(new GridLayout(maxHeight, maxWidth));

		setOpaque(false);
		setPreferredSize(new Dimension((BoardMaker.CELL_SIZE) * (maxWidth),
				(BoardMaker.CELL_SIZE) * maxHeight));




		blockArray = new JPanel[maxHeight][maxWidth];
		layout = new CardLayout();
		for (int i = 0; i < maxHeight; i++) {
			for (int j = 0; j < maxWidth; j++) {
				blockArray[i][j] = new JPanel();
				blockArray[i][j].setLayout(layout);
				blockArray[i][j].setSize(BoardMaker.CELL_SIZE, BoardMaker.CELL_SIZE);
				blockArray[i][j].setBorder(BorderFactory
						.createLineBorder(Color.BLACK));
				if(i == 12){
					blockArray[i][j].setBackground(Color.BLACK);
				}

				blockArray[i][j].setOpaque(false);


				MakeCardPanel mcp = new MakeCardPanel(playerInfo, directory);

				for(int card = 0; card < 13; card++){
					blockArray[i][j].add(mcp.getColorPanel()[card], "color" + card);
				}
//				layout.show(blockArray[i][j], "color2");


				add(blockArray[i][j]);
			}

		}
	}


	public void puyoColor(int color,JPanel block){
		layout.first(block);
		for(int i = 0;i < color; i++){
			layout.next(block);
		}
	}

	public void partRefresh(PuyoType puyoColor,int height,int width){
		if (height < MAX_HEIGHT) {
			puyoColor(PuyoDisplayColor.decideColor(puyoColor), blockArray[MAX_HEIGHT - 1 - height][width]);
		}
	}

	public void lineRefresh(Field field,int width){
		for (int i = 0; i < MAX_HEIGHT; i++) {
			puyoColor(PuyoDisplayColor.decideColor(field.getPuyoType(width, i)), blockArray[MAX_HEIGHT - 1 - i][width]);
		}
	}

	public void putNext(Puyo nextPuyo) {
		partRefresh(nextPuyo.getPuyoType(PuyoNumber.SECOND),0,1);
		partRefresh(nextPuyo.getPuyoType(PuyoNumber.FIRST),1,1);
	}

	public void putNextNext(Puyo nextPuyo) {
		partRefresh(nextPuyo.getPuyoType(PuyoNumber.SECOND),0,2);
		partRefresh(nextPuyo.getPuyoType(PuyoNumber.FIRST),1,2);
	}

	public void fieldRefresh(Field field) {
		for (int i = 0; i < MAX_HEIGHT; i++) {
			for (int j = 0; j < MAX_WIDTH; j++) {
				puyoColor(PuyoDisplayColor.decideColor(field.getPuyoType(j, i)), blockArray[MAX_HEIGHT - 1 - i][j]);
			}
		}
	}

	public void ojamaDisplay(List<Integer> numbersOfOjamaList) {
		int displayNumberOfOjama = 0;

		for (int i = blockArray.length - 1; i > 0; i--) {
			int listIndex = blockArray.length - i - 1;
			int numberOfOjama = numbersOfOjamaList.get(listIndex);
			displayNumberOfOjama += numberOfOjama;
			for (int j = 0; j < MAX_WIDTH; j++) {
				layout.first(blockArray[i][j]);
				for (NoticePuyo noticePuyo : NoticePuyo.values()) {
					if (numberOfOjama >= noticePuyo.getOjamaNumber()) {
						puyoColor(PuyoDisplayColor.decideNoticeColor(noticePuyo),blockArray[i][j]);
						numberOfOjama -= noticePuyo.getOjamaNumber();

						break;
					}
				}
			}
		}

		int totalNumberOfOjama = 0;
		for (int numberOfOjama : numbersOfOjamaList) {
			totalNumberOfOjama += numberOfOjama;
		}

		int restNumberOfOjama = totalNumberOfOjama - displayNumberOfOjama;

		for (int j = 0; j < MAX_WIDTH; j++) {
			layout.first(blockArray[0][j]);
			for (NoticePuyo noticePuyo : NoticePuyo.values()) {
				if (restNumberOfOjama >= noticePuyo.getOjamaNumber()) {
					puyoColor(PuyoDisplayColor.decideNoticeColor(noticePuyo),blockArray[0][j]);
					restNumberOfOjama -= noticePuyo.getOjamaNumber();

					break;
				}
			}
		}
	}

	public void deadEffect(Field field){
		
		for(int height = MAX_HEIGHT-1; height >= 0 ; height--){
			for(int width = 0; width < MAX_WIDTH; width++){
				puyoColor(PuyoDisplayColor.decideColor(field.getPuyoType(height+1, width)), blockArray[height][width]);
			}
		}
		
//		for(int height = MAX_HEIGHT-1; height >= 0 ; height--){
//			for(int width = 0; width < MAX_WIDTH; width++){
//				puyoColor(0, blockArray[height][width]);
//			}
//		}
	}


}
