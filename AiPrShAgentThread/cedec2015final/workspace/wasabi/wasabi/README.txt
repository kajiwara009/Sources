/***************************************

* Date	 : 2015/08/18
* Author : Mizukoshi Toshiki
* e-mail : ma15082@shibaura-it.ac.jp

***************************************/

開発環境 : Eclipse
java version : JavaSE-1.7
jarファイル:
aiwolf-client-0.2.5.jar
aiwolf-common-0.2.5.jar
aiwolf-server-0.2.5.jar


ファイル一覧：
・RoleAssignPlayer,AbstructRoleを継承したクラス
WasabiRoleAssignPlayer
WasabiPlayer

・解析用クラス
WasabiAnalyzer...情報を解析をする
LineInfo...占い霊能ラインの信頼度の計算をする
TalkInfo...発話・投票情報を解析する
Tools...細かい計算をする

・データ構造クラス
Colour...黒（狼）か白（非狼）かなどを表す
JudgeInfo...占い霊能結果を格納する
ListMap...Mapクラス
Message...会話の情報を格納する
Pair...Entryクラス
Personality...エージェントの特徴情報を格納する
PlayerInfo...エージェントのCO,生死などを格納する

・テスト用
AgentTester
RandomPlayer
