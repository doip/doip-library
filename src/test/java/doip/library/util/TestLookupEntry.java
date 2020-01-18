package doip.library.util;

import doip.junit.Assert;

import java.util.LinkedList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import doip.logging.LogManager;
import doip.logging.Logger;

public class TestLookupEntry {

	private static Logger logger = LogManager.getLogger(TestLookupEntry.class);
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception { 
		logger.info("-----------------------------------------------------------------------------");
		logger.info(">>> public static void setUpBeforeClass()");
		logger.info("<<< public static void setUpBeforeClass()");
		logger.info("-----------------------------------------------------------------------------");
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		logger.info("-----------------------------------------------------------------------------");
		logger.info(">>> public static void tearDownAfterClass()");
		logger.info("<<< public static void tearDownAfterClass()");
		logger.info("-----------------------------------------------------------------------------");
	}

	@Before
	public void setUp() throws Exception {
		logger.info("-----------------------------------------------------------------------------");
		logger.info(">>> public void setUp()");
		logger.info("<<< public void setUp()");
		logger.info("-----------------------------------------------------------------------------");
	}

	@After
	public void tearDown() throws Exception {
		logger.info("-----------------------------------------------------------------------------");
		logger.info(">>> public void tearDown()");
		logger.info("<<< public void tearDown()");
		logger.info("-----------------------------------------------------------------------------");
	}

	/**
	 * Test the string "1083:". Expected behavior is a lookup entry where result is just an empty string.
	 */
	@Test
	public void test1083Colon() {
		logger.info("#############################################################################");
		logger.info(">>> public void test1083Colon()");
		
		LookupEntry entry = new LookupEntry("1083:");
		Assert.assertEquals("1083", entry.getRegex());
		Assert.assertEquals("", entry.getResult());
		
		logger.info("<<< public void test1083Colon()");
		logger.info("#############################################################################");
	}
	
	@Test
	public void test4Items() {
		logger.info("#############################################################################");
		logger.info(">>> public void test4Items()");
		
		LookupEntry entry = new LookupEntry("10 03 : 50 03: 31010203 : 71010203");
		Assert.assertEquals("1003", entry.getRegex());
		Assert.assertEquals("5003", entry.getResult());
		List<LookupEntry> modifiers = entry.getModifiers();
		Assert.assertEquals(1, modifiers.size());
		Assert.assertEquals("31010203", modifiers.get(0).getRegex());
		Assert.assertEquals("71010203", modifiers.get(0).getResult());
		
		logger.info("<<< public void test4Items()");
		logger.info("#############################################################################");
		
	}


}
