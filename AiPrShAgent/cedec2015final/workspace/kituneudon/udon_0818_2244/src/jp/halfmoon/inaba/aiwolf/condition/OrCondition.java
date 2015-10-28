package jp.halfmoon.inaba.aiwolf.condition;

import java.util.ArrayList;
import java.util.List;

import jp.halfmoon.inaba.aiwolf.lib.WolfsidePattern;

/**
 * �����̏�����OR���肷������N���X(�w�������1�������ꍇ��True)
 */
public final class OrCondition extends AbstractCondition {

	/** �����̃��X�g */
	private List<AbstractCondition> conditions = new ArrayList<AbstractCondition>();


	@Override
	public boolean isValid( WolfsidePattern pattern ) {

		// �������P�������ꍇ�́A�����𖞂����������ɂ���
		if( conditions.isEmpty() ){
			return true;
		}

		// �������P���`�F�b�N���A�P�ł������΁@OR�����𖞂���
		for( AbstractCondition condition : conditions ){
			if( condition.isValid(pattern) ){
				return true;
			}
		}

		// �����𖞂������̂��������OR�����𖞂����Ȃ�
		return false;

	}

	/**
	 * ������ǉ�����(chainable)
	 * @param condition �ǉ��������
	 * @return ���g�̃I�u�W�F�N�g
	 */
	public OrCondition addCondition( AbstractCondition condition ){

		conditions.add(condition);

		return this;

	}


	@Override
	public ArrayList<Integer> getTargetAgentNo() {

		// �G�[�W�F���g�ԍ��̃��X�g
		ArrayList<Integer> ret = new ArrayList<Integer>();

		// �q�����𑖍�
		for( AbstractCondition condition : conditions ){
			// �q��������G�[�W�F���g�ԍ��̃��X�g���擾
			ArrayList<Integer> subret = condition.getTargetAgentNo();
			// �擾�����G�[�W�F���g�ԍ����A�d����p���ă��X�g�ɒǉ�����
			for( Integer agentno : subret ){
				if( !ret.contains(agentno) ){
					ret.add(agentno);
				}
			}
		}

		return ret;
	}


}
