package jp.halfmoon.inaba.aiwolf.guess;

import java.util.ArrayList;
import java.util.List;

import jp.halfmoon.inaba.aiwolf.lib.WolfsidePattern;
import jp.halfmoon.inaba.aiwolf.strategyplayer.ReceivedGuess;


/**
 * 狼陣営のパターン×推理　の検証結果を格納するクラス
 */
public final class InspectedWolfsidePattern {

	/** 狼陣営のパターン */
	public WolfsidePattern pattern;

	/** パターンの妥当度 */
	public double score;

	/** このパターンに関連する推理 */
	public List<ReceivedGuess> guesses = new ArrayList<ReceivedGuess>();


	/**
	 * コンストラクタ
	 * @param pattern 狼陣営のパターン
	 * @param score パターンの妥当度
	 */
	public InspectedWolfsidePattern(WolfsidePattern pattern, double score){
		this.pattern = pattern;
		this.score = score;
	}


	public String toString(){
		return pattern.toString() + String.format(" (Score:%.5f) ", score);
	}

}
