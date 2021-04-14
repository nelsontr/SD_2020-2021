package pt.tecnico.bicloin.hub;

import org.junit.jupiter.api.*;

import pt.tecnico.bicloin.hub.grpc.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Scanner;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BikeUpIT extends BaseIT {

	private static final String USER_DATA_FILE = "/users.csv";
	private static final String STATION_DATA_FILE = "/stations.csv";
	private static String data = "";

	@BeforeAll
	public static void oneTimeSetUp() throws FileNotFoundException, URISyntaxException {
		//users
		URI uri = BalanceIT.class.getResource(USER_DATA_FILE).toURI();
		try (Scanner fileScanner = new Scanner(new File(uri))) {
			while (fileScanner.hasNextLine()) {
				data = data.concat(fileScanner.nextLine() + "\n");
			}
		} catch (FileNotFoundException fife) {
			System.out.println(String.format("Could not find file '%s'", USER_DATA_FILE));
			throw fife;
		}
		data = data.concat("---\n");
		//stations
		uri = BalanceIT.class.getResource(STATION_DATA_FILE).toURI();
		try (Scanner fileScanner = new Scanner(new File(uri))) {
			while (fileScanner.hasNextLine()) {
				data = data.concat(fileScanner.nextLine() + "\n");
			}
		} catch (FileNotFoundException fife) {
			System.out.println(String.format("Could not find file '%s'", STATION_DATA_FILE));
			throw fife;
		}
		System.out.println(data);
	}

	@AfterAll
	public static void oneTimeTearDown() {
	}

	@BeforeEach
	public void setUp() {
		CtrlInitRequest request = CtrlInitRequest.newBuilder().setInput(data).build();

		try {
			frontend.ctrlInit(request);
		} catch (Exception e) {
			System.out.println(String.format("Exception<ctrl_init>: %s", e.getMessage()));
			throw e;
		}
	}

	@AfterEach
	public void tearDown() {
		CtrlClearRequest request = CtrlClearRequest.newBuilder().build();
		frontend.ctrlClear(request);
	}

	// -------- Tests --------
/*
	@Test
	public void bikeUpInTheSameLocation() {
		BalanceRequest balanceRequest = BalanceRequest.newBuilder().setUserName(USER_ID_1).build();
		int balanceBefore = frontend.balance(balanceRequest).getBalance();
		BikeRequest bikeRequest1 = BikeRequest.newBuilder().setUserName(USER_ID_1)
				.setLat(USER_LAT_1).setLong(USER_LONG_1).setStationId(STATION_ID_1).build();
		String response = frontend.bikeUp(bikeRequest1).getStatus();
		assertEquals("OK", response);
		int balanceAfter = frontend.balance(balanceRequest).getBalance();
		assertEquals(balanceBefore, balanceAfter+10);
	}

	@Test
	public void bikeUpInAFarLocation() {
		BalanceRequest balanceRequest = BalanceRequest.newBuilder().setUserName(USER_ID_1).build();
		int balanceBefore = frontend.balance(balanceRequest).getBalance();
		BikeRequest bikeRequest1 = BikeRequest.newBuilder().setUserName(USER_ID_1)
				.setLat(USER_LAT_1).setLong(USER_LONG_1).setStationId(STATION_ID_2).build();
		int balanceAfter = frontend.balance(balanceRequest).getBalance();
		String response = frontend.bikeUp(bikeRequest1).getStatus();

		assertEquals("ERROR", response);
		assertEquals(balanceAfter, balanceBefore);
	}

	@Test
	public void bikeUpWithoutMoney() {
		BalanceRequest balanceRequest = BalanceRequest.newBuilder().setUserName(USER_ID_1).build();
		BikeRequest bikeRequest1 = BikeRequest.newBuilder().setUserName(USER_ID_1)
				.setLat(USER_LAT_1).setLong(USER_LONG_1).setStationId(STATION_ID_1).build();

		int balanceBefore = frontend.balance(balanceRequest).getBalance();
		String response = frontend.bikeUp(bikeRequest1).getStatus();
		int balanceAfter = frontend.balance(balanceRequest).getBalance();

		assertEquals("ERROR", response);
		assertEquals(balanceAfter, balanceBefore);
	}
*/
}
