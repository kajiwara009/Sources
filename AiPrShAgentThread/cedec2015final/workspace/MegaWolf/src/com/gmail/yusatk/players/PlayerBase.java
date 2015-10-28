/**
 * 
 */
package com.gmail.yusatk.players;

import java.util.*;

import org.aiwolf.client.base.player.AbstractRole;
import org.aiwolf.client.lib.TemplateTalkFactory;
import org.aiwolf.client.lib.TemplateWhisperFactory;
import org.aiwolf.common.data.*;
import org.aiwolf.common.net.*;

import com.gmail.yusatk.data.*;
import com.gmail.yusatk.interfaces.*;
import com.gmail.yusatk.utils.DebugLog;
import com.gmail.yusatk.utils.TimeOverException;
import com.gmail.yusatk.utils.TimeWatcher;

/**
 * @author Yu
 *
 */
public abstract class PlayerBase extends AbstractRole 
					implements IAgentEx{
	Analyzer analyzer;	
	Queue<ITalkEvent> talkEvents = new LinkedList<ITalkEvent>();
	Queue<ITalkEvent> whisperEvents = new LinkedList<ITalkEvent>();
	
	Agent votePlan = null;
	int talkCount = 0;
	final int MAX_TALK_COUNT = 10;
	
	int whisperCount = 0;
	int currentDay = -1;
	
	Queue<IBattlePlan> plans = new LinkedList<IBattlePlan>();
	
	boolean throwException = false;
	boolean throwTimeOverException = false;
	
	public static final long LIMIT_TIME_MILLI_SEC = 800;
	TimeWatcher timeWatcher = new TimeWatcher(LIMIT_TIME_MILLI_SEC);
	
	private void setProperties() {
		String debugLogEnableString = java.lang.System.getProperty("com.gmail.yusatk.data.DebugLog.enable");
		if(debugLogEnableString != null) {
			boolean debugLog = Boolean.parseBoolean(debugLogEnableString);
			DebugLog.setEnable(debugLog);
		}

		String throwExceptionString = java.lang.System.getProperty("com.gmail.yusatk.players.PlayerBase.throwException");
		if(throwExceptionString != null) {
			throwException = Boolean.parseBoolean(throwExceptionString);
		}
		
		String throwTimeOverExceptionString = java.lang.System.getProperty("com.gmail.yusatk.players.PlayerBase.throwTimeOverException");
		if(throwTimeOverExceptionString != null) {
			throwTimeOverException = Boolean.parseBoolean(throwTimeOverExceptionString);
			TimeWatcher.throwException = throwTimeOverException;
		}
	}
	
	public PlayerBase() {
		super();
		setProperties();
		setAssumptionRole(getRole());
	}
	
	protected Queue<ITalkEvent> getTalkEvents (){
		return talkEvents;
	}
	
	protected void addPlan(IBattlePlan plan) {
		plans.add(plan);
	}
	
	@Override
	public Agent getAgent() {
		return getMe();
	}
	
	@Override
	public Role getRole() {
		return getMyRole();
	}

	
	protected <T> T randomSelect(List<T> targets) {
		Random r = new Random();
		int index = r.nextInt(targets.size());
		return targets.get(index);
	}
	
	/* (non-Javadoc)
	 * @see org.aiwolf.common.data.Player#attack()
	 */
	@Override
	public Agent attack() {
		try {
			Agent target = null;
			for(IBattlePlan plan : plans) {
				Agent vote = plan.getAttackPlan();
				if(vote != null) {
					target = vote;
				}
				if(!timeWatcher.hasExtraTime()) {
					return target;
				}
			}
			return target;
		}catch(Exception e) {
			if(throwException) {
				throw e;
			}
			DebugLog.printException(e);
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see org.aiwolf.common.data.Player#dayStart()
	 */
	@Override
	public void dayStart() {
		try {
			timeWatcher.start();
			currentDay = analyzer.getDay();
			talkEvents.clear();
			talkCount = 0;
			
			whisperEvents.clear();
			whisperCount = 0;
			
			for(IBattlePlan p : plans) {
				p.dayStart();
				if(!timeWatcher.hasExtraTime()) {
					return;
				}
			}
			timeWatcher.check();
		}catch(TimeOverException e) {
			if(throwTimeOverException) {
				throw new IllegalArgumentException(e.getMessage());
			}
		}catch(Exception e){
			if(throwException) {
				throw e;
			}
			DebugLog.printException(e);			
		}
	}

	/* (non-Javadoc)
	 * @see org.aiwolf.common.data.Player#divine()
	 */
	@Override
	public Agent divine() {
		try {
			timeWatcher.start();
			Agent target = null;
			for(IBattlePlan plan : plans) {
				Agent divine = plan.getDivinePlan();
				if(divine != null) {
					target = divine;
				}
				if(!timeWatcher.hasExtraTime()) {
					return target;
				}
			}
			timeWatcher.check();
			return target;	
		}catch(TimeOverException e) {
			if(throwTimeOverException) {
				throw new IllegalArgumentException(e.getMessage());
			}
			DebugLog.printException(e);
			return null;
		}catch (Exception e) {
			if(throwException) {
				throw e;
			}
			DebugLog.printException(e);
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see org.aiwolf.common.data.Player#finish()
	 */
	@Override
	public void finish() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.aiwolf.common.data.Player#getName()
	 */
	@Override
	public String getName() {
		return "働きの悪い村人";
	}

	/* (non-Javadoc)
	 * @see org.aiwolf.common.data.Player#guard()
	 */
	@Override
	public Agent guard() {
		try {
			timeWatcher.start();
			Agent target = null;
			for(IBattlePlan plan : plans) {
				Agent guard = plan.getGuardPlan();
				if(guard != null) {
					target = guard;
				}
				if(!timeWatcher.hasExtraTime()) {
					return target;
				}
			}
			timeWatcher.check();
			return target;	
		}catch(TimeOverException e) {
			if(throwTimeOverException) {
				throw new IllegalArgumentException(e.getMessage());
			}
			DebugLog.printException(e);
			return null;
		}catch (Exception e) {
			if(throwException) {
				throw e;
			}
			DebugLog.printException(e);
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see org.aiwolf.common.data.Player#initialize(org.aiwolf.common.net.GameInfo, org.aiwolf.common.net.GameSetting)
	 */
	@Override
	public void initialize(GameInfo gameInfo, GameSetting gameSetting) {
		try{
			timeWatcher.start();
			super.initialize(gameInfo, gameSetting);
			analyzer = new Analyzer(gameSetting);
			timeWatcher.check();
		}catch(TimeOverException e) {
			if(throwTimeOverException) {
				throw new IllegalArgumentException(e.getMessage());
			}
			DebugLog.printException(e);
		}catch(Exception e) {
			if(throwException) {
				throw e;
			}
			DebugLog.printException(e);
		}
	}

	/* (non-Javadoc)
	 * @see org.aiwolf.common.data.Player#talk()
	 */
	@Override
	public String talk() {
		try {
			timeWatcher.start();
			for(IBattlePlan plan : plans) {
				assert plan != null;
				DebugLog.log("Plan: %s\n", plan.getClass().toString());
				Queue<ITalkEvent> talks = plan.getTalkPlan(MAX_TALK_COUNT - talkCount);
				assert talks != null;
				talkCount += talks.size();
				talkEvents.addAll(talks);
				if(!timeWatcher.hasExtraTime()) {
					break;
				}
			}

			timeWatcher.check();
			ITalkEvent talk = talkEvents.poll();
			if(talk == null) {
				return TemplateTalkFactory.over();
			}
			return talk.getTalk();
		}catch(TimeOverException e) {
			if(throwTimeOverException) {
				throw new IllegalArgumentException(e.getMessage());
			}
			DebugLog.printException(e);
			return TemplateTalkFactory.over();
		}catch(Exception e) {
			if(throwException) {
				throw e;
			}
			DebugLog.printException(e);
			return TemplateTalkFactory.over();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.aiwolf.common.data.Player#update(org.aiwolf.common.net.GameInfo)
	 */
	@Override
	public void update(GameInfo info) {
		try {
			timeWatcher.start();
			boolean dayStartUpdate = currentDay != info.getDay();
			analyzer.update(new GameInfoEx(info));
			analyzer.Dump();
			int currentPlanCount = plans.size();
			for(int i = 0; i <currentPlanCount; ++i) {
				IBattlePlan plan = plans.poll();
				IBattlePlan newPlan = plan.planUpdate(dayStartUpdate);
				assert newPlan != null;
				
				plans.add(newPlan);
			}
			timeWatcher.check();
		}catch(TimeOverException e) {
			if(throwTimeOverException) {
				throw new IllegalArgumentException(e.getMessage());
			}
			DebugLog.printException(e);
		}catch (Exception e) {
			if(throwException) {
				throw e;
			}
			DebugLog.printException(e);
		}
	}

	/* (non-Javadoc)
	 * @see org.aiwolf.common.data.Player#vote()
	 */
	@Override
	public Agent vote() {
		try {
			timeWatcher.start();
			Agent target = null;
			for(IBattlePlan plan : plans) {
				Agent vote = plan.getVotePlan();
				if(vote != null) {
					target = vote;
				}
				if(!timeWatcher.hasExtraTime()) {
					return target;
				}
			}
			timeWatcher.check();
			return target;
		}catch(TimeOverException e) {
			if(throwTimeOverException) {
				throw new IllegalArgumentException(e.getMessage());
			}
			DebugLog.printException(e);
			return null;
		}catch(Exception e) {
			if(throwException) {
				throw e;
			}
			DebugLog.printException(e);
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see org.aiwolf.common.data.Player#whisper()
	 */
	@Override
	public String whisper() {
		try{
			timeWatcher.start();
			for(IBattlePlan plan : plans) {
				assert plan != null;
				Queue<ITalkEvent> talks = plan.getWhisperPlan(MAX_TALK_COUNT - talkCount);
				assert talks != null;
				whisperCount += talks.size();
				whisperEvents.addAll(talks);
				if(!timeWatcher.hasExtraTime()) {
					break;
				}
			}

			timeWatcher.check();
			ITalkEvent talk = whisperEvents.poll();
			if(talk == null) {
				return TemplateWhisperFactory.over();
			}
			return talk.getTalk();
		}catch(TimeOverException e) {
			if(throwTimeOverException) {
				throw new IllegalArgumentException(e.getMessage());
			}
			DebugLog.printException(e);
			return TemplateWhisperFactory.over();
		}catch (Exception e) {
			if(throwException) {
				throw e;
			}
			DebugLog.printException(e);
			return TemplateWhisperFactory.over();
		}
	}

	Role assumptionRole;
	@Override
	public void setAssumptionRole(Role role) {
		this.assumptionRole = role;
	}
	@Override
	public Role getAssumptionRole() {
		return assumptionRole;
	}
	
	@Override
	public TimeWatcher getTimeWatcher() {
		return timeWatcher;
	}
}
