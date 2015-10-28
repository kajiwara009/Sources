package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.GameInfo.PlayerNumber;
import jp.ac.nagoya_u.is.ss.kishii.usui.system.game.PuyoPuyo;

public class SimpleClient {
	static final int PORT = 8192; // エコーポート番号

	public void client(PlayerInfo playerInfo) {
			try {
				Socket soc = new Socket("133.6.207.160", PORT); // ソケット(Socket)を開く
				BufferedReader in = new BufferedReader( // ソケットからの入力ストリーム
						new InputStreamReader(soc.getInputStream()));
				PrintStream out = new PrintStream( // ソケットへの出力ストリーム
						soc.getOutputStream());
				BufferedReader sin = new BufferedReader( // 標準入力ストリームを開く
						new InputStreamReader(System.in));
				String line;
				System.out.println("終了");
				String playerNumber = in.readLine();
				String[] stringArray = playerNumber.split(" ");
				int[] playerNumberArray = new int[2];
				for (int num = 0; num < 2; num++) {
					playerNumberArray[num] = Integer.parseInt(stringArray[num]);
				}

				String[] playerName = new String[2];
				for (int number = 0; number < 2; number++) {
					playerName[number] = playerInfo.playerNameMap
							.get(playerNumberArray[number]);
				}

				PuyoPuyo puyopuyo = new PuyoPuyo(
						playerInfo.playerClassMap.get(playerName[0]),
						playerInfo.playerClassMap.get(playerName[1]));
				PlayerNumber winnerNumber = puyopuyo.puyoPuyo();
				if(winnerNumber == PlayerNumber.ONE){
					out.println(playerNumberArray[0]); // サーバーへ出力
				}else if(winnerNumber== PlayerNumber.TWO){
					out.println(playerNumberArray[1]); // サーバーへ出力
				}
				
				
				sin.close(); // 標準入力ストリームを閉じる
				in.close(); // 入力ストリームを閉じる
				out.close(); // 出力ストリームを閉じる
				soc.close(); // ソケットを閉じる
			} catch (IOException e) {

				System.exit(1);
			}
		}
}