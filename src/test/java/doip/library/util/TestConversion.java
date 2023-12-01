package doip.library.util;

import static com.starcode88.jtest.Assertions.*;

import org.junit.jupiter.api.Test;

import doip.library.util.Conversion;

public class TestConversion {

	@Test
	public void testHexStringoByteArray() {
		assertArrayEquals(new byte[0], Conversion.hexStringToByteArray(""));
		assertArrayEquals(new byte[] {0x00, (byte)0xA0}, Conversion.hexStringToByteArray("00 A0"));
		assertArrayEquals(new byte[] {0x00, (byte)0xA0}, Conversion.hexStringToByteArray("00 A0..;;,,"));
	}
	
	@Test
	public void testByteArrayToHexString() {
		byte[] bytes = new byte[] {0x22, (byte) 0xF1, (byte) 0x86};
		assertEquals("22 F1 86", Conversion.byteArrayToHexString(bytes));
		bytes = new byte[0];
		assertEquals("", Conversion.byteArrayToHexString(bytes));
	}
	
	@Test
	public void testByteArrayToHexStringShort() {
		byte[] bytes = new byte[] {1,2,3,4,5,6,7,8};
		assertEquals("01 02 03 04", Conversion.byteArrayToHexStringShort(bytes, 4));
		assertEquals("01 02 03 04 05 06 07 08", Conversion.byteArrayToHexStringShort(bytes, 10));
	}
	
	@Test
	public void testByteArrayToHexStringShortDotted() {
		byte[] bytes = new byte[0];
		assertEquals("", Conversion.byteArrayToHexStringShortDotted(bytes, 0));
		bytes = new byte[] {1,2,3};
		assertEquals("...", Conversion.byteArrayToHexStringShortDotted(bytes, 0));
		bytes = new byte[] {1,2,3,4,5,6,7,8};
		assertEquals("01 02 03 04...", Conversion.byteArrayToHexStringShortDotted(bytes, 4));
	}
	
	@Test
	public void testAsciiStringToByteArray() {
		byte[] actuals = Conversion.asciiStringToByteArray("ABC 123");
		byte[] expecteds = new byte[] {0x41, 0x42, 0x43, 0x20, 0x31, 0x32, 0x33};
		assertArrayEquals(expecteds, actuals);
	}
}
