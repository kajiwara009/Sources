package jp.halfmoon.inaba.aiwolf.strategyplayer;

import java.util.ArrayList;
import java.util.List;

import jp.halfmoon.inaba.aiwolf.guess.AbstractGuessStrategy;
import jp.halfmoon.inaba.aiwolf.guess.Guess;

/**
 * 各推理戦術クラスから受け取った推理
 */
public final class ReceivedGuess {

	/** 推理の内容 */
	public Guess guess;

	/** 推理を行った戦術 */
	public AbstractGuessStrategy strategy;

	/** 推測の重み(係数^推測の重み) */
	public double weight = 1.0;


	public ReceivedGuess(Guess guess, AbstractGuessStrategy strategy){
		this.guess = guess;
		this.strategy = strategy;
	}


	public ReceivedGuess(Guess guess, AbstractGuessStrategy strategy, double weight){
		this.guess = guess;
		this.strategy = strategy;
		this.weight = weight;
	}

	public static List<ReceivedGuess> newGuesses(ArrayList<Guess> guesses, AbstractGuessStrategy strategy){

		ArrayList<ReceivedGuess> rguesses = new ArrayList<ReceivedGuess>();

		for(Guess guess: guesses){
			ReceivedGuess rguess = new ReceivedGuess(guess, strategy);
			rguesses.add(rguess);
		}

		return rguesses;

	}

	public static List<ReceivedGuess> newGuesses(ArrayList<Guess> guesses, AbstractGuessStrategy strategy, double weight){

		ArrayList<ReceivedGuess> rguesses = new ArrayList<ReceivedGuess>();

		for(Guess guess: guesses){
			ReceivedGuess rguess = new ReceivedGuess(guess, strategy, weight);
			rguesses.add(rguess);
		}

		return rguesses;

	}

}
