package pt.tecnico.rec;

import pt.tecnico.rec.grpc.*;

import java.io.IOException;
import java.util.Properties;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;

public class BaseIT {

	static RecFrontend frontend;
	static QuorumFrontend Qfrontend;
	protected static Properties testProps;
	private static final String TEST_PROP_FILE = "/test.properties";

	final static String NAME_1 = "nelson ";
	final static String NAME_2 = "ana ";
	final static String REQUEST_1 = "balance";
	final static int BALANCE_BELLOW_ZERO = -1;
	final static int BALANCE_0 = 0;
	final static int BALANCE_1 = 34;

	@BeforeAll
	public static void oneTimeSetup() throws IOException {
		testProps = new Properties();

		try {
			testProps.load(BaseIT.class.getResourceAsStream(TEST_PROP_FILE));
			System.out.println("Test properties:");
			System.out.println(testProps);

			final String host = testProps.getProperty("server.host");
			final String port = testProps.getProperty("server.port");
			frontend = new RecFrontend(host, port, "1");
			final String zoohost = testProps.getProperty("zoo.host");
			final String zooport = testProps.getProperty("zoo.port");
			Qfrontend = new QuorumFrontend(zoohost, zooport);
		} catch (IOException e) {
			final String msg = String.format("Could not load properties file {}", TEST_PROP_FILE);
			System.out.println(msg);
			throw e;
		}
	}

	@AfterEach
	public void tearDown() {
		ClearRequest request = ClearRequest.newBuilder().build();
		frontend.clear(request);
	}

	@AfterAll
	public static void cleanup() {
		frontend.closeChannel();
	}
}
