package doip.library.util;

import static doip.junit.Assert.*;

import org.junit.Test;

import doip.junit.Assert;
import doip.library.util.Conversion;

public class TestConversion {

	@Test
	public void test() {
		Assert.assertArrayEquals(new byte[] {0x00, (byte)0xA0}, Conversion.hexStringToByteArray("00 A0"));
	}
}
