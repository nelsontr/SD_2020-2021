package pt.tecnico.bicloin.hub;

import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.*;
import pt.tecnico.bicloin.hub.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import static io.grpc.Status.INVALID_ARGUMENT;
import static io.grpc.Status.NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TopUpIT extends BaseIT {

	private static String data = "";
	private static final String USER_DATA_FILE = "users.cvs";
	private static final String STATION_DATA_FILE = "stations.cvs";

	@BeforeAll
	public static void oneTimeSetUp() throws FileNotFoundException {
		//users
		try (Scanner fileScanner = new Scanner(new File(USER_DATA_FILE))) {
			while (fileScanner.hasNextLine()) {
				data = data.concat(fileScanner.nextLine() + "\n");
			}
		} catch (FileNotFoundException fife) {
			System.out.println(String.format("Could not find file '%s'", USER_DATA_FILE));
			throw fife;
		}

		//stations
		try (Scanner fileScanner = new Scanner(new File(STATION_DATA_FILE))) {
			while (fileScanner.hasNextLine()) {
				data = data.concat(fileScanner.nextLine() + "\n");
			}
		} catch (FileNotFoundException fife) {
			System.out.println(String.format("Could not find file '%s'", STATION_DATA_FILE));
			throw fife;
		}
	}

	@AfterAll
	public static void oneTimeTearDown() {
	}

	@BeforeEach
	public void setUp() {
		InitRequest request = InitRequest.newBuilder().setInput(data).build();

		try {
			frontend.ctrl_init(request);
		} catch (Exception e) {
			System.out.println(String.format("Exception<ctrl_init>: %s", e.getMessage()));
			throw e;
		}
	}

	@AfterEach
	public void tearDown() {
		ClearRequest request = ClearRequest.newBuilder(ClearRequest.getDefaultInstance()).build();
		frontend.ctrl_clear(request);
	}

	// -------- Tests --------

	@Test
	public void addingTenBTCS() {
		int MONEY = 10;

		BalanceRequest request1 = BalanceRequest.newBuilder().setUserName(USER_ID_1).build();
		int balanceBefore = frontend.balance(request1).getBalance();

		TopUpRequest topUp1 = TopUpRequest.newBuilder().setUserName(USER_ID_1).
				setStake(MONEY).setPhoneNumber(USER_PHONE_1);
		int balance = frontend.topUp(topUp1).getBalance();
		int balanceAfter = frontend.balance(request1).getBalance();

		assertEquals(balance, balanceAfter);
		assertEquals(balanceBefore+MONEY, balanceAfter);
	}

	@Test
	public void addingTwentyBTCS() {
		int MONEY = 20;

		BalanceRequest request1 = BalanceRequest.newBuilder().setUserName(USER_ID_1).build();
		int balanceBefore = frontend.balance(request1).getBalance();

		TopUpRequest topUp1 = TopUpRequest.newBuilder().setUserName(USER_ID_1).
				setStake(MONEY).setPhoneNumber(USER_PHONE_1);
		int balance = frontend.topUp(topUp1).getBalance();
		int balanceAfter = frontend.balance(request1).getBalance();

		assertEquals(balance, balanceAfter);
		assertEquals(balanceBefore+MONEY, balanceAfter);
	}

	@Test
	public void addingTenBTCSForUnregisteredUser() {
		int MONEY = 10;
		TopUpRequest topUp1 = TopUpRequest.newBuilder().setUserName(USER_ID_NOT_REGISTED).
				setStake(MONEY).setPhoneNumber(USER_PHONE_1);
		StatusRuntimeException sre = assertThrows(StatusRuntimeException.class, () -> frontend.topUp(topUp1));

		assertEquals(NOT_FOUND.getCode(), sre.getStatus().getCode());
		assertEquals("No " + request1.getUsername() + " was found!",
				sre.getStatus().getDescription());
	}

	@Test
	public void addingTenBTCSForEmptyUser() {
		int MONEY = 10;
		TopUpRequest topUp1 = TopUpRequest.newBuilder().setUserName(USER_ID_NOT_REGISTED).
				setStake(MONEY).setPhoneNumber(USER_PHONE_1);
		StatusRuntimeException sre = assertThrows(StatusRuntimeException.class, () -> frontend.topUp(topUp1));

		assertEquals(INVALID_ARGUMENT.getCode(), sre.getStatus().getCode());
		assertEquals("Request cannot be empty!",sre.getStatus().getDescription());
	}

	@Test
	public void addingMoreThanTwentyBTCS() {
		int MONEY = 25;
		TopUpRequest topUp1 = TopUpRequest.newBuilder().setUserName(USER_ID_NOT_REGISTED).
				setStake(MONEY).setPhoneNumber(USER_PHONE_1);
		StatusRuntimeException sre = assertThrows(StatusRuntimeException.class, () -> frontend.topUp(topUp1));

		assertEquals(INVALID_ARGUMENT.getCode(), sre.getStatus().getCode());
		assertEquals("Request cannot!",sre.getStatus().getDescription());
	}

	@Test
	public void addingTenBTCSWithWrongPhoneNumber() {
		BalanceRequest request1 = BalanceRequest.newBuilder().setUsername(USER_ID_1).build();
		TopUpRequest topUp1 = TopUpRequest.newBuilder().setUsername(USER_ID_1).setStake(10)
				.setPhoneNumber(USER_PHONE_2).build();

		StatusRuntimeException sre = assertThrows(StatusRuntimeException.class, () -> frontend.topUp(topUp1));

		assertEquals(INVALID_ARGUMENT.getCode(), sre.getStatus().getCode());
		assertEquals("Request cannot!",sre.getStatus().getDescription());
	}

}
