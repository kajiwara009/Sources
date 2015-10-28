package org.aiwolf.iace10442.lib;


import java.util.*;

import org.aiwolf.client.lib.Utterance;
//import org.aiwolf.common.*;
import org.aiwolf.common.data.*;
import org.aiwolf.common.net.*;

// 
// �f�p(���p)�ɑ��v���C���[�̔��i�l�ԃT�C�h�j�ƍ��i�l�T�T�C�h�j�x�������v�Z����
// 
// Execute�Ől�T�l���㉺

public class NaivePlayerEvaluator{
	
	// ������ID�A��E
	private int player_ID;
	private Role player_role;
	
	// �������m���Ă������
	//private double info_volume;
	
	// �֌W���̃l�b�g���[�N
	private ArrayList<RelationEdge> edges = new ArrayList<RelationEdge>();
	private ArrayList<ContradictionDetector> contra_detectors = new ArrayList<ContradictionDetector>();
	
	
	// �e�v���C���[�̔��x����
	// �e�v���C���[�̍��x����
	private ArrayList< Double > white_level = new ArrayList<Double>();
	private ArrayList< Double > black_level = new ArrayList<Double>();
	
	
	// �G�b�W��
	
	
	// ���\�b�h
	// �g�[�N���e�ɂ��X�V
	public void addTalk( Agent src, Utterance utterance )
	{
		int srcID = src.getAgentIdx();
		
		RelationEdge e = getEdge( src, utterance );
		if( edges.contains(e) ) {
			return ;
		}
		edges.add( e );
		
		Contradiction contra = contra_detectors.get(srcID).addTalk( src, utterance );
		detectTalkContradiction( src, contra );
	}
	
	// ���܂�ɂ��X�V
	public void addAttacked( Agent attacked ) {
		for( int i=0; i< contra_detectors.size(); i++ ) {
			Contradiction contra = contra_detectors.get(i).addAttacked(attacked);
			detectAttackedContradiction(i, contra);
		}
		RelationEdge e = new RelationEdge(
				edges.size(), 
				attacked.getAgentIdx(), 
				attacked.getAgentIdx(), 
				RelationType.ATTACKED );
		edges.add(e);
	}
	
	
	protected void detectTalkContradiction( Agent src, Contradiction contra ) {
		int targID = src.getAgentIdx();
		double prev_white = white_level.get(targID);
		double prev_black = black_level.get(targID);
		
		switch( contra ) {
		case GOOD :
			white_level.set(targID, prev_white +10.0 );
			break;
		case BAD :
			black_level.set(targID, prev_black +100.0 );
			break;
		case NEW :
			break;
		default :
			break;
		}
	}
	
	protected void detectAttackedContradiction( int targID, Contradiction contra ) {
		double prev_white = white_level.get(targID);
		double prev_black = black_level.get(targID);
		
		switch( contra ) {
		case GOOD :
			white_level.set(targID, prev_white +10.0 );
			break;
		case BAD :
			black_level.set(targID, prev_black +100.0 );
			break;
		case NEW :
			break;
		default :
			break;
		}
	}
	
	// ���ʂ̎擾
	
	// �{�d�l�ł͂Ȃ����� �e�v���C���[�̋^���x�����̎擾
	public double getWB( Agent agent ) {
		return (white_level.get(agent.getAgentIdx()) - black_level.get(agent.getAgentIdx()));
	}
	// �{�d�l�ł͂Ȃ�
	public void setBlack( Agent agent ) {
		black_level.set(agent.getAgentIdx(), 100.0);
	}
	// forceDivine
	
	
	
	
	protected void bonus( Agent targ, Utterance utterance ) {
	}
	
	// �R���X�g���N�^
	public NaivePlayerEvaluator( 
			GameSetting gamesetting, int player_ID_ , Role player_role_ ) 
	{
		for( int i=0; i< gamesetting.getPlayerNum() +1; i++ ) {
			contra_detectors.add( new ContradictionDetector(gamesetting) );
			white_level.add(0.0);
			black_level.add(0.0);
		}
		this.player_ID = player_ID_;
		this.player_role = player_role_;
		
		for( int i=0; i< contra_detectors.size(); i++ ) {
			contra_detectors.get(i).initializeSetting( this.player_ID, this.player_role );
		}
	}
	
	
	protected RelationEdge getEdge( Agent src, Utterance utterance )
	{
		int srcID = src.getAgentIdx();
		int dstID = srcID;
		RelationType type = RelationType.ESTIMATE;
		
		switch( utterance.getTopic() ) {
		case ESTIMATE :
			type = RelationType.ESTIMATE; dstID = utterance.getTarget().getAgentIdx(); break;
		case COMINGOUT:
			type = RelationType.COMINGOUT; dstID = srcID; break;
		//RESULT�n
		case DIVINED:
			type = RelationType.DIVINED; dstID = utterance.getTarget().getAgentIdx(); break;
		case INQUESTED:
			type = RelationType.INQUESTED; dstID = utterance.getTarget().getAgentIdx(); break;
		case GUARDED:
			break;

		//���[�n
		case ATTACK:
		case VOTE:
			break;
		default:
			break;
		}

		return new RelationEdge( edges.size(), srcID, dstID, type ); 
	}
}
