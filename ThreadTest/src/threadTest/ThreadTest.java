package threadTest;

/**
 * 同じRunnableを引数にしていないならwait,notifyを用いることができない
 * 上記のメソッドはsyncronized修飾子がついたメソッド内でのみ扱える
 * 
 * tips
 * マルチスレッドで同じListにputするものをsynchronizedせずにすると，時々nullが入ってたりする.
 * 
 * @author kajiwarakengo
 *
 */
public class ThreadTest extends Thread{
	public static void main(String[] a){
		ThreadTest thread1 = new ThreadTest();
		ThreadTest thread2 = new ThreadTest();
		ThreadTest thread3 = new ThreadTest();
		
		MyRunnable myRunnable = new MyRunnable(10);
		MyRunnable myRunnable2 = new MyRunnable(20);
		
		Thread threadRun1 = new Thread(myRunnable);
		Thread threadRun2 = new Thread(myRunnable);
		
		threadRun2.setName(threadRun2.getName());
		threadRun1.start();
		threadRun2.start();
		
		
//		thread1.start();
//		thread2.start();
//		thread3.start();
		return;
	}
	
	@Override
	public void run(){
		for(int i = 0; i < 10; i++){
			System.out.println(getName() + ":" + (i+1));
				try {
					wait();
				} catch (InterruptedException e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
				}
		}
	}
	
	public class MyThread implements Runnable{
		@Override
		public void run(){
			for(int i = 0; i < 10; i++){
				System.out.println(getName() + ":" + i+1);
			}
		}
		
	}

}
