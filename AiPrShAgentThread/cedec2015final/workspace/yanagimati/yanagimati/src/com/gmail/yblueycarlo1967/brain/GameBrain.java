package com.gmail.yblueycarlo1967.brain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import org.aiwolf.client.base.player.AbstractRole;
import org.aiwolf.client.lib.TemplateTalkFactory;
import org.aiwolf.client.lib.Utterance;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Judge;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Species;
import org.aiwolf.common.data.Talk;
import org.aiwolf.common.data.Vote;
import org.aiwolf.common.net.GameInfo;
/**
 * 人狼ゲーム脳クラス
 * 会話などを管理して、それを利用しやすくする <br>
 *  ☆使い方☆ <br>
 *  1.AbstractRoleを継承したクラスに持たせる <br>
 *  2.そのクラスのupdateメソッド内でGameBrainのupdate()を呼ぶ <br>
 *  3.そのクラスのdayStartメソッド内でGameBrainのdayStart()を呼ぶ <br>
 *  4.それぞれスーパークラスのメソッドを呼ぶのも忘れずに <br>
 * @author info
 *
 */

public class GameBrain {
	/** 自分(GameBrainを持っている者への参照) */
	protected AbstractRole mine;
	/** 霊能COしたAgent */
	private List<Agent> mediumCOAgent=new ArrayList<Agent>();
	/**  占いCOしたAgent*/
	private List<Agent> seerCOAgent=new ArrayList<Agent>();
	/** 狩人COしたAgent */
	private List<Agent> bodyguardCOAgent=new ArrayList<Agent>();
	/** 全占いの占い結果 */
	private List<Judge> seerJudgeList=new ArrayList<Judge>();
	/** 全霊能者の霊能結果 */
	private List<Judge> mediumJudgeList=new ArrayList<Judge>();
	/** 襲撃で死んだ人のリスト 死亡日数順(添え字0は0日の朝見つかった。死んでなければnull) */
	private List<Agent> attackedAgentList=new ArrayList<Agent>();
	/** 処刑で死んだ人のリスト*/
	private List<Agent> executedAgentList=new ArrayList<Agent>();
	
	/** 破綻やほぼ破綻などほぼ人狼が確定したもの(全視点)*/
	private List<Agent> werewolfAgents=new ArrayList<Agent>(); 
	/** 自分視点確定人狼   */
	private List<Agent> myViewpointWerewolf=new ArrayList<Agent>();
	/** 処刑したいエージェント。確証はないけど真を切った占いとか */
	private List<Agent> wantToExecuteAgent=new ArrayList<Agent>();
	/** 2番目に処刑したいエージェント。今すぐ処刑したいわけではないが、グレーから吊るなら処刑したい。
	 * 真切った占いが占った先とか。今は使ってない */
	private List<Agent> wantToSecondExecuteAgent=new ArrayList<Agent>();
	/** 今一番真占いっぽいの */
	private Agent truthSeer=null;
	/** ほぼ確定で村人　3-1の霊とか */
	private List<Agent> villagerAgents=new ArrayList<Agent>();
	/** 今日の投票先発言まとめ <発言者,投票先> */
	private Map<Agent,Agent> todayVotes=new HashMap<Agent,Agent>();
	/** 発言をどこまで読んだか */
	private int readTalkNum=0;
	/** 自分がCOしたかどうか  **/
	protected boolean didComingOut=false;
	/** 今日の投票先  **/
	protected Agent todayVoteTarget;
	/**  何かしゃべりたいことがあるか(今は投票についてのみ) **/
	private boolean haveVoteSpeaking;
	
	protected GameInfo gameInfo;
	/** 環境が変化したか*/
	protected boolean isChangeEnvironment; 

	
	
	public GameBrain(AbstractRole mine){
		this.mine=mine;
	}
	public void dayStart(){
		readTalkNum=0;
		todayVoteTarget=null;
		haveVoteSpeaking=true;
		isChangeEnvironment=false;
		attackedAgentList.add(gameInfo.getAttackedAgent());
		executedAgentList.add(gameInfo.getExecutedAgent());
		todayVotes=new HashMap<Agent,Agent>();
		/*
		List<Vote> voteList= mine.getLatestDayGameInfo().getVoteList();
		for(Vote vote:voteList){
			if(seerCOAgent.contains(vote.getTarget())){
				this.wolfAgents.add(vote.getTarget());
			}
		}
		*/
		//System.out.println(voteList);
	}
	public void update(GameInfo gameInfo){
		this.gameInfo=gameInfo;
		readTalkList();
	}
	
	/** 指定したAgentが現在生きているかどうか */
	public boolean isAgentAlive(Agent agent){
		return gameInfo.getAliveAgentList().contains(agent);
	}
	/** その日の朝に襲撃されたAgentを返す。屍体なしはnull */
	public Agent getAttackedAgent(int day){
		return this.attackedAgentList.get(day);
	}
	/** 今日は屍体なしだったかどうか */
	public boolean isTodayNoAttacked(){
		if(getAttackedAgent(mine.getDay())==null) return true;
		else return false;
	}
	/** 指定した占い師の占い結果を返す */
	public List<Judge> getSeerJudgeList(Agent seer){
		List<Judge> judges=new ArrayList<Judge>();
		for(Judge judge:seerJudgeList){
			if(seer==judge.getAgent()) judges.add(judge);
		}
		return judges;
	}
	/** 指定した霊能者の霊能結果を返す */
	public List<Judge> getMediumJudgeList(Agent medium){
		List<Judge> judges=new ArrayList<Judge>();
		for(Judge judge:mediumJudgeList)	{
			if(medium==judge.getAgent()) judges.add(judge);
		}
		return judges;
	}
	/** 生きてる中でグレーのエージェントを返す。
	  **/
	public List<Agent> getGrayAgents(){
		List<Agent> grayAgents=new ArrayList<Agent>();
		grayAgents.addAll(mine.getLatestDayGameInfo().getAliveAgentList());
		grayAgents.removeAll(mediumCOAgent);
		grayAgents.removeAll(seerCOAgent);
		grayAgents.removeAll(bodyguardCOAgent);
		for(Judge judge:seerJudgeList){
			grayAgents.remove(judge.getTarget());
		}
		return grayAgents;
	}
	/** 占い師が全員白出しした、(ほぼ)確定白のエージェント。占い師が2CO以上の時のみ使う。 */
	public List<Agent> getWhiteDefiniteAgent(){
		if(this.seerCOAgent.size()<2) return new ArrayList<Agent>();
		List<Agent> whiteAgents=gameInfo.getAgentList();
		for(Agent seer:this.seerCOAgent){
			//System.out.println(whiteAgents);
			whiteAgents=andList(whiteDivinedAgent(seer),whiteAgents);
		}
		return whiteAgents;
	}
	/** 二つのListの共通オブジェクトからなるListを返す */
	public List<Agent> andList(List<Agent> list1,List<Agent> list2){
		List<Agent> returnList=new ArrayList<Agent>();
		for(Agent agent:list1){
			if(list2.contains(agent)) returnList.add(agent);
		}
		return returnList;
	}
	/** 暫定白(黒は当てられてない)でかつCOがなく生きているエージェントを返す。 */
	public List<Agent> getWhiteAliveAgents(){
		List<Agent> whiteAgents=new ArrayList<Agent>();
		for(Judge judge:seerJudgeList){
			if(judge.getResult()==Species.HUMAN) whiteAgents.add(judge.getTarget());
		}
		for(Judge judge:seerJudgeList){
			if(judge.getResult()==Species.WEREWOLF) whiteAgents.remove(judge.getTarget());
		}
		for(Agent agent:this.getDieAgentList()){
			if(whiteAgents.contains(agent)) whiteAgents.remove(agent);
		}
		whiteAgents.removeAll(mediumCOAgent);
		whiteAgents.removeAll(seerCOAgent);
		whiteAgents.removeAll(bodyguardCOAgent);
		
		return whiteAgents;
	}
	public List<Agent> getSeerCOAgent(){
		List<Agent> agents=new ArrayList<Agent>();
		agents.addAll(seerCOAgent);
		return agents;
	}
	public List<Agent> getMediumCOAgent(){
		List<Agent> agents=new ArrayList<Agent>();
		agents.addAll(mediumCOAgent);
		return agents;
	}
	public List<Agent> getBodyguardCOAgent(){
		List<Agent> agents=new ArrayList<Agent>();
		agents.addAll(bodyguardCOAgent);
		return agents;
	}
	public List<Agent> getAliveAgents(){
		return gameInfo.getAliveAgentList();
	}
	/** 今日の投票先発言まとめを返す */
	public Map<Agent,Agent> getTodayVote(){
		Map<Agent,Agent> map=new HashMap<Agent,Agent>();
		map.putAll(todayVotes);
		return map;
	}
	/** 生きてる中で暫定黒のエージェントを返す **/
	public List<Agent> getBlackAgents(){
		List<Agent> aliveAgents=new ArrayList<Agent>();
		aliveAgents.addAll(mine.getLatestDayGameInfo().getAliveAgentList());
		List<Agent> blackAgents=new ArrayList<Agent>();
		for(Judge judge:seerJudgeList){
			if(judge.getResult()==Species.WEREWOLF && aliveAgents.contains(judge.getTarget()) ) blackAgents.add(judge.getTarget());
		}
		return blackAgents;
	}
	/** 生きている占いCOしたエージェントを返す **/
	public List<Agent> getAliveSeerCOAgents(){
		List<Agent> aliveAgents=mine.getLatestDayGameInfo().getAliveAgentList();
		List<Agent> aliveSeerCOAgents=new ArrayList<Agent>();
		for(Agent agent:seerCOAgent){
			if(aliveAgents.contains(agent)) aliveSeerCOAgents.add(agent);
		}
		return aliveSeerCOAgents;
	}
	/** 生きている霊能COしたエージェントを返す **/
	public List<Agent> getAliveMediumCOAgents(){
		List<Agent> aliveAgents=mine.getLatestDayGameInfo().getAliveAgentList();
		List<Agent> aliveMediumCOAgents=new ArrayList<Agent>();
		for(Agent agent:mediumCOAgent){
			if(aliveAgents.contains(agent)) aliveMediumCOAgents.add(agent);
		}
		return aliveMediumCOAgents;
	}
	/** 生きている狩人COしたエージェントを返す */
	public List<Agent> getAliveBodyguardCOAgents(){
		List<Agent> aliveAgents=mine.getLatestDayGameInfo().getAliveAgentList();
		List<Agent> aliveBodyguardCOAgents=new ArrayList<Agent>();
		for(Agent agent:bodyguardCOAgent){
			if(aliveAgents.contains(agent)) aliveBodyguardCOAgents.add(agent);
		}
		return aliveBodyguardCOAgents;
	}
	public List<Agent> getAliveAgentsExceptMe(){
		
		List<Agent> aliveAgents=new ArrayList<Agent>();
		aliveAgents.addAll(mine.getLatestDayGameInfo().getAliveAgentList());
		aliveAgents.remove(mine.getMe());
		return aliveAgents;
	}
	/**　占い、霊能、狩人CO者を占い対象から除く */
	public List<Agent> getCOAgents(){
		List<Agent> coAgents=new ArrayList<Agent>();
		coAgents.addAll(bodyguardCOAgent);
		coAgents.addAll(mediumCOAgent);
		coAgents.addAll(seerCOAgent);
		return coAgents;
	}
	/** もし自分が人狼だと言われた場合、そのJudgeを返す。なければnull  **/
	public Judge getJudgeIsDivinedWolf(){
		for(Judge judge:seerJudgeList){
			if(judge.getTarget()==mine.getMe() && judge.getResult()==Species.WEREWOLF){
				return judge;
			}
		}
		return null;
	}
	public Agent getTruthSeer(){
		return truthSeer;
	}
	public boolean didComingOut(){
		return didComingOut;
	}
	/**  自分の役職をカミングアウトする文字列を返す
	 *  カミングアウトはこれを使う（フラグ管理のため）
	 * **/
	public String comingOut(){
		didComingOut=true;
		return TemplateTalkFactory.comingout(mine.getMe(), mine.getMyRole());
	}
	public Agent getTodayVoteTarget(){
		return todayVoteTarget;
	}
	/** 投票先発言をすべきかどうか */
	public boolean haveVoteSpeaking(){
		return haveVoteSpeaking;
	}
	/** 投票先発言を返す */
	public String speakAboutVoteing(){
		haveVoteSpeaking=false;
		return TemplateTalkFactory.vote(todayVoteTarget);
	}
	public List<Agent> getDieAgentList(){
		List<Agent> agentList=new ArrayList<Agent>();
		agentList.addAll(gameInfo.getAgentList());
		agentList.removeAll(gameInfo.getAliveAgentList());
		return agentList;
	}
	/** 指定した占い師が占って白だった人を返す */
	public List<Agent> whiteDivinedAgent(Agent seer){
		List<Agent> agents=new ArrayList<Agent>();
		for(Judge judge:this.getSeerJudgeList(seer)){
			if(judge.getResult()==Species.HUMAN && !agents.contains(judge.getTarget())) agents.add(judge.getTarget());
		}
		return agents;
	}
	/** 指定した占い師が占って、黒だった人を返す */
	public List<Agent> blackDivinedAgent(Agent seer){
		List<Agent> agents=new ArrayList<Agent>();
		for(Judge judge:this.getSeerJudgeList(seer)){
			if(judge.getResult()==Species.WEREWOLF && !agents.contains(judge.getTarget())) agents.add(judge.getTarget());
		}
		return agents;
	}
	public Agent randomSelect(List<Agent> agentList){
		int num=new Random().nextInt(agentList.size());
		return agentList.get(num);
	}
	/** 今日処刑されそうなAgentを返す(全員の発言をみて、得票数トップ) */
	public List<Agent> mostVotedAgents(){
		Map<Agent,Integer> countMap=new HashMap<Agent,Integer>();
		//エージェントごとに数を数える
		for(Agent voted:todayVotes.values()){
			if(countMap.containsKey(voted)){
				int c=countMap.get(voted)+1;
				countMap.put(voted, c);
			}
			else {
				countMap.put(voted, 1);
			}
		}
		//得票最大のAgentを探し出す
		int max=0;
		List<Agent> agents=new ArrayList<Agent>();
		Iterator<Entry<Agent, Integer>> entries = countMap.entrySet().iterator();
		while(entries.hasNext()) {
			Map.Entry<Agent,Integer> entry = entries.next();
			
			if(entry.getValue()>max){
				agents=new ArrayList<Agent>();
				max=entry.getValue();
				agents.add(entry.getKey());
			}
			else if(entry.getValue()==max){
				agents.add(entry.getKey());
			}
		}
		return agents;
	}
	
	public List<Agent> getAliveWerewolfAgent(){
		List<Agent> alive=this.getAliveAgents();
		List<Agent> aliveWerewolf=new ArrayList<Agent>();
		for(Agent agent:werewolfAgents){
			if(alive.contains(agent)) aliveWerewolf.add(agent);
		}
		return aliveWerewolf;
	}
	/** 指定されたエージェントのリストから、生きてるエージェントのみを抽出して返す */
	public List<Agent> getAliveAgentList(List<Agent> agentList){
		List<Agent> aliveList=new ArrayList<Agent>();
		List<Agent> alive=this.getAliveAgents();
		for(Agent agent:agentList){
			if(alive.contains(agent)) aliveList.add(agent);
		}
		return aliveList;
		
	}
	
	
	
	/**破綻者(論理的に考えて人狼陣営のAgent)を探す */
	protected void searchWerewolf(){
		//占いの破綻者を考える
		for(Agent seerAgent:this.getAliveSeerCOAgents()){
			List<Judge> judgeList=this.getSeerJudgeList(seerAgent);
			//人狼判定を出した人とCO者
			int coAndWerewolfCount=this.seerCOAgent.size()+this.mediumCOAgent.size();
			//黒判定の対象先
			List<Agent> blackAgents=new ArrayList<Agent>();
			for(Judge judge:judgeList){
				Agent target=judge.getTarget();
				if(judge.getResult()==Species.WEREWOLF && !blackAgents.contains(judge.getTarget())){
					blackAgents.add(judge.getTarget());
				}
				if(judge.getResult()==Species.WEREWOLF && !seerCOAgent.contains(target) && !mediumCOAgent.contains(target) ){
					coAndWerewolfCount++;
				}
			}
			//破綻者リストに追加するかのフラグ
			boolean add=false;
			//黒出しが噛まれてた場合は破綻
			for(Agent attackedAgent:attackedAgentList){
				if(blackAgents.contains(attackedAgent)) add=true;
			}
			//黒を出した数が多すぎる場合破綻
			//黒出し4回
			if(blackAgents.size()>3) add=true;
			//黒出し3回かつ全員死んでる
			if(blackAgents.size()==3 && getDieAgentList().containsAll(blackAgents)) add=true;
			//黒出し+CO数が1(真占い)+1(霊能)+3（人狼)+1(狂人)より多い
			if(coAndWerewolfCount>6) {
				//System.out.println("破綻"+seerAgent);
				add=true;
			}
			
			//追加
			if(add){
				if(!werewolfAgents.contains(seerAgent)) werewolfAgents.add(seerAgent);
				cutTruthSeer(seerAgent);
			}
		}
		//噛まれ狩人COがいれば、残りは吊る
		if(this.bodyguardCOAgent.size()>1){
			Agent truthBodyguard=null; //真狩人
			for(Agent bodyGuard:this.bodyguardCOAgent){
				//例外処理はしない。複数いる場合はスロー
				if(attackedAgentList.contains(bodyGuard)) truthBodyguard=bodyGuard;
			}
			for(Agent agent:this.getAliveBodyguardCOAgents()){
				if(agent!=truthBodyguard) werewolfAgents.add(agent);
			}
			
		}
		
	}
	/** 処刑したいAgentを探す */
	protected void searchWanToExecuteAgent(){
		//占いCO者が噛まれたら、残った占いも吊る(破綻ではないけど)
		for(Agent seer :this.seerCOAgent){
			if(attackedAgentList.contains(seer)){
				for(Agent agent:this.getAliveSeerCOAgents()){
					if(!wantToExecuteAgent.contains(agent)){
						this.wantToExecuteAgent.add(agent);
						cutTruthSeer(agent);
					}
				}
			}
		}
		//占い4COなら、占い全部吊る
		if(seerCOAgent.size()>3){
			for(Agent agent:this.getAliveSeerCOAgents()){
				if(!wantToExecuteAgent.contains(agent)){
					this.wantToExecuteAgent.add(agent);
					cutTruthSeer(agent);
				}
			}
		}
		//霊能2COなら、霊能全部吊る
		//3COに一時変更
		if(mediumCOAgent.size()>2){
			for(Agent agent:this.getAliveMediumCOAgents()){
				if(!wantToExecuteAgent.contains(agent)){
					 this.wantToExecuteAgent.add(agent);
				}
			}
		}
		
		//3-1,2-1で霊能とライン切れた占いは吊る
		if((seerCOAgent.size()==3 ||seerCOAgent.size()==2)  && mediumCOAgent.size()==1 ){
			Agent medium=mediumCOAgent.get(0);
			for(Judge mediumJudge:this.getMediumJudgeList(medium)){
				for(Agent seer:this.getAliveSeerCOAgents()){
					for(Judge judge:this.getSeerJudgeList(seer)){
						//占った先と霊能結果先が一致した場合
						if(judge.getTarget()==mediumJudge.getTarget()){
							//結果が一致してない場合
							if(judge.getResult()!=mediumJudge.getResult()){
								//System.out.println("ライン切れ:"+seer);
								if(!wantToExecuteAgent.contains(seer)) {
									this.wantToExecuteAgent.add(seer);
									cutTruthSeer(seer);
								}
							}
						}
					}
				}
			}
		}
		//狩人COが３人以上なら吊っていく
		if(this.bodyguardCOAgent.size()>2) {
			for(Agent agent:this.getBodyguardCOAgent()){
				if(!wantToExecuteAgent.contains(agent)) wantToExecuteAgent.add(agent);
			}
		}
		//5日目になったら占い真決め打ち
		//と思ったけど、みんなバラバラだから決め打てないからやめた
		/*
		if(mine.getDay()==5 && truthSeer!=null){
			List<Agent> seers=this.getAliveSeerCOAgents();
			seers.removeAll(this.werewolfAgents);
			seers.removeAll(this.wantToExecuteAgent);
			if(seers.size()>0){
				truthSeer=this.randomSelect(this.getAliveSeerCOAgents());
				System.out.println("真占い決め打ち"+truthSeer);
			}
		}
		*/
		
		
	}
	/** ほぼ確定で村人を探す */
	public void searchVillagers(){
		if(seerCOAgent.size()>=3 &&mediumCOAgent.size()==1){
			Agent medium=mediumCOAgent.get(0);
			if(!villagerAgents.contains(medium)) villagerAgents.add(medium);
		}
		
	}
	/**真占いの可能性を切る  */
	protected void cutTruthSeer(Agent seer){
		//System.out.println("カット"+seer);
		//占った先を囲い候補として２番目の吊り先に
		/*
		for(Judge judge:getSeerJudgeList(seer)){
			Agent agent=judge.getTarget();
			if(judge.getResult()==Species.HUMAN && wantToSecondExecuteAgent.contains(agent)){
				wantToSecondExecuteAgent.add(agent);
				//System.out.println(agent+"を吊る");
			}
		}
		*/
		//占い結果抹消
		List<Judge> judgeList=getSeerJudgeList(seer);
		this.seerJudgeList.removeAll(judgeList);
		
		//破綻によって占いが減り、残り１なら真占い認定
		List<Agent> seerList=this.getSeerCOAgent();
		seerList.removeAll(this.wantToExecuteAgent);
		seerList.removeAll(this.werewolfAgents);
		if(getSeerCOAgent().size()<4 && seerList.size()==1){
			
			truthSeer=seerList.get(0);
			//System.out.println("真占い:"+truthSeer);
		}
		
	}
	/** voteTargetをある範囲から決める場合につかう */
	protected void selectVoteTarget(List<Agent> voteCandidate){
		//投票の範囲が変わらないならそのまま
		if(voteCandidate.contains(todayVoteTarget)){
		}
		else{
		//変わるならランダムに
		todayVoteTarget=randomSelect(voteCandidate);
		 this.haveVoteSpeaking=true;
		}
	}
	
	/** 今日の投票先を考える */
	protected void thinkTodayVoteAgent(){
		isChangeEnvironment=false;
		searchWerewolf();
		searchWanToExecuteAgent();
		searchVillagers();
		//破綻者が見つかれば投票
		List<Agent> aliveWerewolf=getAliveWerewolfAgent();
		if(aliveWerewolf.size()>0){
			selectVoteTarget(aliveWerewolf);
			//System.out.println("破綻");
			//System.out.println(aliveWerewolf);
			return;
		}
		//処刑したいとこがあれば投票
		List<Agent> aliveExecuteAgent=getAliveAgentList(wantToExecuteAgent);
		if(aliveExecuteAgent.size()>0){
			selectVoteTarget(aliveExecuteAgent);
			return;
		}
		//２番目に処刑したいところがあれば投票
		//グレーかつ2番目に投票したい(占いが囲った候補)場所は他の占いが占ってなければ吊る
		/*
		List<Agent> aliveSecondExecute=new ArrayList<Agent>();
		List<Agent> gray=this.getGrayAgents();
		for(Agent agent:getAliveAgentList(wantToSecondExecuteAgent)){
			if(gray.contains(agent)) aliveSecondExecute.add(agent);
		}
		
		if(aliveSecondExecute.size()>0){
			System.out.println("２番目"+aliveSecondExecute);
			selectVoteTarget(aliveSecondExecute);
			return;
		}
		*/
		
		//4日を超えたらランダム投票
		if(mine.getLatestDayGameInfo().getDay()>4){
			//真占いがわかってて、黒判定を出したら黒釣り
			
			if(truthSeer!=null){
				List<Agent> blacks=this.blackDivinedAgent(truthSeer);
				List<Agent> aliveBlack=new ArrayList<Agent>();
				List<Agent> alive=this.getAliveAgents();
				for(Agent agent:blacks){
					if(alive.contains(agent)) aliveBlack.add(agent);
				}
				if(aliveBlack.size()>0){
					selectVoteTarget(aliveBlack);
					return;
				}
			}
			//真占いがわかってたら、その判断にそってランダム
			List<Agent> candidate=this.getAliveAgentsExceptMe();
			if(truthSeer!=null){
				candidate.remove(truthSeer);
				candidate.removeAll(this.whiteDivinedAgent(truthSeer));
			}
			candidate.removeAll(getWhiteDefiniteAgent()); //白確は抜く
			candidate.removeAll(villagerAgents);
			if(candidate.size()>0) selectVoteTarget(candidate);
			else selectVoteTarget(getAliveAgentsExceptMe());
			return;
		}
		//それ以外なら黒投票かグレラン
		else {
		
			List<Agent> voteCandidate=null;
			List<Agent> blackAgents=new ArrayList<Agent>();
			blackAgents.addAll(getBlackAgents());
			blackAgents.removeAll(this.getAliveSeerCOAgents());
			blackAgents.removeAll(this.getAliveMediumCOAgents());
			//黒がいるなら黒に投票
			//黒が役職者なら撤回
			
			List<Agent> coBlackList=new ArrayList<Agent>();//黒出しされたCO者
			for(Agent blackAgent:blackAgents){
				if(this.seerCOAgent.contains(blackAgent)||this.mediumCOAgent.contains(blackAgent)||this.bodyguardCOAgent.contains(blackAgent)){
					coBlackList.add(blackAgent);
				}
			}
			blackAgents.removeAll(coBlackList);
			
			if(blackAgents.size()>0){
				selectVoteTarget(blackAgents);
				return;
			}
			//黒がいない場合はグレラン
			voteCandidate=getGrayAgents();
			voteCandidate.remove(mine.getMe());
			voteCandidate.removeAll(villagerAgents);
			if(voteCandidate.size()!=0){
				selectVoteTarget(voteCandidate);
				return;
			}
			//どの条件にも当てはまらなければ
			selectVoteTarget(this.getAliveAgentsExceptMe());
		}
	}
	
	/** 発言を読む */
	private void readTalkList(){
		List<Talk> talkList=gameInfo.getTalkList();
		for(int i=readTalkNum;i<talkList.size();i++){
			Talk talk=talkList.get(i);
			Utterance utterance=new Utterance(talk.getContent());
			switch (utterance.getTopic()){
			case COMINGOUT:
				if(utterance.getRole()==Role.SEER){
					seerCOAgent.add(utterance.getTarget());
					isChangeEnvironment=true;
				}
				else if(utterance.getRole()==Role.MEDIUM){
					mediumCOAgent.add(utterance.getTarget());
					isChangeEnvironment=true;
				}
				else if(utterance.getRole()==Role.BODYGUARD){
					bodyguardCOAgent.add(utterance.getTarget());
					isChangeEnvironment=true;
				}
				break;
			case DIVINED:
				Agent seer=talk.getAgent();
				//吊り先に上がってる占い師の結果は無視
				if(!werewolfAgents.contains(seer) && !wantToExecuteAgent.contains(seer)){
					seerJudgeList.add(new Judge(talk.getDay(),talk.getAgent(),utterance.getTarget(),utterance.getResult()));
					isChangeEnvironment=true;
				}
				//自分のことを人狼だと占った人は、自分視点確定人狼
				if(utterance.getTarget()==mine.getMe() && utterance.getResult()==Species.WEREWOLF){
					if(!myViewpointWerewolf.contains(talk.getAgent())) {
						myViewpointWerewolf.add(talk.getAgent());
						isChangeEnvironment=true;
					}
				}
				break;
			case INQUESTED:
				mediumJudgeList.add(new Judge(talk.getDay(),talk.getAgent(),utterance.getTarget(),utterance.getResult()));
				isChangeEnvironment=true;
				break;
			case VOTE:
				todayVotes.put(talk.getAgent(), utterance.getTarget());
				break;
			}
			readTalkNum++;
		}
		//環境が変化したら投票先をもう一度考える
		if(isChangeEnvironment || todayVoteTarget==null) thinkTodayVoteAgent();
	}
}
