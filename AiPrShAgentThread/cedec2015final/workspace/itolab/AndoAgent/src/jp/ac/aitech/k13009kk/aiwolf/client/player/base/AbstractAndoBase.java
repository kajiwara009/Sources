package jp.ac.aitech.k13009kk.aiwolf.client.player.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.aiwolf.client.base.player.AbstractRole;
import org.aiwolf.client.lib.TemplateTalkFactory;
import org.aiwolf.client.lib.TemplateTalkFactory.TalkType;
import org.aiwolf.client.lib.Topic;
import org.aiwolf.client.lib.Utterance;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Species;
import org.aiwolf.common.data.Talk;
import org.aiwolf.common.net.GameInfo;

import jp.ac.aitech.k13009kk.aiwolf.client.lib.OrganizeConversation;

/**
 * 基本となる行動
 * @author keisuke 愛知工業大学 K13009 安藤圭祐
 * @version AndoAgent 1.0
 */
public abstract class AbstractAndoBase extends AbstractRole {

	// 今日のログをどこまで読んだか
	private int readTalkNum = 0;
	// Talkを整理する
	private OrganizeConversation organizeConversation;
	// カミングアウトしたかどうか
	protected boolean isComingout = false;
	// カミングアウトする日
	protected int comingoutDay = -1;
	// エージェントと死亡日を関連付けたマップ
	Map<Agent, Integer> agentDeadDayMap = new HashMap<>();
	// 自分が話した内容
	List<String> myTalkContents = new ArrayList<>();
	// 投票先
	protected Agent voteTarget;

	public AbstractAndoBase() {
		organizeConversation = new OrganizeConversation();
		voteTarget = null;
	}

	@Override
	public void dayStart() {
		this.readTalkNum = 0;
		this.voteTarget = null;
		if (getGameInfo(getDay()).getAttackedAgent() != null) {
			agentDeadDayMap.put(getGameInfo(getDay()).getAttackedAgent(), getDay() - 1);
		}
		if (getGameInfo(getDay()).getExecutedAgent() != null) {
			agentDeadDayMap.put(getGameInfo(getDay()).getExecutedAgent(), getDay() - 1);
		}
	}

	/**
	 * 今日自分が何回話したかを返します。
	 * @return 今日自分が何回話したか
	 */
	public int getMyTalkCount() {
		return getTodayTalkList(getMe()).size();
	}

	public Agent getReticenceAgent() {
		int minTalkCount = Integer.MAX_VALUE;
		Agent reticenceAgent = getMe();
		for (Agent agent : this.getLatestDayGameInfo().getAliveAgentList()) {
			if (agent == getMe()) {
				continue;
			}
			if (getTodayTalkList(agent).size() < minTalkCount) {
				minTalkCount = getTodayTalkList(getMe()).size();
				reticenceAgent = agent;
			}
		}
		return reticenceAgent;
	}

	/**
	 * 今日自分はすでに投票先を述べたかどうかを示します。
	 * @return 今日自分はすでに投票先を述べたかどうか
	 */
	public boolean isTalkedAboutVote() {
		for (Talk talk : getTodayTalkList(getMe())) {
			Utterance utterance = new Utterance(talk.getContent());
			if (utterance.getTopic() == Topic.VOTE) {
				return true;
			}
		}
		return false;
	}

	/**
	 * エージェントと死亡日を関連付けたマップを返します。
	 * @return エージェントと死亡日を関連付けたマップ
	 */
	public Map<Agent, Integer> getAgentDeadDayMap() {
		return agentDeadDayMap;
	}

	@Override
	public void update(GameInfo gameInfo) {
		super.update(gameInfo);

		List<Talk> talkList = gameInfo.getTalkList();
		for (; this.readTalkNum < talkList.size(); this.readTalkNum++) {
			Talk talk = talkList.get(readTalkNum);

			if (talk.getAgent() == getMe()) {
				this.myTalkContents.add(talk.getContent());
			}

			organizeConversation.addTalk(talk);
		}
	}

	/**
	 * 自分の発言の内容のリストを返します。
	 * @return 自分の発言の内容のリスト
	 */
	public List<String> getMyTalkContents() {
		return myTalkContents;
	}

	/**
	 * 指定されたTopicに関連付けられた発言リストを返します。
	 * @param topic 関連付けられた発言リストが返されるTopic
	 * @return 指定されたTopicがマップされている発言リスト
	 */
	public List<Talk> getTalkList(Topic topic) {
		return organizeConversation.getTalkList(topic);
	}

	/**
	 * 指定された日に関連付けられた発言リストを返します。
	 * @param day 関連付けられた発言リストが返される日付
	 * @return 指定された日付がマップされている発言リスト
	 */
	public List<Talk> getTalkList(int day) {
		return organizeConversation.getTalkList(day);
	}

	/**
	 * 指定されたエージェントに関連付けられた発言リストを返します。
	 * @param agent 関連付けられた発言リストが返されるエージェント
	 * @return 指定されたエージェントがマップされている発言リスト
	 */
	public List<Talk> getTalkList(Agent agent) {
		return organizeConversation.getTalkList(agent);
	}

	/**
	 * 指定されたエージェントが今日発言した発言のリストを返します。
	 * @param agent 返される今日の発言リストのエージェント
	 * @return 指定されたエージェントが今日発言した発言のリスト
	 */
	public List<Talk> getTodayTalkList(Agent agent) {
		List<Talk> todayTalkList = new ArrayList<>();

		for (Talk talk : getTalkList(getDay())) {
			if (talk.getAgent() == agent) {
				todayTalkList.add(talk);
			}
		}
		return todayTalkList;
	}

	/**
	 * 指定されたエージェントのリストからランダムにエージェントを返します。
	 * @param agentList 返されるエージェントを含むエージェントのリスト
	 * @return 指定されたエージェントのリストからランダムに選択したエージェント
	 */
	public Agent randomSelect(List<Agent> agentList) {
		int num = new Random().nextInt(agentList.size());
		return agentList.get(num);
	}

	/**
	 * どのエージェントが何の役職にカミングアウトしているかを関連付けたマップを返します。
	 * @return どのエージェントが何の役職にカミングアウトしているかを関連付けたマップ
	 */
	public Map<Agent, Role> getAgentComingoutRoleMap() {
		return organizeConversation.getAgentComingoutRoleMap();
	}

	/**
	 * ゲーム開始からの全ての発言をリストにして返します。
	 * @return ゲーム開始からの全ての発言リスト
	 */
	public List<Talk> getAllTalkList() {
		return organizeConversation.getAllTalkList();
	}

	/**
	 * 指定されたエージェントのリストから死んでいるエージェントを除いたリストを返します。
	 * @param agentList 返される死んでいるエージェントを取り除くリスト
	 * @return 指定されたエージェントのリストから死んでいるエージェントを除いたリスト
	 */
	public List<Agent> removeDeadAgent(List<Agent> agentList) {
		agentList.removeAll(getAgentDeadDayMap().keySet());
		return agentList;
	}

	/**
	 * 自分を正しく占っているエージェントのリストを返します。
	 * @return 自分を正しく占っているエージェントのリスト
	 */
	public List<Agent> getDivinedCorrectlyAgentList() {
		List<Agent> divinedCorrectlyAgentList = new ArrayList<>();

		for (Talk talk : getTalkList(Topic.DIVINED)) {
			Utterance utterance = new Utterance(talk.getContent());
			if (utterance.getTarget() == getMe() && utterance.getResult() == getMyRole().getSpecies()) {
				divinedCorrectlyAgentList.add(talk.getAgent());
			}
		}
		return divinedCorrectlyAgentList;
	}

	/**
	 * 指定された役職にCOしているエージェントのリストを返します。
	 * @param role 返されるリストのエージェントの役職
	 * @return 指定された役職にCOしているエージェントのリスト
	 */
	public List<Agent> getComingoutAgents(Role role) {
		List<Agent> comingoutAgents = new ArrayList<>();
		for (Agent agent : getAgentComingoutRoleMap().keySet()) {
			if (getAgentComingoutRoleMap().get(agent) == role) {
				comingoutAgents.add(agent);
			}
		}
		return comingoutAgents;
	}

	public List<Agent> getExpectFakeSeer() {
		List<Agent> expectFakeSeer = new ArrayList<>();
		expectFakeSeer.addAll(getComingoutAgents(Role.SEER));
		expectFakeSeer.removeAll(getDivinedCorrectlyAgentList());
		return expectFakeSeer;
	}

	/**
	 * 自分を人狼と占っている占い結果を否定する発言のリストを返します。
	 * @return 自分を人狼と占っている占い結果を否定する発言のリスト
	 */
	public List<String> getDisagreeDivineMeWrongList() {
		List<String> disagreeDivineMeWrong = new ArrayList<>();
		for (Talk talk : getTalkList(Topic.DIVINED)) {
			Utterance utterance = new Utterance(talk.getContent());
			if (utterance.getTarget() == getMe() && utterance.getResult() == Species.WEREWOLF) {
				disagreeDivineMeWrong.add(disagree(talk));
			}
		}
		return disagreeDivineMeWrong;
	}

	/**
	 * 指定された発言に同意する発言を返します。
	 * <b>このメソッドはwisperに対応していません</b>
	 * @param talk 同意する対象の発言
	 * @return 指定された発言に同意する発言
	 */
	public String agree(Talk talk) {
		return TemplateTalkFactory.agree(
				TalkType.TALK, talk.getDay(), talk.getIdx());
	}

	/**
	 * 指定された発言に反対する発言を返します。
	 * <b>このメソッドはwisperに対応していません</b>
	 * @param talk 反対する対象の発言
	 * @return 指定された発言に反対する発言
	 */
	public String disagree(Talk talk) {
		return TemplateTalkFactory.disagree(
				TalkType.TALK, talk.getDay(), talk.getIdx());
	}

	/**
	 * 投票先を決める
	 */
	abstract public void setVoteTarget();
}
