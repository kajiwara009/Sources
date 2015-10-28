package jp.ac.aitech.k13009kk.aiwolf.client.lib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aiwolf.client.lib.Topic;
import org.aiwolf.client.lib.Utterance;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Talk;

/**
 * 会話の整理
 * @author keisuke 愛知工業大学 K13009 安藤圭祐
 * @version AndoAgent 1.0
 */
public class OrganizeConversation {

	// 1ゲーム中の全ての発言のリスト
	private List<Talk> allTalkList;
	// Topic毎に発言リストを保持するマップ
	private Map<Topic, List<Talk>> topicTalkListMap;
	// 日毎に発言リストを保持するマップ
	private Map<Integer, List<Talk>> dayTalkListMap;
	// 各エージェントの発言を保持するマップ
	private Map<Agent, List<Talk>> agentTalkListMap;
	// COしているエージェントとそのCOしている役職を保持するマップ
	private Map<Agent, Role> agentCominoutRoleMap;

	/**
	 * 会話の整理をするオブジェクトを構築します。
	 */
	public OrganizeConversation() {
		super();
		allTalkList = new ArrayList<>();
		initTopicTalkListMap();
		dayTalkListMap = new HashMap<>();
		agentTalkListMap = new HashMap<>();
		agentCominoutRoleMap = new HashMap<>();
	}

	private void initTopicTalkListMap() {
		topicTalkListMap = new HashMap<>();
		topicTalkListMap.put(Topic.AGREE, new ArrayList<>());
		topicTalkListMap.put(Topic.ATTACK, new ArrayList<>());
		topicTalkListMap.put(Topic.COMINGOUT, new ArrayList<>());
		topicTalkListMap.put(Topic.DISAGREE, new ArrayList<>());
		topicTalkListMap.put(Topic.DIVINED, new ArrayList<>());
		topicTalkListMap.put(Topic.ESTIMATE, new ArrayList<>());
		topicTalkListMap.put(Topic.GUARDED, new ArrayList<>());
		topicTalkListMap.put(Topic.INQUESTED, new ArrayList<>());
		topicTalkListMap.put(Topic.OVER, new ArrayList<>());
		topicTalkListMap.put(Topic.SKIP, new ArrayList<>());
		topicTalkListMap.put(Topic.VOTE, new ArrayList<>());
	}

	public List<Talk> getAllTalkList() {
		return allTalkList;
	}

	/**
	 * 発言を加えます。発言を各マップにマッピングします。
	 * @param talk 加える発言
	 */
	public void addTalk(Talk talk) {
		allTalkList.add(talk);
		mapTopicTalkListMap(talk);
		mapDayTalkListMap(talk);
		mapAgentTalkListMap(talk);
		mapAgentComingoutRoleMap(talk);
	}

	/**
	 * 指定されたTopicに関連付けられた発言リストを返します。
	 * @param topic 関連付けられた発言リストが返されるTopic
	 * @return 指定されたTopicがマップされている発言リスト
	 */
	public List<Talk> getTalkList(Topic topic) {
		return topicTalkListMap.get(topic);
	}

	/**
	 *指定された日に関連付けられた発言リストを返します。
	 * @param day 関連付けられた発言リストが返される日付
	 * @return 指定された日付がマップされている発言リスト
	 */
	public List<Talk> getTalkList(int day) {
		if(dayTalkListMap.get(day) == null){
			dayTalkListMap.put(day, new ArrayList<>());
		}
		return dayTalkListMap.get(day);
	}

	/**
	 * 指定されたエージェントに関連付けられた発言リストを返します。
	 * @param agent 関連付けられた発言リストが返されるエージェント
	 * @return 指定されたエージェントがマップされている発言リスト
	 */
	public List<Talk> getTalkList(Agent agent) {
		if (agentTalkListMap.get(agent) == null) {
			agentTalkListMap.put(agent, new ArrayList<>());
		}
		return agentTalkListMap.get(agent);
	}

	public Map<Agent, Role> getAgentComingoutRoleMap() {
		return agentCominoutRoleMap;
	}

	private void mapAgentTalkListMap(Talk talk) {
		List<Talk> talkList = agentTalkListMap.get(talk.getAgent());
		if (talkList == null) {
			talkList = new ArrayList<>();
		}
		talkList.add(talk);
		agentTalkListMap.put(talk.getAgent(), talkList);
	}

	private void mapDayTalkListMap(Talk talk) {
		List<Talk> talkList = dayTalkListMap.get(talk.getDay());
		if (talkList == null) {
			talkList = new ArrayList<>();
		}
		talkList.add(talk);
		dayTalkListMap.put(talk.getDay(), talkList);
	}

	private void mapTopicTalkListMap(Talk talk) {
		Utterance utterance = new Utterance(talk.getContent());
		List<Talk> talkList = topicTalkListMap.get(utterance.getTopic());
		talkList.add(talk);
		topicTalkListMap.put(utterance.getTopic(), talkList);
	}

	private void mapAgentComingoutRoleMap(Talk talk) {
		Utterance utterance = new Utterance(talk.getContent());
		if (utterance.getTopic() == Topic.COMINGOUT) {
			agentCominoutRoleMap.put(talk.getAgent(), utterance.getRole());
		}
	}
}
