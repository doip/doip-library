package doip.library.timer;

import java.util.LinkedList;

/**
 * Implements the publisher for a timer which fires cyclic events.
 */
public abstract class Timer {
	
	private LinkedList<TimerListener> listeners = new LinkedList<TimerListener>();
	
	public void addListener(TimerListener listener) {
		this.listeners.add(listener);
	}
	
	public void removeListener(TimerListener listener) {
		this.listeners.remove(listener);
	}
	
	public void onTimerExpired() {
		for (TimerListener listener : this.listeners) {
			listener.onTimerExpired(this);
		}
	}
	
	public abstract void start(long cycleTime, int numberOfCycles);
	
	public abstract void stop();
}
