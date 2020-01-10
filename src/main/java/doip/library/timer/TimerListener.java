package doip.library.timer;

/**
 * Interface of subscriber for Timer 
 */
public interface TimerListener {
	
	/**
	 * Will be called when the timer expired
	 * @param timer The timer which caused this event
	 */
	public void onTimerExpired(Timer timer);
	
}
