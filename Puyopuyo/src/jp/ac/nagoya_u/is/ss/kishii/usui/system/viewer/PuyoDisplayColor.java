package jp.ac.nagoya_u.is.ss.kishii.usui.system.viewer;

import jp.ac.nagoya_u.is.ss.kishii.usui.system.storage.PuyoType;



public class PuyoDisplayColor {

	public static int decideColor(PuyoType type) {
		if (type == null) {
			return 0;
		} else {
			switch (type) {
			case BLUE_PUYO:
				return 1;
			case RED_PUYO:
				return 2;
			case GREEN_PUYO:
				return 3;
			case YELLOW_PUYO:
				return 4;
			case PURPLE_PUYO:
				return 5;
			case OJAMA_PUYO:
				return 6;
			default :
				return 0;
			}
		}
	}

	public static int decideColorTop(PuyoType type) {
		if (type == null) {
			return 0;
		} else {
			return decideColor(type);
		}
	}

	public static int decideNoticeColor(NoticePuyo noticePuyo) {
		switch (noticePuyo) {
		case CROWN:
			return 12;
		case MOON:
			return 11;
		case STAR:
			return 10;
		case ROCK:
			return 9;
		case BIG:
			return 8;
		case SMALL:
			return 7;
		default :
			return 0;
		}
	}
}
