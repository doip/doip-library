package doip.library.util;

import static doip.junit.Assert.*;

import org.junit.Test;

import doip.library.util.Conversion;

public class TestConversion {

	@Test
	public void testA() {
		assertArrayEquals(new byte[] {0x00, (byte)0xA0}, Conversion.hexStringToByteArray("00 A0"));
	}
	
	@Test
	public void testB() {
		assertArrayEquals(new byte[0], Conversion.hexStringToByteArray(""));
		
	}
}
