package jp.ac.aitech.k13009kk.aiwolf.client.player;

import java.util.ArrayList;
import java.util.List;

import org.aiwolf.client.lib.TemplateTalkFactory;
import org.aiwolf.client.lib.Topic;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Talk;
import org.aiwolf.common.net.GameInfo;
import org.aiwolf.common.net.GameSetting;

import jp.ac.aitech.k13009kk.aiwolf.client.player.base.AbstractAndoWerewolf;

public class KamiwadaWerewolf extends AbstractAndoWerewolf {
	private boolean isTalked = false;
	private boolean isWispered = false;
	private List<Agent> aliveAgent = new ArrayList<Agent>();
	private List<Agent> coAgent = new ArrayList<Agent>();

	// 戦う前の値
	@Override
	public void initialize(GameInfo gameInfo, GameSetting gameSetting) {
		super.initialize(gameInfo, gameSetting);
	}

	// 一日の始まり
	@Override
	public void dayStart() {
		// 生きてる人狼以外のAgentをaliveAgentに入れておく
		isTalked = false;
		isWispered = false;
		aliveAgent = getGameInfo(getDay()).getAliveAgentList();
		List<Agent> wolflist = getWolfList();
		aliveAgent.removeAll(wolflist);
	}

	// 自分の番が回ってきたときの情報更新
	@Override
	public void update(GameInfo gameInfo) {
		super.update(gameInfo);
	}

	// カミングアウトをしたプレイヤー(人狼でない)を返す
	// TODO 死人を含んでしまう
	private List<Agent> getComeoutAgent() {
		for (Talk talk : getTalkList(Topic.COMINGOUT)) {
			if (!coAgent.contains(talk.getAgent()))
				coAgent.add(talk.getAgent());
		}
		return coAgent;
	}

	// 一番会話量が多いAgentを返す
	// TODO 2つ以上に対応してない
	// TODO 会話が回ってくるのはランダムなのでOverを除かなければ
	private Agent getChatAgent() {
		int ts, bf = 0;
		Agent chatAgent = null;
		for (Agent agent : aliveAgent) {
			ts = getTalkList(agent).size();
			if (ts >= bf) {
				chatAgent = agent;
				bf = ts;
			}
		}
		return chatAgent;
	}

	// 会話
	@Override
	public String talk() {
		String talkString = TemplateTalkFactory.over();
		if (!isTalked) {
			talkString = TemplateTalkFactory.vote(getChatAgent());
			isTalked = true;
		}
		return talkString;
	}

	@Override
	public String whisper() {
		String wisperString = TemplateTalkFactory.over();
		if (!isWispered) {
			wisperString = TemplateTalkFactory.vote(getChatAgent());
			isWispered = true;
		}
		return wisperString;
	}

	// 投票
	@Override
	public Agent vote() {
		return getChatAgent();
	}

	// 攻撃
	@Override
	public Agent attack() {
		List<Agent> attackList = new ArrayList<Agent>();

		for (Agent agent : getComeoutAgent()) {
			if (aliveAgent.indexOf(agent) >= 0) {
				attackList.add(agent);
			}
		}
		if (aliveAgent.indexOf(getChatAgent()) >= 0) {
			attackList.add(getChatAgent());
		}

		System.out.print("ATTACK List------>");
		for (Agent agent : attackList)
			System.out.print(agent);
		System.out.println("");

		return randomSelect(attackList);
	}

	// ゲーム終了時に実行
	@Override
	public void finish() {
	}

	@Override
	public void setVoteTarget() {
		// TODO 自動生成されたメソッド・スタブ

	}

}

//TODO wisper talkが雑 Overを会話量に含まない
