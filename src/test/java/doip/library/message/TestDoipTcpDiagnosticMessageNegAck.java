package doip.library.message;

import static com.starcode88.jtest.Assertions.*;

import org.junit.jupiter.api.Test;

import doip.library.util.Conversion;

public class TestDoipTcpDiagnosticMessageNegAck {

	@Test
	public void test() {
		DoipTcpDiagnosticMessageNegAck msg = new DoipTcpDiagnosticMessageNegAck(0x1122, 0x3344, 0x02, new byte[0]);
		String hexString = Conversion.byteArrayToHexString(msg.getMessage());
		assertEquals("03 FC 80 03 00 00 00 05 11 22 33 44 02", hexString);
	}
}
