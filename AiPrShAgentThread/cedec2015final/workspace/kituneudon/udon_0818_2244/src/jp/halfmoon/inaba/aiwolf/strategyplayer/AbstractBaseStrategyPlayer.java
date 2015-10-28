package jp.halfmoon.inaba.aiwolf.strategyplayer;

import java.util.ArrayList;

import jp.halfmoon.inaba.aiwolf.guess.AnalysisOfGuess;
import jp.halfmoon.inaba.aiwolf.guess.COTiming;
import jp.halfmoon.inaba.aiwolf.guess.Favor;
import jp.halfmoon.inaba.aiwolf.guess.FirstImpression;
import jp.halfmoon.inaba.aiwolf.guess.Formation_Basic;
import jp.halfmoon.inaba.aiwolf.guess.FromGuardRecent;
import jp.halfmoon.inaba.aiwolf.guess.Guess;
import jp.halfmoon.inaba.aiwolf.guess.GuessManager;
import jp.halfmoon.inaba.aiwolf.guess.GuessStrategyArgs;
import jp.halfmoon.inaba.aiwolf.guess.InspectedWolfsidePattern;
import jp.halfmoon.inaba.aiwolf.guess.JudgeRecent;
import jp.halfmoon.inaba.aiwolf.guess.JudgeRecent_WolfSide;
import jp.halfmoon.inaba.aiwolf.guess.Noise;
import jp.halfmoon.inaba.aiwolf.guess.VoteRecent;
import jp.halfmoon.inaba.aiwolf.learn.LearnData;
import jp.halfmoon.inaba.aiwolf.lib.AdvanceGameInfo;
import jp.halfmoon.inaba.aiwolf.lib.WolfsidePattern;
import jp.halfmoon.inaba.aiwolf.request.ActionStrategyArgs;
import jp.halfmoon.inaba.aiwolf.request.AnalysisOfRequest;
import jp.halfmoon.inaba.aiwolf.request.AttackObstacle;
import jp.halfmoon.inaba.aiwolf.request.BasicAttack;
import jp.halfmoon.inaba.aiwolf.request.BasicGuard;
import jp.halfmoon.inaba.aiwolf.request.BasicSeer;
import jp.halfmoon.inaba.aiwolf.request.FixInfo;
import jp.halfmoon.inaba.aiwolf.request.FromGuess;
import jp.halfmoon.inaba.aiwolf.request.PowerPlay_Possessed;
import jp.halfmoon.inaba.aiwolf.request.PowerPlay_Werewolf;
import jp.halfmoon.inaba.aiwolf.request.Request;
import jp.halfmoon.inaba.aiwolf.request.ReticentExecute;
import jp.halfmoon.inaba.aiwolf.request.RoleWeight;
import jp.halfmoon.inaba.aiwolf.request.RoleWeight_Wolfside;
import jp.halfmoon.inaba.aiwolf.request.VoteStack;

import org.aiwolf.client.base.player.AbstractRole;
import org.aiwolf.client.lib.TemplateTalkFactory;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Team;
import org.aiwolf.common.net.GameInfo;
import org.aiwolf.common.net.GameSetting;

/**
 * �S�Ă̖�E�̃x�[�X�ƂȂ�N���X
 */
public abstract class AbstractBaseStrategyPlayer extends AbstractRole{

	/** �g���Q�[����� */
	protected AdvanceGameInfo agi;

	/** �s����ݒ肷�邽�߂̋[��UI */
	protected ActionUI actionUI = new ActionUI();

	/** �������[���悤�Ǝv���Ă���v���C���[ */
	protected Integer planningVoteAgent;

	/** �������Ō�ɐ錾�����u���[���悤�Ǝv���Ă���v���C���[�v */
	protected Integer declaredPlanningVoteAgent;

	/** �������Ō�ɍs�������� */
	protected AnalysisOfGuess latestGuess;

	/** �������Ō�ɍs�����s���v�� */
	protected AnalysisOfRequest latestRequest;

	/** �錾�ς݂��x���E */
	protected Role declaredFakeRole;

	/** �������Ō�ɐ錾�����u�P�����悤�Ǝv���Ă���v���C���[�v */
	protected Integer declaredPlanningAttackAgent;

	/** CO�ς� */
	protected boolean isCameOut;


	/** �ۗL���鐄���헪 */
	protected ArrayList<HasGuessStrategy> guessStrategys = new ArrayList<HasGuessStrategy>();

	/** �ۗL����s���헪 */
	protected ArrayList<HasActionStrategy> actionStrategys = new ArrayList<HasActionStrategy>();


	// �����@�\

	/** �r���o�߂��o�͂��邩 */
	protected final boolean isPrintPassageTalk = false;

	/** �I�����ʂ��o�͂��邩 */
	protected final boolean isPrintFinishTalk = false;

	/** �w�K�p�̃f�[�^���o�͂��邩 */
	protected final boolean isPutLearnData = false;



	// �f�o�b�O�p

	/** Update()�ɂ����������Ԃ̂����Œ��̂��́i�f�o�b�O�p�j */
	protected long MaxUpdateTime = Long.MIN_VALUE;

	/** Update()�ɂ����������Ԃ��Œ��̃^�C�~���O�i�f�o�b�O�p�j */
	protected String MaxUpdateTiming;


	@Override
	public void initialize(GameInfo gameInfo, GameSetting gameSetting) {
		super.initialize(gameInfo, gameSetting);

		// �g���Q�[�����̏�����
		agi = new AdvanceGameInfo(gameInfo, gameSetting);

		// �����헪��ݒ�
		HasGuessStrategy guessStrategy;

		guessStrategy = new HasGuessStrategy(new FirstImpression(), 1.0);
		guessStrategys.add(guessStrategy);
		guessStrategy = new HasGuessStrategy(new FromGuardRecent(), 1.0);
		guessStrategys.add(guessStrategy);
		guessStrategy = new HasGuessStrategy(new Formation_Basic(), 1.0);
		guessStrategys.add(guessStrategy);
		guessStrategy = new HasGuessStrategy(new COTiming(), 1.0);
		guessStrategys.add(guessStrategy);
		//guessStrategy = new HasGuessStrategy(new AttackObstacle_Guess(), 1.0);
		//guessStrategys.add(guessStrategy);

		// ���w�c�ł̂ݗp���鐄��
		if( gameInfo.getRole().getTeam() == Team.VILLAGER ){
			guessStrategy = new HasGuessStrategy(new Noise(), 1.0);
			guessStrategys.add(guessStrategy);
			guessStrategy = new HasGuessStrategy(new VoteRecent(), 1.0);
			guessStrategys.add(guessStrategy);
			guessStrategy = new HasGuessStrategy(new JudgeRecent(), 1.0);
			guessStrategys.add(guessStrategy);
		}

		// �T�w�c�ł̂ݗp���鐄��
		if( gameInfo.getRole().getTeam() == Team.WEREWOLF ){
			guessStrategy = new HasGuessStrategy(new Noise(), 0.5);
			guessStrategys.add(guessStrategy);
			guessStrategy = new HasGuessStrategy(new VoteRecent(), 0.5);
			guessStrategys.add(guessStrategy);
			guessStrategy = new HasGuessStrategy(new JudgeRecent_WolfSide(), 1.0);
			guessStrategys.add(guessStrategy);
		}

		// �T�ł̂ݗp���鐄��
		if( gameInfo.getRole() == Role.WEREWOLF ){
			guessStrategy = new HasGuessStrategy(new Favor(), 1.0);
			guessStrategys.add(guessStrategy);
		}


		// �s���헪��ݒ�
		HasActionStrategy actStrategy;
		actStrategy = new HasActionStrategy(new FixInfo(), 1.0);
		actionStrategys.add(actStrategy);
		actStrategy = new HasActionStrategy(new FromGuess(), 1.0);
		actionStrategys.add(actStrategy);

		// ���w�c�ł̂ݗp����s��
		if( gameInfo.getRole().getTeam() == Team.VILLAGER ){
			actStrategy = new HasActionStrategy(new RoleWeight(), 1.0);
			actionStrategys.add(actStrategy);
			actStrategy = new HasActionStrategy(new VoteStack(), 1.0);
			actionStrategys.add(actStrategy);
			actStrategy = new HasActionStrategy(new ReticentExecute(), 1.0);
			actionStrategys.add(actStrategy);
		}

		// ��ł̂ݗp����s��
		if( gameInfo.getRole() == Role.SEER ){
			actStrategy = new HasActionStrategy(new BasicSeer(), 1.0);
			actionStrategys.add(actStrategy);
		}

		// ��ł̂ݗp����s��
		if( gameInfo.getRole() == Role.BODYGUARD ){
			actStrategy = new HasActionStrategy(new BasicGuard(), 1.0);
			actionStrategys.add(actStrategy);
		}


		// �T�w�c�ł̂ݗp����s��
		if( gameInfo.getRole().getTeam() == Team.WEREWOLF ){
			actStrategy = new HasActionStrategy(new RoleWeight_Wolfside(), 1.0);
			actionStrategys.add(actStrategy);
			actStrategy = new HasActionStrategy(new BasicAttack(), 1.0);
			actionStrategys.add(actStrategy);
		}

		// �T�ł̂ݗp����s��
		if( gameInfo.getRole() == Role.WEREWOLF ){
			actStrategy = new HasActionStrategy(new VoteStack(), 3.0);
			actionStrategys.add(actStrategy);
			actStrategy = new HasActionStrategy(new AttackObstacle(), 1.0);
			actionStrategys.add(actStrategy);
			actStrategy = new HasActionStrategy(new PowerPlay_Werewolf(), 1.0);
			actionStrategys.add(actStrategy);
		}

		// ���l�ł̂ݗp����s��
		if( gameInfo.getRole() == Role.POSSESSED ){
			actStrategy = new HasActionStrategy(new PowerPlay_Possessed(), 1.0);
			actionStrategys.add(actStrategy);
		}

	}


	@Override
	public void dayStart() {
		// �s���ݒ�����Z�b�g����
		actionUI.reset();
		planningVoteAgent = null;
		declaredPlanningVoteAgent = null;
	}


	@Override
	public void update(GameInfo gameInfo) {

		try{

			// ���Ԍv���J�n
			long starttime = System.currentTimeMillis();

			super.update(gameInfo);

			// �g���Q�[�����̍X�V
			agi.update(gameInfo);

			// ���t�X�V�������ȊO�ɐ����n�������s��
			if( !agi.isDayUpdate() ){

				// �������s��
				execGuess();

				// �s���\�������
				execActionReserve();

			}

			// ���Ԍv���I��
			long endtime = System.currentTimeMillis();
			long updatetime = endtime - starttime;

			// update()�̏������Ԃ��Œ��Ȃ�L��
			if( updatetime > MaxUpdateTime ){
				MaxUpdateTime = updatetime;
				MaxUpdateTiming = new StringBuilder().append(getDay()).append("���� ").append(gameInfo.getTalkList().size()).append("����").toString();
			}

		}finally{
			// Do Nothing
		}

	}


	@Override
	public Agent attack() {

		// ���点��
		if( isPrintPassageTalk ){
			putDebugMessage(actionUI.attackAgent.toString() + "���P������");
		}

		if( actionUI.attackAgent == null ){
			return null;
		}
		return Agent.getAgent(actionUI.attackAgent);
	}


	@Override
	public Agent vote() {
		if( actionUI.voteAgent == null ){
			// ���[���錾�o���Ă��Ȃ��ꍇ�A���[���悤�Ǝv���Ă����҂ɓ��[
			if( planningVoteAgent == null ){
				return null;
			}
			return Agent.getAgent(planningVoteAgent);
		}
		return Agent.getAgent(actionUI.voteAgent);
	}


	@Override
	public Agent guard() {
		if( actionUI.guardAgent == null ){
			return null;
		}
		return Agent.getAgent(actionUI.guardAgent);
	}


	@Override
	public Agent divine() {
		if( actionUI.inspectAgent == null ){
			return null;
		}
		return Agent.getAgent(actionUI.inspectAgent);
	}


	@Override
	public String whisper(){
		return null;
	}


	@Override
	public void finish() {

		// �I�����̏����𒝂点�邩
		if( isPrintFinishTalk ){
			// ���点��i���܂��j

			GameInfo gameInfo = agi.latestGameInfo;
			ArrayList<Integer> wolves = new ArrayList<Integer>();
			ArrayList<Integer> possess = new ArrayList<Integer>();
			for( int i = 1; i<= agi.gameSetting.getPlayerNum(); i++ ){
				Role role = gameInfo.getRoleMap().get( Agent.getAgent(i) );
				if( role == Role.WEREWOLF ){
					wolves.add(i);
				}else if( role == Role.POSSESSED ){
					possess.add(i);
				}
			}
			WolfsidePattern dummyWolfside = new WolfsidePattern( wolves ,possess );
			InspectedWolfsidePattern dummyInspect = latestGuess.getPattern(dummyWolfside);
			if( dummyInspect != null ){
				double dummyWolfsideScore = latestGuess.getPattern(dummyWolfside).score;
				putDebugMessage("���ۂ̓���� " + dummyWolfside.toString() + String.format(" (Score:%.5f) ", dummyWolfsideScore));
			}


			WolfsidePattern mostValidWolfside = latestGuess.getMostValidPattern().pattern;
			double mostValidWolfsideScore = latestGuess.getMostValidPattern().score;
			putDebugMessage("�ŏI�������� " + mostValidWolfside.toString() + String.format(" (Score:%.5f) ", mostValidWolfsideScore));

			// �f�o�b�O���b�Z�[�W�̏o��
			putDebugMessage("update() �Œ����Ԃ�" + MaxUpdateTime + "ms (" + MaxUpdateTiming + ")");
		}

		// �w�K�f�[�^�̍X�V
		updateLearnData();


		//TODO �t�@�C���o�͂͑��ŋ֎~�����Ȃ̂ŁA����킵���Ȃ��悤�R�����g�A�E�g���Ă����B�g���Ƃ��͖߂�
//		// �w�K�p�f�[�^�̏o��
//		if( isPutLearnData ){
//			putLeaningData();
//		}

	}


	/**
	 * �������s��
	 */
	protected void execGuess(){

		GameInfo gameInfo = agi.latestGameInfo;

		GuessManager guessManager = new GuessManager(agi.gameSetting.getPlayerNum());
		ArrayList<Guess> guesses;

		// �����헪�ւ̈����̐ݒ�
		GuessStrategyArgs args = new GuessStrategyArgs();
		args.agi = agi;

		// �e�����헪�N���X���琄�����擾
		for( HasGuessStrategy hasStrategy : guessStrategys ){
			guesses = hasStrategy.strategy.getGuessList(args);
			guessManager.addGuess(ReceivedGuess.newGuesses(guesses, hasStrategy.strategy));
		}

		// �����̐������番�͌��ʂ��擾����
		AnalysisOfGuess aguess = new AnalysisOfGuess(agi.gameSetting.getPlayerNum()  , agi.selfViewInfo.wolfsidePatterns, guessManager);

		// �ŐV�̐����Ƃ��Ċi�[����
		latestGuess = aguess;

		// ���点��
		if( isPrintPassageTalk ){
			WolfsidePattern mostValidWolfside = aguess.getMostValidPattern().pattern;
			putDebugMessage(mostValidWolfside.toString() + " ��������", gameInfo.getDay(), gameInfo.getTalkList().size());
		}

	}


	/**
	 * �s���\����s��
	 */
	protected void execActionReserve(){

		RequestManager ReqManager = new RequestManager();

		// �s���헪�ւ̈����̐ݒ�
		ActionStrategyArgs args = new ActionStrategyArgs();
		args.agi = agi;
		args.view = agi.selfViewInfo;
		args.aguess = latestGuess;

		// �e�s���헪�N���X����s���v�����擾
		for( HasActionStrategy hasStrategy : actionStrategys ){
			ArrayList<Request> Requests = hasStrategy.strategy.getRequests(args);
			ReqManager.addRequest(ReceivedRequest.newRequests(Requests, hasStrategy.strategy));
		}

		// �s���v�����W�v���A�擾����
		AnalysisOfRequest calcRequest = new AnalysisOfRequest(agi.gameSetting.getPlayerNum(), ReqManager.allRequest);


		// �e�s���̑ΏۂƂ��čł��Ó��Ȑl�����擾
		int voteAgentNo = calcRequest.getMaxVoteRequest().agentNo;
		int guardAgentNo = calcRequest.getMaxGuardRequest().agentNo;
		int inspectAgentNo = calcRequest.getMaxInspectRequest().agentNo;
		int attackAgentNo = calcRequest.getMaxAttackRequest().agentNo;

		// ���[�\��Ƃ��ċL���i���ۂ̓��[��Z�b�g�͓��[���錾�������ɍs���j
		planningVoteAgent = voteAgentNo;

		// ���[�ȊO�̊e�s�����Z�b�g
		actionUI.guardAgent = guardAgentNo;
		actionUI.inspectAgent = inspectAgentNo;
		actionUI.attackAgent = attackAgentNo;

		// �ŐV�̍s���v���Ƃ��Ċi�[����
		latestRequest = calcRequest;

	}


	/**
	 * ���CO���K�v���̔��f���s��
	 * @return
	 */
	protected boolean isAvoidance(){

		GameInfo gameInfo = agi.latestGameInfo;

		// ���[�錾�ς݃G�[�W�F���g�̐�
		int voteAgentCount = 0;

		// �G�[�W�F���g���̓��[�\������擾����
		Integer[] voteTarget = new Integer[agi.gameSetting.getPlayerNum() + 1];
		for( Agent agent : gameInfo.getAliveAgentList() ){
			voteTarget[agent.getAgentIdx()] = agi.getSaidVoteAgent(agent.getAgentIdx());

			// ���[�錾�ς݃G�[�W�F���g�̃J�E���g
			if( voteTarget[agent.getAgentIdx()] != null ){
				voteAgentCount++;
			}
		}

		// �G�[�W�F���g���̔퓊�[�����擾����
		int[] voteReceiveNum = new int[agi.gameSetting.getPlayerNum() + 1];
		for( int i = 1; i < voteTarget.length; i++ ){
			if( voteTarget[i] != null ){
				voteReceiveNum[voteTarget[i]]++;
			}
		}

		// �ő��[�̃G�[�W�F���g�̕[�����擾����
		int maxVoteCount = 0;
		for( int i = 1; i < voteTarget.length; i++ ){
			if( voteReceiveNum[i] > maxVoteCount ){
				maxVoteCount = voteReceiveNum[i];
			}
		}


		// �݂肪�������Ȃ������͉��CO�̕K�v�Ȃ�
		if( gameInfo.getDay() < 1 ){
			return false;
		}

		// 3�����ڂ܂ł͉��CO���Ȃ�(����܂Ŏ�����Over��Ԃ��Ȃ��悤�ɂ��邱��)
		if( agi.getMyTalkNum() < 2 ){
			return false;
		}

		// ���[�錾�҂����Ȃ���Ή��CO�̕K�v�Ȃ�
		if( voteAgentCount < gameInfo.getAliveAgentList().size() * 0.65 ){
			return false;
		}

		// �ő��[�𓾂Ă���Ή��CO���K�v
		if( voteReceiveNum[gameInfo.getAgent().getAgentIdx()] >= maxVoteCount ){
			return true;
		}

		return false;

	}


	/**
	 * �^�����b�����͂��擾����(���b�����̕ۑ����s��)
	 * @return
	 */
	protected String getSuspicionTalkString(){

		// �^���ׂ��l�����擾����
		Integer suspicionAgentNo = getSuspicionTalkAgentNo();

		// �^���ׂ��l��������Θb��
		if( suspicionAgentNo != null ){
			//TODO �L�������Ɏ����̔�����ǂ��Ď擾�����������݌v�v�z�Ƃ��Ă͗ǂ�
			// �^���ςƂ��ċL������
			agi.talkedSuspicionAgentList.add(suspicionAgentNo);

			// �������e��Ԃ�
			String ret = TemplateTalkFactory.estimate( Agent.getAgent(suspicionAgentNo), Role.WEREWOLF );
			return ret;
		}

		// ���̔������s��Ȃ��ꍇ�Anull��Ԃ�
		return null;

	}


	/**
	 * �M�p���b�����͂��擾����(���b�����̕ۑ����s��)
	 * @return
	 */
	protected String getTrustTalkString(){

		// �ŐV�̕��͌��ʂ��擾����
		AnalysisOfGuess aguess = latestGuess;

		// �ł��Ó��ȘT�w�c�̃X�R�A���擾����
		double mostValidWolfsideScore = aguess.getMostValidPattern().score;

		// �������̑S�G�[�W�F���g�𑖍�
		for( Agent agent : agi.latestGameInfo.getAliveAgentList() ){

			// �����̓X�L�b�v
			if( agent.equals(getMe()) ){
				continue;
			}

			int agentNo = agent.getAgentIdx();

			InspectedWolfsidePattern wolfPattern = aguess.getMostValidWolfPattern(agentNo);
			InspectedWolfsidePattern posPattern = aguess.getMostValidPossessedPattern(agentNo);

			double wolfScore = 0.0;
			double posScore = 0.0;
			if( wolfPattern != null ){
				wolfScore = wolfPattern.score;
			}
			if( posPattern != null ){
				posScore = posPattern.score;
			}

			// �T�E���l���ꂼ��̍ő�X�R�A��������
			if( wolfScore < mostValidWolfsideScore * 0.8 &&
			    posScore < mostValidWolfsideScore * 0.8 ){

				if( !agi.talkedTrustAgentList.contains(agentNo) ){

					// �M�p�ςƂ��ċL������
					agi.talkedTrustAgentList.add(agentNo);

					Role role;

					// ����CO���Ă��邩
					if( agi.agentState[agentNo].comingOutRole != null ){
						// CO������E�Ɛ�������
						role = agi.agentState[agentNo].comingOutRole;
					}else{
						// ���l�Ɛ�������
						role = Role.VILLAGER;
					}

					// �������e��Ԃ�
					String ret = TemplateTalkFactory.estimate(agent, role);
					return ret;
				}
			}

		}

		// ���̔������s��Ȃ��ꍇ�Anull��Ԃ�
		return null;

	}


	/**
	 * �^����Ƃ��Ęb���ׂ��G�[�W�F���g�ԍ����擾����
	 * @return �G�[�W�F���g�ԍ�(Null�̏ꍇ���L)
	 */
	protected Integer getSuspicionTalkAgentNo(){

		// �ŐV�̕��͌��ʂ��擾����
		AnalysisOfGuess aguess = latestGuess;

		// �ł��Ó��ȘT�w�c���擾����
		WolfsidePattern mostValidWolfside = aguess.getMostValidPattern().pattern;

		// �^����Ƃ��Ĕ������Ă��Ȃ��T���擾����
		ArrayList<Integer> wolves = new ArrayList<Integer>();
		for( Integer wolf : mostValidWolfside.wolfAgentNo ){
			if( !agi.talkedSuspicionAgentList.contains(wolf) ){
				wolves.add(wolf);
			}
		}

		// �Ώەs�ݎ���Null��Ԃ�
		if( wolves.isEmpty() ){
			return null;
		}

		return wolves.get(0);

	}


	/**
	 * �w�K�f�[�^�̍X�V
	 */
 	protected void updateLearnData(){

		GameInfo gameInfo = agi.latestGameInfo;
		WolfsidePattern lastGuessWolfPattern = latestGuess.getMostValidPattern().pattern;

		// �Q�[����+1
		LearnData.gameCount += 1;

		// ���H�J�E���g
		switch( agi.agentState[gameInfo.getAgent().getAgentIdx()].causeofDeath ){
			case ALIVE:
				LearnData.aliveCount++;
				break;
			case ATTACKED:
				LearnData.attackCount++;
				break;
			case EXECUTED:
				LearnData.executeCount++;
				break;
			default:
				break;
		}

		// �T���𐔂̃J�E���g
		int wolfCorrectCount = 0;
		for( int wolf : lastGuessWolfPattern.wolfAgentNo ){
			if( gameInfo.getRoleMap().get(Agent.getAgent(wolf)) == Role.WEREWOLF ){
				wolfCorrectCount++;
			}
		}
		LearnData.wolfCorrectCount[wolfCorrectCount]++;

		// update()�̏������Ԃ��Œ��Ȃ�L��
		if( MaxUpdateTime > LearnData.maxUpdateTime ){
			LearnData.maxUpdateTime = MaxUpdateTime;
		}
	}


//TODO �t�@�C���o�͂͑��ŋ֎~�����Ȃ̂ŁA����킵���Ȃ��悤�R�����g�A�E�g���Ă����B�g���Ƃ��͖߂�
//	/**
//	 * �w�K�p�f�[�^�̏o��
//	 */
//	protected void putLeaningData(){
//
//		// ���v�p�f�[�^�̏o��
//		try{
//
//			File file = new File("C:\\Temp\\aiwolf_leaning\\hogehoge.csv");
//			FileOutputStream fos = new FileOutputStream(file,true);
//			OutputStreamWriter osw = new OutputStreamWriter(fos);
//			PrintWriter pw = new PrintWriter(osw);
//
//			pw.print("");
//
//			pw.println();
//
//			pw.close();
//
//		}catch(IOException e){
//			System.out.println(e);
//		}
//
//	}


	/**
	 * �f�o�b�O���b�Z�[�W�𒝂点�܂�
	 * @param str
	 * @param day
	 * @param talkid
	 */
	protected void putDebugMessage(String str){

		GameInfo gameInfo = agi.latestGameInfo;

		System.out.println("(Agent" + gameInfo.getAgent().getAgentIdx() + ") "
		                   + "�� "
		                   + str
		                   + "");

	}


	/**
	 * �f�o�b�O���b�Z�[�W�𒝂点�܂�
	 * @param str
	 * @param day
	 * @param talkid
	 */
	protected void putDebugMessage(String str, int day, int talkid){

		GameInfo gameInfo = agi.latestGameInfo;

		System.out.println("(Agent" + gameInfo.getAgent().getAgentIdx() + ") "
		                   + "�� "
		                   + "(" + day + "�� " + talkid + "����) "
		                   + str
		                   + "");

	}


}
