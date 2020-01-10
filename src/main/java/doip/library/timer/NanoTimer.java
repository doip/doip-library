package doip.library.timer;

public class NanoTimer {
	
	private long start = 0;
	
	public NanoTimer() {
		this.start = System.nanoTime();
	}
	
	public void reset() {
		start = System.nanoTime();
	}

	public long getElapsedTime() {
		long current = System.nanoTime();
		return current - start;
	}
}
