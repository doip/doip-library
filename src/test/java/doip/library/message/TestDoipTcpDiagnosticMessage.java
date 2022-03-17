package doip.library.message;

import static doip.junit.Assertions.*;

import org.junit.jupiter.api.Test;

import doip.library.util.Conversion;

public class TestDoipTcpDiagnosticMessage {

	@Test
	public void test() {
		DoipTcpDiagnosticMessage msg = new DoipTcpDiagnosticMessage(0x1122, 0x3344, new byte[] {0x01, 0x02, 0x03, 0x04 });
		String hexString = Conversion.byteArrayToHexString(msg.getMessage());
		assertEquals("02 FD 80 01 00 00 00 08 11 22 33 44 01 02 03 04", hexString);
	}

}
