package jp.halfmoon.inaba.aiwolf.guess;

import java.util.ArrayList;

import org.aiwolf.common.data.Role;
import org.aiwolf.common.net.GameInfo;

import jp.halfmoon.inaba.aiwolf.condition.AndCondition;
import jp.halfmoon.inaba.aiwolf.condition.RoleCondition;
import jp.halfmoon.inaba.aiwolf.lib.DayInfo;
import jp.halfmoon.inaba.aiwolf.lib.GuardRecent;


/**
 * �����u��q�����v�N���X
 */
public final class FromGuardRecent extends AbstractGuessStrategy {

	@Override
	public ArrayList<Guess> getGuessList(GuessStrategyArgs args) {
		// �������X�g
		ArrayList<Guess> guesses = new ArrayList<Guess>();

		GameInfo gameInfo = args.agi.latestGameInfo;

		// ���̂Ȃ��̓����`�F�b�N
		for( DayInfo dayInfo : args.agi.dayInfoList ){
			if( dayInfo.day >= 2 && dayInfo.attackAgentNo == null ){

				// �����̌�q�ʒu����̐���
				if( gameInfo.getRole() == Role.BODYGUARD ){

					// GJ����擾
					Integer guardedAgentNo = args.agi.selfGuardRecent.get(dayInfo.day - 1);

					// ��������q��ݒ肵�Ă�����
					if( guardedAgentNo != null ){
						// GJ�悪�T�̉\����������i�T�̃P�[�X�͏P���Ȃ���݂��Ɣ�����ꍇ�̂݁j
						Guess guess = new Guess();
						guess.condition = RoleCondition.getRoleCondition( guardedAgentNo, Role.WEREWOLF );
						guess.correlation = 0.30;
						guesses.add(guess);
					}

				}


				// ��q�����̑���
				for( GuardRecent guardRecent : args.agi.getGuardRecentList() ){
					if( guardRecent.agentNo != gameInfo.getAgent().getAgentIdx() &&
					    guardRecent.execDay == dayInfo.day - 1 &&
					    guardRecent.isEnable() ){

						// ��l���^�̏ꍇ�AGJ�悪�T�̉\����������i�T�̃P�[�X�͏P���Ȃ���݂��Ɣ�����ꍇ�̂݁j
						RoleCondition bodyguardNotWolf = RoleCondition.getNotRoleCondition( guardRecent.agentNo, Role.WEREWOLF );
						RoleCondition bodyguardNotPos = RoleCondition.getNotRoleCondition( guardRecent.agentNo, Role.POSSESSED );
						RoleCondition guardedWolf = RoleCondition.getRoleCondition( guardRecent.targetAgentNo, Role.WEREWOLF );

						Guess guess = new Guess();
						guess.condition = new AndCondition().addCondition(bodyguardNotWolf).addCondition(bodyguardNotPos).addCondition(guardedWolf);
						guess.correlation = 0.30;
						guesses.add(guess);
					}
				}


			}
		}



		// �������X�g��Ԃ�
		return guesses;
	}

}
