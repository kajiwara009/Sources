package jp.halfmoon.inaba.aiwolf.guess;

import java.util.ArrayList;
import java.util.List;

import jp.halfmoon.inaba.aiwolf.lib.WolfsidePattern;
import jp.halfmoon.inaba.aiwolf.strategyplayer.ReceivedGuess;


/**
 * �T�w�c�̃p�^�[���~�����@�̌��،��ʂ��i�[����N���X
 */
public final class InspectedWolfsidePattern {

	/** �T�w�c�̃p�^�[�� */
	public WolfsidePattern pattern;

	/** �p�^�[���̑Ó��x */
	public double score;

	/** ���̃p�^�[���Ɋ֘A���鐄�� */
	public List<ReceivedGuess> guesses = new ArrayList<ReceivedGuess>();


	/**
	 * �R���X�g���N�^
	 * @param pattern �T�w�c�̃p�^�[��
	 * @param score �p�^�[���̑Ó��x
	 */
	public InspectedWolfsidePattern(WolfsidePattern pattern, double score){
		this.pattern = pattern;
		this.score = score;
	}


	public String toString(){
		return pattern.toString() + String.format(" (Score:%.5f) ", score);
	}

}
