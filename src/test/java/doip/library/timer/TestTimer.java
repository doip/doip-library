package doip.library.timer;

import org.junit.Test;

import doip.junit.Assert;
import doip.logging.LogManager;
import doip.logging.Logger;


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
		Assert.assertEquals(5, count);
	}
	
	@Test
	public void test0() {
		count = 0;
		Timer timer = new TimerThread();
		timer.addListener(this);
		timer.start(100, 0);
		sleep(810);
		timer.stop();
		Assert.assertTrue(count >= 8);
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
