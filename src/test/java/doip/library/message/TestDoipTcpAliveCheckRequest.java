package doip.library.message;

import static com.starcode88.jtest.Assertions.*;

import org.junit.jupiter.api.Test;

import doip.library.util.Conversion;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TestDoipTcpAliveCheckRequest {
	
	private Logger logger = LogManager.getLogger(TestDoipTcpAliveCheckRequest.class); 

	@Test
	public void test() {
		DoipTcpAliveCheckRequest msg = new DoipTcpAliveCheckRequest();
		String hexString = Conversion.byteArrayToHexString(msg.getMessage());
		logger.info("HEX string = " + hexString);
		assertEquals("03 FC 00 07 00 00 00 00", hexString);
	}
}
