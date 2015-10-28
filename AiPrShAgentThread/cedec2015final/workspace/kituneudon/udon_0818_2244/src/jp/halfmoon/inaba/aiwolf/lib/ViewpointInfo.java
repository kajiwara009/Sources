package jp.halfmoon.inaba.aiwolf.lib;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Species;
import org.aiwolf.common.net.GameSetting;


/**
 * 視点情報を表すクラス
 */
public final class ViewpointInfo {

	/** この視点で存在しうる、狼陣営の組み合わせパターン */
	public LinkedHashSet<WolfsidePattern> wolfsidePatterns;

	/** この視点が包含する視点 */
	public List<ViewpointInfo> inclusionViewpoint = new ArrayList<ViewpointInfo>();



	/** キャッシュ情報が有効か */
	private boolean isCacheEnable;


	/** 狼のパターンが存在しないエージェント一覧 */
	private List<Integer> notWolfAgentNo = new ArrayList<Integer>();

	/** 狼/非狼のパターンが両方存在するエージェント一覧 */
	private List<Integer> unclearWolfAgentNo = new ArrayList<Integer>();

	/** 狼のパターンのみが存在するエージェント一覧 */
	private List<Integer> fixWolfAgentNo = new ArrayList<Integer>();


	/** 狂のパターンが存在しないエージェント一覧 */
	private List<Integer> notPossessedAgentNo = new ArrayList<Integer>();

	/** 狂/非狂のパターンが両方存在するエージェント一覧 */
	private List<Integer> unclearPossessedAgentNo = new ArrayList<Integer>();

	/** 狂のパターンのみが存在するエージェント一覧 */
	private List<Integer> fixPossessedAgentNo = new ArrayList<Integer>();



	/** 人外のパターンが存在しないエージェント一覧 */
	private List<Integer> notWolfSideAgentNo = new ArrayList<Integer>();

	/** 人外/非人外のパターンが両方存在するエージェント一覧 */
	private List<Integer> unclearWolfSideAgentNo = new ArrayList<Integer>();

	/** 人外のパターンのみが存在するエージェント一覧 */
	private List<Integer> fixWolfSideAgentNo = new ArrayList<Integer>();






	/**
	 * コンストラクタ
	 * @param gameSetting ゲーム設定
	 */
	public ViewpointInfo(GameSetting gameSetting){

		// 全視点の狼陣営パターンを作成する
		setWolfSidePattern(gameSetting);

		// パターンに変更が出たため、キャッシュ情報を無効にする
		isCacheEnable = false;

	}


	/**
	 * コンストラクタ
	 * @param parent この視点を包含する視点
	 */
	public ViewpointInfo(ViewpointInfo parent){

		// 狼陣営をコピーする（狼陣営の参照をコピー）
		wolfsidePatterns = new LinkedHashSet<WolfsidePattern>(parent.wolfsidePatterns.size() + 1, 1.0f);
		wolfsidePatterns.addAll(parent.wolfsidePatterns);

		// パターンに変更が出たため、キャッシュ情報を無効にする
		isCacheEnable = false;

	}


	/**
	 * コンストラクタ(CODEC2015専用)
	 * @param dummy 意味無し
	 */
	public ViewpointInfo(int dummy){

		// 全視点の狼陣営パターンを作成する
		setWolfSidePattern_CODEC2015();

		// パターンに変更が出たため、キャッシュ情報を無効にする
		isCacheEnable = false;

	}


	/**
	 * この視点が包含する視点を追加する
	 * @param child
	 */
	public void addInclusionViewpoint(ViewpointInfo child){
		inclusionViewpoint.add(child);
	}


	/**
	 * 視点情報を作り直す（親視点に揃える）
	 * @param parent この視点を包含する視点
	 */
	public void remakePattern(ViewpointInfo parent){

		// 狼陣営をコピーする（狼陣営の参照をコピー）　※初期領域の削減を狙って、新しく生成し直す
		wolfsidePatterns = new LinkedHashSet<WolfsidePattern>(parent.wolfsidePatterns.size() + 1, 1.0f);
		wolfsidePatterns.addAll(parent.wolfsidePatterns);

		// パターンに変更が出たため、キャッシュ情報を無効にする
		isCacheEnable = false;

 		// 包含する視点も、この視点を元に作り直す
		for( ViewpointInfo child : inclusionViewpoint ){
			child.remakePattern(this);
		}

	}


	/**
	 * 全視点の狼陣営の組み合わせパターンを設定する(CODEC2015用コード)
	 */
 	private void setWolfSidePattern_CODEC2015(){

 		final int playerNum = 15;

 		LinkedHashSet<WolfsidePattern> patterns = new LinkedHashSet<WolfsidePattern>(5460, 1.0f);

		// 狼A
		for(int wolfAcnt=1; wolfAcnt <= playerNum - 2; wolfAcnt++){
			// 狼B
			for(int wolfBcnt = wolfAcnt + 1; wolfBcnt <= playerNum - 1; wolfBcnt++){
				// 狼C
				for(int wolfCcnt = wolfBcnt + 1; wolfCcnt <= playerNum; wolfCcnt++){

					ArrayList<Integer> wolves = new ArrayList<Integer>();
					wolves.add(wolfAcnt);
					wolves.add(wolfBcnt);
					wolves.add(wolfCcnt);

					// 狂
					for(int possessedcnt = 1; possessedcnt <= playerNum; possessedcnt++){
						if( possessedcnt != wolfAcnt && possessedcnt != wolfBcnt && possessedcnt != wolfCcnt ){

							ArrayList<Integer> possessed = new ArrayList<Integer>();
							possessed.add(possessedcnt);

							WolfsidePattern pattern = new WolfsidePattern(wolves, possessed);
							patterns.add( pattern );
						}
					}
				}
			}
		}

		wolfsidePatterns = patterns;

 	}


	//TODO 他編成対応
	/**
	 * 全視点の狼陣営の組み合わせパターンを設定する
	 * @param gameSetting
	 */
 	private void setWolfSidePattern(GameSetting gameSetting){

 		LinkedHashSet<WolfsidePattern> patterns = new LinkedHashSet<WolfsidePattern>(5460, 1.0f);

		// ２狼１狂
		if( gameSetting.getRoleNum(Role.WEREWOLF) == 2 && gameSetting.getRoleNum(Role.POSSESSED) == 1 ){
			// 狼A
			for(int wolfAcnt=1; wolfAcnt <= gameSetting.getPlayerNum() - 2; wolfAcnt++){
				// 狼B
				for(int wolfBcnt = wolfAcnt + 1; wolfBcnt <= gameSetting.getPlayerNum() - 1; wolfBcnt++){

					ArrayList<Integer> wolves = new ArrayList<Integer>();
					wolves.add(wolfAcnt);
					wolves.add(wolfBcnt);

					// 狂
					for(int possessedcnt = 1; possessedcnt <= gameSetting.getPlayerNum(); possessedcnt++){
						if( possessedcnt != wolfAcnt && possessedcnt != wolfBcnt ){

							ArrayList<Integer> possessed = new ArrayList<Integer>();
							possessed.add(possessedcnt);

							WolfsidePattern pattern = new WolfsidePattern(wolves, possessed);
							patterns.add( pattern );
						}
					}
				}
			}
		}

		// ３狼１狂
		if( gameSetting.getRoleNum(Role.WEREWOLF) == 3 && gameSetting.getRoleNum(Role.POSSESSED) == 1 ){
			// 狼A
			for(int wolfAcnt=1; wolfAcnt <= gameSetting.getPlayerNum() - 2; wolfAcnt++){
				// 狼B
				for(int wolfBcnt = wolfAcnt + 1; wolfBcnt <= gameSetting.getPlayerNum() - 1; wolfBcnt++){
					// 狼C
					for(int wolfCcnt = wolfBcnt + 1; wolfCcnt <= gameSetting.getPlayerNum(); wolfCcnt++){

						ArrayList<Integer> wolves = new ArrayList<Integer>();
						wolves.add(wolfAcnt);
						wolves.add(wolfBcnt);
						wolves.add(wolfCcnt);

						// 狂
						for(int possessedcnt = 1; possessedcnt <= gameSetting.getPlayerNum(); possessedcnt++){
							if( possessedcnt != wolfAcnt && possessedcnt != wolfBcnt && possessedcnt != wolfCcnt ){

								ArrayList<Integer> possessed = new ArrayList<Integer>();
								possessed.add(possessedcnt);

								WolfsidePattern pattern = new WolfsidePattern(wolves, possessed);
								patterns.add( pattern );
							}
						}
					}
				}
			}
		}

		wolfsidePatterns = patterns;

	}


 	/**
 	 * 特定の狼陣営パターンを削除する
 	 * @param pattern
 	 */
 	private void removePattern(WolfsidePattern pattern, Iterator<WolfsidePattern> iter){

 		// 包含する視点から狼陣営のパターンを削除する
		for( ViewpointInfo child : inclusionViewpoint ){
			child.removePattern(pattern);
		}

 		// この視点から狼陣営のパターンを削除する
		iter.remove();

		// パターンに変更が出たため、キャッシュ情報を無効にする
		isCacheEnable = false;

 	}


 	/**
 	 * 特定の狼陣営パターンを削除する
 	 * @param pattern
 	 */
 	private void removePattern(WolfsidePattern pattern){

 		// この視点から狼陣営のパターンを削除する
		wolfsidePatterns.remove(pattern);

 		// 包含する視点から狼陣営のパターンを削除する
		for( ViewpointInfo child : inclusionViewpoint ){
			child.removePattern(pattern);
		}

		// パターンに変更が出たため、キャッシュ情報を無効にする
		isCacheEnable = false;

 	}


	/**
	 * 特定のエージェントが狼のパターンを削除する
	 * @param agentNo
	 */
	public void removeWolfPattern(int agentNo){

		// 全パターンチェック
		Iterator<WolfsidePattern> iter = wolfsidePatterns.iterator();
		while( iter.hasNext() ){
			WolfsidePattern pattern = iter.next();

			// 指定エージェントが狼のパターンであれば削除する
			if( pattern.isWolf(agentNo) ){
				removePattern(pattern, iter);
			}
		}

	}


	/**
	 * 特定のエージェントが狼陣営のパターンを削除する
	 * @param agentNo
	 */
	public void removeWolfsidePattern(int agentNo){

		// 全パターンチェック
		Iterator<WolfsidePattern> iter = wolfsidePatterns.iterator();
		while( iter.hasNext() ){
			WolfsidePattern pattern = iter.next();

			// 指定エージェントが狼陣営のパターンであれば削除する
			if( pattern.isWolfSide(agentNo) ){
				removePattern(pattern, iter);
			}
		}

	}


	/**
	 * 特定のエージェントが狂人のパターン以外を削除する
	 * @param agentNo
	 */
	public void removeNotPossessedPattern(int agentNo){

		// 全パターンチェック
		Iterator<WolfsidePattern> iter = wolfsidePatterns.iterator();
		while( iter.hasNext() ){
			WolfsidePattern pattern = iter.next();

			// 指定エージェントが狂人のパターンでなければ削除する
			if( !pattern.isPossessed(agentNo) ){
				removePattern(pattern, iter);
			}
		}

	}


	/**
	 * 特定のエージェントが狼陣営でないパターンを削除する
	 * @param agentNo
	 */
	public void removeNotWolfsidePattern(int agentNo){

		// 全パターンチェック
		Iterator<WolfsidePattern> iter = wolfsidePatterns.iterator();
		while( iter.hasNext() ){
			WolfsidePattern pattern = iter.next();

			// 指定エージェントが狼陣営のパターンでなければ削除する
			if( !pattern.isWolfSide(agentNo) ){
				removePattern(pattern, iter);
			}
		}

	}


	/**
	 * １人しか存在しない役職からの絞り込み（指定エージェントのうち、村側が０～１名として内訳整理する）
	 * @param agentNo
	 */
	public void removePatternFromUniqueRole(List<Integer> agentNo){

		// 全パターンチェック
		Iterator<WolfsidePattern> iter = wolfsidePatterns.iterator();
		while( iter.hasNext() ){
			WolfsidePattern pattern = iter.next();

			// 指定エージェントに狼陣営に含まない者が２名以上いるパターンを削除
			boolean flag = false;
			for( int agent : agentNo ){
				if( !pattern.isWolfSide(agent) ){
					if( flag == false ){
						flag = true;
					}else{
						removePattern(pattern, iter);
						break;
					}
				}
			}
		}

	}


	/**
	 * 特定グループ内の狼陣営人数を指定し、条件を満たさないパターンを削除する
	 * @param agentNo グループのエージェント番号一覧
	 * @param minnum グループ内の最小狼陣営数
	 * @param maxnum グループ内の最大狼陣営数
	 */
	public void removePatternFromWolfSideNum(List<Integer> agentNo, int minnum, int maxnum){

		// 全パターンチェック
		Iterator<WolfsidePattern> iter = wolfsidePatterns.iterator();
		while( iter.hasNext() ){
			WolfsidePattern pattern = iter.next();

			// 指定パターンの狼数をカウント
			int count = 0;
			for( int agent : agentNo ){
				if( pattern.isWolfSide(agent) ){
					count++;
				}
			}
			// 指定された人数から外れていればパターンを削除
			if( count < minnum || count > maxnum ){
				removePattern(pattern, iter);
			}
		}

	}


	/**
	 * 特定グループ内の狼人数を指定し、条件を満たさないパターンを削除する
	 * @param agentNo グループのエージェント番号一覧(ソートするので変更しても問題ない物)
	 * @param minnum グループ内の最小狼数
	 * @param maxnum グループ内の最大狼数
	 */
	public void removePatternFromWolfNum(List<Integer> agentNo, int minnum, int maxnum){

		// エージェント番号一覧を昇順ソート
		Collections.sort(agentNo);

		// 全パターンチェック
		Iterator<WolfsidePattern> iter = wolfsidePatterns.iterator();
		while( iter.hasNext() ){
			WolfsidePattern pattern = iter.next();

			// 指定パターンの狼数をカウント
			int count = 0;
//			for( int agent : agentNo ){
//				if( pattern.isWolf(agent) ){
//					count++;
//				}
//			}
			List<Integer> wolves = pattern.wolfAgentNo;
			int listIdx = 0;
			int patternIdx = 0;
			int listVal;
			int patternVal;
			while( listIdx < agentNo.size() && patternIdx < wolves.size() ){
				listVal = agentNo.get(listIdx);
				patternVal = wolves.get(patternIdx);
				if( listVal < patternVal ){
					listIdx++;
				}else if( listVal > patternVal ){
					patternIdx++;
				}else{
					count++;
					listIdx++;
					patternIdx++;
				}
			}

			// 指定された人数から外れていればパターンを削除
			if( count < minnum || count > maxnum ){
				removePattern(pattern, iter);
			}
		}

	}


	/**
	 * 判定からのパターン絞り込み
	 * @param agentNo 判定を出した占霊のNo
	 * @param targetno 判定対象のNo
	 * @param result 判定結果
	 */
	public void removePatternFromJudge(int agentNo, int targetno, Species result){

		// 全パターンチェック
		Iterator<WolfsidePattern> iter = wolfsidePatterns.iterator();
		while( iter.hasNext() ){
			WolfsidePattern pattern = iter.next();

			// 判定を出した占霊が人外のパターンは対象外
			if( pattern.isWolfSide(agentNo) ){
				continue;
			}

			// 黒判定か
			if( result == Species.WEREWOLF ){
				// 黒判定 → 対象エージェントが狼のパターンでなければ削除する
				if( !pattern.isWolf(targetno) ){
					removePattern(pattern, iter);
				}
			}else{
				// 白判定 → 対象エージェントが狼のパターンであれば削除する
				if( pattern.isWolf(targetno) ){
					removePattern(pattern, iter);
				}
			}

		}

	}


	/**
	 * 視点情報のキャッシュを作成する
	 */
	private void makeCache(){

		// 白・灰・黒の一覧をクリア
		notWolfAgentNo.clear();
		unclearWolfAgentNo.clear();
		fixWolfAgentNo.clear();

		//TODO 他編成対応
		int wolfCount[] = new int[15+1];
		int possessedCount[] = new int[15+1];

		// 各PLが狼・狂人として含まれるパターンの数をカウント
		for( WolfsidePattern pattern : wolfsidePatterns ){
			for( int wolf : pattern.wolfAgentNo ){
				wolfCount[wolf]++;
			}
			for( int pos : pattern.possessedAgentNo ){
				possessedCount[pos]++;
			}
		}

		// パターンの数によって白・灰・黒の一覧に振り分ける
		for( int i = 1; i <= 15; i++ ){

			// 狼系
			if( wolfCount[i] == 0 ){
				notWolfAgentNo.add(i);
			}else if( wolfCount[i] == wolfsidePatterns.size() ){
				fixWolfAgentNo.add(i);
			}else{
				unclearWolfAgentNo.add(i);
			}

			// 狂人系
			if( possessedCount[i] == 0 ){
				notPossessedAgentNo.add(i);
			}else if( possessedCount[i] == wolfsidePatterns.size() ){
				fixPossessedAgentNo.add(i);
			}else{
				unclearPossessedAgentNo.add(i);
			}

			// 人外系
			if( wolfCount[i] + possessedCount[i] == 0 ){
				notWolfSideAgentNo.add(i);
			}else if( wolfCount[i] + possessedCount[i] == wolfsidePatterns.size() ){
				fixWolfSideAgentNo.add(i);
			}else{
				unclearWolfSideAgentNo.add(i);
			}

		}

		// キャッシュを有効にする
		isCacheEnable = true;

	}


	/**
	 * 特定のエージェントが確定黒かを取得する
	 * @param agentNo
	 * @return
	 */
	public boolean isFixBlack(int agentNo){

		// キャッシュ情報が有効でなければ作成する
		if( !isCacheEnable ){
			makeCache();
		}

		// 確定黒のリストに含まれれば確定黒
		if( fixWolfAgentNo.indexOf(agentNo) != -1 ){
			return true;
		}

		// 確定黒ではない
		return false;

	}


	/**
	 * 特定のエージェントが確定白かを取得する
	 * @param agentNo
	 * @return
	 */
	public boolean isFixWhite(int agentNo){

		// キャッシュ情報が有効でなければ作成する
		if( !isCacheEnable ){
			makeCache();
		}

		// 確定黒のリストに含まれれば確定黒
		if( notWolfAgentNo.indexOf(agentNo) != -1 ){
			return true;
		}

		// 確定黒ではない
		return false;

	}


	/**
	 * 特定のエージェントが確定人外かを取得する
	 * @param agentNo
	 * @return
	 */
	public boolean isFixWolfSide(int agentNo){

		// キャッシュ情報が有効でなければ作成する
		if( !isCacheEnable ){
			makeCache();
		}

		// 確定人外のリストに含まれれば確定人外
		if( fixWolfSideAgentNo.indexOf(agentNo) != -1 ){
			return true;
		}

		// 確定人外ではない
		return false;

	}


	/**
	 * 黒確定のエージェント一覧を取得する
	 * @return
	 */
	public List<Integer> getFixBlackAgent(){

		// キャッシュ情報が有効でなければ作成する
		if( !isCacheEnable ){
			makeCache();
		}

		return fixWolfAgentNo;

	}


	/**
	 * 白確定のエージェント一覧を取得する
	 * @return
	 */
	public List<Integer> getFixWhiteAgent(){

		// キャッシュ情報が有効でなければ作成する
		if( !isCacheEnable ){
			makeCache();
		}

		return notWolfAgentNo;

	}

}
