package doip.library.util;

import static com.starcode88.jtest.Assertions.*;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class TestLookupTable {

	private static Logger logger = LogManager.getLogger(TestLookupTable.class);
	
	private LookupTable lookupTable = null;
	
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
		
		this.lookupTable = new LookupTable();
		
		logger.info("<<< public void setUp()");
		logger.info("-----------------------------------------------------------------------------");
	}

	@AfterEach
	public void tearDown() throws Exception {
		logger.info("-----------------------------------------------------------------------------");
		logger.info(">>> public void tearDown()");
		
		this.lookupTable = null;
		
		logger.info("<<< public void tearDown()");
		logger.info("-----------------------------------------------------------------------------");
	}

	@Test
	public void test() throws IOException {
		logger.info("#############################################################################");
		logger.info(">>> public void test()");
		
		this.lookupTable.addLookupEntriesFromFile("src/test/resources/LookupTableA.txt");
		String result = this.lookupTable.findResultAndApplyModifiers("1003");
		assertEquals("5003003201E8", result);
		
		logger.info("<<< public void test()");
		logger.info("#############################################################################");
	}
	
	
	@Test
	public void testModifier() {
		logger.info("#############################################################################");
		logger.info(">>> testModifier()");
		
		LookupTable table = new LookupTable();
		LookupEntry entry = new LookupEntry("1003:5003:22F186:62F18603");
		table.addEntry(entry);
		entry = new LookupEntry("22F186:62F18601");
		table.addEntry(entry);
		
		List<LookupEntry> entries = table.getLookupEntries();
		assertEquals(2, entries.size());
		assertEquals("1003", entries.get(0).getRegex());
		assertEquals("5003", entries.get(0).getResult());
		LinkedList<LookupEntry> modifiers = entries.get(0).getModifiers();
		assertEquals(1, modifiers.size());
		
		assertEquals("22F186", entries.get(1).getRegex());
		assertEquals("62F18601", entries.get(1).getResult());
		
		table.findResultAndApplyModifiers("1003");
		assertEquals("22F186", entries.get(1).getRegex());
		assertEquals("62F18603", entries.get(1).getResult());
		
		logger.info("<<< testModifier()");
		logger.info("#############################################################################");
	}
	
	@Test
	public void testReference() {
		logger.info("#############################################################################");
		logger.info(">>> public void testReference()");
		
		LookupTable table = new LookupTable();
		LookupEntry entry = new LookupEntry("36\\w*", "76[01]");
		table.addEntry(entry);
		String result = table.findResultAndApplyModifiers("36FF010203");
		assertEquals("76FF", result);
		
		byte[] request = new byte[] { 0x36, 0x10, 0x01, 0x02, 0x03 };
		byte[] response = table.findResultAndApplyModifiers(request);
		assertNotNull(response);
		byte[] expected = new byte[] { 0x76, 0x10 };
		assertArrayEquals(expected, response);
		
		logger.info("<<< public void testReference()");
		logger.info("#############################################################################");
	}
	
	@Test
	public void testCopyConstructor() {
		LookupTable table = new LookupTable();
		table.addEntry(new LookupEntry("10 03 : 50 03"));
		LookupTable copy = new LookupTable(table);
		assertEquals(1, copy.getLookupEntries().size());
		table.addEntry(new LookupEntry("10 03 : 50 03"));
		assertEquals(1, copy.getLookupEntries().size());
	}
}
