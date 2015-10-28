package jp.ac.aitech.k13009kk.aiwolf.client.player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.aiwolf.client.lib.TemplateTalkFactory;
import org.aiwolf.client.lib.Utterance;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Talk;
import org.aiwolf.common.net.GameInfo;
import org.aiwolf.common.net.GameSetting;

import jp.ac.aitech.k13009kk.aiwolf.client.player.base.AbstractAndoWerewolf;

public class AndoWerewolf extends AbstractAndoWerewolf {

	// その日のログの何番目まで読み込んだか
	int readTalkNum = 0;

	// 真占い師
	private Agent trueSeer;

	// 真占い師COがされたかどうか
	private boolean isComingOutTrueSeer = false;

	// 何日にカミングアウトするか
	int comingOutDay;

	// 自分の役職をカミングアウトしたかどうか
	private boolean isComingOut;

	// カミングアウトしたプレイヤーリスト
	// 役職ごと
	private List<Agent> comedOutSeerAgent;				// 占い師
	private List<Agent> comedOutBodyGuardAgent;	// 狩人
	private List<Agent> comedOutMediumAgent;		// 霊能者
	private List<Agent> comedOutVillagerAgent;		// 村人

	// ゲーム設定よりカミングアウトした人数多い役職リスト
	private List<Role> roleNumOverAgent;

	// 自分が偽カミングアウトした役職
	private Role comingOutRole;


	@Override
	public void initialize(GameInfo gameInfo, GameSetting gameSetting) {
		super.initialize(gameInfo, gameSetting);
		Random rnd = new Random();
		// Day 01 ~ 05の間にカミングアウトする日を決める
		comingOutDay = rnd.nextInt(5) + 1;

		// 各リストの初期化
		comedOutSeerAgent = new ArrayList<Agent>();
		comedOutBodyGuardAgent = new ArrayList<Agent>();
		comedOutMediumAgent = new ArrayList<Agent>();
		comedOutVillagerAgent = new ArrayList<Agent>();

		roleNumOverAgent = new ArrayList<Role>();
	}


	@Override
	public Agent attack() {

		// 真占い師がまだ生きているなら, 占い師を襲撃
		if(getLatestDayGameInfo().getAliveAgentList().contains(trueSeer)){
			return trueSeer;
		}

		// 襲撃対象の候補者リスト
		List<Agent> voteCandidates = new ArrayList<Agent>();

		// 生きているプレイヤーを候補者リストに加える
		voteCandidates.addAll(this.getLatestDayGameInfo().getAliveAgentList());

		// ゲーム設定よりカミングアウトした人数多い役職は狂人がいる可能性があるので擁護するため外す
		// ただし, 自分が含まれるカミングアウトリストは必然的に人数が多くなるため除く
		if(roleNumOverAgent.contains(Role.SEER) && !comingOutRole.equals(Role.SEER))
			voteCandidates.removeAll(comedOutSeerAgent);
		if(roleNumOverAgent.contains(Role.BODYGUARD) && !comingOutRole.equals(Role.BODYGUARD))
			voteCandidates.removeAll(comedOutBodyGuardAgent);
		if(roleNumOverAgent.contains(Role.MEDIUM) && !comingOutRole.equals(Role.MEDIUM))
			voteCandidates.removeAll(comedOutMediumAgent);
		if(roleNumOverAgent.contains(Role.VILLAGER) && !comingOutRole.equals(Role.VILLAGER))
			voteCandidates.removeAll(comedOutVillagerAgent);

		// 自分を含めた人狼は外す
		voteCandidates.removeAll(getWolfList());

		return randomSelect(voteCandidates);
	}

	@Override
	public void dayStart() {
		// 襲撃された人を各リストから外す
		Agent attackedAgent = this.getLatestDayGameInfo().getAttackedAgent();
		if(comedOutSeerAgent.contains(attackedAgent))
			comedOutSeerAgent.remove(attackedAgent);
		if(comedOutBodyGuardAgent.contains(attackedAgent))
			comedOutBodyGuardAgent.remove(attackedAgent);
		if(comedOutMediumAgent.contains(attackedAgent))
			comedOutMediumAgent.remove(attackedAgent);
		if(comedOutVillagerAgent.contains(attackedAgent))
			comedOutVillagerAgent.remove(attackedAgent);
	}

	@Override
	public void finish() {

	}

	@Override
	public String talk() {
		// まだ自分の役職をカミングアウトしていなくて, カミングアウトする日が来た
		if (getDay() == comingOutDay && !isComingOut){

			// カミングアウトする
			isComingOut = true;

			// 真占い師がカミングアウトしていない場合
			if(!isComingOutTrueSeer){

				// 偽占い師としてカミングアウトする
				String comingOutTalk = TemplateTalkFactory.comingout(getMe(), Role.SEER);
				comingOutRole = Role.SEER;
				return comingOutTalk;
			} else {

				// 村人としてカミングアウトする
				String comingOutTalk = TemplateTalkFactory.comingout(getMe(), Role.VILLAGER);
				comingOutRole = Role.VILLAGER;
				return comingOutTalk;
			}
		}

		return Talk.OVER;
	}

	@Override
	public void update(GameInfo gameInfo) {
		super.update(gameInfo);
		// 今日のログを取得
		List<Talk> talkList = gameInfo.getTalkList();

		for (int i = readTalkNum; i < talkList.size(); i++) {
			Talk talk = talkList.get(i);

			// 発話をパース
			Utterance utterance = new Utterance(talk.getContent());

			// 発話のトピックごとに処理
			switch (utterance.getTopic()) {
				case COMINGOUT:
					// カミングアウトの発話の処理
					// 自分以外で占い師COしているプレイヤーの場合
					if (utterance.getRole() == Role.SEER && !talk.getAgent().equals(getMe())) {

						// 真占い師として特定
						trueSeer = utterance.getTarget();

						// 真占い師COがされた
						isComingOutTrueSeer = true;
					}
					break;
				default:
					break;
			}
			readTalkNum++;
		}

		// ゲーム設定よりカミングアウトした人数多い役職があるか
		boolean isSeerNumOver = getGameSetting().getRoleNum(Role.SEER) < comedOutSeerAgent.size();
		boolean isBodyguardNumOver = getGameSetting().getRoleNum(Role.BODYGUARD) < comedOutBodyGuardAgent.size();
		boolean isMediumNumOver = getGameSetting().getRoleNum(Role.MEDIUM) < comedOutMediumAgent.size();
		boolean isVillagerNumOver = getGameSetting().getRoleNum(Role.VILLAGER) < comedOutVillagerAgent.size();

		// ゲーム設定よりカミングアウトした人数多い役職リストに役職を加える
		if(isSeerNumOver)
			roleNumOverAgent.add(Role.SEER);
		else if(roleNumOverAgent.contains(Role.SEER))
				roleNumOverAgent.remove(Role.SEER);
		if(isBodyguardNumOver)
			roleNumOverAgent.add(Role.BODYGUARD);
		else if(roleNumOverAgent.contains(Role.BODYGUARD))
			roleNumOverAgent.remove(Role.BODYGUARD);
		if(isMediumNumOver)
			roleNumOverAgent.add(Role.MEDIUM);
		else if(roleNumOverAgent.contains(Role.MEDIUM))
			roleNumOverAgent.remove(Role.MEDIUM);
		if(isVillagerNumOver)
			roleNumOverAgent.add(Role.VILLAGER);
		else if(roleNumOverAgent.contains(Role.VILLAGER))
			roleNumOverAgent.remove(Role.VILLAGER);
	}

	@Override
	public Agent vote() {

		// 真占い師がまだ生きているなら, 占い師に投票
		if(getLatestDayGameInfo().getAliveAgentList().contains(trueSeer)){
			return trueSeer;
		}

		// 投票対象の候補者リスト
		List<Agent> voteCandidates = new ArrayList<Agent>();

		// 生きているプレイヤーを候補者リストに加える
		voteCandidates.addAll(this.getLatestDayGameInfo().getAliveAgentList());

		// ゲーム設定よりカミングアウトした人数多い役職は狂人がいる可能性があるので擁護するため外す
		// ただし, 自分が含まれるカミングアウトリストは必然的に人数が多くなるため除く
		if(roleNumOverAgent.contains(Role.SEER) && !comingOutRole.equals(Role.SEER))
			voteCandidates.removeAll(comedOutSeerAgent);
		if(roleNumOverAgent.contains(Role.BODYGUARD) && !comingOutRole.equals(Role.BODYGUARD))
			voteCandidates.removeAll(comedOutBodyGuardAgent);
		if(roleNumOverAgent.contains(Role.MEDIUM) && !comingOutRole.equals(Role.MEDIUM))
			voteCandidates.removeAll(comedOutMediumAgent);
		if(roleNumOverAgent.contains(Role.VILLAGER) && !comingOutRole.equals(Role.VILLAGER))
			voteCandidates.removeAll(comedOutVillagerAgent);

		// 自分を含めた人狼は外す
		voteCandidates.removeAll(getWolfList());

		return randomSelect(voteCandidates);
	}

	@Override
	public String whisper() {

		return null;
	}

	@Override
	public void setVoteTarget() {
		// TODO 自動生成されたメソッド・スタブ

	}
}
