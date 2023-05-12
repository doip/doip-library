package doip.library.properties;
import static doip.junit.Assertions.*;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TestPropertyFile {
	
	private static Logger logger = LogManager.getLogger(TestPropertyFile.class);
	
	private PropertyFile props = null;

	@BeforeAll
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	public static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	public void setUp() throws Exception {
		props = new PropertyFile("src/test/resources/test.properties");
	}

	@AfterEach
	public void tearDown() throws Exception {
		props = null;
	}

	@Test
	public void testLoadUnexistingFile() throws IOException {
		assertThrows(
				FileNotFoundException.class, 
				() -> { 
					new PropertyFile("BlackHole.properties");
				});
	}

	@Test
	public void testMandatoryBooleanTrue() throws MissingProperty, EmptyPropertyValue {
		boolean b = props.getPropertyAsBoolean("booleanTrue", true);
		assertEquals(b, true);
	}
	
	@Test
	public void testOptionalBoolean() throws MissingProperty, EmptyPropertyValue {
		boolean b = props.getPropertyAsBoolean("empty", false);
		assertEquals(b, false);
	}
}
