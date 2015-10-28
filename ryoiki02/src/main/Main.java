package main;

import java.util.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Main {

	public static void main(String[] args) {

		try {

			int maxcount = 100;
			int stepnum = 10000;
			Random r = new Random();

			FileWriter fw = new FileWriter("Output/timeSeries.csv"); // 時系列データ出力
			PrintWriter pw = new PrintWriter(new BufferedWriter(fw));

			for (int i = 0; i < 1; i++) {
				double newC = 0.55 + i * 0.001;
				System.out.println("Cの値は" + newC);
				int amount[] = new int[stepnum];

				for (int count = 0; count < maxcount; count++) {
					List<Agent> agentList = new ArrayList<Agent>();

					// 初期化　statusの設定
					for (int k = 0; k < Cchange.agentnum; k++) {
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
						int counterStep1 = 0;

						// ステップの表示
						 System.out.println("step:\t" + j);
						

						agentList.get(r.nextInt(Cchange.agentnum)).step1(agentList);
						
						agentList.get(r.nextInt(Cchange.agentnum)).step2();

						for (Agent a : agentList) {
							if (a.status == 1) {
								counterStep1++;
							}
						}

						amount[j] += counterStep1;
						 System.out.println("感染者数" + counterStep1);
						 System.out.println("感染していない人"
						 + (agentList.size() - counterStep1));

						
	//					  pw.print(counterStep1); pw.print(",");
						 
					}
				}

				double average[] = new double[stepnum];

				for (int l = 0; l < stepnum; l++) {
					average[l] = (double) amount[l] / maxcount;
					pw.println(average[l]);
					// System.out.println(l+"番目"+amount[l]);
				}
				// System.out.println(average[stepnum-1]);

				// (l+"回目" +average[l]);

//				pw.print(newC + ",");
//				pw.print(average[stepnum - 1]);//C-finalInfected
				pw.println();

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
		for (int i = 0; i < agentList.size(); i++) {// ///////////////////////////////100
			agentList.get(i).C = newC;
		}

	}

}
