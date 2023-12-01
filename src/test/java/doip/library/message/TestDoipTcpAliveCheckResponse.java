package doip.library.message;

import static com.starcode88.jtest.Assertions.*;

import org.junit.jupiter.api.Test;

import doip.library.util.Conversion;

public class TestDoipTcpAliveCheckResponse {

	@Test
	public void test() {
		DoipTcpAliveCheckResponse msg = new DoipTcpAliveCheckResponse(0x1234);
		String hexString = Conversion.byteArrayToHexString(msg.getMessage());
		assertEquals("03 FC 00 08 00 00 00 02 12 34", hexString);
	}

}
