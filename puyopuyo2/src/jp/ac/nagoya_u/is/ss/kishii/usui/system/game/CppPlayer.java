package jp.ac.nagoya_u.is.ss.kishii.usui.system.game;

import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.GameInfo.PlayerNumber;

/**
 * C++用のプレイヤークラスです。
 * C++で作成した場合，MainClassでこのクラスのインスタンスを生成します。
 */
public final class CppPlayer extends AbstractPlayer{
	private String libraryName;

	public CppPlayer(String playerName, String libraryName) {
		super(playerName);
		this.libraryName = libraryName;
	}

	@Override
	native public Action doMyTurn();
	@Override
	native public void initialize();
	@Override
	native public void inputResult();

	native public void setPlayerNumberToCpp(PlayerNumber playerNumber);
	native public void setGameInfoToCpp(GameInfo gameInfo);
	native private void makeAdapter(String playerName);

	public void loadLibrary() {
		System.loadLibrary(libraryName);
		makeAdapter(super.getPlayerName());
	}

	public String getLibraryName() {
		return libraryName;
	}

}
