package com.github.haretaro.pingwo.role;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayDeque;
import java.util.List;
import java.util.stream.Collectors;

import org.aiwolf.client.base.player.AbstractRole;
import org.aiwolf.client.lib.TemplateTalkFactory;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Talk;
import org.aiwolf.common.net.GameInfo;
import org.aiwolf.common.net.GameSetting;

import com.github.haretaro.pingwo.brain.LongTermMemory;
import com.github.haretaro.pingwo.brain.Reason;
import com.github.haretaro.pingwo.brain.ShortTermMemory;
import com.github.haretaro.pingwo.brain.util.Timer;
import com.github.haretaro.pingwo.brain.util.Util;

public abstract class PingwoBasePlayer extends AbstractRole {

	protected LongTermMemory longTermMemory;
	private int readTalkNumber;
	protected ShortTermMemory shortTermMemory;
	protected GameInfo gameInfo;
	protected ArrayDeque<String> talkQue;
	private Agent todaysTarget;
	private int day = -1;
	protected int talkCount = 0;
	protected Constructor<? extends ShortTermMemory> shortTermMemoryConstructor;
	private Reason reason;
	protected Agent me;
	
	private Timer timer;
	
	public PingwoBasePlayer(){
		try {
			shortTermMemoryConstructor = ShortTermMemory.class.getConstructor(GameInfo.class);
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}

		talkQue = new ArrayDeque<String>();
		
		timer = new Timer();
	}
	
	@Override
	public void initialize(GameInfo gameInfo,GameSetting gameSetting){
		this.gameInfo = gameInfo;
		longTermMemory = new LongTermMemory(gameInfo);
		me = gameInfo.getAgent();
	}

	@Override
	public void dayStart() {
		timer.start("dayStart");
		
		try {
			shortTermMemory = shortTermMemoryConstructor.newInstance(gameInfo);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			e.printStackTrace();
		}
		todaysTarget = null;
		day = gameInfo.getDay();
		readTalkNumber = 0;
		talkCount = 0;
		longTermMemory.dayStart(gameInfo);
		reason=null;
		talkQue = new ArrayDeque<String>();
		
		timer.end();
	}
	
	@Override
	public void update(GameInfo gameInfo){
		timer.start("update");
		
		this.gameInfo = gameInfo;
		
		listen();
		longTermMemory.update(gameInfo);
		
		if(gameInfo.getDay() == day){
			think();
		}
		
		timer.end();
	}

	protected void listen() {
		List<Talk> talkList = gameInfo.getTalkList();
		talkList.stream().skip(readTalkNumber)
		.forEach(talk->{
			shortTermMemory.listen(talk);
			longTermMemory.listen(talk);
		});
		readTalkNumber = talkList.size();
	}

	protected void think() {
		
		if(sayOverOnFirstDay()){
			return;
		}

		if(sayExpectdTarget()){
			return;
		}
		
		if(persuade()){
			return;
		}
		
		if(declareToVoteToExpectedTarget()){
			return;
		}

		if(agreeToMajority()){
			return;
		}
		
		planB();
	}
	
	/**
	 * denyList 以外のプレイヤーで最も票が多そうなプレイヤーに投票する
	 * @return
	 */
	protected void planB(){
		List<Agent> targets = gameInfo.getAliveAgentList()
				.stream()
				.filter(a -> longTermMemory.getDenyList().contains(a) == false)
				.collect(Collectors.toList());
		Agent target = shortTermMemory.whoIsTheMostOf(targets);
		
		if(target != null
				&& target != todaysTarget){
			todaysTarget = target;
			talkQue.add(TemplateTalkFactory.vote(target));
		}
	}
	
	/**
	 * 発言回数が10回以下の時は自分の希望を言って他のプレイヤーの説得を狙う
	 * @return
	 */
	protected boolean persuade(){
		if(longTermMemory.hasExpectedTarget()
				&& shortTermMemory.isTalkOver() == false
				&& talkCount < 11
				&& shortTermMemory.getPotentialCandidates().contains(longTermMemory.getExpectedTarget().orElse(null)) == false){
			Agent target = shortTermMemory.whoIsTheMostOf(longTermMemory.getExpectedTargets());
			talkQue.add(TemplateTalkFactory.vote(target));
			if(target != todaysTarget){
				todaysTarget = target;
				reason = Reason.EXPECTED;
				Util.printout("expected target=" + target);
			}
			return true;
		}
		return false;
	}

	/**
	 * 3票以上得票しそうな人がいるならその人に投票。ただし白確定には入れない。
	 * @return 実行されたらtrue
	 */
	protected final boolean agreeToMajority() {
		Agent target = longTermMemory.getLeastReliableAgentOf(shortTermMemory.getPotentialCandidates())
				.orElse(null);
				
		if(target != null
				&& shortTermMemory.getMaxVoteCount() > 2
				&& longTermMemory.getDenyList().contains(target) == false
				&& longTermMemory.getReliabilityOf(target) < 0.9
				&& target != todaysTarget){
			todaysTarget = target;
			reason = Reason.AGREE_TO_MAJORITY;
			talkQue.add(TemplateTalkFactory.vote(target));
			return true;
		}
		return false;
	}

	/**
	 * 暫定一位の人の得票数と釣り希望の人の得票数の差が少なければ吊り希望の人に投票
	 * @return 実行されたらtrue
	 */
	protected final boolean declareToVoteToExpectedTarget() {
		if(longTermMemory.hasExpectedTarget()
				&& shortTermMemory.getMaxVoteCount() - shortTermMemory.getMaxVoteCountOf(longTermMemory.getExpectedTargets()) < 2){
			
			Agent target = shortTermMemory.whoIsTheMostOf(longTermMemory.getExpectedTargets());
			
			if(target != todaysTarget){
				todaysTarget = target;
				talkQue.add(TemplateTalkFactory.vote(target));
				reason = Reason.EXPECTED;
				Util.printout("expected target=" + target);
			}
			
			return true;
		}
		return false;
	}

	/**
	 * 最初の発言で釣り希望を出す
	 * @return 実行されたらtrue
	 */
	protected final boolean sayExpectdTarget() {
		if(talkCount==0){
			String firstTalk = longTermMemory.getExpectedTarget()
					.map(a->TemplateTalkFactory.vote(a))
					.orElse(Talk.SKIP);
			todaysTarget = longTermMemory.getExpectedTarget().orElse(null);
			talkQue.add(firstTalk);
			return true;
		}
		return false;
	}
	
	/**
	 * 初日は何も発言しない
	 */
	protected final boolean sayOverOnFirstDay(){
		if (gameInfo.getDay() == 0){
			talkQue.add(Talk.OVER);
			return true;
		}
		return false;
	}

	@Override
	public void finish() {
		timer.print();
	}

	@Override
	public String talk() {
		timer.start("talk");
		
		talkCount ++;
		Util.printout(""+reason);
		Util.printout("target "+todaysTarget);
		
		if(talkQue.isEmpty() == false){
			timer.end();
			return talkQue.poll();
		}
		
		if(shortTermMemory.isTalkOver()){
			timer.end();
			return Talk.OVER;
		}
		
		timer.end();
		return Talk.SKIP;
	}

	@Override
	public Agent vote() {
		timer.start("vote");
		
		Util.printout("vote reason:"+reason);
		if(todaysTarget!=null){
			Util.printout("vote reliability="+longTermMemory.getReliabilityOf(todaysTarget));
			return todaysTarget;
		}
		List<Agent> candidates = gameInfo.getAliveAgentList();
		candidates.remove(gameInfo.getAgent());
		Util.printout(Reason.RANDOM.toString());
		
		timer.end();
		return Util.randomSelect(candidates);
	}

	@Override
	public abstract Agent attack();

	@Override
	public abstract Agent divine();

	@Override
	public abstract Agent guard();

	@Override
	public abstract String whisper();

}
