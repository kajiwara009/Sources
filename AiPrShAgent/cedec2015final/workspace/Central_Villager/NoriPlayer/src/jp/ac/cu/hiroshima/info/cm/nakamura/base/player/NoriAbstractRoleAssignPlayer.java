package jp.ac.cu.hiroshima.info.cm.nakamura.base.player;

import jp.ac.cu.hiroshima.info.cm.nakamura.player.NoriBodyguard;
import jp.ac.cu.hiroshima.info.cm.nakamura.player.NoriMedium;
import jp.ac.cu.hiroshima.info.cm.nakamura.player.NoriPossessed;
import jp.ac.cu.hiroshima.info.cm.nakamura.player.NoriSeer;
import jp.ac.cu.hiroshima.info.cm.nakamura.player.NoriVillager;
import jp.ac.cu.hiroshima.info.cm.nakamura.player.NoriWerewolf;

import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Player;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.net.GameInfo;
import org.aiwolf.common.net.GameSetting;


/**
 * 各プレイヤーに使用したいプレイヤーのインスタンスを生成して下さい． 例えば，村人のエージェントだけ自作のエージェントにしたい場合は， <br>
 * Player villagerPlayer = new SampleVillagerPlayer();<br>
 * ↓ <br>
 * Player villagerPlayer = new [自作プレイヤーのクラス名のコンストラクタ];<br>
 * と変更すれば，村人の役職が割り振られた時は自作のエージェント，それ以外の役職になった時はサンプルエージェントでプレイします．
 *
 * @author tori
 *
 */
abstract public class NoriAbstractRoleAssignPlayer implements Player {

	private NoriAbstractRole villagerPlayer = new NoriVillager();
	private NoriAbstractRole seerPlayer = new NoriSeer();
	private NoriAbstractRole mediumPlayer = new NoriMedium();
	private NoriAbstractRole bodyguardPlayer = new NoriBodyguard();
	private NoriAbstractRole possessedPlayer = new NoriPossessed();
	private NoriAbstractRole werewolfPlayer = new NoriWerewolf();

	private NoriAbstractRole rolePlayer;

	/**
	 * @return villagerPlayer
	 */
	final public NoriAbstractRole getVillagerPlayer() {
		return villagerPlayer;
	}

	/**
	 * @param villagerPlayer セットする villagerPlayer
	 */
	final public void setVillagerPlayer(NoriAbstractRole villagerPlayer) {
		this.villagerPlayer = villagerPlayer;
	}

	/**
	 * @return seerPlayer
	 */
	final public NoriAbstractRole getSeerPlayer() {
		return seerPlayer;
	}

	/**
	 * @param seerPlayer セットする seerPlayer
	 */
	final public void setSeerPlayer(NoriAbstractRole seerPlayer) {
		this.seerPlayer = seerPlayer;
	}

	/**
	 * @return mediumPlayer
	 */
	final public NoriAbstractRole getMediumPlayer() {
		return mediumPlayer;
	}

	/**
	 * @param mediumPlayer セットする mediumPlayer
	 */
	final public void setMediumPlayer(NoriAbstractRole mediumPlayer) {
		this.mediumPlayer = mediumPlayer;
	}

	/**
	 * @return bodyGuardPlayer
	 */
	final public NoriAbstractRole getBodyguardPlayer() {
		return bodyguardPlayer;
	}

	/**
	 * @param bodyGuardPlayer セットする bodyGuardPlayer
	 */
	final public void setBodyguardPlayer(NoriAbstractRole bodyGuardPlayer) {
		this.bodyguardPlayer = bodyGuardPlayer;
	}

	/**
	 * @return possesedPlayer
	 */
	final public NoriAbstractRole getPossessedPlayer() {
		return possessedPlayer;
	}

	/**
	 * @param possesedPlayer セットする possesedPlayer
	 */
	final public void setPossessedPlayer(NoriAbstractRole possesedPlayer) {
		this.possessedPlayer = possesedPlayer;
	}

	/**
	 * @return werewolfPlayer
	 */
	final public NoriAbstractRole getWerewolfPlayer() {
		return werewolfPlayer;
	}

	/**
	 * @param werewolfPlayer セットする werewolfPlayer
	 */
	final public void setWerewolfPlayer(NoriAbstractRole werewolfPlayer) {
		this.werewolfPlayer = werewolfPlayer;
	}

	@Override
	abstract public String getName();

	@Override
	final public void update(GameInfo gameInfo) {
		rolePlayer.update(gameInfo);
	}

	@Override
	final public void initialize(GameInfo gameInfo, GameSetting gameSetting) {
		Role myRole = gameInfo.getRole();
		switch (myRole) {
		case VILLAGER:
			rolePlayer = villagerPlayer;
			break;
		case SEER:
			rolePlayer = seerPlayer;
			break;
		case MEDIUM:
			rolePlayer = mediumPlayer;
			break;
		case BODYGUARD:
			rolePlayer = bodyguardPlayer;
			break;
		case POSSESSED:
			rolePlayer = possessedPlayer;
			break;
		case WEREWOLF:
			rolePlayer = werewolfPlayer;
			break;
		default:
			rolePlayer = villagerPlayer;
			break;

		}
		rolePlayer.initialize(gameInfo, gameSetting);

	}

	@Override
	final public void dayStart() {
		rolePlayer.dayStart();
	}

	@Override
	final public String talk() {
		return rolePlayer.talk();
	}

	@Override
	final public String whisper() {
		return rolePlayer.whisper();
	}

	@Override
	final public Agent vote() {
		return rolePlayer.vote();
	}

	@Override
	final public Agent attack() {
		return rolePlayer.attack();
	}

	@Override
	final public Agent divine() {
		return rolePlayer.divine();
	}

	@Override
	final public Agent guard() {
		return rolePlayer.guard();
	}

	@Override
	final public void finish() {
		rolePlayer.finish();
	}

}
