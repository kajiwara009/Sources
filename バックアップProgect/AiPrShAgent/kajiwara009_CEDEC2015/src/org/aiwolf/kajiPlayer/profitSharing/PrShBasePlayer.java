package org.aiwolf.kajiPlayer.profitSharing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.aiwolf.client.base.player.AbstractRole;
import org.aiwolf.client.lib.TemplateTalkFactory;
import org.aiwolf.client.lib.Utterance;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Judge;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Talk;
import org.aiwolf.common.net.GameInfo;
import org.aiwolf.common.net.GameSetting;
import org.aiwolf.kajiClient.lib.Strategies;
import org.aiwolf.laern.lib.Action;
import org.aiwolf.laern.lib.COAction;
import org.aiwolf.laern.lib.Information;
import org.aiwolf.laern.lib.LearningControler;
import org.aiwolf.laern.lib.Observe;
import org.aiwolf.laern.lib.ObservePool;
import org.aiwolf.laern.lib.SelectAction;
import org.aiwolf.laern.lib.Situation;
import org.aiwolf.laern.lib.SituationPool;
import org.aiwolf.laern.lib.LearningControler.Strategy;

import com.gmail.kajiwara009.Learning.SelectStrategy;
import com.gmail.kajiwara009.collectionOperation.CollectionOperator;
import com.gmail.kajiwara009.util.TwoValue;

public class PrShBasePlayer extends AbstractRole {
	//学習データの記録，読み込みに必要なフィールド
	
	protected String filePath = "kajiwara009/resources/";
	
	protected ObservePool observePool = new ObservePool();
	protected SituationPool situationPool = new SituationPool();
	protected String inputFileNameObserve = "inputO.txt";
	protected String inputFileNameSituation = "inputS.txt";
	protected String outputFileNameObsereve = "outputO.txt";
	protected String outputFileNameSituation = "outputS.txt";
	
	//ゲーム中に用いるフィールド
	protected Role myRole;
	protected Information info = new Information();
	protected boolean isLatestInfo = true;
	protected int readTalkNumber = 0;
	protected LearningControler learningControler = new LearningControler();
	protected boolean isCO = false;
	protected List<Judge> toldJudge = new ArrayList<Judge>();
	protected List<Judge> nonToldJudge = new ArrayList<Judge>();
	
	
	
	//学習のために用いるフィールド
	//ActionをとるたびにInformationを複製して保存する．
	protected boolean isLearning = true;
	protected Map<Information, COAction> coActionRecord = new HashMap<Information, COAction>();
//	protected Map<Information, SelectAction> selectActionRecord = new HashMap<Information, SelectAction>();
	protected Map<Information, TwoValue<SelectAction, Action>> selectActionRecord = new HashMap<Information, TwoValue<SelectAction,Action>>();

	
	
	
	
	
	
	public PrShBasePlayer(Role role) {
		myRole = role;
		observePool.importObserves(filePath, inputFileNameObserve, role);
		situationPool.importSituations(filePath, inputFileNameSituation, role);
	}
	
	public void outputData(){
		observePool.outputObserves(outputFileNameObsereve, getMyRole());
		situationPool.outputSituations(outputFileNameSituation, getMyRole());
	}
	
	@Override
	public void initialize(GameInfo gameInfo, GameSetting gameSetting){
		super.initialize(gameInfo, gameSetting);
		info = new Information();
		info.setPlayers(new HashSet<Agent>(gameInfo.getAgentList()));
//		Observe.setSituationPool(situationPool);
		selectActionRecord.clear();
		coActionRecord.clear();
		toldJudge.clear();
		nonToldJudge.clear();
		isCO = false;
	}
	
	@Override
	public void update(GameInfo gameInfo){
		super.update(gameInfo);
		
		List<Talk> talks = getLatestDayGameInfo().getTalkList();
		
		for( ; readTalkNumber < talks.size(); readTalkNumber++){
			Talk talk = talks.get(readTalkNumber);
			Utterance utterance = new Utterance(talk.getContent());
			switch (utterance.getTopic()) {
			case COMINGOUT:
				comingoutTalkDeal(talk, utterance);
				break;

			case DIVINED:
				divinedTalkDeal(talk, utterance);
				break;

			case INQUESTED:
				inquestedTalkDeal(talk, utterance);
				break;

			case VOTE:
				break;
			//上記以外
			default:
				break;
			}
		}
		//TODO 会話の処理によるInformationの変形．
		
	}
	
	public void comingoutTalkDeal(Talk talk, Utterance utterance){
		Agent talker = talk.getAgent();
		Agent target = utterance.getTarget();
		//発話者とCO対象が同じであれば
		if(talker != null && talker.equals(target)){
			Role preRoleInfo = info.getCOmap().get(talker);
			if(preRoleInfo == null || preRoleInfo == Role.VILLAGER){
				updateCOInfo(talk.getAgent(), utterance.getRole());
			}
		}
	}
	
	protected void updateCOInfo(Agent talker, Role role){
		info.getCOmap().put(talker, role);
		if(role == Role.SEER){
			info.getSeerCOList().add(talker);
		}else if(role == Role.MEDIUM){
			info.getMediumCOList().add(talker);
		}
		isLatestInfo = false;
	}
	
	public void divinedTalkDeal(Talk talk, Utterance utterance){
		judgeDeal(talk, utterance, Role.SEER);
	}
	
	public void inquestedTalkDeal(Talk talk, Utterance utterance){
		judgeDeal(talk, utterance, Role.MEDIUM);
	}
	
	
	
	protected void judgeDeal(Talk talk, Utterance utterance, Role role){
		Role preCO = info.getCOmap().get(talk.getAgent());
		if(preCO == null || preCO == Role.VILLAGER){
			updateCOInfo(talk.getAgent(), role);
		}else if(preCO != role){
			//占い師以外の能力者が言ってたら無視
			return;
		}
		
		Judge judge = new Judge(getDay(), talk.getAgent(), utterance.getTarget(), utterance.getResult());
		updateJudgeInfo(talk.getAgent(), judge);
	}
	
	protected void updateJudgeInfo(Agent talker, Judge judge){
		if(!info.hasJudge(talker, judge)){
			Map<Agent, Set<Judge>> judgeSet = info.getJudgeSets();
			if(judgeSet.get(talker) == null){
				judgeSet.put(talker, new HashSet<Judge>());
			}
			judgeSet.get(talker).add(judge);
			isLatestInfo = false;
		}
	}



	@Override
	public void dayStart() {
		readTalkNumber = 0;
		
		Agent attacked = getLatestDayGameInfo().getAttackedAgent();
		Agent executed = getLatestDayGameInfo().getExecutedAgent();
		if(attacked != null){
			info.getAttacked().add(attacked);
			isLatestInfo = false;
		}
		if(executed != null){
			info.getExecuted().add(executed);
			isLatestInfo = false;
		}
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public String talk() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	public String whisper() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	public Agent vote() {
		return getDefaultAction(Action.VOTE);
	}
	
	protected Agent getDefaultAction(Action act){
		Observe obs = info.getObserve(observePool);
		Map<Situation, Integer> situations = obs.getDecodeSituationMap(situationPool);
		
		//TODO 自分を黒判定出したSituationはあり得ないっていうのを入れる．
		
		Set<SelectAction> selActs = SelectAction.getAbleSelectActions(info);
		SelectAction action = decideAction(situations, selActs, act);
		return getAgentFromAction(action);
	}
	
	protected SelectAction decideAction(Map<Situation, Integer> situations, Set<SelectAction> selActs, Action act) {
		SelectAction answer = null;
		if(learningControler.getStrategy() == Strategy.RANDOM || situations.size() == 0){
			answer = CollectionOperator.getRandom(selActs);
		}else{
			//平均行動価値を計算 map selact, float
			Map<SelectAction, Float> actionValueMap = getAvarageActionValues(situations, selActs, act);
			//場合分け
			switch (learningControler.getStrategy()) {
			case GREEDY:
				answer = SelectStrategy.greedyselectF(actionValueMap, learningControler.getEpsilon());
				break;
			case MAX:
				answer = SelectStrategy.getMaxFloatValueKey(actionValueMap);
				break;
			case ROULET:
				answer = SelectStrategy.rouletSelectF(actionValueMap);
				break;
			case SOFTMAX:
				answer = SelectStrategy.softMaxSelect(actionValueMap, learningControler.getTemperature());
				break;
			default:
				break;
			}
		}
		TwoValue<SelectAction, Action> actionSet = new TwoValue<SelectAction, Action>(answer, act);
		selectActionRecord.put(info.copy(), actionSet);
		return answer;
	}
	
	
	
	protected Map<SelectAction, Float> getAvarageActionValues( Map<Situation, Integer> situations, Set<SelectAction> selActs, Action act){
		Map<SelectAction, Float> avevalues = new HashMap<SelectAction, Float>();
		for(SelectAction selAct: selActs){
			float ave = 0f;
			for(Entry<Situation, Integer> set: situations.entrySet()){
				//TODO どの行動かによって変える
				float value;
				switch (act) {
				case VOTE:
					value = set.getKey().getVoteValue(selAct);
					break;
				case DIVINE:
					value = set.getKey().getDivineValue(selAct);
				case GUARD:
					value = set.getKey().getGuardValue(selAct);
				default:
					value = 0f;
					break;
				}
				ave += value * (float)set.getValue();
			}
			ave = ave / (float)situations.size();
			avevalues.put(selAct, ave);
		}
		return avevalues;
	}

	
	
	
	
	protected Agent getAgentFromAction(SelectAction action){
		List<Agent> candidates = action.getSelectedAgents(info);
		return CollectionOperator.getRandom(candidates);
	}
	
	
	
	
	
	protected String getDefaultTalk(){
		if(isCO){
			//judge報告
			if(nonToldJudge.size() > 0){
				Judge report = nonToldJudge.get(0);
				nonToldJudge.remove(0);
				if(myRole == Role.SEER){
					return TemplateTalkFactory.divined(report.getTarget(), report.getResult());
				}else if(myRole == Role.MEDIUM){
					return TemplateTalkFactory.inquested(report.getTarget(), report.getResult());
				}
			}
			return null;
		}
		
		if(isLatestInfo){
			return null;
		}
		
		Observe obs = info.getObserve(observePool);
		Map<Situation, Integer> situations = obs.getDecodeSituationMap(situationPool);
		//TODO 自分を黒判定出したSituationはあり得ないっていうのを入れる．
		
//		Set<SelectAction> selActs = SelectAction.getAbleSelectActions(info);
		COAction co = decideTalk(situations);
		coActionRecord.put(info.copy(), co);
		if(co == COAction.DO){
			String talk = TemplateTalkFactory.comingout(getMe(), myRole);
			isCO = true;
			return talk;
		}else{
			return null;
		}
	}
	
	protected COAction decideTalk(Map<Situation, Integer> situations) {
		COAction answer = null;
		if(learningControler.getStrategy() == Strategy.RANDOM || situations.size() == 0){
			answer = (Math.random() < 0.5f)?COAction.DO: COAction.DONT;
		}else{
			//平均行動価値を計算 map selact, float
			Map<COAction, Float> actionValueMap = getAvarageCOActionValues(situations);
			//場合分け
			switch (learningControler.getStrategy()) {
			case GREEDY:
				answer = SelectStrategy.greedyselectF(actionValueMap, learningControler.getEpsilon());
				break;
			case MAX:
				answer = SelectStrategy.getMaxFloatValueKey(actionValueMap);
				break;
			case ROULET:
				answer = SelectStrategy.rouletSelectF(actionValueMap);
				break;
			case SOFTMAX:
				answer = SelectStrategy.softMaxSelect(actionValueMap, learningControler.getTemperature());
				break;
			default:
				break;
			}
		}
		
		coActionRecord.put(info.copy(), answer);
		return answer;
	}

	
	
	
	
	
	

	protected Map<COAction, Float> getAvarageCOActionValues( Map<Situation, Integer> situations){
		Map<COAction, Float> avevalues = new HashMap<COAction, Float>();
		
		
	//	for(SelectAction selAct: selActs){
		float aveT = 0f;
		float aveF = 0f;
		for(Entry<Situation, Integer> set: situations.entrySet()){
			//TODO どの行動かによって変える
			float valueT = set.getKey().getCOValue(COAction.DO);
			float valueF = set.getKey().getCOValue(COAction.DONT);
			aveT += valueT * (float)set.getValue();
			aveF += valueF * (float)set.getValue();
		}
		aveT = aveT / (float)situations.size();
		aveF = aveF / (float)situations.size();
		avevalues.put(COAction.DO, aveT);
		avevalues.put(COAction.DONT, aveF);
	
		return avevalues;
	
	}

	@Override
	public Agent attack() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	public Agent divine() {
		return getDefaultAction(Action.DIVINE);
	}

	@Override
	public Agent guard() {
		return getDefaultAction(Action.GUARD);
	}

	@Override
	public void finish() {
		if(isLearning){
			learnAction(coActionRecord);
			learnAction(selectActionRecord);
		}
	}
	
	private <T> void learnAction(Map<Information, T> map){
		for(Entry<Information, T> set: map.entrySet()){
			Observe obs = set.getKey().getObserve(observePool);
			Situation sit = set.getKey().getTrueSituation(getLatestDayGameInfo().getRoleMap(), situationPool);
			
			obs.updateSituationMap(sit, situationPool);
			sit.updateActionValue(set.getValue(), isWin(), learningControler);
		}
	}
	
	/**
	 * ゲーム終了時に呼ばないと意味なし
	 * @return
	 */
	private boolean isVillagerWin(){
		boolean answer = true;
		Map<Agent, Role> roleMap = getLatestDayGameInfo().getRoleMap();
		if(roleMap.size() < getGameSetting().getPlayerNum()){
			System.out.println("game isn't over.");
			return false;
		}
		
		for(Agent surviver: getLatestDayGameInfo().getAliveAgentList()){
			if(roleMap.get(surviver) == Role.WEREWOLF){
				answer = false;
				break;
			}
		}
		return answer;
	}
	
	/**
	 * 自分自身が勝利しているか
	 * @return
	 */
	private boolean isWin(){
		switch (myRole) {
		case VILLAGER:
		case BODYGUARD:
		case FREEMASON:
		case MEDIUM:
		case SEER:
			return isVillagerWin();
		case POSSESSED:
		case WEREWOLF:
		default:
			return !isVillagerWin();
		}
	}

	public ObservePool getObservePool() {
		return observePool;
	}

	public void setObservePool(ObservePool observePool) {
		this.observePool = observePool;
	}

	public SituationPool getSituationPool() {
		return situationPool;
	}

	public void setSituationPool(SituationPool situationPool) {
		this.situationPool = situationPool;
	}

	public LearningControler getLearningControler() {
		return learningControler;
	}

	public void setLearningControler(LearningControler learningControler) {
		this.learningControler = learningControler;
	}

}
