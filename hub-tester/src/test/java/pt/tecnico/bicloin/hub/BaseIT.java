package pt.tecnico.bicloin.hub;

import java.io.IOException;
import java.util.Properties;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import pt.tecnico.rec.RecFrontend;

import org.junit.jupiter.api.*;


public class BaseIT {

	//STRING VARIABLES FOR TEST
	public static final String USER_ID_1 = "joao";
	public static final String USER_ID_2 = "maria";
	public static final String USER_ID_3 = "nelson";
	public static final String USER_ID_NOT_REGISTED = "bela";
	public static final String USER_ID_EMPTY = "";
	public static final String STATION_ID_1 = "istt";
	public static final String STATION_ID_2 = "ista";
	public static final String STATION_ID_3 = "ocea";
	public static final String STATION_NAME_1 = "IST-TagusPark";
	public static final String STATION_NAME_2 = "IST-Alameda";
	public static final String STATION_NAME_3 = "Oceanário";

	public static final String USER_PHONE_1 = "+35191102030";
	public static final String USER_PHONE_2 = "+35191102031";
	public static final Double USER_LAT_1 = 38.7372;
	public static final Double USER_LONG_1 =  -9.3023;
	public static final int COMPENSATION_1 = 4;


	private static final String TEST_PROP_FILE = "/test.properties";
	protected static Properties testProps;
	static HubFrontend frontend;
	
	@BeforeAll
	public static void oneTimeSetup () throws IOException {
		testProps = new Properties();
		
		try {
			testProps.load(BaseIT.class.getResourceAsStream(TEST_PROP_FILE));
			System.out.println("Test properties:");
			System.out.println(testProps);

			final String host = testProps.getProperty("server.host");
			final String port = testProps.getProperty("server.port");
			frontend = new HubFrontend(host, port);
		}catch (IOException e) {
			final String msg = String.format("Could not load properties file {}", TEST_PROP_FILE);
			System.out.println(msg);
			throw e;
		}
	}
	
	@AfterAll
	public static void cleanup() {
		
	}

}
