package doip.library.util;

import static doip.junit.Assert.*;

import org.junit.Test;

import doip.library.util.Conversion;

public class TestConversion {

	@Test
	public void testA() {
		assertArrayEquals(new byte[] {0x00, (byte)0xA0}, Conversion.hexStringToByteArray("00 A0"));
		assertArrayEquals(new byte[] {0x00, (byte)0xA0}, Conversion.hexStringToByteArray("00 A0..;;,,"));
	}
	
	@Test
	public void testB() {
		assertArrayEquals(new byte[0], Conversion.hexStringToByteArray(""));
	}
	
	@Test
	public void testC() {
		byte[] bytes = new byte[] {0x22, (byte) 0xF1, (byte) 0x86};
		assertEquals("22 F1 86", Conversion.byteArrayToHexString(bytes));
	}
	
	@Test
	public void testD() {
		byte[] bytes = new byte[] {1,2,3,4,5,6,7,8};
		assertEquals("01 02 03 04", Conversion.byteArrayToHexStringShort(bytes, 4));
		assertEquals("01 02 03 04...", Conversion.byteArrayToHexStringShortDotted(bytes, 4));
	}
	
	@Test
	public void testE() {
		byte[] bytes = new byte[0];
		assertEquals("", Conversion.byteArrayToHexString(bytes));
	
	}
}
