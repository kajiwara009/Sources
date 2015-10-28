package jp.ac.nagoya_u.is.ss.mase.usui.manipulater;

import java.awt.CardLayout;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JPanel;


import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.Action;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.Field;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.PlayerInfo;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.Puyo;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.Puyo.PuyoDirection;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.Puyo.PuyoNumber;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.storage.PuyoType;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.viewer.*;


public class GridMaker extends JPanel{
	CardLayout layout;
	JPanel[][] blockArray;

	private final int MAX_HEIGHT;
	private final int MAX_WIDTH;
	private final int CELL_SIZE = 35;


	public GridMaker(int maxHeight, int maxWidth) {
			MAX_HEIGHT = maxHeight;
			MAX_WIDTH = maxWidth;


		setLayout(new GridLayout(maxHeight, maxWidth));

		setOpaque(true);
		setPreferredSize(new Dimension((CELL_SIZE) * (maxWidth),
				(CELL_SIZE) * maxHeight));
		setBackground(Color.WHITE);




		blockArray = new JPanel[maxHeight][maxWidth];
		layout = new CardLayout();
		for (int i = 0; i < maxHeight; i++) {
			for (int j = 0; j < maxWidth; j++) {
				blockArray[i][j] = new JPanel();
				blockArray[i][j].setLayout(layout);
				blockArray[i][j].setSize(CELL_SIZE, CELL_SIZE);
				blockArray[i][j].setBorder(BorderFactory
						.createLineBorder(Color.BLACK));
				if(i == 12){
					blockArray[i][j].setBackground(Color.BLACK);
				}

				blockArray[i][j].setOpaque(false);


				MakeCardPanel mcp = new MakeCardPanel();

				for(int card = 0; card < 6; card++){
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
	
	public void reset(){
		for(int height = 1; height < MAX_HEIGHT-1 ;height++){
			for(int width = 0; width < MAX_WIDTH;width++){
				layout.first(blockArray[height][width]);
			}
		}
	}
	
	public void puyo(Puyo currentPuyo, Action action){
		PuyoType firstPuyoType = currentPuyo.getPuyoType(PuyoNumber.FIRST);
		PuyoType secondPuyoType = currentPuyo.getPuyoType(PuyoNumber.SECOND);
		
		if(action.getDirection() == PuyoDirection.UP){
			partRefresh(firstPuyoType,1,action.getColmNumber());
			partRefresh(secondPuyoType,2,action.getColmNumber());
		}else if(action.getDirection() == PuyoDirection.DOWN){
			partRefresh(firstPuyoType,2,action.getColmNumber());
			partRefresh(secondPuyoType,1,action.getColmNumber());
		}else if(action.getDirection() == PuyoDirection.LEFT){
			partRefresh(firstPuyoType,2,action.getColmNumber());
			partRefresh(secondPuyoType,2,action.getColmNumber()-1);
		}else if(action.getDirection() == PuyoDirection.RIGHT){
			partRefresh(firstPuyoType,2,action.getColmNumber());
			partRefresh(secondPuyoType,2,action.getColmNumber()+1);
		}
		
	}

}
