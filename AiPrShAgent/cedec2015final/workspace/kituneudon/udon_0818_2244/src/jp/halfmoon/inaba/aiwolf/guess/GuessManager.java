package jp.halfmoon.inaba.aiwolf.guess;

import java.util.ArrayList;
import java.util.List;

import jp.halfmoon.inaba.aiwolf.strategyplayer.ReceivedGuess;

public final class GuessManager {



	/** 全ての推理 */
	private List<ReceivedGuess> allGuess = new ArrayList<ReceivedGuess>();

	/** 各エージェント単体に対する推理 */
	private List<List<ReceivedGuess>> guessForSingleAgent = new ArrayList<List<ReceivedGuess>>();

	/** 複数エージェントの組み合わせに対する推理 */
	private List<ReceivedGuess> guessForMultiAgent = new ArrayList<ReceivedGuess>();


	/**
	 * コンストラクタ
	 * @param agentNum エージェント数
	 */
	public GuessManager(int agentNum){

		for( int i = 0; i <= agentNum; i++ ){
			guessForSingleAgent.add(new ArrayList<ReceivedGuess>());
		}

	}

	/**
	 * 推理を追加
	 * @param guess 追加する推理
	 */
	public void addGuess(ReceivedGuess guess){

		// 全ての推理として追加
		allGuess.add(guess);

		// パターン対象のエージェント番号一覧を取得する
		ArrayList<Integer> TargetAgents = guess.guess.condition.getTargetAgentNo();

		// 前提・現象のエージェント数合計が１以下か
		if( TargetAgents.size()  <= 1 ){
			// 単独エージェントに対する推理として追加
			int agentno = TargetAgents.get(0);
			guessForSingleAgent.get(agentno).add(guess);
		}else{
			// 複数エージェントの組み合わせに対する推理として追加
			guessForMultiAgent.add(guess);
		}

	}

	/**
	 * 推理を追加
	 * @param guesses 追加する推理のリスト
	 */
	public void addGuess(List<ReceivedGuess> guesses){

		// 単体追加メソッドを使い、１個ずつ追加
		for(ReceivedGuess guess: guesses){
			addGuess(guess);
		}

	}


	/**
	 * エージェント単体に対する推理を取得
	 * @param agentno エージェント番号
	 * @return
	 */
	public List<ReceivedGuess> getGuessForSingleAgent(int agentno){
		return guessForSingleAgent.get(agentno);
	}


	/**
	 * 複数エージェントに対する推理を取得
	 * @return
	 */
	public List<ReceivedGuess> getGuessForMultiAgent(){
		return guessForMultiAgent;
	}



}
