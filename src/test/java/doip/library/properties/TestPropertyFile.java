package doip.library.properties;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestPropertyFile {
	
	private PropertyFile props = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		props = new PropertyFile("src/test/resources/test.properties");
	}

	@After
	public void tearDown() throws Exception {
		props = null;
	}

	@Test(expected = IOException.class)
	public void testLoadUnexistingFile() throws IOException {
		new PropertyFile("BlackHole.properties");
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
