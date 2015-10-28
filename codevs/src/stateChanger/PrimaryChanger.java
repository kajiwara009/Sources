package stateChanger;

import stateDirector.AssaultDirector;
import stateDirector.InvadeDirector;
import stateDirector.OpeningDirector;
import stateDirector.StateDirector;
import twinDirector.FighterDirector;
import twinDirector.WorkerDirector;
import codevs.God;

/**
 * Opening, Invade, Assaultの順に移行していく
 * @author kajiwarakengo
 *
 */
public class PrimaryChanger extends StateChanger {

	public PrimaryChanger(God god) {
		super(god);
		// TODO 自動生成されたコンストラクター・スタブ
	}

	@Override
	public void think() {
		StateDirector sd = god.getStateDirector();
		if(isSilber || god.hasFoundOpCastle()){
			god.setStateDirector(new AssaultDirector(god, god.getStateDirector()));
		}
		if(sd instanceof OpeningDirector){
			changeOpeningDirector();
		}else if(sd instanceof InvadeDirector){
			changeInvadeDirector();
		}else if(sd instanceof AssaultDirector){
			changeAssaultDirector();
		}
	}

	/**
	 * 90ターン経つか，(収入 + 探索者/4) > 45
	 */
	private void changeOpeningDirector() {
		int searcherNum = god.getStateDirector().getWorkerDirector().getSearchD().getUnits().size();
		
		boolean turnJudge = god.getCurrentTurn() > 90;
		boolean incomeJudge = god.getIncome() + searcherNum / 4 > 45;
		if(turnJudge || incomeJudge){
			god.setStateDirector(new InvadeDirector(god, god.getStateDirector()));
			System.err.println(getClass().getName() + ":InvadeDirectorに変更");
		}
	}

	/**
	 * 持ち資源が1000を超えたらアサルトへ
	 */
	private void changeInvadeDirector() {
		boolean resourceJudge = god.getCurrentResource() >= 1000;
		boolean isArraved = god.getStateDirector().getWorkerDirector().getInvationD().isArrived();
		if(resourceJudge){
			god.setStateDirector(new AssaultDirector(god, god.getStateDirector()));
			System.err.println(getClass().getName() + ":AssaultDirectorに変更");
		}
	}

	private void changeAssaultDirector() {
		// TODO 自動生成されたメソッド・スタブ
		
	}
	

}
