 package doip.library.util;

import static com.starcode88.jtest.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TestLookupEntry {

	private static Logger logger = LogManager.getLogger(TestLookupEntry.class);
	

	/**
	 * Test the string "1083:". Expected behavior is a lookup entry where result is just an empty string.
	 */
	@Test
	public void test1083Colon() {
		LookupEntry entry = new LookupEntry("1083:");
		assertEquals("1083", entry.getRegex());
		assertEquals("", entry.getResult());
	}
	
	@Test
	public void test4Items() {
		LookupEntry entry = new LookupEntry("10 03 : 50 03: 31010203 : 71010203");
		assertEquals("1003", entry.getRegex());
		assertEquals("5003", entry.getResult());
		List<LookupEntry> modifiers = entry.getModifiers();
		assertEquals(1, modifiers.size());
		assertEquals("31010203", modifiers.get(0).getRegex());
		assertEquals("71010203", modifiers.get(0).getResult());	
	}

	@Test
	public void testCopyConstructor() {
		LookupEntry entry = new LookupEntry("1003:5003");
		LookupEntry copy = new LookupEntry(entry);
		assertNotEquals(entry, copy);
	}
	
	@Test
	public void testCopyConstructorWithModifiers() {
		LookupEntry entry = new LookupEntry("1003:5003");
		LookupEntry modifier = new LookupEntry("11:51");
		entry.getModifiers().add(modifier);
		LookupEntry copy = new LookupEntry(entry);
		LookupEntry modifier2 = new LookupEntry("22F186:62F18601");
		entry.getModifiers().add(modifier2);
		assertEquals(1, copy.getModifiers().size());
	}
}
