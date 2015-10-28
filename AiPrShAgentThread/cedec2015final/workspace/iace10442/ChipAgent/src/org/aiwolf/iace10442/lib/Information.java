package org.aiwolf.iace10442.lib;


import java.util.*;

import org.aiwolf.client.lib.*;
//import org.aiwolf.common.*;
import org.aiwolf.common.data.*;
import org.aiwolf.common.net.*;

//
// �v��������e�L�g�[�Ƀ��^���C����ď��ʂƂ����Z�o����
//

public class Information {
	
	private class PlayerData {
		// �e�v���C���[�̐M���x
		public double reliability;
		// �e�v���C���[�̔��b��
		public int remark_num;
	 	// �e�v���C���[�̎��̖�E
		public Role self_role;
		// �����Ă��邩
		public boolean is_living;
		
		/*
		public PlayerData(double reliability, int remark_num, Role self_role) {
			super();
			this.reliability = reliability;
			this.remark_num = remark_num;
			this.self_role = self_role;
			this.is_living = true;
		}
		*/
		public PlayerData() {
			super();
			this.reliability = 0.0;
			this.remark_num = 0;
			this.self_role = null;
			this.is_living = true;
		}
	}
	
	// �e�v���C���[�̃f�[�^
	private ArrayList<PlayerData> players = new ArrayList<PlayerData>();
	
	// �M���x�v�Z�̗�O�I�⏕��
	private ArrayList<ContradictionDetector> contra_detectors = new ArrayList<ContradictionDetector>();
	
	// �����̃��O
	private HashSet<RelationEdge> talk_log = new HashSet<RelationEdge>();
	
	
	
	// ���\�b�h
	// �g�[�N���e�ɂ��X�V
	public void addTalk( Agent src, Utterance utterance )
	{
		int srcID = src.getAgentIdx();
		
		{
			RelationEdge e = getEdge( src, utterance );
			if( talk_log.contains(e) ) {
				// ���o�Ȃ�Ԃ�
				return ;
			}
			talk_log.add( e );
		}
			
		// ���b���̒ǉ�
		players.get(srcID).remark_num ++;
		
		// ����̔��b���e�Ɋւ��Ă̏���
		switch( utterance.getTopic() ) {
		case COMINGOUT :
			if( players.get(srcID).self_role == null ) {
				players.get(srcID).reliability += 5.0;
				players.get(srcID).self_role = utterance.getRole();
			}
			break;
		case DIVINED :
			if( utterance.getResult() == Species.WEREWOLF ) {
				players.get(utterance.getTarget().getAgentIdx()).reliability -= 5.0;
			}
			if( players.get(srcID).self_role == null ) {
				players.get(srcID).self_role = Role.SEER;
				players.get(srcID).reliability += 5.0;
			}
			break;
		case INQUESTED : // ���l�Ȃ��Ƃ���
			if( players.get(srcID).self_role == null ) {
				players.get(srcID).self_role = Role.MEDIUM;
				players.get(srcID).reliability += 5.0;
			}
			break;
		case GUARDED : // ���l�Ȃ��Ƃ���
			if( players.get(srcID).self_role == null ) {
				players.get(srcID).self_role = Role.BODYGUARD;
				players.get(srcID).reliability += 5.0;
			}
			break;
		default :
			break;
		}
		
		// ��O�I�Ȗ������o
		Contradiction contra = contra_detectors.get(srcID).addTalk( src, utterance );
		if( contra == Contradiction.BAD ) {
			players.get(srcID).reliability -= 10.0;
		}
	}
	
	// ���܂�ɂ��X�V
	public void addAttacked( Agent attacked ) {
		if( attacked == null ) return ;
		for( int i=0; i< contra_detectors.size(); i++ ) {
			Contradiction contra = contra_detectors.get(i).addAttacked(attacked);
			if( contra == Contradiction.BAD ) {
				players.get(i).reliability -= 10.0;
			}
		}
	}
	
	// �Ǖ��ɂ��X�V
	public void addExecuted( Agent executed ) {
		if( executed == null ) return ;
		players.get(executed.getAgentIdx()).is_living = false;
	}
	
	// �肢�ɂ�鋭���X�V
	public void addDivine( Agent divined, Species result ) {
		if( divined == null ) return ;
		for( int i=0; i< contra_detectors.size(); i++ ) {
			Contradiction contra = contra_detectors.get(i).addDivine(divined,result);
			if( (contra == Contradiction.BAD) && 
				(players.get(i).reliability > -10.0) ) 
			{
				players.get(i).reliability -= 10.0;
			}
		}
	}
	// ��}�ɂ�鋭���X�V
	public void addInquest( Agent inquested, Species result ) {
		if( inquested == null ) return ;
		for( int i=0; i< contra_detectors.size(); i++ ) {
			Contradiction contra = contra_detectors.get(i).addInquested(inquested,result);
			if( (contra == Contradiction.BAD) && 
				(players.get(i).reliability > -10.0) ) 
			{
				players.get(i).reliability -= 10.0;
			}
		}
	}
	
	
	
	// ���݂̏��ʂ�Ԃ�
	public double getVolume() {
		double sum = 0.0;
		for( int i=0; i< players.size(); i++ ) {
			if( players.get(i).is_living == false ) continue; 
			sum += players.get(i).reliability * (double)players.get(i).remark_num;
		}
		return sum;
	}
	
	// �v���C���[�̒Ǖ��ɐ��������ꍇ�̗\�z����鎟�̏�Ԃ̏��ʂ�Ԃ�
	public double getSimExecute( Agent execute ) {
		double sum = 0.0;
		for( int i=0; i< players.size(); i++ ) {
			if( players.get(i).is_living == false ) continue;
			if( i == execute.getAgentIdx() ) continue; 
			
			int expect_remark_num = players.get(i).remark_num +1; // �W���ŉ�������1�͔�������Ƃ���
			if( players.get(i).self_role != null ) {
				if( players.get(i).self_role == Role.SEER ) expect_remark_num += 2; // ��̃o�C�A�X��������
				if( players.get(i).self_role == Role.MEDIUM ) expect_remark_num += 1; // ��̃o�C�A�X��������
			}
			sum += players.get(i).reliability * (double)expect_remark_num;
		}
		return sum;
	}
	
	// �肢�����A����𔭌������ꍇ�̗\�z����鎟�̏�Ԃ̏��ʂ�Ԃ�
	public double getSimDivine( Agent devine ) {
		// �ۗ�
		return 0.0;
	}
	
	// �P���ɐ��������ꍇ�̗\�z����鎟�̏�Ԃ̏��ʂ�Ԃ�
	public double getSimAttacked( Agent attack ) {
		double sum = 0.0;
		for( int i=0; i< players.size(); i++ ) {
			if( players.get(i).is_living == false ) continue;
			if( i == attack.getAgentIdx() ) continue; 
			
			int expect_remark_num = players.get(i).remark_num +1; // �W���ŉ�������1�͔�������Ƃ���
			double werewolf_reliability = Double.max(players.get(i).reliability, 0.0);
			sum += werewolf_reliability * (double)expect_remark_num;
		}
		return sum;
	}
	
	// �R���X�g���N�^
	public Information( 
			GameSetting gamesetting , int player_ID, Role player_role ) 
	{
		for( int i=0; i< gamesetting.getPlayerNum() +1; i++ ) {
			players.add( new PlayerData() );
			contra_detectors.add( new ContradictionDetector(gamesetting) );
		}
		
		for( int i=0; i< contra_detectors.size(); i++ ) {
			contra_detectors.get(i).initializeSetting( player_ID, player_role );
		}
	}
	
	
	
	
	// �G�b�W�ϊ�
	protected RelationEdge getEdge( Agent src, Utterance utterance )
	{
		int srcID = src.getAgentIdx();
		int dstID = srcID;
		RelationType type = RelationType.DONTCARE;
		
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
			type = RelationType.VOTED; dstID = utterance.getTarget().getAgentIdx(); break;
		default :
			break;
		}
		
		return new RelationEdge( talk_log.size(), srcID, dstID, type ); 
	}
	
}
