package doip.library.message;

import static doip.junit.Assertions.*;

import org.junit.jupiter.api.Test;

import doip.library.util.Conversion;

public class TestDoipTcpDiagnosticMessagePosAck {

	@Test
	public void test() {
		DoipTcpDiagnosticMessagePosAck msg = new DoipTcpDiagnosticMessagePosAck(0x1122, 0x3344, 0x00, new byte[] { 0x10, 003 });
		String hexString = Conversion.byteArrayToHexString(msg.getMessage());
		assertEquals("02 FD 80 02 00 00 00 07 11 22 33 44 00 10 03", hexString);
	}

}
