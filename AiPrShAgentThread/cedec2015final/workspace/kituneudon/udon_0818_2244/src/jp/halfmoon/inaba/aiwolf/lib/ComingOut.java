package jp.halfmoon.inaba.aiwolf.lib;

import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Talk;

/**
 * カミングアウト(役職の表明)を表すクラス
 */
public final class ComingOut {

	/**
	 * エージェント番号
	 */
	public final int agentNo;

	/**
	 * COした役職
	 */
	public final Role role;

	/**
	 * COした発言
	 */
	public final Talk commingOutTalk;

	/**
	 * CO撤回した発言
	 */
	public Talk cancelTalk;


	/**
	 * コンストラクタ
	 * @param agentNo エージェント番号
	 * @param role COした役職
	 * @param commingOutTalkCOした発言
	 */
	public ComingOut(int agentNo, Role role, Talk commingOutTalk){
		this.agentNo = agentNo;
		this.role = role;
		this.commingOutTalk = commingOutTalk;
	}


	/**
	 * COが有効な状態か
	 * @return
	 */
	public boolean isEnable(){
		// CO後、かつCO撤回前に有効
		if( commingOutTalk != null && cancelTalk == null ){
			return true;
		}

		return false;
	}


	/**
	 * COが有効な状態か(指定した発言の直前状態として判定)
	 * @param day 日
	 * @param talkID 発言ID
	 * @return
	 */
	public boolean isEnable( int day, int talkID ){

		// CO後か
		if( commingOutTalk != null || Common.compareTalkID( commingOutTalk.getDay(), commingOutTalk.getIdx(), day, talkID) == -1 ){
			// 撤回前か
			if( cancelTalk == null || Common.compareTalkID( commingOutTalk.getDay(), commingOutTalk.getIdx(), day, talkID) >= 0  ){
				// CO後、かつCO撤回前に有効
				return true;
			}
		}

		return false;
	}

}
