package jp.halfmoon.inaba.aiwolf.guess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

import jp.halfmoon.inaba.aiwolf.lib.WolfsidePattern;
import jp.halfmoon.inaba.aiwolf.strategyplayer.ReceivedGuess;


/**
 * �����̕��͌��ʂ�\���N���X
 */
public final class AnalysisOfGuess {


	/** �S�p�^�[���̕��͌��� */
	private List<InspectedWolfsidePattern> allPattern = new ArrayList<InspectedWolfsidePattern>();

	/** �G�[�W�F���g���̒P�̘T�v�f���͌��� */
	private List<InspectedWolfsidePattern> singleAgentWolfPattern = new ArrayList<InspectedWolfsidePattern>();

	/** �G�[�W�F���g���̒P�̋��l�v�f���͌��� */
	private List<InspectedWolfsidePattern> singleAgentPossessedPattern = new ArrayList<InspectedWolfsidePattern>();

	/** �G�[�W�F���g���̍ł��Ó��ȘT�p�^�[�� */
	private HashMap<Integer, InspectedWolfsidePattern> mostWolfPattern = new HashMap<Integer, InspectedWolfsidePattern>();

	/** �G�[�W�F���g���̍ł��Ó��ȋ��l�p�^�[�� */
	private HashMap<Integer, InspectedWolfsidePattern> mostPossessedPattern = new HashMap<Integer, InspectedWolfsidePattern>();

	/** ��̘T�w�c�p�^�[��(Null�΍�) */
	private static final InspectedWolfsidePattern emptyWolfsidePattern = new InspectedWolfsidePattern(new WolfsidePattern(new ArrayList<Integer>(), new ArrayList<Integer>()), 0.0);


	//TODO �������ł̓f�[�^�\���݂̂ɂ��āA�i�[�͏�ʃ��W���[���ł��ׂ��H�N���X�̃p�b�P�[�W�ړ�������(ReceivedGuess���z�Q��)
	/**
	 * �R���X�g���N�^
	 * @param patterns �T�w�c�̃p�^�[��
	 * @param guessmanager ��������
	 */
	public AnalysisOfGuess(int agentNum, LinkedHashSet<WolfsidePattern> patterns, GuessManager guessManager) {

		// �X�̃G�[�W�F���g�P�̂ŘT�W���E���l�W�������߂�
		double[] singleWolfScore = new double[agentNum + 1];
		double[] singlePossessedScore = new double[agentNum + 1];
		for(int i = 1; i <= agentNum; i++){
			// �W���̏�����
			singleWolfScore[i] = 1.0;
			singlePossessedScore[i] = 1.0;

			// �_�~�[�p�^�[���̍쐬
			ArrayList<Integer> singleAgent = new ArrayList<Integer>();
			singleAgent.add(i);
			ArrayList<Integer> blank = new ArrayList<Integer>();
			WolfsidePattern wolfPattern = new WolfsidePattern(singleAgent, blank);
			WolfsidePattern posPattern = new WolfsidePattern(blank, singleAgent);

			List<ReceivedGuess> singleGuesses = guessManager.getGuessForSingleAgent(i);
			// �����̑���
			for(ReceivedGuess rguess : singleGuesses ){
				// �����̓���p�^�[�����T�p�^�[���ƃ}�b�`���邩
				if( rguess.guess.condition.isValid(wolfPattern) ){
					singleWolfScore[i] *= Math.pow( rguess.guess.correlation, rguess.weight);
				}
				// �����̓���p�^�[�������l�p�^�[���ƃ}�b�`���邩
				if( rguess.guess.condition.isValid(posPattern) ){
					singlePossessedScore[i] *= Math.pow( rguess.guess.correlation, rguess.weight);
				}
			}

			// �P�̃p�^�[���Ƃ��ċL��
			InspectedWolfsidePattern inspectedWolfPattern = new InspectedWolfsidePattern(wolfPattern, singleWolfScore[i]);
			InspectedWolfsidePattern inspectedPosPattern = new InspectedWolfsidePattern(posPattern, singlePossessedScore[i]);
			inspectedWolfPattern.guesses = singleGuesses;
			inspectedPosPattern.guesses = singleGuesses;
			singleAgentWolfPattern.add( inspectedWolfPattern );
			singleAgentPossessedPattern.add( inspectedPosPattern );
		}

		// �T�w�c�p�^�[���̑���
		Iterator<WolfsidePattern> iter = patterns.iterator();
		while( iter.hasNext() ){
			WolfsidePattern pattern = iter.next();

			// ���̃p�^�[���Ɋ֘A���鐄��
			List<ReceivedGuess> guesses = new ArrayList<ReceivedGuess>();

			double score = 1.0;
			// �e�T�̒P�̌W���̌v�Z
			for( int wolfAgentNo : pattern.wolfAgentNo ){
				score *= singleWolfScore[wolfAgentNo];
			}
			// �e���l�̒P�̌W���̌v�Z
			for( int posAgentNo : pattern.possessedAgentNo ){
				score *= singlePossessedScore[posAgentNo];
			}
			// �����̑���
			for(ReceivedGuess rguess : guessManager.getGuessForMultiAgent() ){
				// �����̏������T�w�c�p�^�[���ƃ}�b�`���邩
				if( rguess.guess.condition.isValid(pattern) ){
					// �T�w�c�̃X�R�A��␳����
					score *= Math.pow( rguess.guess.correlation, rguess.weight);

					// ���̃p�^�[���Ɋ֘A���鐄�����L��
					guesses.add(rguess);
				}
			}
			// �T�w�c�p�^�[���ɑ΂��錟�،��ʂ��i�[
			InspectedWolfsidePattern inspectedPattern = new InspectedWolfsidePattern(pattern, score);
			inspectedPattern.guesses = guesses;

			addPattern(inspectedPattern);

			// �e�G�[�W�F���g�̘T/���l�Ƃ��čł��Ó��ȃp�^�[���𒀎��v�Z
			for( int wolfAgentNo : pattern.wolfAgentNo ){
				if( mostWolfPattern.get(wolfAgentNo) == null ){
					mostWolfPattern.put(wolfAgentNo, inspectedPattern);
				}else{
					if( score > mostWolfPattern.get(wolfAgentNo).score ){
						mostWolfPattern.put(wolfAgentNo, inspectedPattern);
					}
				}
			}
			for( int posAgentNo : pattern.possessedAgentNo ){
				if( mostPossessedPattern.get(posAgentNo) == null ){
					mostPossessedPattern.put(posAgentNo, inspectedPattern);
				}else{
					if( score > mostPossessedPattern.get(posAgentNo).score ){
						mostPossessedPattern.put(posAgentNo, inspectedPattern);
					}
				}
			}

		}


	}


	/**
	 * ���،��ʂ̋L��
	 * @param pattern
	 */
	public void addPattern(InspectedWolfsidePattern pattern){

		// ���،��ʂ̋L��
		allPattern.add(pattern);

	}


	/**
	 * ����̘T�w�c�Ɋւ��镪�͌��ʂ��擾����
	 * @return �w�肵���T�w�c�Ɋւ��镪�͌���
	 */
	public InspectedWolfsidePattern getPattern(WolfsidePattern pattern){

		// �T�w�c�p�^�[���̑���
		for( InspectedWolfsidePattern workpattern : allPattern ){
			// �����񉻂��ē��ꂩ�`�F�b�N
			if( pattern.toString().equals(workpattern.pattern.toString()) ){
				// �w�肳�ꂽ����Ɠ����ł���΁A���͌��ʂ�Ԃ�
				return workpattern;
			}
		}

		// ������Ȃ������ꍇ
		return null;

	}


	//TODO ���\�b�h�ɂ���ă_�~�[��Ԃ��d�l��Null��Ԃ��d�l�����݂��ĂċC���������B���ꂷ��H
	/**
	 * �ł��Ó��ȘT�w�c�Ɋւ��镪�͌��ʂ��擾����
	 * @return �ł��Ó��ȘT�w�c�Ɋւ��镪�͌���(����X�R�A������ꍇ�A���Ԃ͕ۏ؂���Ȃ�)
	 */
	public InspectedWolfsidePattern getMostValidPattern(){

		InspectedWolfsidePattern mostValidWolfsidePattern = emptyWolfsidePattern;
		double mostValidWolfScore = Double.NEGATIVE_INFINITY;

		// �T�w�c�p�^�[���̑���
		for( InspectedWolfsidePattern pattern : mostWolfPattern.values() ){
			// �ő�X�R�A�ł���ΐw�c���L������
			if( pattern.score > mostValidWolfScore ){
				mostValidWolfsidePattern = pattern;
				mostValidWolfScore = pattern.score;
			}
		}

		return mostValidWolfsidePattern;

	}


	/**
	 * ����̃G�[�W�F���g���T�̃p�^�[���̂����A�ł��Ó��ȘT�w�c�Ɋւ��镪�͌��ʂ��擾����
	 * @param agentNo �G�[�W�F���g�ԍ�
	 * @return �ł��Ó��ȘT�w�c�Ɋւ��镪�͌���(����X�R�A������ꍇ�A�ł���ɓo�^���ꂽ����)
	 */
	public InspectedWolfsidePattern getMostValidWolfPattern(int agentNo){

		return mostWolfPattern.get(agentNo);

	}


	/**
	 * ����̃G�[�W�F���g�����l�̃p�^�[���̂����A�ł��Ó��ȘT�w�c�Ɋւ��镪�͌��ʂ��擾����
	 * @param agentNo �G�[�W�F���g�ԍ�
	 * @return �ł��Ó��ȘT�w�c�Ɋւ��镪�͌���(����X�R�A������ꍇ�A�ł���ɓo�^���ꂽ����)
	 */
	public InspectedWolfsidePattern getMostValidPossessedPattern(int agentNo){

		return mostPossessedPattern.get(agentNo);

	}


	/**
	 * �G�[�W�F���g�P�̂Ɋւ���l�T�̕��͌��ʂ��擾����
	 * @param agentNo �G�[�W�F���g�ԍ�
	 * @return
	 */
	public InspectedWolfsidePattern getSingleWolfPattern(int agentNo){

		return singleAgentPossessedPattern.get(agentNo - 1);

	}


	/**
	 * �G�[�W�F���g�P�̂Ɋւ��鋶�l�̕��͌��ʂ��擾����
	 * @param agentNo �G�[�W�F���g�ԍ�
	 * @return
	 */
	public InspectedWolfsidePattern getSinglePossessedPattern(int agentNo){

		return singleAgentPossessedPattern.get(agentNo - 1);

	}


}
