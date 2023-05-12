package doip.library.timer;


import org.junit.jupiter.api.Test;

import doip.junit.Assertions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class TestTimer implements TimerListener {
	
	private static Logger logger = LogManager.getLogger(TestTimer.class);
	
	private volatile int count = 0;

	@Test
	public void test5() {
		count = 0;
		Timer timer = new TimerThread();
		timer.addListener(this);
		timer.start(100, 5);
		sleep(800);
		timer.stop();
		Assertions.assertEquals(5, count);
	}
	
	@Test
	public void test0() {
		count = 0;
		Timer timer = new TimerThread();
		timer.addListener(this);
		timer.start(100, 0);
		sleep(810);
		timer.stop();
		Assertions.assertTrue(count >= 8);
	}

	@Override
	public void onTimerExpired(Timer timer) {
		logger.info("onTimerExpired");
		count++;
	}

	private void sleep(int millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
		}
	}
}
