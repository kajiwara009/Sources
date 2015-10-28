package takata.player;

import java.util.HashMap;
import java.util.Map;

import org.aiwolf.common.data.Role;

/*
 * カミングアウト結果を保存するクラス
 * 作成日時：2015.07.21
 * 作成者：高田
 */


public class COMapInfo {

	private Map<Integer, Role> COMap = new HashMap<Integer, Role>();
	private Role COPastData[][] = new Role[16][99];

	//カミングアウト結果を保存
	public void putCOMap(Integer agentnum, Role role, Integer conum) {
		//既にCOの場合
		if(conum > 0) {
			COPastData[agentnum][conum-1] = COMap.get(agentnum);
			COMap.put(agentnum, role);
		}else {
			COMap.put(agentnum, role);
		}
	}
	//カミングアウト集計マップを取得
	public Map<Integer, Role> getCOMap() {
		return COMap;
	}
	//過去のカミングアウト集計結果を取得
	public Role getCOPastMap(Integer agentnum, Integer conum) {
		return COPastData[agentnum][conum];
	}
}
