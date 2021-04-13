package pt.tecnico.rec;

import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.*;
import pt.tecnico.rec.grpc.*;

import static io.grpc.Status.INVALID_ARGUMENT;
import static io.grpc.Status.NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RecordIT extends BaseIT {

	final static String NAME_1 = "nelson ";
	final static String NAME_2 = "ana ";
	final static String REQUEST_1 = "balance";
	final static int BALANCE_BELLOW_ZERO = -1;
	final static int BALANCE_0 = 0;
	final static int BALANCE_1 = 34;

	// static members
	// TODO	

	// one-time initialization and clean-up
	@BeforeAll
	public static void oneTimeSetUp(){

	}
	
	@AfterAll
	public static void oneTimeTearDown() {
		
	}
	
	// initialization and clean-up for each test
	@BeforeEach
	public void setUp() {
		
	}
	
	@AfterEach
	public void tearDown() {
		ClearRequest request = ClearRequest.newBuilder().setIntValue(1).build();
		ClearResponse response = frontend.clear(request);
		assertEquals(1, response.getResponse());
	}
		
	// -------- Tests --------
	
	@Test
	public void creatingFirstBalanceTest() {
		WriteRequest request = WriteRequest.newBuilder().setName(NAME_1+REQUEST_1).setIntValue(BALANCE_1).build();
		WriteResponse response = frontend.write(request);
		assertEquals("OK", response.getResponse());

		ReadRequest request2 = ReadRequest.newBuilder().setName(NAME_1+REQUEST_1).build();
		ReadResponse response2 = frontend.read(request2);
		assertEquals(BALANCE_1, response2.getValue());
	}

	@Test
	public void writingBalanceBelowZeroTest() {
		WriteRequest request = WriteRequest.newBuilder().setName(NAME_1+REQUEST_1)
				.setIntValue(BALANCE_BELLOW_ZERO).build();
		assertEquals(INVALID_ARGUMENT.getCode(), assertThrows(
				StatusRuntimeException.class, () -> frontend.write(request))
				.getStatus()
				.getCode());
	}

	@Test
	public void readingBalanceWithoutUserTest() {
		ReadRequest request = ReadRequest.newBuilder().setName(NAME_1+REQUEST_1).build();;
		assertEquals(NOT_FOUND.getCode(), assertThrows(
				StatusRuntimeException.class, () -> frontend.read(request))
				.getStatus()
				.getCode());
	}

}
