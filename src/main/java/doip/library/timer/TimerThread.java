package doip.library.timer;

public class TimerThread extends Timer implements Runnable {
	
	private volatile Thread thread = null;
	private volatile long cycle = 1000000; // cycle time = 1 ms
	
	private volatile boolean runFlag = false;
	
	public void start(long cycle) {
		this.cycle = cycle;
		thread = new Thread(this);
		this.runFlag = true;
		thread.start();
	}
	
	public void stop() {
		this.runFlag = false;
		try {
			this.thread.join();
		} catch (InterruptedException e) {
		}
	}

	@Override
	public void run() {
		long nextTime = System.nanoTime() + cycle;
		while (runFlag) {
			long currentTime = System.nanoTime();
			if (nextTime < currentTime) {
				nextTime += cycle;
				this.onTimerExpired();
			}
			try {
				Thread.sleep(0, 1);
			} catch (InterruptedException e) {
			}
		}
	}
}
