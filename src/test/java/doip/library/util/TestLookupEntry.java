package doip.library.util;

import static doip.junit.Assertions.*;

import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import doip.logging.LogManager;
import doip.logging.Logger;

public class TestLookupEntry {

	private static Logger logger = LogManager.getLogger(TestLookupEntry.class);
	
	@BeforeAll
	public static void setUpBeforeClass() throws Exception { 
		logger.info("-----------------------------------------------------------------------------");
		logger.info(">>> public static void setUpBeforeClass()");
		logger.info("<<< public static void setUpBeforeClass()");
		logger.info("-----------------------------------------------------------------------------");
	}

	@AfterAll
	public static void tearDownAfterClass() throws Exception {
		logger.info("-----------------------------------------------------------------------------");
		logger.info(">>> public static void tearDownAfterClass()");
		logger.info("<<< public static void tearDownAfterClass()");
		logger.info("-----------------------------------------------------------------------------");
	}

	@BeforeEach
	public void setUp() throws Exception {
		logger.info("-----------------------------------------------------------------------------");
		logger.info(">>> public void setUp()");
		logger.info("<<< public void setUp()");
		logger.info("-----------------------------------------------------------------------------");
	}

	@AfterEach
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
		assertEquals("1083", entry.getRegex());
		assertEquals("", entry.getResult());
		
		logger.info("<<< public void test1083Colon()");
		logger.info("#############################################################################");
	}
	
	@Test
	public void test4Items() {
		logger.info("#############################################################################");
		logger.info(">>> public void test4Items()");
		
		LookupEntry entry = new LookupEntry("10 03 : 50 03: 31010203 : 71010203");
		assertEquals("1003", entry.getRegex());
		assertEquals("5003", entry.getResult());
		List<LookupEntry> modifiers = entry.getModifiers();
		assertEquals(1, modifiers.size());
		assertEquals("31010203", modifiers.get(0).getRegex());
		assertEquals("71010203", modifiers.get(0).getResult());
		
		logger.info("<<< public void test4Items()");
		logger.info("#############################################################################");
		
	}


}
