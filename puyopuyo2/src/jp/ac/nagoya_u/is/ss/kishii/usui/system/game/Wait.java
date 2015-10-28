package jp.ac.nagoya_u.is.ss.kishii.usui.system.game;

class Wait {

	public static void wait(double time){
		long start = System.currentTimeMillis();
		long now;
		do{
			now = System.currentTimeMillis();
		}while((now-start) < time*1000);
	}
}
