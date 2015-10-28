package jp.halfmoon.inaba.aiwolf.condition;

import java.util.ArrayList;

import jp.halfmoon.inaba.aiwolf.lib.WolfsidePattern;

import org.aiwolf.common.data.Role;


/**
 * 役職の条件を表すクラス
 */
public final class RoleCondition extends AbstractCondition {


	/** エージェント番号 */
	public int agentNo;

	/** 役職 */
	public Role role;

	/** 指定した役職以外であることを表す  */
	public boolean isRoleBesides = false;


	// ↓キャッシュ用

	public static final int AGENT_MAX = 15;

	private static RoleCondition wolfCondition[] = new RoleCondition[AGENT_MAX + 1];
	private static RoleCondition possessedCondition[] = new RoleCondition[AGENT_MAX + 1];
	private static RoleCondition notWolfCondition[] = new RoleCondition[AGENT_MAX + 1];
	private static RoleCondition notPossessedCondition[] = new RoleCondition[AGENT_MAX + 1];

	// ↑キャッシュ用


	private RoleCondition( int agentno, Role role ) {
		this.agentNo = agentno;
		this.role = role;
	}


	private RoleCondition( int agentno, Role role, boolean isRoleBesides) {
		this.agentNo = agentno;
		this.role = role;
		this.isRoleBesides = isRoleBesides;
	}


	@Override
	public boolean isValid( WolfsidePattern pattern ) {

		// 指定した役職へのマッチか
		if( isRoleBesides == false ){
			// 指定した役職にマッチする
			switch( role ){
				case WEREWOLF:
					if( pattern.isWolf(agentNo) ){
						return true;
					}
					break;
				case POSSESSED:
					if( pattern.isPossessed(agentNo) ){
						return true;
					}
					break;
				default:
					//TODO その他役職は未対応
					// 狼陣営に含まれるか＋各狼陣営時に内訳が存在するか？
					break;
			}
		}else{
			// 指定した役職以外にマッチする

			switch( role ){
				case WEREWOLF:
					if( !pattern.isWolf(agentNo) ){
						return true;
					}
					break;
				case POSSESSED:
					if( !pattern.isPossessed(agentNo) ){
						return true;
					}
					break;
				default:
					//TODO その他役職は未対応
					// 狼陣営に含まれるか＋各狼陣営時に内訳が存在するか？
					break;
			}
		}

		return false;
	}


	@Override
	public ArrayList<Integer> getTargetAgentNo() {

		// エージェント番号をリストにして返す
		ArrayList<Integer> ret = new ArrayList<Integer>();
		ret.add(agentNo);

		return ret;

	}


	/**
	 * 「特定の役職である」条件を取得する
	 * @param agentNo
	 * @param role
	 * @return
	 */
	public static RoleCondition getRoleCondition( int agentNo, Role role ){

		if( agentNo < 1 || agentNo > AGENT_MAX ){
			return new RoleCondition(agentNo, role);
		}

		switch( role ){
			case WEREWOLF:
				if( wolfCondition[agentNo] == null ){
					wolfCondition[agentNo] = new RoleCondition(agentNo, role);
				}
				return wolfCondition[agentNo];
			case POSSESSED:
				if( possessedCondition[agentNo] == null ){
					possessedCondition[agentNo] = new RoleCondition(agentNo, role);
				}
				return possessedCondition[agentNo];
			default:
				break;
		}

		return new RoleCondition(agentNo, role);

	}


	/**
	 * 「特定の役職でない」条件を取得する
	 * @param agentNo
	 * @param role
	 * @return
	 */
	public static RoleCondition getNotRoleCondition( int agentNo, Role role ){

		if( agentNo < 1 || agentNo > AGENT_MAX ){
			return new RoleCondition(agentNo, role, true);
		}

		switch( role ){
			case WEREWOLF:
				if( notWolfCondition[agentNo] == null ){
					notWolfCondition[agentNo] = new RoleCondition(agentNo, role, true);
				}
				return notWolfCondition[agentNo];
			case POSSESSED:
				if( notPossessedCondition[agentNo] == null ){
					notPossessedCondition[agentNo] = new RoleCondition(agentNo, role, true);
				}
				return notPossessedCondition[agentNo];
			default:
				break;
		}

		return new RoleCondition(agentNo, role, true);

	}

}
