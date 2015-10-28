package threadTest;

public class MyRunnable implements Runnable{

	int i;
	String name;
	
	public MyRunnable(int i) {
		this.i = i;
	}
	@Override
	public void run() {
		printNum();
	}
	
	public void setThreadName(String str){
		name = str;
	}
	
	private synchronized void printNum() {
		for(int j = 0; j < i; j++){
			System.out.println(this.name + "今は" + j + "番目");
			try {
				notifyAll();
				wait();
				
			} catch (InterruptedException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		}
	}

}
