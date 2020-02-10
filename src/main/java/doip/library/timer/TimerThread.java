package doip.library.timer;

public class TimerThread extends Timer implements Runnable {
	
	private volatile Thread thread = null;
	private volatile long cycleTime = 1; // cycle time = 1 ms
	
	private volatile boolean runFlag = false;
	
	/**
	 * The number of cycles the time shall perform, for example if
	 * number of cycles is 3 the  timer will call three times 
	 * onTimerExpired(). If number of cycles is 0 then timer is
	 * running endless.
	 */
	private volatile int numberOfCycles = 0;
	
	public void start(long cycleTime, int numberOfCycles) {
		this.cycleTime = cycleTime;
		this.numberOfCycles = numberOfCycles;
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
	
	public boolean isAlive() {
		return this.thread.isAlive();
	}

	@Override
	public void run() {
		int count = 0;
		long nextTime = System.nanoTime() + (cycleTime * 1000000);
		while (runFlag) {
			long currentTime = System.nanoTime();
			if (nextTime < currentTime) {
				nextTime += (cycleTime * 1000000);
				this.onTimerExpired();
				count ++;
				if (numberOfCycles > 0) {
					if (count >= numberOfCycles) {
						runFlag = false;
					}
				}
			}
			try {
				Thread.sleep(0, 1000); // Wait 1 microsecond
			} catch (InterruptedException e) {
			}
		}
	}
}
