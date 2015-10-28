package main;

import java.util.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class OptimizedCchange {

	static final int agentnum = 10000;
	static final boolean outputRestTime = true; //残り時間を表示するかどうか

	public static void main(String[] args) {

		try {

			//変数設定
			int maxcount = 10; //同条件で繰り返す回数
			int stepnum = 1000 * 10000; //1試行のステップ数
			
			/*
			 * 変化させるCについての設定
			 * minC = 0.0, maxC = 1.0, divideNum の場合，
			 * C = 0.00, 0.01, 0.02, 0.03, … , 0.99, 1.00 のそれぞれのCについてシミュレーションを行う
			 */
			double minC = 0.0; //Cの最低値
			double maxC = 1.0; //Cの最大値
			int divideNum = 100; //minCからmaxCまでの分割数

			
			int lapseRate = 0;
			long preTime = System.currentTimeMillis();
			
			double stride = (maxC - minC) / (double)divideNum; //出力する刻み幅
			
			Random r = new Random();
			FileWriter fw = new FileWriter("Output/criticalPoint.csv"); // 臨界点についてのデータを出力
			PrintWriter pw = new PrintWriter(new BufferedWriter(fw));
			
			for(int i = 0; i < divideNum; i++){
//			for (int i = 0; i < 15; i++) {
				double newC = minC + (double)i * stride;
				//double newC = 0.4 + i * 0.02;
				System.out.println("Cの値は" + newC);
//				int amount[] = new int[stepnum];
				int finalAmount = 0;
				
				for (int count = 0; count < maxcount; count++) {
					
					System.out.println(count + "回目");
					List<Agent> agentList = new ArrayList<Agent>();
					// 初期化　statusの設定
					for (int k = 0; k < agentnum; k++) {
						Agent a = new Agent(k);
						agentList.add(a);
					}
					decideCtoAll(newC, agentList);
					// 初期化　otherAgentsの設定
					
/*					for (Agent a : agentList) {
						a.otherAgents = new ArrayList<Agent>(agentList);
						a.otherAgents.remove(a);
					}
*/
					// ステップ　

					for (int j = 0; j < stepnum; j++) {
//						int counterStep1 = 0;
						// ステップの表示
						// System.out.println("step:\t" + j);
						agentList.get(r.nextInt(agentnum)).step1(agentList);
						agentList.get(r.nextInt(agentnum)).step2();
/*						for (Agent a : agentList) {
							if (a.status == 1) {
								counterStep1++;
							}
						}
						
						amount[j] += counterStep1;
*/
						// System.out.println("感染者数" + counterStep1);
						// System.out.println("感染していない人"
						// + (agentList.size() - counterStep1));
						/*
						 * pw.print(counterStep1); pw.print(",");
						 */
					}
					
					for (Agent a : agentList) {
						if (a.status == 1) {
							finalAmount++;
						}
					}
					
					
					/*
					 * 残り時間出すための部分なので，特に気にせず
					 */
					if(outputRestTime){
						long postTime = System.currentTimeMillis();
						lapseRate++;
						int sumLapse = divideNum * maxcount;
						double rate = (double)lapseRate / (double)sumLapse;
						double restTimeD = (double)(postTime - preTime) * ((1.0 - rate) / rate) /1000;
						int restTime = (int)Math.round(restTimeD);
						int hour = restTime / 3600;
						int minute = (restTime % 3600) / 60;
						int second = restTime % 60;
						System.out.println("残り時間：" + hour + "時間" + minute + "分" + second + "秒");
						//System.out.println("残り約" + Math.round(restTime) + "秒");
					}
				}

				double ave = (double)finalAmount / (double)maxcount;
				
/*				double average[] = new double[stepnum];
				
				for (int l = 0; l < stepnum; l++) {
					average[l] = (double) amount[l] / maxcount;
					// System.out.println(l+"番目"+amount[l]);
				}
*/				// System.out.println(average[stepnum-1]);
				// (l+"回目" +average[l]);
				pw.print(newC + ",");
				pw.print(ave);
//				pw.print(average[stepnum - 1]);
				pw.println();
				
				pw.flush();// この関数を呼んでおくと，プログラム実行途中でもファイルへの書き込みが反映される．
			}
			pw.close();
			
		} catch (IOException ex) {
			ex.printStackTrace();
			// for(int j = 0;j< agentList.size() ;j++){
			// Agent a = agentList.get(j);
			//
			// }
		}
	}

	public static void decideCtoAll(double newC, List<Agent> agentList) {
		for (int i = 0; i < agentnum; i++) {// ///////////////////////////////100
			agentList.get(i).C = newC;
		}
	}
}
