package doip.library.message;

import static doip.junit.Assert.*;

import org.junit.Test;

import doip.library.util.Conversion;
import doip.logging.Level;
import doip.logging.LogManager;
import doip.logging.Logger;

public class TestDoipTcpAliveCheckRequest {
	
	private Logger logger = LogManager.getLogger(TestDoipTcpAliveCheckRequest.class); 

	@Test
	public void test() {
		DoipTcpAliveCheckRequest msg = new DoipTcpAliveCheckRequest();
		String hexString = Conversion.byteArrayToHexString(msg.getMessage());
		logger.info("HEX string = " + hexString);
		assertEquals("02 FD 00 07 00 00 00 00", hexString);
	}
}
