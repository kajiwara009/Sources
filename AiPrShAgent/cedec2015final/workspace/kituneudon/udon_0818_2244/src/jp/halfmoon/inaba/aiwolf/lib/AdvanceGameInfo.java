package jp.halfmoon.inaba.aiwolf.lib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.aiwolf.client.lib.Topic;
import org.aiwolf.client.lib.Utterance;
import org.aiwolf.client.lib.TemplateTalkFactory.TalkType;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Species;
import org.aiwolf.common.data.Talk;
import org.aiwolf.common.data.Vote;
import org.aiwolf.common.net.GameInfo;
import org.aiwolf.common.net.GameSetting;


/**
 * �Q�[�����
 */
public final class AdvanceGameInfo {


	// ---- �ȉ��A���n�̕ϐ� ----

	/** �Q�[���J�n���Ɏ󂯎����GameSetting */
	public GameSetting gameSetting;

	/** �ŐV��GameInfo */
	public GameInfo latestGameInfo;

	/** �������O�i�S�����j */
	private List<List<Talk>> talkLists = new ArrayList<List<Talk>>();

	/** ���[���O�i�S�����j */
	private List<List<Vote>> voteLists = new ArrayList<List<Vote>>();

	/** CO�̃��X�g(������E��CO) */
	public List<ComingOut> comingOutList = new ArrayList<ComingOut>();

	/** CO�̃��X�g(�l�O��E��CO) */
	public List<ComingOut> wolfsideComingOutList = new ArrayList<ComingOut>();

	/** �G�[�W�F���g�̏�� idx = AgentNo */
	public AgentState[] agentState;

	/** �����Ƃ̏��̃��X�g */
	public List<DayInfo> dayInfoList = new ArrayList<DayInfo>();

	/** �ߋ��ɉ�͂����������i�[����}�b�v(�������p) key=talkContent value=utterance */
	public HashMap<String, Utterance> analysedUtteranceMap = new HashMap<String, Utterance>(32);

	/** �x���E */
	public Role fakeRole;


	// ���_�K�w�}�i���X�̗\��B�^�C���I�[�o�[�΍�ł��Ȃ�팸�ρj
	// �S���_�iroot�j
	// �@�{���x�薳�����_
	// �@�@�{�ePL���_

	/** �S���_�̎��_���(�V�X�e���ɂ��m����̂�) */
	public ViewpointInfo allViewSystemInfo;

	/** �S���_�̎��_���(���ߑł����) */
	public ViewpointInfo allViewTrustInfo;

	/** �������_�̎��_���(�l���Ƃ̎��_���ւ̎Q��) */
	public ViewpointInfo selfViewInfo;

	/** �����̎��ۂ̖�E�̎��_���(���󋶐l�̂ݑΉ�) */
	public ViewpointInfo selfRealRoleViewInfo;


	/** �蔻��ꗗ */
	private List<Judge> seerJudgeList = new ArrayList<Judge>();

	/** �씻��ꗗ */
	private List<Judge> mediumJudgeList = new ArrayList<Judge>();

	/** ��q�����ꗗ */
	private List<GuardRecent> guardRecentList = new ArrayList<GuardRecent>();

	/** �����̔\�͂ɂ�锻��̃��X�g(��p) */
	public List<Judge> selfInspectList = new ArrayList<Judge>();

	/** �����̔\�͂ɂ�锻��̃��X�g(��p) */
	public List<Judge> selfInquestList = new ArrayList<Judge>();

	/** �����̌�q����(��p) key=���s������(����=1) value=�ΏۃG�[�W�F���g�ԍ� */
	public HashMap<Integer, Integer> selfGuardRecent = new HashMap<Integer, Integer>();

	/** �����̔\�͌��ʂ�񍐂�����(���� ���p) */
	public int reportSelfResultCount;

	// ---- �ȉ��A�����ƂɃ��Z�b�g�������n�̕ϐ� ----

	/** �{���̋^���σG�[�W�F���g�̃��X�g */
	public List<Integer> talkedSuspicionAgentList = new ArrayList<Integer>();

	/** �{���̐M�p�σG�[�W�F���g�̃��X�g */
	public List<Integer> talkedTrustAgentList = new ArrayList<Integer>();

	// ---- �ȉ��A����n�̕ϐ� ----

	/** ���t�X�V�ŌĂ΂ꂽUpdate()�̂Ƃ�True�ɂ��� */
	private boolean isDayUpdate;

	/** ��b���ǂ��܂œǂ񂾂�(����ǂݎn�߂锭��No) */
	private int readTalkListNum;


	/**
	 * �R���X�g���N�^
	 */
	public AdvanceGameInfo(GameInfo gameInfo, GameSetting gameSetting){

//		long starttime = System.currentTimeMillis();

		// �Q�[���ݒ�̏�����
		this.gameSetting = gameSetting;

		// �󂯎����gameInfo���ŐV�̂��̂Ƃ��ĕۊǂ���
		//latestGameInfo = gameInfo;

		// �G�[�W�F���g��Ԃ̏�����
		agentState = new AgentState[gameSetting.getPlayerNum() + 1];
		for( int i = 1; i <= gameSetting.getPlayerNum(); i++ ){
			agentState[i] = new AgentState(i);
		}

		// �S���_���̏�����(�V�X�e���ɂ��m����̂�)
		allViewSystemInfo = new ViewpointInfo(gameSetting);

		// �S���_���̏�����(�����̋��U�񍐂������O��)
		allViewTrustInfo = new ViewpointInfo(allViewSystemInfo);
		allViewSystemInfo.addInclusionViewpoint(allViewTrustInfo);

		// �������_���̏�����
		selfViewInfo = new ViewpointInfo(allViewTrustInfo);
		selfViewInfo.removeWolfsidePattern(gameInfo.getAgent().getAgentIdx());
		allViewTrustInfo.addInclusionViewpoint(selfViewInfo);

		// �������l���_���̏�����
		if( gameInfo.getRole() == Role.POSSESSED ){
			selfRealRoleViewInfo = new ViewpointInfo(allViewTrustInfo);
			selfRealRoleViewInfo.removeNotPossessedPattern(gameInfo.getAgent().getAgentIdx());
			allViewTrustInfo.addInclusionViewpoint(selfRealRoleViewInfo);
		}


		// �x���E�̏����ݒ�
		switch( gameInfo.getRole() ){
			case WEREWOLF:
				fakeRole = Role.VILLAGER;
				break;
			case POSSESSED:
				fakeRole = Role.SEER;
				break;
			default:
				fakeRole = null;
				break;
		}


//		long endtime = System.currentTimeMillis();
//
//		// �f�o�b�O���b�Z�[�W�̏o��
//		System.out.println("AdvanceGameInfo InitTime:" + (endtime - starttime));

	}


	/**
	 * ���̍X�V(AbstractVillager�p���N���X����update()���Ăяo�����Ɏ��s����)
	 * @param gameInfo
	 */
	public void update(GameInfo gameInfo){

		// ���t�ύX�`�F�b�N
		boolean updateday = false;
		if( latestGameInfo == null || gameInfo.getDay() > latestGameInfo.getDay() ){
			updateday = true;
		}

		// �󂯎����gameInfo���ŐV�̂��̂Ƃ��ĕۊǂ���
		latestGameInfo = gameInfo;

		// ���t�ύX���̏�����
		if( updateday ){

			isDayUpdate = true;
			dayStart();

		}else{

			isDayUpdate = false;

			// �������O�̍X�V
			setTalkList( latestGameInfo.getDay(), latestGameInfo.getTalkList() );

			// CO�󋵂̍X�V
			setCOList();

			// ��b���ǂ��܂œǂ񂾂�(����ǂݎn�߂锭��No)�̍X�V
			readTalkListNum = latestGameInfo.getTalkList().size();

		}


	}


	private void dayStart(){

		// �Ō�ɓǂ񂾃��O�ԍ��̃��Z�b�g
		readTalkListNum = 0;

		// ���[���ʂ̐ݒ�
		if( latestGameInfo.getDay() >= 1 ){
			setVoteList( latestGameInfo.getDay() - 1, latestGameInfo.getVoteList() );
		}

		// �݂蔭�����̏���
		if( latestGameInfo.getExecutedAgent() != null ){
			// �݂�ꂽ�G�[�W�F���g�̏�ԍX�V
			agentState[latestGameInfo.getExecutedAgent().getAgentIdx()].deathDay = latestGameInfo.getDay();
			agentState[latestGameInfo.getExecutedAgent().getAgentIdx()].causeofDeath = CauseOfDeath.EXECUTED;
		}

		// ���ݔ������̏���
		if( latestGameInfo.getAttackedAgent() != null ){
			// ���܂ꂽ�G�[�W�F���g�̏�ԍX�V
			agentState[latestGameInfo.getAttackedAgent().getAgentIdx()].deathDay = latestGameInfo.getDay();
			agentState[latestGameInfo.getAttackedAgent().getAgentIdx()].causeofDeath = CauseOfDeath.ATTACKED;

			// �e���_����A���ݐ悪�T�̃p�^�[������������(�V�X�e�����)
			allViewSystemInfo.removeWolfPattern(latestGameInfo.getAttackedAgent().getAgentIdx());
		}

		//TODO ���Ґ��Ή�
		// �c��T������̃p�^�[���i����(G16�̏ꍇ�A���Y���R�񔭐�����4���ڈȍ~)(�V�X�e�����)
		int maxWolfNum = ( latestGameInfo.getAliveAgentList().size() - 1 ) / 2;
		if( latestGameInfo.getDay() >= 4 ){
			allViewSystemInfo.removePatternFromWolfNum(Common.getAgentNo(latestGameInfo.getAliveAgentList()), 1, maxWolfNum);
		}

		// �V�������̏���ݒ肷��
		DayInfo toDayInfo = new DayInfo( latestGameInfo );
		dayInfoList.add(toDayInfo);


		// �{���̋^���ρE�M�p�σG�[�W�F���g�̃��X�g������������
		talkedSuspicionAgentList = new ArrayList<Integer>();
		talkedTrustAgentList = new ArrayList<Integer>();


		// ��l�̏���
		if( latestGameInfo.getRole() == Role.BODYGUARD ){
			// �Q���ڈȍ~�A�����Ă���Ό�q�������L������
			if( latestGameInfo.getDay() >= 2 &&
				latestGameInfo.getAliveAgentList().contains( latestGameInfo.getAgent() ) ){
				selfGuardRecent.put( latestGameInfo.getDay() - 1, latestGameInfo.getGuardedAgent().getAgentIdx() );
			}
		}

		// �肢�t�̏���
		if( latestGameInfo.getRole() == Role.SEER ){
			// �P���ڈȍ~�A�����Ă���ΐ肢���ʂ��L������
			if( latestGameInfo.getDay() >= 1 &&
			    latestGameInfo.getAliveAgentList().contains( latestGameInfo.getAgent() ) ){

				Judge newJudge = new Judge( latestGameInfo.getAgent().getAgentIdx(),
				                            latestGameInfo.getDivineResult().getTarget().getAgentIdx(),
				                            latestGameInfo.getDivineResult().getResult(),
				                            null );

				selfInspectList.add(newJudge);
			}
		}

		// ��\�҂̏���
		if( latestGameInfo.getRole() == Role.MEDIUM ){
			// �Q���ڈȍ~�A�����Ă���Η�\���ʂ��L������
			if( latestGameInfo.getDay() >= 2 &&
			    latestGameInfo.getAliveAgentList().contains( latestGameInfo.getAgent() ) ){

				Judge newJudge = new Judge( latestGameInfo.getAgent().getAgentIdx(),
				                            latestGameInfo.getMediumResult().getTarget().getAgentIdx(),
				                            latestGameInfo.getMediumResult().getResult(),
				                            null );

				selfInquestList.add(newJudge);
			}
		}

		//TODO �x��\�͎҂̏����̓f���Q�[�g�̕�����������

	}


	/**
	 * �������O�̃Z�b�g
	 * @param day ��
	 * @param talklist �����̃��X�g
	 */
	private void setTalkList(int day, List<Talk> talklist ){

		// �w����̑O���܂ł̃��O�𖄂߂�
		while( talkLists.size() < day ){
			talkLists.add(new ArrayList<Talk>());
		}

		// �����̃��O��������Βǉ��A����Ώ㏑������
		if( talkLists.size() > day){
			talkLists.set(day, talklist);
		}else{
			talkLists.add(talklist);
		}

	}


	/**
	 * �������O�̎擾
	 * @param day ��
	 * @return
	 */
	public List<Talk> getTalkList(int day){

		// �f�[�^�����݂���ꍇ
		if( day >= 0 && day < talkLists.size() ){
			return talkLists.get(day);
		}

		// �f�[�^�����݂��Ȃ��ꍇ
		return null;

	}


	/**
	 *
	 * @param day ��
	 * @param talkid ����ID
	 * @return
	 */
	public Talk getTalk(int day, int talkid){

		List<Talk> talkList = getTalkList(day);

		// �w����̃��O�����݂��邩
		if( talkList != null ){
			// ���������݂��邩
			if( talkid >= 0 && talkid < talkList.size() ){
				return talkList.get(talkid);
			}
		}

		return null;

	}


	/**
	 * ���[���O�̃Z�b�g
	 * @param day ���[���s��ꂽ��(���񓊕[��1����)
	 * @param votelist ���[���ʂ̃��X�g
	 */
	private void setVoteList( int day, List<Vote> votelist ){

		// �w����̑O���܂ł̓��[���ʂ𖄂߂�
		while( voteLists.size() < day - 1){
			voteLists.add(new ArrayList<Vote>());
		}

		// �����̃��O��������Βǉ��A����Ώ㏑������
		if( voteLists.lastIndexOf(0) >= day){
			voteLists.set(day, votelist);
		}else{
			voteLists.add(votelist);
		}

	}


	/**
	 * ���[���O�̎擾
	 * @param day ���[���s��ꂽ��(���񓊕[��1����)
	 * @return
	 */
	public List<Vote> getVoteList(int day){

		// �f�[�^�����݂���ꍇ
		if( day >= 0 && day < voteLists.size() ){
			return voteLists.get(day);
		}

		// �f�[�^�����݂��Ȃ��ꍇ
		return null;
	}


	/**
	 * CO�󋵂̍X�V
	 */
	private void setCOList(){

		// CO�P��t���O
		boolean existCancel = false;

		int day = latestGameInfo.getDay();
		List<Talk> talkList = talkLists.get(day);

		// �����̑���
		for (int i = readTalkListNum; i < talkList.size(); i++) {
			Talk talk = (Talk)talkList.get(i);
			Utterance utterance = getUtterance(talk.getContent());
			switch (utterance.getTopic())
			{
				case COMINGOUT:	// CO
					int COAgentNo = talk.getAgent().getAgentIdx();
					Role CORole = utterance.getRole();
					switch(CORole){
						case SEER:
						case BODYGUARD:
						case MEDIUM:
						case VILLAGER:

							//TODO ������s���������͋L�����Ă�������
							// CO�Ώۂ������ȊO�Ȃ珈�������ɃX�L�b�v
							if( utterance.getTarget().getAgentIdx() != talk.getAgent().getAgentIdx() ){
								break;
							}

							// ����CO���Ă����ԂŁA���̖�E��CO
							if( agentState[COAgentNo].comingOutRole != null && CORole != agentState[COAgentNo].comingOutRole ){
								// CO�P�񔭐��t���O�𗧂Ă�
								existCancel = true;

								// �񍐍ς݂̖�E��(CO�E���蓙)�𖳌��ɂ���
								cancelRoleReport( COAgentNo, talk );
							}

							// CO���̍X�V
							updateCommingOut( COAgentNo, CORole, talk );
							// CO����̃p�^�[���i�荞��(���ߑł����)
							List<Integer> agents = getEnableCOAgentNo(CORole);
							if( agents.size() > 1 ){
								allViewTrustInfo.removePatternFromUniqueRole(getEnableCOAgentNo(CORole));
							}
							break;
						case WEREWOLF:
						case POSSESSED:
							// CO���̍X�V
							updateWolfSideCommingOut( COAgentNo, CORole, talk );
							break;
						default:
							break;
					}
					break;
				case DIVINED:	// �肢����
					int seerAgentNo = talk.getAgent().getAgentIdx();
					int inspectedAgentNo = utterance.getTarget().getAgentIdx();
					Species inspectResult = utterance.getResult();
					Judge sjudge = new Judge(seerAgentNo, inspectedAgentNo, inspectResult, talk);

					// ����ꗗ�ɓo�^
					seerJudgeList.add(sjudge);

					if( agentState[seerAgentNo].comingOutRole == Role.SEER &&
					    isValidAgentNo(inspectedAgentNo) ){
						// �肢���ʂ���̃p�^�[���i�荞��(���ߑł����)
						allViewTrustInfo.removePatternFromJudge( seerAgentNo, inspectedAgentNo, inspectResult );

						// ���o���̏ꍇ�A�o���ꂽ�Ҏ��_�j�](���ߑł����)
						if( utterance.getResult() == Species.WEREWOLF && utterance.getTarget().equals(latestGameInfo.getAgent()) ){
							// �o���ꂽ�Ҏ��_����A�j�]�҂��T�w�c�łȂ��p�^�[������菜��
							selfViewInfo.removeNotWolfsidePattern(seerAgentNo);
						}

						// �قȂ锻����o����\������Ό݂��ɔj�](���ߑł����)
						for( Judge judge : mediumJudgeList ){
							if( judge.targetAgentNo == sjudge.targetAgentNo && judge.result != sjudge.result ){
								// �e���_����A�Q�l�Ƃ��T�w�c�Ɋ܂܂�Ȃ��p�^�[������菜��
								ArrayList<Integer> opposeList = new ArrayList<Integer>();
								opposeList.add(judge.agentNo);
								opposeList.add(sjudge.agentNo);
								allViewTrustInfo.removePatternFromWolfSideNum(opposeList, 1, 2);
							}
						}
					}else{
						sjudge.cancelTalk = talk;
					}

					break;
				case INQUESTED:	// ��\����
					int mediumAgentNo = talk.getAgent().getAgentIdx();
					int inquestedAgentNo = utterance.getTarget().getAgentIdx();
					Species inquestedResult = utterance.getResult();
					Judge mjudge = new Judge(mediumAgentNo, inquestedAgentNo, inquestedResult, talk);

					// ����ꗗ�ɓo�^
					mediumJudgeList.add(mjudge);

					// ��\���ʂ���̃p�^�[���i�荞��
					allViewSystemInfo.removePatternFromJudge( mediumAgentNo, inquestedAgentNo, inquestedResult );

					// ���o���̏ꍇ�A�o���ꂽ�Ҏ��_�j�](���ߑł����)
					if( utterance.getResult() == Species.WEREWOLF && utterance.getTarget().equals(latestGameInfo.getAgent()) ){
						// �o���ꂽ�Ҏ��_����A�j�]�҂��T�w�c�łȂ��p�^�[������菜��
						selfViewInfo.removeNotWolfsidePattern(mediumAgentNo);
					}

					// �قȂ锻����o���肢������Ό݂��ɔj�](���ߑł����)
					for( Judge judge : seerJudgeList ){
						if( judge.targetAgentNo == mjudge.targetAgentNo && judge.result != mjudge.result ){
							// �e���_����A�Q�l�Ƃ��T�w�c�Ɋ܂܂�Ȃ��p�^�[������菜��
							ArrayList<Integer> opposeList = new ArrayList<Integer>();
							opposeList.add(judge.agentNo);
							opposeList.add(mjudge.agentNo);
							allViewTrustInfo.removePatternFromWolfSideNum(opposeList, 1, 2);
						}
					}

					break;

				case GUARDED:
					int bodyGuardAgentNo = talk.getAgent().getAgentIdx();
					int guardedAgentNo = utterance.getTarget().getAgentIdx();
					GuardRecent guardRecent = new GuardRecent(bodyGuardAgentNo, guardedAgentNo, talk);

					int guardReportCount = 0;
					for( GuardRecent guard : guardRecentList ){
						if( guard.isEnable() && guard.agentNo == bodyGuardAgentNo ){
							guardReportCount++;
						}
					}

					// ��q�����ꗗ�ɓo�^
					guardRecent.execDay = guardReportCount + 1;

					// ��q�����ꗗ�ɓo�^
					guardRecentList.add(guardRecent);
				default:
					break;
			}
		}

		// CO�P�񂪂������ꍇ
		if( existCancel ){
			// ���_�����\�z������
			remakeViewInfo();
		}

	}


	/**
	 * �񍐍ς݂̖�E�񍐂𖳌��ɂ���iCO�P�񎞂̏����j
	 * @param agentNo �G�[�W�F���g�ԍ�
	 * @param cancelTalk �P����s��������
	 */
	private void cancelRoleReport(int agentNo, Talk cancelTalk){

		// CO�̑���
		for( ComingOut co : comingOutList ){
			// �w�肵���G�[�W�F���g�̗L����CO��
			if( co.agentNo == agentNo && co.isEnable() ){
				// �P�񔭌���ݒ肷��
				co.cancelTalk = cancelTalk;
			}
		}

		// �S�Ă̐蔻�藚�����m�F����
		for( Judge judge : getSeerJudgeList() ){
			// �w�肵���G�[�W�F���g�̗L���Ȕ��肩
			if( judge.agentNo == agentNo && judge.isEnable() ){
				// �P�񔭌���ݒ肷��
				judge.cancelTalk = cancelTalk;
			}
		}

		// �S�Ă̗씻�藚�����m�F����
		for( Judge judge : getMediumJudgeList() ){
			// �w�肵���G�[�W�F���g�̗L���Ȕ��肩
			if( judge.agentNo == agentNo && judge.isEnable() ){
				// �P�񔭌���ݒ肷��
				judge.cancelTalk = cancelTalk;
			}
		}

		// �S�Ă̌�q�������m�F����
		for( GuardRecent guard : getGuardRecentList() ){
			// �w�肵���G�[�W�F���g�̗L���Ȍ�q������
			if( guard.agentNo == agentNo && guard.isEnable() ){
				// �P�񔭌���ݒ肷��
				guard.cancelTalk = cancelTalk;
			}
		}

	}


	/**
	 * ���_�����\�z������
	 */
	private void remakeViewInfo(){

		// �S���_���ߑł������V�X�e�����_�ɍ��킹��
		allViewTrustInfo.remakePattern(allViewSystemInfo);

		// �e���_����A�������T�̃p�^�[�������O����
		selfViewInfo.removeWolfPattern(latestGameInfo.getAgent().getAgentIdx());
		if( selfRealRoleViewInfo != null ){
			selfRealRoleViewInfo.removeWolfPattern(latestGameInfo.getAgent().getAgentIdx());
			selfRealRoleViewInfo.removeNotWolfsidePattern(latestGameInfo.getAgent().getAgentIdx());
		}

		// ��CO����̃p�^�[���i�荞��(���ߑł����)
		List<Integer> agents = getEnableCOAgentNo(Role.SEER);
		if( agents.size() > 1 ){
			allViewTrustInfo.removePatternFromUniqueRole(getEnableCOAgentNo(Role.SEER));
		}

		// ��CO����̃p�^�[���i�荞��(���ߑł����)
		agents = getEnableCOAgentNo(Role.MEDIUM);
		if( agents.size() > 1 ){
			allViewTrustInfo.removePatternFromUniqueRole(getEnableCOAgentNo(Role.MEDIUM));
		}

		// ��CO����̃p�^�[���i�荞��(���ߑł����)
		agents = getEnableCOAgentNo(Role.BODYGUARD);
		if( agents.size() > 1 ){
			allViewTrustInfo.removePatternFromUniqueRole(getEnableCOAgentNo(Role.BODYGUARD));
		}

		// �蔻�肩��̃p�^�[���i�荞��
		for( Judge seerJudge : seerJudgeList ){
			if( seerJudge.isEnable() ){
				// �肢���ʂ���̃p�^�[���i�荞��(���ߑł����)
				allViewTrustInfo.removePatternFromJudge( seerJudge.agentNo, seerJudge.targetAgentNo, seerJudge.result );

				// ���o���̏ꍇ�A�o���ꂽ�Ҏ��_�j�](���ߑł����)
				if( seerJudge.result == Species.WEREWOLF && seerJudge.targetAgentNo == latestGameInfo.getAgent().getAgentIdx() ){
					// �o���ꂽ�Ҏ��_����A�j�]�҂��T�w�c�łȂ��p�^�[������菜��
					selfViewInfo.removeNotWolfsidePattern(seerJudge.agentNo);
				}

				// �قȂ锻����o����\������Ό݂��ɔj�](���ߑł����)
				for( Judge mediumJudge : mediumJudgeList ){
					if( mediumJudge.isEnable() && seerJudge.targetAgentNo == mediumJudge.targetAgentNo && seerJudge.result != mediumJudge.result ){
						// �e���_����A�Q�l�Ƃ��T�w�c�Ɋ܂܂�Ȃ��p�^�[������菜��
						ArrayList<Integer> opposeList = new ArrayList<Integer>();
						opposeList.add(seerJudge.agentNo);
						opposeList.add(mediumJudge.agentNo);
						allViewTrustInfo.removePatternFromWolfSideNum(opposeList, 1, 2);
					}
				}

			}
		}

		// �씻�肩��̃p�^�[���i�荞��
		for( Judge mediumJudge : mediumJudgeList ){
			if( mediumJudge.isEnable() ){
				// �肢���ʂ���̃p�^�[���i�荞��(���ߑł����)
				allViewTrustInfo.removePatternFromJudge( mediumJudge.agentNo, mediumJudge.targetAgentNo, mediumJudge.result );

				// ���o���̏ꍇ�A�o���ꂽ�Ҏ��_�j�](���ߑł����)
				// ���o���̏ꍇ�A�o���ꂽ�Ҏ��_�j�](���ߑł����)
				if( mediumJudge.result == Species.WEREWOLF && mediumJudge.targetAgentNo == latestGameInfo.getAgent().getAgentIdx() ){
					// �o���ꂽ�Ҏ��_����A�j�]�҂��T�w�c�łȂ��p�^�[������菜��
					selfViewInfo.removeNotWolfsidePattern(mediumJudge.agentNo);
				}

				// �قȂ锻����o����\������Ό݂��ɔj�](���ߑł����)
				for( Judge seerJudge : seerJudgeList ){
					if( seerJudge.isEnable() && seerJudge.targetAgentNo == mediumJudge.targetAgentNo && seerJudge.result != mediumJudge.result ){
						// �e���_����A�Q�l�Ƃ��T�w�c�Ɋ܂܂�Ȃ��p�^�[������菜��
						ArrayList<Integer> opposeList = new ArrayList<Integer>();
						opposeList.add(seerJudge.agentNo);
						opposeList.add(mediumJudge.agentNo);
						allViewTrustInfo.removePatternFromWolfSideNum(opposeList, 1, 2);
					}
				}

			}
		}

	}


	/**
	 * CO���̍X�V
	 * @param agentNo �G�[�W�F���g�ԍ�
	 * @param role CO������E
	 * @param commingOutTalkCO��������
	 */
	private void updateCommingOut(int agentNo, Role role, Talk commingOutTalk){

		// CO�̑���
		for( ComingOut co : comingOutList ){
			// CO�����G�[�W�F���g�̗L����CO��
			if( co.agentNo == agentNo && co.isEnable() ){
				if( co.role == role ){
					// ���ɓ�����E��CO���Ă����� �� �������������Ĕ�����
					return;
				}
			}
		}

		// �V����CO�Ƃ��ēo�^����
		comingOutList.add( new ComingOut(agentNo, role, commingOutTalk) );

		// �G�[�W�F���g�����X�V����
		agentState[agentNo].comingOutRole = role;

	}


	/**
	 * CO���̍X�V�i�T��E��CO�j
	 * @param agentNo �G�[�W�F���g�ԍ�
	 * @param role CO������E
	 * @param commingOutTalkCO��������
	 */
	private void updateWolfSideCommingOut(int agentNo, Role role, Talk commingOutTalk){

		// CO�̑���
		for( ComingOut co : wolfsideComingOutList ){
			// CO�����G�[�W�F���g�̗L����CO��
			if( co.agentNo == agentNo && co.isEnable() ){
				if( co.role == role ){
					// ���ɓ�����E��CO���Ă����� �� �������������Ĕ�����
					return;
				}
			}
		}

		// �Â�CO�̓L�����Z�������ɂ���
		for( ComingOut co : wolfsideComingOutList ){
			if( co.agentNo == agentNo && co.isEnable() ){
				co.cancelTalk = commingOutTalk;
			}
		}

		// �V����CO�Ƃ��ēo�^����
		wolfsideComingOutList.add( new ComingOut(agentNo, role, commingOutTalk) );

	}


	/**
	 * CO�҂̃��X�g���擾����i�L����CO�̂݁j
	 * @param role �擾����CO�҂̖�E
	 * @return CO�҂̃G�[�W�F���g�ԍ��̃��X�g
	 */
	public List<Integer> getEnableCOAgentNo(Role role) {

		List<Integer> ret = new ArrayList<Integer>();

		// CO�̑���
		for( ComingOut co : comingOutList ){
			// �w�肵����E���L����CO��
			if( co.role == role && co.isEnable() ){
				// ���ʃ��X�g�ɒǉ�
				ret.add(co.agentNo);
			}
		}

		return ret;
	}


	/**
	 * CO�҂̃��X�g���擾����i�w�蔭���̒��O���_�ŗL����CO�̂݁j
	 * @param role �擾����CO�҂̖�E
	 * @param day ��
	 * @param talkid ����ID
	 * @return CO�҂̃G�[�W�F���g�ԍ��̃��X�g
	 */
	public List<Integer> getEnableCOAgentNo(Role role, int day, int talkID) {

		List<Integer> ret = new ArrayList<Integer>();

		// CO�̑���
		for( ComingOut co : comingOutList ){
			// �w�肵����E���L����CO��
			if( co.role == role && co.isEnable(day, talkID) ){
				// ���ʃ��X�g�ɒǉ�
				ret.add(co.agentNo);
			}
		}

		return ret;
	}


	/**
	 * �蔻��̃��X�g���擾����i�����Ȕ�����܂ށj
	 * @return
	 */
	public List<Judge> getSeerJudgeList() {
		return seerJudgeList;
	}


	/**
	 * �씻��̃��X�g���擾����i�����Ȕ�����܂ށj
	 * @return
	 */
	public List<Judge> getMediumJudgeList() {
		return mediumJudgeList;
	}


	/**
	 * ��q�����̃��X�g���擾����i�����ȗ������܂ށj
	 * @return
	 */
	public List<GuardRecent> getGuardRecentList() {
		return guardRecentList;
	}


	/**
	 * ���t�X�V�ŌĂ΂ꂽUpdate()��
	 * @return
	 */
	public boolean isDayUpdate(){
		return isDayUpdate;
	}


	/**
	 * ���[�\������擾����
	 * @param agentNo
	 * @return
	 */
	public Integer getSaidVoteAgent(int agentNo){

		GameInfo gameInfo = latestGameInfo;

		Integer ret = null;

		// �����̑���
		for( Talk talk : gameInfo.getTalkList() ){
			if( talk.getAgent().getAgentIdx() == agentNo ){
				Utterance utterance = getUtterance(talk.getContent());
				if( utterance.getTopic() == Topic.VOTE ){
					// ���[�錾
					if( gameInfo.getAgentList().contains(utterance.getTarget()) ){
						ret = utterance.getTarget().getAgentIdx();
					}
				}else if( utterance.getTopic() == Topic.AGREE ){
					// ���ӂ̈Ӑ}�����[�錾
					Utterance refutterance = getMeanFromAgreeTalk( talk, 0 );
					if( refutterance != null && gameInfo.getAgentList().contains(refutterance.getTarget()) ){
						ret = refutterance.getTarget().getAgentIdx();
					}
				}
			}
		}
		return ret;
	}


	/**
	 * ����/���S��Ԃ��擾����
	 * @param AgentNo
	 * @param day
	 * @return
	 */
	public CauseOfDeath getCauseOfDeath( int AgentNo, int day ){

		// �w�肳�ꂽ���Ɏ��S�� �� ������Ԃ�
		if( agentState[AgentNo].deathDay != null && agentState[AgentNo].deathDay <= day ){
			return agentState[AgentNo].causeofDeath;
		}

		// �w�肳�ꂽ���ɐ��� �� ������Ԃ�
		return CauseOfDeath.ALIVE;

	}


	/**
	 * agree�����̈Ӗ����擾����
	 * @param talk agree����
	 * @return agree�����̈Ӗ�(��͕s�\���Aagree�����ȊO���w�莞��null)
	 */
	public Utterance getMeanFromAgreeTalk( Talk talk, int depth){

		Utterance utterance = getUtterance(talk.getContent());

		// �����̔�����AGREE�ȊO��
		if( utterance.getTopic() != Topic.AGREE ){
			return null;
		}

		// �����̎�ނ̃`�F�b�N(�s�������ւ̓���)
		if( utterance.getTalkType() == TalkType.WHISPER ){
			// ��͕s�\
			return null;
		}

		// ���n��̃`�F�b�N(���݁`�����̔����ւ̓���)
		if( Common.compareTalkID( utterance.getTalkDay(), utterance.getTalkID(), talk.getDay(), talk.getIdx() ) >= 0 ){
			// ��͕s�\
			return null;
		}

		// �Q�Ɛ�̔����̎擾
		Talk refTalk = getTalk( utterance.getTalkDay(), utterance.getTalkID() );

		// �Q�Ɛ悪������Ȃ��ꍇ
		if( refTalk == null ){
			// ��͕s�\
			return null;
		}

		Utterance refutterance = getUtterance(refTalk.getContent());
		switch( refutterance.getTopic() ){
			case ESTIMATE:
				// �Q�Ɛ�Ɠ��������������Ɖ��߂���
				return refutterance;
			case VOTE:
				// �Q�Ɛ�Ɠ��������������Ɖ��߂���
				return refutterance;
			case AGREE:
				// �Q�Ƃ��[������Ή����\�Ƃ���
				if( depth >= 10 ){
					return null;
				}
				// �X�ɎQ�Ƃ��A�Q�Ɛ�Ɠ��������������Ɖ��߂���
				return getMeanFromAgreeTalk(refTalk, depth + 1);
			case DISAGREE:
				// �Ӑ}���s���m������̂ŁA�����͕s�\�Ƃ���
				break;
			default:
				break;
		}

		// ��͕s�\
		return null;

	}


	/**
	 * �w��G�[�W�F���g���w������܂łɐ荕������󂯂Ă��邩
	 * @param agentNo �G�[�W�F���g�ԍ�
	 * @param day ��
	 * @param talkID ����ID
	 * @return
	 */
	public boolean isReceiveWolfJudge( int agentNo, int day, int talkID ){

		for( Judge judge : getSeerJudgeList() ){
			// ���n��̃`�F�b�N�i���肪�w��������O���j
			if( Common.compareTalkID( judge.talk.getDay(), judge.talk.getIdx(), day, talkID) == -1 ){
				// �Ώێ҂ւ̐l�T���肩
				if( judge.targetAgentNo == agentNo && judge.result == Species.WEREWOLF ){
					return true;
				}
			}
		}

		return false;

	}


	/**
	 * �ŐV���Ɏ�����������Talk�̉񐔂��擾����
	 * @return �ŐV���Ɏ�����������Talk�̉�
	 */
	public int getMyTalkNum(){

		int count = 0;

		for( Talk talk : latestGameInfo.getTalkList() ){
			if( talk.getAgent().getAgentIdx() == latestGameInfo.getAgent().getAgentIdx() ){
				count++;
			}
		}

		return count;

	}


	/**
	 * �G�[�W�F���g�ԍ��̑Ó����`�F�b�N
	 * @param agentno
	 * @return
	 */
	public boolean isValidAgentNo(int agentno){

		if( agentno <= 0 || agentno > gameSetting.getPlayerNum() ){
			return false;
		}

		return true;

	}


	/**
	 * ������͓��e�̎擾�i�L���b�V�����p�ō����j
	 * @param talkContent �����̓��e
	 * @return
	 */
	public Utterance getUtterance(String talkContent){

		Utterance ret;

		// �ߋ��̉�͍ςݏ��ɓo�^�ς�
		if( analysedUtteranceMap.containsKey(talkContent) ){

			// �ߋ��̉�͍ςݏ�񂩂�擾����
			ret = analysedUtteranceMap.get(talkContent);

		}else{

			ret = new Utterance(talkContent);

			if( ret != null ){
				// ��͍ςݏ��ɓo�^����
				analysedUtteranceMap.put(talkContent, ret);
			}

		}

		return ret;

	}


	/**
	 * �肢�����ǉ�����i�x��p�j
	 * @param judge
	 */
	public void addFakeSeerJudge(Judge judge){

		selfInspectList.add(judge);

	}


	/**
	 * �w�肵���G�[�W�F���g���T���i�������T�̎��p�j
	 * @param agentNo
	 * @return
	 */
	public boolean isWolf(int agentNo){

		GameInfo gameInfo = latestGameInfo;

		Role role = gameInfo.getRoleMap().get(Agent.getAgent(agentNo));

		if( role == Role.WEREWOLF ){
			return true;
		}

		return false;

	}


	/**
	 * �T�̈ꗗ��Ԃ��i�������T�̎��p�j
	 * @return
	 */
	public List<Integer> getWolfList(){

		GameInfo gameInfo = latestGameInfo;

		List<Integer> ret = new ArrayList<Integer>();

		for( Agent agent : gameInfo.getAgentList() ){
			Role role = gameInfo.getRoleMap().get(agent);
			if( role == Role.WEREWOLF ){
				ret.add(agent.getAgentIdx());
			}
		}

		return ret;

	}


	/**
	 * �������Ă���T�̈ꗗ��Ԃ��i�������T�̎��p�j
	 * @return
	 */
	public List<Integer> getAliveWolfList(){

		GameInfo gameInfo = latestGameInfo;

		List<Integer> ret = new ArrayList<Integer>();

		for( Agent agent : gameInfo.getAgentList() ){
			Role role = gameInfo.getRoleMap().get(agent);
			if( role == Role.WEREWOLF && agentState[agent.getAgentIdx()].causeofDeath == CauseOfDeath.ALIVE ){
				ret.add(agent.getAgentIdx());
			}
		}

		return ret;

	}


	/**
	 * PP���\�����擾����i�������T�̎��p�j
	 * @return
	 */
	public boolean isEnablePowerPlay(){

		//TODO ������PP�Ԃ��΍��PP�U���΍􂪕K�v
		//TODO �^�����������̔���A�닶�̔���


		// �c�菈�Y���������T����葽�����PP�͔������Ȃ�
		if( Common.getRestExecuteCount(latestGameInfo.getAliveAgentList().size()) > getAliveWolfList().size() ){
			return false;
		}

		// ���l�̃G�[�W�F���g�ԍ�
		Integer possessed = null;

		// ���l��T��
		for( Judge judge : getSeerJudgeList() ){
			// �l�Ԃ̐肢�t���Ԉ����������o������
			if( !isWolf(judge.agentNo) &&
			    (judge.result == Species.WEREWOLF) != isWolf(judge.targetAgentNo) ){
				possessed = judge.agentNo;
			}
		}
		for( Judge judge : getMediumJudgeList() ){
			// �l�Ԃ̗�\�҂��Ԉ����������o������
			if( !isWolf(judge.agentNo) &&
			    (judge.result == Species.WEREWOLF) != isWolf(judge.targetAgentNo) ){
				possessed = judge.agentNo;
			}
		}

		// ���l����� �� ���l����
		if( possessed != null && agentState[possessed].causeofDeath == CauseOfDeath.ALIVE ){
			// PP����
			return true;
		}

		return false;

	}


	/**
	 * PP���\�����擾����i���������l�̎��p�j
	 * @return
	 */
	public boolean isEnablePowerPlay_Possessed(){

		// 3�l�ȉ��Ȃ�m����PP
		if( latestGameInfo.getAliveAgentList().size() <= 3 ){
			return true;
		}

		// �����҂�8�l�ȉ�
		if( latestGameInfo.getAliveAgentList().size() <= 8 ){
			// �l�OCO���s�����l�����擾
			for( ComingOut co : wolfsideComingOutList ){
				// �L����CO ���� ���������_�Ŋm���ł͂Ȃ�
				if( co.isEnable() && !selfRealRoleViewInfo.isFixWhite(co.agentNo) ){
					// PP�����Ɣ��f
					return true;
				}
			}
		}

		return false;

	}

}
