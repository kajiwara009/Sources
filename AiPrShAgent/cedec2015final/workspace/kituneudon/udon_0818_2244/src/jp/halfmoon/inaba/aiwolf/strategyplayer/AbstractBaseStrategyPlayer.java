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
 * 全ての役職のベースとなるクラス
 */
public abstract class AbstractBaseStrategyPlayer extends AbstractRole{

	/** 拡張ゲーム情報 */
	protected AdvanceGameInfo agi;

	/** 行動を設定するための擬似UI */
	protected ActionUI actionUI = new ActionUI();

	/** 今日投票しようと思っているプレイヤー */
	protected Integer planningVoteAgent;

	/** 自分が最後に宣言した「投票しようと思っているプレイヤー」 */
	protected Integer declaredPlanningVoteAgent;

	/** 自分が最後に行った推理 */
	protected AnalysisOfGuess latestGuess;

	/** 自分が最後に行った行動要求 */
	protected AnalysisOfRequest latestRequest;

	/** 宣言済みの騙る役職 */
	protected Role declaredFakeRole;

	/** 自分が最後に宣言した「襲撃しようと思っているプレイヤー」 */
	protected Integer declaredPlanningAttackAgent;

	/** CO済か */
	protected boolean isCameOut;


	/** 保有する推理戦略 */
	protected ArrayList<HasGuessStrategy> guessStrategys = new ArrayList<HasGuessStrategy>();

	/** 保有する行動戦略 */
	protected ArrayList<HasActionStrategy> actionStrategys = new ArrayList<HasActionStrategy>();


	// 調整機能

	/** 途中経過を出力するか */
	protected final boolean isPrintPassageTalk = false;

	/** 終了結果を出力するか */
	protected final boolean isPrintFinishTalk = false;

	/** 学習用のデータを出力するか */
	protected final boolean isPutLearnData = false;



	// デバッグ用

	/** Update()にかかった時間のうち最長のもの（デバッグ用） */
	protected long MaxUpdateTime = Long.MIN_VALUE;

	/** Update()にかかった時間が最長のタイミング（デバッグ用） */
	protected String MaxUpdateTiming;


	@Override
	public void initialize(GameInfo gameInfo, GameSetting gameSetting) {
		super.initialize(gameInfo, gameSetting);

		// 拡張ゲーム情報の初期化
		agi = new AdvanceGameInfo(gameInfo, gameSetting);

		// 推理戦略を設定
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

		// 村陣営でのみ用いる推理
		if( gameInfo.getRole().getTeam() == Team.VILLAGER ){
			guessStrategy = new HasGuessStrategy(new Noise(), 1.0);
			guessStrategys.add(guessStrategy);
			guessStrategy = new HasGuessStrategy(new VoteRecent(), 1.0);
			guessStrategys.add(guessStrategy);
			guessStrategy = new HasGuessStrategy(new JudgeRecent(), 1.0);
			guessStrategys.add(guessStrategy);
		}

		// 狼陣営でのみ用いる推理
		if( gameInfo.getRole().getTeam() == Team.WEREWOLF ){
			guessStrategy = new HasGuessStrategy(new Noise(), 0.5);
			guessStrategys.add(guessStrategy);
			guessStrategy = new HasGuessStrategy(new VoteRecent(), 0.5);
			guessStrategys.add(guessStrategy);
			guessStrategy = new HasGuessStrategy(new JudgeRecent_WolfSide(), 1.0);
			guessStrategys.add(guessStrategy);
		}

		// 狼でのみ用いる推理
		if( gameInfo.getRole() == Role.WEREWOLF ){
			guessStrategy = new HasGuessStrategy(new Favor(), 1.0);
			guessStrategys.add(guessStrategy);
		}


		// 行動戦略を設定
		HasActionStrategy actStrategy;
		actStrategy = new HasActionStrategy(new FixInfo(), 1.0);
		actionStrategys.add(actStrategy);
		actStrategy = new HasActionStrategy(new FromGuess(), 1.0);
		actionStrategys.add(actStrategy);

		// 村陣営でのみ用いる行動
		if( gameInfo.getRole().getTeam() == Team.VILLAGER ){
			actStrategy = new HasActionStrategy(new RoleWeight(), 1.0);
			actionStrategys.add(actStrategy);
			actStrategy = new HasActionStrategy(new VoteStack(), 1.0);
			actionStrategys.add(actStrategy);
			actStrategy = new HasActionStrategy(new ReticentExecute(), 1.0);
			actionStrategys.add(actStrategy);
		}

		// 占でのみ用いる行動
		if( gameInfo.getRole() == Role.SEER ){
			actStrategy = new HasActionStrategy(new BasicSeer(), 1.0);
			actionStrategys.add(actStrategy);
		}

		// 狩でのみ用いる行動
		if( gameInfo.getRole() == Role.BODYGUARD ){
			actStrategy = new HasActionStrategy(new BasicGuard(), 1.0);
			actionStrategys.add(actStrategy);
		}


		// 狼陣営でのみ用いる行動
		if( gameInfo.getRole().getTeam() == Team.WEREWOLF ){
			actStrategy = new HasActionStrategy(new RoleWeight_Wolfside(), 1.0);
			actionStrategys.add(actStrategy);
			actStrategy = new HasActionStrategy(new BasicAttack(), 1.0);
			actionStrategys.add(actStrategy);
		}

		// 狼でのみ用いる行動
		if( gameInfo.getRole() == Role.WEREWOLF ){
			actStrategy = new HasActionStrategy(new VoteStack(), 3.0);
			actionStrategys.add(actStrategy);
			actStrategy = new HasActionStrategy(new AttackObstacle(), 1.0);
			actionStrategys.add(actStrategy);
			actStrategy = new HasActionStrategy(new PowerPlay_Werewolf(), 1.0);
			actionStrategys.add(actStrategy);
		}

		// 狂人でのみ用いる行動
		if( gameInfo.getRole() == Role.POSSESSED ){
			actStrategy = new HasActionStrategy(new PowerPlay_Possessed(), 1.0);
			actionStrategys.add(actStrategy);
		}

	}


	@Override
	public void dayStart() {
		// 行動設定をリセットする
		actionUI.reset();
		planningVoteAgent = null;
		declaredPlanningVoteAgent = null;
	}


	@Override
	public void update(GameInfo gameInfo) {

		try{

			// 時間計測開始
			long starttime = System.currentTimeMillis();

			super.update(gameInfo);

			// 拡張ゲーム情報の更新
			agi.update(gameInfo);

			// 日付更新処理時以外に推理系処理を行う
			if( !agi.isDayUpdate() ){

				// 推理を行う
				execGuess();

				// 行動予約を入れる
				execActionReserve();

			}

			// 時間計測終了
			long endtime = System.currentTimeMillis();
			long updatetime = endtime - starttime;

			// update()の処理時間が最長なら記憶
			if( updatetime > MaxUpdateTime ){
				MaxUpdateTime = updatetime;
				MaxUpdateTiming = new StringBuilder().append(getDay()).append("日目 ").append(gameInfo.getTalkList().size()).append("発言").toString();
			}

		}finally{
			// Do Nothing
		}

	}


	@Override
	public Agent attack() {

		// 喋らせる
		if( isPrintPassageTalk ){
			putDebugMessage(actionUI.attackAgent.toString() + "を襲撃する");
		}

		if( actionUI.attackAgent == null ){
			return null;
		}
		return Agent.getAgent(actionUI.attackAgent);
	}


	@Override
	public Agent vote() {
		if( actionUI.voteAgent == null ){
			// 投票先を宣言出来ていない場合、投票しようと思っていた者に投票
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

		// 終了時の処理を喋らせるか
		if( isPrintFinishTalk ){
			// 喋らせる（おまけ）

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
				putDebugMessage("実際の内訳は " + dummyWolfside.toString() + String.format(" (Score:%.5f) ", dummyWolfsideScore));
			}


			WolfsidePattern mostValidWolfside = latestGuess.getMostValidPattern().pattern;
			double mostValidWolfsideScore = latestGuess.getMostValidPattern().score;
			putDebugMessage("最終日推理は " + mostValidWolfside.toString() + String.format(" (Score:%.5f) ", mostValidWolfsideScore));

			// デバッグメッセージの出力
			putDebugMessage("update() 最長時間は" + MaxUpdateTime + "ms (" + MaxUpdateTiming + ")");
		}

		// 学習データの更新
		updateLearnData();


		//TODO ファイル出力は大会で禁止事項なので、紛らわしくないようコメントアウトしておく。使うときは戻す
//		// 学習用データの出力
//		if( isPutLearnData ){
//			putLeaningData();
//		}

	}


	/**
	 * 推理を行う
	 */
	protected void execGuess(){

		GameInfo gameInfo = agi.latestGameInfo;

		GuessManager guessManager = new GuessManager(agi.gameSetting.getPlayerNum());
		ArrayList<Guess> guesses;

		// 推理戦略への引数の設定
		GuessStrategyArgs args = new GuessStrategyArgs();
		args.agi = agi;

		// 各推理戦略クラスから推理を取得
		for( HasGuessStrategy hasStrategy : guessStrategys ){
			guesses = hasStrategy.strategy.getGuessList(args);
			guessManager.addGuess(ReceivedGuess.newGuesses(guesses, hasStrategy.strategy));
		}

		// 複数の推理から分析結果を取得する
		AnalysisOfGuess aguess = new AnalysisOfGuess(agi.gameSetting.getPlayerNum()  , agi.selfViewInfo.wolfsidePatterns, guessManager);

		// 最新の推理として格納する
		latestGuess = aguess;

		// 喋らせる
		if( isPrintPassageTalk ){
			WolfsidePattern mostValidWolfside = aguess.getMostValidPattern().pattern;
			putDebugMessage(mostValidWolfside.toString() + " が怪しい", gameInfo.getDay(), gameInfo.getTalkList().size());
		}

	}


	/**
	 * 行動予約を行う
	 */
	protected void execActionReserve(){

		RequestManager ReqManager = new RequestManager();

		// 行動戦略への引数の設定
		ActionStrategyArgs args = new ActionStrategyArgs();
		args.agi = agi;
		args.view = agi.selfViewInfo;
		args.aguess = latestGuess;

		// 各行動戦略クラスから行動要求を取得
		for( HasActionStrategy hasStrategy : actionStrategys ){
			ArrayList<Request> Requests = hasStrategy.strategy.getRequests(args);
			ReqManager.addRequest(ReceivedRequest.newRequests(Requests, hasStrategy.strategy));
		}

		// 行動要求を集計し、取得する
		AnalysisOfRequest calcRequest = new AnalysisOfRequest(agi.gameSetting.getPlayerNum(), ReqManager.allRequest);


		// 各行動の対象として最も妥当な人物を取得
		int voteAgentNo = calcRequest.getMaxVoteRequest().agentNo;
		int guardAgentNo = calcRequest.getMaxGuardRequest().agentNo;
		int inspectAgentNo = calcRequest.getMaxInspectRequest().agentNo;
		int attackAgentNo = calcRequest.getMaxAttackRequest().agentNo;

		// 投票予定として記憶（実際の投票先セットは投票先を宣言した時に行う）
		planningVoteAgent = voteAgentNo;

		// 投票以外の各行動をセット
		actionUI.guardAgent = guardAgentNo;
		actionUI.inspectAgent = inspectAgentNo;
		actionUI.attackAgent = attackAgentNo;

		// 最新の行動要求として格納する
		latestRequest = calcRequest;

	}


	/**
	 * 回避COが必要かの判断を行う
	 * @return
	 */
	protected boolean isAvoidance(){

		GameInfo gameInfo = agi.latestGameInfo;

		// 投票宣言済みエージェントの数
		int voteAgentCount = 0;

		// エージェント毎の投票予告先を取得する
		Integer[] voteTarget = new Integer[agi.gameSetting.getPlayerNum() + 1];
		for( Agent agent : gameInfo.getAliveAgentList() ){
			voteTarget[agent.getAgentIdx()] = agi.getSaidVoteAgent(agent.getAgentIdx());

			// 投票宣言済みエージェントのカウント
			if( voteTarget[agent.getAgentIdx()] != null ){
				voteAgentCount++;
			}
		}

		// エージェント毎の被投票数を取得する
		int[] voteReceiveNum = new int[agi.gameSetting.getPlayerNum() + 1];
		for( int i = 1; i < voteTarget.length; i++ ){
			if( voteTarget[i] != null ){
				voteReceiveNum[voteTarget[i]]++;
			}
		}

		// 最多票のエージェントの票数を取得する
		int maxVoteCount = 0;
		for( int i = 1; i < voteTarget.length; i++ ){
			if( voteReceiveNum[i] > maxVoteCount ){
				maxVoteCount = voteReceiveNum[i];
			}
		}


		// 吊りが発生しない初日は回避COの必要なし
		if( gameInfo.getDay() < 1 ){
			return false;
		}

		// 3発言目までは回避COしない(それまで自分がOverを返さないようにすること)
		if( agi.getMyTalkNum() < 2 ){
			return false;
		}

		// 投票宣言者が少なければ回避COの必要なし
		if( voteAgentCount < gameInfo.getAliveAgentList().size() * 0.65 ){
			return false;
		}

		// 最多票を得ていれば回避COが必要
		if( voteReceiveNum[gameInfo.getAgent().getAgentIdx()] >= maxVoteCount ){
			return true;
		}

		return false;

	}


	/**
	 * 疑い先を話す文章を取得する(発話履歴の保存も行う)
	 * @return
	 */
	protected String getSuspicionTalkString(){

		// 疑うべき人物を取得する
		Integer suspicionAgentNo = getSuspicionTalkAgentNo();

		// 疑うべき人物がいれば話す
		if( suspicionAgentNo != null ){
			//TODO 記憶せずに自分の発言を追って取得させた方が設計思想としては良い
			// 疑い済として記憶する
			agi.talkedSuspicionAgentList.add(suspicionAgentNo);

			// 発言内容を返す
			String ret = TemplateTalkFactory.estimate( Agent.getAgent(suspicionAgentNo), Role.WEREWOLF );
			return ret;
		}

		// この発言を行わない場合、nullを返す
		return null;

	}


	/**
	 * 信用先を話す文章を取得する(発話履歴の保存も行う)
	 * @return
	 */
	protected String getTrustTalkString(){

		// 最新の分析結果を取得する
		AnalysisOfGuess aguess = latestGuess;

		// 最も妥当な狼陣営のスコアを取得する
		double mostValidWolfsideScore = aguess.getMostValidPattern().score;

		// 生存中の全エージェントを走査
		for( Agent agent : agi.latestGameInfo.getAliveAgentList() ){

			// 自分はスキップ
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

			// 狼・狂人それぞれの最大スコアが小さい
			if( wolfScore < mostValidWolfsideScore * 0.8 &&
			    posScore < mostValidWolfsideScore * 0.8 ){

				if( !agi.talkedTrustAgentList.contains(agentNo) ){

					// 信用済として記憶する
					agi.talkedTrustAgentList.add(agentNo);

					Role role;

					// 何かCOしているか
					if( agi.agentState[agentNo].comingOutRole != null ){
						// COした役職と推理する
						role = agi.agentState[agentNo].comingOutRole;
					}else{
						// 村人と推理する
						role = Role.VILLAGER;
					}

					// 発言内容を返す
					String ret = TemplateTalkFactory.estimate(agent, role);
					return ret;
				}
			}

		}

		// この発言を行わない場合、nullを返す
		return null;

	}


	/**
	 * 疑い先として話すべきエージェント番号を取得する
	 * @return エージェント番号(Nullの場合も有)
	 */
	protected Integer getSuspicionTalkAgentNo(){

		// 最新の分析結果を取得する
		AnalysisOfGuess aguess = latestGuess;

		// 最も妥当な狼陣営を取得する
		WolfsidePattern mostValidWolfside = aguess.getMostValidPattern().pattern;

		// 疑い先として発言していない狼を取得する
		ArrayList<Integer> wolves = new ArrayList<Integer>();
		for( Integer wolf : mostValidWolfside.wolfAgentNo ){
			if( !agi.talkedSuspicionAgentList.contains(wolf) ){
				wolves.add(wolf);
			}
		}

		// 対象不在時はNullを返す
		if( wolves.isEmpty() ){
			return null;
		}

		return wolves.get(0);

	}


	/**
	 * 学習データの更新
	 */
 	protected void updateLearnData(){

		GameInfo gameInfo = agi.latestGameInfo;
		WolfsidePattern lastGuessWolfPattern = latestGuess.getMostValidPattern().pattern;

		// ゲーム数+1
		LearnData.gameCount += 1;

		// 末路カウント
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

		// 狼正解数のカウント
		int wolfCorrectCount = 0;
		for( int wolf : lastGuessWolfPattern.wolfAgentNo ){
			if( gameInfo.getRoleMap().get(Agent.getAgent(wolf)) == Role.WEREWOLF ){
				wolfCorrectCount++;
			}
		}
		LearnData.wolfCorrectCount[wolfCorrectCount]++;

		// update()の処理時間が最長なら記憶
		if( MaxUpdateTime > LearnData.maxUpdateTime ){
			LearnData.maxUpdateTime = MaxUpdateTime;
		}
	}


//TODO ファイル出力は大会で禁止事項なので、紛らわしくないようコメントアウトしておく。使うときは戻す
//	/**
//	 * 学習用データの出力
//	 */
//	protected void putLeaningData(){
//
//		// 統計用データの出力
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
	 * デバッグメッセージを喋らせます
	 * @param str
	 * @param day
	 * @param talkid
	 */
	protected void putDebugMessage(String str){

		GameInfo gameInfo = agi.latestGameInfo;

		System.out.println("(Agent" + gameInfo.getAgent().getAgentIdx() + ") "
		                   + "＞ "
		                   + str
		                   + "");

	}


	/**
	 * デバッグメッセージを喋らせます
	 * @param str
	 * @param day
	 * @param talkid
	 */
	protected void putDebugMessage(String str, int day, int talkid){

		GameInfo gameInfo = agi.latestGameInfo;

		System.out.println("(Agent" + gameInfo.getAgent().getAgentIdx() + ") "
		                   + "＞ "
		                   + "(" + day + "日 " + talkid + "発言) "
		                   + str
		                   + "");

	}


}
