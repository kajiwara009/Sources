package jp.halfmoon.inaba.aiwolf.condition;

import java.util.ArrayList;

import jp.halfmoon.inaba.aiwolf.lib.WolfsidePattern;

import org.aiwolf.common.data.Role;


/**
 * ��E�̏�����\���N���X
 */
public final class RoleCondition extends AbstractCondition {


	/** �G�[�W�F���g�ԍ� */
	public int agentNo;

	/** ��E */
	public Role role;

	/** �w�肵����E�ȊO�ł��邱�Ƃ�\��  */
	public boolean isRoleBesides = false;


	// ���L���b�V���p

	public static final int AGENT_MAX = 15;

	private static RoleCondition wolfCondition[] = new RoleCondition[AGENT_MAX + 1];
	private static RoleCondition possessedCondition[] = new RoleCondition[AGENT_MAX + 1];
	private static RoleCondition notWolfCondition[] = new RoleCondition[AGENT_MAX + 1];
	private static RoleCondition notPossessedCondition[] = new RoleCondition[AGENT_MAX + 1];

	// ���L���b�V���p


	private RoleCondition( int agentno, Role role ) {
		this.agentNo = agentno;
		this.role = role;
	}


	private RoleCondition( int agentno, Role role, boolean isRoleBesides) {
		this.agentNo = agentno;
		this.role = role;
		this.isRoleBesides = isRoleBesides;
	}


	@Override
	public boolean isValid( WolfsidePattern pattern ) {

		// �w�肵����E�ւ̃}�b�`��
		if( isRoleBesides == false ){
			// �w�肵����E�Ƀ}�b�`����
			switch( role ){
				case WEREWOLF:
					if( pattern.isWolf(agentNo) ){
						return true;
					}
					break;
				case POSSESSED:
					if( pattern.isPossessed(agentNo) ){
						return true;
					}
					break;
				default:
					//TODO ���̑���E�͖��Ή�
					// �T�w�c�Ɋ܂܂�邩�{�e�T�w�c���ɓ��󂪑��݂��邩�H
					break;
			}
		}else{
			// �w�肵����E�ȊO�Ƀ}�b�`����

			switch( role ){
				case WEREWOLF:
					if( !pattern.isWolf(agentNo) ){
						return true;
					}
					break;
				case POSSESSED:
					if( !pattern.isPossessed(agentNo) ){
						return true;
					}
					break;
				default:
					//TODO ���̑���E�͖��Ή�
					// �T�w�c�Ɋ܂܂�邩�{�e�T�w�c���ɓ��󂪑��݂��邩�H
					break;
			}
		}

		return false;
	}


	@Override
	public ArrayList<Integer> getTargetAgentNo() {

		// �G�[�W�F���g�ԍ������X�g�ɂ��ĕԂ�
		ArrayList<Integer> ret = new ArrayList<Integer>();
		ret.add(agentNo);

		return ret;

	}


	/**
	 * �u����̖�E�ł���v�������擾����
	 * @param agentNo
	 * @param role
	 * @return
	 */
	public static RoleCondition getRoleCondition( int agentNo, Role role ){

		if( agentNo < 1 || agentNo > AGENT_MAX ){
			return new RoleCondition(agentNo, role);
		}

		switch( role ){
			case WEREWOLF:
				if( wolfCondition[agentNo] == null ){
					wolfCondition[agentNo] = new RoleCondition(agentNo, role);
				}
				return wolfCondition[agentNo];
			case POSSESSED:
				if( possessedCondition[agentNo] == null ){
					possessedCondition[agentNo] = new RoleCondition(agentNo, role);
				}
				return possessedCondition[agentNo];
			default:
				break;
		}

		return new RoleCondition(agentNo, role);

	}


	/**
	 * �u����̖�E�łȂ��v�������擾����
	 * @param agentNo
	 * @param role
	 * @return
	 */
	public static RoleCondition getNotRoleCondition( int agentNo, Role role ){

		if( agentNo < 1 || agentNo > AGENT_MAX ){
			return new RoleCondition(agentNo, role, true);
		}

		switch( role ){
			case WEREWOLF:
				if( notWolfCondition[agentNo] == null ){
					notWolfCondition[agentNo] = new RoleCondition(agentNo, role, true);
				}
				return notWolfCondition[agentNo];
			case POSSESSED:
				if( notPossessedCondition[agentNo] == null ){
					notPossessedCondition[agentNo] = new RoleCondition(agentNo, role, true);
				}
				return notPossessedCondition[agentNo];
			default:
				break;
		}

		return new RoleCondition(agentNo, role, true);

	}

}
