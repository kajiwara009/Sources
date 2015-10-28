package sever;

import java.util.List;

public class PokerGame {

	List<Player> playerList;
	GameData gameData;
	
	public PokerGame(List<Player> playerList) {
		this.playerList = playerList;
		// TODO 自動生成されたコンストラクター・スタブ
	}
	
	public void start(){
		initTournament();
		
		while(!isGameover()){
			initGame();
			
			while(gameData.getPhase() != Phase.END){
				bet(gameData.getPhase());
				if(isBetFinish()){
					break;
				}else{
					toNextPhase();
				}
			}
			
			showDown();
			
		}
		
	}

	
	private void bet(Phase phase) {
		// TODO 自動生成されたメソッド・スタブ
		
	}

	/**
	 * 各プレイヤーにスタック振り分け
	 * 座席をランダムに決定
	 * BB,SB,BTNをランダムに決定
	 */
	
	private void initTournament() {
		// TODO 自動生成されたメソッド・スタブ
		
	}

	private void initGame() {
		// TODO 自動生成されたメソッド・スタブ
		phaseReset();
		payBrind();
		cardDeal();
		
	}

	private void phaseReset() {
		// TODO 自動生成されたメソッド・スタブ
		
	}

	private void payBrind() {
		// TODO 自動生成されたメソッド・スタブ
		
	}

	
	private void cardDeal() {
		// TODO 自動生成されたメソッド・スタブ
		
	}

	/**
	 * もうベットフェーズが必要なければTrue
	 * @return
	 */
	private boolean isBetFinish() {
		// TODO 自動生成されたメソッド・スタブ
		return false;
	}

	/**
	 * フェーズ移行
	 */
	private void toNextPhase() {
		// TODO 自動生成されたメソッド・スタブ
		
	}

	/**
	 * ゲームの勝敗
	 * スタックのやり取り
	 */
	private void showDown() {
		// TODO 自動生成されたメソッド・スタブ
		
	}

	private boolean isGameover() {
		// TODO 自動生成されたメソッド・スタブ
		return false;
	}

}
